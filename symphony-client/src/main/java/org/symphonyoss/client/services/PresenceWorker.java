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
import org.symphonyoss.client.common.Constants;
import org.symphonyoss.symphony.pod.model.PresenceList;
import org.symphonyoss.symphony.pod.model.UserPresence;

import java.util.concurrent.TimeUnit;

/**
 * Worker to poll for presence updates.
 *
 * @author Frank Tarsillo
 */
class PresenceWorker implements Runnable {
    private final SymphonyClient symphonyClient;
    private final PresenceListener presenceListener;
    private final PresenceList presenceList;
    private final Logger logger = LoggerFactory.getLogger(PresenceWorker.class);
    private boolean shutdown = false;


    public PresenceWorker(SymphonyClient symphonyClient, PresenceListener presenceListener, PresenceList presenceList) {
        this.symphonyClient = symphonyClient;
        this.presenceListener = presenceListener;
        this.presenceList = presenceList;

    }

    public void run() {


        if (!Boolean.valueOf(System.getProperty(Constants.PRESENCE_POLL, "false"))) {

            logger.error("Presence polling is no longer supported.  There will be no presence events published.");
            return;

        }

        PresenceList cPresenceList;

        while (true) {


            try {
                cPresenceList = symphonyClient.getPresenceService().getAllUserPresence();

            } catch (Exception e) {

                logger.error("Presence retrieval failure", e);
                continue;
            }

            for (UserPresence cPresence : cPresenceList) {


                UserPresence presence = findUserPresenceById(cPresence.getUid());

                if (presence == null) {
                    presenceList.add(cPresence);
                    presenceListener.onUserPresence(cPresence);
                    logger.debug("Adding new user presence to cache for {}:{}", cPresence.getUid(), cPresence.getCategory());
                    continue;
                }


                if (cPresence.getCategory() != presence.getCategory()) {

                    logger.debug("Presence change for {}: from: {}  to:{}", cPresence.getUid(), presence.getCategory(), cPresence.getCategory());
                    presence.setCategory(cPresence.getCategory());
                    presenceListener.onUserPresence(cPresence);

                }


            }

            if (shutdown) {
                logger.debug("Presence worker thread killed..");
                return;
            }

            try {
                TimeUnit.SECONDS.sleep(
                        Long.valueOf(System.getProperty(Constants.PRESENCE_POLL_SLEEP, "30")));
            } catch (InterruptedException e) {
                logger.error("Sleep timer interrupted", e);
                Thread.currentThread().interrupt();
            }

        }


    }


    private UserPresence findUserPresenceById(Long userId) {


        if (presenceList == null)
            return null;

        for (UserPresence userPresence : presenceList) {

            if (userPresence.getUid().equals(userId))
                return userPresence;

        }

        return null;
    }


    public void shutdown() {
        shutdown = true;

    }
}
