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

import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.CommentEntry;
import com.google.sites.liberation.util.XmlElement;

import java.util.List;

/**
 * Renders the links to a page's ancestors.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
final class CommentsRendererImpl implements CommentsRenderer {

  @Override
  public XmlElement renderComments(List<CommentEntry> comments) {
    XmlElement div = new XmlElement("div");
    XmlElement bold = new XmlElement("b");
    bold.addText("Comments (" + comments.size() + ")");
    div.addElement(bold).addElement(new XmlElement("br"));
    for (BaseContentEntry<?> comment : comments) {
      XmlElement commentDiv = RendererUtils.getEntryElement(comment, "div");
      XmlElement author = RendererUtils.getAuthorElement(comment);
      XmlElement updated = RendererUtils.getUpdatedElement(comment);
      XmlElement revision = RendererUtils.getRevisionElement(comment);
      XmlElement content = RendererUtils.getXhtmlContentElement(comment);
      commentDiv.addElement(author).addText(" - ").addElement(updated);
      commentDiv.addText(" (Version ").addElement(revision).addText(")");
      commentDiv.addElement(new XmlElement("br")).addElement(content);
      div.addElement(new XmlElement("br")).addElement(commentDiv);
    }
    return div;
  }
}
