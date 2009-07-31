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
import com.google.sites.liberation.util.EntryTree;

import org.w3c.dom.Element;

/**
 * Parses an html element for entries.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
@ImplementedBy(EntryParserImpl.class)
public interface EntryParser {

  /**
   * Returns an EntryTree containing the entry defined by this element as
   * its root, and non-page sub-entries as descendants.
   */
  EntryTree parseEntry(Element element);
}
