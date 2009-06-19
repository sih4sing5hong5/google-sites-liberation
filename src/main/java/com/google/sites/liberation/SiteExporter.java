package com.google.sites.liberation;

import com.google.gdata.data.BaseEntry;
import com.google.gdata.client.sites.SitesService;
import com.google.gdata.data.sites.ContentFeed;
import com.google.gdata.util.ServiceException;
import com.google.gdata.data.TextContent;
import com.google.common.base.Preconditions;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.net.URL;
import java.net.MalformedURLException;

public class SiteExporter {

  private String path;
  
  public SiteExporter(URL feedUrl, String path) {
    Preconditions.checkNotNull(feedUrl, "url");
    Preconditions.checkNotNull(path, "path");
    SitesService service = new SitesService("google-sites-export");
    new ContentFeed().declareExtensions(service.getExtensionProfile());
    ContentFeed contentFeed = null;
    try {
      contentFeed = service.getFeed(feedUrl, ContentFeed.class);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ServiceException e) {
      e.printStackTrace();
    }
    this.path = path;
    Iterable<BaseEntry> entries = contentFeed.getEntries();
    for (BaseEntry<?> entry : entries) {
      try {
        writePage(entry);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
  
  private void writePage(BaseEntry<?> entry) throws IOException {
    Preconditions.checkNotNull(entry);
    String fileName = path + entry.getTitle().getPlainText() + ".html";
    FileWriter fileWriter = new FileWriter(fileName);
    BufferedWriter out = new BufferedWriter(fileWriter);
    out.write("<html>\n<body>\n");
    out.write("<h3>" + entry.getTitle().getPlainText() + "</h3>\n");
    try {
      out.write(entry.getTextContent().getContent().getPlainText());
    }
    catch (IllegalStateException e) {}
    out.write("\n</body>\n</html>");
    out.close();
  }
  
  public static void main(String[] args) throws MalformedURLException {
    URL feedUrl = new URL("http://bsimon-chi.chi.corp.google.com:7000/feeds/content/site/test2?prettyprint=true");
    String path = "/home/bsimon/Desktop/test2/";
    new SiteExporter(feedUrl, path);
  }
}
