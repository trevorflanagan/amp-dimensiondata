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
package org.jclouds.dimensiondata.cloudcontroller.compute;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.reference.ComputeServiceConstants.Timeouts;
import org.jclouds.dimensiondata.cloudcontroller.DimensionDataCloudControllerApi;
import org.jclouds.dimensiondata.cloudcontroller.compute.functions.CleanupServer;
import org.jclouds.dimensiondata.cloudcontroller.compute.functions.ServerToServetWithExternalIp;
import org.jclouds.dimensiondata.cloudcontroller.compute.options.DimensionDataCloudControllerTemplateOptions;
import org.jclouds.dimensiondata.cloudcontroller.domain.Account;
import org.jclouds.dimensiondata.cloudcontroller.domain.Datacenter;
import org.jclouds.dimensiondata.cloudcontroller.domain.Disk;
import org.jclouds.dimensiondata.cloudcontroller.domain.FirewallRuleTarget;
import org.jclouds.dimensiondata.cloudcontroller.domain.IpRange;
import org.jclouds.dimensiondata.cloudcontroller.domain.NIC;
import org.jclouds.dimensiondata.cloudcontroller.domain.NetworkInfo;
import org.jclouds.dimensiondata.cloudcontroller.domain.OsImage;
import org.jclouds.dimensiondata.cloudcontroller.domain.Placement;
import org.jclouds.dimensiondata.cloudcontroller.domain.Response;
import org.jclouds.dimensiondata.cloudcontroller.domain.factory.CpuFactory;
import org.jclouds.dimensiondata.cloudcontroller.domain.internal.ServerWithExternalIp;
import org.jclouds.dimensiondata.cloudcontroller.domain.options.CreateServerOptions;
import org.jclouds.dimensiondata.cloudcontroller.utils.DimensionDataCloudControllerUtils;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.logging.Logger;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static java.lang.String.format;
import static org.jclouds.compute.reference.ComputeServiceConstants.COMPUTE_LOGGER;
import static org.jclouds.dimensiondata.cloudcontroller.utils.DimensionDataCloudControllerUtils.generateFirewallRuleName;
import static org.jclouds.dimensiondata.cloudcontroller.utils.DimensionDataCloudControllerUtils.generatePortListName;
import static org.jclouds.dimensiondata.cloudcontroller.utils.DimensionDataCloudControllerUtils.simplifyPorts;

/**
 * defines the connection between the {@link org.jclouds.dimensiondata.cloudcontroller.DimensionDataCloudControllerApi} implementation and
 * the jclouds {@link org.jclouds.compute.ComputeService}
 */
