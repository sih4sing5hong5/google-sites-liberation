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

import java.io.IOException;
import java.util.Iterator;
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
  private final List<ChildType> types;
  private final Map<String, String> attributes;

  private static enum ChildType { ELEMENT, TEXT, XML }
  
  /**
   * Creates a new {@code XmlElement} of the given type.
   * 
   * @param elementType tag name of this element
   */
  public XmlElement(String elementType) {
    Preconditions.checkNotNull(elementType);
    this.elementType = elementType;
    children = new LinkedList<Object>();
    types = new LinkedList<ChildType>();
    attributes = new TreeMap<String, String>();
  }
	
  /**
   * Adds a child XmlElement to this one.
   * 
   * <p>Children appear in the order in which they are added in the xml output.
   * </p>
   */
  public XmlElement addElement(XmlElement child) {
    Preconditions.checkNotNull(child);
    children.add(child);
    types.add(ChildType.ELEMENT);
    return this;
  }
  
  /**
   * Adds a plain text child to this element. The String given is
   * automatically converted to xml-safe characters.
   * 
   * <p>Children appear in the order in which they are added in the xml output.
   * </p>
   */
  public XmlElement addText(String text) {
    Preconditions.checkNotNull(text);
    children.add(StringEscapeUtils.escapeXml(text));
    types.add(ChildType.TEXT);
    return this;
  }
  
  /**
   * Adds a string of xml as a child to this element. 
   * 
   * <p>Unlike addText(String), the string provided will not be escaped. If the 
   * given String is not well formed, then this element may not be well formed.
   * </p>
   */
  public XmlElement addXml(String xml) {
    Preconditions.checkNotNull(xml);
    children.add(xml);
    types.add(ChildType.XML);
    return this;
  }
  
  /**
   * Sets the attribute with the given name to the given value.
   * 
   * <p>Attributes appear alphabetically by name in the xml output.</p>
   */
  public XmlElement setAttribute(String name, String value) {
    Preconditions.checkNotNull(name, "name");
    Preconditions.checkNotNull(value, "value");
    attributes.put(name, StringEscapeUtils.escapeXml(value));
    return this;
  }
  
  /**
   * Appends this XmlElement (and any children) to an Appendable.
   */
  public void appendTo(Appendable a) throws IOException {
    a.append("<" + elementType);
    for(Map.Entry<String, String> attribute : attributes.entrySet()) {
      a.append(" ").append(attribute.getKey()).append("=\"")
          .append(attribute.getValue()).append("\"");
    }
    if (children.isEmpty()) {
      a.append(" />");
    }
    else {
      a.append(">");
      Iterator<ChildType> typeItr = types.iterator();
      for(Object c : children) {
        ChildType type = typeItr.next();
        if(type == ChildType.ELEMENT) {
          ((XmlElement)c).appendTo(a);
        }
        else {
          a.append((String)c);
        }
      }
      a.append("</" + elementType + ">");
    }
  }
  
  /**
   * Returns the String of xml corresponding to this XmlElement.
   */
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    try {
      appendTo(builder);
    } catch(IOException e) {
      throw new RuntimeException(e);
    }
    return builder.toString();
  }
}
