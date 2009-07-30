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

package com.google.sites.liberation.renderers;

import static org.junit.Assert.*;

import com.google.common.collect.Lists;
import com.google.gdata.data.sites.AnnouncementEntry;
import com.google.gdata.data.sites.AnnouncementsPageEntry;
import com.google.gdata.data.sites.FileCabinetPageEntry;
import com.google.gdata.data.sites.ListPageEntry;
import com.google.gdata.data.sites.WebPageEntry;
import com.google.sites.liberation.util.EntryStore;

import org.junit.Before;
import org.junit.Test;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;

/**
 * @author bsimon@google.com (Benjamin Simon)
 */
public class PageRendererFactoryImplTest {

  private Mockery context;
  private PageRendererFactory rendererFactory;
  private EntryStore entryStore;
  
  @Before
  public void before() {
    context = new JUnit4Mockery();
    rendererFactory = new PageRendererFactoryImpl();
    entryStore = context.mock(EntryStore.class);
  }
  
  @Test
  public void testGetPageRenderer() {
    context.checking(new Expectations() {{
      oneOf (entryStore).getChildren(null); will(returnValue(Lists.newArrayList()));
      oneOf (entryStore).getChildren(null); will(returnValue(Lists.newArrayList()));
      oneOf (entryStore).getChildren(null); will(returnValue(Lists.newArrayList()));
      oneOf (entryStore).getChildren(null); will(returnValue(Lists.newArrayList()));
      oneOf (entryStore).getChildren(null); will(returnValue(Lists.newArrayList()));
    }});
    
    assertTrue(rendererFactory.getPageRenderer(new AnnouncementEntry(), 
        entryStore) instanceof BasePageRenderer);
    assertTrue(rendererFactory.getPageRenderer(new AnnouncementsPageEntry(), 
        entryStore) instanceof AnnouncementsPageRenderer);
    assertTrue(rendererFactory.getPageRenderer(new FileCabinetPageEntry(), 
        entryStore) instanceof FileCabinetPageRenderer);
    assertTrue(rendererFactory.getPageRenderer(new ListPageEntry(), 
        entryStore) instanceof ListPageRenderer);
    assertTrue(rendererFactory.getPageRenderer(new WebPageEntry(), 
        entryStore) instanceof BasePageRenderer);
  }
}
