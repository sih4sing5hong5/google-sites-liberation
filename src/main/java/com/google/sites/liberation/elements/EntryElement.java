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

import static com.google.gdata.util.common.base.Preconditions.checkNotNull;
import static com.google.sites.liberation.EntryType.getType;

import com.google.gdata.data.sites.BaseContentEntry;

/**
 * This class extends XmlElement to allow the creation of an hAtom 
 * "hentry" element in a single statement.
 *
 * @author bsimon@google.com (Benjamin Simon)
 */
public class EntryElement extends XmlElement {

  /**
   * Creates a new hAtom "hentry" element for the given entry.
   */
  public EntryElement(BaseContentEntry<?> entry) {
    this(entry, "div");
  }
  
  /**
   * Creates a new hAtom "hentry" element of the given type for the given entry.
   */
  public EntryElement(BaseContentEntry<?> entry, String elementType) {
    super(elementType);
    checkNotNull(entry, "entry");
    this.setAttribute("id", entry.getId());
    String type = "";
    switch(getType(entry)) {
      case ANNOUNCEMENT: type = "announcement"; break;
      case ANNOUNCEMENTS_PAGE: type = "announcementspage"; break;
      case ATTACHMENT: type = "attachment"; break;
      case COMMENT: type = "comment"; break;
      case FILE_CABINET_PAGE: type = "filecabinet"; break;
      case LIST_ITEM: type = "listitem"; break;
      case LIST_PAGE: type = "listpage"; break;
      case WEB_PAGE: type = "webpage"; break;
    }
    this.setAttribute("class", "hentry " + type);
  }  
}
