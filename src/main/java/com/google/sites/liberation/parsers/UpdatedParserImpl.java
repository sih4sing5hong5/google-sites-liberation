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

import com.google.gdata.data.DateTime;

import org.w3c.dom.Element;

/**
 * Implements UpdatedParser to parse an html element representing an entry's 
 * updated time.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
final class UpdatedParserImpl implements UpdatedParser {

  @Override
  public DateTime parseUpdated(Element element) {
    checkNotNull(element);
    String dateTime = element.getAttribute("title");
    if (dateTime.equals("")) {
      dateTime = element.getTextContent();
    }
    try {
      return DateTime.parseDateTime(dateTime);
    } catch (RuntimeException e) {
      return null;
    }
  }
}
