/*
 *
 *  *
 *  * Copyright 2016 The Symphony Software Foundation
 *  *
 *  * Licensed to The Symphony Software Foundation (SSF) under one
 *  * or more contributor license agreements.  See the NOTICE file
 *  * distributed with this work for additional information
 *  * regarding copyright ownership.  The ASF licenses this file
 *  * to you under the Apache License, Version 2.0 (the
 *  * "License"); you may not use this file except in compliance
 *  * with the License.  You may obtain a copy of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *
 */

package org.symphonyoss.symphony.clients.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.exceptions.FirehoseException;
import org.symphonyoss.symphony.agent.api.FirehoseApi;
import org.symphonyoss.symphony.agent.invoker.ApiClient;
import org.symphonyoss.symphony.agent.invoker.ApiException;
import org.symphonyoss.symphony.agent.model.Firehose;
import org.symphonyoss.symphony.agent.model.V2BaseMessage;
import org.symphonyoss.symphony.clients.FirehoseClient;

import javax.ws.rs.client.Client;
import java.util.List;


/**
 * Created by Frank Tarsillo on 5/15/2016.
 */
public class FirehoseClientImpl implements FirehoseClient {

    private final ApiClient apiClient;
    private final SymAuth symAuth;
    private Logger logger = LoggerFactory.getLogger(FirehoseClientImpl.class);




    public FirehoseClientImpl(SymAuth symAuth, String agentUrl) {

        this.symAuth = symAuth;


        //Get Service client to query for userID.
        apiClient = org.symphonyoss.symphony.agent.invoker.Configuration.getDefaultApiClient();
        apiClient.setBasePath(agentUrl);

    }
    /**
     * If you need to override HttpClient.  Important for handling individual client certs.
     * @param symAuth
     * @param serviceUrl
     * @param httpClient
     */
    public FirehoseClientImpl(SymAuth symAuth, String serviceUrl, Client httpClient) {
        this.symAuth = symAuth;

        //Get Service client to query for userID.
        apiClient = org.symphonyoss.symphony.agent.invoker.Configuration.getDefaultApiClient();
        apiClient.setHttpClient(httpClient);
        apiClient.setBasePath(serviceUrl);

    }

    @Override
    public Firehose createFirehose() throws FirehoseException {

        FirehoseApi firehoseApi = new FirehoseApi(apiClient);


        try {
            return firehoseApi.v1FirehoseCreatePost(symAuth.getSessionToken().getToken(), symAuth.getKeyToken().getToken());
        } catch (ApiException e) {
            throw new FirehoseException("Could not start firehose..", e);
        }
    }




    //This will return messages by TYPE.
    @Override
    public List<V2BaseMessage> getMessagesFromFirehose(Firehose firehose) throws FirehoseException {

        FirehoseApi firehoseApi = new FirehoseApi(apiClient);

        if (firehose == null) {
            throw new NullPointerException("Firehose was not provided and null..");
        }

        //V2MessageList messageList = null;
        try {
            return firehoseApi.v2FirehoseIdReadGet(firehose.getId(),symAuth.getSessionToken().getToken(), symAuth.getKeyToken().getToken(),100);
        } catch (ApiException e) {
            throw new FirehoseException("Failed to retrieve messages from firehose...", e);
        }


    }




}
