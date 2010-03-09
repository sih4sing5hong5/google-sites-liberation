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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.sites.liberation.util.EntryType.isPage;

import com.google.gdata.client.sites.SitesService;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.BasePageEntry;
import com.google.gdata.data.sites.PageName;
import com.google.inject.Inject;
import com.google.sites.liberation.parsers.PageParser;
import com.google.sites.liberation.util.EntryUtils;

import java.io.File;
import java.net.URL;
import java.util.List;

/**
 * Imports the revisions of page and uploads them to a feed.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
final class RevisionsImporterImpl implements RevisionsImporter {
  
  private final EntryUpdater entryUpdater;
  private final EntryUploader entryUploader;
  private final PageParser pageParser;
  private final RelativeLinkConverter linkConverter;
  
  /**
   * Creates a new RevisionsImporterImpl with the given dependencies.
   */
  @Inject
  RevisionsImporterImpl(EntryUpdater entryUpdater, EntryUploader entryUploader, 
      PageParser pageParser, RelativeLinkConverter linkConverter) {
    this.entryUpdater = checkNotNull(entryUpdater);
    this.entryUploader = checkNotNull(entryUploader);
    this.pageParser = checkNotNull(pageParser);
    this.linkConverter = checkNotNull(linkConverter);
  }
  
  @Override
  public BasePageEntry<?> importRevisions(File directory, 
      List<BasePageEntry<?>> ancestors, URL feedUrl, URL siteUrl, 
      SitesService sitesService) {
    File revisionsDirectory = new File(directory, "_revisions");
    int num = 1;
    BasePageEntry<?> revision = null;
    while (new File(revisionsDirectory, num + ".html").isFile()) {
      BasePageEntry<?> page = getPageEntry(new File(revisionsDirectory, 
          num + ".html"));
      if (page != null) {
        page.setPageName(new PageName(directory.getName()));
        if (!ancestors.isEmpty()) {
          EntryUtils.setParent(page, ancestors.get(ancestors.size() - 1));
        }
        linkConverter.convertLinks(page, ancestors, siteUrl, true);
        if (revision == null) {
          revision = (BasePageEntry<?>) entryUploader
              .uploadEntry(page, ancestors, feedUrl, sitesService);
        } else {
          revision = (BasePageEntry<?>) entryUpdater
              .updateEntry(revision, page, sitesService);
        }
      }
      num++;
    }
    return revision;
  }
    
  private BasePageEntry<?> getPageEntry(File file) {
    List<BaseContentEntry<?>> entries = pageParser.parsePage(file);
    for (BaseContentEntry<?> entry : entries) {
      if (isPage(entry)) {
        return (BasePageEntry<?>) entry;
      }
    }
    return null;
  }
}
