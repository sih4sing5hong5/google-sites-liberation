package com.google.sites.liberation;

import com.google.gdata.data.sites.BaseEditableContentEntry;
import com.google.gdata.data.sites.AnnouncementEntry;
import com.google.gdata.data.sites.AnnouncementsPageEntry;
import com.google.gdata.data.sites.AttachmentEntry;
import com.google.gdata.data.sites.CommentEntry;
import com.google.gdata.data.sites.FileCabinetPageEntry;
import com.google.gdata.data.sites.ListItemEntry;
import com.google.gdata.data.sites.ListPageEntry;
import com.google.gdata.data.sites.WebPageEntry;

/**
 * An enumeration of the possible entry types
 * @author bsimon@google.com (Ben Simon)
 *
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
   * Returns the <code>EntryType</code> for the given entry
   */
  public static EntryType getType(BaseEditableContentEntry<?> e) {
    if (e instanceof AnnouncementEntry)
      return EntryType.ANNOUNCEMENT;
    if (e instanceof AnnouncementsPageEntry)
      return EntryType.ANNOUNCEMENTS_PAGE;
    if (e instanceof AttachmentEntry)
      return EntryType.ATTACHMENT;
    if (e instanceof CommentEntry)
      return EntryType.COMMENT;
    if (e instanceof FileCabinetPageEntry)
      return EntryType.FILE_CABINET_PAGE;
    if (e instanceof ListItemEntry)
      return EntryType.LIST_ITEM;
    if (e instanceof ListPageEntry)
      return EntryType.LIST_PAGE;
    if (e instanceof WebPageEntry)
      return EntryType.WEB_PAGE;
    return EntryType.OTHER;
  }
}
