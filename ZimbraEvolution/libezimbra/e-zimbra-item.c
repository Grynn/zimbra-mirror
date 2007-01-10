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
 * 
 * Copyright 2006, Zimbra, Inc.
 *
 */

#ifdef HAVE_CONFIG_H
#include <config.h>
#endif
#include <string.h>
#include <glib.h>
#include <glib/gprintf.h>
#include <glib/gstring.h>
#include <libical/icaltimezone.h>
#include "e-zimbra-item.h"
#include "e-zimbra-connection.h"
#include "e-zimbra-debug.h"
#include "e-zimbra-xml.h"
#include <glog/glog.h>

struct _EZimbraItemPrivate
{
	EZimbraItemType item_type;
	char * folder_id;
	GList *category_list; /*list of category ids*/

	// Properties

	char						*	id;
	char						*	rev;
	char						*	delivered_date;
	gboolean						all_day;
	icaltimetype				*	start_date;
	icaltimetype				*	end_date;
	icaltimetype				*	rid;
	gboolean						completed;
	char *subject;
	char *message;
	char *classification;
	char *accept_level;
	char *priority;
	char *task_priority;
	char * place;
	char *source ;
	GSList *recipient_list;
	GSList *recurrence_dates;
	GSList *exdate_list;
	EZimbraItemRecurrenceRule	*	rrule;
	int trigger; /* alarm */
	/*message size*/
	int size;    
	EZimbraItemOrganizer *organizer;

	/*properties for mail*/
	char *from ;
	char *to ;
	char *content_type ;
	char *msg_body_id;
	EZimbraItemStatus status;
	/*Attachments*/
	GSList *attach_list ; 
	/*linkInfo for replies*/
	EZimbraItemLinkInfo *link_info; 

	/* properties for tasks/calendars */
	char *icalid;
	/* if the self is not the organizer of the item, the 
	 * status is not reflected in the recipientStatus.
	 * Hence it should be gleaned from the 'status' element
	 * of the Mail, the parent item.*/
	GList	*	detached_items;
	/* properties for category items*/
	char *category_name;

	/* properties for contacts */
	FullName	*	full_name;
	GList		*	email_list;
	GList		*	im_list;
	GHashTable	*	simple_fields;
	GList		*	member_list;
	GHashTable	*	addresses;
	int				file_as;

	/***** Send Options *****/
	
	gboolean set_sendoptions;
	/* Reply Request */
	char *reply_within;
	gboolean reply_request_set;
	
	/* Status Tracking through sent Item */
	EZimbraItemTrack track_info;
	gboolean autodelete;

	/* Return Notification */
	EZimbraItemReturnNotify notify_completed;
	EZimbraItemReturnNotify notify_accepted;
	EZimbraItemReturnNotify notify_declined;
	EZimbraItemReturnNotify notify_opened;
	EZimbraItemReturnNotify notify_deleted;

	/* Expiration Date */
	char *expires;

	/* Delay delivery */
	char *delay_until;

	/*padding*/
	unsigned int padding[10];
};


static GObjectClass *	parent_class 	=	NULL;
static GHashTable	*	g_zones			=	NULL;
static char			*	days_of_week[]	=	{ "", "SU", "MO", "TU", "WE", "TH", "FR", "SA", };


static gboolean
icaltimezone_to_xmltimezone
	(
	const icaltimezone	*	tz,
	xmlTextWriterPtr		request
	);


static icaltimezone*
xmltimezone_to_icaltimezone
	(
	xmlNode	*	node
	);


static icaltimezone*
lookup_icaltimezone
	(
	const char * tzid
	);


static void
decode_by_day_value
	(
	short	s,
	int	*	day,
	int	*	ordwk
	)
{
	*day = abs( s ) % 8;

	if ( ( *day + 8 ) != s )
	{
		*ordwk = ( abs( s ) - *day ) / 8 * ( ( s < 0 ) ? -1 : 1 );
	}
	else
	{
		*ordwk = 0;
	}
}


static const char *
convert_integer_day_to_string_day
	(
	short day
	) 
{
	if ( ( day > 0 ) && ( day < 8 ) )
	{
		return days_of_week[ day ];
	}
	else
	{
		return "";
	}
}


static int
convert_string_day_to_integer_day
	(
	const char * val
	)
{
	int day = 0;

	if ( g_str_equal( val, "SU" ) )
	{
		day = 1;
	}
	else if ( g_str_equal( val, "MO" ) )
	{
		day = 2;
	}
	else if ( g_str_equal( val, "TU" ) )
	{
		day = 3;
	}
	else if ( g_str_equal( val, "WE" ) )
	{
		day = 4;
	}
	else if ( g_str_equal( val, "TH" ) )
	{
		day = 5;
	}
	else if ( g_str_equal( val, "FR" ) )
	{
		day = 6;
	}
	else if ( g_str_equal( val, "SA" ) )
	{
		day = 7;
	}
	else
	{
		day = 0;
	}

	return day;
}



static void
free_recipient (EZimbraItemRecipient *recipient, gpointer data)
{
	g_free (recipient->email);
	g_free (recipient->display_name);
	g_free (recipient->delivered_date);
	g_free (recipient->opened_date);
	g_free (recipient->accepted_date);
	g_free (recipient->deleted_date);
	g_free (recipient->declined_date);
	g_free (recipient->completed_date);
	g_free (recipient->undelivered_date);
	g_free (recipient);
}

static void 
free_postal_address (gpointer  postal_address)
{
	PostalAddress *address;
	address = (PostalAddress *) postal_address;
	if (address) {
		g_free (address->street_address);
		g_free (address->location);
		g_free(address->city);
		g_free(address->country);
		g_free(address->state);
		g_free(address->postal_code);
		g_free(address);
	}
}

static void 
free_full_name (gpointer full_name)
{
	FullName *name = (FullName *) full_name;

	g_free (name->name_prefix);
	g_free (name->first_name);
	g_free (name->middle_name);
	g_free (name->last_name);
	g_free (name->name_suffix);
	g_free (name);

}

static void 
free_string (gpointer s, gpointer data)
{
	if (s)
		free (s);
}

static void
free_attach (gpointer s, gpointer data) 
{
	EZimbraItemAttachment *attach = (EZimbraItemAttachment *) s ;
	if (attach) {
		if (attach->id) 
			g_free (attach->id), attach->id = NULL ;
		if (attach->name)
			g_free (attach->name), attach->name = NULL ;
		if (attach->contentid)
			g_free (attach->contentid), attach->contentid= NULL ;
		if (attach->contentType)
			g_free (attach->contentType), attach->contentType = NULL ;
		if (attach->date)
			g_free (attach->date), attach->date = NULL ;
		if (attach->data)
			g_free (attach->data), attach->data = NULL ;
	
		g_free(attach) ;
	}
	
}


static void 
free_member (gpointer member, gpointer data)
{
	EGroupMember *group_member = (EGroupMember *) member;
	if (group_member->id)
		g_free (group_member->id);
	if (group_member->email)
		g_free (group_member->email);
	if (group_member->name)
		g_free (group_member->name);
	g_free (group_member);
}

static void 
free_im_address ( gpointer address, gpointer data)
{
	IMAddress *im_address;
	im_address = (IMAddress *) address;
	
	if (im_address) {
		if (im_address->service)
			g_free (im_address->service);
		if (im_address->address)
			g_free (im_address->address);
		g_free (im_address);
	}
}

static void
free_link_info (EZimbraItemLinkInfo *info)
{
	if (info) {
		if (info->id )
			g_free (info->id), info->id = NULL;
		if (info->type)
			g_free (info->type), info->type = NULL;
		if (info->thread)
			g_free (info->thread), info->thread = NULL;
		g_free (info);
		info = NULL;
	}
}


static void
e_zimbra_item_dispose (GObject *object)
{
	EZimbraItem			*	item = (EZimbraItem *) object;
	EZimbraItemPrivate	*	priv;

	g_return_if_fail (E_IS_ZIMBRA_ITEM (item));

	if ( ( priv = item->priv ) != NULL )
	{
		if (priv->folder_id)
		{
			g_free( priv->folder_id );
			priv->folder_id = NULL;
		}

		if (priv->id)
		{
			g_free( priv->id );
			priv->id = NULL;
		}

		if (priv->rev)
		{
			g_free( priv->rev );
			priv->rev = NULL;
		}

		if ( priv->subject )
		{
			g_free( priv->subject );
			priv->subject = NULL;
		}

		if (priv->message)
		{
			g_free( priv->message );
			priv->message = NULL;
		}

		if ( priv->start_date )
		{
			g_free( priv->start_date );
			priv->start_date = NULL;
		}

		if ( priv->end_date )
		{
			g_free( priv->end_date );
			priv->end_date = NULL;
		}

		if ( priv->rid )
		{
			g_free( priv->rid );
			priv->rid = NULL;
		}

		if ( priv->classification )
		{
			g_free (priv->classification);
			priv->classification = NULL;
		}

		if ( priv->accept_level )
		{
			g_free (priv->accept_level);
			priv->accept_level = NULL;
		}

		if ( priv->priority )
		{
			g_free( priv->priority );
			priv->priority = NULL;
		}

		if ( priv->task_priority )
		{
			g_free( priv->task_priority );
			priv->task_priority = NULL;
		}
		
		if ( priv->place )
		{
			g_free( priv->place );
			priv->place = NULL;
		}

		if ( priv->from )
		{
			g_free( priv->from );
			priv->from = NULL ;
		}

		if ( priv->to )
		{
			g_free( priv->to );
			priv->to = NULL ;
		}
		
		if ( priv->content_type )
		{
			g_free( priv->content_type );
			priv->content_type = NULL ;
		}

		if ( priv->msg_body_id )
		{
			g_free( priv->msg_body_id );
			priv->msg_body_id = NULL;
		}

		if ( priv->icalid )
		{
			g_free( priv->icalid );
			priv->icalid = NULL;
		}

		if ( priv->reply_within )
		{
			g_free( priv->reply_within );
			priv->reply_within = NULL;
		}

		if ( priv->expires )
		{
			g_free( priv->expires );
			priv->expires = NULL;
		}

		if (priv->delay_until) {
			g_free (priv->delay_until);
			priv->delay_until = NULL;
		}

		if (priv->recipient_list)
		{
			g_slist_foreach( priv->recipient_list, (GFunc) free_recipient, NULL );
			g_slist_free( priv->recipient_list );
			priv->recipient_list = NULL;
		}	
		
		if ( priv->organizer )
		{
			g_free( priv->organizer->display_name );
			g_free( priv->organizer->email );
			priv->organizer = NULL;
		}
		
		if ( priv->recurrence_dates )
		{
			g_slist_foreach( priv->recurrence_dates, free_string, NULL );
			g_slist_free( priv->recurrence_dates );
			priv->recurrence_dates = NULL;
		}

		if (priv->exdate_list)
		{
			g_slist_foreach( priv->exdate_list, free_string, NULL );
			g_slist_free( priv->exdate_list );
			priv->exdate_list = NULL;
		}

		if ( priv->rrule )
		{
			g_free( priv->rrule );
			priv->rrule = NULL;
		}

		if ( priv->full_name )
		{
			free_full_name( priv->full_name );
			priv->full_name = NULL;
		}
		
		if ( priv->simple_fields )
		{
			g_hash_table_destroy (priv->simple_fields);
			priv->simple_fields = NULL;
		}
		
		if (priv->addresses)
		{
			g_hash_table_destroy( priv->addresses );
			priv->addresses = NULL;
		}
		
		if ( priv->detached_items )
		{
			g_list_foreach( priv->detached_items, ( GFunc ) g_object_unref, NULL );
			g_list_free( priv->detached_items );
			priv->detached_items = NULL;
		}

		if (priv->email_list)
		{
			g_list_foreach( priv->email_list,  free_string , NULL );
			g_list_free( priv->email_list );
			priv->email_list = NULL;
		}
		
		if (priv->member_list)
		{
			g_list_foreach( priv->member_list,  free_member, NULL );
			g_list_free( priv->member_list );
			priv->member_list = NULL;
		}

		if ( priv->im_list )
		{
			g_list_foreach( priv->im_list, free_im_address, NULL );
			g_list_free( priv->im_list );
			priv->im_list = NULL;
		}
		
		if ( priv->category_list )
		{
			g_list_foreach( priv->category_list,  free_string, NULL );
			g_list_free( priv->category_list );
			priv->category_list = NULL;
		}
		
		if ( priv->attach_list )
		{
			g_slist_foreach( priv->attach_list, free_attach, NULL ); 
			g_slist_free( priv->attach_list );
			priv->attach_list = NULL ;
		}
		
		if ( priv->category_name )
		{
			g_free( priv->category_name );
			priv->category_name = NULL;
		}
		
		if ( priv->source )
		{
			g_free( priv->source );
			priv->source = NULL;
		}
		
		if ( priv->link_info )
		{
			free_link_info( priv->link_info );
			priv->link_info = NULL;
		}
	}

	if ( parent_class->dispose )
	{
		( *parent_class->dispose )( object );
	}
}


static void
e_zimbra_item_finalize (GObject *object)
{
	EZimbraItem *item = (EZimbraItem *) object;
	EZimbraItemPrivate *priv;

	g_return_if_fail (E_IS_ZIMBRA_ITEM (item));

	priv = item->priv;

	/* clean up */
	g_free (priv);
	item->priv = NULL;

	if (parent_class->finalize)
		(* parent_class->finalize) (object);
}


static void
e_zimbra_item_class_init (EZimbraItemClass *klass)
{
	GObjectClass *object_class = G_OBJECT_CLASS (klass);

	parent_class = g_type_class_peek_parent (klass);

	object_class->dispose = e_zimbra_item_dispose;
	object_class->finalize = e_zimbra_item_finalize;
}


static void
e_zimbra_item_init (EZimbraItem *item, EZimbraItemClass *klass)
{
	EZimbraItemPrivate *priv;

	/* allocate internal structure */
	priv = g_new0 (EZimbraItemPrivate, 1);
	priv->item_type = E_ZIMBRA_ITEM_TYPE_UNKNOWN;
	priv->id			= NULL;
	priv->rev			= NULL;
	priv->start_date	= NULL;
	priv->end_date		= NULL;
	priv->rid			= NULL;
	priv->delivered_date = NULL;
	priv->trigger = 0;
	priv->recipient_list = NULL;
	priv->organizer = NULL;
	priv->recurrence_dates = NULL;
	priv->completed = FALSE;
	priv->all_day = FALSE;
	priv->im_list = NULL;
	priv->email_list = NULL;
	priv->member_list = NULL;
	priv->category_list = NULL;
	priv->reply_within = NULL;
	priv->reply_request_set = FALSE;
	priv->autodelete = FALSE;
	priv->set_sendoptions = FALSE;
	priv->expires = NULL;
	priv->delay_until = NULL;
	priv->attach_list = NULL ;
	priv->simple_fields = g_hash_table_new_full (g_str_hash, g_str_equal, NULL, g_free);
	priv->full_name = g_new0(FullName, 1);
	priv->addresses = g_hash_table_new_full (g_str_hash, g_str_equal, NULL, free_postal_address);
	priv->link_info = NULL;
	priv->msg_body_id = NULL;
	item->priv = priv;
}


GType
e_zimbra_item_get_type (void)
{
	static GType type = 0;

	if (!type) {
		static GTypeInfo info = {
                        sizeof (EZimbraItemClass),
                        (GBaseInitFunc) NULL,
                        (GBaseFinalizeFunc) NULL,
                        (GClassInitFunc) e_zimbra_item_class_init,
                        NULL, NULL,
                        sizeof (EZimbraItem),
                        0,
                        (GInstanceInitFunc) e_zimbra_item_init
                };
		type = g_type_register_static (G_TYPE_OBJECT, "EZimbraItem", &info, 0);
	}

	return type;
}


