using System;
using System.Drawing;
using System.Collections;
using System.ComponentModel;
using System.Windows.Forms;
using System.IO;
using System.Xml;

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
		private System.Windows.Forms.Button OK_Button;
		private System.Windows.Forms.MenuItem ShowToastMenuItem;
		private System.Windows.Forms.MenuItem CheckNowMenuItem;
		private System.ComponentModel.IContainer components;
		private System.Windows.Forms.GroupBox ZimbraAccountGroupBox;
		private System.Windows.Forms.TextBox AccountTextBox;
		private System.Windows.Forms.Label AccountLabel;
		private System.Windows.Forms.TextBox PasswordTextBox;
		private System.Windows.Forms.Label PasswordLabel;
		private System.Windows.Forms.TextBox VerifyPasswordTextBox;
		private System.Windows.Forms.Label VerifyPasswordLabel;


		private ToastForm					toaster			= null;
		private ToastConfig					toastConfig		= null;
		private ZimbraSession				zimbraSession	= null;
		private System.Timers.Timer			timer			= new System.Timers.Timer();
		private System.Threading.Thread		monitorThread	= null;


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

		public String GetItemUri( String itemId )
		{
			bool bExcludePort = 
				(toastConfig.Port == 80  && !toastConfig.UseSecure) ||
				(toastConfig.Port == 443 && toastConfig.UseSecure );

 			System.Text.StringBuilder sb = new System.Text.StringBuilder();
			sb.AppendFormat( "http{0}://{1}{2}{3}/zimbra/?view=msg&id={4}",
				(toastConfig.UseSecure)?"s":"",
				toastConfig.Server,
				(bExcludePort)?"":":",
				(bExcludePort)?"":toastConfig.Port.ToString(),
				itemId );
			return sb.ToString();
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
			this.OK_Button = new System.Windows.Forms.Button();
			this.TrayIcon = new System.Windows.Forms.NotifyIcon(this.components);
			this.TrayMenu = new System.Windows.Forms.ContextMenu();
			this.CheckNowMenuItem = new System.Windows.Forms.MenuItem();
			this.ShowToastMenuItem = new System.Windows.Forms.MenuItem();
			this.ShowWindowMenuItem = new System.Windows.Forms.MenuItem();
			this.ExitMenuItem = new System.Windows.Forms.MenuItem();
			this.ConfigurationTabControl.SuspendLayout();
			this.ConfigrationTabPage.SuspendLayout();
			this.ZimbraAccountGroupBox.SuspendLayout();
			this.ServerConnectionGroupBox.SuspendLayout();
			this.SuspendLayout();
			// 
			// ConfigurationTabControl
			// 
			this.ConfigurationTabControl.Controls.Add(this.ConfigrationTabPage);
			this.ConfigurationTabControl.Location = new System.Drawing.Point(6, 8);
			this.ConfigurationTabControl.Name = "ConfigurationTabControl";
			this.ConfigurationTabControl.SelectedIndex = 0;
			this.ConfigurationTabControl.Size = new System.Drawing.Size(352, 238);
			this.ConfigurationTabControl.TabIndex = 0;
			this.ConfigurationTabControl.TabStop = false;
			// 
			// ConfigrationTabPage
			// 
			this.ConfigrationTabPage.BackColor = System.Drawing.SystemColors.ControlLightLight;
			this.ConfigrationTabPage.Controls.Add(this.ZimbraAccountGroupBox);
			this.ConfigrationTabPage.Controls.Add(this.ServerConnectionGroupBox);
			this.ConfigrationTabPage.Location = new System.Drawing.Point(4, 22);
			this.ConfigrationTabPage.Name = "ConfigrationTabPage";
			this.ConfigrationTabPage.Size = new System.Drawing.Size(344, 212);
			this.ConfigrationTabPage.TabIndex = 0;
			this.ConfigrationTabPage.Text = "Configuration";
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
			this.ZimbraAccountGroupBox.Size = new System.Drawing.Size(316, 106);
			this.ZimbraAccountGroupBox.TabIndex = 1;
			this.ZimbraAccountGroupBox.TabStop = false;
			this.ZimbraAccountGroupBox.Text = "Zimbra Account";
			// 
			// VerifyPasswordTextBox
			// 
			this.VerifyPasswordTextBox.Location = new System.Drawing.Point(102, 74);
			this.VerifyPasswordTextBox.Name = "VerifyPasswordTextBox";
			this.VerifyPasswordTextBox.PasswordChar = '●';
			this.VerifyPasswordTextBox.Size = new System.Drawing.Size(202, 20);
			this.VerifyPasswordTextBox.TabIndex = 7;
			this.VerifyPasswordTextBox.Text = "";
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
			this.PasswordTextBox.PasswordChar = '●';
			this.PasswordTextBox.Size = new System.Drawing.Size(202, 20);
			this.PasswordTextBox.TabIndex = 5;
			this.PasswordTextBox.Text = "";
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
			this.ServerNameTextBox.KeyUp += new System.Windows.Forms.KeyEventHandler(this.Configuration_TextFieldKeyPress);
			// 
			// UseSecureConnectionCheckBox
			// 
			this.UseSecureConnectionCheckBox.FlatStyle = System.Windows.Forms.FlatStyle.System;
			this.UseSecureConnectionCheckBox.Location = new System.Drawing.Point(102, 48);
			this.UseSecureConnectionCheckBox.Name = "UseSecureConnectionCheckBox";
			this.UseSecureConnectionCheckBox.Size = new System.Drawing.Size(182, 18);
			this.UseSecureConnectionCheckBox.TabIndex = 2;
			this.UseSecureConnectionCheckBox.Text = "Use Secure Connection";
			// 
			// ServerNameLabel
			// 
			this.ServerNameLabel.FlatStyle = System.Windows.Forms.FlatStyle.System;
			this.ServerNameLabel.ImageAlign = System.Drawing.ContentAlignment.MiddleRight;
			this.ServerNameLabel.Location = new System.Drawing.Point(10, 26);
			this.ServerNameLabel.Name = "ServerNameLabel";
			this.ServerNameLabel.Size = new System.Drawing.Size(84, 16);
			this.ServerNameLabel.TabIndex = 0;
			this.ServerNameLabel.Text = "Server Name:";
			this.ServerNameLabel.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
			// 
			// OK_Button
			// 
			this.OK_Button.FlatStyle = System.Windows.Forms.FlatStyle.System;
			this.OK_Button.Location = new System.Drawing.Point(283, 252);
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
																					 this.ShowToastMenuItem,
																					 this.ShowWindowMenuItem,
																					 this.ExitMenuItem});
			// 
			// CheckNowMenuItem
			// 
			this.CheckNowMenuItem.Index = 0;
			this.CheckNowMenuItem.Text = "Check For New Mail";
			// 
			// ShowToastMenuItem
			// 
			this.ShowToastMenuItem.Index = 1;
			this.ShowToastMenuItem.Text = "Show Toast";
			this.ShowToastMenuItem.Click += new System.EventHandler(this.ShowToastMenuItem_Click);
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
			this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
			this.BackColor = System.Drawing.SystemColors.Control;
			this.ClientSize = new System.Drawing.Size(364, 283);
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
			this.ZimbraAccountGroupBox.ResumeLayout(false);
			this.ServerConnectionGroupBox.ResumeLayout(false);
			this.ResumeLayout(false);

		}
		#endregion

		#region Tray Menu Event Handlers
		/// <summary>
		/// TrayIcon was double clicked - show the config window
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void TrayIcon_DoubleClick(object sender, System.EventArgs e)
		{
			Show();
			WindowState = FormWindowState.Normal;
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
			this.toaster.Show();
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
			SaveParams();
			Hide();

			StartMonitoring();
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
			try 
			{
				//create the toaster
				toaster = new ToastForm(this);
				//load the params
				toastConfig = new ToastConfig();
				//set the tf values
				InitDialogFields();
				//create the zimbra session
				//StartMonitoring();
			} 
			catch(Exception) 
			{
			}

			UpdateOkButton();
		}
		

		
		private void Config_Closing(object sender, System.ComponentModel.CancelEventArgs e)
		{
			StopMonitoring();
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

			bool bMatch = false;
			if( TfHasValue(PasswordTextBox) && TfHasValue(VerifyPasswordTextBox)) 
			{
				bMatch = this.PasswordTextBox.Text.Equals( this.VerifyPasswordTextBox.Text );
			}
			this.OK_Button.Enabled = bAllValues && bMatch;
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
			toastConfig = new ToastConfig( 
				this.ServerNameTextBox.Text,
				this.UseSecureConnectionCheckBox.Checked,
				this.AccountTextBox.Text,
				this.PasswordTextBox.Text );
			toastConfig.Save();
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

			this.UseSecureConnectionCheckBox.Checked = toastConfig.UseSecure;
			this.AccountTextBox.Text = toastConfig.Account;
			this.PasswordTextBox.Text = toastConfig.Password;
			this.VerifyPasswordTextBox.Text = toastConfig.Password;
		}

		#endregion

		#region Mailbox Monitoring stuff
		
		/// <summary>
		/// Start monitoring the zimbra mailbox for new items.  
		/// If monitoring has started, it will be stopped and restarted.
		/// </summary>
		private void StartMonitoring()
		{
			if( monitorThread != null )
			{
				StopMonitoring();
			}

			zimbraSession = new ZimbraSession( 
				toastConfig.Account, 
				toastConfig.Password, 
				toastConfig.Server,
				toastConfig.Port, 
				toastConfig.UseSecure );

			monitorThread = new System.Threading.Thread( new System.Threading.ThreadStart(MonitorMailbox) );
			monitorThread.Start();
		}


		/// <summary>
		/// Resume monitoring the zimbra mailbox for new items.
		/// If monitoring is not paused, does nothing.
		/// </summary>
		private void ResumeMonitoring()
		{
			if( monitorThread != null ) 
			{
				monitorThread.Resume();
			}
		}

		/// <summary>
		/// Pause monitoring of the zimbra mailbox 
		/// </summary>
		private void PauseMonitoring()
		{
			if( monitorThread != null )
			{
				monitorThread.Suspend();
			}
		}

		/// <summary>
		/// Stop monitoring the zimbra mailbox
		/// </summary>
		private void StopMonitoring()
		{
			if( monitorThread == null  ) 
			{
				return;
			}

			monitorThread.Abort();
			monitorThread = null;
		}


		/// <summary>
		/// monitors the mailbox for new messages and displays 
		/// the toast for each new item.
		/// </summary>
		private void MonitorMailbox()
		{
			while( true )
			{
				try 
				{
					Zimbra.Client.MessageSummary[] msgs = zimbraSession.NewMsgs;

					if( msgs != null && msgs.Length > 0 ) 
					{
						CycleMessages( msgs );
					}

					System.Threading.Thread.Sleep( 1000 * 5 );
				}
				catch(Exception)
				{
				}
			}
		}

		private void CycleMessages(Zimbra.Client.MessageSummary[] msgs)
		{
			for( int i = 0; i < msgs.Length; i++ )
			{
				toaster.SetFields( msgs[i] );
				Invoke( new MethodInvoker(ShowToaster) );
				System.Threading.Thread.Sleep( 1000 * 3 );
			}

			Invoke( new MethodInvoker(HideToaster) );
		}

		private void ShowToaster()
		{
			toaster.Show();
		}

		private void HideToaster()
		{
			toaster.Hide();
		}


		#endregion


	}



	/// <summary>
	/// The toaster configuration - server, port, acct, etc
	/// </summary>
	class ToastConfig
	{
		//the server name (can end in :<port>)
		private String	server;

		//use a secure connection or clear?
		private bool	useSecure;

		//the zimbra account to monitor
		private String	account;

		//the password of the zimbra account
		private String	password;

		//the filename of the configuration file
		private static String filename = "ztoastcfg.xml";
		

		/// <summary>
		/// Default constructor - attempts to read the parameters from 
		/// configuration file.  It will throw if it has any problems.
		/// </summary>
		public ToastConfig()
		{
			XmlDocument doc = new XmlDocument();
			doc.Load(ParamFilename());

			server = doc.SelectSingleNode( "ZimbraToast/Server" ).InnerText;
			useSecure = bool.Parse( doc.SelectSingleNode( "ZimbraToast/UseSecure" ).InnerText );
			account = doc.SelectSingleNode( "ZimbraToast/Account" ).InnerText;
			password = doc.SelectSingleNode( "ZimbraToast/Password" ).InnerText;
		}

		/// <summary>
		/// Create a new ToastConfig object with the given params
		/// </summary>
		/// <param name="server">The server</param>
		/// <param name="useSecure">Use a secure connection</param>
		/// <param name="account">The account to monitor</param>
		/// <param name="password">The accounts password</param>
		public ToastConfig( String server, bool useSecure, String account, String password )
		{
			this.server = server;
			this.useSecure = useSecure;
			this.account = account;
			this.password = password;
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
			XmlElement zimbraToast = doc.CreateElement( "ZimbraToast" );
			doc.AppendChild( zimbraToast );

			XmlElement eServer = doc.CreateElement( "Server" );
			zimbraToast.AppendChild( eServer );
			eServer.InnerText = server;

			XmlElement eUseSecure = doc.CreateElement( "UseSecure" );
			zimbraToast.AppendChild( eUseSecure );
			eUseSecure.InnerText = useSecure.ToString();

			XmlElement eAccount = doc.CreateElement( "Account" );
			zimbraToast.AppendChild( eAccount );
			eAccount.InnerText = account;

			XmlElement ePassword = doc.CreateElement( "Password" );
			zimbraToast.AppendChild( ePassword );
			ePassword.InnerText = password;

			doc.Save(ParamFilename());
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

	}
}
