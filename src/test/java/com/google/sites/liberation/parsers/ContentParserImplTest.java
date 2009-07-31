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

import com.google.gdata.data.XhtmlTextConstruct;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * @author bsimon@google.com (Benjamin Simon)
 */
public class ContentParserImplTest {

  private ContentParser parser;
  
  @Before
  public void before() {
    parser = new ContentParserImpl();
  }
  
  @Test
  public void testNormalContent() {
    String content = "<h4>This is content!</h4>" +
    		         "This is some text inside the content." +
    		         " <a href=\"mysite.com\">And this is a link</a> " +
    		         "<b><i><span>And this is interesting!</span></i></b>";
    String html = "<div class=\"entry-content\">" + content + "</div>";
    Element element = getElement(html);
    XhtmlTextConstruct construct = (XhtmlTextConstruct) parser.parseContent(
        element);
    String parsed = construct.getXhtml().getBlob();
    assertTrue(parsed.contains(content));
  }
  
  @Test
  public void testJustText() {
    String content = "This is some text.";
    String html = "<div class=\"entry-content\">" + content + "</div>";
    Element element = getElement(html);
    XhtmlTextConstruct construct = (XhtmlTextConstruct) parser.parseContent(
        element);
    String parsed = construct.getXhtml().getBlob();
    assertTrue(parsed.contains(content));
  }
  
  @Test
  public void testEmpty() {
    String html = "<div class=\"entry-content\" />";
    Element element = getElement(html);
    XhtmlTextConstruct construct = (XhtmlTextConstruct) parser.parseContent(
        element);
    String parsed = construct.getXhtml().getBlob();
    assertTrue(construct.getPlainText().equals(""));
  }
  
  private Element getElement(String html) {
    ByteArrayInputStream stream = new ByteArrayInputStream(html.getBytes());
    Document document = null;
    try {
      document = DocumentBuilderFactory.newInstance()
          .newDocumentBuilder().parse(stream);
    } catch (SAXException e) {
      fail("Invalid html!");
    } catch (IOException e) {
      fail("Invalid html!");
    } catch (ParserConfigurationException e) {
      fail("Invalid html!");
    }
    return document.getDocumentElement();
  }
}
