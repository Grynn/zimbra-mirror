using System;
using System.Xml;

namespace Zimbra.Client.Mail
{
	public class MsgActionRequest : MailServiceRequest
	{
		private String op = null;
		private String id = null;
		private String targetFolder = null;

		public MsgActionRequest(String id, String op)
		{
			this.id = id;
			this.op = op;
		}

		public MsgActionRequest( String id, String op, String targetFolder )
		{
			this.id = id;
			this.op = op;
			this.targetFolder = targetFolder;
		}

		public override String Name()
		{
			return MailService.NS_PREFIX + ":" + MailService.MSG_ACTION_REQUEST;
		}

		public override XmlDocument ToXmlDocument()
		{
			XmlDocument doc = new XmlDocument();
			XmlElement reqElem =doc.CreateElement( MailService.MSG_ACTION_REQUEST, MailService.NAMESPACE_URI );
			XmlElement actionElem = doc.CreateElement( MailService.E_ACTION, MailService.NAMESPACE_URI );
			actionElem.SetAttribute( MailService.A_ID, id );
			actionElem.SetAttribute( MailService.A_OP, op );
			if( this.targetFolder != null ) 
			{
				actionElem.SetAttribute( MailService.A_PARENT_FOLDER_ID, targetFolder );
			}
			reqElem.AppendChild( actionElem );
			doc.AppendChild( reqElem );
			return doc;
		}
	}



	public class MsgActionResponse : Response
	{
		public MsgActionResponse()
		{}

		public override String Name
		{
			get { return MailService.NS_PREFIX + ":" + MailService.MSG_ACTION_RESPONSE; }
		}

		public override Response NewResponse(XmlNode responseNode)
		{
			return new MsgActionResponse();
		}


	}
}
