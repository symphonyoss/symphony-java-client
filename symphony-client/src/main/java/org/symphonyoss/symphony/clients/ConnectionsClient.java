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

import org.symphonyoss.exceptions.ConnectionsException;
import org.symphonyoss.symphony.clients.model.SymUserConnection;
import org.symphonyoss.symphony.clients.model.SymUserConnectionRequest;

import java.util.List;

/**
 * @author frank.tarsillo
 */
public interface ConnectionsClient {
    List<SymUserConnection> getIncomingRequests() throws ConnectionsException;

    List<SymUserConnection> getPendingRequests() throws ConnectionsException;

    List<SymUserConnection> getRejectedRequests() throws ConnectionsException;

    List<SymUserConnection> getAcceptedRequests() throws ConnectionsException;

    List<SymUserConnection> getAllConnections() throws ConnectionsException;

    SymUserConnection sendConnectionRequest(SymUserConnectionRequest symUserConnectionRequest) throws ConnectionsException;

    SymUserConnection acceptConnectionRequest(SymUserConnectionRequest symUserConnectionRequest) throws ConnectionsException;

    SymUserConnection rejectConnectionRequest(SymUserConnectionRequest symUserConnectionRequest) throws ConnectionsException;

    SymUserConnection getUserConnection(String userId) throws ConnectionsException;

    SymUserConnection acceptConnectionRequest(SymUserConnection symUserConnection) throws ConnectionsException;
}
