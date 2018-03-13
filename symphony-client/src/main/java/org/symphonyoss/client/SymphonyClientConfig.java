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

    private boolean initialized;

    /**
     * Default constructor will no load or validate configuration.
     */
    public SymphonyClientConfig() {

    }


    /**
     * Constructor that has option to load/validate configuration requirements
     *
     * @param load Load and validate configuration
     */
    public SymphonyClientConfig(boolean load) {

        if (load)
            load();

    }


    /**
     * Constructor requiring configuration properties file, which is loaded and validated
     *
     * @param configFile Properties file containing configuration detail.
     */
    public SymphonyClientConfig(String configFile) {
        this.set(SymphonyClientConfigID.SYMPHONY_CONFIG_FILE, configFile);
        load();
    }

    public void load() throws ProgramFault {
        String configFile = get(SymphonyClientConfigID.SYMPHONY_CONFIG_FILE);

        if (configFile != null) {
            upsertProperties(configFile);
        }

        StringBuilder s = null;

        for (SymphonyClientConfigID id : SymphonyClientConfigID.values()) {
            String v = get(id);
            if (v != null) {
                log.debug("{} = {}", id.getPropName(), v);
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


        initialized = true;
    }


    /**
     * This method is intended for use in unit tests that need to be able to treat this class as a DTO.
     *
     * @param id The configuration parameter to retrieve.
     * @return The value of that configuration parameter, as stored in memory.
     */
    public String rawGet(SymphonyClientConfigID id)
    {
        return(config.getProperty(id.getPropName()));
    }


    /**
     * This method does some unusual shenanigans that probably belong elsewhere, so that this class can be a true DTO.
     * A refactoring task for another day...
     *
     * @param id The configuration parameter to retrieve.
     * @return The value of that configuration parameter, as stored in memory, or one of several Java "system properties"
     * or environment variables with names vaguely similar to <code>id</code>.  Basically non-deterministic, so YMMV.
     */
    public String get(SymphonyClientConfigID id) {
        String value = rawGet(id);

        if (value == null)
            value = System.getProperty(id.getPropName());

        if (value == null)
            value = System.getenv(id.getEnvName());

        if (value == null && id.getAltName() != null)
            value = System.getProperty(id.getAltName());

        return value;
    }


    public String get(SymphonyClientConfigID id, String defaultValue) {

        String value = get(id);
        return (value != null) ? value : defaultValue;


    }

    public String getRequired(SymphonyClientConfigID id) {
        String value = get(id);

        if (value == null)
            throw new ProgramFault("Required config parameter \"" + id + "\" is undefined.");

        return value;
    }

    /**
     * This allows user to obtain a general config value by string.
     *
     * @param id Property name
     * @return Value for property
     */
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

    /**
     * Set a predefined config property
     *
     * @param id    Predefined config property
     * @param value Value for property
     */
    public void set(SymphonyClientConfigID id, String value) {

        config.setProperty(id.getPropName(), value);

    }


    /**
     * Set a any config property
     *
     * @param property    Any config property
     * @param value Value for property
     */
    public void set(String property, String value) {

        config.setProperty(property, value);

    }


    /**
     * Supports the ability to update and insert new properties from additional properties files.
     *
     * @param propertiesFile Properties file to upsert
     * @throws ProgramFault  Fault from merging properties
     *
     */
    public void upsertProperties(String propertiesFile) throws ProgramFault{


        Properties properties = loadProperties(propertiesFile);

        if(config == null) {
            config = properties;
        }else {

            config.putAll(properties);
        }


    }


    private Properties loadProperties(String propertiesFile) throws ProgramFault{

        Properties properties = new Properties();

        if (propertiesFile != null) {
            try (Reader reader = new FileReader(propertiesFile)) {
                properties.load(reader);



            } catch (FileNotFoundException e) {
                throw new ProgramFault("Config file \"" + propertiesFile + "\" not found");
            } catch (IOException e) {
                throw new ProgramFault("Config file \"" + propertiesFile + "\" cannot be built", e);
            }
        }

        return properties;

    }



    public boolean isInitialized() {
        return initialized;
    }

}
