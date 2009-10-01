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

package com.google.sites.liberation.export;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.gdata.client.sites.SitesService;
import com.google.gdata.data.MediaContent;
import com.google.gdata.data.OutOfLineContent;
import com.google.gdata.data.media.MediaSource;
import com.google.gdata.data.sites.AttachmentEntry;
import com.google.gdata.util.ServiceException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implements {@link AttachmentDownloader} to download an attachment
 * to a specified file.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
final class AttachmentDownloaderImpl implements AttachmentDownloader {
  
  private static final Logger LOGGER = Logger.getLogger(
      AttachmentDownloaderImpl.class.getCanonicalName());
  
  /**
   * Downloads the given attachment to the given file name.
   */
  @Override
  public void download(AttachmentEntry attachment, File file, 
      SitesService sitesService) {
    checkNotNull(attachment);
    checkNotNull(file);
    MediaContent mediaContent = new MediaContent();
    mediaContent.setUri(((OutOfLineContent) attachment.getContent()).getUri());
    try {
      MediaSource mediaSource = sitesService.getMedia(mediaContent);
      InputStream inStream = mediaSource.getInputStream();
      OutputStream outStream = new FileOutputStream(file);
      byte[] buf = new byte[4*1024];
      int bytesRead;
      while((bytesRead = inStream.read(buf)) != -1) {
        outStream.write(buf, 0, bytesRead);
      }
      inStream.close();
      outStream.close();
    } catch (IOException e) {
      LOGGER.log(Level.WARNING, "Error downloading attachment: " 
          + attachment.getTitle().getPlainText(), e);
    } catch (ServiceException e) {
      LOGGER.log(Level.WARNING, "Error downloading attachment: " 
          + attachment.getTitle().getPlainText(), e);
    }
  }
}
