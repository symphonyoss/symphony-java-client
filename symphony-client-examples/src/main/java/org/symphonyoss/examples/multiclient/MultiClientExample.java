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
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 *
 */

package org.symphonyoss.examples.multiclient;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.SymphonyClientFactory;
import org.symphonyoss.client.impl.CustomHttpClient;
import org.symphonyoss.client.model.Chat;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.client.services.ChatListener;
import org.symphonyoss.client.services.ChatServiceListener;
import org.symphonyoss.exceptions.*;
import org.symphonyoss.symphony.clients.AuthorizationClient;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymUser;
import org.symphonyoss.symphony.pod.model.Stream;

import javax.ws.rs.client.Client;
import java.util.HashSet;
import java.util.Set;


/**
 * Simple example of the multiple client bot connections from the same JVM.
 * This overrides the default ssl properties and allows for different client certs and trust stores.
 * <p>
 * It will send a message to a call.home.user from different SyphonyClient instances.
 * <p>
 * <p>
 * <p>
 * REQUIRED VM Arguments or System Properties:
 * <p>
 * -Dsessionauth.url=https://pod_fqdn:port/sessionauth
 * -Dkeyauth.url=https://pod_fqdn:port/keyauth
 * -Dsymphony.agent.pod.url=https://agent_fqdn:port/pod
 * -Dsymphony.agent.agent.url=https://agent_fqdn:port/agent
 * -Dcerts.dir=/dev/certs/
 * -Dkeystore.password=(Pass)
 * -Dtruststore.file=/dev/certs/server.truststore
 * -Dtruststore.password=(Pass)
 * -Dbot.user1=bot.user1
 * -Dbot.user2=bot.user2
 * -Dbot.domain=domain.com
 * -Duser.call.home=frank.tarsillo@markit.com
 * <p>
 * <p>
 * <p>
 * <p>
 * Created by Frank Tarsillo on 5/15/2016.
 */
public class MultiClientExample {


    private final Logger logger = LoggerFactory.getLogger(MultiClientExample.class);



    public MultiClientExample(){


        SymphonyClient symClient1 = init(
                System.getProperty("bot.user1") + "@" +  System.getProperty("bot.domain"),
                System.getProperty("certs.dir") + System.getProperty("bot.user1") + ".p12",
                System.getProperty("keystore.password"),
                System.getProperty("truststore.file"),
                System.getProperty("truststore.password"));

        SymphonyClient symClient2 = init(
                System.getProperty("bot.user2") + "@" +  System.getProperty("bot.domain"),
                System.getProperty("certs.dir") + System.getProperty("bot.user2") + ".p12",
                System.getProperty("keystore.password"),
                System.getProperty("truststore.file"),
                System.getProperty("truststore.password"));



            try {
                Stream stream1 = symClient1.getStreamsClient().getStreamFromEmail(System.getProperty("user.call.home"));

                Stream stream2= symClient2.getStreamsClient().getStreamFromEmail(System.getProperty("user.call.home"));


                //A message to send when the BOT comes online.
                SymMessage aMessage = new SymMessage();
                aMessage.setFormat(SymMessage.Format.TEXT);

                aMessage.setMessage("Hello master from bot1...");

                symClient1.getMessagesClient().sendMessage(stream1,aMessage);

                aMessage.setMessage("Hello master from bot2...");

                symClient2.getMessagesClient().sendMessage(stream2,aMessage);

                aMessage.setMessage("Hello master from bot1..again...");

                symClient1.getMessagesClient().sendMessage(stream1,aMessage);

                aMessage.setMessage("Hello master from bot2..again and again..");

                symClient2.getMessagesClient().sendMessage(stream2,aMessage);
                symClient2.getMessagesClient().sendMessage(stream2,aMessage);
                symClient2.getMessagesClient().sendMessage(stream2,aMessage);
                symClient2.getMessagesClient().sendMessage(stream2,aMessage);


                aMessage.setMessage("Hello master from bot1..again and again...");

                symClient1.getMessagesClient().sendMessage(stream1,aMessage);
                symClient1.getMessagesClient().sendMessage(stream1,aMessage);
                symClient1.getMessagesClient().sendMessage(stream1,aMessage);
                symClient1.getMessagesClient().sendMessage(stream1,aMessage);
                symClient1.getMessagesClient().sendMessage(stream1,aMessage);

                System.exit(1);

            } catch (StreamsException e) {
                e.printStackTrace();
            } catch (MessagesException e) {
                e.printStackTrace();
            }




    }

    public static void main(String[] args) {

        new MultiClientExample();

    }

    public SymphonyClient init(String email, String clientKeyStore, String clientKeyStorePass, String trustStore, String trustStorePass) {


        try {

            //Create a basic client instance.
            SymphonyClient symClient = SymphonyClientFactory.getClient(SymphonyClientFactory.TYPE.BASIC);

            logger.debug("{} {}", System.getProperty("sessionauth.url"),
                    System.getProperty("keyauth.url"));


            try {
                Client httpClient = CustomHttpClient.getClient(clientKeyStore,clientKeyStorePass,trustStore,trustStorePass);
                symClient.setDefaultHttpClient(httpClient);
            } catch (Exception e) {
                logger.error("Failed to create custom http client",e);
                return null;
            }



            //Init the Symphony authorization client, which requires both the key and session URL's.  In most cases,
            //the same fqdn but different URLs.
            AuthorizationClient authClient = new AuthorizationClient(
                    System.getProperty("sessionauth.url"),
                    System.getProperty("keyauth.url"),
                    symClient.getDefaultHttpClient());



            //Create a SymAuth which holds both key and session tokens.  This will call the external service.
            SymAuth symAuth = authClient.authenticate();


            //With a valid SymAuth we can now init our client.
            symClient.init(
                    symClient.getDefaultHttpClient(),
                    symAuth,
                    email,
                    System.getProperty("symphony.agent.agent.url"),
                    System.getProperty("symphony.agent.pod.url")

            );


            return symClient;

        } catch (AuthorizationException ae) {

            logger.error(ae.getMessage(), ae);
        } catch (InitException e) {
            logger.error("error", e);
        }

        return null;
    }






}
