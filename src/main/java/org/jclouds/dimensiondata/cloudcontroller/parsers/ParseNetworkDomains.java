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
package org.jclouds.dimensiondata.cloudcontroller.parsers;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.internal.ArgsToPagedIterable;
import org.jclouds.dimensiondata.cloudcontroller.DimensionDataCloudControllerApi;
import org.jclouds.dimensiondata.cloudcontroller.domain.NetworkDomain;
import org.jclouds.dimensiondata.cloudcontroller.domain.NetworkDomains;
import org.jclouds.dimensiondata.cloudcontroller.options.PaginationOptions;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.json.Json;

import com.google.common.base.Function;
import com.google.inject.TypeLiteral;

@Singleton
public class ParseNetworkDomains extends ParseJson<NetworkDomains> {

    @Inject
    public ParseNetworkDomains(Json json) {
        super(json, TypeLiteral.get(NetworkDomains.class));
    }

    public static class ToPagedIterable extends ArgsToPagedIterable<NetworkDomain, ToPagedIterable> {

        private DimensionDataCloudControllerApi api;

        @Inject
        public ToPagedIterable(DimensionDataCloudControllerApi api) {
            this.api = api;
        }

        @Override
        protected Function<Object, IterableWithMarker<NetworkDomain>> markerToNextForArgs(List<Object> args) {
            return new Function<Object, IterableWithMarker<NetworkDomain>>() {
                @Override
                public IterableWithMarker<NetworkDomain> apply(Object input) {
                    PaginationOptions paginationOptions = PaginationOptions.class.cast(input);
                    return api.getNetworkApi().listNetworkDomains(paginationOptions);
                }
            };
        }
    }
}
