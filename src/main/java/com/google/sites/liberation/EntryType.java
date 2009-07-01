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

import com.google.gdata.data.sites.AnnouncementEntry;
import com.google.gdata.data.sites.AnnouncementRevisionEntry;
import com.google.gdata.data.sites.AnnouncementsPageEntry;
import com.google.gdata.data.sites.AttachmentEntry;
import com.google.gdata.data.sites.AttachmentRevisionEntry;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.CommentEntry;
import com.google.gdata.data.sites.CommentRevisionEntry;
import com.google.gdata.data.sites.FileCabinetPageEntry;
import com.google.gdata.data.sites.ListItemEntry;
import com.google.gdata.data.sites.ListPageEntry;
import com.google.gdata.data.sites.WebPageEntry;
import com.google.gdata.util.common.base.Preconditions;

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
  
  /**
   * Returns the <code>EntryType</code> for the given entry.
   */
  public static EntryType getType(BaseContentEntry<?> entry) {
    Preconditions.checkNotNull(entry);
    if (entry instanceof AnnouncementEntry || entry instanceof AnnouncementRevisionEntry) {
      return EntryType.ANNOUNCEMENT;
    }
    if (entry instanceof AnnouncementsPageEntry) {
      return EntryType.ANNOUNCEMENTS_PAGE;
    }
    if (entry instanceof AttachmentEntry || entry instanceof AttachmentRevisionEntry) {
      return EntryType.ATTACHMENT;
    }
    if (entry instanceof CommentEntry || entry instanceof CommentRevisionEntry) {
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
   * Returns whether or not this EntryType represents a page in a site.
   */
  public static boolean isPage(EntryType type) {
  	Preconditions.checkNotNull(type);
    switch(type) {
  	  case ANNOUNCEMENT:
  	  case ANNOUNCEMENTS_PAGE:
  	  case FILE_CABINET_PAGE:
  	  case LIST_PAGE:
  	  case WEB_PAGE:
  		return true;
      default: return false;
  	}
  }
  
  /**
   * Returns whether or not this entry represents a page in a site.
   */
  public static boolean isPage(BaseContentEntry<?> entry) {
    return isPage(getType(entry));
  }
}
