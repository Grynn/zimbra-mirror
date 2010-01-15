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
using System.Net;
using System.Security.Cryptography.X509Certificates;

namespace Zimbra.Client.Util
{

	class AcceptAllCertsPolicy : ICertificatePolicy
	{
		public bool CheckValidationResult(ServicePoint p, X509Certificate c, WebRequest w, int certProb)
		{
			return true;
		}
	}


	public class ConnectionUtil
	{
		public static void AllowInvalidCerts()
		{
			ServicePointManager.CertificatePolicy = new AcceptAllCertsPolicy();
		}
	}

	public class DateUtil
	{
		public static Int64 DateTimeToGmtMillis( DateTime localDateTime )
		{
			DateTime t = new DateTime( 1970, 1, 1 );
			DateTime utc = localDateTime.ToUniversalTime();

			//100 nanoseconds
			Int64 deltaTicks = utc.Ticks - t.Ticks;
			Int64 deltaMillis = (Int64)(deltaTicks / 10000);

			return deltaMillis;
		}

		public static DateTime GmtSecondsToLocalTime( Int64 s )
		{
			DateTime t = new DateTime( 1970, 1, 1 );
			
			Int64 ticks = s * 10000;
			return new DateTime( t.Ticks + ticks ).ToLocalTime();
		}
	}
}
