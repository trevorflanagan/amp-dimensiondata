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
public abstract class CPU {

    CPU() {}

    public abstract Integer count();
    public abstract String speed();
    public abstract Integer coresPerSocket();

    @SerializedNames({ "count", "speed", "coresPerSocket" })
    public static CPU create(Integer count, String speed, Integer coresPerSocket) {
        return builder().count(count).speed(speed).coresPerSocket(coresPerSocket).build();
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder count(Integer count);
        public abstract Builder speed(String speed);
        public abstract Builder coresPerSocket(Integer coresPerSocket);

        public abstract CPU build();
    }

    public static Builder builder() {
        return new AutoValue_CPU.Builder();
    }
}