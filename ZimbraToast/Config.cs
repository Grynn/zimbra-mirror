/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Toaster
 * Copyright (C) 2006, 2007, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
﻿using System;
using System.IO;
using System.Xml;
using System.Drawing;
using System.Threading;
using System.Collections;
using System.Windows.Forms;
using System.ComponentModel;
using System.Runtime.InteropServices;
using Microsoft.Win32; //need it for mailto registration



namespace Zimbra.Toast
{
	/// <summary>
	/// The Config form is the main form of the application.
	/// </summary>
	public class Config : System.Windows.Forms.Form
	{
		private System.Windows.Forms.TabPage ConfigrationTabPage;
		private System.Windows.Forms.TabControl ConfigurationTabControl;
		private System.Windows.Forms.GroupBox ServerConnectionGroupBox;
		private System.Windows.Forms.Label ServerNameLabel;
		private System.Windows.Forms.CheckBox UseSecureConnectionCheckBox;
		private System.Windows.Forms.TextBox ServerNameTextBox;
		private System.Windows.Forms.NotifyIcon TrayIcon;
		private System.Windows.Forms.ContextMenu TrayMenu;
		private System.Windows.Forms.MenuItem ShowWindowMenuItem;
		private System.Windows.Forms.MenuItem ExitMenuItem;
		private System.Windows.Forms.MenuItem CheckNowMenuItem;
		private System.Windows.Forms.Button OK_Button;
		private System.ComponentModel.IContainer components;
		private System.Windows.Forms.GroupBox ZimbraAccountGroupBox;
		private System.Windows.Forms.TextBox AccountTextBox;
		private System.Windows.Forms.Label AccountLabel;
		private System.Windows.Forms.TextBox PasswordTextBox;
		private System.Windows.Forms.Label PasswordLabel;
		private System.Windows.Forms.TextBox VerifyPasswordTextBox;
		private System.Windows.Forms.Label VerifyPasswordLabel;
		private System.Windows.Forms.ToolTip DefaultToolTip;
		private System.Windows.Forms.GroupBox groupBox2;
		private System.Windows.Forms.Button SoundFileBrowseButton;
		private System.Windows.Forms.TextBox SoundFileTextBox;
		private System.Windows.Forms.CheckBox PlaySoundCheckBox;
		private System.Windows.Forms.Button PlaySoundButton;
		private System.Windows.Forms.MenuItem ShowNewMessagesMenuItem;
		private System.Windows.Forms.TabPage GoodiesTabPage;
		private System.Windows.Forms.GroupBox AdvancedGroupBox;
		private System.Windows.Forms.Button RegisterMailto;
		private System.Windows.Forms.Label ClickURLLabel;
		private System.Windows.Forms.NumericUpDown PollingIntervalUpDown;
		private System.Windows.Forms.Label PollIntervalUnitsLabel;
		private System.Windows.Forms.Label PollIntervalLabel;
		private System.Windows.Forms.GroupBox FadeControlGroupBox;
		private System.Windows.Forms.Label UpdateIntervalLabel;
		private System.Windows.Forms.Label OpacityDeltaLabel;
		private System.Windows.Forms.Label MaxOpacityLabel;
		private System.Windows.Forms.Label PauseIntervalLabel;
		private System.Windows.Forms.NumericUpDown OpacityDeltaNumericUpDown;
		private System.Windows.Forms.NumericUpDown UpdateIntervalNumericUpDown;
		private System.Windows.Forms.NumericUpDown PauseIntervalNumericUpDown;
		private System.Windows.Forms.Button ViewToasterButton;
		private System.Windows.Forms.Label PauseIntervalUnitsLabel;
		private System.Windows.Forms.Label UpdateIntervalUnitsLabel;
		
		private ToastConfig		toastConfig				= null;
		private ZimbraSession	zimbraSession			= null;
		private MailboxMonitor	mailboxMonitor			= null;
		
		private int				currentMsgIdx			= 0;
		private ToastForm		toastForm				= null;
		private AutoResetEvent	displayCompletionEvent	= null;
		private System.Windows.Forms.NumericUpDown MaxOpacityNumericUpDown;
		private System.Windows.Forms.ComboBox clickActionComboBox;

		private Zimbra.Client.MessageSummary[] msgSummaries = null;

		private enum CLICK_ACTIONS { OPEN_IN_HTML, OPEN_IN_AJAX, MAX  };
		private String[] clickActionURLs = { 
			"/zimbra/h/search?action=view&sq=item:{0}",
		   "/zimbra/mail?view=msg&id={0}"
		};
		

		/// <summary>
		/// Default constructor
		/// </summary>
		public Config()
		{
			InitializeComponent();
		}


		/// <summary>
		/// Clean up any resources being used.
		/// </summary>
		protected override void Dispose( bool disposing )
		{
			if( disposing )
			{
				if(components != null)
				{
					components.Dispose();
				}
			}
			base.Dispose( disposing );
		}

