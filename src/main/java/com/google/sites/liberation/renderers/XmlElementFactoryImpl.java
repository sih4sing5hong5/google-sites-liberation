package com.google.sites.liberation.renderers;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.sites.liberation.util.EntryType.getType;

import com.google.gdata.data.Person;
import com.google.gdata.data.XhtmlTextConstruct;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.sites.liberation.util.XmlElement;

import org.joda.time.DateTime;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implements XmlElementFactory to construct various XmlElement's.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
public final class XmlElementFactoryImpl implements XmlElementFactory {

  private static final Logger logger = Logger.getLogger(
      XmlElementFactoryImpl.class.getCanonicalName());
  
  @Override
  public XmlElement getAuthorElement(BaseContentEntry<?> entry) {
    checkNotNull(entry);
    XmlElement element = new XmlElement("span");
    element.setAttribute("class", "vcard");
    Person author = entry.getAuthors().get(0);
    String name = author.getName();
    String email = author.getEmail();
    if (name == null) {
      XmlElement link = getHyperLink("mailto:" + email, email);
      link.setAttribute("class", "email");
      element.addElement(link);
    } else {
      XmlElement link = getHyperLink("mailto:" + email, name);
      link.setAttribute("class", "fn");
      element.addElement(link);
    }
    return element;
  }

  @Override
  public XmlElement getContentElement(BaseContentEntry<?> entry) {
    checkNotNull(entry);
    XmlElement element = new XmlElement("div");
    element.setAttribute("class", "entry-content");
    String xhtmlContent;
    try {
      xhtmlContent = ((XhtmlTextConstruct)(entry.getTextContent()
          .getContent())).getXhtml().getBlob();
    } catch(IllegalStateException e) {
      logger.log(Level.WARNING, "Invalid Content", e);
      xhtmlContent = "";
    } catch(ClassCastException e) {
      logger.log(Level.WARNING, "Invalid Content", e);
      xhtmlContent = "";
    } catch(NullPointerException e) {
      logger.log(Level.WARNING, "Invalid Content", e);
      xhtmlContent = "";
    }
    element.addXml(xhtmlContent);
    return element;
  }

  @Override
  public XmlElement getEntryElement(BaseContentEntry<?> entry, String elementType) {
    checkNotNull(entry, "entry");
    checkNotNull(elementType, "elementType");
    XmlElement element = new XmlElement(elementType);
    element.setAttribute("id", entry.getId());
    element.setAttribute("class", "hentry " + getType(entry).toString());
    return element;
  }

  @Override
  public XmlElement getHyperLink(String href, String text) {
    checkNotNull(href, "href");
    checkNotNull(text, "text");
    XmlElement element = new XmlElement("a");
    element.setAttribute("href", href);
    element.addText(text);
    return element;
  }

  @Override
  public XmlElement getRevisionElement(BaseContentEntry<?> entry) {
    checkNotNull(entry);
    XmlElement element = new XmlElement("span");
    element.setAttribute("class", "sites:revision");
    if (entry.getRevision() == null) {
      element.addText("1");
    } else {
      element.addText(entry.getRevision().getValue().toString());
    }
    return element;
  }

  @Override
  public XmlElement getSummaryElement(BaseContentEntry<?> entry) {
    checkNotNull(entry);
    XmlElement element = new XmlElement("span");
    element.setAttribute("class", "entry-summary");
    element.addText(entry.getSummary().getPlainText());
    return element;
  }

  @Override
  public XmlElement getTitleElement(BaseContentEntry<?> entry) {
    checkNotNull(entry);
    XmlElement element = new XmlElement("span");
    element.setAttribute("class", "entry-title");
    element.addText(entry.getTitle().getPlainText());
    return element;
  }

  @Override
  public XmlElement getUpdatedElement(BaseContentEntry<?> entry) {
    checkNotNull(entry);
    XmlElement element = new XmlElement("abbr");
    element.setAttribute("class", "updated");
    element.setAttribute("title", entry.getUpdated().toString());
    DateTime jodaTime = new DateTime(entry.getUpdated().getValue());
    String month = "";
    switch(jodaTime.getMonthOfYear()) {
      case 1: month = "Jan"; break;
      case 2: month = "Feb"; break;
      case 3: month = "Mar"; break;
      case 4: month = "Apr"; break;
      case 5: month = "May"; break;
      case 6: month = "Jun"; break;
      case 7: month = "Jul"; break;
      case 8: month = "Aug"; break;
      case 9: month = "Sep"; break;
      case 10: month = "Oct"; break;
      case 11: month = "Nov"; break;
      case 12: month = "Dec"; break;
    }
    int day = jodaTime.getDayOfMonth();
    int year = jodaTime.getYear();
    element.addText(month + ' ' + day + ", " + year);
    return element;
  }
}
