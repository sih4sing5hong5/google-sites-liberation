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

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gdata.data.ILink;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.sites.AttachmentEntry;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.BasePageEntry;
import com.google.gdata.data.sites.FileCabinetPageEntry;
import com.google.gdata.data.sites.ListPageEntry;
import com.google.gdata.data.sites.PageName;
import com.google.gdata.data.sites.SitesLink;
import com.google.gdata.data.sites.WebPageEntry;
import com.google.sites.liberation.renderers.PageRenderer;
import com.google.sites.liberation.renderers.PageRendererFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * @author bsimon@google.com (Ben Simon)
 */
@RunWith(JMock.class)
public class SiteExporterImplTest {
  
  private Mockery context;
  private AppendableFactory appendableFactory;
  private AttachmentDownloader attachmentDownloader;
  private EntryStore entryStore;
  private EntryStoreFactory entryStoreFactory;
  private PageExporter pageExporter;
  private PageRendererFactory pageRendererFactory;
  private SiteExporter siteExporter;
  private Collection<BaseContentEntry<?>> entries;
  private Map<AttachmentEntry, File> downloaded;
  
  @Before
  public void before() {
    context = new JUnit4Mockery() {{
      setImposteriser(ClassImposteriser.INSTANCE);
    }};
    appendableFactory = context.mock(AppendableFactory.class);
    attachmentDownloader = new MockDownloader();
    entryStore = context.mock(EntryStore.class);
    entryStoreFactory = context.mock(EntryStoreFactory.class);
    pageExporter = context.mock(PageExporter.class);
    pageRendererFactory = context.mock(PageRendererFactory.class);
    siteExporter = new SiteExporterImpl(appendableFactory, attachmentDownloader,
        entryStoreFactory, pageExporter, pageRendererFactory);
    entries = Sets.newHashSet();
  }
  
  public void testNull() {
    try {
      siteExporter.exportSite(null, new File("path"));
      fail("Should throw NullPointerException!");
    } catch (NullPointerException e) {}
    try {
      siteExporter.exportSite(entries, null);
      fail("Should throw NullPointerException!");
    } catch (NullPointerException e) {}
  }
  
  @Test
  public void testEmptyExport() {
    context.checking(new Expectations() {{
      allowing (entryStoreFactory).getEntryStore(); 
          will(returnValue(entryStore));
    }});
    
    siteExporter.exportSite(entries, new File("path"));
  }
  
  @Test
  public void testOnePage() throws IOException {
    final BasePageEntry<?> page = new WebPageEntry();
    page.setId("1");
    page.setTitle(new PlainTextConstruct("Page 1"));
    page.setPageName(new PageName("Page-1"));
    entries.add(page);
    final PageRenderer pageRenderer = context.mock(PageRenderer.class);
    final Appendable out = context.mock(Appendable.class);
    
    context.checking(new Expectations() {{
      allowing (entryStoreFactory).getEntryStore(); 
          will(returnValue(entryStore));
      allowing (entryStore).getEntry("1"); will(returnValue(page));
      oneOf (entryStore).addEntry(page);
      oneOf (pageRendererFactory).getPageRenderer(page, entryStore); 
          will(returnValue(pageRenderer));
      oneOf (appendableFactory).getAppendable(
          new File("path/Page-1/index.html"));
          will(returnValue(out));
      oneOf (pageExporter).exportPage(pageRenderer, out);
    }});
    
    siteExporter.exportSite(entries, new File("path"));
  }
  
  @Test
  public void testOnePageWithAttachment() throws IOException {
    final BasePageEntry<?> page = new FileCabinetPageEntry();
    page.setId("1");
    page.setTitle(new PlainTextConstruct("Page 1"));
    page.setPageName(new PageName("Page-1"));
    final BaseContentEntry<?> attachment = new AttachmentEntry();
    attachment.setId("2");
    attachment.setTitle(new PlainTextConstruct("attach this.wow"));
    attachment.addLink(SitesLink.Rel.PARENT, ILink.Type.ATOM, "1");
    entries.add(page);
    entries.add(attachment);
    final PageRenderer pageRenderer = context.mock(PageRenderer.class);
    final Appendable out = context.mock(Appendable.class);
    
    context.checking(new Expectations() {{
      allowing (entryStoreFactory).getEntryStore(); 
          will(returnValue(entryStore));
      allowing (entryStore).getEntry("1"); will(returnValue(page));
      allowing (entryStore).getEntry("2"); will(returnValue(attachment));
      allowing (entryStore).getParent("2"); will(returnValue(page));
      oneOf (entryStore).addEntry(page);
      oneOf (entryStore).addEntry(attachment);
      oneOf (pageRendererFactory).getPageRenderer(page, entryStore); 
          will(returnValue(pageRenderer));
      oneOf (appendableFactory).getAppendable(
          new File("path/Page-1/index.html"));
          will(returnValue(out));
      oneOf (pageExporter).exportPage(pageRenderer, out);
    }});
    
    siteExporter.exportSite(entries, new File("path"));
    assertTrue(downloaded.get(attachment).equals(
        new File("path/Page-1/attach this.wow")));
  }
  
