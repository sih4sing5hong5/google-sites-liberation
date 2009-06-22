package com.google.sites.liberation;

import com.google.gdata.data.BaseEntry;
import com.google.gdata.data.sites.SitesLink;
import com.google.gdata.client.sites.SitesService;
import com.google.gdata.util.ServiceException;
import com.google.gdata.data.sites.ContentFeed;
import com.google.common.base.Preconditions;

import java.util.Iterator;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;

public class SiteExporter {

  private String path;
  
  public SiteExporter(URL feedUrl, String path) throws IOException, ServiceException {
    Preconditions.checkNotNull(feedUrl, "entries");
    Preconditions.checkNotNull(path, "path");
    SitesService service = new SitesService("google-sites-export");
    this.path = path;
    Iterable<BaseEntry> entries = new ContentIterable(service, feedUrl);
    for (BaseEntry entry : entries) {
      System.out.println(entry);
      EntryType type = EntryType.getType(entry);
      if(EntryType.isPage(type)) {
    	PageExporter exporter = new PageExporter(entry, entries);
    	BufferedWriter out = new BufferedWriter(new FileWriter(getFileName(entry, entries)
    			+ ".html"));
    	out.write(exporter.getXhtml());
    	out.close();
      }
    }
  }
  
  private String getFileName(BaseEntry entry, Iterable<BaseEntry> entries) {
	Preconditions.checkNotNull(entries);
	if(entry == null) {
	  return path; 
	}
	String parentLink = entry.getLink(SitesLink.Rel.PARENT, 
		SitesLink.Type.APPLICATION_XHTML_XML).getHref();
	BaseEntry<?> parent = null;
	for(Iterator<BaseEntry> itr = entries.iterator(); itr.hasNext() && parent==null;) {
	  BaseEntry e = itr.next();
	  if(e.getId().equals(parentLink))
		 parent = e;
	}
    return getFileName(parent, entries) + "/" + entry.getTitle().getPlainText();
  }
  
  public static void main(String[] args) throws MalformedURLException, IOException, ServiceException {
    URL feedUrl = new URL("http://bsimon-chi.chi.corp.google.com:7000/feeds/content/site/test2?prettyprint=true");
    String path = "/home/bsimon/Desktop/test2/";
    new SiteExporter(feedUrl, path);
  }
}
