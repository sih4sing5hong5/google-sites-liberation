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
import static com.google.sites.liberation.util.EntryType.ATTACHMENT;
import static com.google.sites.liberation.util.EntryType.getType;
import static com.google.sites.liberation.util.EntryType.isPage;
import static com.google.sites.liberation.util.EntryUtils.getParentId;

import com.google.common.collect.Sets;
import com.google.gdata.client.sites.SitesService;
import com.google.gdata.data.sites.AttachmentEntry;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.BasePageEntry;
import com.google.gdata.util.common.base.Nullable;
import com.google.inject.Inject;
import com.google.sites.liberation.util.ProgressListener;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implements {@link SiteExporter} to export an entire Site 
 * to a given root folder.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
final class SiteExporterImpl implements SiteExporter {
  
  private static final Logger LOGGER = Logger.getLogger(
      SiteExporterImpl.class.getCanonicalName());
  
  private final AbsoluteLinkConverter linkConverter;
  private final AppendableFactory appendableFactory;
  private final AttachmentDownloader attachmentDownloader;
  private final EntryStoreFactory entryStoreFactory;
  private final FeedProvider feedProvider;
  private final PageExporter pageExporter;
  private final RevisionsExporter revisionsExporter;
  
  /**
   * Creates a new SiteExporter with the given dependencies.
   */
  @Inject
  SiteExporterImpl(AbsoluteLinkConverter linkConverter,
      AppendableFactory appendableFactory,
      AttachmentDownloader attachmentDownloader,
      EntryStoreFactory entryStoreFactory,
      FeedProvider feedProvider,
      PageExporter pageExporter,
      RevisionsExporter revisionsExporter) {
    this.linkConverter = checkNotNull(linkConverter);
    this.appendableFactory = checkNotNull(appendableFactory);
    this.attachmentDownloader = checkNotNull(attachmentDownloader);
    this.entryStoreFactory = checkNotNull(entryStoreFactory);
    this.feedProvider = checkNotNull(feedProvider);
    this.pageExporter = checkNotNull(pageExporter);
    this.revisionsExporter = checkNotNull(revisionsExporter);   
  }
  
  @Override
  public void exportSite(String host, @Nullable String domain, String webspace, 
      boolean exportRevisions, SitesService sitesService, File rootDirectory, 
      ProgressListener progressListener) {
    checkNotNull(host, "host");
    checkNotNull(webspace, "webspace");
    checkNotNull(sitesService, "sitesService");
    checkNotNull(rootDirectory, "rootDirectory");
    checkNotNull(progressListener, "progressListener");
    Set<BasePageEntry<?>> pages = Sets.newHashSet();
    Set<AttachmentEntry> attachments = Sets.newHashSet();
    EntryStore entryStore = entryStoreFactory.getEntryStore();
    progressListener.setStatus("Retrieving site data (this may take a few minutes).");
    URL feedUrl;
    URL siteUrl;
    try {
      if (domain == null) {
        feedUrl = new URL("http://" + host + "/feeds/content/site/" + webspace);
        siteUrl = new URL("http://" + host + "/site/" + webspace);
      } else {
        feedUrl = new URL("http://" + host + "/feeds/content/" + domain + "/" 
            + webspace);
        siteUrl = new URL("http://" + host + "/a/" + domain + "/" + webspace);
      }
    } catch (MalformedURLException e) {
      LOGGER.log(Level.WARNING, "Invalid host, domain, or webspace!");
      throw new RuntimeException(e);
    }
    Iterable<BaseContentEntry<?>> entries = 
        feedProvider.getEntries(feedUrl, sitesService);
    for (BaseContentEntry<?> entry : entries) {
      if (entry != null) {
        entryStore.addEntry(entry);
        if (isPage(entry)) {
          pages.add((BasePageEntry<?>) entry);
        } else if (getType(entry) == ATTACHMENT) {
          attachments.add((AttachmentEntry) entry);
        }
      }
    }
    int totalEntries = pages.size() + attachments.size();
    if (totalEntries > 0) {  
      int currentEntries = 0;
      for (BasePageEntry<?> page : pages) {
        progressListener.setStatus("Exporting page: " 
            + page.getTitle().getPlainText() + '.');
        linkConverter.convertLinks(page, entryStore, siteUrl);
        File relativePath = getPath(page, entryStore);
        if (relativePath != null) {
          File directory = new File(rootDirectory, relativePath.getPath());
          directory.mkdirs();
          exportPage(page, directory, entryStore, exportRevisions);
          if (exportRevisions) {
            revisionsExporter.exportRevisions(page, directory, sitesService);
          }
        }
        progressListener.setProgress(((double) ++currentEntries) / totalEntries);
      }
      for (AttachmentEntry attachment : attachments) {
        progressListener.setStatus("Downloading attachment: " 
            + attachment.getTitle().getPlainText() + '.');
        downloadAttachment(attachment, rootDirectory, entryStore, sitesService);
        progressListener.setProgress(((double) ++currentEntries) / totalEntries);
      }
      progressListener.setStatus("Export complete.");
    } else {
      progressListener.setStatus("No data returned. You may need to provide " +
          "user credentials.");
    }
  }
  
  private void exportPage(BasePageEntry<?> page, File directory, 
      EntryStore entryStore, boolean revisionsExported) {
    File file = new File(directory, "index.html");
    Appendable out = null;
    try {
      out = appendableFactory.getAppendable(file);
      pageExporter.exportPage(page, entryStore, out, revisionsExported);
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Failed writing to file: " + file.getPath(), e);
    } finally {
      if (out instanceof Closeable) {
        try {
          ((Closeable) out).close();
        } catch (IOException e) {
          LOGGER.log(Level.SEVERE, "Failed closing file: " + file.getPath(), e);
        }
      }
    }
  }
  
  private void downloadAttachment(AttachmentEntry attachment, 
      File rootDirectory, EntryStore entryStore, SitesService sitesService) {
    BasePageEntry<?> parent = entryStore.getParent(attachment.getId());
    if (parent != null) {
      File relativePath = getPath(parent, entryStore);
      if (relativePath != null) {
        File folder = new File(rootDirectory, relativePath.getPath());
        folder.mkdirs();
        File file = new File(folder, attachment.getTitle().getPlainText());
        attachmentDownloader.download(attachment, file, sitesService);
      }
    }
  }
  
  /**
   * Returns the site-relative folder path corresponding to the given page, or 
   * {@code null} if any of the page's ancestors are missing.
   */
  private File getPath(BasePageEntry<?> entry, EntryStore entryStore) {
    String parentId = getParentId(entry);
    if (parentId == null) {
      return new File(entry.getPageName().getValue());
    }
    BasePageEntry<?> parent = (BasePageEntry<?>) entryStore.getEntry(parentId);
    if (parent == null) {
      return null;
    }
    return new File(getPath(parent, entryStore), entry.getPageName().getValue());
  }
}
