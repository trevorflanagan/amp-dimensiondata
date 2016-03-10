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
package org.jclouds.dimensiondata.cloudcontroller.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.FluentIterable.from;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.dimensiondata.cloudcontroller.domain.Server;
import org.jclouds.domain.Location;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.location.predicates.LocationPredicates;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

@Singleton
public class ServerToNodeMetadata implements Function<Server, NodeMetadata> {

    private final Supplier<Set<? extends Location>> locations;
    private final GroupNamingConvention nodeNamingConvention;
    private final OsImageToImage osImageToImage;
    private final OsImageToHardware osImageToHardware;


    @Inject
    ServerToNodeMetadata(@Memoized Supplier<Set<? extends Location>> locations,
                               GroupNamingConvention.Factory namingConvention, OsImageToImage osImageToImage,
                               OsImageToHardware osImageToHardware) {
        this.nodeNamingConvention = checkNotNull(namingConvention, "namingConvention").createWithoutPrefix();
        this.locations = checkNotNull(locations, "locations");
        this.osImageToImage = checkNotNull(osImageToImage, "osImageToImage");
        this.osImageToHardware = checkNotNull(osImageToHardware, "osImageToHardware");
    }

    @Override
    public NodeMetadata apply(Server from) {
        NodeMetadataBuilder builder = new NodeMetadataBuilder();
        builder.ids(from.id());
        builder.name(from.name());
        // TODO
        //builder.hostname(from.getFullyQualifiedDomainName());
        if (from.datacenterId() != null) {
            builder.location(from(locations.get()).firstMatch(
                    LocationPredicates.idEquals(from.datacenterId())).orNull());
        }
        builder.group(nodeNamingConvention.groupInUniqueNameOrNull(from.name()));
        // TODO
        /*
        builder.hardware(osImageToHardware.apply(from));
        Image image = osImageToImage.apply(from);
        if (image != null) {
            builder.imageId(image.getId());
            builder.operatingSystem(image.getOperatingSystem());
        }
        */
        if (from.started()) {
            builder.status(NodeMetadata.Status.RUNNING);
        } else {
            builder.status(NodeMetadata.Status.UNRECOGNIZED);
        }

        if (from.nic().privateIpv4() != null)
            builder.privateAddresses(ImmutableSet.of(from.nic().privateIpv4()));
        // TODO
        /*
        if (from.getPrimaryBackendIpAddress() != null)
            builder.publicAddresses(ImmutableSet.of(from.getPrimaryIpAddress()));
        */
        // TODO credentials


        return builder.build();
    }
}
