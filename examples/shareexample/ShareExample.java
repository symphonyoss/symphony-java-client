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

package shareexample;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.SymphonyClientConfig;
import org.symphonyoss.client.SymphonyClientFactory;
import org.symphonyoss.client.exceptions.ShareException;
import org.symphonyoss.client.exceptions.StreamsException;
import org.symphonyoss.client.model.SymShareArticle;


/**
 * * Simple example of the ShareClient which will send a ShareArticle to a stream.
 * <p>
 * <p>
 * <p>
 * REQUIRED VM Arguments or System Properties:
 * <p>
 * -Dtruststore.file=
 * -Dtruststore.password=password
 * -Dsessionauth.url=https://(hostname)/sessionauth
 * -Dkeyauth.url=https://(hostname)/keyauth
 * -Duser.call.home=frank.tarsillo@markit.com
 * -Duser.cert.password=password
 * -Duser.cert.file=bot.user2.p12
 * -Duser.email=bot.user2@domain.com
 * -Dpod.url=https://(pod host)/pod
 * -Dagent.url=https://(agent server host)/agent
 * -Dreceiver.email=bot.user2@markit.com or bot user email

 *
 * @author Frank Tarsillo
 */
//NOSONAR
public class ShareExample {


    private final Logger logger = LoggerFactory.getLogger(ShareExample.class);

    private SymphonyClient symClient;

    public ShareExample() {


        init();


    }

    public static void main(String[] args) {

        new ShareExample();

    }

    public void init() {


        try {

            SymphonyClientConfig symphonyClientConfig = new SymphonyClientConfig(true);

            //Create an initialized client
            symClient = SymphonyClientFactory.getClient(
                    SymphonyClientFactory.TYPE.V4, symphonyClientConfig);


            SymShareArticle shareArticle = new SymShareArticle();

            shareArticle.setArticleId("ID ID");
            shareArticle.setTitle("TEST");
            shareArticle.setSummary(" TEST SUMMARY");
            shareArticle.setMessage("A message from bot..");
            shareArticle.setArticleUrl("http://www.cnn.com");
            shareArticle.setSubTitle("TEST Subtitle");
            shareArticle.setPublisher("A publisher");
            shareArticle.setAuthor("Frank Tarsillo");
            shareArticle.setAppId("APP ID");

            symClient.getShareClient().shareArticle(symClient.getStreamsClient().getStreamFromEmail("frank.tarsillo@markit.com").getStreamId(), shareArticle);


        } catch ( StreamsException | ShareException e) {
            logger.error("error", e);
        }

    }


}
