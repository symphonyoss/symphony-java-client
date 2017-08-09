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

package org.symphonyoss.symphony.clients.model;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.symphonyoss.symphony.pod.model.FeatureList;
import org.symphonyoss.symphony.pod.model.StringList;
import org.symphonyoss.symphony.pod.model.UserAttributes;
import org.symphonyoss.symphony.pod.model.UserDetail;
import org.symphonyoss.symphony.pod.model.UserSystemInfo;
import org.symphonyoss.symphony.pod.model.UserSystemInfo.StatusEnum;

/**
 * Model describing a Symphony User as a complement to {@link SymUser}.
 */
public class SymUserWithDetails {
    private String userName;
    private String emailAddress;
    private String prettyName;
    private String firstName;
    private String lastName;
    private String departmentName;
    private String location;
    private Date lastLoginDate;
    private Date createdDate;
    private boolean active;
    private Set<String> roles;
    private FeatureList features;

    /**
     * Retrieves the username of a Symphony User.
     * @return userName the username of the Symphony User.
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the username for a Symphony User.
     * @param userName the username to set for the Symphony User.
     */
    public void setUserName(String userName) {
        this.userName = userName;
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
     * Retrieves the display/pretty name of a Symphony User.
     * @return prettyName the display name of a Symphony User.
     */
    public String getPrettyName() {
        return prettyName;
    }

    /**
     * Sets the display name of a Symphony User.
     * @param prettyName the display name to set for a Symphony User.
     */
    public void setPrettyName(String prettyName) {
        this.prettyName = prettyName;
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
     * Retrieves the department name of a Symphony User.
     * @return departmentName the department name of a Symphony User.
     */
    public String getDepartmentName() {
        return departmentName;
    }

    /**
     * Sets the department name name of a Symphony User.
     * @param departmentName the department name to set for a Symphony User.
     */
    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
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
     * Converts a {@link UserDetail} to a {@link SymUserWithDetails}.
     * @param userDetail the {@link UserDetail} to convert from.
     * @return symUserWithDetails the {@link SymUserWithDetails} generated from {@link UserDetail}.
     */
    public static SymUserWithDetails toSymUserWithDetails(UserDetail userDetail) {
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

        SymUserWithDetails symUserWithDetails = new SymUserWithDetails();

        symUserWithDetails.setUserName(userAttributes.getUserName());
        symUserWithDetails.setEmailAddress(userAttributes.getEmailAddress());
        symUserWithDetails.setPrettyName(userAttributes.getDisplayName());
        symUserWithDetails.setFirstName(userAttributes.getFirstName());
        symUserWithDetails.setLastName(userAttributes.getLastName());
        symUserWithDetails.setDepartmentName(userAttributes.getDepartment());
        symUserWithDetails.setLocation(userAttributes.getLocation());

        Long lastLoginDate = userSystemInfo.getLastLoginDate();
        if (null != lastLoginDate) {
            symUserWithDetails.setLastLoginDate(new Date(lastLoginDate));
        } else {
            symUserWithDetails.setLastLoginDate(new Date(0));
        }

        Long createdDate = userSystemInfo.getCreatedDate();
        if (null != createdDate) {
            symUserWithDetails.setCreatedDate(new Date(createdDate));
        } else {
            symUserWithDetails.setCreatedDate(new Date(0));
        }

        symUserWithDetails.setActive(StatusEnum.ENABLED == userSystemInfo.getStatus());

        StringList roles = userDetail.getRoles();
        if (null != roles) {
            symUserWithDetails.setRoles(new HashSet<>(roles));
        } else {
            symUserWithDetails.setRoles(Collections.emptySet());
        }

        return symUserWithDetails;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName, emailAddress, prettyName, firstName, lastName,
                departmentName, location, lastLoginDate, createdDate, active, roles, features);
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        } else if (getClass() != obj.getClass()) {
            return false;
        } else {
            final SymUserWithDetails other = (SymUserWithDetails) obj;
            return Objects.equals(this.userName, other.userName)
                    && Objects.equals(this.emailAddress, other.emailAddress)
                    && Objects.equals(this.prettyName, other.prettyName)
                    && Objects.equals(this.firstName, other.firstName)
                    && Objects.equals(this.lastName, other.lastName)
                    && Objects.equals(this.departmentName, other.departmentName)
                    && Objects.equals(this.location, other.location)
                    && Objects.equals(this.lastLoginDate, other.lastLoginDate)
                    && Objects.equals(this.createdDate, other.createdDate)
                    && Objects.equals(this.active, other.active)
                    && Objects.equals(this.roles, other.roles)
                    && Objects.equals(this.features, other.features);
        }
    }

}