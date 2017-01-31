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

package org.symphonyoss.client.ai.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.model.Chat;
import org.symphonyoss.exceptions.MessagesException;
import org.symphonyoss.exceptions.StreamsException;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.pod.model.Stream;
import org.symphonyoss.symphony.pod.model.UserIdList;

/**
 * AI messenger internal utility to send response messages back to users issuing commands.
 *
 * @author Nicholas Tarsillo
 */
@SuppressWarnings("SameParameterValue")
public class Messenger {
    private static final Logger logger = LoggerFactory.getLogger(Messenger.class);

    public static void sendMessage(String message, SymMessage.Format type, Long userID, SymphonyClient symClient) {
        SymMessage userMessage = new SymMessage();
        userMessage.setFormat(type);
        userMessage.setMessage(message);

        UserIdList list = new UserIdList();
        list.add(userID);
        try {
            symClient.getMessagesClient().sendMessage(symClient.getStreamsClient().getStream(list), userMessage);
        } catch (MessagesException e) {
            logger.error("API exception when communicating with POD while sending message",e);
        }catch(Exception e){
            logger.error("Unknown exception when communicating with POD while sending message",e);

        }
    }

    public static void sendMessage(String message, SymMessage.Format type, String email, SymphonyClient symClient) {
        SymMessage userMessage = new SymMessage();
        userMessage.setFormat(type);
        userMessage.setMessage(message);

        try {
            symClient.getMessageService().sendMessage(email, userMessage);
        } catch (MessagesException e) {
            logger.error("API exception when communicating with POD while sending message",e);
        }catch(Exception e){
            logger.error("Unknown exception when communicating with POD while sending message",e);

        }
    }

    public static void sendMessage(String message, SymMessage.Format type, SymMessage refMes, SymphonyClient symClient) {
        SymMessage userMessage = new SymMessage();
        userMessage.setFormat(type);
        userMessage.setMessage(message);

        Stream stream = new Stream();
        stream.setId(refMes.getStreamId());
        try {
            symClient.getMessagesClient().sendMessage(stream, userMessage);
        } catch (MessagesException e) {
            logger.error("API exception when communicating with POD while sending message",e);
        }catch(Exception e){
            logger.error("Unknown exception when communicating with POD while sending message",e);

        }
    }

    public static void sendMessage(String message, SymMessage.Format type, Chat chat, SymphonyClient symClient) {
        SymMessage userMessage = new SymMessage();
        userMessage.setFormat(type);
        userMessage.setMessage(message);

        try {
            symClient.getMessageService().sendMessage(chat, userMessage);
        } catch (MessagesException e) {
            logger.error("API exception when communicating with POD while sending message",e);
        }catch(Exception e){
            logger.error("Unknown exception when communicating with POD while sending message",e);

        }
    }

    public static Chat getChat(Long userID, SymphonyClient symClient) {
        UserIdList list = new UserIdList();
        list.add(userID);
        Stream stream;
        try {

            stream = symClient.getStreamsClient().getStream(list);

            if( stream.getId() != null)
                return symClient.getChatService().getChatByStream(stream != null ? stream.getId() : null);



        } catch (StreamsException e) {
            logger.error("API exception when communicating with POD while retrieving stream",e);
        }catch(Exception e){
            logger.error("Unknown exception when communicating with POD while retrieving stream",e);

        }

        return null;


    }
}
