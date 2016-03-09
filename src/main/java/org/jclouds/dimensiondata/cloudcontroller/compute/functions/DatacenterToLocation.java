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

import javax.inject.Singleton;

import org.jclouds.dimensiondata.cloudcontroller.domain.Datacenter;
import org.jclouds.domain.Location;

import com.google.common.base.Function;

@Singleton
public class DatacenterToLocation implements Function<Datacenter, Location> {

    @Override
    public Location apply(Datacenter input) {
        return null;
    }
}
