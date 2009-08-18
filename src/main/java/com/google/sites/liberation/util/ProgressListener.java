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
 * Listener that responds to the progress of an operation.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
public interface ProgressListener {
  
  /**
   * Returns the status of the operation as a String.
   */
  String getStatus();
  
  /**
   * Returns the progress of the operation.
   */
  double getProgress();
  
  /**
   * Sets the status of the operation to the given String.
   */
  void setStatus(String status);
  
  /**
   * Sets the progress of the operation to the given double between 0.0 and 1.0.
   */
  void setProgress(double progress);
}
