package org.jclouds.dimensiondata.cloudcontroller.functions;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.dimensiondata.cloudcontroller.domain.OperatingSystems;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.json.Json;

import com.google.inject.TypeLiteral;

@Singleton
public class ParseOperatingSystems extends ParseJson<OperatingSystems> {

    @Inject
    public ParseOperatingSystems(Json json) {
        super(json, TypeLiteral.get(OperatingSystems.class));
    }
}
