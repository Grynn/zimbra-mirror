/*
 * Copyright 2008 Yahoo! Inc. All rights reserved.
 */


// Messenger utilities oriented around sending data once a chat
// session is initiated: sending messages, authorizing buddies, etc.


YMSGR.Send = {

	im : function ( buddy, cloud, profile, msg ) {
		msg = msg.replace(/\"/g, "&quot;").replace(/\'/g, "&apos;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
        var x = YMSGR.j2x( {	current_id: profile,
                		target_user: buddy,
                		cloud_id: cloud,
                		msg: msg
        			 } );
        YMSGR.sdk.send( "im", x );
	},
	
	typing : function ( buddy, cloud, profile, typing ) {
		var x = YMSGR.j2x( { appname: "TYPING", 
					   flag: (typing ? 1 : 0),
					   current_id: profile,
					   target_user: buddy,
					   cloud_id: cloud,
					   msg: ""
					 } );
		YMSGR.sdk.send( "typingIndicator", x );
	},

	sms : function ( phone, carrier, txt ) {
		txt = txt.replace(/\"/g, "&quot;").replace(/\'/g, "&apos;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
        var x = YMSGR.j2x( {	current_id: YMSGR.sdk.getPrimaryId(),
                		target_user: phone,
                		phone_carrier_code: carrier,
                		msg: txt
        			 } );
        YMSGR.sdk.send( "sms", x );
	},

    buddyAuthorize : function ( sSelf, sBuddy, nCloudId, bAuthorize, sMsg ) {
    	var x = YMSGR.j2x( { 	current_id: sSelf,
    					target_user: sBuddy,
    					cloud_id: nCloudId || 0,
    					flag: (bAuthorize ? 1 : 2),
    					msg: sMsg
    				 } );
    	YMSGR.sdk.send( "buddyAuthorize", x );
    },

	ignoreUser : function( sUser, nCloudId, bIgnore )
	{
		var x = [ "<payload current_id='", YMSGR.sdk.getPrimaryId(), "'",
			  " flag='", (bIgnore ? 1 : 2), "'><buddy_record_list>",
			  "<record buddy='", sUser, "' cloud_id='", nCloudId || 0,
			  "'/></buddy_record_list></payload>" ];
		YMSGR.sdk.send( "ignoreUser", x.join("") );
	},


	// These are trivial wrappers.  Consider formally exposing the _sdk (SWF object) and
	// having clients talk to that instead. -- kevykev
	
	reportSpim : function ( sSelf, sUser, nCloudId ) {
		// TODO: Can we normalize this with other calls and pass "0" instead of ""? -- kevykev
	    YMSGR.sdk._sdk.reportSpim( sSelf, sUser, nCloudId ? ("" + nCloudId) : "" );
	},
	
	smsValidate : function ( phone )
	{
		YMSGR.sdk._sdk.smsValidate( phone );
	}

};

