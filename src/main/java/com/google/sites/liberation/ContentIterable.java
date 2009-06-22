package com.google.sites.liberation;

import com.google.gdata.data.BaseEntry;
import com.google.gdata.data.sites.ContentFeed;
import com.google.gdata.client.sites.SitesService;
import com.google.gdata.util.ServiceException;
import com.google.gdata.data.Link;
import com.google.gdata.client.sites.ContentQuery;

import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.io.IOException;

public class ContentIterable implements Iterable<BaseEntry> {

  private SitesService service;
  private ContentQuery query;
  
  public ContentIterable(SitesService service, URL feedUrl) {
    this(service, new ContentQuery(feedUrl));
  }
  
  public ContentIterable(SitesService service, ContentQuery query) {
    this.service = service;
    this.query = query;
  }
  
  public Iterator<BaseEntry> iterator() {
    return new FeedIterator();
  }
  
  private class FeedIterator implements Iterator<BaseEntry> {
    
    private ContentFeed contentFeed;
    private Iterator<BaseEntry> currentItr;
    
    FeedIterator() {
      new ContentFeed().declareExtensions(service.getExtensionProfile());
      try {
        contentFeed = service.getFeed(query, ContentFeed.class);
      } catch(IOException e) {
        e.printStackTrace();
      } catch(ServiceException e) {
        e.printStackTrace();
      }
      currentItr = contentFeed.getEntries().iterator();
    }
    
    public boolean hasNext() {
      return currentItr.hasNext() || contentFeed.getNextLink() != null;
    }
    
    public BaseEntry next() {
      if(!currentItr.hasNext()) {
        Link nextLink = contentFeed.getNextLink();
        if(nextLink == null)
          throw new NoSuchElementException();
        try {
          URL feedUrl = new URL(nextLink.getHref());
          contentFeed = service.getFeed(feedUrl, ContentFeed.class);
        } catch(IOException e) {
          e.printStackTrace();
        } catch(ServiceException e) {
          e.printStackTrace();
        }
        currentItr = contentFeed.getEntries().iterator();
      }
      return currentItr.next();
    }
    
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }
}
