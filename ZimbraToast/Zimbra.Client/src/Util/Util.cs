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
