/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite CSharp Client
 * Copyright (C) 2006, 2007, 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
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

namespace Zimbra.Client.Admin
{

	public class GetAccountRequest : AdminServiceRequest
	{
		public enum AccountBy{ ByName, ById };

		private String account;
		private AccountBy nameOrId;
		private bool applyCos;

		public GetAccountRequest( String account, AccountBy nameOrId, bool applyCos)
		{
			this.account = account;
			this.nameOrId = nameOrId;
			this.applyCos = applyCos;
		}

		public override String Name()
		{
			return AdminService.NS_PREFIX + ":" + AdminService.GET_ACCOUNT_REQUEST;
		}


		public override System.Xml.XmlDocument ToXmlDocument()
		{
			XmlDocument doc = new XmlDocument();

			//create the AuthRequest node
			XmlElement requestNode = doc.CreateElement( AdminService.GET_ACCOUNT_REQUEST, AdminService.NAMESPACE_URI);

			if( !applyCos ) //default is 1
				requestNode.SetAttribute( AdminService.A_APPLY_COS, "0" );

			//create & config the account node
			XmlElement accountNode = doc.CreateElement( AdminService.E_ACCOUNT, AdminService.NAMESPACE_URI );
			
			if( nameOrId == AccountBy.ByName )
				accountNode.SetAttribute( AdminService.A_BY, AdminService.A_NAME );
			else if( nameOrId == AccountBy.ById )
				accountNode.SetAttribute( AdminService.A_BY, AdminService.A_ID );

			accountNode.InnerText = account;


			//add em together...
			requestNode.AppendChild( accountNode );
			doc.AppendChild( requestNode );

			return doc;
		}
	}

	public class Account
	{
		private String name;
		private String id;
		private Hashtable attrs;
		
		public Account( String name, String id, Hashtable attrs )
		{
			this.name = name;
			this.id = id;
			this.attrs = attrs;
		}

		public String Name
		{
			get{ return name; }
			set{ name = value; }
		}

		public String Id
		{
			get{ return id; }
			set{ id = value; }
		}

		public Hashtable Attributes
		{
			get{ return attrs; }
			set{ attrs = value; }
		}
	}


	public class GetAccountResponse : Response
	{
		private Account acct;
		public GetAccountResponse( Account acct)
		{
			this.acct = acct;
		}

		public GetAccountResponse(){}

		public override String Name
		{
			get{return AdminService.NS_PREFIX + ":" + AdminService.GET_ACCOUNT_RESPONSE;}
		}

		public Account Acct
		{
			get{ return acct; }
		}

		public override Response NewResponse(XmlNode responseNode)
		{
			XmlNode accountNode = responseNode.SelectSingleNode( AdminService.NS_PREFIX + ":" + AdminService.E_ACCOUNT, XmlUtil.NamespaceManager );

			String name = XmlUtil.AttributeValue( accountNode.Attributes, AdminService.A_NAME );
			String id = XmlUtil.AttributeValue( accountNode.Attributes, AdminService.A_ID );

			Hashtable h = new Hashtable();
			for( int i = 0; i < accountNode.ChildNodes.Count; i++ )
			{
				XmlNode child = accountNode.ChildNodes[i];
				String attrName = XmlUtil.AttributeValue( child.Attributes, AdminService.A_ATTR_NAME );
				String attrValue = child.InnerText;

				if( h.Contains( attrName ) )
				{
					Object o = h[attrName];
					if( o is String )
					{
                        ArrayList al = new ArrayList();
						al.Add( (String)o );
						al.Add( attrValue );
						h.Remove(attrName);
						h.Add( attrName, al );
					}
					else //its a string collection
					{
						ArrayList al = (ArrayList)o;
						al.Add(attrValue);
					}
				}
				else
				{
					h.Add( attrName, attrValue );
				}
			}

			return new GetAccountResponse( new Account( name, id, h ) );
		}

	}
}
