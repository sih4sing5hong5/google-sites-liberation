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

import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.BasePageEntry;

import java.util.Set;

/**
 * Stores entries in a tree structure independent of their id's.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
public interface EntryTree {
  
  /**
   * Returns the root entry of this tree.
   */
  BaseContentEntry<?> getRoot();
  
  /**
   * Returns the parent of the given entry, or null if the given entry is the 
   * root of this tree.
   * 
   * @throws IllegalArgumentException this tree does not contain the given entry
   */
  BasePageEntry<?> getParent(BaseContentEntry<?> entry);
  
  /**
   * Returns a set containing the children of the given entry.
   * 
   * @throws IllegalArgumentException this tree does not contain the given entry
   */
  Set<BaseContentEntry<?>> getChildren(BaseContentEntry<?> entry);
  
  /**
   * Returns whether or not this tree contains the given entry.
   */
  boolean contains(BaseContentEntry<?> entry);
  
  /**
   * Adds the given entry to the tree as a child of the given parent. The parent
   * entry must already be present in the tree.
   * 
   * @throws IllegalArgumentException this tree does not contain the parent entry
   */
  void addEntry(BaseContentEntry<?> entry, BasePageEntry<?> parent);
  
  /**
   * Adds the given EntryTree as a sub-tree to this tree whose root is a child
   * of the given parent entry. The parent entry must already be present in the
   * tree.
   * 
   * @throws IllegalArgumentException this tree does not contain the parent entry
   */
  void addSubTree(EntryTree subTree, BasePageEntry<?> parent);
}
