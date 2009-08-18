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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.gdata.client.sites.SitesService;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.inject.Inject;
import com.google.sites.liberation.util.EntryProvider;

import java.net.URL;

/**
 * Provides an Iterable of BaseContentEntry's, for a given feed URL and 
 * SitesService. 
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
final class FeedProviderImpl implements FeedProvider {

  private static final int RESULTS_PER_REQUEST = 20;
  
  private final EntryProvider entryProvider;
  
  @Inject
  FeedProviderImpl(EntryProvider entryProvider) {
    this.entryProvider = checkNotNull(entryProvider);
  }
  
  @Override
  public Iterable<BaseContentEntry<?>> getEntries(URL feedUrl, 
      SitesService sitesService) {
    return new ContinuousContentFeed(feedUrl, entryProvider, sitesService,
        RESULTS_PER_REQUEST);
  }
}
