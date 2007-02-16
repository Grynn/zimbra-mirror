/* 
 * Copyright (C) 2006-2007 Zimbra, Inc.
 *
 * This program is free software; you can redistribute it and/or 
 * modify it under the terms of version 2 of the GNU Lesser General Public 
 * License as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 */

#include <glib.h>
#include <glib-object.h>
#include <glib/gstdio.h>
#include <libxml/parser.h>
#include <libical/ical.h>
#include <stdio.h>
#include <string.h>

void
print( icaltimetype * t, char * text )
{
	fprintf( stderr, "   icaltimetype %s: year = %d, month = %d, day = %d, hour = %d, minute = %d, second = %d, is_utc = %d, is_date = %d, is_daylight = %d\n", text, t->year, t->month, t->day, t->hour, t->minute, t->second, t->is_utc, t->is_date, t->is_daylight );
}


char*
e_zimbra_xml_find_attribute
	(
	xmlNode		* node,
	const char	* key
	)
{
	xmlAttr *	attr	=	NULL;
	xmlChar	*	val		=	NULL;
	char	*	ret		=	NULL;

	for ( attr = node->properties; attr; attr = attr->next )
	{
		if ( strcmp( ( const char* ) attr->name, key ) == 0 )
		{
			val = attr->children->content;
			break;
		}
	}

	if ( val )
	{
		ret = ( char* ) strdup( ( const char* ) val );
	}

	return ret;
}


gboolean
e_zimbra_xml_check_attribute_exists
	(
	xmlNode		*	node,
	const char	*	key
	)
{
	xmlAttr *	attr	=	NULL;
	gboolean	ret		=	FALSE;

	for ( attr = node->properties; attr; attr = attr->next )
	{
		if ( strcmp( ( const char* ) attr->name, key ) == 0 )
		{
			ret = TRUE;
			break;
		}
	}

	return ret;
}


int
e_zimbra_xml_check_attribute
	(
	xmlNode		*	node,
	const char	*	key,
	const char	*	val
	)
{
	xmlAttr *	attr	=	NULL;
	int			ret		=	0;

	for ( attr = node->properties; attr; attr = attr->next )
	{
		if ( g_str_equal( ( const char* ) attr->name, key )  && g_str_equal( attr->children->content, val ) )
		{
			ret = 1;
			break;
		}
	}

	return ret;
}


xmlNode*
e_zimbra_xml_find_child_by_name
	(
	xmlNode		*	node,
	const char	*	key
	)
{
	xmlNode * child	= NULL;

	for ( child = node->children; child; child = child->next )
	{
		if ( strcmp( ( const char* ) child->name, key ) == 0 )
		{
			return child;
		}
	}

	return NULL;
}


char*
e_zimbra_xml_find_child_value
	(
	xmlNode		* node,
	const char	* key
	)
{
	xmlNode *	child	=	NULL;
	xmlAttr	*	attr	=	NULL;
	xmlChar	*	val		=	NULL;
	char	*	ret 	=	NULL;

	for ( child = node->children; child; child = child->next )
	{
		if ( strcmp( ( const char* ) child->name, "a" ) == 0 )
		{
			for ( attr = child->properties; attr; attr = attr->next )
			{
				if ( strcmp( ( const char* ) attr->name, "n" ) == 0 )
				{
					if ( strcmp( ( const char* ) attr->children->content, key ) == 0 )
					{
						val = child->children->content;
						goto exit;
					}	
				}
			}
		}
	}

exit:

	if ( val )
	{
		ret = ( char* ) strdup( ( const char* ) val );
	}

	return ret;
}


static char*
days_of_week[] =
{
	"",
	"SU",
	"MO",
	"TU",
	"WE",
	"TH",
	"FR",
	"SA",
};


