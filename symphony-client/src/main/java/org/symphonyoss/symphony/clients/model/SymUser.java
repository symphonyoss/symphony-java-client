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

import org.symphonyoss.symphony.agent.model.V4User;
import org.symphonyoss.symphony.pod.model.Avatar;
import org.symphonyoss.symphony.pod.model.AvatarList;
import org.symphonyoss.symphony.pod.model.FeatureList;
import org.symphonyoss.symphony.pod.model.StringList;
import org.symphonyoss.symphony.pod.model.UserAttributes;
import org.symphonyoss.symphony.pod.model.UserDetail;
import org.symphonyoss.symphony.pod.model.UserSystemInfo;
import org.symphonyoss.symphony.pod.model.UserV2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 *
 * Model describing Symphony User
 *
 * @author Frank Tarsillo
 */
@SuppressWarnings("WeakerAccess")
public class SymUser {

    private Long id;

    private String emailAddress;

    private String firstName;

    private String lastName;

    private String department;

    private String displayName;

    private String title;

    private String company;

    private String username;

    private String location;

    private List<SymAvatar> avatars;

    private Date lastLoginDate;

    private Date createdDate;

    private boolean active;

    private Set<String> roles;

    private FeatureList features;

    /**
     * Retrieves the id of a Symphony User.
     * @return id the id of the Symphony User.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the id for a Symphony User.
     * @param id the id to set for the Symphony User
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retrieves the email address of a Symphony User.
     * @return emailAddress the email address of a Symphony User.
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * Sets the email address for a Symphony User.
     * @param emailAddress the email address to set for a Symphony User.
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**
     * Retrieves the first name of a Symphony User.
     * @return firstName the first name of a Symphony User.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name for a Symphony User.
     * @param firstName the first name to set for a Symphony User.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Retrieves the last name of a Symphony User.
     * @return lastName the last name of a Symphony User.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name of a Symphony User.
     * @param lastName the last name to set for a Symphony User.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Retrieves the display/pretty name of a Symphony User.
     * @return displayName the display name of a Symphony User.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Sets the display name of a Symphony User.
     * @param displayName the display name to set for a Symphony User.
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Retrieves the department name of a Symphony User.
     * @return departmentName the department name of a Symphony User.
     */
    public String getDepartment() {
        return department;
    }

    /**
     * Sets the department name of a Symphony User.
     * @param department the department name to set for a Symphony User.
     */
    public void setDepartment(String department) {
        this.department = department;
    }

    /**
     * Retrieves the title of a Symphony User..
     * @return title the title of a Symphony User.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of a Symphony User.
     * @param title the title to set for a Symphony User.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Retrieves the company of a Symphony User.
     * @return company the company of a Symphony User.
     */
    public String getCompany() {
        return company;
    }

    /**
     * Sets the company of a Symphony User.
     * @param company the company to set for a Symphony User.
     */
    public void setCompany(String company) {
        this.company = company;
    }

    /**
     * Retrieves the username of a Symphony User.
     * @return username the username of a Symphony User.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of a Symphony User.
     * @param username the username to set for a Symphony User.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Retrieves the location of a Symphony User.
     * @return location the location of a Symphony User.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the location of a Symphony User.
     * @param location the location to set for a Symphony User.
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Retrieves the avatars of a Symphony User.
     * @return avatars the avatars of a Symphony User.
     */
    public List<SymAvatar> getAvatars() {
        return avatars;
    }

    /**
     * Sets the avatars of a Synphony User.
     * @param avatars the avatars to set for a Symphony User.
     */
    public void setAvatars(List<SymAvatar> avatars) {
        this.avatars = avatars;
    }

    /**
     * Retrieves the last login date of a Symphony User.
     * @return lastLoginDate the last login date of a Symphony User.
     */
    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    /**
     * Sets the last login date of a Symphony User.
     * @param lastLoginDate the last login date to set for a Symphony User.
     */
    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    /**
     * Retrieves the creation date of a Symphony User.
     * @return createdDate the creation date of a Symphony User.
     */
    public Date getCreatedDate() {
        return createdDate;
    }

    /**
     * Sets the creation date of a Symphony User.
     * @param createdDate the creation date to set for a Symphony User.
     */
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * Checks whether a Symphony User is active.
     * @return active true if the Symphony User is active, false if otherwise.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active status of a Symphony User.
     * @param active the active status to set for a Symphony User. Set to true if user is active, false if otherwise.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Retrieves a set of roles of a Symphony User.
     * @return roles the set of roles of a Symphony User.
     */
    public Set<String> getRoles() {
        return roles;
    }

    /**
     * Sets the roles of a Symphony User.
     * @param roles the set of roles to set for a Symphony User.
     */
    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    /**
     * Retrieves the {@link FeatureList} of a Symphony User.
     * @return the features, e.g. isExternalAccessEnabled
     */
    public FeatureList getFeatures() {
        return features;
    }

    /**
     * The {@link FeatureList} to set for a Symphony User.
     * @param features the features to set for a Symphony User.
     */
    public void setFeatures(FeatureList features) {
        this.features = features;
    }

    /**
     * The method convert a {@link SymUser} to a {@link UserV2}.
     * @param symUser the {@link SymUser} to convert from.
     * @return userV2 the {@link UserV2} generated from the {@link SymUser}.
     */
    @SuppressWarnings("unused")
    public static UserV2 toUserV2(SymUser symUser) {

        if(symUser==null)
            return null;

        UserV2 userV2 = new UserV2();


        userV2.setCompany(symUser.getCompany());
        userV2.setDisplayName(symUser.getDisplayName());
        userV2.setEmailAddress(symUser.getEmailAddress());
        userV2.setFirstName(symUser.getFirstName());
        userV2.setLastName(symUser.getLastName());
        userV2.setId(symUser.getId());
        userV2.setLocation(symUser.getLocation());
        userV2.setTitle(symUser.getTitle());
        userV2.setUsername(symUser.getUsername());
        userV2.setDepartment(symUser.getDepartment());

        AvatarList avatars = new AvatarList();
        for (SymAvatar symAvatar : symUser.getAvatars()) {
            Avatar avatar = new Avatar();
            avatar.setSize(symAvatar.getSize());
            avatar.setUrl(symAvatar.getUrl());
            avatars.add(avatar);
        }
        userV2.setAvatars(avatars);

        return userV2;
    }

