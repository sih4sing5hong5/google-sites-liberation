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

package com.google.sites.liberation.renderers;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.sites.liberation.EntryType.COMMENT;
import static com.google.sites.liberation.EntryType.ATTACHMENT;

import com.google.gdata.data.ILink;
import com.google.gdata.data.Link;
import com.google.gdata.data.XhtmlTextConstruct;
import com.google.gdata.data.sites.AttachmentEntry;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.CommentEntry;
import com.google.gdata.data.sites.SitesLink;
import com.google.sites.liberation.EntryStore;
import com.google.sites.liberation.EntryType;
import com.google.sites.liberation.HyperLink;
import com.google.sites.liberation.PageExporter;
import com.google.sites.liberation.XmlElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

/**
 *
 * This is a basic implementation of PageRender that uses a 
 * {@code BaseContentEntry} and an {@code EntryStore} to render a basic web 
 * page from a site.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 * 
 * @param <T> the type of entry being rendered
 */
class BasePageRenderer<T extends BaseContentEntry<?>> implements PageRenderer {

  T entry;
  EntryStore entryStore;
  Collection<BaseContentEntry<?>> subpages;
  Collection<AttachmentEntry> attachments;
  Collection<CommentEntry> comments;
  
  /** 
   * Creates a new instance of BasePageRenderer.
   * 
   * @param entry the entry corresponding to this page
   * @param entryStore EntryStore containing this entry, its children, and its
   *                   parents if they exist
   */
  BasePageRenderer(T entry, EntryStore entryStore) {
    this.entry = checkNotNull(entry);
    this.entryStore = checkNotNull(entryStore);
    initChildren();
  }
  
  /**
   * This method initializes and populates the collections of various types of 
   * children entries. This method is called from the constructor and thus
   * should not be called from a subclass directly if the subclass calls the
   * super constructor.
   */
  void initChildren() {
    subpages = new TreeSet<BaseContentEntry<?>>(new TitleComparator());
    attachments = new TreeSet<AttachmentEntry>(new UpdatedComparator());
    comments = new TreeSet<CommentEntry>(new UpdatedComparator());
    for(BaseContentEntry<?> child : entryStore.getChildren(entry.getId())) {
      if (EntryType.isPage(child)) {
        subpages.add(child);
      }
      else if (EntryType.getType(child) == ATTACHMENT) {
        attachments.add((AttachmentEntry)child);
      }
      else if (EntryType.getType(child) == COMMENT) {
        comments.add((CommentEntry)child);
      }
    }
  }
  
  @Override
  public XmlElement renderAttachments() {
    if (attachments.size() == 0) {
      return null;
    }
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
  
  @Override
  public XmlElement renderComments() {
    if (comments.size() == 0) {
      return null;
    }
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

  @Override
  public XmlElement renderMainHtml() {
    XmlElement div = new XmlElement("div");
    String xhtmlContent = ((XhtmlTextConstruct)(entry.getTextContent()
        .getContent())).getXhtml().getBlob();
    div.addXml(xhtmlContent);
    return div;
  }

  @Override
  public XmlElement renderParentLinks() {
    List<BaseContentEntry<?>> ancestors = new ArrayList<BaseContentEntry<?>>();
    BaseContentEntry<?> currentChild = entry;
    while(currentChild != null) {
      Link parentLink = 
          currentChild.getLink(SitesLink.Rel.PARENT, ILink.Type.ATOM);
      if (parentLink == null) {
        currentChild = null;
      }
      else {
        currentChild = entryStore.getEntry(parentLink.getHref());
        ancestors.add(currentChild);
      }
    }
    if (ancestors.size() == 0) {
      return null;
    }
    XmlElement div = new XmlElement("div");
    for(int i = ancestors.size() - 1; i >= 0; i--) {
      BaseContentEntry<?> ancestor = ancestors.get(i);
      String path = "";
      for(int j = 0; j <= i; j++) {
        path += "../";
      }
      HyperLink link = new HyperLink(path + PageExporter.getNiceTitle(ancestor) 
          + ".html", ancestor.getTitle().getPlainText());
      div.addChild(link);
      div.addText(" > ");
    }
    
    return div;
  }

  @Override
  public XmlElement renderSpecialContent() {
    return null;
  }

  @Override
  public XmlElement renderSubpageLinks() {
    if (subpages.size() == 0) {
      return null;
    }
    XmlElement div = new XmlElement("div");
    div.addChild(new XmlElement("hr"));
    div.addText("Subpages (" + subpages.size() + "): ");
    boolean firstLink = true;
    for(BaseContentEntry<?> subpage : subpages) {
      String href = PageExporter.getNiceTitle(entry) + "/" + 
          PageExporter.getNiceTitle(subpage) + ".html";
      if (!firstLink) {
        div.addText(", ");
      }
      div.addChild(new HyperLink(href, subpage.getTitle().getPlainText()));
      firstLink = false;
    }
    return div;
  }

  @Override
  public XmlElement renderTitle() {
    XmlElement div = new XmlElement("div");
    XmlElement title = new XmlElement("h3");
    title.addText(entry.getTitle().getPlainText());
    div.addChild(title);
    return div;
  }
}
