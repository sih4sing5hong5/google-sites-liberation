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

import com.google.gdata.client.sites.SitesService;
import com.google.gdata.data.sites.BasePageEntry;
import com.google.inject.ImplementedBy;

import java.io.File;
import java.net.URL;
import java.util.List;

/**
 * Parses a page and (possibly) its revisions and uploads them a feed.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
@ImplementedBy(PageImporterImpl.class)
public interface PageImporter {

  /**
   * Parses a page and uploads it as well as its comments, attachments,
   * list items, and possibly revisions to a feed.
   * 
   * @param directory directory of the page
   * @param importRevisions whether on not revisions should also be imported
   * @param ancestors the pages ancestors, its parent as the last entry, etc.
   * @param feedUrl the feedUrl to upload the entries to
   * @param siteUrl the siteUrl the page will exist at
   * @param sitesService SitesService to use for uploading
   * @return the BasePageEntry returned by the server
   */
  BasePageEntry<?> importPage(File directory, boolean importRevisions,
      List<BasePageEntry<?>> ancestors, URL feedUrl, URL siteUrl, 
      SitesService sitesService);
}
