/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite CSharp Client
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
using System.IO;
using System.Xml;
using System.Net;

using Zimbra.Client.Soap;

namespace Zimbra.Client
{
	public class Dispatcher
	{
		private String server;
		private int port;
		private bool useSecure;
		private bool faultToException;
		private TextWriter debugStream;

		public Dispatcher( String server, int port, bool useSecure, bool faultToException )
		{
			this.server = server;
			this.port = port;
			this.useSecure = useSecure;
			this.faultToException = faultToException;
		}

		public String Server
		{
			get{ return server; }
			set{ server = value; }
		}

		public int Port
		{
			get{ return port; }
			set{ port = value; }
		}

		public bool UseSecure
		{
			get{ return useSecure; }
			set{ useSecure = value; }
		}

		public bool FaultToException
		{
			get{ return faultToException; }
			set{ faultToException = value; }
		}


		public ResponseEnvelope SendRequest( RequestEnvelope re )
		{
			XmlDocument requestDoc = re.ToXmlDocument();
			String serviceUri = "http";
			if( useSecure )
				serviceUri += "s";
			serviceUri += "://" + server + ":" + port;

			serviceUri += re.ApiRequest.ServicePath;

			if( debugStream != null )
			{
				debugStream.WriteLine( "Request:" );
				debugStream.WriteLine( requestDoc.OuterXml );
				debugStream.WriteLine( "" );
			}

			HttpWebRequest wr = (HttpWebRequest)WebRequest.Create(serviceUri);
            wr.AllowWriteStreamBuffering = true;
			wr.Method = re.ApiRequest.HttpMethod;

			Stream stream = wr.GetRequestStream();
			requestDoc.Save( stream );
			stream.Close();

			HttpWebResponse resp;
			try
			{
				resp = (HttpWebResponse)wr.GetResponse();
			}
			catch( WebException wex )
			{
                if (wex.Response == null)
                    throw wex;
				resp = (HttpWebResponse)wex.Response;
			}
			stream = resp.GetResponseStream();

			XmlDocument responseDoc = new XmlDocument();
			responseDoc.Load( stream );
			stream.Close();
			
			if( debugStream != null )
			{
				debugStream.WriteLine( "Response:" );
				debugStream.WriteLine( responseDoc.OuterXml );
				debugStream.WriteLine( "\n" );
			}
			
			ResponseEnvelope respEnv = new ResponseEnvelope(
				ResponseManager.NewResponseContext(responseDoc),
				ResponseManager.NewResponse(responseDoc) );

			if( faultToException && respEnv.ApiResponse is SoapFault )
				throw new ZimbraException( respEnv, re );

			return respEnv;
		}

		//replace this with some real logging
		public void SetDebugStream( TextWriter dbgStream )
		{
			this.debugStream = dbgStream;
		}
	}
}
