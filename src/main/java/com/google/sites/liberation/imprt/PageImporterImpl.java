/*
 * Copyright (C) 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.sites.liberation.imprt;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.TextConstruct;
import com.google.gdata.data.sites.BasePageEntry;
import com.google.gdata.data.sites.PageName;
import com.google.gdata.data.sites.WebPageEntry;
import com.google.inject.Inject;
import com.google.sites.liberation.parsers.ContentParser;
import com.google.sites.liberation.parsers.EntryParser;
import com.google.sites.liberation.util.EntryTree;
import com.google.sites.liberation.util.EntryTreeFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Implements {@link PageImporter} to parse a single file as a page in a site.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
final class PageImporterImpl implements PageImporter {

  private static final Logger logger = Logger.getLogger(
      PageImporterImpl.class.getCanonicalName());
  
  private final ContentParser contentParser;
  private final EntryParser entryParser;
  private final EntryTreeFactory entryTreeFactory;
  
  /**
   * Creates a new PageImporterImpl with the given dependencies.
   */
  @Inject
  PageImporterImpl(ContentParser contentParser, 
      EntryParser elementParser,
      EntryTreeFactory entryTreeFactory) {
    this.contentParser = checkNotNull(contentParser);
    this.entryParser = checkNotNull(elementParser);
    this.entryTreeFactory = checkNotNull(entryTreeFactory);
  }
  
  @Override
  public EntryTree importPage(File file) {
    checkNotNull(file);
    Document document;
    EntryTree entryTree;
    try {
      DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance()
          .newDocumentBuilder();
      document = docBuilder.parse(file);
      entryTree = parseElement(document.getDocumentElement());
    } catch (IOException e) {
      String message = "Error importing from file: " + file.getName();
      logger.log(Level.WARNING, message, e);
      return null;
    } catch (ParserConfigurationException e) {
      String message = "Error importing from file: " + file.getName();
      logger.log(Level.WARNING, message, e);
      return null;
    } catch (SAXException e) {
      String message = "Error importing from file: " + file.getName();
      logger.log(Level.WARNING, message, e);
      return null;
    }
    if (entryTree == null) {
      NodeList nodeList = document.getElementsByTagName("body");
      if (nodeList.getLength() == 0) {
        String message = "Error importing from file: " + file.getName();
        logger.log(Level.WARNING, message);
        return null;
      }
      Element body = (Element) nodeList.item(0);
      TextConstruct content = contentParser.parseContent(body);
      WebPageEntry webPage = new WebPageEntry();
      webPage.setTitle(new PlainTextConstruct(""));
      webPage.setContent(content);
      entryTree = entryTreeFactory.getEntryTree(webPage);
    }
    String pageName = file.getParentFile().getName();
    BasePageEntry<?> page = (BasePageEntry<?>) entryTree.getRoot();
    page.setPageName(new PageName(pageName));
    return entryTree;
  }
  
  /**
   * Parses the given element, returning an EntryTree with the first entry
   * encountered as the root.
   */
  private EntryTree parseElement(Element element) {
    EntryTree entryTree = null;
    NodeList nodeList = element.getChildNodes();
    for(int i = 0; (i < nodeList.getLength()) && (entryTree == null); i++) {
      Node node = nodeList.item(i);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        Element child = (Element) node;
        boolean isEntry = false;
        for(String str : child.getAttribute("class").split(" ")) {
          if (str.equals("hentry")) {
            isEntry = true;
          }
        }
        if (isEntry) {
          entryTree = entryParser.parseEntry(child);
        } else {
          entryTree = parseElement(child);
        }
      }
    }
    return entryTree;
  }
}
