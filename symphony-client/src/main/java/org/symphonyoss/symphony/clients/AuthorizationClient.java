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

import javax.ws.rs.client.Client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.exceptions.AuthorizationException;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.symphony.authenticator.api.AuthenticationApi;
import org.symphonyoss.symphony.authenticator.invoker.ApiException;
import org.symphonyoss.symphony.authenticator.invoker.Configuration;
import org.symphonyoss.symphony.authenticator.model.Token;

/**
 * Created by Frank Tarsillo on 5/15/2016.
 */
/* TODO: This class is mis-named, this is about authentication not 
 * authorisation, although I accept that HTTP 401 is mis-named in
 * the same way.
 * 
 *  I think we should re-name it to AuthenticationClient and make
 *  AuthorizationClient a sub-class which adds nothing but is
 *  @Deprecated. This means that existing code will still work 
 *  but callers will get a deprecation warning.
 *  
 *  AuthorizationException is also mis-named but should be re-factored
 *  as part of a re-work of exception handling.
 *  
 *  -Bruce
 */
public class AuthorizationClient {

    private SymAuth symAuth;
    private final String sessionUrl;
    private final String keyUrl;
    private boolean loginStatus = false;
    private final Logger logger = LoggerFactory.getLogger(AuthorizationClient.class);
    private Client httpClient= null;
    private Client keyManagerHttpClient;


    public AuthorizationClient(String sessionUrl, String keyUrl){


        this.sessionUrl = sessionUrl;
        this.keyUrl = keyUrl;



    }

    public AuthorizationClient(String sessionUrl, String keyUrl, Client httpClient){
        this.sessionUrl = sessionUrl;
        this.keyUrl = keyUrl;

        this.httpClient = httpClient;


    }

    public void setKeyManagerHttpClient(Client client) {
        keyManagerHttpClient = client;
    }

    public SymAuth authenticate() throws AuthorizationException {
	String currentUrl = "UNKNOWN";
	
        try {

            if(sessionUrl == null || keyUrl == null)
                throw new NullPointerException("Session URL or Keystore URL is null..");




            symAuth = new SymAuth();
            org.symphonyoss.symphony.authenticator.invoker.ApiClient authenticatorClient = Configuration.getDefaultApiClient();


            //Need this for refresh
            symAuth.setKeyUrl(keyUrl);
            symAuth.setSessionUrl(sessionUrl);


            if(httpClient != null) {
                authenticatorClient.setHttpClient(httpClient);
                symAuth.setHttpClient(httpClient);

            }else{

                //Lets copy the pki info..
                symAuth.setServerTruststore(System.getProperty("javax.net.ssl.trustStore"));
                symAuth.setServerTruststorePassword(System.getProperty("javax.net.ssl.trustStorePassword"));
                symAuth.setClientKeystore(System.getProperty("javax.net.ssl.keyStore"));
                symAuth.setClientKeystorePassword(System.getProperty("javax.net.ssl.keyStorePassword"));

            }


            // Configure the authenticator connection
            currentUrl = sessionUrl;
            authenticatorClient.setBasePath(sessionUrl);
            
            // Get the authentication API
            AuthenticationApi authenticationApi = new AuthenticationApi(authenticatorClient);


            symAuth.setSessionToken(authenticationApi.v1AuthenticatePost());
            logger.debug("SessionToken: {} : {}", symAuth.getSessionToken().getName(), symAuth.getSessionToken().getToken());


            // Configure the keyManager path
            currentUrl = keyUrl;
            authenticatorClient.setBasePath(keyUrl);
            if (keyManagerHttpClient != null) {
                authenticatorClient.setHttpClient(keyManagerHttpClient);
            }


            symAuth.setKeyToken(authenticationApi.v1AuthenticatePost());
            logger.debug("KeyToken: {} : {}", symAuth.getKeyToken().getName(), symAuth.getKeyToken().getToken());

        } catch (ApiException e) {

//            logger.error("Symphony API error",e);
            throw new AuthorizationException("Please check certificates, tokens and paths: " +
            "\nServer TrustStore File: " +  System.getProperty("javax.net.ssl.trustStore") +
            "\nClient Keystore File: " + System.getProperty("javax.net.ssl.keyStore"),
            currentUrl, e.getCode(),
            e);

        }

        loginStatus = true;
        return symAuth;

    }
    

    public void setKeystores(String serverTruststore, String truststorePass, String clientKeystore, String keystorePass) {


        System.setProperty("javax.net.ssl.trustStore", serverTruststore);
        System.setProperty("javax.net.ssl.trustStorePassword", truststorePass);
        System.setProperty("javax.net.ssl.keyStore", clientKeystore);
        System.setProperty("javax.net.ssl.keyStorePassword", keystorePass);
        System.setProperty("javax.net.ssl.keyStoreType", "pkcs12");





    }

    public boolean isLoggedIn(){
        return loginStatus;
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

    @SuppressWarnings("unused")
    public void setSessionToken(Token sessionToken) {
        symAuth.setSessionToken(sessionToken);
    }
}
