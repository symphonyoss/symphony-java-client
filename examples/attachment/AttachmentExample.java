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

package attachment;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.SymphonyClientConfig;
import org.symphonyoss.client.SymphonyClientConfigID;
import org.symphonyoss.client.SymphonyClientFactory;
import org.symphonyoss.client.exceptions.AttachmentsException;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.client.exceptions.SymException;
import org.symphonyoss.client.model.Chat;
import org.symphonyoss.client.services.ChatListener;
import org.symphonyoss.client.services.ChatServiceListener;
import org.symphonyoss.symphony.clients.model.ApiVersion;
import org.symphonyoss.symphony.clients.model.SymAttachmentInfo;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymUser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Example of handling attachments.
 * <p>
 * Will store an incoming file(s) to the bot and echo them back to the sender.
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
 * -Duser.email=bot.user2@domain.com
 * -Dpod.url=https://(pod host)/pod
 * -Dagent.url=https://(agent server host)/agent
 * -Duser.email=bot.user2@markit.com or bot user email
 *
 * @author  Frank Tarsillo
 */
//NOSONAR
public class AttachmentExample implements ChatListener, ChatServiceListener {


    private final Logger logger = LoggerFactory.getLogger(AttachmentExample.class);
    private SymphonyClient symClient;

    public AttachmentExample() {


        init();


    }

    public static void main(String[] args) {

        new AttachmentExample();

    }

    public void init() {



        try {

            logger.info("Attachment example starting...");


            SymphonyClientConfig symphonyClientConfig = new SymphonyClientConfig(true);



            //Create an initialized client
            symClient = SymphonyClientFactory.getClient(
                    SymphonyClientFactory.TYPE.V4,symphonyClientConfig);  //truststore password


            //Will notify the bot of new Chat conversations.
            symClient.getChatService().addListener(this);

            //A message to send when the BOT comes online.
            SymMessage aMessage = new SymMessage();
            aMessage.setMessageText("Hello master, I'm alive again....");


            //Creates a Chat session with that will receive the online message.
            Chat chat = new Chat();
            chat.setLocalUser(symClient.getLocalUser());
            Set<SymUser> remoteUsers = new HashSet<>();
            remoteUsers.add(symClient.getUsersClient().getUserFromEmail(symphonyClientConfig.get(SymphonyClientConfigID.RECEIVER_EMAIL)));
            chat.setRemoteUsers(remoteUsers);
            chat.addListener(this);
            chat.setStream(symClient.getStreamsClient().getStream(remoteUsers));

            //Add the chat to the chat service, in case the "master" continues the conversation.
            symClient.getChatService().addChat(chat);


            //Send a message to the master user.
            symClient.getMessageService().sendMessage(chat, aMessage);


        } catch (SymException e) {
            logger.error("Something went bad...",e);
        }


    }



    //Chat sessions callback method.
    @Override
    public void onChatMessage(SymMessage message) {
        if (message == null)
            return;

        logger.debug("TS: {}\nFrom ID: {}\nSymMessage: {}\nSymMessage Type: {}",
                message.getTimestamp(),
                message.getFromUserId(),
                message.getMessage(),
                message.getMessageType());


        //Do we have any attachments in the incoming message
        if (message.getAttachments() != null) {

            List<SymAttachmentInfo> attachmentInfos = message.getAttachments();
            File outFile = null;
            //Check for multiple files
            for (SymAttachmentInfo symAttachmentInfo : attachmentInfos) {



                try {

                    outFile = new File(symAttachmentInfo.getName());
                    OutputStream out = new FileOutputStream(outFile);
                    out.write(symClient.getAttachmentsClient().getAttachmentData(symAttachmentInfo, message));

                    logger.info("Received file {} with ID: {}", symAttachmentInfo.getName(), symAttachmentInfo.getId());

                    out.close();

                } catch (IOException e) {

                    logger.error("Failed to process file..", e);
                } catch (AttachmentsException e) {
                    logger.error("Failed to send attachment..", e);
                }

            }


            //Lets construct the reply.
            SymMessage symMessage = new SymMessage();
            symMessage.setMessageText(ApiVersion.V4,"Echo the files you sent....");
            symMessage.setStreamId(message.getStreamId());
            symMessage.setAttachment(outFile);


            //Send the message back..
            Chat chat = symClient.getChatService().getChatByStream(message.getStreamId());

            try {
                if (chat != null)
                    symClient.getMessageService().sendMessage(chat, symMessage);
            }catch (MessagesException e){
                logger.error("Could not send echo reply to user",e);
            }

        }




    }

    @Override
    public void onNewChat(Chat chat) {

        chat.addListener(this);

        logger.debug("New chat session detected on stream {} with {}", chat.getStream().getStreamId(), chat.getRemoteUsers());
    }

    @Override
    public void onRemovedChat(Chat chat) {

    }

}
