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
 * This service handles incoming connection requests and provides options for automatically accepting requests.
 *
 * All actions for connection requests should be handled through {@link org.symphonyoss.symphony.clients.ConnectionsClient  }
 *
 *
 * @author Frank Tarsillo on 5/16/2016.
 */
public class ConnectionsService implements ConnectionsListener{
    private final SymphonyClient symClient;
    private boolean autoAccept;
    private final Set<ConnectionsListener> connectionsListeners =  ConcurrentHashMap.newKeySet();
    private final Logger logger = LoggerFactory.getLogger(ChatService.class);
    private ConnectionsWorker connectionsWorker;

    public ConnectionsService(SymphonyClient symClient){
        this.symClient = symClient;
        connectionsWorker = new ConnectionsWorker(symClient,this);
        new Thread(connectionsWorker).start();

    }


    /**
     * New connection request notification message from callback.
     * Issue event to all registered listeners.
     *
     * Option to auto-accept on new event.
     *
     * @param userConnection User connection detail
     */
    @Override
    public void onConnectionNotification(SymUserConnection userConnection) {

    for(ConnectionsListener connectionsListener : connectionsListeners){

        connectionsListener.onConnectionNotification(userConnection);

    }

    //Auto Accept if true.
    try {
        if (autoAccept)
            symClient.getConnectionsClient().acceptConnectionRequest(userConnection);

    }catch (ConnectionsException e){
        logger.error("Could not autoaccept connection request from {}",userConnection.getUserId(),e);

    }
    }

    /**
     * Please use {@link #addListener(ConnectionsListener)}
     * @param connectionsListener Listner for callbacks
     */
    @Deprecated
    public void registerListener(ConnectionsListener connectionsListener){

        addListener(connectionsListener);

    }

    public void addListener(ConnectionsListener connectionsListener){

        connectionsListeners.add(connectionsListener);

    }

    /**
     *
     * @param connectionsListener Listener for callbacks
     */
    public void removeListener(ConnectionsListener connectionsListener){

        connectionsListeners.remove(connectionsListener);

    }



    public boolean isAutoAccept() {
        return autoAccept;
    }

    public void setAutoAccept(boolean autoAccept) {
        this.autoAccept = autoAccept;
    }



    public void shutdown(){

        connectionsWorker.shutdown();
        connectionsWorker=null;

    }
}
