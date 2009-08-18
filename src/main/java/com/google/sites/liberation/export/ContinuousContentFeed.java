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

package com.google.sites.liberation.export;

import static com.google.gdata.util.common.base.Preconditions.checkNotNull;
import static com.google.gdata.util.common.base.Preconditions.checkArgument;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;
import com.google.gdata.client.Query;
import com.google.gdata.client.sites.ContentQuery;
import com.google.gdata.client.sites.SitesService;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.util.ServiceException;
import com.google.gdata.util.common.base.Pair;
import com.google.sites.liberation.util.EntryProvider;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides a continuous iterable of entries even if the results of 
 * a query are split across multiple feeds. This class will also return all
 * valid entries in a feed even if some entries in the feed cause exceptions
 * to be thrown. 
 * 
 * <p>This class can produce unexpected results if used on a feed other than the
 * content feed for a Google Site.</p>
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
final class ContinuousContentFeed implements Iterable<BaseContentEntry<?>> {

  private static final Logger LOGGER = Logger.getLogger(
      ContinuousContentFeed.class.getCanonicalName());
  
  private final EntryProvider entryProvider;
  private final URL feedUrl;
  private final SitesService sitesService;
  private final int resultsPerRequest;
  
  /**
   * Creates a new instance of {@code ContinuousContentFeed} for the given
   * entry provider, feed URL, and number of entries to request per query.
   * 
   * <p>This {@code ContinuousContentFeed} will contain all of the valid entries
   * in the feed at {@code feedUrl}.</p>
   */
  ContinuousContentFeed(URL feedUrl, EntryProvider entryProvider,
      SitesService sitesService, int resultsPerRequest) {
    this.entryProvider = checkNotNull(entryProvider);
    this.feedUrl = checkNotNull(feedUrl);
    this.sitesService = checkNotNull(sitesService);
    checkArgument(resultsPerRequest > 0);
    this.resultsPerRequest = resultsPerRequest;
  }
  
  /**
   * Returns a new iterator of {@code BaseContentEntry}s for this feed. 
   * <p>
   * The iterator returned will iterate through all of the entries corresponding 
   * to this {@code ContinuousContentFeed} even if the results are spread over 
   * multiple feeds. Subsequent calls to this method will return independent 
   * iterators, each starting at the beginning of the feed. However, each 
   * iterator instance will make its own RPC's, and so the use of multiple 
   * iterators should be avoided.
   * </p>
   */
  public AbstractIterator<BaseContentEntry<?>> iterator() {
    return new FeedIterator();
  }

  /**
   * This class defines the iterator returned by a 
   * {@code ContinuousContentFeed} iterable.
   */
  private class FeedIterator extends AbstractIterator<BaseContentEntry<?>> {

    Iterator<BaseContentEntry<?>> currentItr;
    int index;

    /**
     * Constructs a new iterator for this {@code ContinuousContentFeed}.
     */
    FeedIterator() {
      currentItr = Iterators.emptyIterator();
      index = 1;
    }

    /**
     * Returns the next element if it exists, otherwise calls endOfData() and
     * returns null.
     */
    @Override
    public BaseContentEntry<?> computeNext() {
      if (!currentItr.hasNext()) {
        Pair<Iterator<BaseContentEntry<?>>, Integer> pair = 
            getEntries(index, resultsPerRequest);
        currentItr = pair.getFirst();
        index += pair.getSecond();
        if (!currentItr.hasNext()) {
          return endOfData();
        }
      }
      return currentItr.next();
    }
    
    /**
     * Returns an iterator containing the valid entries with indices between
     * {@code start} and {@code start}+{@code num}-1 and the number of entries
     * the iterator contains. 
     */
    private Pair<Iterator<BaseContentEntry<?>>, Integer>
        getEntries(int start, int num) {
      Query query = new ContentQuery(feedUrl);
      try {
        int numReturned = 0;
        Iterator<BaseContentEntry<?>> itr = Iterators.emptyIterator();
        List<BaseContentEntry<?>> entries;
        do {
          query.setStartIndex(start + numReturned);
          query.setMaxResults(num - numReturned);
          entries = entryProvider.getEntries(query, sitesService);
          numReturned += entries.size();
          itr = Iterators.concat(itr, entries.iterator());
        } while (numReturned < num && entries.size() > 0);
        return Pair.of(itr, numReturned);
      } catch (IOException e) {
        return catchException(e, start, num);
      } catch (ServiceException e) {
        return catchException(e, start, num);
      }
    }
    
    private Pair<Iterator<BaseContentEntry<?>>, Integer> 
        catchException(Exception e, int start, int num) {
      String message = "Error retrieving response from query.";
      LOGGER.log(Level.WARNING, message, e);
      if (num == 1) {
        Iterator<BaseContentEntry<?>> itr = Iterators.emptyIterator();
        return Pair.of(itr, 1);
      } else {
        int num1 = num/2;
        int num2 = num - num1;
        Pair<Iterator<BaseContentEntry<?>>, Integer> pair1 = 
            getEntries(start, num1);
        Pair<Iterator<BaseContentEntry<?>>, Integer> pair2 = 
            getEntries(start + num1, num2);
        Iterator<BaseContentEntry<?>> itr = Iterators.concat(pair1.getFirst(),
            pair2.getFirst());
        int numReturned = pair1.getSecond() + pair2.getSecond();
        return Pair.of(itr, numReturned);
      }
    }
  }
}
