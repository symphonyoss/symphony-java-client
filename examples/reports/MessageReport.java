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

package reports;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.SymphonyClientConfig;
import org.symphonyoss.client.SymphonyClientConfigID;
import org.symphonyoss.client.SymphonyClientFactory;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.symphony.clients.model.ApiVersion;
import org.symphonyoss.symphony.clients.model.SymAttachmentInfo;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymStream;

import java.util.ArrayList;
import java.util.List;


/**
 * Retrieve messages from a stream
 *
 * <p>
 * <p>
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
 * -Dstream=Stream ID to retrieve messages from
 *
 * @author Frank Tarsillo on 5/9/17.
 */
//NOSONAR
public class MessageReport {


    private final Logger logger = LoggerFactory.getLogger(MessageReport.class);


    private SymphonyClient symClient;

    public MessageReport() {


        init();


    }

    public static void main(String[] args) {

        new MessageReport();

    }

    public void init() {


        SymphonyClientConfig symphonyClientConfig = new SymphonyClientConfig();

        //Disable all real-time services
        symphonyClientConfig.set(SymphonyClientConfigID.DISABLE_SERVICES, "True");

        //Create an initialized client
        symClient = SymphonyClientFactory.getClient(
                SymphonyClientFactory.TYPE.V4, symphonyClientConfig);


        List<SymAttachmentInfo> symAttachmentInfos = new ArrayList<>();


        SymStream symStream = new SymStream();

        symStream.setStreamId(System.getProperty("stream"));

        long since = 0;
        List<SymMessage> symMessageList = null;

        while (true) {


        try {
            symMessageList = symClient.getMessagesClient().getMessagesFromStream(symStream, since, 0, 20, ApiVersion.V4);




            if (symMessageList.size() == 0) {

                logger.debug("NOTHING FOUND");
              break;
            }

            boolean first = true;
            for (SymMessage symMessage : symMessageList) {

                if(first) {
                    since = Long.valueOf(symMessage.getTimestamp()) + 1;
                    first = false;
                }

                logger.debug("TS: {}\nFrom ID: {}\nSymMessage: {}:{}\n",
                        symMessage.getTimestamp(),
                        symMessage.getFromUserId(),
                        symMessage.getMessage(),
                        symMessage.getEntityData());



            }


        } catch (MessagesException e) {
            logger.error("Failed to send message", e);


        }


        logger.debug("------NEXT---------");
        }


        if (symClient != null)
            symClient.shutdown();

        logger.info("Finished");


    }

}



