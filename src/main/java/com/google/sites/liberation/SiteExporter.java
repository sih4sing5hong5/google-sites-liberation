package com.google.sites.liberation;

import com.google.gdata.data.BaseEntry;
import com.google.gdata.data.Link;
import com.google.gdata.data.sites.SitesLink;
import com.google.gdata.client.sites.ContentQuery;
import com.google.common.base.Preconditions;

import java.util.Set;
import java.util.HashSet;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;

public class SiteExporter {

  private String path;
  private Set<BaseEntry<?>> entries;
  
  public SiteExporter(URL feedUrl, String path) throws IOException {
    Preconditions.checkNotNull(feedUrl, "entries");
    Preconditions.checkNotNull(path, "path");
    this.path = path;
    entries = new HashSet<BaseEntry<?>>();
    ContentQuery query = new ContentQuery(feedUrl);
    ContinuousContentFeed feed = new ContinuousContentFeed(query);
    for(BaseEntry<?> entry : feed) {
      System.out.println(entry);
      entries.add(entry);
    }
    for (BaseEntry<?> entry : entries) {
      EntryType type = EntryType.getType(entry);
      if(EntryType.isPage(type)) {
        String fullPath = getFullPath(entry);
        (new File(fullPath)).mkdirs();
    	PageExporter exporter = new PageExporter(entry, entries);
    	BufferedWriter out = new BufferedWriter(new FileWriter(fullPath + 
    	    entry.getTitle().getPlainText() + ".html"));
    	out.write(exporter.getXhtml());
    	out.close();
      }
    }
  }
  
  private String getFullPath(BaseEntry<?> entry) {
    Preconditions.checkNotNull(entry);
	Link parentLink = entry.getLink(SitesLink.Rel.PARENT, 
		SitesLink.Type.APPLICATION_XHTML_XML);
	if(parentLink == null)
	  return path;
	BaseEntry<?> parent = null;
	for(BaseEntry<?> e : entries) {
	  if(e.getId().equals(parentLink.getHref()))
		 parent = e;
	}
    return getFullPath(parent) + parent.getTitle().getPlainText() + "/";
  }
  
  public static void main(String[] args) throws MalformedURLException, IOException {
    URL feedUrl = new URL("http://bsimon-chi.chi.corp.google.com:7000/feeds/content/site/test");
    String path = "/home/bsimon/Desktop/test/";
    new SiteExporter(feedUrl, path);
  }
}
