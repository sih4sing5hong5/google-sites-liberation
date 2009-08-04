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

import com.google.common.collect.Lists;
import com.google.gdata.client.sites.ContentQuery;
import com.google.gdata.client.sites.SitesService;
import com.google.gdata.data.ILink;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.BasePageEntry;
import com.google.gdata.data.sites.ContentFeed;
import com.google.gdata.data.sites.PageName;
import com.google.gdata.data.sites.WebPageEntry;
import com.google.gdata.util.ServiceException;
import com.google.sites.liberation.util.EntryTree;
import com.google.sites.liberation.util.InMemoryEntryTreeFactory;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author bsimon@google.com (Benjamin Simon)
 */
public class SitesServiceEntryUploaderTest {

  private Mockery context;
  private SitesService sitesService;
  private EntryUploader entryUploader;
  private URL feedUrl;
  
  @Before
  public void before() throws MalformedURLException {
    context = new JUnit4Mockery() {{
      setImposteriser(ClassImposteriser.INSTANCE);
    }};
    sitesService = context.mock(SitesService.class);
    entryUploader = new SitesServiceEntryUploader(sitesService);
    feedUrl = new URL("http://sites.google.com/feeds/content/site/test");
  }
  
  @Test
  public void testUpdateById() throws IOException, ServiceException {
    final String id = feedUrl.toExternalForm() + "/entry";
    final BaseContentEntry<?> newEntry = new WebPageEntry();
    newEntry.setId(id);
    final BaseContentEntry<?> oldEntry = new WebPageEntry();
    oldEntry.setId(id);
    oldEntry.addLink(ILink.Rel.ENTRY_EDIT, ILink.Type.ATOM, id);
    final BaseContentEntry<?> returnedEntry = new WebPageEntry();
    returnedEntry.setId(id);
    
    context.checking(new Expectations() {{
      oneOf (sitesService).getEntry(new URL(id), WebPageEntry.class);
        will(returnValue(oldEntry));
      oneOf (sitesService).update(new URL(id), newEntry);
        will(returnValue(returnedEntry));
    }});
    
    assertEquals(returnedEntry, entryUploader.uploadEntry(newEntry, 
        context.mock(EntryTree.class), feedUrl));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void testUpdateByPath() throws IOException, ServiceException {
    final BasePageEntry<?> parent = new WebPageEntry();
    parent.setPageName(new PageName("parent"));
    final BasePageEntry<?> newEntry = new WebPageEntry();
    newEntry.setPageName(new PageName("entry"));
    final EntryTree entryTree = new InMemoryEntryTreeFactory()
        .getEntryTree(parent);
    entryTree.addEntry(newEntry, parent);
    final BasePageEntry<?> oldEntry = new WebPageEntry();
    final String id = feedUrl.toExternalForm() + "/entry";
    oldEntry.setId(id);
    oldEntry.addLink(ILink.Rel.ENTRY_EDIT, ILink.Type.ATOM, id);
    final BaseContentEntry<?> returnedEntry = new WebPageEntry();
    final ContentFeed feed = context.mock(ContentFeed.class);
    
    context.checking(new Expectations() {{
      allowing (sitesService).getFeed(with(any(ContentQuery.class)), 
          with(ContentFeed.class)); will(returnValue(feed));
      allowing (feed).getEntries(); 
          will(returnValue(Lists.newArrayList(oldEntry)));
      oneOf (sitesService).update(new URL(id), newEntry);
          will(returnValue(returnedEntry));
    }});
    
    assertEquals(returnedEntry, entryUploader.uploadEntry(newEntry, entryTree, 
        feedUrl));
  }
  
  @Test
  public void testInsert() throws IOException, ServiceException {
    final BasePageEntry<?> parent = new WebPageEntry();
    parent.setPageName(new PageName("parent"));
    final BasePageEntry<?> newEntry = new WebPageEntry();
    newEntry.setPageName(new PageName("entry"));
    final EntryTree entryTree = new InMemoryEntryTreeFactory()
        .getEntryTree(parent);
    entryTree.addEntry(newEntry, parent);
    final BasePageEntry<?> oldEntry = new WebPageEntry();
    final String id = feedUrl.toExternalForm() + "/entry";
    oldEntry.setId(id);
    oldEntry.addLink(ILink.Rel.ENTRY_EDIT, ILink.Type.ATOM, id);
    final BaseContentEntry<?> returnedEntry = new WebPageEntry();
    final ContentFeed feed = context.mock(ContentFeed.class);
    
    context.checking(new Expectations() {{
      allowing (sitesService).getFeed(with(any(ContentQuery.class)), 
          with(ContentFeed.class)); will(returnValue(feed));
      allowing (feed).getEntries(); 
          will(returnValue(Lists.newArrayList()));
      oneOf (sitesService).insert(feedUrl, newEntry);
          will(returnValue(returnedEntry));
    }});
    
    assertEquals(returnedEntry, entryUploader.uploadEntry(newEntry, entryTree, 
        feedUrl));
  }
}
