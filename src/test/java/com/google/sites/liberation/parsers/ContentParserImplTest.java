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

import com.google.gdata.data.TextContent;
import com.google.gdata.data.XhtmlTextConstruct;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;

/**
 * @author bsimon@google.com (Benjamin Simon)
 */
public class ContentParserImplTest extends AbstractParserImplTest {

  private ContentParser parser;
  
  @Before
  public void before() {
    parser = new ContentParserImpl();
  }
  
  @Test
  public void testNormalXhtmlContent() {
    String content = "<h4>This is content!</h4>" +
    		         "This is some text inside the content." +
    		         " <a href=\"mysite.com\">And this is a link</a> " +
    		         "<b><i><span>And this is interesting!</span></i></b>";
    String html = "<div class=\"entry-content\">" + content + "</div>";
    Element element = getElement(html);
    XhtmlTextConstruct construct = (XhtmlTextConstruct) ((TextContent) 
        parser.parseContent(element)).getContent();
    String parsed = construct.getXhtml().getBlob();
    assertTrue(parsed.contains(content));
  }
  
  @Test
  public void testJustTextXhtmlContent() {
    String content = "This is some text.";
    String html = "<div class=\"entry-content\">" + content + "</div>";
    Element element = getElement(html);
    XhtmlTextConstruct construct = (XhtmlTextConstruct) ((TextContent) 
        parser.parseContent(element)).getContent();
    String parsed = construct.getXhtml().getBlob();
    assertTrue(parsed.contains(content));
  }
  
  @Test
  public void testEmptyXhtmlContent() {
    String html = "<div class=\"entry-content\" />";
    Element element = getElement(html);
    XhtmlTextConstruct construct = (XhtmlTextConstruct) ((TextContent) 
        parser.parseContent(element)).getContent();
    String parsed = construct.getXhtml().getBlob();
    assertTrue(construct.getPlainText().equals(""));
  }
}