static icaltimezone*
xml_to_icaltimezone
	(
	xmlNode	*	node
	)
{
	icalcomponent	*	comp		=	NULL;
	icaltimezone	*	tz			=	NULL;
	xmlNode			*	std_node	=	NULL;
	xmlNode			*	day_node	=	NULL;
	char			*	id			=	NULL;
	GString			*	str			=	NULL;
	char			*	stdoff		=	NULL;
	char			*	dayoff		=	NULL;
	char			*	std_hour	=	NULL;
	char			*	std_min		=	NULL;
	char			*	std_sec		=	NULL;
	char			*	std_wkday	=	NULL;
	char			*	std_mon		=	NULL;
	char			*	std_week	=	NULL;
	char			*	day_hour	=	NULL;
	char			*	day_min		=	NULL;
	char			*	day_sec		=	NULL;
	char			*	day_wkday	=	NULL;
	char			*	day_mon		=	NULL;
	char			*	day_week	=	NULL;
	int					res;
	gboolean			ok			=	TRUE;

	if ( !g_str_equal( node->name, "tz" ) )
	{
		fprintf( stderr, "bad!!!\n" );
		ok = FALSE;
		goto exit;
	}

	str = g_string_new( "BEGIN:VTIMEZONE\n" );

	id = e_zimbra_xml_find_attribute( node, "id" );

	g_string_sprintfa( str, "TZID:%s\n", id );


	stdoff = e_zimbra_xml_find_attribute( node, "stdoff" );

	if ( dayoff = e_zimbra_xml_find_attribute( node, "dayoff" ) )
	{
		int std_secs = atoi( stdoff );
		int day_secs = atoi( dayoff );

		std_node	= e_zimbra_xml_find_child_by_name( node, "standard" );
		std_wkday	= e_zimbra_xml_find_attribute( std_node, "wkday" );
		std_mon		= e_zimbra_xml_find_attribute( std_node, "mon" );
		std_week	= e_zimbra_xml_find_attribute( std_node, "week" );
		std_sec		= e_zimbra_xml_find_attribute( std_node, "sec" );
		std_min		= e_zimbra_xml_find_attribute( std_node, "min" );
		std_hour	= e_zimbra_xml_find_attribute( std_node, "hour" );
	
		g_string_sprintfa( str, "BEGIN:STANDARD\n" );
		g_string_sprintfa( str, "TZOFFSETFROM:%.2d%.2d\n", day_secs / 60, day_secs % 60 );
		g_string_sprintfa( str, "TZOFFSETTO:%.2d%.2d\n", std_secs / 60, std_secs % 60 );
		g_string_sprintfa( str, "DTSTART:16010101T%.2d%.2d%.2d\n", atoi( std_hour ), atoi( std_min ), atoi( std_sec ) );
		g_string_sprintfa( str, "RRULE:FREQ=YEARLY;INTERVAL=1;BYDAY=%d%s;BYMONTH=%d\n", atoi( std_week ), days_of_week[ atoi( std_wkday ) ], atoi( std_mon ) );
		g_string_sprintfa( str, "END:STANDARD\n" );

		day_node	= e_zimbra_xml_find_child_by_name( node, "daylight" );
		day_wkday	= e_zimbra_xml_find_attribute( day_node, "wkday" );
		day_mon		= e_zimbra_xml_find_attribute( day_node, "mon" );
		day_week	= e_zimbra_xml_find_attribute( day_node, "week" );
		day_sec		= e_zimbra_xml_find_attribute( day_node, "sec" );
		day_min		= e_zimbra_xml_find_attribute( day_node, "min" );
		day_hour	= e_zimbra_xml_find_attribute( day_node, "hour" );

		g_string_sprintfa( str, "BEGIN:DAYLIGHT\n" );
		g_string_sprintfa( str, "TZOFFSETFROM:%.2d%.2d\n", std_secs / 60, std_secs % 60 );
		g_string_sprintfa( str, "TZOFFSETTO:%.2d%.2d\n", day_secs / 60, day_secs % 60 );
		g_string_sprintfa( str, "DTSTART:16010101T%.2d%.2d%.2d\n", atoi( day_hour ), atoi( day_min ), atoi( day_sec ) );
		g_string_sprintfa( str, "RRULE:FREQ=YEARLY;INTERVAL=1;BYDAY=%d%s;BYMONTH=%d\n", atoi( day_week ), days_of_week[ atoi( day_wkday ) ], atoi( day_mon ) );
		g_string_sprintfa( str, "END:DAYLIGHT\n" );
	}
	else
	{
		int secs = atoi( stdoff );

		g_string_sprintfa( str, "BEGIN:STANDARD\n" );
		g_string_sprintfa( str, "TZOFFSETFROM:%.2d%.2d\n", secs / 60, secs % 60 );
		g_string_sprintfa( str, "TZOFFSETTO:%.2d%.2d\n", secs / 60, secs % 60 );
		g_string_sprintfa( str, "END:STANDARD\n" );
	}

	g_string_sprintfa( str, "END:VTIMEZONE" );

	comp	= icalcomponent_new_from_string( str->str );
	tz		= icaltimezone_new();

	res = icaltimezone_set_component( tz, comp );

	if ( res == 0 )
	{
		tz = NULL;
	}

exit:

	if ( stdoff )
	{
		g_free( stdoff );
	}

	if ( dayoff )
	{
		g_free( dayoff );
	}

	if ( std_hour )
	{
		g_free( std_hour );
	}

	if ( std_min )
	{
		g_free( std_min );
	}

	if ( std_sec )
	{
		g_free( std_sec );
	}

	if ( std_wkday )
	{
		g_free( std_wkday );
	}

	if ( std_mon	)
	{
		g_free( std_mon );
	}

	if ( std_week )
	{
		g_free( std_week );
	}

	if ( day_hour )
	{
		g_free( day_hour );
	}

	if ( day_min )
	{
		g_free( day_min );
	}

	if ( day_sec )
	{
		g_free( day_sec );
	}

	if ( day_wkday )
	{
		g_free( day_wkday );
	}

	if ( day_mon )
	{
		g_free( day_mon );
	}

	if ( day_week )
	{
		g_free( day_week );
	}

	if ( str )
	{
		g_string_free( str, TRUE );
	}

	return tz;
}


