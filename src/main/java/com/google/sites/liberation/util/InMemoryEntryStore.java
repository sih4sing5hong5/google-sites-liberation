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

package com.google.sites.liberation.util;

import com.google.gdata.data.ILink;
import com.google.gdata.data.Link;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.BasePageEntry;
import com.google.gdata.data.sites.SitesLink;
import com.google.common.base.Preconditions;
import com.google.common.collect.Multimap;
import com.google.common.collect.Maps;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * An in-memory implementation of {@link EntryStore}.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
final class InMemoryEntryStore implements EntryStore {

  private final Map<String, BaseContentEntry<?>> entries;
  private final Set<BaseContentEntry<?>> topLevelEntries;
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
    Preconditions.checkNotNull(entry);
    String id = entry.getId();
    Preconditions.checkArgument(id != null && entries.get(id) == null,
        "All entries must have a unique non-null id!");
    entries.put(id, entry);
    Link parentLink = entry.getLink(SitesLink.Rel.PARENT, ILink.Type.ATOM);
    if (parentLink == null) {
      topLevelEntries.add(entry);
    } else {
      children.put(parentLink.getHref(), entry);
    }
  }
  
  @Override
  public Collection<BaseContentEntry<?>> getChildren(String id) {
    Preconditions.checkNotNull(id);
    return children.get(id);
  }

  @Override
  public BaseContentEntry<?> getEntry(String id) {
    Preconditions.checkNotNull(id);
    return entries.get(id);
  }
  
  @Override
  public BasePageEntry<?> getParent(String id) {
    Preconditions.checkNotNull(id);
    BaseContentEntry<?> child = getEntry(id);
    Link parentLink = child.getLink(SitesLink.Rel.PARENT, ILink.Type.ATOM);
    if (parentLink == null || parentLink.getHref() == null) {
      return null;
    }
    return (BasePageEntry<?>) getEntry(parentLink.getHref());
  }
}
