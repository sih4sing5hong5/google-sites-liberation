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
import static com.google.sites.liberation.util.EntryType.WEB_ATTACHMENT;
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
import com.google.inject.Inject;
import com.google.sites.liberation.util.EntryProvider;
import com.google.sites.liberation.util.EntryUtils;

import org.apache.commons.lang.StringEscapeUtils;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Uploads (updates if possible, otherwise inserts) an entry to a given feed URL.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
final class EntryUploaderImpl implements EntryUploader {

  private static final Logger LOGGER = Logger.getLogger(
      EntryUploaderImpl.class.getCanonicalName());
  
  private final EntryInserter entryInserter;
  private final EntryProvider entryProvider;
  private final EntryUpdater entryUpdater;
  
  /**
   * Creates a new EntryUploaderImpl with the given dependencies.
   */
  @Inject
  EntryUploaderImpl(EntryInserter entryInserter, EntryProvider entryProvider, 
      EntryUpdater entryUpdater) {
    this.entryInserter = checkNotNull(entryInserter);
    this.entryProvider = checkNotNull(entryProvider);
    this.entryUpdater = checkNotNull(entryUpdater);
  }
  
  @Override
  public BaseContentEntry<?> uploadEntry(BaseContentEntry<?> entry, 
      List<BasePageEntry<?>> ancestors, URL feedUrl, SitesService sitesService) {
    checkNotNull(entry);
    checkNotNull(ancestors);
    checkNotNull(feedUrl);
    checkNotNull(sitesService);
    BaseContentEntry<?> returnedEntry = null;
    if (entry.getId() != null) {
      if (entry.getId().startsWith(feedUrl.toExternalForm() + "/")) {
        returnedEntry = getEntryById(entry, sitesService);
      } else {
        entry.setId(null);
      }
    }
    if (returnedEntry == null) {
      if (isPage(entry) || 
          getType(entry) == ATTACHMENT || 
          getType(entry) == WEB_ATTACHMENT) {
        returnedEntry = getEntryByPath(entry, ancestors, feedUrl, sitesService);
      } else if (getType(entry) == COMMENT) {
        // TODO(gk5885): remove extra cast for
        // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6302214
        if (commentExists((CommentEntry) (BaseContentEntry) entry, feedUrl, sitesService)) {
          return entry;
        }
      } else if (getType(entry) == LIST_ITEM) {
        // TODO(gk5885): remove extra cast for
        // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6302214
        if (listItemExists((ListItemEntry) (BaseContentEntry) entry, feedUrl, sitesService)) {
          return entry;
        }
      }
    }
    if (returnedEntry == null) {
      return entryInserter.insertEntry(entry, feedUrl, sitesService);
    } else {
      return entryUpdater.updateEntry(returnedEntry, entry, 
          sitesService);
    }
  }

  /**
   * Returns whether or not an identical comment to the one given exists at the 
   * given feed URL.
   */
  private boolean commentExists(CommentEntry comment, URL feedUrl, 
      SitesService sitesService) {
    try {
      String content = StringEscapeUtils.unescapeXml(
          EntryUtils.getXhtmlContent(comment));
      ContentQuery query = new ContentQuery(feedUrl);
      String parentId = comment.getLink(SitesLink.Rel.PARENT, ILink.Type.ATOM)
          .getHref();
      query.setParent(parentId.substring(parentId.lastIndexOf('/') + 1));
      query.setKind("comment");
      List<BaseContentEntry<?>> entries = entryProvider
          .getEntries(query, sitesService);
      for (BaseContentEntry<?> entry : entries) {
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
  private boolean listItemExists(ListItemEntry listItem, URL feedUrl, 
      SitesService sitesService) {
    try {
      Map<String, String> values = Maps.newHashMap();
      for (Field field : listItem.getFields()) {
        values.put(field.getIndex(), field.getValue());
      }
      ContentQuery query = new ContentQuery(feedUrl);
      String parentId = listItem.getLink(SitesLink.Rel.PARENT, ILink.Type.ATOM)
          .getHref();
      query.setParent(parentId.substring(parentId.lastIndexOf('/') + 1));
      query.setKind("listitem");
      List<BaseContentEntry<?>> entries = entryProvider
          .getEntries(query, sitesService);
      for (BaseContentEntry<?> entry : entries) {
        // TODO(gk5885): remove extra cast for
        // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6302214
        ListItemEntry item = (ListItemEntry) (BaseContentEntry) entry;
        if (item.getFields().size() == listItem.getFields().size()) {
          boolean equal = true;
          for (Field field : item.getFields()) {
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
   * Returns the given entry's with the given id or null if it doesn't exist.
   */
  private BaseContentEntry<?> getEntryById(BaseContentEntry<?> entry, 
      SitesService sitesService) {
    try {
      return sitesService.getEntry(new URL(entry.getId()), entry.getClass());
    } catch (IOException e) {
      return null;
    } catch (ServiceException e) {
      return null;
    }
  }
  
  /**
   * Returns the site-relative path to the given entry.
   */
  private String getPath(BaseContentEntry<?> entry, 
      List<BasePageEntry<?>> ancestors) {
    String name;
    if (isPage(entry)) {
      name = ((BasePageEntry<?>) entry).getPageName().getValue();
    } else {
      name = entry.getTitle().getPlainText().replaceAll(" ", "%20");
    }
    String path = "/";
    for (BasePageEntry<?> ancestor : ancestors) {
      path += ancestor.getPageName().getValue() + "/";
    }
    return path + name;
  }
  
  /**
   * Returns the entry with the given entry's path, or null if it doesn't exist.
   */
  @SuppressWarnings("unchecked")
  private BaseContentEntry<?> getEntryByPath(BaseContentEntry<?> entry, 
      List<BasePageEntry<?>> ancestors, URL feedUrl, SitesService sitesService) {
    try {
      ContentQuery query = new ContentQuery(feedUrl);
      query.setPath(getPath(entry, ancestors));
      List<BaseContentEntry<?>> entries = entryProvider
          .getEntries(query, sitesService);
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
}
