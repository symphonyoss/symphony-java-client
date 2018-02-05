/*
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
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.symphonyoss.symphony.clients;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClientConfig;
import org.symphonyoss.client.SymphonyClientConfigID;
import org.symphonyoss.client.exceptions.AuthenticationException;
import org.symphonyoss.client.impl.CustomHttpClient;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.symphony.authenticator.api.AuthenticationApi;
import org.symphonyoss.symphony.authenticator.invoker.ApiException;
import org.symphonyoss.symphony.authenticator.invoker.Configuration;
import org.symphonyoss.symphony.authenticator.model.AuthenticateRequest;
import org.symphonyoss.symphony.authenticator.model.Token;
import org.symphonyoss.symphony.clients.model.SymExtensionAppAuth;
import org.symphonyoss.symphony.clients.model.SymUser;

import javax.ws.rs.client.Client;

/**
 * Authentication client to support retrieval of session, key tokens.
 * <p>
 * Tokens are used by most REST API endpoint calls to validate access.
 *
 * @author Frank Tarsillo
 */
public class AuthenticationClient {

    private SymAuth symAuth;
    private final String sessionUrl;
    private final String keyUrl;
    private boolean loginStatus = false;
    private final Logger logger = LoggerFactory.getLogger(AuthenticationClient.class);
    private Client httpClient = null;
    private Client httpClientForSessionToken;
    private Client httpClientForKeyToken;

    /**
     * Construct client implementation with session and key endpoints
     *
     * @param sessionUrl Session Service URL base endpoint
     * @param keyUrl     Key Service URL base endpoint
     */
    public AuthenticationClient(String sessionUrl, String keyUrl) {

        this(sessionUrl, keyUrl, null);


    }

    /**
     * Construct client implementation with session and key endpoints with overridden HTTP client
     *
     * @param sessionUrl Session Service URL base endpoint
     * @param keyUrl     Key Service URL base endpoint
     * @param httpClient Http Client to use when communicating to the Session/Key endpoints.
     */
    public AuthenticationClient(String sessionUrl, String keyUrl, Client httpClient) {
        this.sessionUrl = sessionUrl;
        this.keyUrl = keyUrl;
        this.httpClient = httpClient;

    }

    /**
     * Construct client implementation with session and key endpoints with overridden HTTP client
     *
     * @param symphonyClientConfig Symphony Client Config
     *
     */
    public AuthenticationClient(SymphonyClientConfig symphonyClientConfig)  {


        try {
            this.httpClient = CustomHttpClient.getClient(
                    symphonyClientConfig.get(SymphonyClientConfigID.USER_CERT_FILE),
                    symphonyClientConfig.get(SymphonyClientConfigID.USER_CERT_PASSWORD),
                    symphonyClientConfig.get(SymphonyClientConfigID.TRUSTSTORE_FILE),
                    symphonyClientConfig.get(SymphonyClientConfigID.TRUSTSTORE_PASSWORD));

        }catch(Exception e){

            logger.error("Could not create custom http client for use...",e);

        }
        this.sessionUrl = symphonyClientConfig.get(SymphonyClientConfigID.SESSIONAUTH_URL);
        this.keyUrl = symphonyClientConfig.get(SymphonyClientConfigID.KEYAUTH_URL);

    }



    /**
     * Construct client implementation with session and key endpoints with two different
     * overridden HTTP clients for session-token and key-token
     *
     * @param sessionUrl                Session Service URL base endpoint
     * @param keyUrl                    Key Service URL base endpoint
     * @param httpClientForSessionToken Http Client to use when communicating to the session-token endpoint
     * @param httpClientForKeyToken     Http Client to use when communicating to the key-token endpoint
     */
    public AuthenticationClient(String sessionUrl, String keyUrl, Client httpClientForSessionToken, Client httpClientForKeyToken) {
        this.sessionUrl = sessionUrl;
        this.keyUrl = keyUrl;
        this.httpClientForSessionToken = httpClientForSessionToken;
        this.httpClientForKeyToken = httpClientForKeyToken;
    }

