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
package org.jclouds.dimensiondata.cloudcontroller.parse;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.dimensiondata.cloudcontroller.domain.Datacenter;
import org.jclouds.dimensiondata.cloudcontroller.domain.Datacenters;
import org.jclouds.dimensiondata.cloudcontroller.internal.BaseDimensionDataCloudControllerParseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;


@Test(groups = "unit")
public class DatacentersParseTest extends BaseDimensionDataCloudControllerParseTest<Datacenters> {

    @Override
    public String resource() {
        return "/datacenters.json";
    }

    @Override
    @Consumes(MediaType.APPLICATION_JSON)
    public Datacenters expected() {
        List<Datacenter> datacenters = ImmutableList.of(
                Datacenter.builder()
                        .id("NA3")
                        .displayName("US - West")
                        .type("MCP 1.0")
                        .city("Santa Clara")
                        .state("California")
                        .country("US")
                        .vpnUrl("https://na3.cloud-vpn.net")
                        .ftpsHost("ftps-na.cloud-vpn.net")
                        .build(),
                Datacenter.builder()
                        .id("NA9")
                        .displayName("US - East 3 - MCP 2.0")
                        .type("MCP 2.0")
                        .city("Ashburn")
                        .state("Virginia")
                        .country("US")
                        .vpnUrl("https://na9.cloud-vpn.net")
                        .ftpsHost("ftps-na.cloud-vpn.net")
                        .build()
        );
        return new Datacenters(datacenters, 1, 5, 5, 250);
    }
}
