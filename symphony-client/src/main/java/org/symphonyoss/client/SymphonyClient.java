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

package org.symphonyoss.client;

import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.symphony.clients.*;
import org.symphonyoss.symphony.pod.model.User;
import org.symphonyoss.client.services.*;

/**
 * Created by Frank Tarsillo on 5/14/2016.
 */

public interface SymphonyClient {

    SymAuth getSymAuth();

    void setSymAuth(SymAuth symAuth);

    MessageService getMessageService();

    PresenceService getPresenceService();

    PresenceClient getPresenceClient();

    StreamsClient getStreamsClient();

    UsersClient getUsersClient();

    ChatService getChatService();

    MessagesClient getMessagesClient();

    DataFeedClient getDataFeedClient();

    boolean init(SymAuth symAuth, String email, String agentUrl, String serviceUrl) throws Exception;

    User getLocalUser();

    String getAgentUrl();

    String getServiceUrl();



}
