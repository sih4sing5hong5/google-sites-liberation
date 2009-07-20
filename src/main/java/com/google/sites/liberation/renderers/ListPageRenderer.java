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

import static com.google.gdata.util.common.base.Preconditions.checkNotNull;
import static com.google.sites.liberation.EntryType.getType;
import static com.google.sites.liberation.EntryType.isPage;

import com.google.gdata.data.sites.AttachmentEntry;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.CommentEntry;
import com.google.gdata.data.sites.ListItemEntry;
import com.google.gdata.data.sites.ListPageEntry;
import com.google.gdata.data.spreadsheet.Column;
import com.google.gdata.data.spreadsheet.Field;
import com.google.sites.liberation.EntryStore;
import com.google.sites.liberation.elements.AuthorElement;
import com.google.sites.liberation.elements.EntryElement;
import com.google.sites.liberation.elements.RevisionElement;
import com.google.sites.liberation.elements.UpdatedElement;
import com.google.sites.liberation.elements.XmlElement;

import java.util.Collection;
import java.util.TreeSet;

/**
 * An extension of BasePageRenderer which implements 
 * PageRenderer.renderSpecialContent to render the list section in a List Page.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
class ListPageRenderer extends BasePageRenderer<ListPageEntry> {

  Collection<ListItemEntry> listItems;
  
  ListPageRenderer(ListPageEntry entry, EntryStore entryStore) {
    super(checkNotNull(entry), checkNotNull(entryStore));
  }
  
  /**
   * Overrides addChild from BasePageRenderer so that listItems is populated 
   * in addition to subpages, attachments, and comments.
   */
  @Override
  protected void addChild(BaseContentEntry<?> child) {
    checkNotNull(child);
    if (listItems == null) { 
      listItems = new TreeSet<ListItemEntry>(new UpdatedComparator());
    }
    switch(getType(child)) {
      case ATTACHMENT: attachments.add((AttachmentEntry) child); break;
      case COMMENT: comments.add((CommentEntry) child); break;
      case LIST_ITEM: listItems.add((ListItemEntry) child); break;
      default: 
        if (isPage(child)) {
          subpages.add(child);
        }
    }
  }
  
  /**
   * Renders the list section in the page.
   */
  @Override
  public XmlElement renderAdditionalContent() {
    XmlElement table = new XmlElement("table");
    XmlElement header = new XmlElement("tr");
    header.setAttribute("class", "gs:data");
    for(Column col : entry.getData().getColumns()) {
      XmlElement cell = new XmlElement("th");
      cell.setAttribute("class", "gs:column");
      cell.setAttribute("title", col.getIndex());
      cell.addText(col.getName());
      header.addElement(cell);
    }
    XmlElement authorCell = new XmlElement("th");
    header.addElement(authorCell.addText("Author"));
    XmlElement updatedCell = new XmlElement("th");
    header.addElement(updatedCell.addText("Updated"));
    XmlElement revisionCell = new XmlElement("th");
    header.addElement(revisionCell.addText("Version"));
    table.addElement(header);
    if (listItems != null) {
      for(ListItemEntry item : listItems) {
        table.addElement(getRow(item));
      }
    }
    return table;
  }
  
  private XmlElement getRow(ListItemEntry item) {
    XmlElement element = new EntryElement(entry, "tr");
    for(Field field : item.getFields()) {
      String val = (field.getValue() == null) ? "" : field.getValue();
      XmlElement cell = new XmlElement("td");
      cell.setAttribute("class", "gs:field");
      cell.setAttribute("title", field.getIndex());
      cell.addText(val);
      element.addElement(cell);
    }
    XmlElement authorCell = new XmlElement("td");
    element.addElement(authorCell.addElement(new AuthorElement(item)));
    XmlElement revisionCell = new XmlElement("td");
    element.addElement(revisionCell.addElement(new RevisionElement(item)));
    XmlElement updatedCell = new XmlElement("td");
    element.addElement(updatedCell.addElement(new UpdatedElement(item)));
    return element;
  }
}
