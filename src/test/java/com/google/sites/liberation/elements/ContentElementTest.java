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
import com.google.gdata.data.XhtmlTextConstruct;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.WebPageEntry;
import com.google.gdata.util.XmlBlob;

import org.junit.Test;

/**
 * Test for {@link ContentElement}.
 * 
 * @author bsimon@google.com (Your Name Here)
 */
public class ContentElementTest {

  @Test
  public void testNull() {
    try {
      new ContentElement(null);
      fail("Should throw NullPointerException!");
    } catch (NullPointerException e) {}
  }
  
  @Test
  public void testNormalContent() {
    String xhtml = "<div><a href=\"http://whoa!\">whoa</a></div>";
    XmlBlob blob = new XmlBlob();
    blob.setBlob(xhtml);
    BaseContentEntry<?> entry = new WebPageEntry();
    entry.setContent(new XhtmlTextConstruct(blob));
    XmlElement element = new ContentElement(entry);
    assertEquals("<div class=\"entry-content\">"+xhtml+"</div>", 
        element.toString());
  }
  
  @Test
  public void testEmptyContent() {
    BaseContentEntry<?> entry = new WebPageEntry();
    XmlElement element = new ContentElement(entry);
    assertEquals("<div class=\"entry-content\"></div>", element.toString());
  }
  
  @Test
  public void testIllegalContent() {  
    BaseContentEntry<?> entry = new WebPageEntry();
    entry.setContent(new PlainTextConstruct("this is illegal"));
    XmlElement element = new ContentElement(entry);
    assertEquals("<div class=\"entry-content\"></div>", element.toString());
  }
}
