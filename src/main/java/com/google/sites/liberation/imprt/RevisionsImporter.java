package com.google.sites.liberation.imprt;

import com.google.gdata.client.sites.SitesService;
import com.google.gdata.data.sites.BasePageEntry;
import com.google.inject.ImplementedBy;

import java.io.File;
import java.net.URL;
import java.util.List;

/**
 * Imports the revisions of page and uploads them to a feed.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
@ImplementedBy(RevisionsImporterImpl.class)
public interface RevisionsImporter {
  
  /**
   * Imports the revisions of the page with the given directory.
   */
  BasePageEntry<?> importRevisions(File directory, 
      List<BasePageEntry<?>> ancestors, URL feedUrl, URL siteUrl, 
      SitesService sitesService);
}
