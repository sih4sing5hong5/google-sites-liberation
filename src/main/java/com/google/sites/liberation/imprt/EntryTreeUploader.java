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

package com.google.sites.liberation.imprt;

import com.google.inject.ImplementedBy;
import com.google.sites.liberation.util.EntryTree;

import java.net.URL;

/**
 * Uploads entries to a given feed URL.
 * 
 * @author bsimon@google.com (Benjamin)
 */
@ImplementedBy(EntryTreeUploaderImpl.class)
public interface EntryTreeUploader {

  /**
   * Uploads all of the entries in the given EntryTree to the given feed URL,
   * using the given EntryUploader and retaining the tree structure.
   */
  void uploadEntryTree(EntryTree entryTree, URL feedUrl, 
      EntryUploader entryUploader);
}
