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
import com.google.common.collect.Multimap;
import com.google.common.collect.Maps;
import com.google.gdata.data.sites.BaseEditableContentEntry;
import com.google.gdata.data.sites.SitesLink;
import com.google.gdata.data.Link;
import java.util.Map;

final class InMemoryEntryStore implements EntryStore {
  
  // Stores the entries by id
  private final Map<String, BaseEditableContentEntry<?>> entries;
  // Stores the ids of the entries by EntryType
  private final Multimap<EntryType, String> ids;
  // Stores the children of entries by parent id and then EntryType
  private final Map<String, Multimap<EntryType, String>> children;
  
  /**
   * Constructs a new, empty InMemoryEntryStore
   */
  InMemoryEntryStore() {
    entries = Maps.newHashMap();
    ids = HashMultimap.create();
    children = Maps.newHashMap();
  }
  
  /**
   * Stores the given content entry for later retrieval
   */
  public void addEntry(BaseEditableContentEntry<?> e) {
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
  
  /**
   * Returns the id's of the entries of the given {@code type} whose parent 
   * has the given {@code id}
   */
  public ImmutableSet<String> getChildrenIds(String parentId, EntryType type) {
    if (children.get(parentId)==null || children.get(parentId).get(type)==null)
      return ImmutableSet.of();
    return ImmutableSet.copyOf(children.get(parentId).get(type));
  }

  /**
   * Returns the entry with the given {@code id}. Returns {@code null} if
   * there is no entry with the given {@code id}. 
   */
  public BaseEditableContentEntry<?> getEntry(String id) {
    return entries.get(id);
  }
  
  /**
   * Returns the id's of all entries of the given {@code type}
   */
  public ImmutableSet<String> getEntryIds(EntryType type) {
    if (ids.get(type) == null)
      return ImmutableSet.of();
    return ImmutableSet.copyOf(ids.get(type));
  }
  
  /**
   * Returns a string representation of this object
   */
  @Override
  public String toString() {
    return "{InMemoryEntryStore " + super.toString() + "}";
  }
  
  /**
   * Indicates whether some other object is "equal to" this one
   */
  @Override
  public boolean equals(Object other) {
    if (other instanceof InMemoryEntryStore) {
      InMemoryEntryStore store = (InMemoryEntryStore)other;
      return entries.equals(store.entries);
    }
    return false;
  }
  
  /**
   * Returns a hash code value for this object
   */
  @Override
  public int hashCode() {
    return entries.hashCode();
  }

}
