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

package org.symphonyoss.symphony.clients;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.symphony.authenticator.api.AuthenticationApi;
import org.symphonyoss.symphony.authenticator.invoker.Configuration;
import org.symphonyoss.symphony.authenticator.model.Token;
import org.symphonyoss.client.model.SymAuth;

/**
 * Created by Frank Tarsillo on 5/15/2016.
 */
public class AuthorizationClient {

    private SymAuth symAuth;
    private String authUrl;
    private String keyUrl;
    private boolean LOGIN_STATUS = false;
    private final String NOT_LOGGED_IN_MESSAGE = "Currently not logged into Agent, please check certificates and tokens.";
    private Logger logger = LoggerFactory.getLogger(AuthorizationClient.class);



    public AuthorizationClient(String authUrl, String keyUrl){


        this.authUrl = authUrl;
        this.keyUrl = keyUrl;


    }

    public SymAuth authenticate() throws Exception{

        try {

            symAuth = new SymAuth();
            org.symphonyoss.symphony.authenticator.invoker.ApiClient authenticatorClient = Configuration.getDefaultApiClient();

            // Configure the authenticator connection
            authenticatorClient.setBasePath(authUrl);

            // Get the authentication API
            AuthenticationApi authenticationApi = new AuthenticationApi(authenticatorClient);


            symAuth.setSessionToken(authenticationApi.v1AuthenticatePost());
            logger.debug("SessionToken: {} : {}", symAuth.getSessionToken().getName(), symAuth.getSessionToken().getToken());


            // Configure the keyManager path
            authenticatorClient.setBasePath(keyUrl);


            symAuth.setKeyToken(authenticationApi.v1AuthenticatePost());
            logger.debug("KeyToken: {} : {}", symAuth.getKeyToken().getName(), symAuth.getKeyToken().getToken());




        } catch (Exception e) {

            logger.error("Login failure: {}", e.getCause(), e);
            throw new Exception("Please check certificates, tokens and paths..");

        }

        LOGIN_STATUS = true;
        return symAuth;

    }



    public void setKeystores(String serverTrustore, String truststorePass, String clientKeystore, String keystorePass) {


        System.setProperty("javax.net.ssl.trustStore", serverTrustore);
        System.setProperty("javax.net.ssl.trustStorePassword", truststorePass);
        System.setProperty("javax.net.ssl.keyStore", clientKeystore);
        System.setProperty("javax.net.ssl.keyStorePassword", keystorePass);
        System.setProperty("javax.net.ssl.keyStoreType", "pkcs12");


    }

    public boolean isLoggedIn(){
        return LOGIN_STATUS;
    }

    public Token getKeyToken() {
        return symAuth.getKeyToken();
    }

    public void setKeyToken(Token keyToken) {
        symAuth.setKeyToken(keyToken);
    }

    public Token getSessionToken() {
        return symAuth.getSessionToken();
    }

    public void setSessionToken(Token sessionToken) {
        symAuth.setSessionToken(sessionToken);
    }
}
