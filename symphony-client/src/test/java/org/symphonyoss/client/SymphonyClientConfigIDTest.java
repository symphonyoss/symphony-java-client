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

import org.junit.Test;

public class SymphonyClientConfigIDTest {
	private static final String	PROP_NAME				= "symphony.config.file";
	private static final String	ENV_NAME				= "SYMPHONY_CONFIG_FILE";
	
	@Test
    public void idTest() {

		assertEquals(ENV_NAME, SymphonyClientConfigID.toEnvName(PROP_NAME));
		assertEquals(PROP_NAME, SymphonyClientConfigID.toPropName(ENV_NAME));
    }
}
