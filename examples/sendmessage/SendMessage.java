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

package sendmessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.SymphonyClientConfig;
import org.symphonyoss.client.SymphonyClientConfigID;
import org.symphonyoss.client.SymphonyClientFactory;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.client.exceptions.StreamsException;
import org.symphonyoss.symphony.clients.model.*;


/**
 * Streams example showing how you can search for bot user associated streams by criteria filer.
 *
 *
 * REQUIRED VM Arguments or System Properties or using SymphonyClientConfig:
 * <p>
 * -Dtruststore.file=
 * -Dtruststore.password=password
 * -Dsessionauth.url=https://(hostname)/sessionauth
 * -Dkeyauth.url=https://(hostname)/keyauth
 * -Duser.call.home=frank.tarsillo@markit.com
 * -Duser.cert.password=password
 * -Duser.cert.file=bot.user2.p12
 * -Duser.email=bot.user2@domain.com
 * -Dpod.url=https://(pod host)/pod
 * -Dagent.url=https://(agent server host)/agent
 * -Dreceiver.email= email address of user or bot who will receive a message.
 *
 *
 *
 * @author Frank Tarsillo on 5/9/17.
 */
//NOSONAR
public class SendMessage {


    private final Logger logger = LoggerFactory.getLogger(SendMessage.class);


    private SymphonyClient symClient;

    public SendMessage() {


        init();


    }

    public static void main(String[] args) {

        new SendMessage();

    }

    public void init() {


        try {


            SymphonyClientConfig symphonyClientConfig = new SymphonyClientConfig();

            //Disable all real-time services
            symphonyClientConfig.set(SymphonyClientConfigID.DISABLE_SERVICES, "True");

            //Create an initialized client
            symClient = SymphonyClientFactory.getClient(
                    SymphonyClientFactory.TYPE.V4,symphonyClientConfig);


            String receiverEmail = symphonyClientConfig.get(SymphonyClientConfigID.RECEIVER_EMAIL);


            SymMessage symMessage = new SymMessage();

            symMessage.setMessageText(ApiVersion.V4,"Hello world..");


            try {

                symClient.getMessagesClient().sendMessage(symClient.getStreamsClient().getStreamFromEmail(receiverEmail), symMessage);

            } catch (MessagesException e) {
                logger.error("Failed to send message",e);
            }



            if(symClient != null)
                symClient.shutdown();

            logger.info("Finished");


        } catch (StreamsException e) {
            logger.error("error", e);
        }

    }

}



