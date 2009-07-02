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

import com.google.gdata.util.common.base.Preconditions;

import org.apache.commons.lang.StringEscapeUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * This is a very simple implementation of an xml element to aid
 * in generating well formed xhtml pages.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
public class XmlElement {
	
  private final String elementType;
  private final List<Object> children;
  private final Map<String, String> attributes;

  /**
   * Creates a new {@code XmlElement} of the given type.
   * 
   * @param elementType tag name of this element
   */
  public XmlElement(String elementType) {
    Preconditions.checkNotNull(elementType);
    this.elementType = elementType;
    children = new LinkedList<Object>();
    attributes = new TreeMap<String, String>();
  }
	
  /**
   * Adds a child XmlElement to this one.
   * 
   * <p>Children appear in the order in which they are added in the xml output.
   * </p>
   */
  public void addChild(XmlElement child) {
    Preconditions.checkNotNull(child);
    children.add(child);
  }
  
  /**
   * Adds a plain text child to this element. The String given is
   * automatically converted to xml-safe characters.
   * 
   * <p>Children appear in the order in which they are added in the xml output.
   * </p>
   */
  public void addText(String text) {
    Preconditions.checkNotNull(text);
    children.add(StringEscapeUtils.escapeXml(text));
  }
  
  /**
   * Adds a string of xml as a child to this element. 
   * 
   * <p>Unlike addText(String), the string provided will not be escaped. If the 
   * given String is not well formed, then this element may not be well formed.
   * </p>
   */
  public void addXml(String xml) {
    Preconditions.checkNotNull(xml);
    children.add(xml);
  }
  
  /**
   * Sets the attribute with the given name to the given value.
   * 
   * <p>Attributes appear alphabetically by name in the xml output.</p>
   */
  public void setAttribute(String name, String value) {
    Preconditions.checkNotNull(name, "name");
    Preconditions.checkNotNull(value, "value");
    attributes.put(name, StringEscapeUtils.escapeXml(value));
  }
  
  /**
   * Returns the String of xml corresponding to this XmlElement.
   */
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("<" + elementType);
    for(Map.Entry<String, String> a : attributes.entrySet()) {
      builder.append(" ").append(a.getKey()).append("=\"").append(a.getValue())
          .append("\"");
    }
    if (children.isEmpty()) {
      builder.append(" /");
    }
    builder.append(">");
    for(Object c : children) {
      builder.append(c);
    }
    if (!children.isEmpty()) {
      builder.append("</" + elementType + ">");
    }
    return builder.toString();
  }
}