		#region Windows Form Designer generated code
		/// <summary>
		/// Required method for Designer support - do not modify
		/// the contents of this method with the code editor.
		/// </summary>
		private void InitializeComponent()
		{
			this.components = new System.ComponentModel.Container();
			System.Resources.ResourceManager resources = new System.Resources.ResourceManager(typeof(Config));
			this.ConfigurationTabControl = new System.Windows.Forms.TabControl();
			this.ConfigrationTabPage = new System.Windows.Forms.TabPage();
			this.AdvancedGroupBox = new System.Windows.Forms.GroupBox();
			this.clickActionComboBox = new System.Windows.Forms.ComboBox();
			this.RegisterMailto = new System.Windows.Forms.Button();
			this.ClickURLLabel = new System.Windows.Forms.Label();
			this.PollingIntervalUpDown = new System.Windows.Forms.NumericUpDown();
			this.PollIntervalUnitsLabel = new System.Windows.Forms.Label();
			this.PollIntervalLabel = new System.Windows.Forms.Label();
			this.ZimbraAccountGroupBox = new System.Windows.Forms.GroupBox();
			this.VerifyPasswordTextBox = new System.Windows.Forms.TextBox();
			this.VerifyPasswordLabel = new System.Windows.Forms.Label();
			this.PasswordTextBox = new System.Windows.Forms.TextBox();
			this.PasswordLabel = new System.Windows.Forms.Label();
			this.AccountTextBox = new System.Windows.Forms.TextBox();
			this.AccountLabel = new System.Windows.Forms.Label();
			this.ServerConnectionGroupBox = new System.Windows.Forms.GroupBox();
			this.ServerNameTextBox = new System.Windows.Forms.TextBox();
			this.UseSecureConnectionCheckBox = new System.Windows.Forms.CheckBox();
			this.ServerNameLabel = new System.Windows.Forms.Label();
			this.GoodiesTabPage = new System.Windows.Forms.TabPage();
			this.FadeControlGroupBox = new System.Windows.Forms.GroupBox();
			this.PauseIntervalUnitsLabel = new System.Windows.Forms.Label();
			this.UpdateIntervalUnitsLabel = new System.Windows.Forms.Label();
			this.ViewToasterButton = new System.Windows.Forms.Button();
			this.PauseIntervalNumericUpDown = new System.Windows.Forms.NumericUpDown();
			this.UpdateIntervalNumericUpDown = new System.Windows.Forms.NumericUpDown();
			this.OpacityDeltaNumericUpDown = new System.Windows.Forms.NumericUpDown();
			this.MaxOpacityNumericUpDown = new System.Windows.Forms.NumericUpDown();
			this.PauseIntervalLabel = new System.Windows.Forms.Label();
			this.UpdateIntervalLabel = new System.Windows.Forms.Label();
			this.OpacityDeltaLabel = new System.Windows.Forms.Label();
			this.MaxOpacityLabel = new System.Windows.Forms.Label();
			this.groupBox2 = new System.Windows.Forms.GroupBox();
			this.PlaySoundButton = new System.Windows.Forms.Button();
			this.PlaySoundCheckBox = new System.Windows.Forms.CheckBox();
			this.SoundFileBrowseButton = new System.Windows.Forms.Button();
			this.SoundFileTextBox = new System.Windows.Forms.TextBox();
			this.OK_Button = new System.Windows.Forms.Button();
			this.TrayIcon = new System.Windows.Forms.NotifyIcon(this.components);
			this.TrayMenu = new System.Windows.Forms.ContextMenu();
			this.CheckNowMenuItem = new System.Windows.Forms.MenuItem();
			this.ShowNewMessagesMenuItem = new System.Windows.Forms.MenuItem();
			this.ShowWindowMenuItem = new System.Windows.Forms.MenuItem();
			this.ExitMenuItem = new System.Windows.Forms.MenuItem();
			this.DefaultToolTip = new System.Windows.Forms.ToolTip(this.components);
			this.ConfigurationTabControl.SuspendLayout();
			this.ConfigrationTabPage.SuspendLayout();
			this.AdvancedGroupBox.SuspendLayout();
			((System.ComponentModel.ISupportInitialize)(this.PollingIntervalUpDown)).BeginInit();
			this.ZimbraAccountGroupBox.SuspendLayout();
			this.ServerConnectionGroupBox.SuspendLayout();
			this.GoodiesTabPage.SuspendLayout();
			this.FadeControlGroupBox.SuspendLayout();
			((System.ComponentModel.ISupportInitialize)(this.PauseIntervalNumericUpDown)).BeginInit();
			((System.ComponentModel.ISupportInitialize)(this.UpdateIntervalNumericUpDown)).BeginInit();
			((System.ComponentModel.ISupportInitialize)(this.OpacityDeltaNumericUpDown)).BeginInit();
			((System.ComponentModel.ISupportInitialize)(this.MaxOpacityNumericUpDown)).BeginInit();
			this.groupBox2.SuspendLayout();
			this.SuspendLayout();
			// 
			// ConfigurationTabControl
			// 
			this.ConfigurationTabControl.Controls.Add(this.ConfigrationTabPage);
			this.ConfigurationTabControl.Controls.Add(this.GoodiesTabPage);
			this.ConfigurationTabControl.Location = new System.Drawing.Point(6, 8);
			this.ConfigurationTabControl.Name = "ConfigurationTabControl";
			this.ConfigurationTabControl.SelectedIndex = 0;
			this.ConfigurationTabControl.Size = new System.Drawing.Size(352, 372);
			this.ConfigurationTabControl.TabIndex = 0;
			this.ConfigurationTabControl.TabStop = false;
			// 
			// ConfigrationTabPage
			// 
			this.ConfigrationTabPage.BackColor = System.Drawing.SystemColors.ControlLightLight;
			this.ConfigrationTabPage.Controls.Add(this.AdvancedGroupBox);
			this.ConfigrationTabPage.Controls.Add(this.ZimbraAccountGroupBox);
			this.ConfigrationTabPage.Controls.Add(this.ServerConnectionGroupBox);
			this.ConfigrationTabPage.Location = new System.Drawing.Point(4, 22);
			this.ConfigrationTabPage.Name = "ConfigrationTabPage";
			this.ConfigrationTabPage.Size = new System.Drawing.Size(344, 346);
			this.ConfigrationTabPage.TabIndex = 0;
			this.ConfigrationTabPage.Text = "Configuration";
			// 
			// AdvancedGroupBox
			// 
			this.AdvancedGroupBox.BackColor = System.Drawing.SystemColors.ControlLightLight;
			this.AdvancedGroupBox.Controls.Add(this.clickActionComboBox);
			this.AdvancedGroupBox.Controls.Add(this.RegisterMailto);
			this.AdvancedGroupBox.Controls.Add(this.ClickURLLabel);
			this.AdvancedGroupBox.Controls.Add(this.PollingIntervalUpDown);
			this.AdvancedGroupBox.Controls.Add(this.PollIntervalUnitsLabel);
			this.AdvancedGroupBox.Controls.Add(this.PollIntervalLabel);
			this.AdvancedGroupBox.FlatStyle = System.Windows.Forms.FlatStyle.System;
			this.AdvancedGroupBox.Location = new System.Drawing.Point(14, 214);
			this.AdvancedGroupBox.Name = "AdvancedGroupBox";
			this.AdvancedGroupBox.Size = new System.Drawing.Size(316, 112);
			this.AdvancedGroupBox.TabIndex = 6;
			this.AdvancedGroupBox.TabStop = false;
			this.AdvancedGroupBox.Text = "Advanced";
			// 
			// clickActionComboBox
			// 
			this.clickActionComboBox.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
			this.clickActionComboBox.Items.AddRange(new object[] {
																	 "Open Item in HTML Client",
																	 "Open Item in AJAX Client"});
			this.clickActionComboBox.Location = new System.Drawing.Point(102, 48);
			this.clickActionComboBox.Name = "clickActionComboBox";
			this.clickActionComboBox.Size = new System.Drawing.Size(202, 21);
			this.clickActionComboBox.TabIndex = 11;
			// 
			// RegisterMailto
			// 
			this.RegisterMailto.BackColor = System.Drawing.SystemColors.Control;
			this.RegisterMailto.FlatStyle = System.Windows.Forms.FlatStyle.System;
			this.RegisterMailto.Location = new System.Drawing.Point(102, 76);
			this.RegisterMailto.Name = "RegisterMailto";
			this.RegisterMailto.Size = new System.Drawing.Size(202, 23);
			this.RegisterMailto.TabIndex = 10;
			this.RegisterMailto.Text = "Register Mailto Handler";
			this.RegisterMailto.Click += new System.EventHandler(this.RegisterMailto_Click);
			// 
			// ClickURLLabel
			// 
			this.ClickURLLabel.BackColor = System.Drawing.SystemColors.ControlLightLight;
			this.ClickURLLabel.FlatStyle = System.Windows.Forms.FlatStyle.System;
			this.ClickURLLabel.ImageAlign = System.Drawing.ContentAlignment.MiddleRight;
			this.ClickURLLabel.Location = new System.Drawing.Point(10, 50);
			this.ClickURLLabel.Name = "ClickURLLabel";
			this.ClickURLLabel.Size = new System.Drawing.Size(84, 16);
			this.ClickURLLabel.TabIndex = 6;
			this.ClickURLLabel.Text = "Click Action";
			this.ClickURLLabel.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
			// 
			// PollingIntervalUpDown
			// 
			this.PollingIntervalUpDown.Location = new System.Drawing.Point(102, 22);
			this.PollingIntervalUpDown.Maximum = new System.Decimal(new int[] {
																				  30,
																				  0,
																				  0,
																				  0});
			this.PollingIntervalUpDown.Minimum = new System.Decimal(new int[] {
																				  1,
																				  0,
																				  0,
																				  0});
			this.PollingIntervalUpDown.Name = "PollingIntervalUpDown";
			this.PollingIntervalUpDown.Size = new System.Drawing.Size(46, 20);
			this.PollingIntervalUpDown.TabIndex = 5;
			this.PollingIntervalUpDown.TextAlign = System.Windows.Forms.HorizontalAlignment.Center;
			this.DefaultToolTip.SetToolTip(this.PollingIntervalUpDown, "How often to check for new messages.");
			this.PollingIntervalUpDown.Value = new System.Decimal(new int[] {
																				1,
																				0,
																				0,
																				0});
			// 
			// PollIntervalUnitsLabel
			// 
			this.PollIntervalUnitsLabel.BackColor = System.Drawing.SystemColors.ControlLightLight;
			this.PollIntervalUnitsLabel.FlatStyle = System.Windows.Forms.FlatStyle.System;
			this.PollIntervalUnitsLabel.ImageAlign = System.Drawing.ContentAlignment.MiddleRight;
			this.PollIntervalUnitsLabel.Location = new System.Drawing.Point(156, 24);
			this.PollIntervalUnitsLabel.Name = "PollIntervalUnitsLabel";
			this.PollIntervalUnitsLabel.Size = new System.Drawing.Size(84, 16);
			this.PollIntervalUnitsLabel.TabIndex = 4;
			this.PollIntervalUnitsLabel.Text = "Minutes";
			this.PollIntervalUnitsLabel.TextAlign = System.Drawing.ContentAlignment.MiddleLeft;
			// 
			// PollIntervalLabel
			// 
			this.PollIntervalLabel.BackColor = System.Drawing.SystemColors.ControlLightLight;
			this.PollIntervalLabel.FlatStyle = System.Windows.Forms.FlatStyle.System;
			this.PollIntervalLabel.ImageAlign = System.Drawing.ContentAlignment.MiddleRight;
			this.PollIntervalLabel.Location = new System.Drawing.Point(10, 24);
			this.PollIntervalLabel.Name = "PollIntervalLabel";
			this.PollIntervalLabel.Size = new System.Drawing.Size(84, 16);
			this.PollIntervalLabel.TabIndex = 2;
			this.PollIntervalLabel.Text = "Poll Interval";
			this.PollIntervalLabel.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
			// 
			// ZimbraAccountGroupBox
			// 
			this.ZimbraAccountGroupBox.Controls.Add(this.VerifyPasswordTextBox);
			this.ZimbraAccountGroupBox.Controls.Add(this.VerifyPasswordLabel);
			this.ZimbraAccountGroupBox.Controls.Add(this.PasswordTextBox);
			this.ZimbraAccountGroupBox.Controls.Add(this.PasswordLabel);
			this.ZimbraAccountGroupBox.Controls.Add(this.AccountTextBox);
			this.ZimbraAccountGroupBox.Controls.Add(this.AccountLabel);
			this.ZimbraAccountGroupBox.FlatStyle = System.Windows.Forms.FlatStyle.System;
			this.ZimbraAccountGroupBox.Location = new System.Drawing.Point(12, 96);
			this.ZimbraAccountGroupBox.Name = "ZimbraAccountGroupBox";
			this.ZimbraAccountGroupBox.Size = new System.Drawing.Size(316, 110);
			this.ZimbraAccountGroupBox.TabIndex = 1;
			this.ZimbraAccountGroupBox.TabStop = false;
			this.ZimbraAccountGroupBox.Text = "Zimbra Account";
			// 
			// VerifyPasswordTextBox
			// 
			this.VerifyPasswordTextBox.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((System.Byte)(0)));
			this.VerifyPasswordTextBox.Location = new System.Drawing.Point(102, 74);
			this.VerifyPasswordTextBox.Name = "VerifyPasswordTextBox";
			this.VerifyPasswordTextBox.PasswordChar = '*';
			this.VerifyPasswordTextBox.Size = new System.Drawing.Size(202, 20);
			this.VerifyPasswordTextBox.TabIndex = 7;
			this.VerifyPasswordTextBox.Text = "";
			this.DefaultToolTip.SetToolTip(this.VerifyPasswordTextBox, "Your Zimbra account password.");
			this.VerifyPasswordTextBox.KeyUp += new System.Windows.Forms.KeyEventHandler(this.Configuration_TextFieldKeyPress);
			// 
			// VerifyPasswordLabel
			// 
			this.VerifyPasswordLabel.FlatStyle = System.Windows.Forms.FlatStyle.System;
			this.VerifyPasswordLabel.ImageAlign = System.Drawing.ContentAlignment.MiddleRight;
			this.VerifyPasswordLabel.Location = new System.Drawing.Point(10, 76);
			this.VerifyPasswordLabel.Name = "VerifyPasswordLabel";
			this.VerifyPasswordLabel.Size = new System.Drawing.Size(84, 16);
			this.VerifyPasswordLabel.TabIndex = 6;
			this.VerifyPasswordLabel.Text = "Verify Password";
			this.VerifyPasswordLabel.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
			// 
			// PasswordTextBox
			// 
			this.PasswordTextBox.Location = new System.Drawing.Point(102, 48);
			this.PasswordTextBox.Name = "PasswordTextBox";
			this.PasswordTextBox.PasswordChar = '*';
			this.PasswordTextBox.Size = new System.Drawing.Size(202, 20);
			this.PasswordTextBox.TabIndex = 5;
			this.PasswordTextBox.Text = "";
			this.DefaultToolTip.SetToolTip(this.PasswordTextBox, "Your Zimbra account password.");
			this.PasswordTextBox.KeyUp += new System.Windows.Forms.KeyEventHandler(this.Configuration_TextFieldKeyPress);
			// 
			// PasswordLabel
			// 
			this.PasswordLabel.FlatStyle = System.Windows.Forms.FlatStyle.System;
			this.PasswordLabel.ImageAlign = System.Drawing.ContentAlignment.MiddleRight;
			this.PasswordLabel.Location = new System.Drawing.Point(10, 50);
			this.PasswordLabel.Name = "PasswordLabel";
			this.PasswordLabel.Size = new System.Drawing.Size(84, 16);
			this.PasswordLabel.TabIndex = 4;
			this.PasswordLabel.Text = "Password";
			this.PasswordLabel.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
			// 
			// AccountTextBox
			// 
			this.AccountTextBox.Location = new System.Drawing.Point(102, 22);
			this.AccountTextBox.Name = "AccountTextBox";
			this.AccountTextBox.Size = new System.Drawing.Size(202, 20);
			this.AccountTextBox.TabIndex = 3;
			this.AccountTextBox.Text = "";
			this.DefaultToolTip.SetToolTip(this.AccountTextBox, "Your Zimbra account name.  Example me@zimbra.company.com");
			this.AccountTextBox.KeyUp += new System.Windows.Forms.KeyEventHandler(this.Configuration_TextFieldKeyPress);
			// 
			// AccountLabel
			// 
			this.AccountLabel.FlatStyle = System.Windows.Forms.FlatStyle.System;
			this.AccountLabel.ImageAlign = System.Drawing.ContentAlignment.MiddleRight;
			this.AccountLabel.Location = new System.Drawing.Point(10, 24);
			this.AccountLabel.Name = "AccountLabel";
			this.AccountLabel.Size = new System.Drawing.Size(84, 16);
			this.AccountLabel.TabIndex = 2;
			this.AccountLabel.Text = "Account";
			this.AccountLabel.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
			// 
			// ServerConnectionGroupBox
			// 
			this.ServerConnectionGroupBox.Controls.Add(this.ServerNameTextBox);
			this.ServerConnectionGroupBox.Controls.Add(this.UseSecureConnectionCheckBox);
			this.ServerConnectionGroupBox.Controls.Add(this.ServerNameLabel);
			this.ServerConnectionGroupBox.FlatStyle = System.Windows.Forms.FlatStyle.System;
			this.ServerConnectionGroupBox.Location = new System.Drawing.Point(12, 12);
			this.ServerConnectionGroupBox.Name = "ServerConnectionGroupBox";
			this.ServerConnectionGroupBox.Size = new System.Drawing.Size(316, 78);
			this.ServerConnectionGroupBox.TabIndex = 0;
			this.ServerConnectionGroupBox.TabStop = false;
			this.ServerConnectionGroupBox.Text = "Zimbra Server Connection";
			// 
			// ServerNameTextBox
			// 
			this.ServerNameTextBox.Location = new System.Drawing.Point(102, 23);
			this.ServerNameTextBox.Name = "ServerNameTextBox";
			this.ServerNameTextBox.Size = new System.Drawing.Size(202, 20);
			this.ServerNameTextBox.TabIndex = 1;
			this.ServerNameTextBox.Text = "";
			this.DefaultToolTip.SetToolTip(this.ServerNameTextBox, "The name of yoru Zimbra server. Example zimbra.company.com");
			this.ServerNameTextBox.KeyUp += new System.Windows.Forms.KeyEventHandler(this.Configuration_TextFieldKeyPress);
			// 
			// UseSecureConnectionCheckBox
			// 
			this.UseSecureConnectionCheckBox.Checked = true;
			this.UseSecureConnectionCheckBox.CheckState = System.Windows.Forms.CheckState.Checked;
			this.UseSecureConnectionCheckBox.FlatStyle = System.Windows.Forms.FlatStyle.System;
			this.UseSecureConnectionCheckBox.Location = new System.Drawing.Point(102, 48);
			this.UseSecureConnectionCheckBox.Name = "UseSecureConnectionCheckBox";
			this.UseSecureConnectionCheckBox.Size = new System.Drawing.Size(182, 18);
			this.UseSecureConnectionCheckBox.TabIndex = 2;
			this.UseSecureConnectionCheckBox.Text = "Use Secure Connection";
			this.DefaultToolTip.SetToolTip(this.UseSecureConnectionCheckBox, "Use a secure connection when communication with your Zimbra server");
			// 
			// ServerNameLabel
			// 
			this.ServerNameLabel.FlatStyle = System.Windows.Forms.FlatStyle.System;
			this.ServerNameLabel.ImageAlign = System.Drawing.ContentAlignment.MiddleRight;
			this.ServerNameLabel.Location = new System.Drawing.Point(10, 26);
			this.ServerNameLabel.Name = "ServerNameLabel";
			this.ServerNameLabel.Size = new System.Drawing.Size(84, 16);
			this.ServerNameLabel.TabIndex = 0;
			this.ServerNameLabel.Text = "Server Name";
			this.ServerNameLabel.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
			// 
			// GoodiesTabPage
			// 
			this.GoodiesTabPage.BackColor = System.Drawing.SystemColors.ControlLightLight;
			this.GoodiesTabPage.Controls.Add(this.FadeControlGroupBox);
			this.GoodiesTabPage.Controls.Add(this.groupBox2);
			this.GoodiesTabPage.Location = new System.Drawing.Point(4, 22);
			this.GoodiesTabPage.Name = "GoodiesTabPage";
			this.GoodiesTabPage.Size = new System.Drawing.Size(344, 346);
			this.GoodiesTabPage.TabIndex = 1;
			this.GoodiesTabPage.Text = "Goodies";
			// 
			// FadeControlGroupBox
			// 
			this.FadeControlGroupBox.Controls.Add(this.PauseIntervalUnitsLabel);
			this.FadeControlGroupBox.Controls.Add(this.UpdateIntervalUnitsLabel);
			this.FadeControlGroupBox.Controls.Add(this.ViewToasterButton);
			this.FadeControlGroupBox.Controls.Add(this.PauseIntervalNumericUpDown);
			this.FadeControlGroupBox.Controls.Add(this.UpdateIntervalNumericUpDown);
			this.FadeControlGroupBox.Controls.Add(this.OpacityDeltaNumericUpDown);
			this.FadeControlGroupBox.Controls.Add(this.MaxOpacityNumericUpDown);
			this.FadeControlGroupBox.Controls.Add(this.PauseIntervalLabel);
			this.FadeControlGroupBox.Controls.Add(this.UpdateIntervalLabel);
			this.FadeControlGroupBox.Controls.Add(this.OpacityDeltaLabel);
			this.FadeControlGroupBox.Controls.Add(this.MaxOpacityLabel);
			this.FadeControlGroupBox.FlatStyle = System.Windows.Forms.FlatStyle.System;
			this.FadeControlGroupBox.Location = new System.Drawing.Point(12, 128);
			this.FadeControlGroupBox.Name = "FadeControlGroupBox";
			this.FadeControlGroupBox.Size = new System.Drawing.Size(316, 180);
			this.FadeControlGroupBox.TabIndex = 5;
			this.FadeControlGroupBox.TabStop = false;
			this.FadeControlGroupBox.Text = "Fade Control";
			// 
			// PauseIntervalUnitsLabel
			// 
			this.PauseIntervalUnitsLabel.FlatStyle = System.Windows.Forms.FlatStyle.System;
			this.PauseIntervalUnitsLabel.ImageAlign = System.Drawing.ContentAlignment.MiddleRight;
			this.PauseIntervalUnitsLabel.Location = new System.Drawing.Point(247, 112);
			this.PauseIntervalUnitsLabel.Name = "PauseIntervalUnitsLabel";
			this.PauseIntervalUnitsLabel.Size = new System.Drawing.Size(34, 16);
			this.PauseIntervalUnitsLabel.TabIndex = 23;
			this.PauseIntervalUnitsLabel.Text = "(ms)";
			this.PauseIntervalUnitsLabel.TextAlign = System.Drawing.ContentAlignment.MiddleLeft;
			// 
			// UpdateIntervalUnitsLabel
			// 
			this.UpdateIntervalUnitsLabel.FlatStyle = System.Windows.Forms.FlatStyle.System;
			this.UpdateIntervalUnitsLabel.ImageAlign = System.Drawing.ContentAlignment.MiddleRight;
			this.UpdateIntervalUnitsLabel.Location = new System.Drawing.Point(247, 84);
			this.UpdateIntervalUnitsLabel.Name = "UpdateIntervalUnitsLabel";
			this.UpdateIntervalUnitsLabel.Size = new System.Drawing.Size(34, 16);
			this.UpdateIntervalUnitsLabel.TabIndex = 22;
			this.UpdateIntervalUnitsLabel.Text = "(ms)";
			this.UpdateIntervalUnitsLabel.TextAlign = System.Drawing.ContentAlignment.MiddleLeft;
			// 
			// ViewToasterButton
			// 
			this.ViewToasterButton.BackColor = System.Drawing.SystemColors.Control;
			this.ViewToasterButton.FlatStyle = System.Windows.Forms.FlatStyle.System;
			this.ViewToasterButton.Location = new System.Drawing.Point(153, 138);
			this.ViewToasterButton.Name = "ViewToasterButton";
			this.ViewToasterButton.Size = new System.Drawing.Size(90, 23);
			this.ViewToasterButton.TabIndex = 19;
			this.ViewToasterButton.Text = "View Toaster";
			this.ViewToasterButton.Click += new System.EventHandler(this.ViewToasterButton_Click);
			// 
			// PauseIntervalNumericUpDown
			// 
			this.PauseIntervalNumericUpDown.Location = new System.Drawing.Point(123, 110);
			this.PauseIntervalNumericUpDown.Maximum = new System.Decimal(new int[] {
																					   1000000,
																					   0,
																					   0,
																					   0});
			this.PauseIntervalNumericUpDown.Minimum = new System.Decimal(new int[] {
																					   1,
																					   0,
																					   0,
																					   0});
			this.PauseIntervalNumericUpDown.Name = "PauseIntervalNumericUpDown";
			this.PauseIntervalNumericUpDown.TabIndex = 18;
			this.DefaultToolTip.SetToolTip(this.PauseIntervalNumericUpDown, "How long to pause the toaster after fading in.");
			this.PauseIntervalNumericUpDown.Value = new System.Decimal(new int[] {
																					 1,
																					 0,
																					 0,
																					 0});
			// 
			// UpdateIntervalNumericUpDown
			// 
			this.UpdateIntervalNumericUpDown.Location = new System.Drawing.Point(123, 82);
			this.UpdateIntervalNumericUpDown.Maximum = new System.Decimal(new int[] {
																						10000,
																						0,
																						0,
																						0});
			this.UpdateIntervalNumericUpDown.Minimum = new System.Decimal(new int[] {
																						1,
																						0,
																						0,
																						0});
			this.UpdateIntervalNumericUpDown.Name = "UpdateIntervalNumericUpDown";
			this.UpdateIntervalNumericUpDown.TabIndex = 17;
			this.DefaultToolTip.SetToolTip(this.UpdateIntervalNumericUpDown, "How often to update the opacity of the toaster when fading in or out.");
			this.UpdateIntervalNumericUpDown.Value = new System.Decimal(new int[] {
																					  100,
																					  0,
																					  0,
																					  0});
			// 
			// OpacityDeltaNumericUpDown
			// 
			this.OpacityDeltaNumericUpDown.DecimalPlaces = 3;
			this.OpacityDeltaNumericUpDown.Increment = new System.Decimal(new int[] {
																						1,
																						0,
																						0,
																						196608});
			this.OpacityDeltaNumericUpDown.Location = new System.Drawing.Point(123, 56);
			this.OpacityDeltaNumericUpDown.Maximum = new System.Decimal(new int[] {
																					  100,
																					  0,
																					  0,
																					  131072});
			this.OpacityDeltaNumericUpDown.Minimum = new System.Decimal(new int[] {
																					  1,
																					  0,
																					  0,
																					  196608});
			this.OpacityDeltaNumericUpDown.Name = "OpacityDeltaNumericUpDown";
			this.OpacityDeltaNumericUpDown.TabIndex = 16;
			this.DefaultToolTip.SetToolTip(this.OpacityDeltaNumericUpDown, "Change in opacity when toaster is fading in or out.");
			this.OpacityDeltaNumericUpDown.Value = new System.Decimal(new int[] {
																					1,
																					0,
																					0,
																					196608});
			// 
			// MaxOpacityNumericUpDown
			// 
			this.MaxOpacityNumericUpDown.DecimalPlaces = 2;
			this.MaxOpacityNumericUpDown.Increment = new System.Decimal(new int[] {
																					  1,
																					  0,
																					  0,
																					  131072});
			this.MaxOpacityNumericUpDown.Location = new System.Drawing.Point(123, 30);
			this.MaxOpacityNumericUpDown.Maximum = new System.Decimal(new int[] {
																					10,
																					0,
																					0,
																					65536});
			this.MaxOpacityNumericUpDown.Minimum = new System.Decimal(new int[] {
																					5,
																					0,
																					0,
																					65536});
			this.MaxOpacityNumericUpDown.Name = "MaxOpacityNumericUpDown";
			this.MaxOpacityNumericUpDown.TabIndex = 15;
			this.DefaultToolTip.SetToolTip(this.MaxOpacityNumericUpDown, "Maximum opacity when fading in the toaster.");
			this.MaxOpacityNumericUpDown.Value = new System.Decimal(new int[] {
																				  53,
																				  0,
																				  0,
																				  131072});
			// 
			// PauseIntervalLabel
			// 
			this.PauseIntervalLabel.FlatStyle = System.Windows.Forms.FlatStyle.System;
			this.PauseIntervalLabel.ImageAlign = System.Drawing.ContentAlignment.MiddleRight;
			this.PauseIntervalLabel.Location = new System.Drawing.Point(32, 112);
			this.PauseIntervalLabel.Name = "PauseIntervalLabel";
			this.PauseIntervalLabel.Size = new System.Drawing.Size(84, 16);
			this.PauseIntervalLabel.TabIndex = 14;
			this.PauseIntervalLabel.Text = "Pause Interval";
			this.PauseIntervalLabel.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
			// 
			// UpdateIntervalLabel
			// 
			this.UpdateIntervalLabel.FlatStyle = System.Windows.Forms.FlatStyle.System;
			this.UpdateIntervalLabel.ImageAlign = System.Drawing.ContentAlignment.MiddleRight;
			this.UpdateIntervalLabel.Location = new System.Drawing.Point(32, 84);
			this.UpdateIntervalLabel.Name = "UpdateIntervalLabel";
			this.UpdateIntervalLabel.Size = new System.Drawing.Size(84, 16);
			this.UpdateIntervalLabel.TabIndex = 12;
			this.UpdateIntervalLabel.Text = "Update Interval";
			this.UpdateIntervalLabel.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
			// 
			// OpacityDeltaLabel
			// 
			this.OpacityDeltaLabel.FlatStyle = System.Windows.Forms.FlatStyle.System;
			this.OpacityDeltaLabel.ImageAlign = System.Drawing.ContentAlignment.MiddleRight;
			this.OpacityDeltaLabel.Location = new System.Drawing.Point(32, 58);
			this.OpacityDeltaLabel.Name = "OpacityDeltaLabel";
			this.OpacityDeltaLabel.Size = new System.Drawing.Size(84, 16);
			this.OpacityDeltaLabel.TabIndex = 10;
			this.OpacityDeltaLabel.Text = "Opacity Delta";
			this.OpacityDeltaLabel.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
			// 
			// MaxOpacityLabel
			// 
			this.MaxOpacityLabel.FlatStyle = System.Windows.Forms.FlatStyle.System;
			this.MaxOpacityLabel.ImageAlign = System.Drawing.ContentAlignment.MiddleRight;
			this.MaxOpacityLabel.Location = new System.Drawing.Point(32, 32);
			this.MaxOpacityLabel.Name = "MaxOpacityLabel";
			this.MaxOpacityLabel.Size = new System.Drawing.Size(84, 16);
			this.MaxOpacityLabel.TabIndex = 8;
			this.MaxOpacityLabel.Text = "Max Opacity";
			this.MaxOpacityLabel.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
			// 
			// groupBox2
			// 
			this.groupBox2.Controls.Add(this.PlaySoundButton);
			this.groupBox2.Controls.Add(this.PlaySoundCheckBox);
			this.groupBox2.Controls.Add(this.SoundFileBrowseButton);
			this.groupBox2.Controls.Add(this.SoundFileTextBox);
			this.groupBox2.FlatStyle = System.Windows.Forms.FlatStyle.System;
			this.groupBox2.Location = new System.Drawing.Point(12, 12);
			this.groupBox2.Name = "groupBox2";
			this.groupBox2.Size = new System.Drawing.Size(316, 110);
			this.groupBox2.TabIndex = 4;
			this.groupBox2.TabStop = false;
			this.groupBox2.Text = "Sound";
			// 
			// PlaySoundButton
			// 
			this.PlaySoundButton.BackColor = System.Drawing.SystemColors.Control;
			this.PlaySoundButton.FlatStyle = System.Windows.Forms.FlatStyle.System;
			this.PlaySoundButton.Location = new System.Drawing.Point(132, 72);
			this.PlaySoundButton.Name = "PlaySoundButton";
			this.PlaySoundButton.Size = new System.Drawing.Size(80, 23);
			this.PlaySoundButton.TabIndex = 15;
			this.PlaySoundButton.Text = "Play Sound";
			this.PlaySoundButton.Click += new System.EventHandler(this.PlaySoundButton_Click);
			// 
			// PlaySoundCheckBox
			// 
			this.PlaySoundCheckBox.FlatStyle = System.Windows.Forms.FlatStyle.System;
			this.PlaySoundCheckBox.Location = new System.Drawing.Point(16, 24);
			this.PlaySoundCheckBox.Name = "PlaySoundCheckBox";
			this.PlaySoundCheckBox.Size = new System.Drawing.Size(254, 18);
			this.PlaySoundCheckBox.TabIndex = 14;
			this.PlaySoundCheckBox.Text = "Play sound when new messages arive";
			this.PlaySoundCheckBox.Click += new System.EventHandler(this.PlaySoundCheckBox_Click);
			// 
			// SoundFileBrowseButton
			// 
			this.SoundFileBrowseButton.BackColor = System.Drawing.SystemColors.Control;
			this.SoundFileBrowseButton.FlatStyle = System.Windows.Forms.FlatStyle.System;
			this.SoundFileBrowseButton.Location = new System.Drawing.Point(220, 72);
			this.SoundFileBrowseButton.Name = "SoundFileBrowseButton";
			this.SoundFileBrowseButton.Size = new System.Drawing.Size(80, 23);
			this.SoundFileBrowseButton.TabIndex = 13;
			this.SoundFileBrowseButton.Text = "Browse";
			this.SoundFileBrowseButton.Click += new System.EventHandler(this.SoundFileBrowseButton_Click);
			// 
			// SoundFileTextBox
			// 
			this.SoundFileTextBox.Location = new System.Drawing.Point(16, 46);
			this.SoundFileTextBox.Name = "SoundFileTextBox";
			this.SoundFileTextBox.ReadOnly = true;
			this.SoundFileTextBox.Size = new System.Drawing.Size(284, 20);
			this.SoundFileTextBox.TabIndex = 12;
			this.SoundFileTextBox.Text = "";
			this.DefaultToolTip.SetToolTip(this.SoundFileTextBox, "Path portion of the URL to open when an item is clicked. Use {0} to represent the" +
				" items id.");
			// 
			// OK_Button
			// 
			this.OK_Button.DialogResult = System.Windows.Forms.DialogResult.OK;
			this.OK_Button.FlatStyle = System.Windows.Forms.FlatStyle.System;
			this.OK_Button.Location = new System.Drawing.Point(283, 386);
			this.OK_Button.Name = "OK_Button";
			this.OK_Button.TabIndex = 1;
			this.OK_Button.Text = "OK";
			this.OK_Button.Click += new System.EventHandler(this.OKButton_Click);
			// 
			// TrayIcon
			// 
			this.TrayIcon.ContextMenu = this.TrayMenu;
			this.TrayIcon.Icon = ((System.Drawing.Icon)(resources.GetObject("TrayIcon.Icon")));
			this.TrayIcon.Text = "Zimbra Toaster";
			this.TrayIcon.Visible = true;
			this.TrayIcon.DoubleClick += new System.EventHandler(this.TrayIcon_DoubleClick);
			// 
			// TrayMenu
			// 
			this.TrayMenu.MenuItems.AddRange(new System.Windows.Forms.MenuItem[] {
																					 this.CheckNowMenuItem,
																					 this.ShowNewMessagesMenuItem,
																					 this.ShowWindowMenuItem,
																					 this.ExitMenuItem});
			// 
			// CheckNowMenuItem
			// 
			this.CheckNowMenuItem.Index = 0;
			this.CheckNowMenuItem.Text = "Check For New Mail";
			this.CheckNowMenuItem.Click += new System.EventHandler(this.CheckNowMenuItem_Click);
			// 
			// ShowNewMessagesMenuItem
			// 
			this.ShowNewMessagesMenuItem.Index = 1;
			this.ShowNewMessagesMenuItem.Text = "Show New Messages";
			this.ShowNewMessagesMenuItem.Click += new System.EventHandler(this.ShowNewMessagesMenuItem_Click);
			// 
			// ShowWindowMenuItem
			// 
			this.ShowWindowMenuItem.Index = 2;
			this.ShowWindowMenuItem.Text = "Settings";
			this.ShowWindowMenuItem.Click += new System.EventHandler(this.ShowWindowMenuItem_Click);
			// 
			// ExitMenuItem
			// 
			this.ExitMenuItem.Index = 3;
			this.ExitMenuItem.Text = "Exit";
			this.ExitMenuItem.Click += new System.EventHandler(this.ExitMenuItem_Click);
			// 
			// Config
			// 
			this.AcceptButton = this.OK_Button;
			this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
			this.BackColor = System.Drawing.SystemColors.Control;
			this.ClientSize = new System.Drawing.Size(364, 419);
			this.ControlBox = false;
			this.Controls.Add(this.OK_Button);
			this.Controls.Add(this.ConfigurationTabControl);
			this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedSingle;
			this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
			this.MaximizeBox = false;
			this.MinimizeBox = false;
			this.Name = "Config";
			this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
			this.Text = "Zimbra Toaster Configuration";
			this.Resize += new System.EventHandler(this.Config_Resize);
			this.Closing += new System.ComponentModel.CancelEventHandler(this.Config_Closing);
			this.Load += new System.EventHandler(this.Config_Load);
			this.ConfigurationTabControl.ResumeLayout(false);
			this.ConfigrationTabPage.ResumeLayout(false);
			this.AdvancedGroupBox.ResumeLayout(false);
			((System.ComponentModel.ISupportInitialize)(this.PollingIntervalUpDown)).EndInit();
			this.ZimbraAccountGroupBox.ResumeLayout(false);
			this.ServerConnectionGroupBox.ResumeLayout(false);
			this.GoodiesTabPage.ResumeLayout(false);
			this.FadeControlGroupBox.ResumeLayout(false);
			((System.ComponentModel.ISupportInitialize)(this.PauseIntervalNumericUpDown)).EndInit();
			((System.ComponentModel.ISupportInitialize)(this.UpdateIntervalNumericUpDown)).EndInit();
			((System.ComponentModel.ISupportInitialize)(this.OpacityDeltaNumericUpDown)).EndInit();
			((System.ComponentModel.ISupportInitialize)(this.MaxOpacityNumericUpDown)).EndInit();
			this.groupBox2.ResumeLayout(false);
			this.ResumeLayout(false);

		}
		#endregion

