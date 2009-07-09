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

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;
import com.google.gdata.client.Query;
import com.google.gdata.client.sites.ContentQuery;
import com.google.gdata.client.sites.SitesService;
import com.google.gdata.data.BaseEntry;
import com.google.gdata.data.Kind.AdaptorException;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.ContentFeed;
import com.google.gdata.util.ServiceException;
import com.google.gdata.util.common.base.Preconditions;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

/**
 * This class provides a continuous iterable of entries even if the results of 
 * a query are split across multiple feeds, and also removes some of the 
 * boiler-plate code involved in retrieving the entries for a given query. 
 * 
 * This class can produce unexpected results if used on a feed other than the
 * content feed for a Google Site.
 * 
 * A RuntimeException is thrown if there are problems communicating with the 
 * server during iteration.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
final class ContinuousContentFeed implements Iterable<BaseContentEntry<?>> {

  private final SitesService service;
  private ContentQuery query;
  private final int maxResults;
  private final int startIndex;

  /**
   * Creates a new instance of {@code ContinuousContentFeed} for the given
   * {@code feedUrl}.
   * 
   * <p>This {@code ContinuousContentFeed} will contain all of the entries
   * in the feed at {@code feedUrl}.</p>
   */
  public ContinuousContentFeed(SitesService service, URL feedUrl) {
    this(service, new ContentQuery(feedUrl));
  }

  /**
   * Creates a new instance of {@code ContinuousContentFeed} for the given
   * {@code query}.
   * 
   * This {@code ContinuousContentFeed} will contain all of the entries
   * in the feed for the given {@code query}. If the query contains a value for 
   * maxResults, then only that many entries will be present.
   */
  public ContinuousContentFeed(SitesService service, ContentQuery query) {
    Preconditions.checkNotNull(query);
    this.query = query;
    this.service = service;
    this.maxResults = query.getMaxResults();
    this.startIndex = query.getStartIndex();
  }
  
  /**
   * Returns a new iterator of {@code BaseContentEntry}s for this feed. The 
   * iterator returned will iterate through all of the entries corresponding to 
   * this {@code ContinuousContentFeed} even if the results are spread over 
   * multiple feeds. Subsequent calls to this method will return independent 
   * iterators, each starting at the beginning of the feed. However, each 
   * iterator instance will make its own RPC's, and so the use of multiple 
   * iterators should be avoided.
   */
  public AbstractIterator<BaseContentEntry<?>> iterator() {
    return new FeedIterator();
  }

  /**
   * This class defines the iterator returned by a 
   * {@code ContinuousContentFeed} iterable.
   */
  private class FeedIterator extends AbstractIterator<BaseContentEntry<?>> {

    @SuppressWarnings("unchecked")
    Iterator<BaseEntry> currentItr;
    int index;

    /**
     * Constructs a new iterator for this {@code ContinuousContentFeed}.
     */
    FeedIterator() {
      currentItr = Iterators.emptyIterator();
      index = (startIndex == Query.UNDEFINED) ? 1 : startIndex;
    }

    @Override
    public BaseContentEntry<?> computeNext() {
      if ((maxResults != Query.UNDEFINED) && (maxResults < index)) {
        return endOfData();
      }
      if (!currentItr.hasNext()) {
        query.setStartIndex(index);
        ContentFeed contentFeed = null;
        try {
          contentFeed = service.getFeed(query, ContentFeed.class);
        } catch(IOException e) {
          System.err.println("Error retrieving response from query: " + 
              query.getQueryUri());
          throw new RuntimeException(e);
        } catch(ServiceException e) {
          System.err.println("Error retrieving response from query: " + 
              query.getQueryUri());
          throw new RuntimeException(e);
        }
        currentItr = contentFeed.getEntries().iterator();
        if (!currentItr.hasNext()) {
          return endOfData();
        }
      }
      BaseEntry<?> next = currentItr.next();
      index++;
      try {
        return (BaseContentEntry<?>)next.getAdaptedEntry();
      } catch (AdaptorException e) {
        return (BaseContentEntry<?>)next;
      }
    }
  }
}
