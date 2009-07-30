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

import com.google.gdata.data.XhtmlTextConstruct;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.sites.liberation.util.XmlElement;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Extends XmlElement to allow the creation of an hAtom 
 * "entry-content" element in a single statement.
 *
 * @author bsimon@google.com (Benjamin Simon)
 */
final class ContentElement extends XmlElement {

  private static final Logger logger = Logger.getLogger(
      ContentElement.class.getCanonicalName());
  
  /**
   * Creates a new hAtom "entry-content" div for the given entry.
   */
  public ContentElement(BaseContentEntry<?> entry) {
    super("div");
    checkNotNull(entry);
    this.setAttribute("class", "entry-content");
    String xhtmlContent;
    try {
      xhtmlContent = ((XhtmlTextConstruct)(entry.getTextContent()
          .getContent())).getXhtml().getBlob();
    } catch(IllegalStateException e) {
      logger.log(Level.WARNING, "Invalid Content", e);
      xhtmlContent = "";
    } catch(ClassCastException e) {
      logger.log(Level.WARNING, "Invalid Content", e);
      xhtmlContent = "";
    } catch(NullPointerException e) {
      logger.log(Level.WARNING, "Invalid Content", e);
      xhtmlContent = "";
    }
    this.addXml(xhtmlContent);
  }  
}

