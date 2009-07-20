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

package com.google.sites.liberation.elements;

import static org.junit.Assert.*;

import com.google.gdata.data.DateTime;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.WebPageEntry;

import org.junit.Test;

/**
 * @author bsimon@google.com (Benjamin Simon)
 */
public class UpdatedElementTest {

  @Test
  public void testNull() {
    try {
      new UpdatedElement(null);
      fail("Should throw NullPointerException!");
    } catch (NullPointerException e) {}
    try {
      new UpdatedElement(new WebPageEntry());
      fail("Entry must have an updated time");
    } catch (NullPointerException e) {}
  }
  
  @Test
  public void testConstructor() {
    String date = "2009-07-02T21:46:23.133Z";
    XmlElement element = new UpdatedElement(getEntry(date));
    assertEquals("<abbr class=\"updated\" title=\"" + date + 
        "\">Jul 2, 2009</abbr>", element.toString());
    date = "2598-11-25T23:41:10.256Z";
    element = new UpdatedElement(getEntry(date));
    assertEquals("<abbr class=\"updated\" title=\"" + date + 
    	"\">Nov 25, 2598</abbr>", element.toString());
  }
  
  private BaseContentEntry<?> getEntry(String dateTime) {
    BaseContentEntry<?> entry = new WebPageEntry();
     entry.setUpdated(DateTime.parseDateTime(dateTime));
    return entry;
  }
}
