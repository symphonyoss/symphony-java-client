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

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.exceptions.InitException;
import org.symphonyoss.client.exceptions.NetworkException;
import org.symphonyoss.client.impl.CustomHttpClient;
import org.symphonyoss.client.impl.SymphonyBasicClient;
import org.symphonyoss.symphony.clients.model.ApiVersion;
import org.symphonyoss.symphony.pod.invoker.JSON;

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
        BASIC, V4
    }

    /**
     * Generate a new SymphonyClient based on type
     *
     * @param type The type of SymphonyClient.  Currently only BASIC is available.
     * @return A SymphonyClient instance based on type
     */
    public static SymphonyClient getClient(TYPE type) {

        return type.toString().equals(ApiVersion.V4.toString()) ? new SymphonyBasicClient(ApiVersion.V4) : new SymphonyBasicClient();

    }



    /**
     * Generate a new SymphonyClient and init it based on type
     *
     * @param type       The type of SymphonyClient.  Currently only BASIC is available.
     * @param config SymphonyClientConfig to init
     * @return A SymphonyClient instance based on type which is already instantiated.
     */
    public static SymphonyClient getClient(TYPE type, SymphonyClientConfig config) {


        try {

            //Create a basic client instance.
            SymphonyClient symClient = SymphonyClientFactory.getClient(type);

            logger.debug("{} {}", config.get(SymphonyClientConfigID.SESSIONAUTH_URL),
                    config.get(SymphonyClientConfigID.KEYAUTH_URL));


            try {

                symClient.setDefaultHttpClient(CustomHttpClient.getDefaultHttpClient(config));

            } catch (Exception e) {
                logger.error("Failed to create custom http client", e);
                return null;
            }


            symClient.init(symClient.getDefaultHttpClient(), config);


            return symClient;

        } catch (NetworkException ae) {

            logger.error(ae.getMessage(), ae);
        } catch (InitException e) {
            logger.error("error", e);
        }

        return null;
    }


}
