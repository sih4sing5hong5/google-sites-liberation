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

package com.google.sites.liberation.util;

import static org.junit.Assert.*;

import com.google.common.collect.Sets;
import com.google.gdata.data.sites.BasePageEntry;
import com.google.gdata.data.sites.WebPageEntry;

import org.junit.Before;
import org.junit.Test;

/**
 * @author bsimon@google.com (Benjamin Simon)
 */
public class InMemoryEntryTreeTest {
  
  private BasePageEntry<?> root;
  private EntryTree entryTree;
  
  @Before
  public void before() {
    root = new WebPageEntry();
    entryTree = new InMemoryEntryTree(root);
  }
  
  @Test
  public void testConstructor() {
    assertEquals(root, entryTree.getRoot());
    assertEquals(entryTree.getChildren(root), Sets.newHashSet());
    assertNull(entryTree.getParent(root));
    
    try {
      new InMemoryEntryTree(null);
      fail("Should throw NullPointerException!");
    } catch(NullPointerException e) {}
  }
  
  @Test
  public void testAddEntry() {
    BasePageEntry<?> entry1 = new WebPageEntry();
    BasePageEntry<?> entry2 = new WebPageEntry();
    
    try {
      entryTree.addEntry(entry2, entry1);
      fail("Should throw IllegalStateException!");
    } catch (IllegalStateException e) {
      entryTree.addEntry(entry1, root);
      entryTree.addEntry(entry2, entry1);
    }
  }
  
  @Test
  public void testGetParent() {
    BasePageEntry<?> child1 = new WebPageEntry();
    BasePageEntry<?> child2 = new WebPageEntry();
    BasePageEntry<?> child3 = new WebPageEntry();
    BasePageEntry<?> grandchild1 = new WebPageEntry();
    BasePageEntry<?> grandchild2 = new WebPageEntry();
    BasePageEntry<?> grandchild3 = new WebPageEntry();
    entryTree.addEntry(child1, root);
    entryTree.addEntry(child2, root);
    entryTree.addEntry(child3, root);
    entryTree.addEntry(grandchild1, child1);
    entryTree.addEntry(grandchild2, child1);
    entryTree.addEntry(grandchild3, child2);
    
    assertEquals(root, entryTree.getParent(child1));
    assertEquals(root, entryTree.getParent(child2));
    assertEquals(root, entryTree.getParent(child3));
    assertEquals(child1, entryTree.getParent(grandchild1));
    assertEquals(child1, entryTree.getParent(grandchild2));
    assertEquals(child2, entryTree.getParent(grandchild3));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void testGetChildren() {
    BasePageEntry<?> child1 = new WebPageEntry();
    BasePageEntry<?> child2 = new WebPageEntry();
    BasePageEntry<?> child3 = new WebPageEntry();
    BasePageEntry<?> grandchild1 = new WebPageEntry();
    BasePageEntry<?> grandchild2 = new WebPageEntry();
    BasePageEntry<?> grandchild3 = new WebPageEntry();
    entryTree.addEntry(child1, root);
    entryTree.addEntry(child2, root);
    entryTree.addEntry(child3, root);
    entryTree.addEntry(grandchild1, child1);
    entryTree.addEntry(grandchild2, child1);
    entryTree.addEntry(grandchild3, child2);
  
    assertEquals(Sets.newHashSet(child1, child2, child3), 
        entryTree.getChildren(root));
    assertEquals(Sets.newHashSet(grandchild1, grandchild2), 
        entryTree.getChildren(child1));
    assertEquals(Sets.newHashSet(grandchild3), entryTree.getChildren(child2));
    assertEquals(0, entryTree.getChildren(child3).size());
    assertEquals(0, entryTree.getChildren(grandchild1).size());
    assertEquals(0, entryTree.getChildren(grandchild2).size());
    assertEquals(0, entryTree.getChildren(grandchild3).size());
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void testAddSubTree() {
    BasePageEntry<?> child = new WebPageEntry();
    BasePageEntry<?> grandchild = new WebPageEntry();
    entryTree.addEntry(child, root);
    entryTree.addEntry(grandchild, child);
    
    BasePageEntry<?> subRoot = new WebPageEntry();
    EntryTree subTree = new InMemoryEntryTree(subRoot);
    BasePageEntry<?> subChild1 = new WebPageEntry();
    BasePageEntry<?> subChild2 = new WebPageEntry();
    BasePageEntry<?> subGrandchild = new WebPageEntry();
    subTree.addEntry(subChild1, subRoot);
    subTree.addEntry(subChild2, subRoot);
    subTree.addEntry(subGrandchild, subChild1);
    
    entryTree.addSubTree(subTree, child);
    
    assertEquals(child, entryTree.getParent(subRoot));
    assertEquals(Sets.newHashSet(grandchild, subRoot), 
        entryTree.getChildren(child));
    assertEquals(subRoot, entryTree.getParent(subChild1));
    assertEquals(subRoot, entryTree.getParent(subChild2));
    assertEquals(Sets.newHashSet(subChild1, subChild2), 
        entryTree.getChildren(subRoot));
    assertEquals(subChild1, entryTree.getParent(subGrandchild));
    assertEquals(Sets.newHashSet(subGrandchild), 
        entryTree.getChildren(subChild1));
    assertEquals(0, entryTree.getChildren(subChild2).size());
    assertEquals(0, entryTree.getChildren(subGrandchild).size());
  }
}
