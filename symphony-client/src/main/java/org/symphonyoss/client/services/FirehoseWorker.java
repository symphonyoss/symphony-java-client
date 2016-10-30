/*
 *
 *  *
 *  * Copyright 2016 The Symphony Software Foundation
 *  *
 *  * Licensed to The Symphony Software Foundation (SSF) under one
 *  * or more contributor license agreements.  See the NOTICE file
 *  * distributed with this work for additional information
 *  * regarding copyright ownership.  The ASF licenses this file
 *  * to you under the Apache License, Version 2.0 (the
 *  * "License"); you may not use this file except in compliance
 *  * with the License.  You may obtain a copy of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *
 */

package org.symphonyoss.client.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.exceptions.FirehoseException;
import org.symphonyoss.symphony.agent.model.Firehose;
import org.symphonyoss.symphony.agent.model.V2BaseMessage;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Frank Tarsillo on 5/21/2016.
 */
class FirehoseWorker implements Runnable {

    private final DataFeedListener dataFeedListener;
    private final SymphonyClient symClient;
    private final Logger logger = LoggerFactory.getLogger(FirehoseWorker.class);
    private Firehose firhose;
    private boolean shutdown;


    /**
     * Worker thread reading and publishing messages for firehose
     * @param symClient
     * @param dataFeedListener
     */
    public FirehoseWorker(SymphonyClient symClient, DataFeedListener dataFeedListener) {
        this.symClient = symClient;
        this.dataFeedListener = dataFeedListener;


    }

    public void run() {


        //noinspection InfiniteLoopStatement
        while (!shutdown) {

                initDatafeed();

                readDatafeed();



        }

    }


    private void initDatafeed(){


        while(firhose == null) {
            try {
                logger.info("[Experimental] Creating firehose with pod...");

                firhose = symClient.getFirehoseClient().createFirehose();

                break;
            } catch (FirehoseException e) {

                logger.error("Failed to create firehose with pod, please check connection..", e);
                firhose = null;
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e1) {
                    logger.error("Interrupt.. ", e1);
                }
                continue;
            }

        }


    }

    private void readDatafeed(){

        try {
            List<V2BaseMessage> messageList = symClient.getFirehoseClient().getMessagesFromFirehose(firhose);

            if(messageList != null) {

                logger.debug("Received {} messages..", messageList.size());

                messageList.forEach(dataFeedListener::onMessage);
            }

        } catch (FirehoseException e) {
            logger.error("Failed to create read firehose from pod, please check connection..resetting.", e);
            firhose = null;

        }

    }

    public void shutdown(){
        shutdown = true;
    }


}



