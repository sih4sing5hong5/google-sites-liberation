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
import static com.google.sites.liberation.util.EntryType.getType;

import com.google.gdata.data.Person;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.sites.liberation.util.EntryUtils;
import com.google.sites.liberation.util.XmlElement;

import org.joda.time.DateTime;

/**
 * Provides utility methods to construct various XmlElement's.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
final class RendererUtils {
  
  /**
   * Creates a new hCard element for the given entry.
   */
  static XmlElement getAuthorElement(BaseContentEntry<?> entry) {
    checkNotNull(entry);
    XmlElement element = new XmlElement("span");
    element.setAttribute("class", "vcard");
    Person author = entry.getAuthors().get(0);
    String name = author.getName();
    String email = author.getEmail();
    if (name == null) {
      XmlElement link = getHyperLink("mailto:" + email, email);
      link.setAttribute("class", "email");
      element.addElement(link);
    } else {
      XmlElement link = getHyperLink("mailto:" + email, name);
      link.setAttribute("class", "fn");
      element.addElement(link);
    }
    return element;
  }
  
  /**
   * Creates a new hAtom "entry-content" div for the given entry.
   */
  static XmlElement getContentElement(BaseContentEntry<?> entry) {
    checkNotNull(entry);
    XmlElement element = new XmlElement("div");
    element.setAttribute("class", "entry-content");
    String xhtmlContent = EntryUtils.getContent(entry);
    if(xhtmlContent.contains("CDATA")) {
      System.out.println(xhtmlContent);
    }
    element.addXml(xhtmlContent);
    return element;
  }
  
  /**
   * Creates a new hAtom "hentry" element of the given type for the given entry.
   */
  static XmlElement getEntryElement(BaseContentEntry<?> entry, String elementType) {
    checkNotNull(entry, "entry");
    checkNotNull(elementType, "elementType");
    XmlElement element = new XmlElement(elementType);
    element.setAttribute("id", entry.getId());
    element.setAttribute("class", "hentry " + getType(entry).toString());
    return element;
  }

  /**
   * Creates a new HyperLink with the given href and display text.
   */
  static XmlElement getHyperLink(String href, String text) {
    checkNotNull(href, "href");
    checkNotNull(text, "text");
    XmlElement element = new XmlElement("a");
    element.setAttribute("href", href);
    element.addText(text);
    return element;
  }

  /**
   * Creates a new "sites:revision" for the given entry.
   */
  static XmlElement getRevisionElement(BaseContentEntry<?> entry) {
    checkNotNull(entry);
    XmlElement element = new XmlElement("span");
    element.setAttribute("class", "sites:revision");
    if (entry.getRevision() == null) {
      element.addText("1");
    } else {
      element.addText(entry.getRevision().getValue().toString());
    }
    return element;
  }

  /**
   * Creates a new hAtom "entry-summary" element for the given entry.
   */
  static XmlElement getSummaryElement(BaseContentEntry<?> entry) {
    checkNotNull(entry);
    XmlElement element = new XmlElement("span");
    element.setAttribute("class", "entry-summary");
    element.addText(entry.getSummary().getPlainText());
    return element;
  }

  /**
   * Creates a new hAtom "entry-title" element for the given entry.
   */
  static XmlElement getTitleElement(BaseContentEntry<?> entry) {
    checkNotNull(entry);
    XmlElement element = new XmlElement("span");
    element.setAttribute("class", "entry-title");
    element.addText(entry.getTitle().getPlainText());
    return element;
  }

  /**
   * Creates a new hAtom "updated" element for the given entry.
   */
  static XmlElement getUpdatedElement(BaseContentEntry<?> entry) {
    checkNotNull(entry);
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
}
