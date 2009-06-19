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

import com.google.gdata.data.sites.BaseEditableContentEntry;
import com.google.common.collect.ImmutableSet;

/**
 * This interface defines a data structure that can be used to store the
 * entries of an entire content feed so that the entries can later be fetched 
 * on demand.
 * 
 * @author bsimon@google.com (Ben Simon)
 *
 */
public interface EntryStore {
  
  /**
   * Returns the id's of the entries of the given {@code type} whose parent 
   * has the given {@code id}
   */
  public ImmutableSet<String> getChildrenIds(String parentId, EntryType type);
  
  /**
   * Returns the entry with the given {@code id}. Returns {@code null} if
   * there is no entry with the given {@code id}. 
   */
  public BaseEditableContentEntry<?> getEntry(String id);
  
  /**
   * Returns the id's of all entries of the given {@code type}
   */
  public ImmutableSet<String> getEntryIds(EntryType type);
  
  /**
   * Stores the given content entry for later retrieval
   */
  public void addEntry(BaseEditableContentEntry<?> e);
  
}