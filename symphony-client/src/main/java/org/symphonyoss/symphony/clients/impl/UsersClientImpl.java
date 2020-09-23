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

import com.google.common.base.Strings;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClientConfig;
import org.symphonyoss.client.SymphonyClientConfigID;
import org.symphonyoss.client.common.Constants;
import org.symphonyoss.client.exceptions.RestException;
import org.symphonyoss.client.exceptions.UserNotFoundException;
import org.symphonyoss.client.exceptions.UsersClientException;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.symphony.clients.model.SymUser;
import org.symphonyoss.symphony.pod.api.RoomMembershipApi;
import org.symphonyoss.symphony.pod.api.SessionApi;
import org.symphonyoss.symphony.pod.api.UserApi;
import org.symphonyoss.symphony.pod.api.UsersApi;
import org.symphonyoss.symphony.pod.invoker.ApiClient;
import org.symphonyoss.symphony.pod.invoker.ApiException;
import org.symphonyoss.symphony.pod.model.*;

import javax.ws.rs.client.Client;
import java.util.Base64;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * @author Frank Tarsillo
 */
public class UsersClientImpl implements org.symphonyoss.symphony.clients.UsersClient {
    private final SymAuth symAuth;
    private final ApiClient apiClient;
    private final long getAllUsersTimeout;

    private final Logger logger = LoggerFactory.getLogger(UsersClientImpl.class);

    /**
     * Init
     *
     * @param symAuth Authorization model containing session and key tokens
     * @param config  Symphony Client config
     */
    public UsersClientImpl(SymAuth symAuth, SymphonyClientConfig config) {
        this(symAuth, config, null);
    }

    /**
     * If you need to override HttpClient.  Important for handling individual client certs.
     *
     * @param symAuth    Authorization model containing session and key tokens
     * @param config     Symphony Client config
     * @param httpClient Custom HTTP client
     */
    public UsersClientImpl(SymAuth symAuth, SymphonyClientConfig config, Client httpClient) {
        this.symAuth = symAuth;


        //Get Service client to query for userID.
        apiClient = org.symphonyoss.symphony.pod.invoker.Configuration.getDefaultApiClient();

        if (httpClient != null)
            apiClient.setHttpClient(httpClient);

        apiClient.setBasePath(config.get(SymphonyClientConfigID.POD_URL));

        getAllUsersTimeout = NumberUtils.toLong(config.get(SymphonyClientConfigID.GET_ALL_USERS_TIMEOUT), 5);
    }


    @Override
    public SymUser getUserFromEmail(String email) throws UsersClientException {


        UsersApi usersApi = new UsersApi(apiClient);

        if (email == null)
            throw new NullPointerException("Email was null");

        UserV2 user;
        try {
            user = usersApi.v2UserGet(symAuth.getSessionToken().getToken(), null, email, null, true);

            //Need to speak with LLC on this one.
            if (user == null)
                user = usersApi.v2UserGet(symAuth.getSessionToken().getToken(), null, email, null, false);

        } catch (ApiException e) {
            throw new UsersClientException("API Error communicating with POD, while retrieving user details for " + email,
                    new RestException(usersApi.getApiClient().getBasePath(), e.getCode(), e));
        }

        if (user != null) {

            logger.debug("Found User: {}:{}:{}", user.getEmailAddress(), user.getUsername(), user.getId());
            return SymUser.toSymUser(user);
        }


        logger.warn("Could not locate user: {}", email);

        throw new UserNotFoundException("Could not find user from email: " + email);


    }


    @Override
    public SymUser getUserFromId(Long userId) throws UsersClientException {

        UsersApi usersApi = new UsersApi(apiClient);


        if (userId == null)
            throw new NullPointerException("UserId was null...");

        UserV2 user;
        try {
            user = usersApi.v2UserGet(symAuth.getSessionToken().getToken(), userId, null, null, true);

            if (user == null) {
                user = usersApi.v2UserGet(symAuth.getSessionToken().getToken(), userId, null, null, false);
            }

        } catch (ApiException e) {
            throw new UsersClientException("API Error communicating with POD, while retrieving user details for " + userId,
                    new RestException(usersApi.getApiClient().getBasePath(), e.getCode(), e));
        }

        if (user != null) {

            logger.debug("Found User: {}:{}:{}", user.getDisplayName(), user.getUsername(), user.getId());
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
            user = usersApi.v2UserGet(symAuth.getSessionToken().getToken(), null, null, userName, true);
        } catch (ApiException e) {
            throw new UsersClientException("API Error communicating with POD, while retrieving user details for " + userName,
                    new RestException(usersApi.getApiClient().getBasePath(), e.getCode(), e));
        }

        if (user != null) {

            logger.debug("Found User: {}:{}", user.getEmailAddress(), user.getId());
            return SymUser.toSymUser(user);
        }


        throw new UserNotFoundException("Could not find user from user name: " + userName);
    }

