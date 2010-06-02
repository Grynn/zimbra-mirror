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

namespace Zimbra.Client
{
	public class ResponseContext
	{
		private String			sessionId;
		private String			changeToken;
		private RefreshBlock	refreshBlock;
		private Notification	notification;

		public ResponseContext( XmlNode contextNode )
		{
			if( contextNode == null )
				return;
			sessionId = XmlUtil.GetNodeText( contextNode, ZimbraService.NS_PREFIX + ":" + ZimbraService.E_SESSION );
			changeToken = XmlUtil.GetAttributeValue( contextNode, ZimbraService.E_CHANGE, ZimbraService.A_TOKEN );

			XmlNode notifyNode = contextNode.SelectSingleNode( ZimbraService.NS_PREFIX + ":" + ZimbraService.E_NOTIFY, XmlUtil.NamespaceManager );
			if( notifyNode != null ) 
			{
				notification = new Notification(notifyNode);
			}
		}

		public ResponseContext( String sessionId, String changeToken, RefreshBlock rb, Notification n )
		{
			this.sessionId = sessionId;
			this.changeToken = changeToken;
			this.refreshBlock = rb;
			this.notification = n;
		}

		public String SessionId
		{
			get{ return sessionId; }
			set{ sessionId = value; }
		}

		public String ChangeToken
		{
			get{ return changeToken; }
			set{ changeToken = value; }
		}

		public RefreshBlock Refresh
		{
			get{ return refreshBlock; }
			set{ refreshBlock = value; }
		}

		public Notification Notifications
		{
			get{ return notification; }
			set{ notification = value; }
		}

	}

}

