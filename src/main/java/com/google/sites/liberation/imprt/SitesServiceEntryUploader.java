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

import static com.google.sites.liberation.util.EntryType.isPage;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.gdata.client.sites.ContentQuery;
import com.google.gdata.client.sites.SitesService;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.BasePageEntry;
import com.google.gdata.data.sites.ContentFeed;
import com.google.gdata.util.ServiceException;
import com.google.sites.liberation.util.EntryTree;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implements {@EntryUploader} to upload entries using a SitesService.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
final class SitesServiceEntryUploader implements EntryUploader {

  private static final Logger LOGGER = Logger.getLogger(
      SitesServiceEntryUploader.class.getCanonicalName());
  
  private final SitesService service;
  
  /**
   * Creates a new SitesServiceEntryUploader that uses the given SitesService.
   */
  SitesServiceEntryUploader(SitesService service) {
    this.service = checkNotNull(service);
  }
  
  @Override
  public BaseContentEntry<?> uploadEntry(BaseContentEntry<?> entry, 
      EntryTree entryTree, URL feedUrl) {
    checkNotNull(entry);
    checkNotNull(entryTree);
    checkNotNull(feedUrl);
    BaseContentEntry<?> returnedEntry = null;
    if (entry.getId() != null && 
        entry.getId().startsWith(feedUrl.toExternalForm())) {
      returnedEntry = getEntryById(entry.getId(), entry.getClass());
    }
    if (returnedEntry == null && isPage(entry)) {
      String path = getPath((BasePageEntry<?>) entry, entryTree);
      returnedEntry = getEntryByPath(path, feedUrl);
    }
    if (returnedEntry == null) {
      returnedEntry = insertEntry(entry, feedUrl);
    } else {
      returnedEntry = updateEntry(entry, returnedEntry);
    }
    return returnedEntry;
  }
  
  /**
   * Returns the entry with the given id or null if it doesn't exist.
   */
  private <T extends BaseContentEntry<?>> T getEntryById(String id, Class<T> 
      entryClass) {
    try {
      return service.getEntry(new URL(id), entryClass);
    } catch (IOException e) {
      return null;
    } catch (ServiceException e) {
      return null;
    }
  }
  
  /**
   * Returns the site-relative path to the given entry.
   */
  private String getPath(BasePageEntry<?> entry, EntryTree entryTree) {
    String name = entry.getPageName().getValue();
    BasePageEntry<?> parent = entryTree.getParent(entry);
    if (parent == null) {
      return "/" + name;
    } else {
      return getPath(parent, entryTree) + "/" + name;
    }
  }
  
  /**
   * Returns the entry with the given path, or null if it doesn't exist.
   */
  private BaseContentEntry<?> getEntryByPath(String path, URL feedUrl) {
    try {
      ContentQuery query = new ContentQuery(feedUrl);
      query.setPath(path);
      ContentFeed feed = service.getFeed(query, ContentFeed.class);
      if (feed.getEntries().size() == 0) {
        return null;
      } else {
        return (BaseContentEntry<?>) feed.getEntries().get(0).getAdaptedEntry();
      }
    } catch (IOException e) {
      return null;
    } catch (ServiceException e) {
      return null;
    }
  }
  
  /**
   * Inserts the given entry at the given URL and returns the entry returned
   * from the server or null if it is unable to do the insert.
   */
  private BaseContentEntry<?> insertEntry(BaseContentEntry<?> entry, 
      URL feedUrl) {
    try {
      return service.insert(feedUrl, entry);
    } catch (IOException e) {
      LOGGER.log(Level.WARNING, "Unable to insert entry.", e);
      return null;
    } catch (ServiceException e) {
      LOGGER.log(Level.WARNING, "Unable to insert entry.", e);
      return null;
    }
  }
  
  /**
   * Uploads {@code newEntry} as an update of {@code oldEntry}, returning the 
   * entry returned from the server, or null if it was unable to perform the
   * update.
   */
  private BaseContentEntry<?> updateEntry(BaseContentEntry<?> newEntry, 
      BaseContentEntry<?> oldEntry) {
    try {
      return service.update(new URL(oldEntry.getEditLink().getHref()), newEntry);
    } catch (IOException e) {
      LOGGER.log(Level.WARNING, "Unable to update entry.", e);
      return null;
    } catch (NullPointerException e) {
      LOGGER.log(Level.WARNING, "Unable to update entry.", e);
      return null;
    } catch (ServiceException e) {
      LOGGER.log(Level.WARNING, "Unable to update entry.", e);
      return null;
    }
  }
}
