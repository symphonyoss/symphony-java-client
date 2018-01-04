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
import org.symphonyoss.client.exceptions.PresenceException;
import org.symphonyoss.client.exceptions.UsersClientException;
import org.symphonyoss.client.model.CacheType;
import org.symphonyoss.symphony.clients.model.SymPresence;
import org.symphonyoss.symphony.clients.model.SymUser;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * **NOTE** This service has been suspended for real-time monitoring of events as per LLC.  Polling for all user
 * could cause head end (POD) failure.
 * <p>
 * Presence service provides monitoring of user presence events.  It also provides helper methods to retrieve user
 * presence.  Alternatively the {@link org.symphonyoss.symphony.clients.PresenceClient} can be used directly.
 *
 * @author Frank Tarsillo
 */
public class PresenceService implements PresenceFeedListener {

    private final SymphonyClient symClient;
    private PresenceWorker presenceWorker;
    private final Set<PresenceListener> presenceListeners = ConcurrentHashMap.newKeySet();
    private final Logger logger = LoggerFactory.getLogger(PresenceService.class);


    public PresenceService(SymphonyClient symClient) {

        this.symClient = symClient;


    }


    /**
     * Return a individual user presence by userID
     *
     * @param symUser symUser to lookup
     * @param local   Search local pod
     * @return SymPresence presence
     * @throws PresenceException Thrown by underlying Symphony API calls
     */
    public SymPresence getUserPresence(SymUser symUser, Boolean local) throws PresenceException {

        return symClient.getPresenceClient().getUserPresence(symUser, local);

    }

    /**
     * Return a individual user presence by email
     *
     * @param email email to lookup
     * @return SymPresence presence
     * @throws PresenceException Thrown by underlying Symphony API calls
     */
    @SuppressWarnings("unused")
    public SymPresence getUserPresence(String email) throws PresenceException {

        if (email == null)
            throw new NullPointerException("Email was not provided..");

        SymUser symUser;
        try {
            symUser = ((SymUserCache) symClient.getCache(CacheType.USER)).getUserByEmail(email);
        } catch (UsersClientException e) {
            logger.error("Failed to obtain userID from email", e);
            throw new PresenceException("Failed to obtain user from email: " + email, e);
        }

        return (symUser != null) ?
                symClient.getPresenceClient().getUserPresence(symUser, false) : null;


    }


    /**
     * This will start the presence worker thread
     *
     * @param presenceListener to handle callbacks.
     */
    public void addPresenceListener(PresenceListener presenceListener) {

        if (presenceWorker == null) {
            logger.debug("Starting presence worker thread..");

            //Lets startup the worker thread to listen for raw datafeed messages
            presenceWorker = new PresenceWorker(symClient, this);

            new Thread(presenceWorker, "PresenceWorker: " + symClient.getName()).start();

        }

        presenceListeners.add(presenceListener);

    }

    @SuppressWarnings("unused")
    public void removePresenceListener(PresenceListener presenceListener) {

        presenceListeners.remove(presenceListener);

        if (presenceListeners.isEmpty()) {
            presenceWorker.shutdown();
            presenceWorker = null;

            logger.debug("Killing presence worker thread..");
        }


    }

    @Override
    public void onEvent(SymPresence symPresence) {

        for (PresenceListener listener : presenceListeners) {
            listener.onEvent(symPresence);

        }


    }


    /**
     * Shutdown all underlying threads
     */
    public void shutdown() {

        if (presenceWorker != null) {
            presenceWorker.shutdown();
            presenceWorker = null;
        }
    }


}
