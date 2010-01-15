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
	public class GetFolderRequest : MailServiceRequest
	{
		public String baseFolderId;

		public GetFolderRequest()
		{
			baseFolderId = null;
		}

		public GetFolderRequest( String baseFolderId )
		{
			this.baseFolderId = baseFolderId;
		}

		public override String Name()
		{
			return MailService.NS_PREFIX + ":" + MailService.GET_FOLDER_REQUEST;
		}

		public override System.Xml.XmlDocument ToXmlDocument()
		{
			XmlDocument doc = new XmlDocument();

			XmlElement requestE = doc.CreateElement( MailService.GET_FOLDER_REQUEST, MailService.NAMESPACE_URI );
			if( baseFolderId != null )
			{
				XmlElement folderE = doc.CreateElement( MailService.E_FOLDER, MailService.NAMESPACE_URI );
				folderE.SetAttribute( MailService.A_PARENT_FOLDER_ID, baseFolderId );
				requestE.AppendChild( folderE );
			}
			doc.AppendChild( requestE );
			return doc;
		}
	}

	public class Folder
	{
		private String id;
		private String name;
		private String parentFolderId;
		private String color;
		private String unreadCount;
		private String numMessages;
		private String view;

		private ArrayList children = new ArrayList();

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

		public String ParentFolderId
		{
			get{ return parentFolderId; }
			set{ parentFolderId = value; }
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

		public String NumMessages
		{
			get{ return numMessages; }
			set{ numMessages = value; }
		}

		public String View
		{
			get{ return view; }
			set{ view = value; }
		}

		public ArrayList Children
		{
			get{ return children; }
		}

		public void AddChild( Folder f )
		{
			children.Add( f );
		}
	}

	class SearchFolder : Folder
	{
		String query;
		String types;
		String sortBy;
		
		public String Query
		{
			get{ return query; }
			set{ query = value; }
		}
		public String Types
		{
			get{ return types; }
			set{ types = value; }
		}
		public String SortBy
		{
			get{ return sortBy; }
			set{ sortBy = value; }
		}
	}


	public class GetFolderResponse : Response
	{
		Folder f;
		public GetFolderResponse(Folder f)
		{
			this.f = f;
		}

		public GetFolderResponse(){}

		public override String Name
		{
			get{ return MailService.NS_PREFIX + ":" + MailService.GET_FOLDER_RESPONSE;}
		}


		private Folder NodeToFolder( XmlNode parent )
		{
			XmlAttributeCollection attrs = parent.Attributes;

			Folder f;
			if( parent.Name.ToLower().Equals("search") )
			{
				SearchFolder sf = new SearchFolder();
				sf.Query = XmlUtil.AttributeValue( attrs, MailService.A_QUERY );
				sf.Types = XmlUtil.AttributeValue( attrs, MailService.A_TYPES );
				sf.SortBy = XmlUtil.AttributeValue( attrs, MailService.A_SORT_BY );
				f = sf;
			}
			else
			{
				f = new Folder();
			}

			f.Id = XmlUtil.AttributeValue( attrs, MailService.A_ID );
			f.Name = XmlUtil.AttributeValue( attrs, MailService.A_NAME );
			f.ParentFolderId = XmlUtil.AttributeValue( attrs, MailService.A_PARENT_FOLDER_ID );
			f.Color = XmlUtil.AttributeValue( attrs, MailService.A_COLOR );
			f.UnreadCount = XmlUtil.AttributeValue( attrs, MailService.A_UNREAD_COUNT );
			f.NumMessages = XmlUtil.AttributeValue( attrs, MailService.A_ITEM_COUNT );
			f.View = XmlUtil.AttributeValue( attrs, MailService.A_VIEW );

			for( int i = 0; i < parent.ChildNodes.Count; i++ )
			{
				XmlNode child = parent.ChildNodes.Item(i);
				f.Children.Add( NodeToFolder( child ) );
			}

			return f;
		}


		public override Response NewResponse(XmlNode responseNode)
		{
			//grab the first folder element under a GetFolderResponse
			XmlNode root = responseNode.SelectSingleNode( MailService.NS_PREFIX + ":" + MailService.E_FOLDER, XmlUtil.NamespaceManager );
			return new GetFolderResponse( NodeToFolder( root ) );
		}


	}

}
