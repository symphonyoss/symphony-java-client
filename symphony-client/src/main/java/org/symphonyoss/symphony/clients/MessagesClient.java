/*
 *
 * Copyright 2016 The Symphony Software Foundation
 *
 * Licensed to The Symphony Software Foundation (SSF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.symphonyoss.symphony.clients;

import org.symphonyoss.exceptions.MessagesException;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.pod.model.Stream;

import java.util.List;


/**
 * @author Frank Tarsillo
 */
public interface MessagesClient {

    /**
     * Send message to stream
     * @param stream Stream to send message to
     * @param message Message to send
     * @return Message sent
     * @throws MessagesException Exception caused by Symphony API calls
     */
    SymMessage sendMessage(Stream stream, SymMessage message) throws MessagesException;

    /**
     * Retrieve historical messages from a given stream.  This is NOT a blocking call.
     *
     * @param stream Stream to retrieve messages from
     * @param since Date (long) from point in time
     * @param offset Offset
     * @param maxMessages Maximum number of messages to retrieve from the specified time (since)
     * @return List of messages
     * @throws MessagesException Exception caused by Symphony API calls
     */
    List<SymMessage> getMessagesFromStream(Stream stream, Long since, Integer offset, Integer maxMessages) throws MessagesException;
}