void 
e_zimbra_item_free_cal_id (EZimbraItemCalId *calid)
{
	if (calid->item_id) {
		g_free (calid->item_id);
		calid->item_id = NULL;
	}
	
	if (calid->ical_id) {
		g_free (calid->ical_id);
		calid->ical_id = NULL;
	}
	
	if (calid->recur_key) {
		g_free (calid->recur_key);
		calid->recur_key = NULL;
	}

	g_free (calid);
}


EZimbraItem *
e_zimbra_item_new_empty (void)
{
	return g_object_new (E_TYPE_ZIMBRA_ITEM, NULL);
}


char*
e_zimbra_item_get_field_value (EZimbraItem *item, char *field_name)
{
	gpointer value;

	g_return_val_if_fail (field_name != NULL, NULL);
	g_return_val_if_fail (E_IS_ZIMBRA_ITEM(item), NULL);
	
	if (item->priv->simple_fields == NULL)
		return NULL;
       
	value =  (char *) g_hash_table_lookup (item->priv->simple_fields, field_name);
	if (value)
		return value;
			
	return NULL;
}


void 
e_zimbra_item_set_field_value (EZimbraItem *item, char *field_name, char* field_value)
{
	int err;

	zimbra_check( E_IS_ZIMBRA_ITEM( item ), exit, err = -1 );
	zimbra_check( field_name, exit, err = -1 );
	
	if ( item->priv->simple_fields != NULL )
	{
		g_hash_table_insert( item->priv->simple_fields, field_name, g_strdup( field_value ) );
	}

exit:

	return;
}


EZimbraItemStatus 
e_zimbra_item_get_status (EZimbraItem *item)
{
	return item->priv->status;
}


void
e_zimbra_item_set_status( EZimbraItem * item, EZimbraItemStatus status )
{
	item->priv->status = status;
}


GList * 
e_zimbra_item_get_email_list (EZimbraItem *item)
{
	return item->priv->email_list;
}


void 
e_zimbra_item_set_email_list (EZimbraItem *item, GList* email_list)     
{
	item->priv->email_list = email_list;
}


GList * 
e_zimbra_item_get_im_list (EZimbraItem *item)

{
	return item->priv->im_list;
}


void 
e_zimbra_item_set_im_list (EZimbraItem *item, GList *im_list)
{
	item->priv->im_list = im_list;
}


FullName*
e_zimbra_item_get_full_name (EZimbraItem *item)
{
	return item->priv->full_name;
}


void 
e_zimbra_item_set_full_name (EZimbraItem *item, FullName *full_name)
{	
	item->priv->full_name = full_name;
}


GList *
e_zimbra_item_get_member_list (EZimbraItem *item)
{
	return item->priv->member_list;
}


void 
e_zimbra_item_set_member_list (EZimbraItem *item, GList *list)
{
	item->priv->member_list = list;

}


void 
e_zimbra_item_set_address (EZimbraItem *item, char *address_type, PostalAddress *address)
{
	if (address_type && address)
		g_hash_table_insert (item->priv->addresses, address_type, address);
}


PostalAddress*
e_zimbra_item_get_address (EZimbraItem *item, char *address_type)
{
	return (PostalAddress *) g_hash_table_lookup (item->priv->addresses, address_type);
}


void 
e_zimbra_item_set_categories (EZimbraItem *item, GList *category_list)
{
	item->priv->category_list = category_list;

}


GList*
e_zimbra_item_get_categories (EZimbraItem *item)
{
	return item->priv->category_list;
}


void 
e_zimbra_item_set_category_name (EZimbraItem *item, char *category_name)
{
	item->priv->category_name = category_name;
}


char*
e_zimbra_item_get_category_name (EZimbraItem *item)
{
	return item->priv->category_name;
}


static void 
set_common_addressbook_item_fields_from_soap_parameter
	(
	EZimbraItem	*	item,
	xmlNode		*	node
	)
{
	GHashTable			*	simple_fields	=	NULL;
	char				*	value			=	NULL;
	char				*	firstName		=	NULL;
	char				*	lastName		=	NULL;
	char				*	companyName		=	NULL;
	EZimbraItemPrivate	*	priv			=	NULL;

	priv			= item->priv;
	simple_fields	= priv->simple_fields;

	firstName		= e_zimbra_xml_find_child_value( node, "firstName" );
	lastName 		= e_zimbra_xml_find_child_value( node, "lastName" );
	companyName 	= e_zimbra_xml_find_child_value( node, "company" );

	// ID

	if ( ( value = e_zimbra_xml_find_attribute( node, "id" ) ) != NULL )
	{
		GLOG_DEBUG( "id is %s", value );

		g_hash_table_insert (simple_fields, "id", g_strdup( value ) );
		item->priv->id = value;
	}

	// Revision

	item->priv->rev	= e_zimbra_xml_find_attribute( node, "rev" );

	if ( !item->priv->rev )
	{
		item->priv->rev	= e_zimbra_xml_find_attribute( node, "ms" );
	}

	// Name

	if ( ( value = e_zimbra_xml_find_child_value( node, "fileAs" ) ) != NULL )
	{
		priv->file_as = atoi( value );
		g_free( value );
	}
	else
	{
		priv->file_as = 1;
	}

	GLOG_DEBUG( "fileAs is %d", priv->file_as );

	switch ( priv->file_as )
	{
		case 1:
		{
			if ( firstName && !lastName )
			{
				value = g_strconcat( firstName, NULL );
			}
			else if ( !firstName && lastName )
			{
				value = g_strconcat( lastName, NULL );
			}
			else if ( firstName && lastName )
			{
				value = g_strconcat( lastName, ", ", firstName, NULL );
			}
			else
			{
				value = NULL;
			}

			if ( companyName )
			{
				g_free( companyName );
			}
		}
		break;

		case 2:
		{
			if ( firstName && !lastName )
			{
				value = g_strconcat( firstName, NULL );
			}
			else if ( !firstName && lastName )
			{
				value = g_strconcat( lastName, NULL );
			}
			else if ( firstName && lastName )
			{
				value = g_strconcat( firstName, " ", lastName, NULL );
			}
			else
			{
				value = NULL;
			}

			if ( companyName )
			{
				g_free( companyName );
			}
		}
		break;

		default:
		{
			if ( companyName )
			{
				value = companyName;
			}
			else
			{
				value = NULL;
			}
		}
		break;
	}

	if ( value )
	{
		g_hash_table_insert( simple_fields, "name", value );
	}

	if ( firstName )
	{
		g_free( firstName );
	}

	if ( lastName )
	{
		g_free( lastName );
	}
}


static PostalAddress* 
get_postal_address_from_soap_parameter
	(
	const char	*	type,
	xmlNode		*	node
	)
{
	PostalAddress	*	address	=	NULL;
	char				key[ 1024 ];
	char			*	street;
	char			*	city;
	char			*	state;
	char			*	postalCode;
	char			*	country;
	

	snprintf( key, sizeof( key ), "%sStreet", type );
	street		= e_zimbra_xml_find_child_value( node, key );
	snprintf( key, sizeof( key ), "%sCity", type );
	city		= e_zimbra_xml_find_child_value( node, key );
	snprintf( key, sizeof( key ), "%sState", type );
	state		= e_zimbra_xml_find_child_value( node, key );
	snprintf( key, sizeof( key ), "%sPostalCode", type );
	postalCode	= e_zimbra_xml_find_child_value( node, key );
	snprintf( key, sizeof( key ), "%sCountry", type );
	country 	= e_zimbra_xml_find_child_value( node, key );

	if ( street || city || state || postalCode || country )
	{
		address = g_new0( PostalAddress, 1 );

		if ( !address )
		{
			goto exit;
		}

		address->street_address 	=	street;
		address->city				=	city;
		address->state				=	state;
		address->postal_code		=	postalCode;
		address->country			=	country;
	}

exit:

	return address;
}


static void
clear_rrule
	(
	EZimbraItemRecurrenceRule	*	rrule
	)
{
	memset( rrule, E_ZIMBRA_ITEM_RECUR_MAX_BYTE, sizeof( EZimbraItemRecurrenceRule ) );

	rrule->week_start	= E_ZIMBRA_ITEM_RECURRENCE_WEEKDAY_NONE;
	rrule->frequency	= E_ZIMBRA_ITEM_RECURRENCE_FREQUENCY_NONE;
	rrule->interval		= 1;
	rrule->until		= icaltime_null_time();
    rrule->count		= 0;
}


