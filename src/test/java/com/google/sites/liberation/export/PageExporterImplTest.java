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

import com.google.common.collect.Lists;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.sites.AnnouncementEntry;
import com.google.gdata.data.sites.AnnouncementsPageEntry;
import com.google.gdata.data.sites.AttachmentEntry;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.BasePageEntry;
import com.google.gdata.data.sites.CommentEntry;
import com.google.gdata.data.sites.FileCabinetPageEntry;
import com.google.gdata.data.sites.ListItemEntry;
import com.google.gdata.data.sites.ListPageEntry;
import com.google.gdata.data.sites.PageName;
import com.google.gdata.data.sites.WebPageEntry;
import com.google.sites.liberation.util.EntryUtils;
import com.google.sites.liberation.util.XmlElement;
import com.google.sites.liberation.renderers.AncestorLinksRenderer;
import com.google.sites.liberation.renderers.AnnouncementsRenderer;
import com.google.sites.liberation.renderers.AttachmentsRenderer;
import com.google.sites.liberation.renderers.CommentsRenderer;
import com.google.sites.liberation.renderers.ContentRenderer;
import com.google.sites.liberation.renderers.FileCabinetRenderer;
import com.google.sites.liberation.renderers.ListRenderer;
import com.google.sites.liberation.renderers.SubpageLinksRenderer;
import com.google.sites.liberation.renderers.TitleRenderer;

import org.junit.Before;
import org.junit.Test;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;

import java.io.IOException;
import java.util.List;

/**
 * @author bsimon@google.com (Ben Simon)
 */
public class PageExporterImplTest {
  
  private Mockery context;
  private AncestorLinksRenderer ancestorLinksRenderer;
  private AnnouncementsRenderer announcementsRenderer;
  private AttachmentsRenderer attachmentsRenderer;
  private CommentsRenderer commentsRenderer;
  private ContentRenderer contentRenderer;
  private FileCabinetRenderer fileCabinetRenderer;
  private ListRenderer listRenderer;
  private SubpageLinksRenderer subpageLinksRenderer;
  private TitleRenderer titleRenderer;
  private PageExporter exporter;
  private Appendable out;
  private EntryStore entryStore;
  
