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

package setpresence;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.SymphonyClientConfig;
import org.symphonyoss.client.SymphonyClientFactory;
import org.symphonyoss.symphony.pod.model.Presence;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 *
 *
 * Simple example to set user presence.
 *
 * It will send a message to a call.home.user and listen/create new Chat sessions.
 *
 *
 *
 * REQUIRED VM Arguments or System Properties:
 *
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
 *
 *
 * @author  Frank Tarsillo
 */
//NOSONAR
public class SetPresenceExample {


    private final Logger logger = LoggerFactory.getLogger(SetPresenceExample.class);
    private SymphonyClient symClient;

    public SetPresenceExample() {


        init();


    }

    public static void main(String[] args) {



        new SetPresenceExample();

    }

    public void init() {

        logger.info("Presence Service example starting...");

        try {



            //Create an initialized client
            symClient = SymphonyClientFactory.getClient(
                    SymphonyClientFactory.TYPE.V4, new SymphonyClientConfig(true));



            List<Presence> presences = new ArrayList<>();
            Presence presence = new Presence();
            presence.setCategory(Presence.CategoryEnum.BUSY);
            presences.add(presence);
            presence = new Presence();
            presence.setCategory(Presence.CategoryEnum.AWAY);
            presences.add(presence);
            presence = new Presence();
            presence.setCategory(Presence.CategoryEnum.AVAILABLE);
            presences.add(presence);




            for(Presence p: presences) {
                symClient.getPresenceClient().setUserPresence(symClient.getLocalUser().getId(), p);

                logger.info("SET PRESENCE TO: {} ", p.getCategory().toString());
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

            symClient.shutdown();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }



}
