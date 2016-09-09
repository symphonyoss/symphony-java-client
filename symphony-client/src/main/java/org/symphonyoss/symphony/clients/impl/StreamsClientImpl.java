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
import org.symphonyoss.symphony.clients.UsersClient;
import org.symphonyoss.symphony.clients.UsersFactory;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.symphony.clients.model.SymUser;
import org.symphonyoss.symphony.pod.api.StreamsApi;
import org.symphonyoss.symphony.pod.invoker.ApiClient;
import org.symphonyoss.symphony.pod.model.*;

import java.util.Set;


/**
 * Created by Frank Tarsillo on 5/15/2016.
 */
public class StreamsClientImpl implements org.symphonyoss.symphony.clients.StreamsClient {
    private final SymAuth symAuth;
    private final String serviceUrl;
    private final ApiClient apiClient;

    private final Logger logger = LoggerFactory.getLogger(StreamsClientImpl.class);


    public StreamsClientImpl(SymAuth symAuth, String serviceUrl) {

        this.symAuth = symAuth;
        this.serviceUrl = serviceUrl;


        //Get Service client to query for userID.
        apiClient = org.symphonyoss.symphony.pod.invoker.Configuration.getDefaultApiClient();
        apiClient.setBasePath(serviceUrl);

        apiClient.addDefaultHeader(symAuth.getSessionToken().getName(), symAuth.getSessionToken().getToken());
        apiClient.addDefaultHeader(symAuth.getKeyToken().getName(), symAuth.getKeyToken().getToken());

    }


    public Stream getStream(SymUser user) throws Exception {


        UserIdList userIdList = new UserIdList();
        userIdList.add(user.getId());

        Stream stream = getStream(userIdList);
        logger.debug("Stream ID for one to one chat: {}:{} ", user.getEmailAddress(), stream.getId());

        return stream;


    }

    public Stream getStream(Set<SymUser> users) throws Exception {
        UserIdList userIdList = new UserIdList();
        String usersPrint = "";

        for (SymUser user : users) {
            userIdList.add(user.getId());
            usersPrint += " [" + user.getEmailAddress() + "] ";
        }


        Stream stream = getStream(userIdList);

        logger.debug("Stream ID for multi-party chat: {}:{} ", usersPrint, stream.getId());

        return stream;


    }


    public Stream getStream(UserIdList userIdList) throws Exception {
        StreamsApi streamsApi = new StreamsApi(apiClient);
        return streamsApi.v1ImCreatePost(userIdList, symAuth.getSessionToken().getToken());

    }

    public Stream getStreamFromEmail(String email) throws Exception {

        UsersClient usersClient = UsersFactory.getClient(symAuth,serviceUrl, UsersFactory.TYPE.DEFAULT);



        return getStream(usersClient.getUserFromEmail(email));
    }


    public RoomDetail getRoomDetail(String id) throws Exception{

        StreamsApi streamsApi = new StreamsApi(apiClient);

        return streamsApi.v1RoomIdInfoGet(id,symAuth.getSessionToken().getToken());

    }





}
