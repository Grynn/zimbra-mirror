/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite CSharp Client
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
using System.Xml;
using Zimbra.Client.Util;
using Zimbra.Client.Soap;


namespace Zimbra.Client.Soap
{

	public class SoapFault : Response 
	{
		private String code;
		private String description;

		public SoapFault()
		{
		}

		public SoapFault( String c, String d )
		{
			code = c;
			description = d;
		}

		public String Code
		{
			get{ return code; }
		}

		public String Description
		{
			get{ return description; }
		}

		public override String Name
		{
			get{ return SoapService.NS_PREFIX + ":Fault";}
		}

		public override Response NewResponse(XmlNode responseNode)
		{
			String code  = XmlUtil.GetNodeText( responseNode, 
						SoapService.NS_PREFIX	+ ":" + SoapService.E_DETAIL + 
						"/" +	ZimbraService.NS_PREFIX + ":" + SoapService.E_ERROR  + 
						"/" +	ZimbraService.NS_PREFIX + ":" + ZimbraService.E_CODE );

			String descr = XmlUtil.GetNodeText( responseNode, 
				SoapService.NS_PREFIX + ":" + SoapService.E_REASON + 
						"/" + SoapService.NS_PREFIX + ":" + SoapService.E_TEXT );
			
			return new SoapFault(code, descr);
		}


	}
}


namespace Zimbra.Client
{
	public class ZimbraException : Exception
	{
		private RequestEnvelope req;
		private ResponseEnvelope fault;

		public ZimbraException( ResponseEnvelope sf, RequestEnvelope req )
		{
			fault = sf;
			this.req = req;
		}

		public ZimbraException( String msg, ResponseEnvelope sf, RequestEnvelope req ) : base(msg)
		{
			fault = sf;
			this.req = req;
		}

		public SoapFault Fault
		{
			get{ return (SoapFault)fault.ApiResponse; }
		}

		public ResponseEnvelope Response
		{
			get{ return fault; }
		}

		public RequestEnvelope Request
		{
			get{ return req; }
		}

	}

}
