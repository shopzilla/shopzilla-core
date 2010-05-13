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

package com.shopzilla.spring.util.config;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.w3c.dom.Element;


/**
 *
 * Unit tests for {@link com.shopzilla.spring.util.config.ShopzillaNamespaceUtils} 
 *
 * @author Josh Long
 *
 */
public class ShopzillaNamespaceUtilsTest {
    private Mockery context = new Mockery() {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    private BeanDefinitionBuilder beanDefinitionBuilder;
    private Element element;
    private ShopzillaNamespaceUtils shopzillaNamespaceUtils;

    @Before
    public void before() throws Throwable {
        this.shopzillaNamespaceUtils = new ShopzillaNamespaceUtils();
        this.beanDefinitionBuilder = this.context.mock(BeanDefinitionBuilder.class);
        this.element = this.context.mock(Element.class);
    }

    @After
    public void after() throws Throwable {
        this.context.assertIsSatisfied();
    }

    @Test
    public void testSetValueIfAttributeDefined4ParamWithEmptyAttribute()
        throws Throwable {
        final String attr = "attr";
        final String prop = "prop";
        this.context.checking(new Expectations() {
            {
                one(element).getAttribute(attr);
                will(returnValue(null));
            }
        });

        this.shopzillaNamespaceUtils.setValueIfAttributeDefined(beanDefinitionBuilder, element, attr, prop);
    }

    @Test
    public void testSetValueIfAttributeDefined4Param()
        throws Throwable {
        final String attr = "attr";
        final String prop = "prop";
        final String val = "value";
        this.context.checking(new Expectations() {
            {
                one(element).getAttribute(attr);
                will(returnValue(val));
                one(beanDefinitionBuilder).addPropertyValue(prop, val);
            }
        });
        this.shopzillaNamespaceUtils.setValueIfAttributeDefined(beanDefinitionBuilder, element, attr, prop);
    }

    @Test
    public void testSetValueIfAttributeDefined3Param()
        throws Throwable {
        final String attr = "attr";
        final String prop = "attr";
        final String val = "value";
        this.context.checking(new Expectations() {
            {
                one(element).getAttribute(attr);
                will(returnValue(val));
                one(beanDefinitionBuilder).addPropertyValue(prop, val);
            }
        });
        this.shopzillaNamespaceUtils.setValueIfAttributeDefined(beanDefinitionBuilder, element, attr);
    }

    @Test
    public void testSetReferenceIfAttributeDefinedWithNullAttribute()
        throws Throwable {
        final String attr = "attr";
        final String prop = "attr";
        this.context.checking(new Expectations() {
            {
                one(element).getAttribute(attr);
                will(returnValue(null));
            }
        });
        this.shopzillaNamespaceUtils.setReferenceIfAttributeDefined(beanDefinitionBuilder, element, attr, prop);
    }

    @Test
    public void testSetReferenceIfAttributeDefined() throws Throwable {
        final String attr = "attr";
        final String prop = "attr";
        final String val = "value";
        this.context.checking(new Expectations() {
            {
                one(element).getAttribute(attr);
                will(returnValue(val));
                one(beanDefinitionBuilder).addPropertyReference(prop, val);
            }
        });
        this.shopzillaNamespaceUtils.setReferenceIfAttributeDefined(beanDefinitionBuilder, element, attr, prop);
    }

    @Test
    public void testSetReferenceIfAttributeDefined3Param()
        throws Throwable {
        final String attr = "attr";
        final String prop = "attr";
        final String val = "value";
        this.context.checking(new Expectations() {
            {
                one(element).getAttribute(attr);
                will(returnValue(val));
                one(beanDefinitionBuilder).addPropertyReference(prop, val);
            }
        });
        this.shopzillaNamespaceUtils.setReferenceIfAttributeDefined(beanDefinitionBuilder, element, attr);
    }

    @Test
    public void testCreateElementDescriptionWithNullID()
        throws Throwable {
        this.context.checking(new Expectations() {
            {
                one(element).getNodeName();
                will(returnValue("nodeName"));
                one(element).getAttribute("id");
                will(returnValue(null));
            }
        });
        this.shopzillaNamespaceUtils.createElementDescription(element);
    }

    @Test
    public void testCreateElementDescription() throws Throwable {
        this.context.checking(new Expectations() {
            {
                one(element).getNodeName();
                will(returnValue("nodeName"));
                one(element).getAttribute("id");
                will(returnValue("id"));
            }
        });
        this.shopzillaNamespaceUtils.createElementDescription(element);
    }
}
