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

package org.symphonyoss.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.events.*;
import org.symphonyoss.client.exceptions.AttachmentsException;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.client.exceptions.PresenceException;
import org.symphonyoss.client.exceptions.SymException;
import org.symphonyoss.client.model.Chat;
import org.symphonyoss.client.model.Room;
import org.symphonyoss.client.services.*;
import org.symphonyoss.symphony.agent.model.*;
import org.symphonyoss.symphony.clients.model.ApiVersion;
import org.symphonyoss.symphony.clients.model.SymAttachmentInfo;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymUserConnection;
import org.symphonyoss.symphony.pod.model.Presence;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;


/**
 * This is the test BOT that will receive messages from the sjcTestClient.  These messages will request certain
 * functions be executed.  If the event has been executed, the BOT will send a confirmation reply.   The idea is you
 * can synchronously test features.
 *
 * @author Frank Tarsillo
 */
public class SjcTestBot implements ChatListener, ChatServiceListener, RoomEventListener, RoomServiceEventListener, ConnectionsListener {

    SymphonyClient symphonyClient;
    String sjcClient = System.getProperty("sender.user.email", "sjc.testclient");
    private final Logger logger = LoggerFactory.getLogger(SjcTestBot.class);
    private String testClientStreamId;
    private Long testClientId;

    public SjcTestBot() throws SymException {

        SymphonyClientConfig symphonyClientConfig = new SymphonyClientConfig();
        symphonyClientConfig.set(SymphonyClientConfigID.USER_CERT_FILE,System.getProperty("bot.user.cert.file"));
        symphonyClientConfig.set(SymphonyClientConfigID.USER_CERT_PASSWORD,System.getProperty("bot.user.cert.password"));
        symphonyClientConfig.set(SymphonyClientConfigID.TRUSTSTORE_FILE,System.getProperty("truststore.file"));
        symphonyClientConfig.set(SymphonyClientConfigID.TRUSTSTORE_PASSWORD,System.getProperty("truststore.password"));
        symphonyClientConfig.set(SymphonyClientConfigID.USER_EMAIL, System.getProperty("bot.user.email", "sjc.testbot"));


        symphonyClient = SymphonyClientFactory.getClient(SymphonyClientFactory.TYPE.V4, symphonyClientConfig);

        symphonyClient.getRoomService().addRoomServiceEventListener(this);


        testClientStreamId = symphonyClient.getStreamsClient().getStreamFromEmail(sjcClient).getStreamId();
        testClientId = symphonyClient.getUsersClient().getUserFromEmail(sjcClient).getId();

        Room room = new Room();
        room.setStreamId(System.getProperty("test.room.stream"));
        room.setId(System.getProperty("test.room.stream"));
        room.addEventListener(this);
        symphonyClient.getRoomService().joinRoom(room);

        symphonyClient.getChatService().addListener(this);

    }


    @Override
    public void onRoomMessage(SymMessage symMessage) {

        logger.info("TestBot: New room message detected: {}", symMessage.getMessageText());
        sendResponse(symMessage);


    }

    @Override
    public void onSymRoomDeactivated(SymRoomDeactivated symRoomDeactivated) {

    }

    @Override
    public void onSymRoomMemberDemotedFromOwner(SymRoomMemberDemotedFromOwner symRoomMemberDemotedFromOwner) {

    }

    @Override
    public void onSymRoomMemberPromotedToOwner(SymRoomMemberPromotedToOwner symRoomMemberPromotedToOwner) {

    }

    @Override
    public void onSymRoomReactivated(SymRoomReactivated symRoomReactivated) {

    }

    @Override
    public void onSymRoomUpdated(SymRoomUpdated symRoomUpdated) {

    }

    @Override
    public void onSymUserJoinedRoom(SymUserJoinedRoom symUserJoinedRoom) {

    }

    @Override
    public void onSymUserLeftRoom(SymUserLeftRoom symUserLeftRoom) {

    }

