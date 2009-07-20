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

import static org.junit.Assert.*;

import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.WebPageEntry;
import com.google.sites.liberation.elements.EntryElement;
import com.google.sites.liberation.elements.XmlElement;
import com.google.sites.liberation.renderers.PageRenderer;

import org.junit.Before;
import org.junit.Test;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;

import java.io.IOException;

/**
 * @author bsimon@google.com (Ben Simon)
 */
public class PageExporterImplTest {
  
  private Mockery context;
  private PageRenderer renderer;
  private PageExporter exporter;
  private Appendable out;
  
  @Before
  public void before() {
    context = new JUnit4Mockery();
    renderer = context.mock(PageRenderer.class);
    exporter = new PageExporterImpl();
    out = new StringBuilder();
  }
  
  @Test
  public void testNull() throws IOException {
    try {
      exporter.exportPage(null, out);
      fail("Should throw NullPointerException!");
    } catch (NullPointerException e) {}
    try {
      exporter.exportPage(renderer, null);
      fail("Should throw NullPointerException!");
    } catch (NullPointerException e) {}
  }
  
  @Test
  public void testFullExport() throws IOException {
    final BaseContentEntry<?> entry = new WebPageEntry();
    entry.setId("identification");
    final XmlElement parentLinks = new XmlElement("span").addText("parents");
    final XmlElement title = new XmlElement("h3").addText("Title");
    final XmlElement content = new XmlElement("div").addXml("<b>Whoo!</b>");
    final XmlElement additional = new XmlElement("table")
        .addElement(new XmlElement("tr"));
    final XmlElement subpages = new XmlElement("span").addText("sub");
    final XmlElement attachments = new XmlElement("hr");
    final XmlElement comments = new XmlElement("I like it!");
    
    context.checking(new Expectations() {{
      oneOf (renderer).getEntry(); will(returnValue(entry));
      oneOf (renderer).renderParentLinks(); will(returnValue(parentLinks));
      oneOf (renderer).renderTitle(); will(returnValue(title));
      oneOf (renderer).renderContent(); will(returnValue(content));
      oneOf (renderer).renderAdditionalContent(); will(returnValue(additional));
      oneOf (renderer).renderSubpageLinks(); will(returnValue(subpages));
      oneOf (renderer).renderAttachments(); will(returnValue(attachments));
      oneOf (renderer).renderComments(); will(returnValue(comments));
    }});
    
    exporter.exportPage(renderer, out);
    XmlElement wrapper = new EntryElement(entry);
    wrapper.addElement(parentLinks);
    wrapper.addElement(title).addElement(content).addElement(additional)
        .addElement(subpages).addElement(attachments).addElement(comments);
    XmlElement document = new XmlElement("html");
    document.addElement(new XmlElement("body").addElement(wrapper));
    assertEquals(document.toString(), out.toString());
  }
  
  @Test
  public void testSomeNullExport() throws IOException {
    final BaseContentEntry<?> entry = new WebPageEntry();
    entry.setId("identification");
    final XmlElement parentLinks = null;
    final XmlElement title = new XmlElement("h3").addText("Title");
    final XmlElement content = new XmlElement("div").addXml("<b>Whoo!</b>");
    final XmlElement additional = null;
    final XmlElement subpages = null;
    final XmlElement attachments = null;
    final XmlElement comments = null;
    
    context.checking(new Expectations() {{
      oneOf (renderer).getEntry(); will(returnValue(entry));
      oneOf (renderer).renderParentLinks(); will(returnValue(parentLinks));
      oneOf (renderer).renderTitle(); will(returnValue(title));
      oneOf (renderer).renderContent(); will(returnValue(content));
      oneOf (renderer).renderAdditionalContent(); will(returnValue(additional));
      oneOf (renderer).renderSubpageLinks(); will(returnValue(subpages));
      oneOf (renderer).renderAttachments(); will(returnValue(attachments));
      oneOf (renderer).renderComments(); will(returnValue(comments));
    }});
    
    exporter.exportPage(renderer, out);
    XmlElement wrapper = new EntryElement(entry);
    wrapper.addElement(title);
    wrapper.addElement(content);
    XmlElement document = new XmlElement("html");
    document.addElement(new XmlElement("body").addElement(wrapper));
    assertEquals(document.toString(), out.toString());
  }
}
