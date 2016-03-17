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
package org.jclouds.dimensiondata.cloudcontroller.utils;

import static org.jclouds.util.Predicates2.retry;

import org.jclouds.dimensiondata.cloudcontroller.domain.Property;
import org.jclouds.dimensiondata.cloudcontroller.domain.Response;
import org.jclouds.dimensiondata.cloudcontroller.features.ServerApi;
import org.jclouds.dimensiondata.cloudcontroller.predicates.ServerStatus;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

public class DimensionDataCloudControllerUtils {

    public static String tryFindServerId(Response response) {
        Optional<String> optionalServerId = FluentIterable.from(response.info()).firstMatch(new Predicate<Property>() {
            @Override
            public boolean apply(Property input) {
                return input.name().equals("serverId");
            }
        }).transform(new Function<Property, String>() {
            @Override
            public String apply(Property input) {
                return input.value();
            }
        });
        if (!optionalServerId.isPresent()) {
            // TODO
            throw new IllegalStateException();
        }
        return optionalServerId.get();
    }

    public static void waitForServerStatus(ServerApi api, String serverId, boolean started, boolean deployed, long timeoutMillis, String message) {
        boolean isServerRunning = retry(new ServerStatus(api, started, deployed), timeoutMillis).apply(serverId);
        if (!isServerRunning) {
            throw new IllegalStateException(message);
        }
    }
}
