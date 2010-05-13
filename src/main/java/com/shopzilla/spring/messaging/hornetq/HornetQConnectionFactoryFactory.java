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
package com.shopzilla.spring.messaging.hornetq;

import org.apache.commons.lang.StringUtils;
import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.jms.client.HornetQConnectionFactory;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import java.util.HashMap;
import java.util.Map;

/**
 * This builds a {@link org.hornetq.jms.client.HornetQConnectionFactory} to handle connections to a HornetQ broker
 *
 * @author Josh Long
 *
 */
public class HornetQConnectionFactoryFactory extends AbstractFactoryBean<HornetQConnectionFactory> {

    private String host = null;
    private String backupHost = null;
    private int port = -1;
    private int backupPort = -1;
    private boolean failoverOnServerShutdown = true;
    private int reconnectAttempts = 0;

    /**
     * Builds a {@link org.hornetq.api.core.TransportConfiguration}
     *
     * @param host the host of the server
     * @param port the port of the server
     *
     * @return a {@link org.hornetq.api.core.TransportConfiguration} that can be used to vend a {@link org.hornetq.jms.client.HornetQConnectionFactory}
     *
     * @throws Exception thrown if anything out of the ordinary should trespass
     */
    private TransportConfiguration factoryTransportConfiguration(String host, int port)
        throws Exception {
        if (StringUtils.isEmpty(host) || (port < 1)) {
            return null;
        }

        Map<String, Object> parms = new HashMap<String, Object>();
        parms.put("host", host);
        parms.put("port", port);

        return new TransportConfiguration("org.hornetq.integration.transports.netty.NettyConnectorFactory",
            parms);
    }

    private HornetQConnectionFactory factoryHornetQConnectionFactory() throws Exception {
        TransportConfiguration main = factoryTransportConfiguration(this.host, this.port);
        TransportConfiguration backup = factoryTransportConfiguration(this.backupHost,
                this.backupPort);

        HornetQConnectionFactory conn;

        if (main == null) {
            throw new RuntimeException(
                "both the configuration for the main and backup servers are null! You must specify at least 'host' and 'port.'");
        }

        conn = (backup == null) ? new HornetQConnectionFactory(main)
                                : new HornetQConnectionFactory(main, backup);
        conn.setFailoverOnServerShutdown(this.failoverOnServerShutdown);
        conn.setReconnectAttempts(this.reconnectAttempts);

        return conn;
    }

    @Override
    public Class<?> getObjectType() {
        return HornetQConnectionFactory.class;
    }

    @Override
    protected HornetQConnectionFactory createInstance()
        throws Exception {
        return this.factoryHornetQConnectionFactory();
    }

    public void setHost(final String host) {
        this.host = host;
    }

    public void setBackupHost(final String backupHost) {
        this.backupHost = backupHost;
    }

    public void setPort(final int port) {
        this.port = port;
    }

    public void setBackupPort(final int backupPort) {
        this.backupPort = backupPort;
    }

    public void setFailoverOnServerShutdown(final boolean failoverOnServerShutdown) {
        this.failoverOnServerShutdown = failoverOnServerShutdown;
    }

    public void setReconnectAttempts(final int reconnectAttempts) {
        this.reconnectAttempts = reconnectAttempts;
    }
}
