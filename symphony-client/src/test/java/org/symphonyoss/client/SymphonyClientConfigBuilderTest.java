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
import static org.junit.Assert.assertNull;

public class SymphonyClientConfigBuilderTest
{
    @Test
    public void fullConfigBuildTest()
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

        assertEquals("dummyValue-SessionAuthUrl",     config.rawGet(SymphonyClientConfigID.SESSIONAUTH_URL));
        assertEquals("dummyValue-KeyAuthUrl",         config.rawGet(SymphonyClientConfigID.KEYAUTH_URL));
        assertEquals("dummyValue-PodUrl",             config.rawGet(SymphonyClientConfigID.POD_URL));
        assertEquals("dummyValue-AgentUrl",           config.rawGet(SymphonyClientConfigID.AGENT_URL));
        assertEquals("dummyValue-TrustStoreFile",     config.rawGet(SymphonyClientConfigID.TRUSTSTORE_FILE));
        assertEquals("dummyValue-TrustStorePassword", config.rawGet(SymphonyClientConfigID.TRUSTSTORE_PASSWORD));
        assertEquals("dummyValue-UserEmail",          config.rawGet(SymphonyClientConfigID.USER_EMAIL));
        assertEquals("dummyValue-UserCertFile",       config.rawGet(SymphonyClientConfigID.USER_CERT_FILE));
        assertEquals("dummyValue-UserCertPassword",   config.rawGet(SymphonyClientConfigID.USER_CERT_PASSWORD));
        assertEquals("dummyValue-ReceiverEmail",      config.rawGet(SymphonyClientConfigID.RECEIVER_EMAIL));
        assertEquals(String.valueOf(false),                    config.rawGet(SymphonyClientConfigID.DISABLE_SERVICES));
        assertEquals(String.valueOf(true),                     config.rawGet(SymphonyClientConfigID.HEALTHCHECK_JMX_ENABLED));
    }

    @Test
    public void nullConfigBuildTest()
    {
        SymphonyClientConfig config = SymphonyClientConfigBuilder.newBuilder()
            .withSessionAuthUrl(null)
            .withKeyAuthUrl(null)
            .withPodUrl(null)
            .withAgentUrl(null)
            .withTrustStore(null, null)
            .withUserCreds(null, null, null)
            .withReceiverEmail(null)
            .build();

        assertNull(config.get(SymphonyClientConfigID.SESSIONAUTH_URL));
        assertNull(config.get(SymphonyClientConfigID.KEYAUTH_URL));
        assertNull(config.get(SymphonyClientConfigID.POD_URL));
        assertNull(config.get(SymphonyClientConfigID.AGENT_URL));
        assertNull(config.get(SymphonyClientConfigID.TRUSTSTORE_FILE));
        assertNull(config.get(SymphonyClientConfigID.TRUSTSTORE_PASSWORD));
        assertNull(config.get(SymphonyClientConfigID.USER_EMAIL));
        assertNull(config.get(SymphonyClientConfigID.USER_CERT_FILE));
        assertNull(config.get(SymphonyClientConfigID.USER_CERT_PASSWORD));
        assertNull(config.get(SymphonyClientConfigID.RECEIVER_EMAIL));
    }

    @Test
    public void defaultConfigBuildTest()
    {
        SymphonyClientConfig config = SymphonyClientConfigBuilder.newBuilder()
            .withSessionAuthUrl(null)
            .withKeyAuthUrl(null)
            .withPodUrl(null)
            .withAgentUrl(null)
            .withTrustStore(null, null)
            .withUserCreds(null, null, null)
            .build();

        assertEquals(String.valueOf(false), config.rawGet(SymphonyClientConfigID.DISABLE_SERVICES));
        assertEquals(String.valueOf(true),  config.rawGet(SymphonyClientConfigID.HEALTHCHECK_JMX_ENABLED));
    }
}
