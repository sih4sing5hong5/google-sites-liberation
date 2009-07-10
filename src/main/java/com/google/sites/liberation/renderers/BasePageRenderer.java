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
import static com.google.sites.liberation.EntryType.getType;
import static com.google.sites.liberation.EntryType.isPage;
import static com.google.sites.liberation.HAtomFactory.getAuthorElement;
import static com.google.sites.liberation.HAtomFactory.getEntryElement;
import static com.google.sites.liberation.HAtomFactory.getContentElement;
import static com.google.sites.liberation.HAtomFactory.getTitleElement;
import static com.google.sites.liberation.HAtomFactory.getUpdatedElement;

import com.google.gdata.data.ILink;
import com.google.gdata.data.Link;
import com.google.gdata.data.sites.AttachmentEntry;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.CommentEntry;
import com.google.gdata.data.sites.SitesLink;
import com.google.sites.liberation.EntryStore;
import com.google.sites.liberation.HyperLink;
import com.google.sites.liberation.XmlElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

/**
 * This is a basic implementation of PageRender that uses a 
 * {@code BaseContentEntry} and an {@code EntryStore} to render a basic web 
 * page from a site.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 * 
 * @param <T> the type of entry being rendered
 */
class BasePageRenderer<T extends BaseContentEntry<?>> implements PageRenderer {

  protected final T entry;
  protected final EntryStore entryStore;
  protected Collection<BaseContentEntry<?>> subpages;
  protected Collection<AttachmentEntry> attachments;
  protected Collection<CommentEntry> comments;
  
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
    subpages = new TreeSet<BaseContentEntry<?>>(new TitleComparator());
    attachments = new TreeSet<AttachmentEntry>(new UpdatedComparator());
    comments = new TreeSet<CommentEntry>(new UpdatedComparator());
    for(BaseContentEntry<?> child : entryStore.getChildren(entry.getId())) {
      addChild(child);
    }
  }
  
  /**
   * This method adds the given child to the correct collection. It is called
   * from the constructor and should be overridden for subclasses with
   * additional child types. 
   */
  protected void addChild(BaseContentEntry<?> child) {
    switch(getType(child)) {
      case ATTACHMENT: attachments.add((AttachmentEntry) child); break;
      case COMMENT: comments.add((CommentEntry) child); break;
      default: 
        if (isPage(child)) {
          subpages.add(child);
        }
    }
  }
  
  @Override
  public XmlElement renderAttachments() {
    if (attachments.size() == 0) {
      return null;
    }
    XmlElement div = new XmlElement("div");
    div.addElement(new XmlElement("hr"));
    XmlElement h4 = new XmlElement("h4");
    h4.addText("Attachments (" + attachments.size() + ")");
    div.addElement(h4);
    for(BaseContentEntry<?> attachment : attachments) {
      XmlElement attachmentDiv = getEntryElement(attachment, "div");
      XmlElement link = getTitleElement(attachment, "a");
      String href = entryStore.getName(entry.getId()) + "/" + 
          attachment.getTitle().getPlainText();
      link.setAttribute("href", href);
      XmlElement updated = getUpdatedElement(attachment);
      XmlElement author = getAuthorElement(attachment);
      attachmentDiv.addElement(link);
      attachmentDiv.addText(" - on ");
      attachmentDiv.addElement(updated);
      attachmentDiv.addText(" by ");
      attachmentDiv.addElement(author);
      div.addElement(attachmentDiv);
    }
    return div;
  }
  
  @Override
  public XmlElement renderComments() {
    if (comments.size() == 0) {
      return null;
    }
    XmlElement div = new XmlElement("div");
    div.addElement(new XmlElement("hr"));
    XmlElement h4 = new XmlElement("h4");
    h4.addText("Comments (" + comments.size() + ")");
    div.addElement(h4);
    for(BaseContentEntry<?> comment : comments) {
      XmlElement commentDiv = getEntryElement(comment, "div");
      XmlElement author = getAuthorElement(comment);
      XmlElement updated = getUpdatedElement(comment);
      XmlElement content = getContentElement(comment);
      commentDiv.addElement(author);
      commentDiv.addText(" - ");
      commentDiv.addElement(updated);
      commentDiv.addElement(content);
      div.addElement(commentDiv);
    }
    return div;
  }

  @Override
  public XmlElement renderMainContent() {
    XmlElement div = new XmlElement("div");
    div.addText("Updated on ");
    div.addElement(getUpdatedElement(entry));
    div.addText(" by ");
    div.addElement(getAuthorElement(entry));
    div.addElement(getContentElement(entry));
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
      } else {
        currentChild = entryStore.getEntry(parentLink.getHref());
        if (currentChild != null) {
          ancestors.add(currentChild);
        }
      }
    }
    if (ancestors.size() == 0) {
      return null;
    }
    XmlElement div = new XmlElement("div");
    HyperLink link = null;
    BaseContentEntry<?> ancestor = null;
    for(int i = ancestors.size() - 1; i >= 0; i--) {
      ancestor = ancestors.get(i);
      String path = "";
      for(int j = 0; j <= i; j++) {
        path += "../";
      }
      link = new HyperLink(path + entryStore.getName(ancestor.getId()) 
          + ".html", ancestor.getTitle().getPlainText());
      div.addElement(link);
      div.addText(" > ");
    }
    link.setAttribute("rel", "up");
    link.setAttribute("id", ancestor.getId());
    return div;
  }

  @Override
  public XmlElement renderAdditionalContent() {
    return null;
  }

  @Override
  public XmlElement renderSubpageLinks() {
    if (subpages.size() == 0) {
      return null;
    }
    XmlElement div = new XmlElement("div");
    div.addElement(new XmlElement("hr"));
    div.addText("Subpages (" + subpages.size() + "): ");
    boolean firstLink = true;
    for(BaseContentEntry<?> subpage : subpages) {
      String href = entryStore.getName(entry.getId()) + "/" + 
          entryStore.getName(subpage.getId()) + ".html";
      if (!firstLink) {
        div.addText(", ");
      }
      div.addElement(new HyperLink(href, subpage.getTitle().getPlainText()));
      firstLink = false;
    }
    return div;
  }

  @Override
  public XmlElement renderTitle() {
    XmlElement title = getTitleElement(entry, "h3");
    return title;
  }
}
