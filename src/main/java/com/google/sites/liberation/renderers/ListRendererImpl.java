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

import com.google.gdata.data.sites.ListItemEntry;
import com.google.gdata.data.sites.ListPageEntry;
import com.google.gdata.data.spreadsheet.Column;
import com.google.gdata.data.spreadsheet.Field;
import com.google.gdata.util.common.base.Nullable;
import com.google.sites.liberation.util.XmlElement;

import java.util.List;

/**
 * Renders the list in a list page.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
final class ListRendererImpl implements ListRenderer {
  
  @Override
  public XmlElement renderList(ListPageEntry entry, 
      @Nullable List<ListItemEntry> listItems) {
    XmlElement table = new XmlElement("table").setAttribute("border", "1");
    XmlElement header = new XmlElement("tr").setAttribute("class", "gs:data");
    for (Column col : entry.getData().getColumns()) {
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
      for (ListItemEntry item : listItems) {
        table.addElement(getRow(item));
      }
    }
    return table;
  }
  
  private XmlElement getRow(ListItemEntry item) {
    XmlElement element = RendererUtils.getEntryElement(item, "tr");
    for (Field field : item.getFields()) {
      String val;
      if (field.getValue() == null || field.getValue().equals("")) {
        val = "&#160;"; //Equivalent to &nbsp; but XML compliant
      } else if (field.getValue().equals("on")) {
        val = "\u2713"; //Checkmark
      } else {
        val = field.getValue();
      }
      XmlElement cell = new XmlElement("td");
      cell.setAttribute("class", "gs:field");
      cell.setAttribute("title", field.getIndex());
      cell.addXml(val);
      element.addElement(cell);
    }
    XmlElement authorCell = new XmlElement("td");
    element.addElement(authorCell.addElement(
        RendererUtils.getAuthorElement(item)));
    XmlElement revisionCell = new XmlElement("td");
    XmlElement updatedCell = new XmlElement("td");
    element.addElement(updatedCell.addElement(
        RendererUtils.getUpdatedElement(item)));
    element.addElement(revisionCell.addElement(
        RendererUtils.getRevisionElement(item)));
    return element;
  }
}
