/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.dimensiondata.cloudcontroller.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.List;

import org.jclouds.dimensiondata.cloudcontroller.DimensionDataCloudControllerApi;
import org.jclouds.dimensiondata.cloudcontroller.domain.Response;
import org.jclouds.dimensiondata.cloudcontroller.domain.Server;
import org.jclouds.dimensiondata.cloudcontroller.internal.BaseDimensionDataCloudControllerMockTest;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.testng.annotations.Test;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Mock tests for the {@link ServerApi} class.
 */
@Test(groups = "unit", testName = "ServerApiMockTest")
public class ServerApiMockTest extends BaseDimensionDataCloudControllerMockTest {

    public void testListServers() throws Exception {
        MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/servers.json")));
        ServerApi api = api(server);

        try {
            List<Server> servers = api.listServers().concat().toList();

            assertSent(server, "GET", "/server/server");
            assertEquals(servers.size(), 1);
            for (Server s : servers) {
                assertNotNull(s);
            }

        } finally {
            server.shutdown();
        }
    }

    public void testGetServer() throws Exception {
        MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/server.json")));
        ServerApi api = api(server);

        try {
            Server found = api.getServer("12345");

            assertSent(server, "GET", "/server/server/12345");
            assertNotNull(found);

        } finally {
            server.shutdown();
        }
    }

    public void testDeleteServer() throws Exception {
        MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/deleteServer.json")));
        ServerApi api = api(server);

        try {
            Response found = api.deleteServer("12345");

            assertSent(server, "POST", "/server/deleteServer");
            assertNotNull(found);

        } finally {
            server.shutdown();
        }
    }

    private ServerApi api(MockWebServer server) {
        return api(DimensionDataCloudControllerApi.class, server.getUrl("/").toString(), new JavaUrlHttpCommandExecutorServiceModule())
                .getServerApi();
    }

}
