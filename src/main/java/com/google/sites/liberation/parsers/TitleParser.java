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

import com.google.gdata.data.TextConstruct;
import com.google.inject.ImplementedBy;

import org.w3c.dom.Element;

/**
 * Parses an html element representing an entry title.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
@ImplementedBy(TitleParserImpl.class)
public interface TitleParser {

  /**
   * Returns a TextConstruct containing any title information in the given 
   * element. 
   */
  TextConstruct parseTitle(Element element);
}
