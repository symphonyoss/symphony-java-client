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

package org.symphonyoss.client.exceptions;

/**
 * An exception relating to network connectivity with the Symphony
 * Pod or other remote component.
 * 
 * @author bruce.skingle
 *
 */
public class NetworkException extends SymException {
    private static final long serialVersionUID = 1L;
    
    private final String endpoint;
    
    public NetworkException(String message, String endpoint) {
	super(message);
	this.endpoint = endpoint;
    }

    public NetworkException(String endpoint, Throwable cause) {
	super(cause);
	this.endpoint = endpoint;
    }

    public NetworkException(String message, String endpoint, Throwable cause) {
	super(message, cause);

	this.endpoint = endpoint;
    }

    public String getEndpoint() {
        return endpoint;
    }

    @Override
    public String getMessage() {
	return super.getMessage() + " to " + endpoint;
    }

}
