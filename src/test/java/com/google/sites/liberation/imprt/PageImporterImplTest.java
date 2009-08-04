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

package com.google.sites.liberation.imprt;

import static org.junit.Assert.*;

import com.google.gdata.data.TextConstruct;
import com.google.gdata.data.TextContent;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.sites.liberation.parsers.AbstractParserImplTest;
import com.google.sites.liberation.parsers.ContentParser;
import com.google.sites.liberation.parsers.EntryParser;
import com.google.sites.liberation.util.EntryTree;
import com.google.sites.liberation.util.InMemoryEntryTreeFactory;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author bsimon@google.com (Benjamin Simon)
 */
public class PageImporterImplTest extends AbstractParserImplTest {

  private Mockery context;
  private ContentParser contentParser;
  private EntryParser entryParser;
  private PageImporter pageImporter;
  private Document document;
  
  @Before
  public void before() {
    context = new JUnit4Mockery() {{
      setImposteriser(ClassImposteriser.INSTANCE);
    }};
    contentParser = context.mock(ContentParser.class);
    entryParser = context.mock(EntryParser.class);
    pageImporter = new PageImporterImpl(contentParser, entryParser, 
        new InMemoryEntryTreeFactory());
    document = context.mock(Document.class);
  }
  
  @Test
  public void testNoEntryOrBody() {
    String html = "<html><head>" +
                    "<link rel=\"stylesheet\" />" +
                    "<script type=\"text/javascript\">alert();</script>" +
                  "</head></html>";
    final Element element = getElement(html);
    
    context.checking(new Expectations() {{
      allowing (document).getDocumentElement(); will(returnValue(element));
    }});
    
    assertNull(pageImporter.importPage(document));
  }
  
  @Test
  public void testNoEntryHasBody() {
    String body = "<body><h3>Title!</h3>And here's some content!</body>";
    String html = "<html><head></head>" + body + "</html>";
    final Element element = getElement(html);
    final Element bodyElement = (Element) element
        .getElementsByTagName("body").item(0);
    final TextConstruct content = context.mock(TextConstruct.class); 
    
    context.checking(new Expectations() {{
      allowing (document).getDocumentElement(); will(returnValue(element));
      allowing (contentParser).parseContent(bodyElement); 
        will(returnValue(content));
    }});
    
    BaseContentEntry<?> root = pageImporter.importPage(document).getRoot();
    assertEquals(content, ((TextContent)root.getContent()).getContent());
  }
  
  @Test
  public void testHasEntry() {
    String html = "<html><body>" +
                    "<h3>Title</h3>" +
                    "<div class=\"main\">" +
                      "<span class=\"hentry webpage\"></span>" +
                    "</div>" +
                  "</body></html>";
    final Element element = getElement(html);
    final Element entryElement = (Element) element
        .getElementsByTagName("span").item(0);
    final EntryTree entryTree = context.mock(EntryTree.class);
    
    context.checking(new Expectations() {{
      allowing (document).getDocumentElement(); will(returnValue(element));
      allowing (entryParser).parseEntry(entryElement); 
        will(returnValue(entryTree));
    }});
    
    assertEquals(entryTree, pageImporter.importPage(document));
  }
}
