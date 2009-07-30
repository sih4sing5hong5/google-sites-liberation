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

import com.google.gdata.data.sites.BaseContentEntry;
import com.google.sites.liberation.util.XmlElement;

import org.joda.time.DateTime;

/**
 * Extends XmlElement to allow the creation of an hAtom "updated"
 * element in a single statement.
 *
 * @author bsimon@google.com (Benjamin Simon)
 */
final class UpdatedElement extends XmlElement {

  /**
   * Creates a new hAtom "updated" element for the given entry.
   */
  public UpdatedElement(BaseContentEntry<?> entry) {
    super("abbr");
    checkNotNull(entry);
    this.setAttribute("class", "updated");
    this.setAttribute("title", entry.getUpdated().toString());
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
    this.addText(month + ' ' + day + ", " + year);
  }  
}

