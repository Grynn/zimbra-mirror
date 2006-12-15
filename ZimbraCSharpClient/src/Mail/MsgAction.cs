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
