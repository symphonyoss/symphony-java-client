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
import org.symphonyoss.client.SymphonyClientConfig;
import org.symphonyoss.client.SymphonyClientConfigID;
import org.symphonyoss.client.exceptions.PresenceException;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.symphony.clients.model.SymPresence;
import org.symphonyoss.symphony.clients.model.SymPresenceFeed;
import org.symphonyoss.symphony.clients.model.SymUser;
import org.symphonyoss.symphony.pod.api.PresenceApi;
import org.symphonyoss.symphony.pod.invoker.ApiClient;
import org.symphonyoss.symphony.pod.invoker.ApiException;
import org.symphonyoss.symphony.pod.model.V2PresenceStatus;

import javax.ws.rs.client.Client;
import java.util.List;


/**
 * @author Frank Tarsillo
 */
public class PresenceClientImpl implements org.symphonyoss.symphony.clients.PresenceClient {
    private final SymAuth symAuth;
    private final ApiClient apiClient;

    private Logger logger = LoggerFactory.getLogger(PresenceClientImpl.class);


    /**
     * Init
     *
     * @param symAuth Authorization object holding session and key tokens
     * @param config  Symphony client config
     */
    public PresenceClientImpl(SymAuth symAuth, SymphonyClientConfig config) {

        this(symAuth, config, null);

    }

    /**
     * If you need to override HttpClient.  Important for handling individual client certs.
     *
     * @param symAuth    Authorization object holding session and key tokens
     * @param config     Symphony client config
     * @param httpClient The HttpClient to use when calling Symphony API
     */
    public PresenceClientImpl(SymAuth symAuth, SymphonyClientConfig config, Client httpClient) {
        this.symAuth = symAuth;

        //Get Service client to query for userID.
        apiClient = org.symphonyoss.symphony.pod.invoker.Configuration.getDefaultApiClient();

        if (httpClient != null)
            apiClient.setHttpClient(httpClient);

        apiClient.setBasePath(config.get(SymphonyClientConfigID.POD_URL));

    }


    @Override
    public SymPresence getUserPresence(Long userId, Boolean local) throws PresenceException{

        SymUser symUser = new SymUser();
        symUser.setId(userId);

        return getUserPresence(symUser, local);

    }


    @Override
    public SymPresence getUserPresence(SymUser symUser, Boolean local) throws PresenceException {

        PresenceApi presenceApi = new PresenceApi(apiClient);

        if (symUser == null || symUser.getId() == null) {
            throw new NullPointerException("UserId was not provided...");
        }


        try {

            //This should return V2Presence..but returning Presence instead..need to contact LLC.
            return SymPresence.toSymPresence(presenceApi.v2UserUidPresenceGet(symUser.getId(),symAuth.getSessionToken().getToken(), local));
        } catch (ApiException e) {
            throw new PresenceException("Failed to retrieve user presence for ID: " + symUser.getId(), e);
        }
    }

    /**
     * Obtain a presence feed id to attach to.
     *
     * @return SymPresenceFeed
     */
    @Override
    public SymPresenceFeed createPresenceFeed() throws PresenceException {
        PresenceApi presenceApi = new PresenceApi(apiClient);

        try {

            return SymPresenceFeed.toSymPresenceFeed(presenceApi.v1PresenceFeedCreatePost(symAuth.getSessionToken().getToken()));

        } catch (ApiException e) {
            throw new PresenceException("Failed to retrieve presence ID..", e);
        }


    }

    /**
     * Remove a presence feed
     *
     * @param symPresenceFeed Feed id to remove
     *
     */
    @Override
    public void removePresenceFeed(SymPresenceFeed symPresenceFeed) throws PresenceException {

        if (symPresenceFeed == null || symPresenceFeed.getId() == null)
            throw new NullPointerException("SymPresence was null...");

        PresenceApi presenceApi = new PresenceApi(apiClient);


        try {

            presenceApi.v1PresenceFeedFeedIdDeletePost(symAuth.getSessionToken().getToken(), symPresenceFeed.getId());

        } catch (ApiException e) {
            throw new PresenceException("Failed to remove presence ID..", e);
        }


    }


    /**
     * Obtain a presence feed id to attach to.
     *
     * @return SymPresenceFeed
     */
    @Override
    public List<SymPresence> getPresenceFeedUpdates(SymPresenceFeed symPresenceFeed) throws PresenceException {

        if (symPresenceFeed == null || symPresenceFeed.getId() == null)
            return null;

        PresenceApi presenceApi = new PresenceApi(apiClient);


        try {

            return SymPresence.toSymPresence(presenceApi.v1PresenceFeedFeedIdReadGet(symAuth.getSessionToken().getToken(), symPresenceFeed.getId()));


        } catch (ApiException e) {
            throw new PresenceException("Failed to remove presence ID..", e);
        }


    }


    @Override
    public SymPresence setUserPresence(SymPresence presence) throws PresenceException {


        if( presence == null)
            return null;

        PresenceApi presenceApi = new PresenceApi(apiClient);

        try {

            V2PresenceStatus v2PresenceStatus = new V2PresenceStatus();
            v2PresenceStatus.setCategory(presence.getCategory().toString());

            return SymPresence.toSymPresence(presenceApi.v2UserPresencePost(symAuth.getSessionToken().getToken(), v2PresenceStatus));

        } catch (ApiException e) {
            throw new PresenceException("Failed to set presence for user.", e);
        }


    }


}
