package com.google.sites.liberation.parsers;

import static org.junit.Assert.fail;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilderFactory;

public abstract class AbstractParserImplTest {
  
  protected Element getElement(String html) {
    ByteArrayInputStream stream = new ByteArrayInputStream(html.getBytes());
    Document document = null;
    try {
      document = DocumentBuilderFactory.newInstance()
          .newDocumentBuilder().parse(stream);
    } catch (Exception e) {
      fail("Invalid html!");
    }
    return document.getDocumentElement();
  }
}
