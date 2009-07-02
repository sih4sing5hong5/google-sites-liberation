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

import com.google.common.base.Preconditions;
import com.google.gdata.client.sites.ContentQuery;
import com.google.gdata.data.ILink;
import com.google.gdata.data.Link;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.SitesLink;
import com.google.sites.liberation.renderers.PageRenderer;
import com.google.sites.liberation.renderers.PageRendererFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * This class exports an entire site to a given root folder.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
public final class SiteExporter {

  private URL feedUrl;
  private EntryStore entryStore;
  
  /**
   * Creates a new SiteExporter for the given feedUrl
   */
  public SiteExporter(URL feedUrl) {
    Preconditions.checkNotNull(feedUrl);
    this.feedUrl = feedUrl;
    this.entryStore = new InMemoryEntryStore();
  }
  
  /**
   * Exports this site to a root folder given by {@code path}.
   * 
   * @param path the path to the root folder for the exported site.
   * @return true if the export succeeds, false otherwise.
   */
  public boolean export(String path) {
    try {
      ContentQuery query = new ContentQuery(feedUrl);
      Set<String> pageIds = new HashSet<String>();
      for(BaseContentEntry<?> entry : new ContinuousContentFeed(query)) {
        entryStore.addEntry(entry);
        if (EntryType.isPage(entry)) {
          pageIds.add(entry.getId());
        }
      }
      for(String id : pageIds) {
        BaseContentEntry<?> entry = entryStore.getEntry(id);
        String fullPath = path + getPath(entry);
        (new File(fullPath)).mkdirs();
        PageRenderer renderer = PageRendererFactory.getPageRenderer(entry, 
            entryStore);
        PageExporter exporter = new PageExporter(renderer);
        exporter.export(fullPath + PageExporter.getNiceTitle(entry) + ".html");
      }
      return true;
    } catch(Exception e) {
      return false;
    }
  }
  
  private String getPath(BaseContentEntry<?> entry) {
    Preconditions.checkNotNull(entry);
	Link parentLink = entry.getLink(SitesLink.Rel.PARENT, ILink.Type.ATOM);
	if (parentLink == null) {
	  return "";
	}
	BaseContentEntry<?> parent = entryStore.getEntry(parentLink.getHref());
    return getPath(parent) + PageExporter.getNiceTitle(parent) + "/";
  }
  
  public static void main(String[] args) throws MalformedURLException {
    URL feedUrl = new URL("http://bsimon-chi.chi.corp.google.com:7000/feeds/content/site/test/");
    String path = "/home/bsimon/Desktop/test/";
    SiteExporter exporter = new SiteExporter(feedUrl);
    exporter.export(path);
  }
}
