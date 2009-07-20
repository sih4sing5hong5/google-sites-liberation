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

import com.google.gdata.data.ILink;
import com.google.gdata.data.Link;
import com.google.gdata.data.sites.BaseContentEntry;
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
public final class InMemoryEntryStore implements EntryStore {

  private final Map<String, BaseContentEntry<?>> entries;
  private final Set<BaseContentEntry<?>> topLevelEntries;
  private final Multimap<String, BaseContentEntry<?>> children;
  private final Map<String, String> names;
  
  /**
   * Creates a new InMemoryEntryStore which provides constant time storage 
   * and retrieval of entries by id or parent id.
   */
  public InMemoryEntryStore() {
    entries = Maps.newHashMap();
    topLevelEntries = Sets.newHashSet();
    children = HashMultimap.create();
    names = Maps.newHashMap();
  }

  @Override
  public void addEntry(BaseContentEntry<?> entry) {
    Preconditions.checkNotNull(entry);
    String id = entry.getId();
    Preconditions.checkArgument(id != null && entries.get(id) == null,
        "All entries must have a unique non-null id!");
    entries.put(id, entry);
    Link parentLink = entry.getLink(SitesLink.Rel.PARENT, ILink.Type.ATOM);
    if (EntryType.isPage(entry)) {
      String niceTitle = getNiceTitle(entry);
      String name = niceTitle;
      Collection<BaseContentEntry<?>> siblings;
      if (parentLink == null) {
        siblings = topLevelEntries;
      } else {
        siblings = children.get(parentLink.getHref());
      }
      Set<String> siblingNames = Sets.newHashSet();
      for(BaseContentEntry<?> sibling : siblings) {
        siblingNames.add(names.get(sibling.getId()));
      }
      int num = 2;
      while(siblingNames.contains(name)) {
        name = niceTitle;
        if (name.charAt(name.length() - 1) != '-') {
          name += '-';
        }
        name += num;
        num++;
      }
      names.put(id, name);
    }
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
  public String getName(String id) {
    Preconditions.checkNotNull(id);
    return names.get(id);
  }
  
  /**
   * Returns the given entry's title with all sequences of non-word characters
   * (^[a-zA-z0-9_]) replaced by a single hyphen.
   */
  private static String getNiceTitle(BaseContentEntry<?> entry) {
    String title = entry.getTitle().getPlainText();
    String niceTitle = title.replaceAll("[\\W]+", "-");
    if(niceTitle.length() == 0) {
      niceTitle = "-";
    }
    return niceTitle;
  }
}
