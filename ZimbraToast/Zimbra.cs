using System;
using System.Xml;


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

		public void DeleteItem( String itemId )
		{
			Zimbra.Client.Mail.MsgActionRequest mar = new Zimbra.Client.Mail.MsgActionRequest( itemId, "delete" );
			Zimbra.Client.ResponseEnvelope res = SendRequest( mar );
		}

		public void MoveItem( String itemId, String targetFolderId )
		{
			Zimbra.Client.Mail.MsgActionRequest mar = new Zimbra.Client.Mail.MsgActionRequest( itemId, "move", targetFolderId );
			Zimbra.Client.ResponseEnvelope res = SendRequest( mar );
		}


		public void FlagItem( String itemId )
		{
			Zimbra.Client.Mail.MsgActionRequest mar = new Zimbra.Client.Mail.MsgActionRequest( itemId, "flag" );
			Zimbra.Client.ResponseEnvelope res = SendRequest( mar );
		}


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

	}




	//application entry point
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
			
			//otherwise, we are running the tray app
			System.Windows.Forms.Application.EnableVisualStyles();
			System.Windows.Forms.Application.DoEvents();
			System.Windows.Forms.Application.Run( new Config() );
		}

	}

}
