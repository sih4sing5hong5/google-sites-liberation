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

import static com.google.sites.liberation.util.EntryType.ATTACHMENT;
import static com.google.sites.liberation.util.EntryType.getType;
import static com.google.sites.liberation.util.EntryType.WEB_ATTACHMENT;

import com.google.gdata.data.sites.BaseContentEntry;
import com.google.sites.liberation.util.EntryType;
import com.google.sites.liberation.util.XmlElement;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Renders the attachment links in a page.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
final class AttachmentsRendererImpl implements AttachmentsRenderer {
  
  private static final Logger LOGGER = Logger.getLogger(
        AttachmentsRendererImpl.class.getCanonicalName());
  
  @Override
  public XmlElement renderAttachments(List<BaseContentEntry<?>> attachments) {
    XmlElement div = new XmlElement("div");
    XmlElement bold = new XmlElement("b");
    bold.addText("Attachments (" + attachments.size() + ")");
    div.addElement(bold).addElement(new XmlElement("br"));
    for (BaseContentEntry<?> attachment : attachments) {
      EntryType type = getType(attachment);
      if (type == ATTACHMENT || type == WEB_ATTACHMENT) {
        XmlElement attachmentDiv = RendererUtils.getEntryElement(attachment, 
            "div");
        XmlElement link = RendererUtils.getOutOfLineContentElement(attachment);
        XmlElement updated = RendererUtils.getUpdatedElement(attachment);
        XmlElement author = RendererUtils.getAuthorElement(attachment);
        XmlElement revision = RendererUtils.getRevisionElement(attachment);
        attachmentDiv.addElement(link).addText(" - on ").addElement(updated)
            .addText(" by ").addElement(author).addText(" (Version ")
            .addElement(revision).addText(")");
        div.addElement(new XmlElement("br")).addElement(attachmentDiv);
      } else {
        LOGGER.log(Level.WARNING, "Invalid Attachment Type!");
      }
    }
    return div;
  }
}
