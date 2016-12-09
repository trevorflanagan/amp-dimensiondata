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
package org.jclouds.dimensiondata.cloudcontroller.features;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import org.jclouds.dimensiondata.cloudcontroller.domain.PaginatedCollection;
import org.jclouds.dimensiondata.cloudcontroller.domain.Response;
import org.jclouds.dimensiondata.cloudcontroller.domain.Tag;
import org.jclouds.dimensiondata.cloudcontroller.domain.TagInfo;
import org.jclouds.dimensiondata.cloudcontroller.domain.TagKey;
import org.jclouds.dimensiondata.cloudcontroller.internal.BaseDimensionDataCloudControllerApiLiveTest;
import org.jclouds.dimensiondata.cloudcontroller.options.PaginationOptions;
import org.jclouds.dimensiondata.cloudcontroller.utils.DimensionDataCloudControllerUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.*;

import static org.testng.Assert.*;

@Test(groups = "live", testName = "AccountApiLiveTest", singleThreaded = true)
public class TagApiLiveTest  extends BaseDimensionDataCloudControllerApiLiveTest {

   private String tagKeyId;
   private String tagKeyName;
   private String assetType = "SERVER";
   private final String assetId = "b8201405-bf9c-4896-b9cb-97fce95553a1";

   @BeforeClass
   public void setup(){
      super.setup();
      createTagKeyIfNotExist();
      applyTagToAsset();
   }

   private Response applyTagToAsset() {
      if(tagKeyId != null){
         Response response = api().applyTags(assetId, assetType,
                    Collections.singletonList(TagInfo.create(tagKeyId, "jcloudsValue")));
         return response;
      }
      return null;
   }

   @Test
   public void testCreateTagKey()
   {
      String keyName = "jcloudsTagKeyName" + System.currentTimeMillis();
      Response response = api().createTagKey(keyName, "jcloudsTagKeyDescription", Boolean.TRUE, Boolean.FALSE);
      assertNotNull(DimensionDataCloudControllerUtils.tryFindPropertyValue(response, "tagKeyId"), "No tagKeyId returned");
      String keyId = DimensionDataCloudControllerUtils.tryFindPropertyValue(response, "tagKeyId");
      assertTagKeyExistsAndIsValid(keyId, keyName, "jcloudsTagKeyDescription", Boolean.TRUE, Boolean.FALSE);
   }

   private Response createTagKey() {
      tagKeyName = "jcloudsTagKeyName" + System.currentTimeMillis();
      Response response = api().createTagKey(tagKeyName, "jcloudsTagKeyDescription", Boolean.TRUE, Boolean.FALSE);
      tagKeyId = DimensionDataCloudControllerUtils.tryFindPropertyValue(response, "tagKeyId");
      assertNotNull(response);
      return response;
   }

   @Test
   public void testEditTagKey()
   {
      tagKeyName = "jcloudsTagKeyName" + System.currentTimeMillis();
      Response response = api().editTagKey(tagKeyName, tagKeyId, "newDescription", Boolean.FALSE, Boolean.FALSE);
      assertEquals(response.responseCode(), "OK");
      assertTagKeyExistsAndIsValid(tagKeyId, tagKeyName, "newDescription", Boolean.FALSE, Boolean.FALSE);
   }

   @Test
   public void testListTagKeys()
   {
      PaginatedCollection<TagKey> response = api().listTagKeys(PaginationOptions.Builder.pageSize(100));
      // assert that the created tag is present in the list of tag keys.
      assertTrue( FluentIterable.from(response.getResources()).anyMatch(new Predicate<TagKey>() {
               @Override public boolean apply(TagKey input) {
                  return input.id().equals(tagKeyId);
               }
            }));
   }

   private void createTagKeyIfNotExist() {
      if (tagKeyId == null) {
         createTagKey();
      }
   }

   @Test
   public void testApplyTags()
   {
      Response response = api().applyTags(assetId, assetType,
            Collections.singletonList(TagInfo.create(tagKeyId, "jcloudsValue")));
      assertEquals(response.responseCode(), "OK");
   }

   @Test
   public void testListTags()
   {
      PaginatedCollection<Tag> response = api().listTags(PaginationOptions.Builder.pageSize(100));
      assertTrue(FluentIterable.from(response.getResources()).anyMatch(new Predicate<Tag>() {
         @Override public boolean apply(Tag input) {
            return input.tagKeyId().equals(tagKeyId);
         }
      }), String.format("Couldn't find tagKeyId %s in listTags response", tagKeyId));
   }

   @Test
   public void testRemoveTags()
   {
      Response response = api().removeTags(assetId, assetType, Collections.singletonList(tagKeyId));
      assertEquals(response.responseCode(), "OK");
      assertFalse(FluentIterable.from(
            api().listTags(PaginationOptions.Builder.pageSize(100))
                  .getResources()).anyMatch(new Predicate<Tag>() {
         @Override public boolean apply(Tag input) {
            return input.tagKeyId().equals(tagKeyId);
         }
      }));
   }



   private void assertTagKeyExistsAndIsValid(String tagKeyId, String tagKeyName, String description, Boolean valueRequired,
         Boolean displayOnReport) {
      TagKey tagKey = api().tagKeysById(tagKeyId);
      assertNotNull(tagKey);
      assertEquals(tagKey.name(), tagKeyName);
      assertEquals(tagKey.description(), description);
      assertEquals(tagKey.valueRequired(), valueRequired);
      assertEquals(tagKey.displayOnReport(), displayOnReport);
   }

   @AfterClass
   public void cleanup(){
      if(!tagKeyId.isEmpty()){
         api().deleteTagKey(tagKeyId);
      }
   }

   private TagApi api()
   {
       return api.getTagApi();
   }
}
