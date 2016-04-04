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

import static org.jclouds.dimensiondata.cloudcontroller.compute.DimensionDataCloudControllerComputeServiceAdapter.DEFAULT_ACTION;
import static org.jclouds.dimensiondata.cloudcontroller.compute.DimensionDataCloudControllerComputeServiceAdapter.DEFAULT_IP_VERSION;
import static org.jclouds.dimensiondata.cloudcontroller.compute.DimensionDataCloudControllerComputeServiceAdapter.DEFAULT_PROTOCOL;
import static org.jclouds.dimensiondata.cloudcontroller.compute.strategy.GetOrCreateNetworkDomainThenCreateNodes.DEFAULT_PRIVATE_IPV4_BASE_ADDRESS;
import static org.jclouds.dimensiondata.cloudcontroller.compute.strategy.GetOrCreateNetworkDomainThenCreateNodes.DEFAULT_PRIVATE_IPV4_PREFIX_SIZE;
import static org.jclouds.dimensiondata.cloudcontroller.utils.DimensionDataCloudControllerUtils.generateFirewallName;
import static org.testng.Assert.assertNotNull;

import java.util.List;

import org.jclouds.dimensiondata.cloudcontroller.domain.FirewallRule;
import org.jclouds.dimensiondata.cloudcontroller.domain.FirewallRuleTarget;
import org.jclouds.dimensiondata.cloudcontroller.domain.IpRange;
import org.jclouds.dimensiondata.cloudcontroller.domain.NatRule;
import org.jclouds.dimensiondata.cloudcontroller.domain.Placement;
import org.jclouds.dimensiondata.cloudcontroller.domain.Response;
import org.jclouds.dimensiondata.cloudcontroller.internal.BaseDimensionDataCloudControllerApiLiveTest;
import org.jclouds.dimensiondata.cloudcontroller.utils.DimensionDataCloudControllerUtils;
import org.jclouds.rest.ResourceAlreadyExistsException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

@Test(groups = "live", testName = "NetworkApiLiveTest", singleThreaded = true)
public class NetworkApiLiveTest extends BaseDimensionDataCloudControllerApiLiveTest {

    private String networkDomainId;
    private String vlanId;
    private List<String> firewallRuleIds;

    @BeforeClass
    public void init() {
        firewallRuleIds = Lists.newArrayList();
        /*
        NetworkDomain networkDomain = api().listNetworkDomains().concat().firstMatch(new Predicate<NetworkDomain>() {
            @Override
            public boolean apply(NetworkDomain networkDomain) {
                return networkDomain.datacenterId().equals("NA9");
            }
        }).orNull();
        if (networkDomain == null) {
            Assert.fail();
        }
        networkDomainId = networkDomain.id();
        */
    }

    @Test(dependsOnMethods = "testCreateMultipleFirewallRules")
    public void testExploreNetworkDomains() {
        List<NatRule> natRules = api().listNatRules(networkDomainId).concat().toList();
        assertNotNull(natRules);
        List<FirewallRule> firewallRules = api().listFirewallRules(networkDomainId).concat().toList();
        assertNotNull(firewallRules);
            for (FirewallRule firewallRule : firewallRules) {
                api().deleteFirewallRule(firewallRule.id());
            }
    }