static gboolean 
set_appointment_fields_from_invite
	(
	EZimbraItem			*	item,
	EZimbraItem			*	parent,
	xmlNode				*	invite,
	EZimbraConnection	*	cnc
	)
{
	xmlNode 					*	child		= NULL;
	xmlNode						*	comp		= NULL;
	xmlNode						*	recur		= NULL;
	xmlNode						*	recur_child	= NULL;
	char						*	scratch		= NULL;
	char						*	rid			= NULL;
	xmlNode						*	rule		= NULL;
	xmlNode						*	prop		= NULL;
	xmlNode 					*	temp		= NULL;
	char						*	inv_id		= NULL;
	char						*	val			= NULL;
	gboolean						ok			= TRUE;

	item->priv->item_type = E_ZIMBRA_ITEM_TYPE_APPOINTMENT;

	// Timezone

	if ( ( temp = e_zimbra_xml_find_child_by_name( invite, "tz" ) ) != NULL )
	{
		xmltimezone_to_icaltimezone( temp );
	}

	comp = e_zimbra_xml_find_child_by_name( invite, "comp" );
	zimbra_check( comp, exit, g_warning( "%s: comp is NULL", __FUNCTION__ ); ok = FALSE );


	// ID

	item->priv->id				= e_zimbra_xml_find_attribute( comp, "apptId" );
	zimbra_check( item->priv->id, exit, ok = FALSE );

	// Inv-ID

	inv_id						= e_zimbra_xml_find_attribute( invite, "id" );
	zimbra_check( inv_id, exit, ok = FALSE );

	// XID

	item->priv->icalid			= e_zimbra_xml_find_attribute( comp, "x_uid" );
	zimbra_check( item->priv->icalid, exit, ok = FALSE );

	// Summary
		
	item->priv->subject			= e_zimbra_xml_find_attribute( comp, "name" );
	zimbra_check( item->priv->subject, exit, ok = FALSE );

	// All day

	item->priv->all_day			= e_zimbra_xml_check_attribute( comp, "allDay", "1" );

	// Location

	item->priv->place			= e_zimbra_xml_find_attribute( comp, "loc" );

	// Status

	if ( ( scratch = e_zimbra_xml_find_attribute( comp, "status" ) ) != NULL )
	{
		if ( g_str_equal( scratch, "TENT" ) )
		{
			item->priv->status = E_ZIMBRA_ITEM_STAT_TENTATIVE;
		}
		else if ( g_str_equal( scratch, "CONF" ) )
		{
			item->priv->status = E_ZIMBRA_ITEM_STAT_CONFIRMED;
		}
		else if ( g_str_equal( scratch, "CANC" ) )
		{
			item->priv->status = E_ZIMBRA_ITEM_STAT_CANCELLED;
		}

		g_free( scratch );
	}

	// Start date

	if ( ( temp = e_zimbra_xml_find_child_by_name( comp, "s" ) ) != NULL )
	{
		char * start_date_string = e_zimbra_xml_find_attribute( temp, "d" );
		char * tee;

		item->priv->start_date = g_new( struct icaltimetype, 1 );
		zimbra_check( item->priv->start_date, exit, ok = FALSE );

		// I don't know why ZCS does this, but it sends us a time for all day events that's a datetime.
		// Evolution don't like that none.  Let's fix it.

		if ( item->priv->all_day && ( tee = strchr( start_date_string, 'T' ) ) )
		{
			*tee = '\0';
		}

		*item->priv->start_date = icaltime_from_string( start_date_string );

		if ( !icaltime_is_date( *item->priv->start_date ) )
		{
			char * tzid = NULL;

			if ( ( tzid = e_zimbra_xml_find_attribute( temp, "tz" ) ) != NULL )
			{
				icaltime_set_timezone( item->priv->start_date, lookup_icaltimezone( tzid ) );
				g_free( tzid );
			}
			else
			{
				icaltime_set_timezone( item->priv->start_date, icaltimezone_get_utc_timezone() );
			}
		}

		if ( start_date_string )
		{
			g_free( start_date_string );
		}
	}

	// End date

	if ( ( temp = e_zimbra_xml_find_child_by_name( comp, "e" ) ) != NULL )
	{
		char * end_date_string = e_zimbra_xml_find_attribute( temp, "d" );
		char * tee;

		item->priv->end_date = g_new( struct icaltimetype, 1 );
		zimbra_check( item->priv->end_date, exit, ok = FALSE );

		// I don't know why ZCS does this, but it sends us a time for all day events that's a datetime.
		// Evolution don't like that none.  Let's fix it.

		if ( item->priv->all_day && ( tee = strchr( end_date_string, 'T' ) ) )
		{
			*tee = '\0';
		}

		*item->priv->end_date = icaltime_from_string( end_date_string );

		if ( icaltime_is_date( *item->priv->end_date ) )
		{
			// Add 24 hours to this date.  Don't ask.  It's lame.

			icaltime_adjust( item->priv->end_date, 1, 0, 0, 0 );
		}
		else
		{
			char * tzid = NULL;

			if ( ( tzid = e_zimbra_xml_find_attribute( temp, "tz" ) ) != NULL )
			{
				icaltime_set_timezone( item->priv->end_date, lookup_icaltimezone( tzid ) );
				g_free( tzid );
			}
			else
			{
				icaltime_set_timezone( item->priv->end_date, icaltimezone_get_utc_timezone() );
			}
		}

		if ( end_date_string )
		{
			g_free( end_date_string );
		}
	}

	// Organizer

	if ( ( temp = e_zimbra_xml_find_child_by_name( comp, "or" ) ) != NULL )
	{
		EZimbraItemOrganizer * organizer;

		organizer = g_new0( EZimbraItemOrganizer, 1 );
		zimbra_check( organizer, exit, ok = FALSE );

		organizer->display_name	= e_zimbra_xml_find_attribute( temp, "d" );
		organizer->email		= e_zimbra_xml_find_attribute( temp, "url" );

		item->priv->organizer = organizer;
	}

	// Attendees

	for ( child = comp->children; child; child = child->next )
	{
		if ( g_str_equal( child->name, "at" ) )
		{
			EZimbraItemRecipient * recipient;

			recipient = g_new0( EZimbraItemRecipient, 1 );	
			zimbra_check( recipient, exit, ok = FALSE );

			recipient->display_name = e_zimbra_xml_find_attribute( child, "d" );
			recipient->email		= e_zimbra_xml_find_attribute( child, "url" );

			if ( ( val = e_zimbra_xml_find_attribute( child, "role" ) ) != NULL )
			{
				if ( g_str_equal( val, "CHA" ) )
				{
					recipient->role = E_ZIMBRA_ITEM_ROLE_CHAIR;
				}
				else if ( g_str_equal( val, "REQ" ) )
				{
					recipient->role = E_ZIMBRA_ITEM_ROLE_REQUIRED_PARTICIPANT;
				}
				else if ( g_str_equal( val, "OPT" ) )
				{
					recipient->role = E_ZIMBRA_ITEM_ROLE_OPTIONAL_PARTICIPANT;
				}
				else
				{
					recipient->role = E_ZIMBRA_ITEM_ROLE_NON_PARTICIPANT;
				}

				g_free( val );
			}
			else
			{
				recipient->role = E_ZIMBRA_ITEM_ROLE_NON_PARTICIPANT;
			}

			if ( ( val = e_zimbra_xml_find_attribute( child, "ptst" ) ) != NULL )
			{
				if ( g_str_equal( val, "AC" ) )
				{
					recipient->status = E_ZIMBRA_ITEM_PART_STAT_ACCEPTED;
				}
				if ( g_str_equal( val, "DE" ) )
				{
					recipient->status = E_ZIMBRA_ITEM_PART_STAT_DECLINED;
				}
				if ( g_str_equal( val, "TE" ) )
				{
					recipient->status = E_ZIMBRA_ITEM_PART_STAT_TENTATIVE;
				}
				if ( g_str_equal( val, "DG" ) )
				{
					recipient->status = E_ZIMBRA_ITEM_PART_STAT_DELEGATED;
				}
				if ( g_str_equal( val, "CO" ) )
				{
					recipient->status = E_ZIMBRA_ITEM_PART_STAT_COMPLETED;
				}
				if ( g_str_equal( val, "IN" ) )
				{
					recipient->status = E_ZIMBRA_ITEM_PART_STAT_INPROCESS;
				}
				else
				{
					recipient->status = E_ZIMBRA_ITEM_PART_STAT_NEEDSACTION;
				}

				g_free( val );
			}
			else
			{
				recipient->status = E_ZIMBRA_ITEM_PART_STAT_NONE;
			}

			item->priv->recipient_list = g_slist_append( item->priv->recipient_list, recipient );
		}
	}

	// Recurrence

	if ( ( recur = e_zimbra_xml_find_child_by_name( comp, "recur" ) ) != NULL )
	{
		for ( recur_child = recur->children; recur_child; recur_child = recur_child->next )
		{
			if ( g_str_equal( recur_child->name, "add" ) )
			{
				item->priv->rrule = g_new0( EZimbraItemRecurrenceRule, 1);
				zimbra_check( item->priv->rrule, exit, ok = FALSE );

				clear_rrule( item->priv->rrule );
				
				for ( rule = recur_child->children; rule; rule = rule->next )
				{
					if ( g_str_equal( rule->name, "rule" ) )
					{
						// Frequency

						if ( ( val = e_zimbra_xml_find_attribute( rule, "freq" ) ) != NULL )
						{
							if ( g_str_equal( val, "WEE" ) )
							{
								item->priv->rrule->frequency = E_ZIMBRA_ITEM_RECURRENCE_FREQUENCY_WEEKLY;
							}
							else if ( g_str_equal( val, "MON" ) )
							{
								item->priv->rrule->frequency = E_ZIMBRA_ITEM_RECURRENCE_FREQUENCY_MONTHLY;
							}
							else if ( g_str_equal( val, "YEA" ) )
							{
								item->priv->rrule->frequency = E_ZIMBRA_ITEM_RECURRENCE_FREQUENCY_YEARLY;
							}
							else
							{
								item->priv->rrule->frequency = E_ZIMBRA_ITEM_RECURRENCE_FREQUENCY_DAILY;
							}

							g_free( val );
						}
						else
						{
							item->priv->rrule->frequency = E_ZIMBRA_ITEM_RECURRENCE_FREQUENCY_NONE;
						}

						// Until

						if ( ( prop = e_zimbra_xml_find_child_by_name( rule, "until" ) ) != NULL )
						{
							char * end_date_string = e_zimbra_xml_find_attribute( prop, "d" );

							item->priv->rrule->until = icaltime_from_string( end_date_string );

							if ( !icaltime_is_date( item->priv->rrule->until ) )
							{
								char * tzid = NULL;

								if ( ( tzid = e_zimbra_xml_find_attribute( prop, "tz" ) ) != NULL )
								{
									icaltime_set_timezone( &item->priv->rrule->until, lookup_icaltimezone( tzid ) );
									g_free( tzid );
								}
								else
								{
									icaltime_set_timezone( &item->priv->rrule->until, icaltimezone_get_utc_timezone() );
								}
							}

							if ( end_date_string )
							{
								g_free( end_date_string );
							}
						}

						// Interval

						if ( ( prop = e_zimbra_xml_find_child_by_name( rule, "interval" ) ) != NULL )
						{
							if ( ( val = e_zimbra_xml_find_attribute( prop, "ival" ) ) != NULL )
							{
								item->priv->rrule->interval = atoi( val );
								g_free( val );
							}
							else
							{
								item->priv->rrule->interval = 0;
							}
						}

						// Count

						if ( ( prop = e_zimbra_xml_find_child_by_name( rule, "count" ) ) != NULL )
						{
							if ( ( val = e_zimbra_xml_find_attribute( prop, "num" ) ) != NULL )
							{
								item->priv->rrule->count = atoi( val );
								g_free( val );
							}
							else
							{
								item->priv->rrule->count = 0;
							}
						}

						// ByDay

						if ( ( prop = e_zimbra_xml_find_child_by_name( rule, "byday" ) ) != NULL )
						{
							xmlNode	*	child;
							int			i;

							for ( child = prop->children, i = 0; child; child = child->next, i++ )
							{
								int ordwk = 0;
								int day = 0;

								if ( ( val = e_zimbra_xml_find_attribute( child, "ordwk" ) ) != NULL )
								{
									ordwk = atoi( val );
									g_free( val );
								}

								if ( ( val = e_zimbra_xml_find_attribute( child, "day" ) ) != NULL )
								{
									day = convert_string_day_to_integer_day( val ); 
									g_free( val );
								}

								// Encode the values.  This is the same encoding used by libical.

								if ( ordwk )
								{
									item->priv->rrule->by_day[ i ] = ( ( ordwk / abs( ordwk ) ) * ( day + 8 * abs( ordwk ) ) );
								}
								else
								{
									item->priv->rrule->by_day[ i ] = ( ( day + 8 ) );
								}
							}
						}

						// ByMonthDay

						if ( ( prop = e_zimbra_xml_find_child_by_name( rule, "bymonthday" ) ) != NULL )
						{
							if ( ( val = e_zimbra_xml_find_attribute( prop, "modaylist" ) ) != NULL )
							{
								char	*	savept	=	NULL;
								char	*	tok		=	NULL;
								int			i		=	0;

								for ( tok = strtok_r( val, ",", &savept ), i = 0; tok; tok = strtok_r( NULL, ",", &savept ), i++ )
								{
									item->priv->rrule->by_month_day[ i ] = atoi( tok );
								}

								g_free( val );
							}
						}

						// ByYearDay

						if ( ( prop = e_zimbra_xml_find_child_by_name( rule, "byyearday" ) ) != NULL )
						{
							if ( ( val = e_zimbra_xml_find_attribute( prop, "yrdaylist" ) ) != NULL )
							{
								char	*	savept	=	NULL;
								char	*	tok		=	NULL;
								int			i		=	0;

								for ( tok = strtok_r( val, ",", &savept ), i = 0; tok; tok = strtok_r( NULL, ",", &savept ), i++ )
								{
									item->priv->rrule->by_year_day[ i ] = atoi( tok );
								}

								g_free( val );
							}
						}

						// ByWeekNo

						if ( ( prop = e_zimbra_xml_find_child_by_name( rule, "byweekno" ) ) != NULL )
						{
							if ( ( val = e_zimbra_xml_find_attribute( prop, "wklist" ) ) != NULL )
							{
								char	*	savept	=	NULL;
								char	*	tok		=	NULL;
								int			i		=	0;

								for ( tok = strtok_r( val, ",", &savept ), i = 0; tok; tok = strtok_r( NULL, ",", &savept ), i++ )
								{
									item->priv->rrule->by_week_no[ i ] = atoi( tok );
								}

								g_free( val );
							}
						}

						// ByMonth

						if ( ( prop = e_zimbra_xml_find_child_by_name( rule, "bymonth" ) ) != NULL )
						{
							if ( ( val = e_zimbra_xml_find_attribute( prop, "molist" ) ) != NULL )
							{
								char	*	savept	=	NULL;
								char	*	tok		=	NULL;
								int			i		=	0;

								for ( tok = strtok_r( val, ",", &savept ), i = 0; tok; tok = strtok_r( NULL, ",", &savept ), i++ )
								{
									item->priv->rrule->by_month[ i ] = atoi( tok );
								}

								g_free( val );
							}
						}

						// BySetPos

						if ( ( prop = e_zimbra_xml_find_child_by_name( rule, "bysetpos" ) ) != NULL )
						{
							if ( ( val = e_zimbra_xml_find_attribute( prop, "poslist" ) ) != NULL )
							{
								char	*	savept	=	NULL;
								char	*	tok		=	NULL;
								int			i		=	0;

								for ( tok = strtok_r( val, ",", &savept ), i = 0; tok; tok = strtok_r( NULL, ",", &savept ), i++ )
								{
									item->priv->rrule->by_set_pos[ i ] = atoi( tok );
								}

								g_free( val );
							}
						}

						// Wkst

						if ( ( prop = e_zimbra_xml_find_child_by_name( rule, "wkst" ) ) != NULL )
						{
							if ( ( val = e_zimbra_xml_find_attribute( prop, "day" ) ) != NULL )
							{
								item->priv->rrule->week_start = convert_string_day_to_integer_day( val );	
								g_free( val );
							}
						}
					}
				}
			}
		}
	}

	// Recurrence ID

	if ( ( rid = e_zimbra_xml_find_attribute( invite, "recurId" ) ) != NULL )
	{
		char * start_date_string	= NULL;
		char * tzid					= NULL;
		char * tee					= NULL;
	
		if ( ( strstr( rid, "TZID=" ) ) != NULL )
		{
			tzid				= rid + strlen( "TZID=" );
			start_date_string	= strchr( rid, ':' );
	
			*start_date_string++ = '\0';
		}
		else
		{
			tzid				= NULL;
			start_date_string	= rid;
		}
	
		item->priv->rid = g_new( struct icaltimetype, 1 );
		zimbra_check( item->priv->rid, exit, ok = FALSE );

		// I don't know why ZCS does this, but it sends us a time for all day events that's a datetime.
		// Evolution don't like that none.  Let's fix it.

		if ( parent->priv->all_day && ( tee = strchr( start_date_string, 'T' ) ) )
		{
			*tee = '\0';
		}

		// Store the time

		*item->priv->rid = icaltime_from_string( start_date_string );

		if ( !icaltime_is_date( *item->priv->rid ) )
		{
			if ( tzid )
			{
				icaltime_set_timezone( item->priv->rid, lookup_icaltimezone( tzid ) );
			}
			else
			{
				icaltime_set_timezone( item->priv->rid, icaltimezone_get_utc_timezone() );
			}
		}

		g_free( rid );
	}

	// Description. This is a hack I took from the iSync code.  Basically, GetMsgRequest fails
	// on items in linked folders.

	if ( !strchr( item->priv->id, ':' ) && !strchr( inv_id, ':' ) )
	{
		char				*	full_id;
		EZimbraConnectionStatus err;

		full_id = g_strdup_printf( "%s-%s", item->priv->id, inv_id );
		zimbra_check( full_id, exit, ok = FALSE );

		err = e_zimbra_connection_get_message( cnc, full_id, &item->priv->message );
		g_free( full_id );
		zimbra_check( !err, exit, ok = FALSE );
	}
	
exit:

	if ( inv_id )
	{
		g_free( inv_id );
	}

	return ok;
}


static void 
set_appointment_fields_from_soap_parameter
	(
	EZimbraItem			*	item,
	xmlNode				*	node,
	EZimbraConnection	*	cnc
	)
{
	xmlNode *	child	= NULL;
	gboolean	ok;

	// Revision

	item->priv->rev	= e_zimbra_xml_find_attribute( node, "rev" );

	if ( !item->priv->rev )
	{
		item->priv->rev	= e_zimbra_xml_find_attribute( node, "ms" );
	}
		
	zimbra_check( item->priv->rev, exit, ok = FALSE );

	for ( child = node->children; child; child = child->next )
	{
		if ( !e_zimbra_xml_check_attribute_exists( child, "recurId" ) )
		{
			ok = set_appointment_fields_from_invite( item, NULL, child, cnc );
			zimbra_check( ok, exit, ok = FALSE );
		}
		else
		{
			EZimbraItem * detached_item;

			detached_item = g_object_new( E_TYPE_ZIMBRA_ITEM, NULL );
			zimbra_check( detached_item, exit, ok = FALSE );

			ok = set_appointment_fields_from_invite( detached_item, item, child, cnc );
			zimbra_check( detached_item, exit, ok = FALSE );

			item->priv->detached_items = g_list_append( item->priv->detached_items, detached_item );
		}
	}

exit:

	return;
}


static char*
convert_array_to_string
	(
	const short * array
	)
{
	char	*	string = NULL;
	int			i;

	for ( i = 0; array[i] != E_ZIMBRA_ITEM_RECUR_END_MARKER; i++ )
	{
		if ( !string )
		{
			string = g_strdup_printf( "%d", array[i] );
		}
		else
		{
			char * temp;

			temp = string;

			string = g_strdup_printf( "%d,%s", array[i], temp );

			g_free( temp );
		}
	}

	return string;
}


