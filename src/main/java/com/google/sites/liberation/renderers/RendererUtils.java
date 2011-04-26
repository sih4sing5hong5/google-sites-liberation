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
import static com.google.sites.liberation.util.EntryType.ATTACHMENT;
import static com.google.sites.liberation.util.EntryType.COMMENT;
import static com.google.sites.liberation.util.EntryType.WEB_ATTACHMENT;
import static com.google.sites.liberation.util.EntryType.getType;
import static com.google.sites.liberation.util.EntryType.isPage;

import com.google.gdata.data.OutOfLineContent;
import com.google.gdata.data.Person;
import com.google.gdata.data.TextConstruct;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.sites.liberation.util.EntryUtils;
import com.google.sites.liberation.util.XmlElement;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides utility methods to construct various XmlElement's.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
final class RendererUtils {
  
  private static final Logger LOGGER = Logger.getLogger(
      RendererUtils.class.getCanonicalName());
  
  private static final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
      .appendMonthOfYearShortText()
      .appendLiteral(' ')
      .appendDayOfMonth(1)
      .appendLiteral(", ")
      .appendYear(4, 4)
      .toFormatter();
  
  /**
   * Creates a new hCard element for the given entry.
   */
  static XmlElement getAuthorElement(BaseContentEntry<?> entry) {
    checkNotNull(entry);
    XmlElement element = new XmlElement("span");
    element.setAttribute("class", "author");
    XmlElement vcard = new XmlElement("span");
    vcard.setAttribute("class", "vcard");
    Person author = entry.getAuthors().get(0);
    String name = author.getName();
    String email = author.getEmail();
    if (name == null) {
      name = "[no name found]";
    }
    if (email == null) {
      email = "[no email found]";
    }
    if (name == null) {
      XmlElement link = getHyperLink("mailto:" + email, email);
      link.setAttribute("class", "email");
      vcard.addElement(link);
    } else {
      XmlElement link = getHyperLink("mailto:" + email, name);
      link.setAttribute("class", "fn");
      vcard.addElement(link);
    }
    return element.addElement(vcard);
  }
  
  /**
   * Creates a new hAtom "entry-content" div containing the given entry's
   * xhtml content.
   */
  static XmlElement getXhtmlContentElement(BaseContentEntry<?> entry) {
    checkNotNull(entry);
    XmlElement element = new XmlElement("div");
    element.setAttribute("class", "entry-content");
    if (isPage(entry) || getType(entry) == COMMENT) {
      String xhtmlContent = EntryUtils.getXhtmlContent(entry);
      element.addXml(xhtmlContent);
    } else {
      LOGGER.log(Level.WARNING, "Only pages and comments have xhtml content!");
    }
    return element;
  }
  
  /**
   * Creates a new hAtom "entry-content entry-title" anchor, containing the 
   * given entry's out of line content link in the href attribute, and title as
   * its text.
   */
  static XmlElement getOutOfLineContentElement(BaseContentEntry<?> entry) {
    checkNotNull(entry);
    XmlElement element = new XmlElement("a");
    element.setAttribute("class", "entry-content entry-title");
    String title = entry.getTitle().getPlainText();
    String href;
    if (getType(entry) == ATTACHMENT) {
      href = title;  
    } else if (getType(entry) == WEB_ATTACHMENT) {
      href = ((OutOfLineContent) entry.getContent()).getUri();
    } else {
      LOGGER.log(Level.WARNING, "Only attachments have out of line content!");
      href = "";
    }
    element.setAttribute("href", href);
    element.addText(title);
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
    TextConstruct summary = entry.getSummary();
    if (summary == null) {
      element.addText("");
    } else {
      element.addText(entry.getSummary().getPlainText());
    }
    return element;
  }

  /**
   * Creates a new hAtom "entry-title" element for the given entry.
   */
  static XmlElement getTitleElement(BaseContentEntry<?> entry) {
    checkNotNull(entry);
    String title = entry.getTitle().getPlainText();
    if (getType(entry) == ATTACHMENT) {
      return getHyperLink(title, title).setAttribute("class", "entry-title");
    } else if (getType(entry) == WEB_ATTACHMENT) {
      String href = ((OutOfLineContent) entry.getContent()).getUri();
      return getHyperLink(href, title).setAttribute("class", "entry-title");
    }
    return new XmlElement("span").setAttribute("class", "entry-title")
        .addText(title);
  }

  /**
   * Creates a new hAtom "updated" element for the given entry.
   */
  static XmlElement getUpdatedElement(BaseContentEntry<?> entry) {
    checkNotNull(entry);
    XmlElement element = new XmlElement("abbr");
    element.setAttribute("class", "updated");
    element.setAttribute("title", entry.getUpdated().toString());
    DateTime jodaTime = new DateTime(entry.getUpdated().getValue(),
        DateTimeZone.UTC);
    element.addText(jodaTime.toString(formatter));
    return element;
  }
}
