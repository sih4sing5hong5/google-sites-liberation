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

package com.google.sites.liberation.export;

import static org.junit.Assert.*;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gdata.client.sites.SitesService;
import com.google.gdata.data.ILink;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.XhtmlTextConstruct;
import com.google.gdata.data.sites.AttachmentEntry;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.BasePageEntry;
import com.google.gdata.data.sites.FileCabinetPageEntry;
import com.google.gdata.data.sites.ListPageEntry;
import com.google.gdata.data.sites.PageName;
import com.google.gdata.data.sites.SitesLink;
import com.google.gdata.data.sites.WebPageEntry;
import com.google.gdata.util.XmlBlob;
import com.google.sites.liberation.util.ProgressListener;

import org.junit.Before;
import org.junit.Test;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

/**
 * @author bsimon@google.com (Benjamin Simon)
 */
public class SiteExporterImplTest {
  
  private Mockery context;
  private AbsoluteLinkConverter linkConverter;
  private AppendableFactory appendableFactory;
  private AttachmentDownloader attachmentDownloader;
  private EntryStore entryStore;
  private EntryStoreFactory entryStoreFactory;
  private FeedProvider feedProvider;
  private PageExporter pageExporter;
  private ProgressListener progressListener;
  private RevisionsExporter revisionsExporter;
  private SiteExporter siteExporter;
  private SitesService sitesService;
  private Collection<BaseContentEntry<?>> entries;
  private URL feedUrl;
  private Map<AttachmentEntry, File> downloaded;
  
  @Before
  public void before() throws MalformedURLException {
    context = new JUnit4Mockery() {{
      setImposteriser(ClassImposteriser.INSTANCE);
    }};
    linkConverter = context.mock(AbsoluteLinkConverter.class);
    appendableFactory = context.mock(AppendableFactory.class);
    attachmentDownloader = new FakeDownloader();
    entryStore = context.mock(EntryStore.class);
    entryStoreFactory = context.mock(EntryStoreFactory.class);
    feedProvider = context.mock(FeedProvider.class);
    pageExporter = context.mock(PageExporter.class);
    progressListener = context.mock(ProgressListener.class);
    revisionsExporter = context.mock(RevisionsExporter.class);
    siteExporter = new SiteExporterImpl(linkConverter, appendableFactory, 
        attachmentDownloader, entryStoreFactory, feedProvider, pageExporter, 
        revisionsExporter);
    sitesService = new SitesService("");
    entries = Sets.newHashSet();
    feedUrl = new URL("https://host/feeds/content/domain/webspace");
  }
  
  @Test
  public void testEmptyExport() {
    context.checking(new Expectations() {{
      allowing (entryStoreFactory).newEntryStore(); 
          will(returnValue(entryStore));
      allowing (progressListener).setStatus(with(any(String.class)));
      allowing (feedProvider).getEntries(feedUrl, sitesService);
          will(returnValue(entries));
    }});
    
    export(false);
  }
  
  @Test
  public void testOnePage() throws IOException {
    final BasePageEntry<?> page = new WebPageEntry();
    page.setId("1");
    page.setTitle(new PlainTextConstruct("Page 1"));
    page.setPageName(new PageName("Page-1"));
    XmlBlob blob = new XmlBlob();
    blob.setBlob("content");
    page.setContent(new XhtmlTextConstruct(blob));
    entries.add(page);
    final Appendable out = context.mock(Appendable.class);
    
    context.checking(new Expectations() {{
      allowing (entryStoreFactory).newEntryStore(); 
          will(returnValue(entryStore));
      allowing (feedProvider).getEntries(feedUrl, sitesService);
          will(returnValue(entries));
      allowing (entryStore).getEntry("1"); will(returnValue(page));
      allowing (entryStore).getParent("1"); will(returnValue(null));
      allowing (progressListener).setStatus(with(any(String.class)));
      allowing (progressListener).setProgress(with(any(Double.class)));
      oneOf (entryStore).addEntry(page);
      oneOf (appendableFactory).getAppendable(
          new File("path/Page-1/index.html"));
          will(returnValue(out));
      oneOf (linkConverter).convertLinks(page, entryStore, 
          new URL("https://host/a/domain/webspace"), false);
      oneOf (pageExporter).exportPage(page, entryStore, out, true);
      oneOf (revisionsExporter).exportRevisions(page, entryStore, 
          new File("path/Page-1"), sitesService, 
          new URL("https://host/a/domain/webspace"));
    }});
    
    export(true);
  }
  
  @Test
  public void testOnePageWithAttachment() throws IOException {
    final BasePageEntry<?> page = new FileCabinetPageEntry();
    page.setId("1");
    page.setTitle(new PlainTextConstruct("Page 1"));
    page.setPageName(new PageName("Page-1"));
    XmlBlob blob = new XmlBlob();
    blob.setBlob("content");
    page.setContent(new XhtmlTextConstruct(blob));
    final BaseContentEntry<?> attachment = new AttachmentEntry();
    attachment.setId("2");
    attachment.setTitle(new PlainTextConstruct("attach this.wow"));
    attachment.addLink(SitesLink.Rel.PARENT, ILink.Type.ATOM, "1");
    entries.add(page);
    entries.add(attachment);
    final Appendable out = context.mock(Appendable.class);
    
    context.checking(new Expectations() {{
      allowing (entryStoreFactory).newEntryStore(); 
          will(returnValue(entryStore));
      allowing (feedProvider).getEntries(feedUrl, sitesService);
          will(returnValue(entries));
      allowing (entryStore).getEntry("1"); will(returnValue(page));
      allowing (entryStore).getParent("1"); will(returnValue(null));
      allowing (entryStore).getEntry("2"); will(returnValue(attachment));
      allowing (entryStore).getParent("2"); will(returnValue(page));
      allowing (progressListener).setStatus(with(any(String.class)));
      allowing (progressListener).setProgress(with(any(Double.class)));
      oneOf (entryStore).addEntry(page);
      oneOf (entryStore).addEntry(attachment);
      oneOf (appendableFactory).getAppendable(
          new File("path/Page-1/index.html"));
          will(returnValue(out));
      oneOf (linkConverter).convertLinks(page, entryStore, 
          new URL("https://host/a/domain/webspace"), false);
      oneOf (pageExporter).exportPage(page, entryStore, out, false);
    }});
    
    export(false);
    assertTrue(downloaded.get(attachment).equals(
        new File("path/Page-1/attach this.wow")));
  }
  
