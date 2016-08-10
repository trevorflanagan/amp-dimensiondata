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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;
import com.google.common.base.CaseFormat;

@AutoValue
public abstract class Server {

    public enum State {
        NORMAL,
        PENDING_DELETE,
        DELETED,
        UNRECOGNIZED;

        @Override
        public String toString() {
            return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name());
        }

        public static State fromValue(String state) {
            try {
                return valueOf(CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, checkNotNull(state, "state")));
            } catch (IllegalArgumentException e) {
                return UNRECOGNIZED;
            }
        }
    }

    Server() {}

    public static Builder builder() {
        return new AutoValue_Server.Builder();
    }

    public abstract String id();
    public abstract String name();
    @Nullable public abstract String description();
    public abstract String datacenterId();
    public abstract State state();
    public abstract String sourceImageId();
    public abstract String createTime();
    public abstract Boolean started();
    public abstract Boolean deployed();
    public abstract OperatingSystem operatingSystem();
    public abstract CPU cpu();
    public abstract Integer memoryGb();
    public abstract List<Disk> disks();
    @Nullable public abstract NetworkInfo networkInfo();
    public abstract List<Object> softwareLabels();
//    @Nullable public abstract VMwareTools vmwareTools(); // FIXME HYPERV only (?)
    @Nullable public abstract Progress progress();
    @Nullable public abstract VirtualHardware virtualHardware();

    @SerializedNames({ "id", "name", "description", "datacenterId", "state", "sourceImageId", "createTime", "started", "deployed",
            "operatingSystem", "cpu", "memoryGb", "disk", "networkInfo", "softwareLabel", "vmwareTools", "progress", "virtualHardware" })
    public static Server create(String id, String name,  String description, String datacenterId, State state, String sourceImageId,
                                String createTime, Boolean started, Boolean deployed, OperatingSystem operatingSystem,
                                CPU cpu, Integer memoryGb, List<Disk> disks, NetworkInfo networkInfo, List<Object> softwareLabels, VMwareTools vmwareTools, Progress progress, VirtualHardware virtualHardware
    ) {
        return builder()
                .id(id).name(name).datacenterId(datacenterId).description(description).state(state).sourceImageId(sourceImageId).createTime(createTime).started(started).deployed(deployed).operatingSystem(operatingSystem).cpu(cpu).memoryGb(memoryGb).disks(disks).networkInfo(networkInfo).softwareLabels(softwareLabels).vmwareTools(vmwareTools)
                .progress(progress).virtualHardware(virtualHardware)
                .build();
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder id(String id);
        public abstract Builder name(String name);
        public abstract Builder description(String description);
        public abstract Builder datacenterId(String datacenterId);
        public abstract Builder state(State state);
        public abstract Builder sourceImageId(String sourceImageId);
        public abstract Builder createTime(String createTime);
        public abstract Builder started(Boolean started);
        public abstract Builder deployed(Boolean deployed);
        public abstract Builder operatingSystem(OperatingSystem operatingSystem);
        public abstract Builder cpu(CPU cpu);
        public abstract Builder memoryGb(Integer memoryGb);
        public abstract Builder disks(List<Disk> disks);
        public abstract Builder networkInfo(NetworkInfo networkInfo);
        public abstract Builder softwareLabels(List<Object> softwareLabels);
        public abstract Builder vmwareTools(VMwareTools vmwareTools);
        public abstract Builder progress(Progress progress);
        public abstract Builder virtualHardware(VirtualHardware virtualHardware);

        public abstract Server build();
    }
}
