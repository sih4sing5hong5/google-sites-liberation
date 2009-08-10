package com.google.sites.liberation.imprt;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.sites.liberation.util.EntryType.isPage;

import com.google.gdata.data.TextConstruct;
import com.google.gdata.data.XhtmlTextConstruct;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.BasePageEntry;
import com.google.gdata.data.sites.PageName;
import com.google.gdata.util.XmlBlob;
import com.google.inject.Inject;
import com.google.sites.liberation.util.EntryTree;
import com.google.sites.liberation.util.EntryUtils;

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
  public void importSite(File rootDirectory, URL siteUrl, 
      EntryUploader entryUploader) {
    if (rootDirectory.isDirectory()) {
      for(File subDirectory : rootDirectory.listFiles()) {
        if (subDirectory.isDirectory()) {
          EntryTree entryTree = importPage(subDirectory);
          if (entryTree != null) {
            fixLinks(entryTree, siteUrl);
            System.out.println(entryTree.getRoot().getTitle().getPlainText());
            entryTreeUploader.uploadEntryTree(entryTree, entryUploader);
          }
        }
      }
    } else {
      LOGGER.log(Level.WARNING, "Invalid directory!");
      return;
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
      String message = "Error importing from " + pageDirectory.getPath();
      LOGGER.log(Level.WARNING, message, e);
      return null;
    } catch (ParserConfigurationException e) {
      String message = "Error importing from " + pageDirectory.getPath();
      LOGGER.log(Level.WARNING, message, e);
      return null;
    } catch (SAXException e) {
      String message = "Error importing from " + pageDirectory.getPath();
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
  
  /**
   * Converts all of the relative links in all of the entries in the given 
   * EntryTree to absolute links, assuming that the root of the EntryTree
   * exists at the given siteUrl.
   */
  private void fixLinks(EntryTree entryTree, URL siteUrl) {
    fixLinks(entryTree.getRoot(), entryTree, siteUrl, "href=\"", "\"");
    fixLinks(entryTree.getRoot(), entryTree, siteUrl, "href='", "'");
  }
  
  /**
   * Converts the relative links in the content of the given entry and
   * recursively on all of its children. A link is defined by the given
   * prefix and suffix. 
   */
  private void fixLinks(BaseContentEntry<?> entry, EntryTree entryTree,
      URL siteUrl, String prefix, String suffix) {
    if (!isPage(entry)) {
      return;
    }
    String content = EntryUtils.getContent(entry);
    String url = siteUrl.toExternalForm();
    int index = content.indexOf(prefix + "../");
    while(index != -1) {
      int startIndex = index + 6;
      int endIndex = content.indexOf(suffix, startIndex);
      if (endIndex == -1) {
        break;
      }
      String link = content.substring(startIndex, endIndex);
      if (link.startsWith("../")) {
        BasePageEntry<?> currentAncestor = (BasePageEntry<?>) entry;
        while(link.startsWith("../") && currentAncestor != null) {
          link = link.substring(3);
          currentAncestor = entryTree.getParent(currentAncestor);
        }
        String ancestors = "";
        while(currentAncestor != null) {
          ancestors = currentAncestor.getPageName().getValue() + 
              "/" + ancestors;
          currentAncestor = entryTree.getParent(currentAncestor);
        }
        link = ancestors + link;
      }
      if (link.endsWith("/index.html")) {
        link = link.substring(0, link.lastIndexOf("/index.html"));
      }
      String beforeLink = content.substring(0, startIndex);
      String afterLink = content.substring(endIndex);
      content = beforeLink + url + "/" + link + afterLink;
      index = content.indexOf(prefix + "../");
    }
    XmlBlob blob = new XmlBlob();
    blob.setBlob(content);
    TextConstruct textConstruct = new XhtmlTextConstruct(blob);
    entry.setContent(textConstruct);
    for (BaseContentEntry<?> child : entryTree.getChildren(entry)) {
      fixLinks(child, entryTree, siteUrl, prefix, suffix);
    }
  }
}
