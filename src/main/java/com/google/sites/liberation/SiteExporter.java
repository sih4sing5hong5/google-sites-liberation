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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.sites.liberation.EntryType.ATTACHMENT;
import static com.google.sites.liberation.EntryType.getType;
import static com.google.sites.liberation.EntryType.isPage;

import com.google.common.collect.Sets;
import com.google.gdata.client.sites.ContentQuery;
import com.google.gdata.client.sites.SitesService;
import com.google.gdata.data.ILink;
import com.google.gdata.data.Link;
import com.google.gdata.data.sites.AttachmentEntry;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.SitesLink;
import com.google.sites.liberation.renderers.PageRenderer;
import com.google.sites.liberation.renderers.PageRendererFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

  private final SitesService service;
  private final URL feedUrl;
  private final EntryStore entryStore;
  
  /**
   * Creates a new SiteExporter for the given feedUrl
   */
  public SiteExporter(SitesService service, URL feedUrl) {
    this.service = checkNotNull(service, "service");
    this.feedUrl = checkNotNull(feedUrl, "feedUrl");
    this.entryStore = new InMemoryEntryStore();
  }
  
  /**
   * Exports this site to a root folder given by {@code path}.
   * 
   * @param path the path to the root folder for the exported site.
   */
  public void export(String path) {
    ContentQuery query = new ContentQuery(feedUrl);
    Set<String> pageIds = Sets.newHashSet();
    Set<String> attachmentIds = new HashSet<String>();
    for(BaseContentEntry<?> entry : new ContinuousContentFeed(service, query)) {
      entryStore.addEntry(entry);
      if (isPage(entry)) {
        pageIds.add(entry.getId());
      } else if(getType(entry) == ATTACHMENT) {
        attachmentIds.add(entry.getId());
      }
    }
    for(String id : pageIds) {
      BaseContentEntry<?> entry = entryStore.getEntry(id);
      String fullPath = path + getPath(entry);
      new File(fullPath).mkdirs();
      PageRenderer renderer = PageRendererFactory.getPageRenderer(entry, 
          entryStore);
      PageExporter exporter = new PageExporter(entry, renderer);
      exporter.export(fullPath + entryStore.getName(id) + ".html");
    }
    for(String id : attachmentIds) {
      AttachmentEntry attachment = (AttachmentEntry) entryStore.getEntry(id);
      String fullPath = path + getPath(attachment);
      new File(fullPath).mkdirs();
      String fileName = fullPath + attachment.getTitle().getPlainText();
      downloadAttachment(attachment, fileName);
    }
  }
  
  private String getPath(BaseContentEntry<?> entry) {
    checkNotNull(entry);
	Link parentLink = entry.getLink(SitesLink.Rel.PARENT, ILink.Type.ATOM);
	if (parentLink == null) {
	  return "";
	}
	String parentId = parentLink.getHref();
	BaseContentEntry<?> parent = entryStore.getEntry(parentId);
    return getPath(parent) + entryStore.getName(parentId) + "/";
  }
  
  private void downloadAttachment(AttachmentEntry attachment, String fileName) {
    try {
      OutputStream out = new FileOutputStream(new File(fileName));
      URL url = new URL(attachment.getEnclosureLink().getHref());
      InputStream in = url.openStream();
      byte[] buf = new byte[4*1024];
      int bytesRead;
      while((bytesRead = in.read(buf)) != -1) {
        out.write(buf, 0, bytesRead);
      }
      out.close();
    } catch(IOException e) {
      System.err.println("Error reading from " + attachment.getEnclosureLink()
          .getHref() + "and/or writing to " + fileName);
      throw new RuntimeException(e);
    }
  }
  
  public static void main(String[] args) throws MalformedURLException {
    URL feedUrl = new URL("http://bsimon-chi.chi.corp.google.com:7000/feeds/" +
    	"content/site/test/");
    String path = "/home/bsimon/Desktop/test/";
    SitesService service = new SitesService("google-sites-liberation");
    try {
      //service.setUserCredentials("yourfriendben@gmail.com", "");
    } catch(Exception e) {
      e.printStackTrace();
    }
    SiteExporter exporter = new SiteExporter(service, feedUrl);
    exporter.export(path);
  }
}
