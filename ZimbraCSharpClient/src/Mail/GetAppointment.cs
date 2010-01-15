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

namespace Zimbra.Client.Mail
{
	public class GetAppointmentRequest : MailServiceRequest
	{
		private String id;

		public GetAppointmentRequest(String id)
		{
			this.id = id;
		}

		public override String Name()
		{
			return MailService.NS_PREFIX + ":" + MailService.GET_APPT_REQUEST;
		}

		public override XmlDocument ToXmlDocument()
		{
			XmlDocument doc = new XmlDocument();
			XmlElement reqElem =doc.CreateElement( MailService.GET_APPT_REQUEST, MailService.NAMESPACE_URI );
			reqElem.SetAttribute(  MailService.A_ID, id );
			doc.AppendChild( reqElem );
			return doc;
		}
	}



	public class GetAppointmentResponse : Response
	{
		public GetAppointmentResponse()
		{}

		public override String Name
		{
			get { return MailService.NS_PREFIX + ":" + MailService.GET_APPT_RESPONSE; }
		}

		public override Response NewResponse(XmlNode responseNode)
		{
			return null;
		}


	}
}
