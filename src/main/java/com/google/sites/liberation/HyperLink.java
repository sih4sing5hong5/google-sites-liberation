package com.google.sites.liberation;

import com.google.gdata.util.common.base.Preconditions;

/**
 * This class extends XmlElement to allow the creation of a hyper link
 * in a single statement.
 *
 * @author bsimon@google.com (Benjamin Simon)
 */
public class HyperLink extends XmlElement {

  /**
   * Creates a new HyperLink with the given href and display text
   */
  public HyperLink(String href, String text) {
    super("a");
    Preconditions.checkNotNull(href, "href");
    Preconditions.checkNotNull(text, "text");
    this.setAttribute("href", href);
    this.addText(text);
  }
  
}
