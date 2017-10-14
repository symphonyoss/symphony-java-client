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

package multiclient;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.SymphonyClientConfig;
import org.symphonyoss.client.SymphonyClientConfigID;
import org.symphonyoss.client.SymphonyClientFactory;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.client.exceptions.StreamsException;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymStream;
import org.symphonyoss.symphony.pod.model.Stream;


/**
 * Simple example of the multiple client bot connections from the same JVM.
 * This overrides the default ssl properties and allows for different client certs and trust stores.
 * <p>
 * It will send a message to a call.home.user from different SyphonyClient instances.
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
 *
 * -Duser.cert.password=password
 * -Duser.cert.file=bot.user1.p12
 * -Duser.email=bot.user1@markit.com
 *
 * -Duser2.cert.password=password
 * -Duser2.cert.file=bot.user2.p12
 * -Duser2.email=bot.user2@markit.com
 *
 * -Dpod.url=https://(pod host)/pod
 * -Dagent.url=https://(agent server host)/agent
 * -Dreceiver.email=bot.user2@markit.com or bot user email
 *
 *
 * @author Frank Tarsillo
 */
//NOSONAR
public class MultiClientExample {


    private final Logger logger = LoggerFactory.getLogger(MultiClientExample.class);


    public MultiClientExample() {

        //Note: You can replace all the properties with two different instances of SymphonyClientConfig in this example

        SymphonyClientConfig symphonyClientConfig = new SymphonyClientConfig(true);


        //This will take all the default properties as defined by enums in configuration
        SymphonyClient symClient1 = SymphonyClientFactory.getClient(SymphonyClientFactory.TYPE.V4,
                symphonyClientConfig);


        //We will override the default and pull in a different user
        SymphonyClientConfig symphonyClientConfig1 = new SymphonyClientConfig(true);

        symphonyClientConfig1.set(SymphonyClientConfigID.USER_EMAIL, System.getProperty("user2.email"));
        symphonyClientConfig1.set(SymphonyClientConfigID.USER_CERT_FILE, System.getProperty("user2.cert.file"));
        symphonyClientConfig.set(SymphonyClientConfigID.USER_CERT_PASSWORD, System.getProperty("user2.cert.password"));

        SymphonyClient symClient2 = SymphonyClientFactory.getClient(SymphonyClientFactory.TYPE.V4,symphonyClientConfig1);


        try {
            SymStream stream1 = symClient1.getStreamsClient().getStreamFromEmail(System.getProperty("user.call.home"));

            SymStream stream2 = symClient2.getStreamsClient().getStreamFromEmail(System.getProperty("user.call.home"));


            //A message to send when the BOT comes online.
            SymMessage aMessage = new SymMessage();


            aMessage.setMessageText("Hello master from bot1...");

            symClient1.getMessagesClient().sendMessage(stream1, aMessage);

            aMessage.setMessageText("Hello master from bot2...");

            symClient2.getMessagesClient().sendMessage(stream2, aMessage);

            aMessage.setMessageText("Hello master from bot1..again...");

            symClient1.getMessagesClient().sendMessage(stream1, aMessage);

            aMessage.setMessageText("Hello master from bot2..again and again..");

            symClient2.getMessagesClient().sendMessage(stream2, aMessage);
            symClient2.getMessagesClient().sendMessage(stream2, aMessage);
            symClient2.getMessagesClient().sendMessage(stream2, aMessage);
            symClient2.getMessagesClient().sendMessage(stream2, aMessage);


            aMessage.setMessage("Hello master from bot1..again and again...");

            symClient1.getMessagesClient().sendMessage(stream1, aMessage);
            symClient1.getMessagesClient().sendMessage(stream1, aMessage);
            symClient1.getMessagesClient().sendMessage(stream1, aMessage);
            symClient1.getMessagesClient().sendMessage(stream1, aMessage);
            symClient1.getMessagesClient().sendMessage(stream1, aMessage);

            System.exit(1);

        } catch (StreamsException e) {
            e.printStackTrace();
        } catch (MessagesException e) {
            e.printStackTrace();
        }


    }

    public static void main(String[] args) {

        new MultiClientExample();

    }


}
