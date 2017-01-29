/*
 *
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
 *
 */

package org.symphonyoss.client.ai;

import org.symphonyoss.client.util.MlMessageParser;
import org.symphonyoss.symphony.clients.model.SymMessage;

/**
 * An interface that developers can implement to create their own actions
 *
 * @author Nicholas Tarsillo
 */
public interface AiAction {

    /**
     * Provides a sequence of responses to send back to user executing command
     *
     * @param mlMessageParser Parser used to process incoming message
     * @param message         Message sent from user
     * @param command         Command being requested by user
     * @return {@link AiResponseSequence}
     */
    AiResponseSequence respond(MlMessageParser mlMessageParser, SymMessage message, AiCommand command);
}
