package com.google.sites.liberation.parsers;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.sites.liberation.parsers.ParserUtils.hasClass;
import static com.google.sites.liberation.util.EntryType.isPage;

import com.google.gdata.data.sites.AnnouncementEntry;
import com.google.gdata.data.sites.AnnouncementsPageEntry;
import com.google.gdata.data.sites.AttachmentEntry;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.BasePageEntry;
import com.google.gdata.data.sites.CommentEntry;
import com.google.gdata.data.sites.FileCabinetPageEntry;
import com.google.gdata.data.sites.ListItemEntry;
import com.google.gdata.data.sites.ListPageEntry;
import com.google.gdata.data.sites.WebPageEntry;
import com.google.inject.Inject;
import com.google.sites.liberation.util.EntryTree;
import com.google.sites.liberation.util.EntryTreeFactory;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implements EntryParser to parse an html element representing an entry.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
final class EntryParserImpl implements EntryParser {
  
  private static final Logger LOGGER = Logger.getLogger(
      EntryParserImpl.class.getCanonicalName());
  
  private final AuthorParser authorParser;
  private final ContentParser contentParser;
  private final DataParser dataParser;
  private final EntryTreeFactory entryTreeFactory;
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
      EntryTreeFactory entryTreeFactory,
      FieldParser fieldParser,
      SummaryParser summaryParser,
      TitleParser titleParser,
      UpdatedParser updatedParser) {
    this.authorParser = checkNotNull(authorParser);
    this.contentParser = checkNotNull(contentParser);
    this.dataParser = checkNotNull(dataParser);
    this.entryTreeFactory = checkNotNull(entryTreeFactory);
    this.fieldParser = checkNotNull(fieldParser);
    this.summaryParser = checkNotNull(summaryParser);
    this.titleParser = checkNotNull(titleParser);
    this.updatedParser = checkNotNull(updatedParser);
  }
  
  @Override
  public EntryTree parseEntry(Element element) {
    checkNotNull(element);
    BaseContentEntry<?> entry = getEntry(element);
    EntryTree entryTree = entryTreeFactory.getEntryTree(entry);
    parseElement(element, entry, entryTree);
    return entryTree;
  }
  
  /**
   * Parses the given element, populating the given entry with its data, and
   * adding its descendants to the given entryTree.
   */
  private void parseElement(Element element, BaseContentEntry<?> entry,
      EntryTree entryTree) {
    NodeList nodeList = element.getChildNodes();
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node node = nodeList.item(i);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        Element child = (Element) node;
        if (hasClass(child, "hentry")) {
          if (!isPage(getEntry(child))) {
            entryTree.addSubTree(parseEntry(child), (BasePageEntry<?>) 
                entryTree.getRoot());
          }
        } else if (hasClass(child, "entry-title")) {
          entry.setTitle(titleParser.parseTitle(child));
        } else if (hasClass(child, "entry-content")) {
          entry.setContent(contentParser.parseContent(child));
        } else if (hasClass(child, "updated")) {
          entry.setUpdated(updatedParser.parseUpdated(child));
        } else if (hasClass(child, "vcard")) {
          entry.getAuthors().add(authorParser.parseAuthor(child));
        } else if (hasClass(child, "entry-summary")) {
          entry.setSummary(summaryParser.parseSummary(child));
        } else if (hasClass(child, "gs:data")) {
          if (entry instanceof ListPageEntry) {
            ((ListPageEntry) entry).setData(dataParser.parseData(child));
          }
        } else if (hasClass(child, "gs:field")) {
          if (entry instanceof ListItemEntry) {
            ((ListItemEntry) entry).addField(fieldParser.parseField(child));
          }
        } else {
          parseElement(child, entry, entryTree);
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
