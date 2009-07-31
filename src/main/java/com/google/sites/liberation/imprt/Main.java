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

package com.google.sites.liberation.imprt;

import com.google.gdata.client.sites.SitesService;
import com.google.gdata.util.ServiceException;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.sites.liberation.util.EntryTreeFactory;
import com.google.sites.liberation.util.InMemoryEntryTreeFactory;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Processes command line arguments for importing a site and then
 * calls SiteImporter accordingly.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
public class Main {

  private static final Logger logger = Logger.getLogger(
      Main.class.getCanonicalName());
  
  @Option(name="-u", usage="username with which to authenticate")
  private String username = null;
  
  @Option(name="-p", usage="password with which to authenticate")
  private String password = null;
  
  @Option(name="-d", usage="domain of site")
  private String domain = "site";
  
  @Option(name="-n", usage="name of site")
  private String name = null;
  
  @Option(name="-f", usage="directory from which to import")
  private File rootDirectory = new File("");
  
  // TODO(bsimon): Remove once no longer testing locally.
  @Option(name="-s", usage="server")
  private String server = "sites.google.com";
  
  private void doMain(String[] args) {
    CmdLineParser parser = new CmdLineParser(this);
    Injector injector = Guice.createInjector(new ImportModule());
    SiteImporter siteImporter = injector.getInstance(SiteImporter.class);
    try {
      parser.parseArgument(args);
      if (name == null) {
        throw new CmdLineException("Name of site not specified!");
      }
      SitesService service = new SitesService("google-sites-liberation");
      if (username != null && password != null) {
        service.setUserCredentials(username, password);
      }
      EntryUploader entryUploader = new SitesServiceEntryUploader(service);
      String feedUrl = "http://" + server + "/feeds/content/" + 
          domain + '/' + name;
      siteImporter.importSite(rootDirectory, new URL(feedUrl), entryUploader);
    } catch (MalformedURLException e) {
      logger.log(Level.SEVERE, e.getMessage());
      throw new RuntimeException(e);
    } catch (CmdLineException e) {
      logger.log(Level.SEVERE, e.getMessage());
      parser.printUsage(System.err);
      return;
    } catch (ServiceException e) {
      logger.log(Level.SEVERE, "Invalid User Credentials!");
      throw new RuntimeException(e);
    }
  }
  
  /**
   * Imports a Site.
   */
  public static void main(String[] args) {
    new Main().doMain(args);
  }
  
  /**
   * GUICE module defining default bindings.
   * 
   * @author bsimon@google.com (Benjamin Simon)
   */
  private class ImportModule extends AbstractModule {

    @Override
    protected void configure() {
      bind(EntryTreeFactory.class).to(InMemoryEntryTreeFactory.class);
    }
  }
}
