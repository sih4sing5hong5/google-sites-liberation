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

package com.google.sites.liberation.elements;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.gdata.data.sites.BaseContentEntry;

/**
 * This class extends XmlElement to allow the creation of a "sites:revision" 
 * element in a single statement.
 *
 * @author bsimon@google.com (Benjamin Simon)
 */
public class RevisionElement extends XmlElement {

  /**
   * Creates a new "sites:revision" for the given entry.
   */
  public RevisionElement(BaseContentEntry<?> entry) {
    super("span");
    checkNotNull(entry);
    this.setAttribute("class", "sites:revision");
    if (entry.getRevision() == null) {
      this.addText("1");
    } else {
      this.addText(entry.getRevision().getValue().toString());
    }
  }  
}