static gboolean
append_invite_fields_to_soap_message
	(
	EZimbraItem		*	item,
	EZimbraItem		*	parent,
	xmlTextWriterPtr	request
	)
{
	const icaltimezone	*	zone	=	NULL;
	GSList				*	rl		=	NULL;
	int						rc;
	gboolean				ok	=	TRUE;
	
/*
	if ( item->priv->summary )
	{
		tmp = *msg;
		*msg = g_strconcat( tmp, "<su>", item->priv->summary, "</su>" );
		g_free( tmp );
	}
*/

	// Blop out the invite

	rc = xmlTextWriterStartElement( request, BAD_CAST "inv" );
	zimbra_check( rc != -1, exit, ok = FALSE );

	// We're going to cheat here a little.  We're going to assume that the timezones for the start date
	// and the end date are the same.

	// Regardless, we won't send a tz spec if our date is specified in UTC

	if ( !item->priv->all_day && !icaltime_is_utc( *item->priv->start_date ) )
	{
		zone = icaltime_get_timezone( *item->priv->start_date );
		zimbra_check( zone, exit, ok = FALSE );

		ok = icaltimezone_to_xmltimezone( zone, request );
		zimbra_check( ok, exit, ok = FALSE );
	}

	rc = xmlTextWriterStartElement( request, BAD_CAST "comp" );
	zimbra_check( rc != -1, exit, ok = FALSE );

	rc = xmlTextWriterWriteAttribute( request, BAD_CAST "uid", BAD_CAST item->priv->icalid );
	zimbra_check( rc != -1, exit, ok = FALSE );

	rc = xmlTextWriterWriteAttribute( request, BAD_CAST "type", BAD_CAST "event" );
	zimbra_check( rc != -1, exit, ok = FALSE );

	if ( item->priv->all_day )
	{
		rc = xmlTextWriterWriteAttribute( request, BAD_CAST "allDay", BAD_CAST "1" );
		zimbra_check( rc != -1, exit, ok = FALSE );
	}
	else
	{
		rc = xmlTextWriterWriteAttribute( request, BAD_CAST "allDay", BAD_CAST "0" );
		zimbra_check( rc != -1, exit, ok = FALSE );
	}

	if ( item->priv->subject )
	{
		rc = xmlTextWriterWriteAttribute( request, BAD_CAST "name", BAD_CAST item->priv->subject );
		zimbra_check( rc != -1, exit, ok = FALSE );
	}

	if ( item->priv->place )
	{
		rc = xmlTextWriterWriteAttribute( request, BAD_CAST "loc", BAD_CAST item->priv->place );
		zimbra_check( rc != -1, exit, ok = FALSE );
	}

	// Recurrences.

	if ( item->priv->rrule )
	{
		rc = xmlTextWriterStartElement( request, BAD_CAST "recur" );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterStartElement( request, BAD_CAST "add" );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterStartElement( request, BAD_CAST "rule" );
		zimbra_check( rc != -1, exit, ok = FALSE );

		switch ( item->priv->rrule->frequency )
		{
			case E_ZIMBRA_ITEM_RECURRENCE_FREQUENCY_DAILY:
			{
				rc = xmlTextWriterWriteAttribute( request, BAD_CAST "freq", BAD_CAST "DAI" );
				zimbra_check( rc != -1, exit, ok = FALSE );
			}
			break;

			case E_ZIMBRA_ITEM_RECURRENCE_FREQUENCY_WEEKLY:
			{
				rc = xmlTextWriterWriteAttribute( request, BAD_CAST "freq", BAD_CAST "WEE" );
				zimbra_check( rc != -1, exit, ok = FALSE );
			}
			break;

			case E_ZIMBRA_ITEM_RECURRENCE_FREQUENCY_MONTHLY:
			{
				rc = xmlTextWriterWriteAttribute( request, BAD_CAST "freq", BAD_CAST "MON" );
				zimbra_check( rc != -1, exit, ok = FALSE );
			}	
			break;

			case E_ZIMBRA_ITEM_RECURRENCE_FREQUENCY_YEARLY:
			{
				rc = xmlTextWriterWriteAttribute( request, BAD_CAST "freq", BAD_CAST "YEA" );
				zimbra_check( rc != -1, exit, ok = FALSE );
			}
			break;

			default:
			{
			}
			break;
		}

		if ( item->priv->rrule->interval )
		{
			rc = xmlTextWriterStartElement( request, BAD_CAST "interval" );
			zimbra_check( rc != -1, exit, ok = FALSE );

			rc = xmlTextWriterWriteFormatAttribute( request, BAD_CAST "ival", "%d", item->priv->rrule->interval );
			zimbra_check( rc != -1, exit, ok = FALSE );

			rc = xmlTextWriterEndElement( request );
			zimbra_check( rc != -1, exit, ok = FALSE );
		}

		if ( item->priv->rrule->count )
		{
			rc = xmlTextWriterStartElement( request, BAD_CAST "count" );
			zimbra_check( rc != -1, exit, ok = FALSE );

			rc = xmlTextWriterWriteFormatAttribute( request, BAD_CAST "num", "%d", item->priv->rrule->count );
			zimbra_check( rc != -1, exit, ok = FALSE );

			rc = xmlTextWriterEndElement( request );
			zimbra_check( rc != -1, exit, ok = FALSE );
		}
		else if ( !icaltime_is_null_time( item->priv->rrule->until ) )
		{
			rc = xmlTextWriterStartElement( request, BAD_CAST "until" );
			zimbra_check( rc != -1, exit, ok = FALSE );

			rc = xmlTextWriterWriteAttribute( request, BAD_CAST "d", BAD_CAST icaltime_as_ical_string( item->priv->rrule->until ) );
			zimbra_check( rc != -1, exit, ok = FALSE );

			rc = xmlTextWriterEndElement( request );
			zimbra_check( rc != -1, exit, ok = FALSE );
		}

		if ( item->priv->rrule->by_day[0] != E_ZIMBRA_ITEM_RECUR_END_MARKER )
		{
			int	max_elements = sizeof( item->priv->rrule->by_day ) / sizeof( item->priv->rrule->by_day[0] );
			int	i;

			rc = xmlTextWriterStartElement( request, BAD_CAST "byday" );
			zimbra_check( rc != -1, exit, ok = FALSE );

			for ( i = 0; i <= max_elements && item->priv->rrule->by_day[i] != E_ZIMBRA_ITEM_RECUR_END_MARKER; i++)
			{
				int day;
				int ordwk;

				rc = xmlTextWriterStartElement( request, BAD_CAST "wkday" );
				zimbra_check( rc != -1, exit, ok = FALSE );

				decode_by_day_value( item->priv->rrule->by_day[ i ], &day, &ordwk );

				if ( ordwk )
				{
					rc = xmlTextWriterWriteFormatAttribute( request, BAD_CAST "ordwk", "%d", ordwk );
					zimbra_check( rc != -1, exit, ok = FALSE );
				}

				rc = xmlTextWriterWriteAttribute( request, BAD_CAST "day", BAD_CAST convert_integer_day_to_string_day( day ) );
				zimbra_check( rc != -1, exit, ok = FALSE );

				rc = xmlTextWriterEndElement( request );
				zimbra_check( rc != -1, exit, ok = FALSE );
			}

			rc = xmlTextWriterEndElement( request );
			zimbra_check( rc != -1, exit, ok = FALSE );
		}

		if ( item->priv->rrule->by_month_day[0] != E_ZIMBRA_ITEM_RECUR_END_MARKER )
		{
			char * string;

			rc = xmlTextWriterStartElement( request, BAD_CAST "bymonthday" );
			zimbra_check( rc != -1, exit, ok = FALSE );

			string = convert_array_to_string( item->priv->rrule->by_month_day );
			zimbra_check( string, exit, ok = FALSE );

			rc = xmlTextWriterWriteAttribute( request, BAD_CAST "modaylist", BAD_CAST string );
			g_free( string );
			zimbra_check( rc != -1, exit, ok = FALSE );

			rc = xmlTextWriterEndElement( request );
			zimbra_check( rc != -1, exit, ok = FALSE );
		}

		if ( item->priv->rrule->by_year_day[0] != E_ZIMBRA_ITEM_RECUR_END_MARKER )
		{
			char * string;

			rc = xmlTextWriterStartElement( request, BAD_CAST "byyearday" );
			zimbra_check( rc != -1, exit, ok = FALSE );

			string = convert_array_to_string( item->priv->rrule->by_year_day );
			zimbra_check( string, exit, ok = FALSE );

			rc = xmlTextWriterWriteAttribute( request, BAD_CAST "yrdaylist", BAD_CAST string );
			g_free( string );
			zimbra_check( rc != -1, exit, ok = FALSE );

			rc = xmlTextWriterEndElement( request );
			zimbra_check( rc != -1, exit, ok = FALSE );
		}

		if ( item->priv->rrule->by_week_no[0] != E_ZIMBRA_ITEM_RECUR_END_MARKER )
		{
			char * string;

			rc = xmlTextWriterStartElement( request, BAD_CAST "byweekno" );
			zimbra_check( rc != -1, exit, ok = FALSE );

			string = convert_array_to_string( item->priv->rrule->by_week_no );
			zimbra_check( string, exit, ok = FALSE );

			rc = xmlTextWriterWriteAttribute( request, BAD_CAST "wklist", BAD_CAST string );
			g_free( string );
			zimbra_check( rc != -1, exit, ok = FALSE );

			rc = xmlTextWriterEndElement( request );
			zimbra_check( rc != -1, exit, ok = FALSE );
		}

		if ( item->priv->rrule->by_month[0] != E_ZIMBRA_ITEM_RECUR_END_MARKER )
		{
			char * string;

			rc = xmlTextWriterStartElement( request, BAD_CAST "bymonth" );
			zimbra_check( rc != -1, exit, ok = FALSE );

			string = convert_array_to_string( item->priv->rrule->by_month );
			zimbra_check( string, exit, ok = FALSE );

			rc = xmlTextWriterWriteAttribute( request, BAD_CAST "molist", BAD_CAST string );
			g_free( string );
			zimbra_check( rc != -1, exit, ok = FALSE );

			rc = xmlTextWriterEndElement( request );
			zimbra_check( rc != -1, exit, ok = FALSE );
		}

		if ( item->priv->rrule->by_set_pos[0] != E_ZIMBRA_ITEM_RECUR_END_MARKER )
		{
			char * string;

			rc = xmlTextWriterStartElement( request, BAD_CAST "bysetpos" );
			zimbra_check( rc != -1, exit, ok = FALSE );

			string = convert_array_to_string( item->priv->rrule->by_set_pos );
			zimbra_check( string, exit, ok = FALSE );

			rc = xmlTextWriterWriteAttribute( request, BAD_CAST "poslist", BAD_CAST string );
			g_free( string );
			zimbra_check( rc != -1, exit, ok = FALSE );

			rc = xmlTextWriterEndElement( request );
			zimbra_check( rc != -1, exit, ok = FALSE );
		}

		if ( item->priv->rrule->week_start != E_ZIMBRA_ITEM_RECURRENCE_WEEKDAY_NONE )
		{
			rc = xmlTextWriterStartElement( request, BAD_CAST "wkst" );
			zimbra_check( rc != -1, exit, ok = FALSE );

			rc = xmlTextWriterWriteAttribute( request, BAD_CAST "day", BAD_CAST convert_integer_day_to_string_day( item->priv->rrule->week_start ) );
			zimbra_check( rc != -1, exit, ok = FALSE );

			rc = xmlTextWriterEndElement( request );
			zimbra_check( rc != -1, exit, ok = FALSE );
		}

		// </rule>

		rc = xmlTextWriterEndElement( request );
		zimbra_check( rc != -1, exit, ok = FALSE );

		// </add>

		rc = xmlTextWriterEndElement( request );
		zimbra_check( rc != -1, exit, ok = FALSE );

		// </recur>

		rc = xmlTextWriterEndElement( request );
		zimbra_check( rc != -1, exit, ok = FALSE );
	}

	// Detached recurrence

	if ( item->priv->rid )
	{
		const char * ical_string_time;

		if ( parent->priv->all_day )
		{
			if ( !item->priv->rid->is_date )
			{
				GLOG_DEBUG( "Rid of all_day event is not set to is_date" );
			}

			item->priv->rid->is_date = 1;
		}

		ical_string_time = icaltime_as_ical_string( *item->priv->rid );

		rc = xmlTextWriterStartElement( request, BAD_CAST "exceptId" );
		zimbra_check( rc != -1, exit, ok = FALSE );

		if ( item->priv->all_day || icaltime_is_utc( *item->priv->rid ) )
		{
			rc = xmlTextWriterWriteAttribute( request, BAD_CAST "d", BAD_CAST ical_string_time );
			zimbra_check( rc != -1, exit, ok = FALSE );
		}
		else
		{
			rc = xmlTextWriterWriteAttribute( request, BAD_CAST "d", BAD_CAST ical_string_time );
			zimbra_check( rc != -1, exit, ok = FALSE );

			rc = xmlTextWriterWriteAttribute( request, BAD_CAST "tz", BAD_CAST icaltimezone_get_tzid( ( icaltimezone* ) zone ) );
			zimbra_check( rc != -1, exit, ok = FALSE );
		}

		rc = xmlTextWriterEndElement( request );
		zimbra_check( rc != -1, exit, ok = FALSE );
	}

	// Start date

	if ( item->priv->start_date )
	{
		const char * ical_string_time;

		rc = xmlTextWriterStartElement( request, BAD_CAST "s" );
		zimbra_check( rc != -1, exit, ok = FALSE );

		if ( item->priv->all_day )
		{
			if ( !item->priv->start_date->is_date )
			{
				GLOG_DEBUG( "start date of all_day event is not set to is_date" );
			}

			item->priv->start_date->is_date = 1;
		}

		ical_string_time = icaltime_as_ical_string( *item->priv->start_date );

		if ( item->priv->all_day )
		{
			rc = xmlTextWriterWriteAttribute( request, BAD_CAST "d", BAD_CAST ical_string_time );
			zimbra_check( rc != -1, exit, ok = FALSE );
		}
		else
		{
			rc = xmlTextWriterWriteAttribute( request, BAD_CAST "d", BAD_CAST ical_string_time );
			zimbra_check( rc != -1, exit, ok = FALSE );

			// If we're not UTC, then specify the tzid

			if ( !icaltime_is_utc( *item->priv->start_date ) )
			{
				rc = xmlTextWriterWriteAttribute( request, BAD_CAST "tz", BAD_CAST icaltimezone_get_tzid( ( icaltimezone* ) zone ) );
				zimbra_check( rc != -1, exit, ok = FALSE );
			}
		}

		rc = xmlTextWriterEndElement( request );
		zimbra_check( rc != -1, exit, ok = FALSE );
	}

	// End date

	if ( item->priv->end_date )
	{
		const char * ical_string_time;

		rc = xmlTextWriterStartElement( request, BAD_CAST "e" );
		zimbra_check( rc != -1, exit, ok = FALSE );

		if ( item->priv->all_day )
		{
			icaltimetype adjusted;

			if ( !item->priv->end_date->is_date )
			{
				GLOG_DEBUG( "end date of all_day event is not set to is_date" );
			}

			item->priv->end_date->is_date = 1;

			// We want to subtrace 24 hours from this time, so our events line up with ZCS

			adjusted = *item->priv->end_date;
			icaltime_adjust( &adjusted, -1, 0, 0, 0 );

 			ical_string_time = icaltime_as_ical_string( adjusted );

			// And now write it out
			
			rc = xmlTextWriterWriteAttribute( request, BAD_CAST "d", BAD_CAST ical_string_time );
			zimbra_check( rc != -1, exit, ok = FALSE );
		}
		else
		{
 			ical_string_time = icaltime_as_ical_string( *item->priv->end_date );

			rc = xmlTextWriterWriteAttribute( request, BAD_CAST "d", BAD_CAST ical_string_time );
			zimbra_check( rc != -1, exit, ok = FALSE );

			// If we're not UTC, then specify the tzid

			if ( !icaltime_is_utc( *item->priv->start_date ) )
			{
				rc = xmlTextWriterWriteAttribute( request, BAD_CAST "tz", BAD_CAST icaltimezone_get_tzid( ( icaltimezone* ) zone ) );
				zimbra_check( rc != -1, exit, ok = FALSE );
			}
		}

		rc = xmlTextWriterEndElement( request );
		zimbra_check( rc != -1, exit, ok = FALSE );
	}

	// Organizer

	rc = xmlTextWriterStartElement( request, BAD_CAST "or" );
	zimbra_check( rc != -1, exit, ok = FALSE );

	if ( item->priv->organizer && item->priv->organizer->display_name )
	{
		rc = xmlTextWriterWriteAttribute( request, BAD_CAST "d", BAD_CAST item->priv->organizer->display_name );
		zimbra_check( rc != -1, exit, ok = FALSE );
	}

	if ( item->priv->organizer && item->priv->organizer->email )
	{
		rc = xmlTextWriterWriteAttribute( request, BAD_CAST "a", BAD_CAST item->priv->organizer->email );
		zimbra_check( rc != -1, exit, ok = FALSE );
	}

	rc = xmlTextWriterEndElement( request );
	zimbra_check( rc != -1, exit, ok = FALSE );

	// Attendees

	for ( rl = item->priv->recipient_list; rl; rl = rl->next )
	{
		EZimbraItemRecipient * recipient = ( EZimbraItemRecipient* ) rl->data;

		rc = xmlTextWriterStartElement( request, BAD_CAST "at" );
		zimbra_check( rc != -1, exit, ok = FALSE );

		if ( recipient->display_name )
		{
			rc = xmlTextWriterWriteAttribute( request, BAD_CAST "d", BAD_CAST recipient->display_name );
			zimbra_check( rc != -1, exit, ok = FALSE );
		}

		if ( recipient->email )
		{
			rc = xmlTextWriterWriteAttribute( request, BAD_CAST "a", BAD_CAST recipient->email );
			zimbra_check( rc != -1, exit, ok = FALSE );
		}

		switch ( recipient->role )
		{
			case E_ZIMBRA_ITEM_ROLE_CHAIR:
			{
				rc = xmlTextWriterWriteAttribute( request, BAD_CAST "role", BAD_CAST "CHA" );
				zimbra_check( rc != -1, exit, ok = FALSE );
			}
			break;

			case E_ZIMBRA_ITEM_ROLE_REQUIRED_PARTICIPANT:
			{
				rc = xmlTextWriterWriteAttribute( request, BAD_CAST "role", BAD_CAST "REQ" );
				zimbra_check( rc != -1, exit, ok = FALSE );
			}
			break;

			case E_ZIMBRA_ITEM_ROLE_OPTIONAL_PARTICIPANT:
			{
				rc = xmlTextWriterWriteAttribute( request, BAD_CAST "role", BAD_CAST "OPT" );
				zimbra_check( rc != -1, exit, ok = FALSE );
			}
			break;

			case E_ZIMBRA_ITEM_ROLE_NON_PARTICIPANT:
			{
				rc = xmlTextWriterWriteAttribute( request, BAD_CAST "role", BAD_CAST "NON" );
				zimbra_check( rc != -1, exit, ok = FALSE );
			}
			break;
		}

		switch ( recipient->status )
		{
			case E_ZIMBRA_ITEM_PART_STAT_ACCEPTED:
			{
				rc = xmlTextWriterWriteAttribute( request, BAD_CAST "ptst", BAD_CAST "AC" );
				zimbra_check( rc != -1, exit, ok = FALSE );
			}
			break;

			case E_ZIMBRA_ITEM_PART_STAT_DECLINED:
			{
				rc = xmlTextWriterWriteAttribute( request, BAD_CAST "ptst", BAD_CAST "DE" );
				zimbra_check( rc != -1, exit, ok = FALSE );
			}
			break;

			case E_ZIMBRA_ITEM_PART_STAT_TENTATIVE:
			{
				rc = xmlTextWriterWriteAttribute( request, BAD_CAST "ptst", BAD_CAST "TE" );
				zimbra_check( rc != -1, exit, ok = FALSE );
			}
			break;

			case E_ZIMBRA_ITEM_PART_STAT_DELEGATED:
			{
				rc = xmlTextWriterWriteAttribute( request, BAD_CAST "ptst", BAD_CAST "DG" );
				zimbra_check( rc != -1, exit, ok = FALSE );
			}
			break;

			case E_ZIMBRA_ITEM_PART_STAT_COMPLETED:
			{
				rc = xmlTextWriterWriteAttribute( request, BAD_CAST "ptst", BAD_CAST "CO" );
				zimbra_check( rc != -1, exit, ok = FALSE );
			}
			break;

			case E_ZIMBRA_ITEM_PART_STAT_INPROCESS:
			{
				rc = xmlTextWriterWriteAttribute( request, BAD_CAST "ptst", BAD_CAST "NO" );
				zimbra_check( rc != -1, exit, ok = FALSE );
			}
			break;

			default:
			{
				rc = xmlTextWriterWriteAttribute( request, BAD_CAST "ptst", BAD_CAST "NE" );
				zimbra_check( rc != -1, exit, ok = FALSE );
			}
			break;
		}

		rc = xmlTextWriterEndElement( request );
		zimbra_check( rc != -1, exit, ok = FALSE );
	}
		
	// </comp>

	rc = xmlTextWriterEndElement( request );
	zimbra_check( rc != -1, exit, ok = FALSE );

	// </inv>

	rc = xmlTextWriterEndElement( request );
	zimbra_check( rc != -1, exit, ok = FALSE );

	// Now blop out the description

	rc = xmlTextWriterStartElement( request, BAD_CAST "mp" );
	zimbra_check( rc != -1, exit, ok = FALSE );

	rc = xmlTextWriterWriteAttribute( request, BAD_CAST "ct", BAD_CAST "text/plain" );
	zimbra_check( rc != -1, exit, ok = FALSE );
	
	if ( item->priv->message )
	{
		rc = xmlTextWriterWriteElement( request, BAD_CAST "content", BAD_CAST item->priv->message );
		zimbra_check( rc != -1, exit, ok = FALSE );
	}
	else
	{
		rc = xmlTextWriterWriteElement( request, BAD_CAST "content", BAD_CAST "" );
		zimbra_check( rc != -1, exit, ok = FALSE );
	}

	// </mp>

	rc =xmlTextWriterEndElement( request );
	zimbra_check( rc != -1, exit, ok = FALSE );

exit:

	return ok;
}


