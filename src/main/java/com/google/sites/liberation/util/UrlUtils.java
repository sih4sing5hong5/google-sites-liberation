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

import com.google.gdata.util.common.base.Nullable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides utility methods for dealing with URL's.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
public class UrlUtils {

  private static final Logger LOGGER = Logger.getLogger(
      UrlUtils.class.getCanonicalName());  
  
  /**
   * Returns the site URL corresponding to the given host, domain, and webspace.
   */
  public static URL getSiteUrl(String host, @Nullable String domain, 
      String webspace) {
    try {
      if (domain == null) {
        return new URL("https://" + host + "/site/" + webspace);
      } else {
        return new URL("https://" + host + "/a/" + domain + "/" + webspace);
      }
    } catch (MalformedURLException e) {
      LOGGER.log(Level.WARNING, "Invalid host, domain, or webspace!");
      throw new RuntimeException(e);
    }
  }
  
  /**
   * Returns the feed URL corresponding to the given host, domain, and webspace.
   */
  public static URL getFeedUrl(String host, @Nullable String domain,
      String webspace) {
    try {
      if (domain == null) {
        return new URL("https://" + host + "/feeds/content/site/" + webspace);
      } else {
        return new URL("https://" + host + "/feeds/content/" + domain + "/" 
            + webspace);
      }
    } catch (MalformedURLException e) {
      LOGGER.log(Level.WARNING, "Invalid host, domain, or webspace!");
      throw new RuntimeException(e);
    }
  }
}
