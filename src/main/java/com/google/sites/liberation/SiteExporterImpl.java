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
      PageExporterImpl.class.getCanonicalName());
  
  private final AppendableFactory appendableFactory;
  private final AttachmentDownloader attachmentDownloader;
  private final EntryStore entryStore;
  private final PageExporter pageExporter;
  private final PageRendererFactory pageRendererFactory;
  
  /**
   * Creates a new SiteExporter for the given AppendableFactory,
   * AttachmentDownloader, EntryStore, PageExporter, and PageRendererFactory.
   */
  @Inject
  SiteExporterImpl(AppendableFactory appendableFactory,
      AttachmentDownloader attachmentDownloader,
      EntryStore entryStore,
      PageExporter pageExporter,
      PageRendererFactory pageRendererFactory) {
    this.appendableFactory = checkNotNull(appendableFactory);
    this.attachmentDownloader = checkNotNull(attachmentDownloader);
    this.entryStore = checkNotNull(entryStore);
    this.pageExporter = checkNotNull(pageExporter);
    this.pageRendererFactory = checkNotNull(pageRendererFactory);
  }
  
  @Override
  public void exportSite(Iterable<BaseContentEntry<?>> entries, String folder) {
    checkNotNull(entries, "entries");
    checkNotNull(folder, "folder");
    Set<String> pageIds = Sets.newHashSet();
    Set<String> attachmentIds = Sets.newHashSet();
    for(BaseContentEntry<?> entry : entries) {
      entryStore.addEntry(entry);
      if (isPage(entry)) {
        pageIds.add(entry.getId());
      } else if(getType(entry) == ATTACHMENT) {
        attachmentIds.add(entry.getId());
      }
    }
    for(String id : pageIds) {
      exportPage(id, folder);
    }
    for(String id : attachmentIds) {
      downloadAttachment(id, folder);
    }
  }
  
  private void exportPage(String id, String rootFolder) {
    BaseContentEntry<?> entry = entryStore.getEntry(id);
    String folderPath = getPath(entry);
    if (folderPath != null) {
      folderPath = rootFolder + folderPath;
      new File(folderPath).mkdirs();
      PageRenderer renderer = pageRendererFactory.getPageRenderer(entry, 
          entryStore);
      String fileName = folderPath + entryStore.getName(id) + ".html";
      Appendable out = null;
      try {
        out = appendableFactory.getAppendable(fileName);
        pageExporter.exportPage(renderer, out);
      } catch (IOException e) {
        logger.log(Level.SEVERE, "Failed writing to file: " + fileName, e);
      } finally {
        if (out instanceof Closeable) {
          try {
            ((Closeable) out).close();
          } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed closing file: " + fileName, e);
          }
        }
      }
    }
  }
  
  private void downloadAttachment(String id, String rootFolder) {
    AttachmentEntry attachment = (AttachmentEntry) entryStore.getEntry(id);
    String folderPath = getPath(attachment);
    if (folderPath != null) {
      folderPath = rootFolder + folderPath;
      new File(folderPath).mkdirs();
      String fileName = folderPath + attachment.getTitle().getPlainText();
      try {
        attachmentDownloader.download(attachment, fileName);
      } catch (IOException e) {
        String message = "Error reading from " + attachment.getEnclosureLink()
            .getHref() + "and/or writing to " + fileName;
        logger.log(Level.WARNING, message, e);
      }
    }
  }
  
  /**
   * Returns the site-relative path to the given entry, or {@code null} if
   * if any of this entry's ancestors are missing. The empty string is returned
   * if the given entry has no parent.
   */
  private String getPath(BaseContentEntry<?> entry) {
    Link parentLink = entry.getLink(SitesLink.Rel.PARENT, ILink.Type.ATOM);
    if (parentLink == null) {
      return "";
    }
    String parentId = parentLink.getHref();
    BaseContentEntry<?> parent = entryStore.getEntry(parentId);
    if (parent == null) {
      return null;
    }
    return getPath(parent) + entryStore.getName(parentId) + "/";
  }
}