		#region Tray Menu Event Handlers
		/// <summary>
		/// TrayIcon was double clicked - check for new messages
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void TrayIcon_DoubleClick(object sender, System.EventArgs e)
		{
			mailboxMonitor.CheckMailbox(null);
		}


		/// <summary>
		/// Show Window was selected from the tray menu
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void ShowWindowMenuItem_Click(object sender, System.EventArgs e)
		{
			Show();
			WindowState = FormWindowState.Normal;
		}

		/// <summary>
		/// Exit was selected from the tray menu
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void ExitMenuItem_Click(object sender, System.EventArgs e)
		{
			Close();
		}

		/// <summary>
		/// Show Toast was selecte from the menu
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void ShowToastMenuItem_Click(object sender, System.EventArgs e)
		{
			//this.toaster.Show();
		}

		/// <summary>
		/// Check for new stuff right now
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void CheckNowMenuItem_Click(object sender, System.EventArgs e)
		{
			mailboxMonitor.CheckMailbox(null);
		}

		private void ShowNewMessagesMenuItem_Click(object sender, System.EventArgs e)
		{
			mailboxMonitor.CheckMailbox(this.msgSummaries);
		}

		#endregion

		#region Form Control Event Handlers

		/// <summary>
		/// Config was resized - used to know when the window has been minimized
		/// so we can hide it from the user
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void Config_Resize(object sender, System.EventArgs e)
		{
			if (FormWindowState.Minimized == WindowState) 
			{
				Hide();
			}
		}


