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

package org.symphonyoss.symphony.clients.model;

import org.symphonyoss.symphony.authenticator.model.ExtensionAppTokens;

/**
 * @author Frank Tarsillo on 8/7/17.
 */
public class SymExtensionAppAuth {


    private String appId = null;


    private String appToken = null;


    private String symphonyToken = null;


    private Long expireAt = null;


    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppToken() {
        return appToken;
    }

    public void setAppToken(String appToken) {
        this.appToken = appToken;
    }

    public String getSymphonyToken() {
        return symphonyToken;
    }

    public void setSymphonyToken(String symphonyToken) {
        this.symphonyToken = symphonyToken;
    }

    public Long getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(Long expireAt) {
        this.expireAt = expireAt;
    }

    public static SymExtensionAppAuth toSymExtensionAppAuth(ExtensionAppTokens extensionAppTokens){

        if(extensionAppTokens == null)
            return null;

        SymExtensionAppAuth symExtensionAppAuth = new SymExtensionAppAuth();

        symExtensionAppAuth.setAppId(extensionAppTokens.getAppId());
        symExtensionAppAuth.setAppToken(extensionAppTokens.getAppToken());
        symExtensionAppAuth.setSymphonyToken(extensionAppTokens.getSymphonyToken());
        symExtensionAppAuth.setExpireAt(extensionAppTokens.getExpireAt());

        return symExtensionAppAuth;

    }
}
