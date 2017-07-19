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
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 *
 */

package org.symphonyoss.client.impl;

import org.glassfish.jersey.client.ClientConfig;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.io.InputStream;
import java.security.KeyStore;

/**
 * A custom HTTP client can be used when there are specific connectivity requirements or there is a need to support
 * multiple keystores.
 *
 * @author Frank Tarsillo on 10/26/2016.
 */
@SuppressWarnings("unused")
//NOSONAR
public class CustomHttpClient {

    public CustomHttpClient() {

    }


    /**
     * Create custom client with specific keystores.
     *
     * @param clientKeyStore     Client (BOT) keystore file
     * @param clientKeyStorePass Client (BOT) keystore password
     * @param trustStore         Truststore file
     * @param trustStorePass     Truststore password
     * @return Custom HttpClient
     * @throws Exception Generally IOExceptions thrown from instantiation.
     */
    public static Client getClient(String clientKeyStore, String clientKeyStorePass, String trustStore, String trustStorePass) throws Exception {


        KeyStore cks = KeyStore.getInstance("PKCS12");
        KeyStore tks = KeyStore.getInstance("JKS");

        loadKeyStore(cks, clientKeyStore, clientKeyStorePass);
        loadKeyStore(tks, trustStore, trustStorePass);


        return ClientBuilder.newBuilder().keyStore(cks, clientKeyStorePass.toCharArray()).trustStore(tks).build();


    }

    /**
     * Create custom client with specific keystores.
     *
     * @param clientKeyStore     Client (BOT) keystore file
     * @param clientKeyStorePass Client (BOT) keystore password
     * @param trustStore         Truststore file
     * @param trustStorePass     Truststore password
     * @param clientConfig       - HttpClient configuration to use when constructing the client
     * @return Custom HttpClient
     * @throws Exception Generally IOExceptions thrown from instantiation.
     */
    public static Client getClient(String clientKeyStore, String clientKeyStorePass, String trustStore, String trustStorePass, ClientConfig clientConfig) throws Exception {


        KeyStore cks = KeyStore.getInstance("PKCS12");
        KeyStore tks = KeyStore.getInstance("JKS");

        loadKeyStore(cks, clientKeyStore, clientKeyStorePass);
        loadKeyStore(tks, trustStore, trustStorePass);


        return  getClient(cks, clientKeyStorePass, tks, trustStorePass, clientConfig);


    }




    /**
     * Create custom client with specific keystores.
     *
     * @param clientKeyStore     Client (BOT) keystore InputStream (usually represents a file)
     * @param clientKeyStorePass Client (BOT) keystore password
     * @param trustStore         Truststore IntputStream (usually represents a file)
     * @param trustStorePass     Truststore password
     * @param clientConfig       - HttpClient configuration to use when constructing the client
     * @return Custom HttpClient
     * @throws Exception Generally IOExceptions thrown from instantiation.
     */
    public static Client getClient(InputStream clientKeyStore, String clientKeyStorePass, InputStream trustStore, String trustStorePass, ClientConfig clientConfig) throws Exception {


        KeyStore cks = KeyStore.getInstance("PKCS12");
        KeyStore tks = KeyStore.getInstance("JKS");

        loadKeyStore(cks, clientKeyStore, clientKeyStorePass);
        loadKeyStore(tks, trustStore, trustStorePass);


        return getClient(cks, clientKeyStorePass, tks, trustStorePass, clientConfig);


    }



    /**
     * Create custom client with specific keystores.
     *
     * @param clientKeyStore     Client (BOT) keystore
     * @param clientKeyStorePass Client (BOT) keystore password
     * @param trustStore         Truststore
     * @param trustStorePass     Truststore password
     * @param clientConfig       - HttpClient configuration to use when constructing the client
     * @return Custom HttpClient
     * @throws Exception Generally IOExceptions thrown from instantiation.
     */
    public static Client getClient(KeyStore clientKeyStore, String clientKeyStorePass, KeyStore trustStore, String trustStorePass, ClientConfig clientConfig) throws Exception {


        return ClientBuilder.newBuilder().keyStore(clientKeyStore, clientKeyStorePass.toCharArray()).trustStore(trustStore).withConfig(clientConfig).build();


    }

    /**
     * Internal keystore loader
     *
     * @param ks     Keystore object which defines the expected type (PKCS12, JKS)
     * @param ksFile Keystore file to process
     * @param ksPass Keystore password for file to process
     * @throws Exception Generally IOExceptions generated from file read
     */
    //NOSONAR
    private static void loadKeyStore(KeyStore ks, String ksFile, String ksPass) throws Exception {

        java.io.FileInputStream fis = null;
        try {
            fis = new java.io.FileInputStream(ksFile);
            loadKeyStore(ks,fis, ksPass);
        } finally {
            if (fis != null) {
                fis.close();
            }
        }

    }


    /**
     * Internal keystore loader
     *
     * @param ks     Keystore object which defines the expected type (PKCS12, JKS)
     * @param ksInputStream Keystore InputStream  to process
     * @param ksPass Keystore password for InputStream to process
     * @throws Exception Generally IOExceptions generated from file read
     */
    //NOSONAR
    private static void loadKeyStore(KeyStore ks, InputStream ksInputStream, String ksPass) throws Exception {

            ks.load(ksInputStream, ksPass.toCharArray());

    }



}
