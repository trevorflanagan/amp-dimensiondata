package org.jclouds.dimensiondata.cloudcontroller.features;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks;
import org.jclouds.collect.PagedIterable;
import org.jclouds.dimensiondata.cloudcontroller.domain.OsImage;
import org.jclouds.dimensiondata.cloudcontroller.domain.PaginatedCollection;
import org.jclouds.dimensiondata.cloudcontroller.options.PaginationOptions;
import org.jclouds.dimensiondata.cloudcontroller.parsers.ParseOsImages;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.Transform;

@RequestFilters({BasicAuthentication.class})
@Consumes(MediaType.APPLICATION_JSON)
@Path("/image")
public interface ServerImageApi {

    @Named("image:list")
    @GET
    @Path("/osImage")
    @ResponseParser(ParseOsImages.class)
    @Fallback(Fallbacks.EmptyIterableWithMarkerOnNotFoundOr404.class)
    PaginatedCollection<OsImage> listOsImages(PaginationOptions options);

    @Named("image:list")
    @GET
    @Path("/osImage")
    @Transform(ParseOsImages.ToPagedIterable.class)
    @ResponseParser(ParseOsImages.class)
    @Fallback(Fallbacks.EmptyPagedIterableOnNotFoundOr404.class)
    PagedIterable<OsImage> listOsImages();

    @Named("image:get")
    @GET
    @Path("/osImage/{id}")
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    OsImage getOsImage(@PathParam("id") String id);
}