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

import static com.google.sites.liberation.EntryType.getType;

import com.google.gdata.data.Person;
import com.google.gdata.data.XhtmlTextConstruct;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.ListItemEntry;
import com.google.gdata.data.sites.ListPageEntry;
import com.google.gdata.data.spreadsheet.Column;
import com.google.gdata.data.spreadsheet.Field;
import com.google.gdata.util.common.base.Preconditions;

import org.joda.time.DateTime;

/**
 * This is a utility class used to create XmlElements representing various
 * hAtom components.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
public final class HAtomFactory {
  
  /**
   * Returns a new hAtom author element.
   * 
   * @param entry the entry that the element will represent
   * @return new html span representing the given entry's author
   */
  public static XmlElement getAuthorElement(BaseContentEntry<?> entry) {
    Preconditions.checkNotNull(entry);
    XmlElement element = new XmlElement("span");
    element.setAttribute("class", "vcard");
    Person author = entry.getAuthors().get(0);
    String name = author.getName();
    String email = author.getEmail();
    if (name == null) {
      XmlElement link = new HyperLink("mailto:" + email, email);
      link.setAttribute("class", "email");
      element.addElement(link);
    } else {
      XmlElement link = new HyperLink("mailto:" + email, name);
      link.setAttribute("class", "fn");
      XmlElement abbr = new XmlElement("abbr");
      abbr.setAttribute("class", "email");
      abbr.setAttribute("title", email);
      element.addElement(link);
      element.addElement(abbr);
    }
    return element;
  }
  
  /**
   * Returns a new hAtom entry element for the given entry.
   * 
   * @param entry the entry represented by this link
   * @param elementType the type of element to return
   * @return new XmlElement representing the given entry's type
   */
  public static XmlElement getEntryElement(BaseContentEntry<?> entry,
      String elementType) {
    Preconditions.checkNotNull(entry, "entry");
    Preconditions.checkNotNull(elementType, "elementType");
    XmlElement element = new XmlElement(elementType);
    element.setAttribute("id", entry.getId());
    String type = "";
    switch(getType(entry)) {
      case ANNOUNCEMENT: type = "announcement"; break;
      case ANNOUNCEMENTS_PAGE: type = "announcementspage"; break;
      case ATTACHMENT: type = "attachment"; break;
      case COMMENT: type = "comment"; break;
      case FILE_CABINET_PAGE: type = "filecabinet"; break;
      case LIST_ITEM: type = "listitem"; break;
      case LIST_PAGE: type = "listpage"; break;
      case WEB_PAGE: type = "webpage"; break;
    }
    element.setAttribute("class", "hentry " + type);
    return element;
  }
  
  /**
   * Returns a new hAtom updated element.
   * 
   * @param entry the entry that the element will represent
   * @return new html abbr representing the given entry's updated time
   */
  public static XmlElement getUpdatedElement(BaseContentEntry<?> entry) {
    Preconditions.checkNotNull(entry);
    XmlElement element = new XmlElement("abbr");
    element.setAttribute("class", "updated");
    element.setAttribute("title", entry.getUpdated().toString());
    DateTime jodaTime = new DateTime(entry.getUpdated().getValue());
    String month = "";
    switch(jodaTime.getMonthOfYear()) {
      case 1: month = "Jan"; break;
      case 2: month = "Feb"; break;
      case 3: month = "Mar"; break;
      case 4: month = "Apr"; break;
      case 5: month = "May"; break;
      case 6: month = "Jun"; break;
      case 7: month = "Jul"; break;
      case 8: month = "Aug"; break;
      case 9: month = "Sep"; break;
      case 10: month = "Oct"; break;
      case 11: month = "Nov"; break;
      case 12: month = "Dec"; break;
    }
    int day = jodaTime.getDayOfMonth();
    int year = jodaTime.getYear();
    element.addText(month + ' ' + day + ", " + year);
    return element;
  }
  
  /**
   * Returns a new hAtom content element for the given entry.
   * 
   * @param entry the entry that the element will represent
   * @return new XmlElement representing the given entry's content
   */
  public static XmlElement getContentElement(BaseContentEntry<?> entry) {
    Preconditions.checkNotNull(entry);
    XmlElement element = new XmlElement("div");
    element.setAttribute("class", "entry-content");
    String xhtmlContent = ((XhtmlTextConstruct)(entry.getTextContent()
        .getContent())).getXhtml().getBlob();
    element.addXml(xhtmlContent);
    return element;
  }
  
  /**
   * Returns a new hAtom title element for the given entry.
   *  
   * @param entry the entry the element will represent
   * @param elementType the type of html element to return
   * @return new XmlElement representing the given entry's title
   */
  public static XmlElement getTitleElement(BaseContentEntry<?> entry, 
      String elementType) {
    Preconditions.checkNotNull(entry, "entry");
    Preconditions.checkNotNull(elementType, "elementType");
    XmlElement element = new XmlElement(elementType);
    element.setAttribute("class", "entry-title");
    element.addText(entry.getTitle().getPlainText());
    return element;
  }
  
  /**
   * Returns a new html table header containing the data information given by a 
   * ListPageEntry.
   * 
   * @param entry ListPageEntry defining the columns in the list
   * @return new html row representing the given entry's list data
   */
  public static XmlElement getListHeaderElement(ListPageEntry entry) {
    Preconditions.checkNotNull(entry);
    XmlElement header = new XmlElement("tr");
    header.setAttribute("class", "data");
    char index = 'A';
    for(Column col : entry.getData().getColumns()) {
      XmlElement cell = new XmlElement("th");
      cell.setAttribute("class", "column");
      cell.setAttribute("title", Character.toString(index));
      cell.addText(col.getName());
      header.addElement(cell);
      index++;
    }
    return header;
  }
  
  /**
   * Returns a new row containing the fields of a given ListItemEntry.
   * 
   * @param entry ListItemEntry this element represents
   * @return new XmlElement representing the given entry's list fields
   */
  public static XmlElement getListItemElement(ListItemEntry entry) {
    Preconditions.checkNotNull(entry);
    XmlElement element = getEntryElement(entry, "tr");
    for(Field col : entry.getFields()) {
      String val = (col.getValue() == null) ? "" : col.getValue();
      XmlElement cell = new XmlElement("td");
        cell.addText(val);
      cell.setAttribute("class", "field");
      element.addElement(cell);
    }
    return element;
  }
}
