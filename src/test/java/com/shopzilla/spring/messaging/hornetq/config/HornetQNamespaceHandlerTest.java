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
package com.shopzilla.spring.messaging.hornetq.config;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;


/**
 *
 * A unit test for the {@link com.shopzilla.spring.messaging.hornetq.config.HornetQNamespaceHandler} code which teaches Spring how to parse a relevant fragment of XML 
 *
 * @author Josh Long
 */
public class HornetQNamespaceHandlerTest {
    private HornetQNamespaceHandler hornetQNamespaceHandler;
    private HornetQNamespaceHandler.HornetQConnectionFactoryFactoryBeanDefinitionParser hornetQConnectionFactoryFactoryBeanDefinitionParser;
    private Mockery context = new Mockery() {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    private String host = "host";
    private String backupHost = "backupHost";
    private String port = "" + 1;
    private String backupPort = "" + 10;
    private String reconnectAttempts = "10";
    private String failover = "true";

    @Before
    public void before() throws Throwable {
        this.hornetQNamespaceHandler = new HornetQNamespaceHandler();
        this.hornetQConnectionFactoryFactoryBeanDefinitionParser = new HornetQNamespaceHandler.HornetQConnectionFactoryFactoryBeanDefinitionParser();
    }

    @Test
    public void testParseInternal() throws Throwable {
        final Element elem = this.context.mock(Element.class);
        final ParserContext parserContext = null;
        this.context.checking(new Expectations() {
            {
                one(elem).getAttribute("host");
                will(returnValue(host));
                one(elem).getAttribute("backup-host");
                will(returnValue(backupHost));
                one(elem).getAttribute("port");
                will(returnValue(port));
                one(elem).getAttribute("backup-port");
                will(returnValue(backupPort));
                one(elem).getAttribute("reconnect-attempts");
                will(returnValue(reconnectAttempts));
                one(elem).getAttribute("failover-on-server-shutdown");
                will(returnValue(failover));
            }
        });
        hornetQConnectionFactoryFactoryBeanDefinitionParser.parseInternal(elem, parserContext);
    }

    @Test
    public void testInit() throws Throwable {
        this.context.checking(new Expectations() {
        });
        this.hornetQNamespaceHandler.init();
    }

    @After
    public void after() throws Throwable {
        this.context.assertIsSatisfied();
    }
}
