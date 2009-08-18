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

package com.google.sites.liberation.util;

import static org.junit.Assert.*;

import com.google.gdata.data.sites.AnnouncementEntry;
import com.google.gdata.data.sites.AnnouncementsPageEntry;
import com.google.gdata.data.sites.AttachmentEntry;
import com.google.gdata.data.sites.CommentEntry;
import com.google.gdata.data.sites.FileCabinetPageEntry;
import com.google.gdata.data.sites.ListItemEntry;
import com.google.gdata.data.sites.ListPageEntry;
import com.google.gdata.data.sites.WebAttachmentEntry;
import com.google.gdata.data.sites.WebPageEntry;
import org.junit.Test;

/**
 * @author bsimon@google.com (Ben Simon)
 */
public class EntryTypeTest {
  
  @Test
  public void testGetType() {
    assertEquals(EntryType.getType(new AnnouncementEntry()), 
        EntryType.ANNOUNCEMENT);
    assertEquals(EntryType.getType(new AnnouncementsPageEntry()), 
        EntryType.ANNOUNCEMENTS_PAGE);
    assertEquals(EntryType.getType(new AttachmentEntry()), 
        EntryType.ATTACHMENT);
    assertEquals(EntryType.getType(new CommentEntry()), 
        EntryType.COMMENT);
    assertEquals(EntryType.getType(new FileCabinetPageEntry()), 
        EntryType.FILE_CABINET_PAGE);
    assertEquals(EntryType.getType(new ListItemEntry()), 
        EntryType.LIST_ITEM);
    assertEquals(EntryType.getType(new ListPageEntry()), 
        EntryType.LIST_PAGE);
    assertEquals(EntryType.getType(new WebAttachmentEntry()), 
        EntryType.WEB_ATTACHMENT);
    assertEquals(EntryType.getType(new WebPageEntry()), 
        EntryType.WEB_PAGE);
  }
  
  @Test
  public void testIsPage() {
    assertTrue(EntryType.isPage(new AnnouncementEntry()));
    assertTrue(EntryType.isPage(new AnnouncementsPageEntry()));
    assertTrue(EntryType.isPage(new FileCabinetPageEntry()));
    assertTrue(EntryType.isPage(new ListPageEntry()));
    assertTrue(EntryType.isPage(new WebPageEntry()));
    assertFalse(EntryType.isPage(new AttachmentEntry()));
    assertFalse(EntryType.isPage(new CommentEntry()));
    assertFalse(EntryType.isPage(new ListItemEntry()));
  }
  
}
