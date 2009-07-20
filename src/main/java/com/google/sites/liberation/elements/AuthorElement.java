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

import com.google.gdata.data.Person;
import com.google.gdata.data.sites.BaseContentEntry;

/**
 * Extends XmlElement to allow the creation of an hCard element in a 
 * single statement.
 *
 * @author bsimon@google.com (Benjamin Simon)
 */
public class AuthorElement extends XmlElement {

  /**
   * Creates a new hCard element for the given entry.
   */
  public AuthorElement(BaseContentEntry<?> entry) {
    super("span");
    checkNotNull(entry);
    this.setAttribute("class", "vcard");
    Person author = entry.getAuthors().get(0);
    String name = author.getName();
    String email = author.getEmail();
    if (name == null) {
      XmlElement link = new HyperLink("mailto:" + email, email);
      link.setAttribute("class", "email");
      this.addElement(link);
    } else {
      XmlElement link = new HyperLink("mailto:" + email, name);
      link.setAttribute("class", "fn");
      this.addElement(link);
    }
  }  
}
