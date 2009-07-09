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

import com.google.gdata.data.sites.BaseContentEntry;

import java.util.Collection;

/**
 * This interface defines a data structure that can be used to store the
 * entries of a content feed so that the entries can be later fetched 
 * by id or parent id.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
public interface EntryStore {

  /**
   * Stores the given content entry.
   */
  public void addEntry(BaseContentEntry<?> entry);
  
  /**
   * Retrieves the entry with the given {@code id} or {@code null} if there is
   * no such entry.
   */
  public BaseContentEntry<?> getEntry(String id);
  
  /**
   * Returns a collection containing all entries with parent specified by the 
   * given {@code id}.
   */
  public Collection<BaseContentEntry<?>> getChildren(String id);
  
  /**
   * Returns a name for the entry with the given id, unique amongst the entry's
   * siblings or {@code null} if there is no such entry.
   */
  public String getName(String id);
  
}
