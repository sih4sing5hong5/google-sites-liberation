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

import static org.junit.Assert.*;

import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.WebPageEntry;
import com.google.sites.liberation.util.XmlElement;

import org.junit.Test;

/**
 * @author bsimon@google.com (Benjamin Simon)
 */
public class SummaryElementTest {

  @Test
  public void testNull() {
    try {
      new SummaryElement(null);
      fail("Should throw NullPointerException!");
    } catch (NullPointerException e) {}
    try {
      new SummaryElement(getEntry(""), null);
      fail("Should throw NullPointerException!");
    } catch (NullPointerException e) {}
  }
  
  @Test
  public void testOneArgConstructor() {
    BaseContentEntry<?> entry = getEntry("summary 1");
    XmlElement element = new SummaryElement(entry);
    assertEquals("<span class=\"entry-summary\">summary 1</span>",
        element.toString());
    entry = getEntry("<summary 1>");
    element = new SummaryElement(entry);
    assertEquals("<span class=\"entry-summary\">&lt;summary 1&gt;</span>",
        element.toString());
  }
  
  @Test
  public void testTwoArgsConstructor() {
    BaseContentEntry<?> entry = getEntry("another summary!");
    XmlElement element = new SummaryElement(entry, "h3");
    assertEquals("<h3 class=\"entry-summary\">another summary!</h3>",
        element.toString());
    entry = getEntry("");
    element = new SummaryElement(entry, "a");
    assertEquals("<a class=\"entry-summary\"></a>",
        element.toString());
  }
  
  private BaseContentEntry<?> getEntry(String summary) {
    BaseContentEntry<?> entry = new WebPageEntry();
    entry.setSummary(new PlainTextConstruct(summary));
    return entry;
  }
}
