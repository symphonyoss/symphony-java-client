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
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.symphonyoss.symphony.clients.impl;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.exceptions.UserManagementClientException;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.symphony.authenticator.model.Token;
import org.symphonyoss.symphony.clients.UserManagementClient;
import org.symphonyoss.symphony.clients.model.SymFeatureList;
import org.symphonyoss.symphony.pod.api.UserApi;
import org.symphonyoss.symphony.pod.invoker.ApiClient;
import org.symphonyoss.symphony.pod.invoker.ApiException;
import org.symphonyoss.symphony.pod.invoker.Configuration;

/**
 * Implementation of {@link UserManagementClient}.
 */
public class UserManagementClientImpl implements UserManagementClient {
    private static final Logger LOG = LoggerFactory.getLogger(UserManagementClientImpl.class);

    private static final String IS_EXTERNAL_ROOM_ENABLED = "isExternalRoomEnabled";
    private static final String IS_EXTERNAL_IM_ENABLED = "isExternalIMEnabled";

    private final SymAuth symAuth;
    private final ApiClient apiClient;

    public UserManagementClientImpl(SymAuth symAuth, String serviceUrl) {
        this.symAuth = symAuth;

        apiClient = Configuration.getDefaultApiClient();
        apiClient.setBasePath(serviceUrl);
        apiClient.addDefaultHeader(symAuth.getSessionToken().getName(), symAuth.getSessionToken().getToken());
        apiClient.addDefaultHeader(symAuth.getKeyToken().getName(), symAuth.getKeyToken().getToken());
    }

    @Override
    public void updateExternalAccess(long userId, boolean allowExternalAccess) throws UserManagementClientException {
        UserApi userApi = new UserApi(apiClient);
        SymFeatureList symFeatureList;

        try {
            symFeatureList = SymFeatureList.toSymFeatureList(userApi.v1AdminUserUidFeaturesGet(getSessionToken(), userId));
        } catch (ApiException e) {
            String errorMessage = "API Error communicating with POD while retrieving user features for user id " + userId;
            LOG.error(errorMessage, e);
            throw new UserManagementClientException(errorMessage, e.getCause());
        }

        SymFeatureList changedSymFeatureList = new SymFeatureList();

        symFeatureList.forEach(symFeature -> {
            boolean hasChanged = allowExternalAccess != symFeature.getEnabled();
            boolean updateExternalRoom = IS_EXTERNAL_ROOM_ENABLED.equals(symFeature.getEntitlement()) && hasChanged;
            boolean updateExternalIM = IS_EXTERNAL_IM_ENABLED.equals(symFeature.getEntitlement()) && hasChanged;
            boolean updateExternalAccess = updateExternalRoom || updateExternalIM;

            if (updateExternalAccess) {
                symFeature.setEnabled(allowExternalAccess);
                changedSymFeatureList.add(symFeature);
                String externalAccessTypeMessage = (updateExternalRoom) ? "Room Chat access" : "Chat access";
                LOG.info("Setting External " + externalAccessTypeMessage + " to " + allowExternalAccess + " for user id " + userId);
            }
        });

        if (!changedSymFeatureList.isEmpty()) {
            try {
                userApi.v1AdminUserUidFeaturesUpdatePost(getSessionToken(), userId, SymFeatureList.toFeatureList(changedSymFeatureList));
            } catch (ApiException e) {
                String errorMessage = "API Error communicating with POD while updating user features for user id " + userId;
                LOG.error(errorMessage, e);
                throw new UserManagementClientException(errorMessage, e.getCause());
            }
        }
    }

    /* ************************************************************************
     *                              Private Methods
     * ***********************************************************************/

    private String getSessionToken() {
        Token tokenObject = symAuth.getSessionToken();

        if (tokenObject == null) {
            throw new IllegalStateException("Invalid token object. It must not be null or empty");
        }

        String sessionToken = tokenObject.getToken();

        if (Strings.isNullOrEmpty(sessionToken)) {
            throw new IllegalStateException("Invalid session token. It must not be null or empty");
        }

        return sessionToken;
    }

}
