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
import org.symphonyoss.exceptions.UserNotFoundException;
import org.symphonyoss.exceptions.UsersClientException;
import org.symphonyoss.symphony.clients.model.SymUser;
import org.symphonyoss.symphony.pod.api.UsersApi;
import org.symphonyoss.symphony.pod.invoker.ApiClient;
import org.symphonyoss.symphony.pod.invoker.ApiException;
import org.symphonyoss.symphony.pod.model.UserV2;


/**
 * Created by Frank Tarsillo on 5/15/2016.
 */
public class UsersClientImpl implements org.symphonyoss.symphony.clients.UsersClient {
    private final SymAuth symAuth;
    private final ApiClient apiClient;

    private final Logger logger = LoggerFactory.getLogger(UsersClientImpl.class);


    public UsersClientImpl(SymAuth symAuth, String serviceUrl) {

        this.symAuth = symAuth;


        //Get Service client to query for userID.
        apiClient = org.symphonyoss.symphony.pod.invoker.Configuration.getDefaultApiClient();
        apiClient.setBasePath(serviceUrl);

        apiClient.addDefaultHeader(symAuth.getSessionToken().getName(), symAuth.getSessionToken().getToken());
        apiClient.addDefaultHeader(symAuth.getKeyToken().getName(), symAuth.getKeyToken().getToken());

    }

    public SymUser getUserFromEmail(String email) throws UsersClientException {


        UsersApi usersApi = new UsersApi(apiClient);

        if (email == null)
            throw new NullPointerException("Email was null");

        UserV2 user;
        try {
            user = usersApi.v2UserGet(symAuth.getSessionToken().getToken(), null, email, null, false);
        } catch (ApiException e) {
            throw new UsersClientException("API Error communicating with POD, while retrieving user details for " + email, e);
        }

        if (user != null) {

            logger.debug("Found User: {}:{}", user.getEmailAddress(), user.getId());
            return SymUser.toSymUser(user);
        }


        logger.warn("Could not locate user: {}", email);

        throw new UserNotFoundException("Could not find user from email: " + email);


    }

    public SymUser getUserFromId(Long userId) throws UsersClientException {

        UsersApi usersApi = new UsersApi(apiClient);


        if (userId == null)
            throw new NullPointerException("UserId was null...");

        UserV2 user;
        try {
            user =  usersApi.v2UserGet(symAuth.getSessionToken().getToken(), userId, null, null, false);
        }catch(ApiException e){
            throw new UsersClientException("API Error communicating with POD, while retrieving user details for " + userId, e);
        }

        if (user != null) {

            logger.debug("Found User: {}:{}", user.getDisplayName(), user.getId());
            return SymUser.toSymUser(user);
        }


        throw new UserNotFoundException("Could not find user from ID: " + userId);


    }


    @Override
    public SymUser getUserFromName(String userName) throws UsersClientException {

        UsersApi usersApi = new UsersApi(apiClient);


        if (userName == null)
            throw new NullPointerException("User name was null...");

        UserV2 user;
        try {
            user =  usersApi.v2UserGet(symAuth.getSessionToken().getToken(), null, null, userName, false);
        }catch(ApiException e){
            throw new UsersClientException("API Error communicating with POD, while retrieving user details for " + userName, e);
        }

        if (user != null) {

            logger.debug("Found User: {}:{}", user.getEmailAddress(), user.getId());
            return SymUser.toSymUser(user);
        }


        throw new UserNotFoundException("Could not find user from user name: " + userName);
    }

}
