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
import com.google.gdata.data.ILink;
import com.google.gdata.data.Link;
import com.google.gdata.data.XhtmlTextConstruct;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.SitesLink;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * This class can be used to export a single page in a Site as
 * a String of XHTML. 
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
public final class PageExporter {

  BaseContentEntry<?> entry;
  EntryStore entryStore;
	
  /**
   * Constructs a new PageExporter for the given entry and feedUrl
   * to which the entry belongs. The entry must be of a type that 
   * represents a page in a Site.
   */
  public PageExporter(BaseContentEntry<?> entry, EntryStore entryStore) {
    Preconditions.checkNotNull(entry, "entry");
    Preconditions.checkNotNull(entryStore, "entryStore");
    Preconditions.checkArgument(EntryType.isPage(entry));
    this.entry = entry;
    this.entryStore = entryStore;
  }
	
  /**
   * Exports this page as a String of XHTML.
   */
  public String getXhtml() {
    Set<BaseContentEntry<?>> subPages = new HashSet<BaseContentEntry<?>>();
    Set<BaseContentEntry<?>> attachments = new HashSet<BaseContentEntry<?>>();
    Set<BaseContentEntry<?>> comments = new HashSet<BaseContentEntry<?>>();
    for(BaseContentEntry<?> child : entryStore.getChildren(entry.getId())) {
      if (EntryType.isPage(child)) {
        subPages.add(child);
      }
      if (EntryType.getType(child) == EntryType.ATTACHMENT) {
        attachments.add(child);
      }
      if (EntryType.getType(child) == EntryType.COMMENT) {
        comments.add(child);
      }
    }
    XmlElement html = new XmlElement("html");
    XmlElement body = new XmlElement("body");
    XmlElement parentXhtml = getParentXhtml();
    if (parentXhtml != null) {
      body.addChild(parentXhtml);
    }
    body.addChild(getMainXhtml());
    XmlElement subPagesXhtml = getSubPagesXhtml(subPages);
    if (subPagesXhtml != null) {
      body.addChild(subPagesXhtml);
    }
    body.addChild(getAttachmentsXhtml(attachments));
    body.addChild(getCommentsXhtml(comments));
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
    BaseContentEntry<?> parent = entryStore.getEntry(parentLink.getHref());
    HyperLink link = new HyperLink("../" + getNiceTitle(parent) + ".html", 
        parent.getTitle().getPlainText());
    div.addChild(link);
    div.addText(" >");
    return div;
  }
	
  /**
   * Returns the main xhtml for this page including the title.
   * TODO: Return the special content if this is not a WebPageEntry
   */
  private XmlElement getMainXhtml() {
    XmlElement div = new XmlElement("div");
    XmlElement title = new XmlElement("h3");
    title.addText(entry.getTitle().getPlainText());
    div.addChild(title);
    String xhtmlContent = ((XhtmlTextConstruct)(entry.getTextContent()
        .getContent())).getXhtml().getBlob();
    div.addXml(xhtmlContent);
    return div;
  }
	
  /**
   * Returns the xhtml containing links to this page's subpages, if they
   * exist. If this page has no pages below it, this returns {@code null}.
   */
  private XmlElement getSubPagesXhtml(Collection<BaseContentEntry<?>> subPages) {
    XmlElement div = new XmlElement("div");
    div.addChild(new XmlElement("hr"));
    div.addText("Subpages (" + subPages.size() + "): ");
    boolean firstLink = true;
    for(BaseContentEntry<?> subPage : subPages) {
      String href = getNiceTitle(entry) + "/" + getNiceTitle(subPage) + ".html";
      if (!firstLink) {
        div.addText(", ");
      }
      div.addChild(new HyperLink(href, subPage.getTitle().getPlainText()));
      firstLink = false;
    }
    return div;
  }
	
  /**
   * Returns the xhtml for this page's comments.
   */
  private XmlElement getCommentsXhtml(Collection<BaseContentEntry<?>> comments) {
    XmlElement div = new XmlElement("div");
    div.addChild(new XmlElement("hr"));
    XmlElement h4 = new XmlElement("h4");
    h4.addText("Comments (" + comments.size() + ")");
    div.addChild(h4);
    for(BaseContentEntry<?> comment : comments) {
      String xhtmlContent = ((XhtmlTextConstruct)comment.getTextContent()
          .getContent()).getXhtml().getBlob();
      XmlElement commentXhtml = new XmlElement("div");
      XmlElement strong = new XmlElement("strong");
      strong.addText(comment.getAuthors().get(0).getEmail());
      commentXhtml.addChild(strong);
      commentXhtml.addText(" - " + comment.getUpdated().toUiString());
      commentXhtml.addXml(xhtmlContent);
      div.addChild(commentXhtml);
    }
    return div;
  }
	
  /**
   * Returns the xhtml for this page's attachments
   */
  private XmlElement getAttachmentsXhtml(
      Collection<BaseContentEntry<?>> attachments) {
    XmlElement div = new XmlElement("div");
    div.addChild(new XmlElement("hr"));
    XmlElement h4 = new XmlElement("h4");
    h4.addText("Attachments (" + attachments.size() + ")");
    div.addChild(h4);
    for(BaseContentEntry<?> attachment : attachments) {
      XmlElement attachmentXhtml = new XmlElement("div");
      attachmentXhtml.addText(attachment.getTitle().getPlainText() + " - on " +
                     attachment.getUpdated().toUiString() + " by " +
                     attachment.getAuthors().get(0).getEmail());
      div.addChild(attachmentXhtml);
    }
    return div;
  }
  
  /**
   * Returns the given entry's title with all sequences of non-word characters
   * (^[a-zA-z0-9_]) replaced by a single hyphen.
   */
  private String getNiceTitle(BaseContentEntry<?> entry) {
    String title = entry.getTitle().getPlainText();
    String niceTitle = "";
    for(String s : title.split("[\\W]+")) {
      niceTitle += s + "-";
    }
    if (niceTitle.length() > 0) {
      niceTitle = niceTitle.substring(0, niceTitle.length()-1);
    }
    else {
      niceTitle = "-";
    }
    return niceTitle;
  }		
}