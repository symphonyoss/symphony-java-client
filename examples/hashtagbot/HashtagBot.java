///*
// *
// * Copyright 2016 The Symphony Software Foundation
// *
// * Licensed to The Symphony Software Foundation (SSF) under one
// * or more contributor license agreements.  See the NOTICE file
// * distributed with this work for additional information
// * regarding copyright ownership.  The ASF licenses this file
// * to you under the Apache License, Version 2.0 (the
// * "License"); you may not use this file except in compliance
// * with the License.  You may obtain a copy of the License at
// *
// *   http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing,
// * software distributed under the License is distributed on an
// * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// * KIND, either express or implied.  See the License for the
// * specific language governing permissions and limitations
// * under the License.
// */
//
//package org.symphonyoss.examples.hashtagbot;
//
//import com.google.gson.Gson;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.symphonyoss.client.SymphonyClient;
//import org.symphonyoss.client.SymphonyClientFactory;
//import org.symphonyoss.client.exceptions.MessagesException;
//import org.symphonyoss.client.exceptions.SymException;
//import org.symphonyoss.client.exceptions.UsersClientException;
//import org.symphonyoss.client.model.AttribTypes;
//import org.symphonyoss.client.model.Chat;
//import org.symphonyoss.client.model.NodeTypes;
//import org.symphonyoss.client.model.SymAuth;
//import org.symphonyoss.client.services.ChatListener;
//import org.symphonyoss.client.services.ChatServiceListener;
//import org.symphonyoss.client.services.PresenceListener;
//import org.symphonyoss.client.util.MlMessageParser;
//import org.symphonyoss.symphony.clients.AuthenticationClient;
//import org.symphonyoss.symphony.clients.model.SymMessage;
//import org.symphonyoss.symphony.clients.model.SymUser;
//import org.symphonyoss.symphony.pod.model.Stream;
//import org.symphonyoss.symphony.pod.model.UserPresence;
//
//import java.io.File;
//import java.io.FileReader;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashSet;
//import java.util.Set;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * The hashtag.bot is provides an open hashtag dictionary service.
// * Anyone can add new, append or update existing definitions.
// * Definitions have last update and authors.
// * The BOT stores and loads all definitions through JSON serialized files.
// * <p>
// * This program attempts to show some of the symphony-java-client features, such as
// * ChatService.
// * <p>
// * <p>
// * REQUIRED VM Arguments or System Properties:
// * <p>
// * -Dsessionauth.url=https://pod_fqdn:port/sessionauth
// * -Dkeyauth.url=https://pod_fqdn:port/keyauth
// * -Dsymphony.agent.pod.url=https://agent_fqdn:port/pod
// * -Dsymphony.agent.agent.url=https://agent_fqdn:port/agent
// * -Dcerts.dir=/dev/certs/
// * -Dkeystore.password=(Pass)
// * -Dtruststore.file=/dev/certs/server.truststore
// * -Dtruststore.password=(Pass)
// * -Dbot.user=hashtag.bot
// * -Dbot.domain=@markit.com
// * -Duser.call.home=frank.tarsillo@markit.com
// * -Dfiles.json=/dev/json/
// *
// * @author  Frank Tarsillo
// */
////NOSONAR
//public class HashtagBot implements ChatListener, ChatServiceListener, PresenceListener {
//
//
//    private final Logger logger = LoggerFactory.getLogger(HashtagBot.class);
//    private final ConcurrentHashMap<String, Hashtag> hashtags = new ConcurrentHashMap<>();
//    private SymphonyClient symClient;
//
//    public HashtagBot() {
//
//        logger.info("HashtagBOT starting...");
//        loadAllHashtags();
//        init();
//
//
//    }
//
//    public static void main(String[] args) {
//
//
//        new HashtagBot();
//
//    }
//
//    public void init() {
//
//
//        try {
//
//            //Create a basic client instance.
//            symClient = SymphonyClientFactory.getClient(SymphonyClientFactory.TYPE.BASIC);
//
//            logger.debug("{} {}", System.getProperty("sessionauth.url"),
//                    System.getProperty("keyauth.url"));
//
//
//            //Init the Symphony authorization client, which requires both the key and session URL's.  In most cases,
//            //the same fqdn but different URLs.
//            AuthenticationClient authClient = new AuthenticationClient(
//                    System.getProperty("sessionauth.url"),
//                    System.getProperty("keyauth.url"));
//
//
//            //Set the local keystores that hold the server CA and client certificates
//            authClient.setKeystores(
//                    System.getProperty("truststore.file"),
//                    System.getProperty("truststore.password"),
//                    System.getProperty("certs.dir") + System.getProperty("bot.user") + ".p12",
//                    System.getProperty("keystore.password"));
//
//            //Create a SymAuth which holds both key and session tokens.  This will call the external service.
//            SymAuth symAuth = authClient.authenticate();
//
//            //With a valid SymAuth we can now init our client.
//            symClient.init(
//                    symAuth,
//                    System.getProperty("bot.user") + System.getProperty("bot.domain"),
//                    System.getProperty("symphony.agent.agent.url"),
//                    System.getProperty("symphony.agent.pod.url")
//            );
//
//            //This is not needed, but added for future use.  This will monitor all presence events on the network.
//            symClient.getPresenceService().addPresenceListener(this);
//
//            //Will notify the bot of new Chat conversations.
//            symClient.getChatService().addListener(this);
//
//            //A message to send when the BOT comes online.
//            SymMessage aMessage = new SymMessage();
//            aMessage.setFormat(SymMessage.Format.TEXT);
//            aMessage.setMessage("Hello master, I'm alive again....");
//
//
//            //Creates a Chat session with that will receive the online message.
//            Chat chat = new Chat();
//            chat.setLocalUser(symClient.getLocalUser());
//            Set<SymUser> remoteUsers = new HashSet<>();
//            remoteUsers.add(symClient.getUsersClient().getUserFromEmail(System.getProperty("user.call.home")));
//            chat.setRemoteUsers(remoteUsers);
//            chat.addListener(this);
//            chat.setStream(symClient.getStreamsClient().getStream(remoteUsers));
//
//            //Add the chat to the chat service, in case the "master" continues the conversation.
//            symClient.getChatService().addChat(chat);
//
//
//            //Send a message to the master user.
//            symClient.getMessageService().sendMessage(chat, aMessage);
//
//
//        } catch (SymException e) {
//            logger.error("Something went wrong..", e);
//        }
//
//    }
//
//    //Callback from PresenceService.  This will monitor all presence on the network.
//    @Override
//    public void onUserPresence(UserPresence userPresence) {
//
//        logger.debug("Received user presence update: {} : {}", userPresence.getUid(), userPresence.getCategory());
//
//
//    }
//
//    //Chat sessions callback method.
//    @Override
//    public void onChatMessage(SymMessage message) {
//        if (message == null)
//            return;
//
//        logger.debug("TS: {}\nFrom ID: {}\nSymMessage: {}\nSymMessage Type: {}",
//                message.getTimestamp(),
//                message.getFromUserId(),
//                message.getMessage(),
//                message.getMessageType());
//
//        //Handle the new incoming message.
//        processMessage(message);
//
//    }
//
//    public void processMessage(SymMessage message) {
//
//        MlMessageParser mlMessageParser;
//
//        try {
//            mlMessageParser = new MlMessageParser(symClient);
//            mlMessageParser.parseMessage(message.getMessage());
//        } catch (Exception e) {
//            logger.error("Could not parse message {}", message.getMessage(), e);
//            sendUsage(message);
//            return;
//        }
//
//        //Fix for v1 sending messages with mention tags containing UID attributes.
//        mlMessageParser.updateMentionUidToEmail(symClient);
//
//        //Split the text repsenstation of the incoming message.
//        String[] chunks = mlMessageParser.getTextChunks();
//
//
//        if (chunks.length > 1) {
//
//            String command = chunks[0].toLowerCase().trim();
//
//            switch (command) {
//                case "add":
//                    logger.debug("Add command received from {} ", message.getFromUserId());
//                    addHashtag(mlMessageParser, message);
//                    break;
//                case "update":
//                    logger.debug("Update command received from {} ", message.getFromUserId());
//                    updateHashtag(mlMessageParser, message);
//                    break;
//                case "remove":
//                    logger.debug("Remove command received from {} ", message.getFromUserId());
//                    removeHashtag(mlMessageParser, message);
//                    break;
//                case "search":
//                    logger.debug("Search command received from {} ", message.getFromUserId());
//                    searchHashtag(mlMessageParser, message);
//                    break;
//                default:
//                    sendUsage(message);
//                    break;
//
//            }
//        } else {
//            sendUsage(message);
//
//        }
//
//
//    }
//
//    private void addHashtag(MlMessageParser mlMessageParser, SymMessage message) {
//
//        String[] chunks = mlMessageParser.getTextChunks();
//
//        if (chunks.length < 3) {
//
//            logger.error("Not enough arguments to add hashtag from user {}", message.getFromUserId());
//            sendUsage(message);
//            return;
//        }
//
//        if (chunks[1].startsWith("#")) {
//
//            Hashtag hashtag = new Hashtag();
//            hashtag.setName(chunks[1].substring(1));
//
//            HashtagDef hashtagDef = new HashtagDef();
//            hashtagDef.setUserId(message.getFromUserId());
//            hashtagDef.setDefinition(mlMessageParser.getHtmlStartingFromNode(NodeTypes.HASHTAG.toString(), AttribTypes.TAG.toString(), chunks[1].substring(1)));
//
//
//            Hashtag cHashtag = hashtags.get(hashtag.getName());
//
//
//            if (cHashtag != null) {
//
//                cHashtag.getDefinitions().add(hashtagDef);
//                cHashtag.setLastChange(System.currentTimeMillis());
//                sendHashtagMessage(cHashtag, message);
//            } else {
//                ArrayList<HashtagDef> defs = new ArrayList<>();
//                defs.add(hashtagDef);
//                hashtag.setDefinitions(defs);
//                hashtag.setLastChange(System.currentTimeMillis());
//                hashtags.put(hashtag.getName(), hashtag);
//                cHashtag = hashtag;
//                sendHashtagMessage(hashtag, message);
//            }
//
//            writeHashtagToFile(cHashtag);
//
//        }
//
//    }
//
//    //NOSONAR
//    private void updateHashtag(MlMessageParser mlMessageParser, SymMessage message) {
//        String[] chunks = mlMessageParser.getTextChunks();
//
//
//        if (chunks.length < 4) {
//            logger.error("Not enough arguments to add hashtag from user {}", message.getFromUserId());
//            sendUsage(message);
//            return;
//        }
//
//
//        if (chunks[1].startsWith("#") && chunks[2].startsWith("#")) {
//
//            Hashtag hashtag = new Hashtag();
//            hashtag.setName(chunks[1].substring(1));
//
//            Hashtag cHashtag = hashtags.get(hashtag.getName());
//
//
//            if (cHashtag != null) {
//
//                ArrayList<HashtagDef> defs = cHashtag.getDefinitions();
//
//                if (defs == null) {
//                    sendUsage(message);
//                    return;
//                }
//
//                Hashtag hNum = new Hashtag();
//                hNum.setName(chunks[2].substring(1));
//
//
//                try {
//                    HashtagDef hashtagDef = defs.get(Integer.parseInt(hNum.getName()) - 1);
//
//                    if (hashtagDef == null) {
//                        sendUsage(message);
//                        return;
//                    }
//
//                    logger.debug("Updated message: {}", mlMessageParser.getHtmlStartingFromText(chunks[2]));
//
//                    hashtagDef.setDefinition(mlMessageParser.getHtmlStartingFromNode(NodeTypes.HASHTAG.toString(), AttribTypes.TAG.toString(), chunks[2].substring(1)));
//
//
//                } catch (IndexOutOfBoundsException e) {
//                    logger.error("Index issue",e);
//                    sendDefinitionNotFound(message, hashtag.getName(), hNum.getName());
//                }
//
//
//                cHashtag.setLastChange(System.currentTimeMillis());
//                sendHashtagMessage(cHashtag, message);
//                writeHashtagToFile(cHashtag);
//
//            } else {
//
//                sendNotFound(message, hashtag.getName());
//
//            }
//
//
//        }
//
//
//    }
//
//    private void removeHashtag(MlMessageParser mlMessageParser, SymMessage message) {
//        String[] chunks = mlMessageParser.getTextChunks();
//
//        if (chunks.length < 2 && !chunks[1].startsWith("#")) {
//            sendUsage(message);
//            return;
//        }
//
//
//        Hashtag hashtag = new Hashtag();
//        hashtag.setName(chunks[1].substring(1));
//
//        Hashtag cHashtag = hashtags.get(hashtag.getName());
//
//        try {
//            if (cHashtag != null) {
//
//                if (chunks.length == 3 && chunks[2].startsWith("#")) {
//
//                    int val = Integer.parseInt(chunks[2].substring(1));
//
//                    if ((val - 1) < cHashtag.getDefinitions().size())
//                        cHashtag.getDefinitions().remove(val - 1);
//
//                    sendHashtagMessage(cHashtag, message);
//                    writeHashtagToFile(cHashtag);
//                } else {
//
//                    hashtags.remove(hashtag.getName());
//                    sendRemovedHashtag(message, hashtag.getName());
//                    removeHashtagFile(hashtag);
//                }
//
//
//            } else {
//                sendNotFound(message, hashtag.getName());
//            }
//
//        } catch (Exception e) {
//            logger.error("", e);
//            sendNotFound(message, hashtag.getName());
//        }
//
//
//    }
//
//    private void searchHashtag(MlMessageParser mlMessageParser, SymMessage message) {
//
//        String[] chunks = mlMessageParser.getTextChunks();
//
//        if (chunks.length < 2 && !chunks[1].startsWith("#")) {
//            sendUsage(message);
//            return;
//        }
//
//        Hashtag hashtag = new Hashtag();
//        hashtag.setName(chunks[1].substring(1));
//
//
//        Hashtag cHashtag = hashtags.get(hashtag.getName());
//
//        if (cHashtag != null) {
//            sendHashtagMessage(cHashtag, message);
//        } else {
//            sendNotFound(message, hashtag.getName());
//        }
//
//
//    }
//
//    private void sendNotFound(SymMessage message, String hashtag) {
//
//        SymMessage aMessage = new SymMessage();
//        aMessage.setFormat(SymMessage.Format.MESSAGEML);
//        aMessage.setMessage("<messageML><br/>Sorry..hashtag <hash tag=\"" + hashtag + "\"/> not found.<br/></messageML>");
//
//        Stream stream = new Stream();
//        stream.setId(message.getStreamId());
//        try {
//            symClient.getMessagesClient().sendMessage(stream, aMessage);
//        } catch (MessagesException e) {
//            logger.error("Failed to send message...",e);
//        }
//
//
//    }
//
//
//    private void sendDefinitionNotFound(SymMessage message, String hashtag, String num) {
//
//        SymMessage aMessage = new SymMessage();
//        aMessage.setFormat(SymMessage.Format.MESSAGEML);
//        aMessage.setMessage("<messageML><br/>Sorry..hashtag <hash tag=\"" + hashtag + "\"/> <hash tag=\"" + num + "\"/> not found.<br/></messageML>");
//
//        Stream stream = new Stream();
//        stream.setId(message.getStreamId());
//        try {
//            symClient.getMessagesClient().sendMessage(stream, aMessage);
//        } catch (MessagesException e) {
//            logger.error("Failed to send message..",e);
//        }
//
//
//    }
//
//    private void sendUsage(SymMessage message) {
//
//        SymMessage aMessage = new SymMessage();
//        aMessage.setFormat(SymMessage.Format.MESSAGEML);
//        aMessage.setMessage("<messageML>Sorry...  <br/><b>Check the usage:</b><br/>" +
//                "<b>   Add</b>        #hashtag definition<br/>" +
//                "<b>   Update</b>  #hashtag #(num) definition<br/>" +
//                "<b>   Remove</b> #hashtag #(num) <br/>" +
//                "<b>   Search</b>   #hashtag<br/></messageML>"
//        );
//
//        Stream stream = new Stream();
//        stream.setId(message.getStreamId());
//        try {
//            symClient.getMessagesClient().sendMessage(stream, aMessage);
//        } catch (MessagesException e) {
//            logger.error("Error sending message..",e);
//        }
//
//
//    }
//
//
//    private void sendRemovedHashtag(SymMessage message, String hashtag) {
//
//        SymMessage aMessage = new SymMessage();
//        aMessage.setFormat(SymMessage.Format.MESSAGEML);
//        aMessage.setMessage("<messageML><br/>Completely removed hashtag <b>#" + hashtag + "</b></messageML>");
//
//        Stream stream = new Stream();
//        stream.setId(message.getStreamId());
//        try {
//            symClient.getMessagesClient().sendMessage(stream, aMessage);
//        } catch (MessagesException e) {
//            logger.error("Failed to send message..",e);
//        }
//
//
//    }
//
//    private void sendHashtagMessage(Hashtag hashtag, SymMessage message) {
//
//        StringBuilder out = new StringBuilder();
//
//        SymMessage aMessage = new SymMessage();
//        aMessage.setFormat(SymMessage.Format.MESSAGEML);
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss z");
//        Date date = new Date(hashtag.getLastChange());
//
//
//        out.append("<messageML>  <br/>Definition for <hash tag=\"").append(hashtag.getName()).append("\"/>: <br/>Last modified [").append(simpleDateFormat.format(date)).append("]<br/> ");
//
//        int i = 1;
//        for (HashtagDef def : hashtag.getDefinitions()) {
//
//            out.append("<br/>   <hash tag=\"").append(i).append("\"/>:  ");
//            out.append(def.getDefinition());
//            try {
//                out.append("<br/>         by <mention email=\"").append(symClient.getUsersClient().getUserFromId(def.getUserId()).getEmailAddress()).append("\"/>");
//            } catch (UsersClientException e) {
//                logger.error("Could not append mention due to failed email lookup from userId:  {}", def.getUserId(),e);
//            }
//
//            ++i;
//        }
//
//        out.append("</messageML>");
//        aMessage.setMessage(out.toString());
//
//        logger.debug("{}", out.toString());
//
//        Stream stream = new Stream();
//        stream.setId(message.getStreamId());
//        try {
//            symClient.getMessagesClient().sendMessage(stream, aMessage);
//        } catch (MessagesException e) {
//            logger.error("Error sending message...",e);
//        }
//
//
//    }
//
//    @Override
//    public void onNewChat(Chat chat) {
//
//        chat.addListener(this);
//
//        logger.debug("New chat session detected on stream {} with {}", chat.getStream().getId(), chat.getRemoteUsers());
//    }
//
//    @Override
//    public void onRemovedChat(Chat chat) {
//
//    }
//
//
//    private void writeHashtagToFile(Hashtag hashtag) {
//
//        try {
//            Gson gson = new Gson();
//            FileWriter jsonFile = new FileWriter(System.getProperty("files.json") + hashtag.getName() + ".json");
//            gson.toJson(hashtag, jsonFile);
//            jsonFile.flush();
//            jsonFile.close();
//
//        } catch (IOException e) {
//            logger.error("Could not write file for hashtag {}", hashtag.getName(), e);
//        }
//
//    }
//
//    private void removeHashtagFile(Hashtag hashtag) {
//
//
//        final boolean delete = new File(System.getProperty("files.json") + hashtag.getName() + ".json").delete();
//
//
//    }
//
//
//    private void loadAllHashtags() {
//
//	String fileName = System.getProperty("files.json");
//
//        if (fileName == null) {
//            logger.error("Set the directory containing config files as -Dfiles.json=path");
//
//            System.exit(1);
//        }
//
//        File[] files = new File(fileName).listFiles();
//
//        if (files == null) {
//            logger.error("Failed to load locate directory [{}] for json pre-load..exiting", fileName);
//            System.exit(1);
//        }
//        Gson gson = new Gson();
//
//        for (File file : files) {
//
//            try {
//                Hashtag hashtag = gson.fromJson(new FileReader(file), Hashtag.class);
//                hashtags.put(hashtag.getName(), hashtag);
//
//            } catch (IOException e) {
//                logger.error("Could not load json {} ", file.getName(), e);
//            }
//        }
//
//
//    }
//
//}
