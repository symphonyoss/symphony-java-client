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

package org.symphonyoss.client;

import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.services.SymUserCache;
import org.symphonyoss.symphony.clients.model.SymUser;

import java.util.concurrent.TimeUnit;

/**
 * This test will simulate and verify cache performance for user requests.
 *
 * @author Frank Tarsillo
 */
public class SymUserCacheIT {

    private static SymphonyClient sjcTestClient;
    private static final Logger logger = LoggerFactory.getLogger(SymUserCacheIT.class);


    public final static String MP_USER_EMAIL = "frank.tarsillo@ihsmarkit.com";

    private static boolean responded;

    private final String botEmail = System.getProperty("bot.user.email", "sjc.testbot");

    @BeforeClass
    public static void setupBeforeClass() throws Exception {


        try {


            sjcTestClient = SymphonyClientFactory.getClient(
                    SymphonyClientFactory.TYPE.BASIC, System.getProperty("sender.user.email", "sjc.testclient"),
                    System.getProperty("sender.user.cert.file"),
                    System.getProperty("sender.user.cert.password"),
                    System.getProperty("truststore.file"),
                    System.getProperty("truststore.password"));


        } catch (Exception e) {

            logger.error("Could not init symphony test client", e);


        }

        Assume.assumeTrue(sjcTestClient != null);


    }

    @Before
    public void setupBefore() throws Exception {

    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        if (sjcTestClient != null)
            sjcTestClient.shutdown();


    }


    @Test
    public void verifyUserCache() throws Exception {
        //init it.
        try {
            SymUserCache.getUserById(sjcTestClient, (long) 1000000);
        } catch (Exception e) {
        }



            long start = System.currentTimeMillis();
            SymUser symUser = SymUserCache.getUserByEmail(sjcTestClient, MP_USER_EMAIL);
            logger.info("Lookup {} took: {}", MP_USER_EMAIL, System.currentTimeMillis() - start);

            start = System.currentTimeMillis();
            SymUserCache.getUserByEmail(sjcTestClient, MP_USER_EMAIL);
            logger.info("Lookup {} took: {}", MP_USER_EMAIL, System.currentTimeMillis() - start);


            start = System.currentTimeMillis();
            SymUserCache.getUserById(sjcTestClient, symUser.getId());
            logger.info("Lookup {} took: {}", symUser.getId(), System.currentTimeMillis() - start);

            start = System.currentTimeMillis();
            SymUserCache.getUserByName(sjcTestClient, symUser.getUsername());
            logger.info("Lookup {} took: {}", symUser.getDisplayName(), System.currentTimeMillis() - start);




    }


    public void pause(){

        //Can use properties to override default time wait
        try {

            TimeUnit.SECONDS.sleep(6 );
        } catch (InterruptedException e1) {
            logger.error("Interrupt.. ", e1);
            Thread.currentThread().interrupt();
        }

    }

}