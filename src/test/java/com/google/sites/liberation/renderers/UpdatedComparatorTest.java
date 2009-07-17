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

import static org.junit.Assert.*;

import com.google.gdata.data.DateTime;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.WebPageEntry;

import org.junit.Test;

import java.util.Comparator;

/**
 * Test for {@link UpdatedComparator}.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
public class UpdatedComparatorTest {

  @Test
  public void testCompare() {
    Comparator<BaseContentEntry<?>> c = new UpdatedComparator();
    BaseContentEntry<?> entry1 = getEntry("2009-07-02T21:46:23.133Z");
    BaseContentEntry<?> entry2 = getEntry("2598-11-25T21:46:23.133Z");
    assertTrue(c.compare(entry1, entry2) < 0);
    assertTrue(c.compare(entry2, entry1) > 0);
    entry2 = getEntry("2009-07-01T21:46:23.133Z");
    assertTrue(c.compare(entry1, entry2) > 0);
    entry2 = getEntry("2009-07-02T21:46:23.133Z");
    assertTrue(c.compare(entry1, entry2) == 0);
  }
  
  private BaseContentEntry<?> getEntry(String dateTime) {
    BaseContentEntry<?> entry = new WebPageEntry();
     entry.setUpdated(DateTime.parseDateTime(dateTime));
    return entry;
  }
}
