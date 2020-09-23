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
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package org.symphonyoss.symphony.clients;

import org.symphonyoss.symphony.agent.api.AttachmentsApi;
import org.symphonyoss.symphony.agent.api.DatafeedApi;
import org.symphonyoss.symphony.agent.api.MessagesApi;
import org.symphonyoss.symphony.agent.api.ShareApi;
import org.symphonyoss.symphony.agent.api.SignalsApi;
import org.symphonyoss.symphony.pod.api.*;

/**
 * @author Frank Tarsillo on 10/15/17.
 */
public interface SymphonyApis {
    AttachmentsApi getAttachmentsApi();

    ConnectionApi getConnectionApi();

    DatafeedApi getDatafeedApi();

    MessagesApi getMessagesApi();

    PresenceApi getPresenceApi();

    RoomMembershipApi getRoomMembershipApi();

    StreamsApi getStreamsApi();

    UsersApi getUsersApi();

    UserApi getUserApi();

    ShareApi getShareApi();

    SystemApi getPodSystemApi();

    org.symphonyoss.symphony.agent.api.SystemApi getAgentSystemApi();

    AppEntitlementApi getAppEntitlementApi();

    ApplicationApi getApplicationApi();

    DisclaimerApi getDisclaimerApi();

    InfoBarriersApi getInfoBarriersApi();

    MessageSuppressionApi getMessageSuppressionApi();

    SecurityApi getSecurityApi();

    SessionApi getSessionApi();
    
    SignalsApi getSignalsApi();
}
