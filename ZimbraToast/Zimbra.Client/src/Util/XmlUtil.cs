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
