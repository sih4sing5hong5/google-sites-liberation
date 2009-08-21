package com.google.sites.liberation.imprt;

import com.google.gdata.data.sites.BasePageEntry;
import com.google.inject.ImplementedBy;

import java.net.URL;
import java.util.List;

/**
 * Converts relative links to absolute links.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
@ImplementedBy(RelativeLinkConverterImpl.class)
public interface RelativeLinkConverter {

  /**
   * Converts all of the relative links in the given entry with the given
   * ancestors, to absolute links starting with the given siteUrl. 
   */
  void convertLinks(BasePageEntry<?> entry, List<BasePageEntry<?>> ancestors, 
      URL siteUrl, boolean isRevision);
}
