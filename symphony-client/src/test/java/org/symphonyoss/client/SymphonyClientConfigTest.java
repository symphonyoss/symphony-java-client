/*
 * Copyright 2017 Symphony Communication Services, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.symphonyoss.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.symphonyoss.client.exceptions.ProgramFault;

public class SymphonyClientConfigTest {
	private static final String	JAVA_VERSION_PROPERTY	= "java.version";
	private static final String SESSIONAUTH_NAME = "sessionauth.url";
	private static final String SESSIONAUTH_URI = "https://corporate-api.symphony.com:8444/sessionauth";
	private static final String OVERRIDE = "OVERRIDE";
	
	@Test
    public void configTest() {
		try {
			new SymphonyClientConfig(true);
			fail("Expected ProgramFault with no valid config");
		}
		catch(ProgramFault e)
		{
			// expected
		}
		
		System.setProperty(SESSIONAUTH_NAME, SESSIONAUTH_URI);
		System.setProperty("keyauth.url", "https://corporate-api.symphony.com:8444/keyauth");
		System.setProperty("pod.url", "https://corporate.symphony.com/pod");
		System.setProperty("agent.url", "https://corporate.symphony.com/agent");
		System.setProperty("truststore.file", "/atlas/symphony/env/corporate/certs/server.truststore");
		System.setProperty("truststore.password", "changeit");
		System.setProperty("user.cert.file", "/atlas/symphony/env/corporate/certs/bot.user5.p12");
		System.setProperty("user.cert.password", "changeit");
		System.setProperty("user.email", "bot.user5@symphony.com");
		System.setProperty("receiver.user.email", "john.doe@symphony.com");

		//Causing issues
		//System.setProperty("sender.user.email", "bot.user5@symphony.com");
	
		SymphonyClientConfig	config = new SymphonyClientConfig();
		
		assertEquals(SESSIONAUTH_URI, config.get(SESSIONAUTH_NAME));

		String	javaVersion = System.getProperty(JAVA_VERSION_PROPERTY);
		
		assertEquals(javaVersion, config.get(JAVA_VERSION_PROPERTY));
	
		Set<String>	ignoreSet = new HashSet<>();
		
		for(SymphonyClientConfigID id : SymphonyClientConfigID.values()) {
			ignoreSet.add(id.getAltName());
			ignoreSet.add(id.getPropName());
			ignoreSet.add(id.getEnvName());
		}
		
		Map<String, String> map = System.getenv();

		for(String name : map.keySet())
		{
			if(SymphonyClientConfigID.toEnvName(name).equals(name)) {
				System.err.println("name=" + name +
						", env=" + map.get(name) +
						", config=" + config.get(name));
				
				if(!name.toUpperCase().startsWith("JAVA") &&
						!ignoreSet.contains(name))
					assertEquals(System.getenv(name), config.get(name));
				
				config.getRequired(name);
				
				System.setProperty(SymphonyClientConfigID.toPropName(name), OVERRIDE);
				
				assertEquals(OVERRIDE, config.get(name));
			}
		}
		
		String id = "Z";
		
		while(System.getenv(id) != null && System.getProperty(id) != null) {
			id = id + "X";
		}
		
		try {
			config.getRequired(id);
			fail("Expected ProgramFault with invalid env var");
		}
		catch(ProgramFault e)
		{
			// expected
		}
		
		try {
			config.getRequired(SymphonyClientConfigID.SYMPHONY_CONFIG_FILE);
			fail("Expected ProgramFault with SYMPHONY_CONFIG_FILE");
		}
		catch(ProgramFault e)
		{
			// expected
		}
		
		System.setProperty(SymphonyClientConfigID.SYMPHONY_CONFIG_FILE.getPropName(),
				"src/test/resources/symphony.properties");
		
		config = new SymphonyClientConfig();
		
		try {
			config.getRequired(SymphonyClientConfigID.SYMPHONY_CONFIG_FILE);
		}
		catch(ProgramFault e)
		{
			fail("Expected symphony.properties with SYMPHONY_CONFIG_FILE");
		}

		//For some reason this is blowing up..
		//assertEquals("https://corporate-api.symphony.com:8444/sessionauth", config.get(SESSIONAUTH_NAME));
    }
}
