/* -*- Mode: C; tab-width: 4; indent-tabs-mode: t; c-basic-offset: 8 -*- */
/* 
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
 * Authors : 
 *  Scott Herscher <scott.herscher@zimbra.com>
 *  Copyright 2006, Zimbra, Inc.
 *
 */

#include <config.h>

#include <string.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>

#ifndef O_BINARY
#define O_BINARY 0
#endif

#include <glib.h>
#include <glib/gi18n.h>
#include <glib/gstdio.h>
#include <libgnomevfs/gnome-vfs-mime-utils.h>
#include <libezimbra/e-zimbra-connection.h>
#include <libezimbra/e-zimbra-debug.h>
#include <libecal/e-cal-recur.h>
#include <libecal/e-cal-time-util.h>
#include <libical/icalrecur.h>
#include "e-cal-backend-zimbra-utils.h"
#include <libedataserver/e-source-list.h>
#include "e-cal-backend-zimbra.h"


const char *
e_cal_component_get_zimbra_id
	(
	ECalComponent	*	comp
	)
{
	return e_cal_component_get_x_data( comp, ZIMBRA_X_APPT_ID );
}


icalproperty*
e_cal_component_get_x_property
	(
	ECalComponent	*	comp,
	const char		*	prop_name
	)
{
	icalproperty * prop;	
	
	prop = icalcomponent_get_first_property( e_cal_component_get_icalcomponent( comp ), ICAL_X_PROPERTY );

	while ( prop )
	{
		const char * x_name;
		const char * x_val;

		x_name = icalproperty_get_x_name( prop );

		if ( !strcmp( x_name, prop_name ) )
		{
			return prop;
		}

		prop = icalcomponent_get_next_property( e_cal_component_get_icalcomponent( comp ), ICAL_X_PROPERTY );
	}

	return NULL;
}


const char *
e_cal_component_get_x_data
	(
	ECalComponent	*	comp,
	const char		*	prop_name
	)
{
	icalproperty * icalprop;	

	if ( ( icalprop = e_cal_component_get_x_property( comp, prop_name ) ) != NULL )
	{
		return icalproperty_get_x( icalprop );
	}
	else
	{
		return NULL;
	}
}


static void
e_cal_backend_zimbra_set_attachments_from_comp (ECalComponent *comp,
		EZimbraItem *item)
{
#if 0
	GSList *attach_list = NULL, *attach_file_list = NULL;
	GSList *l;

	e_cal_component_get_attachment_list (comp, &attach_file_list);
	
	for (l = attach_file_list; l ; l = l->next) {
		
		EZimbraItemAttachment *attach_item;
		char *file_contents, *encoded_data;
		int file_len;
		char *attach_filename_full, *filename;
		const char *uid;

		attach_filename_full = g_filename_from_uri ((char *)l->data, NULL, NULL);
		if (!g_file_get_contents (attach_filename_full, &file_contents, ( unsigned int* ) &file_len, NULL)) {
			g_message ("DEBUG: could not read %s\n", attach_filename_full);
			g_free (attach_filename_full);
			continue;
		}

		/* Extract the simple file name from the
		 * attach_filename_full which is of the form
		 * file://<path>/compuid-<simple filename> 
		 */
		e_cal_component_get_uid (comp, &uid);
		filename = g_strrstr (attach_filename_full, uid); 		
		if (filename == NULL) {
			g_message ("DEBUG: This is an invalid attachment file\n");
			g_free (attach_filename_full);
			g_free (file_contents);
			continue;
		}

		attach_item = g_new0 (EZimbraItemAttachment, 1);
		/* FIXME the member does not follow the naming convention.
		 * Should be fixed in e-gw-item*/
		attach_item->contentType = g_strdup (gnome_vfs_get_mime_type (attach_filename_full));
		g_free (attach_filename_full);

		attach_item->name = g_strdup (filename + strlen(uid) + 1);
		/* do a base64 encoding so it can be embedded in a soap
		 * message */
		encoded_data = soup_base64_encode (file_contents, file_len);
		attach_item->data = encoded_data;
		attach_item->size = strlen (encoded_data); 

		g_free (file_contents);
		attach_list = g_slist_append (attach_list, attach_item);
	}

	e_zimbra_item_set_attach_id_list (item, attach_list);
#endif
}

/* Returns the icalproperty for the Attendee associted with email id */
static icalproperty *
get_attendee_prop (icalcomponent *icalcomp, const char *attendee)
{
	icalproperty *prop;	

	for (prop = icalcomponent_get_first_property (icalcomp, ICAL_ATTENDEE_PROPERTY);
			prop;
			prop = icalcomponent_get_next_property (icalcomp, ICAL_ATTENDEE_PROPERTY)) {
		const char *att = icalproperty_get_attendee (prop);

		if (!g_ascii_strcasecmp (att, attendee)) {
			return prop;
		}
	}

	return NULL;
}


/* get_attendee_list from cal comp and convert into
 * egwitemrecipient and set it on recipient_list*/
