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

import com.google.gdata.data.Content;
import com.google.gdata.data.OutOfLineContent;
import com.google.gdata.data.TextContent;
import com.google.gdata.data.XhtmlTextConstruct;
import com.google.gdata.util.XmlBlob;
import com.google.sites.liberation.util.XmlElement;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Implements ContentParser to parse an html element for entry content.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
final class ContentParserImpl implements ContentParser {

  @Override
  public Content parseContent(Element element) {
    checkNotNull(element);
    String href = element.getAttribute("href");
    if (href.equals("")) {
      StringBuilder builder = new StringBuilder();
      NodeList nodeList = element.getChildNodes();
      for (int i = 0; i < nodeList.getLength(); i++) {
        Node child = nodeList.item(i);
        if (child.getNodeType() == Node.ELEMENT_NODE) {
          builder.append(xmlElementOf((Element) child));
        } else if (child.getNodeType() == Node.TEXT_NODE) {
          builder.append(child.getNodeValue());
        }
      }
      XmlBlob xmlBlob = new XmlBlob();
      xmlBlob.setBlob(builder.toString());
      TextContent content = new TextContent();
      content.setContent(new XhtmlTextConstruct(xmlBlob));
      return content;
    }
    OutOfLineContent content = new OutOfLineContent();
    content.setUri(href);
    return content;
  }
  
  /**
   * Returns an {@link XmlElement} version of the given {@link Element}.
   */
  private XmlElement xmlElementOf(Element element) {
    XmlElement xmlElement = new XmlElement(element.getTagName());
    NamedNodeMap attributes = element.getAttributes();
    for (int i = 0; i < attributes.getLength(); i++) {
      Node attribute = attributes.item(i);
      String name = attribute.getNodeName();
      String value = attribute.getNodeValue();
      xmlElement.setAttribute(name, value);
    }
    NodeList nodeList = element.getChildNodes();
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node child = nodeList.item(i);
      if (child.getNodeType() == Node.ELEMENT_NODE) {
        XmlElement childElement = xmlElementOf((Element) child);
        xmlElement.addElement(childElement);
      } else if (child.getNodeType() == Node.TEXT_NODE) {
        xmlElement.addText(child.getNodeValue());
      }
    }
    return xmlElement;    
  }
}
