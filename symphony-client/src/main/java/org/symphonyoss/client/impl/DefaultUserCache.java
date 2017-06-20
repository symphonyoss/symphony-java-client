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

package org.symphonyoss.client.impl;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.common.Constants;
import org.symphonyoss.client.exceptions.UsersClientException;
import org.symphonyoss.client.model.CacheType;
import org.symphonyoss.client.services.SymUserCache;
import org.symphonyoss.symphony.clients.model.SymUser;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * User default cache subsystem
 * <p>
 * This subsystem utilizes Ignite Cache services
 *
 * @author Frank Tarsillo 4/1/17.
 */
public class DefaultUserCache implements SymUserCache {

    LoadingCache<Long, SymUser> symUserById;
    LoadingCache<String, SymUser> symUserByEmail;
    LoadingCache<String, SymUser> symUserByName;
    LoadingCache<String, Set<SymUser>> symUserByStream;
    SymphonyClient symClient;
    private final Logger logger = LoggerFactory.getLogger(DefaultUserCache.class);


    /**
     * DefaultUserCache implements symuser caches by id, email, name and streams
     *
     * This cache can be replaced with a custom cache plugin.  Please set in SymphonyClient setCache(SymCache)
     * @param symClient SymphonyClient required to call underlying client implementations and retrieve data.
     */
    public DefaultUserCache(SymphonyClient symClient) {

        this.symClient = symClient;


        symUserById = CacheBuilder.newBuilder().expireAfterAccess(new Long(System.getProperty(Constants.SYMUSERS_CACHE_ACCESSEDEXPIRY, "86400")), TimeUnit.SECONDS)
                .build(new UidUserLoader());

        symUserByEmail = CacheBuilder.newBuilder().expireAfterAccess(new Long(System.getProperty(Constants.SYMUSERS_CACHE_ACCESSEDEXPIRY, "86400")), TimeUnit.SECONDS)
                .build(new EmailUserLoader());

        symUserByName = CacheBuilder.newBuilder().expireAfterAccess(new Long(System.getProperty(Constants.SYMUSERS_CACHE_ACCESSEDEXPIRY, "86400")), TimeUnit.SECONDS)
                .build(new NameUserLoader());

        symUserByStream = CacheBuilder.newBuilder().expireAfterAccess(new Long(System.getProperty(Constants.SYMUSERS_CACHE_ACCESSEDEXPIRY, "86400")), TimeUnit.SECONDS)
                .build(new UserStreamLoader());

        logger.debug("Initialized default SymUser cache");

    }


    /**
     * Email CacheLoader.  The implementation will also set cache by uid and name
     */
    private class EmailUserLoader extends CacheLoader<String, SymUser> {

        @Override
        public SymUser load(String key) throws Exception {

            if (key != null) key = key.toLowerCase();

            SymUser user = symClient.getUsersClient().getUserFromEmail(key);

            if (user != null) {

                if (user.getEmailAddress() != null)
                    symUserByEmail.put(user.getEmailAddress().toLowerCase(), user);


                    symUserById.put(user.getId(), user);

                if(user.getUsername() != null)
                    symUserByName.put(user.getUsername(), user);




                return user;

            }

            throw new RuntimeException("cannot find Symphony user by email " + key);

        }

    }


    /**
     * UID CacheLoader.  The implementation will also set cache by email and name
     */
    private class UidUserLoader extends CacheLoader<Long, SymUser> {

        @Override
        public SymUser load(Long key) throws Exception {


            SymUser user = symClient.getUsersClient().getUserFromId(key);

            if (user != null) {

                if (user.getEmailAddress() != null)
                    symUserByEmail.put(user.getEmailAddress().toLowerCase(), user);


                symUserById.put(user.getId(), user);

                if(user.getUsername() != null)
                    symUserByName.put(user.getUsername(), user);


                return user;

            } else

                throw new RuntimeException("cannot find Symphony user by ID " + key);

        }

    }


    /**
     * UserName CacheLoader.  The implementation will also set cache by id and email
     */
    private class NameUserLoader extends CacheLoader<String, SymUser> {

        @Override

        public SymUser load(String key) throws Exception {

            if (key != null) key = key.toLowerCase();

            SymUser user = symClient.getUsersClient().getUserFromName(key);

            if (user != null) {

                if (user.getEmailAddress() != null)
                    symUserByEmail.put(user.getEmailAddress().toLowerCase(), user);


                if(user.getId() != null)
                    symUserById.put(user.getId(), user);

                if(user.getUsername() != null)
                    symUserByName.put(user.getUsername(), user);

                return user;

            } else

                throw new RuntimeException("cannot find Symphony user by name " + key);

        }


    }


    /**
     * Stream CacheLoader.  Will provide a set of symusers from streamId.
     */
    private class UserStreamLoader extends CacheLoader<String, Set<SymUser>> {

        @Override

        public Set<SymUser> load(String key) throws Exception {


            Set<SymUser> symUsers = symClient.getUsersClient().getUsersFromStream(key);

            if (symUsers != null) {

                symUserByStream.put(key, symUsers);
                return symUsers;

            }else{

                throw new RuntimeException("cannot find Symphony users by stream " + key);
            }


        }


    }


    /**
     * Set the SymphonyClient used for underlying calls.
     * @param symphonyClient SymphonyClient
     */
    public void setSymphonyClient(SymphonyClient symphonyClient) {

        this.symClient = symphonyClient;

    }


    /**
     * Retrieve user by UID
     * @param uid Id of user
     * @return Symuser
     * @throws UsersClientException Exceptions from underlying API's
     */
    @Override
    public SymUser getUserById(Long uid) throws UsersClientException{

        try {


            return symUserById.get(uid);

        } catch (ExecutionException exc) {


            throw new UsersClientException("Cannot load user from cache by id", exc);

        }

    }


    /**
     * Retrieve user by user name
     * @param name      Name of user
     * @return Symuser
     * @throws UsersClientException Exception from underlying API's
     */
    @Override
    public SymUser getUserByName(String name) throws UsersClientException {
        try {

            return symUserByName.get(name);

        } catch (ExecutionException exc) {

            logger.error("Exception loading user from cache by name", exc);

            throw new UsersClientException("Cannot load user from cache by naem", exc);

        }

    }

    /**
     * Retrieve users by S
     * @param streamId  StreamID that users are part of
     * @return Set of sym users representing the users that are part of the stream.
     * @throws UsersClientException Exceptions from underlying API calls.
     */
    @Override
    public Set<SymUser> getUsersByStream(String streamId) throws UsersClientException {
        try {

            logger.debug("Looking up user in name cache :" + streamId);

            return symUserByStream.get(streamId);

        } catch (ExecutionException exc) {


            throw new UsersClientException("Cannot load users from cache by stream", exc);

        }
    }


    /**
     * Retrieve user by user email
     * @param email     email of user
     * @return Symuser
     * @throws UsersClientException Exception from underlying API's
     */
    public SymUser getUserByEmail(String email) throws UsersClientException{

        try {


            return symUserByEmail.get(email);

        } catch (ExecutionException exc) {


            throw new UsersClientException("Cannot load user from cache by email", exc);

        }

    }


    /**
     * Return cache type. This should be CacheType.USER
     * @return SymUserCache
     */
    @Override
    public CacheType getCacheType() {
        return CacheType.USER;
    }

    /**
     * THIS IS IGNORED
     * @param cacheType type of cache eg. USER
     */
    @Override
    public void setCacheType(CacheType cacheType) {

    }
}
