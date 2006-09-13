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
