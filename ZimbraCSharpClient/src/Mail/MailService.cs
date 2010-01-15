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

namespace Zimbra.Client.Mail
{
	public class MailService : IZimbraService
	{
		public static String SERVICE_PATH					= "/service/soap";

		public static String NS_PREFIX						= "mail";
		public static String NAMESPACE_URI					= "urn:zimbraMail";

		//requests
		public static String GET_FOLDER_REQUEST				= "GetFolderRequest";
		public static String GET_TAG_REQUEST				= "GetTagRequest";
		public static String SEARCH_REQUEST					= "SearchRequest";
		public static String GET_APPT_SUMMARIES_REQUEST		= "GetApptSummariesRequest";
		public static String GET_APPT_REQUEST				= "GetAppointmentRequest";
		public static String GET_MSG_REQUEST				= "GetMsgRequest";
		public static String SYNC_REQUEST					= "SyncRequest";
		public static String NO_OP_REQUEST					= "NoOpRequest";
		public static String MSG_ACTION_REQUEST				= "MsgActionRequest";
        
		//responses
		public static String GET_FOLDER_RESPONSE			= "GetFolderResponse";
		public static String GET_TAG_RESPONSE				= "GetTagResponse";
		public static String SEARCH_RESPONSE				= "SearchResponse";
		public static String GET_APPT_SUMMARIES_RESPONSE	= "GetApptSummariesResponse";
		public static String GET_APPT_RESPONSE				= "GetAppointmentResponse";
		public static String GET_MSG_RESPONSE				= "GetMsgResponse";
		public static String NO_OP_RESPONSE					= "NoOpResponse";
		public static String MSG_ACTION_RESPONSE			= "MsgActionResponse";
        
		//elements
		public static String E_FOLDER						= "folder";
		public static String E_TAG							= "tag";
		public static String E_QUERY						= "query";
		public static String E_FRAGMENT						= "fr";
		public static String E_INSTANCE						= "inst";
		public static String E_MESSAGE						= "m";
		public static String E_ACTION						= "action"   ;
        
		//attributes
		public static String A_PARENT_FOLDER_ID				= "l";
		public static String A_ID							= "id";
		public static String A_NAME							= "name";
		public static String A_COLOR						= "color";
		public static String A_UNREAD_COUNT					= "u";
		public static String A_ITEM_COUNT					= "n";
		public static String A_VIEW							= "view";
		public static String A_QUERY						= "query";
		public static String A_TYPES						= "types";
		public static String A_SORT_BY						= "sortBy";
		public static String A_LIMIT						= "limit";
		public static String A_OFFSET						= "offset";
		public static String A_GROUP_BY						= "groupBy";
		public static String A_FETCH						= "fetch";
		public static String A_HTML							= "html";
		public static String A_READ							= "read";
		public static String A_RECIP						= "recip";
		public static String A_START						= "s";
		public static String A_END							= "e";
		public static String A_LOCATION						= "loc";
		public static String A_INV_ID						= "invId";
		public static String A_COMP_NUM						= "compNum";
		public static String A_OP							= "op";


		//qualified names

		//responses...
		public static Response[] responses = { 
			new GetFolderResponse(), 
			new GetTagResponse(),
			new GetApptSummariesResponse(),
			new GetAppointmentResponse(),
			new NoOpResponse(),
			new MsgActionResponse()
        };

		//IZimbraService
		public String NamespacePrefix{ get{ return NS_PREFIX;} } 
		public String NamepsaceUri{ get{ return NAMESPACE_URI; } }
		public Response[] Responses{get{ return responses;} }

	}

	public abstract class MailServiceRequest : Request
	{
		public override String ServicePath{ get{ return MailService.SERVICE_PATH; } }
		public override String HttpMethod{ get { return "POST"; } }
	}
}