    /**
     * Authenticate and return session and key tokens encapsulated in SymAuth object.
     *
     * @return SymAuth object containing session and key tokens.
     * @throws AuthenticationException Exception generated from underlying REST API calls.
     */
    public SymAuth authenticate() throws AuthenticationException {


        try {

            AuthenticationApi authenticationApi = getAuthenticationApi();

            // Configure the authenticator connection
            authenticationApi.getApiClient().setBasePath(sessionUrl);

            if (httpClientForSessionToken != null) {
                Configuration.getDefaultApiClient().setHttpClient(httpClientForSessionToken);
            }

            symAuth.setSessionToken(authenticationApi.v1AuthenticatePost());
            logger.debug("SessionToken: {} : {}", symAuth.getSessionToken().getName(), symAuth.getSessionToken().getToken());


            // Configure the keyManager path
            authenticationApi.getApiClient().setBasePath(keyUrl);

            if (httpClientForKeyToken != null) {
                Configuration.getDefaultApiClient().setHttpClient(httpClientForKeyToken);
            }

            symAuth.setKeyToken(authenticationApi.v1AuthenticatePost());
            logger.debug("KeyToken: {} : {}", symAuth.getKeyToken().getName(), symAuth.getKeyToken().getToken());

        } catch (ApiException e) {


            throw new AuthenticationException("Please check certificates, tokens and paths.. ", e.getCode(), e);

        }

        loginStatus = true;
        return symAuth;

    }


    /**
     * Logout from an existing session
     *
     * @param symAuth SymAuth object containing session token to logout
     * @throws AuthenticationException Exception generated from underlying REST API calls.
     */
    public void sessionLogout(SymAuth symAuth) throws AuthenticationException {


        try {

            AuthenticationApi authenticationApi = getAuthenticationApi();

            // Configure the authenticator connection
            authenticationApi.getApiClient().setBasePath(sessionUrl);

            if (httpClientForSessionToken != null) {
                Configuration.getDefaultApiClient().setHttpClient(httpClientForSessionToken);
            }

            authenticationApi.v1LogoutPost(symAuth.getSessionToken().getToken());
            logger.debug("Logged out from session: {} : {}", symAuth.getSessionToken().getName(), symAuth.getSessionToken().getToken());


        } catch (ApiException e) {

            throw new AuthenticationException("Please check if you supplied the correct session token.. ", e.getCode(), e);

        }


    }


    /**
     * Authentication call for Extensions API Applications.  Returns
     *
     * @param appToken Arbitrary application token to assign session.
     * @return SymExtensionAppAuth object containing session token and application ID.
     * @throws AuthenticationException Exception generated from underlying REST API calls.
     */
    public SymExtensionAppAuth authenticateExtensionApp(String appToken) throws AuthenticationException {


        try {


            AuthenticationApi authenticationApi = getAuthenticationApi();

            AuthenticateRequest authenticateRequest = new AuthenticateRequest();
            authenticateRequest.setAppToken(appToken);


            authenticationApi.getApiClient().setBasePath(sessionUrl);

            SymExtensionAppAuth symExtensionAppAuth = SymExtensionAppAuth.toSymExtensionAppAuth(authenticationApi.v1AuthenticateExtensionAppPost(authenticateRequest));
            logger.debug("SymExtensionsAppAuth- AppId: [{}] AppToken: [{}] SymToken: [{}], Expire: [{}]",
                    symExtensionAppAuth.getAppId(),
                    symExtensionAppAuth.getAppToken(),
                    symExtensionAppAuth.getSymphonyToken(),
                    symExtensionAppAuth.getExpireAt());

            return symExtensionAppAuth;

        } catch (ApiException e) {

            throw new AuthenticationException("Please check certificates, tokens and paths.. ", e.getCode(), e);

        }


    }


    /**
     * Authentication call for Applications to be used with OBO workflow.
     *
     *
     * @return SymExtensionAppAuth object containing session token and application ID.
     * @throws AuthenticationException Exception generated from underlying REST API calls.
     */
    public SymAuth authenticateApp() throws AuthenticationException {

        SymAuth symAuth = new SymAuth();

        try {

            AuthenticationApi authenticationApi = getAuthenticationApi();

            // Configure the authenticator connection
            authenticationApi.getApiClient().setBasePath(sessionUrl);

            if (httpClientForSessionToken != null) {
                Configuration.getDefaultApiClient().setHttpClient(httpClientForSessionToken);
            }

            symAuth.setSessionToken(authenticationApi.v1AppAuthenticatePost());
            logger.debug("App SessionToken: {} : {}", symAuth.getSessionToken().getName(), symAuth.getSessionToken().getToken());


//            // Configure the keyManager path
//            authenticationApi.getApiClient().setBasePath(keyUrl);
//
//            if (httpClientForKeyToken != null) {
//                Configuration.getDefaultApiClient().setHttpClient(httpClientForKeyToken);
//            }
//
//            symAuth.setKeyToken(authenticationApi.v1AuthenticatePost());
//            logger.debug("KeyToken: {} : {}", symAuth.getKeyToken().getName(), symAuth.getKeyToken().getToken());

        } catch (ApiException e) {


            throw new AuthenticationException("Please check certificates, tokens and paths.. ", e.getCode(), e);

        }

        loginStatus = true;
        return symAuth;


    }


