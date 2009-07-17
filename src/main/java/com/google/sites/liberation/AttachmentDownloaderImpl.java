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

package com.google.sites.liberation;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.gdata.data.sites.AttachmentEntry;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * This class implements {@link AttachmentDownloader} to download an attachment
 * to a specified file.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
public class AttachmentDownloaderImpl implements AttachmentDownloader {
  
  /**
   * Downloads the given attachment to the given file name.
   */
  @Override
  public void download(AttachmentEntry attachment, String fileName)
      throws IOException {
    checkNotNull(attachment);
    checkNotNull(fileName);
    URL url = new URL(attachment.getEnclosureLink().getHref());
    InputStream in = url.openStream();
    OutputStream out = new FileOutputStream(new File(fileName));
    byte[] buf = new byte[4*1024];
    int bytesRead;
    while((bytesRead = in.read(buf)) != -1) {
      out.write(buf, 0, bytesRead);
    }
    in.close();
    out.close();
  }
}
