/*
 *
 *  *
 *  * Copyright 2016 The Symphony Software Foundation
 *  *
 *  * Licensed to The Symphony Software Foundation (SSF) under one
 *  * or more contributor license agreements.  See the NOTICE file
 *  * distributed with this work for additional information
 *  * regarding copyright ownership.  The ASF licenses this file
 *  * to you under the Apache License, Version 2.0 (the
 *  * "License"); you may not use this file except in compliance
 *  * with the License.  You may obtain a copy of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *
 */

package org.symphonyoss.symphony.agent.api;

import org.symphonyoss.symphony.agent.invoker.ApiClient;
import org.symphonyoss.symphony.agent.invoker.ApiException;
import org.symphonyoss.symphony.agent.invoker.Configuration;
import org.symphonyoss.symphony.agent.invoker.Pair;
import org.symphonyoss.symphony.agent.model.Firehose;
import org.symphonyoss.symphony.agent.model.MessageList;
import org.symphonyoss.symphony.agent.model.V2MessageList;

import javax.ws.rs.core.GenericType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2016-05-23T06:12:55.304-04:00")
public class FirehoseApi {
  private ApiClient apiClient;

  public FirehoseApi() {
    this(Configuration.getDefaultApiClient());
  }

  public FirehoseApi(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  public ApiClient getApiClient() {
    return apiClient;
  }

  public void setApiClient(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  
  /**
   * LIMITED RELEASE Create a new real time firehose.
   * A firehose provides all user entered messages that occur within a\nSymphony pod beginning at the point in time that it is created.\n\nNote that unlike a datafeed, the user calling a firehose does not need to be a member of the conversations\nfor which it will receive messages.\n\nSystem messages like new users joining a chatroom are not part of the firehose.\n\nA firehose can only be created and accessed by a user with the content export role.\n\nA firehose will expire if its capacity for unread messages is reached.\n
   * @param sessionToken Session authentication token. (required)
   * @param keyManagerToken Key Manager authentication token. (required)
   * @return Firehose
   * @throws ApiException if fails to make API call
   */
  public Firehose v1FirehoseCreatePost(String sessionToken, String keyManagerToken) throws ApiException {
    Object localVarPostBody = null;
    
    // verify the required parameter 'sessionToken' is set
    if (sessionToken == null) {
      throw new ApiException(400, "Missing the required parameter 'sessionToken' when calling v1FirehoseCreatePost");
    }
    
    // verify the required parameter 'keyManagerToken' is set
    if (keyManagerToken == null) {
      throw new ApiException(400, "Missing the required parameter 'keyManagerToken' when calling v1FirehoseCreatePost");
    }
    
    // create path and map variables
    String localVarPath = "/v1/firehose/create".replaceAll("\\{format\\}","json");

    // query params
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();

    

    if (sessionToken != null)
      localVarHeaderParams.put("sessionToken", apiClient.parameterToString(sessionToken));
    if (keyManagerToken != null)
      localVarHeaderParams.put("keyManagerToken", apiClient.parameterToString(keyManagerToken));
    

    

    final String[] localVarAccepts = {
      "application/json"
    };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);

    final String[] localVarContentTypes = {
      "application/json"
    };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] {  };

    
    GenericType<Firehose> localVarReturnType = new GenericType<Firehose>() {};
    return apiClient.invokeAPI(localVarPath, "POST", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    
  }
  
  /**
   * LIMITED RELEASE Read a given firehose.
   * Read messages from the given firehose. If no more messages are available then this method will block.\nIt is intended that the client should re-call this method as soon as it has processed the messages\nreceived in the previous call. If the client is able to consume messages more quickly than they become\navailable then each call will initially block, there is no need to delay before re-calling this method.\n\nA firehose can only be read by a user with the content export role.\n\nA firehose will expire if its unread capacity is reached.\n\nA firehose can only be consumed by one client thread at a time. E.g. polling the firehose by two threads may lead to messages being delivered out of order.\n
   * @param id Firehose ID\n (required)
   * @param sessionToken Session authentication token. (required)
   * @param keyManagerToken Key Manager authentication token. (required)
   * @param maxMessages Max No. of messages to return.\n (optional)
   * @return MessageList
   * @throws ApiException if fails to make API call
   */
  public MessageList v1FirehoseIdReadGet(String id, String sessionToken, String keyManagerToken, Integer maxMessages) throws ApiException {
    Object localVarPostBody = null;
    
    // verify the required parameter 'id' is set
    if (id == null) {
      throw new ApiException(400, "Missing the required parameter 'id' when calling v1FirehoseIdReadGet");
    }
    
    // verify the required parameter 'sessionToken' is set
    if (sessionToken == null) {
      throw new ApiException(400, "Missing the required parameter 'sessionToken' when calling v1FirehoseIdReadGet");
    }
    
    // verify the required parameter 'keyManagerToken' is set
    if (keyManagerToken == null) {
      throw new ApiException(400, "Missing the required parameter 'keyManagerToken' when calling v1FirehoseIdReadGet");
    }
    
    // create path and map variables
    String localVarPath = "/v1/firehose/{id}/read".replaceAll("\\{format\\}","json")
      .replaceAll("\\{" + "id" + "\\}", apiClient.escapeString(id.toString()));

    // query params
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();

    
    localVarQueryParams.addAll(apiClient.parameterToPairs("", "maxMessages", maxMessages));
    

    if (sessionToken != null)
      localVarHeaderParams.put("sessionToken", apiClient.parameterToString(sessionToken));
    if (keyManagerToken != null)
      localVarHeaderParams.put("keyManagerToken", apiClient.parameterToString(keyManagerToken));
    

    

    final String[] localVarAccepts = {
      "application/json"
    };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);

    final String[] localVarContentTypes = {
      "application/json"
    };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] {  };

    
    GenericType<MessageList> localVarReturnType = new GenericType<MessageList>() {};
    return apiClient.invokeAPI(localVarPath, "GET", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    
  }
  
