package com.google.sites.liberation.service;

import java.io.IOException;
import java.net.URL;

import com.google.api.client.auth.oauth2.Credential;
import com.google.gdata.client.http.HttpAuthToken;
import com.google.gdata.util.AuthenticationException;

/**
 * Encapsulates the OAuth 2.0 information used by applications to login on
 * behalf of a user.
 */
public class OAuth2Token implements HttpAuthToken {

  static final String HEADER_PREFIX = "Bearer ";
  final Credential credential;

  /**
   * Create a new {@link OAuth2Token} object.  Store the {@link Credential} to
   * use when generating the header.
   *
   * @param credential the required OAuth 2.0 credentials
   */
  public OAuth2Token(Credential credential) {
    this.credential = credential;
  }

  /**
   * Returns the authorization header using the user's OAuth 2.0 credentials.
   *
   * @param requestUrl the URL being requested
   * @param requestMethod the HTTP method of the request
   * @return the authorization header to be used for the request
   */
  public String getAuthorizationHeader(URL requestUrl, String requestMethod) {
    return HEADER_PREFIX + this.credential.getAccessToken();
  }

  /**
   * Use the {@link Credential} to request a new access token from the
   * authorization endpoint.
   *
   * @return whether a new access token was successfully retrieved
   */
  public boolean refreshToken() throws AuthenticationException {
    try {
      return this.credential.refreshToken();
    } catch (IOException e) {
      AuthenticationException ae =
          new AuthenticationException("Failed to refresh access token: " + e.getMessage());
      ae.initCause(e);
      throw ae;
    }
  }
}
