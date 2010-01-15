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
using Zimbra.Client.Account;
using Zimbra.Client.Soap;
using Zimbra.Client.Util;

namespace Zimbra.Client
{

	public class MessageSummary
	{
		public String itemId;
		public String subject;
		public String fragment;
		public String parentFolderId;

		public String email_display;
		public String email_address;
		public String email_personal_name;
	}

	public class RefreshBlock
	{
	}

	public class Notification
	{
		//ids of all the messages created on the server
		private MessageSummary[] createdMessages = null;

		//the server needs this on subsequent requests
		private String sequenceToken = null;

		public Notification( XmlNode notifyNode )
		{
			sequenceToken = XmlUtil.AttributeValue( 
				notifyNode.Attributes, 
				ZimbraService.A_NOTIFY_SEQUENCE );

			XmlNodeList newMsgNodes = notifyNode.SelectNodes( 
				ZimbraService.NS_PREFIX + ":" + ZimbraService.E_CREATED + "/" + 
				ZimbraService.NS_PREFIX + ":" + ZimbraService.E_MSG,
				XmlUtil.NamespaceManager );

			createdMessages = new MessageSummary[ newMsgNodes.Count ];
			for( int i = 0; i < newMsgNodes.Count; i++ )
			{
				MessageSummary s = new MessageSummary();
				
				XmlNode msgNode = newMsgNodes[i];
				s.itemId = XmlUtil.AttributeValue( msgNode.Attributes, ZimbraService.A_ID );
				s.parentFolderId = XmlUtil.AttributeValue( msgNode.Attributes, ZimbraService.A_PARENT_FOLDER_ID );
			
				XmlNode emailNode = msgNode.SelectSingleNode( ZimbraService.NS_PREFIX + ":" + ZimbraService.E_EMAIL, XmlUtil.NamespaceManager );
				if( emailNode != null ) 
				{
					s.email_display = XmlUtil.AttributeValue( emailNode.Attributes, ZimbraService.A_EMAIL_DISPLAY );
					s.email_address = XmlUtil.AttributeValue( emailNode.Attributes, ZimbraService.A_EMAIL_ADDRESS );
					s.email_personal_name = XmlUtil.AttributeValue( emailNode.Attributes, ZimbraService.A_EMAIL_PERSONAL_NAME );
				}

				XmlNode subjectNode = msgNode.SelectSingleNode( ZimbraService.NS_PREFIX + ":" + ZimbraService.E_SUBJECT, XmlUtil.NamespaceManager );
				if( subjectNode != null )
				{
					s.subject = subjectNode.InnerText;
				}

				XmlNode fragmentNode = msgNode.SelectSingleNode( ZimbraService.NS_PREFIX + ":" + ZimbraService.E_FRAGMENT, XmlUtil.NamespaceManager );
				if( fragmentNode != null )
				{
					s.fragment = fragmentNode.InnerText;
				}

				createdMessages[i] = s;
			}
		}

		public MessageSummary[] CreatedMessages
		{
			get{ return createdMessages; }
		}

		public String SequenceToken
		{
			get{ return sequenceToken; }
		}


	}

	public class ResponseEnvelope
	{
		private ResponseContext context;
		private Response		apiResponse;

		public ResponseEnvelope( ResponseContext rc, Response r )
		{
			context = rc;
			apiResponse = r;
		}

		public ResponseContext Context
		{
			get{ return context; }
			set{ context = value; }
		}

		public Response ApiResponse
		{
			get{ return apiResponse; }
			set{ apiResponse = value; }
		}
	}


	public interface IResponseClassFactory
	{
		Response NewResponse( XmlNode responseDocument );
	}


	public abstract class Response : IResponseClassFactory
	{
		public abstract Response NewResponse(XmlNode responseNode);
		public abstract String Name{ get; }
	}


	public class ResponseManager
	{
		//an instance of all available response types.
		private static IZimbraService[] services = { 
			new Zimbra.Client.ZimbraService(),
			new Zimbra.Client.Soap.SoapService(),
			new Zimbra.Client.Account.AccountService(),
			new Zimbra.Client.Admin.AdminService(),
			new Zimbra.Client.Mail.MailService()
		};

		private static Hashtable classFactories;
		private static String apiResponseSelector = 
				"/" + SoapService.NS_PREFIX		+ ":" + SoapService.E_ENVELOPE + 
				"/" + SoapService.NS_PREFIX		+ ":" + SoapService.E_BODY;

		private static String contextSelector = 
				"/" + SoapService.NS_PREFIX		+ ":" + SoapService.E_ENVELOPE + 
				"/" + SoapService.NS_PREFIX		+ ":" + SoapService.E_HEADER + 
				"/" + ZimbraService.NS_PREFIX	+ ":" + ZimbraService.E_CONTEXT;
		
		static ResponseManager()
		{
			classFactories = new Hashtable();

			for( int svci = 0; svci < services.Length; svci++ )
			{
				//setup the response class factories
				for( int ri = 0; ri < services[svci].Responses.Length; ri++ )
				{
					classFactories.Add( services[svci].Responses[ri].Name, services[svci].Responses[ri] );
				}

				//set the xml namespace manager
				XmlUtil.NamespaceManager.AddNamespace( services[svci].NamespacePrefix, services[svci].NamepsaceUri );

			}
		}


		public static Response NewResponse( XmlDocument responseDocument )
		{
			XmlNamespaceManager nsmgr = XmlUtil.NamespaceManager;
			XmlNode body = responseDocument.SelectSingleNode( apiResponseSelector, nsmgr );
			
			XmlNode firstChild = body.FirstChild;
			if( firstChild == null )
				throw new Exception( "invalid server response" );

			String responseLocal = firstChild.LocalName;
			String responseUriPrefix = nsmgr.LookupPrefix( nsmgr.NameTable.Get(body.FirstChild.NamespaceURI) );

			String responseName = responseUriPrefix + ":" + responseLocal;
			if( !classFactories.ContainsKey(responseName) )
			{
				throw new Exception( "Unknown response: " + responseName );
			}

			IResponseClassFactory rcf = (IResponseClassFactory)classFactories[responseName];
			return rcf.NewResponse(firstChild);
		}

		public static ResponseContext NewResponseContext( XmlDocument responseDocument )
		{
			XmlNamespaceManager nsmgr = XmlUtil.NamespaceManager;
			XmlNode context = responseDocument.SelectSingleNode( contextSelector, nsmgr );

			return new ResponseContext( context );
		}
	}

}
