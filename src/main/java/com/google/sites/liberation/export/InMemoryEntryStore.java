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
import static com.google.sites.liberation.util.EntryType.isPage;

import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.BasePageEntry;
import com.google.sites.liberation.util.EntryUtils;
import com.google.common.collect.Multimap;
import com.google.common.collect.Maps;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An in-memory implementation of {@link EntryStore}.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
final class InMemoryEntryStore implements EntryStore {

  private static final Logger LOGGER = Logger.getLogger(
      InMemoryEntryStore.class.getCanonicalName());
  
  private final Map<String, BaseContentEntry<?>> entries;
  private final Set<BasePageEntry<?>> topLevelEntries;
  private final Multimap<String, BaseContentEntry<?>> children;
  
  /**
   * Creates a new InMemoryEntryStore which provides constant time storage 
   * and retrieval of entries by id or parent id.
   */
  InMemoryEntryStore() {
    entries = Maps.newHashMap();
    topLevelEntries = Sets.newHashSet();
    children = HashMultimap.create();
  }

  @Override
  public void addEntry(BaseContentEntry<?> entry) {
    checkNotNull(entry);
    String id = entry.getId();
    if (id != null && entries.get(id) == null) {
      entries.put(id, entry);
      String parentId = EntryUtils.getParentId(entry);
      if (parentId == null) {
        if (isPage(entry)) {
          topLevelEntries.add((BasePageEntry<?>) entry);
        } else {
          LOGGER.log(Level.WARNING, "All non-page entries must have a parent!");
        }
      } else {
        children.put(parentId, entry);
      }
    } else {
      LOGGER.log(Level.WARNING, "All entries should have a unique non-null id!");
    }
  }
  
  @Override
  public Collection<BaseContentEntry<?>> getChildren(String id) {
    checkNotNull(id);
    return children.get(id);
  }

  @Override
  public BaseContentEntry<?> getEntry(String id) {
    checkNotNull(id);
    return entries.get(id);
  }
  
  @Override
  public BasePageEntry<?> getParent(String id) {
    checkNotNull(id);
    BaseContentEntry<?> child = getEntry(id);
    String parentId = EntryUtils.getParentId(child);
    if (parentId == null) {
      return null;
    }
    return (BasePageEntry<?>) getEntry(parentId);
  }
  
  @Override
  public Collection<BasePageEntry<?>> getTopLevelEntries() {
    return topLevelEntries;
  }
}
