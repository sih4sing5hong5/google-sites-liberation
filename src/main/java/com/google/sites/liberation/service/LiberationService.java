/* Copyright (c) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.google.sites.liberation.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.common.annotations.Beta;
import com.google.gdata.client.AuthTokenFactory.TokenListener;
import com.google.gdata.client.sites.SitesService;

/**
 * The GoogleService class extends the basic GData {@link SitesService}
 * abstraction to add support for authentication and cookies.
 *
 * 
 */
public class LiberationService extends SitesService implements TokenListener{

   public LiberationService(String applicationName)
	{
		super(applicationName);
	}

/**
   * Sets the OAuth 2.0 credentials used to generate the authorization header.
   *
   * @param credential the OAuth 2.0 credentials to use to generate the header
   */
  @Beta
  public void setOAuth2Credentials(Credential credential) {
    this.tokenChanged(new OAuth2Token(credential));
  }
}