@Singleton
public class DimensionDataCloudControllerComputeServiceAdapter implements
        ComputeServiceAdapter<ServerWithExternalIp, OsImage, OsImage, Datacenter> {

    public static String ORG_ID;
    private static final String DEFAULT_LOGIN_PASSWORD = "P$$ssWwrrdGoDd!";
    private static final String DEFAULT_LOGIN_USER = "root";
    public static final String DEFAULT_ACTION = "ACCEPT_DECISIVELY";
    public static final String DEFAULT_IP_VERSION = "IPV4";
    public static final String DEFAULT_PROTOCOL = "TCP";

    @Resource
    @Named(COMPUTE_LOGGER)
    protected Logger logger = Logger.NULL;

    private final DimensionDataCloudControllerApi api;
    private final Timeouts timeouts;
    protected final CleanupServer cleanupServer;


    @Inject
    public DimensionDataCloudControllerComputeServiceAdapter(DimensionDataCloudControllerApi api, Timeouts timeouts, CleanupServer cleanupServer) {
        this.api = checkNotNull(api, "api");
        this.timeouts = timeouts;
        this.cleanupServer = cleanupServer;
        if (ORG_ID == null) {
            Account myAccount = api.getAccountApi().getMyAccount();
            ORG_ID = myAccount.orgId();
        }
    }

    @Override
    public NodeAndInitialCredentials<ServerWithExternalIp> createNodeWithGroupEncodedIntoName(String group, final String name, Template template) {
        // Infer the login credentials from the VM, defaulting to "root" user
        LoginCredentials.Builder credsBuilder = LoginCredentials.builder().user(DEFAULT_LOGIN_USER).password(DEFAULT_LOGIN_PASSWORD);
        // If login overrides are supplied in TemplateOptions, always prefer those.
        String loginPassword = Objects.firstNonNull(template.getOptions().getLoginPassword(), DEFAULT_LOGIN_PASSWORD);
        if (loginPassword != null) {
            credsBuilder.password(loginPassword);
        }

        String imageId = checkNotNull(template.getImage().getId(), "template image id must not be null");
        Image image = checkNotNull(template.getImage(), "template image must not be null");
        Hardware hardware = checkNotNull(template.getHardware(), "template hardware must not be null");

        DimensionDataCloudControllerTemplateOptions templateOptions = DimensionDataCloudControllerTemplateOptions.class.cast(template.getOptions());

        String networkDomainId = templateOptions.getNetworkDomainId();
        String vlanId = templateOptions.getVlanId();

        NetworkInfo networkInfo = NetworkInfo.create(
                networkDomainId,
                NIC.builder().vlanId(vlanId).build(),
                // TODO allow additional NICs
                Lists.<NIC>newArrayList()
        );

        List<Disk> disks = Lists.newArrayList();
        // TODO add all the volumes as disks
        if (template.getHardware().getVolumes() != null) {
            Volume volume = template.getHardware().getVolumes().get(0);
            disks.add(Disk.builder()
                    .scsiId(Integer.valueOf(volume.getDevice()))
                    .sizeGb(volume.getSize().intValue())
                    .speed("STANDARD")
                    .build());
        }

        CreateServerOptions createServerOptions = CreateServerOptions.builder()
                .memoryGb(template.getHardware().getRam() / 1024)
                .cpu(new CpuFactory().create(template.getHardware().getProcessors()))
                .build();

        Response deployServerResponse = api.getServerApi().deployServer(ORG_ID, name, imageId, Boolean.TRUE, networkInfo, disks, loginPassword, createServerOptions);
        String serverId = DimensionDataCloudControllerUtils.tryFindPropertyValue(deployServerResponse, "serverId");

        String message = format("Server(%s) is not ready within %d ms.", serverId, timeouts.nodeRunning);
        DimensionDataCloudControllerUtils.waitForServerStatus(api.getServerApi(), serverId, true, true, timeouts.nodeRunning, message);

        ServerWithExternalIp.Builder serverWithExternalIpBuilder = ServerWithExternalIp.builder().server(api.getServerApi().getServer(ORG_ID, serverId));

        if (templateOptions.autoCreateNatRule()) {
            // addPublicIPv4AddressBlock
            Response addPublicIpBlockResponse = api.getNetworkApi().addPublicIpBlock(ORG_ID, networkDomainId);
            //manageResponse(response, format("Cannot add a publicIpBlock to networkDomainId %s", networkDomainId));
            String ipBlockId = DimensionDataCloudControllerUtils.tryFindPropertyValue(addPublicIpBlockResponse, "ipBlockId");
            String externalIp = api.getNetworkApi().getPublicIPv4AddressBlock(ORG_ID, ipBlockId).baseIp();

            serverWithExternalIpBuilder.externalIp(externalIp);
            String internalIp = api.getServerApi().getServer(ORG_ID, serverId).networkInfo().primaryNic().privateIpv4();
            Response createNatRuleOperation = api.getNetworkApi().createNatRule(ORG_ID, networkDomainId, internalIp, externalIp);
            if (!createNatRuleOperation.error().isEmpty()) {
                // rollback
                String natRuleErrorMessage = String.format("Cannot create a NAT rule for internalIp %s (server %s) using externalIp %s. Rolling back ...", internalIp, serverId, externalIp);
                logger.warn(natRuleErrorMessage);
                destroyNode(serverId);
                throw new IllegalStateException(natRuleErrorMessage);
            }

            List<FirewallRuleTarget.Port> ports = simplifyPorts(templateOptions.getInboundPorts());
            Response createPorlListResponse = api.getNetworkApi()
                    .createPortList(ORG_ID, networkDomainId,
                            generatePortListName(serverId),
                            "port list created by jclouds",
                            ports,
                            ImmutableList.<String>of());
            final String portListId = DimensionDataCloudControllerUtils.tryFindPropertyValue(createPorlListResponse, "portListId");

            Response createFirewallRuleResponse = api.getNetworkApi().createFirewallRule(
                    ORG_ID,
                    templateOptions.getNetworkDomainId(),
                    generateFirewallRuleName(serverId),
                    DEFAULT_ACTION,
                    DEFAULT_IP_VERSION,
                    DEFAULT_PROTOCOL,
                    FirewallRuleTarget.builder()
                            .ip(IpRange.create("ANY", null))
                            .build(),
                    FirewallRuleTarget.builder()
                            .ip(IpRange.create(externalIp, null))
                            .portListId(portListId)
                            .build(),
                    Boolean.TRUE,
                    Placement.builder().position("LAST").build());
            if (!createFirewallRuleResponse.error().isEmpty()) {
                // rollback
                String firewallRuleErrorMessage = String.format("Cannot create a firewall rule %s. Rolling back ...", portListId);
                logger.warn(firewallRuleErrorMessage);
                destroyNode(serverId);
                throw new IllegalStateException(firewallRuleErrorMessage);
            }
        }
        return new NodeAndInitialCredentials<ServerWithExternalIp>(serverWithExternalIpBuilder.build(), serverId, credsBuilder.build());
    }

    @Override
    public Iterable<OsImage> listHardwareProfiles() {
        return api.getServerImageApi().listOsImages(ORG_ID).concat().toList();
    }

    @Override
    public Iterable<OsImage> listImages() {
        return api.getServerImageApi().listOsImages(ORG_ID).concat().toList();
    }

    @Override
    public OsImage getImage(String id) {
        return api.getServerImageApi().getOsImage(ORG_ID, id);
    }

    @Override
    public Iterable<Datacenter> listLocations() {
        return api.getInfrastructureApi().listDatacenters(ORG_ID).concat().toList();
    }

    @Override
    public ServerWithExternalIp getNode(String id) {
        return new ServerToServetWithExternalIp(api).apply(api.getServerApi().getServer(ORG_ID, id));
    }

    @Override
    public void destroyNode(final String serverId) {
        checkState(cleanupServer.apply(serverId), "server(%s) still there after deleting!?", serverId);
    }

    @Override
    public void rebootNode(String id) {
        api.getServerApi().rebootServerServer(ORG_ID, id);
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
        return api.getServerApi().listServers(ORG_ID).concat().transform(new ServerToServetWithExternalIp(api)).toList();
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
