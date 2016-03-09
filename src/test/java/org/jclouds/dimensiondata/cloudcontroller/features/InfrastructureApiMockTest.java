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

import org.jclouds.dimensiondata.cloudcontroller.DimensionDataCloudControllerApi;
import org.jclouds.dimensiondata.cloudcontroller.internal.BaseDimensionDataCloudControllerMockTest;
import org.jclouds.dimensiondata.cloudcontroller.parse.DatacentersParseTest;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.testng.annotations.Test;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Mock tests for the {@link InfrastructureApi} class.
 */
@Test(groups = "unit", testName = "InfrastructureApiMockTest")
public class InfrastructureApiMockTest extends BaseDimensionDataCloudControllerMockTest {

    public void testListDatacenters() throws Exception {
        MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/datacenters.json")));
        InfrastructureApi api = api(server);

        try {
            assertEquals(api.listDatacenters().concat().toList(), new DatacentersParseTest().expected().toList());
            assertSent(server, "GET", "/infrastructure/datacenter");
        } finally {
            server.shutdown();
        }
    }

    public void testOperatingSystems() throws Exception {
        MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/operatingSystems.json")));
        InfrastructureApi api = api(server);

        try {
            assertEquals(api.listOperatingSystems("datacenterId"), new DatacentersParseTest().expected().toList());
            assertSent(server, "GET", "/infrastructure/datacenter");
        } finally {
            server.shutdown();
        }
    }

    private InfrastructureApi api(MockWebServer server) {
        return api(DimensionDataCloudControllerApi.class, server.getUrl("/").toString(), new JavaUrlHttpCommandExecutorServiceModule())
                .getInfrastructureApi();
    }

}
