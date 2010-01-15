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
using System.Xml;
using System.Net;
using System.Security.Cryptography.X509Certificates;

namespace Zimbra.Toast
{

	/// <summary>
	/// Establish a communication channel with zimbra
	/// maintian state across requests
	/// </summary>
	public class ZimbraSession
	{
		private Zimbra.Client.Account.AuthRequest	authRequest		= null;
		private Zimbra.Client.Dispatcher			dispatcher		= null;
		private Zimbra.Client.ResponseContext		prevRespContext = null;
		private Zimbra.Client.Account.AuthResponse	authResponse	= null;

		/// <summary>
		/// Create th session
		/// </summary>
		/// <param name="username">user</param>
		/// <param name="password">password</param>
		/// <param name="server">zimbra server</param>
		/// <param name="nPort">port</param>
		/// <param name="bSSL">use secure connection</param>
		public ZimbraSession( String username, String password, String server, UInt16 nPort, bool bSSL ) 
		{
			authRequest = new Zimbra.Client.Account.AuthRequest( username, password );
			dispatcher = new Zimbra.Client.Dispatcher( server, nPort, bSSL, true );
		}


		/// <summary>
		/// Get a list of the new msgs in the mailbox since the last request
		/// </summary>
		public Zimbra.Client.MessageSummary[] NewMsgs 
		{
			get
			{
				Zimbra.Client.Mail.NoOpRequest noop = new Zimbra.Client.Mail.NoOpRequest();
				Zimbra.Client.ResponseEnvelope res = SendRequest( noop );
				if( res.Context.Notifications != null ) 
				{
					return res.Context.Notifications.CreatedMessages;
				}
				return new Zimbra.Client.MessageSummary[0];
			}
		}

		/// <summary>
		/// Delete the item specified by id
		/// </summary>
		/// <param name="itemId">The item-id of the item to delete</param>
		public void DeleteItem( String itemId )
		{
			Zimbra.Client.Mail.MsgActionRequest mar = new Zimbra.Client.Mail.MsgActionRequest( itemId, "delete" );
			Zimbra.Client.ResponseEnvelope res = SendRequest( mar );
		}

		/// <summary>
		/// Move the item to the specified folder
		/// </summary>
		/// <param name="itemId">The item-id of the item to move</param>
		/// <param name="targetFolderId">The folder id of the target folder</param>
		public void MoveItem( String itemId, String targetFolderId )
		{
			Zimbra.Client.Mail.MsgActionRequest mar = new Zimbra.Client.Mail.MsgActionRequest( itemId, "move", targetFolderId );
			Zimbra.Client.ResponseEnvelope res = SendRequest( mar );
		}


		/// <summary>
		/// Flag the item specified by id
		/// </summary>
		/// <param name="itemId">Item id of the item to flag</param>
		public void FlagItem( String itemId )
		{
			Zimbra.Client.Mail.MsgActionRequest mar = new Zimbra.Client.Mail.MsgActionRequest( itemId, "flag" );
			Zimbra.Client.ResponseEnvelope res = SendRequest( mar );
		}


		/// <summary>
		/// Send the request, auth if necessary
		/// </summary>
		/// <param name="apiRequest">The request to send</param>
		/// <returns>The servers response</returns>
		private Zimbra.Client.ResponseEnvelope SendRequest( Zimbra.Client.Request apiRequest )
		{
			if( authResponse == null || authResponse.AuthToken == null ) 
			{
				Auth();
			}
				
			Zimbra.Client.RequestContext rc = new Zimbra.Client.RequestContext();
			rc.Update( prevRespContext, authResponse );
				
			Zimbra.Client.RequestEnvelope req = new Zimbra.Client.RequestEnvelope( rc, apiRequest );
			Zimbra.Client.ResponseEnvelope res = dispatcher.SendRequest( req );
			prevRespContext = res.Context;
			return res;
		}


		/// <summary>
		/// Invalidate the auth token cached in this session object
		/// Forces an auth before the next request is issued to the server
		/// </summary>
		public void InvalidateAuthToken()
		{
			authResponse = null;
			prevRespContext = null;
		}


		/// <summary>
		/// Authenticate against the server
		/// </summary>
		private void Auth()
		{
			Zimbra.Client.RequestContext rc = new Zimbra.Client.RequestContext();
			Zimbra.Client.RequestEnvelope req = new Zimbra.Client.RequestEnvelope( rc, authRequest );
			Zimbra.Client.ResponseEnvelope res = dispatcher.SendRequest( req );
			prevRespContext = res.Context;
			authResponse = (Zimbra.Client.Account.AuthResponse)res.ApiResponse;
		}

		/// <summary>
		/// Return the current sessions authToken
		/// </summary>
		public String AuthToken 
		{
			get
			{
				if( authResponse != null ) 
				{
					return authResponse.AuthToken;
				}
				return null;
			}
		}

	}



	/// <summary>
	/// Used to indicate that an invalid ssl certificate is ok 
	/// </summary>
	public class InvalidCertOKPolicy : ICertificatePolicy 
	{
		/// <summary>
		/// The CheckValidationResult method implements the application 
		/// certificate validation policy. The method can examine the 
		/// srvPoint, certificate, request, and certificateProblem parameters 
		/// to determine whether the certificate should be honored.
		/// 
		/// The certificateProblem parameter is a Security Support Provider 
		/// Interface (SSPI) status code. For more information, see the SSPI 
		/// documentation on MSDN.
		/// </summary>
		/// <param name="srvPoint">The ServicePoint that will use the certificate</param>
		/// <param name="certificate">The certificate to validate</param>
		/// <param name="request">the request that received the certificate</param>
		/// <param name="certificateProblem">the problem encountered when using the certificate</param>
		/// <returns></returns>
		public bool CheckValidationResult(
			ServicePoint		srvPoint, 
			X509Certificate		certificate,
			WebRequest			request,
			int					certificateProblem) 
		{
			return true;

		} // end CheckValidationResult
	} // class MyPolicy



	/// <summary>
	/// Application entry-point
	/// </summary>
	public class Application
	{

		/// <summary>
		/// invoked to handle a mailto url
		/// </summary>
		/// <param name="mailToUrl"></param>
		private static void HandleMailTo( String mailToUrl )
		{
			ToastConfig config = new ToastConfig();
			System.Diagnostics.Process.Start( config.GetMailtoUri( mailToUrl ) );
		}




		/// <summary>
		/// The main entry point for the application.
		/// </summary>
		[STAThread]
		static void Main(String[] args) 
		{
			//if we are handling a mailto url, do it and return
			if( args.Length == 1 )
			{
				HandleMailTo( args[0] );
				return;
			}
			
			//set the visual styles
			//some runtimes won't support these calls, so 
			//if they fail, continue.
			try 
			{
				//otherwise, we are running the tray app
				System.Windows.Forms.Application.EnableVisualStyles();
				System.Windows.Forms.Application.DoEvents();
			}
			catch(Exception)
			{
			}

			System.Windows.Forms.Application.Run( new Config() );
		}

	}

}
