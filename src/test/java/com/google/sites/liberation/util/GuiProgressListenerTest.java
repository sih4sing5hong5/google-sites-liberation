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

import static org.junit.Assert.*;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

import javax.swing.JProgressBar;
import javax.swing.text.JTextComponent;

/**
 * @author bsimon@google.com (Benjamin Simon)
 */
public class GuiProgressListenerTest {

  private Mockery context;
  private JProgressBar progressBar;
  private JTextComponent textComponent;
  private GuiProgressListener progressListener;
  
  @Before
  public void before() {
    context = new JUnit4Mockery() {{
      setImposteriser(ClassImposteriser.INSTANCE);
    }};
    progressBar = context.mock(JProgressBar.class);
    textComponent = context.mock(JTextComponent.class);
    progressListener = new GuiProgressListener(progressBar, textComponent);
  }

  @Test
  public void testSetProgress() {
    context.checking(new Expectations() {{
      allowing (progressBar).setIndeterminate(false);
      allowing (progressBar).getMinimum(); will(returnValue(0));
      allowing (progressBar).getMaximum(); will(returnValue(100));
      oneOf (progressBar).setValue(40);
      oneOf (progressBar).getValue(); will(returnValue(40));
    }});
    
    progressListener.setProgress(.4);
    assertTrue(Math.abs(.4 - progressListener.getProgress()) < .0001);
  }
  
  @Test
  public void testSetStatus() {
    final Sequence setSequence = context.sequence("setSequence");
    final Sequence getSequence = context.sequence("getSequence");
    
    context.checking(new Expectations() {{
      oneOf (textComponent).setText("First Status\n"); inSequence(setSequence);
      oneOf (textComponent).setText("First Status\nSecond Status\n"); 
          inSequence(setSequence);
      oneOf (textComponent).getText(); inSequence(getSequence); 
          will(returnValue(""));
      oneOf (textComponent).getText(); inSequence(getSequence); 
          will(returnValue("First Status\n"));
      oneOf (textComponent).getText(); inSequence(getSequence); 
          will(returnValue("First Status\n"));
      oneOf (textComponent).getText(); inSequence(getSequence); 
          will(returnValue("First Status\nSecondStatus\n"));
    }});
    
    progressListener.setStatus("First Status");
    assertEquals("First Status", progressListener.getStatus());
    progressListener.setStatus("Second Status");
    assertEquals("Second Status", progressListener.getStatus());
  }
}
