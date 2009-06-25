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

import com.google.gdata.data.BaseEntry;
import com.google.gdata.data.Kind.AdaptorException;
import com.google.gdata.data.sites.ContentFeed;
import com.google.gdata.client.Query;
import com.google.gdata.client.sites.SitesService;
import com.google.gdata.util.ServiceException;
import com.google.gdata.client.sites.ContentQuery;

import java.net.URL;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.io.IOException;

/**
 * This class removes some of the boiler-plate code involved in retrieving 
 * the entries for a given query, and also provides a continuous iterable of
 * entries even if the results of a query are split across multiple feeds. 
 * 
 * This class can produce unexpected results if used on a feed other than the
 * content feed for a Google Site.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
public final class ContinuousContentFeed implements Iterable<BaseEntry<?>> {

  private SitesService service;
  private ContentQuery query;
  private int maxResults;

  /**
   * Creates a new instance of {@code ContinuousContentFeed} for the given
   * {@code feedUrl}.
   * 
   * This {@code ContinuousContentFeed} will contain all of the entries
   * in the feed at {@code feedUrl}.
   */
  public ContinuousContentFeed(URL feedUrl) {
    this(new ContentQuery(feedUrl));
  }

  /**
   * Creates a new instance of {@code ContinuousContentFeed} for the given
   * {@code query}.
   * 
   * This {@code ContinuousContentFeed} will contain all of the entries
   * in the feed for the given {@code query}. If the query contains a value for 
   * maxResults, then only that many entries will be present.
   */
  public ContinuousContentFeed(ContentQuery query) {
    service = new SitesService("google-sites-export");
    this.query = query;
    maxResults = query.getMaxResults();
  }

  /**
   * Returns a new iterator of {@code BaseEntry}s for this feed. The iterator
   * returned will iterate through all of the entries corresponding to this 
   * {@code ContinuousContentFeed} even if the results are spread over multiple
   * feeds. Subsequent calls to this method will return independent iterators,
   * each starting at the beginning of the feed. However, each iterator instance
   * will make its own RPC's, and so the use of multiple iterators should be 
   * avoided.
   */
  public Iterator<BaseEntry<?>> iterator() {
    return new FeedIterator();
  }

  /**
   * This class defines the iterator returned by a 
   * {@code ContinuousContentFeed} iterable.
   */
  private class FeedIterator implements Iterator<BaseEntry<?>> {

    @SuppressWarnings("unchecked")
    private Iterator<BaseEntry> currentItr;
    @SuppressWarnings("unchecked")
    private Iterator<BaseEntry> nextItr;
    private int index;

    /**
     * Constructs a new iterator for this {@code ContinuousContentFeed}.
     */
    private FeedIterator() {
      ContentFeed contentFeed = null;
      try {
        contentFeed = service.getFeed(query, ContentFeed.class);
      } catch(IOException e) {
        e.printStackTrace();
      } catch(ServiceException e) {
        e.printStackTrace();
      }
      currentItr = contentFeed.getEntries().iterator();
      nextItr = null;
      index = 1;
    }

    /**
     * Returns true if the iteration has more elements.
     */
    public boolean hasNext() {
      if(maxResults != Query.UNDEFINED && maxResults < index)
        return false;
      if(currentItr.hasNext())
        return true;
      if(nextItr == null) {
        int originalIndex = query.getStartIndex();
        query.setStartIndex(index);
        ContentFeed contentFeed = null;
        try {
          contentFeed = service.getFeed(query, ContentFeed.class);
        } catch(IOException e) {
          e.printStackTrace();
        } catch(ServiceException e) {
          e.printStackTrace();
        }
        query.setStartIndex(originalIndex);
        nextItr = contentFeed.getEntries().iterator();
      }
      return nextItr.hasNext();
    }

    /**
     * Returns the next element in the iteration.
     * 
     * @throws NoSuchElementException the iteration has no more elements.
     */
    public BaseEntry<?> next() {
      if(!hasNext())
        throw new NoSuchElementException();
      if(!currentItr.hasNext()) {
        currentItr = nextItr;
        nextItr = null;
      }
      index++;
      BaseEntry<?> next = currentItr.next();
      try {
        return next.getAdaptedEntry();
      } catch (AdaptorException e) {
        return next;
      }
    }

    /**
     * @throws UnsupportedOperationException the remove operation is not
     *         supported by this iterator. 
     */
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }
}
