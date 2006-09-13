using System;
using System.Xml;
namespace Zimbra.Client.Mail
{

	public class GetMsgRequest : MailServiceRequest
	{
		private String id;

		public GetMsgRequest(String id)
		{
			this.id = id;
		}

		public override String Name()
		{
			return MailService.NS_PREFIX + ":" + MailService.GET_MSG_REQUEST;
		}

		public override XmlDocument ToXmlDocument()
		{
			XmlDocument doc = new XmlDocument();
			XmlElement reqElem =doc.CreateElement( MailService.GET_MSG_REQUEST, MailService.NAMESPACE_URI );

			XmlElement mElem = doc.CreateElement( MailService.E_MESSAGE, MailService.NAMESPACE_URI );
			mElem.SetAttribute(  MailService.A_ID, id );

			reqElem.AppendChild( mElem );
			doc.AppendChild( reqElem );
			return doc;
		}
	}
}
