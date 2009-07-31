package com.google.sites.liberation.imprt;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.gdata.data.sites.BasePageEntry;
import com.google.inject.Inject;
import com.google.sites.liberation.util.EntryTree;

import java.io.File;
import java.net.URL;

/**
 * Implements {@link SiteImporter} to import an entrire site.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
final class SiteImporterImpl implements SiteImporter {

  private final EntryTreeUploader entryTreeUploader;
  private final PageImporter pageImporter;
  
  /**
   * Creates a new SiteImporterImpl with the given dependencies.
   */
  @Inject
  SiteImporterImpl(EntryTreeUploader entryTreeUploader,
      PageImporter pageImporter) {
    this.entryTreeUploader = checkNotNull(entryTreeUploader);
    this.pageImporter = checkNotNull(pageImporter);
  }
  
  @Override
  public void importSite(File rootDirectory, URL feedUrl, 
      EntryUploader entryUploader) {
    for(File subDirectory : rootDirectory.listFiles()) {
      if (subDirectory.isDirectory()) {
        EntryTree entryTree = importPage(subDirectory);
        if (entryTree != null) {
          entryTreeUploader.uploadEntryTree(entryTree, feedUrl, entryUploader);
        }
      }
    }
  }
  
  /**
   * Returns an EntryTree representing the page in the given directory
   * and all its children.
   */
  private EntryTree importPage(File pageDirectory) {
    File page = new File(pageDirectory, "index.html");
    if (!page.isFile()) {
      return null;
    }
    EntryTree entryTree = pageImporter.importPage(page);
    if (entryTree == null) {
      return null;
    }
    for(File subDirectory : pageDirectory.listFiles()) {
      if (subDirectory.isDirectory()) {
        EntryTree subTree = importPage(subDirectory);
        if (subTree != null) {
          entryTree.addSubTree(subTree, 
              (BasePageEntry<?>) entryTree.getRoot());
        }
      }
    }
    return entryTree;
  }
}
