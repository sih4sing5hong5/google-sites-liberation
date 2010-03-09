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
import static com.google.sites.liberation.util.EntryType.getType;
import static com.google.sites.liberation.util.EntryType.isPage;
import static com.google.sites.liberation.util.EntryType.ATTACHMENT;

import com.google.common.collect.Lists;
import com.google.gdata.client.sites.SitesService;
import com.google.gdata.data.Content;
import com.google.gdata.data.OutOfLineContent;
import com.google.gdata.data.media.MediaFileSource;
import com.google.gdata.data.media.MediaSource;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.BasePageEntry;
import com.google.gdata.data.sites.PageName;
import com.google.inject.Inject;
import com.google.sites.liberation.parsers.PageParser;
import com.google.sites.liberation.util.EntryUtils;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Parses a page and its children and uploads them to a feed.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
final class PageImporterImpl implements PageImporter {

  private static final Logger LOGGER = Logger.getLogger(
      PageImporterImpl.class.getCanonicalName());
  
  private final EntryUpdater entryUpdater;
  private final EntryUploader entryUploader;
  private final PageParser pageParser;
  private final RelativeLinkConverter linkConverter;
  private final RevisionsImporter revisionsImporter;
  
  /**
   * Creates a new PageImporterImpl with the given dependencies.
   */
  @Inject
  PageImporterImpl(EntryUpdater entryUpdater,
      EntryUploader entryUploader,
      PageParser pageParser,
      RelativeLinkConverter linkConverter,
      RevisionsImporter revisionsImporter) {
    this.entryUpdater = checkNotNull(entryUpdater);
    this.entryUploader = checkNotNull(entryUploader);
    this.pageParser = checkNotNull(pageParser);
    this.linkConverter = checkNotNull(linkConverter);
    this.revisionsImporter = checkNotNull(revisionsImporter);
  }
  
  @Override
  public BasePageEntry<?> importPage(File directory, boolean importRevisions, 
      List<BasePageEntry<?>> ancestors, URL feedUrl, URL siteUrl, 
      SitesService sitesService) {
    checkNotNull(directory);
    File file = new File(directory, "index.html");
    if (!file.isFile()) {
      LOGGER.log(Level.WARNING, "No valid file in directory: " + directory);
      return null;
    }
    List<BaseContentEntry<?>> entries = pageParser.parsePage(file);
    BasePageEntry<?> page = getFirstPageEntry(entries);
    if (page == null) {
      LOGGER.log(Level.WARNING, "No valid page entry!");
      return null;
    }
    
    page.setPageName(new PageName(directory.getName()));
    linkConverter.convertLinks(page, ancestors, siteUrl, false);
    if (!ancestors.isEmpty()) {
      EntryUtils.setParent(page, ancestors.get(ancestors.size() - 1));
    }
    BasePageEntry<?> returnedEntry = null;
    if (importRevisions && new File(directory, "_revisions").isDirectory()) {
      returnedEntry = revisionsImporter.importRevisions(
          directory, ancestors, feedUrl, siteUrl, sitesService);
    }
    if (returnedEntry == null) {
      returnedEntry = (BasePageEntry<?>) entryUploader.uploadEntry(
          page, ancestors, feedUrl, sitesService);
    } else {
      returnedEntry = (BasePageEntry<?>) entryUpdater.updateEntry(
          returnedEntry, page, sitesService);
    }
    
    List<BasePageEntry<?>> newAncestors = Lists.newLinkedList(ancestors);
    newAncestors.add(returnedEntry);
    for (BaseContentEntry<?> child : getNonPageEntries(entries)) {
      if (getType(child) == ATTACHMENT) {
        if (child.getContent() != null) {
          String src = ((OutOfLineContent) child.getContent()).getUri();
          File attachmentFile = new File(directory, src);
          MediaSource mediaSource = new MediaFileSource(attachmentFile, 
              "application/octet-stream");
          child.setContent((Content) null);
          child.setMediaSource(mediaSource);
        } else {
          System.out.println(child.getTitle().getPlainText());
        }
      }
      EntryUtils.setParent(child, returnedEntry);
      entryUploader.uploadEntry(child, newAncestors, feedUrl, sitesService);
    }
    return returnedEntry;
  }
  
  private BasePageEntry<?> getFirstPageEntry(List<BaseContentEntry<?>> entries) {
    for (BaseContentEntry<?> entry : entries) {
      if (isPage(entry)) {
        return (BasePageEntry<?>) entry;
      }
    }
    return null;
  }
  
  private List<BaseContentEntry<?>> getNonPageEntries(
      List<BaseContentEntry<?>> entries) {
    List<BaseContentEntry<?>> children = Lists.newLinkedList();
    for (BaseContentEntry<?> entry : entries) {
      if (!isPage(entry)) {
        children.add(entry);
      }
    }
    return children;
  }
}