static gboolean
append_appointment_fields_to_soap_message
	(
	EZimbraItem		*	item,
	xmlTextWriterPtr	request
	)
{
	GList			*	l	=	NULL;
	int					rc;
	gboolean			ok	=	TRUE;
	
	rc = xmlTextWriterWriteAttribute( request, BAD_CAST "l", BAD_CAST item->priv->folder_id );
	zimbra_check( rc != -1, exit, ok = FALSE );

	rc = xmlTextWriterStartElement( request, BAD_CAST "default" );
	zimbra_check( rc != -1, exit, ok = FALSE );

	// What do I put here?  How do I get the ptst of this guy?  Let's just punt for now

	rc = xmlTextWriterWriteAttribute( request, BAD_CAST "needsReply", BAD_CAST "0" );
	zimbra_check( rc != -1, exit, ok = FALSE );

	rc = xmlTextWriterWriteAttribute( request, BAD_CAST "ptst", BAD_CAST "AC" );
	zimbra_check( rc != -1, exit, ok = FALSE );

	rc = xmlTextWriterStartElement( request, BAD_CAST "m" );
	zimbra_check( rc != -1, exit, ok = FALSE );

/*
	if ( item->priv->summary )
	{
		tmp = *msg;
		*msg = g_strconcat( tmp, "<su>", item->priv->summary, "</su>" );
		g_free( tmp );
	}
*/

	// Main invite

	ok = append_invite_fields_to_soap_message( item, NULL, request );
	zimbra_check( ok, exit, ok = FALSE );

	// </m>

	rc = xmlTextWriterEndElement( request );
	zimbra_check( rc != -1, exit, ok = FALSE );

	// </default>

	rc = xmlTextWriterEndElement( request );
	zimbra_check( rc != -1, exit, ok = FALSE );

	// Detached invites

	for ( l = item->priv->detached_items; l != NULL; l = g_list_next( l ) )
	{
		EZimbraItem * detached_item = ( EZimbraItem* ) l->data;

		rc = xmlTextWriterStartElement( request, BAD_CAST "except" );
		zimbra_check( rc != -1, exit, ok = FALSE );

		// What do I put here?  How do I get the ptst of this guy?  Let's just punt for now

		rc = xmlTextWriterWriteAttribute( request, BAD_CAST "needsReply", BAD_CAST "0" );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteAttribute( request, BAD_CAST "ptst", BAD_CAST "AC" );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterStartElement( request, BAD_CAST "m" );
		zimbra_check( rc != -1, exit, ok = FALSE );
		
		ok = append_invite_fields_to_soap_message( detached_item, item, request );
		zimbra_check( ok, exit, ok = FALSE );

		// </m>

		rc = xmlTextWriterEndElement( request );
		zimbra_check( rc != -1, exit, ok = FALSE );

		// </except>

		rc = xmlTextWriterEndElement( request );
		zimbra_check( rc != -1, exit, ok = FALSE );
	}

	// Exceptions

	if ( g_slist_length( item->priv->exdate_list ) > 0 )
	{
		GSList * sl;

		for ( sl = item->priv->exdate_list; sl != NULL; sl = g_slist_next( sl ) )
		{
			char * dt_string = ( char* ) sl->data;

			rc = xmlTextWriterStartElement( request, BAD_CAST "cancel" );
			zimbra_check( rc != -1, exit, ok = FALSE );

			rc = xmlTextWriterStartElement( request, BAD_CAST "m" );
			zimbra_check( rc != -1, exit, ok = FALSE );
	
			rc = xmlTextWriterStartElement( request, BAD_CAST "inv" );
			zimbra_check( rc != -1, exit, ok = FALSE );
	
			rc = xmlTextWriterStartElement( request, BAD_CAST "comp" );
			zimbra_check( rc != -1, exit, ok = FALSE );
	
			rc = xmlTextWriterWriteAttribute( request, BAD_CAST "uid", BAD_CAST item->priv->icalid );
			zimbra_check( rc != -1, exit, ok = FALSE );
	
			rc = xmlTextWriterWriteAttribute( request, BAD_CAST "type", BAD_CAST "event" );
			zimbra_check( rc != -1, exit, ok = FALSE );
	
			if ( item->priv->all_day )
			{
				rc = xmlTextWriterWriteAttribute( request, BAD_CAST "allDay", BAD_CAST "1" );
				zimbra_check( rc != -1, exit, ok = FALSE );
			}
			else
			{
				rc = xmlTextWriterWriteAttribute( request, BAD_CAST "allDay", BAD_CAST "0" );
				zimbra_check( rc != -1, exit, ok = FALSE );
			}

			if ( item->priv->subject )
			{
				rc = xmlTextWriterWriteAttribute( request, BAD_CAST "name", BAD_CAST item->priv->subject );
				zimbra_check( rc != -1, exit, ok = FALSE );
			}
		
			if ( item->priv->place )
			{
				rc = xmlTextWriterWriteAttribute( request, BAD_CAST "loc", BAD_CAST item->priv->place );
				zimbra_check( rc != -1, exit, ok = FALSE );
			}

			// Except ID
	
			rc = xmlTextWriterStartElement( request, BAD_CAST "exceptId" );
			zimbra_check( rc != -1, exit, ok = FALSE );
	
			rc = xmlTextWriterWriteAttribute( request, BAD_CAST "d", BAD_CAST dt_string );
			zimbra_check( rc != -1, exit, ok = FALSE );
	
			rc = xmlTextWriterEndElement( request );
			zimbra_check( rc != -1, exit, ok = FALSE );
	
			// Start time

			rc = xmlTextWriterStartElement( request, BAD_CAST "s" );
			zimbra_check( rc != -1, exit, ok = FALSE );
	
			rc = xmlTextWriterWriteAttribute( request, BAD_CAST "d", BAD_CAST dt_string );
			zimbra_check( rc != -1, exit, ok = FALSE );
	
			rc = xmlTextWriterEndElement( request );
			zimbra_check( rc != -1, exit, ok = FALSE );
		
			// Organizer
		
			rc = xmlTextWriterStartElement( request, BAD_CAST "or" );
			zimbra_check( rc != -1, exit, ok = FALSE );

			if ( item->priv->organizer && item->priv->organizer->display_name )
			{
				rc = xmlTextWriterWriteAttribute( request, BAD_CAST "d", BAD_CAST item->priv->organizer->display_name );
				zimbra_check( rc != -1, exit, ok = FALSE );
			}
	
			if ( item->priv->organizer && item->priv->organizer->email )
			{
				rc = xmlTextWriterWriteAttribute( request, BAD_CAST "a", BAD_CAST item->priv->organizer->email );
				zimbra_check( rc != -1, exit, ok = FALSE );
			}

			// </or>
	
			rc = xmlTextWriterEndElement( request );
			zimbra_check( rc != -1, exit, ok = FALSE );
	
			// </comp>
	
			rc = xmlTextWriterEndElement( request );
			zimbra_check( rc != -1, exit, ok = FALSE );
	
			// </inv>
		
			rc = xmlTextWriterEndElement( request );
			zimbra_check( rc != -1, exit, ok = FALSE );

			rc = xmlTextWriterStartElement( request, BAD_CAST "mp" );
			zimbra_check( rc != -1, exit, ok = FALSE );

			rc = xmlTextWriterWriteAttribute( request, BAD_CAST "ct", BAD_CAST "text/plain" );
			zimbra_check( rc != -1, exit, ok = FALSE );
	
			rc = xmlTextWriterStartElement( request, BAD_CAST "content" );
			zimbra_check( rc != -1, exit, ok = FALSE );

			// </content>

			rc = xmlTextWriterEndElement( request );
			zimbra_check( rc != -1, exit, ok = FALSE );

			// </mp>

			rc = xmlTextWriterEndElement( request );
			zimbra_check( rc != -1, exit, ok = FALSE );

			// </m>
		
			rc = xmlTextWriterEndElement( request );
			zimbra_check( rc != -1, exit, ok = FALSE );

			// </cancel>
	
			rc = xmlTextWriterEndElement( request );
			zimbra_check( rc != -1, exit, ok = FALSE );
		}
	}

exit:

	return ok;
}


static void 
set_contact_fields_from_soap_parameter
	(
	EZimbraItem	*	item,
	xmlNode		*	node
	)
{
	char *value;
	GHashTable *simple_fields;
	FullName *full_name ;
	PostalAddress *address;

	item->priv->item_type = E_ZIMBRA_ITEM_TYPE_CONTACT;

	value = NULL;

	set_common_addressbook_item_fields_from_soap_parameter( item, node );
	simple_fields	= item->priv->simple_fields;
	full_name		= item->priv->full_name;

	if ( ( full_name = item->priv->full_name ) != NULL )
	{
		if ( item->priv->file_as != 3 )
		{
			if ( ( value = e_zimbra_xml_find_child_value( node, "firstName" ) ) != NULL )
			{
				full_name->first_name = value;
			}

			if ( ( value = e_zimbra_xml_find_child_value( node, "middleName" ) ) != NULL )
			{
				full_name->middle_name = value;
			}

			if ( ( value = e_zimbra_xml_find_child_value( node, "lastName" ) ) != NULL )
			{
				full_name->last_name = value;
			}
		}
		else if ( ( value = e_zimbra_xml_find_child_value( node, "company" ) ) != NULL )
		{
			GLOG_DEBUG( "setting full name to company name!!" );
			full_name->first_name = value;
		}
	}

	// Email

	if ( ( value = e_zimbra_xml_find_child_value( node, "email" ) ) != NULL )
	{
		item->priv->email_list = g_list_append( item->priv->email_list, value );
	}

	if ( ( value = e_zimbra_xml_find_child_value( node, "email2" ) ) != NULL )
	{
		item->priv->email_list = g_list_append( item->priv->email_list, value );
	}

	if ( ( value = e_zimbra_xml_find_child_value( node, "email3" ) ) != NULL )
	{
		item->priv->email_list = g_list_append( item->priv->email_list, value );
	}

	// Phone

	if ( ( value = e_zimbra_xml_find_child_value( node, "homePhone" ) ) != NULL )
	{
		g_hash_table_insert( item->priv->simple_fields, "homePhone", value );
	}

	if ( ( value = e_zimbra_xml_find_child_value( node, "workPhone" ) ) != NULL )
	{
		g_hash_table_insert( item->priv->simple_fields, "workPhone", value );
	}

	if ( ( value = e_zimbra_xml_find_child_value( node, "otherPhone" ) ) != NULL )
	{
		g_hash_table_insert( item->priv->simple_fields, "otherPhone", value );
	}

	if ( ( value = e_zimbra_xml_find_child_value( node, "homeFax" ) ) != NULL )
	{
		g_hash_table_insert( item->priv->simple_fields, "homeFax", value );
	}

	if ( ( value = e_zimbra_xml_find_child_value( node, "workFax" ) ) != NULL )
	{
		g_hash_table_insert( item->priv->simple_fields, "workFax", value );
	}

	if ( ( value = e_zimbra_xml_find_child_value( node, "otherFax" ) ) != NULL )
	{
		g_hash_table_insert( item->priv->simple_fields, "otherFax", value );
	}

	if ( ( value = e_zimbra_xml_find_child_value( node, "mobilePhone" ) ) != NULL )
	{
		g_hash_table_insert( item->priv->simple_fields, "mobilePhone", value );
	}

	if ( ( value = e_zimbra_xml_find_child_value( node, "carPhone" ) ) != NULL )
	{
		g_hash_table_insert( item->priv->simple_fields, "carPhone", value );
	}

	if ( ( value = e_zimbra_xml_find_child_value( node, "assistantPhone" ) ) != NULL )
	{
		g_hash_table_insert( item->priv->simple_fields, "assistantPhone", value );
	}

	if ( ( value = e_zimbra_xml_find_child_value( node, "companyPhone" ) ) != NULL )
	{
		g_hash_table_insert( item->priv->simple_fields, "companyPhone", value );
	}

	if ( ( value = e_zimbra_xml_find_child_value( node, "pager" ) ) != NULL )
	{
		g_hash_table_insert( item->priv->simple_fields, "pager", value );
	}

	if ( ( value = e_zimbra_xml_find_child_value( node, "callbackPhone" ) ) != NULL )
	{
		g_hash_table_insert( item->priv->simple_fields, "callbackPhone", value );
	}

	// Homepage

	if ( ( value = e_zimbra_xml_find_child_value( node, "workURL" ) ) != NULL )
	{
		g_hash_table_insert( simple_fields, "website", value );
	}
	else if ( ( value = e_zimbra_xml_find_child_value( node, "homeURL" ) ) != NULL )
	{
		g_hash_table_insert( simple_fields, "website", value );
	}

	// Organizational

	if ( ( value = e_zimbra_xml_find_child_value( node, "company" ) ) != NULL )
	{
		g_hash_table_insert( simple_fields, "company", value );
	}

	if ( ( value = e_zimbra_xml_find_child_value( node, "jobTitle" ) ) != NULL )
	{
		g_hash_table_insert( simple_fields, "jobTitle", value );
	}

	// Addresses

	if ( ( address = get_postal_address_from_soap_parameter( "home", node ) ) != NULL )
	{
		g_hash_table_insert( item->priv->addresses, "home", address );
	}

	if ( ( address = get_postal_address_from_soap_parameter( "work", node ) ) != NULL )
	{
		g_hash_table_insert( item->priv->addresses, "work", address );
	}

	if ( ( address = get_postal_address_from_soap_parameter( "other", node ) ) != NULL )
	{
		g_hash_table_insert( item->priv->addresses, "other", address );
	}

	// Note

	if ( ( value = e_zimbra_xml_find_child_value( node, "notes" ) ) != NULL )
	{
		g_hash_table_insert( simple_fields , "notes", value );
	}
}


