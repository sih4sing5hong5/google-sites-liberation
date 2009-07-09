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

import com.google.sites.liberation.XmlElement;

/**
 * This interface defines an object that can render the different components
 * of a page from a site as appropriate xhtml.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
public interface PageRenderer {

  /** Renders link(s) to the parent(s) of a page. */
  public XmlElement renderParentLinks();
  
  /** Renders the title of a page. */
  public XmlElement renderTitle();
  
  /** Renders the main html content of a page. */
  public XmlElement renderMainContent();
  
  /** Renders content that is specific to certain page types if it exists. */
  public XmlElement renderAdditionalContent();
  
  /** Renders the links to the subpages of a page. */
  public XmlElement renderSubpageLinks();
  
  /** Renders the attachment links of a page. */
  public XmlElement renderAttachments();
  
  /** Renders the comments of a page. */
  public XmlElement renderComments(); 
}