static gboolean
parse_tzoffset
	(
	const char	*	tzoffsetfrom,
	int			*	tzoffsetfrom_hour, 
	int			*	tzoffsetfrom_min 
	)
{
	char	digits[3]	=	{ 0, 0, 0 };
	int		sign		=	1;

	if ( tzoffsetfrom[0] == '-' )
	{
		tzoffsetfrom++;
		sign = -1;
	}
	else if ( tzoffsetfrom[0] == '+' )
	{
		tzoffsetfrom++;
		sign = 1;
	}

	digits[0] = *tzoffsetfrom++;
	digits[1] = *tzoffsetfrom++;

	*tzoffsetfrom_hour = atoi( digits ) * sign;

	digits[0] = *tzoffsetfrom++;
	digits[1] = *tzoffsetfrom++;
		
	*tzoffsetfrom_min = atoi( digits );

	return TRUE;
}


static gboolean
parse_dtstart
	(
	const char	*	dtstart,
	int			*	dtstart_hour,
	int			*	dtstart_min,
	int			*	dtstart_sec
	)
{
	char		digits[3]	=	{ 0, 0, 0 };
	gboolean	ok			=	TRUE;

//DTSTART:19701025T020000

	// Find the 'T'

	while ( *dtstart && ( *dtstart != 'T' ) )
	{
		dtstart++;
	}

	if ( !*dtstart )
	{
		ok = FALSE;
		goto exit;
	}

	*dtstart++;

	digits[0] = *dtstart++;
	digits[1] = *dtstart++;

	*dtstart_hour = atoi( digits );

	digits[0] = *dtstart++;
	digits[1] = *dtstart++;

	*dtstart_min = atoi( digits );
	
	digits[0] = *dtstart++;
	digits[1] = *dtstart++;

	*dtstart_sec = atoi( digits );

exit:

	return ok;
}


