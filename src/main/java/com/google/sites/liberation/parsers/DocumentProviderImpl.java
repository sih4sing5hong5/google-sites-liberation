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

package com.google.sites.liberation.parsers;

import com.google.inject.Inject;

import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Parses a file into an org.w3c.Document.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
final class DocumentProviderImpl implements DocumentProvider {

  private DocumentBuilder docBuilder;
  
  @Inject
  DocumentProviderImpl() {
    try {
      docBuilder = DocumentBuilderFactory.newInstance()
          .newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      throw new RuntimeException(e);
    }
  }
  
  @Override
  public Document getDocument(File file) throws IOException {
    try {
      return docBuilder.parse(file);
    } catch (SAXException e) {
      return useJTidy(file);
    }
  }
  
  private Document useJTidy(File file) throws IOException {
    File xmlFile = new File(file.getParentFile(), "index.xml");
    Tidy tidy = new Tidy();
    tidy.setXHTML(true);
    tidy.parse(new FileInputStream(file), new FileOutputStream(xmlFile));
    Document document = getDocument(xmlFile);
    xmlFile.deleteOnExit();
    return document;
  }
}
