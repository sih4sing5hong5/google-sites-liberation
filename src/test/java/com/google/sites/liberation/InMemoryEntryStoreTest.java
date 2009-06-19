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

import java.util.Set;
import junit.framework.TestCase;
import com.google.common.collect.ImmutableSet;
import com.google.gdata.data.BaseEntry;
import com.google.gdata.data.sites.AnnouncementEntry;
import com.google.gdata.data.sites.AnnouncementsPageEntry;
import com.google.gdata.data.sites.CommentEntry;
import com.google.gdata.data.sites.SitesLink;
import com.google.gdata.data.sites.WebPageEntry;

public class InMemoryEntryStoreTest extends TestCase {

  private EntryStore store;
  private AnnouncementEntry announce1;
  private AnnouncementEntry announce2;
  private AnnouncementsPageEntry announcePage;
  private CommentEntry comment;
  private WebPageEntry webPage;
  
  private void addParentLink(BaseEntry<?> child, BaseEntry<?> parent) {
    child.addLink(SitesLink.Rel.PARENT, SitesLink.Type.APPLICATION_XHTML_XML,
        parent.getId());
  }
  
  @Override
  public void setUp() {
	store = new InMemoryEntryStore();
    announce1 = new AnnouncementEntry();
    announce1.setId("announce1");
    announce2 = new AnnouncementEntry();
    announce2.setId("announce2");
    announcePage = new AnnouncementsPageEntry();
    announcePage.setId("announcePage");
    comment = new CommentEntry();
    comment.setId("comment");
    webPage = new WebPageEntry();
    webPage.setId("webPage");
    addParentLink(announce1, announcePage);
    addParentLink(announce2, announcePage);
    addParentLink(webPage, announcePage);
    addParentLink(comment, webPage);
    store.addEntry(announce1);
    store.addEntry(announce2);
    store.addEntry(announcePage);
    store.addEntry(comment);
    store.addEntry(webPage);
  }
  
  public void testGetChildrenIds() {
	assertEquals(store.getChildrenIds("foo", EntryType.ANNOUNCEMENT),
	    ImmutableSet.of());
	assertEquals(store.getChildrenIds("announcePage", EntryType.COMMENT),
	    ImmutableSet.of());
	Set<?> announcements = store.getChildrenIds("announcePage", EntryType.ANNOUNCEMENT);
	assertEquals(announcements.size(), 2);
	assertTrue(announcements.contains("announce1"));
	assertTrue(announcements.contains("announce2"));
	assertEquals(store.getChildrenIds("announcePage", EntryType.WEB_PAGE),
	    ImmutableSet.of("webPage"));
	assertEquals(store.getChildrenIds("webPage", EntryType.COMMENT),
		ImmutableSet.of("comment"));
  }
  
  public void testGetEntry() {
	assertEquals(store.getEntry("announce1"), announce1);
	assertEquals(store.getEntry("announce2"), announce2);
	assertEquals(store.getEntry("announcePage"), announcePage);
	assertEquals(store.getEntry("comment"), comment);
	assertEquals(store.getEntry("webPage"), webPage);
	assertNull(store.getEntry("foo"));
  }
  
  public void testGetEntryIds() {
	Set<?> announcements = store.getEntryIds(EntryType.ANNOUNCEMENT);
	assertEquals(announcements.size(), 2);
	assertTrue(announcements.contains("announce1"));
	assertTrue(announcements.contains("announce2"));
	assertEquals(store.getEntryIds(EntryType.ANNOUNCEMENTS_PAGE),
		ImmutableSet.of("announcePage"));
	assertEquals(store.getEntryIds(EntryType.COMMENT),
			ImmutableSet.of("comment"));
	assertEquals(store.getEntryIds(EntryType.WEB_PAGE),
			ImmutableSet.of("webPage"));
	assertEquals(store.getEntryIds(EntryType.ATTACHMENT),
			ImmutableSet.of());
  }
}
