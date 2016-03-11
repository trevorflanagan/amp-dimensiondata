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
package org.jclouds.dimensiondata.cloudcontroller.predicates;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Resource;

import org.jclouds.dimensiondata.cloudcontroller.domain.Server;
import org.jclouds.dimensiondata.cloudcontroller.features.ServerApi;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;

public class ServerDeployed implements Predicate<String> {

   @Resource
   protected Logger logger = Logger.NULL;

   private final ServerApi serverApi;

   public ServerDeployed(ServerApi serverApi) {
      this.serverApi = serverApi;
   }

   @Override
   public boolean apply(String serverId) {
      checkNotNull(serverId, "serverId");
      logger.trace("looking for state on server %s", serverId);

      Server server = serverApi.getServer(serverId);

      // perhaps request isn't available, yet
      if (server == null) return false;
      logger.trace("%s: looking for server %s deployed: currently: %s", server, server.state());
      return server.deployed();
   }

   @Override
   public String toString() {
      return "requestServerDeployed()";
   }
}
