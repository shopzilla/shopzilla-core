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

import java.util.Collection;

import javax.jms.Message;


/**
 * Similar to a JMS message listener, but receives a collection of messages.
 *
 * @author  Tim Morrow
 * @since  Sep 27, 2006
 */
public interface BatchMessageListener {

    /**
     * Invoked when a batch of messages are ready.
     *
     * @param  messages  the messages
     */
    void onMessages(Collection<Message> messages);
}
  