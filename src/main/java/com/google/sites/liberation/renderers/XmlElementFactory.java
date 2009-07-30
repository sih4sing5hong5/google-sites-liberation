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
import com.google.inject.ImplementedBy;
import com.google.sites.liberation.util.XmlElement;

/**
 * Factory with methods to create various XmlElement's.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
@ImplementedBy(XmlElementFactoryImpl.class)
public interface XmlElementFactory {

  /**
   * Creates a new hCard element for the given entry.
   */
  XmlElement getAuthorElement(BaseContentEntry<?> entry);
  
  /**
   * Creates a new hAtom "entry-content" div for the given entry.
   */
  XmlElement getContentElement(BaseContentEntry<?> entry);
  
  /**
   * Creates a new hAtom "hentry" element of the given type for the given entry.
   */
  XmlElement getEntryElement(BaseContentEntry<?> entry, String elementType);
  
  /**
   * Creates a new HyperLink with the given href and display text.
   */
  XmlElement getHyperLink(String href, String text);
  
  /**
   * Creates a new "sites:revision" for the given entry.
   */
  XmlElement getRevisionElement(BaseContentEntry<?> entry);
  
  /**
   * Creates a new hAtom "entry-summary" element for the given entry.
   */
  XmlElement getSummaryElement(BaseContentEntry<?> entry);
  
  /**
   * Creates a new hAtom "entry-title" element for the given entry.
   */
  XmlElement getTitleElement(BaseContentEntry<?> entry);
  
  /**
   * Creates a new hAtom "updated" element for the given entry.
   */
  XmlElement getUpdatedElement(BaseContentEntry<?> entry);  
}
