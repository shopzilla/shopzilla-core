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
package com.shopzilla.spring.messaging.jms.connection;

import org.springframework.jms.connection.UserCredentialsConnectionFactoryAdapter;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.XAConnection;
import javax.jms.XAConnectionFactory;

/**
 * This works in very much the same fashion as {@link org.springframework.jms.connection.UserCredentialsConnectionFactoryAdapter} from
 * the Spring Framework. This also deals with XA connections, however.
 *
 * @author Josh Long
 * @since May 11, 2010
 *
 */
public class UserCredentialsXAConnectionFactoryAdapter
    extends UserCredentialsConnectionFactoryAdapter implements XAConnectionFactory {
    private String user;
    private String pw;
    private XAConnectionFactory xaConnectionFactory;

    @Override
    public void setTargetConnectionFactory(ConnectionFactory targetConnectionFactory) {
        super.setTargetConnectionFactory(targetConnectionFactory);

        if (!(targetConnectionFactory instanceof XAConnectionFactory)) {
            throw new RuntimeException(
                "the targetConnectionFactory isn't an javax.jms.XAConnectionFactory instance!");
        }

        this.xaConnectionFactory = (XAConnectionFactory) targetConnectionFactory;
    }

    @Override
    public void setPassword(String password) {
        super.setPassword(password);
        this.pw = password;
    }

    @Override
    public void setUsername(String username) {
        super.setUsername(username);
        this.user = username;
    }

    protected XAConnection doCreateXAConnection(String user, String pw)
        throws JMSException {
        return this.xaConnectionFactory.createXAConnection(user, pw);
    }

    @Override
    public final XAConnection createXAConnection() throws JMSException {
        return this.doCreateXAConnection(this.user, this.pw);
    }

    @Override
    public final XAConnection createXAConnection(String s, String s1)
        throws JMSException {
        return this.doCreateXAConnection(s, s1);
    }
}