    /**
     * Authentication for OBO user.
     *
     * @param symUser Symphony user to obtain a session token for.
     * @param symAuth Arbitrary application token to assign session.
     * @return SymExtensionAppAuth object containing session token and application ID.
     * @throws AuthenticationException Exception generated from underlying REST API calls.
     */
    public SymAuth authenticateAppUser(SymUser symUser, SymAuth symAuth) throws AuthenticationException {


        try {

            AuthenticationApi authenticationApi = getAuthenticationApi();
            authenticationApi.getApiClient().setBasePath(sessionUrl);

            if(symUser.getId() != null) {

                return SymAuth.fromOboAuth(authenticationApi.v1AppUserUidAuthenticatePost(symUser.getId(), symAuth.getSessionToken().getToken()));
            }else if(symUser.getUsername() !=  null){

                return SymAuth.fromOboAuth(authenticationApi.v1AppUsernameUsernameAuthenticatePost(symUser.getUsername(), symAuth.getSessionToken().getToken()));
            }

            return null;


        } catch (ApiException e) {

            throw new AuthenticationException("Authentication of App User for OBO failed, please check certificates, tokens and paths.. ", e.getCode(), e);

        }


    }

    /**
     * Force registration of certificate stores
     *
     * @param serverTruststore Truststore file containing root and chain certs
     * @param truststorePass   Truststore password
     * @param clientKeystore   Client certificate keystore (P12) containing CN= bot user name
     * @param keystorePass     Client keystore password
     */
    public void setKeystores(String serverTruststore, String truststorePass, String clientKeystore, String keystorePass) {


        System.setProperty("javax.net.ssl.trustStore", serverTruststore);

        if (truststorePass != null)
            System.setProperty("javax.net.ssl.trustStorePassword", truststorePass);
        System.setProperty("javax.net.ssl.keyStore", clientKeystore);
        System.setProperty("javax.net.ssl.keyStorePassword", keystorePass);
        System.setProperty("javax.net.ssl.keyStoreType", "pkcs12");


    }


    /**
     * Retrieve an instance of the AuthenticationApi
     *
     * @return AuthenticationApi AuthenticationApi instance
     * @throws AuthenticationException Exception generated from underlying REST API calls.
     */
    private AuthenticationApi getAuthenticationApi() throws AuthenticationException {

        if (sessionUrl == null || keyUrl == null)
            throw new NullPointerException("Session URL or Keystore URL is null..");


        symAuth = new SymAuth();
        org.symphonyoss.symphony.authenticator.invoker.ApiClient authenticatorClient = Configuration.getDefaultApiClient();


        //Need this for refresh
        symAuth.setKeyUrl(keyUrl);
        symAuth.setSessionUrl(sessionUrl);


        if (httpClient != null) {
            authenticatorClient.setHttpClient(httpClient);
            symAuth.setHttpClient(httpClient);

        } else {

            //Lets copy the pki info..
            symAuth.setServerTruststore(System.getProperty("javax.net.ssl.trustStore"));
            symAuth.setServerTruststorePassword(System.getProperty("javax.net.ssl.trustStorePassword"));
            symAuth.setClientKeystore(System.getProperty("javax.net.ssl.keyStore"));
            symAuth.setClientKeystorePassword(System.getProperty("javax.net.ssl.keyStorePassword"));

        }


        // Get the authentication API
        return new AuthenticationApi(authenticatorClient);


    }


    public boolean isLoggedIn() {
        return loginStatus;
    }

    public Token getKeyToken() {
        return symAuth.getKeyToken();
    }

    public void setKeyToken(Token keyToken) {
        symAuth.setKeyToken(keyToken);
    }

    public Token getSessionToken() {
        return symAuth.getSessionToken();
    }

    @SuppressWarnings("unused")
    public void setSessionToken(Token sessionToken) {
        symAuth.setSessionToken(sessionToken);
    }
}
