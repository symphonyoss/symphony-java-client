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

package streams;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.SymphonyClientConfig;
import org.symphonyoss.client.SymphonyClientFactory;
import org.symphonyoss.client.exceptions.StreamsException;
import org.symphonyoss.client.model.CacheType;
import org.symphonyoss.client.services.SymUserCache;
import org.symphonyoss.symphony.clients.model.SymAdminStreamAttributes;
import org.symphonyoss.symphony.clients.model.SymAdminStreamFilter;
import org.symphonyoss.symphony.clients.model.SymAdminStreamInfo;
import org.symphonyoss.symphony.clients.model.SymAdminStreamList;

import java.util.Date;


/**
 * Streams example showing how you can search for bot user associated streams by criteria filer.
 *
 *
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
 *
 *
 * @author Frank Tarsillo on 5/9/17.
 */
//NOSONAR
public class StreamsExample {


    private final Logger logger = LoggerFactory.getLogger(StreamsExample.class);

    private SymphonyClient symClient;

    public StreamsExample() {


        init();


    }

    public static void main(String[] args) {

        new StreamsExample();

    }

    public void init() {


        try {


            //Create an initialized client
            symClient = SymphonyClientFactory.getClient(
                    SymphonyClientFactory.TYPE.V4,new SymphonyClientConfig(true));  //truststore password

            SymAdminStreamFilter symAdminStreamFilter = new SymAdminStreamFilter();

// Uncomment below to filter on ROOM streams..
//
//            List<SymStreamType> symStreamTypes = new ArrayList<>();
//            SymStreamType symStreamType = new SymStreamType();
//            symStreamType.setType(SymStreamType.Type.ROOM);
//
//            symStreamTypes.add(symStreamType);
//
//            symAdminStreamFilter.setStreamTypes(symStreamTypes);



            SymAdminStreamList symAdminStreamList = symClient.getStreamsClient().getStreams(null, null, symAdminStreamFilter);

            for (SymAdminStreamInfo symAdminStreamInfo : symAdminStreamList.getStreams()) {
                prettyOutput(symAdminStreamInfo);

            }

            symClient.shutdown();

            logger.info("Finished");


        } catch (StreamsException e) {
            logger.error("error", e);
        }

    }


    public void prettyOutput(SymAdminStreamInfo symAdminStreamInfo) {

        SymUserCache symUserCache = (SymUserCache) symClient.getCache(CacheType.USER);

        StringBuffer stringBuffer = new StringBuffer();


        stringBuffer.append("[").append(symAdminStreamInfo.getId()).append("]:");
        stringBuffer.append("[").append(symAdminStreamInfo.getType()).append("]:");
        stringBuffer.append("[EXTERNAL:").append(symAdminStreamInfo.getIsExternal().toString()).append("] ");


        SymAdminStreamAttributes attrib = symAdminStreamInfo.getAttributes();

        try {
            stringBuffer.append("Created By: ").append(symUserCache.getUserById(attrib.getCreatedByUserId()).getDisplayName()).append(" ,");
        } catch (Exception e) {
            logger.error("failed to retrieve user {}", attrib.getCreatedByUserId());
        }


        stringBuffer.append("Created Date: ").append(new Date(attrib.getCreatedDate()).toString()).append(" ,");
        stringBuffer.append("#Members: ").append(attrib.getMembersCount()).append(" ,");
        stringBuffer.append("RoomName: ").append(attrib.getRoomName()).append(" ,");
        stringBuffer.append("Company: ").append(attrib.getOriginCompany()).append(" ,");
        stringBuffer.append("Users: ");


        for (Long uid : attrib.getMembers()) {


            try {
                stringBuffer.append(symUserCache.getUserById(uid).getDisplayName()).append(", ");
            } catch (Exception e) {
                logger.error("failed to retrieve user {}", uid);
            }

        }

        stringBuffer.append("\n");

        logger.info("{}", stringBuffer.toString());

    }

}



