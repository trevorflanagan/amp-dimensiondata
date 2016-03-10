package org.jclouds.dimensiondata.cloudcontroller.features;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks;
import org.jclouds.collect.PagedIterable;
import org.jclouds.dimensiondata.cloudcontroller.domain.PaginatedCollection;
import org.jclouds.dimensiondata.cloudcontroller.domain.Server;
import org.jclouds.dimensiondata.cloudcontroller.options.PaginationOptions;
import org.jclouds.dimensiondata.cloudcontroller.parsers.ParseServers;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.Transform;

@RequestFilters({BasicAuthentication.class})
@Consumes(MediaType.APPLICATION_JSON)
@Path("/server")
public interface ServerApi {

    @Named("server:list")
    @GET
    @Path("/server")
    @ResponseParser(ParseServers.class)
    @Fallback(Fallbacks.EmptyIterableWithMarkerOnNotFoundOr404.class)
    PaginatedCollection<Server> listServers(PaginationOptions options);

    @Named("server:list")
    @GET
    @Path("/server")
    @Transform(ParseServers.ToPagedIterable.class)
    @ResponseParser(ParseServers.class)
    @Fallback(Fallbacks.EmptyPagedIterableOnNotFoundOr404.class)
    PagedIterable<Server> listServers();

}