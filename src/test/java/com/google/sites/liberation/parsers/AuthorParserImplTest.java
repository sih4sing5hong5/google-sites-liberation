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
public class AuthorParserImplTest {

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
    html = "<span class=\"vcard\">" +
    		        "<a class=\"fn\" href=\"mailto:" + 
    		        email + "\">" + name + "</a>" +
    		      "</span>";
    element = getElement(html);
    author = parser.parseAuthor(element);
    assertEquals(name, author.getName());
    assertEquals(email, author.getEmail());
    
    html = "<span class=\"vcard\">" +
                    "<div>" +
                      "<h1 class=\"fn\">" + name + "</h1>" +
                    "</div>" +
                  "</span>";
    element = getElement(html);
    author = parser.parseAuthor(element);
    assertEquals(name, author.getName());
    assertNull(author.getEmail());
  }
  
  @Test
  public void testN() {
    html = "<span class=\"vcard\">" +
                    "<h3 class=\"n\">" + name + "</h3>" +
                  "</span>";
    element = getElement(html);
    author = parser.parseAuthor(element);
    assertEquals(name, author.getName());
    assertNull(author.getEmail());
    
    html = "<div class=\"vcard\">" +
             "<span class=\"vcard\">" + 
               "<span class=\"n\">" + name + "</span>" +
             "</span>" +
           "</div>";
    element = getElement(html);
    author = parser.parseAuthor(element);
    assertEquals(name, author.getName());
    assertNull(author.getEmail());
  }
  
  @Test
  public void testEmail() {
    html = "<span class=\"vcard\">" +
             "<span class=\"email\">" + email + "</span>" +
           "</span>";
    element = getElement(html);
    author = parser.parseAuthor(element);
    assertNull(author.getName());
    assertEquals(email, author.getEmail());
    
    html = "<span class=\"vcard\">" +
             "<span class=\"email\">" + 
               "<a href=\"mailto:me\">" + email + "</a>" +
             "</span>" +
           "</span>";
    element = getElement(html);
    author = parser.parseAuthor(element);
    assertNull(author.getName());
    assertEquals(email, author.getEmail());
  }
  
  @Test
  public void testNAndEmail() {
    html = "<addr class=\"vcard\">" +
             "Name and email:" +
             "<span class=\"email\">" + email + "</span>, " +
             "<h3 class=\"n\">" + name + "</h3>" + 
           "</addr>";
    element = getElement(html);
    author = parser.parseAuthor(element);
    assertEquals(name, author.getName());
    assertEquals(email, author.getEmail());
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
