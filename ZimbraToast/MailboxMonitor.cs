/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Toaster
 * Copyright (C) 2006, 2007, 2009, 2010 Zimbra, Inc.
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
using System;
using System.Threading;
using System.Runtime.CompilerServices;

namespace Zimbra.Toast
{
	/// <summary>
	/// Monitor a mailbox for changes
	/// </summary>
	public class MailboxMonitor
	{
		//default polling interval is 5 minutes
		private static UInt16 DEFAULT_POLL_INTERVAL = 5;

		//the thread that monitors the mailbox and fires event
		private Thread monitorThread = null;

		// used to unblock the monitoring thread
		private AutoResetEvent wakeEvent = null;

		//the zimbra client session
		private ZimbraSession zimbraSession = null;

		//how often to check for new items on the server (minutes)
		private UInt16 pollInterval = DEFAULT_POLL_INTERVAL;

		//the previous set of messages that were obtained
		private Zimbra.Client.MessageSummary[] prevMsgs = null;

		/// <summary>
		/// Handle new message notifications
		/// </summary>
		public delegate void NewMsgHandler( Zimbra.Client.MessageSummary[] newMsgs, System.Threading.AutoResetEvent are );

		/// <summary>
		/// Fired when new messages have arrived on the server
		/// </summary>
		public event NewMsgHandler OnNewMsgs;



		/// <summary>
		/// Craete a mailbox monitor for the given account.  Communicate with the give server
		/// </summary>
		/// <param name="session">The zimbra sesson to use when monitoring</param>
		/// <param name="pollInterval">schedule monitoring interval</param>
		public MailboxMonitor( ZimbraSession session, UInt16 pollInterval )
		{
			wakeEvent = new AutoResetEvent(false);
			Update( session, pollInterval );
		}


		/// <summary>
		/// Update the account/server configuration.  If monitoring is in progress, it will
		/// be stopped and resarted.
		/// </summary>
		/// <param name="session">The session to use when monitoring</param>
		/// <param name="pollInterval">seconds before checking for new items on the server</param>
		[MethodImpl(MethodImplOptions.Synchronized)]
		public void Update( ZimbraSession session, UInt16 pollInterval )
		{
			bool bMonitoring = IsMonitoring();

			StopMonitoring();

			zimbraSession = session;
			this.pollInterval = pollInterval;

			if( bMonitoring )
				StartMonitoring();
		}



		/// <summary>
		/// True if the mailbox is being monitored actively
		/// </summary>
		[MethodImpl(MethodImplOptions.Synchronized)]
		public bool IsMonitoring()
		{
			return 
				( monitorThread != null && 
					( ( monitorThread.ThreadState & (ThreadState.Stopped | ThreadState.Unstarted ) ) == 0 ) );
		}


		/// <summary>
		/// Start monitoring the zimbra mailbox for new items.  
		/// If monitoring has started, it will be stopped and restarted.
		/// </summary>
		[MethodImpl(MethodImplOptions.Synchronized)]
		public void StartMonitoring()
		{
			if( monitorThread != null )
			{
				StopMonitoring();
			}

			monitorThread = new System.Threading.Thread( new System.Threading.ThreadStart(MonitorMailbox) );
			monitorThread.Start();
		}



		/// <summary>
		/// Resume monitoring the zimbra mailbox for new items.
		/// If monitoring is not paused, does nothing.
		/// </summary>
		[MethodImpl(MethodImplOptions.Synchronized)]
		public void ResumeMonitoring()
		{
			if( monitorThread != null ) 
			{
				monitorThread.Resume();
			}
		}



		/// <summary>
		/// Pause monitoring of the zimbra mailbox 
		/// </summary>
		[MethodImpl(MethodImplOptions.Synchronized)]
		public void PauseMonitoring()
		{
			if( monitorThread != null )
			{
				monitorThread.Suspend();
			}
		}


		/// <summary>
		/// Stop monitoring the zimbra mailbox
		/// </summary>
		[MethodImpl(MethodImplOptions.Synchronized)]
		public void StopMonitoring()
		{
			if( monitorThread == null  ) 
			{
				return;
			}
			monitorThread.Abort();
			monitorThread = null;
		}



		/// <summary>
		/// True if the fault is informing the client it must re-authenticate
		/// </summary>
		/// <param name="faultCode">the lowercase fault code returned by the server</param>
		/// <returns>true, if the fault is informing the client it must re-authenticate</returns>
		private static bool RequiresReAuth( String faultCode )
		{
			return faultCode.CompareTo( "service.auth_required" ) == 0 || 
				   faultCode.CompareTo( "service.auth_expired"  ) == 0;
		}


		/// <summary>
		/// true if the fault is temporary and should be silently ignored
		/// </summary>
		/// <param name="faultCode">the lowercase fault code returned by the server</param>
		/// <returns>true if the fault is temporary and should be silently ignored</returns>
		private static bool IgnorableFault( String faultCode )
		{
			return faultCode.CompareTo( "service.temporarily_unavailable" ) == 0 ||
				   faultCode.CompareTo( "account.maintenance_mode" ) == 0 ||
				   faultCode.CompareTo( "mail.maintenance" ) == 0;
		}


