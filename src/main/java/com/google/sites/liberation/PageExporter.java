package com.google.sites.liberation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import com.google.gdata.data.BaseEntry;
import com.google.gdata.data.sites.AttachmentEntry;
import com.google.gdata.data.sites.CommentEntry;
import com.google.gdata.data.sites.SitesLink;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;

public class PageExporter {

	BaseEntry entry;
	Iterable<BaseEntry> entries;
	
	public PageExporter(BaseEntry entry, Iterable<BaseEntry> entries) {
	  Preconditions.checkNotNull(entry, "entry");
	  Preconditions.checkNotNull(entries, "entries");
	  this.entry = entry;
	  this.entries = entries;
	}
	
	public String getXhtml() {
	  return getParentXhtml()+getMainXhtml()+getSubPagesXhtml()+
		  getCommentsXhtml()+getAttachmentsXhtml();
	}
	
	private String getParentXhtml() {
	  String parentLink = entry.getLink(SitesLink.Rel.PARENT, 
			SitesLink.Type.APPLICATION_XHTML_XML).getHref();
	  BaseEntry parent = null;
	  for(Iterator<BaseEntry> itr = entries.iterator(); itr.hasNext() && parent==null;) {
	    BaseEntry e = itr.next();
	    if(e.getId().equals(parentLink))
	      parent = e;
	  }
	  String xhtml = "";
	  if(parent != null)
		xhtml = "<div>"+parent.getTitle().getPlainText()+"</div>";
	  return xhtml;
	}
	
	private String getMainXhtml() {
	  return entry.getTextContent().getContent().getPlainText();
	}
	
	private String getSubPagesXhtml() {
	  Collection<BaseEntry> subPages = new HashSet<BaseEntry>();
	  for(BaseEntry e : entries) {
		if(EntryType.isPage(EntryType.getType(e)))
		  subPages.add(e);
	  }
	  String xhtml = "";
	  Iterator<BaseEntry> itr = subPages.iterator();
	  if(itr.hasNext()) {
		xhtml = "<div>Subpages: " + itr.next().getTitle().getPlainText();
		while(itr.hasNext()) {
		  xhtml += ", " + itr.next().getTitle().getPlainText();
		}
		xhtml += "</div>";
	  }
	  return xhtml;
	}
	
	private String getCommentsXhtml() {
	  Iterable<CommentEntry> comments = Iterables.filter(entries, CommentEntry.class);
	  String xhtml = "";
	  Iterator<CommentEntry> itr = comments.iterator();
	  if(itr.hasNext()) {
		xhtml = "<div>Comments:</div>";
		while(itr.hasNext()) {
		  xhtml += "<div>" + itr.next().getTextContent().getContent().getPlainText() + "</div>";
		}
	  }
	  return xhtml;
	}
	
	private String getAttachmentsXhtml() {
	  Iterable<AttachmentEntry> attachments = Iterables.filter(entries, AttachmentEntry.class);
      String xhtml = "";
	  Iterator<AttachmentEntry> itr = attachments.iterator();
	  if(itr.hasNext()) {
		xhtml = "<div>Attachments:</div>";
		while(itr.hasNext()) {
		  xhtml += "<div>" + itr.next().getTextContent().getContent().getPlainText() + "</div>";
		}
	  }
	  return xhtml;
	}
}
