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
import org.symphonyoss.symphony.agent.api.DatafeedApi;
import org.symphonyoss.symphony.agent.invoker.ApiClient;
import org.symphonyoss.symphony.agent.model.Datafeed;
import org.symphonyoss.symphony.agent.model.MessageList;
import org.symphonyoss.symphony.clients.DataFeedClient;
import org.symphonyoss.client.model.SymAuth;



/**
 * Created by Frank Tarsillo on 5/15/2016.
 */
public class DataFeedClientImpl implements DataFeedClient {

    private ApiClient apiClient;
    private SymAuth symAuth;
    private String agentUrl;
    private Logger logger = LoggerFactory.getLogger(DataFeedClientImpl.class);

    public DataFeedClientImpl(SymAuth symAuth, String agentUrl) {

        this.symAuth = symAuth;
        this.agentUrl = agentUrl;


        //Get Service client to query for userID.
        apiClient = org.symphonyoss.symphony.agent.invoker.Configuration.getDefaultApiClient();
        apiClient.setBasePath(agentUrl);

    }


    public Datafeed createDatafeed() throws Exception {

        DatafeedApi datafeedApi = new DatafeedApi(apiClient);

        return datafeedApi.v1DatafeedCreatePost(symAuth.getSessionToken().getToken(), symAuth.getKeyToken().getToken());
    }


    public MessageList getMessagesFromDatafeed(Datafeed datafeed) throws Exception {

        DatafeedApi datafeedApi = new DatafeedApi(apiClient);

        return datafeedApi.v1DatafeedIdReadGet(datafeed.getId(),symAuth.getSessionToken().getToken(), symAuth.getKeyToken().getToken(),100);
    }

}
