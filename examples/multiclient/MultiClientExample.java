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

package multiclient;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.SymphonyClientFactory;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.client.exceptions.StreamsException;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymStream;
import org.symphonyoss.symphony.pod.model.Stream;


/**
 * Simple example of the multiple client bot connections from the same JVM.
 * This overrides the default ssl properties and allows for different client certs and trust stores.
 * <p>
 * It will send a message to a call.home.user from different SyphonyClient instances.
 * <p>
 * <p>
 * <p>
 * REQUIRED VM Arguments or System Properties:
 * <p>
 * -Dsessionauth.url=https://pod_fqdn:port/sessionauth
 * -Dkeyauth.url=https://pod_fqdn:port/keyauth
 * -Dsymphony.agent.pod.url=https://agent_fqdn:port/pod
 * -Dsymphony.agent.agent.url=https://agent_fqdn:port/agent
 * -Dcerts.dir=/dev/certs/
 * -Dkeystore.password=(Pass)
 * -Dtruststore.file=/dev/certs/server.truststore
 * -Dtruststore.password=(Pass)
 * -Dbot.user1=bot.user1
 * -Dbot.user2=bot.user2
 * -Dbot.domain=domain.com
 * -Duser.call.home=frank.tarsillo@markit.com
 *
 * @author Frank Tarsillo
 */
//NOSONAR
public class MultiClientExample {


    private final Logger logger = LoggerFactory.getLogger(MultiClientExample.class);


    public MultiClientExample() {

        //Note: You can replace all the properties with two different instances of SymphonyClientConfig in this example

        SymphonyClient symClient1 = SymphonyClientFactory.getClient(SymphonyClientFactory.TYPE.V4,
                System.getProperty("bot.user1") + "@" + System.getProperty("bot.domain"),
                System.getProperty("certs.dir") + System.getProperty("bot.user1") + ".p12",
                System.getProperty("keystore.password"),
                System.getProperty("truststore.file"),
                System.getProperty("truststore.password"));

        SymphonyClient symClient2 = SymphonyClientFactory.getClient(SymphonyClientFactory.TYPE.V4,
                System.getProperty("bot.user2") + "@" + System.getProperty("bot.domain"),
                System.getProperty("certs.dir") + System.getProperty("bot.user2") + ".p12",
                System.getProperty("keystore.password"),
                System.getProperty("truststore.file"),
                System.getProperty("truststore.password"));


        try {
            SymStream stream1 = symClient1.getStreamsClient().getStreamFromEmail(System.getProperty("user.call.home"));

            SymStream stream2 = symClient2.getStreamsClient().getStreamFromEmail(System.getProperty("user.call.home"));


            //A message to send when the BOT comes online.
            SymMessage aMessage = new SymMessage();


            aMessage.setMessageText("Hello master from bot1...");

            symClient1.getMessagesClient().sendMessage(stream1, aMessage);

            aMessage.setMessageText("Hello master from bot2...");

            symClient2.getMessagesClient().sendMessage(stream2, aMessage);

            aMessage.setMessageText("Hello master from bot1..again...");

            symClient1.getMessagesClient().sendMessage(stream1, aMessage);

            aMessage.setMessageText("Hello master from bot2..again and again..");

            symClient2.getMessagesClient().sendMessage(stream2, aMessage);
            symClient2.getMessagesClient().sendMessage(stream2, aMessage);
            symClient2.getMessagesClient().sendMessage(stream2, aMessage);
            symClient2.getMessagesClient().sendMessage(stream2, aMessage);


            aMessage.setMessage("Hello master from bot1..again and again...");

            symClient1.getMessagesClient().sendMessage(stream1, aMessage);
            symClient1.getMessagesClient().sendMessage(stream1, aMessage);
            symClient1.getMessagesClient().sendMessage(stream1, aMessage);
            symClient1.getMessagesClient().sendMessage(stream1, aMessage);
            symClient1.getMessagesClient().sendMessage(stream1, aMessage);

            System.exit(1);

        } catch (StreamsException e) {
            e.printStackTrace();
        } catch (MessagesException e) {
            e.printStackTrace();
        }


    }

    public static void main(String[] args) {

        new MultiClientExample();

    }


}
