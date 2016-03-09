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

import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Template;
import org.jclouds.dimensiondata.cloudcontroller.domain.Datacenter;
import org.jclouds.dimensiondata.cloudcontroller.domain.OperatingSystem;
import org.jclouds.dimensiondata.cloudcontroller.domain.Server;

/**
 * defines the connection between the {@link org.jclouds.dimensiondata.cloudcontroller.DimensionDataCloudControllerApi} implementation and
 * the jclouds {@link org.jclouds.compute.ComputeService}
 *
 */
@Singleton
public class DimensionDataCloudControllerComputeServiceAdapter implements
        ComputeServiceAdapter<Server, Hardware, OperatingSystem, Datacenter> {

    @Override
    public NodeAndInitialCredentials<Server> createNodeWithGroupEncodedIntoName(String group, String name, Template template) {
        return null;
    }

    @Override
    public Iterable<Hardware> listHardwareProfiles() {
        return null;
    }

    @Override
    public Iterable<OperatingSystem> listImages() {
        return null;
    }

    @Override
    public OperatingSystem getImage(String id) {
        return null;
    }

    @Override
    public Iterable<Datacenter> listLocations() {
        return null;
    }

    @Override
    public Server getNode(String id) {
        return null;
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
        return null;
    }

    @Override
    public Iterable<Server> listNodesByIds(Iterable<String> ids) {
        return null;
    }
}
