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
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 *
 */

package org.symphonyoss.symphony.clients.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.exceptions.ShareException;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.client.model.SymShareArticle;
import org.symphonyoss.symphony.agent.api.ShareApi;
import org.symphonyoss.symphony.agent.invoker.ApiException;
import org.symphonyoss.symphony.agent.model.ShareContent;
import org.symphonyoss.symphony.clients.ShareClient;
import org.symphonyoss.symphony.agent.invoker.ApiClient;

import javax.ws.rs.client.Client;


/**
 *
 * @author Frank Tarsillo on 10/22/2016.
 */
public class ShareClientImpl implements ShareClient{

    private final SymAuth symAuth;
    @SuppressWarnings("unused")
    private final String podUrl;
    private final ApiClient apiClient;

    @SuppressWarnings("unused")
    private final Logger logger = LoggerFactory.getLogger(StreamsClientImpl.class);

    public ShareClientImpl(SymAuth symAuth, String podUrl) {

        this.symAuth = symAuth;
        this.podUrl = podUrl;


        //Get Service client to query for userID.
        apiClient = org.symphonyoss.symphony.agent.invoker.Configuration.getDefaultApiClient();
        apiClient.setBasePath(podUrl);

        apiClient.addDefaultHeader(symAuth.getSessionToken().getName(), symAuth.getSessionToken().getToken());
       // apiClient.addDefaultHeader(symAuth.getKeyToken().getName(), symAuth.getKeyToken().getToken());

    }

    /**
     * If you need to override HttpClient.  Important for handling individual client certs.
     * @param symAuth Authorization model containing session and key tokens
     * @param podUrl Service URL used to access API
     * @param httpClient Custom HTTP client
     */
    public ShareClientImpl(SymAuth symAuth, String podUrl, Client httpClient) {
        this.symAuth = symAuth;
        this.podUrl = podUrl;

        //Get Service client to query for userID.
        apiClient = org.symphonyoss.symphony.agent.invoker.Configuration.getDefaultApiClient();
        apiClient.setHttpClient(httpClient);
        apiClient.setBasePath(podUrl);

        apiClient.addDefaultHeader(symAuth.getSessionToken().getName(), symAuth.getSessionToken().getToken());
        apiClient.addDefaultHeader(symAuth.getKeyToken().getName(), symAuth.getKeyToken().getToken());


    }


    @Override
    public void shareArticle(String streamId, SymShareArticle article) throws ShareException{

        ShareApi shareApi = new ShareApi(apiClient);

        ShareContent shareContent = new ShareContent();
        shareContent.setContent(SymShareArticle.toShareArticle(article));
        shareContent.setType("com.symphony.sharing.article");

        try {
            shareApi.v1StreamSidSharePost(streamId, symAuth.getSessionToken().getToken(),symAuth.getKeyToken().getToken(),shareContent);
        } catch (ApiException e) {
          throw new ShareException(e);
        }


    }
}
