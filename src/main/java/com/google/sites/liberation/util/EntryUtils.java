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

import com.google.gdata.data.ILink;
import com.google.gdata.data.Link;
import com.google.gdata.data.XhtmlTextConstruct;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.SitesLink;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides utility methods for dealing with BaseContentEntry's.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
public class EntryUtils {

  private static final Logger LOGGER = Logger.getLogger(
      EntryUtils.class.getCanonicalName());
  
  /**
   * Returns the id given by the given entry's parent link, or null if it has
   * no parent link.
   */
  public static String getParentId(BaseContentEntry<?> entry) {
    Link link = entry.getLink(SitesLink.Rel.PARENT, ILink.Type.ATOM);
    if (link == null) {
      return null;
    }
    return link.getHref();
  }
  
  /**
   * Sets the parent link of the given entry to the given id
   */
  public static void setParentId(BaseContentEntry<?> entry, String id) {
    entry.addLink(SitesLink.Rel.PARENT, ILink.Type.ATOM, id);
  }
  
  /**
   * Returns the given entry's content as a String.
   */
  public static String getContent(BaseContentEntry<?> entry) {
    try {
      return ((XhtmlTextConstruct)(entry.getTextContent().getContent()))
          .getXhtml().getBlob();
    } catch(IllegalStateException e) {
      LOGGER.log(Level.WARNING, "Invalid Content", e);
      return "";
    } catch(ClassCastException e) {
      LOGGER.log(Level.WARNING, "Invalid Content", e);
      return "";
    } catch(NullPointerException e) {
      LOGGER.log(Level.WARNING, "Invalid Content", e);
      return "";
    }
  }
  
}
