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

import com.google.gdata.data.sites.BaseContentEntry;
import com.google.sites.liberation.util.XmlElement;

/**
 * Extends XmlElement to allow the creation of an hAtom "entry-title"
 * element in a single statement.
 *
 * @author bsimon@google.com (Benjamin Simon)
 */
final class TitleElement extends XmlElement {

  /**
   * Creates a new hAtom "entry-title" element for the given entry.
   */
  public TitleElement(BaseContentEntry<?> entry) {
    this(entry, "span");
  }
  
  /**
   * Creates a new hAtom "entry-title" element of the given type for the given 
   * entry.
   */
  public TitleElement(BaseContentEntry<?> entry, String elementType) {
    super(elementType);
    checkNotNull(entry, "entry");
    checkNotNull(elementType, "elementType");
    this.setAttribute("class", "entry-title");
    this.addText(entry.getTitle().getPlainText());
  }  
}

