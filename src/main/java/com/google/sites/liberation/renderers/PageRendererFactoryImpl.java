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

package com.google.sites.liberation.renderers;

import static com.google.gdata.util.common.base.Preconditions.checkNotNull;
import static com.google.sites.liberation.EntryType.getType;

import com.google.gdata.data.sites.AnnouncementsPageEntry;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.FileCabinetPageEntry;
import com.google.gdata.data.sites.ListPageEntry;
import com.google.sites.liberation.EntryStore;

/**
 * This class implements {@link PageRendererFactory} to create appropriate 
 * implementations of PageRenderer.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
public class PageRendererFactoryImpl implements PageRendererFactory {

  @Override
  public PageRenderer getPageRenderer(BaseContentEntry<?> entry,
      EntryStore entryStore) {
    checkNotNull(entry, "entry");
    checkNotNull(entryStore, "entryStore");
    switch(getType(entry)) {
      case ANNOUNCEMENT:
      case WEB_PAGE:
        return new BasePageRenderer<BaseContentEntry<?>>(entry, entryStore);
      case ANNOUNCEMENTS_PAGE:
        return new AnnouncementsPageRenderer((AnnouncementsPageEntry)entry, 
            entryStore);
      case FILE_CABINET_PAGE:
        return new FileCabinetPageRenderer((FileCabinetPageEntry)entry, 
            entryStore);
      case LIST_PAGE:
        return new ListPageRenderer((ListPageEntry)entry, entryStore);
      default: throw new IllegalArgumentException("Invalid entry type!");      
    }
  }
}
