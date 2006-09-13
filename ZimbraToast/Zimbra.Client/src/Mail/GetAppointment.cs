using System;
using System.Xml;

namespace Zimbra.Client.Mail
{
	public class GetAppointmentRequest : MailServiceRequest
	{
		private String id;

		public GetAppointmentRequest(String id)
		{
			this.id = id;
		}

		public override String Name()
		{
			return MailService.NS_PREFIX + ":" + MailService.GET_APPT_REQUEST;
		}

		public override XmlDocument ToXmlDocument()
		{
			XmlDocument doc = new XmlDocument();
			XmlElement reqElem =doc.CreateElement( MailService.GET_APPT_REQUEST, MailService.NAMESPACE_URI );
			reqElem.SetAttribute(  MailService.A_ID, id );
			doc.AppendChild( reqElem );
			return doc;
		}
	}



	public class GetAppointmentResponse : Response
	{
		public GetAppointmentResponse()
		{}

		public override String Name
		{
			get { return MailService.NS_PREFIX + ":" + MailService.GET_APPT_RESPONSE; }
		}

		public override Response NewResponse(XmlNode responseNode)
		{
			return null;
		}


	}
}
