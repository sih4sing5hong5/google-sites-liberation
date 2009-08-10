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

import com.google.gdata.data.spreadsheet.Field;

import org.w3c.dom.Element;

/**
 * Implements FieldParser to parse an html element for a {@link Field}.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
final class FieldParserImpl implements FieldParser {

  @Override
  public Field parseField(Element element) {
    checkNotNull(element);
    Field field = new Field();
    field.setIndex(element.getAttribute("title"));
    String value = element.getTextContent();
    if (value.equals("\u2713")) {
      value = "on";
    }
    field.setValue(value);
    return field;
  }
}
