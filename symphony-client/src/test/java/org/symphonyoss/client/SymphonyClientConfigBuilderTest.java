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

        assertEquals("dummyValue-SessionAuthUrl",     config.get(SymphonyClientConfigID.SESSIONAUTH_URL));
        assertEquals("dummyValue-KeyAuthUrl",         config.get(SymphonyClientConfigID.KEYAUTH_URL));
        assertEquals("dummyValue-PodUrl",             config.get(SymphonyClientConfigID.POD_URL));
        assertEquals("dummyValue-AgentUrl",           config.get(SymphonyClientConfigID.AGENT_URL));
        assertEquals("dummyValue-TrustStoreFile",     config.get(SymphonyClientConfigID.TRUSTSTORE_FILE));
        assertEquals("dummyValue-TrustStorePassword", config.get(SymphonyClientConfigID.TRUSTSTORE_PASSWORD));
        assertEquals("dummyValue-UserEmail",          config.get(SymphonyClientConfigID.USER_EMAIL));
        assertEquals("dummyValue-UserCertFile",       config.get(SymphonyClientConfigID.USER_CERT_FILE));
        assertEquals("dummyValue-UserCertPassword",   config.get(SymphonyClientConfigID.USER_CERT_PASSWORD));
        assertEquals("dummyValue-ReceiverEmail",      config.get(SymphonyClientConfigID.RECEIVER_EMAIL));
        assertEquals(String.valueOf(false),                    config.get(SymphonyClientConfigID.DISABLE_SERVICES));
        assertEquals(String.valueOf(true),                     config.get(SymphonyClientConfigID.HEALTHCHECK_JMX_ENABLED));
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

        assertEquals(null, config.get(SymphonyClientConfigID.SESSIONAUTH_URL));
        assertEquals(null, config.get(SymphonyClientConfigID.KEYAUTH_URL));
        assertEquals(null, config.get(SymphonyClientConfigID.POD_URL));
        assertEquals(null, config.get(SymphonyClientConfigID.AGENT_URL));
        assertEquals(null, config.get(SymphonyClientConfigID.TRUSTSTORE_FILE));
        assertEquals(null, config.get(SymphonyClientConfigID.TRUSTSTORE_PASSWORD));
        assertEquals(null, config.get(SymphonyClientConfigID.USER_EMAIL));
        assertEquals(null, config.get(SymphonyClientConfigID.USER_CERT_FILE));
        assertEquals(null, config.get(SymphonyClientConfigID.USER_CERT_PASSWORD));
        assertEquals(null, config.get(SymphonyClientConfigID.RECEIVER_EMAIL));
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

        assertEquals(String.valueOf(false), config.get(SymphonyClientConfigID.DISABLE_SERVICES));
        assertEquals(String.valueOf(true),  config.get(SymphonyClientConfigID.HEALTHCHECK_JMX_ENABLED));
    }
}
