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
package org.jclouds.dimensiondata.cloudcontroller.compute.strategy;

import static com.google.common.base.Objects.firstNonNull;
import static com.google.common.collect.Iterables.tryFind;
import static java.lang.String.format;
import static org.jclouds.dimensiondata.cloudcontroller.compute.DimensionDataCloudControllerComputeServiceAdapter.DEFAULT_ACTION;
import static org.jclouds.dimensiondata.cloudcontroller.compute.DimensionDataCloudControllerComputeServiceAdapter.DEFAULT_IP_VERSION;
import static org.jclouds.dimensiondata.cloudcontroller.compute.DimensionDataCloudControllerComputeServiceAdapter.DEFAULT_PROTOCOL;
import static org.jclouds.dimensiondata.cloudcontroller.utils.DimensionDataCloudControllerUtils.generateFirewallName;
import static org.jclouds.dimensiondata.cloudcontroller.utils.DimensionDataCloudControllerUtils.simplifyPorts;
import static org.jclouds.util.Predicates2.retry;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.config.CustomizationResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.compute.reference.ComputeServiceConstants.Timeouts;
import org.jclouds.compute.strategy.CreateNodeWithGroupEncodedIntoName;
import org.jclouds.compute.strategy.CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap.Factory;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.impl.CreateNodesWithGroupEncodedIntoNameThenAddToSet;
import org.jclouds.dimensiondata.cloudcontroller.DimensionDataCloudControllerApi;
import org.jclouds.dimensiondata.cloudcontroller.compute.options.DimensionDataCloudControllerTemplateOptions;
import org.jclouds.dimensiondata.cloudcontroller.domain.FirewallRule;
import org.jclouds.dimensiondata.cloudcontroller.domain.FirewallRuleTarget;
import org.jclouds.dimensiondata.cloudcontroller.domain.IpRange;
import org.jclouds.dimensiondata.cloudcontroller.domain.NetworkDomain;
import org.jclouds.dimensiondata.cloudcontroller.domain.Placement;
import org.jclouds.dimensiondata.cloudcontroller.domain.Response;
import org.jclouds.dimensiondata.cloudcontroller.domain.Vlan;
import org.jclouds.dimensiondata.cloudcontroller.features.NetworkApi;
import org.jclouds.dimensiondata.cloudcontroller.utils.DimensionDataCloudControllerUtils;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;

