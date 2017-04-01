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
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package org.symphonyoss.client.services;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.common.Constants;
import org.symphonyoss.exceptions.UsersClientException;
import org.symphonyoss.symphony.clients.model.SymUser;

import javax.cache.expiry.AccessedExpiryPolicy;
import javax.cache.expiry.Duration;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * User cache subsystem
 * <p>
 * This subsystem utilizes Ignite Cache services
 *
 * @author Frank Tarsillo 4/1/17.
 */
public class SymUserCache {

    private static IgniteCache<Long, SymUser> symUserById;
    private static IgniteCache<String, SymUser> symUserByEmail;
    private static IgniteCache<String, SymUser> symUserByName;
    private static IgniteCache<String, Set<SymUser>> symUserByStream;
    private static boolean ENABLED = new Boolean(System.getProperty(Constants.SYMUSERS_CACHE_ENABLED, "true"));


    static {


        if (ENABLED) {
            Ignite ignite = Ignition.start();


            // Get an instance of named cache.
            symUserById = ignite.getOrCreateCache(Constants.SYMUSERS_BY_ID_CACHE);
            symUserById = symUserById.withExpiryPolicy(
                    new AccessedExpiryPolicy(new Duration(TimeUnit.SECONDS, new Long(System.getProperty(Constants.SYMUSERS_CACHE_ACCESSEDEXPIRY, "86400")))));


            symUserByEmail = ignite.getOrCreateCache(Constants.SYMUSERS_BY_EMAIL_CACHE);
            symUserByEmail = symUserByEmail.withExpiryPolicy(
                    new AccessedExpiryPolicy(new Duration(TimeUnit.SECONDS, new Long(System.getProperty(Constants.SYMUSERS_CACHE_ACCESSEDEXPIRY, "86400")))));


            symUserByName = ignite.getOrCreateCache(Constants.SYMUSERS_BY_NAME_CACHE);
            symUserByName = symUserByName.withExpiryPolicy(
                    new AccessedExpiryPolicy(new Duration(TimeUnit.SECONDS, new Long(System.getProperty(Constants.SYMUSERS_CACHE_ACCESSEDEXPIRY, "86400")))));


            symUserByStream = ignite.getOrCreateCache(Constants.SYMUSERS_BY_STREAM_CACHE);
            symUserByStream = symUserByStream.withExpiryPolicy(
                    new AccessedExpiryPolicy(new Duration(TimeUnit.SECONDS, new Long(System.getProperty(Constants.SYMUSERS_CACHE_ACCESSEDEXPIRY, "86400")))));
        }

    }

    public SymUserCache() {
    }

    /**
     * Get SymUser by email address through cache.
     *
     * @param symClient Initialized {@link SymphonyClient }
     * @param email     Email address of the user
     * @return {@link SymUser }
     * @throws UsersClientException Exception from underlying API
     */
    public static SymUser getUserByEmail(SymphonyClient symClient, String email) throws UsersClientException {

        SymUser symUser = null;

        if (ENABLED)
            symUser = symUserByEmail.get(email);

        if (symUser == null) {

            symUser = symClient.getUsersClient().getUserFromEmail(email);

            if (symUser != null && ENABLED) {

                symUserByEmail.put(email, symUser);
                symUserById.put(symUser.getId(), symUser);
                //Hack to fix bug in REST API
                if (symUser.getUsername() != null)
                    symUserByName.put(symUser.getUsername(), symUser);

            }

        }


        return symUser;
    }

    /**
     * Get SymUser by user ID through cache.
     *
     * @param symClient Initialized {@link SymphonyClient }
     * @param id        UserID of user
     * @return {@link SymUser }
     * @throws UsersClientException Exception from underlying API
     */
    public static SymUser getUserById(SymphonyClient symClient, Long id) throws UsersClientException {

        SymUser symUser = null;

        if (ENABLED)
            symUser = symUserById.get(id);

        if (symUser == null) {

            symUser = symClient.getUsersClient().getUserFromId(id);

            if (symUser != null && ENABLED) {

                symUserByEmail.put(symUser.getEmailAddress(), symUser);
                symUserById.put(id, symUser);

                //Hack to fix bug in REST API
                if (symUser.getUsername() != null)
                    symUserByName.put(symUser.getUsername(), symUser);

            }

        }

        return symUser;
    }


    /**
     * Get SymUser by name through cache.
     *
     * @param symClient Initialized {@link SymphonyClient }
     * @param name      Name of user
     * @return {@link SymUser }
     * @throws UsersClientException Exception from underlying API
     */
    public static SymUser getUserByName(SymphonyClient symClient, String name) throws UsersClientException {

        SymUser symUser = null;

        if (ENABLED)
            symUser = symUserByName.get(name);

        if (symUser == null) {

            symUser = symClient.getUsersClient().getUserFromName(name);

            if (symUser != null && ENABLED) {

                symUserByEmail.put(symUser.getEmailAddress(), symUser);
                symUserById.put(symUser.getId(), symUser);
                //Hack to fix bug in REST API
                if (symUser.getUsername() != null)
                    symUserByName.put(symUser.getUsername(), symUser);

            }

        }

        return symUser;
    }


    /**
     * Get SymUsers by stream through cache.
     *
     * @param symClient Initialized {@link SymphonyClient }
     * @param streamId  StreamID that users are part of
     * @return Set of {@link SymUser }
     * @throws UsersClientException Exception from underlying API
     */
    public static Set<SymUser> getUsersByStream(SymphonyClient symClient, String streamId) throws UsersClientException {

        Set<SymUser> symUsers = null;

        if (ENABLED)
            symUsers = symUserByStream.get(streamId);

        if (symUsers == null) {

            symUsers = symClient.getUsersClient().getUsersFromStream(streamId);

            if (symUsers != null && ENABLED) {

                symUserByStream.put(streamId, symUsers);

            }

        }

        return symUsers;
    }


}
