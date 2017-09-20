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

package cacheignite;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.SymphonyClientConfig;
import org.symphonyoss.client.SymphonyClientFactory;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.client.exceptions.SymCacheException;
import org.symphonyoss.client.exceptions.UsersClientException;
import org.symphonyoss.client.model.Chat;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymUser;

import java.util.HashSet;
import java.util.Set;


/**
 * Example of a custom SymUserCache implementation using Apache Ignite (IgniteUserCache).
 * <p>
 * The use of ignite is one of many cache solutions to select from.
 * <p>
 * This implementation supports USER level cache only.
 * <p>
 * <p>
 * <p>
 * REQUIRED VM Arguments or System Properties:
 * <p>
 * -Dtruststore.file=
 * -Dtruststore.password=password
 * -Dsessionauth.url=https://(hostname)/sessionauth
 * -Dkeyauth.url=https://(hostname)/keyauth
 * -Duser.call.home=frank.tarsillo@markit.com
 * -Duser.cert.password=password
 * -Duser.cert.file=bot.user2.p12
 * -Dpod.url=https://(pod host)/pod
 * -Dagent.url=https://(agent server host)/agent
 * -Duser.email=bot.user2@markit.com or bot user email
 *
 *
 * @author Frank Tarsillo
 */
//NOSONAR
public class IgniteCacheExample {


    private final Logger logger = LoggerFactory.getLogger(IgniteCacheExample.class);

    private SymphonyClient symClient;

    public IgniteCacheExample() {


        init();


    }

    public static void main(String[] args) {

        new IgniteCacheExample();

    }

    //Start it up..
    public void init() {


        try {

            //Create an initialized client
            symClient = SymphonyClientFactory.getClient(
                    SymphonyClientFactory.TYPE.BASIC,new SymphonyClientConfig(true));  //truststore password


            symClient.setCache(new IgniteUserCache(symClient));

            //A message to send when the BOT comes online.
            SymMessage aMessage = new SymMessage();
            aMessage.setMessageText("Hello master, I'm alive again....");


            //Creates a Chat session with that will receive the online message.
            Chat chat = new Chat();
            chat.setLocalUser(symClient.getLocalUser());
            Set<SymUser> remoteUsers = new HashSet<>();
            remoteUsers.add(symClient.getUsersClient().getUserFromEmail(System.getProperty("user.call.home")));
            chat.setRemoteUsers(remoteUsers);


            //Add the chat to the chat service, in case the "master" continues the conversation.
            symClient.getChatService().addChat(chat);


            //Send a message to the master user.
            symClient.getMessageService().sendMessage(chat, aMessage);


        } catch (MessagesException | UsersClientException e) {
            logger.error("error", e);
        } catch (SymCacheException e) {
            logger.error("could not set cache", e);
        }

    }

}
