/*
 *
 *
 * Copyright 2017 The Symphony Software Foundation
 *
 * Licensed to The Symphony Software Foundation (SSF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/**
 * Exceptions and Faults.
 * 
 * @author bruce.skingle
 *
 * Classes in this package with names ending Exception are sub-classes of
 * SymException which itself extends java.lang.Exception, they are
 * therefore checked exceptions, and represent exceptional
 * return values from API methods.
 * 
 * Classes with names ending in Fault are sub-classes of
 * SymFault which itself extends java.lang.RuntimeException, they are
 * therefore unchecked exceptions, and represent unexpected faults which
 * the caller could not have reasonably avoided and which they
 * should not be required to handle.
 * 
 * Calling programs should establish a "fault barrier" where these
 * faults are caught and handled, and they should generally not be
 * caught from any other code.
 * 
 * A stand alone program may have no
 * fault barrier with the effect that all faults cause program 
 * termination.
 * 
 * A program with a thread pool executor might have a fault barrier
 * at the thread pool so that a fault causes the current thread 
 * processing to terminate, similarly for a web server etc.
 * 
 * See http://www.oracle.com/technetwork/articles/entarch/effective-exceptions2-097044.html
 * 
 * NetworkException has an endpoint (String intended to contain a URL)
 * which is extended by Exceptions related to network errors and
 * RestException extends NetworkException and adds an HTTP status code.
 * 
 * Where an error occurs in a REST call a RestException is created
 * containing the network endpoint (URL) and HTTP status code received
 * and this is added to the exception chain.
 */
package org.symphonyoss.client.exceptions;