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
using System.Collections;
using Zimbra.Client.Util;

namespace Zimbra.Client.Mail
{
	public class Tag
	{
		private String id;
		private String name;
		private String color;
		private String unreadCount;

		public String Id
		{
			get{ return id; }
			set{ id = value; }
		}

		public String Name
		{
			get{ return name; }
			set{ name = value; }
		}

		public String Color
		{
			get{ return color; }
			set{ color = value; }
		}

		public String UnreadCount
		{
			get{ return unreadCount; }
			set{ unreadCount = value; }
		}
	}


	public class GetTagRequest : MailServiceRequest
	{
		public override String Name()
		{
			return MailService.NS_PREFIX + ":" + MailService.GET_TAG_REQUEST;
		}

		public override System.Xml.XmlDocument ToXmlDocument()
		{
			XmlDocument d = new XmlDocument();
			XmlNode requestE = d.CreateElement( MailService.GET_TAG_REQUEST, MailService.NAMESPACE_URI);
			d.AppendChild( requestE );
			return d;
		}
	}


	public class GetTagResponse : Response
	{
		private Tag[] tags;

		public GetTagResponse(){}

		public GetTagResponse( Tag[] tags )
		{
			this.tags = tags;
		}

		public Tag[] Tags
		{
			get{ return tags; }
		}

		public override String Name
		{
			get{ return MailService.NS_PREFIX + ":" + MailService.GET_TAG_RESPONSE;}
		}


		public override Response NewResponse(XmlNode responseNode)
		{
			XmlNode n = responseNode;

			ArrayList tags = new ArrayList();
			for( int i = 0; i < n.ChildNodes.Count; i++ )
			{
				XmlNode child = n.ChildNodes.Item(i);
				if( !child.Name.ToLower().Equals(MailService.E_TAG) )
					continue;

				Tag t = new Tag();
				t.Id = XmlUtil.AttributeValue( child.Attributes, MailService.A_ID );
				t.Name = XmlUtil.AttributeValue( child.Attributes, MailService.A_NAME );
				t.Color = XmlUtil.AttributeValue( child.Attributes, MailService.A_COLOR );
				t.UnreadCount = XmlUtil.AttributeValue( child.Attributes, MailService.A_UNREAD_COUNT );

				tags.Add( t );
			}

			if( tags.Count == 0 )
				return new GetTagResponse(null);

			return new GetTagResponse( (Tag[])tags.ToArray(tags[0].GetType()) );
		}

	}
}
