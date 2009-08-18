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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.sites.liberation.util.EntryType.ANNOUNCEMENTS_PAGE;
import static com.google.sites.liberation.util.EntryType.FILE_CABINET_PAGE;
import static com.google.sites.liberation.util.EntryType.LIST_PAGE;
import static com.google.sites.liberation.util.EntryType.getType;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gdata.data.sites.AnnouncementEntry;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.BasePageEntry;
import com.google.gdata.data.sites.CommentEntry;
import com.google.gdata.data.sites.ListItemEntry;
import com.google.gdata.data.sites.ListPageEntry;
import com.google.inject.Inject;
import com.google.sites.liberation.renderers.AncestorLinksRenderer;
import com.google.sites.liberation.renderers.AnnouncementsRenderer;
import com.google.sites.liberation.renderers.AttachmentsRenderer;
import com.google.sites.liberation.renderers.CommentsRenderer;
import com.google.sites.liberation.renderers.ContentRenderer;
import com.google.sites.liberation.renderers.FileCabinetRenderer;
import com.google.sites.liberation.renderers.ListRenderer;
import com.google.sites.liberation.renderers.SubpageLinksRenderer;
import com.google.sites.liberation.renderers.TitleRenderer;
import com.google.sites.liberation.util.EntryUtils;
import com.google.sites.liberation.util.XmlElement;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Implements {@link PageExporter} to export a single page in a 
 * Site as to a given {@code Appendable}. 
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
final class PageExporterImpl implements PageExporter {
  
  private static final Comparator<BaseContentEntry<?>> updatedComparator =
      EntryUtils.getReverseUpdatedComparator();
  private static final Comparator<BaseContentEntry<?>> titleComparator =
      EntryUtils.getTitleComparator();
  
  private AncestorLinksRenderer ancestorLinksRenderer;
  private AnnouncementsRenderer announcementsRenderer;
  private AttachmentsRenderer attachmentsRenderer;
  private CommentsRenderer commentsRenderer;
  private ContentRenderer contentRenderer;
  private FileCabinetRenderer fileCabinetRenderer;
  private ListRenderer listRenderer;
  private SubpageLinksRenderer subpageLinksRenderer;
  private TitleRenderer titleRenderer;
  
  @Inject
  PageExporterImpl(
      AncestorLinksRenderer ancestorLinksRenderer,
      AnnouncementsRenderer announcementsRenderer,
      AttachmentsRenderer attachmentsRenderer,
      CommentsRenderer commentsRenderer,
      ContentRenderer contentRenderer,
      FileCabinetRenderer fileCabinetRenderer,
      ListRenderer listRenderer,
      SubpageLinksRenderer subpageLinksRenderer,
      TitleRenderer titleRenderer) {
    this.ancestorLinksRenderer = checkNotNull(ancestorLinksRenderer);
    this.announcementsRenderer = checkNotNull(announcementsRenderer);
    this.attachmentsRenderer = checkNotNull(attachmentsRenderer);
    this.commentsRenderer = checkNotNull(commentsRenderer);
    this.contentRenderer = checkNotNull(contentRenderer);
    this.fileCabinetRenderer = checkNotNull(fileCabinetRenderer);
    this.listRenderer = checkNotNull(listRenderer);
    this.subpageLinksRenderer = checkNotNull(subpageLinksRenderer);
    this.titleRenderer = checkNotNull(titleRenderer);
  }
  
