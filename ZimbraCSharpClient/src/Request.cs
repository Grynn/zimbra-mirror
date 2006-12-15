/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * 
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra CSharp Client
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */
using System;
using System.Xml;

using Zimbra.Client.Soap;

namespace Zimbra.Client
{

	public class RequestEnvelope
	{
		private RequestContext		context;
		private Request				apiRequest;

		public RequestEnvelope( RequestContext rc, Request ar )
		{
			context = rc;
			apiRequest = ar;
		}

		public RequestContext Context
		{
			get{ return context; }
			set{ context = value; }
		}

		public Request ApiRequest
		{
			get{ return apiRequest; }
			set{ apiRequest = value; }
		}

		public XmlDocument ToXmlDocument()
		{
			XmlDocument contextDoc = context.ToXmlDocument();
			XmlDocument apiDoc = apiRequest.ToXmlDocument();

			//wrap it in soap....
			XmlDocument soapDoc = new XmlDocument();
			XmlElement envelope = soapDoc.CreateElement( SoapService.E_ENVELOPE, SoapService.NAMESPACE_URI );
			XmlElement header   = soapDoc.CreateElement( SoapService.E_HEADER, SoapService.NAMESPACE_URI );
			XmlElement body     = soapDoc.CreateElement( SoapService.E_BODY, SoapService.NAMESPACE_URI );

			if( contextDoc.ChildNodes.Count > 0 )
				header.AppendChild( soapDoc.ImportNode( contextDoc.FirstChild, true ) );
			body.AppendChild( soapDoc.ImportNode( apiDoc.FirstChild, true ) );

			if( header.ChildNodes.Count > 0 )
				envelope.AppendChild( header );
			envelope.AppendChild( body );
			soapDoc.AppendChild( envelope );
			return soapDoc;
		}
	}



	public enum AccountFormat { ByName, ById };
	public enum RaceConditionType { Modify, New };


	public abstract class Request
	{
		public abstract String Name();
		public abstract XmlDocument ToXmlDocument();
		public abstract String ServicePath{ get; }
		public abstract String HttpMethod{ get; }
	}
}
