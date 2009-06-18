package com.google.sites.liberation;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.HashMultimap;
import com.google.gdata.data.sites.BaseEditableContentEntry;
import com.google.gdata.data.sites.SitesLink;
import com.google.gdata.data.Link;
import java.util.HashMap;

public class InMemoryEntryStore implements EntryStore {
  
  // Stores the entries by id
  private HashMap<String, BaseEditableContentEntry<?>> entries;
  // Stores the ids of the entries by EntryType
  private HashMultimap<EntryType, String> ids;
  // Stores the children of entries by parent id and then EntryType
  private HashMap<String, HashMultimap<EntryType, String>> children;
  
  /**
   * Constructs a new, empty InMemoryEntryStore
   */
  public InMemoryEntryStore() {
    entries = new HashMap<String, BaseEditableContentEntry<?>>();
    ids = HashMultimap.create();
    children = new HashMap<String, HashMultimap<EntryType, String>>();
  }
  
  @Override
  public ImmutableSet<String> getChildrenIds(String parentId, EntryType type) {
    return ImmutableSet.copyOf(children.get(parentId).get(type));
  }

  @Override
  public BaseEditableContentEntry<?> getEntry(String id) {
    return entries.get(id);
  }

  @Override
  public ImmutableSet<String> getEntryIds(EntryType type) {
    return ImmutableSet.copyOf(ids.get(type));
  }

  @Override
  public void storeEntry(BaseEditableContentEntry<?> e) {
    String id = e.getId();
    entries.put(id, e);
    EntryType type = EntryType.getType(e);
    ids.put(type, id);
    Link link = e.getLink(SitesLink.Rel.PARENT, SitesLink.Type.APPLICATION_XHTML_XML);
    if (link != null) {
      String parentId = link.getHref();
      if (children.get(parentId) == null) {
        HashMultimap<EntryType, String> map = HashMultimap.create();
        children.put(parentId, map);
      }
      children.get(parentId).put(type, id);
    }
  }

}
