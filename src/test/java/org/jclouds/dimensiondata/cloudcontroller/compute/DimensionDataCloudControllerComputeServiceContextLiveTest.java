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

import static org.assertj.core.api.Assertions.assertThat;
import static org.jclouds.compute.predicates.NodePredicates.inGroup;
import static org.jclouds.compute.predicates.NodePredicates.runningInGroup;

import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.RunScriptOnNodesException;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.dimensiondata.cloudcontroller.compute.options.DimensionDataCloudControllerTemplateOptions;
import org.jclouds.domain.Location;
import org.jclouds.logging.Logger;
import org.jclouds.scriptbuilder.statements.login.AdminAccess;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

@Test(groups = "live", testName = "DimensionDataCloudControllerComputeServiceContextLiveTest")
public class DimensionDataCloudControllerComputeServiceContextLiveTest extends BaseComputeServiceContextLiveTest {

    private static final int NUM_NODES = 1;

    @Resource
    @Named(ComputeServiceConstants.COMPUTE_LOGGER)
    protected Logger logger = Logger.NULL;

    public DimensionDataCloudControllerComputeServiceContextLiveTest() {
        provider = "dimensiondata-cloudcontroller";
    }

    @Test
    public void testListHardwareProfiles() {
        for (Hardware hardware : view.getComputeService().listHardwareProfiles()) {
            System.out.println(hardware);
        }
    }

    @Test
    public void testListAvailableLocations() throws RunNodesException {
        for (Location location : view.getComputeService().listAssignableLocations()) {
            System.out.println(location);
        }
    }

    @Test
    public void testListImages() throws RunNodesException {
        for (Image image : view.getComputeService().listImages()) {
            System.out.println(image);
        }
    }

    @Test
    public void testListNodes() throws RunNodesException {
        for (ComputeMetadata node : view.getComputeService().listNodes()) {
            System.out.println(node);
        }
    }

    @Test
    public void testLaunchClusterWithDomainName() throws RunNodesException {
        final String name = "test";

        Template template = view.getComputeService().templateBuilder()
                .imageId("58f67195-1b0a-4b79-ae67-550896c2d22b")
                .locationId("NA12")
                .build();
        DimensionDataCloudControllerTemplateOptions options = template.getOptions().as(DimensionDataCloudControllerTemplateOptions.class);
        options.inboundPorts(22, 8080, 8081, 8082)
                .runScript(AdminAccess.standard());

        try {
            Set<? extends NodeMetadata> nodes = view.getComputeService().createNodesInGroup(name, NUM_NODES, template);
            assertThat(nodes).hasSize(NUM_NODES);

            Map<? extends NodeMetadata, ExecResponse> responses = view.getComputeService().runScriptOnNodesMatching(runningInGroup(name), "echo hello");
            assertThat(responses).hasSize(NUM_NODES);

            for (ExecResponse execResponse : responses.values()) {
                assertThat(execResponse.getOutput().trim()).isEqualTo("hello");
            }
        } catch (RunScriptOnNodesException e) {
            Assert.fail();
        } finally {
            view.getComputeService().destroyNodesMatching(inGroup(name));
        }
}

    @Override
    protected Iterable<Module> setupModules() {
        return ImmutableSet.<Module>of(getLoggingModule(), new SshjSshClientModule());
    }

}