  @Test
  public void testSeveralOfEach() throws IOException {
    final BasePageEntry<?> page1 = new WebPageEntry();
    page1.setId("1");
    page1.setTitle(new PlainTextConstruct("Page 1"));
    page1.setPageName(new PageName("Page-1"));
    final BaseContentEntry<?> attachment1 = new AttachmentEntry();
    attachment1.setId("2");
    attachment1.setTitle(new PlainTextConstruct("attach this.wow"));
    attachment1.addLink(SitesLink.Rel.PARENT, ILink.Type.ATOM, "1");
    final BasePageEntry<?> page2 = new ListPageEntry();
    page2.setId("3");
    page2.setTitle(new PlainTextConstruct("Page 2"));
    page2.setPageName(new PageName("Page-2"));
    page2.addLink(SitesLink.Rel.PARENT, ILink.Type.ATOM, "1");
    final BaseContentEntry<?> attachment2 = new AttachmentEntry();
    attachment2.setId("4");
    attachment2.setTitle(new PlainTextConstruct("picture.png"));
    attachment2.addLink(SitesLink.Rel.PARENT, ILink.Type.ATOM, "1");
    final BaseContentEntry<?> attachment3 = new AttachmentEntry();
    attachment3.setId("5");
    attachment3.setTitle(new PlainTextConstruct("document.doc"));
    attachment3.addLink(SitesLink.Rel.PARENT, ILink.Type.ATOM, "3");
    entries.add(page1);
    entries.add(attachment1);
    entries.add(page2);
    entries.add(attachment2);
    entries.add(attachment3);
    final PageRenderer pageRenderer1 = context.mock(PageRenderer.class, 
        "pageRenderer1");
    final Appendable out1 = context.mock(Appendable.class,
        "out1");
    final PageRenderer pageRenderer2 = context.mock(PageRenderer.class,
        "pageRenderer2");
    final Appendable out2 = context.mock(Appendable.class,
        "out2");
    
    context.checking(new Expectations() {{
      allowing (entryStoreFactory).getEntryStore(); 
          will(returnValue(entryStore));
      allowing (entryStore).getEntry("1"); will(returnValue(page1));
      allowing (entryStore).getEntry("2"); will(returnValue(attachment1));
      allowing (entryStore).getEntry("3"); will(returnValue(page2));
      allowing (entryStore).getEntry("4"); will(returnValue(attachment2));
      allowing (entryStore).getEntry("5"); will(returnValue(attachment3));
      allowing (entryStore).getParent("2"); will(returnValue(page1));
      allowing (entryStore).getParent("3"); will(returnValue(page1));
      allowing (entryStore).getParent("4"); will(returnValue(page1));
      allowing (entryStore).getParent("5"); will(returnValue(page2));
      oneOf (entryStore).addEntry(page1);
      oneOf (entryStore).addEntry(attachment1);
      oneOf (entryStore).addEntry(page2);
      oneOf (entryStore).addEntry(attachment2);
      oneOf (entryStore).addEntry(attachment3);
      oneOf (pageRendererFactory).getPageRenderer(page1, entryStore); 
          will(returnValue(pageRenderer1));
      oneOf (pageRendererFactory).getPageRenderer(page2, entryStore); 
          will(returnValue(pageRenderer2));
      oneOf (appendableFactory).getAppendable(
          new File("path/Page-1/index.html"));
          will(returnValue(out1));
      oneOf (appendableFactory).getAppendable(
          new File("path/Page-1/Page-2/index.html"));
          will(returnValue(out2));
      oneOf (pageExporter).exportPage(pageRenderer1, out1);
      oneOf (pageExporter).exportPage(pageRenderer2, out2);
    }});
    
    siteExporter.exportSite(entries, new File("path"));
    assertTrue(downloaded.get(attachment1).equals(
        new File("path/Page-1/attach this.wow")));
    assertTrue(downloaded.get(attachment2).equals(
        new File("path/Page-1/picture.png")));
    assertTrue(downloaded.get(attachment3).equals(
        new File("path/Page-1/Page-2/document.doc")));
  }
  
  /**
   * This class was needed because mocking the AttachmentDownloader with JMock
   * kept non-sensically failing (Greg agreed).
   */
  private class MockDownloader implements AttachmentDownloader {
    
    MockDownloader() {
      downloaded = Maps.newHashMap();
    }
    
    @Override
    public void download(AttachmentEntry attachment, File file) {
      downloaded.put(attachment, file);
    }
  }
}
