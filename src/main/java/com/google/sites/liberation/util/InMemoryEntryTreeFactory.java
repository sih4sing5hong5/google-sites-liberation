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

/**
 * Implementation of {@link EntryTreeFactory} that returns instances of 
 * {@link InMemoryEntryTree}. 
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
public class InMemoryEntryTreeFactory implements EntryTreeFactory {

  @Override
  public EntryTree getEntryTree(BaseContentEntry<?> root) {
    return new InMemoryEntryTree(root);
  }
}
