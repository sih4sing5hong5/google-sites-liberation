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

import com.google.inject.AbstractModule;
import com.google.sites.liberation.renderers.PageRendererFactory;
import com.google.sites.liberation.renderers.PageRendererFactoryImpl;

/**
 * This class is a GUICE module defining default bindings.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
public class SitesModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(SiteExporter.class).to(SiteExporterImpl.class);
    bind(PageExporter.class).to(PageExporterImpl.class);
    bind(EntryStore.class).to(InMemoryEntryStore.class);
    bind(AppendableFactory.class).to(BufferedWriterFactory.class);
    bind(AttachmentDownloader.class).to(AttachmentDownloaderImpl.class);
    bind(PageRendererFactory.class).to(PageRendererFactoryImpl.class);
  }
}