		/// <summary>
		/// monitors the mailbox for new messages and fires
		/// 
		/// the toast for each new item.
		/// </summary>
		private void MonitorMailbox()
		{
			//create a timer to wake this thread up periodically
			long lSleep = pollInterval * 1000 * 60;
			System.Threading.Timer t = new Timer( new TimerCallback(CheckForNewMsgs), null, lSleep, lSleep );
			bool bError = false;
			while( true )
			{
				try 
				{
					CheckMailbox_Internal();
					bError = false;
				}
				catch(System.Threading.ThreadAbortException)
				{
					//if the thread is reqested to die, really kill it
					return;
				}
				catch(Zimbra.Client.ZimbraException ze)
				{
					String faultCode = ze.Fault.Code.ToLower();

					if( RequiresReAuth(faultCode) ) 
					{
						zimbraSession.InvalidateAuthToken();
					}
					else if( !IgnorableFault(faultCode)  )
					{
						if( bError ) 
						{
							//if an soap fault was thrown, let the user know
							//and continue monitoring the mailbox
							System.Windows.Forms.MessageBox.Show(
								"Zimbra server returned an error message: " +  ze.Fault.Code + "\n" + ze.Fault.Description + "\n", 
								"Zimbra Toast - Error", 
								System.Windows.Forms.MessageBoxButtons.OK, System.Windows.Forms.MessageBoxIcon.Error );
						}
						bError = true;
					}
				}
				catch(System.Net.WebException we)
				{
					if( we.Status == System.Net.WebExceptionStatus.TrustFailure ) 
					{
						System.Windows.Forms.DialogResult dr;
						dr = System.Windows.Forms.MessageBox.Show(
							"Error Monitoring Mailbox\nThe security certificate presented be the server is not valid.\n\n" +
							"Continue communicating with the server?",
							"Zimbra Toast - Invalid Certificate",
							System.Windows.Forms.MessageBoxButtons.YesNo, System.Windows.Forms.MessageBoxIcon.Question );
						if( dr == System.Windows.Forms.DialogResult.Yes ) 
						{
							System.Net.ServicePointManager.CertificatePolicy = new InvalidCertOKPolicy();
						}
					}	
					else
					{
						if( bError ) 
						{
							System.Windows.Forms.MessageBox.Show(
								"WebException: " + we.Status.ToString() + "\n" + 
								we.Message + "\n" + we.StackTrace,
								"Zimbra Toast - WebException",
								System.Windows.Forms.MessageBoxButtons.OK, System.Windows.Forms.MessageBoxIcon.Error );
						}
						bError = true;
					}
				}
				catch(Exception e)
				{
					if( bError ) 
					{
						//if some other error occurred, let the user know
						//and continue monitoring the mailbox
						String msg = e.Message;
						Type type = e.GetType();
						String stackTrace = e.StackTrace;

						System.Windows.Forms.MessageBox.Show(
							"Error monitoring mailbox\n" + msg + "\n\n" + type + "\n" + stackTrace, 
							"Zimbra Toast - Unexpected Error", 
							System.Windows.Forms.MessageBoxButtons.OK, System.Windows.Forms.MessageBoxIcon.Error );
					}
					bError = true;
				}
				wakeEvent.WaitOne();
			}
		}


		/// <summary>
		/// Set the flag to check for new messages and signal the worker thread
		/// </summary>
		/// <param name="o">nothing</param>
		private void CheckForNewMsgs(object o)
		{
			this.prevMsgs = null;
			SignalWorkerThread(o);
		}


		/// <summary>
		/// Release the worker thread so it can check for new items on the server
		/// </summary>
		/// <param name="o"></param>
		private void SignalWorkerThread(object o)
		{
			wakeEvent.Set();
		}


		/// <summary>
		/// check the mailbox right now to see if there are any new messages
		/// can be called from any thread - request handled asynchronously
		/// </summary>
		/// <param name="msgs">Messages to display - if null, check server</param>
		public void CheckMailbox(Zimbra.Client.MessageSummary[] msgs)
		{
			this.prevMsgs = msgs;
			SignalWorkerThread(null);
		}


		/// <summary>
		/// check the mailbox right now to see if there are any new messages
		/// must run in the mailbox monitors worker thread
		/// </summary>
		private bool CheckMailbox_Internal()
		{
			lock(this)
			{
				Zimbra.Client.MessageSummary[] inboxMsgs = this.prevMsgs;

				if( this.prevMsgs == null ) 
				{
					//check for new messages
					Zimbra.Client.MessageSummary[] msgs = zimbraSession.NewMsgs;
							
					//if we got new stuff, fire off the events
					if( msgs != null && msgs.Length > 0 ) 
					{
						int nCount = 0;
						for( int i = 0; i < msgs.Length; i++ )
						{
							if( msgs[i].parentFolderId.Equals( "2" ) )
							{
								nCount++;
							} 
							else
							{
								msgs[i] = null;
							}
						}
						
						if( nCount == 0 )
							return false;

						int insLoc = 0;

						inboxMsgs = new Zimbra.Client.MessageSummary[ nCount ];
						for( int i = 0; i < msgs.Length; i++ )
						{
							if( msgs[i] != null )
							{
								inboxMsgs[insLoc++] = msgs[i];
							}
						}
					}
				} 
				else if( this.prevMsgs.Length <= 0 )
				{
					return false;
				}

				System.Threading.AutoResetEvent are = new AutoResetEvent(false);
				OnNewMsgs( inboxMsgs, are );
				are.WaitOne();
				return true;
			}
		}
	}


	
}