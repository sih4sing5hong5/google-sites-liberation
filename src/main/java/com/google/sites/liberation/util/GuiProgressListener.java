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

import static com.google.common.base.Preconditions.checkNotNull;

import javax.swing.JProgressBar;
import javax.swing.text.JTextComponent;

/**
 * Graphical implementation of {@link ProgressListener}, which updates a 
 * JProgressBar and JTextComponent.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
public class GuiProgressListener implements ProgressListener {

  private double progress;
  private String status;
  private JProgressBar progressBar;
  private JTextComponent textComponent;
  
  /**
   * Creates a new GuiProgressListener that updates the given progress bar
   * and text component.
   */
  GuiProgressListener(JProgressBar progressBar, 
      JTextComponent textComponent) {
    this.progressBar = checkNotNull(progressBar);
    this.textComponent = checkNotNull(textComponent);
  }
  
  @Override
  public void setProgress(double progress) {
    this.progress = progress;
    progressBar.setIndeterminate(false);
    int min = progressBar.getMinimum();
    int max = progressBar.getMaximum();
    int val = (int) (min + ((max - min) * progress));
    progressBar.setValue(val);
  }

  @Override
  public void setStatus(String status) {
    this.status = checkNotNull(status);
    textComponent.setText(textComponent.getText() + status + "\n");
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
