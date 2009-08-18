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

import com.google.gdata.client.sites.SitesService;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.BasePageEntry;
import com.google.inject.ImplementedBy;

import java.net.URL;
import java.util.List;

/**
 * Uploads (updates if possible, otherwise inserts) an entry to a given feed URL.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
@ImplementedBy(EntryUploaderImpl.class)
public interface EntryUploader {

  /**
   * Uploads the given entry which has the given ancestors to the given
   * URL, using the given SitesService. The entry will be updated if at all
   * possible, otherwise it will be inserted.
   */
  BaseContentEntry<?> uploadEntry(BaseContentEntry<?> entry, 
      List<BasePageEntry<?>> ancestors, URL feedUrl, SitesService sitesService);
}
