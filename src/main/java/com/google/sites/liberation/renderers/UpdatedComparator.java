package com.google.sites.liberation.renderers;

import com.google.gdata.data.sites.BaseContentEntry;

import java.util.Comparator;

/** 
 * Compares BaseContentEntry's based on when they were last updated.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
class UpdatedComparator implements Comparator<BaseContentEntry<?>> {
  
  /**
   * Returns a positive integer if {@code e1} was less recently updated than
   * {@code e2}.
   */
  @Override
  public int compare(BaseContentEntry<?> e1, BaseContentEntry<?> e2) {
    return e2.getUpdated().compareTo(e1.getUpdated());
  }
}
