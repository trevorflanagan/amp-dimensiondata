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

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.reference.ComputeServiceConstants.Timeouts;
import org.jclouds.dimensiondata.cloudcontroller.DimensionDataCloudControllerApi;
import org.jclouds.dimensiondata.cloudcontroller.domain.Datacenter;
import org.jclouds.dimensiondata.cloudcontroller.domain.Disk;
import org.jclouds.dimensiondata.cloudcontroller.domain.NetworkDomain;
import org.jclouds.dimensiondata.cloudcontroller.domain.OsImage;
import org.jclouds.dimensiondata.cloudcontroller.domain.Response;
import org.jclouds.dimensiondata.cloudcontroller.domain.Server;
import org.jclouds.dimensiondata.cloudcontroller.domain.options.NetworkInfo;
import org.jclouds.dimensiondata.cloudcontroller.utils.DimensionDataCloudControllerUtils;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.logging.Logger;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import autovalue.shaded.com.google.common.common.collect.Lists;

/**
 * defines the connection between the {@link org.jclouds.dimensiondata.cloudcontroller.DimensionDataCloudControllerApi} implementation and
 * the jclouds {@link org.jclouds.compute.ComputeService}
 *
 */
@Singleton
public class DimensionDataCloudControllerComputeServiceAdapter implements
        ComputeServiceAdapter<Server, OsImage, OsImage, Datacenter> {

    private static final String DEFAULT_LOGIN_PASSWORD = "P$$ssWwrrdGoDd!";
    private static final String DEFAULT_LOGIN_USER = "root";

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
    public NodeAndInitialCredentials<Server> createNodeWithGroupEncodedIntoName(String group, String name, Template template) {

        // Infer the login credentials from the VM, defaulting to "root" user
        LoginCredentials.Builder credsBuilder = LoginCredentials.builder().user(DEFAULT_LOGIN_USER).password(DEFAULT_LOGIN_PASSWORD);
        // If login overrides are supplied in TemplateOptions, always prefer those.
        String overriddenLoginPassword = template.getOptions().getLoginPassword();
        if (overriddenLoginPassword != null) {
            credsBuilder.password(overriddenLoginPassword);
        }

        String imageId = checkNotNull(template.getImage().getId(), "template image id must not be null");
        final String locationId = checkNotNull(template.getLocation().getId(), "template location id must not be null");
        final String hardwareId = checkNotNull(template.getHardware().getId(), "template hardware id must not be null");

        // TODO createNetworkInfo
        List<NetworkDomain> filterdNetworkDomainsPerDatacenter = api.getNetworkApi().listNetworkDomains().concat()
                .filter(new Predicate<NetworkDomain>() {
                    @Override
                    public boolean apply(NetworkDomain input) {
                        return input.datacenterId().equalsIgnoreCase(locationId);
                    }
                }).toList();


        // TODO getOrCreateNetworkDomain with vlan?
        NetworkInfo networkInfo = NetworkInfo.create(
                        "690de302-bb80-49c6-b401-8c02bbefb945",
                        NetworkInfo.NicRequest.create("6b25b02e-d3a2-4e69-8ca7-9bab605deebd", null),
                        Lists.<NetworkInfo.NicRequest>newArrayList()
                );
        List<Disk> disks = ImmutableList.of(
                Disk.builder()
                        .scsiId(0)
                        .speed("STANDARD")
                        .build()
        );

        Response response = api.getServerApi().deployServer(name, imageId, Boolean.TRUE, networkInfo, disks, overriddenLoginPassword);
        Optional<String> optionalResponseServerId = DimensionDataCloudControllerUtils.tryFindServerId(response);
        if (!optionalResponseServerId.isPresent()) {
        }
        String serverId = optionalResponseServerId.get();
        boolean IsServerRunning = DimensionDataCloudControllerUtils.waitForServerStatus(api.getServerApi(), serverId, true, true, timeouts.nodeRunning);
        if (!IsServerRunning) {
            final String message = format("server(%s, %s) not ready within %d ms.", name, serverId, timeouts.nodeRunning);
            logger.warn(message);
            throw new IllegalStateException(message);
        }

        return new NodeAndInitialCredentials<Server>(api.getServerApi().getServer(serverId), serverId, credsBuilder.build());
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
                return input.type().equalsIgnoreCase("MCP 2.0");
            }
        }).toList();
    }

    @Override
    public Server getNode(String id) {
        return api.getServerApi().getServer(id);
    }

    @Override
    public void destroyNode(String id) {
        Response powerOffResponse = api.getServerApi().powerOffServer(id);
        if (!powerOffResponse.error().isEmpty()) {
            final String message = format("Cannot stop server %s.", id);
            logger.error(message);
            throw new IllegalStateException(message);
        }
        boolean IsServerTerminated = DimensionDataCloudControllerUtils.waitForServerStatus(api.getServerApi(), id, false, true, timeouts.nodeTerminated);
        if (!IsServerTerminated) {
            final String message = format("server(%s) not terminated within %d ms.", id, timeouts.nodeTerminated);
            logger.warn(message);
            throw new IllegalStateException(message);
        }
        Response deleteServerResponse = api.getServerApi().deleteServer(id);
        if (!deleteServerResponse.error().isEmpty()) {
            final String message = format("Cannot delete server %s.", id);
            logger.error(message);
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
    public Iterable<Server> listNodes() {
        return api.getServerApi().listServers().concat().toList();
    }

    @Override
    public Iterable<Server> listNodesByIds(final Iterable<String> ids) {
        return Iterables.filter(listNodes(), new Predicate<Server>() {
            @Override
            public boolean apply(final Server input) {
                return Iterables.contains(ids, input.id());
            }
        });
    }
}