static gboolean
parse_rrule
	(
	const char	*	rrule,
	int			*	rrule_freq,
	int			*	rrule_interval,
	int			*	rrule_month,
	int			*	rrule_week,
	int			*	rrule_day
	)
{
	char	*	scratch = g_strdup( rrule );
	char	*	savept;
	char	*	tok;

	tok = strtok_r( scratch, ";", &savept );


	while ( tok )
	{
		char * dup = g_strdup( tok );
		char * val;

		val = strchr( dup, '=' );

		*val = '\0';

		val++;

		if ( g_str_equal( dup, "FREQ" ) )
		{
			*rrule_freq = atoi( val );
		}
		else if ( g_str_equal( dup, "INTERVAL" ) )
		{
			*rrule_interval = atoi( val );
		}
		else if ( g_str_equal( dup, "BYDAY" ) )
		{
			int sign = 1;

			if ( *val == '-' )
			{
				sign = -1;
				val++;
			}
			else if ( *val == '+' )
			{
				sign = 1;
				val++;
			}

			*rrule_week = ( *val++ - '0' ) * sign;

			if ( g_str_equal( val, "SU" ) )
			{
				*rrule_day = 1;
			}
			else if ( g_str_equal( val, "MO" ) )
			{
				*rrule_day = 2;
			}
			else if ( g_str_equal( val, "TU" ) )
			{
				*rrule_day = 3;
			}
			else if ( g_str_equal( val, "WE" ) )
			{
				*rrule_day = 4;
			}
			else if ( g_str_equal( val, "TH" ) )
			{
				*rrule_day = 5;
			}
			else if ( g_str_equal( val, "FR" ) )
			{
				*rrule_day = 6;
			}
			else if ( g_str_equal( val, "SA" ) )
			{
				*rrule_day = 7;
			}
		}
		else if ( g_str_equal( dup, "BYMONTH" ) )
		{
			*rrule_month = atoi( val );
		}
		else
		{
		}

		tok = strtok_r( NULL, ";", &savept );
	}
}

	
static char*
icaltimezone_to_xml
	(
	icaltimezone * tz
	)
{
	icalcomponent	*	comp					=	NULL;
	icalcomponent	*	child					=	NULL;
	icalproperty	*	prop					=	NULL;
	gboolean			standard				=	FALSE;
	gboolean			daylight				=	FALSE;
	const char		*	id						=	NULL;
	const char		*	std_tzoffsetfrom		=	NULL;
	const char		*	std_tzoffsetto			=	NULL;
	const char		*	std_dtstart				=	NULL;
	const char		*	std_rrule				=	NULL;
	int					std_tzoffsetfrom_hour	=	0;
	int					std_tzoffsetfrom_min	=	0;
	int					std_tzoffsetto_hour		=	0;
	int					std_tzoffsetto_min		=	0;
	int					std_dtstart_hour		=	0;
	int					std_dtstart_min			=	0;
	int					std_dtstart_sec			=	0;
	int					std_rrule_freq			=	0;
	int					std_rrule_interval		=	0;
	int					std_rrule_month			=	0;
	int					std_rrule_week			=	0;
	int					std_rrule_day			=	0;
	int					day_tzoffsetfrom_hour	=	0;
	int					day_tzoffsetfrom_min	=	0;
	int					day_tzoffsetto_hour		=	0;
	int					day_tzoffsetto_min		=	0;
	int					day_dtstart_hour		=	0;
	int					day_dtstart_min			=	0;
	int					day_dtstart_sec			=	0;
	int					day_rrule_freq			=	0;
	int					day_rrule_interval		=	0;
	int					day_rrule_month			=	0;
	int					day_rrule_week			=	0;
	int					day_rrule_day			=	0;
	const char		*	day_tzoffsetfrom		=	NULL;
	const char		*	day_tzoffsetto			=	NULL;
	const char		*	day_dtstart				=	NULL;
	const char		*	day_rrule				=	NULL;
	GString			*	str						=	NULL;

	comp = icaltimezone_get_component( tz );

	prop = icalcomponent_get_first_property( comp, ICAL_TZID_PROPERTY );

	str = g_string_new( "<tz " );

	// Get the TZID

	id = icalproperty_get_value_as_string( prop );

	// Now get the subcomponents

	for ( child = icalcomponent_get_first_component( comp, ICAL_ANY_COMPONENT ); child; child = icalcomponent_get_next_component( comp, ICAL_ANY_COMPONENT ) )
	{
		icalcomponent_kind kind = icalcomponent_isa( child );

		switch ( kind )
		{
			case ICAL_XSTANDARD_COMPONENT:
			{
				standard = TRUE;

				for ( prop = icalcomponent_get_first_property( child, 0 ); prop; prop = icalcomponent_get_next_property( child, 0 ) )
				{
					icalproperty_kind kind = icalproperty_isa( prop );

					switch (kind)
					{
						case ICAL_TZOFFSETFROM_PROPERTY:
						{
							std_tzoffsetfrom = icalproperty_get_value_as_string( prop );
						}
						break;

						case ICAL_TZOFFSETTO_PROPERTY:
						{
							std_tzoffsetto = icalproperty_get_value_as_string( prop );
						}
						break;

						case ICAL_DTSTART_PROPERTY:
						{
							std_dtstart = icalproperty_get_value_as_string( prop );
						}
						break;

						case ICAL_RRULE_PROPERTY:
						{
							std_rrule = icalproperty_get_value_as_string( prop );
						}
						break;

						case ICAL_RDATE_PROPERTY:
						{
						}
						break;

						default:
						{
							// Ignore any property we're not looking for
						}
					}
				}
			}
			break;

			case ICAL_XDAYLIGHT_COMPONENT:
			{
				daylight = TRUE;

				for ( prop = icalcomponent_get_first_property( child, 0 ); prop; prop = icalcomponent_get_next_property( child, 0 ) )
				{
					icalproperty_kind kind = icalproperty_isa( prop );

					switch (kind)
					{
						case ICAL_TZOFFSETFROM_PROPERTY:
						{
							day_tzoffsetfrom = icalproperty_get_value_as_string( prop );
						}
						break;

						case ICAL_TZOFFSETTO_PROPERTY:
						{
							day_tzoffsetto = icalproperty_get_value_as_string( prop );
						}
						break;

						case ICAL_DTSTART_PROPERTY:
						{
							day_dtstart = icalproperty_get_value_as_string( prop );
						}
						break;

						case ICAL_RRULE_PROPERTY:
						{
							day_rrule = icalproperty_get_value_as_string( prop );
						}
						break;

						case ICAL_RDATE_PROPERTY:
						{
						}
						break;

						default:
						{
							// Ignore any property we're not looking for
						}
					}
				}
			}
			break;

			default:
			{
				fprintf( stderr, "unknown component\n" );
			}
			break;
		}
	}

	if ( standard && !daylight )
	{
		parse_tzoffset( std_tzoffsetfrom, &std_tzoffsetfrom_hour, &std_tzoffsetfrom_min );
		parse_tzoffset( std_tzoffsetto, &std_tzoffsetto_hour, &std_tzoffsetto_min );
		parse_dtstart( std_dtstart, &std_dtstart_hour, &std_dtstart_min, &std_dtstart_sec );

		g_string_sprintfa( str, "stdoff=\"%d\" id=\"%s\"></tz>", std_tzoffsetfrom_hour * 60, id );
	}
	else if ( standard && daylight )
	{
		parse_tzoffset( std_tzoffsetfrom, &std_tzoffsetfrom_hour, &std_tzoffsetfrom_min );
		parse_tzoffset( std_tzoffsetto, &std_tzoffsetto_hour, &std_tzoffsetto_min );
		parse_dtstart( std_dtstart, &std_dtstart_hour, &std_dtstart_min, &std_dtstart_sec );
		parse_rrule( std_rrule, &std_rrule_freq, &std_rrule_interval, &std_rrule_month, &std_rrule_week, &std_rrule_day );

		parse_tzoffset( day_tzoffsetfrom, &day_tzoffsetfrom_hour, &day_tzoffsetfrom_min );
		parse_tzoffset( day_tzoffsetto, &day_tzoffsetto_hour, &day_tzoffsetto_min );
		parse_dtstart( day_dtstart, &day_dtstart_hour, &day_dtstart_min, &day_dtstart_sec );
		parse_rrule( day_rrule, &day_rrule_freq, &day_rrule_interval, &day_rrule_month, &day_rrule_week, &day_rrule_day );

/*
		fprintf( stderr, "std_tzoffsetfrom_hour = %d, std_tzoffsetfrom_min = %d\n", std_tzoffsetfrom_hour, std_tzoffsetfrom_min );
		fprintf( stderr, "std_tzoffsetto_hour = %d, std_tzoffsetto_min = %d\n", std_tzoffsetto_hour, std_tzoffsetto_min );
		fprintf( stderr, "std_dtstart_hour = %d, std_dtstart_min = %d, std_dtstart_sec = %d\n", std_dtstart_hour, std_dtstart_min, std_dtstart_sec );
		fprintf( stderr, "std_rrule_freq = %d, std_rrule_interval = %d, std_rrule_month = %d, std_rrule_week = %d, std_rrule_day = %d\n", std_rrule_freq, std_rrule_interval, std_rrule_month, std_rrule_week, std_rrule_day );

		fprintf( stderr, "day_tzoffsetfrom_hour = %d, day_tzoffsetfrom_min = %d\n", day_tzoffsetfrom_hour, day_tzoffsetfrom_min );
		fprintf( stderr, "day_tzoffsetto_hour = %d, day_tzoffsetto_min = %d\n", day_tzoffsetto_hour, day_tzoffsetto_min );
		fprintf( stderr, "day_dtstart_hour = %d, day_dtstart_min = %d, day_dtstart_sec = %d\n", day_dtstart_hour, day_dtstart_min, day_dtstart_sec );
		fprintf( stderr, "day_rrule_freq = %d, day_rrule_interval = %d, day_rrule_month = %d, day_rrule_week = %d, day_rrule_day = %d\n", day_rrule_freq, day_rrule_interval, day_rrule_month, day_rrule_week, day_rrule_day );
*/

		g_string_sprintfa( str, "dayoff=\"%d\" stdoff=\"%d\" id=\"%s\"><standard sec=\"%d\" hour=\"%d\" wkday=\"%d\" min=\"%d\" mon=\"%d\" week=\"%d\"></standard><daylight sec=\"%d\" hour=\"%d\" wkday=\"%d\" min=\"%d\" mon=\"%d\" week=\"%d\"></daylight></tz>", std_tzoffsetfrom_hour * 60, std_tzoffsetto_hour * 60, id, std_dtstart_sec, std_dtstart_hour, std_rrule_day, std_dtstart_min, std_rrule_month, std_rrule_week, day_dtstart_sec, day_dtstart_hour, day_rrule_day, day_dtstart_min, day_rrule_month, day_rrule_week );

	}
	else
	{
		// error
	}

	return str->str;
}

