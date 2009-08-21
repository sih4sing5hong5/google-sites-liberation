package com.google.sites.liberation.imprt;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.Lists;
import com.google.gdata.client.sites.SitesService;
import com.google.gdata.data.sites.BasePageEntry;
import com.google.gdata.util.common.base.Nullable;
import com.google.inject.Inject;
import com.google.sites.liberation.util.ProgressListener;
import com.google.sites.liberation.util.UrlUtils;

import java.io.File;
import java.net.URL;
import java.util.List;

/**
 * Implements {@link SiteImporter} to import an entire site.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
final class SiteImporterImpl implements SiteImporter {

  private final PageImporter pageImporter;
  
  /**
   * Creates a new SiteImporterImpl with the given dependencies.
   */
  @Inject
  SiteImporterImpl(PageImporter pageImporter) {
    this.pageImporter = checkNotNull(pageImporter);
  }
  
  @Override
  public void importSite(String host, @Nullable String domain, String webspace, 
      boolean importRevisions, SitesService sitesService, File rootDirectory, 
      ProgressListener progressListener) {
    URL feedUrl = UrlUtils.getFeedUrl(host, domain, webspace);
    URL siteUrl = UrlUtils.getSiteUrl(host, domain, webspace);
    
    progressListener.setStatus("Scanning directory.");
    int numPages = getNumPages(rootDirectory);
    List<BasePageEntry<?>> ancestors = Lists.newLinkedList();
    for (File subDirectory : rootDirectory.listFiles()) {
      if (subDirectory.isDirectory()
          && !subDirectory.getName().startsWith("_")) {
        importPage(subDirectory, importRevisions, ancestors, feedUrl, siteUrl, 
            sitesService, progressListener, numPages);
      }
    }
    progressListener.setProgress(1.0);
    progressListener.setStatus("Import complete.");
  }
  
  private void importPage(File pageDirectory, boolean importRevisions,
      List<BasePageEntry<?>> ancestors, URL feedUrl, URL siteUrl, 
      SitesService sitesService, ProgressListener progressListener, int numPages) {
    File file = new File(pageDirectory, "index.html");
    if (file.isFile()) {
      progressListener.setStatus("Importing page: " + pageDirectory.getName());
      BasePageEntry<?> page = pageImporter.importPage(pageDirectory, 
          importRevisions, ancestors, feedUrl, siteUrl, sitesService);
      progressListener.setProgress(progressListener.getProgress() + 1.0/numPages);
      if (page != null) {
        List<BasePageEntry<?>> newAncestors = Lists.newLinkedList(ancestors);
        newAncestors.add(page);
        for (File subDirectory : pageDirectory.listFiles()) {
          if (subDirectory.isDirectory() 
              && !subDirectory.getName().startsWith("_")) {
            importPage(subDirectory, importRevisions, newAncestors, feedUrl, 
                siteUrl, sitesService, progressListener, numPages);
          }
        }
      }
    }
  }
  
  private int getNumPages(File directory) {
    int num = 0;
    for (File subDirectory : directory.listFiles()) {
      if (subDirectory.isDirectory() 
          && !subDirectory.getName().startsWith("_")) {
        if (new File(subDirectory, "index.html").isFile()) {
          num += 1 + getNumPages(subDirectory);
        }
      }
    }
    return num;
  }
}
