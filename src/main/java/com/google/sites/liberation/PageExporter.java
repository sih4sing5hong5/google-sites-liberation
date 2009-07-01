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

import com.google.common.base.Preconditions;
import com.google.gdata.client.sites.ContentQuery;
import com.google.gdata.client.sites.SitesService;
import com.google.gdata.data.Entry;
import com.google.gdata.data.ILink;
import com.google.gdata.data.Link;
import com.google.gdata.data.XhtmlTextConstruct;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.SitesLink;
import com.google.gdata.util.ServiceException;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * This class can be used to export a single page in a Site as
 * a String of XHTML. 
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
public final class PageExporter {

  BaseContentEntry<?> entry;
  URL feedUrl;
	
  /**
   * Constructs a new PageExporter for the given entry and feedUrl
   * to which the entry belongs. The entry must be of a type that 
   * represents a page in a Site.
   */
  public PageExporter(BaseContentEntry<?> entry, URL feedUrl) {
    Preconditions.checkNotNull(entry, "entry");
    Preconditions.checkNotNull(feedUrl, "feedUrl");
    Preconditions.checkArgument(EntryType.isPage(entry));
    this.entry = entry;
    this.feedUrl = feedUrl;
  }
	
  /**
   * Exports this page as a String of XHTML.
   */
  public String getXhtml() {
    XmlElement html = new XmlElement("html");
    XmlElement body = new XmlElement("body");
    XmlElement parent = getParentXhtml();
    if (parent != null) {
      body.addChild(parent);
    }
    body.addChild(getMainXhtml());
    XmlElement subPages = getSubPagesXhtml();
    if (subPages != null) {
      body.addChild(subPages);
    }
    body.addChild(getAttachmentsXhtml());
    body.addChild(getCommentsXhtml());
    html.addChild(body);
    return html.toString();
  }
	
  /**
   * Returns the {@code XmlElement} containing the xhtml representing the link
   * to this page's parent page if it exists. Returns {@code null} if this is
   * a top-level page.
   */
  private XmlElement getParentXhtml() {
    XmlElement div = new XmlElement("div");
    Link parentLink = entry.getLink(SitesLink.Rel.PARENT, ILink.Type.ATOM);
    if (parentLink == null) {
      return null;
    }
    BaseContentEntry<?> parent = null;
    try {
      URL parentUrl = new URL(parentLink.getHref());
      parent = (BaseContentEntry<?>)(new SitesService("google-sites-export"))
          .getEntry(parentUrl, Entry.class).getAdaptedEntry();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ServiceException e) {
      e.printStackTrace();
    }
    HyperLink link = new HyperLink("../index.html", parent.getTitle().getPlainText());
    div.addChild(link);
    div.addText(" >");
    return div;
  }
	
  /**
   * Returns the main xhtml for this page including the title.
   */
  private XmlElement getMainXhtml() {
    XmlElement div = new XmlElement("div");
    XmlElement title = new XmlElement("h3");
    title.addText(entry.getTitle().getPlainText());
    div.addChild(title);
    String xhtmlContent = ((XhtmlTextConstruct)(entry.getTextContent().getContent()))
        .getXhtml().getBlob();
    div.addXml(xhtmlContent);
    return div;
  }
	
  /**
   * Returns the xhtml containing links to this page's subpages, if they
   * exist. If this page has no pages below it, this returns {@code null}.
   */
  private XmlElement getSubPagesXhtml() {
    ContentQuery childrenQuery = new ContentQuery(feedUrl);
    String id = entry.getId().substring(entry.getId().lastIndexOf('/') + 1);
    childrenQuery.setParent(id);
    XmlElement div = new XmlElement("div");
    List<XmlElement> links = new LinkedList<XmlElement>();
    for(BaseContentEntry<?> e : new ContinuousContentFeed(childrenQuery)) {
      if (EntryType.isPage(EntryType.getType(e))) {
        String href = getNiceTitle(e) + "/index.html";
        links.add(new HyperLink(href, e.getTitle().getPlainText())); 
      }
    }
    if (links.size() == 0) {
      return null;
    }
    div.addChild(new XmlElement("hr"));
    div.addText("Subpages (" + links.size() + "): ");
    boolean firstLink = true;
    for(XmlElement link : links) {
      if (!firstLink) {
        div.addText(", ");
      }
      div.addChild(link);
      firstLink = false;
    }
    return div;
  }
	
  /**
   * Returns the xhtml for this page's comments.
   */
  private XmlElement getCommentsXhtml() {
    ContentQuery childrenQuery = new ContentQuery(feedUrl);
    String id = entry.getId().substring(entry.getId().lastIndexOf('/') + 1);
    childrenQuery.setParent(id);
    XmlElement div = new XmlElement("div");
    List<XmlElement> comments = new LinkedList<XmlElement>();
    for(BaseContentEntry<?> e : new ContinuousContentFeed(childrenQuery)) {
      if (EntryType.getType(e) == EntryType.COMMENT) {
        String xhtmlContent = ((XhtmlTextConstruct)e.getTextContent().getContent())
            .getXhtml().getBlob();
        XmlElement comment = new XmlElement("div");
        XmlElement strong = new XmlElement("strong");
        strong.addText(e.getAuthors().get(0).getEmail());
        comment.addChild(strong);
        comment.addText(" - " + e.getUpdated().toUiString());
        comment.addXml(xhtmlContent);
        comments.add(comment);
      }
    }
    div.addChild(new XmlElement("hr"));
    XmlElement h4 = new XmlElement("h4");
    h4.addText("Comments (" + comments.size() + ")");
    div.addChild(h4);
    for(XmlElement comment : comments) {
      div.addChild(comment);
    }
    return div;
  }
	
  /**
   * Returns the xhtml for this page's attachments
   */
  private XmlElement getAttachmentsXhtml() {
    ContentQuery childrenQuery = new ContentQuery(feedUrl);
    String id = entry.getId().substring(entry.getId().lastIndexOf('/') + 1);
    childrenQuery.setParent(id);
    XmlElement div = new XmlElement("div");
    List<XmlElement> attachments = new LinkedList<XmlElement>();
    for(BaseContentEntry<?> e : new ContinuousContentFeed(childrenQuery)) {
      if (EntryType.getType(e) == EntryType.ATTACHMENT) {
        XmlElement attachment = new XmlElement("div");
        attachment.addText(e.getTitle().getPlainText() + " - on " +
                       e.getUpdated().toUiString() + " by " +
                       e.getAuthors().get(0).getEmail());
        attachments.add(attachment);
      }
    }
    div.addChild(new XmlElement("hr"));
    XmlElement h4 = new XmlElement("h4");
    h4.addText("Attachments (" + attachments.size() + ")");
    div.addChild(h4);
    for(XmlElement attachment : attachments) {
      div.addChild(attachment);
    }
    return div;
  }
  
  /**
   * Returns the given entry's title with all sequences of non-word characters
   * (^[a-zA-z0-9_]) replaced by a single hyphen.
   */
  public static String getNiceTitle(BaseContentEntry<?> entry) {
    String title = entry.getTitle().getPlainText();
    String niceTitle = title.replaceAll("[\\W]+", "-");
    if(niceTitle.length() == 0) {
      niceTitle = "-";
    }
    return niceTitle;
  }
}