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

package org.symphonyoss.client.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.exceptions.ConnectionsException;
import org.symphonyoss.symphony.clients.model.SymUserConnection;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by frank.tarsillo on 9/9/2016.
 */
public class ConnectionsService implements ConnectionsListener{
    private final SymphonyClient symClient;
    private boolean autoAccept;
    private final Set<ConnectionsListener> connectionsListeners =  ConcurrentHashMap.newKeySet();
    private final Logger logger = LoggerFactory.getLogger(ChatService.class);

    public ConnectionsService(SymphonyClient symClient){
        this.symClient = symClient;
        new Thread(new ConnectionsWorker(symClient,this)).start();

    }


    @Override
    public void onConnectionNotification(SymUserConnection userConnection) {

    for(ConnectionsListener connectionsListener : connectionsListeners){

        connectionsListener.onConnectionNotification(userConnection);

    }

    try {
        if (autoAccept)
            symClient.getConnectionsClient().acceptConnectionRequest(userConnection);

    }catch (ConnectionsException e){
        logger.error("Could not autoaccept connection request from {}",userConnection.getUserId(),e);

    }
    }

    public void registerListener(ConnectionsListener connectionsListener){

        connectionsListeners.add(connectionsListener);

    }

    public void removeListener(ConnectionsListener connectionsListener){

        connectionsListeners.remove(connectionsListener);

    }



    public boolean isAutoAccept() {
        return autoAccept;
    }

    public void setAutoAccept(boolean autoAccept) {
        this.autoAccept = autoAccept;
    }
}
