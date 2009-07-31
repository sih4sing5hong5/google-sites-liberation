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

import static org.junit.Assert.*;

import com.google.gdata.data.DateTime;
import com.google.gdata.data.Person;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.TextConstruct;
import com.google.gdata.data.TextContent;
import com.google.gdata.data.XhtmlTextConstruct;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.ListItemEntry;
import com.google.gdata.data.sites.ListPageEntry;
import com.google.gdata.data.spreadsheet.Data;
import com.google.gdata.data.spreadsheet.Field;
import com.google.gdata.util.XmlBlob;
import com.google.sites.liberation.util.EntryTree;
import com.google.sites.liberation.util.EntryType;
import com.google.sites.liberation.util.InMemoryEntryTreeFactory;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * @author bsimon@google.com (Benjamin Simon)
 */
public class EntryParserImplTest {
  
  private Mockery context;
  private AuthorParser authorParser;
  private ContentParser contentParser;
  private DataParser dataParser;
  private EntryParser entryParser;
  private FieldParser fieldParser;
  private SummaryParser summaryParser;
  private TitleParser titleParser;
  private UpdatedParser updatedParser;
  
  @Before
  public void before() {
    context = new JUnit4Mockery() {{
      setImposteriser(ClassImposteriser.INSTANCE);
    }};
    authorParser = context.mock(AuthorParser.class);
    contentParser = context.mock(ContentParser.class);
    dataParser = context.mock(DataParser.class);
    fieldParser = context.mock(FieldParser.class);
    summaryParser = context.mock(SummaryParser.class);
    titleParser = context.mock(TitleParser.class);
    updatedParser = context.mock(UpdatedParser.class);
    entryParser = new EntryParserImpl(authorParser, contentParser, dataParser,
        new InMemoryEntryTreeFactory(), fieldParser, summaryParser, titleParser, 
        updatedParser);
  }
  
  @Test
  public void testNormalPage() {
    Document document = null;
    try {
      document = DocumentBuilderFactory.newInstance()
          .newDocumentBuilder().newDocument();
    } catch (ParserConfigurationException e) {
      fail("Failure to create test document");
    }
    final Element entryElement = document.createElement("div");
    entryElement.setAttribute("class", "hentry webpage");
    entryElement.setAttribute("id", "http://identification");
    final Element authorElement = document.createElement("span");
    authorElement.setAttribute("class", "vcard");
    entryElement.appendChild(authorElement);
    final Element contentElement = document.createElement("div");
    contentElement.setAttribute("class", "entry-content");
    entryElement.appendChild(contentElement);
    final Element summaryElement = document.createElement("p");
    summaryElement.setAttribute("class", "summary");
    entryElement.appendChild(summaryElement);
    final Element titleElement = document.createElement("h3");
    titleElement.setAttribute("class", "entry-title");
    entryElement.appendChild(titleElement);
    final Element updatedElement = document.createElement("abbr");
    updatedElement.setAttribute("class", "updated");
    entryElement.appendChild(updatedElement);  
    
    final Person author = new Person();
    final TextConstruct content = new XhtmlTextConstruct(new XmlBlob());
    final TextConstruct summary = new PlainTextConstruct();
    final TextConstruct title = new PlainTextConstruct();
    final DateTime updated = DateTime.parseDateTime("2009-07-30T15:48:23.975Z");
    
    context.checking(new Expectations() {{
      oneOf (authorParser).parseAuthor(authorElement); 
        will(returnValue(author));
      oneOf (contentParser).parseContent(contentElement);
        will(returnValue(content));
      oneOf (summaryParser).parseSummary(summaryElement);
        will(returnValue(summary));
      oneOf (titleParser).parseTitle(titleElement);
        will(returnValue(title));
      oneOf (updatedParser).parseUpdated(updatedElement);
        will(returnValue(updated));
    }});
    
    EntryTree entryTree = entryParser.parseEntry(entryElement);
    BaseContentEntry<?> root = entryTree.getRoot();
    assertEquals("http://identification", root.getId());
    assertTrue(EntryType.getType(root) == EntryType.WEB_PAGE);
    assertEquals(author, root.getAuthors().get(0));
    //THIS LINE MAKES ME SO MAD!
    assertEquals(content, ((TextContent)root.getContent()).getContent());
    assertEquals(title, root.getTitle());
    assertEquals(updated, root.getUpdated());
  }
  
  @Test
  public void testDataParsing() {
    Document document = null;
    try {
      document = DocumentBuilderFactory.newInstance()
          .newDocumentBuilder().newDocument();
    } catch (ParserConfigurationException e) {
      fail("Failure to create test document");
    }
    final Element entryElement = document.createElement("div");
    entryElement.setAttribute("class", "hentry listpage");
    final Element dataElement = document.createElement("tr");
    dataElement.setAttribute("class", "gs:data");
    entryElement.appendChild(dataElement);
    
    final Data data = new Data();
    
    context.checking(new Expectations() {{
      oneOf (dataParser).parseData(dataElement); 
        will(returnValue(data));
    }});
    
    EntryTree entryTree = entryParser.parseEntry(entryElement);
    BaseContentEntry<?> root = entryTree.getRoot();
    assertTrue(EntryType.getType(root) == EntryType.LIST_PAGE);
    assertEquals(data, ((ListPageEntry) root).getData());
  }
  
