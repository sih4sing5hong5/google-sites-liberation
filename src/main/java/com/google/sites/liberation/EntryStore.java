package com.google.sites.liberation;

import com.google.gdata.data.sites.BaseContentEntry;

import java.util.Collection;

/**
 * This interface defines a data structure that can be used to store the
 * entries of a content feed so that the entries can be later fetched 
 * by id or parent id.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
public interface EntryStore {

  /**
   * Stores the given content entry.
   */
  public void addEntry(BaseContentEntry<?> entry);
  
  /**
   * Retrieves the entry with the given {@code id} or {@code null} if there is
   * no such entry.
   */
  public BaseContentEntry<?> getEntry(String id);
  
  /**
   * Returns a collection containing all entries with parent specified by the 
   * given {@code id}.
   */
  public Collection<BaseContentEntry<?>> getChildren(String id);
  
}
