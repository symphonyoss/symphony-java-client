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
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.symphonyoss.client.exceptions;


public class RestException extends NetworkException {
    private static final long serialVersionUID = 1L;

    private final int httpStatus;
    
    public RestException(String message, String endpoint, int httpStatus) {
        super(message, endpoint);
        this.httpStatus = httpStatus;
    }

    public RestException(String endpoint, int httpStatus, Throwable cause) {
        super(endpoint, cause);
        this.httpStatus = httpStatus;
    }

    public RestException(String message, String endpoint, int httpStatus, Throwable cause) {
        super(message, endpoint, cause);
        this.httpStatus = httpStatus;
    }

    public RestException(String message, RestException cause) {
    	super(message, cause.getEndpoint(), cause);
        this.httpStatus = cause.getHttpStatus();
	}
    
    public RestException(RestException cause) {
    	super(cause.getEndpoint(), cause);
        this.httpStatus = cause.getHttpStatus();
	}

	public int getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
	return super.getMessage() +
		"\nendpoint " + getEndpoint() +
		"\nhttpStatus " + httpStatus;
    }
}
