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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.exceptions.ProgramFault;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

/**
 * Configuration management
 * <p>
 * Will evaluate ENV and file properties
 *
 * @author Bruce Skingle
 */
public class SymphonyClientConfig {
    private static Logger log = LoggerFactory.getLogger(SymphonyClientConfig.class);

    private Properties config = new Properties();

    public SymphonyClientConfig() {
        String configFile = get(SymphonyClientConfigID.SYMPHONY_CONFIG_FILE);

        if (configFile != null) {
            try (Reader reader = new FileReader(configFile)) {
                config.load(reader);
            } catch (FileNotFoundException e) {
                throw new ProgramFault("Config file \"" + configFile + "\" not found");
            } catch (IOException e) {
                throw new ProgramFault("Config file \"" + configFile + "\" cannot be loaded", e);
            }
        }

        StringBuilder s = null;

        for (SymphonyClientConfigID id : SymphonyClientConfigID.values()) {
            String v = get(id);
            if (v != null) {
                log.debug("%s = %s", id.getPropName(), v);
            } else if (id.isCore()) {
                if (s == null) {
                    s = new StringBuilder();
                } else {
                    s.append(",\n");
                }

                s.append(id);
            }
        }

        if (s != null)
            throw new ProgramFault("The following required properties are undefined:\n"
                    + s.toString());
    }

    public String get(SymphonyClientConfigID id) {
        String value = config.getProperty(id.getPropName());

        if (value == null)
            value = System.getProperty(id.getPropName());

        if (value == null)
            value = System.getenv(id.getEnvName());

        if (value == null && id.getAltName() != null)
            value = System.getProperty(id.getAltName());

        return value;
    }

    public String getRequired(SymphonyClientConfigID id) {
        String value = get(id);

        if (value == null)
            throw new ProgramFault("Required config parameter \"" + id + "\" is undefined.");

        return value;
    }

    public String get(String id) {
        String value = config.getProperty(id);

        if (value == null)
            value = System.getProperty(SymphonyClientConfigID.toPropName(id));

        if (value == null)
            value = System.getenv(SymphonyClientConfigID.toEnvName(id));

        return value;
    }

    public String getRequired(String id) {
        String value = get(id);

        if (value == null)
            throw new ProgramFault("Required config parameter \"" + id + "\" is undefined.");

        return value;
    }

}
