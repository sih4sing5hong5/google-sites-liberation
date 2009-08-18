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

import com.google.common.collect.Lists;
import com.google.gdata.client.Query;
import com.google.gdata.client.sites.SitesService;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.WebPageEntry;
import com.google.gdata.util.ServiceException;
import com.google.sites.liberation.util.EntryProvider;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author bsimon@google.com (Ben Simon)
 */
public class ContinuousContentFeedTest {
  
  private URL url;
  private SitesService sitesService;
  private final BaseContentEntry<?> serviceExceptionEntry = new WebPageEntry();
  private final BaseContentEntry<?> ioExceptionEntry = new WebPageEntry();
  
  @Before
  public void initUrl() throws MalformedURLException {
    url = new URL("http://test.com");
    sitesService = new SitesService("");
  }
  
  @Test
  public void testEmptyFeed() {
    EntryProvider entryProvider = new FakeEntryDownloader(
        new ArrayList<BaseContentEntry<?>>(), 5);
    ContinuousContentFeed feed = 
        new ContinuousContentFeed(url, entryProvider, sitesService, 3);
    for(BaseContentEntry<?> entry : feed) {
      fail("There should be no entries!");
    }
  }
  
  @Test
  public void testClientLimitsNumPerRequest() {
    List<BaseContentEntry<?>> entries = Lists.newArrayList();
    addNormalEntries(entries, 32);
    EntryProvider entryProvider = new FakeEntryDownloader(entries, 5);
    ContinuousContentFeed feed = 
        new ContinuousContentFeed(url, entryProvider, sitesService, 3);
    List<BaseContentEntry<?>> newEntries = Lists.newArrayList(feed);
    assertEquals(entries, newEntries);
  }
  
  @Test
  public void testClientLimitsWithExceptions() {
    List<BaseContentEntry<?>> entries = Lists.newArrayList();
    addNormalEntries(entries, 16);
    addServiceExceptions(entries, 2);
    addNormalEntries(entries, 5);
    addIoExceptions(entries, 1);
    addNormalEntries(entries, 4);
    addIoExceptions(entries, 3);
    EntryProvider entryProvider = new FakeEntryDownloader(entries, 5);
    ContinuousContentFeed feed = 
        new ContinuousContentFeed(url, entryProvider, sitesService, 4);
    List<BaseContentEntry<?>> newEntries = Lists.newArrayList(feed);
    while(entries.remove(serviceExceptionEntry)) {}
    while(entries.remove(ioExceptionEntry)) {}
    assertEquals(entries, newEntries);
  }
  
  @Test
  public void testServerLimitsNumPerRequest() {
    List<BaseContentEntry<?>> entries = Lists.newArrayList();
    addNormalEntries(entries, 45);
    EntryProvider entryProvider = new FakeEntryDownloader(entries, 5);
    ContinuousContentFeed feed = 
        new ContinuousContentFeed(url, entryProvider, sitesService, 7);
    List<BaseContentEntry<?>> newEntries = Lists.newArrayList(feed);
    assertEquals(entries, newEntries);
  }
  
  @Test
  public void testServerLimitsWithExceptions() {
    List<BaseContentEntry<?>> entries = Lists.newArrayList();
    addNormalEntries(entries, 8);
    addIoExceptions(entries, 1);
    addServiceExceptions(entries, 2);
    addNormalEntries(entries, 16);
    addIoExceptions(entries, 3);
    addServiceExceptions(entries, 1);
    addNormalEntries(entries, 20);
    EntryProvider entryProvider = new FakeEntryDownloader(entries, 5);
    ContinuousContentFeed feed = 
        new ContinuousContentFeed(url, entryProvider, sitesService, 8);
    List<BaseContentEntry<?>> newEntries = Lists.newArrayList(feed);
    while(entries.remove(serviceExceptionEntry)) {}
    while(entries.remove(ioExceptionEntry)) {}
    assertEquals(entries, newEntries);
  }
  
  @Test
  public void testNothingLimitsNumPerRequest() {
    List<BaseContentEntry<?>> entries = Lists.newArrayList();
    addNormalEntries(entries, 28);
    EntryProvider entryProvider = new FakeEntryDownloader(entries, 100);
    ContinuousContentFeed feed = 
        new ContinuousContentFeed(url, entryProvider, sitesService, 100);
    List<BaseContentEntry<?>> newEntries = Lists.newArrayList(feed);
    assertEquals(entries, newEntries);
  }
  
  @Test
  public void testNothingLimitsWithExceptions() {
    List<BaseContentEntry<?>> entries = Lists.newArrayList();
    addIoExceptions(entries, 4);
    addNormalEntries(entries, 21);
    addServiceExceptions(entries, 1);
    addNormalEntries(entries, 20);
    addServiceExceptions(entries, 4);
    addNormalEntries(entries, 1);
    EntryProvider entryProvider = new FakeEntryDownloader(entries, 100);
    ContinuousContentFeed feed = 
        new ContinuousContentFeed(url, entryProvider, sitesService, 100);
    List<BaseContentEntry<?>> newEntries = Lists.newArrayList(feed);
    while(entries.remove(serviceExceptionEntry)) {}
    while(entries.remove(ioExceptionEntry)) {}
    assertEquals(entries, newEntries);
  }
  
  private void addNormalEntries(List<BaseContentEntry<?>> entries, int num) {
    for(int i = 0; i < num; i++) {
      entries.add(new WebPageEntry());
    }
  }
  
  private void addServiceExceptions(List<BaseContentEntry<?>> entries, int num) {
    for(int i = 0; i < num; i++) {
      entries.add(serviceExceptionEntry);
    }
  }
  
  private void addIoExceptions(List<BaseContentEntry<?>> entries, int num) {
    for(int i = 0; i < num; i++) {
      entries.add(ioExceptionEntry);
    }
  }
  
  private class FakeEntryDownloader implements EntryProvider {
    
    private final int maxResultsPerRequest;
    private final List<BaseContentEntry<?>> entries;
    
    FakeEntryDownloader(List<BaseContentEntry<?>> entries, int maxResultsPerRequest) {
      this.maxResultsPerRequest = maxResultsPerRequest;
      this.entries = entries;
    }
    
    public List<BaseContentEntry<?>> getEntries(Query query, SitesService sitesService) 
        throws ServiceException, IOException {
      int fromIndex = query.getStartIndex() - 1;
      int max = Math.min(maxResultsPerRequest, query.getMaxResults());
      int toIndex = Math.min(fromIndex + max, entries.size());
      if (fromIndex > toIndex) {
        return new ArrayList<BaseContentEntry<?>>();
      }
      List<BaseContentEntry<?>> response = entries.subList(fromIndex, toIndex);
      if (response.contains(serviceExceptionEntry)) {
        throw new ServiceException("Error");
      }
      if (response.contains(ioExceptionEntry)) {
        throw new IOException("Error");
      }
      return response;
    }
  }
}
