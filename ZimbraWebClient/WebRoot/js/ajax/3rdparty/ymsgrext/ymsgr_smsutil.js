/**
 * Copyright 2006-2008 Yahoo! Inc. All rights reserved.
 */


//
// SMS utility functions - cheifly for processing phone numbers
//
// Dependencies: YMSGR core (_ymsgr.js)
//


YMSGR.SMSUtil = {

	// Normalize a phone number for use with SMS.
	//
	// 'intl' is expected, there is no default value--
	// this library does not depend on any global variables
	//
	normalize : function( phone, intl ) {

		var alphas = "abcdefghijklmnoprstuvwyz";
		
		var stripped = "";
	
		// strip out all invalid chars and replace chars
		// allow * or + at the beginning, strip out everything else non alphanumeric
		var i;
		var c;
		var bStar = false;
		var bPlus = false;
	
		phone = phone.toLowerCase();
	
		for (i=0; i<phone.length; i++)
		{
			var c = phone.charAt(i);
			if ( !bPlus && c == '+' )
			{
				bPlus = true;
				stripped += c;
			}
			else if ( !bStar && c == '*' )
			{
				bStar = true;
				stripped += c;
			}
			else if ( '0' <= c && c <= '9' )
			{
				stripped += c;
			}
			else if ( 'q' == c )
			{
				stripped += '7';
			}
			else if ( 'z' == c )
			{
				stripped += '9';
			}
			else if ( 'a' <= c && c <= 'z' )
			{
				stripped += Math.floor ( alphas.indexOf ( c ) / 3 ) + 2;
			}
		}    

		phone = stripped;
	
		// if starts with +, then assume user entered + format.  strip + and return
		if (phone.charAt(0) == '+')
			return phone.substr(1);

		if ( !intl )
			// We used to check gSMSHomeCountry here - but we have migrated this
			// file to be completely independent of other files.  Pass in the intl
			// to get proper intl processing. -- kevykev
			return phone;


		var idds = {};
		idds['us'] = idds['ca'] = [ '011' ];
		idds['in'] = idds['ph'] = idds['my'] = idds['vn'] = [ '00' ]; // note 'in' trips up yuicompressor if it's not quoted - reserved word
		idds['sg'] = [ '001','002','008' ];
		idds['id'] = [ '001','007','008' ];
		
		var ndds = {};
		ndds['us'] = ndds['ca'] = [ '1' ];
		ndds['in'] = ndds['ph'] = ndds['my'] = ndds['vn'] = ndds['id'] = [ '0' ];
		ndds['sg'] = [ '' ];
		
		var ccodes = {};
		ccodes['us'] = ccodes['ca'] = '1';
		ccodes['in'] = '91';
		ccodes['ph'] = '63';
		ccodes['sg'] = '65';
		ccodes['id'] = '62';
		ccodes['my'] = '60';
		ccodes['vn'] = '84';
		
		// check for idd+countryCode+number format, if matched, strip idd and return
		var idd = idds[intl];
		for (i=0; i<idd.length; i++)
			if (phone.indexOf(idd[i]) == 0)
				return phone.substr(idd[i].length);
	
		var ccode = ccodes[intl];
	
		// check for ndd+number format. if matched, strip ndd, prepend country code and return
		var ndd = ndds[intl];
		for (i=0; i<ndd.length; i++)
			if (phone.indexOf(ndd[i]) == 0)
				return ccode + phone.substr(ndd[i].length);
		
		// fix bug 1250891
		// this is not consistent with the messenger client.  if it starts to create issues,
		// we should revisit this
		if (phone.indexOf(ccode) == 0)
			return phone;
	
		// if no match, assume countrycode was just missing and prepend
		return ccode + phone;    
	},


	// A simple validity check for hand-entered numbers:
	// No alphabetic characters?  Is it long enough?
	//
	// DOES NOT NORMALIZE.  The old utilities (from imViews.js) didn't
	// either, and I was unsure about adding that without thinking
	// through the issues.
	// This function is called when the user inputs a number manually,
	// and normalization might be weird there, and unexpected if say
	// they pasted in something from somewhere else.
	//
	isValid : function ( phone ) {

		// Disallow alphabetics.  This is kind of dumb... should be
		// "anything not a number or + or *".  Copied from the old
		// utils that did this.
		//
		if ( phone && phone.match (/^[a-zA-Z]/) )
			return false;

		// We need a minimum of 10 digits in the string--
		// just need a null check
		//
		return ( phone && phone.match(/^(\D*\d){10,}/) )
	}

};
