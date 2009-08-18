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

package com.google.sites.liberation.export;

import static org.junit.Assert.*;

import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.WebPageEntry;
import com.google.sites.liberation.export.EntryStore;
import com.google.sites.liberation.export.InMemoryEntryStore;
import com.google.sites.liberation.util.EntryUtils;

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
      EntryUtils.setParentId(entry, parentId);
    }
    if (title != null) {
      entry.setTitle(new PlainTextConstruct(title));
    }
    return entry;
  }
}
