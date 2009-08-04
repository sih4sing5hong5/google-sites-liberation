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

import static com.google.sites.liberation.util.EntryType.ATTACHMENT;
import static com.google.sites.liberation.util.EntryType.COMMENT;
import static com.google.sites.liberation.util.EntryType.getType;
import static com.google.sites.liberation.util.EntryType.LIST_ITEM;
import static com.google.sites.liberation.util.EntryType.LIST_PAGE;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gdata.data.ILink;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.CommentEntry;
import com.google.gdata.data.sites.ListItemEntry;
import com.google.gdata.data.sites.ListPageEntry;
import com.google.gdata.data.sites.SitesLink;
import com.google.gdata.data.spreadsheet.Column;
import com.google.gdata.data.spreadsheet.Data;
import com.google.gdata.data.spreadsheet.Field;
import com.google.gdata.data.threading.InReplyTo;
import com.google.sites.liberation.util.EntryTree;

import java.net.URL;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;

/**
 * Implements {@link EntryTreeUploader} to upload an entire EntryTree.
 *
 * @author bsimon@google.com (Benjamin Simon)
 */
final class EntryTreeUploaderImpl implements EntryTreeUploader {
  
  @Override
  public void uploadEntryTree(EntryTree entryTree, URL feedUrl, 
      EntryUploader entryUploader) {
    checkNotNull(entryTree);
    checkNotNull(feedUrl);
    checkNotNull(entryUploader);
    BaseContentEntry<?> root = entryTree.getRoot();
    Set<BaseContentEntry<?>> rootSet = Sets.newHashSet();
    rootSet.add(root);
    uploadEntries(rootSet, entryTree, feedUrl, entryUploader);
  }
  
  /**
   * Uploads the given set of entries, updates their children and then 
   * recursively uploads the children.
   */
  private void uploadEntries(Set<BaseContentEntry<?>> entries, 
      EntryTree entryTree, URL feedUrl, EntryUploader entryUploader) {    
    Set<BaseContentEntry<?>> children = Sets.newTreeSet(new UpdatedComparator());
    for(BaseContentEntry<?> entry : entries) {
      if (getType(entry) != ATTACHMENT) {
        BaseContentEntry<?> returnedEntry = entryUploader.uploadEntry(
            entry, entryTree, feedUrl);
        if (returnedEntry != null) {
          for(BaseContentEntry<?> child : entryTree.getChildren(entry)) {
            updateChild(child, returnedEntry);
            children.add(child);
          }
        }
      }
    }
    if (!children.isEmpty()) {
      uploadEntries(children, entryTree, feedUrl, entryUploader);
    }
  }
  
  /**
   * Adds parent data to the given child entry.
   */
  private void updateChild(BaseContentEntry<?> child, 
      BaseContentEntry<?> parent) {
    child.addLink(SitesLink.Rel.PARENT, ILink.Type.ATOM, parent.getId());
    if (getType(child) == COMMENT) {
      InReplyTo inReplyTo = new InReplyTo();
      inReplyTo.setHref(
          parent.getLink(ILink.Rel.ALTERNATE, "text").getHref()); 
      inReplyTo.setRef(parent.getId());
      ((CommentEntry)child).setInReplyTo(inReplyTo);
    } else if (getType(child) == LIST_ITEM) {
      if (getType(parent) != LIST_PAGE) {
        throw new IllegalStateException("List items can only be descendents of " 
            + "list pages!");
      }
      ListItemEntry listItem = (ListItemEntry) child;
      ListPageEntry listPage = (ListPageEntry) parent;
      Data data = listPage.getData();
      Map<String, String> names = Maps.newHashMap();
      for(Column column : data.getColumns()) {
        names.put(column.getIndex(), column.getName());
      }
      for(Field field : listItem.getFields()) {
        String name = names.get(field.getIndex());
        field.setName(name);
      }
    }
  }
  
  /** 
   * Compares BaseContentEntry's based on when they were last updated.
   * 
   * @author bsimon@google.com (Benjamin Simon)
   */
  private class UpdatedComparator implements Comparator<BaseContentEntry<?>> {
    
    /**
     * Orders entries by when they were updated so that the oldest entries
     * come first.
     */
    @Override
    public int compare(BaseContentEntry<?> e1, BaseContentEntry<?> e2) {
      return e1.getUpdated().compareTo(e2.getUpdated());
    }
  }
}
