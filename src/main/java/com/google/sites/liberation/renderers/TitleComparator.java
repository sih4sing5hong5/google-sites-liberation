package com.google.sites.liberation.renderers;

import com.google.gdata.data.sites.BaseContentEntry;

import java.util.Comparator;

/**
 * Compares BaseContentEntry's based on their titles.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
class TitleComparator implements Comparator<BaseContentEntry<?>> {
  
  /**
   * Returns a positive integer if {@code e1}'s title comes after {@code e2}'s
   * title alphabetically.
   */
  @Override
  public int compare(BaseContentEntry<?> e1, BaseContentEntry<?> e2) {
    return e1.getTitle().getPlainText().compareTo(e2.getTitle().getPlainText());
  }
}
