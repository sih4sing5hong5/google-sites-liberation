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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.sites.liberation.parsers.ParserUtils.hasClass;

import com.google.gdata.data.Person;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Implements AuthorParser to parse an html element for authorship.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
final class AuthorParserImpl implements AuthorParser {

  @Override
  public Person parseAuthor(Element element) {
    checkNotNull(element);
    Person author = new Person();
    parseElement(element, author);
    return author;
  }
  
  /**
   * Parses the given element and populates the given Person object accordingly.
   */
  private void parseElement(Element element, Person author) {
    NodeList nodeList = element.getChildNodes();
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node node = nodeList.item(i);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        Element child = (Element) node;
        if (hasClass(child, "vcard")) {
          parseHCard(child, author);
        } else {
          parseElement(child, author);
        }
      }
    }
  }
  
  private void parseHCard(Element element, Person author) {
    NodeList nodeList = element.getChildNodes();
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node node = nodeList.item(i);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        Element child = (Element) node;
        if (hasClass(child, "fn")) {
          author.setName(child.getTextContent());
          String href = child.getAttribute("href");
          if (href.startsWith("mailto:") && (author.getEmail() == null)) {
            author.setEmail(href.substring(7));
          }
        } else if (hasClass(child, "n")) {
          author.setName(child.getTextContent());
        } else if (hasClass(child, "email")) {
          author.setEmail(child.getTextContent());
        } else {
          parseHCard(child, author);
        }
      }
    }
  }
}
