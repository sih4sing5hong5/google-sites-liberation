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

package com.google.sites.liberation.util;

import static org.junit.Assert.*;

import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author bsimon@google.com (Benjamin Simon)
 */
public class UrlUtilsTest {

  @Test
  public void testGetFeedUrl() throws MalformedURLException {
    String host = "sites.google.com";
    String domain = "google.com";
    String webspace = "dataliberation";
    assertEquals(new URL("https://sites.google.com/feeds/content/google.com" +
    		"/dataliberation"), UrlUtils.getFeedUrl(host, domain, webspace));
    
    domain = null;
    assertEquals(new URL("https://sites.google.com/feeds/content/site" +
    		"/dataliberation"), UrlUtils.getFeedUrl(host, domain, webspace));
  }
  
  @Test
  public void testGetSiteUrl() throws MalformedURLException {
    String host = "sites.google.com";
    String domain = "google.com";
    String webspace = "dataliberation";
    assertEquals(new URL("https://sites.google.com/a/google.com/dataliberation"),
        UrlUtils.getSiteUrl(host, domain, webspace));
    
    domain = null;
    assertEquals(new URL("https://sites.google.com/site/dataliberation"),
        UrlUtils.getSiteUrl(host, domain, webspace));
  }
}
