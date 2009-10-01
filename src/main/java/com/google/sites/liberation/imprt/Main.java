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
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.sites.liberation.util.StdOutProgressListener;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Processes command line arguments for importing a site and then
 * calls SiteImporter accordingly.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
public class Main {

  private static final Logger LOGGER = Logger.getLogger(
      Main.class.getCanonicalName());
  
  @Option(name="-u", usage="username with which to authenticate")
  private String username = null;
  
  @Option(name="-p", usage="password with which to authenticate")
  private String password = null;
  
  @Option(name="-d", usage="domain of site")
  private String domain = null;
  
  @Option(name="-w", usage="webspace of the site")
  private String webspace = null;
  
  @Option(name="-f", usage="directory from which to import")
  private File directory = new File("");
  
  @Option(name="-h", usage="host")
  private String host = "sites.google.com";
  
  @Option(name="-r", usage="import revisions")
  private boolean importRevisions = false;
  
  private void doMain(String[] args) {
    CmdLineParser parser = new CmdLineParser(this);
    Injector injector = Guice.createInjector(new SiteImporterModule());
    SiteImporter siteImporter = injector.getInstance(SiteImporter.class);
    try {
      parser.parseArgument(args);
      if (webspace == null) {
        throw new CmdLineException("Webspace of site not specified!");
      }
      if (username == null) {
        throw new CmdLineException("Username not specified!");
      }
      if (password == null) {
        throw new CmdLineException("Password not specified!");
      }
      if (!username.contains("@") && domain != null) {
        username += '@' + domain;
      }
      SitesService sitesService = new SitesService("google-sites-liberation");
      sitesService.setUserCredentials(username, password);
      siteImporter.importSite(host, domain, webspace, importRevisions, 
          sitesService, directory, new StdOutProgressListener());
    } catch (CmdLineException e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
      parser.printUsage(System.err);
      return;
    } catch (ServiceException e) {
      LOGGER.log(Level.SEVERE, "Invalid User Credentials!");
      throw new RuntimeException(e);
    }
  }
  
  /**
   * Imports a Site.
   */
  public static void main(String[] args) {
    new Main().doMain(args);
  }
}
