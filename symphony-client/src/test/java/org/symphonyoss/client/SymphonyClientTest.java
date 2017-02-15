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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.model.Room;
import org.symphonyoss.client.services.ChatListener;
import org.symphonyoss.client.services.RoomListener;
import org.symphonyoss.exceptions.InitException;
import org.symphonyoss.exceptions.SymException;
import org.symphonyoss.symphony.agent.model.*;
import org.symphonyoss.symphony.clients.model.SymMessage;

/**
 * This test will simulate an end to end communication between two BOTS.  This test will attempt to establish two
 * connections to the Symphony POD representing two discrete network endpoints.  These endpoints will test against
 * all SymphonyClient features.
 *
 * @author Frank Tarsillo
 */
public class SymphonyClientTest implements ChatListener, RoomListener {

    SymphonyClient sjcTestClient;
    private final  Logger logger = LoggerFactory.getLogger(SymphonyClientFactory.class);

    @Before
    public void setUp() throws Exception {


        try {
            sjcTestClient = SymphonyClientFactory.getClient(
                    SymphonyClientFactory.TYPE.BASIC,System.getProperty("sjc.test.client", "sjc.testclient") + "@" + System.getProperty("bot.domain"),
                    System.getProperty("certs.dir") + System.getProperty("sjc.test.client", "sjc.testclient") + ".p12",
                    System.getProperty("keystore.password"),
                    System.getProperty("truststore.file"),
                    System.getProperty("truststore.password"));


        }catch(Exception e){

            logger.error("Could not init symphony test client",e);


        }

        org.junit.Assume.assumeTrue(sjcTestClient != null);


    }

    @After
    public void tearDown() throws Exception {

        if (sjcTestClient != null)
            sjcTestClient.shutdown();

    }


    @Test
    public void sendRoomMessage() throws Exception {

        Room room = new Room();
        room.setStreamId(System.getProperty("testroom.stream"));
        room.setId(System.getProperty("testroom.stream"));
        room.addListener(this);
        sjcTestClient.getRoomService().joinRoom(room);

        SymMessage message = new SymMessage();
        message.setMessage("/onRoomMessage");
        sjcTestClient.getMessageService().sendMessage(room, message);


    }


    @Override
    public void onChatMessage(SymMessage message) {


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
}