static void
set_attendees_to_item (EZimbraItem *item, ECalComponent *comp, icaltimezone *default_zone, gboolean delegate, const char *user_email)
{
	EZimbraItemOrganizer *organizer = NULL;

	if (e_cal_component_has_attendees (comp)) {
		GSList *attendee_list, *recipient_list = NULL, *al;

		e_cal_component_get_attendee_list (comp, &attendee_list);	
		for (al = attendee_list; al != NULL; al = al->next) {
			ECalComponentAttendee *attendee = (ECalComponentAttendee *) al->data;
			EZimbraItemRecipient *recipient;
				
			if (delegate && (g_str_equal (attendee->value + 7, user_email) || !(attendee->delfrom && *attendee->delfrom)))
				continue;		
			
			if (delegate) {
				icalproperty *prop = get_attendee_prop (e_cal_component_get_icalcomponent (comp), 
						attendee->value);
				if (prop) 
					icalproperty_remove_parameter_by_kind (prop, ICAL_DELEGATEDFROM_PARAMETER);
			}
	
			recipient = g_new0 (EZimbraItemRecipient, 1);

			/* len (MAILTO:) + 1 = 7 */
			recipient->email = g_strdup (attendee->value + 7);
			if (attendee->cn != NULL)
				recipient->display_name = g_strdup (attendee->cn);
			if (attendee->role == ICAL_ROLE_REQPARTICIPANT) 
			{
				recipient->role = E_ZIMBRA_ITEM_ROLE_REQUIRED_PARTICIPANT;
			}
			else if (attendee->role == ICAL_ROLE_OPTPARTICIPANT)
			{
				recipient->role = E_ZIMBRA_ITEM_ROLE_OPTIONAL_PARTICIPANT;
			}
			else
			{
				recipient->role = E_ZIMBRA_ITEM_ROLE_NON_PARTICIPANT;
			}

			if ( attendee->status == ICAL_PARTSTAT_ACCEPTED )
			{
				recipient->status = E_ZIMBRA_ITEM_PART_STAT_ACCEPTED;
			}
			else if ( attendee->status == ICAL_PARTSTAT_DECLINED )
			{
				recipient->status = E_ZIMBRA_ITEM_PART_STAT_DECLINED;
			}
			else if ( attendee->status == ICAL_PARTSTAT_TENTATIVE )
			{
				recipient->status = E_ZIMBRA_ITEM_PART_STAT_TENTATIVE;
			}
			else if ( attendee->status == ICAL_PARTSTAT_DELEGATED )
			{
				recipient->status = E_ZIMBRA_ITEM_PART_STAT_DELEGATED;
			}
			else if ( attendee->status == ICAL_PARTSTAT_COMPLETED )
			{
				recipient->status = E_ZIMBRA_ITEM_PART_STAT_DELEGATED;
			}
			else if ( attendee->status == ICAL_PARTSTAT_INPROCESS )
			{
				recipient->status = E_ZIMBRA_ITEM_PART_STAT_DELEGATED;
			}
			else
			{
				recipient->status = E_ZIMBRA_ITEM_PART_STAT_NEEDSACTION;
			}

			recipient_list = g_slist_append (recipient_list, recipient);
		}

		e_cal_component_free_attendee_list(attendee_list);
	
		/* recipient_list shouldn't be freed. Look into the function below. */
		e_zimbra_item_set_recipient_list (item, recipient_list);
	}

	organizer = g_new0 (EZimbraItemOrganizer, 1);

	if ( e_cal_component_has_organizer( comp ) )
	{
		ECalComponentOrganizer cal_organizer;

		e_cal_component_get_organizer (comp, &cal_organizer);
		organizer->display_name = g_strdup (cal_organizer.cn);
		organizer->email = g_strdup (cal_organizer.value + 7);
	}
	else
	{
		organizer = g_new0( EZimbraItemOrganizer, 1 );
		organizer->display_name = 0;
		organizer->email = g_strdup( user_email );
	}

	e_zimbra_item_set_organizer (item, organizer);
}


static void
set_rrule_from_item
	(
	ECalBackendZimbra	*	cbz,
	EZimbraItem			*	item,
	ECalComponent		*	comp
	)
{
	EZimbraItemRecurrenceRule	*	item_rrule	=	NULL;
	struct icalrecurrencetype	*	ical_rrule	=	NULL;
	GSList						*	rule_list	=	NULL;
	int								i;

	if ( ( item_rrule = e_zimbra_item_get_rrule( item ) ) != NULL )
	{
		ical_rrule = g_new0( struct icalrecurrencetype, 1 );
		icalrecurrencetype_clear( ical_rrule );

		// Frequency

		switch ( item_rrule->frequency )
		{
			case E_ZIMBRA_ITEM_RECURRENCE_FREQUENCY_DAILY:
			{
				ical_rrule->freq = ICAL_DAILY_RECURRENCE;
			}
			break;

			case E_ZIMBRA_ITEM_RECURRENCE_FREQUENCY_WEEKLY:
			{
				ical_rrule->freq = ICAL_WEEKLY_RECURRENCE;
			}
			break;

			case E_ZIMBRA_ITEM_RECURRENCE_FREQUENCY_MONTHLY:
			{
				ical_rrule->freq = ICAL_MONTHLY_RECURRENCE;
			}
			break;

			case E_ZIMBRA_ITEM_RECURRENCE_FREQUENCY_YEARLY:
			{
				ical_rrule->freq = ICAL_YEARLY_RECURRENCE;
			}
			break;

			default:
			{
			}
			break;
		}

		// Count/Until

		if ( item_rrule->count )
		{
			ical_rrule->count = item_rrule->count;
		}
		else if ( !icaltime_is_null_time( item_rrule->until ) )
		{
			ical_rrule->until = item_rrule->until;

			if ( !icaltime_is_date( ical_rrule->until ) )
			{
				// We want to convert every time to our default timezone

				icaltime_set_timezone( &ical_rrule->until, e_cal_backend_zimbra_get_default_zone( cbz ) );

				icaltimezone_convert_time( &ical_rrule->until, ( icaltimezone* ) item_rrule->until.zone, ( icaltimezone* ) ical_rrule->until.zone ); 
			}
		}

		// Interval

		ical_rrule->interval = item_rrule->interval;

		for (i = 0; i < ICAL_BY_DAY_SIZE; i++)
		{
			ical_rrule->by_day[i] = item_rrule->by_day[i];
		}

		for (i = 0; i < ICAL_BY_MONTHDAY_SIZE; i++)
		{
			ical_rrule->by_month_day[i] = item_rrule->by_month_day[i];
		}

		for (i = 0; i < ICAL_BY_YEARDAY_SIZE; i++)
		{
			ical_rrule->by_year_day[i] = item_rrule->by_year_day[i];
		}

		for (i = 0; i < ICAL_BY_WEEKNO_SIZE; i++)
		{
			ical_rrule->by_week_no[i] = item_rrule->by_week_no[i];
		}

		for (i = 0; i < ICAL_BY_MONTH_SIZE; i++)
		{
			ical_rrule->by_month[i] = item_rrule->by_month[i];
		}

		for (i = 0; i < ICAL_BY_SETPOS_SIZE; i++)
		{
			ical_rrule->by_set_pos[i] = item_rrule->by_set_pos[i];
		}

		switch ( item_rrule->week_start )
		{
			case E_ZIMBRA_ITEM_RECURRENCE_WEEKDAY_NONE:
			{
				ical_rrule->week_start = ICAL_NO_WEEKDAY;
			}
			break;

			case E_ZIMBRA_ITEM_RECURRENCE_WEEKDAY_SUNDAY:
			{
				ical_rrule->week_start = ICAL_SUNDAY_WEEKDAY;
			}
			break;

			case E_ZIMBRA_ITEM_RECURRENCE_WEEKDAY_MONDAY:
			{
				ical_rrule->week_start = ICAL_MONDAY_WEEKDAY;
			}
			break;

			case E_ZIMBRA_ITEM_RECURRENCE_WEEKDAY_TUESDAY:
			{
				ical_rrule->week_start = ICAL_TUESDAY_WEEKDAY;
			}
			break;

			case E_ZIMBRA_ITEM_RECURRENCE_WEEKDAY_WEDNESDAY:
			{
				ical_rrule->week_start = ICAL_WEDNESDAY_WEEKDAY;
			}
			break;

			case E_ZIMBRA_ITEM_RECURRENCE_WEEKDAY_THURSDAY:
			{
				ical_rrule->week_start = ICAL_THURSDAY_WEEKDAY;
			}
			break;

			case E_ZIMBRA_ITEM_RECURRENCE_WEEKDAY_FRIDAY:
			{
				ical_rrule->week_start = ICAL_FRIDAY_WEEKDAY;
			}
			break;

			case E_ZIMBRA_ITEM_RECURRENCE_WEEKDAY_SATURDAY:
			{
				ical_rrule->week_start = ICAL_SATURDAY_WEEKDAY;
			}
			break;
		}

		rule_list = g_slist_append( rule_list, ical_rrule );

		e_cal_component_set_rrule_list( comp, rule_list );
	}
}


