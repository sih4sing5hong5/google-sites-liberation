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

package com.google.sites.liberation.util;

import static com.google.gdata.util.common.base.Preconditions.checkNotNull;

import com.google.gdata.data.sites.AnnouncementEntry;
import com.google.gdata.data.sites.AnnouncementsPageEntry;
import com.google.gdata.data.sites.AttachmentEntry;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.BasePageEntry;
import com.google.gdata.data.sites.CommentEntry;
import com.google.gdata.data.sites.FileCabinetPageEntry;
import com.google.gdata.data.sites.ListItemEntry;
import com.google.gdata.data.sites.ListPageEntry;
import com.google.gdata.data.sites.WebPageEntry;

/**
 * An enumeration of the possible entry types.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
public enum EntryType {
  ANNOUNCEMENT,
  ANNOUNCEMENTS_PAGE,
  ATTACHMENT,
  COMMENT,
  FILE_CABINET_PAGE,
  LIST_ITEM,
  LIST_PAGE,
  WEB_PAGE,
  OTHER;
  
  @Override
  public String toString() {
    switch(this) {
      case ANNOUNCEMENT: return "announcement";
      case ANNOUNCEMENTS_PAGE: return "announcementsPage";
      case ATTACHMENT: return "attachment";
      case COMMENT: return "comment";
      case FILE_CABINET_PAGE: return "fileCabinet";
      case LIST_ITEM: return "listItem";
      case LIST_PAGE: return "listPage";
      case WEB_PAGE: return "webPage";
      default: return "other";
    }
  }
  
  /**
   * Returns the {@code EntryType} for the given entry.
   */
  public static EntryType getType(BaseContentEntry<?> entry) {
    checkNotNull(entry);
    if (entry instanceof AnnouncementEntry) {
      return EntryType.ANNOUNCEMENT;
    }
    if (entry instanceof AnnouncementsPageEntry) {
      return EntryType.ANNOUNCEMENTS_PAGE;
    }
    if (entry instanceof AttachmentEntry) {
      return EntryType.ATTACHMENT;
    }
    if (entry instanceof CommentEntry) {
      return EntryType.COMMENT;
    }
    if (entry instanceof FileCabinetPageEntry) {
      return EntryType.FILE_CABINET_PAGE;
    }
    if (entry instanceof ListItemEntry) {
      return EntryType.LIST_ITEM;
    }
    if (entry instanceof ListPageEntry) {
      return EntryType.LIST_PAGE;
    }
    if (entry instanceof WebPageEntry) {
      return EntryType.WEB_PAGE;
    }
    return EntryType.OTHER;
  }
  
  /**
   * Returns whether or not this entry represents a page in a site.
   */
  public static boolean isPage(BaseContentEntry<?> entry) {
    checkNotNull(entry);
    return (entry instanceof BasePageEntry);
  }
}
