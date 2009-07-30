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

import com.google.common.collect.Lists;
import com.google.gdata.client.Query;
import com.google.gdata.client.sites.SitesService;
import com.google.gdata.data.BaseEntry;
import com.google.gdata.data.Kind.AdaptorException;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.ContentFeed;
import com.google.gdata.util.ServiceException;

import java.io.IOException;
import java.util.List;

/**
 * Implements {@link EntryDownloader} to provide entries for a given
 * query using a SitesService.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
final class SitesServiceEntryDownloader implements EntryDownloader {

  SitesService service;
  
  /**
   * Creates a new SitesServiceEntryProvider that uses the given service.
   */
  public SitesServiceEntryDownloader(SitesService service) {
    this.service = checkNotNull(service);
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public List<BaseContentEntry<?>> getEntries(Query query) 
      throws IOException, ServiceException {
    List<BaseEntry> baseEntries = 
        service.getFeed(query, ContentFeed.class).getEntries();
    List<BaseContentEntry<?>> adaptedEntries = Lists.newLinkedList();
    for(BaseEntry entry : baseEntries) {
      try {
        adaptedEntries.add((BaseContentEntry<?>) entry.getAdaptedEntry());
      } catch (AdaptorException e) {
        adaptedEntries.add((BaseContentEntry<?>) entry);
      }
    }
    return adaptedEntries;
  }
}
