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
package org.jclouds.dimensiondata.cloudcontroller.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.internal.Arg0ToPagedIterable;
import org.jclouds.dimensiondata.cloudcontroller.DimensionDataCloudControllerApi;
import org.jclouds.dimensiondata.cloudcontroller.domain.Datacenter;
import org.jclouds.dimensiondata.cloudcontroller.features.NetworkApi;
import org.jclouds.dimensiondata.cloudcontroller.options.PaginationOptions;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Optional;

@Beta
public class NetworkDomainsToPagedIterable extends Arg0ToPagedIterable.FromCaller<Datacenter, NetworkDomainsToPagedIterable> {

    private final DimensionDataCloudControllerApi api;

    @Inject
    protected NetworkDomainsToPagedIterable(DimensionDataCloudControllerApi api) {
        this.api = checkNotNull(api, "api");
    }

    @Override
    protected Function<Object, IterableWithMarker<Datacenter>> markerToNextForArg0(Optional<Object> ignored) {
        final NetworkApi networkApi = api.getNetworkApi();
        return new Function<Object, IterableWithMarker<Datacenter>>() {

            @SuppressWarnings("unchecked")
            @Override
            public IterableWithMarker<Datacenter> apply(Object input) {
                PaginationOptions paginationOptions = PaginationOptions.class.cast(input);
                return IterableWithMarker.class.cast(networkApi.listNetworkDomains(paginationOptions));
            }

            @Override
            public String toString() {
                return "listNetworkDomains()";
            }
        };
    }

}