  /**
   * LIMITED RELEASE Read a given firehose.
   * Read messages from the given firehose. If no more messages are available then this method will block.\nIt is intended that the client should re-call this method as soon as it has processed the messages\nreceived in the previous call. If the client is able to consume messages more quickly than they become\navailable then each call will initially block, there is no need to delay before re-calling this method.\n\nA firehose can only be read by a user with the content export role.\n\nA firehose will expire if its unread capacity is reached.\n\nA firehose can only be consumed by one client thread at a time. E.g. polling the firehose by two threads may lead to messages being delivered out of order.\n
   * @param id Firehose ID\n (required)
   * @param sessionToken Session authentication token. (required)
   * @param keyManagerToken Key Manager authentication token. (required)
   * @param maxMessages Max No. of messages to return.\n (optional)
   * @return V2MessageList
   * @throws ApiException if fails to make API call
   */
  public V2MessageList v2FirehoseIdReadGet(String id, String sessionToken, String keyManagerToken, Integer maxMessages) throws ApiException {
    Object localVarPostBody = null;
    
    // verify the required parameter 'id' is set
    if (id == null) {
      throw new ApiException(400, "Missing the required parameter 'id' when calling v2FirehoseIdReadGet");
    }
    
    // verify the required parameter 'sessionToken' is set
    if (sessionToken == null) {
      throw new ApiException(400, "Missing the required parameter 'sessionToken' when calling v2FirehoseIdReadGet");
    }
    
    // verify the required parameter 'keyManagerToken' is set
    if (keyManagerToken == null) {
      throw new ApiException(400, "Missing the required parameter 'keyManagerToken' when calling v2FirehoseIdReadGet");
    }
    
    // create path and map variables
    String localVarPath = "/v2/firehose/{id}/read".replaceAll("\\{format\\}","json")
      .replaceAll("\\{" + "id" + "\\}", apiClient.escapeString(id.toString()));

    // query params
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();

    
    localVarQueryParams.addAll(apiClient.parameterToPairs("", "maxMessages", maxMessages));
    

    if (sessionToken != null)
      localVarHeaderParams.put("sessionToken", apiClient.parameterToString(sessionToken));
    if (keyManagerToken != null)
      localVarHeaderParams.put("keyManagerToken", apiClient.parameterToString(keyManagerToken));
    

    

    final String[] localVarAccepts = {
      "application/json"
    };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);

    final String[] localVarContentTypes = {
      "application/json"
    };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] {  };

    
    GenericType<V2MessageList> localVarReturnType = new GenericType<V2MessageList>() {};
    return apiClient.invokeAPI(localVarPath, "GET", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    
  }
  
}
