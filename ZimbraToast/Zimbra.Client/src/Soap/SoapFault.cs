using System;
using System.Xml;
using Zimbra.Client.Util;
using Zimbra.Client.Soap;


namespace Zimbra.Client.Soap
{

	public class SoapFault : Response 
	{
		private String code;
		private String description;

		public SoapFault()
		{
		}

		public SoapFault( String c, String d )
		{
			code = c;
			description = d;
		}

		public String Code
		{
			get{ return code; }
		}

		public String Description
		{
			get{ return description; }
		}

		public override String Name
		{
			get{ return SoapService.NS_PREFIX + ":Fault";}
		}

		public override Response NewResponse(XmlNode responseNode)
		{
			String code  = XmlUtil.GetNodeText( responseNode, 
						SoapService.NS_PREFIX	+ ":" + SoapService.E_DETAIL + 
						"/" +	ZimbraService.NS_PREFIX + ":" + SoapService.E_ERROR  + 
						"/" +	ZimbraService.NS_PREFIX + ":" + ZimbraService.E_CODE );

			String descr = XmlUtil.GetNodeText( responseNode, 
				SoapService.NS_PREFIX + ":" + SoapService.E_REASON + 
						"/" + SoapService.NS_PREFIX + ":" + SoapService.E_TEXT );
			
			return new SoapFault(code, descr);
		}


	}
}


namespace Zimbra.Client
{
	public class ZimbraException : Exception
	{
		private RequestEnvelope req;
		private ResponseEnvelope fault;

		public ZimbraException( ResponseEnvelope sf, RequestEnvelope req )
		{
			fault = sf;
			this.req = req;
		}

		public ZimbraException( String msg, ResponseEnvelope sf, RequestEnvelope req ) : base(msg)
		{
			fault = sf;
			this.req = req;
		}

		public SoapFault Fault
		{
			get{ return (SoapFault)fault.ApiResponse; }
		}

		public ResponseEnvelope Response
		{
			get{ return fault; }
		}

		public RequestEnvelope Request
		{
			get{ return req; }
		}

	}

}
