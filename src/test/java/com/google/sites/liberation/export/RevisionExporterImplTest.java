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

import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.sites.BasePageEntry;
import com.google.gdata.data.sites.ListItemEntry;
import com.google.gdata.data.sites.ListPageEntry;
import com.google.gdata.data.sites.Revision;
import com.google.gdata.data.sites.WebPageEntry;
import com.google.sites.liberation.renderers.ListRenderer;
import com.google.sites.liberation.renderers.RevisionRenderer;
import com.google.sites.liberation.renderers.TitleRenderer;
import com.google.sites.liberation.util.XmlElement;

import org.junit.Before;
import org.junit.Test;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author bsimon@google.com (Ben Simon)
 */
public class RevisionExporterImplTest {
  
  private Mockery context;
  private ListRenderer listRenderer;
  private RevisionRenderer revisionRenderer;
  private TitleRenderer titleRenderer;
  private RevisionExporter revisionExporter;
  private Appendable out;
  
  @Before
  public void before() {
    context = new JUnit4Mockery();
    listRenderer = context.mock(ListRenderer.class);
    revisionRenderer = context.mock(RevisionRenderer.class);
    titleRenderer = context.mock(TitleRenderer.class);
    revisionExporter = new RevisionExporterImpl(
        listRenderer,
        revisionRenderer,
        titleRenderer);
    out = new StringBuilder();
  }
  
  @Test
  public void testNormalRevision() throws IOException {
    final BasePageEntry<?> revision = new WebPageEntry();
    revision.setTitle(new PlainTextConstruct("Title"));
    revision.setRevision(new Revision(3));
    revision.setId("http://revision");
    
    context.checking(new Expectations() {{
      oneOf (revisionRenderer).renderRevision(revision); 
          will(returnValue(new XmlElement("div")));
      oneOf (titleRenderer).renderTitle(revision);
          will(returnValue(new XmlElement("div")));
    }});
    
    revisionExporter.exportRevision(revision, out);
  }
  
  @Test
  public void testListPageRevision() throws IOException {
    final ListPageEntry revision = new ListPageEntry();
    revision.setTitle(new PlainTextConstruct("Title"));
    revision.setRevision(new Revision(3));
    revision.setId("http://revision");
    
    context.checking(new Expectations() {{
      oneOf (revisionRenderer).renderRevision(revision); 
          will(returnValue(new XmlElement("div")));
      oneOf (titleRenderer).renderTitle(revision);
          will(returnValue(new XmlElement("div")));
      oneOf (listRenderer).renderList(revision, new ArrayList<ListItemEntry>());
          will(returnValue(new XmlElement("div")));
    }});
    
    revisionExporter.exportRevision(revision, out);
  }
}
