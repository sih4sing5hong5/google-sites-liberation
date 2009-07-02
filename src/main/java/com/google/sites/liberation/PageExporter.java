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

import com.google.gdata.data.sites.BaseContentEntry;
import com.google.sites.liberation.renderers.PageRenderer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class can be used to export a single page in a Site as
 * a String of XHTML. 
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
public final class PageExporter {

  PageRenderer renderer;
	
  /**
   * Constructs a new PageExporter from the given PageRenderer.
   */
  public PageExporter(PageRenderer renderer) {
    this.renderer = checkNotNull(renderer);
  }
  
  /**
   * Exports this entry's page as XHTML to the given file name.
   */
  public void export(String fileName) throws IOException {
    XmlElement html = new XmlElement("html");
    XmlElement body = new XmlElement("body");
    XmlElement parentLinks = renderer.renderParentLinks();
    if (parentLinks != null) {
      body.addChild(parentLinks);
    }
    XmlElement title = renderer.renderTitle();
    if (title != null) {
      body.addChild(title);
    }
    XmlElement mainHtml = renderer.renderMainHtml();
    if (mainHtml != null) {
      body.addChild(mainHtml);
    }
    XmlElement specialContent = renderer.renderSpecialContent();
    if(specialContent != null) {
      body.addChild(specialContent);
    }
    XmlElement subpageLinks = renderer.renderSubpageLinks();
    if (subpageLinks != null) {
      body.addChild(subpageLinks);
    }
    XmlElement attachments = renderer.renderAttachments();
    if (attachments != null) {
      body.addChild(attachments);
    }
    XmlElement comments = renderer.renderComments();
    if (comments != null) {
      body.addChild(comments);
    }
    html.addChild(body);
    BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
    out.write(html.toString());
    out.close();
  }
  
  /**
   * Returns the given entry's title with all sequences of non-word characters
   * (^[a-zA-z0-9_]) replaced by a single hyphen.
   */
  public static String getNiceTitle(BaseContentEntry<?> entry) {
    String title = entry.getTitle().getPlainText();
    String niceTitle = title.replaceAll("[\\W]+", "-");
    if(niceTitle.length() == 0) {
      niceTitle = "-";
    }
    return niceTitle;
  }
}