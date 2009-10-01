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

package com.google.sites.liberation.imprt;

import com.google.gdata.data.TextConstruct;
import com.google.gdata.data.XhtmlTextConstruct;
import com.google.gdata.data.sites.BasePageEntry;
import com.google.gdata.util.XmlBlob;
import com.google.sites.liberation.util.EntryUtils;

import java.net.URL;
import java.util.List;

/**
 * Converts relative links to absolute links.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
final class RelativeLinkConverterImpl implements RelativeLinkConverter {

  @Override
  public void convertLinks(BasePageEntry<?> entry, List<BasePageEntry<?>> ancestors, 
      URL siteUrl, boolean isRevision) {
    convertLinks(entry, ancestors, siteUrl, isRevision, "href=\"", "\"");
    convertLinks(entry, ancestors, siteUrl, isRevision, "href='", "'");
  }
  
  /**
   * Converts the relative links in the content of the given entry, where a 
   * link is defined by the given prefix and suffix. 
   */
  private void convertLinks(BasePageEntry<?> entry, 
      List<BasePageEntry<?>> ancestors, URL siteUrl, boolean isRevision,
      String prefix, String suffix) {
    String content = EntryUtils.getXhtmlContent(entry);
    String url = siteUrl.toExternalForm();
    int index = content.indexOf(prefix + "../");
    while (index != -1) {
      int startIndex = index + prefix.length();
      int endIndex = content.indexOf(suffix, startIndex);
      if (endIndex == -1) {
        break;
      }
      String link = content.substring(startIndex, endIndex);
      if (link.startsWith("../")) {
        if (isRevision) {
          link = link.substring(3);
        }
        int ancestorIndex = ancestors.size();
        while (link.startsWith("../") && ancestorIndex >= 0) {
          link = link.substring(3);
          ancestorIndex--;
        }
        String str = "";
        while (ancestorIndex >= 0 && ancestorIndex < ancestors.size()) {
          str = ancestors.get(ancestorIndex).getPageName().getValue() + "/" + str;
          ancestorIndex--;
        }
        link = str + link;
      }
      if (link.endsWith("/index.html")) {
        link = link.substring(0, link.lastIndexOf("/index.html"));
      }
      String beforeLink = content.substring(0, startIndex);
      String afterLink = content.substring(endIndex);
      content = beforeLink + url + "/" + link + afterLink;
      index = content.indexOf(prefix + "../");
    }
    XmlBlob blob = new XmlBlob();
    blob.setBlob(content);
    TextConstruct textConstruct = new XhtmlTextConstruct(blob);
    entry.setContent(textConstruct);
  }
}
