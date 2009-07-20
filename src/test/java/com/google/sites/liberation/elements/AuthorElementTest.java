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

import static org.junit.Assert.*;

import com.google.gdata.data.Person;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.WebPageEntry;

import org.junit.Test;

/**
 * @author bsimon@google.com (Benjamin Simon)
 */
public class AuthorElementTest {

  @Test
  public void testNull() {
    try {
      new AuthorElement(null);
      fail("Should throw NullPointerException!");
    } catch (NullPointerException e) {}
  }
  
  @Test
  public void testNoName() {
    String email = "me@company.com";
    XmlElement element = new AuthorElement(getEntry(null, email));
    assertEquals("<span class=\"vcard\"><a class=\"email\" href=\"mailto:" + email 
        + "\">" + email + "</a></span>", element.toString());
  }
  
  @Test
  public void testWithName() {
    String name = "Ben Simon";
    String email = "me@company.com";
    XmlElement element = new AuthorElement(getEntry(name, email));
    assertEquals("<span class=\"vcard\"><a class=\"fn\" href=\"mailto:" + email 
        + "\">" + name + "</a></span>", element.toString());
  }
  
  private BaseContentEntry<?> getEntry(String name, String email) {
    BaseContentEntry<?> entry = new WebPageEntry();
    Person p = new Person();
    if (name != null) {
      p.setName(name);
    }
    p.setEmail(email);
    entry.getAuthors().add(p);
    return entry;
  }
}
