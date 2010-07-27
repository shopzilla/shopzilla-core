/*
 *
 * Copyright (C) 2010 Shopzilla, Inc
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 *
 * http://tech.shopzilla.com
 *
 *
 */
package com.shopzilla.spring.messaging.jms.mdp.batch;




import java.util.ArrayList;
import java.util.Collection;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.DisposableBean;

import org.springframework.context.Lifecycle;

import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import org.springframework.jms.listener.AbstractMessageListenerContainer;
import org.springframework.jms.support.JmsUtils;
import org.springframework.jms.support.destination.JmsDestinationAccessor;

/**
 * Modelled on Spring's {@link org.springframework.jms.listener.AbstractMessageListenerContainer} this supports the batching of
 * messages before being sent to an {@link com.shopzilla.spring.messaging.jms.mdp.batch.BatchMessageListener} with a timeout period to ensure
 * timely delivery of messages when a batch is incomplete.
 *
 * <p>
 * This listener container will only commit when all messages in a batch are consumed successfully;
 * this is different from any of Spring's implementations which commit per message.
 * </p>
 *
 * <p>
 * This listener currently supports only a single worker thread which polls for messages; this is
 * necessary to ensure that an incomplete batch can be sent when a timeout expires.
 * </p>
 *
 * @author Tim Morrow
 * @since Sep 27, 2006
 */