		/// <summary>
		/// OK was clicked in the config form - serialize the params and hide the window
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void OKButton_Click(object sender, System.EventArgs e)
		{
			Hide();
			
			if( mailboxMonitor != null )
				mailboxMonitor.StopMonitoring();

			//this updates both toastConfig and zimbraSession
			SaveParams();
			
			mailboxMonitor.Update( zimbraSession, toastConfig.PollInterval );

			//start monitoring
			mailboxMonitor.StartMonitoring();
		}


		/// <summary>
		/// A key was pressed in one of the config text fields.  Figure out if we can 
		/// the enabled state of the OK button
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void Configuration_TextFieldKeyPress(object sender, System.Windows.Forms.KeyEventArgs e)		
		{
			UpdateOkButton();
		}

		/// <summary>
		/// One time initializations
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void Config_Load(object sender, System.EventArgs e)
		{
			//load the params from the file - defaults used if file doesn't exist
			toastConfig = new ToastConfig();

			//create the zimbra session
			UpdateZimbraSession();
							
			//initialize the mailbox monitor
			mailboxMonitor = new MailboxMonitor( zimbraSession, toastConfig.PollInterval );

			//the toaster needs to know when new msgs arrive 
			mailboxMonitor.OnNewMsgs += new Zimbra.Toast.MailboxMonitor.NewMsgHandler(DisplayNewMessages);

			//set the tf values
			InitDialogFields();
			
			UpdateOkButton();
		}
		
		
		/// <summary>
		/// Form is closing, shut everything down
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void Config_Closing(object sender, System.ComponentModel.CancelEventArgs e)
		{
			if( mailboxMonitor != null )
				mailboxMonitor.StopMonitoring();
			SaveParams();
		}


