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
package com.shopzilla.spring.messaging.hornetq.config.example;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;


/**
 * A simple example demonstrating how to use the HornetQ connection factory
 *
 * @author Josh Long
 * @see {@link com.shopzilla.spring.messaging.hornetq.HornetQConnectionFactoryFactory} the actual {@link javax.jms.ConnectionFactory} factory implementation.
 * @since May 10, 2010 
 */

@Component
public class HornetQConfigurationExample {
    @Autowired
    private JmsTemplate jmsTemplate;

    @PostConstruct
    public void start() {
        final String destination = "exampleQueue"; // this assumes you've configured the relevant queue in your $HORNETQ/**/config folder

        // send
        jmsTemplate.send(destination,
            new MessageCreator() {
                public Message createMessage(final Session session)
                    throws JMSException {
                    return session.createTextMessage(String.format("Hello, world! @ %s", System.currentTimeMillis() + ""));
                }
            });

        // receive
        Message msg = jmsTemplate.receive(destination);
        System.out.println(ToStringBuilder.reflectionToString(msg));
    }

    public static void main(String[] args) throws Throwable {
        ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext("hornetqns-example.xml");
        classPathXmlApplicationContext.start();
    }
}