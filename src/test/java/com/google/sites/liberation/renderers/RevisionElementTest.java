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

import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.Revision;
import com.google.gdata.data.sites.WebPageEntry;
import com.google.sites.liberation.util.XmlElement;

import org.junit.Test;

/**
 * @author bsimon@google.com (Benjamin Simon)
 */
public class RevisionElementTest {

  @Test
  public void testNull() {
    try {
      new RevisionElement(null);
      fail("Should throw NullPointerException!");
    } catch (NullPointerException e) {}
  }
  
  @Test
  public void testNoRevision() {
    XmlElement element = new RevisionElement(new WebPageEntry());
    assertEquals("<span class=\"sites:revision\">1</span>",
        element.toString());
  }
  
  @Test
  public void testNormal() {
    BaseContentEntry<?> entry = new WebPageEntry();
    entry.setRevision(new Revision(25));
    XmlElement element = new RevisionElement(entry);
    assertEquals("<span class=\"sites:revision\">25</span>",
        element.toString());
  }
}
