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
import org.symphonyoss.util.TestFactory;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Frank Tarsillo on 6/19/2016.
 */
public class AuthenticationClientTest {

    private AuthenticationClient authenticationClient;

    @Before
    public void beforeTest(){


        authenticationClient = mock(AuthenticationClient.class);

    try {
        when(authenticationClient.authenticate()).thenReturn(TestFactory.getSymAuth());
        when(authenticationClient.getKeyToken()).thenReturn(TestFactory.getSymAuth().getKeyToken());
        when(authenticationClient.getSessionToken()).thenReturn(TestFactory.getSymAuth().getSessionToken());

    }catch(Exception e){
        fail("Could not setup authenticate");
    }

    }

    @Test
    public void authenticate() throws Exception {

        //AuthenticationClient authorizationClientReal = new AuthenticationClient("AUTHURL","KEYURL");

        authenticationClient.setKeystores("/dir/file.trustore","trustpass","/dir/client.keystore","keystorepass");

        assertTrue("Verify authenticate..", authenticationClient.authenticate() != null);


    }


    @Test
    public void isLoggedIn() throws Exception {

        assertEquals(false, authenticationClient.isLoggedIn());

    }

    @Test
    public void getKeyToken() throws Exception {
        assertEquals("TOKEN_VALUE", authenticationClient.authenticate().getKeyToken().getToken());

    }

    @Test
    public void setKeyToken() throws Exception {

        authenticationClient.setKeyToken(TestFactory.getSymAuth().getKeyToken());
        assertEquals("TOKEN_VALUE", authenticationClient.authenticate().getKeyToken().getToken());
    }

    @Test
    public void getSessionToken() throws Exception {
        assertEquals("TOKEN_VALUE", authenticationClient.authenticate().getSessionToken().getToken());
    }

    @Test
    public void setSessionToken() throws Exception {
        authenticationClient.setKeyToken(TestFactory.getSymAuth().getSessionToken());
        assertEquals("TOKEN_VALUE", authenticationClient.authenticate().getSessionToken().getToken());
    }

}