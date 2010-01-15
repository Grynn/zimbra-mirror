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

namespace Zimbra.Client.Soap
{
	public class SoapService : IZimbraService
	{
		//this services namespace uri
		public static String NS_PREFIX		= "soap";
		public static String NAMESPACE_URI	= "http://www.w3.org/2003/05/soap-envelope";

		public static String E_ENVELOPE		= "Envelope";
		public static String E_HEADER		= "Header";
		public static String E_BODY			= "Body";
		public static String E_DETAIL		= "Detail";
		public static String E_REASON		= "Reason";
		public static String E_ERROR		= "Error";
		public static String E_TEXT			= "Text";

		public static Response[] responses = { new SoapFault() };
		public String NamespacePrefix{ get{ return NS_PREFIX; }}
		public String NamepsaceUri{ get{ return NAMESPACE_URI; }}
		public Response[] Responses{get{ return responses;}}


	}
}
