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

package lex;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.SymphonyClientConfig;
import org.symphonyoss.client.SymphonyClientFactory;
import org.symphonyoss.client.model.Chat;
import org.symphonyoss.client.services.ChatServiceListener;


/**
 * LexBot is an example of an integration with AWS LEX bots.  It acts as a simple relay between the Symphony endpoint and
 * a AWS Lex bot.  The idea here is to handle all input through Lex NLP engines and responses through Lambda.
 *
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
 * -Dpod.url=https://(pod host)/pod
 * -Dagent.url=https://(agent server host)/agent
 * -Dreceiver.email=bot.user2@markit.com or bot user email
 *
 * @author Frank Tarsillo
 */
//NOSONAR
public class LexBot  implements ChatServiceListener{


    private final Logger logger = LoggerFactory.getLogger(LexBot.class);

    private SymphonyClient symClient;

    private LexBotDetail lexBotDetail = new LexBotDetail();



    public LexBot(String botName, String botAlias) {

        lexBotDetail.setBotName(botName);
        lexBotDetail.setBotAlias(botAlias);
        init();

    }

    public static void main(String[] args) {

        if(args.length == 2) {
            new LexBot(args[0], args[1]);
        }else{

            System.out.println("You need to provide a (lexbotname) (lexbotalias) to start");
        }
    }

    //Start it up..
    public void init() {


        SymphonyClientConfig symphonyClientConfig = new SymphonyClientConfig(true);

        //Create an initialized client
        symClient = SymphonyClientFactory.getClient(
                SymphonyClientFactory.TYPE.V4, symphonyClientConfig);

        if(symClient!=null)
            symClient.getChatService().addListener(this);
    }


    @Override
    public void onNewChat(Chat chat) {

        chat.addListener(new LexBotRelay(symClient, lexBotDetail));

    }

    @Override
    public void onRemovedChat(Chat chat) {

    }
}




