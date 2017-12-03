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

import org.symphonyoss.client.exceptions.ConnectionsException;
import org.symphonyoss.symphony.clients.model.SymUser;
import org.symphonyoss.symphony.clients.model.SymUserConnection;
import org.symphonyoss.symphony.clients.model.SymUserConnectionRequest;

import java.util.List;

/**
 * @author frank.tarsillo
 */
public interface ConnectionsClient {

    /**
     * Retrieve incoming connection requests
     *
     * @return List of {@link SymUserConnectionRequest}
     * @throws ConnectionsException from underlying API exceptions.
     */
    List<SymUserConnection> getIncomingRequests() throws ConnectionsException;


    /**
     * Retrieve pending incoming connection requests
     *
     * @return List of {@link SymUserConnectionRequest}
     * @throws ConnectionsException from underlying API exceptions.
     */
    List<SymUserConnection> getPendingRequests() throws ConnectionsException;


    /**
     * Retrieve rejected connection requests
     *
     * @return List of {@link SymUserConnectionRequest}
     * @throws ConnectionsException from underlying API exceptions.
     */
    List<SymUserConnection> getRejectedRequests() throws ConnectionsException;

    /**
     * Retrieve accepted connection requests
     *
     * @return List of {@link SymUserConnectionRequest}
     * @throws ConnectionsException from underlying API exceptions.
     */
    List<SymUserConnection> getAcceptedRequests() throws ConnectionsException;

    /**
     * Retrieve all connection requests with all states
     *
     * @return List of {@link SymUserConnectionRequest}
     * @throws ConnectionsException from underlying API exceptions.
     */
    List<SymUserConnection> getAllConnections() throws ConnectionsException;

    /**
     * Remove incoming connection requests
     *
     * @param symUser The symphony user to remove a connection from.
     * @throws ConnectionsException from underlying API exceptions.
     */
    void removeConnectionRequest(SymUser symUser) throws ConnectionsException;


    /**
     * Send a connection request
     *
     * @param symUserConnectionRequest User connection request
     * @return The result of the connection inclusive of state
     * @throws ConnectionsException from underlying API exceptions.
     */
    SymUserConnection sendConnectionRequest(SymUserConnectionRequest symUserConnectionRequest) throws ConnectionsException;

    /**
     * Accept a connection request
     *
     * @param symUserConnectionRequest User connection request
     * @return The result of the connection inclusive of state
     * @throws ConnectionsException from underlying API exceptions.
     */
    SymUserConnection acceptConnectionRequest(SymUserConnectionRequest symUserConnectionRequest) throws ConnectionsException;


    /**
     * Reject a connection request
     *
     * @param symUserConnectionRequest User connection request
     * @return The result of the connection inclusive of state
     * @throws ConnectionsException from underlying API exceptions.
     */
    SymUserConnection rejectConnectionRequest(SymUserConnectionRequest symUserConnectionRequest) throws ConnectionsException;


    /**
     * @deprecated please use {@link #getUserConnection(SymUser)}
     *
     * Retrieve a user connection request
     *
     * @param userId Symphony user ID
     * @return The result of the connection inclusive of state
     * @throws ConnectionsException from underlying API exceptions.
     */
    @Deprecated
    SymUserConnection getUserConnection(String userId) throws ConnectionsException;


    /**
     * Accept a connection request
     *
     * @param symUserConnection User connection request
     * @return The result of the connection inclusive of state
     * @throws ConnectionsException from underlying API exceptions.
     */
    SymUserConnection acceptConnectionRequest(SymUserConnection symUserConnection) throws ConnectionsException;

    /**
     * Retrieve a user connection request by SymUser
     * @param symUser The Symphony user
     * @return The connection
     * @throws ConnectionsException from underlying API exceptions
     */
    SymUserConnection getUserConnection(SymUser symUser) throws ConnectionsException;
}
