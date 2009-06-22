package com.google.sites.liberation;

import com.google.gdata.data.sites.*;
import com.google.gdata.data.*;

import java.util.HashSet;

public class EntryFactory {

	public static BaseEntry<?> newEntry(EntryType type, String title, String id, 
			String xhtml, String parentId) {
	  BaseEntry<?> entry;
	  switch(type) {
	   case ANNOUNCEMENT: 
		 entry = new AnnouncementEntry(); break;
	   case ANNOUNCEMENTS_PAGE: 
		 entry = new AnnouncementsPageEntry(); break;
	   case ATTACHMENT: 
		 entry = new AttachmentEntry(); break;
	   case COMMENT: 
		 entry = new CommentEntry(); break;
	   case FILE_CABINET_PAGE: 
		 entry = new FileCabinetPageEntry(); break;
	   case LIST_ITEM:
		 entry = new ListItemEntry(); break;
	   case LIST_PAGE:
		 entry = new ListPageEntry(); break;
	   default:
		 entry = new WebPageEntry();
	  }
	  entry.setTitle(new PlainTextConstruct(title));
	  entry.setId(id);
	  entry.setContent(new PlainTextConstruct(xhtml));
	  return entry;
	}
	
	public static Iterable<BaseEntry<?>> getTestSite() {
	  Iterable<BaseEntry<?>> entries = new HashSet<BaseEntry<?>>();
	  BaseEntry<?> entry = newEntry(EntryType.WEB_PAGE, "Home", "1", "This is home", null);
	  entries.add(entry);
	  
	  return entries;
	}
}
