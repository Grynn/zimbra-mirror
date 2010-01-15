/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite CSharp Client
 * Copyright (C) 2006, 2007, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
using System;
using System.Xml;
using System.Collections;

using Zimbra.Client.Util;

namespace Zimbra.Client.Mail
{
	public class GetApptSummariesRequest : MailServiceRequest
	{
		private DateTime start;	//localTime
		private DateTime end;	//localTime
		private String parentFolderId;

		public GetApptSummariesRequest()
		{
		}

		public GetApptSummariesRequest( DateTime localStart, DateTime localEnd, String folderId )
		{
			this.start = localStart;
			this.end = localEnd;
			this.parentFolderId = folderId;
		}

		public override String Name()
		{
			return MailService.NS_PREFIX + ":" + MailService.GET_APPT_SUMMARIES_REQUEST;
		}

		public override XmlDocument ToXmlDocument()
		{
			XmlDocument doc = new XmlDocument();

			XmlElement reqElem = doc.CreateElement( MailService.GET_APPT_SUMMARIES_REQUEST, MailService.NAMESPACE_URI );

			Int64 gmtStartMillis = DateUtil.DateTimeToGmtMillis( start );
			Int64 gmtEndMillis = DateUtil.DateTimeToGmtMillis( end );

			reqElem.SetAttribute( MailService.A_START, gmtStartMillis.ToString() );
			reqElem.SetAttribute( MailService.A_END, gmtEndMillis.ToString() );
			reqElem.SetAttribute( MailService.A_PARENT_FOLDER_ID, parentFolderId );

			doc.AppendChild( reqElem );
			return doc;
		}
	}


	public class GetApptSummariesResponse : Response
	{
		public ArrayList apptSummaries;
		public GetApptSummariesResponse(){}
		public GetApptSummariesResponse(ArrayList apptSummaries)
		{
			this.apptSummaries = apptSummaries;
		}

		public ArrayList ApptSummaries{ get{ return apptSummaries; } }

		public override String Name
		{
			get { return MailService.NS_PREFIX + ":" + MailService.GET_APPT_SUMMARIES_RESPONSE; }
		}

		public override Response NewResponse(XmlNode responseNode)
		{
			ArrayList summaries = new ArrayList();
			for( int i = 0; i < responseNode.ChildNodes.Count; i++ )
			{
				XmlNode child = responseNode.ChildNodes.Item(i);
				summaries.Add( ApptNodeToApptSummary( child ) );
			}
			return new GetApptSummariesResponse(summaries);
		}

		private ApptSummary ApptNodeToApptSummary( XmlNode node )
		{
			//node is the appt node
			ApptSummary summary = new ApptSummary();

			summary.ItemId = XmlUtil.AttributeValue( node.Attributes, MailService.A_ID );
			summary.Name = XmlUtil.AttributeValue( node.Attributes, MailService.A_NAME );
			summary.Location = XmlUtil.AttributeValue( node.Attributes, MailService.A_LOCATION );
			summary.InvId = XmlUtil.AttributeValue( node.Attributes, MailService.A_INV_ID );
			summary.CompNum = XmlUtil.AttributeValue( node.Attributes, MailService.A_COMP_NUM );

			XmlNode fragmentNode = node.SelectSingleNode( MailService.NS_PREFIX + ":" + MailService.E_FRAGMENT, XmlUtil.NamespaceManager );
			if( fragmentNode != null )
			{
				summary.Fragment = fragmentNode.InnerText;
			}

			ArrayList instanceList = new ArrayList();
			XmlNodeList iNodes = node.SelectNodes( MailService.NS_PREFIX + ":" + MailService.E_INSTANCE, XmlUtil.NamespaceManager );
			for( int i = 0; i < iNodes.Count; i++ )
			{
				XmlNode iNode = iNodes.Item(i);
				String s = XmlUtil.AttributeValue( iNode.Attributes, MailService.A_START );
				Int64 seconds = Int64.Parse( s );
				DateTime start = DateUtil.GmtSecondsToLocalTime( seconds );
				ApptSummaryInstance asi = new ApptSummaryInstance( start );
				instanceList.Add( asi );
			}

			summary.Instances = instanceList;
			return summary;
		}

	}




	public class ApptSummary
	{
		public enum ApptType { Event, Todo };
		public enum TimeTransparency { Opaque, Transparent };
		public enum BusyStatus { Free, Busy, Tentative, Unavailable };
		public enum ApptStatus { Tentative, Confirmed, Canceled };
		public enum PartStatus { NeedsAction, Tentative, Accepted, Declined, Delegated };

		private String				itemId;
		private String				invId;
		private String				compNum;
//		private ApptType			type;
//		private TimeTransparency	transp;
//		private BusyStatus			actualBusyStatus;
//		private BusyStatus			intendedBusyStatus;
//		private ApptStatus			apptStatus;
//		private PartStatus			partStat;
//		private bool				allDay;
//		private bool				otherAttendees;
//		private bool				hasAlarms;
//		private bool				isRecurring;
//		private bool				organizedByMe;
		private String				name;
		private String				location;
		private String				fragment;
//		private Int64				duration;

		private ArrayList			apptInstances;

		
		public ApptSummary()
		{
		}


		public String ItemId{ get{ return itemId; } set{ itemId = value; } }
		public String InvId{ get{ return invId; } set{ invId = value; } }
		public String CompNum{ get{ return compNum; } set{ compNum = value; } }
//		public ApptType Type{ get{ return type; } set{ type = value; } }
//		public TimeTransparency Transparency{ get{return transp; } set{ transp = value; } }
//		public BusyStatus ActualBusyStatus{ get{ return actualBusyStatus; } set{ actualBusyStatus = value;  } }
//		public BusyStatus IntendedBusyStatus{ get{ return intendedBusyStatus; } set{ intendedBusyStatus = value;  } }
//		public ApptStatus Status{ get{ return apptStatus; } set{ apptStatus = value; } }
//		public PartStatus ParticipationStatus { get{ return partStat; } set{ partStat = value;  } }
//		public bool IsAllDay{ get{ return allDay; } set{ allDay = value; } }
//		public bool HasAttendees{ get{ return otherAttendees; } set{ otherAttendees = value; }  }
//		public bool HasAlarms{ get{ return hasAlarms; } set{ hasAlarms = value; } }
//		public bool IsRecurring{ get{ return isRecurring; } set{ isRecurring = value; } }
//		public bool OrganizedByMe{ get{ return organizedByMe; } set{ organizedByMe = value; } }
		public String Name{ get{ return name; } set{ name = value; } }
		public String Location{ get{ return location; } set{ location = value; } }
		public String Fragment{ get{ return fragment; } set{ fragment = value; } }
//		public Int64 Duration{ get{ return duration; } set{ duration = value; } }
		public ArrayList Instances{ get{ return apptInstances; } set{ apptInstances = value; } }
	}





	public class ApptSummaryInstance
	{
		private DateTime start; //local start time of this thing

		public ApptSummaryInstance( DateTime start )
		{
			this.start = start; 
		}

		public DateTime Start{ get{ return start; } }
	}
}