    @Override
    public Set<SymUser> getUsersFromStream(String streamId) throws UsersClientException {

        if (streamId == null) {
            throw new NullPointerException("Stream ID was not provided...");
        }

        //A bit odd that its coming from room membership API!
        RoomMembershipApi roomMembershipApi = new RoomMembershipApi(apiClient);

        try {
            MembershipList memberInfos = roomMembershipApi.v1RoomIdMembershipListGet(streamId, symAuth.getSessionToken().getToken());

            Set<SymUser> users = new HashSet<>();
            for (MemberInfo memberInfo : memberInfos) {

                users.add(getUserFromId(memberInfo.getId()));


            }
            return users;

        } catch (ApiException e) {
            throw new UsersClientException("Failed to retrieve room membership for room ID: " + streamId,
                    new RestException(roomMembershipApi.getApiClient().getBasePath(), e.getCode(), e));
        }


    }


    /**
     * Retrieve all users without any details
     * This method could require elevated privileges
     *
     * @return All users as part of a set (returns a simple user without any details)
     * @throws UsersClientException Exceptions thrown from Symphony API's
     */
    @SuppressWarnings("unused")
    @Override
    public Set<SymUser> getAllUsers() throws UsersClientException {

        UserApi userApi = new UserApi(apiClient);

        Set<SymUser> symUsers = ConcurrentHashMap.newKeySet();


        UserV2 user;
        try {
            UserIdList userIdList = userApi.v1AdminUserListGet(symAuth.getSessionToken().getToken());

            int nThreads = Integer.parseInt(System.getProperty(Constants.USERSCLIENT_GETALLUSERS_THREADPOOL, "8"));

            ExecutorService executor = Executors.newFixedThreadPool(nThreads);

            long startTime = System.currentTimeMillis();
            logger.debug("Started to retrieve all users..");

            for (Long userId : userIdList) {


                executor.execute((new Thread(() -> {

                    UsersApi usersApi2 = new UsersApi(apiClient);
                    SymUser symUser;

                    if (userId == null)
                        throw new NullPointerException("UserId was null...");

                    UserV2 user1;
                    try {
                        user1 = usersApi2.v2UserGet(symAuth.getSessionToken().getToken(), userId, null, null, true);

                        if (user1 == null) {
                            user1 = usersApi2.v2UserGet(symAuth.getSessionToken().getToken(), userId, null, null, false);
                        }


                    } catch (ApiException e) {
                        logger.error("API Error while communicating with POD while retrieving user details", e);
                        return;
                    }

                    if (user1 != null) {

                        //logger.debug("Found User: {}:{}", user.getDisplayName(), user.getId());
                        symUser = SymUser.toSymUser(user1);
                        symUsers.add(symUser);
                    }

                })));

            }


            executor.shutdown();


            final boolean processCompleted = executor.awaitTermination(getAllUsersTimeout, TimeUnit.SECONDS);
            if (processCompleted) {
            	logger.debug("Finished all threads. Total time retrieving users: {} sec", (System.currentTimeMillis() - startTime) / 1000);
            } else {
                logger.warn("Process timed-out waiting to getAllUsers(). Total time retrieving users: {} sec", (System.currentTimeMillis() - startTime) / 1000);
            }

        } catch (ApiException e) {
            throw new UsersClientException("API Error communicating with POD, while retrieving all user details",
                    new RestException(userApi.getApiClient().getBasePath(), e.getCode(), e));
        } catch (InterruptedException e) {
            logger.error("Executor failed to terminate after retrieving all users.", e);
            throw new UsersClientException("Interrupt waiting for search executor to finish", e);
        }


        return symUsers;

    }

    /**
     * Retrieve all symphony users with details of features and roles
     *
     * @return All users including details of features and roles as part of a set
     * @throws UsersClientException Exceptions thrown from Symphony API's
     */
    @Override
    public Set<SymUser> getAllUsersWithDetails() throws UsersClientException {
        Set<SymUser> symUsers = getAllUsers();
        String sessionToken = symAuth.getSessionToken().getToken();
        UserApi userApi = new UserApi(apiClient);
        FeatureList featureList;
        UserDetail userDetail;
        try {
            for (SymUser symUser : symUsers) {
                logger.debug("Obtaining user details for {}", symUser.getDisplayName());
                Long uid = symUser.getId();
                featureList = userApi.v1AdminUserUidFeaturesGet(sessionToken, uid);
                symUser.setFeatures(featureList);
                userDetail = userApi.v1AdminUserUidGet(sessionToken, uid);
                symUser.setRoles(new HashSet<>(userDetail.getRoles()));

                if (userDetail.getUserSystemInfo() != null) {
                    symUser.setActive(UserSystemInfo.StatusEnum.ENABLED == userDetail.getUserSystemInfo().getStatus());
                    if (userDetail.getUserSystemInfo().getLastLoginDate() != null) {
                        symUser.setLastLoginDate(new Date(userDetail.getUserSystemInfo().getLastLoginDate()));
                    }
                    if (userDetail.getUserSystemInfo().getCreatedDate() != null) {
                        symUser.setCreatedDate(new Date(userDetail.getUserSystemInfo().getCreatedDate()));
                    }
                }

            }
        } catch (ApiException e) {
            throw new UsersClientException("API Error communicating with POD, while retrieving all user details",
                    new RestException(userApi.getApiClient().getBasePath(), e.getCode(), e));
        }
        return symUsers;
    }

