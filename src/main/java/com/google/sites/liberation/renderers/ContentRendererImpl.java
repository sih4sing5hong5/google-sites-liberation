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

import com.google.gdata.data.sites.BasePageEntry;
import com.google.sites.liberation.util.XmlElement;

/**
 * Renders a page's main content.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
final class ContentRendererImpl implements ContentRenderer {

  @Override
  public XmlElement renderContent(BasePageEntry<?> entry, 
      boolean revisionsExported) {
    XmlElement div = new XmlElement("div");
    div.addElement(RendererUtils.getXhtmlContentElement(entry));
    div.addElement(new XmlElement("br"));
    XmlElement info = new XmlElement("small");
    info.addText("Updated on ");
    info.addElement(RendererUtils.getUpdatedElement(entry));
    info.addText(" by ");
    info.addElement(RendererUtils.getAuthorElement(entry));
    if (revisionsExported) {
      info.addText(" (");
      XmlElement historyLink = new XmlElement("a");
      historyLink.addText("Version ").addElement(
          RendererUtils.getRevisionElement(entry));
      historyLink.setAttribute("href", "history.html");
      info.addElement(historyLink).addText(")");
    } else {
      info.addText(" (Version ")
          .addElement(RendererUtils.getRevisionElement(entry)).addText(")");
    }
    div.addElement(info);
    div.addElement(new XmlElement("br")).addElement(new XmlElement("br"));
    return div;
  }
}
