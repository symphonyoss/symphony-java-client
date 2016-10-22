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

package org.symphonyoss.examples.shareexample;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.SymphonyClientFactory;
import org.symphonyoss.client.model.Chat;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.client.model.SymShareArticle;
import org.symphonyoss.client.services.ChatListener;
import org.symphonyoss.client.services.ChatServiceListener;
import org.symphonyoss.exceptions.*;
import org.symphonyoss.symphony.clients.AuthorizationClient;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymUser;

import java.util.HashSet;
import java.util.Set;


/**
 * * Simple example of the ShareClient which will send a ShareArticle to a stream.
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
 * -Dbot.user=bot.user1
 * -Dbot.domain=@domain.com
 * -Duser.call.home=frank.tarsillo@markit.com
 * <p>
 * <p>
 * <p>
 * <p>
 * Created by Frank Tarsillo on 5/15/2016.
 */
public class ShareExample  {


    private final Logger logger = LoggerFactory.getLogger(ShareExample.class);

    private SymphonyClient symClient;

    public ShareExample() {


        init();


    }

    public static void main(String[] args) {

        new ShareExample();

    }

    public void init() {


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

            SymShareArticle shareArticle = new SymShareArticle();

            shareArticle.setArticleId("ID ID");
            shareArticle.setTitle("TEST");
            shareArticle.setSummary(" TEST SUMMARY");
            shareArticle.setMessage("A message from bot..");
            shareArticle.setArticleUrl("http://www.cnn.com");
            shareArticle.setSubTitle("TEST Subtitle");
            shareArticle.setPublisher("A publisher");
            shareArticle.setAuthor("Frank Tarsillo");
            shareArticle.setAppId("APP ID");

            symClient.getShareClient().shareArticle(symClient.getStreamsClient().getStreamFromEmail("frank.tarsillo@markit.com").getId(),shareArticle);



        } catch (AuthorizationException ae) {

            logger.error(ae.getMessage(), ae);

        }catch (InitException e) {
            logger.error("error", e);
        } catch (StreamsException e) {
            e.printStackTrace();
        } catch (ShareException e) {
            e.printStackTrace();
        }

    }




}
