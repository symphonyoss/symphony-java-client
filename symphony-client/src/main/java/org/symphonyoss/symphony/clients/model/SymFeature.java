/*
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

import org.symphonyoss.symphony.pod.model.Feature;
import org.symphonyoss.symphony.pod.model.FeatureList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Abstract model for Feature (symphony-pod API) - entitlement feature record.
 */
public class SymFeature {

    private String entitlement = null;
    private Boolean enabled = null;

    /**
     * Empty constructor for an entitlement feature record.
     */
    public SymFeature() {

    }

    /**
     * Instantiates an entitlement feature record.
     * @param entitlement the entitlement to set.
     * @return {@link SymFeature}.
     */
    public SymFeature entitlement(String entitlement) {
        this.entitlement = entitlement;
        return this;
    }

    /**
     * Retrieves the entitlement from a {@link SymFeature}.
     * @return entitlement the entitlement from a {@link SymFeature}.
     */
    public String getEntitlement() {
        return this.entitlement;
    }

    /**
     * Sets the entitlement for a {@link SymFeature}.
     * @param entitlement the entitlement to set for a {@link SymFeature}.
     */
    public void setEntitlement(String entitlement) {
        this.entitlement = entitlement;
    }

    /**
     * Instantiates a {@link SymFeature} by setting it enabled/disabled.
     * @param enabled the status to set for the {@link SymFeature}.
     * @return {@link SymFeature}.
     */
    public SymFeature enabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    /**
     * Checks whether a {@link SymFeature} is enabled or disabled.
     * @return enabled the status of a {@link SymFeature}.
     */
    public Boolean getEnabled() {
        return this.enabled;
    }

    /**
     * Enables/disables a {@link SymFeature}.
     * @param enabled the enabled/disable status to set for a {@link SymFeature}.
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            SymFeature symFeature = (SymFeature)o;
            return Objects.equals(this.entitlement, symFeature.entitlement) && Objects.equals(this.enabled, symFeature.enabled);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.entitlement, this.enabled});
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class SymFeature {\n");
        sb.append("    entitlment: ").append(this.toIndentedString(this.entitlement)).append("\n");
        sb.append("    enabled: ").append(this.toIndentedString(this.enabled)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Converts a {@link FeatureList} to a list of {@link SymFeature}.
     * @param features the {@link FeatureList} to convert from.
     * @return symFeatureList the list of {@link SymFeature}.
     */
    public static List<SymFeature> toSymFeatures(FeatureList features) {
        List<SymFeature> symFeatureList = new ArrayList<>();
        features.forEach(feature -> {
            symFeatureList.add(SymFeature.toSymFeature(feature));
        });
        return symFeatureList;
    }

    /**
     * Converts a {@link SymFeatureList} to a list of {@link Feature}.
     * @param symFeatures the {@link SymFeatureList} to convert from.
     * @return featureList the list of {@link Feature}.
     */
    public static List<Feature> toFeatures(SymFeatureList symFeatures) {
        List<Feature> featureList = new ArrayList<>();
        symFeatures.forEach(symFeature -> {
            featureList.add(SymFeature.toFeature(symFeature));
        });
        return featureList;
    }

    /*
     * Converts a Feature instance to a SymFeature.
     */
    private static SymFeature toSymFeature(Feature feature) {
        SymFeature symFeature = new SymFeature();
        symFeature.setEntitlement(feature.getEntitlment());
        symFeature.setEnabled(feature.getEnabled());
        return symFeature;
    }

    /*
     * Converts a SymFeature to a Feature.
     */
    private static Feature toFeature(SymFeature symFeature) {
        Feature feature = new Feature();
        feature.setEntitlment(symFeature.getEntitlement());
        feature.setEnabled(symFeature.getEnabled());
        return feature;
    }

    private String toIndentedString(Object o) {
        return o == null ? "null" : o.toString().replace("\n", "\n    ");
    }

}
