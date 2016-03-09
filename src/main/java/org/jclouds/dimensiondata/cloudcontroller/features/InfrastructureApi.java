package org.jclouds.dimensiondata.cloudcontroller.features;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks;
import org.jclouds.collect.PagedIterable;
import org.jclouds.dimensiondata.cloudcontroller.compute.functions.OperatingSystemsToPagedIterable;
import org.jclouds.dimensiondata.cloudcontroller.domain.Datacenter;
import org.jclouds.dimensiondata.cloudcontroller.domain.OperatingSystem;
import org.jclouds.dimensiondata.cloudcontroller.domain.PaginatedCollection;
import org.jclouds.dimensiondata.cloudcontroller.functions.ParseDatacenters;
import org.jclouds.dimensiondata.cloudcontroller.functions.ParseOperatingSystems;
import org.jclouds.dimensiondata.cloudcontroller.options.PaginationOptions;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.Transform;

@RequestFilters({BasicAuthentication.class})
@Consumes(MediaType.APPLICATION_JSON)
@Path("/infrastructure")
public interface InfrastructureApi {

    @Named("infrastructure:datacenter")
    @GET
    @Path("/datacenter")
    @ResponseParser(ParseDatacenters.class)
    @Fallback(Fallbacks.EmptyIterableWithMarkerOnNotFoundOr404.class)
    PaginatedCollection<Datacenter> listDatacenters(PaginationOptions options);

    @Named("infrastructure:datacenter")
    @GET
    @Path("/datacenter")
    @Transform(ParseDatacenters.ToPagedIterable.class)
    @ResponseParser(ParseDatacenters.class)
    @Fallback(Fallbacks.EmptyPagedIterableOnNotFoundOr404.class)
    PagedIterable<Datacenter> listDatacenters();

    @Named("infrastructure:operatingSystem")
    @GET
    @Path("/operatingSystem")
    @ResponseParser(ParseOperatingSystems.class)
    @Fallback(Fallbacks.EmptyIterableWithMarkerOnNotFoundOr404.class)
    PaginatedCollection<OperatingSystem> listOperatingSystems(@QueryParam("datacenterId") String datacenterId, PaginationOptions options);

    @Named("infrastructure:operatingSystem")
    @GET
    @Path("/operatingSystem")
    @Transform(OperatingSystemsToPagedIterable.class)
    @ResponseParser(ParseOperatingSystems.class)
    @Fallback(Fallbacks.EmptyPagedIterableOnNotFoundOr404.class)
    PagedIterable<OperatingSystem> listOperatingSystems(@QueryParam("datacenterId") String datacenterId);

}
