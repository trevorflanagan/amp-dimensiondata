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

import com.google.auto.value.AutoValue;
import org.jclouds.json.SerializedNames;

import java.util.List;

@AutoValue
public abstract class CustomerImage extends BaseImage
{

    public static final String TYPE = "CUSTOMER_IMAGE";

    CustomerImage()
    {
        type = TYPE;
    }

    public static Builder builder()
    {
        return new AutoValue_CustomerImage.Builder();
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

            "state"
    })
    public static CustomerImage create(
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

            String state
    )
    {
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

                .state(state)

                .build();
    }

    public abstract String state();

    public abstract Builder toBuilder();

    @AutoValue.Builder
    public abstract static class Builder
    {
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

        public abstract Builder state(String state);

        public abstract CustomerImage build();
    }
}
