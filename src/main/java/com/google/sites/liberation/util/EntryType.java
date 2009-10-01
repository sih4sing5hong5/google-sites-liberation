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
import com.google.gdata.data.sites.WebAttachmentEntry;
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
  WEB_ATTACHMENT,
  WEB_PAGE,
  OTHER;
  
  @Override
  public String toString() {
    switch (this) {
      case ANNOUNCEMENT: return "announcement";
      case ANNOUNCEMENTS_PAGE: return "announcementspage";
      case ATTACHMENT: return "attachment";
      case COMMENT: return "comment";
      case FILE_CABINET_PAGE: return "filecabinet";
      case LIST_ITEM: return "listitem";
      case LIST_PAGE: return "listpage";
      case WEB_ATTACHMENT: return "webattachment";
      case WEB_PAGE: return "webpage";
      default: return "other";
    }
  }
  
  /**
   * Returns the {@code EntryType} for the given entry.
   */
  public static final EntryType getType(BaseContentEntry<?> entry) {
    // TODO(gk5885): use the parameterized entry when we stop running into
    // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6302214
    @SuppressWarnings("unchecked")
    BaseContentEntry rawEntry = checkNotNull(entry);
    if (rawEntry instanceof AnnouncementEntry) {
      return EntryType.ANNOUNCEMENT;
    }
    if (rawEntry instanceof AnnouncementsPageEntry) {
      return EntryType.ANNOUNCEMENTS_PAGE;
    }
    if (rawEntry instanceof AttachmentEntry) {
      return EntryType.ATTACHMENT;
    }
    if (rawEntry instanceof CommentEntry) {
      return EntryType.COMMENT;
    }
    if (rawEntry instanceof FileCabinetPageEntry) {
      return EntryType.FILE_CABINET_PAGE;
    }
    if (rawEntry instanceof ListItemEntry) {
      return EntryType.LIST_ITEM;
    }
    if (rawEntry instanceof ListPageEntry) {
      return EntryType.LIST_PAGE;
    }
    if (rawEntry instanceof WebAttachmentEntry) {
      return EntryType.WEB_ATTACHMENT;
    }
    if (rawEntry instanceof WebPageEntry) {
      return EntryType.WEB_PAGE;
    }
    return EntryType.OTHER;
  }
  
  /**
   * Returns whether or not this entry represents a page in a site.
   */
  public static final boolean isPage(BaseContentEntry<?> entry) {
    checkNotNull(entry);
    return (entry instanceof BasePageEntry);
  }
}
