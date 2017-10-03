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

package lex;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lexruntime.AmazonLexRuntime;
import com.amazonaws.services.lexruntime.AmazonLexRuntimeClientBuilder;
import com.amazonaws.services.lexruntime.model.PostTextRequest;
import com.amazonaws.services.lexruntime.model.PostTextResult;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.client.services.ChatListener;
import org.symphonyoss.symphony.clients.model.SymMessage;

/**
 * @author Frank Tarsillo on 10/3/17.
 */
public class LexBotRelay implements ChatListener {

    SymphonyClient symClient;
    LexBotDetail lexBotDetail;

    private AWSCredentials credentials = new BasicAWSCredentials(System.getProperty("s3.key.id"), System.getProperty("s3.access.key"));
    private AmazonLexRuntime lexClient = AmazonLexRuntimeClientBuilder.standard().withRegion(Regions.US_EAST_1).withCredentials(new AWSStaticCredentialsProvider(credentials)).build();



    public LexBotRelay(SymphonyClient symClient, LexBotDetail lexBotDetail) {

        this.symClient = symClient;
        this.lexBotDetail = lexBotDetail;

    }


    @Override
    public void onChatMessage(SymMessage message) {


        PostTextResult postTextResult = sendLexMessage(message);

        if(postTextResult.getDialogState().equals("ReadyForFulfillment")) {

            message.setMessageText("Completed appointment..");

        }else{

            message.setMessageText(postTextResult.getMessage());

        }

        try {
            symClient.getMessagesClient().sendMessage(message.getStream(), message);
        } catch (MessagesException e) {
            e.printStackTrace();
        }


    }


    private PostTextResult sendLexMessage(SymMessage symMessage) {

        PostTextRequest postTextRequest = new PostTextRequest();

        postTextRequest.setBotAlias(lexBotDetail.getBotAlias());
        postTextRequest.setBotName(lexBotDetail.getBotName());
        postTextRequest.setRequestAttributes(lexBotDetail.getRequestAttributes());
        postTextRequest.setSessionAttributes(lexBotDetail.getSessionAttributes());
        postTextRequest.setUserId(symMessage.getFromUserId().toString());
        postTextRequest.setInputText(symMessage.getMessageText());


        return lexClient.postText(postTextRequest);

    }
}
