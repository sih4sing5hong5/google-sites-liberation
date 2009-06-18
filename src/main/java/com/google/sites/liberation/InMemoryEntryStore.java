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

package com.google.sites.liberation;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.HashMultimap;
import com.google.gdata.data.sites.BaseEditableContentEntry;
import com.google.gdata.data.sites.SitesLink;
import com.google.gdata.data.Link;
import java.util.HashMap;

public class InMemoryEntryStore implements EntryStore {
  
  // Stores the entries by id
  private HashMap<String, BaseEditableContentEntry<?>> entries;
  // Stores the ids of the entries by EntryType
  private HashMultimap<EntryType, String> ids;
  // Stores the children of entries by parent id and then EntryType
  private HashMap<String, HashMultimap<EntryType, String>> children;
  
  /**
   * Constructs a new, empty InMemoryEntryStore
   */
  public InMemoryEntryStore() {
    entries = new HashMap<String, BaseEditableContentEntry<?>>();
    ids = HashMultimap.create();
    children = new HashMap<String, HashMultimap<EntryType, String>>();
  }
  
  @Override
  public ImmutableSet<String> getChildrenIds(String parentId, EntryType type) {
    if (children.get(parentId)==null || children.get(parentId).get(type)==null)
      return ImmutableSet.of();
    return ImmutableSet.copyOf(children.get(parentId).get(type));
  }

  @Override
  public BaseEditableContentEntry<?> getEntry(String id) {
    return entries.get(id);
  }

  @Override
  public ImmutableSet<String> getEntryIds(EntryType type) {
    if (ids.get(type) == null)
      return ImmutableSet.of();
    return ImmutableSet.copyOf(ids.get(type));
  }

  @Override
  public void storeEntry(BaseEditableContentEntry<?> e) {
    String id = e.getId();
    entries.put(id, e);
    EntryType type = EntryType.getType(e);
    ids.put(type, id);
    Link link = e.getLink(SitesLink.Rel.PARENT, SitesLink.Type.APPLICATION_XHTML_XML);
    if (link != null) {
      String parentId = link.getHref();
      if (children.get(parentId) == null) {
        HashMultimap<EntryType, String> map = HashMultimap.create();
        children.put(parentId, map);
      }
      children.get(parentId).put(type, id);
    }
  }

}
