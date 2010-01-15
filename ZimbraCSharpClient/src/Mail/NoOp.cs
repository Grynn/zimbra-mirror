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
	public class NoOpRequest : MailServiceRequest
	{
		public NoOpRequest()
		{
		}

		public override String Name()
		{
			return MailService.NS_PREFIX + ":" + MailService.NO_OP_REQUEST;
		}

		public override System.Xml.XmlDocument ToXmlDocument()
		{
			XmlDocument doc = new XmlDocument();
			XmlElement reqElem =doc.CreateElement( MailService.NO_OP_REQUEST, MailService.NAMESPACE_URI );
			doc.AppendChild( reqElem );
			return doc;
		}
	}

	public class NoOpResponse : Response
	{
		public override String Name
		{
			get
			{
				return MailService.NS_PREFIX + ":" + MailService.NO_OP_RESPONSE;
			}
		}

		public override Response NewResponse(XmlNode responseNode)
		{
			return new NoOpResponse();
		}


	}

}
