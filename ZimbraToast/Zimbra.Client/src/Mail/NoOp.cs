using System;
using System.Xml;

namespace Zimbra.Client.Mail
{
	public class NoOpRequest : MailServiceRequest
	{
		public NoOpRequest()
		{
		}

		public override String Name()
		{
			return MailService.NS_PREFIX + ":" + MailService.NO_OP_REQUEST;
		}

		public override System.Xml.XmlDocument ToXmlDocument()
		{
			XmlDocument doc = new XmlDocument();
			XmlElement reqElem =doc.CreateElement( MailService.NO_OP_REQUEST, MailService.NAMESPACE_URI );
			doc.AppendChild( reqElem );
			return doc;
		}
	}

	public class NoOpResponse : Response
	{
		public override String Name
		{
			get
			{
				return MailService.NS_PREFIX + ":" + MailService.NO_OP_RESPONSE;
			}
		}

		public override Response NewResponse(XmlNode responseNode)
		{
			return new NoOpResponse();
		}


	}

}
