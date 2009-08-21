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

import com.google.gdata.data.sites.BasePageEntry;
import com.google.inject.ImplementedBy;

import java.io.IOException;

/**
 * Export a single page in a Site to a given Appendable.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
@ImplementedBy(PageExporterImpl.class)
interface PageExporter {

  /** 
   * Exports the given page.
   * 
   * @param entry the entry for the page being exported
   * @param entryStore the EntryStore containing this entry and its related entries
   * @param out Appendable to export to
   * @param revisionsExported whether or not revisions were exported 
   *        (i.e. whether or not to link to a the version history file) 
   */
  void exportPage(BasePageEntry<?> entry, EntryStore entryStore, Appendable out,
      boolean revisionsExported) throws IOException;
}
