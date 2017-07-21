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

import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.model.Chat;
import org.symphonyoss.client.model.Room;
import org.symphonyoss.client.services.ChatListener;
import org.symphonyoss.client.services.ChatServiceListener;
import org.symphonyoss.client.services.RoomListener;
import org.symphonyoss.symphony.agent.model.*;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymUser;
import org.symphonyoss.symphony.pod.model.Presence;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * This test will simulate an end to end communication between two BOTS.  This test will attempt to establish two
 * connections to the Symphony POD representing two discrete network endpoints.  These endpoints will test against
 * all SymphonyClient features.
 *
 * @author Frank Tarsillo
 */
public class SymphonyClientIT implements ChatServiceListener, ChatListener, RoomListener {

    private static SymphonyClient sjcTestClient;
    private static final Logger logger = LoggerFactory.getLogger(SymphonyClientIT.class);

    public final static String ROOM_COMMAND_MESSAGE = "/onRoomMessage";
    public final static String CHAT_COMMAND_MESSAGE = "/onChatMessage";
    public final static String MULTI_PARTY_CHAT_COMMAND_MESSAGE = "/onMultiPartyChatMessage";
    public final static String PRESENCE_COMMAND_MESSAGE = "/onPresenceMessage";
    private final static String MP_USER_EMAIL = System.getProperty("mp.user.email","Frank.Tarsillo@ihsmarkit.com");

    private static boolean responded;

    private final String botEmail = System.getProperty("bot.user.email", "sjc.testbot");

    private static SjcTestBot sjcTestBot;

    @BeforeClass
    public static void setupBeforeClass() throws Exception {


        try {

            sjcTestBot = new SjcTestBot();


            sjcTestClient = SymphonyClientFactory.getClient(
                    SymphonyClientFactory.TYPE.BASIC, System.getProperty("sender.user.email", "sjc.testclient"),
                    System.getProperty("sender.user.cert.file"),
                    System.getProperty("sender.user.cert.password"),
                    System.getProperty("truststore.file"),
                    System.getProperty("truststore.password"));


        } catch (Exception e) {

            logger.error("Could not init symphony test client", e);


        }

        org.junit.Assume.assumeTrue(sjcTestClient != null);


    }

    @Before
    public void setupBefore() throws Exception {

    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        if (sjcTestClient != null)
            sjcTestClient.shutdown();

        if (sjcTestBot != null) {
            sjcTestBot.shutdown();
            sjcTestBot = null;
        }

    }


    @Test
    public void sendRoomMessage() throws Exception {

        responded = false;

        sjcTestClient.getChatService().addListener(this);

        Room room = new Room();
        room.setStreamId(System.getProperty("test.room.stream"));
        room.setId(System.getProperty("test.room.stream"));
        room.addListener(this);
        sjcTestClient.getRoomService().joinRoom(room);


        SymMessage message = new SymMessage();
        message.setMessage(ROOM_COMMAND_MESSAGE);

        sjcTestClient.getMessageService().sendMessage(room, message);

        if (!isResponded()) {
            Assert.fail("Timeout receiving confirmation of room message");
        }

    }


    @Test
    public void sendChatMessage() throws Exception {

        responded = false;

        sjcTestClient.getChatService().addListener(this);


        SymMessage message = new SymMessage();
        message.setMessage(CHAT_COMMAND_MESSAGE);
        sjcTestClient.getMessageService().sendMessage(botEmail, message);



        if (!isResponded()) {
            Assert.fail("Timeout receiving confirmation of chat message");
        }

    }

    @Test
    public void sendMultiPartyChatMessage() throws Exception {

        responded = false;

        //Creates a Chat session with that will receive the online message.
        Chat chat = new Chat();
        chat.setLocalUser(sjcTestClient.getLocalUser());
        Set<SymUser> remoteUsers = new HashSet<>();
        remoteUsers.add(sjcTestClient.getUsersClient().getUserFromEmail(botEmail));
        remoteUsers.add(sjcTestClient.getUsersClient().getUserFromEmail(MP_USER_EMAIL));
        chat.setRemoteUsers(remoteUsers);
        chat.addListener(this);


        //Add the chat to the chat service, in case the "master" continues the conversation.
        sjcTestClient.getChatService().addChat(chat);



        SymMessage message = new SymMessage();
        message.setMessage(MULTI_PARTY_CHAT_COMMAND_MESSAGE);


        //Send a message to the master user.
        sjcTestClient.getMessageService().sendMessage(chat, message);




        if (!isResponded()) {
            Assert.fail("Timeout receiving confirmation of chat message");
        }

    }

    @Test
    public void sendPresenceMessage() throws Exception {

        responded = false;

        sjcTestClient.getChatService().addListener(this);

        SymMessage message = new SymMessage();
        message.setMessage(PRESENCE_COMMAND_MESSAGE);
        sjcTestClient.getMessageService().sendMessage(botEmail, message);


        if (!isResponded()) {
            Assert.fail("Timeout receiving confirmation of presence message");
        }

    }


    @Override
    public void onChatMessage(SymMessage message) {

        logger.info("Client Test: New message detected {}", message.getMessageText());


        String text = message.getMessageText();

        String[] chunks = text.split(" ");

        switch (chunks[0]) {
            case ROOM_COMMAND_MESSAGE:
                responded = true;
                logger.info("ETE Test:  Room Message: Success");
                break;
            case CHAT_COMMAND_MESSAGE:
                responded = true;
                logger.info("ETE Test: Chat Message: Success");
                break;
            case MULTI_PARTY_CHAT_COMMAND_MESSAGE:
                responded = true;
                logger.info("ETE Test: Multi-Party Chat Message: Success");
                break;
            case PRESENCE_COMMAND_MESSAGE:

                if (chunks[1] != null && chunks[1].equals(Presence.CategoryEnum.AVAILABLE.toString())) {

                    responded = true;
                    logger.info("ETE Test: Presence Message: Success:  {}", text);
                } else {
                    logger.error("ETE Test: Presence Message: Failure: {}", text);
                }
                break;
        }


    }

    @Override
    public void onRoomMessage(SymMessage symMessage) {

    }

    @Override
    public void onRoomDeactivatedMessage(RoomDeactivatedMessage roomDeactivatedMessage) {

    }

    @Override
    public void onRoomMemberDemotedFromOwnerMessage(RoomMemberDemotedFromOwnerMessage roomMemberDemotedFromOwnerMessage) {

    }

    @Override
    public void onRoomMemberPromotedToOwnerMessage(RoomMemberPromotedToOwnerMessage roomMemberPromotedToOwnerMessage) {

    }

    @Override
    public void onRoomReactivatedMessage(RoomReactivatedMessage roomReactivatedMessage) {

    }

    @Override
    public void onRoomUpdatedMessage(RoomUpdatedMessage roomUpdatedMessage) {

    }

    @Override
    public void onUserJoinedRoomMessage(UserJoinedRoomMessage userJoinedRoomMessage) {

    }

    @Override
    public void onUserLeftRoomMessage(UserLeftRoomMessage userLeftRoomMessage) {

    }

    @Override
    public void onNewChat(Chat chat) {

        chat.addListener(this);

    }

    @Override
    public void onRemovedChat(Chat chat) {

    }


    /**
     *
     * Block until response is received from remote bot
     *
     * @return True if remote BOT has responded
     */
    public boolean isResponded() {


        for (int i = 0; i < 10; ++i) {

            logger.info("RESPONDED: {}: Elapsed Time: {}", responded, i);

            if (responded)
                return true;

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                logger.error("Interrupted Exception, waiting for test response");
            }


        }


        return false;
    }
}