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
import org.symphonyoss.client.model.Chat;
import org.symphonyoss.client.model.Room;
import org.symphonyoss.client.services.*;
import org.symphonyoss.exceptions.MessagesException;
import org.symphonyoss.exceptions.SymException;
import org.symphonyoss.symphony.agent.model.*;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymUserConnection;
import org.symphonyoss.symphony.pod.model.UserPresence;

/**
 * This is the test BOT that will receive messages from the sjcTestClient.  These messages will request certain
 * functions be executed.  If the event has been executed, the BOT will send a confirmation reply.   The idea is you
 * can synchronously test features.
 *
 * @author Frank Tarsillo
 */
public class sjcTestBot implements ChatListener, ChatServiceListener, RoomListener, RoomServiceListener, PresenceListener, ConnectionsListener {

    SymphonyClient symphonyClient;
    String sjcClient = System.getProperty("sjc.test.client", "sjc.testclient") + "@" + System.getProperty("bot.domain");
    private final Logger logger = LoggerFactory.getLogger(sjcTestBot.class);

    public sjcTestBot() throws SymException {

        symphonyClient = SymphonyClientFactory.getClient(SymphonyClientFactory.TYPE.BASIC,
                System.getProperty("sjc.test.bot", "sjc.testbot") + "@" + System.getProperty("bot.domain"),
                System.getProperty("certs.dir") + System.getProperty("sjc.test.bot", "sjc.testbot") + ".p12",
                System.getProperty("keystore.password"),
                System.getProperty("truststore.file"),
                System.getProperty("truststore.password"));


    }


    @Override
    public void onRoomMessage(SymMessage symMessage) {

        try {
            symphonyClient.getMessageService().sendMessage(sjcClient, symMessage);
        } catch (MessagesException e) {
            logger.error("Could not send onRoomMessage reply", e);
        }


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
    public void onRoomCreatedMessage(RoomCreatedMessage roomCreatedMessage) {

    }

    @Override
    public void onMessage(SymMessage symMessage) {

    }

    @Override
    public void onNewRoom(Room room) {

    }

    @Override
    public void onUserPresence(UserPresence userPresence) {

    }

    @Override
    public void onNewChat(Chat chat) {

    }

    @Override
    public void onRemovedChat(Chat chat) {

    }

    @Override
    public void onChatMessage(SymMessage message) {

    }

    @Override
    public void onConnectionNotification(SymUserConnection userConnection) {

    }
}
