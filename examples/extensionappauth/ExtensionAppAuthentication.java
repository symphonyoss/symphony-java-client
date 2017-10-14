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
package extensionappauth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.SymphonyClientConfig;
import org.symphonyoss.client.SymphonyClientConfigID;
import org.symphonyoss.client.exceptions.AuthenticationException;
import org.symphonyoss.client.impl.CustomHttpClient;
import org.symphonyoss.symphony.clients.AuthenticationClient;
import org.symphonyoss.symphony.clients.model.SymExtensionAppAuth;

import javax.ws.rs.client.Client;


/**
 * Simple example of the Extension App Authentication.
 *
 * Note: The client certificate must have a CN=(AppId) registered in the Symphony POD
 *
 * <p>
 * It will send a message to a call.home.user and listen/create new Chat sessions.
 * <p>
 * <p>
 * <p>
 * REQUIRED VM Arguments or System Properties:
 * <p>
 * -Dtruststore.file=
 * -Dtruststore.password=password
 * -Dsessionauth.url=https://(hostname)/sessionauth
 * -Dkeyauth.url=https://(hostname)/keyauth
 * -Duser.call.home=joe.smith@email.com
 * -Duser.cert.password=password
 * -Duser.cert.file=(AppId).p12
 * -Duser.email=(AppId.email)
 * -Dpod.url=https://(pod host)/pod
 * -Dagent.url=https://(agent server host)/agent
 * -Dreceiver.email=bot.user2@markit.com or bot user email
 *
 * @author Frank Tarsillo
 */
//NOSONAR
public class ExtensionAppAuthentication  {


    private final Logger logger = LoggerFactory.getLogger(ExtensionAppAuthentication.class);

    private SymphonyClient symClient;

    public ExtensionAppAuthentication() {


        init();


    }

    public static void main(String[] args) {

        new ExtensionAppAuthentication();

    }

    public void init() {





            SymphonyClientConfig symphonyClientConfig = new SymphonyClientConfig(true);

            try {
                Client httpClient = CustomHttpClient.getClient(
                        symphonyClientConfig.get(SymphonyClientConfigID.USER_CERT_FILE),
                        symphonyClientConfig.get(SymphonyClientConfigID.USER_CERT_PASSWORD),
                        symphonyClientConfig.get(SymphonyClientConfigID.TRUSTSTORE_FILE),
                        symphonyClientConfig.get(SymphonyClientConfigID.TRUSTSTORE_PASSWORD));


                //Init the Symphony authorization client, which requires both the key and session URL's.  In most cases,
                //the same fqdn but different URLs.
                AuthenticationClient authClient = new AuthenticationClient(
                        symphonyClientConfig.get(SymphonyClientConfigID.SESSIONAUTH_URL),
                        symphonyClientConfig.get(SymphonyClientConfigID.KEYAUTH_URL),
                        httpClient);


                SymExtensionAppAuth symExtensionAppAuth = authClient.authenticateExtensionApp("Anything");

                logger.info("SymExtensionsAppAuth- AppId: [{}] AppToken: [{}] SymToken: [{}], Expire: [{}]",
                        symExtensionAppAuth.getAppId(),
                        symExtensionAppAuth.getAppToken(),
                        symExtensionAppAuth.getSymphonyToken(),
                        symExtensionAppAuth.getExpireAt());

                logger.info("Finished");


            } catch (AuthenticationException e) {
                logger.error("error", e);
            } catch (Exception e) {
                logger.error("General exception thrown", e);
            }



    }

}
