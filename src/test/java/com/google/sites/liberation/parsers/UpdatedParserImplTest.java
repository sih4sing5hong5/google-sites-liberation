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

package com.google.sites.liberation.parsers;

import static org.junit.Assert.*;

import com.google.gdata.data.DateTime;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;

/**
 * @author bsimon@google.com (Benjamin Simon)
 */
public class UpdatedParserImplTest extends AbstractParserImplTest {

  private UpdatedParser parser;
   
  @Before
  public void before() {
    parser = new UpdatedParserImpl();  
  }
  
  @Test
  public void testValidDate() {
    String html = "<abbr class=\"updated\" title=\"2009-07-30T15:48:23.975Z\">" +
    		        "Some random date" +
    		      "</abbr>";
    Element element = getElement(html);
    DateTime updated = parser.parseUpdated(element);
    assertEquals("2009-07-30T15:48:23.975Z", updated.toString());   
  }
  
  @Test
  public void testInvalidDate() {
    String html = "<abbr class=\"updated\" title=\"This isn't right...\">" +
                    "Some random date" +
                  "</abbr>";
    Element element = getElement(html);
    DateTime updated = parser.parseUpdated(element);
    assertNull(updated);
  }
}
