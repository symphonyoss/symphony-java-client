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

package org.symphonyoss.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.impl.CustomHttpClient;
import org.symphonyoss.client.impl.SymphonyBasicClient;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.exceptions.AuthorizationException;
import org.symphonyoss.exceptions.InitException;
import org.symphonyoss.symphony.clients.AuthorizationClient;

import javax.ws.rs.client.Client;

/**
 * Supports the creation of SymphonyClient implementations.
 *
 * @author Frank Tarsillo
 */
public class SymphonyClientFactory {
    private final static Logger logger = LoggerFactory.getLogger(SymphonyClientFactory.class);


    /**
     * Currently only one SymphonyClient implementation called BASIC
     */
    public enum TYPE {
        BASIC
    }

    /**
     * Generate a new SymphonyClient based on type
     *
     * @param type The type of SymphonyClient.  Currently only BASIC is available.
     * @return A SymphonyClient instance based on type
     */
    public static SymphonyClient getClient(TYPE type) {

        return new SymphonyBasicClient();

    }

    /**
     * Generate a new SymphonyClient and init it based on type
     *
     * @param type               The type of SymphonyClient.  Currently only BASIC is available.
     * @param email              Email address of the BOT
     * @param clientKeyStore     BOT keystore file location
     * @param clientKeyStorePass BOT keystore password
     * @param trustStore         TrustStore file location
     * @param trustStorePass     Truststore password
     * @return A SymphonyClient instance based on type which is already instantiated.
     */
    public static SymphonyClient getClient(TYPE type, String email, String clientKeyStore, String clientKeyStorePass, String trustStore, String trustStorePass) {


        try {

            //Create a basic client instance.
            SymphonyClient symClient = SymphonyClientFactory.getClient(type);

            logger.debug("{} {}", System.getProperty("sessionauth.url"),
                    System.getProperty("keyauth.url"));


            try {
                Client httpClient = CustomHttpClient.getClient(clientKeyStore, clientKeyStorePass, trustStore, trustStorePass);
                symClient.setDefaultHttpClient(httpClient);
            } catch (Exception e) {
                logger.error("Failed to create custom http client", e);
                return null;
            }


            //Init the Symphony authorization client, which requires both the key and session URL's.  In most cases,
            //the same fqdn but different URLs.
            AuthorizationClient authClient = new AuthorizationClient(
                    System.getProperty("sessionauth.url"),
                    System.getProperty("keyauth.url"),
                    symClient.getDefaultHttpClient());


            //Create a SymAuth which holds both key and session tokens.  This will call the external service.
            SymAuth symAuth = authClient.authenticate();


            //With a valid SymAuth we can now init our client.
            symClient.init(
                    symClient.getDefaultHttpClient(),
                    symAuth,
                    email,
                    System.getProperty("symphony.agent.agent.url"),
                    System.getProperty("symphony.agent.pod.url")

            );


            return symClient;

        } catch (AuthorizationException ae) {

            logger.error(ae.getMessage(), ae);
        } catch (InitException e) {
            logger.error("error", e);
        }

        return null;
    }


}
