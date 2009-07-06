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

import static com.google.sites.liberation.EntryType.ANNOUNCEMENT;
import static com.google.sites.liberation.EntryType.ATTACHMENT;
import static com.google.sites.liberation.EntryType.COMMENT;

import com.google.gdata.data.XhtmlTextConstruct;
import com.google.gdata.data.sites.AnnouncementEntry;
import com.google.gdata.data.sites.AnnouncementsPageEntry;
import com.google.gdata.data.sites.AttachmentEntry;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.CommentEntry;
import com.google.sites.liberation.EntryStore;
import com.google.sites.liberation.EntryType;
import com.google.sites.liberation.HyperLink;
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
   * Overrides initChildren from BasePageRenderer so that a collection of 
   * AnnouncementEntry's for this page is initialized and populated in addition
   * to subpages, attachments, and comments.
   */
  @Override
  void initChildren() {
    subpages = new TreeSet<BaseContentEntry<?>>(new TitleComparator());
    attachments = new TreeSet<AttachmentEntry>(new UpdatedComparator());
    comments = new TreeSet<CommentEntry>(new UpdatedComparator());
    announcements = new TreeSet<AnnouncementEntry>(new UpdatedComparator());
    for(BaseContentEntry<?> child : entryStore.getChildren(entry.getId())) {
      if (EntryType.getType(child) == ATTACHMENT) {
        attachments.add((AttachmentEntry)child);
      }
      else if (EntryType.getType(child) == COMMENT) {
        comments.add((CommentEntry)child);
      }
      else if (EntryType.getType(child) == ANNOUNCEMENT) {
        announcements.add((AnnouncementEntry)child);
      }
    }
  }
  
  /**
   * Renders the announcements section in a page.
   */
  @Override
  public XmlElement renderSpecialContent() {
    if (announcements.size() == 0) {
      return null;
    }  
    XmlElement div = new XmlElement("div");
    for(AnnouncementEntry announcement : announcements) {
      XmlElement announceDiv = new XmlElement("div");
      XmlElement title = new XmlElement("h4");
      String href = entryStore.getName(entry.getId()) + "/" + 
          entryStore.getName(announcement.getId()) + ".html";
      title.addElement(new HyperLink(href, 
          announcement.getTitle().getPlainText()));
      announceDiv.addElement(title);
      XmlElement info = new XmlElement("span");
      String author = announcement.getAuthors().get(0).getName();
      if(author == null) {
        author = announcement.getAuthors().get(0).getEmail();
      }
      info.addText("posted by " + announcement.getAuthors().get(0).getEmail());
      announceDiv.addElement(info);
      String xhtmlContent = ((XhtmlTextConstruct)(announcement.getTextContent()
          .getContent())).getXhtml().getBlob();
      XmlElement mainHtml = new XmlElement("div");
      mainHtml.addXml(xhtmlContent);
      announceDiv.addElement(mainHtml);
      div.addElement(new XmlElement("hr"));
      div.addElement(announceDiv);
    }
    return div;
  }
}
