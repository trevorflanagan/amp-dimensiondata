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

import static org.jclouds.util.Predicates2.retry;
import static org.testng.Assert.assertNotNull;

import java.util.List;

import org.jclouds.dimensiondata.cloudcontroller.domain.Disk;
import org.jclouds.dimensiondata.cloudcontroller.domain.Property;
import org.jclouds.dimensiondata.cloudcontroller.domain.Response;
import org.jclouds.dimensiondata.cloudcontroller.domain.Server;
import org.jclouds.dimensiondata.cloudcontroller.domain.options.NetworkInfo;
import org.jclouds.dimensiondata.cloudcontroller.internal.BaseDimensionDataCloudControllerApiLiveTest;
import org.jclouds.dimensiondata.cloudcontroller.predicates.ServerStatus;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;

import autovalue.shaded.com.google.common.common.collect.Lists;

@Test(groups = "live", testName = "ServerApiLiveTest", singleThreaded = true)
public class ServerApiLiveTest extends BaseDimensionDataCloudControllerApiLiveTest {

    private String serverId;

    @Test
    public void testListServers() {
        List<Server> servers = api().listServers().concat().toList();
        assertNotNull(servers);
        for (Server s : servers) {
            assertNotNull(s);
        }
    }

    @Test
    public void testDeployAndStartServer() {
        Boolean started = Boolean.TRUE;
        NetworkInfo networkInfo = NetworkInfo.create(
                "690de302-bb80-49c6-b401-8c02bbefb945",
                NetworkInfo.NicRequest.create("6b25b02e-d3a2-4e69-8ca7-9bab605deebd", null),
                Lists.<NetworkInfo.NicRequest> newArrayList()
        );
        List<Disk> disks = ImmutableList.of(
                Disk.builder()
                        .scsiId(0)
                        .speed("STANDARD")
                        .build()
        );
        Response response = api().deployServer(ServerApiLiveTest.class.getSimpleName(), "4c02126c-32fc-4b4c-9466-9824c1b5aa0f", started, networkInfo, disks, "P$$ssWwrrdGoDd!");
        assertNotNull(response);
        Optional<String> optionalResponseServerId = tryFindServerId(response);
        if (!optionalResponseServerId.isPresent()) {
            Assert.fail();
        }
        serverId = optionalResponseServerId.get();
        boolean IsServerRunning = waitForServerStatus(serverId, true, true, 30 * 60 * 1000);
        if (!IsServerRunning) {
            Assert.fail();
        }
    }

    private Optional<String> tryFindServerId(Response response) {
        return FluentIterable.from(response.info()).firstMatch(new Predicate<Property>() {
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
    }

    @Test(dependsOnMethods = "testDeployAndStartServer")
    public void testPowerOffServer() {
        api().powerOffServer(serverId);
        boolean IsServerRunning = waitForServerStatus(serverId, false, true, 30 * 60 * 1000);
        if (!IsServerRunning) {
            Assert.fail();
        }
    }

    @AfterTest
    public void testDeleteServer() {
        if (serverId != null) {
            Response response = api().deleteServer(serverId);
            assertNotNull(response);
        }
    }

    private boolean waitForServerStatus(String serverId, boolean started, boolean deployed, long timeoutMillis) {
        return retry(new ServerStatus(api.getServerApi(), started, deployed), timeoutMillis).apply(serverId);
    }

    private ServerApi api() {
        return api.getServerApi();
    }

}
