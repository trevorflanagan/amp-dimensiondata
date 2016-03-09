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

import static org.jclouds.dimensiondata.cloudcontroller.domain.NetworkDomain.NetworkDomainType.*;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.dimensiondata.cloudcontroller.domain.NetworkDomain;
import org.jclouds.dimensiondata.cloudcontroller.internal.BaseDimensionDataCloudControllerParseTest;
import org.testng.annotations.Test;

@Test(groups = "unit")
public class NetworkDomainParseTest extends BaseDimensionDataCloudControllerParseTest<NetworkDomain> {

    @Override
    public String resource() {
        return "/networkDomain.json";
    }

    @Override
    @Consumes(MediaType.APPLICATION_JSON)
    public NetworkDomain expected() {
        return NetworkDomain.builder()
                        .id("8e082ed6-c198-4eff-97cb-aeac6f9685d8")
                        .datacenterId("NA9")
                        .name("test")
                        .description("")
                        .type(ESSENTIALS)
                        .state("NORMAL")
                        .build();
    }
}
