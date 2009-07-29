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

import static com.google.gdata.util.common.base.Preconditions.checkNotNull;
import static com.google.sites.liberation.EntryType.getType;
import static com.google.sites.liberation.EntryType.isPage;

import com.google.common.collect.Sets;
import com.google.gdata.data.sites.AnnouncementEntry;
import com.google.gdata.data.sites.AnnouncementsPageEntry;
import com.google.gdata.data.sites.AttachmentEntry;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.BasePageEntry;
import com.google.gdata.data.sites.CommentEntry;
import com.google.sites.liberation.EntryStore;
import com.google.sites.liberation.elements.AuthorElement;
import com.google.sites.liberation.elements.ContentElement;
import com.google.sites.liberation.elements.EntryElement;
import com.google.sites.liberation.elements.TitleElement;
import com.google.sites.liberation.elements.UpdatedElement;
import com.google.sites.liberation.elements.XmlElement;

import java.util.Collection;

/**
 * An extension of BasePageRenderer which implements 
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
    super(checkNotNull(entry), checkNotNull(entryStore));
  }
  
  /**
   * Overrides initChildren from BasePageRenderer so that announcements is 
   * initialized and populated in addition to subpages, attachments, and 
   * comments.
   */
  @Override
  protected void addChild(BaseContentEntry<?> child) {
    checkNotNull(child);
    if (announcements == null) {
      announcements = Sets.newTreeSet(new UpdatedComparator());
    }
    switch(getType(child)) {
      case ANNOUNCEMENT: announcements.add((AnnouncementEntry) child); break;
      case ATTACHMENT: attachments.add((AttachmentEntry) child); break;
      case COMMENT: comments.add((CommentEntry) child); break;
      default: 
        if (isPage(child)) {
          subpages.add((BasePageEntry<?>) child);
        }
    }
  }
  
  /**
   * Renders the announcements section in a page.
   */
  @Override
  public XmlElement renderAdditionalContent() {
    if (announcements == null || announcements.size() == 0) {
      return null;
    }  
    XmlElement div = new XmlElement("div");
    for(AnnouncementEntry announcement : announcements) {
      XmlElement announceDiv = new EntryElement(announcement);
      XmlElement title = new XmlElement("h4");
      String href = announcement.getPageName().getValue() + "/index.html";
      XmlElement titleLink = new TitleElement(announcement, "a");
      titleLink.setAttribute("href", href);
      title.addElement(titleLink);
      announceDiv.addElement(title);
      XmlElement author = new AuthorElement(announcement);
      announceDiv.addText("posted by ").addElement(author);
      XmlElement updated = new UpdatedElement(announcement);
      announceDiv.addText(" on ").addElement(updated);
      XmlElement mainHtml = new ContentElement(announcement);
      announceDiv.addElement(mainHtml);
      div.addElement(new XmlElement("hr"));
      div.addElement(announceDiv);
    }
    return div;
  }
}
