package com.google.sites.liberation;

import java.util.LinkedList;
import java.util.List;

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
  private List<String> attributes;

  public XmlElement(String elementType) {
    Preconditions.checkNotNull(elementType);
    this.elementType = elementType;
    children = new LinkedList<Object>();
    attributes = new LinkedList<String>();
  }
	
  public void add(XmlElement child) {
    Preconditions.checkNotNull(child);
    children.add(child);
  }
  
  public void add(String child) {
    Preconditions.checkNotNull(child);
    //TODO: escape child string
    children.add(child);
  }
  
  public void addXml(String xml) {
    Preconditions.checkNotNull(xml);
    children.add(xml);
  }
  
  public void setAttribute(String name, String value) {
    Preconditions.checkNotNull(name, "name");
    Preconditions.checkNotNull(value, "value");
    attributes.add(name + "=\"" + value + "\"");
  }
  
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("<" + elementType);
    for(String a : attributes) {
      builder.append(" " + a);
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
