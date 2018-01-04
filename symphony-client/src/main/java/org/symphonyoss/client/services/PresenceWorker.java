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
import org.symphonyoss.client.exceptions.PresenceException;
import org.symphonyoss.symphony.agent.model.Datafeed;
import org.symphonyoss.symphony.clients.model.ApiVersion;
import org.symphonyoss.symphony.clients.model.SymPresence;
import org.symphonyoss.symphony.clients.model.SymPresenceFeed;
import org.symphonyoss.symphony.clients.model.SymUserPresence;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * This thread will long-poll for Symphony base messages based on a specific BOT user identified through the
 * {@link SymphonyClient} and publish on the provided {@link DataFeedListener} interface.
 * <p>
 *
 * @author Frank Tarsillo
 */
class PresenceWorker implements Runnable {

    private final PresenceFeedListener presenceFeedListener;
    private final SymphonyClient symClient;
    private final Logger logger = LoggerFactory.getLogger(PresenceWorker.class);
    private SymPresenceFeed symPresenceFeed;
    private boolean shutdown;


    /**
     * Constructor
     *
     * @param symClient        Identifies the BOT user and exposes client APIs
     * @param  presenceFeedListener Callback listener to publish presence events.
     */
    public PresenceWorker(SymphonyClient symClient, PresenceFeedListener presenceFeedListener) {
        this.symClient = symClient;
        this.presenceFeedListener = presenceFeedListener;


    }

    @Override
    public void run() {



        while (!shutdown) {

            //Make sure its active
            initDatafeed();

            //Poll it
            readPresenceFeed();


        }

    }


    /**
     * Create or restore an instance of the {@link Datafeed}
     */
    private void initDatafeed() {


        while (symPresenceFeed == null) {
            try {
                logger.info("Creating datafeed with pod...");

                symPresenceFeed = symClient.getPresenceClient().createPresenceFeed();

                break;
            } catch (Exception e) {


                logger.error("Failed to create presence feed with pod, please check connection..", e);
                symPresenceFeed = null;

                //Can use properties to override default time wait
                try {

                    TimeUnit.SECONDS.sleep(
                            Long.valueOf(System.getProperty(Constants.PRESENCEFEED_RECOVERY_WAIT_TIME, "5"))
                    );
                } catch (InterruptedException e1) {
                    logger.error("Interrupt.. ", e1);
                    Thread.currentThread().interrupt();
                }

            }

        }


    }

    /**
     *
     */
    private void readPresenceFeed() {

        try {


            List<SymPresence> symPresences = symClient.getPresenceClient().getPresenceFeedUpdates(symPresenceFeed);

            if (symPresences != null) {

                symPresences.forEach(presenceFeedListener::onEvent);

            }


        } catch (Exception e) {
            logger.error("Failed to create read presence feed from pod, please check connection..resetting.", e);

            //Trying to remove it..
            try {
                symClient.getPresenceClient().removePresenceFeed(symPresenceFeed);
            } catch (PresenceException e1) {
               logger.error("Failed to remove presence feed handler: {}",  symPresenceFeed.getId());
            }
            symPresenceFeed = null;

            //Can use properties to override default time wait
            try {

                TimeUnit.SECONDS.sleep(
                        Long.valueOf(System.getProperty(Constants.PRESENCEFEED_RECOVERY_WAIT_TIME, "5"))
                );
            } catch (InterruptedException e1) {
                logger.error("Interrupt.. ", e1);
                Thread.currentThread().interrupt();
            }



        }

    }

    public void shutdown() {
        shutdown = true;
    }


}



