/*
 * Copyright (C) 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.sites.liberation;

import junit.framework.TestCase;
import com.google.common.collect.ImmutableSet;
import com.google.gdata.data.BaseEntry;
import com.google.gdata.data.sites.AnnouncementEntry;
import com.google.gdata.data.sites.AnnouncementsPageEntry;
import com.google.gdata.data.sites.AttachmentEntry;
import com.google.gdata.data.sites.CommentEntry;
import com.google.gdata.data.sites.FileCabinetPageEntry;
import com.google.gdata.data.sites.ListItemEntry;
import com.google.gdata.data.sites.ListPageEntry;
import com.google.gdata.data.sites.SitesLink;
import com.google.gdata.data.sites.WebPageEntry;

public class InMemoryEntryStoreTest extends TestCase {

  private EntryStore store;
  
  private void addEntries() {
    AnnouncementEntry announcement1 = new AnnouncementEntry();
    announcement1.setId("announcement1");
    AnnouncementEntry announcement2 = new AnnouncementEntry();
    announcement2.setId("announcement2");
    AnnouncementsPageEntry announcementsPage = new AnnouncementsPageEntry();
    announcementsPage.setId("announcementsPage");
    AttachmentEntry attachment = new AttachmentEntry();
    attachment.setId("attachment");
    CommentEntry comment = new CommentEntry();
    comment.setId("comment");
    FileCabinetPageEntry fileCabinetPage = new FileCabinetPageEntry();
    fileCabinetPage.setId("fileCabinetPage");
    ListItemEntry listItem = new ListItemEntry();
    listItem.setId("listItem");
    ListPageEntry listPage = new ListPageEntry();
    listPage.setId("listPage");
    WebPageEntry webPage = new WebPageEntry();
    webPage.setId("webPage");
    addParentLink(announcement1, announcementsPage);
    addParentLink(announcement2, announcementsPage);
    addParentLink(announcementsPage, fileCabinetPage);
    addParentLink(attachment, fileCabinetPage);
    addParentLink(comment, fileCabinetPage);
    addParentLink(listItem, listPage);
    addParentLink(listPage, webPage);
  }
  
  private void addParentLink(BaseEntry<?> child, BaseEntry<?> parent) {
    child.addLink(SitesLink.Rel.PARENT, SitesLink.Type.APPLICATION_XHTML_XML,
        parent.getId());
  }
  
  public void testGetChildrenIds() {
    store = new InMemoryEntryStore();
    assertEquals(store.getChildrenIds("http://foo.bar", EntryType.ANNOUNCEMENT),
        ImmutableSet.of());
    
  }
  
}
