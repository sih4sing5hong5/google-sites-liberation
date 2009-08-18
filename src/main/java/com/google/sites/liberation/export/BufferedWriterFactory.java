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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Implements {@link AppendableFactory} to provide a 
 * {@code BufferedWriter} for a given file name.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
final class BufferedWriterFactory implements AppendableFactory {
  
  @Override
  public Appendable getAppendable(File file) throws IOException {
    checkNotNull(file);
    return new BufferedWriter(new OutputStreamWriter(
        new FileOutputStream(file), "UTF-8"));
  }
}
