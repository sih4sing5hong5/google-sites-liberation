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

import com.google.gdata.data.Person;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;

/**
 * @author bsimon@google.com (Benjamin Simon)
 */
public class AuthorParserImplTest extends AbstractParserImplTest {

  private static final String name = "Ben Simon";
  private static final String email = "me@company.com";
  private AuthorParser parser;
  private Element element;
  private Person author;
  private String html;
  
  @Before
  public void before() {
    parser = new AuthorParserImpl();
  }
  
  @Test
  public void testFn() {
    html = "<span class=\"author\"><span class=\"vcard\">" +
    		        "<a class=\"fn\" href=\"mailto:" + 
    		        email + "\">" + name + "</a>" +
    		      "</span></span>";
    element = getElement(html);
    author = parser.parseAuthor(element);
    assertEquals(name, author.getName());
    assertEquals(email, author.getEmail());
    
    html = "<span class=\"author\"><span class=\"vcard\">" +
                    "<div>" +
                      "<h1 class=\"fn\">" + name + "</h1>" +
                    "</div>" +
                  "</span></span>";
    element = getElement(html);
    author = parser.parseAuthor(element);
    assertEquals(name, author.getName());
    assertNull(author.getEmail());
  }
  
  @Test
  public void testN() {
    html = "<span class=\"author\"><span class=\"vcard\">" +
                    "<h3 class=\"n\">" + name + "</h3>" +
                  "</span></span>";
    element = getElement(html);
    author = parser.parseAuthor(element);
    assertEquals(name, author.getName());
    assertNull(author.getEmail());
    
    html = "<span class=\"author\"><div class=\"vcard\">" +
               "<span class=\"n\">" + name + "</span>" +
           "</div></span>";
    element = getElement(html);
    author = parser.parseAuthor(element);
    assertEquals(name, author.getName());
    assertNull(author.getEmail());
  }
  
  @Test
  public void testEmail() {
    html = "<span class=\"author\"><span class=\"vcard\">" +
             "<span class=\"email\">" + email + "</span>" +
           "</span></span>";
    element = getElement(html);
    author = parser.parseAuthor(element);
    assertNull(author.getName());
    assertEquals(email, author.getEmail());
    
    html = "<span class=\"author\"><span class=\"vcard\">" +
             "<span class=\"email\">" + 
               "<a href=\"mailto:me\">" + email + "</a>" +
             "</span>" +
           "</span></span>";
    element = getElement(html);
    author = parser.parseAuthor(element);
    assertNull(author.getName());
    assertEquals(email, author.getEmail());
  }
  
  @Test
  public void testNAndEmail() {
    html = "<span class=\"author\"><addr class=\"vcard\">" +
             "Name and email:" +
             "<span class=\"email\">" + email + "</span>, " +
             "<h3 class=\"n\">" + name + "</h3>" + 
           "</addr></span>";
    element = getElement(html);
    author = parser.parseAuthor(element);
    assertEquals(name, author.getName());
    assertEquals(email, author.getEmail());
  }
}
