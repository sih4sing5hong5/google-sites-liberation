package com.google.sites.liberation;

import com.google.gdata.data.BaseEntry;
import com.google.gdata.data.Entry;
import com.google.gdata.data.ILink;
import com.google.gdata.data.Link;
import com.google.gdata.data.sites.SitesLink;
import com.google.gdata.util.ServiceException;
import com.google.gdata.client.sites.ContentQuery;
import com.google.gdata.client.sites.SitesService;
import com.google.common.base.Preconditions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;

public class SiteExporter {

  private String path;
  
  public SiteExporter(URL feedUrl, String path) throws IOException {
    Preconditions.checkNotNull(feedUrl, "entries");
    Preconditions.checkNotNull(path, "path");
    this.path = path;
    ContentQuery query = new ContentQuery(feedUrl);
    for(BaseEntry<?> entry : new ContinuousContentFeed(query)) {
      EntryType type = EntryType.getType(entry);
      if(EntryType.isPage(type)) {
        String fullPath = getFullPath(entry);
        (new File(fullPath)).mkdirs();
    	PageExporter exporter = new PageExporter(entry, feedUrl);
    	BufferedWriter out = new BufferedWriter(new FileWriter(
    	    fullPath + "index.html"));
    	out.write(exporter.getXhtml());
    	out.close();
      }
    }
  }
  
  private String getFullPath(BaseEntry<?> entry) {
    Preconditions.checkNotNull(entry);
	Link parentLink = entry.getLink(SitesLink.Rel.PARENT, ILink.Type.ATOM);
	if(parentLink == null)
	  return path + getNiceTitle(entry) + "/";
	BaseEntry<?> parent = null;
    try {
      URL parentUrl = new URL(parentLink.getHref());
      parent = (new SitesService("google-sites-export")).getEntry(
          parentUrl, Entry.class).getAdaptedEntry();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ServiceException e) {
      e.printStackTrace();
    }
    return getFullPath(parent) + getNiceTitle(entry) + "/";
  }
  
  private String getNiceTitle(BaseEntry<?> entry) {
    String title = entry.getTitle().getPlainText();
    String niceTitle = "";
    for(String s : title.split("[\\W]+")) {
      niceTitle += s + "-";
    }
    if(niceTitle.length() > 0)
      niceTitle = niceTitle.substring(0, niceTitle.length()-1);
    else
      niceTitle = "-";
    return niceTitle;
  }
  
  public static void main(String[] args) throws MalformedURLException, IOException {
    URL feedUrl = new URL("http://bsimon-chi.chi.corp.google.com:7000/feeds/content/site/test");
    String path = "/home/bsimon/Desktop/test/";
    new SiteExporter(feedUrl, path);
  }
}
