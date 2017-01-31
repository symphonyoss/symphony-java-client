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

import org.symphonyoss.symphony.authenticator.model.Token;

/**
 * Simple abstraction object to hold session and key tokens retrieved from authorization process.
 *
 * Created by Frank Tarsillo on 5/15/2016.
 */
public class SymAuth {
    private Token sessionToken;
    private Token keyToken;
    private String serverTruststore;
    private String clientTruststore;
    private String email;

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
    public String getServerTruststore() {
        return serverTruststore;
    }

    @SuppressWarnings("unused")
    public void setServerTruststore(String serverTruststore) {
        this.serverTruststore = serverTruststore;
    }

    @SuppressWarnings("unused")
    public String getClientTruststore() {
        return clientTruststore;
    }

    @SuppressWarnings("unused")
    public void setClientTruststore(String clientTruststore) {
        this.clientTruststore = clientTruststore;
    }

    @SuppressWarnings("unused")
    public String getEmail() {
        return email;
    }

    @SuppressWarnings("unused")
    public void setEmail(String email) {
        this.email = email;
    }
}
