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

package botit;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.SymphonyClientFactory;
import org.symphonyoss.client.ai.*;
import org.symphonyoss.client.exceptions.*;
import org.symphonyoss.client.model.Chat;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.client.services.ChatListener;
import org.symphonyoss.client.services.ChatServiceListener;
import org.symphonyoss.client.util.MlMessageParser;
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

    //Start it up..
    public void init() {


        try {


            logger.debug("{} {}", System.getProperty("sessionauth.url"),
                    System.getProperty("keyauth.url"));


            //Create an initialized client
            symClient = SymphonyClientFactory.getClient(
                    SymphonyClientFactory.TYPE.BASIC,
                    System.getProperty("bot.user") + System.getProperty("bot.domain"), //bot email
                    System.getProperty("certs.dir") + System.getProperty("bot.user") + ".p12", //bot cert
                    System.getProperty("keystore.password"), //bot cert/keystore pass
                    System.getProperty("truststore.file"), //truststore file
                    System.getProperty("truststore.password"));  //truststore password



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

            //***********************************************
            //Add a command listener as part of AI framework
            chat.addListener(new CommandManager(symClient));


            //Add the chat to the chat service, in case the "master" continues the conversation.
            symClient.getChatService().addChat(chat);


            //Send a message to the master user.
            symClient.getMessageService().sendMessage(chat, aMessage);



        } catch (MessagesException | UsersClientException  e) {
            logger.error("error", e);
        }

    }


    /**
     * Command manager for registering commands that the bot can action.
     *
     * The AiCommandListener extends ChatListener
     */
    class CommandManager extends AiCommandListener implements  AiPermission {

        public CommandManager(SymphonyClient symphonyClient) {
            super(symphonyClient);


            ///////////////////
            //Register commands

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
            testCommand3.addAction(new commandAction());
            testCommand3.addPermission(this);
            addCommand(testCommand3);


        }



        @Override
        public boolean userHasPermission(Long userID) {
            return true;
        }
    }


    /**
     * An action associated with command.  Action will be called if command is detected.  Action will requires a
     * response.
     */
    class commandAction implements AiAction{

        /**
         * AiAction requires respond method, which responds to matched command
         * @param mlMessageParser  The parser pre-loaded with message
         * @param message Incoming message
         * @param command The matching command
         * @return Response to the sending user based on the matching command.
         */
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
