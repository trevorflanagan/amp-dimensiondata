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

import java.util.List;

import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class OsImage extends BaseImage
{
    public static final String TYPE = "OS_IMAGE";

    OsImage() {
        type = TYPE;
    }

    public static Builder builder() {
        return new AutoValue_OsImage.Builder();
    }

    @SerializedNames({
            "id",
            "datacenterId",
            "name",
            "description",
            "operatingSystem",
            "cpu",
            "memoryGb",
            "disk",
            "softwareLabel",
            "createTime",

            "osImageKey"
    })
    public static OsImage create(
            String id,
            String datacenterId,
            String name,
            String description,
            OperatingSystem operatingSystem,
            CPU cpu,
            Integer memoryGb,
            List<Disk> disk,
            List<Object> softwareLabel,
            String createTime,

            String osImageKey
    ) {
        return builder()
                .id(id)
                .datacenterId(datacenterId)
                .name(name)
                .description(description)
                .operatingSystem(operatingSystem)
                .cpu(cpu)
                .memoryGb(memoryGb)
                .disk(disk)
                .softwareLabel(softwareLabel)
                .createTime(createTime)

                .osImageKey(osImageKey)

                .build();
    }
    public abstract String osImageKey();

    public abstract Builder toBuilder();

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder id(String id);
        public abstract Builder datacenterId(String datacenterId);
        public abstract Builder name(String name);
        public abstract Builder description(String description);
        public abstract Builder operatingSystem(OperatingSystem operatingSystem);
        public abstract Builder cpu(CPU cpu);
        public abstract Builder memoryGb(Integer memoryGb);
        public abstract Builder disk(List<Disk> disks);
        public abstract Builder softwareLabel(List<Object> softwareLabels);
        public abstract Builder createTime(String createTime);

        public abstract Builder osImageKey(String osImageKey);

        public abstract OsImage build();
    }
}
