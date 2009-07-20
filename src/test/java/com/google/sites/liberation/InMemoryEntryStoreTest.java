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

package com.google.sites.liberation;

import static org.junit.Assert.*;

import com.google.gdata.data.ILink;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.SitesLink;
import com.google.gdata.data.sites.WebPageEntry;

import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

/**
 * @author bsimon@google.com (Benjamin Simon)
 */
public class InMemoryEntryStoreTest {

  private EntryStore entryStore;
  
  @Before
  public void setUp() {
    entryStore = new InMemoryEntryStore();
  }
  
  @Test
  public void testAddEntry() {
    BaseContentEntry<?> entry1 = new WebPageEntry();
    boolean thrown = false;
    try {
      entryStore.addEntry(entry1);
    } catch (IllegalArgumentException e) {
      thrown = true;
    }
    assertTrue(thrown);
    entry1 = getNewEntry("entry1");
    entryStore.addEntry(entry1);
    BaseContentEntry<?> entry2 = getNewEntry("entry1");
    thrown = false;
    try {
      entryStore.addEntry(entry2);
    } catch (IllegalArgumentException e) {
      thrown = true;
    }
    assertTrue(thrown);
    entry2.setId("entry2");
    entryStore.addEntry(entry2);
  }
  
  @Test
  public void testGetEntry() {
    assertNull(entryStore.getEntry("entry1"));
    BaseContentEntry<?> entry1 = getNewEntry("entry1");
    BaseContentEntry<?> entry2 = getNewEntry("entry2", "entry1");
    BaseContentEntry<?> entry3 = getNewEntry("entry3", "entry2");
    entryStore.addEntry(entry1);
    entryStore.addEntry(entry2);
    entryStore.addEntry(entry3);
    assertEquals(entry1, entryStore.getEntry("entry1"));
    assertEquals(entry2, entryStore.getEntry("entry2"));
    assertEquals(entry3, entryStore.getEntry("entry3")); 
    assertNull(entryStore.getEntry("entry4"));
  }
  
  @Test
  public void testGetChildren() {
    assertTrue(entryStore.getChildren("entry1").isEmpty());
    BaseContentEntry<?> entry1 = getNewEntry("entry1");
    BaseContentEntry<?> entry2 = getNewEntry("entry2", "entry1");
    BaseContentEntry<?> entry3 = getNewEntry("entry3", "entry1");
    BaseContentEntry<?> entry4 = getNewEntry("entry4", "entry2");
    entryStore.addEntry(entry1);
    entryStore.addEntry(entry2);
    entryStore.addEntry(entry3);
    entryStore.addEntry(entry4);
    Collection<BaseContentEntry<?>> children1 = entryStore.getChildren("entry1");
    assertEquals(2, children1.size());
    assertTrue(children1.contains(entry2));
    assertTrue(children1.contains(entry3));
    Collection<BaseContentEntry<?>> children2 = entryStore.getChildren("entry2");
    assertEquals(1, children2.size());
    assertTrue(children2.contains(entry4));
    assertTrue(entryStore.getChildren("entry3").isEmpty());
    assertTrue(entryStore.getChildren("entry4").isEmpty());
  }
  
  @Test
  public void testGetName() {
    assertNull(entryStore.getName("entry1"));
    entryStore.addEntry(getNewEntry("entry1"));
    assertEquals("-", entryStore.getName("entry1"));
    entryStore.addEntry(getNewEntry("entry2"));
    assertEquals("-2", entryStore.getName("entry2"));
    entryStore.addEntry(getNewEntry("entry3"));
    assertEquals("-3", entryStore.getName("entry3"));
    entryStore.addEntry(getNewEntry("entry4", "entry1"));
    assertEquals("-", entryStore.getName("entry4"));
    entryStore.addEntry(getNewEntry("entry5", "entry1"));
    assertEquals("-2", entryStore.getName("entry5"));
    entryStore.addEntry(getNewEntry("entry6", "entry1", "hey&*3_^^\"t"));
    assertEquals("hey-3_-t", entryStore.getName("entry6"));
    entryStore.addEntry(getNewEntry("entry7", "entry1", "hey-3_-t"));
    assertEquals("hey-3_-t-2", entryStore.getName("entry7"));
    entryStore.addEntry(getNewEntry("entry8", "entry1", "hey-3_-t-2"));
    assertEquals("hey-3_-t-2-2", entryStore.getName("entry8"));
    entryStore.addEntry(getNewEntry("entry9", "entry1", "hey-3_-t-1"));
    assertEquals("hey-3_-t-1", entryStore.getName("entry9"));
  }
  
  private BaseContentEntry<?> getNewEntry(String id) {
    return getNewEntry(id, null, "");
  }
  
  private BaseContentEntry<?> getNewEntry(String id, String parentId) {
    return getNewEntry(id, parentId, "");
  }
  
  private BaseContentEntry<?> getNewEntry(String id, String parentId,
      String title) {
    WebPageEntry entry = new WebPageEntry();
    entry.setId(id);
    if (parentId != null) {
      entry.addLink(SitesLink.Rel.PARENT, ILink.Type.ATOM, parentId);
    }
    if (title != null) {
      entry.setTitle(new PlainTextConstruct(title));
    }
    return entry;
  }
}