#if 0
BEGIN:VTIMEZONE
TZID:/softwarestudio.org/Olson_20011030_5/America/Los_Angeles
X-LIC-LOCATION:America/Los_Angeles
BEGIN:STANDARD
END:STANDARD
BEGIN:DAYLIGHT
TZOFFSETFROM:-0800
TZOFFSETTO:-0700
TZNAME:PDT
DTSTART:19700405T020000
END:DAYLIGHT
END:VTIMEZONE
#endif

#if 0
char * str = "BEGIN:VTIMEZONE\nTZID:(GMT-08.00) Pacific Time (US & Canada)/Tijuana\nBEGIN:STANDARD\nTZOFFSETFROM:-0700\nTZOFFSETTO:-0800\nRULE:FREQ=YEARLY;INTERVAL=1;BYDAY=-1SU;BYMONTH=10\nEND:STANDARD\nBEGIN:DAYLIGHT\nTZOFFSETFROM:-0800\nTZOFFSETTO:-0700\nRRULE:FREQ=YEARLY;INTERVAL=1;BYDAY=1SU;BYMONTH=4\nEND:DAYLIGHT\nEND:VTIMEZONE";
#endif

char * xml = "<tz dayoff=\"-420\" stdoff=\"-480\" id=\"(GMT-08.00) Pacific Time (US &amp; Canada) / Tijuana\"><standard sec=\"0\" hour=\"2\" wkday=\"1\" min=\"0\" mon=\"10\" week=\"-1\"></standard><daylight sec=\"0\" hour=\"2\" wkday=\"1\" min=\"0\" mon=\"4\" week=\"1\"></daylight></tz>";


