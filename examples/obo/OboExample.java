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
package obo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.SymphonyClientConfig;
import org.symphonyoss.client.SymphonyClientConfigID;
import org.symphonyoss.client.SymphonyClientFactory;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.symphony.clients.*;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymStream;
import org.symphonyoss.symphony.clients.model.SymUser;



/**
 *
 * Simple example of the OBO workflow
 *
 * READ THIS FIRST:: https://rest-api.symphony.com/v1.49/docs/on-behalf-of
 *
 * <p>
 *
 * <p>
 * <p>
 * <p>
 * REQUIRED VM Arguments or System Properties:
 * <p>
 * -Dtruststore.file=
 * -Dtruststore.password=password
 * -Dsessionauth.url=https://(hostname)/sessionauth
 * -Dkeyauth.url=https://(hostname)/keyauth
 * -Duser.cert.password=password
 * -Duser.cert.file=Application certificate CN=appId
 * -Dpod.url=https://(pod host)/pod
 * -Dagent.url=https://(agent server host)/agent
 * -Dreceiver.email=Email of user who will receive the obo message
 *
 * @author Frank Tarsillo
 */
//NOSONAR
public class OboExample {


    private final Logger logger = LoggerFactory.getLogger(OboExample.class);

    private SymphonyClient symClient;

    public OboExample() {


        init();


    }

    public static void main(String[] args) {

        new OboExample();

    }

    public void init() {

        //Define on behalf of user..  In reality you might want to create a service bot instance that can lookup users ids
        SymUser oboUser = new SymUser();
        oboUser.setId(Long.valueOf("70781061038085"));  //Frank Tarsillo (:-))


        //Load config
        SymphonyClientConfig symphonyClientConfig = new SymphonyClientConfig(true);

        //You have to disable real-time services...you will see down below.
        symphonyClientConfig.set(SymphonyClientConfigID.DISABLE_SERVICES,"True");

        try {

            //STEP 1: Create an authentication client
            AuthenticationClient authClient = new AuthenticationClient(symphonyClientConfig);

            //STEP 2: Obtain a session token from the Obo Application. Make sure cert has CN=appid
            SymAuth symAuthApp = authClient.authenticateApp();


            //STEP 3: Using the session token from the Application, obtain a session token for the OboUser or AppUser.
            SymAuth symAuthUser = authClient.authenticateAppUser(oboUser, symAuthApp);

            //Get the result!
            logger.debug("Session Token:{}", symAuthUser.getSessionToken().getToken());



            //Init the client with the SymAuthUser token.  Note: Real-time services are disabled (look above) because they are not supported in Obo.
            symClient = SymphonyClientFactory.getClient(SymphonyClientFactory.TYPE.V4);
            symClient.init(symAuthUser, symphonyClientConfig);


            //Lets test this!
            //
            //Using the SymClient lets get the SymUser for the message.  Remember we are acting as the OboUser now...
            SymUser symUserDest = symClient.getUsersClient().getUserFromEmail(symphonyClientConfig.get(SymphonyClientConfigID.RECEIVER_EMAIL));

            //A message to send
            SymMessage aMessage = new SymMessage();
            aMessage.setMessageText("Hello master, Obo isn't so hard after all....");


            //Get stream for the OboUser<-->DestinationUser
            SymStream symStream = symClient.getStreamsClient().getStream(symUserDest);


            //OboUser sending the message to the user
            symClient.getMessagesClient().sendMessage(symStream, aMessage);


            //Done!


        } catch (Exception e) {
            logger.error("Failed", e);
        }



        logger.info("Finished");



    }


}
