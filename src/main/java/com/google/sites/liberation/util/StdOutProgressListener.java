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

/**
 * Implementation of ProgressListener that uses standard out to report the
 * progress of an operation.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
public class StdOutProgressListener implements ProgressListener {

  private double progress;
  private String status;
  
  @Override
  public void setProgress(double progress) {
    this.progress = progress;
    System.out.println("Current progress: " + (int)(progress*100) + "%.");
  }

  @Override
  public void setStatus(String status) {
    this.status = status;
    System.out.println(status);
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
