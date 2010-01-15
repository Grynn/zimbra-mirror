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

namespace Zimbra.Client.Admin
{
	public class AdminService : IZimbraService
	{
		public static String SERVICE_PATH			= "/service/admin/soap";

		//this services namespace uri
		public static String NS_PREFIX				= "admin";
		public static String NAMESPACE_URI			= "urn:zimbraAdmin";
		
		//requests
		public static String AUTH_REQUEST			= "AuthRequest";
		public static String GET_ACCOUNT_REQUEST	= "GetAccountRequest";

		//responses
		public static String AUTH_RESPONSE			= "AuthResponse";
		public static String GET_ACCOUNT_RESPONSE	= "GetAccountResponse";

		//element names
		public static String E_NAME					= "name";
		public static String E_PASSWORD				= "password";
		public static String E_LIFETIME				= "lifetime";
		public static String E_SESSIONID			= "sessionId";
		public static String E_AUTHTOKEN			= "authToken";
		public static String E_ACCOUNT				= "account";

		//attribute names
		public static String A_NAME					= "name";
		public static String A_BY					= "by";
		public static String A_ID					= "id";
		public static String A_APPLY_COS			= "applyCos";
		public static String A_ATTR_NAME			= "n";

		//qualified names
		public static String Q_AUTHTOKEN			= NS_PREFIX + ":" + E_AUTHTOKEN;
		public static String Q_LIFETIME				= NS_PREFIX + ":" + E_LIFETIME;
		public static String Q_SESSIONID			= NS_PREFIX + ":" + E_SESSIONID;


		public static Response[] responses = { 
			new AuthResponse(),
			new GetAccountResponse()
		};

		//IZimbraService
		public String NamespacePrefix{ get{return NS_PREFIX; }}
		public String NamepsaceUri{ get{return NAMESPACE_URI; }}
		public Response[] Responses{ get{return responses; }}

	}


	public abstract class AdminServiceRequest : Request
	{
		public override String ServicePath{ get{ return AdminService.SERVICE_PATH;} }
		public override String HttpMethod{ get { return "POST"; } }
	}




}