static void
set_rrule_from_comp
	(
	ECalBackendZimbra	*	cbz,
	ECalComponent		*	comp,
	EZimbraItem			*	item
	)
{
	EZimbraItemRecurrenceRule	*	item_rrule	=	NULL;
	struct icalrecurrencetype	*	ical_rrule	=	NULL;
	GSList						*	rrule_list	=	NULL;
	GSList						*	exdate_list	=	NULL;
	int								i;

	item_rrule = g_new0 (EZimbraItemRecurrenceRule, 1);
	e_cal_component_get_rrule_list( comp, &rrule_list );

	if (rrule_list)
	{
		// assumes only one rrule is present

		ical_rrule = ( struct icalrecurrencetype* ) rrule_list->data;
		
		g_message ("DEBUG: Processing rule\n%s\n", icalrecurrencetype_as_string (ical_rrule));

		// set the data

		switch ( ical_rrule->freq )
		{
			case ICAL_DAILY_RECURRENCE:
			{
				item_rrule->frequency = E_ZIMBRA_ITEM_RECURRENCE_FREQUENCY_DAILY;
			}
			break;

			case ICAL_WEEKLY_RECURRENCE:
			{
				item_rrule->frequency = E_ZIMBRA_ITEM_RECURRENCE_FREQUENCY_WEEKLY;
			}
			break;

			case ICAL_MONTHLY_RECURRENCE:
			{
				item_rrule->frequency = E_ZIMBRA_ITEM_RECURRENCE_FREQUENCY_MONTHLY;
			}
			break;

			case ICAL_YEARLY_RECURRENCE:
			{
				item_rrule->frequency = E_ZIMBRA_ITEM_RECURRENCE_FREQUENCY_YEARLY;
			}
			break;

			default:
			{
			}
			break;
		}

		// Count/Until

		if ( ical_rrule->count != 0 )
		{
			item_rrule->count = ical_rrule->count;
		}
		else if ( !icaltime_is_null_time( ical_rrule->until ) )
		{
			// Convert all these bad boys to UTC time per zimbra soap spec

			item_rrule->until = ical_rrule->until;

			if ( !icaltime_is_date( item_rrule->until ) )
			{
				icaltimezone_convert_time( &item_rrule->until, e_cal_backend_zimbra_get_default_zone( cbz ), icaltimezone_get_utc_timezone() );
			}
		}

		// Until

		item_rrule->interval = ical_rrule->interval;

		for ( i = 0; i < ICAL_BY_DAY_SIZE; i++ )
		{
			item_rrule->by_day[i] = ical_rrule->by_day[i];
		}

		for ( i = 0; i < ICAL_BY_MONTHDAY_SIZE; i++ )
		{
			item_rrule->by_month_day[i] = ical_rrule->by_month_day[i];
		}

		for ( i = 0; i < ICAL_BY_YEARDAY_SIZE; i++ )
		{
			item_rrule->by_year_day[i] = ical_rrule->by_year_day[i];
		}

		for ( i = 0; i < ICAL_BY_WEEKNO_SIZE; i++ )
		{
			item_rrule->by_week_no[i] = ical_rrule->by_week_no[i];
		}

		for ( i = 0; i < ICAL_BY_MONTH_SIZE; i++ )
		{
			item_rrule->by_month[i] = ical_rrule->by_month[i];
		}

		for ( i = 0; i < ICAL_BY_SETPOS_SIZE; i++ )
		{
			item_rrule->by_set_pos[i] = ical_rrule->by_set_pos[i];
		}

		switch ( ical_rrule->week_start )
		{
			case ICAL_NO_WEEKDAY:
			{
				item_rrule->week_start = E_ZIMBRA_ITEM_RECURRENCE_WEEKDAY_NONE;
			}
			break;

			case ICAL_SUNDAY_WEEKDAY:
			{
				item_rrule->week_start = E_ZIMBRA_ITEM_RECURRENCE_WEEKDAY_SUNDAY;
			}
			break;

			case ICAL_MONDAY_WEEKDAY:
			{
				item_rrule->week_start = E_ZIMBRA_ITEM_RECURRENCE_WEEKDAY_MONDAY;
			}
			break;

			case ICAL_TUESDAY_WEEKDAY:
			{
				item_rrule->week_start = E_ZIMBRA_ITEM_RECURRENCE_WEEKDAY_TUESDAY;
			}
			break;

			case ICAL_WEDNESDAY_WEEKDAY:
			{
				item_rrule->week_start = E_ZIMBRA_ITEM_RECURRENCE_WEEKDAY_WEDNESDAY;
			}
			break;

			case ICAL_THURSDAY_WEEKDAY:
			{
				item_rrule->week_start = E_ZIMBRA_ITEM_RECURRENCE_WEEKDAY_THURSDAY;
			}
			break;

			case ICAL_FRIDAY_WEEKDAY:
			{
				item_rrule->week_start = E_ZIMBRA_ITEM_RECURRENCE_WEEKDAY_FRIDAY;
			}
			break;

			case ICAL_SATURDAY_WEEKDAY:
			{
				item_rrule->week_start = E_ZIMBRA_ITEM_RECURRENCE_WEEKDAY_SATURDAY;
			}
			break;
		}

		e_zimbra_item_set_rrule( item, item_rrule );

		// Exception List

		if ( e_cal_component_has_exdates( comp ) )
		{
			GSList					*	l					=	NULL;
			GSList					*	item_exdate_list	=	NULL;
			icaltimezone			*	default_zone		=	NULL;
			icaltimezone			*	utc					=	NULL;
			struct icaltimetype			itt_utc;
			
			e_cal_component_get_exdate_list (comp, &exdate_list);
			default_zone = e_cal_backend_zimbra_get_default_zone (cbz);
			utc = icaltimezone_get_utc_timezone ();

fprintf( stderr, "********************* component has %d exdates\n", g_slist_length( exdate_list ) );

			for ( l = exdate_list; l ; l = l->next )
			{
				ECalComponentDateTime * dt = (ECalComponentDateTime *) l->data; 

				if (dt->value)
				{
					icaltimetype tt;

					tt = *dt->value;

					if ( !icaltime_get_timezone( tt ) )
					{
						icaltime_set_timezone( &tt, default_zone ? default_zone : utc);
					}

					itt_utc = icaltime_convert_to_zone( tt, utc );
					item_exdate_list = g_slist_append( item_exdate_list, g_strdup( icaltime_as_ical_string( itt_utc ) ) );
				}
			}

			e_zimbra_item_set_exdate_list (item, item_exdate_list);
			e_cal_component_free_exdate_list (exdate_list);
		}
	} 
}