static void 
append_postal_address_to_soap_message
	(
	PostalAddress	*	address,
	xmlTextWriterPtr	request,
	char			*	type
	)
{
	int			rc;
	gboolean	ok;

	if ( address->street_address )
	{
		rc = xmlTextWriterStartElement( request, BAD_CAST "a" );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteFormatAttribute( request, BAD_CAST "n", "%sStreet", type );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteString( request, BAD_CAST address->street_address );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterEndElement( request );
		zimbra_check( rc != -1, exit, ok = FALSE );
	}

	if ( address->city )
	{
		rc = xmlTextWriterStartElement( request, BAD_CAST "a" );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteFormatAttribute( request, BAD_CAST "n", "%sCity", type );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteString( request, BAD_CAST address->city );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterEndElement( request );
		zimbra_check( rc != -1, exit, ok = FALSE );
	}

	if ( address->state )
	{
		rc = xmlTextWriterStartElement( request, BAD_CAST "a" );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteFormatAttribute( request, BAD_CAST "n", "%sState", type );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteString( request, BAD_CAST address->state );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterEndElement( request );
		zimbra_check( rc != -1, exit, ok = FALSE );
	}

	if ( address->postal_code )
	{
		rc = xmlTextWriterStartElement( request, BAD_CAST "a" );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteFormatAttribute( request, BAD_CAST "n", "%sPostalCode", type );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteString( request, BAD_CAST address->postal_code );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterEndElement( request );
		zimbra_check( rc != -1, exit, ok = FALSE );
	}

	if ( address->country )
	{
		rc = xmlTextWriterStartElement( request, BAD_CAST "a" );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteFormatAttribute( request, BAD_CAST "n", "%sCountry", type );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteString( request, BAD_CAST address->country );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterEndElement( request );
		zimbra_check( rc != -1, exit, ok = FALSE );
	}

exit:

	return;
}


static void
append_common_addressbook_item_fields_to_soap_message
	(
	GHashTable		*	simple_fields,
	GList			*	category_list,
	xmlTextWriterPtr	request
	)
{
	char	*	value;
	int			rc;
	gboolean	ok;
	
	if ( ( value = g_hash_table_lookup( simple_fields, "website" ) ) != NULL )
	{
		rc = xmlTextWriterStartElement( request, BAD_CAST "a" );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteAttribute( request, BAD_CAST "n", BAD_CAST "homeURL" );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteString( request, BAD_CAST value);
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterEndElement( request );
		zimbra_check( rc != -1, exit, ok = FALSE );
	}

	if ( ( value = g_hash_table_lookup( simple_fields, "notes" ) ) != NULL )
	{
		rc = xmlTextWriterStartElement( request, BAD_CAST "a" );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteAttribute( request, BAD_CAST "n", BAD_CAST "notes" );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteString( request, BAD_CAST value);
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterEndElement( request );
		zimbra_check( rc != -1, exit, ok = FALSE );
	}

exit:

	return;
}


static void 
append_full_name_to_soap_message
	(
	FullName		*	full_name,
	char			*	display_name,
	xmlTextWriterPtr	request
	)
{
	int			rc;
	gboolean	ok;

	zimbra_check( full_name, exit, g_warning( "%s: full_name is NULL", __FUNCTION__ ) );

	if ( full_name->first_name )
	{
		rc = xmlTextWriterStartElement( request, BAD_CAST "a" );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteAttribute( request, BAD_CAST "n", BAD_CAST "firstName" );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteString( request, BAD_CAST full_name->first_name);
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterEndElement( request );
		zimbra_check( rc != -1, exit, ok = FALSE );
	}

	if ( full_name->middle_name )
	{
		rc = xmlTextWriterStartElement( request, BAD_CAST "a" );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteAttribute( request, BAD_CAST "n", BAD_CAST "middleName" );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteString( request, BAD_CAST full_name->middle_name);
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterEndElement( request );
		zimbra_check( rc != -1, exit, ok = FALSE );
	}

	if ( full_name->last_name )
	{
		rc = xmlTextWriterStartElement( request, BAD_CAST "a" );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteAttribute( request, BAD_CAST "n", BAD_CAST "lastName" );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteString( request, BAD_CAST full_name->last_name );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterEndElement( request );
		zimbra_check( rc != -1, exit, ok = FALSE );
	}

exit:

	return;
}


static void 
append_email_list_soap_message
	(
	GList			*	email_list,
	xmlTextWriterPtr	request
	)
{
	int			i = 0;
	int			rc;
	gboolean	ok;

	zimbra_check( email_list, exit, g_warning( "%s: email_list is NULL", __FUNCTION__ ) );

	for ( i = 0; email_list != NULL; email_list = g_list_next(email_list ), i++ )
	{
		if ( email_list->data ) 
		{
			char name[ 256 ];

			rc = xmlTextWriterStartElement( request, BAD_CAST "a" );
			zimbra_check( rc != -1, exit, ok = FALSE );

			if ( !i )
			{
				strcpy( name, "email" );
			}
			else
			{
				snprintf( name, sizeof( name ), "email%d", i + 1 );
			}

			rc = xmlTextWriterWriteAttribute( request, BAD_CAST "n", BAD_CAST name );
			zimbra_check( rc != -1, exit, ok = FALSE );

			rc = xmlTextWriterWriteString( request, BAD_CAST email_list->data );
			zimbra_check( rc != -1, exit, ok = FALSE );

			rc = xmlTextWriterEndElement( request );
			zimbra_check( rc != -1, exit, ok = FALSE );
		}
	}

exit:

	return;
}


static void
append_phone_list_to_soap_message
	(
	GHashTable		*	simple_fields,
	xmlTextWriterPtr	request
	)
{
	char	*	value;
	int			rc;
	gboolean	ok;

	zimbra_check( simple_fields, exit, g_warning( "%s: simple_fields is NULL", __FUNCTION__ ) );

	if ( ( value = g_hash_table_lookup( simple_fields, "homePhone" ) ) != NULL )
	{
		rc = xmlTextWriterStartElement( request, BAD_CAST "a" );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteAttribute( request, BAD_CAST "n", BAD_CAST "homePhone" );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteString( request, BAD_CAST value );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterEndElement( request );
		zimbra_check( rc != -1, exit, ok = FALSE );
	}

	if ( ( value = g_hash_table_lookup( simple_fields, "workPhone" ) ) != NULL )
	{
		rc = xmlTextWriterStartElement( request, BAD_CAST "a" );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteAttribute( request, BAD_CAST "n", BAD_CAST "workPhone" );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteString( request, BAD_CAST value );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterEndElement( request );
		zimbra_check( rc != -1, exit, ok = FALSE );
	}

	if ( ( value = g_hash_table_lookup( simple_fields, "otherPhone" ) ) != NULL )
	{
		rc = xmlTextWriterStartElement( request, BAD_CAST "a" );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteAttribute( request, BAD_CAST "n", BAD_CAST "otherPhone" );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteString( request, BAD_CAST value );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterEndElement( request );
		zimbra_check( rc != -1, exit, ok = FALSE );
	}

	if ( ( value = g_hash_table_lookup( simple_fields, "homeFax" ) ) != NULL )
	{
		rc = xmlTextWriterStartElement( request, BAD_CAST "a" );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteAttribute( request, BAD_CAST "n", BAD_CAST "homeFax" );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteString( request, BAD_CAST value );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterEndElement( request );
		zimbra_check( rc != -1, exit, ok = FALSE );
	}

	if ( ( value = g_hash_table_lookup( simple_fields, "workFax" ) ) != NULL )
	{
		rc = xmlTextWriterStartElement( request, BAD_CAST "a" );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteAttribute( request, BAD_CAST "n", BAD_CAST "workFax" );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteString( request, BAD_CAST value );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterEndElement( request );
		zimbra_check( rc != -1, exit, ok = FALSE );
	}

	if ( ( value = g_hash_table_lookup( simple_fields, "otherFax" ) ) != NULL )
	{
		rc = xmlTextWriterStartElement( request, BAD_CAST "a" );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteAttribute( request, BAD_CAST "n", BAD_CAST "otherFax" );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteString( request, BAD_CAST value );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterEndElement( request );
		zimbra_check( rc != -1, exit, ok = FALSE );
	}

	if ( ( value = g_hash_table_lookup( simple_fields, "mobilePhone" ) ) != NULL )
	{
		rc = xmlTextWriterStartElement( request, BAD_CAST "a" );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteAttribute( request, BAD_CAST "n", BAD_CAST "mobilePhone" );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteString( request, BAD_CAST value );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterEndElement( request );
		zimbra_check( rc != -1, exit, ok = FALSE );
	}

	if ( ( value = g_hash_table_lookup( simple_fields, "carPhone" ) ) != NULL )
	{
		rc = xmlTextWriterStartElement( request, BAD_CAST "a" );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteAttribute( request, BAD_CAST "n", BAD_CAST "carPhone" );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteString( request, BAD_CAST value );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterEndElement( request );
		zimbra_check( rc != -1, exit, ok = FALSE );
	}

	if ( ( value = g_hash_table_lookup( simple_fields, "assistantPhone" ) ) != NULL )
	{
		rc = xmlTextWriterStartElement( request, BAD_CAST "a" );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteAttribute( request, BAD_CAST "n", BAD_CAST "assistantPhone" );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteString( request, BAD_CAST value );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterEndElement( request );
		zimbra_check( rc != -1, exit, ok = FALSE );
	}

	if ( ( value = g_hash_table_lookup( simple_fields, "companyPhone" ) ) != NULL )
	{
		rc = xmlTextWriterStartElement( request, BAD_CAST "a" );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteAttribute( request, BAD_CAST "n", BAD_CAST "companyPhone" );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteString( request, BAD_CAST value );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterEndElement( request );
		zimbra_check( rc != -1, exit, ok = FALSE );
	}

	if ( ( value = g_hash_table_lookup( simple_fields, "pager" ) ) != NULL )
	{
		rc = xmlTextWriterStartElement( request, BAD_CAST "a" );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteAttribute( request, BAD_CAST "n", BAD_CAST "pager" );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteString( request, BAD_CAST value );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterEndElement( request );
		zimbra_check( rc != -1, exit, ok = FALSE );
	}

	if ( ( value = g_hash_table_lookup( simple_fields, "callbackPhone" ) ) != NULL )
	{
		rc = xmlTextWriterStartElement( request, BAD_CAST "a" );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteAttribute( request, BAD_CAST "n", BAD_CAST "callbackPhone" );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteString( request, BAD_CAST value );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterEndElement( request );
		zimbra_check( rc != -1, exit, ok = FALSE );
	}

exit:

	return;
}


static void 
append_office_info_to_soap_message
	(
	GHashTable		*	simple_fields,
	xmlTextWriterPtr	request
	)
{
	char	*	value;
	int			rc;
	gboolean	ok;

	zimbra_check( simple_fields, exit, g_warning( "%s: simple_fields is NULL", __FUNCTION__ ) );

	if ( ( value = g_hash_table_lookup( simple_fields, "company" ) ) != NULL )
	{
		rc = xmlTextWriterStartElement( request, BAD_CAST "a" );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteAttribute( request, BAD_CAST "n", BAD_CAST "company" );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteString( request, BAD_CAST value );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterEndElement( request );
		zimbra_check( rc != -1, exit, ok = FALSE );
	}
	
	if ( ( value = g_hash_table_lookup( simple_fields, "jobTitle" ) ) != NULL )
	{
		rc = xmlTextWriterStartElement( request, BAD_CAST "a" );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteAttribute( request, BAD_CAST "n", BAD_CAST "jobTitle" );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteString( request, BAD_CAST value );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterEndElement( request );
		zimbra_check( rc != -1, exit, ok = FALSE );
	}

exit:

	return;
}


static void 
append_personal_info_to_soap_message
	(
	GHashTable		*	simple_fields,
	xmlTextWriterPtr	request
	)
{
}


static void
append_contact_fields_to_soap_message
	(
	EZimbraItem			*	item,
	EZimbraItemChangeType	type,
	xmlTextWriterPtr		request
	)
{
	gboolean	ok	=	TRUE;
	int			rc;

	char			*	value;
	GHashTable		*	simple_fields;
	FullName		*	full_name;
	PostalAddress	*	postal_address;
	
	simple_fields	= item->priv->simple_fields;
	value			= g_hash_table_lookup (simple_fields, "id");

	rc = xmlTextWriterStartElement( request, BAD_CAST "cn" );
	zimbra_check( rc != -1, exit, ok = FALSE );

	if ( type == E_ZIMBRA_ITEM_CHANGE_TYPE_UPDATE )
	{
		rc = xmlTextWriterWriteAttribute( request, BAD_CAST "id", BAD_CAST value );
		zimbra_check( rc != -1, exit, ok = FALSE );
	}

	append_common_addressbook_item_fields_to_soap_message( simple_fields, item->priv->category_list, request );
	value =  g_hash_table_lookup (simple_fields, "name");
	
	if ( ( full_name = item->priv->full_name ) != NULL )
	{
		append_full_name_to_soap_message( full_name, value, request );
	}
	
	if ( item->priv->email_list )
	{
		append_email_list_soap_message( item->priv->email_list, request );
	}
	
	if ( simple_fields )
	{
		append_phone_list_to_soap_message( simple_fields, request );
	}
		
	if ( ( postal_address = g_hash_table_lookup( item->priv->addresses, "home" ) ) != NULL )
	{
		append_postal_address_to_soap_message( postal_address, request, "home" );
	}

	if ( ( postal_address = g_hash_table_lookup( item->priv->addresses, "work" ) ) != NULL )
	{
		append_postal_address_to_soap_message( postal_address, request, "work" );
	}

	if ( ( postal_address = g_hash_table_lookup( item->priv->addresses, "other" ) ) != NULL )
	{
		append_postal_address_to_soap_message( postal_address, request, "other" );
	}

	append_office_info_to_soap_message( simple_fields, request );
	append_personal_info_to_soap_message( simple_fields, request );

	// </cn>

	rc = xmlTextWriterEndElement( request );
	zimbra_check( rc != -1, exit, ok = FALSE );

exit:

	return;
}


