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

package com.google.sites.liberation.parsers;

import static org.junit.Assert.*;

import com.google.common.collect.Lists;
import com.google.gdata.data.spreadsheet.Column;
import com.google.gdata.data.spreadsheet.Data;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;

import java.util.List;

/**
 * @author bsimon@google.com (Benjamin Simon)
 */
public class DataParserImplTest extends AbstractParserImplTest {

  private DataParser parser;
  private List<Column> columns;
   
  @Before
  public void before() {
    parser = new DataParserImpl();
    columns = Lists.newArrayList();
    Column col1 = new Column();
    col1.setIndex("A");
    col1.setName("Column 1");
    Column col2 = new Column();
    col2.setIndex("B");
    col2.setName("Column 2");
    Column col3 = new Column();
    col3.setIndex("C");
    col3.setName("Column 3");
    Column col4 = new Column();
    col4.setIndex("D");
    col4.setName("Column 4");
    columns.add(col1);
    columns.add(col2);
    columns.add(col3);
    columns.add(col4);
  }
  
  @Test
  public void testNormalData() {
    String html = "<tr class=\"gs:data\">";
    for(Column col : columns) {
      html += "<th class=\"gs:column\" title=\"" + col.getIndex() + "\">" +
              col.getName() + "</th>";
    }
    html += "</tr>";
    Element element = getElement(html);
    Data data = parser.parseData(element);
    int index = 0;
    for(Column col : data.getColumns()) {
      assertEquals(columns.get(index).getIndex(), col.getIndex());
      assertEquals(columns.get(index).getName(), col.getName());
      index++;
    }
  }
  
  @Test
  public void testComplicatedStructure() {
    String html = "<div class=\"gs:data\">" +
                    "<table>" +
                      "<tr class=\"gs:column\" title=\"A\">" +
                        "<td>Column <b>1</b></td>" + 
                      "</tr>" +
                      "<tr class=\"gs:column\" title=\"B\">" +
                        "<td><i>Column</i> 2</td>" +
                      "</tr>" +
                    "</table>" +
                    "<div class=\"gs:column\" title=\"C\">" +
                      "<a href=\"website.com\">Column</a> 3" +
                    "</div>" +
                    "<span class=\"gs:column\" title=\"D\">" +
                      "Column 4" +
                    "</span>" +
                  "</div>";
    Element element = getElement(html);
    Data data = parser.parseData(element);
    int index = 0;
    for(Column col : data.getColumns()) {
      assertEquals(columns.get(index).getIndex(), col.getIndex());
      assertEquals(columns.get(index).getName(), col.getName());
      index++;
    }
  }
}
