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

package com.google.sites.liberation.util;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Listener that logs status messages to a file and ignores progress updates.
 *
 * @author jlueck@google.com (JJ Lueck)
 */
public class LoggingProgressListener implements ProgressListener {

  private static final Logger LOG =
      Logger.getLogger(LoggingProgressListener.class.getCanonicalName());
  private double progress;
  private String status;

  /*
  public LoggingProgressListener(String logfile) throws IOException {
    FileHandler handler = new FileHandler(logfile);
    handler.setFormatter(new Formatter() {
        public String format(LogRecord record) {
          return record.getMessage() + "\n";
        }
      });
    LOG.addHandler(handler);
  }
  */

  @Override
  public void setProgress(double progress) {
    this.progress = progress;
  }

  @Override
  public void setStatus(String status) {
    this.status = status;
    LOG.info(status);
  }

  @Override
  public double getProgress() {
    return progress;
  }

  @Override
  public String getStatus() {
    return status;
  }
}
