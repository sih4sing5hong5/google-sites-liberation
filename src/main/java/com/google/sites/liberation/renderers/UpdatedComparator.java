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
   * Orders two entries such that the more recently updated entry comes first.
   */
  @Override
  public int compare(BaseContentEntry<?> e1, BaseContentEntry<?> e2) {
    return e2.getUpdated().compareTo(e1.getUpdated());
  }
}