  @Before
  public void before() {
    context = new JUnit4Mockery();
    ancestorLinksRenderer = context.mock(AncestorLinksRenderer.class);
    announcementsRenderer = context.mock(AnnouncementsRenderer.class);
    attachmentsRenderer = context.mock(AttachmentsRenderer.class);
    commentsRenderer = context.mock(CommentsRenderer.class);
    contentRenderer = context.mock(ContentRenderer.class);
    fileCabinetRenderer = context.mock(FileCabinetRenderer.class);
    listRenderer = context.mock(ListRenderer.class);
    subpageLinksRenderer = context.mock(SubpageLinksRenderer.class);
    titleRenderer = context.mock(TitleRenderer.class);
    exporter = new PageExporterImpl(
        ancestorLinksRenderer,
        announcementsRenderer,
        attachmentsRenderer,
        commentsRenderer,
        contentRenderer,
        fileCabinetRenderer,
        listRenderer,
        subpageLinksRenderer,
        titleRenderer);
    out = new StringBuilder();
    entryStore = new InMemoryEntryStoreFactory().newEntryStore();
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void testNormalExport() throws IOException {
    final BasePageEntry<?> grandparent = new WebPageEntry();
    grandparent.setId("grandparent");
    grandparent.setTitle(new PlainTextConstruct("grandparent"));
    grandparent.setPageName(new PageName("grandparent"));
    final BasePageEntry<?> parent = new WebPageEntry();
    parent.setId("parent");
    EntryUtils.setParentId(parent, grandparent.getId());
    final BasePageEntry<?> entry = new WebPageEntry();
    entry.setId("entry");
    entry.setTitle(new PlainTextConstruct("entry"));
    EntryUtils.setParentId(entry, parent.getId());
    final BasePageEntry<?> subpage1 = new WebPageEntry();
    subpage1.setId("subpage1");
    subpage1.setTitle(new PlainTextConstruct("subpage1"));
    EntryUtils.setParentId(subpage1, entry.getId());
    final BasePageEntry<?> subpage2 = new WebPageEntry();
    subpage2.setId("subpage2");
    subpage2.setTitle(new PlainTextConstruct("subpage2"));
    EntryUtils.setParentId(subpage2, entry.getId());
    final AttachmentEntry attachment1 = new AttachmentEntry();
    attachment1.setId("attachment1");
    attachment1.setUpdated(DateTime.parseDateTime("2009-08-06T16:08:12.107Z"));
    EntryUtils.setParentId(attachment1, entry.getId());
    final AttachmentEntry attachment2 = new AttachmentEntry();
    attachment2.setId("attachment2");
    attachment2.setUpdated(DateTime.parseDateTime("2009-08-06T16:26:57.019Z"));
    EntryUtils.setParentId(attachment2, entry.getId());
    final CommentEntry comment1 = new CommentEntry();
    comment1.setId("comment1");
    comment1.setUpdated(DateTime.parseDateTime("2009-08-06T16:08:12.107Z"));
    EntryUtils.setParentId(comment1, entry.getId());
    final CommentEntry comment2 = new CommentEntry();
    comment2.setId("comment2");
    comment2.setUpdated(DateTime.parseDateTime("2009-08-06T16:26:57.019Z"));
    EntryUtils.setParentId(comment2, entry.getId());
    
    entryStore.addEntry(grandparent);
    entryStore.addEntry(parent);
    entryStore.addEntry(entry);
    entryStore.addEntry(subpage1);
    entryStore.addEntry(subpage2);
    entryStore.addEntry(attachment1);
    entryStore.addEntry(attachment2);
    entryStore.addEntry(comment1);
    entryStore.addEntry(comment2);
    
    final List<BasePageEntry<?>> ancestors = Lists.newArrayList();
    ancestors.add(grandparent);
    ancestors.add(parent);
    final List<BasePageEntry<?>> subpages = Lists.newArrayList();
    subpages.add(subpage1);
    subpages.add(subpage2);
    final List<BaseContentEntry<?>> attachments = Lists.newArrayList();
    attachments.add(attachment2);
    attachments.add(attachment1);
    final List<CommentEntry> comments = Lists.newArrayList();
    comments.add(comment2);
    comments.add(comment1);
    
    context.checking(new Expectations() {{
      oneOf (ancestorLinksRenderer).renderAncestorLinks(
          with(equal(ancestors))); 
        will(returnValue(new XmlElement("div")));
      oneOf (titleRenderer).renderTitle(entry); 
        will(returnValue(new XmlElement("div")));
      oneOf (contentRenderer).renderContent(entry, false); 
        will(returnValue(new XmlElement("div")));
      oneOf (subpageLinksRenderer).renderSubpageLinks(with(equal(subpages))); 
        will(returnValue(new XmlElement("div")));
      oneOf (attachmentsRenderer).renderAttachments(with(equal(attachments))); 
        will(returnValue(new XmlElement("div")));
      oneOf (commentsRenderer).renderComments(with(equal(comments))); 
        will(returnValue(new XmlElement("div")));
    }});
    
    exporter.exportPage(entry, entryStore, out, false);
  }

  @Test
  public void testListExport() throws IOException {
    final ListPageEntry entry = new ListPageEntry();
    entry.setId("entry");
    entry.setTitle(new PlainTextConstruct("entry"));
    final ListItemEntry listItem1 = new ListItemEntry();
    listItem1.setId("listItem1");
    listItem1.setUpdated(DateTime.parseDateTime("2009-08-06T16:08:12.107Z"));
    EntryUtils.setParentId(listItem1, entry.getId());
    final ListItemEntry listItem2 = new ListItemEntry();
    listItem2.setId("listItem2");
    listItem2.setUpdated(DateTime.parseDateTime("2009-08-06T16:26:57.019Z"));
    EntryUtils.setParentId(listItem2, entry.getId());
    
    entryStore.addEntry(entry);
    entryStore.addEntry(listItem1);
    entryStore.addEntry(listItem2);
    
    final List<ListItemEntry> listItems = Lists.newArrayList();
    listItems.add(listItem2);
    listItems.add(listItem1);
    
    context.checking(new Expectations() {{
      oneOf (titleRenderer).renderTitle(entry); 
        will(returnValue(new XmlElement("div")));
      oneOf (contentRenderer).renderContent(entry, true); 
        will(returnValue(new XmlElement("div")));
      oneOf (listRenderer).renderList(with(entry), with(equal(listItems))); 
        will(returnValue(new XmlElement("div")));
    }});
    
    exporter.exportPage(entry, entryStore, out, true);
  }
  
  @Test
  public void testFileCabinetExport() throws IOException {
    final FileCabinetPageEntry entry = new FileCabinetPageEntry();
    entry.setId("entry");
    entry.setTitle(new PlainTextConstruct("entry"));
    final AttachmentEntry attachment1 = new AttachmentEntry();
    attachment1.setId("attachment1");
    attachment1.setUpdated(DateTime.parseDateTime("2009-08-06T16:08:12.107Z"));
    EntryUtils.setParentId(attachment1, entry.getId());
    final AttachmentEntry attachment2 = new AttachmentEntry();
    attachment2.setId("attachment2");
    attachment2.setUpdated(DateTime.parseDateTime("2009-08-06T16:26:57.019Z"));
    EntryUtils.setParentId(attachment2, entry.getId());
    
    entryStore.addEntry(entry);
    entryStore.addEntry(attachment1);
    entryStore.addEntry(attachment2);
    
    final List<BaseContentEntry<?>> attachments = Lists.newArrayList();
    attachments.add(attachment2);
    attachments.add(attachment1);
    
    context.checking(new Expectations() {{
      oneOf (titleRenderer).renderTitle(entry); 
        will(returnValue(new XmlElement("div")));
      oneOf (contentRenderer).renderContent(entry, false); 
        will(returnValue(new XmlElement("div")));
      oneOf (fileCabinetRenderer).renderFileCabinet(with(equal(attachments))); 
        will(returnValue(new XmlElement("div")));
    }});
    
    exporter.exportPage(entry, entryStore, out, false);
  }
  
  @Test
  public void testAnnouncementsExport() throws IOException {
    final AnnouncementsPageEntry entry = new AnnouncementsPageEntry();
    entry.setId("entry");
    entry.setTitle(new PlainTextConstruct("entry"));
    final AnnouncementEntry announcement1 = new AnnouncementEntry();
    announcement1.setId("announcement1");
    announcement1.setUpdated(DateTime.parseDateTime("2009-08-06T16:08:12.107Z"));
    EntryUtils.setParentId(announcement1, entry.getId());
    final AnnouncementEntry announcement2 = new AnnouncementEntry();
    announcement2.setId("attachment2");
    announcement2.setUpdated(DateTime.parseDateTime("2009-08-06T16:26:57.019Z"));
    EntryUtils.setParentId(announcement2, entry.getId());
    
    entryStore.addEntry(entry);
    entryStore.addEntry(announcement1);
    entryStore.addEntry(announcement2);
    
    final List<AnnouncementEntry> announcements = Lists.newArrayList();
    announcements.add(announcement2);
    announcements.add(announcement1);
    
    context.checking(new Expectations() {{
      oneOf (titleRenderer).renderTitle(entry); 
        will(returnValue(new XmlElement("div")));
      oneOf (contentRenderer).renderContent(entry, true); 
        will(returnValue(new XmlElement("div")));
      oneOf (announcementsRenderer).renderAnnouncements(
          with(equal(announcements))); 
        will(returnValue(new XmlElement("div")));
    }});
    
    exporter.exportPage(entry, entryStore, out, true);
  }
}
