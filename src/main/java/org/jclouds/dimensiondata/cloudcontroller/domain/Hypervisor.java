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

import java.util.List;

import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Hypervisor {

    @AutoValue
    public abstract static class DiskSpeed {

        DiskSpeed() {} // For AutoValue only!

        public abstract String id();
        public abstract String displayName();
        public abstract String abbreviation();
        public abstract String description();
        public abstract Boolean available();
        public abstract Boolean _default();

        @SerializedNames({ "id", "displayName", "abbreviation", "description", "available", "default" })
        public static DiskSpeed create(String id, String displayName, String abbreviation, String description, Boolean available, Boolean _default) {
            return new AutoValue_Hypervisor_DiskSpeed(id, displayName, abbreviation, description, available, _default);
        }
    }

    @AutoValue
    public abstract static class CpuSpeed {

        CpuSpeed() {} // For AutoValue only!

        public abstract String id();
        public abstract String displayName();
        public abstract String description();
        public abstract Boolean available();
        public abstract Boolean _default();

        @SerializedNames({ "id", "displayName", "description", "available", "default" })
        public static CpuSpeed create(String id, String displayName, String description, Boolean available, Boolean _default) {
            return new AutoValue_Hypervisor_CpuSpeed(id, displayName, description, available, _default);
        }
    }

    Hypervisor() {}

    public abstract String type();
    public abstract String maintenanceStatus();
    public abstract List<Property> properties();
    public abstract List<DiskSpeed> disksSpeed();
    public abstract List<CpuSpeed> cpusSpeed();

    @SerializedNames({ "type", "maintenanceStatus", "property", "diskSpeed", "cpuSpeed" })
    public static Hypervisor create(String type, String maintenanceStatus, List<Property> properties, List<DiskSpeed> disksSpeed, List<CpuSpeed> cpusSpeed) {
        return builder().type(type).maintenanceStatus(maintenanceStatus).properties(properties).disksSpeed(disksSpeed).cpusSpeed(cpusSpeed).build();
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder maintenanceStatus(String maintenanceStatus);
        public abstract Builder type(String type);
        public abstract Builder properties(List<Property> properties);
        public abstract Builder disksSpeed(List<DiskSpeed> properties);
        public abstract Builder cpusSpeed(List<CpuSpeed> properties);

        public abstract Hypervisor build();
    }

    public static Builder builder() {
        return new AutoValue_Hypervisor.Builder();
    }
}
