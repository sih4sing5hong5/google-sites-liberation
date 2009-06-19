package com.google.gdata.client.sites;

import com.google.gdata.client.AuthTokenFactory;
import com.google.gdata.client.GoogleService;
import com.google.gdata.client.Service;
import com.google.gdata.data.batch.BatchUtils;
import com.google.gdata.data.sites.ActivityFeed;
import com.google.gdata.data.sites.AnnouncementEntry;
import com.google.gdata.data.sites.AnnouncementRevisionEntry;
import com.google.gdata.data.sites.AnnouncementsPageEntry;
import com.google.gdata.data.sites.AnnouncementsPageRevisionEntry;
import com.google.gdata.data.sites.AttachmentEntry;
import com.google.gdata.data.sites.AttachmentRevisionEntry;
import com.google.gdata.data.sites.CommentEntry;
import com.google.gdata.data.sites.CommentRevisionEntry;
import com.google.gdata.data.sites.ContentFeed;
import com.google.gdata.data.sites.CreationActivityEntry;
import com.google.gdata.data.sites.DeletionActivityEntry;
import com.google.gdata.data.sites.EditActivityEntry;
import com.google.gdata.data.sites.FileCabinetPageEntry;
import com.google.gdata.data.sites.FileCabinetPageRevisionEntry;
import com.google.gdata.data.sites.ListItemEntry;
import com.google.gdata.data.sites.ListItemRevisionEntry;
import com.google.gdata.data.sites.ListPageEntry;
import com.google.gdata.data.sites.ListPageRevisionEntry;
import com.google.gdata.data.sites.MoveActivityEntry;
import com.google.gdata.data.sites.RecoveryActivityEntry;
import com.google.gdata.data.sites.RevisionFeed;
import com.google.gdata.data.sites.WebPageEntry;
import com.google.gdata.data.sites.WebPageRevisionEntry;
import com.google.gdata.util.Version;
import com.google.gdata.util.VersionRegistry;

/**
 * Extends the basic {@link GoogleService} abstraction to define a service that
 * is preconfigured for access to the Google Sites data API.
 *
 * @author yanivi@google.com (Yaniv Inbar)
 */
public class SitesService extends GoogleService {

  /**
   * The abbreviated name of Google Sites recognized by Google.  The service
   * name is used when requesting an authentication token.
   */
  public static final String SITES_SERVICE = "jotspot";

  /**
   * The version ID of the service.
   */
  public static final String SITES_SERVICE_VERSION = "GSites-Java/" +
      SitesService.class.getPackage().getImplementationVersion();

  /**
   * GData versions supported by Google Sites Service.
   */
  public static final class Versions {

    /** Version 1.0. */
    public static final Version V1 = new Version(SitesService.class, "1.0",
        Service.Versions.V1);

    private Versions() {}
  }

  /**
   * Default GData version used by the Google Sites service.
   */
  public static final Version DEFAULT_VERSION =
      Service.initServiceVersion(SitesService.class, Versions.V1);

  /**
   * Constructs an instance connecting to the Google Sites service for an
   * application with the name {@code applicationName}.
   *
   * @param applicationName the name of the client application accessing the
   *     service. Application names should preferably have the format
   *     [company-id]-[app-name]-[app-version]. The name will be used by the
   *     Google servers to monitor the source of authentication.
   */
  public SitesService(String applicationName) {
    super(SITES_SERVICE, applicationName);
    declareExtensions();
  }

  /**
   * Constructs an instance connecting to the Google Sites service for an
   * application with the name {@code applicationName} and the given {@code
   * GDataRequestFactory} and {@code AuthTokenFactory}. Use this constructor to
   * override the default factories.
   *
   * @param applicationName the name of the client application accessing the
   *     service. Application names should preferably have the format
   *     [company-id]-[app-name]-[app-version]. The name will be used by the
   *     Google servers to monitor the source of authentication.
   * @param requestFactory the request factory that generates gdata request
   *     objects
   * @param authTokenFactory the factory that creates auth tokens
   */
  public SitesService(String applicationName,
      Service.GDataRequestFactory requestFactory,
      AuthTokenFactory authTokenFactory) {
    super(applicationName, requestFactory, authTokenFactory);
    declareExtensions();
  }

  /**
   * Constructs an instance connecting to the Google Sites service with name
   * {@code serviceName} for an application with the name {@code
   * applicationName}.  The service will authenticate at the provided {@code
   * domainName}.
   *
   * @param applicationName the name of the client application accessing the
   *     service. Application names should preferably have the format
   *     [company-id]-[app-name]-[app-version]. The name will be used by the
   *     Google servers to monitor the source of authentication.
   * @param protocol        name of protocol to use for authentication
   *     ("http"/"https")
   * @param domainName      the name of the domain hosting the login handler
   */
  public SitesService(String applicationName, String protocol,
      String domainName) {
    super(SITES_SERVICE, applicationName, protocol, domainName);
    declareExtensions();
  }

  @Override
  public String getServiceVersion() {
    return SITES_SERVICE_VERSION + " " + super.getServiceVersion();
  }

  /**
   * Returns the current GData version used by the Google Sites service.
   */
  public static Version getVersion() {
    return VersionRegistry.get().getVersion(SitesService.class);
  }

  /**
   * Declare the extensions of the feeds for the Google Sites service.
   */
  private void declareExtensions() {
    new AnnouncementEntry().declareExtensions(extProfile);
    new AnnouncementRevisionEntry().declareExtensions(extProfile);
    new AnnouncementsPageEntry().declareExtensions(extProfile);
    new AnnouncementsPageRevisionEntry().declareExtensions(extProfile);
    new AttachmentEntry().declareExtensions(extProfile);
    new AttachmentRevisionEntry().declareExtensions(extProfile);
    new CommentEntry().declareExtensions(extProfile);
    new CommentRevisionEntry().declareExtensions(extProfile);
    new FileCabinetPageEntry().declareExtensions(extProfile);
    new FileCabinetPageRevisionEntry().declareExtensions(extProfile);
    new ListItemEntry().declareExtensions(extProfile);
    new ListItemRevisionEntry().declareExtensions(extProfile);
    new ListPageEntry().declareExtensions(extProfile);
    new ListPageRevisionEntry().declareExtensions(extProfile);
    new WebPageEntry().declareExtensions(extProfile);
    new WebPageRevisionEntry().declareExtensions(extProfile);
    extProfile.setAutoExtending(true);
    new ActivityFeed().declareExtensions(extProfile);
    new ContentFeed().declareExtensions(extProfile);
    new CreationActivityEntry().declareExtensions(extProfile);
    new DeletionActivityEntry().declareExtensions(extProfile);
    new EditActivityEntry().declareExtensions(extProfile);
    new MoveActivityEntry().declareExtensions(extProfile);
    new RecoveryActivityEntry().declareExtensions(extProfile);
    new RevisionFeed().declareExtensions(extProfile);
    BatchUtils.declareExtensions(extProfile);
  }

}
