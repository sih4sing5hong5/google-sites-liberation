package com.google.sites.liberation.imprt;

import com.google.gdata.client.sites.SitesService;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.inject.ImplementedBy;

import java.net.URL;

/**
 * Inserts individual entries to a feed.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
@ImplementedBy(EntryInserterImpl.class)
public interface EntryInserter {

  /**
   * Inserts the given entry at the given URL and returns the entry returned
   * from the server or null if it is unable to do the insert.
   */
  BaseContentEntry<?> insertEntry(BaseContentEntry<?> entry, 
      URL feedUrl, SitesService sitesService);
}