    @Test(dependsOnMethods = "testCreateSameFirewallRuleTwice")
    public void testCreateMultipleFirewallRules() {
        for (int i = 0; i < 12; i++) {
            int j = i * 1024;
            FirewallRuleTarget.Port destinationPort = FirewallRuleTarget.Port.create(j + 1 , j + 1025);
            Response createFirewallRuleResponse = api().createFirewallRule(
                    networkDomainId,
                    generateFirewallName("serverId", destinationPort),
                    DEFAULT_ACTION,
                    DEFAULT_IP_VERSION,
                    DEFAULT_PROTOCOL,
                    FirewallRuleTarget.builder()
                            .ip(IpRange.create("ANY", null))
                            .build(),
                    FirewallRuleTarget.builder()
                            .ip(IpRange.create("ANY", null))
                            .port(destinationPort)
                            .build(),
                    Boolean.TRUE,
                    Placement.builder().position("LAST").build());
            if (!createFirewallRuleResponse.error().isEmpty()) {
                String firewallRuleErrorMessage = String.format("Cannot create a firewall rule %s-%s. Rolling back ...", destinationPort.begin(), destinationPort.end());
                throw new IllegalStateException(firewallRuleErrorMessage);
            }
            firewallRuleIds.add(DimensionDataCloudControllerUtils.tryFindPropertyValue(createFirewallRuleResponse, "firewallRuleId"));
        }
    }

    @Test(dependsOnMethods = "testDeployVlan")
    public void testCreateSameFirewallRuleTwice() {
        for (int i = 0; i < 2; i++) {
            FirewallRuleTarget.Port destinationPort = FirewallRuleTarget.Port.create(1, 1025);
            Response createFirewallRuleResponse = api().createFirewallRule(
                    networkDomainId,
                    generateFirewallName("serverId", destinationPort),
                    DEFAULT_ACTION,
                    DEFAULT_IP_VERSION,
                    DEFAULT_PROTOCOL,
                    FirewallRuleTarget.builder()
                            .ip(IpRange.create("ANY", null))
                            .build(),
                    FirewallRuleTarget.builder()
                            .ip(IpRange.create("ANY", null))
                            .port(destinationPort)
                            .build(),
                    Boolean.TRUE,
                    Placement.builder().position("LAST").build());
            if (!createFirewallRuleResponse.error().isEmpty()) {
                String firewallRuleErrorMessage = String.format("Cannot create a firewall rule %s-%s. Rolling back ...", destinationPort.begin(), destinationPort.end());
                throw new IllegalStateException(firewallRuleErrorMessage);
            }
            String firewallRuleId = DimensionDataCloudControllerUtils.tryFindPropertyValue(createFirewallRuleResponse, "firewallRuleId");
            if (firewallRuleId != null) {
                firewallRuleIds.add(firewallRuleId);
            }
        }
    }

    @Test(dependsOnMethods = "testDeployNetworkDomain")
    public void testDeployVlan() {
        Response deployVlanResponse =  api().deployVlan(networkDomainId, NetworkApiLiveTest.class.getSimpleName(), NetworkApiLiveTest.class.getSimpleName(), DEFAULT_PRIVATE_IPV4_BASE_ADDRESS, DEFAULT_PRIVATE_IPV4_PREFIX_SIZE);
        vlanId = DimensionDataCloudControllerUtils.tryFindPropertyValue(deployVlanResponse, "vlanId");

    }

    @Test
    public void testDeployNetworkDomain() {
        String location = "NA9";
        String networkDomainName = NetworkApiLiveTest.class.getSimpleName();
        Response deployNetworkDomainResponse = api().deployNetworkDomain(location, networkDomainName, NetworkApiLiveTest.class.getSimpleName(), "ESSENTIALS");
        networkDomainId = DimensionDataCloudControllerUtils.tryFindPropertyValue(deployNetworkDomainResponse, "networkDomainId");
        assertNotNull(networkDomainId);
    }

    @Test(expectedExceptions = ResourceAlreadyExistsException.class)
    public void testDeploySameNetworkDomain() {
        api().deployNetworkDomain("NA9", NetworkApiLiveTest.class.getSimpleName(), NetworkApiLiveTest.class.getSimpleName(), "ESSENTIALS");
    }

    @AfterClass
    public void tearDown() {
        if (!firewallRuleIds.isEmpty()) {
            for (String firewallRuleId : firewallRuleIds) {
                api().deleteFirewallRule(firewallRuleId);
            }
        }
    }

    private NetworkApi api() {
        return api.getNetworkApi();
    }

}
