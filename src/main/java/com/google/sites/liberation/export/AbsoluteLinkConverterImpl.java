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

package com.google.sites.liberation.export;

import static com.google.sites.liberation.util.EntryUtils.getXhtmlContent;
import static com.google.sites.liberation.util.EntryUtils.setContent;

import com.google.gdata.data.sites.BasePageEntry;

import java.net.URL;

/**
 * Converts the absolute links in a page's content to relative links.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
final class AbsoluteLinkConverterImpl implements AbsoluteLinkConverter {

  @Override
  public void convertLinks(BasePageEntry<?> entry, EntryStore entryStore,
      URL siteUrl, boolean isRevision) {
    convertLinks(entry, entryStore, siteUrl, isRevision, "href=\"", "\"");
    convertLinks(entry, entryStore, siteUrl, isRevision, "href='", "'");
  }
  
  private void convertLinks(BasePageEntry<?> entry, EntryStore entryStore, 
      URL siteUrl, boolean isRevision, String prefix, String suffix) {
    String content = getXhtmlContent(entry);
    String url = siteUrl.toExternalForm();
    String siteRoot = getSiteRoot(entry, entryStore);
    if (isRevision) {
      siteRoot += "../";
    }
    int index = content.indexOf(prefix + url);
    while (index != -1) {
      int startIndex = index + prefix.length();
      int endIndex = content.indexOf(suffix, startIndex + 1);
      String beforeLink = content.substring(0, startIndex);
      String link = content.substring(startIndex + url.length() + 1, endIndex);
      String afterLink = content.substring(endIndex);
      content = beforeLink + siteRoot + link + "/index.html" + afterLink;
      index = content.indexOf(prefix + url);
    }
    setContent(entry, content);
  }
  
  private String getSiteRoot(BasePageEntry<?> entry, EntryStore entryStore) {
    BasePageEntry<?> parent = entryStore.getParent(entry.getId());
    if (parent == null) {
      return "../";
    }
    return getSiteRoot(parent, entryStore) + "../";
  }
}
