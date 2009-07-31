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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.BasePageEntry;

import java.util.Map;
import java.util.Set;

/**
 * An in-memory implementation of {@link EntryTree}.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
final class InMemoryEntryTree implements EntryTree {
  
  private BaseContentEntry<?> root;
  private Map<BaseContentEntry<?>, BasePageEntry<?>> parents;
  private Multimap<BaseContentEntry<?>, BaseContentEntry<?>> children;
  
  /**
   * Creates a new InMemoryEntryTree with the given root entry.
   */
  InMemoryEntryTree(BaseContentEntry<?> root) {
    this.root = checkNotNull(root);
    parents = Maps.newHashMap();
    children = HashMultimap.create();
  }
  
  @Override
  public void addEntry(BaseContentEntry<?> entry, BasePageEntry<?> parent) {
    parents.put(checkNotNull(entry), checkNotNull(parent));
    children.put(parent, entry);
  }

  @Override
  public Set<BaseContentEntry<?>> getChildren(BaseContentEntry<?> entry) {
    return Sets.newHashSet(children.get(checkNotNull(entry)));
  }

  @Override
  public BasePageEntry<?> getParent(BaseContentEntry<?> entry) {
    return parents.get(checkNotNull(entry));
  }

  @Override
  public BaseContentEntry<?> getRoot() {
    return root;
  }
  
  @Override
  public void addSubTree(EntryTree subTree, BasePageEntry<?> parent) {
    checkNotNull(subTree);
    checkNotNull(parent);
    BaseContentEntry<?> root = subTree.getRoot();
    addEntry(root, parent);
    for(BaseContentEntry<?> child : subTree.getChildren(root)) {
      addEntryFromSubTree(child, subTree);
    }
  }
  
  private void addEntryFromSubTree(BaseContentEntry<?> entry, 
      EntryTree subTree) {
    checkNotNull(entry);
    checkNotNull(subTree);
    addEntry(entry, subTree.getParent(entry));
    for(BaseContentEntry<?> child : subTree.getChildren(entry)) {
      addEntryFromSubTree(child, subTree);
    }
  }
}
