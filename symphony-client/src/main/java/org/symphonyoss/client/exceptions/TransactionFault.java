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
 * A fault which prevents the API from completing the current task.
 * 
 * A reasonable response to such a fault is to terminate the current
 * "business transaction", the name transaction does not refer to
 * a database transaction in this instance.
 * 
 * @author bruce.skingle
 *
 */
public class TransactionFault extends SymFault {
    private static final long serialVersionUID = 1L;
    
    public TransactionFault() {
    }

    public TransactionFault(String message) {
	super(message);
    }

    public TransactionFault(Throwable cause) {
	super(cause);
    }

    public TransactionFault(String message, Throwable cause) {
	super(message, cause);
    }

    public TransactionFault(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
	super(message, cause, enableSuppression, writableStackTrace);
    }

}