    @Override
    public void onSymRoomCreated(SymRoomCreated symRoomCreated) {

    }


    @Override
    public void onMessage(SymMessage symMessage) {

    }

    @Override
    public void onNewRoom(Room room) {
        room.addEventListener(this);

    }


    @Override
    public void onNewChat(Chat chat) {
        chat.addListener(this);
        //onChatMessage(chat.getLastMessage());

    }

    @Override
    public void onRemovedChat(Chat chat) {

    }

    @Override
    public void onChatMessage(SymMessage symMessage) {

        String text = symMessage.getMessageText();

        logger.info("Received: {}", text);

        String[] chunks = text.split(" ");

        if (chunks == null)
            return;

        if (chunks[0].equals(SymphonyClientIT.CHAT_COMMAND_MESSAGE)) {
            logger.info("Test Bot: New chat message detected: {}", symMessage.getMessageText());
            sendResponse(symMessage);
        } else if (chunks[0].equals(SymphonyClientIT.MULTI_PARTY_CHAT_COMMAND_MESSAGE)) {
            logger.info("Test Bot: New chat message detected: {}", symMessage.getMessageText());
            sendResponse(symMessage);
        } else if (chunks[0].equals(SymphonyClientIT.PRESENCE_COMMAND_MESSAGE)) {

            sendPresenceResponse();

        } else if (chunks[0].equals(SymphonyClientIT.ATTACHMENT_COMMAND_MESSAGE)) {

            logger.info("Attachment message received");
            sendAttachmentResponse(symMessage);

        }


    }

    private void sendAttachmentResponse(SymMessage symMessage) {

        logger.info("Attachment message received");

        //Do we have any attachments in the incoming message
        if (symMessage.getAttachments() != null) {

            List<SymAttachmentInfo> attachmentInfos = symMessage.getAttachments();

            //Check for multiple files
            for (SymAttachmentInfo symAttachmentInfo : attachmentInfos) {

                try {

                    File outFile = new File(symAttachmentInfo.getName() + ".received");

                    //blind delete
                    outFile.delete();

                    OutputStream out = new FileOutputStream(outFile);
                    out.write(symphonyClient.getAttachmentsClient().getAttachmentData(symAttachmentInfo, symMessage));

                    logger.info("Received file {} with ID: {}", symAttachmentInfo.getName(), symAttachmentInfo.getId());

                    out.close();

                } catch (IOException e) {

                    logger.error("Failed to process file..", e);
                } catch (AttachmentsException e) {
                    logger.error("Failed to send attachment..", e);
                }

            }

            sendResponse(symMessage);

        } else {
            logger.error("FAILED TO DETECT ATTACHMENTS");
        }
    }

    @Override
    public void onConnectionNotification(SymUserConnection userConnection) {

    }

    /**
     * Send the sjcClient presence back to sjcClient
     */

    private void sendPresenceResponse() {

        try {


            Presence presence = symphonyClient.getPresenceClient().getUserPresence(testClientId);


            SymMessage message = new SymMessage();
         //  message.setApiVersion(ApiVersion.V2);
            message.setMessageText(SymphonyClientIT.PRESENCE_COMMAND_MESSAGE + " " + presence.getCategory().toString());

            sendResponse(message);

        } catch (PresenceException e) {
            logger.error("Could not obtain presence for SymphonyClientIT user", e);
        }
    }

    /**
     * Send response message
     *
     * @param symMessage the response message to a command
     */
    private void sendResponse(SymMessage symMessage) {

        try {

            symMessage.setStreamId(testClientStreamId);

            symphonyClient.getMessageService().sendMessage(sjcClient, symMessage);
        } catch (MessagesException e) {
            logger.error("Could not send onRoomMessage reply", e);
        }

    }

    public void shutdown() {
        if (symphonyClient != null)
            symphonyClient.shutdown();
    }

}
