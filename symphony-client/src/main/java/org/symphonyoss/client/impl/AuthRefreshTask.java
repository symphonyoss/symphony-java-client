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
import org.symphonyoss.client.exceptions.NetworkException;
import org.symphonyoss.client.exceptions.SystemException;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.symphony.clients.AgentSystemClient;
import org.symphonyoss.symphony.clients.AgentSystemClientFactory;
import org.symphonyoss.symphony.clients.AuthenticationClient;
import org.symphonyoss.symphony.clients.model.SymAgentHealthCheck;
import org.symphonyoss.symphony.clients.model.SymAgentHealthCheckMBean;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.util.TimerTask;

/**
 * Created by frank.tarsillo on 9/19/2016.
 * <p>
 * Task will refresh session tokens when called.
 */
@SuppressWarnings("WeakerAccess")
public class AuthRefreshTask extends TimerTask {

    private final Logger logger = LoggerFactory.getLogger(AuthRefreshTask.class);
    private final SymphonyClient symClient;

    public AuthRefreshTask(SymphonyClient symClient) {
        this.symClient = symClient;
    }


    @Override
    public void run() {

        runTask();

    }


    @SuppressWarnings("UnusedReturnValue")
    public SymAuth runTask() {

        SymAuth symAuth = null;
        try {

            AuthenticationClient authClient;

            //Init the Symphony authorization client, which requires both the key and session URL's.  In most cases,
            //the same fqdn but different URLs.
            if (symClient.getSymAuth() != null && symClient.getSymAuth().getHttpClient() != null) {

                //Take the stored http client configuration with the pre-loaded keystores.
                authClient = new AuthenticationClient(symClient.getSymAuth().getSessionUrl(), symClient.getSymAuth().getKeyUrl(), symClient.getSymAuth().getHttpClient());

            } else {

                authClient = new AuthenticationClient(
                        symClient.getSymAuth().getSessionUrl(),
                        symClient.getSymAuth().getKeyUrl());


                //Set the local keystores that hold the server CA and client certificates
                authClient.setKeystores(
                        symClient.getSymAuth().getServerTruststore(),
                        symClient.getSymAuth().getServerTruststorePassword(),
                        symClient.getSymAuth().getClientKeystore(),
                        symClient.getSymAuth().getClientKeystorePassword());


            }


            //Create a SymAuth which holds both key and session tokens.  This will call the external service.
             symAuth = authClient.authenticate();

            symClient.getSymAuth().setKeyToken(symAuth.getKeyToken());
            symClient.getSymAuth().setSessionToken(symAuth.getSessionToken());

            logger.info("Successfully refreshed SymAuth tokens...");

            logger.info("Exposing SymAgentHealthCheck as JMX MBean...");
            AgentSystemClient agentSystemClient = AgentSystemClientFactory.getClient(symClient);
            //SymAgentHealthCheck check = agentSystemClient.getAgentHealthCheck();
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName mBeanName = new ObjectName("org.symphonyoss.client:type=HealthCheck");
            mbs.registerMBean(agentSystemClient, mBeanName);
        } catch (NetworkException e) {
            logger.error("Unable to refresh SymAuth keys...", e);
//        } catch (SystemException e) {
//            logger.error("Cannot get SymAgentHealthCheck...", e);
        } catch (MalformedObjectNameException e) {
            logger.error("Cannot expose MBean...", e);
        } catch (NotCompliantMBeanException e) {
            logger.error("Cannot expose MBean...", e);
        } catch (InstanceAlreadyExistsException e) {
            logger.error("Cannot expose MBean...", e);
        } catch (MBeanRegistrationException e) {
            logger.error("Cannot expose MBean...", e);
        }

        return symAuth;

    }


}