    @Override
    public void setUserStatus(long userId, UserStatus userStatus) throws UsersClientException {
        if (userStatus == null) {
            throw new IllegalArgumentException("Argument userStatus must not be null");
        }

        UserApi usersApi = new UserApi(apiClient);

        try {
            String sessionToken = getSessionToken();

            SuccessResponse successResponse = usersApi.v1AdminUserUidStatusUpdatePost(sessionToken, userId, userStatus);

            if (successResponse == null) {
                throw new IllegalStateException("Update user status response must not be null");
            }
        } catch (ApiException e) {
            String message = "API error communicating with POD, while settIng status " + userStatus.getStatus() + " for user id: " + userId;
            logger.error(message, e);
            throw new UsersClientException(message,
                    new RestException(usersApi.getApiClient().getBasePath(), e.getCode(), e));
        } catch (IllegalStateException e) {
            String message = "Failed to set status " + userStatus.getStatus() + " for user id: " + userId;
            logger.error(message, e);
            throw new UsersClientException(message, e);
        }
    }

    @Override
    public SymUser updateUser(long userId, SymUser symUser) throws UsersClientException {
        UserApi usersApi = new UserApi(apiClient);
        SymUser updatedSymUser;

        try {
            String sessionToken = getSessionToken();
            UserAttributes userAttributes = SymUser.toUserAttributes(symUser);

            UserDetail userDetail = usersApi.v1AdminUserUidUpdatePost(sessionToken, userId, userAttributes);

            if (userDetail != null) {
                updatedSymUser = SymUser.toSymUser(userDetail);
            } else {
                throw new IllegalStateException("Update user response must not be null");
            }
        } catch (ApiException e) {
            String message = "API error communicating with POD, while updating user";
            logger.error(message, e);
            throw new UsersClientException(message, new RestException(usersApi.getApiClient().getBasePath(), e.getCode(), e));
        } catch (IllegalStateException e) {
            String message = "Failed to update user";
            logger.error(message, e);
            throw new UsersClientException(message, e);
        }

        return updatedSymUser;
    }

    @Override
    public SymUser createUser(UserCreate userCreate) throws UsersClientException {
        UserApi usersApi = new UserApi(apiClient);
        UserDetail userDetail;

        try {
            String sessionToken = getSessionToken();

            userDetail = usersApi.v1AdminUserCreatePost(sessionToken, userCreate);

            if (userDetail == null) {
                throw new IllegalStateException("User Detail must not be null");
            }
        } catch (ApiException e) {
            String message = "API error communicating with POD, while creating user";
            logger.error(message, e);
            throw new UsersClientException(message,
                    new RestException(usersApi.getApiClient().getBasePath(), e.getCode(), e));
        } catch (IllegalStateException e) {
            String message = "Failed to create user";
            logger.error(message, e);
            throw new UsersClientException(message, e);
        }

        return SymUser.toSymUser(userDetail);
    }


    private String getSessionToken() throws IllegalStateException {
        String sessionToken = symAuth.getSessionToken().getToken();

        if (Strings.isNullOrEmpty(sessionToken)) {
            throw new IllegalStateException("Invalid session token string: it must not be null or empty");
        }
        return sessionToken;
    }

    @Override
    public SymUser getUserBySession(SymAuth symAuth) throws UsersClientException {

        if (symAuth == null)
            return null;


        SessionApi sessionApi = new SessionApi(apiClient);

        try {
            return SymUser.toSymUser(sessionApi.v2SessioninfoGet(symAuth.getSessionToken().getToken()));
        } catch (ApiException e) {
            throw new UsersClientException("Unable to obtain user by session token", e);
        }

    }

    /**
     * Update the avatar of a particular user
     *
     * @param userId User ID as a decimal integer  (required)
     * @param avatar user image. Should be less then 2MB.
     * @throws UsersClientException if fails to make the avatar update
     */
    @Override
    public void updateUserAvatar(long userId, byte[] avatar) throws UsersClientException {
        if (avatar != null) {
            UserApi usersApi = new UserApi(apiClient);
            try {
                String sessionToken = getSessionToken();

                String image = Base64.getEncoder().encodeToString(avatar);

                AvatarUpdate avatarUpdate = new AvatarUpdate();
                avatarUpdate.setImage(image);

                SuccessResponse response = usersApi.v1AdminUserUidAvatarUpdatePost(sessionToken, userId, avatarUpdate);

                if (!"OK".equals(response.getMessage())) {
                    throw new IllegalStateException(
                            "The message differs from expected OK message. Response message: " + response.getMessage());
                }

            } catch (ApiException e) {
                String message = "API error communicating with POD, while updating avatar";
                logger.error(message, e);
                throw new UsersClientException(message,
                        new RestException(usersApi.getApiClient().getBasePath(), e.getCode(), e));
            } catch (IllegalStateException e) {
                String message = "Avatar update failed";
                logger.error(message, e);
                throw new UsersClientException(message);
            }
        }
    }
}
