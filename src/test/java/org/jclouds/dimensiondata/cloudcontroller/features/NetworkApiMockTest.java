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
import org.jclouds.dimensiondata.cloudcontroller.parse.NetworkDomainsParseTest;
import org.jclouds.dimensiondata.cloudcontroller.parse.PublicIpBlocksParseTest;
import org.jclouds.dimensiondata.cloudcontroller.parse.VlansParseTest;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.testng.annotations.Test;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Mock tests for the {@link org.jclouds.dimensiondata.cloudcontroller.features.NetworkApi} class.
 */
@Test(groups = "unit", testName = "NetworkApiMockTest")
public class NetworkApiMockTest extends BaseDimensionDataCloudControllerMockTest {

    public void testListNetworkDomains() throws Exception {
        MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/networkDomains.json")));
        NetworkApi api = api(server);

        try {
            assertEquals(api.listNetworkDomains().concat().toList(), new NetworkDomainsParseTest().expected().toList());
            assertSent(server, "GET", "/network/networkDomain");
        } finally {
            server.shutdown();
        }
    }

    public void testLisVlans() throws Exception {
        MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/vlans.json")));
        NetworkApi api = api(server);

        try {
            assertEquals(api.listVlans("12345").concat().toList(), new VlansParseTest().expected().toList());
            assertSent(server, "GET", "/network/vlan?networkDomainId=12345");
        } finally {
            server.shutdown();
        }
    }

    public void testListPublicIPv4AddressBlock() throws Exception {
        MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/publicIpBlocks.json")));
        NetworkApi api = api(server);

        try {
            assertEquals(api.listPublicIPv4AddressBlocks("12345").concat().toList(), new PublicIpBlocksParseTest().expected().toList());
            assertSent(server, "GET", "/network/publicIpBlock?networkDomainId=12345");
        } finally {
            server.shutdown();
        }
    }

    private NetworkApi api(MockWebServer server) {
        return api(DimensionDataCloudControllerApi.class, server.getUrl("/").toString(), new JavaUrlHttpCommandExecutorServiceModule())
                .getNetworkApi();
    }

}
