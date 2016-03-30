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
package org.jclouds.dimensiondata.cloudcontroller.utils;

import java.util.List;

import org.jclouds.dimensiondata.cloudcontroller.domain.FirewallRuleTarget;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test(groups = "unit")
public class DimensionDataCloudControllerUtilsTest {

    @Test
    public void testSimplifyPortsWithFullRange() throws Exception {
        int[] ports = new int[65535];
        for (int i = 0; i < ports.length; i++) {
            ports[i] = i + 1;
        }
        List<FirewallRuleTarget.Port> portList = DimensionDataCloudControllerUtils.simplifyPorts(ports);
        Assert.assertEquals(portList.size(), 64);
        for (FirewallRuleTarget.Port port : portList) {
            Assert.assertEquals(port.end() - port.begin(), 1024);
        }
    }

    @Test
    public void testSimplifyPortsWithFewPorts() throws Exception {
        int[] ports = new int[10];
        for (int i = 0; i < ports.length; i++) {
            ports[i] = i + 1;
        }
        List<FirewallRuleTarget.Port> portList = DimensionDataCloudControllerUtils.simplifyPorts(ports);
        Assert.assertEquals(portList.size(), 1);
        for (FirewallRuleTarget.Port port : portList) {
            Assert.assertEquals(port.end() - port.begin(), 9);
        }
    }
}