static EZimbraItem*
set_properties_from_cal_component
	(
	EZimbraItem			*	item,
	ECalComponent		*	comp,
	ECalBackendZimbra	*	cbz
	)
{
	const char					*	uid;
	const char					*	location;
	const char					*	free_busy_status;
	ECalComponentDateTime			dt;
	ECalComponentClassification		classif;
	ECalComponentTransparency		transp;
	ECalComponentRange				rid;
	ECalComponentText				text;
	GSList						*	slist;
	GSList						*	sl;
	icaltimezone				*	default_zone;
	icaltimezone				*	utc;
	icalproperty_status				ical_status;
	
	default_zone = e_cal_backend_zimbra_get_default_zone (cbz);
	utc = icaltimezone_get_utc_timezone ();

	/* first set specific properties */

	switch (e_cal_component_get_vtype (comp))
	{
		case E_CAL_COMPONENT_EVENT:
		{
			e_zimbra_item_set_item_type( item, E_ZIMBRA_ITEM_TYPE_APPOINTMENT );

			// Free/Busy Status

			if ( ( free_busy_status = e_cal_component_get_x_data( comp, ZIMBRA_X_FB_ID ) ) != NULL )
			{
				e_zimbra_item_set_free_busy_status( item, free_busy_status );
			}

			// Transparency

			e_cal_component_get_transparency( comp, &transp );

			switch ( transp )
			{
				case E_CAL_COMPONENT_TRANSP_OPAQUE:
				{
					e_zimbra_item_set_transparency( item, E_ZIMBRA_ITEM_TRANSPARENCY_OPAQUE );
				}
				break;

				case E_CAL_COMPONENT_TRANSP_TRANSPARENT:
				{
					e_zimbra_item_set_transparency( item, E_ZIMBRA_ITEM_TRANSPARENCY_TRANSPARENT );
				}
				break;
			}

			// Location

			e_cal_component_get_location (comp, &location);
			e_zimbra_item_set_place( item, location );

			// Alarms

			if ( e_cal_component_has_alarms( comp ) )
			{
				ECalComponentAlarm *alarm;
				ECalComponentAlarmTrigger trigger;
				int duration;
				GList *l = e_cal_component_get_alarm_uids (comp);

				alarm = e_cal_component_get_alarm (comp, l->data);
				e_cal_component_alarm_get_trigger (alarm, &trigger);
				duration = abs (icaldurationtype_as_int (trigger.u.rel_duration));
				e_zimbra_item_set_trigger (item, duration);
			}
		
			// End date

			e_cal_component_get_dtend( comp, &dt );

			if ( dt.value )
			{
				if ( dt.tzid )
				{
					icaltime_set_timezone( dt.value, e_cal_backend_zimbra_get_zone( cbz, dt.tzid ) );
				}
				else
				{
					icaltime_set_timezone( dt.value, e_cal_backend_zimbra_get_default_zone( cbz ) );
				}

				e_zimbra_item_set_end_date( item, dt.value );
			}
		}
		break;

		default:
		{
			g_object_unref( item );
			return NULL;
		}
		break;
	}

	// Set common properties

	e_zimbra_item_set_id( item, e_cal_component_get_x_data( comp, ZIMBRA_X_APPT_ID ) );
	
	// UID

	e_cal_component_get_uid( comp, &uid );
	e_zimbra_item_set_icalid( item, uid );

	// Subject

	e_cal_component_get_summary( comp, &text );
	e_zimbra_item_set_subject( item, text.value );

	// Description

	e_cal_component_get_description_list( comp, &slist );

	if ( slist )
	{
		GString * str = g_string_new( "" );

		for (sl = slist; sl != NULL; sl = sl->next)
		{
			ECalComponentText *pt = sl->data;

			if (pt && pt->value)
			{
				str = g_string_append (str, pt->value);
			}
		}

		e_zimbra_item_set_message( item, (const char *) str->str );

		g_string_free( str, TRUE );
		e_cal_component_free_text_list( slist );
	}

	// Start date

	e_cal_component_get_dtstart( comp, &dt );

	if ( dt.value )
	{
		if ( dt.tzid )
		{
			icaltime_set_timezone( dt.value, e_cal_backend_zimbra_get_zone( cbz, dt.tzid ) );
		}
		else
		{
			icaltime_set_timezone( dt.value, e_cal_backend_zimbra_get_default_zone( cbz ) );
		}

		e_zimbra_item_set_start_date( item, dt.value );
	}
	else if (e_zimbra_item_get_item_type (item) == E_ZIMBRA_ITEM_TYPE_APPOINTMENT)
	{
		// appointments need the start date property

		g_object_unref (item);
		return NULL;
	}
	
	// All day event

	if ( icaltime_is_date( *dt.value ) && e_zimbra_item_get_item_type (item) == E_ZIMBRA_ITEM_TYPE_APPOINTMENT )
	{
		e_zimbra_item_set_is_allday_event( item, TRUE );
	}
	
	// Status

	e_cal_component_get_status( comp, &ical_status );

	switch ( ical_status )
	{
 		case ICAL_STATUS_TENTATIVE:
		{
			e_zimbra_item_set_status( item, E_ZIMBRA_ITEM_STAT_TENTATIVE );
		}
		break;

    	case ICAL_STATUS_CONFIRMED:
		{
			e_zimbra_item_set_status( item, E_ZIMBRA_ITEM_STAT_CONFIRMED );
		}
		break;

    	case ICAL_STATUS_COMPLETED:
		{
			e_zimbra_item_set_status( item, E_ZIMBRA_ITEM_STAT_COMPLETED );
		}
		break;

    	case ICAL_STATUS_NEEDSACTION:
		{
			e_zimbra_item_set_status( item, E_ZIMBRA_ITEM_STAT_NEEDSACTION );
		}
		break;

    	case ICAL_STATUS_CANCELLED:
		{
			e_zimbra_item_set_status( item, E_ZIMBRA_ITEM_STAT_CANCELLED );
		}
		break;

    	case ICAL_STATUS_INPROCESS:
		{
			e_zimbra_item_set_status( item, E_ZIMBRA_ITEM_STAT_INPROCESS );
		}
		break;

    	case ICAL_STATUS_DRAFT:
		{
			e_zimbra_item_set_status( item, E_ZIMBRA_ITEM_STAT_DRAFT );
		}
		break;

    	case ICAL_STATUS_FINAL:
		{
			e_zimbra_item_set_status( item, E_ZIMBRA_ITEM_STAT_FINAL );
		}
		break;

    	default:
		{
			e_zimbra_item_set_status( item, E_ZIMBRA_ITEM_STAT_NONE );
		}
		break;
	}

	// classification

	e_cal_component_get_classification (comp, &classif);

	switch (classif)
	{
		case E_CAL_COMPONENT_CLASS_PUBLIC:
		{
			e_zimbra_item_set_classification (item, E_ZIMBRA_ITEM_CLASSIFICATION_PUBLIC);
		}
		break;

		case E_CAL_COMPONENT_CLASS_PRIVATE:
		{
			e_zimbra_item_set_classification (item, E_ZIMBRA_ITEM_CLASSIFICATION_PRIVATE);
		}
		break;

		case E_CAL_COMPONENT_CLASS_CONFIDENTIAL:
		{
			e_zimbra_item_set_classification (item, E_ZIMBRA_ITEM_CLASSIFICATION_CONFIDENTIAL);
		}
		break;

		default :
		{
			e_zimbra_item_set_classification (item, NULL);
		}
		break;
	}

	set_attendees_to_item( item, comp, default_zone, FALSE, e_cal_backend_zimbra_peek_account( cbz ) );
	
	// Recurrences.  Evolution is kinda lame in that it will sometimes have a component that has both
	// a recurrence id and rrule. Let's try to clean that up a little bit.

	e_cal_component_get_recurid( comp, &rid );

	if ( rid.datetime.value )
	{
fprintf( stderr, "****** recurrence id tzid = %s\n", rid.datetime.tzid );

		if ( rid.datetime.tzid )
		{
			icaltime_set_timezone( rid.datetime.value, e_cal_backend_zimbra_get_zone( cbz, rid.datetime.tzid ) );
		}

		e_zimbra_item_set_rid( item, rid.datetime.value );
	}
	else if (e_cal_component_has_recurrences( comp ) )
	{
		if ( e_cal_component_has_rrules( comp ) )
		{
			set_rrule_from_comp( cbz, comp, item );
		}
	}

	// Attachments

	if (e_cal_component_has_attachments( comp ) )
	{
		e_cal_backend_zimbra_set_attachments_from_comp( comp, item );
	}

	return item;
}


