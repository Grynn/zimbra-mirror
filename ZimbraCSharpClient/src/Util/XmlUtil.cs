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
