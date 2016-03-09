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
package org.jclouds.dimensiondata.cloudcontroller;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import org.jclouds.Constants;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.config.ComputeServiceProperties;
import org.jclouds.dimensiondata.cloudcontroller.compute.config.DimensionDataCloudControllerComputeServiceContextModule;
import org.jclouds.dimensiondata.cloudcontroller.config.DimensionDataCloudControllerHttpApiModule;
import org.jclouds.dimensiondata.cloudcontroller.config.DimensionDataCloudControllerParserModule;
import org.jclouds.http.okhttp.config.OkHttpCommandExecutorServiceModule;
import org.jclouds.rest.internal.BaseHttpApiMetadata;

import java.net.URI;
import java.util.Properties;

import static org.jclouds.compute.config.ComputeServiceProperties.TEMPLATE;
import static org.jclouds.reflect.Reflection2.typeToken;

@AutoService(ApiMetadata.class)
public class DimensionDataCloudControllerApiMetadata extends BaseHttpApiMetadata<DimensionDataCloudControllerApi> {

    public static final String DIMENSIONDATA_ORG_ID = "dimensiondata.org-id";

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public DimensionDataCloudControllerApiMetadata() {
      this(new Builder());
   }

   protected DimensionDataCloudControllerApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseHttpApiMetadata.defaultProperties();
      properties.setProperty(Constants.PROPERTY_CONNECTION_TIMEOUT, "1200000"); // 15 minutes
      properties.setProperty(ComputeServiceProperties.IMAGE_LOGIN_USER, "root:password");
      properties.setProperty(TEMPLATE, "osFamily=UBUNTU,os64Bit=true");
      properties.setProperty(DIMENSIONDATA_ORG_ID, "");
      return properties;
   }

   public static class Builder extends BaseHttpApiMetadata.Builder<DimensionDataCloudControllerApi, Builder> {

      protected Builder() {
         super(DimensionDataCloudControllerApi.class);
         id("dimensiondata-cloudcontroller")
                 .name("DimensionData CloudController API")
                 .identityName("user name")
                 .credentialName("user password")
                 .documentation(URI.create("http://www.dimensiondata.com/en-US/Solutions/Cloud"))
                 .version("2.1")
                 .defaultEndpoint("https://api-REGION.dimensiondata.com/caas/2.1/ORG-ID")
                 .defaultProperties(DimensionDataCloudControllerApiMetadata.defaultProperties())
                 .view(typeToken(ComputeServiceContext.class))
                 .defaultModules(ImmutableSet.<Class<? extends Module>>of(
                         DimensionDataCloudControllerParserModule.class,
                         DimensionDataCloudControllerHttpApiModule.class,
                         OkHttpCommandExecutorServiceModule.class,
                         DimensionDataCloudControllerComputeServiceContextModule.class));
      }

      @Override
      public DimensionDataCloudControllerApiMetadata build() {
         return new DimensionDataCloudControllerApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }

      @Override
      public Builder fromApiMetadata(ApiMetadata in) {
         return this;
      }
   }
}
