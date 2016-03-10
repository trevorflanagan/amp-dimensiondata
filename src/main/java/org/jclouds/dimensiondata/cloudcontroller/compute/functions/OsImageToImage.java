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
package org.jclouds.dimensiondata.cloudcontroller.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.get;

import javax.inject.Singleton;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.dimensiondata.cloudcontroller.domain.OsImage;

import com.google.common.base.Function;
import com.google.common.base.Splitter;

@Singleton
public class OsImageToImage implements Function<OsImage, Image> {


    private static final String CENTOS = "CENTOS";
    private static final String REDHAT = "REDHAT";
    private static final String UBUNTU = "UBUNTU";

    @Override
    public Image apply(OsImage from) {

        checkNotNull(from, "image");

        OsFamily osFamily = osFamily().apply(from.operatingSystem().id());
        String osVersion = parseVersion(from.operatingSystem().id());

        OperatingSystem os = OperatingSystem.builder()
                .description(from.description())
                .family(osFamily)
                .version(osVersion)
                .is64Bit(is64bit(from))
                .build();

        return new ImageBuilder()
                .id(from.id())
                .name(from.name())
                .status(Image.Status.AVAILABLE)
                .operatingSystem(os)
                .build();
    }

    private boolean is64bit(OsImage inspectedImage) {
        return inspectedImage.operatingSystem().id().contains("64");
    }

    private Function<String, OsFamily> osFamily() {
        return new Function<String, OsFamily>() {

            @Override
            public OsFamily apply(final String id) {
                if (id != null) {
                    if (id.startsWith(CENTOS)) return OsFamily.CENTOS;
                    else if (id.contains(UBUNTU)) return OsFamily.UBUNTU;
                    else if (id.contains(REDHAT)) return OsFamily.RHEL;
                }
                return OsFamily.UNRECOGNIZED;
            }
        };
    }

    private String parseVersion(String id) {
        String idWithoutArch = get(Splitter.on("/").split(id), 0);
        return idWithoutArch.substring(idWithoutArch.length() - 1);
    }
}
