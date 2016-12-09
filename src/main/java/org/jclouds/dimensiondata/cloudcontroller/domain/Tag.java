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
public abstract class Tag {

   Tag() {}

   public abstract String assetType();
   public abstract String assetId();
   @Nullable
   public abstract String assetName();
   @Nullable
   public abstract String datacenterId();
   public abstract String tagKeyId();
   public abstract String tagKeyName();
   @Nullable
   public abstract String value();
   public abstract Boolean valueRequired();
   @Nullable
   public abstract Boolean displayOnReport();

   @SerializedNames({ "assetType", "assetId" , "assetName" , "datacenterId", "tagKeyId", "tagKeyName", "value", "valueRequired" ,"displayOnReport"})
      public static Tag create(String assetType, String assetId, String assetName, String datacenterId, String tagKeyId, String tagKeyName, String value, Boolean valueRequired, Boolean displayOnReport) {
          return builder().assetType(assetType).assetId(assetId).assetName(assetName).datacenterId(datacenterId).tagKeyId(tagKeyId).tagKeyName(tagKeyName).value(value).valueRequired(valueRequired).displayOnReport(displayOnReport).build();
      }

      public abstract Tag.Builder toBuilder();

      @AutoValue.Builder
      public abstract static class Builder {
          public abstract Tag.Builder assetType(String assetType);
          public abstract Tag.Builder assetId(String assetId);
          public abstract Tag.Builder assetName(String assetName);
          public abstract Tag.Builder datacenterId(String datacenterId);
          public abstract Tag.Builder tagKeyId(String tagKeyId);
          public abstract Tag.Builder tagKeyName(String tagKeyName);
          public abstract Tag.Builder value(String value);
          public abstract Tag.Builder valueRequired(Boolean valueRequired);
          public abstract Tag.Builder displayOnReport(Boolean displayOnReport);
          public abstract Tag build();

      }

      public static Tag.Builder builder() {
          return new AutoValue_Tag.Builder();
      }

}
