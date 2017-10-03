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

package lex;


import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lexruntime.AmazonLexRuntime;
import com.amazonaws.services.lexruntime.AmazonLexRuntimeClient;
import com.amazonaws.services.lexruntime.AmazonLexRuntimeClientBuilder;
import com.amazonaws.services.lexruntime.model.PostContentRequest;
import com.amazonaws.services.lexruntime.model.PostTextRequest;
import com.amazonaws.services.lexruntime.model.PostTextResult;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.SymphonyClientConfig;
import org.symphonyoss.client.SymphonyClientConfigID;
import org.symphonyoss.client.SymphonyClientFactory;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.client.exceptions.UsersClientException;
import org.symphonyoss.client.model.Chat;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymUser;

import java.util.HashSet;
import java.util.Set;


/**
 * BotIt is an example of creating an interactive Bot with use of SJC services and AI framework
 * <p>
 * Will highlight command line framework as part of AI package.
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
 * -Dreceiver.email=bot.user2@markit.com or bot user email
 *
 * @author Frank Tarsillo
 */
//NOSONAR
public class LexBot {


    private final Logger logger = LoggerFactory.getLogger(LexBot.class);

    private SymphonyClient symClient;




    public LexBot() {


        init();


    }

    public static void main(String[] args) {

        new LexBot();

    }

    //Start it up..
    public void init() {


//
        try {

            SymphonyClientConfig symphonyClientConfig = new SymphonyClientConfig(true);


            AWSCredentials credentials = new BasicAWSCredentials(System.getProperty("s3.key.id"), System.getProperty("s3.access.key"));
            AmazonLexRuntime lexClient = AmazonLexRuntimeClientBuilder.standard().withRegion(Regions.US_EAST_1).withCredentials(new AWSStaticCredentialsProvider(credentials)).build();


            PostTextRequest postTextRequest = new PostTextRequest();

            postTextRequest.setBotName("ScheduleAppointment");
            postTextRequest.setBotAlias("ScheduleAppointment");
            postTextRequest.setUserId("21232");
            postTextRequest.setInputText("I would like to make an appointment");


            PostTextResult postTextResult = lexClient.postText(postTextRequest);

            logger.debug("Returned: [{}]", postTextResult.getMessage());


                    //Create an initialized client
            symClient = SymphonyClientFactory.getClient(
                    SymphonyClientFactory.TYPE.V4, symphonyClientConfig);


            //A message to send when the BOT comes online.
            SymMessage aMessage = new SymMessage();
            aMessage.setMessageText("Hello master, I'm alive again....");


            //Creates a Chat session with that will receive the online message.
            Chat chat = new Chat();
            chat.setLocalUser(symClient.getLocalUser());
            Set<SymUser> remoteUsers = new HashSet<>();
            remoteUsers.add(symClient.getUsersClient().getUserFromEmail(symphonyClientConfig.get(SymphonyClientConfigID.RECEIVER_EMAIL)));
            chat.setRemoteUsers(remoteUsers);


            //Add the chat to the chat service, in case the "master" continues the conversation.
            symClient.getChatService().addChat(chat);


            //Send a message to the master user.
            symClient.getMessageService().sendMessage(chat, aMessage);


        } catch (MessagesException e) {
            logger.error("error", e);
        } catch (UsersClientException e) {
            e.printStackTrace();
        }

    }


}




