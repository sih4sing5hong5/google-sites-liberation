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

import static com.google.sites.liberation.util.EntryType.ATTACHMENT;
import static com.google.sites.liberation.util.EntryType.COMMENT;
import static com.google.sites.liberation.util.EntryType.LIST_ITEM;
import static com.google.sites.liberation.util.EntryType.getType;
import static com.google.sites.liberation.util.EntryType.isPage;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.Maps;
import com.google.gdata.client.sites.ContentQuery;
import com.google.gdata.client.sites.SitesService;
import com.google.gdata.data.ILink;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.BasePageEntry;
import com.google.gdata.data.sites.CommentEntry;
import com.google.gdata.data.sites.ListItemEntry;
import com.google.gdata.data.sites.SitesLink;
import com.google.gdata.data.spreadsheet.Field;
import com.google.gdata.util.ServiceException;
import com.google.sites.liberation.util.EntryDownloader;
import com.google.sites.liberation.util.EntryTree;
import com.google.sites.liberation.util.SitesServiceEntryDownloader;

import org.apache.commons.lang.StringEscapeUtils;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
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
  private final URL feedUrl;
  private final EntryDownloader entryDownloader;
  
  /**
   * Creates a new SitesServiceEntryUploader that uses the given SitesService to
   * upload to the given feedUrl.
   */
  SitesServiceEntryUploader(SitesService service, URL feedUrl) {
    this.service = checkNotNull(service);
    this.feedUrl = checkNotNull(feedUrl);
    entryDownloader = new SitesServiceEntryDownloader(service);
  }
  
  /**
   * Creates a new SitesServiceEntryUploader that uses the given SitesService 
   * and EntryDownloader to upload to the given feedUrl (used only for testing).
   */
  SitesServiceEntryUploader(SitesService service, URL feedUrl, 
      EntryDownloader entryDownloader) {
    this.service = checkNotNull(service);
    this.feedUrl = checkNotNull(feedUrl);
    this.entryDownloader = checkNotNull(entryDownloader);
  }
  
  @Override
  public BaseContentEntry<?> uploadEntry(BaseContentEntry<?> entry, 
      EntryTree entryTree) {
    checkNotNull(entry);
    checkNotNull(entryTree);
    checkNotNull(feedUrl);
    BaseContentEntry<?> returnedEntry = null;
    if (entry.getId() != null && 
        entry.getId().startsWith(feedUrl.toExternalForm())) {
      returnedEntry = getEntryById(entry.getId(), entry.getClass());
    }
    if (returnedEntry == null) {
      if (isPage(entry)) {
        String path = getPath((BasePageEntry<?>) entry, entryTree);
        returnedEntry = getEntryByPath(path, feedUrl);
      } else if (getType(entry) == ATTACHMENT) {
        String name = entry.getTitle().getPlainText().replaceAll(" ", "%20");
        String path = getPath(entryTree.getParent(entry), entryTree) + 
            "/" + name;
        returnedEntry = getEntryByPath(path, feedUrl);        
      } else if (getType(entry) == COMMENT) {
        if (commentExists((CommentEntry) entry, feedUrl)) {
          return entry;
        }
      } else if (getType(entry) == LIST_ITEM) {
        if (listItemExists((ListItemEntry) entry, feedUrl)) {
          return entry;
        }
      }
    }
    if (returnedEntry == null) {
      returnedEntry = insertEntry(entry, feedUrl);
    } else {
      returnedEntry = updateEntry(entry, returnedEntry);
    }
    return returnedEntry;
  }

  /**
   * Returns whether or not an identical comment to the one given exists at the 
   * given feed URL.
   */
  private boolean commentExists(CommentEntry comment, URL feedUrl) {
    try {
      String content = StringEscapeUtils.unescapeXml(
          comment.getTextContent().getContent().getPlainText());
      ContentQuery query = new ContentQuery(feedUrl);
      String parentId = comment.getLink(SitesLink.Rel.PARENT, ILink.Type.ATOM)
          .getHref();
      query.setParent(parentId.substring(parentId.lastIndexOf('/') + 1));
      query.setKind("comment");
      List<BaseContentEntry<?>> entries = entryDownloader.getEntries(query);
      for(BaseContentEntry<?> entry : entries) {
        String otherContent = entry.getTextContent().getContent().getPlainText();
        if (otherContent.equals(content)) {
          return true;
        }
      }
      return false;
    } catch(IOException e) {
      LOGGER.log(Level.WARNING, "Error communicating with the server.", e);
      return false;
    } catch (ServiceException e) {
      LOGGER.log(Level.WARNING, "Error communicating with the server.", e);
      return false;
    }
  }

  /**
   * Returns whether or not an identical list item to the one given exists at 
   * the given feed URL.
   */
  private boolean listItemExists(ListItemEntry listItem, URL feedUrl) {
    try {
      Map<String, String> values = Maps.newHashMap();
      for(Field field : listItem.getFields()) {
        values.put(field.getIndex(), field.getValue());
      }
      ContentQuery query = new ContentQuery(feedUrl);
      String parentId = listItem.getLink(SitesLink.Rel.PARENT, ILink.Type.ATOM)
          .getHref();
      query.setParent(parentId.substring(parentId.lastIndexOf('/') + 1));
      query.setKind("listitem");
      List<BaseContentEntry<?>> entries = entryDownloader.getEntries(query);
      for(BaseContentEntry<?> entry : entries) {
        ListItemEntry item = (ListItemEntry) entry;
        if (item.getFields().size() == listItem.getFields().size()) {
          boolean equal = true;
          for(Field field : item.getFields()) {
            if (!values.get(field.getIndex()).equals(field.getValue())) {
              equal = false;
            }
          }
          if (equal) { 
            return true;
          }
        }
      }
      return false;
    } catch (IOException e) {
      LOGGER.log(Level.WARNING, "Error communicating with the server.", e);
      return false;
    } catch (ServiceException e) {
      LOGGER.log(Level.WARNING, "Error communicating with the server.", e);
      return false;
    }
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
  @SuppressWarnings("unchecked")
  private BaseContentEntry<?> getEntryByPath(String path, URL feedUrl) {
    try {
      ContentQuery query = new ContentQuery(feedUrl);
      query.setPath(path);
      List<BaseContentEntry<?>> entries = entryDownloader.getEntries(query);
      if (entries.size() == 0) {
        return null;
      } else {
        return entries.get(0);
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