  @Test
  public void testFieldParsing() {
    Document document = null;
    try {
      document = DocumentBuilderFactory.newInstance()
          .newDocumentBuilder().newDocument();
    } catch (ParserConfigurationException e) {
      fail("Failure to create test document");
    }
    final Element entryElement = document.createElement("tr");
    entryElement.setAttribute("class", "hentry listitem");
    final Element fieldElement1 = document.createElement("td");
    fieldElement1.setAttribute("class", "gs:field");
    entryElement.appendChild(fieldElement1);
    final Element fieldElement2 = document.createElement("td");
    fieldElement2.setAttribute("class", "gs:field");
    entryElement.appendChild(fieldElement2);
    final Element fieldElement3 = document.createElement("td");
    fieldElement3.setAttribute("class", "gs:field");
    entryElement.appendChild(fieldElement3);
    
    final Field field1 = new Field();
    final Field field2 = new Field();
    final Field field3 = new Field();
    
    context.checking(new Expectations() {{
      oneOf (fieldParser).parseField(fieldElement1); 
        will(returnValue(field1));
      oneOf (fieldParser).parseField(fieldElement2); 
        will(returnValue(field2));
      oneOf (fieldParser).parseField(fieldElement3); 
        will(returnValue(field3));
    }});
    
    EntryTree entryTree = entryParser.parseEntry(entryElement);
    BaseContentEntry<?> root = entryTree.getRoot();
    assertTrue(EntryType.getType(root) == EntryType.LIST_ITEM);
    assertTrue(((ListItemEntry) root).getFields().contains(field1));
    assertTrue(((ListItemEntry) root).getFields().contains(field2));
    assertTrue(((ListItemEntry) root).getFields().contains(field3));
  }
  
  @Test
  public void testChildParsing() {
    Document document = null;
    try {
      document = DocumentBuilderFactory.newInstance()
          .newDocumentBuilder().newDocument();
    } catch (ParserConfigurationException e) {
      fail("Failure to create test document");
    }
    final Element entryElement = document.createElement("div");
    entryElement.setAttribute("class", "hentry listpage");
    final Element attachmentElement1 = document.createElement("span");
    attachmentElement1.setAttribute("class", "hentry attachment");
    entryElement.appendChild(attachmentElement1);
    final Element attachmentElement2 = document.createElement("p");
    attachmentElement2.setAttribute("class", "hentry attachment");
    entryElement.appendChild(attachmentElement2);
    final Element commentItemElement = document.createElement("tr");
    commentItemElement.setAttribute("class", "hentry comment");
    entryElement.appendChild(commentItemElement);
    final Element listItemElement = document.createElement("tr");
    listItemElement.setAttribute("class", "hentry listitem");
    entryElement.appendChild(listItemElement);
    
    EntryTree entryTree = entryParser.parseEntry(entryElement);
    ListPageEntry root = (ListPageEntry) entryTree.getRoot();
    Set<BaseContentEntry<?>> children = entryTree.getChildren(root);
    int numAttachments = 0;
    int numComments = 0;
    int numListItems = 0;
    for(BaseContentEntry<?> child : entryTree.getChildren(root)) {
      switch(EntryType.getType(child)) {
        case ATTACHMENT: numAttachments++; break;
        case COMMENT: numComments++; break;
        case LIST_ITEM: numListItems++; break;
        default: fail("There should be no other children.");
      }
    }
    assertEquals(2, numAttachments);
    assertEquals(1, numComments);
    assertEquals(1, numListItems);
  }
  
  @Test
  public void testUnusualStructure() {
    Document document = null;
    try {
      document = DocumentBuilderFactory.newInstance()
          .newDocumentBuilder().newDocument();
    } catch (ParserConfigurationException e) {
      fail("Failure to create test document");
    }
    final Element entryElement = document.createElement("div");
    entryElement.setAttribute("class", "hentry announcementspage");
    final Element row = document.createElement("tr");
    final Element cell = document.createElement("td");
    final Element authorElement = document.createElement("span");
    authorElement.setAttribute("class", "vcard");
    cell.appendChild(authorElement);
    row.appendChild(cell);
    final Element contentElement = document.createElement("td");
    contentElement.setAttribute("class", "entry-content");
    row.appendChild(contentElement);
    entryElement.appendChild(document.createElement("table").appendChild(row));
    final Element titleElement = document.createElement("h3");
    titleElement.setAttribute("class", "entry-title");
    entryElement.appendChild(document.createElement("b")
        .appendChild(document.createElement("i").appendChild(titleElement)));
    
    final Person author = new Person();
    final TextConstruct content = new XhtmlTextConstruct(new XmlBlob());
    final TextConstruct title = new PlainTextConstruct();
    
    context.checking(new Expectations() {{
      oneOf (authorParser).parseAuthor(authorElement); 
        will(returnValue(author));
      oneOf (contentParser).parseContent(contentElement);
        will(returnValue(content));
      oneOf (titleParser).parseTitle(titleElement);
        will(returnValue(title));
    }});
    
    EntryTree entryTree = entryParser.parseEntry(entryElement);
    BaseContentEntry<?> root = entryTree.getRoot();
    assertTrue(EntryType.getType(root) == EntryType.ANNOUNCEMENTS_PAGE);
    assertEquals(author, root.getAuthors().get(0));
    assertEquals(content, ((TextContent)root.getContent()).getContent());
    assertEquals(title, root.getTitle());
  }
}
