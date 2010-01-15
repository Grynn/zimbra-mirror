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
	public class MsgActionRequest : MailServiceRequest
	{
		private String op = null;
		private String id = null;
		private String targetFolder = null;

		public MsgActionRequest(String id, String op)
		{
			this.id = id;
			this.op = op;
		}

		public MsgActionRequest( String id, String op, String targetFolder )
		{
			this.id = id;
			this.op = op;
			this.targetFolder = targetFolder;
		}

		public override String Name()
		{
			return MailService.NS_PREFIX + ":" + MailService.MSG_ACTION_REQUEST;
		}

		public override XmlDocument ToXmlDocument()
		{
			XmlDocument doc = new XmlDocument();
			XmlElement reqElem =doc.CreateElement( MailService.MSG_ACTION_REQUEST, MailService.NAMESPACE_URI );
			XmlElement actionElem = doc.CreateElement( MailService.E_ACTION, MailService.NAMESPACE_URI );
			actionElem.SetAttribute( MailService.A_ID, id );
			actionElem.SetAttribute( MailService.A_OP, op );
			if( this.targetFolder != null ) 
			{
				actionElem.SetAttribute( MailService.A_PARENT_FOLDER_ID, targetFolder );
			}
			reqElem.AppendChild( actionElem );
			doc.AppendChild( reqElem );
			return doc;
		}
	}



	public class MsgActionResponse : Response
	{
		public MsgActionResponse()
		{}

		public override String Name
		{
			get { return MailService.NS_PREFIX + ":" + MailService.MSG_ACTION_RESPONSE; }
		}

		public override Response NewResponse(XmlNode responseNode)
		{
			return new MsgActionResponse();
		}


	}
}
