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

package org.symphonyoss.client.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.common.Constants;
import org.symphonyoss.exceptions.DataFeedException;
import org.symphonyoss.symphony.agent.model.Datafeed;
import org.symphonyoss.symphony.agent.model.V2BaseMessage;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * This thread will long-poll for Symphony base messages based on a specific BOT user identified through the
 * {@link SymphonyClient} and publish on the provided {@link DataFeedListener} interface.
 * <p>
 *
 * @author Frank Tarsillo
 */
class MessageFeedWorker implements Runnable {

    private final DataFeedListener dataFeedListener;
    private final SymphonyClient symClient;
    private final Logger logger = LoggerFactory.getLogger(MessageFeedWorker.class);
    private Datafeed datafeed;
    private boolean shutdown;


    /**
     * Constructor
     *
     * @param symClient        Identifies the BOT user and exposes client APIs
     * @param dataFeedListener Callback listener to publish new base messages on.
     */
    public MessageFeedWorker(SymphonyClient symClient, DataFeedListener dataFeedListener) {
        this.symClient = symClient;
        this.dataFeedListener = dataFeedListener;


    }

    public void run() {


        //noinspection InfiniteLoopStatement
        while (!shutdown) {

            //Make sure its active
            initDatafeed();

            //Poll it
            readDatafeed();


        }

    }


    /**
     * Create or restore an instance of the {@link Datafeed}
     */
    private void initDatafeed() {


        while (datafeed == null) {
            try {
                logger.info("Creating datafeed with pod...");

                datafeed = symClient.getDataFeedClient().createDatafeed();

                break;
            } catch (DataFeedException e) {

                logger.error("Failed to create datafeed with pod, please check connection..", e);
                datafeed = null;

                //Can use properties to override default time wait
                try {

                    TimeUnit.SECONDS.sleep(
                            Long.valueOf(System.getProperty(Constants.DATAFEED_RECOVERY_WAIT_TIME, "5"))
                    );
                } catch (InterruptedException e1) {
                    logger.error("Interrupt.. ", e1);
                }

            }

        }


    }

    /**
     * Reads in raw messages from {@link org.symphonyoss.symphony.clients.DataFeedClient} and publishes out through
     * {@link DataFeedListener}
     */
    private void readDatafeed() {

        try {
            List<V2BaseMessage> messageList = symClient.getDataFeedClient().getMessagesFromDatafeed(datafeed);

            if (messageList != null) {

                logger.debug("Received {} messages..", messageList.size());

                messageList.forEach(dataFeedListener::onMessage);
            }

        } catch (DataFeedException e) {
            logger.error("Failed to create read datafeed from pod, please check connection..resetting.", e);
            datafeed = null;

        }

    }

    public void shutdown() {
        shutdown = true;
    }


}



