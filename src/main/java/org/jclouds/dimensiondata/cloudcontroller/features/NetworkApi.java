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
package org.jclouds.dimensiondata.cloudcontroller.features;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks;
import org.jclouds.collect.PagedIterable;
import org.jclouds.dimensiondata.cloudcontroller.compute.functions.NetworkDomainsToPagedIterable;
import org.jclouds.dimensiondata.cloudcontroller.domain.NetworkDomain;
import org.jclouds.dimensiondata.cloudcontroller.domain.PaginatedCollection;
import org.jclouds.dimensiondata.cloudcontroller.functions.ParseNetworkDomains;
import org.jclouds.dimensiondata.cloudcontroller.options.PaginationOptions;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.Transform;

@Consumes(MediaType.APPLICATION_JSON)
@Path("/network")
public interface NetworkApi {

    @Named("network:list")
    @GET
    @Path("/networkDomain")
    @ResponseParser(ParseNetworkDomains.class)
    @Fallback(Fallbacks.EmptyIterableWithMarkerOnNotFoundOr404.class)
    PaginatedCollection<NetworkDomain> listNetworkDomains(PaginationOptions options);

    @Named("network:list")
    @GET
    @Path("/networkDomain")
    @Transform(NetworkDomainsToPagedIterable.class)
    @ResponseParser(ParseNetworkDomains.class)
    @Fallback(Fallbacks.EmptyPagedIterableOnNotFoundOr404.class)
    PagedIterable<NetworkDomain> listNetworkDomains();
}
