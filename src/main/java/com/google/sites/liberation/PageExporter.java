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

public final class PageExporter {

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
    return "<div><a href=\"../index.html\">"+parent.getTitle().getPlainText()+"</a></div>\n";
  }
	
  private String getMainXhtml() {
    String xhtml = "<h3>" + entry.getTitle().getPlainText() + "</h3>\n";
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
        xhtml += "<a href=\""+ getNiceTitle(e) + "/index.html\">" + 
                 e.getTitle().getPlainText() + "</a>\n";
        numSubPages++;
      }
    }
    if(numSubPages > 0) {
      xhtml = xhtml.substring(0, xhtml.length()-2);
      xhtml = "<hr /><div>Subpages (" + numSubPages + "): " + xhtml + "</div>\n";
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
                 "</strong> - " + e.getUpdated().toUiString() + "</div>\n" +
                 content;
        numComments++;
      }
    }
    xhtml = "<hr /><h4>Comments (" + numComments + ")</h4>\n" + xhtml;
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
                 e.getAuthors().get(0).getEmail() + "</div>\n";
        numAttachments++;
      }
    }
    xhtml = "<hr /><h4>Attachments (" + numAttachments + ")</h4>\n" + xhtml;
    return xhtml;
  }
  
  private String getNiceTitle(BaseEntry<?> entry) {
    String title = entry.getTitle().getPlainText();
    String niceTitle = "";
    for(String s : title.split("[\\W]+")) {
      niceTitle += s + "-";
    }
    if(niceTitle.length() > 0)
      niceTitle = niceTitle.substring(0, niceTitle.length()-1);
    else
      niceTitle = "-";
    return niceTitle;
  }		
}