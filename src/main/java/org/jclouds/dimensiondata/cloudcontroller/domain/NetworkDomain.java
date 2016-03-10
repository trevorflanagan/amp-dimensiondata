/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.dimensiondata.cloudcontroller.domain;

import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class NetworkDomain {

    NetworkDomain() {}

    public static Builder builder() {
        return new AutoValue_NetworkDomain.Builder();
    }

    public abstract String id();
    public abstract String datacenterId();
    public abstract String name();
    public abstract String description();
    public abstract String state();
    public abstract String type();
    public abstract String snatIpv4Address();
    public abstract String createTime();

    @SerializedNames({ "id", "datacenterId", "name", "description", "state", "type", "snatIpv4Address", "createTime" })
    public static NetworkDomain create(String id, String datacenterId, String name, String description, String state, String type, String snatIpv4Address, String createTime) {
        return builder().id(id).datacenterId(datacenterId).name(name).description(description).state(state).type(type).snatIpv4Address(snatIpv4Address).createTime(createTime).build();
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder id(String id);
        public abstract Builder datacenterId(String datacenterId);
        public abstract Builder name(String name);
        public abstract Builder description(String description);
        public abstract Builder state(String state);
        public abstract Builder type(String type);
        public abstract Builder snatIpv4Address(String snatIpv4Address);
        public abstract Builder createTime(String createTime);

        public abstract NetworkDomain build();
    }

}
