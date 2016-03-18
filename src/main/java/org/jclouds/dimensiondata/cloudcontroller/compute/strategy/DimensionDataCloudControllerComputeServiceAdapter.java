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

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import static org.jclouds.compute.reference.ComputeServiceConstants.COMPUTE_LOGGER;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.reference.ComputeServiceConstants.Timeouts;
import org.jclouds.dimensiondata.cloudcontroller.DimensionDataCloudControllerApi;
import org.jclouds.dimensiondata.cloudcontroller.compute.functions.ServerToServetWithExternalIp;
import org.jclouds.dimensiondata.cloudcontroller.compute.options.DimensionDataCloudControllerTemplateOptions;
import org.jclouds.dimensiondata.cloudcontroller.domain.Datacenter;
import org.jclouds.dimensiondata.cloudcontroller.domain.Disk;
import org.jclouds.dimensiondata.cloudcontroller.domain.FirewallRuleTarget;
import org.jclouds.dimensiondata.cloudcontroller.domain.FirewallRuleTarget.Port;
import org.jclouds.dimensiondata.cloudcontroller.domain.IpRange;
import org.jclouds.dimensiondata.cloudcontroller.domain.NIC;
import org.jclouds.dimensiondata.cloudcontroller.domain.NatRule;
import org.jclouds.dimensiondata.cloudcontroller.domain.NetworkDomain;
import org.jclouds.dimensiondata.cloudcontroller.domain.NetworkInfo;
import org.jclouds.dimensiondata.cloudcontroller.domain.OsImage;
import org.jclouds.dimensiondata.cloudcontroller.domain.Placement;
import org.jclouds.dimensiondata.cloudcontroller.domain.PublicIpBlock;
import org.jclouds.dimensiondata.cloudcontroller.domain.Response;
import org.jclouds.dimensiondata.cloudcontroller.domain.Vlan;
import org.jclouds.dimensiondata.cloudcontroller.domain.internal.ServerWithExternalIp;
import org.jclouds.dimensiondata.cloudcontroller.domain.options.CreateServerOptions;
import org.jclouds.dimensiondata.cloudcontroller.utils.DimensionDataCloudControllerUtils;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import autovalue.shaded.com.google.common.common.collect.Sets;

/**
 * defines the connection between the {@link org.jclouds.dimensiondata.cloudcontroller.DimensionDataCloudControllerApi} implementation and
 * the jclouds {@link org.jclouds.compute.ComputeService}
 *
 */
