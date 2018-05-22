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


import org.symphonyoss.symphony.agent.model.V2HealthCheckResponse;

/**
 * @author Frank Tarsillo on 10/15/17.
 */
public class SymAgentHealthCheck {

    @Override
    public String toString() {
        return  "OK? "+this.isUp()+"\n"+
                "podConnectivity: "+this.podConnectivity+"\n"+
                "podConnectivityError: "+this.podConnectivityError+"\n"+
                "keyManagerConnectivity: "+this.keyManagerConnectivity+"\n"+
                "keyManagerConnectivityError: "+this.keyManagerConnectivityError+"\n"+
                "encryptDecryptSuccess: "+this.encryptDecryptSuccess+"\n"+
                "encryptDecryptError: "+this.encryptDecryptError+"\n"+
                "podVersion: "+this.podVersion+"\n"+
                "agentVersion: "+this.agentVersion;
    }

    public Boolean isUp() {
        return  this.podConnectivity &&
                this.keyManagerConnectivity &&
                this.encryptDecryptSuccess &&
                !this.podVersion.isEmpty() &&
                !this.agentVersion.isEmpty();
    }

    private Boolean podConnectivity = null;


    private String podConnectivityError = null;


    private Boolean keyManagerConnectivity = null;


    private String keyManagerConnectivityError = null;


    private Boolean encryptDecryptSuccess = null;


    private String encryptDecryptError = null;


    private String podVersion = null;


    private String agentVersion = null;


    public Boolean getPodConnectivity() {
        return podConnectivity;
    }

    public void setPodConnectivity(Boolean podConnectivity) {
        this.podConnectivity = podConnectivity;
    }

    public String getPodConnectivityError() {
        return podConnectivityError;
    }

    public void setPodConnectivityError(String podConnectivityError) {
        this.podConnectivityError = podConnectivityError;
    }

    public Boolean getKeyManagerConnectivity() {
        return keyManagerConnectivity;
    }

    public void setKeyManagerConnectivity(Boolean keyManagerConnectivity) {
        this.keyManagerConnectivity = keyManagerConnectivity;
    }

    public String getKeyManagerConnectivityError() {
        return keyManagerConnectivityError;
    }

    public void setKeyManagerConnectivityError(String keyManagerConnectivityError) {
        this.keyManagerConnectivityError = keyManagerConnectivityError;
    }

    public Boolean getEncryptDecryptSuccess() {
        return encryptDecryptSuccess;
    }

    public void setEncryptDecryptSuccess(Boolean encryptDecryptSuccess) {
        this.encryptDecryptSuccess = encryptDecryptSuccess;
    }

    public String getEncryptDecryptError() {
        return encryptDecryptError;
    }

    public void setEncryptDecryptError(String encryptDecryptError) {
        this.encryptDecryptError = encryptDecryptError;
    }

    public String getPodVersion() {
        return podVersion;
    }

    public void setPodVersion(String podVersion) {
        this.podVersion = podVersion;
    }

    public String getAgentVersion() {
        return agentVersion;
    }

    public void setAgentVersion(String agentVersion) {
        this.agentVersion = agentVersion;
    }


    public static SymAgentHealthCheck toSymAgentHealthCheck(V2HealthCheckResponse v2HealthCheckResponse) {

        if(v2HealthCheckResponse==null)
            return null;

        SymAgentHealthCheck symAgentHealthCheck = new SymAgentHealthCheck();

        symAgentHealthCheck.setAgentVersion(v2HealthCheckResponse.getAgentVersion());
        symAgentHealthCheck.setEncryptDecryptError(v2HealthCheckResponse.getEncryptDecryptError());
        symAgentHealthCheck.setEncryptDecryptSuccess(v2HealthCheckResponse.isEncryptDecryptSuccess());
        symAgentHealthCheck.setKeyManagerConnectivity(v2HealthCheckResponse.isKeyManagerConnectivity());
        symAgentHealthCheck.setKeyManagerConnectivityError(v2HealthCheckResponse.getKeyManagerConnectivityError());
        symAgentHealthCheck.setPodConnectivity(v2HealthCheckResponse.isPodConnectivity());
        symAgentHealthCheck.setPodConnectivityError(v2HealthCheckResponse.getPodConnectivityError());
        symAgentHealthCheck.setPodVersion(v2HealthCheckResponse.getPodVersion());

        return symAgentHealthCheck;
    }
}