EZimbraItem*
e_zimbra_item_new_from_cal_component
	(
	const char			*	folder_id,
	ECalBackendZimbra	*	cbz,
	ECalComponent		*	comp
	)
{
	EZimbraItem *item;

	g_return_val_if_fail (E_IS_CAL_COMPONENT (comp), NULL);

	item = e_zimbra_item_new_empty ();
	e_zimbra_item_set_folder_id( item, folder_id );
	
	return set_properties_from_cal_component (item, comp, cbz);
}


EZimbraItem*
e_zimbra_item_new_from_cal_components
	(
	const char			*	folder_id,
	ECalBackendZimbra	*	cbz,
	GSList				*	components
	)
{
	ECalComponent	*	comp			=	NULL;
	GSList			*	l				=	NULL;
	EZimbraItem 	*	main_item		=	NULL;
	GSList			*	detached_comps	=	NULL;
	gboolean			ok				=	TRUE;

	if ( g_slist_length( components ) > 1 )
	{
		// The components might be in any old order...so find the main invite first, and 
		// put the rest of the components in their own list

		for ( l = components; l; l = l->next )
		{
			ECalComponentRange rid;

			comp = E_CAL_COMPONENT( l->data );
			zimbra_check( E_IS_CAL_COMPONENT( comp ), exit, ok = FALSE );

			// We're just gonna try and find a component that doesn't have a recurrence-id

			e_cal_component_get_recurid( comp, &rid );

			if ( !rid.datetime.value )
			{
				// Bingo

				zimbra_check( !main_item, exit, g_warning( "more than one main component?" ); ok = FALSE );

				main_item = e_zimbra_item_new_from_cal_component( folder_id, cbz, comp );
				zimbra_check( main_item, exit, ok = FALSE );
			}
			else
			{
				detached_comps = g_slist_append( detached_comps, comp );
			}
		}

		zimbra_check( main_item, exit, g_warning( "no main component?" ); ok = FALSE );

		// Now go through the detached items
					
		for ( l = detached_comps; l; l = l->next )
		{
			EZimbraItem * detached_item;

			comp = E_CAL_COMPONENT( l->data );

			detached_item = e_zimbra_item_new_from_cal_component( folder_id, cbz, comp );
			zimbra_check( detached_item, exit, ok = FALSE );
	
			e_zimbra_item_add_detached_item( main_item, detached_item );
		}
	}
	else
	{
		comp = E_CAL_COMPONENT( components->data );
		zimbra_check( comp, exit, ok = FALSE );

		main_item = e_zimbra_item_new_from_cal_component( folder_id, cbz, comp );
		zimbra_check( main_item, exit, ok = FALSE );
	}


exit:

	if ( detached_comps )
	{
		g_slist_free( detached_comps );
	}

	if ( !ok )
	{
		g_object_unref( main_item );
		main_item = NULL;
	}

	return main_item;
}


