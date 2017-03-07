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
package org.jclouds.dimensiondata.cloudcontroller.filters;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.jclouds.dimensiondata.cloudcontroller.features.AccountApi;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;

/**
 * Accepts requests and modifies the endpoint path so that it is injected with the organisation id.
 * Handles both oec and caas based URLs.
 */
@Singleton
public class OrganisationIdFilter implements HttpRequestFilter {

   private static final String CAAS = "caas";
   private static final String OEC = "oec";
   private static String ORG_ID;

   @Inject
   public OrganisationIdFilter(AccountApi accountApi) {
      ORG_ID = accountApi.getMyAccount().orgId();
   }

   @Override public HttpRequest filter(HttpRequest request) throws HttpException {
      return request.toBuilder().replacePath(injectOrganisationId(request.getEndpoint().getPath())).build();
   }

   @VisibleForTesting
   public String injectOrganisationId(String path) {

      int indexOfApiBase = path.indexOf(CAAS);
      int indexOfVersionParam;
      if (indexOfApiBase != -1) {
         // looks for index of next slash after the slash preceding the API version
         indexOfVersionParam = path.indexOf("/", indexOfApiBase + 5);
      } else {
         indexOfApiBase = path.indexOf(OEC);
         // looks for index of next slash after the slash preceding the API version
         indexOfVersionParam = path.indexOf("/", indexOfApiBase + 4);
      }
      String substring = path.substring(0, indexOfVersionParam);
               substring = substring + "/" + ORG_ID;
      return substring + path.substring(indexOfVersionParam, path.length());
   }

}
