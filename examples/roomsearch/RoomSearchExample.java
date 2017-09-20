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

package roomsearch;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.SymphonyClientConfig;
import org.symphonyoss.client.SymphonyClientFactory;
import org.symphonyoss.client.exceptions.StreamsException;
import org.symphonyoss.client.services.RoomService;
import org.symphonyoss.symphony.clients.model.SymRoomDetail;
import org.symphonyoss.symphony.clients.model.SymRoomSearchCriteria;
import org.symphonyoss.symphony.clients.model.SymRoomSearchResults;


/**
 * Simple example of room search functions.
 * <p>
 * <p>
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
 * -Dpod.url=https://(pod host)/pod
 * -Dagent.url=https://(agent server host)/agent
 * -Dreceiver.email=bot.user2@markit.com or bot user email
 *
 * -Droom.search.query=(search term)
 * <p>
 * <p>
 * <p>
 * <p>
 * Created by Frank Tarsillo on 5/15/2016.
 */
public class RoomSearchExample {


    private final Logger logger = LoggerFactory.getLogger(RoomSearchExample.class);
    private RoomService roomService;

    public RoomSearchExample() {


        init();


    }

    public static void main(String[] args) {

        new RoomSearchExample();

    }

    public void init() {

        logger.info("Room Example starting...");

        try {


            //Create an initialized client
            SymphonyClient symClient = SymphonyClientFactory.getClient(SymphonyClientFactory.TYPE.V4,new SymphonyClientConfig(true));


            SymRoomSearchCriteria symRoomSearchCriteria = new SymRoomSearchCriteria();
            symRoomSearchCriteria.setQuery(System.getProperty("room.search.query"));

            SymRoomSearchResults symRoomSearchResults = symClient.getStreamsClient().roomSearch(symRoomSearchCriteria, 0, 100);

            for (SymRoomDetail symRoomDetail : symRoomSearchResults.getRooms()) {

                logger.info("Found room {}: {}", symRoomDetail.getRoomAttributes().getName(), symRoomDetail.getRoomSystemInfo().getId());


            }

            symClient.shutdown();

            System.exit(1);


        } catch (StreamsException e) {
            logger.error("error", e);
        } catch (Exception e) {
            logger.error("Unkown Exception", e);
        }

    }

}
