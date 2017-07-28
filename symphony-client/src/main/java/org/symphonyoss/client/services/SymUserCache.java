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


import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.UsersClientException;
import org.symphonyoss.symphony.clients.model.SymUser;
import java.util.Set;

/**
 * User cache subsystem
 * <p>
 * This subsystem utilizes Ignite Cache services
 *
 * @author Frank Tarsillo 4/1/17.
 */
public interface SymUserCache extends SymCache{



    /**
     * Get SymUser by email address through cache.
     *
     *
     * @param email     Email address of the user
     * @return {@link SymUser }
     * @throws UsersClientException Exception from underlying API
     */
    public  SymUser getUserByEmail( String email) throws UsersClientException;


    /**
     * Get SymUser by user ID through cache.
     *
     *
     * @param id        UserID of user
     * @return {@link SymUser }
     * @throws UsersClientException Exception from underlying API
     */
    public  SymUser getUserById( Long id) throws UsersClientException;




    /**
     * Get SymUser by name through cache.
     *
     *
     * @param name      Name of user
     * @return {@link SymUser }
     * @throws UsersClientException Exception from underlying API
     */
    public  SymUser getUserByName( String name) throws UsersClientException ;

    /**
     * Get SymUsers by stream through cache.
     *
     *
     * @param streamId  StreamID that users are part of
     * @return Set of {@link SymUser }
     * @throws UsersClientException Exception from underlying API
     */
    public  Set<SymUser> getUsersByStream(String streamId) throws UsersClientException;




    public void setSymphonyClient(SymphonyClient symphonyClient);



}
