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
			sessionId = XmlUtil.GetNodeText( contextNode, ZimbraService.NS_PREFIX + ":" + ZimbraService.E_SESSIONID );
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

