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

package com.google.sites.liberation;

import static org.junit.Assert.*;

import org.junit.Test;

public class HyperLinkTest {

  @Test
  public void testConstructor() {
    HyperLink link = new HyperLink("http://test.html", "test");
    assertEquals("<a href=\"http://test.html\">\ntest\n</a>\n", link.toString());
    link = new HyperLink("", "");
    assertEquals("<a href=\"\">\n\n</a>\n", link.toString());
  }
  
}