@Singleton
public class DimensionDataCloudControllerComputeServiceAdapter implements
        ComputeServiceAdapter<ServerWithExternalIp, OsImage, OsImage, Datacenter> {

    private static final String DEFAULT_LOGIN_PASSWORD = "P$$ssWwrrdGoDd!";
    private static final String DEFAULT_LOGIN_USER = "root";
    public static final String DEFAULT_DATACENTER_TYPE = "MCP 2.0";
    public static final String JCLOUDS_FW_RULE_PATTERN = "jclouds%s%s";

    @Resource
    @Named(COMPUTE_LOGGER)
    protected Logger logger = Logger.NULL;

    private final DimensionDataCloudControllerApi api;
    private final Timeouts timeouts;

    @Inject
    public DimensionDataCloudControllerComputeServiceAdapter(DimensionDataCloudControllerApi api, Timeouts timeouts) {
        this.api = checkNotNull(api, "api");
        this.timeouts = timeouts;
    }

    @Override
    public NodeAndInitialCredentials<ServerWithExternalIp> createNodeWithGroupEncodedIntoName(String group, String name, Template template) {
        // Infer the login credentials from the VM, defaulting to "root" user
        LoginCredentials.Builder credsBuilder = LoginCredentials.builder().user(DEFAULT_LOGIN_USER).password(DEFAULT_LOGIN_PASSWORD);
        // If login overrides are supplied in TemplateOptions, always prefer those.
        String overriddenLoginPassword = Objects.firstNonNull(template.getOptions().getLoginPassword(), DEFAULT_LOGIN_PASSWORD);
        if (overriddenLoginPassword != null) {
            credsBuilder.password(overriddenLoginPassword);
        }

        String imageId = checkNotNull(template.getImage().getId(), "template image id must not be null");
        final String locationId = checkNotNull(template.getLocation().getId(), "template location id must not be null");
        final Hardware hardware = checkNotNull(template.getHardware(), "template hardware must not be null");

        DimensionDataCloudControllerTemplateOptions templateOptions = DimensionDataCloudControllerTemplateOptions.class.cast(template.getOptions());
        List<Port> ports = simplifyPorts(templateOptions.getInboundPorts());

        // TODO getOrCreateNetworkDomain with vlan? if yes, move to subclass of CreateNodesWithGroupEncodedIntoNameThenAddToSet
        NetworkDomain foundNetworkDomain = tryFindNetworkDomain(new Predicate<NetworkDomain>() {
            @Override
            public boolean apply(NetworkDomain input) {
                return input.datacenterId().equalsIgnoreCase(locationId);
            }
        });
        Vlan vlan = tryGetVlan(foundNetworkDomain.id());

        NetworkInfo networkInfo = NetworkInfo.create(
                foundNetworkDomain.id(),
                NIC.builder().vlanId(vlan.id()).build(),
                // TODO allow additional NICs
                Lists.<NIC>newArrayList()
        );

        List<Disk> disks = Lists.newArrayList();
        for (int i = 0; i < template.getHardware().getVolumes().size(); i++) {
            // TODO make speed configurable
            disks.add(Disk.builder().scsiId(i).speed("STANDARD").build());
        }

        CreateServerOptions createServerOptions = CreateServerOptions.builder()
                .memoryGb(template.getHardware().getRam())
                .build();

        Response response = api.getServerApi().deployServer(name, imageId, Boolean.TRUE, networkInfo, disks, overriddenLoginPassword, createServerOptions);
        String serverId = DimensionDataCloudControllerUtils.tryFindServerId(response);

        String message = format("server(%s) is not ready within %d ms.", serverId, timeouts.nodeRunning);
        DimensionDataCloudControllerUtils.waitForServerStatus(api.getServerApi(), serverId, true, true, timeouts.nodeRunning, message);

        String externalIp = tryFindExternalIp(api, foundNetworkDomain.id());
        ServerWithExternalIp serverWithExternalIp = ServerWithExternalIp.builder().server(api.getServerApi().getServer(serverId)).externalIp(externalIp).build();
        String internalIp = api.getServerApi().getServer(serverId).networkInfo().primaryNic().privateIpv4();
        Response createNatRuleOperation = api.getNetworkApi().createNatRule(foundNetworkDomain.id(), internalIp, externalIp);
        if (!createNatRuleOperation.error().isEmpty()) {
            // rollback
            api.getServerApi().deleteServer(serverId);
            throw new IllegalStateException(String.format("Cannot create a NAT rule for internalIp %s (server %s) using externalIp %s", internalIp, serverId, externalIp));
        }
        // set firewall policies
        for (Port destinationPort : ports) {
            Response createFirewallRuleOperation = api.getNetworkApi().createFirewallRule(
                    foundNetworkDomain.id(),
                    String.format(JCLOUDS_FW_RULE_PATTERN, name, destinationPort.end() == null ? destinationPort.begin() : destinationPort.begin() + "" + destinationPort.end()),
                    "ACCEPT_DECISIVELY",
                    "IPV4",
                    "TCP",
                    FirewallRuleTarget.builder()
                            .ip(IpRange.create("ANY", null))
                            .build(),
                    FirewallRuleTarget.builder()
                            .ip(IpRange.create(internalIp, null))
                            .port(destinationPort)
                            .build(),
                    Boolean.TRUE,
                    Placement.builder().position("LAST").build());
            if (!createFirewallRuleOperation.error().isEmpty()) {
                // rollback
                // TODO delete all the firewall rules?
                api.getServerApi().deleteServer(serverId);
                throw new IllegalStateException(String.format("Cannot create a firewall rule %s for (server %s) using externalIp %s", destinationPort.begin(), serverId, externalIp));
            }
        }

        return new NodeAndInitialCredentials<ServerWithExternalIp>(serverWithExternalIp, serverId, credsBuilder.build());
    }

    @Override
    public Iterable<OsImage> listHardwareProfiles() {
        return api.getServerImageApi().listOsImages().concat().toList();
    }

    @Override
    public Iterable<OsImage> listImages() {
        return api.getServerImageApi().listOsImages().concat().toList();
    }

    @Override
    public OsImage getImage(String id) {
        return api.getServerImageApi().getOsImage(id);
    }

    @Override
    public Iterable<Datacenter> listLocations() {
        return api.getInfrastructureApi().listDatacenters().concat().filter(new Predicate<Datacenter>() {
            @Override
            public boolean apply(Datacenter input) {
                return input.type().equalsIgnoreCase(DEFAULT_DATACENTER_TYPE);
            }
        }).toList();
    }

    @Override
    public ServerWithExternalIp getNode(String id) {
        return new ServerToServetWithExternalIp().apply(api.getServerApi().getServer(id));
    }

    @Override
    public void destroyNode(String id) {
        // delete NAT rule associated to the VM, if any
        final String internalIp = api.getServerApi().getServer(id).networkInfo().primaryNic().privateIpv4();
        String networkDomainId = api.getServerApi().getServer(id).networkInfo().networkDomainId();

        Optional<NatRule> optionalNatRule = api.getNetworkApi().listNatRules(networkDomainId).concat().firstMatch(new Predicate<NatRule>() {
            @Override
            public boolean apply(NatRule input) {
                return input.internalIp().equalsIgnoreCase(internalIp);
            }
        });
        if (optionalNatRule.isPresent()) {
            api.getNetworkApi().deleteNatRule(optionalNatRule.get().id());
        }

        // power off the vm
        Response powerOffResponse = api.getServerApi().powerOffServer(id);
        if (!powerOffResponse.error().isEmpty()) {
            final String message = format("Cannot power off the server %s.", id);
            throw new IllegalStateException(message);
        }
        String message = format("server(%s) not terminated within %d ms.", id, timeouts.nodeTerminated);
        DimensionDataCloudControllerUtils.waitForServerStatus(api.getServerApi(), id, false, true, timeouts.nodeTerminated, message);

        // delete the vm
        message = format("Cannot delete server %s.", id);
        Response deleteServerResponse = api.getServerApi().deleteServer(id);
        if (!deleteServerResponse.error().isEmpty()) {
            throw new IllegalStateException(message);
        }
    }

    @Override
    public void rebootNode(String id) {
        api.getServerApi().rebootServerServer(id);
    }

    @Override
    public void resumeNode(String id) {
        throw new UnsupportedOperationException("resume not supported");
    }

    @Override
    public void suspendNode(String id) {
        throw new UnsupportedOperationException("suspend not supported");
    }

    @Override
    public Iterable<ServerWithExternalIp> listNodes() {
        return api.getServerApi().listServers().concat().transform(new ServerToServetWithExternalIp()).toList();
    }

    @Override
    public Iterable<ServerWithExternalIp> listNodesByIds(final Iterable<String> ids) {
        return Iterables.filter(listNodes(), new Predicate<ServerWithExternalIp>() {
            @Override
            public boolean apply(final ServerWithExternalIp input) {
                return Iterables.contains(ids, input.server().id());
            }
        });
    }

    private String tryFindExternalIp(DimensionDataCloudControllerApi api, String networkDomainId) {
        // check nat rule in use
        Set<String> externalIpInUse = api.getNetworkApi().listNatRules(networkDomainId).concat()
                .transform(new Function<NatRule, String>() {
            @Override
            public String apply(NatRule input) {
                return input.externalIp();
            }
        })
                .toSet();

        Set<String> availablePublicIpInAllBlocks = getAvailableOrAddPublicIPv4InAllBlocks(api, networkDomainId);

        Sets.SetView<String> difference = Sets.difference(availablePublicIpInAllBlocks, externalIpInUse);
        if (difference.isEmpty()) {
            throw new IllegalStateException();
        }
        return Iterables.get(difference, 0);
    }

    private Set<String> getAvailableOrAddPublicIPv4InAllBlocks(DimensionDataCloudControllerApi api, String networkDomainId) {
        List<PublicIpBlock> publicIpBlocks = api.getNetworkApi().listPublicIPv4AddressBlocks(networkDomainId).concat().toList();
        if (publicIpBlocks.isEmpty()) {
            // addPublicIPv4AddressBlock
            Response response = api.getNetworkApi().addPublicIpBlock(networkDomainId);
            if (!response.error().isEmpty()) {
                throw new IllegalStateException("Cannot add a publicIpBlock to networkDomainId: " + networkDomainId);
            }
            publicIpBlocks = api.getNetworkApi().listPublicIPv4AddressBlocks(networkDomainId).concat().toList();
        }

        return FluentIterable.from(publicIpBlocks)
                .transformAndConcat(new Function<PublicIpBlock, Iterable<String>>() {
                    @Override
                    public Iterable<String> apply(PublicIpBlock input) {
                        List<String> ipAddresses = Lists.newArrayList();
                        String ipAddress = input.baseIp();
                        for (int i = 0; i < input.size(); i++) {
                            ipAddresses.add(ipAddress);
                            ipAddress = getNextIPV4Address(ipAddress);
                        }
                        return ipAddresses;
                    }
                })
                .toSet();
    }

    private Vlan tryGetVlan(String networkDomainId) {
        Optional<Vlan> vlanOptional = api.getNetworkApi().listVlans(networkDomainId).concat().firstMatch(Predicates.<Vlan>notNull());
        if (!vlanOptional.isPresent()) {
            throw new IllegalStateException();
        }
        return vlanOptional.get();
    }

    private NetworkDomain tryFindNetworkDomain(Predicate<NetworkDomain> networkDomainPredicate) {
        Optional<NetworkDomain> optionalNetworkDomain = api.getNetworkApi().listNetworkDomains().concat()
                .filter(networkDomainPredicate)
                .firstMatch(Predicates.notNull());

        if (!optionalNetworkDomain.isPresent()) {
            throw new IllegalStateException();
        }
        return optionalNetworkDomain.get();
    }

    private static String getNextIPV4Address(String ip) {
        String[] nums = ip.split("\\.");
        int i = (Integer.parseInt(nums[0]) << 24 | Integer.parseInt(nums[2]) << 8
                |  Integer.parseInt(nums[1]) << 16 | Integer.parseInt(nums[3])) + 1;

        // If you wish to skip over .255 addresses.
        if ((byte) i == -1) i++;

        return String.format("%d.%d.%d.%d", i >>> 24 & 0xFF, i >> 16 & 0xFF,
                i >>   8 & 0xFF, i >>  0 & 0xFF);
    }

    // Helper function for simplifying an array of ports to a list of ranges FirewallOptions expects.
    public static List<Port> simplifyPorts(int[] ports){
        if ((ports == null) || (ports.length == 0)) {
            return null;
        }
        ArrayList<Port> output = Lists.newArrayList();
        Arrays.sort(ports);

        int range_start = ports[0];
        int range_end = ports[0];
        for (int i = 1; i < ports.length; i++) {
            if ((ports[i - 1] == ports[i] - 1) || (ports[i - 1] == ports[i])){
                // Range continues.
                range_end = ports[i];
            }
            else {
                // Range ends.
                output.add(formatRange(range_start, range_end));
                range_start = ports[i];
                range_end = ports[i];
            }
        }
        // Make sure we get the last range.
        output.add(formatRange(range_start, range_end));
        return output;
    }

    // Helper function for simplifyPorts. Formats port range strings.
    private static Port formatRange(int start, int finish){
        if (start == finish){
            return Port.create(start, null); //Integer.toString(start);
        }
        else {
            return Port.create(start, finish);//String.format("%s:%s", Integer.toString(start), Integer.toString(finish));
        }
    }
}
