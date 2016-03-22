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
package org.jclouds.dimensiondata.cloudcontroller.parse;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.dimensiondata.cloudcontroller.domain.Backup;
import org.jclouds.dimensiondata.cloudcontroller.domain.ConsoleAccess;
import org.jclouds.dimensiondata.cloudcontroller.domain.Datacenter;
import org.jclouds.dimensiondata.cloudcontroller.domain.Datacenters;
import org.jclouds.dimensiondata.cloudcontroller.domain.Hypervisor;
import org.jclouds.dimensiondata.cloudcontroller.domain.Monitoring;
import org.jclouds.dimensiondata.cloudcontroller.domain.Networking;
import org.jclouds.dimensiondata.cloudcontroller.domain.Property;
import org.jclouds.dimensiondata.cloudcontroller.internal.BaseDimensionDataCloudControllerParseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;


@Test(groups = "unit")
public class DatacentersParseTest extends BaseDimensionDataCloudControllerParseTest<Datacenters> {

    @Override
    public String resource() {
        return "/datacenters.json";
    }

    @Override
    @Consumes(MediaType.APPLICATION_JSON)
    public Datacenters expected() {
        List<Datacenter> datacenters = ImmutableList.of(
                Datacenter.builder()
                        .id("NA3")
                        .displayName("US - West")
                        .type("MCP 1.0")
                        .city("Santa Clara")
                        .state("California")
                        .country("US")
                        .vpnUrl("https://na3.cloud-vpn.net")
                        .ftpsHost("ftps-na.cloud-vpn.net")
                        .networking(Networking.builder()
                                .type("1")
                                .maintenanceStatus("NORMAL")
                                .properties(ImmutableList.of(
                                        Property.create("MAX_SERVER_TO_VIP_CONNECTIONS", "20")
                                ))
                                .build()
                        )
                        .hypervisor(Hypervisor.builder()
                                .type("VMWARE")
                                .maintenanceStatus("NORMAL")
                                .properties(
                                        ImmutableList.of(
                                                Property.create("MIN_DISK_SIZE_GB", "10"),
                                                Property.create("MAX_DISK_SIZE_GB", "1000"),
                                                Property.create("MAX_TOTAL_ADDITIONAL_STORAGE_GB", "10000"),
                                                Property.create("MAX_TOTAL_IMAGE_STORAGE_GB", "2600"),
                                                Property.create("MAX_CPU_COUNT", "16"),
                                                Property.create("MIN_MEMORY_GB", "1"),
                                                Property.create("MAX_MEMORY_GB", "128"),
                                                Property.create("VMWARE_HARDWARE_VERSION", "vmx-10"),
                                                Property.create("VLAN_SECURITY_GROUPS_ENABLED", "false")
                                        )
                                )
                                .cpusSpeed(ImmutableList.of(
                                        Hypervisor.CpuSpeed.create(
                                                "STANDARD",
                                                "Standard",
                                                "Standard CPU Speed",
                                                true,
                                                true
                                        )
                                ))
                                .disksSpeed(ImmutableList.of(
                                        Hypervisor.DiskSpeed.create(
                                                "STANDARD",
                                                "Standard",
                                                "STD",
                                                "Standard Disk Speed",
                                                true,
                                                true
                                        ),
                                        Hypervisor.DiskSpeed.create(
                                                "HIGHPERFORMANCE",
                                                "High Performance",
                                                "HPF",
                                                "Faster than Standard. Uses 15000 RPM disk with Fast Cache.",
                                                true,
                                                false
                                        ),
                                        Hypervisor.DiskSpeed.create(
                                                "ECONOMY",
                                                "Economy",
                                                "ECN",
                                                "Slower than Standard. Uses 7200 RPM disk without Fast Cache.",
                                                true,
                                                false
                                        )
                                ))
                                .build())
                        .backup(Backup.create(
                                "COMMVAULT",
                                "NORMAL",
                                ImmutableList.<Property>of()
                        ))
                        .consoleAccess(ConsoleAccess.create(
                                ImmutableList.<Property>of(),
                                "NORMAL"
                        ))
                        .monitoring(Monitoring.create(
                                ImmutableList.<Property>of(),
                                "NORMAL"
                        ))
                        .build(),
                Datacenter.builder()
                        .id("NA9")
                        .displayName("US - East 3 - MCP 2.0")
                        .type("MCP 2.0")
                        .city("Ashburn")
                        .state("Virginia")
                        .country("US")
                        .vpnUrl("https://na9.cloud-vpn.net")
                        .ftpsHost("ftps-na.cloud-vpn.net")
                        .networking(Networking.builder()
                                .type("2")
                                .maintenanceStatus("NORMAL")
                                .properties(ImmutableList.of(
                                        Property.create("MAX_NODE_CONNECTION_LIMIT", "100000"),
                                        Property.create("MAX_NODE_CONNECTION_RATE_LIMIT", "4000"),
                                        Property.create("MAX_VIRTUAL_LISTENER_CONNECTION_LIMIT", "100000"),
                                        Property.create("MAX_VIRTUAL_LISTENER_CONNECTION_RATE_LIMIT", "4000")
                                ))
                                .build()
                        )
                        .hypervisor(Hypervisor.builder()
                                .type("VMWARE")
                                .maintenanceStatus("NORMAL")
                                .properties(
                                        ImmutableList.of(
                                                Property.create("MIN_DISK_SIZE_GB", "10"),
                                                Property.create("MAX_DISK_SIZE_GB", "1000"),
                                                Property.create("MAX_TOTAL_ADDITIONAL_STORAGE_GB", "14000"),
                                                Property.create("MAX_TOTAL_IMAGE_STORAGE_GB", "2600"),
                                                Property.create("MAX_CPU_COUNT", "32"),
                                                Property.create("MIN_MEMORY_GB", "1"),
                                                Property.create("MAX_MEMORY_GB", "256"),
                                                Property.create("VMWARE_HARDWARE_VERSION", "vmx-10"),
                                                Property.create("VLAN_SECURITY_GROUPS_ENABLED", "false")
                                        )
                                )
                                .disksSpeed(ImmutableList.of(
                                        Hypervisor.DiskSpeed.create(
                                                "STANDARD",
                                                "Standard",
                                                "STD",
                                                "Standard Disk Speed",
                                                true,
                                                true
                                        ),
                                        Hypervisor.DiskSpeed.create(
                                                "HIGHPERFORMANCE",
                                                "High Performance",
                                                "HPF",
                                                "Faster than Standard. Uses 15000 RPM disk with Fast Cache.",
                                                true,
                                                false
                                        ),
                                        Hypervisor.DiskSpeed.create(
                                                "ECONOMY",
                                                "Economy",
                                                "ECN",
                                                "Slower than Standard. Uses 7200 RPM disk without Fast Cache.",
                                                true,
                                                false
                                        )
                                ))
                                .cpusSpeed(ImmutableList.of(
                                        Hypervisor.CpuSpeed.create(
                                                "STANDARD",
                                                "Standard",
                                                "Standard CPU Speed",
                                                true,
                                                true
                                        ),
                                        Hypervisor.CpuSpeed.create(
                                                "HIGHPERFORMANCE",
                                                "High Performance",
                                                "Faster and more consistent than Standard. Suitable for applications that are more CPU intensive.",
                                                true,
                                                false
                                        )
                                ))
                                .build()
                        )
                        .backup(Backup.create(
                                "COMMVAULT",
                                "NORMAL",
                                ImmutableList.<Property>of()
                        ))
                        .consoleAccess(ConsoleAccess.create(
                                ImmutableList.<Property>of(),
                                "NORMAL"
                        ))
                        .monitoring(Monitoring.create(
                                ImmutableList.<Property>of(),
                                "NORMAL"
                        ))
                        .build()
        );
        return new Datacenters(datacenters, 1, 5, 5, 250);
    }
}
