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

package com.google.sites.liberation.export;

import com.google.gdata.data.sites.BaseContentEntry;
import com.google.sites.liberation.util.XmlElement;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import java.io.IOException;
import java.util.List;

/**
 * Exports the history of a page as html.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
final class HistoryExporterImpl implements HistoryExporter {

  private static final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
      .appendMonthOfYearShortText()
      .appendLiteral(' ')
      .appendDayOfMonth(1)
      .appendLiteral(", ")
      .appendYear(4, 4)
      .appendLiteral(' ')
      .appendHourOfHalfday(1)
      .appendLiteral(':')
      .appendMinuteOfHour(1)
      .appendLiteral(' ')
      .appendHalfdayOfDayText()
      .toFormatter();
  
  @Override
  public void exportHistory(List<BaseContentEntry<?>> revisions, Appendable out)
      throws IOException {
    XmlElement history = new XmlElement("table");
    history.setAttribute("width", "100%");
    XmlElement header = new XmlElement("tr");
    header.setAttribute("align", "left");
    header.addElement(new XmlElement("th").addText("Version"));
    header.addElement(new XmlElement("th").addText("Last Edited"));
    header.addElement(new XmlElement("th").addText("Edited By"));
    history.addElement(header);
    int maxRevision = getMaxRevision(revisions);
    for (BaseContentEntry<?> revision : revisions) {
      int number = revision.getRevision().getValue();
      XmlElement row = new XmlElement("tr");
      String href = (number == maxRevision) ? "index.html" : 
          "_revisions/" + number + ".html";
      XmlElement link = new XmlElement("a").addText("Version " + number)
          .setAttribute("href", href);
      row.addElement(new XmlElement("td").addElement(link));
      DateTime jodaTime = new DateTime(revision.getUpdated().getValue());
      XmlElement updated = new XmlElement("td").addText(
          jodaTime.toString(formatter));
      row.addElement(updated);
      XmlElement author = new XmlElement("a");
      String name = revision.getAuthors().get(0).getName();
      String email = revision.getAuthors().get(0).getEmail();
      row.addElement(new XmlElement("td").addElement(author.addText(name)
          .setAttribute("href", "mailto:" + email)));
      history.addElement(row);
    }
    XmlElement html = new XmlElement("html");
    XmlElement head = new XmlElement("head");
    XmlElement title = new XmlElement("title");
    title.addText("Version history for " + 
        revisions.get(0).getTitle().getPlainText());
    html.addElement(head.addElement(title));
    html.addElement(new XmlElement("body").addElement(history));
    html.appendTo(out);
  }
  
  private int getMaxRevision(List<BaseContentEntry<?>> revisions) {
    int max = 0;
    for (BaseContentEntry<?> revision : revisions) {
      max = Math.max(max, revision.getRevision().getValue());
    }
    return max;
  }
}
