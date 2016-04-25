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
package org.jclouds.dimensiondata.cloudcontroller.compute.functions;

import javax.inject.Singleton;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.internal.VolumeImpl;
import org.jclouds.dimensiondata.cloudcontroller.domain.Disk;
import org.jclouds.dimensiondata.cloudcontroller.domain.OsImage;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;

@Singleton
public class OsImageToHardware implements Function<OsImage, Hardware> {

   @Override
   public Hardware apply(final OsImage from) {
      HardwareBuilder builder = new HardwareBuilder().ids(from.id())
              .name(from.name())
              .hypervisor("vmx")
              .processors(ImmutableList.of(new Processor(from.cpu().count(), from.cpu().coresPerSocket())))
              .ram(from.memoryGb() * 1024);

      if (from.disks() != null) {
         builder.volumes(
                 FluentIterable.from(from.disks())
                         .transform(new Function<Disk, Volume>() {
                            @Override
                            public Volume apply(Disk disk) {
                               float volumeSize = disk.sizeGb();
                               return new VolumeImpl(
                                       disk.id(),
                                       // TODO check other supported types
                                       Volume.Type.LOCAL,
                                       volumeSize, null, true, false);
                            }
                         }).toSet());
      }
      return builder.build();
   }
}
