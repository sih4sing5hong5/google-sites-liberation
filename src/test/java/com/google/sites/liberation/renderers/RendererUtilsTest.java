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

import static org.junit.Assert.*;

import com.google.gdata.data.sites.AnnouncementEntry;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.Revision;
import com.google.gdata.data.sites.WebPageEntry;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.Person;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.XhtmlTextConstruct;
import com.google.gdata.util.XmlBlob;
import com.google.sites.liberation.util.XmlElement;

import org.junit.Test;

public class RendererUtilsTest {

  @Test
  public void testGetAuthorElement() {
    Person author = new Person();
    author.setName("Ben Simon");
    author.setEmail("me@company.com");
    BaseContentEntry<?> entry = new WebPageEntry();
    entry.getAuthors().add(author);
    XmlElement element = RendererUtils.getAuthorElement(entry);
    assertEquals("<span class=\"author\"><span class=\"vcard\"><a class=\"fn\" href=\"" +
    		"mailto:me@company.com\">Ben Simon</a></span></span>", element.toString());
  }

  @Test
  public void testGetContentElement() {
    String xhtml = "<div><a href=\"http://whoa!\">whoa</a></div>";
    XmlBlob blob = new XmlBlob();
    blob.setBlob(xhtml);
    BaseContentEntry<?> entry = new WebPageEntry();
    entry.setContent(new XhtmlTextConstruct(blob));
    XmlElement element = RendererUtils.getXhtmlContentElement(entry);
    assertEquals("<div class=\"entry-content\">" + xhtml + "</div>", 
        element.toString());
    entry = new WebPageEntry();
    element = RendererUtils.getXhtmlContentElement(entry);
    assertEquals("<div class=\"entry-content\"></div>", element.toString());
  }

  @Test
  public void testGetEntryElement() {
    BaseContentEntry<?> entry = new AnnouncementEntry();
    entry.setId("announce");
    XmlElement element = RendererUtils.getEntryElement(entry, "div");
    assertEquals("<div class=\"hentry announcement\" id=\"announce\" />",
        element.toString());
  }

  @Test
  public void testGetHyperLink() {
    XmlElement link = RendererUtils.getHyperLink("http://test.html", "test");
    assertEquals("<a href=\"http://test.html\">test</a>", link.toString());
    
    link = RendererUtils.getHyperLink("", "");
    assertEquals("<a href=\"\"></a>", link.toString());
  }

  @Test
  public void testGetRevisionElement() {
    BaseContentEntry<?> entry = new WebPageEntry();
    entry.setRevision(new Revision(25));
    XmlElement element = RendererUtils.getRevisionElement(entry);
    assertEquals("<span class=\"sites:revision\">25</span>",
        element.toString());
    
    element = RendererUtils.getRevisionElement(new WebPageEntry());
    assertEquals("<span class=\"sites:revision\">1</span>",
        element.toString());
  }

  @Test
  public void testGetSummaryElement() {
    BaseContentEntry<?> entry = new WebPageEntry();
    entry.setSummary(new PlainTextConstruct("summary 1"));
    XmlElement element = RendererUtils.getSummaryElement(entry);
    assertEquals("<span class=\"entry-summary\">summary 1</span>",
        element.toString());
    
    entry.setSummary(new PlainTextConstruct("<summary 1>"));
    element = RendererUtils.getSummaryElement(entry);
    assertEquals("<span class=\"entry-summary\">&lt;summary 1&gt;</span>",
        element.toString());
  }

  @Test
  public void testGetTitleElement() {
    BaseContentEntry<?> entry = new WebPageEntry();
    entry.setTitle(new PlainTextConstruct("title 1"));
    XmlElement element = RendererUtils.getTitleElement(entry);
    assertEquals("<span class=\"entry-title\">title 1</span>",
        element.toString());
    
    entry.setTitle(new PlainTextConstruct("<title 1>"));
    element = RendererUtils.getTitleElement(entry);
    assertEquals("<span class=\"entry-title\">&lt;title 1&gt;</span>",
        element.toString());
  }

  @Test
  public void testGetUpdatedElement() {
    String date = "2009-07-02T21:46:23.133Z";
    BaseContentEntry<?> entry = new WebPageEntry();
    entry.setUpdated(DateTime.parseDateTime(date));
    XmlElement element = RendererUtils.getUpdatedElement(entry);
    assertEquals("<abbr class=\"updated\" title=\"" + date + 
        "\">Jul 2, 2009</abbr>", element.toString());
    
    date = "2598-11-25T23:41:10.256Z";
    entry.setUpdated(DateTime.parseDateTime(date));
    element = RendererUtils.getUpdatedElement(entry);
    assertEquals("<abbr class=\"updated\" title=\"" + date + 
        "\">Nov 25, 2598</abbr>", element.toString());
  }
}