static void
set_attachments_to_cal_component (EZimbraItem *item, ECalComponent *comp, ECalBackendZimbra *cbz)
{
#if 0
	GSList *fetch_list = NULL, *l;
	GSList *comp_attachment_list = NULL;
	const char *uid;
	char *attach_file_url;
	
	fetch_list = e_zimbra_item_get_attach_id_list (item);
	if (fetch_list == NULL)
		return; /* No attachments exist */

	e_cal_component_get_uid (comp, &uid);
	for (l = fetch_list; l ; l = l->next) {
		int fd;
		EZimbraItemAttachment *attach_item;
		char *attach_data = NULL;
		struct stat st;
		char *filename;

		attach_item = (EZimbraItemAttachment *) l->data;
		attach_file_url = g_strconcat (e_cal_backend_zimbra_get_local_attachments_store (cbz), 
			 "/", uid, "-", attach_item->name, NULL);

		filename = g_filename_from_uri (attach_file_url, NULL, NULL);
		if (g_stat (filename, &st) == -1) {
			if (!get_attach_data_from_server (attach_item, cbz)) {
				g_free (filename);
				return; /* Could not get the attachment from the server */
			}
			fd = g_open (filename, O_RDWR|O_CREAT|O_TRUNC|O_BINARY, 0600);
			if (fd == -1) { 
				/* skip gracefully */
				g_warning ("DEBUG: could not serialize attachments\n");
			} else if (write (fd, attach_item->data, attach_item->size) == -1) {
				/* skip gracefully */
				g_warning ("DEBUG: attachment write failed.\n");
			}
			g_free (attach_data);
			close (fd);
		}
		g_free (filename);

		comp_attachment_list = g_slist_append (comp_attachment_list, attach_file_url);
	}

	e_cal_component_set_attachment_list (comp, comp_attachment_list);
#endif
}