		/// <summary>
		/// use ZWC as the default mailto handler
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void RegisterMailto_Click(object sender, System.EventArgs e)
		{
			String openCmd = "\"" + System.Windows.Forms.Application.ExecutablePath + "\" \"%1\"";
			RegistryKey key = Registry.LocalMachine.OpenSubKey( @"software\classes\mailto\shell\open\command", true );
			key.SetValue( "", openCmd );

			RegistryKey zimbraKey = Registry.LocalMachine.CreateSubKey( @"Software\Clients\Mail\Zimbra" );
			zimbraKey.SetValue( "", "Zimbra" );
			
			key = zimbraKey.CreateSubKey( @"Protocols\mailto\shell\open\command" );
			key.SetValue( "", openCmd );

			key = zimbraKey.CreateSubKey( @"Shell\open\command" );
			key.SetValue( "", openCmd );

			System.Windows.Forms.MessageBox.Show( 
				"Registered Successfully", 
				"Register Zimbra As Mailto Handler", 
				System.Windows.Forms.MessageBoxButtons.OK, 
				System.Windows.Forms.MessageBoxIcon.Information );
		}


		/// <summary>
		/// 
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void PlaySoundCheckBox_Click(object sender, System.EventArgs e)
		{
			UpdateOkButton();
		}


		/// <summary>
		/// 
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void SoundFileBrowseButton_Click(object sender, System.EventArgs e)
		{
			OpenFileDialog f = new OpenFileDialog();
			f.Multiselect = false;
			f.ShowReadOnly = false;
			f.CheckFileExists = true;
			f.Filter = "Wav files (*.wav)|*.wav|All files (*.*)|*.*";
			f.Title = "Select sound file";
			f.ValidateNames = true;
			f.RestoreDirectory = false;
			f.ShowHelp = false;
			f.InitialDirectory = System.Environment.SystemDirectory + @"\..\Media";

			DialogResult dr = f.ShowDialog();

			if( dr == DialogResult.OK )
			{
				this.SoundFileTextBox.Text = f.FileName;
			}

			UpdateOkButton();
		}

		/// <summary>
		/// Play the sound file
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void PlaySoundButton_Click(object sender, System.EventArgs e)
		{
			PlaySound( this.SoundFileTextBox.Text, IntPtr.Zero, 
				SoundFlags.SND_FILENAME | SoundFlags.SND_ASYNC );
		}

		

		#endregion Form Control Event Handlers

		#region Private helpers

