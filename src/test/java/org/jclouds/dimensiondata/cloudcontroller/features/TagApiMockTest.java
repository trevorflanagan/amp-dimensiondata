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

import com.squareup.okhttp.mockwebserver.RecordedRequest;
import org.jclouds.dimensiondata.cloudcontroller.domain.PaginatedCollection;
import org.jclouds.dimensiondata.cloudcontroller.domain.Tag;
import org.jclouds.dimensiondata.cloudcontroller.domain.TagInfo;
import org.jclouds.dimensiondata.cloudcontroller.domain.TagKey;
import org.jclouds.dimensiondata.cloudcontroller.internal.BaseDimensionDataCloudControllerMockTest;
import org.jclouds.dimensiondata.cloudcontroller.options.PaginationOptions;
import org.testng.annotations.Test;

import java.util.*;

import static org.testng.Assert.assertEquals;

@Test(groups = "unit", testName = "TagApiMockTest") public class TagApiMockTest
      extends BaseDimensionDataCloudControllerMockTest {

   @Test public void testCreateTagKey() throws Exception {
      server.enqueue(response200());
      api.getTagApi().createTagKey("myTagKey", "myTagKeyDescription", Boolean.TRUE, Boolean.FALSE);
      RecordedRequest recordedRequest = assertSent(server, "POST", "/caas/2.2/" + ORG_ID + "/tag/createTagKey");
      assertBodyContains(recordedRequest,
            "{\"name\":\"myTagKey\",\"description\":\"myTagKeyDescription\",\"valueRequired\":true,\"displayOnReport\":false}");
   }

   @Test public void testEditTagKey() throws Exception {
      server.enqueue(response200());
      api.getTagApi().editTagKey("myTagKey", "myTagKeyId", "myTagKeyDescription", Boolean.TRUE, Boolean.FALSE);
      RecordedRequest recordedRequest = assertSent(server, "POST", "/caas/2.2/" + ORG_ID + "/tag/editTagKey");
      assertBodyContains(recordedRequest,
            "{\"name\":\"myTagKey\",\"id\":\"myTagKeyId\",\"description\":\"myTagKeyDescription\",\"valueRequired\":true,\"displayOnReport\":false}");
   }

   @Test public void testRemoveTags() throws Exception {
      server.enqueue(response200());
      api.getTagApi().removeTags("b8201405-bf9c-4896-b9cb-97fce95553a1", "SERVER",
            Collections.singletonList("f357d63e-5a00-44ab-8c2f-ccd2923f5849"));
      RecordedRequest recordedRequest = assertSent(server, "POST", "/caas/2.2/" + ORG_ID + "/tag/removeTags");
      assertBodyContains(recordedRequest,
            "{\"assetId\":\"b8201405-bf9c-4896-b9cb-97fce95553a1\",\"assetType\":\"SERVER\",\"tagKeyId\":[\"f357d63e-5a00-44ab-8c2f-ccd2923f5849\"]}");
   }

   @Test public void testApplyTags() throws Exception {
      server.enqueue(response200());
      TagInfo tagInfo = TagInfo.builder().tagKeyId("f357d63e-5a00-44ab-8c2f-ccd2923f5849").value("jcloudsValue")
            .build();
      api.getTagApi().applyTags("b8201405-bf9c-4896-b9cb-97fce95553a1", "SERVER", Collections.singletonList(tagInfo));
      RecordedRequest recordedRequest = assertSent(server, "POST", "/caas/2.2/" + ORG_ID + "/tag/applyTags");
      assertBodyContains(recordedRequest,
            "{\"assetId\":\"b8201405-bf9c-4896-b9cb-97fce95553a1\",\"assetType\":\"SERVER\",\"tagById\":[{\"tagKeyId\":\"f357d63e-5a00-44ab-8c2f-ccd2923f5849\",\"value\":\"jcloudsValue\"}]}");
   }

   @Test public void testListTags() throws Exception {
      server.enqueue(jsonResponse("/tags.json"));
      PaginatedCollection<Tag> tagPaginatedCollection = api.getTagApi()
            .listTags(PaginationOptions.Builder.pageSize(100));
      assertEquals(tagPaginatedCollection.size(), 4);
      assertEquals(tagPaginatedCollection.getPageSize(), 100);

      assertSent(server, "GET", "/caas/2.2/" + ORG_ID + "/tag/tag?pageSize=100");
   }

   @Test public void testListTagKeys() throws Exception {
      server.enqueue(jsonResponse("/tagkeys.json"));
      PaginatedCollection<TagKey> tagPaginatedCollection = api.getTagApi()
            .listTagKeys(PaginationOptions.Builder.pageSize(100));
      assertEquals(tagPaginatedCollection.size(), 9);
      assertEquals(tagPaginatedCollection.getPageSize(), 100);

      assertSent(server, "GET", "/caas/2.2/" + ORG_ID + "/tag/tagKey?pageSize=100");
   }

   @Test public void testDeleteTagKey() throws Exception {
      server.enqueue(response200());
      api.getTagApi().deleteTagKey("tagKeyId");
      RecordedRequest recordedRequest = assertSent(server, "POST", "/caas/2.2/" + ORG_ID + "/tag/deleteTagKey");
      assertBodyContains(recordedRequest, "{\"id\":\"tagKeyId\"}");
   }

}