#if 0
int
main()
{
	icalcomponent	*	comp;
	icaltimezone	*	tz;
	xmlDocPtr			doc;
	char			*	str;
	xmlNode			*	root;

	doc = xmlParseMemory( xml, strlen( xml ) );

	root = xmlDocGetRootElement( doc );

	tz = xml_to_icaltimezone( root );

	if ( tz )
	{
		fprintf( stderr, "got timezone\n" );
	}
	else
	{	
		fprintf( stderr, "no timezone\n" );
	}

/*
 	tz = icaltimezone_get_builtin_timezone_from_tzid( "/softwarestudio.org/Olson_20011030_5/America/Los_Angeles" );

	if ( tz )
	{
		fprintf( stderr, "yo\n" );
	}
*/

	str = icaltimezone_to_xml( tz );

	fprintf( stderr, "xml = %s\n", str );



 icalarray * arr = icaltimezone_get_builtin_timezones();

    if ( arr )
    {
        fprintf( stderr, "got array!!: %d\n", arr->num_elements );

        int i;

        for ( i = 0; i < arr->num_elements; i++ )
        {
            char * tznames;

            tz = icalarray_element_at( arr, i );

            fprintf( stderr, "tzid[%d] = %s\n", i, icaltimezone_get_location( tz ) );

            tznames = icaltimezone_get_tznames( tz );
            fprintf( stderr, "  timezones = %s\n", tznames ? tznames : "NULL" );

            icalcomponent * comp = icaltimezone_get_component( tz );

            if ( comp )
            {
                fprintf( stderr, "  component as string: %s\n", icalcomponent_as_ical_string( comp ) );
            }

			char * str = icaltimezone_to_xml( tz );

			if ( str )
			{
				fprintf( stderr, "   as xml: %s\n\n\n", str );
			}
			else
			{
				fprintf( stderr, "unable to translate to xml\n\n\n" );
			}
        }
	}

}
#endif

int
main()
{
	icaltimetype tt;

	g_type_init();
	g_thread_init( NULL );

	fprintf( stderr, "about to call!\n" );

	tt = icaltime_from_string( "20050315T183023Z" );

	fprintf( stderr, "year: %d\n", tt.year );

	const icaltimezone * tz = tt.zone;

	const char * s = icaltime_as_ical_string( tt );

	fprintf( stderr, "yo: %s\n", s );
	fprintf( stderr, "tzid: %s\n", icaltimezone_get_tzid( tz ) );
}
