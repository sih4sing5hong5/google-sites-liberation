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

package com.google.sites.liberation.imprt;

import static com.google.sites.liberation.util.EntryUtils.getParentId;
import static org.junit.Assert.*;

import com.google.gdata.data.DateTime;
import com.google.gdata.data.sites.BasePageEntry;
import com.google.gdata.data.sites.WebPageEntry;
import com.google.sites.liberation.imprt.EntryUploader;
import com.google.sites.liberation.util.EntryTree;
import com.google.sites.liberation.util.InMemoryEntryTreeFactory;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

public class EntryTreeUploaderImplTest {

  private Mockery context;
  private EntryUploader entryUploader;
  private BasePageEntry<?> root;
  private EntryTree entryTree;
  private EntryTreeUploader entryTreeUploader;
  
  @Before
  public void before() {
    context = new JUnit4Mockery() {{
      setImposteriser(ClassImposteriser.INSTANCE);
    }};
    entryUploader = context.mock(EntryUploader.class);
    root = getEntry("root");
    entryTree = new InMemoryEntryTreeFactory().getEntryTree(root);
    entryTreeUploader = new EntryTreeUploaderImpl();
  }
  
  @Test
  public void testJustRoot() {
    context.checking(new Expectations() {{
      oneOf (entryUploader).uploadEntry(root, entryTree);
        will(returnValue(root));
    }});
    
    entryTreeUploader.uploadEntryTree(entryTree, entryUploader);
  }
  
  @Test
  public void testFullTree() {
    final BasePageEntry<?> child1 = getEntry("child1");
    final BasePageEntry<?> child2 = getEntry("child2");
    final BasePageEntry<?> child3 = getEntry("child3");
    final BasePageEntry<?> grandchild1 = getEntry("grandchild1");
    final BasePageEntry<?> grandchild2 = getEntry("grandchild2");
    final BasePageEntry<?> grandchild3 = getEntry("grandchild3");
    entryTree.addEntry(child1, root);
    entryTree.addEntry(child2, root);
    entryTree.addEntry(child3, root);
    entryTree.addEntry(grandchild1, child1);
    entryTree.addEntry(grandchild2, child1);
    entryTree.addEntry(grandchild3, child2);
    
    context.checking(new Expectations() {{
      oneOf (entryUploader).uploadEntry(root, entryTree);
        will(returnValue(root));
      oneOf (entryUploader).uploadEntry(child1, entryTree);
        will(returnValue(child1));
      oneOf (entryUploader).uploadEntry(child2, entryTree);
        will(returnValue(child2));
      oneOf (entryUploader).uploadEntry(child3, entryTree);
        will(returnValue(child3));
      oneOf (entryUploader).uploadEntry(grandchild1, entryTree);
        will(returnValue(grandchild1));
      oneOf (entryUploader).uploadEntry(grandchild2, entryTree);
        will(returnValue(grandchild2));
      oneOf (entryUploader).uploadEntry(grandchild3, entryTree);
        will(returnValue(grandchild3));
    }});
    
    entryTreeUploader.uploadEntryTree(entryTree, entryUploader);
    assertEquals(getParentId(child1), "root");
    assertEquals(getParentId(child2), "root");
    assertEquals(getParentId(child3), "root");
    assertEquals(getParentId(grandchild1), "child1");
    assertEquals(getParentId(grandchild2), "child1");
    assertEquals(getParentId(grandchild3), "child2");
  }
  
  private BasePageEntry<?> getEntry(String id) {
    BasePageEntry<?> entry = new WebPageEntry();
    entry.setId(id);
    org.joda.time.DateTime dateTime = new org.joda.time.DateTime(
        (long) (Math.random()*1000));
    entry.setUpdated(DateTime.parseDateTime(dateTime.toString()));
    return entry;
  }
} 
