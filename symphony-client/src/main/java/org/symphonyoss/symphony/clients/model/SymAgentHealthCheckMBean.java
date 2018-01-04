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

/**
 * @author Frank Tarsillo on 10/15/17.
 */
public interface SymAgentHealthCheckMBean {

    public Boolean getPodConnectivity();

    public void setPodConnectivity(Boolean podConnectivity);

    public String getPodConnectivityError();

    public void setPodConnectivityError(String podConnectivityError);

    public Boolean getKeyManagerConnectivity();

    public void setKeyManagerConnectivity(Boolean keyManagerConnectivity);

    public String getKeyManagerConnectivityError();

    public void setKeyManagerConnectivityError(String keyManagerConnectivityError);

    public Boolean getEncryptDecryptSuccess();

    public void setEncryptDecryptSuccess(Boolean encryptDecryptSuccess);

    public String getEncryptDecryptError();

    public void setEncryptDecryptError(String encryptDecryptError);

    public String getPodVersion();

    public void setPodVersion(String podVersion);

    public String getAgentVersion();

    public void setAgentVersion(String agentVersion);
}
