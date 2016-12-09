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

import org.jclouds.Fallbacks;
import org.jclouds.dimensiondata.cloudcontroller.domain.PaginatedCollection;
import org.jclouds.dimensiondata.cloudcontroller.domain.Response;
import org.jclouds.dimensiondata.cloudcontroller.domain.Tag;
import org.jclouds.dimensiondata.cloudcontroller.domain.TagInfo;
import org.jclouds.dimensiondata.cloudcontroller.domain.TagKey;
import org.jclouds.dimensiondata.cloudcontroller.filters.OrganisationIdFilter;
import org.jclouds.dimensiondata.cloudcontroller.options.PaginationOptions;
import org.jclouds.dimensiondata.cloudcontroller.parsers.ParseTagKeys;
import org.jclouds.dimensiondata.cloudcontroller.parsers.ParseTags;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.binders.BindToJsonPayload;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.*;

@RequestFilters({ BasicAuthentication.class,
      OrganisationIdFilter.class })
@Consumes(MediaType.APPLICATION_JSON)
@Path("/caas/{jclouds.api-version}/tag")
public interface TagApi {

   @Named("tag:createTagKey")
   @POST
   @Path("/createTagKey")
   @Produces(MediaType.APPLICATION_JSON)
   @MapBinder(BindToJsonPayload.class)
   Response createTagKey(@PayloadParam("name") String name, @PayloadParam("description") String description,
         @PayloadParam("valueRequired") Boolean valueRequired, @PayloadParam("displayOnReport") Boolean displayOnReport);

   @Named("tag:applyTags")
   @POST
   @Path("/applyTags")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   @MapBinder(BindToJsonPayload.class)
   Response applyTags(@PayloadParam("assetId") String assetId, @PayloadParam("assetType") String assetType, @PayloadParam("tagById") List<TagInfo> tagById);

   @Named("tag:removeTags")
   @POST
   @Path("/removeTags")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   @MapBinder(BindToJsonPayload.class)
   Response removeTags(@PayloadParam("assetId") String assetId, @PayloadParam("assetType") String assetType, @PayloadParam("tagKeyId") List<String> tagKeyName);

   @Named("tag:tagKey")
   @GET
   @Path("/tagKey")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   @ResponseParser(ParseTagKeys.class)
   PaginatedCollection<TagKey> listTagKeys(PaginationOptions options);

   @Named("tag:tagKeysById")
   @GET
   @Path("/tagKey/{tagKeyId}")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   TagKey tagKeysById(@PathParam("tagKeyId") String tagKeyId);

   @Named("tag:editTagKey")
   @POST
   @Path("/editTagKey")
   @Produces(MediaType.APPLICATION_JSON)
   @MapBinder(BindToJsonPayload.class)
   Response editTagKey(@PayloadParam("name") String name, @PayloadParam("id") String id, @PayloadParam("description") String description,
         @PayloadParam("valueRequired") Boolean valueRequired, @PayloadParam("displayOnReport") Boolean displayOnReport);

   @Named("tag:deleteTagKey")
   @POST
   @Path("/deleteTagKey")
   @Produces(MediaType.APPLICATION_JSON)
   @MapBinder(BindToJsonPayload.class)
   Response deleteTagKey(@PayloadParam("id") String id);

   @Named("tag:tags")
   @GET
   @Path("/tag")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   @ResponseParser(ParseTags.class)
   PaginatedCollection<Tag> listTags(PaginationOptions options);

}
