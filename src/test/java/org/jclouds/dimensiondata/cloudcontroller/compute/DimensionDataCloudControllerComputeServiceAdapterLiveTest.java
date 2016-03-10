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

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.net.HostAndPort;
import com.google.inject.Injector;
import com.google.inject.Module;
import org.jclouds.compute.ComputeServiceAdapter.NodeAndInitialCredentials;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.functions.DefaultCredentialsFromImageOrOverridingCredentials;
import org.jclouds.compute.strategy.PrioritizeCredentialsFromTemplate;
import org.jclouds.dimensiondata.cloudcontroller.DimensionDataCloudControllerApi;
import org.jclouds.dimensiondata.cloudcontroller.compute.strategy.DimensionDataCloudControllerComputeServiceAdapter;
import org.jclouds.dimensiondata.cloudcontroller.domain.Datacenter;
import org.jclouds.dimensiondata.cloudcontroller.domain.OsImage;
import org.jclouds.dimensiondata.cloudcontroller.domain.Server;
import org.jclouds.dimensiondata.cloudcontroller.internal.BaseDimensionDataCloudControllerApiLiveTest;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.SshClient.Factory;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.Test;

import java.util.Properties;
import java.util.Random;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

@Test(groups = "live", singleThreaded = true, testName = "SoftLayerComputeServiceAdapterLiveTest")
public class DimensionDataCloudControllerComputeServiceAdapterLiveTest extends BaseDimensionDataCloudControllerApiLiveTest {

   private DimensionDataCloudControllerComputeServiceAdapter adapter;
   private TemplateBuilder templateBuilder;
   private Factory sshFactory;
   private NodeAndInitialCredentials<Server> guest;

   @Override
   protected DimensionDataCloudControllerApi create(Properties props, Iterable<Module> modules) {
      Injector injector = newBuilder().modules(modules).overrides(props).buildInjector();
      adapter = injector.getInstance(DimensionDataCloudControllerComputeServiceAdapter.class);
      templateBuilder = injector.getInstance(TemplateBuilder.class);
      sshFactory = injector.getInstance(SshClient.Factory.class);
      return injector.getInstance(DimensionDataCloudControllerApi.class);
   }

   @Test
   public void testListLocations() {
      Iterable<Datacenter> locations = adapter.listLocations();
      assertFalse(Iterables.isEmpty(locations), "locations must not be empty");
   }

   @Test
   public void testListImages() {
      assertFalse(Iterables.isEmpty(adapter.listImages()), "images must not be empty");
   }

   private static final PrioritizeCredentialsFromTemplate prioritizeCredentialsFromTemplate = new PrioritizeCredentialsFromTemplate(
           new DefaultCredentialsFromImageOrOverridingCredentials());

   @Test
   public void testCreateNodeWithGroupEncodedIntoNameThenStoreCredentials() {
      String group = "foo";
      String name = "node" + new Random().nextInt();

      Template template = templateBuilder.build();
      // test passing custom options
      //template.getOptions().as(SoftLayerTemplateOptions.class).domainName("me.org");

      guest = adapter.createNodeWithGroupEncodedIntoName(group, name, template);
      /*
      assertEquals(guest.getNode().getHostname(), name);
      assertEquals(guest.getNodeId(), guest.getNode().getId() + "");
      assertEquals(guest.getNode().getDomain(), template.getOptions().as(SoftLayerTemplateOptions.class)
              .getDomainName());
      assert InetAddresses.isInetAddress(guest.getNode().getPrimaryBackendIpAddress()) : guest;
      doConnectViaSsh(guest.getNode(), prioritizeCredentialsFromTemplate.apply(template, guest.getCredentials()));
      */
   }

   protected void doConnectViaSsh(Server guest, LoginCredentials creds) {
      SshClient ssh = sshFactory.create(HostAndPort.fromParts(guest.nic().privateIpv4(), 22), creds);
      try {
         ssh.connect();
         ExecResponse hello = ssh.exec("echo hello");
         assertEquals(hello.getOutput().trim(), "hello");
         System.err.println(ssh.exec("df -k").getOutput());
         System.err.println(ssh.exec("mount").getOutput());
         System.err.println(ssh.exec("uname -a").getOutput());
      } finally {
         if (ssh != null)
            ssh.disconnect();
      }
   }

   @Test
   public void testListHardwareProfiles() {
      Iterable<OsImage> profiles = adapter.listHardwareProfiles();
      assertFalse(Iterables.isEmpty(profiles));

      for (OsImage profile : profiles) {
         assertNotNull(profile);
      }
   }

   @AfterGroups(groups = "live")
   protected void tearDown() {
      if (guest != null) {
         adapter.destroyNode(guest.getNodeId() + "");
      }
      super.tearDown();
   }

   @Override
   protected Iterable<Module> setupModules() {
      return ImmutableSet.<Module> of(getLoggingModule(), new SshjSshClientModule());
   }

   @Override
   protected Properties setupProperties() {
      Properties properties = super.setupProperties();
      properties.setProperty("jclouds.ssh.max-retries", "10");
      return properties;
   }
}
