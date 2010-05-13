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

import com.shopzilla.spring.messaging.hornetq.HornetQConnectionFactoryFactory;
import com.shopzilla.spring.util.config.ShopzillaNamespaceUtils;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;


/**
 * This configures a no-frills {@link javax.jms.ConnectionFactory} for JBoss's HornetQ message broker.
 * This, in tandem with the META-INF/spring.(handlers|schemas) files, teaches Spring how to handle a
 * given namespace. You may specify a primary and a backup host for the message queue using the
 * 'host,' 'port,' 'backup-host,' and 'backup-port' properties
 *
 * @author Josh Long
 */
public class HornetQNamespaceHandler extends NamespaceHandlerSupport {

    public void init() {
        registerBeanDefinitionParser("connection-factory", new HornetQConnectionFactoryFactoryBeanDefinitionParser());
    }

    /**
     * parser for the hornetq:connection-factory element.
     */
    static class HornetQConnectionFactoryFactoryBeanDefinitionParser extends AbstractBeanDefinitionParser {

        private ShopzillaNamespaceUtils szNamespaceUtils = new ShopzillaNamespaceUtils();

        @Override
        protected AbstractBeanDefinition parseInternal(final Element element,
                                                       final ParserContext parserContext) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(HornetQConnectionFactoryFactory.class.getName());
            szNamespaceUtils.setValueIfAttributeDefined(builder, element, "host", "host");
            szNamespaceUtils.setValueIfAttributeDefined(builder, element, "backup-host", "backupHost");
            szNamespaceUtils.setValueIfAttributeDefined(builder, element, "port", "port");
            szNamespaceUtils.setValueIfAttributeDefined(builder, element, "backup-port", "backupPort");
            szNamespaceUtils.setValueIfAttributeDefined(builder, element, "reconnect-attempts", "reconnectAttempts");
            szNamespaceUtils.setValueIfAttributeDefined(builder, element, "failover-on-server-shutdown", "failoverOnServerShutdown");
            return builder.getBeanDefinition();
        }
    }
}