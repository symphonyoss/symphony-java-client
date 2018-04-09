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

package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.SymphonyClientConfig;
import org.symphonyoss.client.SymphonyClientConfigID;
import org.symphonyoss.client.SymphonyClientFactory;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.client.exceptions.UsersClientException;
import org.symphonyoss.symphony.agent.api.MessagesApi;
import org.symphonyoss.symphony.agent.invoker.ApiException;
import org.symphonyoss.symphony.agent.model.V4ImportResponseList;
import org.symphonyoss.symphony.agent.model.V4ImportedMessage;
import org.symphonyoss.symphony.agent.model.V4MessageImportList;
import org.symphonyoss.symphony.clients.model.ApiVersion;
import org.symphonyoss.symphony.clients.model.SymAttachmentInfo;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymStream;
import org.symphonyoss.symphony.pod.model.MemberInfo;
import org.symphonyoss.symphony.pod.model.MembershipList;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * CAUTION: This is a fairly dangerous example, so make sure to read detail below before running.
 * <p>
 * Did you ever need to copy historical content and users from one chat room to another?  You tend to need this
 * when you have no way of changing compliance properties of a chat room, so the only option is to create a new one.
 * <p>
 * This example will copy membership and historical IM's from source to destination chat rooms. Attachments are ignored.
 * <p>
 * You must first create a destination room and add in user2 as owner.
 * <p>
 * <p>
 * <p>
 * REQUIRED VM Arguments or System Properties or using SymphonyClientConfig:
 * <p>
 * -Dtruststore.file=
 * -Dtruststore.password=password
 * -Dsessionauth.url=https://(hostname)/sessionauth
 * -Dkeyauth.url=https://(hostname)/keyauth
 * -Duser.cert.password=password
 * -Duser.cert.file=User that has access to source room from start of time
 * -Duser.email=User that has access to source room from start of time
 * <p>
 * -Duser2.cert.password=password
 * -Duser2.cert.file=Bot user who is owner of destination room
 * -Duser2.email=User that has access to source room from start of time
 * <p>
 * -Dpod.url=https://(pod host)/pod
 * -Dagent.url=https://(agent server host)/agent
 * -Dreceiver.email= email address of user or bot who will receive a message.
 * -Dsource.stream= Stream ID of source chat room
 * -Ddest.stream= Stream ID of destination chat room
 * -
 *
 * @author Frank Tarsillo on 5/9/17.
 */
//NOSONAR
public class CopyChatRoom {


    private final Logger logger = LoggerFactory.getLogger(CopyChatRoom.class);


    private SymphonyClient symClient;

    public CopyChatRoom() {


        init();


    }

    public static void main(String[] args) {

        new CopyChatRoom();

    }

    public void init() {


        SymphonyClientConfig symphonyClientConfig = new SymphonyClientConfig();

        //Disable all real-time services
        symphonyClientConfig.set(SymphonyClientConfigID.DISABLE_SERVICES, "True");

        //Create an initialized client
        symClient = SymphonyClientFactory.getClient(
                SymphonyClientFactory.TYPE.V4, symphonyClientConfig);


        //We will override the default and pull in a different user
        SymphonyClientConfig symphonyClientConfig1 = new SymphonyClientConfig();
        //Disable all real-time services
        symphonyClientConfig1.set(SymphonyClientConfigID.DISABLE_SERVICES, "True");

        symphonyClientConfig1.set(SymphonyClientConfigID.USER_EMAIL, System.getProperty("user2.email"));
        symphonyClientConfig1.set(SymphonyClientConfigID.USER_CERT_FILE, System.getProperty("user2.cert.file"));
        symphonyClientConfig1.set(SymphonyClientConfigID.USER_CERT_PASSWORD, System.getProperty("user2.cert.password"));

        SymphonyClient symClient2 = SymphonyClientFactory.getClient(SymphonyClientFactory.TYPE.V4, symphonyClientConfig1);


        String sourceRoomId = System.getProperty("source.stream");

        String destRoomId = System.getProperty("dest.stream");


        try {

            MembershipList memberInfos = symClient.getRoomMembershipClient().getRoomMembership(sourceRoomId);


            for (MemberInfo memberInfo : memberInfos) {

                symClient2.getRoomMembershipClient().addMemberToRoom(destRoomId, memberInfo.getId());

            }


        } catch (Exception e) {
            e.printStackTrace();
        }


        List<SymAttachmentInfo> symAttachmentInfos = new ArrayList<>();


        SymStream symStream = new SymStream();

        symStream.setStreamId(sourceRoomId);
        long since = 0;
        List<SymMessage> symMessageList = null;

        MessagesApi messagesApi = symClient2.getSymphonyApis().getMessagesApi();
        SymMessage prevMessage = new SymMessage();
        prevMessage.setMessage("");

        while (true) {


            try {
                symMessageList = symClient.getMessagesClient().getMessagesFromStream(symStream, since, 0, 20, ApiVersion.V2);
                if (symMessageList.size() == 0)
                    break;

            } catch (MessagesException e) {
                logger.error("Failed to send message", e);

                since += 86400000;
                continue;
            }


            V4MessageImportList v2ImportedMessages = new V4MessageImportList();


            for (SymMessage symMessage : symMessageList) {

                try {

                    String userName = symClient.getUsersClient().getUserFromId(symMessage.getFromUserId()).getDisplayName();

                    logger.debug("{}:{}:{}:{}", userName, new Date(Long.valueOf(symMessage.getTimestamp())).toString(), symMessage.getMessage());

                    if (symMessage.getAttachments() != null && symMessage.getAttachments().size() > 0) {
                        symAttachmentInfos.addAll(symMessage.getAttachments());


                    }


                    V4ImportedMessage v2ImportedMessage = new V4ImportedMessage();
                    v2ImportedMessage.setIntendedMessageFromUserId(symMessage.getFromUserId());
                    v2ImportedMessage.setIntendedMessageTimestamp(Long.valueOf(symMessage.getTimestamp()));
                    v2ImportedMessage.setStreamId(destRoomId);


                    v2ImportedMessage.setOriginatingSystemId(userName);
                    v2ImportedMessage.setMessage(symMessage.getMessage());

                    v2ImportedMessages.add(v2ImportedMessage);


                    since = Long.valueOf(symMessage.getTimestamp()) + 1;

                } catch (UsersClientException e) {
                    e.printStackTrace();
                }
            }

            try {
                V4ImportResponseList v2ImportResponses = messagesApi.v4MessageImportPost(symClient2.getSymAuth().getSessionToken().getToken(), symClient2.getSymAuth().getKeyToken().getToken(), v2ImportedMessages);

                logger.debug("Response: {}", v2ImportResponses.toString());

            } catch (ApiException e) {
                e.printStackTrace();
            }


            logger.debug("------NEXT---------");
        }


        if (symClient != null)
            symClient.shutdown();

        logger.info("Finished");


    }

}



