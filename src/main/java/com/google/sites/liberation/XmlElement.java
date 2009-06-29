package com.google.sites.liberation;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.Map;

import com.google.gdata.util.common.base.Preconditions;

/**
 * This is a very simple implementation of an xml element to aid
 * in generating well formed xhtml pages.
 * 
 *@author bsimon@google.com (Benjamin Simon)
 */
public class XmlElement {
	
  private String elementType;
  private List<Object> children;
  private Map<String, String> attributes;

  /**
   * Creates a new {@code XmlElement} of the given type.
   * @param elementType
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
  public void add(XmlElement child) {
    Preconditions.checkNotNull(child);
    children.add(child);
  }
  
  /**
   * Adds a plain text child to this element. The String given is
   * automatically converted to xml-safe characters.
   * 
   * Children appear in the order in which they are added in the xml output.
   */
  public void add(String child) {
    Preconditions.checkNotNull(child);
    //TODO: escape child string
    children.add(child);
  }
  
  /**
   * Adds a string of xml as a child to this element. Unlike add(String),
   * the {@code xml} string provided will be left as is. 
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
    if(children.isEmpty())
      builder.append(" /");
    builder.append(">\n");
    for(Object c : children) {
      builder.append(c);
    }
    if(!children.isEmpty())
      builder.append("</" + elementType + ">\n");
    return builder.toString();
  }
  
}
