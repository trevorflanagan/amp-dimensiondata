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
import static org.jclouds.compute.reference.ComputeServiceConstants.COMPUTE_LOGGER;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Template;
import org.jclouds.dimensiondata.cloudcontroller.DimensionDataCloudControllerApi;
import org.jclouds.dimensiondata.cloudcontroller.domain.Datacenter;
import org.jclouds.dimensiondata.cloudcontroller.domain.OsImage;
import org.jclouds.dimensiondata.cloudcontroller.domain.Server;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * defines the connection between the {@link org.jclouds.dimensiondata.cloudcontroller.DimensionDataCloudControllerApi} implementation and
 * the jclouds {@link org.jclouds.compute.ComputeService}
 *
 */
@Singleton
public class DimensionDataCloudControllerComputeServiceAdapter implements
        ComputeServiceAdapter<Server, OsImage, OsImage, Datacenter> {

    @Resource
    @Named(COMPUTE_LOGGER)
    protected Logger logger = Logger.NULL;


    private final DimensionDataCloudControllerApi api;

    @Inject
    public DimensionDataCloudControllerComputeServiceAdapter(DimensionDataCloudControllerApi api) {
        this.api = checkNotNull(api, "api");
    }

    @Override
    public NodeAndInitialCredentials<Server> createNodeWithGroupEncodedIntoName(String group, String name, Template template) {
        return null;
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
        return null;
    }

    @Override
    public Iterable<Datacenter> listLocations() {
        return api.getInfrastructureApi().listDatacenters().concat().toList();
    }

    @Override
    public Server getNode(String id) {
        return api.getServerApi().getServer(id);
    }

    @Override
    public void destroyNode(String id) {

    }

    @Override
    public void rebootNode(String id) {

    }

    @Override
    public void resumeNode(String id) {

    }

    @Override
    public void suspendNode(String id) {

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
