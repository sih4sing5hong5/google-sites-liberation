package com.google.sites.liberation;

import com.google.gdata.data.BaseEntry;
import com.google.gdata.data.sites.SitesLink;
import com.google.common.base.Preconditions;

import java.util.Iterator;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class SiteExporter {

  private String path;
  
  public SiteExporter(Iterable<BaseEntry<?>> entries, String path) throws IOException{
    Preconditions.checkNotNull(entries, "entries");
    Preconditions.checkNotNull(path, "path");
    for (BaseEntry<?> entry : entries) {
      EntryType type = EntryType.getType(entry);
      if(EntryType.isPage(type)) {
    	PageExporter exporter = new PageExporter(entry, entries);
    	BufferedWriter out = new BufferedWriter(new FileWriter(getFileName(entry, entries)
    			+ ".html"));
    	out.write(exporter.getXhtml());
    	out.close();
      }
    }
  }
  
  private String getFileName(BaseEntry<?> entry, Iterable<BaseEntry<?>> entries) {
	Preconditions.checkNotNull(entries);
	if(entry == null) {
	  return path; 
	}
	String parentLink = entry.getLink(SitesLink.Rel.PARENT, 
		SitesLink.Type.APPLICATION_XHTML_XML).getHref();
	BaseEntry<?> parent = null;
	for(Iterator<BaseEntry<?>> itr = entries.iterator(); itr.hasNext() && parent==null;) {
	  BaseEntry<?> e = itr.next();
	  if(e.getId().equals(parentLink))
		 parent = e;
	}
    return getFileName(parent, entries) + "/" + entry.getTitle().getPlainText();
  }
}