@Singleton
public class GetOrCreateNetworkDomainThenCreateNodes
        extends CreateNodesWithGroupEncodedIntoNameThenAddToSet {

    private static final String DEFAULT_NETWORK_DOMAIN_NAME = "JCLOUDS_NETWORK_DOMAIN";
    private static final String DEFAULT_VLAN_NAME = "JCLOUDS_VLAN";
    private static final String DEFAULT_PRIVATE_IPV4_BASE_ADDRESS = "10.0.0.0";
    private static final Integer DEFAULT_PRIVATE_IPV4_PREFIX_SIZE = 24;

    private final DimensionDataCloudControllerApi api;
    private final Timeouts timeouts;

    @Inject
    protected GetOrCreateNetworkDomainThenCreateNodes(
            CreateNodeWithGroupEncodedIntoName addNodeWithGroupStrategy,
            ListNodesStrategy listNodesStrategy,
            GroupNamingConvention.Factory namingConvention,
            @Named("jclouds.user-threads") ListeningExecutorService userExecutor,
            Factory customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory,
            DimensionDataCloudControllerApi api, Timeouts timeouts) {
        super(addNodeWithGroupStrategy, listNodesStrategy, namingConvention, userExecutor,
                customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory);
        this.api = api;
        this.timeouts = timeouts;
    }

    @Override
    public Map<?, ListenableFuture<Void>> execute(
            final String group, final int count, final Template template,
            final Set<NodeMetadata> goodNodes, final Map<NodeMetadata, Exception> badNodes,
            final Multimap<NodeMetadata, CustomizationResponse> customizationResponses) {

        final DimensionDataCloudControllerTemplateOptions templateOptions = template.getOptions().as(DimensionDataCloudControllerTemplateOptions.class);

        String networkDomainName = firstNonNull(templateOptions.getNetworkDomainName(), DEFAULT_NETWORK_DOMAIN_NAME);
        String vlanName = firstNonNull(templateOptions.getVlanName(), DEFAULT_VLAN_NAME);
        String defaultPrivateIPv4BaseAddress = firstNonNull(templateOptions.getDefaultPrivateIPv4BaseAddress(), DEFAULT_PRIVATE_IPV4_BASE_ADDRESS);
        Integer defaultPrivateIPv4PrefixSize = firstNonNull(templateOptions.getDefaultPrivateIPv4PrefixSize(), DEFAULT_PRIVATE_IPV4_PREFIX_SIZE);
        String location = template.getLocation().getId();

        // If networkDomain and vlanId overrides are supplied in TemplateOptions, always prefer those.
        if (templateOptions.getNetworkDomainId() == null && templateOptions.getVlanId() == null) {
            String networkDomainId = tryFindExistingNetworkDomainOrCreate(api, location, networkDomainName);
            templateOptions.networkDomainId(networkDomainId);
            String vlanId = tryFindExistingVlanOrCreate(api, templateOptions.getNetworkDomainId(), vlanName, defaultPrivateIPv4BaseAddress, defaultPrivateIPv4PrefixSize);
            templateOptions.vlanId(vlanId);
        }

        // set firewall rules on networkDomain
        Set<FirewallRuleTarget.Port> ports = ImmutableSet.copyOf(simplifyPorts(templateOptions.getInboundPorts()));
        Set<FirewallRuleTarget.Port> existingDestinationPorts = api.getNetworkApi().listFirewallRules(templateOptions.getNetworkDomainId()).concat()
                .filter(new Predicate<FirewallRule>() {
                    @Override
                    public boolean apply(FirewallRule firewallRule) {
                        return firewallRule.destination() != null;
                    }
                })
                .transform(new Function<FirewallRule, FirewallRuleTarget.Port>() {
                    @Override
                    public FirewallRuleTarget.Port apply(FirewallRule firewallRule) {
                        return firewallRule.destination().port();
                    }
                })
                .filter(Predicates.<FirewallRuleTarget.Port>notNull())
                .toSet();

        Set<FirewallRuleTarget.Port> portsToBeInstalled = Sets.difference(ports, existingDestinationPorts).immutableCopy();
        for (FirewallRuleTarget.Port destinationPort : portsToBeInstalled) {
            Response createFirewallRuleResponse = api.getNetworkApi().createFirewallRule(
                    templateOptions.getNetworkDomainId(),
                    generateFirewallName(destinationPort),
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
            if (createFirewallRuleResponse != null && !createFirewallRuleResponse.responseCode().equals("RESOURCE_BUSY")) {
                DimensionDataCloudControllerUtils.tryFindPropertyValue(createFirewallRuleResponse, "firewallRuleId");
                if (!createFirewallRuleResponse.error().isEmpty()) {
                    String firewallRuleErrorMessage = String.format("Cannot create a firewall rule %s-%s. Rolling back ...", destinationPort.begin(), destinationPort.end());
                    logger.warn(firewallRuleErrorMessage);
                    throw new IllegalStateException(firewallRuleErrorMessage);
                }
            }
        }
        return super.execute(group, count, template, goodNodes, badNodes, customizationResponses);
    }

    private String tryFindExistingNetworkDomainOrCreate(final DimensionDataCloudControllerApi api, final String location, final String networkDomainName) {
        List<NetworkDomain> networkDomains = api.getNetworkApi().listNetworkDomains().concat().toList();
        logger.debug("Looking for a suitable existing network domain in datacenter %s ...", location);

        Predicate<NetworkDomain> networkDomainPredicate = new Predicate<NetworkDomain>() {
            @Override
            public boolean apply(NetworkDomain networkDomain) {
                return networkDomain.datacenterId().equalsIgnoreCase(location) &&
                        networkDomain.name().equals(networkDomainName);
            }
        };

        Optional<NetworkDomain> networkDomainOptional = tryFind(networkDomains, networkDomainPredicate);

        String networkDomainId;
        if (networkDomainOptional.isPresent()) {
            logger.debug("Found a suitable existing network domain {}", networkDomainOptional.get());
            return networkDomainOptional.get().id();
        } else {
            logger.debug("Creating a network domain '%s' in location '%s' ...", networkDomainName, location);
            Response deployNetworkDomainResponse = api.getNetworkApi().deployNetworkDomain(location, networkDomainName, "network domain created by jclouds", "ESSENTIALS");

            networkDomainId = DimensionDataCloudControllerUtils.tryFindPropertyValue(deployNetworkDomainResponse, "networkDomainId");
            String message = format("networkDomain(%s) is not ready within %d ms.", networkDomainId, timeouts.nodeRunning);
            boolean isNetworkDomainReady = retry(new NetworkDomainStatus(api.getNetworkApi()), timeouts.nodeRunning).apply(networkDomainId);
            if (!isNetworkDomainReady) {
                throw new IllegalStateException(message);
            }
            return networkDomainId;
        }
    }

    private String tryFindExistingVlanOrCreate(final DimensionDataCloudControllerApi api, final String networkDomainId,
                                             final String vlanName, final String defaultPrivateIPv4BaseAddress,
                                             final Integer defaultPrivateIPv4PrefixSize) {
        Optional<Vlan> optionalVlan = DimensionDataCloudControllerUtils.tryGetVlan(api.getNetworkApi(), networkDomainId);
        if (optionalVlan.isPresent()) {
            logger.debug("Found a suitable existing vlan %s", optionalVlan.get());
            return optionalVlan.get().id();
        } else {
            logger.debug("Creating a vlan %s in network domain '%s' ...", vlanName, networkDomainId);
            Response deployVlanResponse = api.getNetworkApi().deployVlan(networkDomainId, vlanName, "vlan created by jclouds", defaultPrivateIPv4BaseAddress, defaultPrivateIPv4PrefixSize);
            String vlanId = DimensionDataCloudControllerUtils.tryFindPropertyValue(deployVlanResponse, "vlanId");
            String message = format("vlan(%s) is not ready within %d ms.", vlanId, timeouts.nodeRunning);
            boolean isVlanReady = retry(new VlanStatus(api.getNetworkApi()), timeouts.nodeRunning).apply(vlanId);
            if (!isVlanReady) {
                throw new IllegalStateException(message);
            }
            return vlanId;
        }
    }

    private class NetworkDomainStatus implements Predicate<String> {

        @Resource
        protected Logger logger = Logger.NULL;

        private final NetworkApi api;

        public NetworkDomainStatus(NetworkApi api) {
            this.api = api;
        }

        @Override
        public boolean apply(String networkDomainId) {
            logger.trace("looking for networkDomain state %s", networkDomainId);

            NetworkDomain found = api.getNetworkDomain(networkDomainId);

            // perhaps networkDomain isn't available, yet
            if (found == null) return false;
            logger.trace("%s: looking for network domain %s deployed: currently: %s", found, found.state());
            return found.state().equals("NORMAL");
        }

        @Override
        public String toString() {
            return "requestNetworkDomainStatus()";
        }
    }

    private class VlanStatus implements Predicate<String> {

        @Resource
        protected Logger logger = Logger.NULL;

        private final NetworkApi api;

        public VlanStatus(NetworkApi api) {
            this.api = api;
        }

        @Override
        public boolean apply(String vlanId) {
            logger.trace("looking for vlan state %s", vlanId);

            Vlan found = api.getVlan(vlanId);

            // perhaps networkDomain isn't available, yet
            if (found == null) return false;
            logger.trace("%s: looking for vlan %s deployed: currently: %s", found, found.state());
            return found.state().equals("NORMAL");
        }

        @Override
        public String toString() {
            return "requestVlanStatus()";
        }
    }
}
