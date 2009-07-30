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

package com.google.sites.liberation.util;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author bsimon@google.com (Benjamin Simon)
 */
public class XmlElementTest {

  @Test
  public void testConstructor() {
    try {
      new XmlElement(null);
      fail("Should not accept null!");
    } catch (NullPointerException e) {}
    XmlElement empty = new XmlElement("");
    assertEquals("< />", empty.toString());
    XmlElement div = new XmlElement("div");
    assertEquals("<div />", div.toString());
  }
  
  @Test
  public void testAddChild() {
    XmlElement div = new XmlElement("div");
    XmlElement span = new XmlElement("span");
    XmlElement hr = new XmlElement("hr");
    XmlElement br = new XmlElement("br");
    try {
      div.addElement(null);
      fail("Adding null child should throw exception");
    } catch(NullPointerException e) {}
    div.addElement(span);
    assertEquals("<div><span /></div>", div.toString());
    div.addElement(hr);
    assertEquals("<div><span /><hr /></div>", div.toString());
    div = new XmlElement("div");
    div.addElement(hr);
    div.addElement(span);
    assertEquals("<div><hr /><span /></div>", div.toString());
    div = new XmlElement("div");
    span.addElement(hr);
    div.addElement(span);
    assertEquals("<div><span><hr /></span></div>", div.toString());
  }
  
  @Test
  public void testAddText() {
    XmlElement div = new XmlElement("div");
    try {
      div.addText(null);
      fail("Adding null text should throw exception");
    } catch(NullPointerException e) {}
    div.addText("text");
    assertEquals("<div>text</div>", div.toString());
    div.addText("");
    assertEquals("<div>text</div>", div.toString());
    div.addText("more text");
    assertEquals("<div>textmore text</div>", div.toString());
    div.addText("<div />");
    assertEquals("<div>textmore text&lt;div /&gt;</div>", 
        div.toString());
  }
  
  @Test
  public void testAddXml() {
    XmlElement div = new XmlElement("div");
    try {
      div.addXml(null);
      fail("Adding null xml should throw exception");
    } catch(NullPointerException e) {}
    div.addXml("");
    assertEquals("<div></div>", div.toString());
    div.addXml("<span class=\"class\"><hr />text</span>");
    assertEquals("<div><span class=\"class\"><hr />text</span></div>",
        div.toString());
  }
  
  @Test
  public void testSetAttribute() {
    XmlElement div = new XmlElement("div");
    try {
      div.setAttribute(null, "value");
      fail("Adding null name should throw exception");
    } catch(NullPointerException e) {}
    try {
      div.setAttribute("name", null);
      fail("Adding null value should throw exception");
    } catch(NullPointerException e) {}
    div.setAttribute("href", "#");
    assertEquals("<div href=\"#\" />", div.toString());
    div.setAttribute("class", "main");
    assertEquals("<div class=\"main\" href=\"#\" />", div.toString());
    div.setAttribute("href", "not#");
    assertEquals("<div class=\"main\" href=\"not#\" />", div.toString());
  }
}
