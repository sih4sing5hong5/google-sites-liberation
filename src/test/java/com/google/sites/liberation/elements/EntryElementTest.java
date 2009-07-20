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

package com.google.sites.liberation.elements;

import static org.junit.Assert.*;

import com.google.gdata.data.sites.AnnouncementEntry;
import com.google.gdata.data.sites.AnnouncementsPageEntry;
import com.google.gdata.data.sites.AttachmentEntry;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.CommentEntry;
import com.google.gdata.data.sites.FileCabinetPageEntry;
import com.google.gdata.data.sites.ListItemEntry;
import com.google.gdata.data.sites.ListPageEntry;
import com.google.gdata.data.sites.WebPageEntry;

import org.junit.Test;

/**
 * @author bsimon@google.com (Benjamin Simon)
 */
public class EntryElementTest {

  @Test
  public void testNull() {
    try {
      new EntryElement(null);
      fail("Should throw NullPointerException!");
    } catch (NullPointerException e) {}
  }
  
  @Test
  public void testAnnouncement() {
    BaseContentEntry<?> entry = new AnnouncementEntry();
    entry.setId("announce");
    XmlElement element = new EntryElement(entry);
    assertEquals("<div class=\"hentry announcement\" id=\"announce\" />",
        element.toString());
  }
  
  @Test
  public void testAnnouncementsPage() {
    BaseContentEntry<?> entry = new AnnouncementsPageEntry();
    entry.setId("announce page");
    XmlElement element = new EntryElement(entry, "span");
    assertEquals("<span class=\"hentry announcementspage\" id=\"announce page\" />",
        element.toString());
  }
  
  public void testAttachment() {
    BaseContentEntry<?> entry = new AttachmentEntry();
    entry.setId("attach");
    XmlElement element = new EntryElement(entry, "div");
    assertEquals("<div class=\"hentry attachment\" id=\"attach\" />",
        element.toString());
  }
  
  public void testComment() {
    BaseContentEntry<?> entry = new CommentEntry();
    entry.setId("comment");
    XmlElement element = new EntryElement(entry);
    assertEquals("<div class=\"hentry comment\" id=\"comment\" />",
        element.toString());
  }
  
  public void testFileCabinet() {
    BaseContentEntry<?> entry = new FileCabinetPageEntry();
    entry.setId("files");
    XmlElement element = new EntryElement(entry);
    assertEquals("<div class=\"hentry filecabinet\" id=\"files\" />",
        element.toString());
  }
  
  public void testListItem() {
    BaseContentEntry<?> entry = new ListItemEntry();
    entry.setId("item");
    XmlElement element = new EntryElement(entry);
    assertEquals("<div class=\"hentry listitem\" id=\"item\" />",
        element.toString());
  }
  
  public void testListPage() {
    BaseContentEntry<?> entry = new ListPageEntry();
    entry.setId("list page");
    XmlElement element = new EntryElement(entry);
    assertEquals("<div class=\"hentry listpage\" id=\"list page\" />",
        element.toString());
  }
  
  public void testWebPage() {
    BaseContentEntry<?> entry = new WebPageEntry();
    entry.setId("web");
    XmlElement element = new EntryElement(entry);
    assertEquals("<div class=\"hentry webpage\" id=\"web\" />",
        element.toString());
  }
}
