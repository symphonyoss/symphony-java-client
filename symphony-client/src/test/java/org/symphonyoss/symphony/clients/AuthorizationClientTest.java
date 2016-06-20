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

package org.symphonyoss.symphony.clients;

import org.junit.Before;
import org.junit.Test;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.util.TestFactory;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Frank Tarsillo on 6/19/2016.
 */
public class AuthorizationClientTest {

    AuthorizationClient authorizationClient;

    @Before
    public void beforeTest(){


        authorizationClient = mock(AuthorizationClient.class);

    try {
        when(authorizationClient.authenticate()).thenReturn(TestFactory.getSymAuth());
        when(authorizationClient.getKeyToken()).thenReturn(TestFactory.getSymAuth().getKeyToken());
        when(authorizationClient.getSessionToken()).thenReturn(TestFactory.getSymAuth().getSessionToken());

    }catch(Exception e){
        fail("Could not setup authenticate");
    }

    }

    @Test
    public void authenticate() throws Exception {

        AuthorizationClient authorizationClientReal = new AuthorizationClient("AUTHURL","KEYURL");

        authorizationClient.setKeystores("/dir/file.trustore","trustpass","/dir/client.keystore","keystorepass");

        assertTrue("Verify authenticate..", authorizationClient.authenticate() != null);


    }


    @Test
    public void isLoggedIn() throws Exception {

        assertEquals(false,authorizationClient.isLoggedIn());

    }

    @Test
    public void getKeyToken() throws Exception {
        assertEquals("TOKEN_VALUE", authorizationClient.authenticate().getKeyToken().getToken());

    }

    @Test
    public void setKeyToken() throws Exception {

        authorizationClient.setKeyToken(TestFactory.getSymAuth().getKeyToken());
        assertEquals("TOKEN_VALUE", authorizationClient.authenticate().getKeyToken().getToken());
    }

    @Test
    public void getSessionToken() throws Exception {
        assertEquals("TOKEN_VALUE", authorizationClient.authenticate().getSessionToken().getToken());
    }

    @Test
    public void setSessionToken() throws Exception {
        authorizationClient.setKeyToken(TestFactory.getSymAuth().getSessionToken());
        assertEquals("TOKEN_VALUE", authorizationClient.authenticate().getSessionToken().getToken());
    }

}