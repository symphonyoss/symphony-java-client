/*
 *
 *
 * Copyright 2018 The Symphony Software Foundation
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

package signals;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.SymphonyClientConfig;
import org.symphonyoss.client.SymphonyClientFactory;
import org.symphonyoss.client.SymphonyClientFactory.TYPE;
import org.symphonyoss.client.exceptions.SignalsException;
import org.symphonyoss.symphony.agent.invoker.ApiException;
import org.symphonyoss.symphony.clients.model.SymSignal;

/**
 * Simple example to list signals
 * <p>
 * It will send a message to a call.home.user and listen/create new Chat
 * sessions.
 * <p>
 * <p>
 * <p>
 * REQUIRED VM Arguments or System Properties:
 * <p>
 * -Dtruststore.file= -Dtruststore.password=password
 * -Dsessionauth.url=https://(hostname)/sessionauth
 * -Dkeyauth.url=https://(hostname)/keyauth
 * -Duser.call.home=frank.tarsillo@markit.com -Duser.cert.password=password
 * -Duser.cert.file=bot.user2.p12 -Duser.email=bot.user2@domain.com
 * -Dpod.url=https://(pod host)/pod -Dagent.url=https://(agent server
 * host)/agent -Dreceiver.email=bot.user2@markit.com or bot user email
 *
 * @author Dov Katz
 */
// NOSONAR
public class SignalsExample {

	private final Logger logger = LoggerFactory.getLogger(SignalsExample.class);
	private SymphonyClient symClient;

	public SignalsExample() {

		init();

	}

	public static void main(String[] args) {

		new SignalsExample();

	}

	public void init() {
		logger.info("Signal Client example starting...");

		try {

			// Create an initialized client
			symClient = SymphonyClientFactory.getClient(TYPE.V4);
			symClient.init(new SymphonyClientConfig(true));
			logger.info("My bot email is {}", symClient.getLocalUser().getEmailAddress());
			logger.info("My bot' name is {}", symClient.getLocalUser().getDisplayName());

			// List Signals
			List<SymSignal> signals = symClient.getSignalsClient().listSignals(0, 10);
			logger.info("Found (max 10) {} signals", signals.size());
			signals.forEach(signal -> {
				logger.info("Signal :{}", signal);
			});

			// Create Signal
			SymSignal signal = new SymSignal();
			signal.setVisibleOnProfile(false);
			signal.setName("Test Signal " + System.currentTimeMillis());
			signal.setQuery("HASHTAG:testSignal AND CASHTAG:money");
			signal.setCompanyWide(false);
			;
			logger.info("Going to create a signal : Name {}, Query: {}", signal.getName(), signal.getQuery());
			;
			SymSignal result = symClient.getSignalsClient().createSignal(signal);
			logger.info("Created Signal ID {}, Name {}", result.getId(), result.getName());
			;

			// Search for the one we just created
			logger.info("Let's search for our new signal");
			SymSignal found = symClient.getSignalsClient().getSignal(result.getId());
			logger.info("Found signal we just created by ID {} --> {} ( {} )", result.getId(), found.getName(),
					found.getQuery());

			// Delete Signal
			logger.info("Let's delete our signal");
			symClient.getSignalsClient().deleteSignal(result.getId());
			logger.info("Deleted Signal {},  Let's prove it", result.getId());

			// Search for the one we just deleted
			try {
				found = symClient.getSignalsClient().getSignal(result.getId());
				logger.info("We didn't delete the signal,  it seems this was found {}: ",found);
			} catch (SignalsException sigExc) {
				if (sigExc.getCause() instanceof ApiException && ((ApiException) sigExc.getCause()).getCode() == 404) {
					logger.info(
							"Successfully got a 404 when trying to search for a non-existent signal. Deletion succeeded");
					;
				}
			}
			logger.info("Client shutting down.");
			symClient.shutdown();
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
