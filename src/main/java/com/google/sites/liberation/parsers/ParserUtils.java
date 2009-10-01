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

import static com.google.common.base.Preconditions.checkNotNull;

import org.w3c.dom.Element;

/**
 * Contains utility methods for parsing.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
final class ParserUtils {

  /**
   * Returns whether or not the given element has the given class.
   */
  static boolean hasClass(Element element, String cls) {
    checkNotNull(element);
    checkNotNull(cls);
    for (String str : element.getAttribute("class").split(" ")) {
      if (str.equals(cls)) {
        return true;
      }
    }
    return false;
  }
}
