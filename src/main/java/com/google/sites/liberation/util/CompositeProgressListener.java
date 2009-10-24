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

import java.util.List;

/**
 * Listener that notifies multiple progress listeners.
 *
 * @author jlueck@google.com (JJ Lueck)
 */
public class CompositeProgressListener implements ProgressListener {

  private final ProgressListener[] listeners;
  private double progress;
  private String status;

  public CompositeProgressListener(ProgressListener... listeners) {
    this.listeners = listeners;
  }

  @Override
  public void setProgress(double progress) {
    this.progress = progress;
    for (ProgressListener listener : listeners) {
      listener.setProgress(progress);
    }
  }

  @Override
  public void setStatus(String status) {
    this.status = status;
    for (ProgressListener listener : listeners) {
      listener.setStatus(status);
    }
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
