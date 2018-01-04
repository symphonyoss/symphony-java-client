///*
// *
// *
// * Copyright 2016 The Symphony Software Foundation
// *
// * Licensed to The Symphony Software Foundation (SSF) under one
// * or more contributor license agreements.  See the NOTICE file
// * distributed with this work for additional information
// * regarding copyright ownership.  The ASF licenses this file
// * to you under the Apache License, Version 2.0 (the
// * "License"); you may not use this file except in compliance
// * with the License.  You may obtain a copy of the License at
// *
// *   http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing,
// * software distributed under the License is distributed on an
// * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// * KIND, either express or implied.  See the License for the
// * specific language governing permissions and limitations
// * under the License.
// *
// */
//package firehose;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.symphonyoss.client.SymphonyClient;
//import org.symphonyoss.client.SymphonyClientConfig;
//import org.symphonyoss.client.SymphonyClientFactory;
//import org.symphonyoss.client.events.SymEvent;
//import org.symphonyoss.client.exceptions.DataFeedException;
//import org.symphonyoss.client.services.FirehoseListener;
//import org.symphonyoss.client.services.FirehoseService;
//
//
///**
// * Firehose example requires Agent Server 1.49 or higher
// * <p>
// * Bot user requires elevated privs
// * <p>
// * <p>
// * <p>
// * <p>
// * REQUIRED VM Arguments or System Properties:
// * <p>
// * -Dtruststore.file=
// * -Dtruststore.password=password
// * -Dsessionauth.url=https://(hostname)/sessionauth
// * -Dkeyauth.url=https://(hostname)/keyauth
// * -Duser.call.home=frank.tarsillo@markit.com
// * -Duser.cert.password=password
// * -Duser.cert.file=bot.user2.p12
// * -Duser.email=bot.user2@domain.com
// * -Dpod.url=https://(pod host)/pod
// * -Dagent.url=https://(agent server host)/agent
// * -Dreceiver.email=bot.user2@markit.com or bot user email
// *
// * @author Frank Tarsillo
// */
////NOSONAR
//public class FirehoseExample implements FirehoseListener {
//
//
//    private final Logger logger = LoggerFactory.getLogger(FirehoseExample.class);
//
//    private SymphonyClient symClient;
//
//    public FirehoseExample() {
//
//
//        init();
//
//
//    }
//
//    public static void main(String[] args) {
//
//        new FirehoseExample();
//
//    }
//
//    public void init() {
//
//
//        try {
//
//            SymphonyClientConfig symphonyClientConfig = new SymphonyClientConfig(true);
//
//
//
//            //Create an initialized client
//            symClient = SymphonyClientFactory.getClient(
//                    SymphonyClientFactory.TYPE.V4, symphonyClientConfig);
//
//            FirehoseService firehoseService = new FirehoseService(symClient);
//            firehoseService.addFirehoseListener(this);
//            firehoseService.init();
//
//
//        } catch (DataFeedException e) {
//            logger.error("error", e);
//        }
//
//    }
//
//
//    //Chat sessions callback method.
//    //@Override
//    public void onEvent(SymEvent symEvent) {
//
//        logger.info("{}", symEvent.toString());
//
//
//    }
//
//
//}
