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

package org.symphonyoss.examples.botit;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.SymphonyClientFactory;
import org.symphonyoss.client.ai.*;
import org.symphonyoss.client.model.Chat;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.client.services.ChatListener;
import org.symphonyoss.client.services.ChatServiceListener;
import org.symphonyoss.client.util.MlMessageParser;
import org.symphonyoss.exceptions.*;
import org.symphonyoss.symphony.clients.AuthorizationClient;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymUser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * BotIt is an example of creating an interactive Bot with use of SJC services and AI framework
 *
 * Will highlight command line framework as part of AI package.
 *
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
 *
 * @author  Frank Tarsillo
 */
//NOSONAR
public class BotIt {


    private final Logger logger = LoggerFactory.getLogger(BotIt.class);

    private SymphonyClient symClient;

    public BotIt() {


        init();


    }

    public static void main(String[] args) {

        new BotIt();

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


            //A message to send when the BOT comes online.
            SymMessage aMessage = new SymMessage();
            aMessage.setFormat(SymMessage.Format.TEXT);
            aMessage.setMessage("Hello master, I'm alive again....");


            //Creates a Chat session with that will receive the online message.
            Chat chat = new Chat();
            chat.setLocalUser(symClient.getLocalUser());
            Set<SymUser> remoteUsers = new HashSet<>();
            remoteUsers.add(symClient.getUsersClient().getUserFromEmail(System.getProperty("user.call.home")));
            chat.setRemoteUsers(remoteUsers);

            //Add a command listener as part of AI framework
            chat.addListener(new CommandManager(symClient));


            //Add the chat to the chat service, in case the "master" continues the conversation.
            symClient.getChatService().addChat(chat);


            //Send a message to the master user.
            symClient.getMessageService().sendMessage(chat, aMessage);




        } catch (AuthorizationException ae) {

            logger.error(ae.getMessage(), ae);

        } catch (MessagesException | UsersClientException | InitException e) {
            logger.error("error", e);
        }

    }


    class CommandManager extends AiCommandListener implements  AiPermission {

        public CommandManager(SymphonyClient symphonyClient) {
            super(symphonyClient);



            AiCommand testCommand = new AiCommand("command", 1, "Command Usage");
            testCommand.setArgument(0, "arg");
            testCommand.addAction(new commandAction());
            testCommand.addPermission(this);
            addCommand(testCommand);

            AiCommand testCommand2 = new AiCommand("command2", 1, "Command2 Usage");
            testCommand2.setArgument(0, "arg2");
            testCommand2.addAction(new command2Action());
            testCommand2.addPermission(this);
            addCommand(testCommand2);

            AiCommand testCommand3 = new AiCommand("command", 0, "Command Usage no args..");
            //testCommand3.setArgument(0, "arg");
            testCommand3.addAction(new commandAction());
            testCommand3.addPermission(this);
            addCommand(testCommand3);


        }




        @Override
        public boolean userHasPermission(Long userID) {
            return true;
        }
    }


    class commandAction implements AiAction{

        @Override
        public AiResponseSequence respond(MlMessageParser mlMessageParser, SymMessage message, AiCommand command) {

            AiResponseSequence responseSequence = new AiResponseSequence();
            AiResponse aiResponse;

            String[] chunks = mlMessageParser.getTextChunks();

            List<SymUser> symUsers = new ArrayList<>();
            symUsers.add(message.getSymUser());


            aiResponse = new AiResponse("Received command..." + command.getCommand(), SymMessage.Format.TEXT, symUsers);
            responseSequence.addResponse(aiResponse);
            return responseSequence;

        }



    }


    class command2Action implements AiAction{

        @Override
        public AiResponseSequence respond(MlMessageParser mlMessageParser, SymMessage message, AiCommand command) {



            AiResponseSequence responseSequence = new AiResponseSequence();
            AiResponse aiResponse;

            String[] chunks = mlMessageParser.getTextChunks();

            List<SymUser> symUsers = new ArrayList<>();
            symUsers.add(message.getSymUser());


            aiResponse = new AiResponse("Received command2..." + command.getCommand(), SymMessage.Format.TEXT, symUsers);
            responseSequence.addResponse(aiResponse);
            return responseSequence;

        }



    }

}
