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

package reports;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.SymphonyClientConfig;
import org.symphonyoss.client.SymphonyClientConfigID;
import org.symphonyoss.client.SymphonyClientFactory;
import org.symphonyoss.client.exceptions.UsersClientException;
import org.symphonyoss.symphony.clients.model.SymUser;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;


/**
 * Outputs a CSV file of users with limited fields (you can always extend)
 *
 * <p>
 * <p>
 * REQUIRED VM Arguments or System Properties or using SymphonyClientConfig:
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
 * -Dreceiver.email= email address of user or bot who will receive a message.
 * -Dreport.file=UsersReport.csv
 *
 * @author Frank Tarsillo on 5/9/17.
 */
//NOSONAR
public class UserReport {


    private final Logger logger = LoggerFactory.getLogger(UserReport.class);


    private SymphonyClient symClient;

    public UserReport() {


        init();


    }

    public static void main(String[] args) {

        new UserReport();

    }

    public void init() {


        SymphonyClientConfig symphonyClientConfig = new SymphonyClientConfig();

        //Disable all real-time services
        symphonyClientConfig.set(SymphonyClientConfigID.DISABLE_SERVICES, "True");

        //Create an initialized client
        symClient = SymphonyClientFactory.getClient(
                SymphonyClientFactory.TYPE.V4, symphonyClientConfig);



        try {

            final FileWriter fw = new FileWriter(System.getProperty("report.file","UsersReport.csv"));
            final BufferedWriter bw = new BufferedWriter(fw);

            Set<SymUser> allUsers = symClient.getUsersClient().getAllUsersWithDetails();



            allUsers.forEach(symUser -> {
                try {
                    logger.debug("{}:{}:{}:{}", symUser.getUsername(), symUser.getId(), symUser.getCreatedDate(), symUser.getLastLoginDate());

                    bw.write(symUser.getUsername() + "," + symUser.getId() + "," + symUser.getCreatedDate() + "," + symUser.getLastLoginDate() + "\n");

                } catch (IOException e) {
                    logger.error("Writing to file");
                }
            });


        } catch (UsersClientException e) {
            logger.error("Failed to send message", e);
        } catch (IOException e) {
            logger.error("Writing to file");
        }


        if (symClient != null)
            symClient.shutdown();

        logger.info("Finished");


    }

}



