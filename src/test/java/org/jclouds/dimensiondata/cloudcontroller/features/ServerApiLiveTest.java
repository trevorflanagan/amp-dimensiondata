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
package org.jclouds.dimensiondata.cloudcontroller.features;

import static org.testng.Assert.assertNotNull;

import java.util.List;

import org.jclouds.dimensiondata.cloudcontroller.domain.Server;
import org.jclouds.dimensiondata.cloudcontroller.internal.BaseDimensionDataCloudControllerApiLiveTest;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "ServerApiLiveTest", singleThreaded = true)
public class ServerApiLiveTest extends BaseDimensionDataCloudControllerApiLiveTest {

    @Test
    public void testListServers() {
        List<Server> servers = api().listServers().concat().toList();
        assertNotNull(servers);
        for (Server s : servers) {
            assertNotNull(s);
        }
    }

    private ServerApi api() {
        return api.getServerApi();
    }

}