		/// <summary>
		/// Based on the current values of all the text fields set
		/// the enabled state of the OK button.  It should only be
		/// enabled if all fields have what look like valid values.
		/// </summary>
		private void UpdateOkButton()
		{
			bool bAllValues = (
				TfHasValue( this.ServerNameTextBox ) &&
				TfHasValue( this.AccountTextBox ) &&
				TfHasValue( this.PasswordTextBox ) &&
				TfHasValue( this.VerifyPasswordTextBox ) );

			bool bSoundValues = (this.PlaySoundCheckBox.Checked && TfHasValue(this.SoundFileTextBox ) ) ||
				!this.PlaySoundCheckBox.Checked;

			bool bMatch = false;
			if( TfHasValue(PasswordTextBox) && TfHasValue(VerifyPasswordTextBox)) 
			{
				bMatch = this.PasswordTextBox.Text.Equals( this.VerifyPasswordTextBox.Text );
			}
			
			this.PlaySoundButton.Enabled = TfHasValue( this.SoundFileTextBox ) && this.PlaySoundCheckBox.Checked;

			this.SoundFileBrowseButton.Enabled = this.PlaySoundCheckBox.Checked;
			
			this.OK_Button.Enabled = bAllValues && bMatch && bSoundValues;
			
			this.RegisterMailto.Enabled = bAllValues && bMatch;
		}


		/// <summary>
		/// Helper to determine if the text box contains a string
		/// </summary>
		/// <param name="t">The text box</param>
		/// <returns>True if t contains a string of length > 0</returns>
		private static bool TfHasValue( TextBox t )
		{
			return (t != null && t.Text != null && t.Text.Length > 0 );
		}


		/// <summary>
		/// Is the toaster fully configured
		/// </summary>
		private bool Configured 
		{
			get{ return toastConfig != null; }
		}


		/// <summary>
		/// Serialize the toaster configuration to an xml file
		/// </summary>
		private void SaveParams()
		{
			UpdateToastConfig();
			toastConfig.Save();
		}


		/// <summary>
		/// Update the toast config based on whats in the UI
		/// </summary>
		private void UpdateToastConfig()
		{
			int clickActionIdx = this.clickActionComboBox.SelectedIndex;
			if( clickActionIdx > (int)CLICK_ACTIONS.MAX ) 
			{
				clickActionIdx = 0;
			}

			toastConfig = new ToastConfig( 
				this.ServerNameTextBox.Text,
				this.UseSecureConnectionCheckBox.Checked,
				this.AccountTextBox.Text,
				this.PasswordTextBox.Text,
				(ushort)this.PollingIntervalUpDown.Value,
				clickActionURLs[ clickActionIdx ],
				toastConfig.Location,
				this.SoundFileTextBox.Text,
				(double)this.MaxOpacityNumericUpDown.Value,
				(double)this.OpacityDeltaNumericUpDown.Value,
				(Int32)this.UpdateIntervalNumericUpDown.Value,
				(Int32)this.PauseIntervalNumericUpDown.Value );

			//any time we update the toast config we should update the zimbra session
			UpdateZimbraSession();
		}


		/// <summary>
		/// update thee zimbra session with whats in the toast config
		/// </summary>
		private void UpdateZimbraSession()
		{
			zimbraSession = new ZimbraSession(
					toastConfig.Account, 
					toastConfig.Password,
					toastConfig.Server,
					toastConfig.Port,
					toastConfig.UseSecure );
		}

		/// <summary>
		/// Populate the dialog text fields with the values loaded in toastConfig
		/// </summary>
		private void InitDialogFields()
		{
			if( toastConfig.Port != 80 && toastConfig.Port != 443 ) 
			{
				this.ServerNameTextBox.Text = toastConfig.Server + ":" + toastConfig.Port;
			}
			else
			{
				this.ServerNameTextBox.Text = toastConfig.Server;
			}

			String url = toastConfig.ClickURLPathFmt.ToLower();
			int clickActionIdx = 0;
			for( int i = 0; i < clickActionURLs.Length; i++ ) 
			{
				if( url.Equals( clickActionURLs[i].ToLower() ) ) 
				{
					clickActionIdx = i;
					break;
				}
			}

			this.UseSecureConnectionCheckBox.Checked = toastConfig.UseSecure;
			this.AccountTextBox.Text = toastConfig.Account;
			this.PasswordTextBox.Text = toastConfig.Password;
			this.VerifyPasswordTextBox.Text = toastConfig.Password;
			this.PollingIntervalUpDown.Value = toastConfig.PollInterval;
			this.clickActionComboBox.SelectedIndex = clickActionIdx;

			if( toastConfig.SoundFile != null ) 
			{
				this.PlaySoundCheckBox.Checked = true;
				this.SoundFileTextBox.Text = toastConfig.SoundFile;
			}

			this.MaxOpacityNumericUpDown.Value = (decimal)toastConfig.MaxOpacity;
			this.OpacityDeltaNumericUpDown.Value = (decimal)toastConfig.OpacityDelta;
			this.UpdateIntervalNumericUpDown.Value = toastConfig.UpdateInterval;
			this.PauseIntervalNumericUpDown.Value = toastConfig.PauseInterval;
		}

		#endregion

		#region Toaser event handlers

		/// <summary>
		/// Open the item in a browser
		/// </summary>
		/// <param name="itemId"></param>
		private void OpenItem( String itemId )
		{
			//open the uri in the default browsers
			String uri = toastConfig.GetItemUri( itemId, this.zimbraSession.AuthToken );

			//this could be dangerous, but at lease we are
			//assured it starts with "http"
			System.Diagnostics.Process.Start( uri );
		}

		/// <summary>
		/// Tell Zimbra to flag the item
		/// </summary>
		/// <param name="itemId"></param>
		private void FlagItem( String itemId )
		{
			//for now do it in the UI thread to block the UI
			zimbraSession.FlagItem(itemId);
		}

		/// <summary>
		/// Tell zimbra to move the item to the trash folder
		/// </summary>
		/// <param name="itemId"></param>
		private void DeleteItem( String itemId )
		{
			zimbraSession.MoveItem( itemId, "3" );
		}

		#endregion

		#region handle display of msg summaries (cycle toaster)
		/// <summary>
		/// This should be called in the mailboxmonitors worker thread
		/// the idea is to hold it up while toast is being displayed so
		/// it doesn't poll the server and obtain a new MessageSummary[]
		/// </summary>
		/// <param name="msgs">the new messages on the server</param>
		/// <param name="are">the event to signal once all msgs have been displayed</param>
		public void DisplayNewMessages( Zimbra.Client.MessageSummary[] msgs, AutoResetEvent are )
		{
			int nMsgs = 0;
			if( msgs != null ) 
			{
				nMsgs = msgs.Length;
			}

			//nothing to display, signal the event and bail
			if( msgs == null || msgs.Length == 0 ) 
			{
				are.Set();
				return;
			}

			//block this so the UI doesn't get confused
			this.ShowNewMessagesMenuItem.Enabled = false;
			this.CheckNowMenuItem.Enabled = false;

			//play a sound?
			if( toastConfig.SoundFile != null ) 
			{
				PlaySound( toastConfig.SoundFile, IntPtr.Zero, 
					SoundFlags.SND_ASYNC | SoundFlags.SND_FILENAME );
			}

			//setup the state to cycle through the message summaries
			this.currentMsgIdx = 0;
			this.msgSummaries = msgs;
			this.displayCompletionEvent = are;

			//update the new message count in the tray icon tooltip
			this.TrayIcon.Text = nMsgs + " new message" + ((nMsgs!=1)?"":"s");

			//start it
			Invoke( new MethodInvoker(ShowCurrentMessagSummary) );
		}


		/// <summary>
		/// Display the current message summary in a new piece of toast
		/// assumes the previous toast has been closed
		/// </summary>
		private void ShowCurrentMessagSummary()
		{
			toastForm = new ToastForm(
				this.msgSummaries[this.currentMsgIdx++],
				toastConfig.MaxOpacity,
				toastConfig.OpacityDelta,
				toastConfig.UpdateInterval,
				toastConfig.PauseInterval );
			toastForm.Location = toastConfig.Location;
			toastForm.Closed += new EventHandler(ToasterClosed);
			toastForm.OnOpenItem += new ToastForm.OpenItemHandler(OpenItem);
			toastForm.OnFlagItem += new ToastForm.FlagItemHandler(FlagItem);
			toastForm.OnDeleteItem += new ToastForm.DeleteItemHandler(DeleteItem);
			toastForm.Show();
		}


		/// <summary>
		/// Handle 'Closed' event fired from a ToastForm
		/// updates the current msg summary idx and fires off the next toast
		/// if all msg summaries displayed, signal mailboxMonitor
		/// </summary>
		/// <param name="o"></param>
		/// <param name="a"></param>
		private void ToasterClosed(object o, EventArgs a)
		{
			toastConfig.Location = toastForm.Location;
			if( this.currentMsgIdx < this.msgSummaries.Length ) 
			{
				ShowCurrentMessagSummary();
			}
			else
			{
				this.ShowNewMessagesMenuItem.Enabled = true;
				this.CheckNowMenuItem.Enabled = true;
				this.displayCompletionEvent.Set();
			}
		}
		#endregion


		#region  Helper to play sound
		// PlaySound()
		[DllImport("winmm.dll", SetLastError=true, 
			 CallingConvention=CallingConvention.Winapi)]
		static extern bool PlaySound(
			string pszSound,
			IntPtr hMod,
			SoundFlags sf );


		private void ViewToasterButton_Click(object sender, System.EventArgs e)
		{
			Zimbra.Client.MessageSummary msg = new Zimbra.Client.MessageSummary();
			msg.email_address = "sam@company.com";
			msg.email_display = "Sam Someone";
			msg.email_personal_name = "Sam Employee";
			msg.fragment = "Do you like how the toaster window fades in and out?";
			msg.itemId = null;
			msg.parentFolderId = null;
			msg.subject = "Toaster Window Fading";

			ToastForm tempToaster = new ToastForm( msg,
				(double)this.MaxOpacityNumericUpDown.Value,
				(double)this.OpacityDeltaNumericUpDown.Value,
				(Int32)this.UpdateIntervalNumericUpDown.Value,
				(Int32)this.PauseIntervalNumericUpDown.Value );
			tempToaster.Location = toastConfig.Location;

			tempToaster.ShowDialog();
		}


