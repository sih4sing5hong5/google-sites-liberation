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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.sites.liberation.util.EntryType.isPage;

import com.google.common.collect.Lists;
import com.google.gdata.client.sites.SitesService;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.BasePageEntry;
import com.google.inject.Inject;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Exports the history and all of the revisions of a page. 
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
final class RevisionsExporterImpl implements RevisionsExporter {
  
  private static Logger LOGGER = Logger.getLogger(
      RevisionsExporterImpl.class.getCanonicalName());
  
  private final AbsoluteLinkConverter linkConverter;
  private final AppendableFactory appendableFactory;
  private final FeedProvider feedProvider;
  private final HistoryExporter historyExporter;
  private final RevisionExporter revisionExporter;
  
  @Inject
  RevisionsExporterImpl(AbsoluteLinkConverter linkConverter,
      AppendableFactory appendableFactory,
      FeedProvider feedProvider,
      HistoryExporter historyExporter,
      RevisionExporter revisionExporter) {
    this.linkConverter = checkNotNull(linkConverter);
    this.appendableFactory = checkNotNull(appendableFactory);
    this.feedProvider = checkNotNull(feedProvider);
    this.historyExporter = checkNotNull(historyExporter);
    this.revisionExporter = checkNotNull(revisionExporter);
  }
  
  @Override
  public void exportRevisions(BasePageEntry<?> page, EntryStore entryStore, 
      File directory, SitesService sitesService, URL siteUrl) {
    checkNotNull(page, "page");
    checkNotNull(directory, "directory");
    checkNotNull(sitesService, "sitesService");
    File revisionsDirectory = new File(directory, "_revisions");
    revisionsDirectory.mkdir();
    URL feedUrl;
    try {
      feedUrl = new URL(page.getId().replace("content", "revision"));
    } catch (MalformedURLException e) {
      LOGGER.log(Level.WARNING, "Invalid revisions URL!", e);
      return;
    }
    List<BaseContentEntry<?>> revisions = Lists.newLinkedList();
    for (BaseContentEntry<?> entry : 
        feedProvider.getEntries(feedUrl, sitesService)) {
      entry.setId(page.getId());
      revisions.add(entry);
    }
    for (BaseContentEntry<?> revision : revisions) {
      if (revision.getRevision().getValue() != page.getRevision().getValue()) {
        if (isPage(revision)) {
          linkConverter.convertLinks((BasePageEntry<?>) revision, entryStore, 
              siteUrl, true);
          exportRevision((BasePageEntry<?>) revision, revisionsDirectory);
        }
      }
    }    
    File file = new File(directory, "history.html");
    Appendable out = null;
    try {
      out = appendableFactory.getAppendable(file);
      historyExporter.exportHistory(revisions, out);
    } catch(IOException e) {
      LOGGER.log(Level.WARNING, "Failed writing to file: " + file, e);
    } finally {
      if (out instanceof Closeable) {
        try {
          ((Closeable) out).close();
        } catch (IOException e) {
          LOGGER.log(Level.SEVERE, "Failed closing file: " + file, e);
        }
      }
    }
  }
  
  private void exportRevision(BasePageEntry<?> revision, 
      File revisionsDirectory) {
    int number = revision.getRevision().getValue();
    File file = new File(revisionsDirectory, number + ".html");
    Appendable out = null;
    try {
      out = appendableFactory.getAppendable(file);
      revisionExporter.exportRevision(revision, out);
    } catch(IOException e) {
      LOGGER.log(Level.WARNING, "Failed writing to file: " + file, e);
    } finally {
      if (out instanceof Closeable) {
        try {
          ((Closeable) out).close();
        } catch (IOException e) {
          LOGGER.log(Level.SEVERE, "Failed closing file: " + file, e);
        }
      }
    }
  }
}
