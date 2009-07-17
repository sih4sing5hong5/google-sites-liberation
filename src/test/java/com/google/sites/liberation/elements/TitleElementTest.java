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

import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.WebPageEntry;

import org.junit.Test;

/**
 * Test for {@link TitleElement}.
 */
public class TitleElementTest {

  @Test
  public void testNull() {
    try {
      new TitleElement(null);
      fail("Should throw NullPointerException!");
    } catch (NullPointerException e) {}
    try {
      new TitleElement(getEntry(""), null);
      fail("Should throw NullPointerException!");
    } catch (NullPointerException e) {}
  }
  
  @Test
  public void testOneArgConstructor() {
    BaseContentEntry<?> entry = getEntry("title 1");
    XmlElement element = new TitleElement(entry);
    assertEquals("<span class=\"entry-title\">title 1</span>",
        element.toString());
    entry = getEntry("<title 1>");
    element = new TitleElement(entry);
    assertEquals("<span class=\"entry-title\">&lt;title 1&gt;</span>",
        element.toString());
  }
  
  @Test
  public void testTwoArgsConstructor() {
    BaseContentEntry<?> entry = getEntry("another title!");
    XmlElement element = new TitleElement(entry, "h3");
    assertEquals("<h3 class=\"entry-title\">another title!</h3>",
        element.toString());
    entry = getEntry("");
    element = new TitleElement(entry, "a");
    assertEquals("<a class=\"entry-title\"></a>",
        element.toString());
  }
  
  private BaseContentEntry<?> getEntry(String title) {
    BaseContentEntry<?> entry = new WebPageEntry();
    entry.setTitle(new PlainTextConstruct(title));
    return entry;
  }
}
