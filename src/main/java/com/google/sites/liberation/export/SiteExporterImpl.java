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

import com.google.common.collect.Sets;
import com.google.gdata.data.sites.AttachmentEntry;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.BasePageEntry;
import com.google.inject.Inject;
import com.google.sites.liberation.util.EntryStore;
import com.google.sites.liberation.util.EntryStoreFactory;
import com.google.sites.liberation.util.EntryUtils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
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
  
  private final AppendableFactory appendableFactory;
  private final AttachmentDownloader attachmentDownloader;
  private final EntryStoreFactory entryStoreFactory;
  private final PageExporter pageExporter;
  
  /**
   * Creates a new SiteExporter for the given AppendableFactory,
   * AttachmentDownloader, EntryStoreFactory, PageExporter, and 
   * PageRendererFactory.
   */
  @Inject
  SiteExporterImpl(AppendableFactory appendableFactory,
      AttachmentDownloader attachmentDownloader,
      EntryStoreFactory entryStoreFactory,
      PageExporter pageExporter) {
    this.appendableFactory = checkNotNull(appendableFactory);
    this.attachmentDownloader = checkNotNull(attachmentDownloader);
    this.entryStoreFactory = checkNotNull(entryStoreFactory);
    this.pageExporter = checkNotNull(pageExporter);
  }
  
  @Override
  public void exportSite(Iterable<BaseContentEntry<?>> entries, 
      File rootDirectory, URL siteUrl) {
    checkNotNull(entries, "entries");
    checkNotNull(rootDirectory, "rootDirectory");
    checkNotNull(siteUrl, "siteUrl");
    boolean someEntries = false;
    Set<BasePageEntry<?>> pages = Sets.newHashSet();
    Set<AttachmentEntry> attachments = Sets.newHashSet();
    EntryStore entryStore = entryStoreFactory.getEntryStore();
    for (BaseContentEntry<?> entry : entries) {
      someEntries = true;
      entryStore.addEntry(entry);
      if (isPage(entry)) {
        pages.add((BasePageEntry<?>) entry);
      } else if (getType(entry) == ATTACHMENT) {
        attachments.add((AttachmentEntry) entry);
      }
    }
    if (!someEntries) {
      LOGGER.log(Level.WARNING, "No data returned. You may need to provide " +
          "user credentials.");
    }
    for (BasePageEntry<?> page : pages) {
      fixLinks(page, entryStore, siteUrl);
      exportPage(page, rootDirectory, entryStore);
    }
    for (AttachmentEntry attachment : attachments) {
      downloadAttachment(attachment, rootDirectory, entryStore);
    }
  }
  
  /**
   * Changes all of the absolute links to other pages in this site to relative 
   * links in the given entry's content.
   */
  private void fixLinks(BasePageEntry<?> entry, EntryStore entryStore, 
      URL siteUrl) {
    fixLinks(entry, entryStore, siteUrl, "href=\"", "\"");
    fixLinks(entry, entryStore, siteUrl, "href='", "'");
  }
  
  private void fixLinks(BasePageEntry<?> entry, EntryStore entryStore, 
      URL siteUrl, String prefix, String suffix) {
    String content = EntryUtils.getContent(entry);
    String url = siteUrl.toExternalForm();
    String siteRoot = getSiteRoot(entry, entryStore);
    int index = content.indexOf(prefix + url);
    while (index != -1) {
      int startIndex = index + prefix.length();
      int endIndex = content.indexOf(suffix, startIndex + 1);
      String beforeLink = content.substring(0, startIndex);
      String link = content.substring(startIndex + url.length() + 1, endIndex);
      String afterLink = content.substring(endIndex);
      content = beforeLink + siteRoot + link + "/index.html" + afterLink;
      index = content.indexOf(prefix + url);
    }
    EntryUtils.setContent(entry, content);
  }
  
  private String getSiteRoot(BasePageEntry<?> entry, EntryStore entryStore) {
    BasePageEntry<?> parent = entryStore.getParent(entry.getId());
    if (parent == null) {
      return "../";
    }
    return getSiteRoot(parent, entryStore) + "../";
  }
  
  private void exportPage(BasePageEntry<?> entry, 
      File rootDirectory, EntryStore entryStore) {
    File relativePath = getPath(entry, entryStore);
    if (relativePath != null) {
      File folder = new File(rootDirectory, relativePath.getPath());
      folder.mkdirs();
      File file = new File(folder, "index.html");
      Appendable out = null;
      try {
        out = appendableFactory.getAppendable(file);
        pageExporter.exportPage(entry, entryStore, out);
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
  }
  
  private void downloadAttachment(AttachmentEntry attachment, 
      File rootDirectory, EntryStore entryStore) {
    BasePageEntry<?> parent = entryStore.getParent(attachment.getId());
    if (parent != null) {
      File relativePath = getPath(parent, entryStore);
      if (relativePath != null) {
        File folder = new File(rootDirectory, relativePath.getPath());
        folder.mkdirs();
        File file = new File(folder, attachment.getTitle().getPlainText());
        try {
          attachmentDownloader.download(attachment, file);
        } catch (IOException e) {
          String message = "Error reading from " + attachment.getEnclosureLink()
              .getHref() + "and/or writing to " + file.getPath();
          LOGGER.log(Level.WARNING, message, e);
        }
      }
    }
  }
  
  /**
   * Returns the site-relative folder path corresponding to the given page, or 
   * {@code null} if any of the page's ancestors are missing.
   */
  private File getPath(BasePageEntry<?> entry, EntryStore entryStore) {
    String parentId = EntryUtils.getParentId(entry);
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