ECalComponent*
e_zimbra_item_to_cal_component
	(
	EZimbraItem			*	item,
	EZimbraItem			*	parent,
	ECalBackendZimbra	*	cbz
	)
{
	ECalComponent				*	comp				=	NULL;
	ECalComponentText				text;
	ECalComponentDateTime			dt;
	const char					*	description			=	NULL;
	const char					*	uid					=	NULL;
	const char					*	free_busy_status	=	NULL;
	gboolean						is_allday;
	const icaltimetype			*	itt;
	const icaltimetype			*	rid;
	int								alarm_duration;
	GSList						*	recipient_list		=	NULL;
	GSList						*	rl					=	NULL;
	GSList						*	attendee_list 		=	NULL;
	EZimbraItemOrganizer		*	organizer			=	NULL;
	EZimbraItemType					item_type;
	gboolean						ok					=	TRUE;

	g_return_val_if_fail( E_IS_ZIMBRA_ITEM (item), NULL );

	comp = e_cal_component_new ();
	zimbra_check( comp, exit, ok = FALSE );

	item_type = e_zimbra_item_get_item_type( item );
	zimbra_check( ( item_type == E_ZIMBRA_ITEM_TYPE_APPOINTMENT ) || ( item_type == E_ZIMBRA_ITEM_TYPE_TASK ), exit, ok = FALSE );

	if ( item_type == E_ZIMBRA_ITEM_TYPE_APPOINTMENT )
	{
		e_cal_component_set_new_vtype( comp, E_CAL_COMPONENT_EVENT );
	}
	else
	{
		e_cal_component_set_new_vtype( comp, E_CAL_COMPONENT_TODO );
	}

	// Set common properties - Zimbra Server ID

	if ( ( description = e_zimbra_item_get_id( item ) ) != NULL )
	{
		icalproperty * icalprop;

		icalprop = icalproperty_new_x( description );
		icalproperty_set_x_name( icalprop, ZIMBRA_X_APPT_ID );
		icalcomponent_add_property( e_cal_component_get_icalcomponent( comp ), icalprop );
	}

	// Summary

	text.value	= e_zimbra_item_get_subject( item );
	text.altrep = NULL;
	e_cal_component_set_summary (comp, &text);

	// Description

	if ( ( description = e_zimbra_item_get_message( item ) ) )
	{
		GSList l;

		text.value	= description;
		text.altrep	= NULL;
		l.data		= &text;
		l.next		= NULL;

		e_cal_component_set_description_list (comp, &l);
	}

	// Status

	switch ( e_zimbra_item_get_status( item ) )
	{
		case E_ZIMBRA_ITEM_STAT_TENTATIVE:
		{
 			e_cal_component_set_status( comp, ICAL_STATUS_TENTATIVE );
		}
		break;

		case E_ZIMBRA_ITEM_STAT_CONFIRMED:
		{
			e_cal_component_set_status( comp, ICAL_STATUS_CONFIRMED );
		}
		break;

		case E_ZIMBRA_ITEM_STAT_COMPLETED:
		{
			e_cal_component_set_status( comp, ICAL_STATUS_COMPLETED );
		}
		break;

		case E_ZIMBRA_ITEM_STAT_NEEDSACTION:
		{
			e_cal_component_set_status( comp, ICAL_STATUS_NEEDSACTION );
		}
		break;

		case E_ZIMBRA_ITEM_STAT_CANCELLED:
		{
			e_cal_component_set_status( comp, ICAL_STATUS_CANCELLED );
		}
		break;

		case E_ZIMBRA_ITEM_STAT_INPROCESS:
		{
			e_cal_component_set_status( comp, ICAL_STATUS_INPROCESS );
		}
		break;

		case E_ZIMBRA_ITEM_STAT_DRAFT:
		{
			e_cal_component_set_status( comp, ICAL_STATUS_DRAFT );
		}
		break;

		case E_ZIMBRA_ITEM_STAT_FINAL:
		{
			e_cal_component_set_status( comp, ICAL_STATUS_FINAL );
		}
		break;

		case E_ZIMBRA_ITEM_STAT_NONE:
		{
			e_cal_component_set_status( comp, ICAL_STATUS_NONE );
		}
		break;
	}

	// All day event

	is_allday = e_zimbra_item_get_is_allday_event( item );

	// Start date

	itt = e_zimbra_item_get_start_date( item );
	zimbra_check( itt, exit, ok = FALSE );

	dt.value = g_new( struct icaltimetype, 1 );
	zimbra_check( dt.value, exit, ok = FALSE );

	*dt.value	=	*itt;
	dt.tzid		=	NULL;

	if ( !is_allday )
	{
		// We want to convert every time to our default timezone

		icaltime_set_timezone( dt.value, e_cal_backend_zimbra_get_default_zone( cbz ) );

		icaltimezone_convert_time( dt.value, ( icaltimezone* ) itt->zone, ( icaltimezone* ) dt.value->zone ); 

		dt.tzid = g_strdup( icaltime_get_tzid( *dt.value ) );
	}
	else
	{
		dt.value->is_date = 1;
	}

	e_cal_component_set_dtstart( comp, &dt );
	e_cal_component_free_datetime( &dt );
	
	// UID 

	uid = e_zimbra_item_get_icalid (item);
	zimbra_check( uid, exit, ok = FALSE );

	e_cal_component_set_uid( comp, e_zimbra_item_get_icalid( item ) );

	// Classification (we're gonna punt on this one )

	e_cal_component_set_classification( comp, E_CAL_COMPONENT_CLASS_PRIVATE );

	// Attendees

	if ( ( recipient_list = e_zimbra_item_get_recipient_list( item ) ) != NULL )
	{
		for ( rl = recipient_list; rl != NULL; rl = rl->next )
		{
			EZimbraItemRecipient	*	recipient = ( EZimbraItemRecipient* ) rl->data;
			ECalComponentAttendee	*	attendee;

			attendee = g_new0( ECalComponentAttendee, 1 );

			attendee->cn	= g_strdup (recipient->display_name);
			attendee->value = g_strconcat("MAILTO:", recipient->email, NULL);

			switch ( recipient->role )
			{
				case E_ZIMBRA_ITEM_ROLE_CHAIR:
				{
					attendee->role = ICAL_ROLE_CHAIR;
				}
				break;

				case E_ZIMBRA_ITEM_ROLE_REQUIRED_PARTICIPANT:
				{
					attendee->role = ICAL_ROLE_REQPARTICIPANT;
				}
				break;

				case E_ZIMBRA_ITEM_ROLE_OPTIONAL_PARTICIPANT:
				{
					attendee->role = ICAL_ROLE_OPTPARTICIPANT;
				}
				break;

				case E_ZIMBRA_ITEM_ROLE_NON_PARTICIPANT:
				{
					attendee->role = ICAL_ROLE_NONE;
				}
				break;
			}
		
			attendee->cutype = ICAL_CUTYPE_INDIVIDUAL;

			switch ( recipient->status )
			{
				case E_ZIMBRA_ITEM_PART_STAT_ACCEPTED:
				{
					attendee->status = ICAL_PARTSTAT_ACCEPTED;
				}
				break;

				case E_ZIMBRA_ITEM_PART_STAT_DECLINED:
				{
					attendee->status = ICAL_PARTSTAT_DECLINED;
				}
				break;

				case E_ZIMBRA_ITEM_PART_STAT_TENTATIVE:
				{
					attendee->status = ICAL_PARTSTAT_TENTATIVE;
				}
				break;

				case E_ZIMBRA_ITEM_PART_STAT_DELEGATED:
				{
					attendee->status = ICAL_PARTSTAT_DELEGATED;
				}
				break;

				case E_ZIMBRA_ITEM_PART_STAT_COMPLETED:
				{
					attendee->status = ICAL_PARTSTAT_COMPLETED;
				}
				break;

				case E_ZIMBRA_ITEM_PART_STAT_INPROCESS:
				{
					attendee->status = ICAL_PARTSTAT_INPROCESS;
				}
				break;

				default:
				{
					attendee->status = ICAL_PARTSTAT_NEEDSACTION;
				}
				break;
			}
			 
			attendee_list = g_slist_append (attendee_list, attendee);				
		}

		e_cal_component_set_attendee_list (comp, attendee_list);
	}

	// Organizer

	if ( ( organizer = e_zimbra_item_get_organizer( item ) ) != NULL )
	{
		ECalComponentOrganizer	cal_organizer;
		char					value[ 256 ];

		memset( &cal_organizer, 0, sizeof( cal_organizer ) );
		cal_organizer.cn		= organizer->display_name;
		sprintf( value, "MAILTO:%s", organizer->email );
		cal_organizer.value	= value;

		e_cal_component_set_organizer( comp, &cal_organizer );
	}

	// Attachments

	set_attachments_to_cal_component (item, comp, cbz);

	// Set specific properties

	switch ( item_type )
	{
		case E_ZIMBRA_ITEM_TYPE_APPOINTMENT:
		{
			// Free/Busy Status

			if ( free_busy_status = e_zimbra_item_get_free_busy_status( item ) )
			{
				icalproperty * icalprop;

				icalprop = icalproperty_new_x( free_busy_status );
				icalproperty_set_x_name( icalprop, ZIMBRA_X_FB_ID );
				icalcomponent_add_property( e_cal_component_get_icalcomponent( comp ), icalprop );
			}

			// Transparency

			switch ( e_zimbra_item_get_transparency( item ) )
			{
				case E_ZIMBRA_ITEM_TRANSPARENCY_TRANSPARENT:
				{
					e_cal_component_set_transparency( comp, E_CAL_COMPONENT_TRANSP_TRANSPARENT );
				}
				break;
		
				case E_ZIMBRA_ITEM_TRANSPARENCY_OPAQUE:
				{
					e_cal_component_set_transparency( comp, E_CAL_COMPONENT_TRANSP_OPAQUE );
				}
				break;
			}

			// Location

			e_cal_component_set_location (comp, e_zimbra_item_get_place (item));

			// End date

			if ( ( itt = e_zimbra_item_get_end_date( item ) ) != NULL )
			{
				dt.value = g_new( struct icaltimetype, 1 );
				zimbra_check( dt.value, exit, ok = FALSE );

				*dt.value	=	*itt;
				dt.tzid		=	NULL;

				if ( !is_allday )
				{
					// We want to convert every time to our default timezone

					icaltime_set_timezone( dt.value, e_cal_backend_zimbra_get_default_zone( cbz ) );

					icaltimezone_convert_time( dt.value, ( icaltimezone* ) itt->zone, ( icaltimezone* ) dt.value->zone ); 

					dt.tzid	= g_strdup( icaltime_get_tzid( *dt.value ) );
				}
				else
				{
					dt.value->is_date = 1;
				}

				e_cal_component_set_dtend( comp, &dt );
				e_cal_component_free_datetime( &dt );
			}

			// Alarms

			alarm_duration = 0 - e_zimbra_item_get_trigger (item);

			if (alarm_duration != 0)
			{
				ECalComponentAlarm *alarm;
				ECalComponentAlarmTrigger trigger;
			
				alarm = e_cal_component_alarm_new ();
				e_cal_component_alarm_set_action (alarm, E_CAL_COMPONENT_ALARM_DISPLAY);
				trigger.type = E_CAL_COMPONENT_ALARM_TRIGGER_RELATIVE_START;
				trigger.u.rel_duration = icaldurationtype_from_int (alarm_duration);
				e_cal_component_alarm_set_trigger (alarm, trigger);
				e_cal_component_add_alarm (comp, alarm);
			}

			// Recurrence rules

			if ( e_zimbra_item_get_rrule( item ) )
			{
				set_rrule_from_item( cbz, item, comp );
			}

			// Recurrence ID

			if ( ( rid = e_zimbra_item_get_rid( item ) ) != NULL )
			{
				ECalComponentRange range;

				range.type				=	E_CAL_COMPONENT_RANGE_SINGLE;
				range.datetime.value	=	g_new( struct icaltimetype, 1 );
				zimbra_check( range.datetime.value, exit, ok = FALSE );

				*range.datetime.value	=	*rid;
				range.datetime.tzid		=	NULL;

				if ( !e_zimbra_item_get_is_allday_event( parent ) )
				{
					// We want to convert every time to our default timezone

					icaltime_set_timezone( range.datetime.value, e_cal_backend_zimbra_get_default_zone( cbz ) );

					icaltimezone_convert_time( range.datetime.value, ( icaltimezone* ) rid->zone, ( icaltimezone* ) range.datetime.value->zone ); 

					range.datetime.tzid = g_strdup( icaltime_get_tzid( *range.datetime.value ) );
				}
				else
				{
					range.datetime.value->is_date = 1;
				}

				e_cal_component_set_recurid( comp, &range );
				e_cal_component_free_range( &range );
			}
		}
		break;

		case E_ZIMBRA_ITEM_TYPE_TASK:
		{
		}
		break;

		default:
		{
			zimbra_check( 0, exit, g_warning( "unknown item type!" ); ok = FALSE );
		}
		break;
	}

	e_cal_component_commit_sequence( comp );

exit:

	if ( !ok && comp )
	{
		g_object_unref( comp );
		comp = NULL;
	}

	return comp;
}


