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

import com.google.inject.ImplementedBy;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Parses a file into an org.w3c.Document.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
@ImplementedBy(DocumentProviderImpl.class)
public interface DocumentProvider {

  /**
   * Returns a Document from the given File.
   */
  Document getDocument(File file) throws ParserConfigurationException, 
      SAXException, IOException;
}
