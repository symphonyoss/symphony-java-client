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

package org.symphonyoss.symphony.clients.model;

import org.symphonyoss.symphony.pod.model.UserV2;

/**
 * Created by frank.tarsillo on 9/8/2016.
 */
public class SymUser extends UserV2 {

   public static UserV2 toUserV2(SymUser symUser){
       UserV2 userV2 = new UserV2();

       userV2.setAvatars(symUser.getAvatars());
       userV2.setCompany(symUser.getCompany());
       userV2.setDisplayName(symUser.getDisplayName());
       userV2.setEmailAddress(symUser.getEmailAddress());
       userV2.setFirstName(symUser.getFirstName());
       userV2.setLastName(symUser.getLastName());
       userV2.setId(symUser.getId());
       userV2.setLocation(symUser.getLocation());
       userV2.setTitle(symUser.getTitle());
       userV2.setUsername(symUser.getUsername());
       return userV2;
   }


    public static SymUser toSymUser(UserV2 user) {



        SymUser symUser = new SymUser();

        symUser.setAvatars(user.getAvatars());
        symUser.setCompany(user.getCompany());
        symUser.setDisplayName(user.getDisplayName());
        symUser.setEmailAddress(user.getEmailAddress());
        symUser.setFirstName(user.getFirstName());
        symUser.setLastName(user.getLastName());
        symUser.setId(user.getId());
        symUser.setLocation(user.getLocation());
        symUser.setTitle(user.getTitle());
        symUser.setUsername(user.getUsername());
        return symUser;

    }
}
