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

import com.google.gdata.client.sites.SitesService;
import com.google.gdata.util.common.base.Nullable;
import com.google.inject.ImplementedBy;
import com.google.sites.liberation.util.ProgressListener;

import java.io.File;

/**
 * Used to export an entire Site to a given root folder.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
@ImplementedBy(SiteExporterImpl.class)
public interface SiteExporter {
  
  /**
   * Exports a Site.
   * 
   * @param host host serving the site
   * @param domain the domain of the site, if not the default
   * @param webspace the webspace (name) of the site
   * @param exportRevisions whether or not to export page's revisions
   * @param sitesService SitesService with which to access the site
   * @param rootDirectory directory in which to export
   * @param progressListener ProgressListener to monitor progress on the export
   */
  void exportSite(String host, @Nullable String domain, String webspace, 
      boolean exportRevisions, SitesService sitesService, File rootDirectory, 
      ProgressListener progressListener);
}
