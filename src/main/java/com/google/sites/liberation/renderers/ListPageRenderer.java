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

import static com.google.sites.liberation.EntryType.getType;
import static com.google.sites.liberation.EntryType.isPage;
import static com.google.sites.liberation.HAtomFactory.getListHeaderElement;
import static com.google.sites.liberation.HAtomFactory.getListItemElement;

import com.google.gdata.data.sites.AttachmentEntry;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.CommentEntry;
import com.google.gdata.data.sites.ListItemEntry;
import com.google.gdata.data.sites.ListPageEntry;
import com.google.sites.liberation.EntryStore;
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
   * Overrides addChild from BasePageRenderer so that listItems is populated 
   * in addition to subpages, attachments, and comments.
   */
  @Override
  protected void addChild(BaseContentEntry<?> child) {
    if (listItems == null) { 
      listItems = new TreeSet<ListItemEntry>(new UpdatedComparator());
    }
    switch(getType(child)) {
      case ATTACHMENT: attachments.add((AttachmentEntry) child); break;
      case COMMENT: comments.add((CommentEntry) child); break;
      case LIST_ITEM: listItems.add((ListItemEntry) child); break;
      default: 
        if (isPage(child)) {
          subpages.add(child);
        }
    }
  }
  
  /**
   * Renders the list section in the page.
   */
  @Override
  public XmlElement renderAdditionalContent() {
    XmlElement table = new XmlElement("table");
    table.addElement(getListHeaderElement(entry));
    for(ListItemEntry item : listItems) {
      table.addElement(getListItemElement(item));
    }
    return table;
  }
}
