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
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.exceptions.PresenceException;
import org.symphonyoss.symphony.pod.api.PresenceApi;
import org.symphonyoss.symphony.pod.invoker.ApiClient;
import org.symphonyoss.symphony.pod.invoker.ApiException;
import org.symphonyoss.symphony.pod.invoker.Pair;
import org.symphonyoss.symphony.pod.model.Presence;
import org.symphonyoss.symphony.pod.model.PresenceList;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.GenericType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author  Frank Tarsillo
 */
public class PresenceClientImpl implements org.symphonyoss.symphony.clients.PresenceClient {
    private final SymAuth symAuth;
    private final ApiClient apiClient;

    private Logger logger = LoggerFactory.getLogger(PresenceClientImpl.class);


    public PresenceClientImpl(SymAuth symAuth, String serviceUrl) {

        this.symAuth = symAuth;


        //Get Service client to query for userID.
        apiClient = org.symphonyoss.symphony.pod.invoker.Configuration.getDefaultApiClient();
        apiClient.setBasePath(serviceUrl);

        apiClient.addDefaultHeader(symAuth.getSessionToken().getName(), symAuth.getSessionToken().getToken());
        apiClient.addDefaultHeader(symAuth.getKeyToken().getName(), symAuth.getKeyToken().getToken());

    }

    /**
     * If you need to override HttpClient.  Important for handling individual client certs.
     * @param symAuth Authorization object holding session and key tokens
     * @param serviceUrl The Symphony service URL
     * @param httpClient The HttpClient to use when calling Symphony API
     */
    public PresenceClientImpl(SymAuth symAuth, String serviceUrl, Client httpClient) {
        this.symAuth = symAuth;

        //Get Service client to query for userID.
        apiClient = org.symphonyoss.symphony.pod.invoker.Configuration.getDefaultApiClient();
        apiClient.setHttpClient(httpClient);
        apiClient.setBasePath(serviceUrl);

        apiClient.addDefaultHeader(symAuth.getSessionToken().getName(), symAuth.getSessionToken().getToken());
        apiClient.addDefaultHeader(symAuth.getKeyToken().getName(), symAuth.getKeyToken().getToken());

    }


    public PresenceList getAllUserPresence() throws PresenceException {


        PresenceApi presenceApi = new PresenceApi(apiClient);


        try {
            return presenceApi.v1PresenceGet(symAuth.getSessionToken().getToken());
        } catch (ApiException e) {
            throw new PresenceException("Failed to retrieve all user presence...", e);
        }


    }

    public Presence getUserPresence(Long userId) throws PresenceException {


        PresenceApi presenceApi = new PresenceApi(apiClient);

        if (userId == null) {
            throw new NullPointerException("UserId was not provided...");
        }

        try {
            return presenceApi.v1UserUidPresenceGet(userId,symAuth.getSessionToken().getToken());
        } catch (ApiException e) {
            throw new PresenceException("Failed to retrieve user presence for ID: " + userId,e);
        }


    }

    @Override
    public Presence setUserPresence(Long userId, Presence presence) throws PresenceException {
        // INFO: This uses code from pod-api 0.9.0 as version 0.9.1 does not contain that
        //       functionality anymore. If/When Symphony put that functionality back in its
        //       pod-api, then we can invoke the set presence endpoint like done elswhere.

        if (userId == null) {
            throw new PresenceException("Failed to set user presence. User id must not be null");
        }

        if (presence == null) {
            throw new PresenceException("Failed to set user presence. Presence must not be null");
        }

        // create path and map variables
        String urlPath = "/v1/user/{uid}/presence"
                .replaceAll("\\{format\\}", "json")
                .replaceAll("\\{" + "uid" + "\\}", apiClient.escapeString(userId.toString()));

        // query params
        List<Pair> queryParams = new ArrayList<>();
        Map<String, String> headerParams = new HashMap<>();
        Map<String, Object> formParams = new HashMap<>();

        String sessionToken = symAuth.getSessionToken().getToken();

        if (sessionToken != null) {
            headerParams.put("sessionToken", apiClient.parameterToString(sessionToken));
        }

        final String[] accepts = { "application/json" };
        final String accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = {};
        final String contentType = apiClient.selectHeaderContentType(contentTypes);
        final String[] authNames = new String[] {};
        final GenericType<Presence> returnType = new GenericType<Presence>() { };

        Object postBody = presence;

        try {
            return apiClient.invokeAPI(urlPath, "POST", queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
        } catch (ApiException e) {
            throw new PresenceException("Failed to set user presence for user " + userId + ". Error while invoking Symphony API.", e);
        }
    }


}
