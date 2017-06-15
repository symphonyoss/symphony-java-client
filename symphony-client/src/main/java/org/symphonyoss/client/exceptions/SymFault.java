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
 * A generic unexpected fault condition, not thrown directly but useful
 * in cases where a caller wished to catch all API fault conditions.
 * 
 * Faults are RuntimeExceptions and are unchecked. There is not expected
 * to be any obvious action that the caller can take which would allow a
 * further attempt to perform the action to succeed.
 * 
 * @author bruce.skingle
 *
 */
public abstract class SymFault extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public SymFault() {
    }

    public SymFault(String message) {
	super(message);
    }

    public SymFault(Throwable cause) {
	super(cause);
    }

    public SymFault(String message, Throwable cause) {
	super(message, cause);
    }

    public SymFault(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
	super(message, cause, enableSuppression, writableStackTrace);
    }

}
