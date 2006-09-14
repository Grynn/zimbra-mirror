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
	public class ToastForm : System.Windows.Forms.Form
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
		private System.Windows.Forms.ImageList PopupImages;

		Zimbra.Client.MessageSummary currentMsg = null;
		Zimbra.Toast.Config config = null;

		enum PopupImagesIdx 
		{
			CLOSE_GRAY,
			CLOSE_RED,
			FLAG_GRAY,
			FLAG_RED,
			DELETE_GRAY,
			DELETE_RED,
		}

		public ToastForm(Config cfg)
		{
			this.config = cfg;
			InitializeComponent();
			ManualInitializeComponent();
		}

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
			config = null;
		}


		private void ManualInitializeComponent()
		{
			this.ClosePictureBox.Image = this.PopupImages.Images[ (int)PopupImagesIdx.CLOSE_GRAY ];
			this.FlagPictureBox.Image = this.PopupImages.Images[ (int)PopupImagesIdx.FLAG_GRAY ];
			this.DeletePictureBox.Image = this.PopupImages.Images[ (int)PopupImagesIdx.DELETE_GRAY ];
		}

		#region Windows Form Designer generated code
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
			this.BodyPanel.MouseMove += new System.Windows.Forms.MouseEventHandler(this.BodyPanel_MouseMove);
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
			this.SnippetLabel.Click += new System.EventHandler(this.ViewItemInBrowser);
			// 
			// SubjectLabel
			// 
			this.SubjectLabel.Cursor = System.Windows.Forms.Cursors.Hand;
			this.SubjectLabel.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((System.Byte)(0)));
			this.SubjectLabel.Location = new System.Drawing.Point(54, 22);
			this.SubjectLabel.Name = "SubjectLabel";
			this.SubjectLabel.Size = new System.Drawing.Size(244, 16);
			this.SubjectLabel.TabIndex = 16;
			this.SubjectLabel.Click += new System.EventHandler(this.ViewItemInBrowser);
			// 
			// DisplayNameLabel
			// 
			this.DisplayNameLabel.Cursor = System.Windows.Forms.Cursors.Hand;
			this.DisplayNameLabel.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((System.Byte)(0)));
			this.DisplayNameLabel.Location = new System.Drawing.Point(54, 6);
			this.DisplayNameLabel.Name = "DisplayNameLabel";
			this.DisplayNameLabel.Size = new System.Drawing.Size(178, 16);
			this.DisplayNameLabel.TabIndex = 15;
			this.DisplayNameLabel.Click += new System.EventHandler(this.ViewItemInBrowser);
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
			// 
			// ThinBlackSeperatorPanel
			// 
			this.ThinBlackSeperatorPanel.BackColor = System.Drawing.Color.Black;
			this.ThinBlackSeperatorPanel.Location = new System.Drawing.Point(0, 0);
			this.ThinBlackSeperatorPanel.Name = "ThinBlackSeperatorPanel";
			this.ThinBlackSeperatorPanel.Size = new System.Drawing.Size(368, 1);
			this.ThinBlackSeperatorPanel.TabIndex = 3;
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
			this.TitleBarPanel.MouseMove += new System.Windows.Forms.MouseEventHandler(this.TitleBarPanel_MouseMove);
			this.TitleBarPanel.MouseDown += new System.Windows.Forms.MouseEventHandler(this.TitleBarPanel_MouseDown);
			// 
			// PopupImages
			// 
			this.PopupImages.ImageSize = new System.Drawing.Size(16, 16);
			this.PopupImages.ImageStream = ((System.Windows.Forms.ImageListStreamer)(resources.GetObject("PopupImages.ImageStream")));
			this.PopupImages.TransparentColor = System.Drawing.Color.Transparent;
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
			this.ShowInTaskbar = false;
			this.Text = "Zimbra Toast";
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
		}

		private void ClosePictureBox_MouseLeave(object sender, System.EventArgs e)
		{
			this.ClosePictureBox.Image = this.PopupImages.Images[ (int)PopupImagesIdx.CLOSE_GRAY ];
		}

		private void FlagPictureBox_MouseEnter(object sender, System.EventArgs e)
		{
			this.FlagPictureBox.Image = this.PopupImages.Images[ (int)PopupImagesIdx.FLAG_RED ];
		}

		private void FlagPictureBox_MouseLeave(object sender, System.EventArgs e)
		{
			this.FlagPictureBox.Image = this.PopupImages.Images[ (int)PopupImagesIdx.FLAG_GRAY ];
		}

		private void DeletePictureBox_MouseEnter(object sender, System.EventArgs e)
		{
			this.DeletePictureBox.Image = this.PopupImages.Images[ (int)PopupImagesIdx.DELETE_RED];
		}

		private void DeletePictureBox_MouseLeave(object sender, System.EventArgs e)
		{
			this.DeletePictureBox.Image = this.PopupImages.Images[ (int)PopupImagesIdx.DELETE_GRAY];		
		}
		#endregion 


		private void UpdateMessageLabel( System.Windows.Forms.Label l, String s, String e )
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

		private void SetFrom( String from )
		{
			UpdateMessageLabel( this.DisplayNameLabel, from, "<Unknown Sender>" );
		}

		private void SetSubject( String subject )
		{
			UpdateMessageLabel( this.SubjectLabel, subject, "<No Subject>" );
		}

		private void SetSnippet( String snippet )
		{
			UpdateMessageLabel( this.SnippetLabel, snippet, "<No body>" );
		}

		public void SetFields( Zimbra.Client.MessageSummary msg )
		{
			this.currentMsg = msg;
			SetFrom( msg.email_personal_name );
			SetSubject( msg.subject );
			SetSnippet( msg.fragment );
		}


		private void ZimbraLogoPictureBox_Click(object sender, System.EventArgs e)
		{
			//open http://www.zimbra.com using the default browser
		}

		private void ClosePictureBox_Click(object sender, System.EventArgs e)
		{
			this.Hide();
		}

		private void ViewItemInBrowser(object sender, System.EventArgs e)
		{
			if( currentMsg == null || currentMsg.itemId == null || currentMsg.itemId.Length <= 0 )
				return;

			System.Diagnostics.Process.Start( config.GetItemUri( currentMsg.itemId ) );
		}

	}

}
