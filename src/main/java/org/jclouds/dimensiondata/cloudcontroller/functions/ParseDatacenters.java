package org.jclouds.dimensiondata.cloudcontroller.functions;

import java.net.URI;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.internal.ArgsToPagedIterable;
import org.jclouds.dimensiondata.cloudcontroller.DimensionDataCloudControllerApi;
import org.jclouds.dimensiondata.cloudcontroller.domain.Datacenter;
import org.jclouds.dimensiondata.cloudcontroller.domain.Datacenters;
import org.jclouds.dimensiondata.cloudcontroller.options.PaginationOptions;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.json.Json;

import com.google.common.base.Function;
import com.google.inject.TypeLiteral;

@Singleton
public class ParseDatacenters extends ParseJson<Datacenters> {

    @Inject
    public ParseDatacenters(Json json) {
        super(json, TypeLiteral.get(Datacenters.class));
    }

    public static class ToPagedIterable extends ArgsToPagedIterable<Datacenter, ToPagedIterable> {

        private DimensionDataCloudControllerApi api;

        @Inject
        public ToPagedIterable(DimensionDataCloudControllerApi api) {
            this.api = api;
        }

        @Override
        protected Function<Object, IterableWithMarker<Datacenter>> markerToNextForArgs(List<Object> args) {
            URI endpoint = request.getEndpoint();
            return new Function<Object, IterableWithMarker<Datacenter>>() {
                @Override
                public IterableWithMarker<Datacenter> apply(Object input) {
                    PaginationOptions paginationOptions = PaginationOptions.class.cast(input);
                    return api.getInfrastructureApi().listDatacenters(paginationOptions);
                }
            };
        }
    }
}
