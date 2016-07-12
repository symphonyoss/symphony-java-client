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
import org.symphonyoss.symphony.pod.model.PresenceList;
import org.symphonyoss.symphony.pod.model.UserPresence;

import java.util.concurrent.TimeUnit;

/**
 * Created by Frank Tarsillo on 5/15/2016.
 */
class PresenceWorker implements Runnable {
    private final SymphonyClient symphonyClient;
    private final PresenceListener presenceListener;
    private final PresenceList presenceList;
    private final Logger logger = LoggerFactory.getLogger(PresenceWorker.class);
    private boolean KILL = false;


    public PresenceWorker(SymphonyClient symphonyClient, PresenceListener presenceListener, PresenceList presenceList) {
        this.symphonyClient = symphonyClient;
        this.presenceListener = presenceListener;
        this.presenceList = presenceList;

    }

    public void run() {

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

                if(presence == null) {
                    presenceList.add(cPresence);
                    presenceListener.onUserPresence(cPresence);
                    logger.debug("Adding new user presence to cache for {}:{}", cPresence.getUid(), cPresence.getCategory());
                    continue;
                }


                if(cPresence.getCategory() != presence.getCategory()){

                    logger.debug("Presence change for {}: from: {}  to:{}", cPresence.getUid(), presence.getCategory(), cPresence.getCategory());
                    presence.setCategory(cPresence.getCategory());
                    presenceListener.onUserPresence(cPresence);

                }


            }

            if (KILL) {
                logger.debug("Presence worker thread killed..");
                return;
            }

            try{TimeUnit.SECONDS.sleep(2);}catch(InterruptedException e){e.printStackTrace();}

        }


    }


    public void kill() {
        KILL = true;

    }

    private UserPresence findUserPresenceById(Long userId){


        if(presenceList == null)
            return null;

        for(UserPresence userPresence: presenceList){

            if(userPresence.getUid().equals(userId))
                return userPresence;

        }

        return null;
    }

}
