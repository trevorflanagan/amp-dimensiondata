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
public abstract class Response {

    Response() {}

    public abstract String operation();
    public abstract String responseCode();
    public abstract String message();
    public abstract List<Property> info();
    public abstract List<String> warning();
    public abstract List<String> error();
    public abstract String requestId();

    @SerializedNames({ "operation", "responseCode", "message", "info", "warning", "error", "requestId" })
    public static Response create(String operation, String responseCode, String message, List<Property> info, List<String> warning, List<String> error, String requestId) {
        return builder().operation(operation).responseCode(responseCode).message(message)
                .info(info).warning(warning).error(error).requestId(requestId).build();
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder operation(String operation);
        public abstract Builder responseCode(String responseCode);
        public abstract Builder message(String message);
        public abstract Builder info(List<Property> info);
        public abstract Builder warning(List<String> warning);
        public abstract Builder error(List<String> error);
        public abstract Builder requestId(String requestId);

        public abstract Response build();
    }

    public static Builder builder() {
        return new AutoValue_Response.Builder();
    }
}