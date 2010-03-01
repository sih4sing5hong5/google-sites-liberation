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

package com.google.sites.liberation.imprt;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.gdata.client.sites.SitesService;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.util.ServiceException;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Updates a single entry.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
final class EntryUpdaterImpl implements EntryUpdater {

  private static final Logger LOGGER = Logger.getLogger(
      EntryUpdaterImpl.class.getCanonicalName());
  
  @Override
  public BaseContentEntry<?> updateEntry(BaseContentEntry<?> oldEntry,
      BaseContentEntry<?> newEntry, SitesService sitesService) {
    try {
      checkNotNull(oldEntry, "oldEntry");
      checkNotNull(newEntry, "newEntry");
      checkNotNull(sitesService, "sitesService");
      return sitesService.update(new URL(oldEntry.getId()), newEntry,
          oldEntry.getEtag());
    } catch (IOException e) {
      LOGGER.log(Level.WARNING, "Unable to update entry:" + oldEntry, e);
      return null;
    } catch (NullPointerException e) {
      LOGGER.log(Level.WARNING, "Unable to update entry:" + oldEntry, e);
      return null;
    } catch (ServiceException e) {
      LOGGER.log(Level.WARNING, "Unable to update entry:" + oldEntry, e);
      return null;
    }
  }

}
