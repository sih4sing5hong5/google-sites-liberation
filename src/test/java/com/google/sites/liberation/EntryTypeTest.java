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
import junit.framework.TestCase;

/**
 * Test for {@link EntryType}
 * @author bsimon@google.com (Your Name Here)
 *
 */
public class EntryTypeTest extends TestCase {
  
  public static void testGetType() {
    AnnouncementEntry e0 = new AnnouncementEntry();
    assertEquals(EntryType.getType(e0), EntryType.ANNOUNCEMENT);
    AnnouncementsPageEntry e1 = new AnnouncementsPageEntry();
    assertEquals(EntryType.getType(e1), EntryType.ANNOUNCEMENTS_PAGE);
    AttachmentEntry e2 = new AttachmentEntry();
    assertEquals(EntryType.getType(e2), EntryType.ATTACHMENT);
    CommentEntry e3 = new CommentEntry();
    assertEquals(EntryType.getType(e3), EntryType.COMMENT);
    FileCabinetPageEntry e4 = new FileCabinetPageEntry();
    assertEquals(EntryType.getType(e4), EntryType.FILE_CABINET_PAGE);
    ListItemEntry e5 = new ListItemEntry();
    assertEquals(EntryType.getType(e5), EntryType.LIST_ITEM);
    ListPageEntry e6 = new ListPageEntry();
    assertEquals(EntryType.getType(e6), EntryType.LIST_PAGE);
    WebPageEntry e7 = new WebPageEntry();
    assertEquals(EntryType.getType(e7), EntryType.WEB_PAGE);
  }
  
}
