package com.google.sites.liberation;

import com.google.gdata.data.ILink;
import com.google.gdata.data.Link;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.SitesLink;
import com.google.common.base.Preconditions;
import com.google.common.collect.Multimap;
import com.google.common.collect.Maps;
import com.google.common.collect.HashMultimap;

import java.util.Collection;
import java.util.Map;

/**
 * This class is an in-memory implementation of {@link EntryStore}.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
final class InMemoryEntryStore implements EntryStore {

  private final Map<String, BaseContentEntry<?>> entries;
  private final Multimap<String, BaseContentEntry<?>> children;
  
  /**
   * Creates a new InMemoryEntryStore which provides constant time storage 
   * and retrieval of entries by id or parent id.
   */
  public InMemoryEntryStore() {
    entries = Maps.newHashMap();
    children = HashMultimap.create();
  }

  @Override
  public void addEntry(BaseContentEntry<?> entry) {
    Preconditions.checkNotNull(entry);
    String id = entry.getId();
    Preconditions.checkArgument(id != null && entries.get(id) == null,
        "All entries must have a unique non-null id!");
    entries.put(entry.getId(), entry);
    Link parentLink = entry.getLink(SitesLink.Rel.PARENT, ILink.Type.ATOM);
    if (parentLink != null) {
      String parentId = parentLink.getHref();
      children.put(parentId, entry);
    }
  }
  
  @Override
  public Collection<BaseContentEntry<?>> getChildren(String id) {
    Preconditions.checkNotNull(id);
    return children.get(id);
  }

  @Override
  public BaseContentEntry<?> getEntry(String id) {
    Preconditions.checkNotNull(id);
    return entries.get(id);
  }
}
