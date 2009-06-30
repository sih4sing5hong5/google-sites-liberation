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

package com.google.sites.liberation;

import static org.junit.Assert.*;

import org.junit.Test;

public class XmlElementTest {

  @Test
  public void testConstructor() {
    XmlElement empty = new XmlElement("");
    assertEquals("< />\n", empty.toString());
    XmlElement div = new XmlElement("div");
    assertEquals("<div />\n", div.toString());
  }
  
  @Test
  public void testAddChild() {
    XmlElement div = new XmlElement("div");
    XmlElement span = new XmlElement("span");
    XmlElement hr = new XmlElement("hr");
    XmlElement br = new XmlElement("br");
    boolean thrown = false;
    try {
      div.addChild(null);
    } catch(NullPointerException e) {
      thrown = true;
    }
    assertTrue(thrown);
    div.addChild(span);
    assertEquals("<div>\n<span />\n</div>\n", div.toString());
    div.addChild(hr);
    assertEquals("<div>\n<span />\n<hr />\n</div>\n", div.toString());
    div = new XmlElement("div");
    div.addChild(hr);
    div.addChild(span);
    assertEquals("<div>\n<hr />\n<span />\n</div>\n", div.toString());
    div = new XmlElement("div");
    span.addChild(hr);
    div.addChild(span);
    assertEquals("<div>\n<span>\n<hr />\n</span>\n</div>\n", div.toString());
  }
  
  @Test
  public void testAddText() {
    XmlElement div = new XmlElement("div");
    div.addText("text");
    assertEquals("<div>\ntext\n</div>\n", div.toString());
    div.addText("");
    assertEquals("<div>\ntext\n\n</div>\n", div.toString());
    div.addText("more text");
    assertEquals("<div>\ntext\n\nmore text\n</div>\n", div.toString());
    div.addText("<div />");
    assertEquals("<div>\ntext\n\nmore text\n&lt;div /&gt;\n</div>\n", 
        div.toString());
  }
  
  @Test
  public void testAddXml() {
    XmlElement div = new XmlElement("div");
    div.addXml("");
    assertEquals("<div>\n\n</div>\n", div.toString());
    div.addXml("<span class=\"class\"><hr />text</span>");
    assertEquals("<div>\n\n<span class=\"class\"><hr />text</span>\n</div>\n",
        div.toString());
  }
  
  @Test
  public void testSetAttribute() {
    XmlElement div = new XmlElement("div");
    div.setAttribute("href", "#");
    assertEquals("<div href=\"#\" />\n", div.toString());
    div.setAttribute("class", "main");
    assertEquals("<div class=\"main\" href=\"#\" />\n", div.toString());
    div.setAttribute("href", "not#");
    assertEquals("<div class=\"main\" href=\"not#\" />\n", div.toString());
  }
}
