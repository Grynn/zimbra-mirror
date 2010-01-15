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


namespace Zimbra.Client.Util
{
	public class XmlUtil
	{
		public static XmlNamespaceManager NamespaceManager;

		static XmlUtil()
		{
			NamespaceManager = new XmlNamespaceManager( new NameTable() );
		}


		public static String GetNodeText( XmlDocument d, String xPath )
		{
			XmlNode n = d.SelectSingleNode( xPath, NamespaceManager );
			if( n != null ) 
				return n.InnerText;
			return null;
		}

		public static String GetNodeText( XmlNode d, String xPath )
		{
			XmlNode n = d.SelectSingleNode( xPath, NamespaceManager );
			if( n != null ) 
				return n.InnerText;
			return null;
		}

		public static String AttributeValue( XmlAttributeCollection attrs, String name )
		{
			XmlNode n = attrs[name];
			if( n != null )
				return n.Value;
			return null;
		}

		public static String GetAttributeValue( XmlNode n, String nodeSelector, String attrName )
		{
			XmlNode s = n.SelectSingleNode( nodeSelector, NamespaceManager );
			if( s == null )
				return null;
			return AttributeValue( s.Attributes, attrName );
		}

	}
}
