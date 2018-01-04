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

package org.symphonyoss.client.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.events.SymEvent;
import org.symphonyoss.client.exceptions.DataFeedException;
import org.symphonyoss.client.exceptions.SystemException;
import org.symphonyoss.symphony.clients.AgentSystemClient;
import org.symphonyoss.symphony.clients.AgentSystemClientFactory;
import org.symphonyoss.symphony.clients.FirehoseClientFactory;
import org.symphonyoss.symphony.clients.model.RestApiVersion;
import org.symphonyoss.symphony.clients.model.SymAgentHealthCheck;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Firehose service
 *
 * @author Frank Tarsillo on 5/15/2016.
 */
@SuppressWarnings("WeakerAccess")
public class FirehoseService implements FirehoseListener {

    private final SymphonyClient symClient;
    @SuppressWarnings("unused")
    private org.symphonyoss.symphony.agent.invoker.ApiClient agentClient;
    private final Logger logger = LoggerFactory.getLogger(FirehoseService.class);
    private final Set<FirehoseListener> firehoseListeners = ConcurrentHashMap.newKeySet();

    private FirehoseWorker firehoseWorker;


    /**
     * Constructor
     *
     * @param symClient Identifies the BOT user and exposes client APIs
     */
    public FirehoseService(SymphonyClient symClient) {

        this.symClient = symClient;


    }


    public void init() throws DataFeedException {

        RestApiVersion restApiVersion = RestApiVersion.v1_49_0;


        AgentSystemClient agentSystemClient = AgentSystemClientFactory.getClient(symClient);


        try {
            SymAgentHealthCheck symAgentHealthCheck = agentSystemClient.getAgentHealthCheck();

            if (!restApiVersion.isCompatible(symAgentHealthCheck.getAgentVersion()))
            throw new DataFeedException("Agent Server doesn't support Firehose.  It must be 1.49 or higher. Version detected=" + symAgentHealthCheck.getAgentVersion(), 0, null);


        } catch (SystemException e) {
            throw new DataFeedException("Failed to obtain agent server version", 0, e.getCause());
        }


        //Lets startup the worker thread to listen for raw datafeed messages
        firehoseWorker = new FirehoseWorker(FirehoseClientFactory.getClient(symClient), this);

        new Thread(firehoseWorker, "FirehoseWorker: " + symClient.getName()).start();

    }


    @Override
    public void onEvent(SymEvent symEvent) {


        logger.debug("{} event type received...", symEvent.getType());


        //Publish all messages to registered Message Listeners...
        for (FirehoseListener firehoseListener : firehoseListeners) {
            firehoseListener.onEvent(symEvent);
        }


    }


    /**
     * Add {@link FirehoseListener} to receive for all new events
     *
     * @param firehoseListener listener that will be notified of events
     */
    public void addFirehoseListener(FirehoseListener firehoseListener) {

        firehoseListeners.add(firehoseListener);

    }

    /**
     * Remove a registered {@link FirehoseListener} t
     *
     * @param firehoseListener that will removed from service
     * @return True if listener is removed
     */
    public boolean removeFirehoseListener(FirehoseListener firehoseListener) {

        return firehoseListeners.remove(firehoseListener);

    }


    /**
     * Shutdown the underlying threads and workers.
     */
    public void shutdown() {

        if (firehoseWorker != null) {
            firehoseWorker.shutdown();
            firehoseWorker = null;
        }


    }

}
