/*
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
 */

package org.symphonyoss.symphony.client;

import org.symphonyoss.symphony.agent.api.MessagesApi;
import org.symphonyoss.symphony.agent.model.MessageSubmission;
import org.symphonyoss.symphony.authenticator.api.AuthenticationApi;
import org.symphonyoss.symphony.authenticator.invoker.Configuration;
import org.symphonyoss.symphony.authenticator.model.Token;
import org.symphonyoss.symphony.pod.api.StreamsApi;
import org.symphonyoss.symphony.pod.api.UsersApi;
import org.symphonyoss.symphony.pod.invoker.ApiClient;
import org.symphonyoss.symphony.pod.model.Stream;
import org.symphonyoss.symphony.pod.model.User;
import org.symphonyoss.symphony.pod.model.UserIdList;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

class SymphonyTest {
static {
    HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier()
        {
            public boolean verify(String hostname, SSLSession session)
            {
                // ip address of the service URL(like.23.28.244.244)
                if (hostname.equals("10.35.224.89"))
                    return true;
                return false;
            }
        });
}
    public static void main(String[] args) {


        //System.setProperty("javax.net.ssl.trustStore", "/dev/certs/server.truststore");
        System.setProperty("javax.net.ssl.trustStore", System.getProperty("truststore.path")); 
        System.setProperty("javax.net.ssl.trustStorePassword", System.getProperty("keystore.password"));
        //System.setProperty("javax.net.ssl.keyStore", "/dev/certs/bot.user1.p12");
        System.setProperty("javax.net.ssl.keyStore", System.getProperty("keystore.path"));
        System.setProperty("javax.net.ssl.keyStorePassword", System.getProperty("keystore.password"));
        //System.setProperty("javax.net.ssl.keyStoreType", System.getProperty("keystore.type"));
        //System.setProperty("javax.net.ssl.keyStoreType", "pkcs12");

	String podHost = "markit.symphony.com";
	String gatewayHost = "10.35.224.89";
        

        try {
            org.symphonyoss.symphony.authenticator.invoker.ApiClient authenticatorClient = Configuration.getDefaultApiClient();

            // Configure the authenticator connection
            authenticatorClient.setBasePath("https://"+podHost+":8444/sessionauth");

            // Get the authentication API
            AuthenticationApi authenticationApi = new AuthenticationApi(authenticatorClient);


            Token sessionToken = authenticationApi.v1AuthenticatePost();
            System.out.println(sessionToken.getName() + " : " + sessionToken.getToken());


            // Configure the keyManager path
            authenticatorClient.setBasePath("https://"+podHost+":8444/keyauth");


            Token keyToken = authenticationApi.v1AuthenticatePost();
            System.out.println(keyToken.getName() + " : " + keyToken.getToken());



            //Get Service client to query for userID.
            ApiClient serviceClient = org.symphonyoss.symphony.pod.invoker.Configuration.getDefaultApiClient();
            serviceClient.setBasePath("https://"+gatewayHost+":8446/pod");
            serviceClient.addDefaultHeader(sessionToken.getName(), sessionToken.getToken());
            serviceClient.addDefaultHeader(keyToken.getName(),keyToken.getToken());



            UsersApi usersApi = new UsersApi(serviceClient);


            User user = usersApi.v1UserGet("amit.joshi@markit.com", sessionToken.getToken(),true);


            if(user != null){


                System.out.println("Found userId: " + user.getId());

            }else{

                System.out.println("Could not locate user..");
            }


            StreamsApi streamsApi = new StreamsApi(serviceClient);

            UserIdList userIdList = new UserIdList();
            userIdList.add(user.getId());
            Stream stream = streamsApi.v1ImCreatePost(userIdList,sessionToken.getToken());

            System.out.println("Stream is: " + stream.getId());



            org.symphonyoss.symphony.agent.invoker.ApiClient agentClient = org.symphonyoss.symphony.agent.invoker.Configuration.getDefaultApiClient();

            agentClient.setBasePath("https://"+gatewayHost+":8446/agent");


            MessagesApi messagesApi = new MessagesApi(agentClient);

            MessageSubmission message = new MessageSubmission();
            message.setMessage("THIS IS A MESSAGE FROM YOUR TEST CLIENT");
            message.setFormat(MessageSubmission.FormatEnum.TEXT);

            messagesApi.v1StreamSidMessageCreatePost(stream.getId(),sessionToken.getToken(),keyToken.getToken(),message);


//
//            RoomCreate roomCreate = new RoomCreate();
//            RoomAttributes roomAttributes = new RoomAttributes();
//            roomAttributes.setName("Symbols");
//
//            roomCreate.setRoomAttributes(roomAttributes);
//
//            RoomDetail roomDetail = streamsApi.v1RoomCreatePost(roomCreate,sessionToken.getToken());
//
//            String roomId = roomDetail.getRoomSystemInfo().getId();
//
//            messagesApi.v1StreamSidMessageCreatePost(roomId, sessionToken.getToken(),keyToken.getToken(),message);




        } catch (Exception e) {
            System.err.println("Exception ");
            e.printStackTrace();
        }
    }


}
