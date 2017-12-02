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
import org.symphonyoss.symphony.pod.invoker.ApiException;



/**
 *
 *
 * CAUTION: This is a fairly dangerous example, so make sure to read detail below before running.
 * <p>
 *  This example redacts (suppresses) a message
 *
 *  The bot user name needs to have privileged access to content, so check with your administrator.
 *
 *  Search and replace "REDACT_MESSAGE_ID" with the symphony messageID to suppress
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
 * -Duser.cert.file=privileged user
 * -Duser.email=privileged user email
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
 * @author Frank Tarsillo on 11/29/17.
 */
public class RedactMessage {
    private SymphonyClient symClient;


    private final Logger logger = LoggerFactory.getLogger(RedactMessage.class);

    public RedactMessage(){

        init();
    }

    public static void main(String[] args) {

        new RedactMessage();

    }

    public void init() {


        SymphonyClientConfig symphonyClientConfig = new SymphonyClientConfig();

        //Disable all real-time services
        symphonyClientConfig.set(SymphonyClientConfigID.DISABLE_SERVICES, "True");

        //Create an initialized client
        symClient = SymphonyClientFactory.getClient(
                SymphonyClientFactory.TYPE.V4, symphonyClientConfig);


        try {
            symClient.getSymphonyApis().getMessageSuppressionApi().v1AdminMessagesuppressionIdSuppressPost(
                    "REDACT_MESSAGE_ID", symClient.getSymAuth().getSessionToken().getToken());
        } catch (ApiException e) {
            e.printStackTrace();
        }


        if (symClient != null)
            symClient.shutdown();

        logger.info("Finished");


    }

}



