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

import com.google.gdata.client.sites.SitesService;
import com.google.gdata.util.AuthenticationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.sites.liberation.export.SiteExporter;
import com.google.sites.liberation.export.SiteExporterModule;
import com.google.sites.liberation.imprt.SiteImporter;
import com.google.sites.liberation.imprt.SiteImporterModule;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

/**
 * Provides a GUI for initiating a Sites import or export.
 * 
 * @author bsimon@google.com (Benjamin Simon)
 */
public class GuiMain {

  private static final Logger LOGGER = Logger.getLogger(
      GuiMain.class.getCanonicalName());
  
  private JFrame optionsFrame;
  private JFrame progressFrame;
  private JTextField hostField;
  private JTextField domainField;
  private JTextField webspaceField;
  private JTextField usernameField;
  private JTextField passwordField;
  private JTextField fileField;
  private JFileChooser fileChooser;
  private JCheckBox revisionsCheckBox;
  private JTextArea textArea;
  private JProgressBar progressBar;
  private JButton doneButton;
  
  private GuiMain() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException e) {
      LOGGER.log(Level.WARNING, "Unable to set look and feel.", e);
    } catch (InstantiationException e) {
      LOGGER.log(Level.WARNING, "Unable to set look and feel.", e);
    } catch (IllegalAccessException e) {
      LOGGER.log(Level.WARNING, "Unable to set look and feel.", e);
    } catch (UnsupportedLookAndFeelException e) {
      LOGGER.log(Level.WARNING, "Unable to set look and feel.", e);
    }
    initOptionsFrame();
    initProgressFrame();
  }
  
  private void initOptionsFrame() {
    optionsFrame = new JFrame("Sites Import/Export");
    JPanel mainPanel = new JPanel();
    GridLayout layout = new GridLayout(0, 2);
    mainPanel.setLayout(layout);
    mainPanel.add(new JLabel(" Host: "));
    hostField = new JTextField("sites.google.com");
    mainPanel.add(hostField);
    mainPanel.add(new JLabel(" Domain: "));
    domainField = new JTextField();
    mainPanel.add(domainField);
    mainPanel.add(new JLabel(" Webspace: "));
    webspaceField = new JTextField();
    mainPanel.add(webspaceField);
    mainPanel.add(new JLabel(" Import/Export Revisions: "));
    revisionsCheckBox = new JCheckBox();
    mainPanel.add(revisionsCheckBox);
    mainPanel.add(new JLabel(" Username: "));
    usernameField = new JTextField();
    mainPanel.add(usernameField);
    mainPanel.add(new JLabel(" Password: "));
    passwordField = new JPasswordField();
    mainPanel.add(passwordField);
    fileField = new JTextField();
    fileField.setEditable(false);
    fileChooser = new JFileChooser();
    JButton directoryButton = new JButton("Choose Target Directory");
    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    directoryButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        int result = fileChooser.showOpenDialog(optionsFrame);
        if (result == JFileChooser.APPROVE_OPTION) {
          fileField.setText(fileChooser.getSelectedFile().getPath());
        }
      }
    });
    mainPanel.add(directoryButton);
    mainPanel.add(fileField);
    mainPanel.add(new JPanel());
    mainPanel.add(new JPanel());
    JButton importButton = new JButton("Import to Sites");
    importButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (checkArguments()) {
          startAction(false);
        }
      }
    });
    mainPanel.add(importButton);
    JButton exportButton = new JButton("Export from Sites");
    exportButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (checkArguments()) {
          startAction(true);
        }
      }
    });
    mainPanel.add(exportButton);
    mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
    optionsFrame.getContentPane().add(mainPanel);
    optionsFrame.pack();
    optionsFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    optionsFrame.setVisible(true);
  }
  
  private void initProgressFrame() {
    progressFrame = new JFrame("Progress");    
    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BorderLayout());
    progressBar = new JProgressBar();
    progressBar.setMinimum(0);
    progressBar.setMaximum(100);
    progressBar.setPreferredSize(new Dimension(500, 25));
    JPanel progressPanel = new JPanel();
    progressPanel.add(progressBar);
    progressPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
    mainPanel.add(progressPanel, BorderLayout.NORTH);
    textArea = new JTextArea();
    textArea.setRows(20);
    textArea.setEditable(false);
    JScrollPane scrollPane = new JScrollPane(textArea);
    scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));
    mainPanel.add(scrollPane, BorderLayout.CENTER);
    doneButton = new JButton("Done");
    doneButton.setPreferredSize(new Dimension(495, 25));
    doneButton.setEnabled(false);
    doneButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        doneButton.setEnabled(false);
        progressFrame.setVisible(false);
        optionsFrame.setVisible(true);
      }
    });
    JPanel donePanel = new JPanel();
    donePanel.setLayout(new BorderLayout());
    donePanel.add(doneButton, BorderLayout.CENTER);
    donePanel.setBorder(new EmptyBorder(0, 10, 10, 10));
    mainPanel.add(donePanel, BorderLayout.SOUTH);
    progressFrame.getContentPane().add(mainPanel);
    progressFrame.pack();
    progressFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }
  
  private void startAction(boolean export) {
    optionsFrame.setVisible(false);
    progressBar.setValue(0);
    progressBar.setIndeterminate(true);
    textArea.setText("");
    progressFrame.setVisible(true);
    new Thread(new ImportExportRunnable(export)).start();
  }
  
  private boolean checkArguments() {
    if (hostField.getText().equals("")) {
      error("Please provide a host name.");
      return false;
    }
    if (webspaceField.getText().equals("")) {
      error("Please provide a webspace (site name).");
      return false;
    }
    if (usernameField.getText().equals("")) {
      error("Please provide a username.");
      return false;
    }
    if (passwordField.getText().equals("")) {
      error("Please provide a password.");
      return false;
    }
    if (fileChooser.getSelectedFile() == null) {
      error("Please provide a target directory.");
      return false;
    }
    return true;
  }
  
  private void error(String message) {
    JOptionPane.showMessageDialog(optionsFrame, message, "Error", 
        JOptionPane.ERROR_MESSAGE);
  }
  
  /**
   * Launches a new GuiMain, allowing a user to graphically initiate a Sites 
   * import or export.
   */
  public static void main(String[] args) {
    new GuiMain();
  }
  
  private class ImportExportRunnable implements Runnable {

    private boolean export;
    
    ImportExportRunnable(boolean export) {
      this.export = export;
    }
    
    @Override
    public void run() {
      String host = hostField.getText();
      String domain = (domainField.getText().equals("")) ? null 
          : domainField.getText();
      String webspace = webspaceField.getText();
      boolean revisions = revisionsCheckBox.isSelected();
      String username = usernameField.getText();
      if (domain != null && !username.contains("@")) {
        username = username + '@' + domain;
      }
      String password = passwordField.getText();
      File directory = fileChooser.getSelectedFile();
      SitesService sitesService = new SitesService("google-sites-liberation");
      try {
        sitesService.setUserCredentials(username, password);
      } catch (AuthenticationException e) {
        error("Invalid user credentials.");
        progressFrame.setVisible(false);
        optionsFrame.setVisible(true);
        return;
      }
      if (export) {
        Injector injector = Guice.createInjector(new SiteExporterModule());
        SiteExporter siteExporter = injector.getInstance(SiteExporter.class);
        siteExporter.exportSite(host, domain, webspace, revisions,
            sitesService, directory, new GuiProgressListener(progressBar, textArea));
      } else {
        Injector injector = Guice.createInjector(new SiteImporterModule());
        SiteImporter siteImporter = injector.getInstance(SiteImporter.class);
        siteImporter.importSite(host, domain, webspace, revisions,
            sitesService, directory, new GuiProgressListener(progressBar, textArea));
      }
      
      doneButton.setEnabled(true);
    }
  }
}