GSList*
e_zimbra_item_to_cal_components
	(
	EZimbraItem			*	item,
	ECalBackendZimbra	*	cbz
	)
{
	ECalComponent	*	main_comp		=	NULL;
	GSList			*	item_list		=	NULL;
	GSList			*	exdate_list		=	NULL;
	GList			*	l				=	NULL;
	gboolean			ok				=	TRUE;

	main_comp = e_zimbra_item_to_cal_component( item, NULL, cbz );
	zimbra_check( main_comp, exit, ok = FALSE );

	item_list = g_slist_append( item_list, main_comp );

	for ( l = e_zimbra_item_peek_detached_items( item ); l; l = g_list_next( l ) )
	{
		EZimbraItem * detached_item = E_ZIMBRA_ITEM( l->data );

		// Check to make sure that status isn't cancelled

		if ( e_zimbra_item_get_status( detached_item ) != E_ZIMBRA_ITEM_STAT_CANCELLED )
		{
			ECalComponent * detached_comp = NULL;

			detached_comp = e_zimbra_item_to_cal_component( detached_item, item, cbz );
			zimbra_check( detached_comp, exit, ok = FALSE );

			item_list = g_slist_append( item_list, detached_comp );
		}
		else
		{
			ECalComponentDateTime	*	dt;
			const icaltimetype		*	rid;

			// We're gonna add all cancelled items to our exclude list

			dt = g_new( ECalComponentDateTime, 1 );
			zimbra_check( dt, exit, ok = FALSE );

			dt->value = g_new( struct icaltimetype, 1 );
			zimbra_check( dt->value, exit, ok = FALSE );

			rid = e_zimbra_item_get_rid( detached_item );

			*dt->value	=	*rid;
			dt->tzid	=	NULL;

			if ( !icaltime_is_date( *dt->value ) )
			{
				// We want to convert every time to our default timezone

				icaltime_set_timezone( dt->value, e_cal_backend_zimbra_get_default_zone( cbz ) );

				icaltimezone_convert_time( dt->value, ( icaltimezone* ) rid->zone, ( icaltimezone* ) dt->value->zone ); 

				dt->tzid = g_strdup( icaltime_get_tzid( *dt->value ) );
			}

			exdate_list = g_slist_append( exdate_list, dt );
		}
	}

fprintf( stderr, "e_zimbra_item_to_cal_components: item count = %d, exdate count = %d\n", g_slist_length( item_list ), g_slist_length( exdate_list  ) );

	if ( exdate_list )
	{
		e_cal_component_set_exdate_list( main_comp, exdate_list );
		e_cal_component_commit_sequence( main_comp );
	}

exit:

	if ( exdate_list )
	{
		e_cal_component_free_exdate_list( exdate_list );
	}

	if ( !ok )
	{
		g_slist_foreach( item_list, ( GFunc ) g_object_unref, NULL );
		g_slist_free( item_list );
		item_list = NULL;
	}

	return item_list;
}


EZimbraConnectionStatus
e_zimbra_connection_get_freebusy_info
	(
	EZimbraConnection	*	cnc,
	GList				*	users,
	time_t					start,
	time_t					end,
	GList				**	freebusy
	)
{
	unsigned				i;
	EZimbraConnectionStatus	err = 0;

	zimbra_check( cnc, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	for ( i = 0; i < g_list_length( users ); i++ )
	{
		icalcomponent	*	icalparent	= NULL;
		icalcomponent	*	icalinner	= NULL;
		ECalComponent	*	comp		= NULL;
		const char		*	user		= NULL;
		char			*	page		= NULL;
		char				url[ 512 ];

		user = g_list_nth_data( users, i );

		GLOG_DEBUG( "user = %s", user );

		snprintf( url, sizeof( url ), "%s://%s:%d/service/pubcal/freebusy.ifb?acct=%s", e_zimbra_connection_use_ssl( cnc ) ? "https" : "http", e_zimbra_connection_get_hostname( cnc ), e_zimbra_connection_get_port( cnc ), user );

		err = e_zimbra_connection_get_page( cnc, url, &page );

		if ( err != E_ZIMBRA_CONNECTION_STATUS_OK )
		{
			GLOG_ERROR( "unable to get free/busy info for user: %s", user );
			continue;
		}

		icalparent = icalparser_parse_string( page );

		if ( !icalparent )
		{
			GLOG_ERROR( "unable to parse string: %s", page );
			free( page );
			continue;
		}

		icalinner = icalcomponent_get_inner( icalparent );

		if ( !icalinner )
		{
			GLOG_ERROR( "unable to get inner component: %s", page );
			icalcomponent_free( icalparent );
			free( page );
			continue;
		}

		comp = e_cal_component_new();

		if ( !comp )
		{
			GLOG_ERROR( "unable to create ECalComponent" );
			icalcomponent_free( icalparent );
			free( page );
			continue;
		}

		if ( !e_cal_component_set_icalcomponent( comp, icalinner ) )
		{
			GLOG_ERROR( "e_cal_component_set_icalcomponent failed" );
			icalcomponent_free( icalparent );
			g_object_unref( comp );
			free( page );
			continue;
		}

		e_cal_component_commit_sequence( comp );

		*freebusy = g_list_append( *freebusy, e_cal_component_get_as_string( comp ) );

		g_object_unref( comp );
	}

	err = E_ZIMBRA_CONNECTION_STATUS_OK;

exit:

	return err;
}