  @Override
  public void exportPage(BasePageEntry<?> entry, EntryStore entryStore,
      Appendable out, boolean revisionsExported) throws IOException {
    checkNotNull(entry, "entry");
    checkNotNull(entryStore, "entryStore");
    checkNotNull(out, "out");
    XmlElement html = new XmlElement("html");
    XmlElement head = new XmlElement("head");
    XmlElement title = new XmlElement("title");
    title.addText(entry.getTitle().getPlainText());
    html.addElement(head.addElement(title));
    XmlElement body = new XmlElement("body");
    XmlElement table = new XmlElement("table").setAttribute("width", "100%");
    XmlElement row = new XmlElement("tr").setAttribute("valign", "top");
    XmlElement sideBar = getSideBar(entry, entryStore);
    row.addElement(new XmlElement("td").addElement(sideBar).setAttribute(
        "width", "150px"));
    row.addElement(new XmlElement("td").addXml("&#160;"));
    XmlElement mainDiv = new XmlElement("div");
    mainDiv.setAttribute("class", "hentry " + getType(entry).toString());
    mainDiv.setAttribute("id", entry.getId());
    if (entryStore.getParent(entry.getId()) != null) {
      List<BasePageEntry<?>> ancestors = getAncestors(entry, entryStore);
      mainDiv.addElement(ancestorLinksRenderer.renderAncestorLinks(ancestors));      
    }
    mainDiv.addElement(titleRenderer.renderTitle(entry));
    mainDiv.addElement(contentRenderer.renderContent(entry, revisionsExported));
    List<AnnouncementEntry> announcements = Lists.newArrayList();
    List<BaseContentEntry<?>> attachments = Lists.newArrayList();
    List<CommentEntry> comments = Lists.newArrayList();
    List<ListItemEntry> listItems = Lists.newArrayList();
    List<BasePageEntry<?>> subpages = Lists.newArrayList();
    for (BaseContentEntry<?> child : entryStore.getChildren(entry.getId())) {
      switch(getType(child)) {
        case ANNOUNCEMENT:
          announcements.add((AnnouncementEntry) child); break;
        case ATTACHMENT:
        case WEB_ATTACHMENT:
          attachments.add(child); break;
        case COMMENT:
          comments.add((CommentEntry) child); break;
        case LIST_ITEM:
          listItems.add((ListItemEntry) child); break;
        default:
          subpages.add((BasePageEntry<?>) child); break;
      }
    }
    Collections.sort(announcements, updatedComparator);
    Collections.sort(attachments, updatedComparator);
    Collections.sort(comments, updatedComparator);
    Collections.sort(listItems, updatedComparator);
    Collections.sort(subpages, titleComparator);
    if (getType(entry) == ANNOUNCEMENTS_PAGE) {
      mainDiv.addElement(announcementsRenderer
          .renderAnnouncements(announcements));
    } else if (getType(entry) == FILE_CABINET_PAGE) {
      mainDiv.addElement(fileCabinetRenderer.renderFileCabinet(attachments));
    } else if (getType(entry) == LIST_PAGE) {
      mainDiv.addElement(listRenderer.renderList(
          (ListPageEntry) entry, listItems));
    }
    if (!subpages.isEmpty()) {
      mainDiv.addElement(new XmlElement("hr"));
      mainDiv.addElement(subpageLinksRenderer.renderSubpageLinks(subpages));
    }
    if (!attachments.isEmpty() && getType(entry) != FILE_CABINET_PAGE) {
      mainDiv.addElement(new XmlElement("hr"));
      mainDiv.addElement(attachmentsRenderer.renderAttachments(attachments));
    }
    if (!comments.isEmpty()) {
      mainDiv.addElement(new XmlElement("hr"));
      mainDiv.addElement(commentsRenderer.renderComments(comments));
    }
    row.addElement(new XmlElement("td").addElement(mainDiv));
    html.addElement(body.addElement(table.addElement(row)));
    html.appendTo(out);
  }
  
  private XmlElement getSideBar(BasePageEntry<?> entry, EntryStore entryStore) {
    XmlElement table = new XmlElement("table");
    table.addElement(new XmlElement("tr").addElement(new XmlElement("th")
        .addText("Navigation").setAttribute("align", "left")));
    Set<BasePageEntry<?>> pages = Sets.newTreeSet(titleComparator);
    pages.addAll(entryStore.getTopLevelEntries());
    String pathToRoot = getPathToRoot(entry, entryStore);
    for (BasePageEntry<?> page : pages) {
      String text = page.getTitle().getPlainText();
      if (page.equals(entry)) {
        table.addElement(new XmlElement("tr").addElement(new XmlElement("td")
            .addElement(new XmlElement("small").addText(text))));
      } else {
        String href = pathToRoot + page.getPageName().getValue() + "/index.html";
        XmlElement link = new XmlElement("a").addText(text)
            .setAttribute("href", href);
        table.addElement(new XmlElement("tr").addElement(new XmlElement("td")
            .addElement(new XmlElement("small").addElement(link))));
      }
    }
    return table;
  }
  
  private String getPathToRoot(BasePageEntry<?> entry, EntryStore entryStore) {
    BasePageEntry<?> parent = entryStore.getParent(entry.getId());
    if (parent == null) {
      return "../";
    }
    return getPathToRoot(parent, entryStore) + "../";
  }
  
  private List<BasePageEntry<?>> getAncestors(BasePageEntry<?> entry,
      EntryStore entryStore) {
    BasePageEntry<?> parent = entryStore.getParent(entry.getId());
    if (parent == null) {
      return Lists.newLinkedList();
    }
    List<BasePageEntry<?>> ancestors = getAncestors(parent, entryStore);
    ancestors.add(parent);
    return ancestors;
  }
}