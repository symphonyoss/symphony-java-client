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

package org.symphonyoss.examples.presenceservice;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.SymphonyClientFactory;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.client.services.PresenceListener;
import org.symphonyoss.exceptions.UsersClientException;
import org.symphonyoss.symphony.clients.AuthorizationClient;
import org.symphonyoss.symphony.clients.model.SymUser;
import org.symphonyoss.symphony.pod.model.Presence;
import org.symphonyoss.symphony.pod.model.PresenceList;
import org.symphonyoss.symphony.pod.model.UserPresence;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


/**
 * Example of creating a complete user cache w/ associated presence.
 *
 *
 * REQUIRED VM Arguments or System Properties:
 *
 * -Dsessionauth.url=https://pod_fqdn:port/sessionauth
 * -Dkeyauth.url=https://pod_fqdn:port/keyauth
 * -Dsymphony.agent.pod.url=https://agent_fqdn:port/pod
 * -Dsymphony.agent.agent.url=https://agent_fqdn:port/agent
 * -Dcerts.dir=/dev/certs/
 * -Dkeystore.password=(Pass)
 * -Dtruststore.file=/dev/certs/server.truststore
 * -Dtruststore.password=(Pass)
 * -Dbot.user=bot.user1
 * -Dbot.domain=@domain.com
 * -Duser.call.home=frank.tarsillo@markit.com
 *
 * Created by Frank Tarsillo
 */
public class SymUserCache implements PresenceListener {


    private final Logger logger = LoggerFactory.getLogger(SymUserCache.class);
    private SymphonyClient symClient;

    public SymUserCache() {


        init();


    }

    public static void main(String[] args) {


        new SymUserCache();

    }

    public void init() {

        logger.info("Presence Service example starting...");

        try {

            //Create a basic client instance.
            symClient = SymphonyClientFactory.getClient(SymphonyClientFactory.TYPE.BASIC);

            logger.debug("{} {}", System.getProperty("sessionauth.url"),
                    System.getProperty("keyauth.url"));


            //Init the Symphony authorization client, which requires both the key and session URL's.  In most cases,
            //the same fqdn but different URLs.
            AuthorizationClient authClient = new AuthorizationClient(
                    System.getProperty("sessionauth.url"),
                    System.getProperty("keyauth.url"));


            //Set the local keystores that hold the server CA and client certificates
            authClient.setKeystores(
                    System.getProperty("truststore.file"),
                    System.getProperty("truststore.password"),
                    System.getProperty("certs.dir") + System.getProperty("bot.user") + ".p12",
                    System.getProperty("keystore.password"));

            //Create a SymAuth which holds both key and session tokens.  This will call the external service.
            SymAuth symAuth = authClient.authenticate();

            //With a valid SymAuth we can now init our client.
            symClient.init(
                    symAuth,
                    System.getProperty("bot.user") + System.getProperty("bot.domain"),
                    System.getProperty("symphony.agent.agent.url"),
                    System.getProperty("symphony.agent.pod.url")
            );



            Set<SymUser> symUsers = symClient.getUsersClient().getAllUsers();


            PresenceList presences = symClient.getPresenceClient().getAllUserPresence();


            HashMap<Long, UserPresence> allPresence = new HashMap<>();


            for (UserPresence userPresence : presences)
                allPresence.put(userPresence.getUid(), userPresence);



            for (SymUser symUser : symUsers) {

                if (allPresence.get(symUser.getId()) == null) {
                    UserPresence userPresence = new UserPresence();
                    userPresence.setUid(symUser.getId());
                    userPresence.setCategory(UserPresence.CategoryEnum.UNDEFINED);
                    allPresence.put(symUser.getId(), userPresence);
                }

            }

            logger.info("Found total of {} users", symUsers.size());

            for(UserPresence userPresence : allPresence.values()){
                SymUser aUser = symUsers.stream().filter(x -> x.getId().equals(userPresence.getUid())).findAny().orElse(null);

                if(aUser!=null) {
                    logger.debug("User: {} Status: {}", aUser.getDisplayName(), userPresence.getCategory().toString());
                }else{

                }
            }

            symClient.shutdown();

            System.exit(1);



        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    //Callback from PresenceService.  This will monitor all presence on the network.
    @Override
    public void onUserPresence(UserPresence userPresence) {

        try {
            logger.debug("Received user presence change from: {} : {}: {}",
                    userPresence.getUid(),
                    symClient.getUsersClient().getUserFromId(userPresence.getUid()).getEmailAddress(),
                    userPresence.getCategory());
        } catch (UsersClientException e) {

            logger.error("Failed to retrieve email from userID..", e);
        }

    }

}
