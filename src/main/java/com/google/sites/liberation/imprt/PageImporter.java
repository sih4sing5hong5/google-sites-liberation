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

import java.io.File;

/**
 * Parses a single file representing a page in a site.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
@ImplementedBy(PageImporterImpl.class)
public interface PageImporter {

  /**
   * Parses the given file, returning an EntryTree containing a BasePageEntry
   * as the root, and its comments and attachments as children.
   */
  EntryTree importPage(File file);
}
