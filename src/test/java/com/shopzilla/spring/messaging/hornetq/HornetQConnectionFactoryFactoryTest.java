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

import org.hornetq.jms.client.HornetQConnectionFactory;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 *
 * Unit test for the {@link org.hornetq.jms.client.HornetQConnectionFactory} implementation.
 *
 * @author  Josh Long
 */
public class HornetQConnectionFactoryFactoryTest {
    
    private String backupHost = "backupHost";
    private String host = "host";
    private int port = 80;
    private int backupPort = 81;
    private boolean failoverOnShutdown = true;
    private int reconnectAttempts = 10;
    private HornetQConnectionFactoryFactory hornetQConnectionFactoryFactory;
    private Mockery context = new Mockery() {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    @Test
    public void testCreatingInstance() throws Throwable {
        this.context.checking(new Expectations() {
            {
            }
        });
        HornetQConnectionFactory hornetQConnectionFactoryFactory = this.hornetQConnectionFactoryFactory.createInstance();
        Assert.assertNotNull(hornetQConnectionFactoryFactory);
    }
    @Test
      public void testStipulatingJustBadPort() throws Throwable {
          this.context.checking(new Expectations() {
              {
              }
          });

          this.hornetQConnectionFactoryFactory.setBackupPort( 00);
          this.hornetQConnectionFactoryFactory.setBackupHost("okhost");


          HornetQConnectionFactory hornetQConnectionFactoryFactory = this.hornetQConnectionFactoryFactory.createInstance();
          Assert.assertNotNull(hornetQConnectionFactoryFactory);
      }

    @Test
    public void testStipulatingJustBadHost() throws Throwable {
        this.context.checking(new Expectations() {
            {
            }
        });

        this.hornetQConnectionFactoryFactory.setBackupPort(100);
        this.hornetQConnectionFactoryFactory.setBackupHost(null);


        HornetQConnectionFactory hornetQConnectionFactoryFactory = this.hornetQConnectionFactoryFactory.createInstance();
        Assert.assertNotNull(hornetQConnectionFactoryFactory);
    }


    @Test
    public void testNotStipulatingBackup() throws Throwable {
        this.context.checking(new Expectations() {
            {
            }
        });

        this.hornetQConnectionFactoryFactory.setBackupPort(0);
        this.hornetQConnectionFactoryFactory.setBackupHost(null);


        HornetQConnectionFactory hornetQConnectionFactoryFactory = this.hornetQConnectionFactoryFactory.createInstance();
        Assert.assertNotNull(hornetQConnectionFactoryFactory);
    }


    @Test(expected = RuntimeException.class)
    public void testNotStipulatingMainServer() throws Throwable {
        this.context.checking(new Expectations() {
            {
            }
        });

        this.hornetQConnectionFactoryFactory.setHost(null);
        this.hornetQConnectionFactoryFactory.setPort(0);


        HornetQConnectionFactory hornetQConnectionFactoryFactory = this.hornetQConnectionFactoryFactory.createInstance();
        Assert.assertNotNull(hornetQConnectionFactoryFactory);
    }

    @Test
    public void testClassTypeRetreival() throws Throwable {
        Assert.assertEquals(this.hornetQConnectionFactoryFactory.getObjectType(), HornetQConnectionFactory.class);
    }

    @Before
    public void before() throws Throwable {
        this.hornetQConnectionFactoryFactory = new HornetQConnectionFactoryFactory();
        this.hornetQConnectionFactoryFactory.setBackupHost(this.backupHost);
        this.hornetQConnectionFactoryFactory.setBackupPort(this.backupPort);
        this.hornetQConnectionFactoryFactory.setHost(this.host);
        this.hornetQConnectionFactoryFactory.setPort(this.port);
        this.hornetQConnectionFactoryFactory.setReconnectAttempts(reconnectAttempts);
        this.hornetQConnectionFactoryFactory.setFailoverOnServerShutdown(failoverOnShutdown);
    }


    @After
    public void after() throws Throwable {
        this.context.assertIsSatisfied();
    }
}