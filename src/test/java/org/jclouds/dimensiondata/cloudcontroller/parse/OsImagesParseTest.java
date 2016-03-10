/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.dimensiondata.cloudcontroller.parse;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.dimensiondata.cloudcontroller.domain.CPU;
import org.jclouds.dimensiondata.cloudcontroller.domain.Disk;
import org.jclouds.dimensiondata.cloudcontroller.domain.OperatingSystem;
import org.jclouds.dimensiondata.cloudcontroller.domain.OsImage;
import org.jclouds.dimensiondata.cloudcontroller.domain.OsImages;
import org.jclouds.dimensiondata.cloudcontroller.domain.Servers;
import org.jclouds.dimensiondata.cloudcontroller.internal.BaseDimensionDataCloudControllerParseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

import autovalue.shaded.com.google.common.common.collect.Lists;


@Test(groups = "unit")
public class OsImagesParseTest extends BaseDimensionDataCloudControllerParseTest<OsImages> {

    @Override
    public String resource() {
        return "/osImages.json";
    }

    @Override
    @Consumes(MediaType.APPLICATION_JSON)
    public OsImages expected() {
        List<OsImage> osImages = ImmutableList.of(
                OsImage.builder()
                        .id("15c3fef6-be0e-44c7-836d-f7af10074fcc")
                        .name("RedHat 7 64-bit 2 CPU")
                        .description("RedHat 7.2 Enterprise (Maipo) 64-bit")
                        .operatingSystem(OperatingSystem.create(
                                "REDHAT764",
                                "REDHAT7/64",
                                "UNIX"
                        ))
                        .cpu(CPU.create(
                                2,
                                "STANDARD",
                                1
                        ))
                        .memoryGb(4)
                        .disks(ImmutableList.of(
                                Disk.builder()
                                        .id("98299851-37a3-4ebe-9cf1-090da9ae42a0")
                                        .scsiId(0)
                                        .sizeGb(20)
                                        .speed("STANDARD")
                                        .build()
                                )
                        )
                        .softwareLabels(Lists.newArrayList())
                        .osImageKey("T-RHEL-7-64-2-4-20")
                        .createTime("2016-02-10T07:18:17.000Z")
                        .datacenterId("NA1")
                        .build()
        );
        return new OsImages(osImages, 1, 1, 1, 250);
    }
}
