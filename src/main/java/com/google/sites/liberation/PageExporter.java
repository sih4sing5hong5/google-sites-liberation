package com.google.sites.liberation;

import java.util.Set;
import java.util.HashSet;

import com.google.gdata.data.BaseEntry;
import com.google.gdata.data.sites.AttachmentEntry;
import com.google.gdata.data.sites.CommentEntry;
import com.google.gdata.data.sites.SitesLink;
import com.google.gdata.data.Link;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;

public class PageExporter {

	BaseEntry<?> entry;
	BaseEntry<?> parent;
	Set<BaseEntry<?>> children;
	
	public PageExporter(BaseEntry<?> entry, Iterable<BaseEntry<?>> entries) {
	  Preconditions.checkNotNull(entry, "entry");
	  Preconditions.checkNotNull(entries, "entries");
	  Link parentLink = entry.getLink(SitesLink.Rel.PARENT, 
          SitesLink.Type.APPLICATION_XHTML_XML);
	  this.entry = entry;
	  parent = null;
	  children = new HashSet<BaseEntry<?>>();
	  for(BaseEntry<?> e : entries) {
	    if(parentLink != null && parentLink.getHref().equals(e.getId()))
	      parent = e;
	    else {
	      Link eParentLink = e.getLink(SitesLink.Rel.PARENT, 
	          SitesLink.Type.APPLICATION_XHTML_XML);
	      if(eParentLink != null && eParentLink.getHref().equals(entry.getId()))
	        children.add(e);
	    }
	  }
	}
	
	public String getXhtml() {
	  return getParentXhtml()+getMainXhtml()+getSubPagesXhtml()+
		  getCommentsXhtml()+getAttachmentsXhtml();
	}
	
	private String getParentXhtml() {
  	  String xhtml = "";
	  if(parent != null && parent.getTitle() != null)
		xhtml = "<div>"+parent.getTitle().getPlainText()+"</div>";
	  return xhtml;
	}
	
	private String getMainXhtml() {
	  String xhtml = "<h3>" + entry.getTitle().getPlainText() + "</h3>";
	  xhtml += entry.getTextContent().getContent().getPlainText();
	  return xhtml;
	}
	
	private String getSubPagesXhtml() {
	  Set<BaseEntry<?>> subPages = new HashSet<BaseEntry<?>>();
	  for(BaseEntry<?> e : children) {
		if(EntryType.isPage(EntryType.getType(e)))
		  subPages.add(e);
	  }
	  String xhtml = "";
	  if(!subPages.isEmpty()) {
	    xhtml = "<div>Subpages: ";
	    for(BaseEntry<?> e : subPages) {
		  xhtml += e.getTitle().getPlainText() + ", ";
	    }
	    xhtml = xhtml.substring(0, xhtml.length()-2);
        xhtml += "</div>";
	  }
	  return xhtml;
	}
	
	private String getCommentsXhtml() {
	  Iterable<CommentEntry> comments = Iterables.filter(children, CommentEntry.class);
	  String xhtml = comments.iterator().hasNext() ? "<div>Comments:</div>" : "";
	  for(CommentEntry e : comments) {
		xhtml += "<div>" + e.getTextContent().getContent().getPlainText() + "</div>";
	  }
	  return xhtml;
	}
	
	private String getAttachmentsXhtml() {
	  Iterable<AttachmentEntry> attachments = Iterables.filter(children, AttachmentEntry.class);
      String xhtml = attachments.iterator().hasNext() ? "<div>Attachments:</div>" : "";
      for(AttachmentEntry e : attachments) {
        xhtml += "<div>" + e.getTextContent().getContent().getPlainText() + "</div>";
      }
      return xhtml;
	}
}
