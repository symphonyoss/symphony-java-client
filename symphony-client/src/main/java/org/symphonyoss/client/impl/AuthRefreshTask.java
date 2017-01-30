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
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 *
 */

package org.symphonyoss.client.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.exceptions.AuthorizationException;
import org.symphonyoss.symphony.clients.AuthorizationClient;

import java.util.TimerTask;

/**
 * Created by frank.tarsillo on 9/19/2016.
 *
 * Task will refresh session tokens when called.
 */
@SuppressWarnings("WeakerAccess")
public class AuthRefreshTask extends TimerTask {

    private final Logger logger = LoggerFactory.getLogger(AuthRefreshTask.class);
    private final SymphonyClient symClient;

    public AuthRefreshTask(SymphonyClient symClient){
        this.symClient = symClient;
    }

    @Override
    public void run() {

        runTask();

    }

    @SuppressWarnings("UnusedReturnValue")
    public SymAuth runTask(){

        SymAuth symAuth = null;
        try {
            //Init the Symphony authorization client, which requires both the key and session URL's.  In most cases,
            //the same fqdn but different URLs.
            AuthorizationClient authClient = new AuthorizationClient(
                    System.getProperty("sessionauth.url"),
                    System.getProperty("keyauth.url"));


            //Set the local keystores that hold the server CA and client certificates
            authClient.setKeystores(
                    System.getProperty("truststore.file"),
                    System.getProperty("truststore.password"),
                    System.getProperty("certs.dir") + System.getProperty("bot.user") + ".p12",
                    System.getProperty("keystore.password"));

            //Create a SymAuth which holds both key and session tokens.  This will call the external service.
             symAuth = authClient.authenticate();

            symClient.setSymAuth(symAuth);
            logger.info("Successfully refreshed SymAuth keys...");

        }catch (AuthorizationException e){
            logger.error("Unable to refresh SymAuth keys...", e);
        }

        return symAuth;

    }


}
