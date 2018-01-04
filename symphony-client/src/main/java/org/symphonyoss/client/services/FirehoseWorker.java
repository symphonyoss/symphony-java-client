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
import org.symphonyoss.client.common.Constants;
import org.symphonyoss.client.events.SymEvent;
import org.symphonyoss.symphony.clients.FirehoseClient;
import org.symphonyoss.symphony.clients.model.ApiVersion;
import org.symphonyoss.symphony.clients.model.SymFirehose;
import org.symphonyoss.symphony.clients.model.SymFirehoseRequest;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * This thread will long-poll for all symphony messages related to the POD itself.
 * The bot service user should have elevated privs.
 *
 * <p>
 *
 * @author Frank Tarsillo
 */
class FirehoseWorker implements Runnable {

    private final FirehoseListener firehoseListener;

    private final FirehoseClient firehoseClient;
    private final Logger logger = LoggerFactory.getLogger(FirehoseWorker.class);
    private SymFirehose symFirehose;
    private boolean shutdown;


    /**
     * Constructor
     *
     * @param firehoseClient  Firehose client
     * @param firehoseListener Callback listener to publish new base messages on.
     */
    public FirehoseWorker(FirehoseClient firehoseClient, FirehoseListener firehoseListener) {
        this.firehoseClient = firehoseClient;
        this.firehoseListener = firehoseListener;


    }

    @Override
    public void run() {


        //noinspection InfiniteLoopStatement
        while (!shutdown) {

                //Make sure its active
                initFirehose();

                //Poll it
                readFirehose();


        }

    }


    /**
     * Create or restore an instance of the {@link org.symphonyoss.symphony.clients.model.SymFirehose}
     */
    private void initFirehose() {


        while (symFirehose == null) {
            try {
                logger.info("Creating symFirehose with pod...");

                symFirehose = firehoseClient.createFirehose();

                break;
            } catch (Exception e) {

                logger.error("Failed to create firehose with agent server, please check connection..", e);
                symFirehose = null;

                //Can use properties to override default time wait
                try {

                    TimeUnit.SECONDS.sleep(
                            Long.valueOf(System.getProperty(Constants.DATAFEED_RECOVERY_WAIT_TIME, "5"))
                    );
                } catch (InterruptedException e1) {
                    logger.error("Interrupt.. ", e1);
                    Thread.currentThread().interrupt();
                }

            }

        }


    }

    /**
     * Reads in raw messages from {@link org.symphonyoss.symphony.clients.DataFeedClient} and publishes out through
     * {@link DataFeedListener}
     */
    private void readFirehose() {

        try {

            SymFirehoseRequest symFirehoseRequest = new SymFirehoseRequest();
            symFirehoseRequest.setMaxMsgs(Integer.valueOf(System.getProperty(Constants.DATAFEED_MAX_MESSAGES,"100")));
            symFirehoseRequest.setTimeout(Integer.valueOf(System.getProperty(Constants.DATAFEED_WAIT_TIME,"5000")));


            List<SymEvent> symEvents = firehoseClient.getEventsFromFirehose(symFirehose,symFirehoseRequest);

            if (symEvents != null) {

                symEvents.forEach(firehoseListener::onEvent);

            }


        } catch (Exception e) {
            logger.error("Failed to create read firehose from pod, please check connection..resetting.", e);
            symFirehose = null;



        }

    }

    public void shutdown() {
        shutdown = true;
    }


}



