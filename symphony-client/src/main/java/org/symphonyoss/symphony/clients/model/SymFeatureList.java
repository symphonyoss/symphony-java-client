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
 * Abstract Model for FeatureList (symphony-pod API).
 */
public class SymFeatureList extends ArrayList<SymFeature> {

    /**
     * Empty {@link SymFeatureList} constructor.
     */
    public SymFeatureList() {

    }

    /**
     * Converts a {@link FeatureList} to a {@link SymFeatureList}.
     * @param featureList the {@link FeatureList} to convert from.
     * @return symFeatureList the converted {@link SymFeatureList}.
     */
    public static SymFeatureList toSymFeatureList(FeatureList featureList) {
        if (featureList == null) {
            return null;
        }

        List<SymFeature> symFeatures = SymFeature.toSymFeatures(featureList);
        SymFeatureList symFeatureList = new SymFeatureList();
        symFeatures.forEach(symFeature -> {
            symFeatureList.add(symFeature);
        });
        return symFeatureList;
    }

    /**
     * Converts a {@link SymFeatureList} to a {@link FeatureList}.
     * @param symFeatureList the {@link SymFeatureList} to convert from.
     * @return featureList the converted {@link FeatureList}.
     */
    public static FeatureList toFeatureList(SymFeatureList symFeatureList) {
        if (symFeatureList == null) {
            return null;
        }

        List<Feature> features = SymFeature.toFeatures(symFeatureList);
        FeatureList featureList = new FeatureList();
        features.forEach(feature -> {
            featureList.add(feature);
        });
        return featureList;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else {
            return o != null && this.getClass() == o.getClass() ? super.equals(o) : false;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{super.hashCode()});
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class SymFeatureList {\n");
        sb.append("    ").append(this.toIndentedString(super.toString())).append("\n");
        sb.append("}");
        return sb.toString();
    }

    private String toIndentedString(Object o) {
        return o == null ? "null" : o.toString().replace("\n", "\n    ");
    }

}