public class BatchMessageListenerContainer extends JmsDestinationAccessor implements
        DisposableBean, Lifecycle {

    static final org.apache.commons.logging.Log log = LogFactory.getLog(BatchMessageListenerContainer.class);

    /**
     * Default length of time to wait after receiving last message before decided to send a batch.
     */
    static final int DEFAULT_QUIET_PERIOD = 1000;

    /**
     * Default length of time to spend batching messages even if quiet period is never reached.
     *
     * <p>
     * After this time, all received messages will be passed to the listener. This ensures that even
     * if messages are continuously received such that there is never a quiet period, we may still
     * send the batch of messages before receiving a complete batch if it is taking too long.
     * </p>
     */
    static final int DEFAULT_BATCH_TIMEOUT = 5000;

    /**
     * Default length of time to spend waiting for a single message. Used to block until a message
     * is received or timeout so that we can evaluate whether to send the batch or check if this
     * container has been requested to be stopped.
     */
    static final int DEFAULT_RECEIVE_TIMEOUT = 1000;

    /** The default batch size. */
    private static final int DEFAULT_BATCH_SIZE = 1;

    int batchSize = DEFAULT_BATCH_SIZE;
    int batchTimeout = DEFAULT_BATCH_TIMEOUT;

    String destinationName;
    BatchMessageListener messageListener;
    int quietPeriod = DEFAULT_QUIET_PERIOD;
    int receiveTimeout = DEFAULT_RECEIVE_TIMEOUT;
    private boolean running = false;
    private TaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();

    private Worker worker;

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();

        if (destinationName == null) {
            throw new IllegalStateException("destinationName is required");
        }

        start();
    }

    public void destroy() {
        stop();
        destroyListener();
    }

    public Throwable getFailure() {
        return worker.getFailure();
    }

    public boolean isFailure() {
        return worker.isFailure();
    }

    public boolean isRunning() {
        return running;
    }

    /**
     * Specifies the batch size.
     *
     * @param batchSize
     *        the number of messages to receive in a batch; the default is 1
     */
    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    /**
     * Specifies the greatest length of time, in milliseconds, that should be spent accumulating a
     * batch of messages.
     *
     * <p>
     * After this time, any received messages will be sent to the listener, regardless of whether it
     * makes a full batch.
     * </p>
     *
     * @param batchTimeout
     *        the timeout in ms; the default is 5000 ms (5 seconds)
     */
    public void setBatchTimeout(int batchTimeout) {
        this.batchTimeout = batchTimeout;
    }

    /**
     * Specifies a destination name to listen on.
     *
     * <p>
     * This is resolved at runtime.
     * </p>
     *
     * @param destinationName
     *        the name of the destination to listen on
     *
     * @see #resolveDestinationName(Session, String)
     */
    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    /**
     * Specifies the message listener to pass batches of messages to.
     *
     * @param messageListener
     *        the message listener
     */
    public void setMessageListener(BatchMessageListener messageListener) {
        this.messageListener = messageListener;
    }

    /**
     * Specifies the quiet period.
     *
     * <p>
     * If no message is received for this length of time, the batch is sent.
     * </p>
     *
     * @param quietPeriod
     *        the quiet period; the default is 1000 ms (1 second)
     */
    public void setQuietPeriod(int quietPeriod) {
        this.quietPeriod = quietPeriod;
    }

    public void start() {
        running = true;
        registerListener();
        if (log.isInfoEnabled()) {
            log.info("listener container  started");
        }
    }

    public void stop() {
        try {
            doStop();
            if (log.isInfoEnabled()) {
                log.info("listener container stopped");
            }
        } catch (JMSException e) {
            throw convertJmsAccessException(e);
        }
    }

    void doStop() throws JMSException {
        destroyListener();
        running = false;
    }

    private void destroyListener() {
        worker.stop();
    }

    private void registerListener() {
        worker = new Worker();
        taskExecutor.execute(worker);
    }

    class Worker implements Runnable {

        // connection, session, and consumer are effectively thread-confined to the thread calling
        // run()
        private Connection connection;
        private Session session;
        private MessageConsumer consumer;

        private Throwable failure; // guarded by "this"
        private volatile boolean stopRequested = false;

        public void run() {

            try {

                try {

                    while (!this.stopRequested) {

                        boolean error = false;

                        try {

                            initJms();

                            final Collection<Message> messages = new ArrayList<Message>();
                            Message msg;

                            // Loop until we have a batch or we have taken long enough
                            final long start = System.currentTimeMillis();
                            long lastMessageReceived = System.currentTimeMillis();
                            while ((messages.size() < BatchMessageListenerContainer.this.batchSize)
                                    && ((System.currentTimeMillis() - lastMessageReceived) < BatchMessageListenerContainer.this.quietPeriod)
                                    && ((System.currentTimeMillis() - start) < BatchMessageListenerContainer.this.batchTimeout)) {
                                msg = this.consumer.receive(BatchMessageListenerContainer.this.receiveTimeout);
                                if (msg != null) {
                                    messages.add(msg);
                                    lastMessageReceived = System.currentTimeMillis();
                                }
                            }

                            if (!messages.isEmpty()) {
                                final long elapsed = System.currentTimeMillis() - start;
                                if (BatchMessageListenerContainer.log.isDebugEnabled()) {
                                    BatchMessageListenerContainer.log.debug("Received a total of " + messages.size() + " in "
                                            + elapsed + " ms");
                                }

                                BatchMessageListenerContainer.this.messageListener.onMessages(messages);

                                if (isSessionTransacted()) {
                                    this.session.commit();
                                } else if (getSessionAcknowledgeMode() == Session.CLIENT_ACKNOWLEDGE) {
                                    for (Message message : messages) {
                                        message.acknowledge();
                                    }
                                }

                            }

                        } catch (JMSException e) {
                            error = true;
                            BatchMessageListenerContainer.log.error("Error listening for logging messages",
                                    e);
                        }

                        if (error) {
                            closeJms();
                            // if we're down, don't spin in a tight loop, but sleep in between
                            // checking
                            Thread.sleep(BatchMessageListenerContainer.this.batchTimeout);
                        }

                    }

                } finally {
                    closeJms();
                }

            } catch (InterruptedException e) {

                synchronized (this) {
                    this.failure = e;
                }

                BatchMessageListenerContainer.log.error("Worker thread interrupted", e);

                // reset the interrupted status
                Thread.currentThread().interrupt();

            } catch (Throwable e) {
                synchronized (this) {
                    this.failure = e;
                }

                BatchMessageListenerContainer.log.error("Error in Worker thread", e);
            }

        }

        private void initJms() throws JMSException {

            if (this.connection == null) {
                try {
                    this.connection = getConnectionFactory().createConnection();
                    this.connection.start();
                } catch (JMSException e) {
                    JmsUtils.closeConnection(this.connection);
                    this.connection = null;
                    throw e;
                }
            }

            if (this.session == null && this.connection != null) {
                this.session = this.connection.createSession(isSessionTransacted(),
                        getSessionAcknowledgeMode());
            }

            if (this.consumer == null && this.session != null) {
                this.consumer = this.session.createConsumer(resolveDestinationName(this.session,
                        BatchMessageListenerContainer.this.destinationName));
            }
        }

        private void closeJms() {
            if (consumer != null) {
                JmsUtils.closeMessageConsumer(consumer);
                this.consumer = null;
            }

            if (this.session != null) {
                JmsUtils.closeSession(session);
                this.session = null;
            }

            if (this.connection != null) {
                JmsUtils.closeConnection(connection);
                this.connection = null;
            }
        }

        public void stop() {
            this.stopRequested = true;
        }

        synchronized Throwable getFailure() {
            return this.failure;
        }

        synchronized boolean isFailure() {
            return this.failure != null;
        }
    }
}