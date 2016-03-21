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
import static org.jclouds.compute.reference.ComputeServiceConstants.COMPUTE_LOGGER;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.dimensiondata.cloudcontroller.DimensionDataCloudControllerApi;
import org.jclouds.dimensiondata.cloudcontroller.domain.NatRule;
import org.jclouds.dimensiondata.cloudcontroller.domain.Server;
import org.jclouds.dimensiondata.cloudcontroller.domain.internal.ServerWithExternalIp;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

public class ServerToServetWithExternalIp implements Function<Server, ServerWithExternalIp> {

    @Resource
    @Named(COMPUTE_LOGGER)
    protected Logger logger = Logger.NULL;

    private final DimensionDataCloudControllerApi api;

    @Inject
    public ServerToServetWithExternalIp(DimensionDataCloudControllerApi api) {
        this.api = checkNotNull(api, "api");
    }

    @Override
    public ServerWithExternalIp apply(Server input) {
        if (input == null) return null;

        NatRule natRule = api.getNetworkApi().listNatRules(input.networkInfo().networkDomainId()).concat()
                .firstMatch(new Predicate<NatRule>() {
                    @Override
                    public boolean apply(NatRule input) {
                        return input.internalIp().equalsIgnoreCase(input.internalIp());
                    }
                })
                .orNull();

        ServerWithExternalIp.Builder builder = ServerWithExternalIp.builder().server(input);
                if (natRule != null) {
                    builder.externalIp(natRule.externalIp());
                }
        return builder.build();
    }
}
