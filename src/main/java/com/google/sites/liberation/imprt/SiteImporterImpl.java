package com.google.sites.liberation.imprt;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.gdata.data.sites.BasePageEntry;
import com.google.gdata.data.sites.PageName;
import com.google.inject.Inject;
import com.google.sites.liberation.util.EntryTree;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Implements {@link SiteImporter} to import an entrire site.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
final class SiteImporterImpl implements SiteImporter {

  private static final Logger LOGGER = Logger.getLogger(
      SiteImporterImpl.class.getCanonicalName());
  
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
    Document document;
    try {
      DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance()
          .newDocumentBuilder();
      document = docBuilder.parse(page);
    } catch (IOException e) {
      String message = "Error importing from file: " + page.getName();
      LOGGER.log(Level.WARNING, message, e);
      return null;
    } catch (ParserConfigurationException e) {
      String message = "Error importing from file: " + page.getName();
      LOGGER.log(Level.WARNING, message, e);
      return null;
    } catch (SAXException e) {
      String message = "Error importing from file: " + page.getName();
      LOGGER.log(Level.WARNING, message, e);
      return null;
    }
    EntryTree entryTree = pageImporter.importPage(document);
    if (entryTree == null) {
      return null;
    }
    BasePageEntry<?> pageEntry = (BasePageEntry<?>) entryTree.getRoot();
    pageEntry.setPageName(new PageName(pageDirectory.getName()));
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
