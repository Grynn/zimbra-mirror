/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Toaster
 * Copyright (C) 2006, 2007, 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
using System;
using System.Drawing;
using System.Collections;
using System.ComponentModel;
using System.Windows.Forms;
using System.Data;

namespace Zimbra.Toast
{


	/// <summary>
	/// The main toaster window - displays alert data
	/// </summary>
	public class ToastForm : TransparentDialog
	{
		private System.Windows.Forms.Panel RootPanel;
		private System.Windows.Forms.Panel BodyPanel;
		private System.Windows.Forms.Label SnippetLabel;
		private System.Windows.Forms.Label SubjectLabel;
		private System.Windows.Forms.Label DisplayNameLabel;
		private System.Windows.Forms.PictureBox ZimbraLogoPictureBox;
		private System.Windows.Forms.Panel ThinBlackSeperatorPanel;
		private System.Windows.Forms.Panel TitleBarPanel;
		private System.Windows.Forms.PictureBox ClosePictureBox;
		private System.ComponentModel.IContainer components;
		private System.Windows.Forms.PictureBox FlagPictureBox;
		private System.Windows.Forms.PictureBox DeletePictureBox;
		private System.Windows.Forms.Timer timer;
		private System.Windows.Forms.ImageList PopupImages;
		private Zimbra.Client.MessageSummary msg;

		enum PopupImagesIdx 
		{
			CLOSE_GRAY,
			CLOSE_RED,
			FLAG_GRAY,
			FLAG_RED,
			DELETE_GRAY,
			DELETE_RED,
		}

		/// <summary>
		/// how long to pause the toaster when at full opacity
		/// </summary>
		private Int32 pauseInterval = 3000;

		#region Custom events and delegates

		/// <summary>
		/// delegate for handling OnOpenItem
		/// </summary>
		public delegate void OpenItemHandler( String itemId );

		/// <summary>
		/// Delegate for handling OnFlagItem
		/// </summary>
		public delegate void FlagItemHandler( String itemId );

		/// <summary>
		/// Delegate for handling OnDeleteItem
		/// </summary>
		public delegate void DeleteItemHandler( String itemId );

		/// <summary>
		/// Fired when the user wants to open the current item
		/// </summary>
		public event OpenItemHandler OnOpenItem;

		/// <summary>
		/// Fired when the user wants to flag the current item
		/// </summary>
		public event FlagItemHandler OnFlagItem;


		/// <summary>
		/// Fired when the user wants to delete the item
		/// </summary>
		public event DeleteItemHandler OnDeleteItem;


		#endregion


		/// <summary>
		/// 
		/// </summary>
		/// <param name="msg"></param>
		/// <param name="maxOpacity"></param>
		/// <param name="opacityDelta"></param>
		/// <param name="opacityUpdateInterval"></param>
		/// <param name="pauseInterval"></param>
		public ToastForm(
			Zimbra.Client.MessageSummary msg, 
			double maxOpacity, 
			double opacityDelta, 
			Int32 opacityUpdateInterval,
			Int32 pauseInterval ) : base( maxOpacity, opacityDelta, opacityUpdateInterval )
		{
			this.msg = msg;
			this.pauseInterval = pauseInterval;
			InitializeComponent();
			ManualInitializeComponent();
		}


		#region cleanup
		/// <summary>
		/// Clean up any resources being used.
		/// </summary>
		protected override void Dispose( bool disposing )
		{
			if( disposing )
			{
				if (components != null) 
				{
					components.Dispose();
				}
			}
			base.Dispose( disposing );
		}
		#endregion

		#region Form related initialization
		
		/// <summary>
		/// Additional componenet configuration
		/// </summary>
		private void ManualInitializeComponent()
		{
			this.ClosePictureBox.Image = this.PopupImages.Images[ (int)PopupImagesIdx.CLOSE_GRAY ];
			this.FlagPictureBox.Image = this.PopupImages.Images[ (int)PopupImagesIdx.FLAG_GRAY ];
			this.DeletePictureBox.Image = this.PopupImages.Images[ (int)PopupImagesIdx.DELETE_GRAY ];

		}