  @Test
  public void testSeveralOfEach() throws IOException {
    final BasePageEntry<?> page1 = new WebPageEntry();
    page1.setId("1");
    page1.setTitle(new PlainTextConstruct("Page 1"));
    page1.setPageName(new PageName("Page-1"));
    XmlBlob blob = new XmlBlob();
    blob.setBlob("content");
    page1.setContent(new XhtmlTextConstruct(blob));
    final BaseContentEntry<?> attachment1 = new AttachmentEntry();
    attachment1.setId("2");
    attachment1.setTitle(new PlainTextConstruct("attach this.wow"));
    attachment1.addLink(SitesLink.Rel.PARENT, ILink.Type.ATOM, "1");
    final BasePageEntry<?> page2 = new ListPageEntry();
    page2.setId("3");
    page2.setTitle(new PlainTextConstruct("Page 2"));
    page2.setPageName(new PageName("Page-2"));
    page2.setContent(new XhtmlTextConstruct(blob));
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
    final Appendable out1 = context.mock(Appendable.class,
        "out1");
    final Appendable out2 = context.mock(Appendable.class,
        "out2");
    
    context.checking(new Expectations() {{
      allowing (entryStoreFactory).newEntryStore(); 
          will(returnValue(entryStore));
      allowing (feedProvider).getEntries(feedUrl, sitesService);
          will(returnValue(entries));
      allowing (entryStore).getEntry("1"); will(returnValue(page1));
      allowing (entryStore).getEntry("2"); will(returnValue(attachment1));
      allowing (entryStore).getEntry("3"); will(returnValue(page2));
      allowing (entryStore).getEntry("4"); will(returnValue(attachment2));
      allowing (entryStore).getEntry("5"); will(returnValue(attachment3));
      allowing (entryStore).getParent("1"); will(returnValue(null));
      allowing (entryStore).getParent("2"); will(returnValue(page1));
      allowing (entryStore).getParent("3"); will(returnValue(page1));
      allowing (entryStore).getParent("4"); will(returnValue(page1));
      allowing (entryStore).getParent("5"); will(returnValue(page2));
      allowing (progressListener).setStatus(with(any(String.class)));
      allowing (progressListener).setProgress(with(any(Double.class)));
      oneOf (entryStore).addEntry(page1);
      oneOf (entryStore).addEntry(attachment1);
      oneOf (entryStore).addEntry(page2);
      oneOf (entryStore).addEntry(attachment2);
      oneOf (entryStore).addEntry(attachment3);
      oneOf (appendableFactory).getAppendable(
          new File("path/Page-1/index.html"));
          will(returnValue(out1));
      oneOf (appendableFactory).getAppendable(
          new File("path/Page-1/Page-2/index.html"));
          will(returnValue(out2));
      oneOf (linkConverter).convertLinks(page1, entryStore, 
          new URL("https://host/a/domain/webspace"), false);
      oneOf (linkConverter).convertLinks(page2, entryStore, 
          new URL("https://host/a/domain/webspace"), false);
      oneOf (pageExporter).exportPage(page1, entryStore, out1, true);
      oneOf (pageExporter).exportPage(page2, entryStore, out2, true);
      oneOf (revisionsExporter).exportRevisions(page1, entryStore,
          new File("path/Page-1"), sitesService, 
          new URL("https://host/a/domain/webspace"));
      oneOf (revisionsExporter).exportRevisions(page2, entryStore,
          new File("path/Page-1/Page-2"), sitesService,
          new URL("https://host/a/domain/webspace"));
    }});
    
    export(true);
    assertTrue(downloaded.get(attachment1).equals(
        new File("path/Page-1/attach this.wow")));
    assertTrue(downloaded.get(attachment2).equals(
        new File("path/Page-1/picture.png")));
    assertTrue(downloaded.get(attachment3).equals(
        new File("path/Page-1/Page-2/document.doc")));
  }
  
  private void export(boolean exportRevisions) {
    siteExporter.exportSite("host", "domain", "webspace", exportRevisions, 
        sitesService, new File("path"), progressListener);
  }
  
  /**
   * This class was needed because mocking the AttachmentDownloader with JMock
   * kept non-sensically failing (Greg agreed).
   */
  private class FakeDownloader implements AttachmentDownloader {
    
    FakeDownloader() {
      downloaded = Maps.newHashMap();
    }
    
    @Override
    public void download(AttachmentEntry attachment, File file, 
        SitesService sitesService) {
      downloaded.put(attachment, file);
    }
  }
}
