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

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.gdata.client.sites.SitesService;
import com.google.gdata.util.ServiceException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.sites.liberation.util.StdOutProgressListener;

/**
 * Processes command line arguments for importing a site and then
 * calls SiteImporter accordingly.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
public class Main {

  private static final Logger LOGGER = Logger.getLogger(
      Main.class.getCanonicalName());
  
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
  
  /**
   * Be sure to specify the name of your application. If the application name is {@code null} or
   * blank, the application will log a warning. Suggested format is "MyCompany-ProductName/1.0".
   */
  private static final String APPLICATION_NAME = "sElaiassi-GoogleSiteLiberation/1.0";

  /** Directory to store user credentials. */
  private static final java.io.File DATA_STORE_DIR =
      new java.io.File(System.getProperty("user.home"), ".store/GoogleSiteLiberation");
  
  /**
   * Global instance of the {@link DataStoreFactory}. The best practice is to make it a single
   * globally shared instance across your application.
   */
  private static FileDataStoreFactory dataStoreFactory;

  /** Global instance of the HTTP transport. */
  private static HttpTransport httpTransport;
  
  private static Oauth2 oauth2;

  /** Global instance of the JSON factory. */
  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
  
  private static GoogleClientSecrets clientSecrets;
  
  /** Scope Authorization. */
  private static List<String> SCOPES = Arrays
	      .asList("https://sites.google.com/feeds");
  
  
  /** Authorizes the installed application to access user's protected data. 
 * @throws Exception */
  private static Credential authorize() throws Exception {
    // load client secrets
    clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
        new InputStreamReader(Main.class.getResourceAsStream("/client_secrets.json")));
    if (clientSecrets.getDetails().getClientId().startsWith("Enter")
        || clientSecrets.getDetails().getClientSecret().startsWith("Enter")) {
      System.out.println("Enter Client ID and Secret from https://code.google.com/apis/console/ "
          + "into google-sites-liberation/src/main/resources/client_secrets.json");
      System.exit(1);
    }
    // set up authorization code flow
    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        httpTransport, JSON_FACTORY, clientSecrets, SCOPES).setDataStoreFactory(
        dataStoreFactory).setAccessType("offline").build();
    
    //Loading receiver on 8080 port (you can change this if already in use) 
    LocalServerReceiver localServerReceiver = new LocalServerReceiver.Builder().setPort( 8080 ).build(); 
    
    // authorize
    return new AuthorizationCodeInstalledApp(flow, localServerReceiver).authorize("user");
  }
  
  private void doMain(String[] args) {
    CmdLineParser parser = new CmdLineParser(this);
    Injector injector = Guice.createInjector(new SiteImporterModule());
    SiteImporter siteImporter = injector.getInstance(SiteImporter.class);
    try {
      parser.parseArgument(args);
      if (webspace == null) {
        throw new CmdLineException("Webspace of site not specified!");
      }
      SitesService sitesService = new SitesService("google-sites-liberation");
      
      httpTransport = GoogleNetHttpTransport.newTrustedTransport();
      dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
      

      Credential credential = authorize();
      oauth2 = new Oauth2.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(
              APPLICATION_NAME).build();
      
	  sitesService.setOAuth2Credentials(credential);
      
      siteImporter.importSite(host, domain, webspace, importRevisions, 
          sitesService, directory, new StdOutProgressListener());
    } catch (CmdLineException e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
      parser.printUsage(System.err);
      return;
    } catch (GeneralSecurityException e) {
      LOGGER.log(Level.SEVERE, "Error while setting up the security");
  	} catch (IOException e) {
  	  LOGGER.log(Level.SEVERE, "Error handling resources files");
  	} catch (Exception e) {
  	  LOGGER.log(Level.SEVERE, "Error while getting the AccesToken");
  	}
  }
  
  /**
   * Imports a Site.
   */
  public static void main(String[] args) {
    new Main().doMain(args);
  }
}
