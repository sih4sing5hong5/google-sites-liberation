package com.google.sites.liberation;

import com.google.gdata.util.common.base.Preconditions;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.Validate;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * This is a very simple implementation of an xml element to aid
 * in generating well formed xhtml pages.
 * 
 *@author bsimon@google.com (Benjamin Simon)
 */
public class XmlElement {
	
  private final String elementType;
  private final List<Object> children;
  private final Map<String, String> attributes;

  /**
   * Creates a new {@code XmlElement} of the given type.
   * @param elementType Tag name of this element
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
   * Children appear in the order in which they are added in the xml output.
   */
  public void addChild(XmlElement child) {
    Preconditions.checkNotNull(child);
    children.add(child);
  }
  
  /**
   * Adds a plain text child to this element. The String given is
   * automatically converted to xml-safe characters.
   * 
   * Children appear in the order in which they are added in the xml output.
   */
  public void addText(String text) {
    Preconditions.checkNotNull(text);
    children.add(StringEscapeUtils.escapeXml(text));
  }
  
  /**
   * Adds a string of xml as a child to this element. Unlike addText(String),
   * the string provided will not be escaped. If the given String is not well 
   * formed, then this element may not be well formed.
   */
  public void addXml(String xml) {
    Preconditions.checkNotNull(xml);
    children.add(xml);
  }
  
  /**
   * Sets the attribute with the given name to the given value.
   * 
   * Attributes appear alphabetically by name in the xml output.
   */
  public void setAttribute(String name, String value) {
    Preconditions.checkNotNull(name, "name");
    Preconditions.checkNotNull(value, "value");
    attributes.put(name, value);
  }
  
  /**
   * Returns the String of xml corresponding to this XmlElement.
   */
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("<" + elementType);
    for(Map.Entry<String, String> a : attributes.entrySet()) {
      builder.append(" " + a.getKey() + "=\"" + a.getValue() + "\"");
    }
    if (children.isEmpty()) {
      builder.append(" /");
    }
    builder.append(">\n");
    for(Object c : children) {
      builder.append(c);
      if(c instanceof String)
        builder.append("\n");
    }
    if (!children.isEmpty()) {
      builder.append("</" + elementType + ">\n");
    }
    return builder.toString();
  }
  
}