EZimbraItem *
e_zimbra_item_new_from_soap_parameter
	(
	gpointer				opaque,
	EZimbraItemType			type,
	xmlNode				*	node
	)
{
	EZimbraConnection	*	cnc = ( EZimbraConnection* ) opaque;
	EZimbraItem			*	item;

	item = g_object_new( E_TYPE_ZIMBRA_ITEM, NULL );
	zimbra_check( item, exit, g_warning( "g_object_new failed" ) );

	switch ( type )
	{
		case E_ZIMBRA_ITEM_TYPE_APPOINTMENT:
		{
			set_appointment_fields_from_soap_parameter( item, node, cnc );
		}
		break;

		case E_ZIMBRA_ITEM_TYPE_CONTACT:
		{
			set_contact_fields_from_soap_parameter( item, node );
		}
		break;

		default:
		{
			g_object_unref( item );
			item = NULL;
		}
		break;
	}

exit:

	return item;
}


EZimbraItemType
e_zimbra_item_get_item_type (EZimbraItem *item)
{
	g_return_val_if_fail (E_IS_ZIMBRA_ITEM (item), E_ZIMBRA_ITEM_TYPE_UNKNOWN);

	return item->priv->item_type;
}


void
e_zimbra_item_set_item_type (EZimbraItem *item, EZimbraItemType new_type)
{
	g_return_if_fail (E_IS_ZIMBRA_ITEM (item));

	item->priv->item_type = new_type;
}


const char *
e_zimbra_item_get_folder_id (EZimbraItem *item)
{
	g_return_val_if_fail (E_IS_ZIMBRA_ITEM (item), NULL);

	return (const char *) item->priv->folder_id;
}


void
e_zimbra_item_set_folder_id (EZimbraItem *item, const char *new_id)
{
	g_return_if_fail (E_IS_ZIMBRA_ITEM (item));

	if (item->priv->folder_id)
		g_free (item->priv->folder_id);
	item->priv->folder_id = g_strdup (new_id);
}


const char *
e_zimbra_item_get_icalid (EZimbraItem *item)
{
	g_return_val_if_fail (E_IS_ZIMBRA_ITEM (item), NULL);

	return (const char *) item->priv->icalid;
}


void
e_zimbra_item_set_icalid (EZimbraItem *item, const char *new_icalid)
{
	g_return_if_fail (E_IS_ZIMBRA_ITEM (item));

	if (item->priv->icalid)
		g_free (item->priv->icalid);
	item->priv->icalid = g_strdup (new_icalid);
}


const char *
e_zimbra_item_get_id (EZimbraItem *item)
{
	g_return_val_if_fail (E_IS_ZIMBRA_ITEM (item), NULL);

	return (const char *) item->priv->id;
}

void
e_zimbra_item_set_id (EZimbraItem *item, const char *new_id)
{
	g_return_if_fail (E_IS_ZIMBRA_ITEM (item));

	if (item->priv->id)
		g_free (item->priv->id);
	item->priv->id = g_strdup (new_id);
}


const char *e_zimbra_item_get_rev( EZimbraItem * item )
{
	g_return_val_if_fail (E_IS_ZIMBRA_ITEM (item), NULL);

	return (const char *) item->priv->rev;
}


void
e_zimbra_item_set_rev(EZimbraItem *item, const char * new_rev)
{
	g_return_if_fail (E_IS_ZIMBRA_ITEM (item));

	if (item->priv->rev)
		g_free (item->priv->rev);
	item->priv->rev = g_strdup (new_rev);
}


void
e_zimbra_item_add_detached_item( EZimbraItem * item, EZimbraItem * detached_item )
{
	item->priv->detached_items = g_list_append( item->priv->detached_items, detached_item );
}


GList*
e_zimbra_item_peek_detached_items( EZimbraItem * item )
{
	return item->priv->detached_items;
}

int
e_zimbra_item_get_mail_size (EZimbraItem *item)
{
	g_return_val_if_fail (E_IS_ZIMBRA_ITEM (item), 0);

	return item->priv->size;
}


char *
e_zimbra_item_get_delivered_date (EZimbraItem *item)
{
	g_return_val_if_fail (E_IS_ZIMBRA_ITEM (item), NULL);

	return item->priv->delivered_date;
}


void
e_zimbra_item_set_delivered_date (EZimbraItem *item, const char *new_date)
{
	g_return_if_fail (E_IS_ZIMBRA_ITEM (item));

	if (item->priv->delivered_date)
		g_free (item->priv->delivered_date);
	item->priv->delivered_date = g_strdup (new_date);
}


const icaltimetype*
e_zimbra_item_get_start_date (EZimbraItem *item)
{
	g_return_val_if_fail( E_IS_ZIMBRA_ITEM (item), NULL );

	return item->priv->start_date;
}


void
e_zimbra_item_set_start_date
	(
	EZimbraItem			*	item,
	const icaltimetype	*	new_date 
	)
{
	g_return_if_fail( E_IS_ZIMBRA_ITEM( item ) );
	
	if ( new_date )
	{
		if ( !item->priv->start_date )
		{
			item->priv->start_date = g_new( struct icaltimetype, 1 );
		}

		*item->priv->start_date = *new_date;
	}
	else if ( item->priv->start_date )
	{
		g_free( item->priv->start_date );
		item->priv->start_date = NULL;
	}
}


const icaltimetype*
e_zimbra_item_get_end_date (EZimbraItem *item)
{
	g_return_val_if_fail( E_IS_ZIMBRA_ITEM (item), NULL );

	return item->priv->end_date;
}


void
e_zimbra_item_set_end_date
	(
	EZimbraItem			*	item,
	const icaltimetype	*	new_date 
	)
{
	g_return_if_fail (E_IS_ZIMBRA_ITEM (item));

	if ( new_date )
	{
		if ( !item->priv->end_date )
		{
			item->priv->end_date = g_new( struct icaltimetype, 1 );
		}
	
		*item->priv->end_date = *new_date;
	}
	else if ( item->priv->end_date )
	{
		g_free( item->priv->end_date );
		item->priv->end_date = NULL;
	}
}


const char *
e_zimbra_item_get_subject (EZimbraItem *item)
{
	g_return_val_if_fail (E_IS_ZIMBRA_ITEM (item), NULL);

	return (const char *) item->priv->subject;
}


void
e_zimbra_item_set_subject (EZimbraItem *item, const char *new_subject)
{
	g_return_if_fail (E_IS_ZIMBRA_ITEM (item));

	if (item->priv->subject)
		g_free (item->priv->subject);
	item->priv->subject = g_strdup (new_subject);
}


const char *
e_zimbra_item_get_message (EZimbraItem *item)
{
	g_return_val_if_fail (E_IS_ZIMBRA_ITEM (item), NULL);

	return (const char *) item->priv->message;
}


void
e_zimbra_item_set_message (EZimbraItem *item, const char *new_message)
{
	g_return_if_fail (E_IS_ZIMBRA_ITEM (item));

	if (item->priv->message)
		g_free (item->priv->message);
	item->priv->message = g_strdup (new_message);
}


const char *
e_zimbra_item_get_place (EZimbraItem *item)
{
	g_return_val_if_fail (E_IS_ZIMBRA_ITEM (item), NULL);

	return item->priv->place;
}


void
e_zimbra_item_set_place (EZimbraItem *item, const char *new_place)
{
	g_return_if_fail (E_IS_ZIMBRA_ITEM (item));

	if (item->priv->place)
		g_free (item->priv->place);
	item->priv->place = g_strdup (new_place);
}


const char *
e_zimbra_item_get_classification (EZimbraItem *item)
{
	g_return_val_if_fail (E_IS_ZIMBRA_ITEM (item), NULL);

	return (const char *) item->priv->classification;
}


void
e_zimbra_item_set_classification (EZimbraItem *item, const char *new_class)
{
	g_return_if_fail (E_IS_ZIMBRA_ITEM (item));

	if (item->priv->classification)
		g_free (item->priv->classification);
	item->priv->classification = g_strdup (new_class);
}


gboolean
e_zimbra_item_get_completed (EZimbraItem *item)
{
	g_return_val_if_fail (E_IS_ZIMBRA_ITEM (item), FALSE);

	return item->priv->completed;
}


void
e_zimbra_item_set_completed (EZimbraItem *item, gboolean new_completed)
{
	g_return_if_fail (E_IS_ZIMBRA_ITEM (item));

	item->priv->completed = new_completed;
}


gboolean 
e_zimbra_item_get_is_allday_event (EZimbraItem *item)
{
	g_return_val_if_fail (E_IS_ZIMBRA_ITEM (item), FALSE);

	return item->priv->all_day;
}


void 
e_zimbra_item_set_is_allday_event (EZimbraItem *item, gboolean allday_event)
{
	g_return_if_fail (E_IS_ZIMBRA_ITEM (item));

	item->priv->all_day = allday_event;
}


const char *
e_zimbra_item_get_accept_level (EZimbraItem *item)
{
	g_return_val_if_fail (E_IS_ZIMBRA_ITEM (item), NULL);

	return (const char *) item->priv->accept_level;
}


void
e_zimbra_item_set_accept_level (EZimbraItem *item, const char *new_level)
{
	g_return_if_fail (E_IS_ZIMBRA_ITEM (item));

	if (item->priv->accept_level)
		g_free (item->priv->accept_level);
	item->priv->accept_level = g_strdup (new_level);
}


const char *
e_zimbra_item_get_priority (EZimbraItem *item)
{
	g_return_val_if_fail (E_IS_ZIMBRA_ITEM (item), NULL);
	return (const char *) item->priv->priority;
}


void
e_zimbra_item_set_priority (EZimbraItem *item, const char *new_priority)
{
	g_return_if_fail (E_IS_ZIMBRA_ITEM (item));

	if (item->priv->priority)
		g_free (item->priv->priority);
	item->priv->priority = g_strdup (new_priority);
}


const char *
e_zimbra_item_get_task_priority (EZimbraItem *item)
{
	g_return_val_if_fail (E_IS_ZIMBRA_ITEM (item), NULL);

	return (const char *) item->priv->task_priority;
}


void
e_zimbra_item_set_task_priority (EZimbraItem *item, const char *new_priority)
{
	g_return_if_fail (E_IS_ZIMBRA_ITEM (item));

	if (item->priv->task_priority)
		g_free (item->priv->task_priority);
	item->priv->task_priority = g_strdup (new_priority);
}


GSList *
e_zimbra_item_get_recipient_list (EZimbraItem *item)
{
	g_return_val_if_fail (E_IS_ZIMBRA_ITEM (item), NULL);
	return item->priv->recipient_list;
}


void
e_zimbra_item_set_recipient_list (EZimbraItem  *item, GSList *new_recipient_list)
{
	/* free old list and set a new one*/
	g_slist_foreach (item->priv->recipient_list, (GFunc) free_recipient, NULL);
	g_slist_free (item->priv->recipient_list);
	item->priv->recipient_list = new_recipient_list;
}


EZimbraItemOrganizer *
e_zimbra_item_get_organizer (EZimbraItem *item)
{
	g_return_val_if_fail (E_IS_ZIMBRA_ITEM (item), NULL);
	return item->priv->organizer;
}


void
e_zimbra_item_set_attach_id_list (EZimbraItem *item, GSList *attach_list)
{
	g_return_if_fail (E_IS_ZIMBRA_ITEM (item)) ;
	if (attach_list) {
		g_slist_foreach (item->priv->attach_list, (GFunc)free_attach, NULL) ;
		g_slist_free (item->priv->attach_list) ;
	}
	item->priv->attach_list = attach_list ;
}


GSList *
e_zimbra_item_get_attach_id_list (EZimbraItem *item)
{
	g_return_val_if_fail (E_IS_ZIMBRA_ITEM (item), NULL) ;
	return item->priv->attach_list ;
}


void
e_zimbra_item_set_organizer (EZimbraItem  *item, EZimbraItemOrganizer *organizer)
{
	/* free organizer */ 
	g_free (item->priv->organizer);
	item->priv->organizer = organizer;
}


const icaltimetype*
e_zimbra_item_get_rid
	(
	EZimbraItem *	item 
	)
{
	g_return_val_if_fail (E_IS_ZIMBRA_ITEM (item), NULL) ;

	return item->priv->rid;
}


void
e_zimbra_item_set_rid
	(
	EZimbraItem			*	item,
	const icaltimetype	*	rid
	)
{
	if ( rid )
	{
		if ( !item->priv->rid )
		{
			item->priv->rid = g_new( struct icaltimetype, 1 );
		}

		*item->priv->rid = *rid;
	}
	else if ( item->priv->rid )
	{
		g_free( item->priv->rid );
		item->priv->rid = NULL;
	}
}


GSList *
e_zimbra_item_get_recurrence_dates (EZimbraItem *item)
{
	g_return_val_if_fail (E_IS_ZIMBRA_ITEM (item), NULL);
	return item->priv->recurrence_dates;
}


void
e_zimbra_item_set_recurrence_dates (EZimbraItem  *item, GSList *new_recurrence_dates)
{
	/* free old list and set a new one*/
	g_slist_foreach (item->priv->recurrence_dates, free_string, NULL);
	/*free the list */
	g_slist_free (item->priv->recurrence_dates);
	item->priv->recurrence_dates = new_recurrence_dates;
}


GSList *
e_zimbra_item_get_exdate_list (EZimbraItem *item)
{
	g_return_val_if_fail (E_IS_ZIMBRA_ITEM (item), NULL);
	return item->priv->exdate_list;
}


void
e_zimbra_item_set_exdate_list (EZimbraItem  *item, GSList *new_exdate_list)
{
	/* free old list and set a new one*/
	g_slist_foreach (item->priv->exdate_list, free_string, NULL);
	/*free the list */
	g_slist_free (item->priv->exdate_list);
	item->priv->exdate_list = new_exdate_list;
}


EZimbraItemRecurrenceRule *
e_zimbra_item_get_rrule (EZimbraItem *item)
{
	g_return_val_if_fail (E_IS_ZIMBRA_ITEM (item), NULL);
	return item->priv->rrule;
}


void
e_zimbra_item_set_rrule (EZimbraItem  *item, EZimbraItemRecurrenceRule *new_rrule)
{
	if (item->priv->rrule) {
	/* TODO free old list and set a new one*/
	}
	item->priv->rrule = new_rrule;
}


int
e_zimbra_item_get_trigger (EZimbraItem *item)
{
	g_return_val_if_fail (E_IS_ZIMBRA_ITEM (item), 0);

	return item->priv->trigger;
}

void
e_zimbra_item_set_trigger (EZimbraItem *item, int trigger)
{
	g_return_if_fail (E_IS_ZIMBRA_ITEM (item));

	item->priv->trigger = trigger;
}


void 
e_zimbra_item_set_to (EZimbraItem *item, const char *to)
{
	g_return_if_fail (E_IS_ZIMBRA_ITEM (item));
	item->priv->to = g_strdup (to) ; 
}

const char *
e_zimbra_item_get_to (EZimbraItem *item)
{
	g_return_val_if_fail (E_IS_ZIMBRA_ITEM(item), NULL) ;
	return item->priv->to ;
}

