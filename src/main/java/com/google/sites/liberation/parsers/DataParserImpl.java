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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.sites.liberation.parsers.ParserUtils.hasClass;

import com.google.gdata.data.spreadsheet.Column;
import com.google.gdata.data.spreadsheet.Data;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Implements DataParser to parse an html element for {@link Data}.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
final class DataParserImpl implements DataParser {

  @Override
  public Data parseData(Element element) {
    checkNotNull(element);
    Data data = new Data();
    //This line is needed for the spreadsheet API
    data.setStartIndex(2);
    parseElement(element, data);
    return data;
  }
  
  /**
   * Populates the given Data object with information from the given element.
   */
  private void parseElement(Element element, Data data) {
    NodeList nodeList = element.getChildNodes();
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node node = nodeList.item(i);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        Element child = (Element) node;
        if (hasClass(child, "gs:column")) {
          Column column = new Column();
          column.setIndex(child.getAttribute("title"));
          column.setName(child.getTextContent());
          data.addColumn(column);
        } else {
          parseElement(child, data);
        }
      }
    }
  }
}
