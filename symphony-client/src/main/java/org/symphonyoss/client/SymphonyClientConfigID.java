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

public enum SymphonyClientConfigID {
    SYMPHONY_CONFIG_FILE(false),
    SESSIONAUTH_URL,
    KEYAUTH_URL,
    POD_URL,
    AGENT_URL,
    TRUSTSTORE_FILE("javax.net.ssl.trustStore",false),
    TRUSTSTORE_PASSWORD("javax.net.ssl.trustStorePassword",false),
    USER_CERT_FILE("javax.net.ssl.keyStore"),
    USER_CERT_PASSWORD("javax.net.ssl.keyStorePassword"),
    USER_EMAIL,
    RECEIVER_EMAIL(false),
    GET_ALL_USERS_TIMEOUT(false),
    DISABLE_SERVICES(false),
    HEALTHCHECK_JMX_ENABLED(false);
    
    private final String altName;
    private final String propName;
    private final boolean core;
    
    SymphonyClientConfigID()
    {
	this(null, true);
    }
    
    SymphonyClientConfigID(String altName)
    {
	this(altName, true);
    }
    
    SymphonyClientConfigID(boolean core)
    {
	this(null, core);
    }

    SymphonyClientConfigID(String altName, boolean core)
    {
	this.altName = altName;
	propName = toPropName(name());
	this.core = core;
    }

    public static String toPropName(String name) {
	return name.toLowerCase().replaceAll("_", ".");
    }
    
    public static String toEnvName(String name) {
	return name.toUpperCase().replaceAll("\\.", "_");
    }

    public String getAltName() {
        return altName;
    }

    public String getPropName() {
        return propName;
    }
    
    public String getEnvName() {
        return name();
    }

    public boolean isCore() {
        return core;
    }

    @Override
    public String toString() {
	if(altName == null)
	    return getPropName();
	
	return getPropName() + " (or " + getAltName() + ")";
    }
}