const char *
e_zimbra_item_get_msg_content_type (EZimbraItem *item)
{
	g_return_val_if_fail (E_IS_ZIMBRA_ITEM (item), NULL) ;
	return item->priv->content_type ;
}

const char *
e_zimbra_item_get_msg_body_id (EZimbraItem *item)
{
	g_return_val_if_fail (E_IS_ZIMBRA_ITEM (item), NULL);

	return item->priv->msg_body_id;
}

void
e_zimbra_item_set_sendoptions (EZimbraItem *item, gboolean set)
{
	g_return_if_fail (E_IS_ZIMBRA_ITEM (item));

	item->priv->set_sendoptions = set;
}

void
e_zimbra_item_set_reply_request (EZimbraItem *item, gboolean set)
{
	g_return_if_fail (E_IS_ZIMBRA_ITEM (item));

	item->priv->reply_request_set = set;
}

gboolean
e_zimbra_item_get_reply_request (EZimbraItem *item)
{
	g_return_val_if_fail (E_IS_ZIMBRA_ITEM (item), FALSE);

	return item->priv->reply_request_set;
}

void
e_zimbra_item_set_reply_within (EZimbraItem *item, char *reply_within)
{
	g_return_if_fail (E_IS_ZIMBRA_ITEM (item));

	item->priv->reply_within = g_strdup (reply_within);
}

char *
e_zimbra_item_get_reply_within (EZimbraItem *item)
{
	g_return_val_if_fail (E_IS_ZIMBRA_ITEM (item), NULL);

	return item->priv->reply_within;
}

void
e_zimbra_item_set_track_info (EZimbraItem *item, EZimbraItemTrack track_info)
{
	g_return_if_fail (E_IS_ZIMBRA_ITEM (item));

	item->priv->track_info = track_info;
}

EZimbraItemTrack
e_zimbra_item_get_track_info (EZimbraItem *item)
{
	g_return_val_if_fail (E_IS_ZIMBRA_ITEM (item), E_ZIMBRA_ITEM_NONE);

	return item->priv->track_info;
}


void
e_zimbra_item_set_autodelete (EZimbraItem *item, gboolean set)
{
	g_return_if_fail (E_IS_ZIMBRA_ITEM (item));

	item->priv->autodelete = set;
}

gboolean
e_zimbra_item_get_autodelete (EZimbraItem *item)
{
	g_return_val_if_fail (E_IS_ZIMBRA_ITEM (item), FALSE);

	return item->priv->autodelete;
}

void 
e_zimbra_item_set_notify_completed (EZimbraItem *item, EZimbraItemReturnNotify notify)
{
	g_return_if_fail (E_IS_ZIMBRA_ITEM (item));

	item->priv->notify_completed = notify;
}

EZimbraItemReturnNotify 
e_zimbra_item_get_notify_completed (EZimbraItem *item)
{
	g_return_val_if_fail (E_IS_ZIMBRA_ITEM (item), FALSE);

	return item->priv->notify_completed;
}

void 
e_zimbra_item_set_notify_accepted (EZimbraItem *item, EZimbraItemReturnNotify notify)
{
	g_return_if_fail (E_IS_ZIMBRA_ITEM (item));

	item->priv->notify_accepted = notify;
}

EZimbraItemReturnNotify 
e_zimbra_item_get_notify_accepted (EZimbraItem *item)
{
	g_return_val_if_fail (E_IS_ZIMBRA_ITEM (item), FALSE);

	return item->priv->notify_accepted;
}

void 
e_zimbra_item_set_notify_declined (EZimbraItem *item, EZimbraItemReturnNotify notify)
{
	g_return_if_fail (E_IS_ZIMBRA_ITEM (item));

	item->priv->notify_declined = notify;
}

EZimbraItemReturnNotify 
e_zimbra_item_get_notify_declined (EZimbraItem *item)
{
	g_return_val_if_fail (E_IS_ZIMBRA_ITEM (item), FALSE);

	return item->priv->notify_declined;
}

void 
e_zimbra_item_set_notify_opened (EZimbraItem *item, EZimbraItemReturnNotify notify)
{
	g_return_if_fail (E_IS_ZIMBRA_ITEM (item));

	item->priv->notify_opened = notify;
}

EZimbraItemReturnNotify 
e_zimbra_item_get_notify_opened (EZimbraItem *item)
{
	g_return_val_if_fail (E_IS_ZIMBRA_ITEM (item), FALSE);

	return item->priv->notify_opened;
}

void 
e_zimbra_item_set_notify_deleted (EZimbraItem *item, EZimbraItemReturnNotify notify)
{
	g_return_if_fail (E_IS_ZIMBRA_ITEM (item));

	item->priv->notify_deleted = notify;
}

EZimbraItemReturnNotify 
e_zimbra_item_get_notify_deleted (EZimbraItem *item)
{
	g_return_val_if_fail (E_IS_ZIMBRA_ITEM (item), FALSE);

	return item->priv->notify_deleted;
}

void
e_zimbra_item_set_expires (EZimbraItem *item, char *expires)
{
	g_return_if_fail (E_IS_ZIMBRA_ITEM (item));

	item->priv->expires = g_strdup (expires);
}

char *
e_zimbra_item_get_expires (EZimbraItem *item)
{
	g_return_val_if_fail (E_IS_ZIMBRA_ITEM (item), NULL);

	return item->priv->expires;
}

void
e_zimbra_item_set_delay_until (EZimbraItem *item, char *delay_until)
{
	g_return_if_fail (E_IS_ZIMBRA_ITEM (item));

	item->priv->delay_until = g_strdup (delay_until);
}

char *
e_zimbra_item_get_delay_until (EZimbraItem *item)
{
	g_return_val_if_fail (E_IS_ZIMBRA_ITEM (item), NULL);

	return item->priv->delay_until;
}

void
e_zimbra_item_set_source (EZimbraItem *item, char *source)
{
	g_return_if_fail (E_IS_ZIMBRA_ITEM (item));
	item->priv->source = g_strdup (source);
}

void
e_zimbra_item_set_content_type (EZimbraItem *item, const char *content_type)
{
	g_return_if_fail (E_IS_ZIMBRA_ITEM (item));

	if (item->priv->content_type)
		g_free (item->priv->content_type);
	item->priv->content_type= g_strdup (content_type);
}

char *
e_zimbra_item_get_content_type (EZimbraItem *item)
{
	g_return_val_if_fail (E_IS_ZIMBRA_ITEM (item), NULL);

	return item->priv->content_type ;
}

void
e_zimbra_item_set_link_info (EZimbraItem *item, EZimbraItemLinkInfo *info)
{
	g_return_if_fail (E_IS_ZIMBRA_ITEM (item));
	item->priv->link_info = info;
}

EZimbraItemLinkInfo *
e_zimbra_item_get_link_info (EZimbraItem *item)
{
	g_return_val_if_fail (E_IS_ZIMBRA_ITEM (item), NULL);
	return item->priv->link_info;
}


gboolean
e_zimbra_item_append_to_soap_message
	(
	EZimbraItem			*	item,
	EZimbraItemChangeType	type,
	xmlTextWriterPtr		msg
	)
{
	EZimbraItemPrivate	*	priv;
	gboolean				ok = FALSE;

	zimbra_check( E_IS_ZIMBRA_ITEM( item ), exit, ok = FALSE );

	priv = item->priv;

	switch (priv->item_type)
	{
		case E_ZIMBRA_ITEM_TYPE_APPOINTMENT:
		{
			ok = append_appointment_fields_to_soap_message( item, msg );
			zimbra_check( ok, exit, g_warning( "append_appointment_fields_to_soap_message" ) );
		}
		break;

		case E_ZIMBRA_ITEM_TYPE_ORGANISATION:
		case E_ZIMBRA_ITEM_TYPE_CONTACT:
		{
			append_contact_fields_to_soap_message( item, type, msg );
			ok = TRUE;
		}
		break;

		default:
		{
			zimbra_check( 0, exit, ok = FALSE; g_warning( G_STRLOC ": Unknown type for item" ) );
		}
		break;
	}

exit:

	return ok;
}


static icaltimezone*
xmltimezone_to_icaltimezone
	(
	xmlNode	*	node
	)
{
	icaltimezone	*	zone		=	NULL;
	icalcomponent	*	comp		=	NULL;
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

	zimbra_check( g_str_equal( node->name, "tz" ), exit, ok = FALSE );

	id = e_zimbra_xml_find_attribute( node, "id" );

	zone = lookup_icaltimezone( id );

	if ( !zone )
	{
		str = g_string_new( "BEGIN:VTIMEZONE\n" );
		g_string_append_printf( str, "TZID:%s\n", id );

		stdoff = e_zimbra_xml_find_attribute( node, "stdoff" );

		if ( ( dayoff = e_zimbra_xml_find_attribute( node, "dayoff" ) ) != NULL )
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
	
			g_string_append_printf( str, "BEGIN:STANDARD\n" );
			g_string_append_printf( str, "TZOFFSETFROM:%.2d%.2d\n", day_secs / 60, day_secs % 60 );
			g_string_append_printf( str, "TZOFFSETTO:%.2d%.2d\n", std_secs / 60, std_secs % 60 );
			g_string_append_printf( str, "DTSTART:16010101T%.2d%.2d%.2d\n", atoi( std_hour ), atoi( std_min ), atoi( std_sec ) );
			g_string_append_printf( str, "RRULE:FREQ=YEARLY;INTERVAL=1;BYDAY=%d%s;BYMONTH=%d\n", atoi( std_week ), days_of_week[ atoi( std_wkday ) ], atoi( std_mon ) );
			g_string_append_printf( str, "END:STANDARD\n" );

			day_node	= e_zimbra_xml_find_child_by_name( node, "daylight" );
			day_wkday	= e_zimbra_xml_find_attribute( day_node, "wkday" );
			day_mon		= e_zimbra_xml_find_attribute( day_node, "mon" );
			day_week	= e_zimbra_xml_find_attribute( day_node, "week" );
			day_sec		= e_zimbra_xml_find_attribute( day_node, "sec" );
			day_min		= e_zimbra_xml_find_attribute( day_node, "min" );
			day_hour	= e_zimbra_xml_find_attribute( day_node, "hour" );

			g_string_append_printf( str, "BEGIN:DAYLIGHT\n" );
			g_string_append_printf( str, "TZOFFSETFROM:%.2d%.2d\n", std_secs / 60, std_secs % 60 );
			g_string_append_printf( str, "TZOFFSETTO:%.2d%.2d\n", day_secs / 60, day_secs % 60 );
			g_string_append_printf( str, "DTSTART:16010101T%.2d%.2d%.2d\n", atoi( day_hour ), atoi( day_min ), atoi( day_sec ) );
			g_string_append_printf( str, "RRULE:FREQ=YEARLY;INTERVAL=1;BYDAY=%d%s;BYMONTH=%d\n", atoi( day_week ), days_of_week[ atoi( day_wkday ) ], atoi( day_mon ) );
			g_string_append_printf( str, "END:DAYLIGHT\n" );
		}
		else
		{
			int secs = atoi( stdoff );
	
			g_string_append_printf( str, "BEGIN:STANDARD\n" );
			g_string_append_printf( str, "TZOFFSETFROM:%.2d%.2d\n", secs / 60, secs % 60 );
			g_string_append_printf( str, "TZOFFSETTO:%.2d%.2d\n", secs / 60, secs % 60 );
			g_string_append_printf( str, "END:STANDARD\n" );
		}
	
		g_string_append_printf( str, "END:VTIMEZONE" );

		comp	= icalcomponent_new_from_string( str->str );
		zone	= icaltimezone_new();
	
		res = icaltimezone_set_component( zone, comp );

		if ( res == 0 )
		{
			icaltimezone_free( zone, 0 );
			zone = NULL;
		}

		if ( !g_zones )
		{
			g_zones = g_hash_table_new( g_str_hash, g_str_equal );
			zimbra_check( g_zones, exit, ok = FALSE );
		}

		g_hash_table_insert( g_zones, id, zone );
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

	return zone;
}


static icaltimezone*
lookup_icaltimezone
	(
	const char * tzid
	)
{
	icaltimezone * zone;

	zone = icaltimezone_get_builtin_timezone_from_tzid( tzid );

	if ( !zone && g_zones )
	{
		zone = g_hash_table_lookup( g_zones, tzid );
	}

	return zone;
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

	return TRUE;
}


static gboolean
icaltimezone_to_xmltimezone
	(
	const icaltimezone	*	zone,
	xmlTextWriterPtr		request
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
	int					rc;
	gboolean			ok						=	TRUE;

	comp = icaltimezone_get_component( ( icaltimezone* ) zone );

	prop = icalcomponent_get_first_property( comp, ICAL_TZID_PROPERTY );

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
				g_error( "unknown component" );
			}
			break;
		}
	}

	rc = xmlTextWriterStartElement( request, BAD_CAST "tz" );
	zimbra_check( rc != -1, exit, ok = FALSE );

	if ( standard && !daylight )
	{
		parse_tzoffset( std_tzoffsetfrom, &std_tzoffsetfrom_hour, &std_tzoffsetfrom_min );
		parse_tzoffset( std_tzoffsetto, &std_tzoffsetto_hour, &std_tzoffsetto_min );
		parse_dtstart( std_dtstart, &std_dtstart_hour, &std_dtstart_min, &std_dtstart_sec );

		rc = xmlTextWriterWriteFormatAttribute( request, BAD_CAST "stdoff", "%d", std_tzoffsetfrom_hour * 60 );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteAttribute( request, BAD_CAST "id", BAD_CAST id );
		zimbra_check( rc != -1, exit, ok = FALSE );
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

		rc = xmlTextWriterWriteFormatAttribute( request, BAD_CAST "dayoff", "%d", std_tzoffsetfrom_hour * 60 );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteFormatAttribute( request, BAD_CAST "stdoff", "%d", std_tzoffsetto_hour * 60 );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteAttribute( request, BAD_CAST "id", BAD_CAST id );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterStartElement( request, BAD_CAST "standard" );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteFormatAttribute( request, BAD_CAST "sec", "%d", std_dtstart_sec );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteFormatAttribute( request, BAD_CAST "hour", "%d", std_dtstart_hour );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteFormatAttribute( request, BAD_CAST "wkday", "%d", std_rrule_day );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteFormatAttribute( request, BAD_CAST "min", "%d", std_dtstart_min );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteFormatAttribute( request, BAD_CAST "mon", "%d", std_rrule_month );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteFormatAttribute( request, BAD_CAST "week", "%d", std_rrule_week );
		zimbra_check( rc != -1, exit, ok = FALSE );

		// </standard>

		rc = xmlTextWriterEndElement( request );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterStartElement( request, BAD_CAST "daylight" );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteFormatAttribute( request, BAD_CAST "sec", "%d", day_dtstart_sec );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteFormatAttribute( request, BAD_CAST "hour", "%d", day_dtstart_hour );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteFormatAttribute( request, BAD_CAST "wkday", "%d", day_rrule_day );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteFormatAttribute( request, BAD_CAST "min", "%d", day_dtstart_min );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteFormatAttribute( request, BAD_CAST "mon", "%d", day_rrule_month );
		zimbra_check( rc != -1, exit, ok = FALSE );

		rc = xmlTextWriterWriteFormatAttribute( request, BAD_CAST "week", "%d", day_rrule_week );
		zimbra_check( rc != -1, exit, ok = FALSE );

		// </daylight>

		rc = xmlTextWriterEndElement( request );
		zimbra_check( rc != -1, exit, ok = FALSE );
	}

	// </tz>

	rc = xmlTextWriterEndElement( request );
	zimbra_check( rc != -1, exit, ok = FALSE );

exit:

	return ok;
}