		/// <summary>
		/// Required method for Designer support - do not modify
		/// the contents of this method with the code editor.
		/// </summary>
		private void InitializeComponent()
		{
			this.components = new System.ComponentModel.Container();
			System.Resources.ResourceManager resources = new System.Resources.ResourceManager(typeof(ToastForm));
			this.RootPanel = new System.Windows.Forms.Panel();
			this.BodyPanel = new System.Windows.Forms.Panel();
			this.DeletePictureBox = new System.Windows.Forms.PictureBox();
			this.FlagPictureBox = new System.Windows.Forms.PictureBox();
			this.ClosePictureBox = new System.Windows.Forms.PictureBox();
			this.SnippetLabel = new System.Windows.Forms.Label();
			this.SubjectLabel = new System.Windows.Forms.Label();
			this.DisplayNameLabel = new System.Windows.Forms.Label();
			this.ZimbraLogoPictureBox = new System.Windows.Forms.PictureBox();
			this.ThinBlackSeperatorPanel = new System.Windows.Forms.Panel();
			this.TitleBarPanel = new System.Windows.Forms.Panel();
			this.PopupImages = new System.Windows.Forms.ImageList(this.components);
			this.timer = new System.Windows.Forms.Timer(this.components);
			this.RootPanel.SuspendLayout();
			this.BodyPanel.SuspendLayout();
			this.SuspendLayout();
			// 
			// RootPanel
			// 
			this.RootPanel.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
			this.RootPanel.Controls.Add(this.BodyPanel);
			this.RootPanel.Controls.Add(this.TitleBarPanel);
			this.RootPanel.Dock = System.Windows.Forms.DockStyle.Fill;
			this.RootPanel.Location = new System.Drawing.Point(0, 0);
			this.RootPanel.Name = "RootPanel";
			this.RootPanel.Size = new System.Drawing.Size(330, 78);
			this.RootPanel.TabIndex = 0;
			this.RootPanel.MouseEnter += new System.EventHandler(this.ToastForm_MouseEnter);
			this.RootPanel.MouseLeave += new System.EventHandler(this.ToastForm_MouseLeave);
			// 
			// BodyPanel
			// 
			this.BodyPanel.Controls.Add(this.DeletePictureBox);
			this.BodyPanel.Controls.Add(this.FlagPictureBox);
			this.BodyPanel.Controls.Add(this.ClosePictureBox);
			this.BodyPanel.Controls.Add(this.SnippetLabel);
			this.BodyPanel.Controls.Add(this.SubjectLabel);
			this.BodyPanel.Controls.Add(this.DisplayNameLabel);
			this.BodyPanel.Controls.Add(this.ZimbraLogoPictureBox);
			this.BodyPanel.Controls.Add(this.ThinBlackSeperatorPanel);
			this.BodyPanel.Dock = System.Windows.Forms.DockStyle.Fill;
			this.BodyPanel.Location = new System.Drawing.Point(0, 6);
			this.BodyPanel.Name = "BodyPanel";
			this.BodyPanel.Size = new System.Drawing.Size(328, 70);
			this.BodyPanel.TabIndex = 1;
			this.BodyPanel.MouseUp += new System.Windows.Forms.MouseEventHandler(this.BodyPanel_MouseUp);
			this.BodyPanel.MouseEnter += new System.EventHandler(this.ToastForm_MouseEnter);
			this.BodyPanel.MouseMove += new System.Windows.Forms.MouseEventHandler(this.BodyPanel_MouseMove);
			this.BodyPanel.MouseLeave += new System.EventHandler(this.ToastForm_MouseLeave);
			this.BodyPanel.MouseDown += new System.Windows.Forms.MouseEventHandler(this.BodyPanel_MouseDown);
			// 
			// DeletePictureBox
			// 
			this.DeletePictureBox.Cursor = System.Windows.Forms.Cursors.Hand;
			this.DeletePictureBox.Location = new System.Drawing.Point(32, 44);
			this.DeletePictureBox.Name = "DeletePictureBox";
			this.DeletePictureBox.Size = new System.Drawing.Size(16, 16);
			this.DeletePictureBox.TabIndex = 22;
			this.DeletePictureBox.TabStop = false;
			this.DeletePictureBox.Click += new System.EventHandler(this.DeletePictureBox_Click);
			this.DeletePictureBox.MouseEnter += new System.EventHandler(this.DeletePictureBox_MouseEnter);
			this.DeletePictureBox.MouseLeave += new System.EventHandler(this.DeletePictureBox_MouseLeave);
			// 
			// FlagPictureBox
			// 
			this.FlagPictureBox.Cursor = System.Windows.Forms.Cursors.Hand;
			this.FlagPictureBox.Location = new System.Drawing.Point(8, 44);
			this.FlagPictureBox.Name = "FlagPictureBox";
			this.FlagPictureBox.Size = new System.Drawing.Size(16, 16);
			this.FlagPictureBox.TabIndex = 21;
			this.FlagPictureBox.TabStop = false;
			this.FlagPictureBox.Click += new System.EventHandler(this.FlagPictureBox_Click);
			this.FlagPictureBox.MouseEnter += new System.EventHandler(this.FlagPictureBox_MouseEnter);
			this.FlagPictureBox.MouseLeave += new System.EventHandler(this.FlagPictureBox_MouseLeave);
			// 
			// ClosePictureBox
			// 
			this.ClosePictureBox.Cursor = System.Windows.Forms.Cursors.Hand;
			this.ClosePictureBox.Location = new System.Drawing.Point(306, 4);
			this.ClosePictureBox.Name = "ClosePictureBox";
			this.ClosePictureBox.Size = new System.Drawing.Size(16, 16);
			this.ClosePictureBox.TabIndex = 20;
			this.ClosePictureBox.TabStop = false;
			this.ClosePictureBox.Click += new System.EventHandler(this.ClosePictureBox_Click);
			this.ClosePictureBox.MouseEnter += new System.EventHandler(this.ClosePictureBox_MouseEnter);
			this.ClosePictureBox.MouseLeave += new System.EventHandler(this.ClosePictureBox_MouseLeave);
			// 
			// SnippetLabel
			// 
			this.SnippetLabel.Cursor = System.Windows.Forms.Cursors.Hand;
			this.SnippetLabel.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((System.Byte)(0)));
			this.SnippetLabel.ForeColor = System.Drawing.Color.DimGray;
			this.SnippetLabel.Location = new System.Drawing.Point(54, 36);
			this.SnippetLabel.Name = "SnippetLabel";
			this.SnippetLabel.Size = new System.Drawing.Size(270, 28);
			this.SnippetLabel.TabIndex = 17;
			this.SnippetLabel.Click += new System.EventHandler(this.ZimbraItem_Click);
			this.SnippetLabel.MouseEnter += new System.EventHandler(this.ToastForm_MouseEnter);
			this.SnippetLabel.MouseLeave += new System.EventHandler(this.ToastForm_MouseLeave);
			// 
			// SubjectLabel
			// 
			this.SubjectLabel.Cursor = System.Windows.Forms.Cursors.Hand;
			this.SubjectLabel.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((System.Byte)(0)));
			this.SubjectLabel.Location = new System.Drawing.Point(54, 22);
			this.SubjectLabel.Name = "SubjectLabel";
			this.SubjectLabel.Size = new System.Drawing.Size(244, 16);
			this.SubjectLabel.TabIndex = 16;
			this.SubjectLabel.Click += new System.EventHandler(this.ZimbraItem_Click);
			this.SubjectLabel.MouseEnter += new System.EventHandler(this.ToastForm_MouseEnter);
			this.SubjectLabel.MouseLeave += new System.EventHandler(this.ToastForm_MouseLeave);
			// 
			// DisplayNameLabel
			// 
			this.DisplayNameLabel.Cursor = System.Windows.Forms.Cursors.Hand;
			this.DisplayNameLabel.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((System.Byte)(0)));
			this.DisplayNameLabel.Location = new System.Drawing.Point(54, 6);
			this.DisplayNameLabel.Name = "DisplayNameLabel";
			this.DisplayNameLabel.Size = new System.Drawing.Size(178, 16);
			this.DisplayNameLabel.TabIndex = 15;
			this.DisplayNameLabel.Click += new System.EventHandler(this.ZimbraItem_Click);
			this.DisplayNameLabel.MouseEnter += new System.EventHandler(this.ToastForm_MouseEnter);
			this.DisplayNameLabel.MouseLeave += new System.EventHandler(this.ToastForm_MouseLeave);
			// 
			// ZimbraLogoPictureBox
			// 
			this.ZimbraLogoPictureBox.Cursor = System.Windows.Forms.Cursors.Hand;
			this.ZimbraLogoPictureBox.Image = ((System.Drawing.Image)(resources.GetObject("ZimbraLogoPictureBox.Image")));
			this.ZimbraLogoPictureBox.Location = new System.Drawing.Point(10, 10);
			this.ZimbraLogoPictureBox.Name = "ZimbraLogoPictureBox";
			this.ZimbraLogoPictureBox.Size = new System.Drawing.Size(36, 24);
			this.ZimbraLogoPictureBox.TabIndex = 12;
			this.ZimbraLogoPictureBox.TabStop = false;
			this.ZimbraLogoPictureBox.Click += new System.EventHandler(this.ZimbraLogoPictureBox_Click);
			this.ZimbraLogoPictureBox.MouseEnter += new System.EventHandler(this.ToastForm_MouseEnter);
			this.ZimbraLogoPictureBox.MouseLeave += new System.EventHandler(this.ToastForm_MouseLeave);
			// 
			// ThinBlackSeperatorPanel
			// 
			this.ThinBlackSeperatorPanel.BackColor = System.Drawing.Color.Black;
			this.ThinBlackSeperatorPanel.Location = new System.Drawing.Point(0, 0);
			this.ThinBlackSeperatorPanel.Name = "ThinBlackSeperatorPanel";
			this.ThinBlackSeperatorPanel.Size = new System.Drawing.Size(368, 1);
			this.ThinBlackSeperatorPanel.TabIndex = 3;
			this.ThinBlackSeperatorPanel.MouseEnter += new System.EventHandler(this.ToastForm_MouseEnter);
			this.ThinBlackSeperatorPanel.MouseLeave += new System.EventHandler(this.ToastForm_MouseLeave);
			// 
			// TitleBarPanel
			// 
			this.TitleBarPanel.BackColor = System.Drawing.Color.Red;
			this.TitleBarPanel.Cursor = System.Windows.Forms.Cursors.Default;
			this.TitleBarPanel.Dock = System.Windows.Forms.DockStyle.Top;
			this.TitleBarPanel.Location = new System.Drawing.Point(0, 0);
			this.TitleBarPanel.Name = "TitleBarPanel";
			this.TitleBarPanel.Size = new System.Drawing.Size(328, 6);
			this.TitleBarPanel.TabIndex = 0;
			this.TitleBarPanel.MouseUp += new System.Windows.Forms.MouseEventHandler(this.TitleBarPanel_MouseUp);
			this.TitleBarPanel.MouseEnter += new System.EventHandler(this.ToastForm_MouseEnter);
			this.TitleBarPanel.MouseMove += new System.Windows.Forms.MouseEventHandler(this.TitleBarPanel_MouseMove);
			this.TitleBarPanel.MouseLeave += new System.EventHandler(this.ToastForm_MouseLeave);
			this.TitleBarPanel.MouseDown += new System.Windows.Forms.MouseEventHandler(this.TitleBarPanel_MouseDown);
			// 
			// PopupImages
			// 
			this.PopupImages.ImageSize = new System.Drawing.Size(16, 16);
			this.PopupImages.ImageStream = ((System.Windows.Forms.ImageListStreamer)(resources.GetObject("PopupImages.ImageStream")));
			this.PopupImages.TransparentColor = System.Drawing.Color.Transparent;
			// 
			// timer
			// 
			this.timer.Interval = this.pauseInterval;
			this.timer.Tick += new System.EventHandler(this.timer_Tick);
			// 
			// ToastForm
			// 
			this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
			this.BackColor = System.Drawing.Color.White;
			this.ClientSize = new System.Drawing.Size(330, 78);
			this.ControlBox = false;
			this.Controls.Add(this.RootPanel);
			this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.None;
			this.MaximizeBox = false;
			this.MinimizeBox = false;
			this.Name = "ToastForm";
			this.Opacity = 0.8;
			this.ShowInTaskbar = false;
			this.StartPosition = System.Windows.Forms.FormStartPosition.Manual;
			this.Text = "Zimbra Toast";
			this.Load += new System.EventHandler(this.ToastForm_Load);
			this.MouseEnter += new System.EventHandler(this.ToastForm_MouseEnter);
			this.MouseLeave += new System.EventHandler(this.ToastForm_MouseLeave);
			this.RootPanel.ResumeLayout(false);
			this.BodyPanel.ResumeLayout(false);
			this.ResumeLayout(false);

		}
		#endregion

		#region Window Dragging Mouse Handlers

		private int dragX = 0;
		private int dragY = 0;
		private bool bDragging = false;
		private void TitleBarPanel_MouseDown(object sender, System.Windows.Forms.MouseEventArgs e)
		{
			StartDrag( e.X, e.Y );
		}

		private void TitleBarPanel_MouseUp(object sender, System.Windows.Forms.MouseEventArgs e)
		{
			StopDrag();
		}

		private void TitleBarPanel_MouseMove(object sender, System.Windows.Forms.MouseEventArgs e)
		{
			DoDrag( e.X, e.Y );
		}

		private void BodyPanel_MouseDown(object sender, System.Windows.Forms.MouseEventArgs e)
		{
			StartDrag( e.X, e.Y );
		}

		private void BodyPanel_MouseUp(object sender, System.Windows.Forms.MouseEventArgs e)
		{
			StopDrag();
		}

		private void BodyPanel_MouseMove(object sender, System.Windows.Forms.MouseEventArgs e)
		{
			DoDrag( e.X, e.Y );
		}
		
		private void StartDrag( int x, int y )
		{
			dragX = x;
			dragY = y;
			bDragging = true;
		}

		private void StopDrag()
		{
			bDragging = false;
		}

		private void DoDrag( int x, int y )
		{
			if( !bDragging ) 
			{
				return;
			}

			this.Top  += y - dragY;
			this.Left += x - dragX;
		}

		#endregion

		#region Image Swapping Mouse Handlers
		private void ClosePictureBox_MouseEnter(object sender, System.EventArgs e)
		{
			this.ClosePictureBox.Image = this.PopupImages.Images[ (int)PopupImagesIdx.CLOSE_RED ];
			ToastForm_MouseEnter( sender, e );
		}

		private void ClosePictureBox_MouseLeave(object sender, System.EventArgs e)
		{
			this.ClosePictureBox.Image = this.PopupImages.Images[ (int)PopupImagesIdx.CLOSE_GRAY ];
			ToastForm_MouseLeave( sender, e );
		}

		private void FlagPictureBox_MouseEnter(object sender, System.EventArgs e)
		{
			this.FlagPictureBox.Image = this.PopupImages.Images[ (int)PopupImagesIdx.FLAG_RED ];
			ToastForm_MouseEnter( sender, e );
		}

		private void FlagPictureBox_MouseLeave(object sender, System.EventArgs e)
		{
			this.FlagPictureBox.Image = this.PopupImages.Images[ (int)PopupImagesIdx.FLAG_GRAY ];
			ToastForm_MouseLeave( sender, e );
		}

		private void DeletePictureBox_MouseEnter(object sender, System.EventArgs e)
		{
			this.DeletePictureBox.Image = this.PopupImages.Images[ (int)PopupImagesIdx.DELETE_RED];
			ToastForm_MouseEnter( sender, e );
		}

		private void DeletePictureBox_MouseLeave(object sender, System.EventArgs e)
		{
			this.DeletePictureBox.Image = this.PopupImages.Images[ (int)PopupImagesIdx.DELETE_GRAY];	
			ToastForm_MouseLeave( sender, e );
		}
		#endregion 

		#region Update the text on the toaster
		/// <summary>
		/// Helper to update a label on the toaster
		/// </summary>
		/// <param name="l">the label to update</param>
		/// <param name="s">the string to put int the label</param>
		/// <param name="e">if s is invalid, put </param>
		private void  UpdateMessageLabel( System.Windows.Forms.Label l, String s, String e )
		{
			if( s == null || s.Length == 0 ) 
			{
				l.Text = e;
			}
			else
			{
				l.Text = s;
			}
		}

		/// <summary>
		/// Update the from label on the toaster
		/// </summary>
		/// <param name="from"></param>
		private void SetFrom( String from )
		{
			UpdateMessageLabel( this.DisplayNameLabel, from, "<Unknown Sender>" );
		}

		/// <summary>
		/// update the subject on the toaster
		/// </summary>
		/// <param name="subject"></param>
		private void SetSubject( String subject )
		{
			UpdateMessageLabel( this.SubjectLabel, subject, "<No Subject>" );
		}

		/// <summary>
		/// update the snippet on the toaster
		/// </summary>
		/// <param name="snippet"></param>
		private void SetSnippet( String snippet )
		{
			UpdateMessageLabel( this.SnippetLabel, snippet, "<No body>" );
		}

		#endregion

		#region Button Handlers

		/// <summary>
		/// the close button was clicked
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void ClosePictureBox_Click(object sender, System.EventArgs e)
		{
			this.CloseNoFade();
		}

		/// <summary>
		/// something about the item was clicked
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void ZimbraItem_Click(object sender, System.EventArgs e )
		{
			if( this.msg.itemId != null )
			{
				OnOpenItem(this.msg.itemId);
			}
		}

		/// <summary>
		/// the zimbra logo was clicked
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void ZimbraLogoPictureBox_Click(object sender, System.EventArgs e)
		{
			System.Diagnostics.Process.Start( "http://www.zimbra.com" );
		}

		/// <summary>
		/// the flag button was clicked
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void FlagPictureBox_Click(object sender, System.EventArgs e)
		{
			//fire OnFlagItem
			if( this.msg.itemId != null ) 
			{
				OnFlagItem(this.msg.itemId);
				this.FlagPictureBox.Enabled = false;
			}
		}

		/// <summary>
		/// the delete button was clicked
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void DeletePictureBox_Click(object sender, System.EventArgs e)
		{
			//fire OnDeleteItem
			if( this.msg.itemId != null )
			{
				OnDeleteItem(this.msg.itemId);
				this.DeletePictureBox.Enabled = false;
			}
		}

		#endregion

		#region Window fade-in / fade-out handling
		/// <summary>
		/// Called when the timer expires incidacting the toaster
		/// should start closing
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void timer_Tick(object sender, System.EventArgs e)
		{
			Close();
		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void ToastForm_Load(object sender, System.EventArgs e)
		{
			timer.Enabled = true;
			SetFrom( msg.email_address );
			SetSubject( msg.subject );
			SetSnippet( msg.fragment );
		}


		/// <summary>
		/// stop fading and stop the close-window-timer
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void ToastForm_MouseEnter(object sender, System.EventArgs e)
		{
			//stop fading, set opacity to max
			timer.Stop();
			CancelFade();
		}


		/// <summary>
		/// resume fading-out
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void ToastForm_MouseLeave(object sender, System.EventArgs e)
		{
			//resume fading
			this.Opacity = base.maxOpacity;
			Close();
		}
		#endregion


	}







	/// <summary>
	/// Base class that gives any form that derives from it the effect of slowly 
	/// appearing and then disapperaing. Much like outlook email notification pop-ups.
	/// </summary>
	public class TransparentDialog : System.Windows.Forms.Form
	{
		private bool			m_bShowing		= true;
		private bool			m_bForceClose	= false;
		private Timer			m_clock			= null;
		private IContainer		components		= null;
		private DialogResult	m_origDialogResult;

		/// <summary>
		/// 
		/// </summary>
		protected double maxOpacity = 0.8;
		
		/// <summary>
		/// 
		/// </summary>
		protected double opacityDelta = 0.05;
		
		/// <summary>
		/// 
		/// </summary>
		protected Int32 opacityUpdateInterval = 10;
		
		/// <summary>
		/// 
		/// </summary>
		protected double minOpacity = 0.0;


		/// <summary>
		/// 
		/// </summary>
		/// <param name="maxOpacity"></param>
		/// <param name="opacityDelta"></param>
		/// <param name="opacityUpdateInterval"></param>
		public TransparentDialog( double maxOpacity, double opacityDelta, Int32 opacityUpdateInterval )
		{
			this.maxOpacity = maxOpacity;
			this.opacityDelta = opacityDelta;
			this.opacityUpdateInterval = opacityUpdateInterval;
			InitializeComponents();
		}
		
		/// <summary>
		/// 
		/// </summary>
		void InitializeComponents()
		{
			this.components = new System.ComponentModel.Container();
			this.m_clock =  new Timer(this.components);
			this.m_clock.Interval = opacityUpdateInterval;
			this.SuspendLayout();
			this.m_clock.Tick += new EventHandler(Animate);
			this.Load += new EventHandler(TransparentDialog_Load);
			this.Closing += new CancelEventHandler(TransparentDialog_Closing);
			this.ResumeLayout(false);
			this.PerformLayout();
		}

		/// <summary>
		/// Cancel the currently in-progress fade and show the window
		/// </summary>
		protected void CancelFade()
		{
			m_clock.Stop();
			this.Opacity = 1;
		}


		/// <summary>
		/// Close the window without fading out
		/// </summary>
		protected void CloseNoFade()
		{
			m_bForceClose = true;
			Close();
		}


		/// <summary>
		/// 
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void TransparentDialog_Load(object sender, EventArgs e)
		{
			this.Opacity = 0.0;
			m_bShowing = true;

			m_clock.Start();
		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void TransparentDialog_Closing(object sender, CancelEventArgs e)
		{
			if (!m_bForceClose)
			{
				m_origDialogResult = this.DialogResult;
				e.Cancel = true;
				m_bShowing = false;
				m_clock.Start();
			}
			else
			{
				this.DialogResult = m_origDialogResult;
			}
		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void Animate(object sender, EventArgs e)
		{
			double o = this.Opacity;
			if (m_bShowing)
			{
				if (this.Opacity < maxOpacity)
				{
					this.Opacity += opacityDelta;
				}
				else
				{
					m_clock.Stop();
				}
			}
			else
			{
				if (this.Opacity > minOpacity)
				{
					this.Opacity -= opacityDelta;
				}
				else
				{
					m_clock.Stop();
					m_bForceClose = true;
					this.Close();
					this.Dispose();
				}
			}
		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="disposing"></param>
		protected override void Dispose(bool disposing)
		{
			if (disposing && (components != null))
			{
				components.Dispose();
			}
			base.Dispose(disposing);
		}
	}
}
