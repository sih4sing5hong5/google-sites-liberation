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

import com.google.gdata.data.sites.AnnouncementEntry;
import com.google.sites.liberation.util.XmlElement;

import java.util.List;

/**
 * Renders the announcements in an announcements page.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
final class AnnouncementsRendererImpl implements AnnouncementsRenderer {

  @Override
  public XmlElement renderAnnouncements(
      List<AnnouncementEntry> announcements) {
    XmlElement div = new XmlElement("div");
    for (AnnouncementEntry announcement : announcements) {
      div.addElement(new XmlElement("hr"));
      XmlElement announceDiv = RendererUtils.getEntryElement(announcement, 
          "blockquote");
      XmlElement title = new XmlElement("b");
      String href = announcement.getPageName().getValue() + "/index.html";
      XmlElement titleLink = new XmlElement("a").addElement(
          RendererUtils.getTitleElement(announcement));
      titleLink.setAttribute("href", href);
      title.addElement(titleLink);
      announceDiv.addElement(title).addElement(new XmlElement("br"));
      XmlElement info = new XmlElement("small");
      XmlElement author = RendererUtils.getAuthorElement(announcement);
      info.addText("posted by ").addElement(author);
      XmlElement updated = RendererUtils.getUpdatedElement(announcement);
      info.addText(" on ").addElement(updated);
      announceDiv.addElement(info).addElement(new XmlElement("br"));
      XmlElement mainHtml = RendererUtils.getXhtmlContentElement(announcement);
      announceDiv.addElement(mainHtml);
      div.addElement(announceDiv);
    }
    return div;
  }
}
