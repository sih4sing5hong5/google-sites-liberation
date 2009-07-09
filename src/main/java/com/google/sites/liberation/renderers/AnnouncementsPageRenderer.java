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

package com.google.sites.liberation.renderers;

import static com.google.sites.liberation.EntryType.getType;
import static com.google.sites.liberation.EntryType.isPage;
import static com.google.sites.liberation.HAtomFactory.getAuthorElement;
import static com.google.sites.liberation.HAtomFactory.getEntryElement;
import static com.google.sites.liberation.HAtomFactory.getContentElement;
import static com.google.sites.liberation.HAtomFactory.getTitleElement;

import com.google.gdata.data.sites.AnnouncementEntry;
import com.google.gdata.data.sites.AnnouncementsPageEntry;
import com.google.gdata.data.sites.AttachmentEntry;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.CommentEntry;
import com.google.sites.liberation.EntryStore;
import com.google.sites.liberation.XmlElement;

import java.util.Collection;
import java.util.TreeSet;

/**
 * This is an extension of BasePageRenderer which implements 
 * PageRenderer.renderSpecialContent to render the announcements in an 
 * Announcements Page.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
class AnnouncementsPageRenderer extends 
    BasePageRenderer<AnnouncementsPageEntry> {

  Collection<AnnouncementEntry> announcements;
  
  /**
   * Creates a new instance of AnnouncementsPageRenderer for the given
   * AnnouncementsPageEntry and EntryStore.
   */
  AnnouncementsPageRenderer(AnnouncementsPageEntry entry, 
      EntryStore entryStore) {
    super(entry, entryStore);
  }
  
  /**
   * Overrides initChildren from BasePageRenderer so that announcements is 
   * initialized and populated in addition to subpages, attachments, and 
   * comments.
   */
  @Override
  protected void addChild(BaseContentEntry<?> child) {
    if (announcements == null) {
      announcements = new TreeSet<AnnouncementEntry>(new UpdatedComparator());
    }
    switch(getType(child)) {
      case ANNOUNCEMENT: announcements.add((AnnouncementEntry) child); break;
      case ATTACHMENT: attachments.add((AttachmentEntry) child); break;
      case COMMENT: comments.add((CommentEntry) child); break;
      default: 
        if (isPage(child)) {
          subpages.add(child);
        }
    }
  }
  
  /**
   * Renders the announcements section in a page.
   */
  @Override
  public XmlElement renderAdditionalContent() {
    if (announcements.size() == 0) {
      return null;
    }  
    XmlElement div = new XmlElement("div");
    for(AnnouncementEntry announcement : announcements) {
      XmlElement announceDiv = getEntryElement(announcement, "div");
      XmlElement title = new XmlElement("h4");
      String href = entryStore.getName(entry.getId()) + "/" + 
          entryStore.getName(announcement.getId()) + ".html";
      XmlElement titleLink = getTitleElement(announcement, "a");
      titleLink.setAttribute("href", href);
      title.addElement(titleLink);
      announceDiv.addElement(title);
      XmlElement author = getAuthorElement(announcement);
      announceDiv.addText("posted by ");
      announceDiv.addElement(author);
      XmlElement mainHtml = getContentElement(announcement);
      announceDiv.addElement(mainHtml);
      div.addElement(new XmlElement("hr"));
      div.addElement(announceDiv);
    }
    return div;
  }
}
