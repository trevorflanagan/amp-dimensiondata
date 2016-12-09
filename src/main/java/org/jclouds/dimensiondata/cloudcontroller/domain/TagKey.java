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
package org.jclouds.dimensiondata.cloudcontroller.domain;

import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class TagKey {

   TagKey() {}

   public abstract String id();
   public abstract String name();
   @Nullable
   public abstract String description();
   public abstract Boolean valueRequired();
   @Nullable
   public abstract Boolean displayOnReport();

   @SerializedNames({ "id", "name", "description" , "valueRequired" , "displayOnReport" })
      public static TagKey create(String id, String name, String description, Boolean valueRequired, Boolean displayOnReport) {
          return builder().id(id).name(name).description(description).valueRequired(valueRequired).displayOnReport(displayOnReport).build();
      }

      public abstract TagKey.Builder toBuilder();

      @AutoValue.Builder
      public abstract static class Builder {
         public abstract TagKey.Builder id(String id);
          public abstract TagKey.Builder name(String tagKeyName);
          public abstract TagKey.Builder description(String description);
          public abstract TagKey.Builder valueRequired(Boolean valueRequired);
          public abstract TagKey.Builder displayOnReport(Boolean displayOnReport);
          public abstract TagKey build();

      }

      public static TagKey.Builder builder() {
          return new AutoValue_TagKey.Builder();
      }
}
