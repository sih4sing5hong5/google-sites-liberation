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
import static com.google.sites.liberation.HAtomFactory.getEntryElement;

import com.google.gdata.data.sites.BaseContentEntry;
import com.google.sites.liberation.renderers.PageRenderer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class can be used to export a single page in a Site as
 * a String of XHTML. 
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
public final class PageExporter {

  private static final Logger logger = Logger.getLogger(
      PageExporter.class.getCanonicalName());
  
  private final PageRenderer renderer;
  private final BaseContentEntry<?> entry;
  
  /**
   * Constructs a new PageExporter from the given PageRenderer and entry this
   * page corresponds to.
   */
  public PageExporter(BaseContentEntry<?> entry, PageRenderer renderer) {
    this.renderer = checkNotNull(renderer);
    this.entry = checkNotNull(entry);
  }
  
  /**
   * Exports this entry's page as XHTML to the given file name.
   */
  public void export(String fileName) {
    XmlElement html = new XmlElement("html");
    XmlElement body = new XmlElement("body");
    XmlElement mainDiv = getEntryElement(entry, "div");
    XmlElement parentLinks = renderer.renderParentLinks();
    if (parentLinks != null) {
      mainDiv.addElement(parentLinks);
    }
    XmlElement title = renderer.renderTitle();
    if (title != null) {
      mainDiv.addElement(title);
    }
    XmlElement content = renderer.renderMainContent();
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
    try {
      BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
      html.appendTo(out);
      out.close();
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Failed writing to file: " + fileName, e);
    }
  }
}