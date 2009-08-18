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

import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.BasePageEntry;

import java.util.Collection;

/**
 * Defines a data structure that can be used to store the
 * entries of a content feed so that the entries can be later fetched 
 * by id or parent id.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
interface EntryStore {

  /**
   * Stores the given content entry.
   */
  void addEntry(BaseContentEntry<?> entry);
  
  /**
   * Retrieves the entry with the given {@code id} or {@code null} if there is
   * no such entry.
   */
  BaseContentEntry<?> getEntry(String id);
  
  /**
   * Retrieves the parent of the entry with the given {@code id} or {@code null}
   * if there is no such entry.
   */
  BasePageEntry<?> getParent(String id);
  
  /**
   * Returns a collection containing all entries with parent specified by the 
   * given {@code id}.
   */
  Collection<BaseContentEntry<?>> getChildren(String id);
  
  /**
   * Returns a collection containing all of the top level entries in this
   * store.
   */
  Collection<BasePageEntry<?>> getTopLevelEntries();
}
