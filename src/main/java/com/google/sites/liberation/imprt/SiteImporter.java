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

import java.io.File;
import java.net.URL;

/**
 * Imports an entire site from a given directory.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
@ImplementedBy(SiteImporterImpl.class)
public interface SiteImporter {

  /**
   * Imports the site with the given root directory, to the given feed URL, 
   * using the given EntryUploader.
   */
  public void importSite(File rootDirectory, URL feedUrl, 
      EntryUploader entryUploader);
}
