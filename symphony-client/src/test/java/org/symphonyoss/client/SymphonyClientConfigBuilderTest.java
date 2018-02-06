/*
 * Copyright 2018 Symphony Communication Services, LLC.
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

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SymphonyClientConfigBuilderTest
{
    @Test
    public void configTest()
    {
        SymphonyClientConfig config = SymphonyClientConfigBuilder.newBuilder()
                                                                 .withSessionAuthUrl("dummyValue-SessionAuthUrl")
                                                                 .withKeyAuthUrl("dummyValue-KeyAuthUrl")
                                                                 .withPodUrl("dummyValue-PodUrl")
                                                                 .withAgentUrl("dummyValue-AgentUrl")
                                                                 .withTrustStore("dummyValue-TrustStoreFile", "dummyValue-TrustStorePassword".toCharArray())
                                                                 .withUserCreds("dummyValue-UserEmail", "dummyValue-UserCertFile", "dummyValue-UserCertPassword".toCharArray())
                                                                 .withReceiverEmail("dummyValue-ReceiverEmail")
                                                                 .withServices(true)
                                                                 .withJMXHealthcheck(true)
                                                                 .build();

        assertEquals(config.get(SymphonyClientConfigID.SESSIONAUTH_URL), "dummyValue-SessionAuthUrl");
        assertEquals(config.get(SymphonyClientConfigID.KEYAUTH_URL), "dummyValue-KeyAuthUrl");
        assertEquals(config.get(SymphonyClientConfigID.POD_URL), "dummyValue-PodUrl");
        assertEquals(config.get(SymphonyClientConfigID.AGENT_URL), "dummyValue-AgentUrl");
        assertEquals(config.get(SymphonyClientConfigID.TRUSTSTORE_FILE), "dummyValue-TrustStoreFile");
        assertEquals(config.get(SymphonyClientConfigID.TRUSTSTORE_PASSWORD), "dummyValue-TrustStorePassword");
        assertEquals(config.get(SymphonyClientConfigID.USER_EMAIL), "dummyValue-UserEmail");
        assertEquals(config.get(SymphonyClientConfigID.USER_CERT_FILE), "dummyValue-UserCertFile");
        assertEquals(config.get(SymphonyClientConfigID.USER_CERT_PASSWORD), "dummyValue-UserCertPassword");
        assertEquals(config.get(SymphonyClientConfigID.RECEIVER_EMAIL), "dummyValue-ReceiverEmail");
        assertEquals(config.get(SymphonyClientConfigID.DISABLE_SERVICES), String.valueOf(false));
        assertEquals(config.get(SymphonyClientConfigID.HEALTHCHECK_JMX_ENABLED), String.valueOf(true));
    }
}
