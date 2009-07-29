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

package com.google.sites.liberation;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.sites.liberation.EntryType.ATTACHMENT;
import static com.google.sites.liberation.EntryType.getType;
import static com.google.sites.liberation.EntryType.isPage;

import com.google.common.collect.Sets;
import com.google.gdata.data.ILink;
import com.google.gdata.data.Link;
import com.google.gdata.data.sites.AttachmentEntry;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.BasePageEntry;
import com.google.gdata.data.sites.SitesLink;
import com.google.inject.Inject;
import com.google.sites.liberation.renderers.PageRenderer;
import com.google.sites.liberation.renderers.PageRendererFactory;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
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
  
  private static final Logger logger = Logger.getLogger(
      SiteExporterImpl.class.getCanonicalName());
  
  private final AppendableFactory appendableFactory;
  private final AttachmentDownloader attachmentDownloader;
  private final EntryStoreFactory entryStoreFactory;
  private final PageExporter pageExporter;
  private final PageRendererFactory pageRendererFactory;
  
  /**
   * Creates a new SiteExporter for the given AppendableFactory,
   * AttachmentDownloader, EntryStoreFactory, PageExporter, and 
   * PageRendererFactory.
   */
  @Inject
  SiteExporterImpl(AppendableFactory appendableFactory,
      AttachmentDownloader attachmentDownloader,
      EntryStoreFactory entryStoreFactory,
      PageExporter pageExporter,
      PageRendererFactory pageRendererFactory) {
    this.appendableFactory = checkNotNull(appendableFactory);
    this.attachmentDownloader = checkNotNull(attachmentDownloader);
    this.entryStoreFactory = checkNotNull(entryStoreFactory);
    this.pageExporter = checkNotNull(pageExporter);
    this.pageRendererFactory = checkNotNull(pageRendererFactory);
  }
  
  @Override
  public void exportSite(Iterable<BaseContentEntry<?>> entries, 
      File rootDirectory) {
    checkNotNull(entries, "entries");
    checkNotNull(rootDirectory, "rootDirectory");
    boolean someEntries = false;
    Set<String> pageIds = Sets.newHashSet();
    Set<String> attachmentIds = Sets.newHashSet();
    EntryStore entryStore = entryStoreFactory.getEntryStore();
    for(BaseContentEntry<?> entry : entries) {
      entryStore.addEntry(entry);
      someEntries = true;
      if (isPage(entry)) {
        pageIds.add(entry.getId());
      } else if (getType(entry) == ATTACHMENT) {
        attachmentIds.add(entry.getId());
      }
    }
    if (!someEntries) {
      logger.log(Level.WARNING, "No data returned. You may need to provide " +
          "user credentials.");
    }
    for(String id : pageIds) {
      exportPage(id, rootDirectory, entryStore);
    }
    for(String id : attachmentIds) {
      downloadAttachment(id, rootDirectory, entryStore);
    }
  }
  
  private void exportPage(String id, File rootDirectory, EntryStore entryStore) {
    BasePageEntry<?> entry = (BasePageEntry<?>) entryStore.getEntry(id);
    File relativePath = getPath(entry, entryStore);
    if (relativePath != null) {
      File folder = new File(rootDirectory, relativePath.getPath());
      folder.mkdirs();
      PageRenderer renderer = pageRendererFactory.getPageRenderer(entry, 
          entryStore);
      File file = new File(folder, "index.html");
      Appendable out = null;
      try {
        out = appendableFactory.getAppendable(file);
        pageExporter.exportPage(renderer, out);
      } catch (IOException e) {
        logger.log(Level.SEVERE, "Failed writing to file: " + file.getPath(), e);
      } finally {
        if (out instanceof Closeable) {
          try {
            ((Closeable) out).close();
          } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed closing file: " + file.getPath(), e);
          }
        }
      }
    }
  }
  
  private void downloadAttachment(String id, File rootDirectory, 
      EntryStore entryStore) {
    AttachmentEntry attachment = (AttachmentEntry) entryStore.getEntry(id);
    BasePageEntry<?> parent = entryStore.getParent(id);
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
          logger.log(Level.WARNING, message, e);
        }
      }
    }
  }
  
  /**
   * Returns the site-relative folder path corresponding to the given page, or 
   * {@code null} if any of the page's ancestors are missing.
   */
  private File getPath(BasePageEntry<?> entry, EntryStore entryStore) {
    Link parentLink = entry.getLink(SitesLink.Rel.PARENT, ILink.Type.ATOM);
    if (parentLink == null) {
      return new File(entry.getPageName().getValue());
    }
    BasePageEntry<?> parent = entryStore.getParent(entry.getId());
    if (parent == null) {
      return null;
    }
    return new File(getPath(parent, entryStore), entry.getPageName().getValue());
  }
}
