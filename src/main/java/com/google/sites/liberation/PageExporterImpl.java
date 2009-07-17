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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.sites.liberation.elements.EntryElement;
import com.google.sites.liberation.elements.XmlElement;
import com.google.sites.liberation.renderers.PageRenderer;

import java.io.IOException;

/**
 * This class can implements {@link PageExporter} to export a single page in a 
 * Site as to a given {@code Appendable}. 
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
final class PageExporterImpl implements PageExporter {
  
  @Override
  public void exportPage(PageRenderer renderer, Appendable out) 
      throws IOException {
    checkNotNull(renderer, "renderer");
    checkNotNull(out, "out");
    XmlElement html = new XmlElement("html");
    XmlElement body = new XmlElement("body");
    XmlElement mainDiv = new EntryElement(renderer.getEntry());
    XmlElement parentLinks = renderer.renderParentLinks();
    if (parentLinks != null) {
      mainDiv.addElement(parentLinks);
    }
    XmlElement title = renderer.renderTitle();
    if (title != null) {
      mainDiv.addElement(title);
    }
    XmlElement content = renderer.renderContent();
    if (content != null) {
      mainDiv.addElement(content);
    }
    XmlElement specialContent = renderer.renderAdditionalContent();
    if(specialContent != null) {
      mainDiv.addElement(specialContent);
    }
    XmlElement subpageLinks = renderer.renderSubpageLinks();
    if (subpageLinks != null) {
      mainDiv.addElement(subpageLinks);
    }
    XmlElement attachments = renderer.renderAttachments();
    if (attachments != null) {
      mainDiv.addElement(attachments);
    }
    XmlElement comments = renderer.renderComments();
    if (comments != null) {
      mainDiv.addElement(comments);
    }
    body.addElement(mainDiv);
    html.addElement(body);
    html.appendTo(out);
  }
}