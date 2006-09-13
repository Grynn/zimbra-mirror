using System;
using System.Xml;

namespace Zimbra.Client.Mail
{
	public class SyncRequest : MailServiceRequest
	{
		public SyncRequest()
		{
		}

		public override String Name()
		{
			return MailService.NS_PREFIX + ":" + MailService.SYNC_REQUEST;
		}

		public override System.Xml.XmlDocument ToXmlDocument()
		{
			XmlDocument doc = new XmlDocument();
			XmlElement reqElem =doc.CreateElement( MailService.SYNC_REQUEST, MailService.NAMESPACE_URI );
			doc.AppendChild( reqElem );
			return doc;
		}
	}
}
