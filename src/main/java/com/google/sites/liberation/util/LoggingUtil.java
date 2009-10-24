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

import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.LogManager;
import java.util.logging.SimpleFormatter;

import java.io.File;
import java.io.IOException;

public class LoggingUtil {

  /**
   * Initializes the logging infrastructure to log to a file.
   */
  public static void initializeFileLogger(File logfile) throws IOException {
    LogManager.getLogManager().reset();
    FileHandler handler = new FileHandler(logfile.getPath());
    handler.setFormatter(new SimpleFormatter());
    Logger logger = Logger.getLogger("com.google.sites.liberation");
    logger.addHandler(handler);
 }
}
