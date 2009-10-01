package com.google.sites.liberation.parsers;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.sites.liberation.parsers.ParserUtils.hasClass;
import static com.google.sites.liberation.util.EntryType.getType;
import static com.google.sites.liberation.util.EntryType.LIST_ITEM;
import static com.google.sites.liberation.util.EntryType.LIST_PAGE;

import com.google.gdata.data.sites.AnnouncementEntry;
import com.google.gdata.data.sites.AnnouncementsPageEntry;
import com.google.gdata.data.sites.AttachmentEntry;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.CommentEntry;
import com.google.gdata.data.sites.FileCabinetPageEntry;
import com.google.gdata.data.sites.ListItemEntry;
import com.google.gdata.data.sites.ListPageEntry;
import com.google.gdata.data.sites.WebAttachmentEntry;
import com.google.gdata.data.sites.WebPageEntry;
import com.google.inject.Inject;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Parses an html element representing an entry.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
final class EntryParserImpl implements EntryParser {
  
  private static final Logger LOGGER = Logger.getLogger(
      EntryParserImpl.class.getCanonicalName());
  
  private final AuthorParser authorParser;
  private final ContentParser contentParser;
  private final DataParser dataParser;
  private final FieldParser fieldParser;
  private final SummaryParser summaryParser;
  private final TitleParser titleParser;
  private final UpdatedParser updatedParser;
  
  /**
   * Creates a new EntryParserImpl with the given dependencies.
   */
  @Inject
  EntryParserImpl(
      AuthorParser authorParser,
      ContentParser contentParser,
      DataParser dataParser,
      FieldParser fieldParser,
      SummaryParser summaryParser,
      TitleParser titleParser,
      UpdatedParser updatedParser) {
    this.authorParser = checkNotNull(authorParser);
    this.contentParser = checkNotNull(contentParser);
    this.dataParser = checkNotNull(dataParser);
    this.fieldParser = checkNotNull(fieldParser);
    this.summaryParser = checkNotNull(summaryParser);
    this.titleParser = checkNotNull(titleParser);
    this.updatedParser = checkNotNull(updatedParser);
  }
  
  /**
   * Parses the given element, returning an EntryTree with the first entry
   * encountered as the root, and enclosing non-page entries as children.
   */
  @Override
  public BaseContentEntry<?> parseEntry(Element element) {
    BaseContentEntry<?> entry = getEntry(element);
    parseElement(element, entry);
    return entry;
  }
  
  /**
   * Parses the given element, populating the given entry with its data.
   */
  private void parseElement(Element element, BaseContentEntry<?> entry) {
    NodeList nodeList = element.getChildNodes();
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node node = nodeList.item(i);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        Element child = (Element) node;
        if (!hasClass(child, "hentry") && !child.getTagName().equals("q")
            && !child.getTagName().equals("blockquote")) {
          boolean parseDeeper = true;
          if (hasClass(child, "entry-title")) {
            entry.setTitle(titleParser.parseTitle(child));
            parseDeeper = false;
          } 
          if (hasClass(child, "entry-content")) {
            entry.setContent(contentParser.parseContent(child));
            parseDeeper = false;
          } 
          if (hasClass(child, "updated")) {
            entry.setUpdated(updatedParser.parseUpdated(child));
            parseDeeper = false;
          } 
          if (hasClass(child, "vcard")) {
            entry.getAuthors().add(authorParser.parseAuthor(child));
            parseDeeper = false;
          } 
          if (hasClass(child, "entry-summary")) {
            entry.setSummary(summaryParser.parseSummary(child));
            parseDeeper = false;
          } 
          if (hasClass(child, "gs:data")) {
            if (getType(entry) == LIST_PAGE) {
              // TODO(gk5885): remove extra cast for
              // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6302214
              ((ListPageEntry) (BaseContentEntry) entry).setData(dataParser.parseData(child));
            }
            parseDeeper = false;
          } 
          if (hasClass(child, "gs:field")) {
            if (getType(entry) == LIST_ITEM) {
              // TODO(gk5885): remove extra cast for
              // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6302214
              ((ListItemEntry) (BaseContentEntry) entry).addField(fieldParser.parseField(child));
            }
            parseDeeper = false;
          }
          if (parseDeeper) {
            parseElement(child, entry);
          }
        }
      }
    }
  }
  
  /**
   * Returns an appropriate BaseContentEntry for the given element that 
   * represents the entry. This method only parses the element's root tag, not
   * its children.
   */
  private BaseContentEntry<?> getEntry(Element element) {
    BaseContentEntry<?> entry = null;
    if (hasClass(element, "announcement")) {
      entry = new AnnouncementEntry();
    } else if (hasClass(element, "announcementspage")) {
      entry = new AnnouncementsPageEntry();
    } else if (hasClass(element, "attachment")) {
      entry = new AttachmentEntry();
    } else if (hasClass(element, "comment")) {
      entry = new CommentEntry();
    } else if (hasClass(element, "filecabinet")) {
      entry = new FileCabinetPageEntry();
    } else if (hasClass(element, "listitem")) {
      entry = new ListItemEntry();
    } else if (hasClass(element, "listpage")) {
      entry = new ListPageEntry();
    } else if (hasClass(element, "webattachment")) {
      entry = new WebAttachmentEntry();
    } else if (hasClass(element, "webpage")) {
      entry = new WebPageEntry();
    } else {
      LOGGER.log(Level.WARNING, "Entry type is undefined!");
      entry = new WebPageEntry();
    }
    String id = element.getAttribute("id");
    if (id != "") {
      entry.setId(id);
    }
    return entry;
  }
}
