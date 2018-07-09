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

package org.symphonyoss.client.model;

import org.symphonyoss.symphony.authenticator.model.OboAuthResponse;
import org.symphonyoss.symphony.authenticator.model.Token;


import javax.ws.rs.client.Client;

/**
 * Simple abstraction object to hold session and key tokens retrieved from authorization process.
 * <p>
 * Created by Frank Tarsillo on 5/15/2016.
 */
public class SymAuth {
    private Token sessionToken;
    private Token keyToken;
    private String email;
    private String sessionUrl;
    private String keyUrl;
    private String serverTruststore;
    private String clientKeystore;
    private String serverTruststorePassword;
    private String clientKeystorePassword;
    private Client httpClient;
    private Client httpClientForSessionToken;
    private Client httpClientForKeyToken;


    public String getServerTruststore() {
        return serverTruststore;
    }

    public void setServerTruststore(String serverTruststore) {
        this.serverTruststore = serverTruststore;
    }

    public String getClientKeystore() {
        return clientKeystore;
    }

    public void setClientKeystore(String clientKeystore) {
        this.clientKeystore = clientKeystore;
    }

    public String getServerTruststorePassword() {
        return serverTruststorePassword;
    }

    public void setServerTruststorePassword(String serverTruststorePassword) {
        this.serverTruststorePassword = serverTruststorePassword;
    }

    public String getClientKeystorePassword() {
        return clientKeystorePassword;
    }

    public void setClientKeystorePassword(String clientKeystorePassword) {
        this.clientKeystorePassword = clientKeystorePassword;
    }

    public Client getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(Client httpClient) {
        this.httpClient = httpClient;
    }


    public Token getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(Token sessionToken) {
        this.sessionToken = sessionToken;
    }

    public Token getKeyToken() {
        return keyToken;
    }

    public void setKeyToken(Token keyToken) {
        this.keyToken = keyToken;
    }


    @SuppressWarnings("unused")
    public String getEmail() {
        return email;
    }

    @SuppressWarnings("unused")
    public void setEmail(String email) {
        this.email = email;
    }

    public String getSessionUrl() {
        return sessionUrl;
    }

    public void setSessionUrl(String sessionUrl) {
        this.sessionUrl = sessionUrl;
    }

    public String getKeyUrl() {
        return keyUrl;
    }

    public void setKeyUrl(String keyUrl) {
        this.keyUrl = keyUrl;
    }

    public Client getHttpClientForSessionToken() {
        return httpClientForSessionToken;
    }

    public void setHttpClientForSessionToken(Client httpClientForSessionToken) {
        this.httpClientForSessionToken = httpClientForSessionToken;
    }

    public Client getHttpClientForKeyToken() {
        return httpClientForKeyToken;
    }

    public void setHttpClientForKeyToken(Client httpClientForKeyToken) {
        this.httpClientForKeyToken = httpClientForKeyToken;
    }

    public static SymAuth fromOboAuth(OboAuthResponse oboAuthResponse) {

        if(oboAuthResponse==null)
            return null;

        SymAuth symAuth = new SymAuth();
        Token token = new Token();
        token.setName("OboSessionToken");
        token.setToken(oboAuthResponse.getSessionToken());
        symAuth.setSessionToken(token);
        token = new Token();
        token.setName("KeyToken");
        token.setToken(null);
        symAuth.setKeyToken(token);
        return symAuth;

    }


}