    /**
     * This method converts a {@link UserV2} to a {@link SymUser}.
     * @param user the {@link UserV2} to convert from.
     * @return symUser the {@link SymUser} generated from the {@link UserV2}.
     */
    public static SymUser toSymUser(UserV2 user) {
        if(user==null)
            return null;

        SymUser symUser = new SymUser();


        symUser.setCompany(user.getCompany());
        symUser.setDisplayName(user.getDisplayName());
        symUser.setEmailAddress(user.getEmailAddress());
        symUser.setFirstName(user.getFirstName());
        symUser.setLastName(user.getLastName());
        symUser.setId(user.getId());
        symUser.setLocation(user.getLocation());
        symUser.setTitle(user.getTitle());
        symUser.setUsername(user.getUsername());
        symUser.setDepartment(user.getDepartment());


        List<SymAvatar> avatars = new ArrayList<>();
        for (Avatar avatar : user.getAvatars()) {
            SymAvatar symAvatar = new SymAvatar();
            symAvatar.setSize(avatar.getSize());
            symAvatar.setUrl(avatar.getUrl());
            avatars.add(symAvatar);
        }
        symUser.setAvatars(avatars);


        return symUser;

    }

    /**
     *  This method converts a {@link V4User} to a {@link SymUser}.
     * @param user the {@link V4User}  to convert from.
     * @return symUser the {@link SymUser} generated from {@link UserDetail}.
     */
    public static SymUser toSymUser(V4User user) {

        if(user==null)
            return null;

        SymUser symUser = new SymUser();


        symUser.setDisplayName(user.getDisplayName());
        symUser.setEmailAddress(user.getEmail());
        symUser.setFirstName(user.getFirstName());
        symUser.setLastName(user.getLastName());
        symUser.setId(user.getUserId());
        symUser.setUsername(user.getUsername());
        symUser.setDepartment(null);

        return symUser;

    }

    /**
     * This method takes a {@link UserDetail} and converts it into {@link SymUser}.
     * <b>NOTE:</b> Avatars and Company are not exposed by UserDetail
     * and so are not set as part of this implementation
     *
     * @param userDetail User detail
     * @return symUser User object
     */
    public static SymUser toSymUser(UserDetail userDetail) {
        if (userDetail == null) {
            throw new IllegalStateException("User Detail must not be null");
        }
        UserAttributes userAttributes = userDetail.getUserAttributes();
        if (userAttributes == null) {
            throw new IllegalStateException("User Attributes must not be null");
        }
        UserSystemInfo userSystemInfo = userDetail.getUserSystemInfo();
        if (userSystemInfo == null) {
            throw new IllegalStateException("User System Info must not be null");
        }

        SymUser symUser = new SymUser();

        symUser.setDisplayName(userAttributes.getDisplayName());
        symUser.setEmailAddress(userAttributes.getEmailAddress());
        symUser.setFirstName(userAttributes.getFirstName());
        symUser.setLastName(userAttributes.getLastName());
        symUser.setId(userSystemInfo.getId());
        symUser.setLocation(userAttributes.getLocation());
        symUser.setTitle(userAttributes.getTitle());
        symUser.setUsername(userAttributes.getUserName());
        symUser.setDepartment(userAttributes.getDepartment());

        Long lastLoginDate = userSystemInfo.getLastLoginDate();
        if (null != lastLoginDate) {
            symUser.setLastLoginDate(new Date(lastLoginDate));
        } else {
            symUser.setLastLoginDate(null);
        }

        Long createdDate = userSystemInfo.getCreatedDate();
        if (null != createdDate) {
            symUser.setCreatedDate(new Date(createdDate));
        } else {
            symUser.setCreatedDate(null);
        }

        symUser.setActive(UserSystemInfo.StatusEnum.ENABLED == userSystemInfo.getStatus());

        StringList roles = userDetail.getRoles();
        if (null != roles) {
            symUser.setRoles(new HashSet<>(roles));
        } else {
            symUser.setRoles(new HashSet<>());
        }

        return symUser;
    }

    /**
     * This method takes a SymUser and converts it into UserAttributes.
     * <b>NOTE:</b> department is not part of SymUser and needs to be implemented
     * and so is set to null
     *
     * @param symUser User object
     * @return userAttributes User attributes
     */
    public static UserAttributes toUserAttributes(SymUser symUser) {
        if (symUser == null) {
            throw new IllegalStateException("symUser must not be null");
        }

        UserAttributes userAttributes = new UserAttributes();

        userAttributes.setDisplayName(symUser.getDisplayName());
        userAttributes.setEmailAddress(symUser.getEmailAddress());
        userAttributes.setFirstName(symUser.getFirstName());
        userAttributes.setLastName(symUser.getLastName());
        userAttributes.setLocation(symUser.getLocation());
        userAttributes.setTitle(symUser.getTitle());
        userAttributes.setUserName(symUser.getUsername());
        userAttributes.setDepartment(symUser.getDepartment());

        return userAttributes;
    }


    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof SymUser)) {
            return false;
        }
        SymUser symUser = (SymUser) o;
        return id.equals(symUser.id);
    }

}