		/// <summary>
		/// Flags for playing sounds.  For this example, we are reading 
		/// the sound from a filename, so we need only specify 
		/// SND_FILENAME | SND_ASYNC 
		/// </summary>
		[Flags]
		public enum SoundFlags : int 
		{
			/// <summary>
			/// play synchronously (default)
			/// </summary>
			SND_SYNC = 0x0000,
			/// <summary>
			/// play asynchronously
			/// </summary>
			SND_ASYNC = 0x0001,
			/// <summary>
			/// silence (!default) if sound not found 
			/// </summary>
			SND_NODEFAULT = 0x0002,
			/// <summary>
			/// pszSound points to a memory file
			/// </summary>
			SND_MEMORY = 0x0004,
			/// <summary>
			/// loop the sound until next sndPlaySound 
			/// </summary>
			SND_LOOP = 0x0008,
			/// <summary>
			/// don't stop any currently playing sound 
			/// </summary>
			SND_NOSTOP = 0x0010,
			/// <summary>
			/// don't wait if the driver is busy 
			/// </summary>
			SND_NOWAIT = 0x00002000,
			/// <summary>
			/// name is a registry alias 
			/// </summary>
			SND_ALIAS = 0x00010000,
			/// <summary>
			/// alias is a predefined ID
			/// </summary>
			SND_ALIAS_ID = 0x00110000,
			/// <summary>
			/// name is file name 
			/// </summary>
			SND_FILENAME = 0x00020000,
			/// <summary>
			/// name is resource name or atom 
			/// </summary>
			SND_RESOURCE = 0x00040004 
		}
		#endregion


	}







	/// <summary>
	/// The toaster configuration - server, port, acct, etc
	/// </summary>
	public class ToastConfig
	{
		//the server name (can end in :<port>)
		private String	server;

		//use a secure connection or clear?
		private bool	useSecure = true;

		//the zimbra account to monitor
		private String	account;

		//the password of the zimbra account
		private String	password;

		//how often to hit the server and check for new messages
		private UInt16	pollInterval = 1;

		//path to something on the server
		private String	clickURLPathFmt = DEFAULT_CLICK_URL_PATH_FMT;

		//the top left of the toaster window
		private Point	location;

		//the sound to play when new msgs arrive
		private String	soundFile;

		//max opacity when fading the toaster in
		private double	maxOpacity;

		//change in opacity on updates
		private double	opacityDelta;

		//how often to change the opacity
		private Int32	updateInterval;

		//how long to pause when at max opacity
		private Int32	pauseInterval;

		//the filename of the configuration file
		private static String filename = "ztoastcfg.xml";

		//key that identifies the password in the password db
		private static String PASSWORD_KEY = "ZT0A5T";

		//default polling interval is 5 minutes
		private static UInt16 DEFAULT_POLL_INTERVAL = 5;

		//default click-url path format specifier
		private static String DEFAULT_CLICK_URL_PATH_FMT = "/zimbra/?view=msg&id={0}";

		//default maximum opacity for the fade of the toaster window
		private static double DEFAULT_MAX_OPACITY = 0.8;
		
		//default change in opacity for the fade of the toaster window
		private static double DEFAULT_OPACITY_DELTA = 0.01;
		
		//default update interval for the fade of the toaster window
		private static Int32  DEFAULT_UPDATE_INTERVAL = 10;
		
		//how long toaster window waits at full opacity before fading out
		private static Int32  DEFAULT_PAUSE_INTERVAL = 5000;

		//TODO: request a REST api for this
		//private static String DEFAULT_CLICK_URL_PATH_FMT = "/service/home/~/?fmt=html&id={0}";


		private static String CFG_ZIMBRA_TOAST		= "ZimbraToast";
		private static String CFG_SERVER			= "Server";
		private static String CFG_USE_SECURE		= "UseSecure";
		private static String CFG_ACCOUNT			= "Account";
		private static String CFG_PASSWORD			= "EncryptedPassword";
		private static String CFG_POLL_INTERVAL		= "PollInterval";
		private static String CFG_CLICK_URL			= "ClickURLPathFmt";
		private static String CFG_LOCATION			= "WindowLocation";
		private static String CFG_SOUND_FILE		= "SoundFile";
		private static String CFG_MAX_OPACITY		= "MaxOpacity";
		private static String CFG_OPACITY_DELTA		= "OpacityDelta";
		private static String CFG_UPDATE_INTERVAL	= "UpdateInterval";
		private static String CFG_PAUSE_INTERVAL	= "PauseInterval";
		

		/// <summary>
		/// Default constructor - attempts to read the parameters from 
		/// configuration file.  It will throw if it has any problems.
		/// </summary>
		public ToastConfig()
		{
			XmlDocument doc = new XmlDocument();
			try
			{
				doc.Load(ParamFilename());
			}
			catch( Exception )
			{
			}

			server			= GetConfigParamString( doc, CFG_ZIMBRA_TOAST + "/" + CFG_SERVER, null );
			useSecure		= GetConfigParamBool  ( doc, CFG_ZIMBRA_TOAST + "/" + CFG_USE_SECURE, true );
			account			= GetConfigParamString( doc, CFG_ZIMBRA_TOAST + "/" + CFG_ACCOUNT, null );
			password		= GetConfigParamPassword( doc, CFG_ZIMBRA_TOAST + "/" + CFG_PASSWORD, null );
			pollInterval	= GetConfigParamUInt16( doc, CFG_ZIMBRA_TOAST + "/" + CFG_POLL_INTERVAL, DEFAULT_POLL_INTERVAL );
			clickURLPathFmt = GetConfigParamString( doc, CFG_ZIMBRA_TOAST + "/" + CFG_CLICK_URL, DEFAULT_CLICK_URL_PATH_FMT );
			location		= GetConfigParamPoint ( doc, CFG_ZIMBRA_TOAST + "/" + CFG_LOCATION, new Point( 100, 100 ) );
			soundFile		= GetConfigParamString( doc, CFG_ZIMBRA_TOAST + "/" + CFG_SOUND_FILE, null );
			maxOpacity		= GetConfigParamDouble( doc, CFG_ZIMBRA_TOAST + "/" + CFG_MAX_OPACITY, DEFAULT_MAX_OPACITY );
			opacityDelta	= GetConfigParamDouble( doc, CFG_ZIMBRA_TOAST + "/" + CFG_OPACITY_DELTA, DEFAULT_OPACITY_DELTA );
			updateInterval  = GetConfigParamInt32 ( doc, CFG_ZIMBRA_TOAST + "/" + CFG_UPDATE_INTERVAL, DEFAULT_UPDATE_INTERVAL );
			pauseInterval   = GetConfigParamInt32 ( doc, CFG_ZIMBRA_TOAST + "/" + CFG_PAUSE_INTERVAL, DEFAULT_PAUSE_INTERVAL );
		}


		/// <summary>
		/// Return a string configuration parameter from the xml config file
		/// </summary>
		/// <param name="doc">the config file</param>
		/// <param name="xpath">the node</param>
		/// <param name="strDefault">the default string to return if the node doesn't exist or is invalid</param>
		/// <returns>the string value of the param, or null if it doesnt exist</returns>
		private String GetConfigParamString( XmlDocument doc, String xpath, String strDefault )
		{
			XmlNode n = doc.SelectSingleNode( xpath );
			try 
			{
				if( n.InnerText == null || n.InnerText.Length <= 0 )
					return strDefault;

				return n.InnerText;
			}
			catch( Exception )
			{
				return strDefault;
			}
		}


		/// <summary>
		/// Returns a bool configuration paramter from the xml config file
		/// </summary>
		/// <param name="doc">the config file</param>
		/// <param name="xpath">how to get to the node</param>
		/// <param name="bDefault">the default value if the node doesn't exist or is invalie</param>
		/// <returns>the bool value of the param, or bDefault if something goes wrong</returns>
		private bool GetConfigParamBool( XmlDocument doc, String xpath, bool bDefault )
		{
			String temp = GetConfigParamString( doc, xpath, null );
			try
			{
				return bool.Parse(temp);
			}
			catch( Exception )
			{
				return bDefault;
			}
		}

		/// <summary>
		/// Returns a UInt16 configuration parameter from the xml config file
		/// </summary>
		/// <param name="doc">the config file</param>
		/// <param name="xpath">the xpath to get to the node</param>
		/// <param name="nDefault">the default value if the node doesn't exist or is invalid</param>
		/// <returns>the UInt16 value of the param, or nDefault if something goes wrong</returns>
		private UInt16 GetConfigParamUInt16( XmlDocument doc, String xpath, UInt16 nDefault )
		{
			String temp = GetConfigParamString( doc, xpath, null );
			try
			{
				return UInt16.Parse(temp);
			}
			catch( Exception )
			{
				return nDefault;
			}
		}


		/// <summary>
		/// Returns a Int32 configuration parameter from the xml config file
		/// </summary>
		/// <param name="doc">the config file</param>
		/// <param name="xpath">the node selector</param>
		/// <param name="nDefault">default value if the node doesn't exist or is invalid</param>
		/// <returns>the UInt32 value of the param, or nDefault if something goes wrong</returns>
		private Int32 GetConfigParamInt32( XmlDocument doc, String xpath, Int32 nDefault )
		{
			String temp = GetConfigParamString( doc, xpath, null );
			try
			{
				return Int32.Parse(temp);
			}
			catch( Exception )
			{
				return nDefault;
			}
		}


		/// <summary>
		/// Returns a double configuration parameter from the xml config file
		/// </summary>
		/// <param name="doc">the config file</param>
		/// <param name="xpath">the node selector</param>
		/// <param name="dDefault">the double value of the param, or dDefault if something goes wrong</param>
		/// <returns></returns>
		private double GetConfigParamDouble( XmlDocument doc, String xpath, double dDefault )
		{
			String temp = GetConfigParamString( doc, xpath, null );
			try
			{
				return Double.Parse(temp);
			}
			catch( Exception )
			{
				return dDefault;
			}
		}


		/// <summary>
		/// Return a Point configuration parameter from the xml config file
		/// </summary>
		/// <param name="doc">the config file</param>
		/// <param name="xpath">the xpath to get the node</param>
		/// <param name="pDefault">the default value if the node doesn't exist or is invalid</param>
		/// <returns>the Point value of the param or pDefault if something goes wrong</returns>
		private Point GetConfigParamPoint( XmlDocument doc, String xpath, Point pDefault )
		{
			String temp = GetConfigParamString( doc, xpath, null );
			try
			{
				String[] xy = temp.Split( new char[] { ',' } );
				return new Point( Int32.Parse( xy[0] ), Int32.Parse( xy[1] ) );
			}
			catch( Exception )
			{
				return pDefault;
			}
		}


		private String GetConfigParamPassword( XmlDocument doc, String xpath, String strDefault )
		{
			String temp = GetConfigParamString( doc, xpath, null );
			try
			{
				//decrypt the buffer
				return DPAPI.Decrypt( temp );
			}
			catch(Exception)
			{
				return strDefault;
			}
		}


		/// <summary>
		/// Create a new ToastConfig object with the given params
		/// </summary>
		/// <param name="server">The server</param>
		/// <param name="useSecure">Use a secure connection</param>
		/// <param name="account">The account to monitor</param>
		/// <param name="password">The accounts password</param>
		/// <param name="pollInterval">The polling interval - must be > 0</param>
		/// <param name="clickURLPathFmt">The format specifier of the path portion of the url to be opened when an item is clicked</param>
		/// <param name="location">The default location of the toaster window</param>
		/// <param name="soundFile">Sound to play when new msgs arrive</param>
		/// <param name="maxOpacity">Max opacity of the toaster window</param>
		/// <param name="opacityDelta">change in opacity when fading in or out</param>
		/// <param name="updateInterval">how often to update the opacity</param>
		/// <param name="pauseInterval">how long to pause at max opacity</param>
		public ToastConfig( 
			String	server, 
			bool	useSecure, 
			String	account, 
			String	password, 
			UInt16	pollInterval, 
			String	clickURLPathFmt, 
			Point	location ,
			String	soundFile,
			double  maxOpacity,
			double  opacityDelta,
			Int32  updateInterval,
			Int32  pauseInterval )
		{
			this.server = server;
			this.useSecure = useSecure;
			this.account = account;
			this.password = password;
			if( pollInterval > 0 )
			{
				this.pollInterval = pollInterval;
			}
			else 
			{
				this.pollInterval = DEFAULT_POLL_INTERVAL;
			}
			this.clickURLPathFmt = clickURLPathFmt;
			this.location = location;
			this.soundFile = soundFile;
			this.maxOpacity = maxOpacity;
			this.opacityDelta = opacityDelta;
			this.updateInterval = updateInterval;
			this.pauseInterval = pauseInterval;
		}


		/// <summary>
		/// Retrieve the param file name.
		/// </summary>
		/// <returns></returns>
		private static String ParamFilename()
		{
			String path = System.Windows.Forms.Application.UserAppDataPath;
			int idx = path.IndexOf( System.Windows.Forms.Application.ProductVersion );
			path = path.Substring( 0, idx ) + filename;
			return path;
		}


		/// <summary>
		/// Serialize the parameters to the param file
		/// </summary>
		public void Save()
		{
			XmlDocument doc = new XmlDocument();
			XmlElement zimbraToast = doc.CreateElement( CFG_ZIMBRA_TOAST );
			doc.AppendChild( zimbraToast );

			SaveElement( doc, zimbraToast, CFG_SERVER, server );
			SaveElement( doc, zimbraToast, CFG_USE_SECURE, useSecure.ToString() );
			SaveElement( doc, zimbraToast, CFG_ACCOUNT, account );
			SaveElement( doc, zimbraToast, CFG_PASSWORD, EncryptedPassword );
			SaveElement( doc, zimbraToast, CFG_POLL_INTERVAL, pollInterval.ToString() );
			SaveElement( doc, zimbraToast, CFG_CLICK_URL, clickURLPathFmt );
			
			String point = location.X.ToString() + "," + location.Y.ToString();
			SaveElement( doc, zimbraToast, CFG_LOCATION, point );
			SaveElement( doc, zimbraToast, CFG_SOUND_FILE, soundFile );

			SaveElement( doc, zimbraToast, CFG_MAX_OPACITY, this.maxOpacity.ToString() );
			SaveElement( doc, zimbraToast, CFG_OPACITY_DELTA, this.opacityDelta.ToString() );
			SaveElement( doc, zimbraToast, CFG_UPDATE_INTERVAL, this.updateInterval.ToString() );
			SaveElement( doc, zimbraToast, CFG_PAUSE_INTERVAL, this.pauseInterval.ToString() );

			doc.Save(ParamFilename());
		}

		
		/// <summary>
		/// Write the param to the xml file
		/// </summary>
		/// <param name="doc">the xml config document</param>
		/// <param name="parent">the parent xml node</param>
		/// <param name="paramName">the name of the param</param>
		/// <param name="paramValue">the param as a string</param>
		private static void SaveElement( XmlDocument doc, XmlElement parent, String paramName, String paramValue )
		{
			if( paramValue != null && paramValue.Length > 0 )
			{
				XmlElement e = doc.CreateElement( paramName );
				parent.AppendChild( e );
				e.InnerText = paramValue;
			}
		}


		/// <summary>
		/// The server to connecto to.  Can end in :{port}
		/// otherwise the defualt ports are used (80/443)
		/// </summary>
		public String Server 
		{
			get
			{ 
				try 
				{
					return server.Split( new char[] { ':' } )[0];
				}
				catch(Exception)
				{
					return server;
				}
			}
		}


		/// <summary>
		/// The port to connect to
		/// </summary>
		public UInt16 Port
		{
			get
			{
				UInt16 port = 80;
				if( useSecure ) 
				{
					port = 443;
				}

				try 
				{
					return UInt16.Parse( server.Split( new char[] { ':' } )[1] );
				} 
				catch(Exception)
				{
					return port;
				}
			}
		}

		/// <summary>
		/// The zimbra account name (aka email address)
		/// </summary>
		public String Account 
		{
			get{ return account; }
		}

		/// <summary>
		/// zimbra account password
		/// </summary>
		public String Password
		{
			get{ return password; }
		}

		/// <summary>
		/// use a secure connection to the server?
		/// </summary>
		public bool UseSecure
		{
			get{ return useSecure; }
		}



		/// <summary>
		/// how often to poll the server for updates
		/// </summary>
		public UInt16 PollInterval
		{
			get{ return pollInterval; }
		}


		/// <summary>
		/// format specifier of the path portion of the URL to open 
		/// when an item is clicked in the toaster
		/// </summary>
		public String ClickURLPathFmt
		{
			get{ return clickURLPathFmt; }
		}


		/// <summary>
		/// the default location of the toaster window
		/// </summary>
		public Point Location
		{
			get{ return location; }
			set{ location = value; }
		}


		/// <summary>
		/// Based on the current configration, get the URI for an item
		/// </summary>
		/// <param name="itemId"></param>
		/// <param name="authToken"></param>
		/// <returns></returns>
		public String GetItemUri( String itemId, String authToken )
		{
			//construct the URL encoded redirect url
			System.Text.StringBuilder sb = new System.Text.StringBuilder();
			sb.AppendFormat( this.clickURLPathFmt, itemId );
			String redirectUrl = System.Web.HttpUtility.UrlEncode( sb.ToString() );

			//append the configurable path portion
			sb = new System.Text.StringBuilder( GetServerUri() );

			//add the url to the preauth servlet
			sb.AppendFormat( "/service/preauth?isredirect=1&authtoken={0}&redirectURL={1}", authToken, redirectUrl );
			
			return sb.ToString();
		}



		/// <summary>
		/// url to open a compose window in ZWC
		/// </summary>
		/// <param name="mailToUrl"></param>
		/// <returns></returns>
		public String GetMailtoUri( String mailToUrl )
		{
			System.Text.StringBuilder sb = new System.Text.StringBuilder( GetServerUri() );

			mailToUrl = mailToUrl.Replace( "mailto:", "to=" );
			int idx = mailToUrl.IndexOf( '?' );
			if( idx != -1 ) 
			{
				mailToUrl = mailToUrl.Substring( 0, idx ) + "&" + mailToUrl.Substring( idx + 1 );
			}

			sb.AppendFormat( "/zimbra/mail?view=compose&{0}", mailToUrl );

			return sb.ToString();
		}


		/// <summary>
		/// returns the server uri
		/// </summary>
		/// <returns></returns>
		public String GetServerUri()
		{
			bool bExcludePort = 
				(Port == 80  && !UseSecure) ||
				(Port == 443 && UseSecure );

			System.Text.StringBuilder sb = new System.Text.StringBuilder();

			//protocol/server part
			sb.AppendFormat( "http{0}://{1}{2}{3}",
				(UseSecure)?"s":"",
				Server,
				(bExcludePort)?"":":",
				(bExcludePort)?"":Port.ToString() );

			return sb.ToString();
		}


		/// <summary>
		/// encrypted password base64 encoded as a string
		/// </summary>
		public String EncryptedPassword
		{
			get
			{
				return DPAPI.Encrypt( DPAPI.KeyType.UserKey, this.password, null, PASSWORD_KEY );
			}
		}


		/// <summary>
		/// The sound file to play when new messages arrive
		/// </summary>
		public String SoundFile
		{
			get{ return soundFile; }
		}


		/// <summary>
		/// max opacity of toaster window
		/// </summary>
		public double MaxOpacity
		{
			get{ return maxOpacity; }
		}

		/// <summary>
		/// change in opacity when fading in/out
		/// </summary>
		public double OpacityDelta
		{
			get{ return opacityDelta; }
		}

		/// <summary>
		/// how often to change the opacity of the toaster
		/// </summary>
		public Int32 UpdateInterval
		{
			get{ return updateInterval; }
		}

		/// <summary>
		/// how long to pause at full opacity
		/// </summary>
		public Int32 PauseInterval
		{
			get{ return pauseInterval; }
		}


	}
}

