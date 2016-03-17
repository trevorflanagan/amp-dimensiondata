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

import java.util.List;
import java.util.concurrent.TimeUnit;

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
import org.jclouds.dimensiondata.cloudcontroller.domain.Datacenter;
import org.jclouds.dimensiondata.cloudcontroller.domain.Disk;
import org.jclouds.dimensiondata.cloudcontroller.domain.NIC;
import org.jclouds.dimensiondata.cloudcontroller.domain.NatRule;
import org.jclouds.dimensiondata.cloudcontroller.domain.NetworkDomain;
import org.jclouds.dimensiondata.cloudcontroller.domain.OsImage;
import org.jclouds.dimensiondata.cloudcontroller.domain.PublicIpBlock;
import org.jclouds.dimensiondata.cloudcontroller.domain.Response;
import org.jclouds.dimensiondata.cloudcontroller.domain.Vlan;
import org.jclouds.dimensiondata.cloudcontroller.domain.internal.ServerWithExternalIp;
import org.jclouds.dimensiondata.cloudcontroller.domain.options.CreateServerOptions;
import org.jclouds.dimensiondata.cloudcontroller.domain.NetworkInfo;
import org.jclouds.dimensiondata.cloudcontroller.utils.DimensionDataCloudControllerUtils;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.logging.Logger;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

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
                NIC.builder()
                        .vlanId(vlan.id())
                        .build(), // (vlan.id(), null),
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
        Optional<String> optionalResponseServerId = DimensionDataCloudControllerUtils.tryFindServerId(response);
        if (!optionalResponseServerId.isPresent()) {
            // TODO
        }
        String serverId = optionalResponseServerId.get();
        boolean IsServerRunning = DimensionDataCloudControllerUtils.waitForServerStatus(api.getServerApi(), serverId, true, true, timeouts.nodeRunning);
        if (!IsServerRunning) {
            final String message = format("server(%s, %s) not ready within %d ms.", name, serverId, timeouts.nodeRunning);
            throw new IllegalStateException(message);
        }

        // TODO getOrAllocatePublicIPv4AddressBlock ?
        Optional<PublicIpBlock> optionalPublicIpBlock = api.getNetworkApi().listPublicIPv4AddressBlocks(foundNetworkDomain.id()).concat().firstMatch(Predicates.<PublicIpBlock>notNull());

        if (!optionalPublicIpBlock.isPresent()) {
            // TODO
        }

        // TODO checkNatRules in use

        ServerWithExternalIp.Builder serverWithExternalIpBuilder = ServerWithExternalIp.builder().server(api.getServerApi().getServer(serverId));

        // TODO getExternalIp
        String externalIp = optionalPublicIpBlock.get().baseIp();
        // TODO getOrCreateNatRule ?
        Response createNatRuleOperation = api.getNetworkApi().createNatRule(foundNetworkDomain.id(), api.getServerApi().getServer(serverId).networkInfo().primaryNic().privateIpv4(), externalIp); // TODO
        if (!createNatRuleOperation.error().isEmpty()) {
            // TODO
        } else {
            serverWithExternalIpBuilder.externalIp(externalIp);
        }
        return new NodeAndInitialCredentials<ServerWithExternalIp>(serverWithExternalIpBuilder.build(), serverId, credsBuilder.build());
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

        // poweroff the vm
        Response powerOffResponse = api.getServerApi().powerOffServer(id);
        if (!powerOffResponse.error().isEmpty()) {
            final String message = format("Cannot power off the server %s.", id);
            throw new IllegalStateException(message);
        }
        boolean IsServerTerminated = DimensionDataCloudControllerUtils.waitForServerStatus(api.getServerApi(), id, false, true, timeouts.nodeTerminated);
        if (!IsServerTerminated) {
            final String message = format("server(%s) not terminated within %d ms.", id, timeouts.nodeTerminated);
            throw new IllegalStateException(message);
        }
        // delete the vm
        Response deleteServerResponse = api.getServerApi().deleteServer(id);

        boolean IsServerDeleted = DimensionDataCloudControllerUtils.waitForServerStatus(api.getServerApi(), id, false, false, TimeUnit.MINUTES.toMillis(10));
        if (!IsServerDeleted || !deleteServerResponse.error().isEmpty()) {
            final String message = format("Cannot delete server %s.", id);
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
}
