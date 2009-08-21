package com.google.sites.liberation.parsers;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.sites.liberation.util.EntryType.isPage;
import static com.google.sites.liberation.parsers.ParserUtils.hasClass;

import com.google.common.collect.Lists;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.inject.Inject;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implements PageParser to parse an html element for any contained entries.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
final class PageParserImpl implements PageParser {
  
  private static final Logger LOGGER = Logger.getLogger(
      PageParserImpl.class.getCanonicalName());
  
  private final DocumentProvider documentProvider;
  private final EntryParser entryParser;
  
  /**
   * Creates a new PageParserImpl with the given dependencies.
   */
  @Inject
  PageParserImpl(DocumentProvider documentProvider,
      EntryParser entryParser) {
    this.documentProvider = checkNotNull(documentProvider);
    this.entryParser = checkNotNull(entryParser);
  }
  
  /**
   * Parses the given File, returning a list of all the entries within.
   */
  @Override
  public List<BaseContentEntry<?>> parsePage(File file) {
    Document document = null;
    try {
      document = documentProvider.getDocument(file);
    } catch (IOException e) {
      LOGGER.log(Level.WARNING, "Error parsing file: " + file);
      return null;
    }
    List<BaseContentEntry<?>> entries = Lists.newLinkedList();
    parseElement(document.getDocumentElement(), entries);
    for (BaseContentEntry<?> entry : entries) {
      if (isPage(entry) && entry.getTitle() == null) {
        NodeList nodeList = document.getElementsByTagName("title");
        for (int i = 0; i < nodeList.getLength(); i++) {
          Node node = nodeList.item(i);
          if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element title = (Element) node;
            entry.setTitle(new PlainTextConstruct(title.getTextContent()));
            System.out.println(entry);
          }
        }
      }
    }
    return entries;
  }
  
  private void parseElement(Element element, List<BaseContentEntry<?>> entries) {
    NodeList nodeList = element.getChildNodes();
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node node = nodeList.item(i);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        Element child = (Element) node;
        if (!child.getTagName().equals("q") && 
            !child.getTagName().equals("blockquote")) {
          if (hasClass(child, "hentry")) {
            entries.add(entryParser.parseEntry(child));
          }
          parseElement(child, entries);
        }
      }
    }
  }
}
