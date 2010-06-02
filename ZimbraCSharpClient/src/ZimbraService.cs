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

namespace Zimbra.Client
{
	public interface IZimbraService
	{
		String NamespacePrefix{ get; }
		String NamepsaceUri{ get; }
		Response[] Responses{ get; }
	}


	public class ZimbraService : IZimbraService
	{
		//this services namespace uri
		public static String NS_PREFIX				= "zimbra";
		public static String NAMESPACE_URI			= "urn:zimbra";

//		public static String E_SESSIONID			= "sessionId";
		public static String E_CONTEXT				= "context";
		public static String E_AUTHTOKEN			= "authToken";
		public static String E_NONOTIFY				= "nonotify";
		public static String E_NOSESSION			= "nosession";
		public static String E_SESSION				= "session";
		public static String E_ACCOUNT				= "account";
		public static String E_TARGET_SERVER		= "targetServer";
		public static String E_CHANGE				= "change";
		public static String E_CODE					= "Code";
		public static String E_CREATED				= "created";
		public static String E_MSG					= "m";
		public static String E_NOTIFY				= "notify";
		public static String E_EMAIL				= "e";
		public static String E_SUBJECT				= "su";
		public static String E_FRAGMENT				= "fr";
		
		public static String A_TOKEN				= "token";
		public static String A_TYPE					= "type";
		public static String A_BY					= "by";
		public static String A_NOTIFY_SEQUENCE		= "seq";
		public static String A_ID					= "id";
		public static String A_EMAIL_DISPLAY		= "d";
		public static String A_EMAIL_ADDRESS		= "a";
		public static String A_EMAIL_PERSONAL_NAME	= "p";
		public static String A_PARENT_FOLDER_ID		= "l";

		public static Response[] responses = {  };
		public String NamespacePrefix{ get{ return NS_PREFIX; }}
		public String NamepsaceUri{ get{ return NAMESPACE_URI; }}
		public Response[] Responses{get{ return responses;}}


	}

}


