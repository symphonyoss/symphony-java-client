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

package org.symphonyoss.symphony.clients;

import java.util.Set;

import org.symphonyoss.client.exceptions.UsersClientException;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.symphony.clients.model.SymUser;
import org.symphonyoss.symphony.pod.model.UserCreate;
import org.symphonyoss.symphony.pod.model.UserStatus;


/**
 * @author Frank Tarsillo
 */
@SuppressWarnings("unused")
public interface UsersClient {
    SymUser getUserFromEmail(String email) throws UsersClientException;

    SymUser getUserFromId(Long userId) throws UsersClientException;

    SymUser getUserFromName(String userName) throws UsersClientException;

    Set<SymUser> getUsersFromStream(String streamId) throws UsersClientException;

    Set<SymUser> getAllUsers() throws UsersClientException;

    void setUserStatus(long userId, UserStatus userStatus) throws UsersClientException;

    SymUser updateUser(long userId, SymUser symUser) throws UsersClientException;

    SymUser createUser(UserCreate userCreate) throws UsersClientException;

    Set<SymUser> getAllUsersWithDetails() throws UsersClientException;

    SymUser getUserBySession(SymAuth symAuth) throws UsersClientException;
}
