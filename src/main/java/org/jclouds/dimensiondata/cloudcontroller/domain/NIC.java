/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.dimensiondata.cloudcontroller.domain;

import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class NIC {

    NIC() {}

    public abstract String id();
    public abstract String privateIpv4();
    public abstract String networkId();
    public abstract String networkName();
    public abstract String state();

    @SerializedNames({ "id", "privateIpv4", "networkId", "networkName", "state" })
    public static NIC create(String id, String privateIpv4, String networkId, String networkName, String state) {
        return builder().id(id).privateIpv4(privateIpv4).networkId(networkId).networkName(networkName).state(state).build();
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder id(String id);
        public abstract Builder privateIpv4(String privateIpv4);
        public abstract Builder networkId(String networkId);
        public abstract Builder networkName(String networkName);
        public abstract Builder state(String state);

        public abstract NIC build();
    }

    public static Builder builder() {
        return new AutoValue_NIC.Builder();
    }
}