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

import static com.google.sites.liberation.EntryType.ATTACHMENT;
import static com.google.sites.liberation.EntryType.COMMENT;
import static com.google.sites.liberation.EntryType.LIST_ITEM;

import com.google.gdata.data.sites.AttachmentEntry;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.CommentEntry;
import com.google.gdata.data.sites.ListItemEntry;
import com.google.gdata.data.sites.ListPageEntry;
import com.google.sites.liberation.EntryStore;
import com.google.sites.liberation.EntryType;
import com.google.sites.liberation.XmlElement;

import java.util.Collection;
import java.util.TreeSet;

/**
 * This is an extension of BasePageRenderer which implements 
 * PageRenderer.renderSpecialContent to render the list section in a List Page.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
class ListPageRenderer extends BasePageRenderer<ListPageEntry> {

  Collection<ListItemEntry> listItems;
  
  ListPageRenderer(ListPageEntry entry, EntryStore entryStore) {
    super(entry, entryStore);
  }
  
  /**
   * Overrides initChildren from BasePageRenderer so that a collection of 
   * ListItemEntry's for this page is initialized and populated in addition
   * to subpages, attachments, and comments.
   */
  @Override
  void initChildren() {
    subpages = new TreeSet<BaseContentEntry<?>>(new TitleComparator());
    attachments = new TreeSet<AttachmentEntry>(new UpdatedComparator());
    comments = new TreeSet<CommentEntry>(new UpdatedComparator());
    listItems = new TreeSet<ListItemEntry>();
    for(BaseContentEntry<?> child : entryStore.getChildren(entry.getId())) {
      if (EntryType.isPage(child)) {
        subpages.add(child);
      }
      else if (EntryType.getType(child) == ATTACHMENT) {
        attachments.add((AttachmentEntry)child);
      }
      else if (EntryType.getType(child) == COMMENT) {
        comments.add((CommentEntry)child);
      }
      else if (EntryType.getType(child) == LIST_ITEM) {
        listItems.add((ListItemEntry)child);
      }
    }
  }
  
  /**
   * Renders the list section in the page.
   */
  @Override
  public XmlElement renderSpecialContent() {
    XmlElement div = new XmlElement("div");
    div.addText("This is where the List will eventually go");
    return div;
  }
}
