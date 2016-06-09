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

package org.symphonyoss.symphony.clients.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
;
import org.symphonyoss.symphony.agent.api.MessagesApi;
import org.symphonyoss.symphony.agent.invoker.ApiClient;
import org.symphonyoss.symphony.agent.model.Message;
import org.symphonyoss.symphony.agent.model.MessageList;
import org.symphonyoss.symphony.agent.model.MessageSubmission;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.symphony.pod.model.Stream;



/**
 * Created by Frank Tarsillo on 5/15/2016.
 */
public class MessagesClientImpl implements org.symphonyoss.symphony.clients.MessagesClient {

    private ApiClient apiClient;
    private SymAuth symAuth;
    private String agentUrl;
    private Logger logger = LoggerFactory.getLogger(MessagesClientImpl.class);

    public MessagesClientImpl(SymAuth symAuth, String agentUrl) {

        this.symAuth = symAuth;
        this.agentUrl = agentUrl;


        //Get Service client to query for userID.
        apiClient = org.symphonyoss.symphony.agent.invoker.Configuration.getDefaultApiClient();
        apiClient.setBasePath(agentUrl);

    }


    public Message sendMessage(Stream stream, MessageSubmission message) throws Exception {


        MessagesApi messagesApi = new MessagesApi(apiClient);


        return messagesApi.v1StreamSidMessageCreatePost(stream.getId(), symAuth.getSessionToken().getToken(), symAuth.getKeyToken().getToken(), message);

    }


    public MessageList getMessagesFromStream(Stream stream, Long since, Integer offset, Integer maxMessages) throws Exception {


        MessagesApi messagesApi = new MessagesApi(apiClient);

        return messagesApi.v1StreamSidMessageGet(stream.getId(), since, symAuth.getSessionToken().getToken(), symAuth.getKeyToken().getToken(), offset, maxMessages);


    }


}
