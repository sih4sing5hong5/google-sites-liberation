package com.google.sites.liberation;

import com.google.gdata.data.sites.BaseEditableContentEntry;
import java.util.Collection;

/**
 * This interface defines a data structure that can be used to store the
 * entries of an entire content feed so that the entries can later be fetched 
 * on demand. Most importantly, this allows the children of a particular entry 
 * to be fetched without re-querying the content feed for all entries and 
 * finding those with the correct parent.
 * 
 * @author bsimon@google.com (Ben Simon)
 *
 */
public interface EntryStore {

  /**
   * Stores the given content entry for later retrieval
   * @param e entry to be stored
   */
  public void storeEntry(BaseEditableContentEntry<?> e);
  
  /**
   * Retrieves the entry with the given <code>id</code>
   * @param id the <code>atom:id</code> element of the entry
   * @return entry with the given <code>id</code>
   */
  public BaseEditableContentEntry<?> getEntry(String id);
  
  /**
   * Returns the id's of the entries of the given <code>type</code> whose parent 
   * has the given <code>id</code>
   * @param parentId the <code>atom:id</code> element of the entries' parent
   * @param type the <code>EntryType</code> of children to be retrieved
   * @return a collection of <code>Strings</code>, the id's of the children
   */
  public Collection<String> getChildren(String parentId, EntryType type);
  
  /**
   * Returns the id's of all entries that represent a page in the site
   * @return a collection of <code>Strings</code>, the id's of the page entries
   */
  public Collection<String> getPages();
  
}