package com.google.sites.liberation;

import java.io.IOException;
import java.net.URL;

import com.google.gdata.data.BaseEntry;
import com.google.gdata.data.ILink;
import com.google.gdata.data.sites.SitesLink;
import com.google.gdata.data.Link;
import com.google.gdata.data.Entry;
import com.google.gdata.data.XhtmlTextConstruct;
import com.google.gdata.util.ServiceException;
import com.google.gdata.client.sites.ContentQuery;
import com.google.gdata.client.sites.SitesService;
import com.google.common.base.Preconditions;

public class PageExporter {

  BaseEntry<?> entry;
  URL feedUrl;
	
  public PageExporter(BaseEntry<?> entry, URL feedUrl) {
    Preconditions.checkNotNull(entry, "entry");
    Preconditions.checkNotNull(feedUrl, "feedUrl");
    this.entry = entry;
    this.feedUrl = feedUrl;
  }
	
  public String getXhtml() {
    return getParentXhtml()+getMainXhtml()+getSubPagesXhtml()+
        getCommentsXhtml()+getAttachmentsXhtml();
  }
	
  private String getParentXhtml() {
    Link parentLink = entry.getLink(SitesLink.Rel.PARENT, ILink.Type.ATOM);
    if(parentLink == null)
      return "";
    BaseEntry<?> parent = null;
    try {
      URL parentUrl = new URL(parentLink.getHref());
      parent = (new SitesService("google-sites-export")).getEntry(
          parentUrl, Entry.class).getAdaptedEntry();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ServiceException e) {
      e.printStackTrace();
    }
    return "<div>"+parent.getTitle().getPlainText()+"</div>";
  }
	
  private String getMainXhtml() {
    String xhtml = "<h3>" + entry.getTitle().getPlainText() + "</h3>";
    xhtml += ((XhtmlTextConstruct)(entry.getTextContent().getContent()))
        .getXhtml().getBlob();
    return xhtml;
  }
	
  private String getSubPagesXhtml() {
    ContentQuery childrenQuery = new ContentQuery(feedUrl);
    String id = entry.getId().substring(entry.getId().lastIndexOf('/') + 1);
    childrenQuery.setParent(id);
    String xhtml = "";
    int numSubPages = 0;
    for(BaseEntry<?> e : new ContinuousContentFeed(childrenQuery)) {
      if(EntryType.isPage(EntryType.getType(e))) {
        xhtml += e.getTitle().getPlainText() + ", ";
        numSubPages++;
      }
    }
    if(numSubPages > 0) {
      xhtml = xhtml.substring(0, xhtml.length()-2);
      xhtml = "<hr /><div>Subpages (" + numSubPages + "): " + xhtml + "</div>";
    }
    return xhtml;
  }
	
  private String getCommentsXhtml() {
    ContentQuery childrenQuery = new ContentQuery(feedUrl);
    String id = entry.getId().substring(entry.getId().lastIndexOf('/') + 1);
    childrenQuery.setParent(id);
    String xhtml = "";
    int numComments = 0;
    for(BaseEntry<?> e : new ContinuousContentFeed(childrenQuery)) {
      if(EntryType.getType(e) == EntryType.COMMENT) {
        String content = ((XhtmlTextConstruct)e.getTextContent().getContent())
            .getXhtml().getBlob();
        xhtml += "<div><strong>" + e.getAuthors().get(0).getEmail() + 
                 "</strong> - " + e.getUpdated().toUiString() + "</div>" +
                 content;
        numComments++;
      }
    }
    xhtml = "<hr /><h4>Comments (" + numComments + ")</h4>" + xhtml;
    return xhtml;
  }
	
  private String getAttachmentsXhtml() {
    ContentQuery childrenQuery = new ContentQuery(feedUrl);
    String id = entry.getId().substring(entry.getId().lastIndexOf('/') + 1);
    childrenQuery.setParent(id);
    String xhtml = "";
    int numAttachments = 0;
    for(BaseEntry<?> e : new ContinuousContentFeed(childrenQuery)) {
      if(EntryType.getType(e) == EntryType.ATTACHMENT) {
        xhtml += "<div>" + e.getTitle().getPlainText() + " - on " +
                 e.getUpdated().toUiString() + " by " + 
                 e.getAuthors().get(0).getEmail() + "</div>";
        numAttachments++;
      }
    }
    xhtml = "<hr /><h4>Attachments (" + numAttachments + ")</h4>" + xhtml;
    return xhtml;
  }
		
}