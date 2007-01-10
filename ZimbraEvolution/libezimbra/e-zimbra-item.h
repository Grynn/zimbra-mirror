/* -*- Mode: C; tab-width: 8; indent-tabs-mode: t; c-basic-offset: 8 -*- */
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
 * Authors: Scott Herscher <scott.herscher@zimbra.com>
 * 
 * Copyright (C) 2006 Zimbra, Inc.
 * 
 */

#ifndef E_ZIMBRA_ITEM_H
#define E_ZIMBRA_ITEM_H

#include <libxml/xmlwriter.h>
#include <libical/icaltime.h>
#include "e-zimbra-folder.h"


G_BEGIN_DECLS


#define E_TYPE_ZIMBRA_ITEM            (e_zimbra_item_get_type ())
#define E_ZIMBRA_ITEM(obj)            (G_TYPE_CHECK_INSTANCE_CAST ((obj), E_TYPE_ZIMBRA_ITEM, EZimbraItem))
#define E_ZIMBRA_ITEM_CLASS(klass)    (G_TYPE_CHECK_CLASS_CAST ((klass), E_TYPE_ZIMBRA_ITEM, EZimbraItemClass))
#define E_IS_ZIMBRA_ITEM(obj)         (G_TYPE_CHECK_INSTANCE_TYPE ((obj), E_TYPE_ZIMBRA_ITEM))
#define E_IS_ZIMBRA_ITEM_CLASS(klass) (G_TYPE_CHECK_CLASS_TYPE ((klass), E_TYPE_ZIMBRA_ITEM))

typedef struct _EZimbraItem        EZimbraItem;
typedef struct _EZimbraItemClass   EZimbraItemClass;
typedef struct _EZimbraItemPrivate EZimbraItemPrivate;


typedef enum
{
	E_ZIMBRA_ITEM_TYPE_MAIL,
	E_ZIMBRA_ITEM_TYPE_APPOINTMENT,
	E_ZIMBRA_ITEM_TYPE_TASK,
	E_ZIMBRA_ITEM_TYPE_CONTACT,
	E_ZIMBRA_ITEM_TYPE_GROUP,
	E_ZIMBRA_ITEM_TYPE_ORGANISATION,
	E_ZIMBRA_ITEM_TYPE_RESOURCE,
	E_ZIMBRA_ITEM_TYPE_CATEGORY,
	E_ZIMBRA_ITEM_TYPE_NOTIFICATION, 
	E_ZIMBRA_ITEM_TYPE_NOTE,
	E_ZIMBRA_ITEM_TYPE_UNKNOWN
} EZimbraItemType;


typedef enum
{
	E_ZIMBRA_ITEM_CHANGE_TYPE_ADD,
	E_ZIMBRA_ITEM_CHANGE_TYPE_UPDATE,
	E_ZIMBRA_ITEM_CHANGE_TYPE_DELETE,
	E_ZIMBRA_ITEM_CHNAGE_TYPE_UNKNOWN
} EZimbraItemChangeType;


typedef enum
{
	E_ZIMBRA_ITEM_STAT_TENTATIVE 	= 1<<0,
	E_ZIMBRA_ITEM_STAT_CONFIRMED	= 1<<1,
	E_ZIMBRA_ITEM_STAT_COMPLETED	= 1<<2,
	E_ZIMBRA_ITEM_STAT_NEEDSACTION  = 1<<3,
	E_ZIMBRA_ITEM_STAT_CANCELLED	= 1<<4,
	E_ZIMBRA_ITEM_STAT_INPROCESS 	= 1<<5,
	E_ZIMBRA_ITEM_STAT_DRAFT  		= 1<<6,
	E_ZIMBRA_ITEM_STAT_FINAL		= 1<<7,
	E_ZIMBRA_ITEM_STAT_NONE      	= 1<<31
} EZimbraItemStatus;


typedef	enum
{
	E_ZIMBRA_ITEM_ROLE_CHAIR,
	E_ZIMBRA_ITEM_ROLE_REQUIRED_PARTICIPANT,
	E_ZIMBRA_ITEM_ROLE_OPTIONAL_PARTICIPANT,
	E_ZIMBRA_ITEM_ROLE_NON_PARTICIPANT
} EZimbraItemRole;


typedef enum
{
	E_ZIMBRA_ITEM_PART_STAT_NEEDSACTION	= 1<<0,
	E_ZIMBRA_ITEM_PART_STAT_ACCEPTED	= 1<<1,
	E_ZIMBRA_ITEM_PART_STAT_DECLINED  	= 1<<2,
	E_ZIMBRA_ITEM_PART_STAT_TENTATIVE 	= 1<<3,
	E_ZIMBRA_ITEM_PART_STAT_DELEGATED	= 1<<4,
	E_ZIMBRA_ITEM_PART_STAT_COMPLETED	= 1<<5,
	E_ZIMBRA_ITEM_PART_STAT_INPROCESS 	= 1<<6,
	E_ZIMBRA_ITEM_PART_STAT_NONE      	= 1<<31
} EZimbraItemPartStatus;


typedef enum
{
	E_ZIMBRA_ITEM_RECURRENCE_FREQUENCY_NONE		=	0,
	E_ZIMBRA_ITEM_RECURRENCE_FREQUENCY_DAILY	=	( 1 << 0 ),
	E_ZIMBRA_ITEM_RECURRENCE_FREQUENCY_WEEKLY	=	( 1 << 1 ),
	E_ZIMBRA_ITEM_RECURRENCE_FREQUENCY_MONTHLY	=	( 1 << 2 ),
	E_ZIMBRA_ITEM_RECURRENCE_FREQUENCY_YEARLY	=	( 1 << 3 )
} EZimbraItemRecurrenceFrequency;


typedef enum
{
	E_ZIMBRA_ITEM_RECURRENCE_WEEKDAY_NONE		=	0,
	E_ZIMBRA_ITEM_RECURRENCE_WEEKDAY_SUNDAY,
	E_ZIMBRA_ITEM_RECURRENCE_WEEKDAY_MONDAY,
	E_ZIMBRA_ITEM_RECURRENCE_WEEKDAY_TUESDAY,
	E_ZIMBRA_ITEM_RECURRENCE_WEEKDAY_WEDNESDAY,
	E_ZIMBRA_ITEM_RECURRENCE_WEEKDAY_THURSDAY,
	E_ZIMBRA_ITEM_RECURRENCE_WEEKDAY_FRIDAY,
	E_ZIMBRA_ITEM_RECURRENCE_WEEKDAY_SATURDAY
} EZimbraItemRecurrenceWeekday;


#define E_ZIMBRA_ITEM_RECUR_OCCURRENCE_TYPE_FIRST  "First" 
#define E_ZIMBRA_ITEM_RECUR_OCCURRENCE_TYPE_SECOND "Second" 
#define E_ZIMBRA_ITEM_RECUR_OCCURRENCE_TYPE_THIRD  "Third" 
#define E_ZIMBRA_ITEM_RECUR_OCCURRENCE_TYPE_FOURTH "Fourth" 
#define E_ZIMBRA_ITEM_RECUR_OCCURRENCE_TYPE_FIFTH  "Fifth"
#define E_ZIMBRA_ITEM_RECUR_OCCURRENCE_TYPE_LAST   "Last"

#define E_ZIMBRA_ITEM_BY_DAY_SIZE			364 /* 7 days * 52 weeks */
#define E_ZIMBRA_ITEM_BY_MONTHDAY_SIZE		32
#define E_ZIMBRA_ITEM_BY_YEARDAY_SIZE		367
#define E_ZIMBRA_ITEM_BY_WEEKNO_SIZE		54
#define E_ZIMBRA_ITEM_BY_MONTH_SIZE			13
#define E_ZIMBRA_ITEM_BY_SET_POS_SIZE		367


typedef struct
{
	EZimbraItemRecurrenceFrequency	frequency;
	int								count;
	icaltimetype					until;
	int								interval;
	short							by_day[E_ZIMBRA_ITEM_BY_DAY_SIZE];
	short							by_month_day[E_ZIMBRA_ITEM_BY_MONTHDAY_SIZE];
	short							by_year_day[E_ZIMBRA_ITEM_BY_YEARDAY_SIZE];
	short							by_week_no[E_ZIMBRA_ITEM_BY_WEEKNO_SIZE];
	short							by_month[E_ZIMBRA_ITEM_BY_MONTH_SIZE];
	short							by_set_pos[E_ZIMBRA_ITEM_BY_SET_POS_SIZE];
	EZimbraItemRecurrenceWeekday	week_start;
} EZimbraItemRecurrenceRule;


#define E_ZIMBRA_ITEM_RECUR_END_MARKER  0x7f7f
#define E_ZIMBRA_ITEM_RECUR_MAX_BYTE	0x7f


struct _EZimbraItem
{
	GObject					parent;
	EZimbraItemPrivate	*	priv;
};


struct _EZimbraItemClass
{
	GObjectClass parent_class;
};


/* structures defined to hold contact item fields */
typedef struct
{
	char *name_prefix;
	char *first_name;
	char *middle_name;
	char *last_name;
	char *name_suffix;
} FullName;


typedef struct
{
	char *street_address;
	char *location;
	char *city;
	char *state;
	char *postal_code;
	char *country;
} PostalAddress;

typedef struct
{
	char *service;
	char *address;
} IMAddress;


typedef struct
{
	char *id;
	char *email;
	char *name;
} EGroupMember;


typedef struct
{
	char *email;
	char *display_name;
} EZimbraItemOrganizer;


typedef enum
{
	E_ZIMBRA_ITEM_RANGE_SINGLE,
	E_ZIMBRA_ITEM_RANGE_THISPRIOR,
	E_ZIMBRA_ITEM_RANGE_THISFUTURE
} EZimbraItemRange;


typedef struct
{
	char *id ;
	char *name ;
	char *item_reference;
	char *contentid;
	char *contentType ;
	int size ;
	char *date ;
	char *data ;
} EZimbraItemAttachment ;


typedef enum
{
	E_ZIMBRA_ITEM_NOTIFY_NONE,
	E_ZIMBRA_ITEM_NOTIFY_MAIL
} EZimbraItemReturnNotify;


typedef enum
{
	E_ZIMBRA_ITEM_NONE,
	E_ZIMBRA_ITEM_DELIVERED,
	E_ZIMBRA_ITEM_DELIVERED_OPENED,
	E_ZIMBRA_ITEM_ALL
} EZimbraItemTrack;


typedef struct
{
	char *id;
	char *type;
	char *thread;
} EZimbraItemLinkInfo;


typedef struct
{
	char *item_id;
	char *ical_id;
	char *recur_key;
} EZimbraItemCalId;


GType       e_zimbra_item_get_type (void);
EZimbraItem    *e_zimbra_item_new_empty (void);

EZimbraItem*
e_zimbra_item_new_from_soap_parameter
	(
	gpointer		cnc,
	EZimbraItemType	type,
	xmlNode		*	node
	);

EZimbraItemType e_zimbra_item_get_item_type (EZimbraItem *item);
void        e_zimbra_item_set_item_type (EZimbraItem *item, EZimbraItemType new_type);
const char *e_zimbra_item_get_folder_id (EZimbraItem *item);
void        e_zimbra_item_set_folder_id (EZimbraItem *item, const char *new_id);
const char *e_zimbra_item_get_icalid (EZimbraItem *item);
void        e_zimbra_item_set_icalid (EZimbraItem *item, const char *new_icalid);
const char *e_zimbra_item_get_id (EZimbraItem *item);
void        e_zimbra_item_set_id (EZimbraItem *item, const char *new_id);
const char *e_zimbra_item_get_rev( EZimbraItem * item );
void		e_zimbra_item_set_rev(EZimbraItem *item, const char * new_rev);
char       *e_zimbra_item_get_delivered_date (EZimbraItem *item);
void        e_zimbra_item_set_delivered_date (EZimbraItem *item, const char *new_date);

const icaltimetype*
e_zimbra_item_get_start_date
	(
	EZimbraItem * item
	);


void
e_zimbra_item_set_start_date
	(
	EZimbraItem			*	item,
	const icaltimetype	*	start_date
	);


const icaltimetype*
e_zimbra_item_get_end_date
	(
	EZimbraItem	*	item
	);


void
e_zimbra_item_set_end_date
	(
	EZimbraItem			*	item,
	const icaltimetype	*	end_date
	);


const char *e_zimbra_item_get_subject (EZimbraItem *item);
void        e_zimbra_item_add_detached_item( EZimbraItem * item, EZimbraItem * detached_item );
GList	*   e_zimbra_item_peek_detached_items( EZimbraItem * item );
void        e_zimbra_item_set_subject (EZimbraItem *item, const char *new_subject);
const char *e_zimbra_item_get_message (EZimbraItem *item);
void        e_zimbra_item_set_message (EZimbraItem *item, const char *new_message);
const char *e_zimbra_item_get_place (EZimbraItem *item);
void        e_zimbra_item_set_place (EZimbraItem *item, const char *new_place);
gboolean    e_zimbra_item_get_completed (EZimbraItem *item);
void        e_zimbra_item_set_completed (EZimbraItem *item, gboolean new_completed);
gboolean    e_zimbra_item_get_is_allday_event (EZimbraItem *item);
void	    e_zimbra_item_set_is_allday_event (EZimbraItem *item, gboolean is_allday);	
char*       e_zimbra_item_get_field_value (EZimbraItem *item, char *field_name);
void        e_zimbra_item_set_field_value (EZimbraItem *item, char *field_name, char* field_value);
GList*      e_zimbra_item_get_email_list (EZimbraItem *item);
void        e_zimbra_item_set_email_list (EZimbraItem *item, GList *email_list);
FullName*   e_zimbra_item_get_full_name (EZimbraItem *item);
void        e_zimbra_item_set_full_name (EZimbraItem *item, FullName* full_name);
GList*      e_zimbra_item_get_member_list (EZimbraItem *item);
void        e_zimbra_item_set_member_list (EZimbraItem *item, GList *list);
PostalAddress* e_zimbra_item_get_address (EZimbraItem *item, char *address_type);
void        e_zimbra_item_set_address (EZimbraItem *item, char *addres_type, PostalAddress *address);
GList*      e_zimbra_item_get_im_list (EZimbraItem *item);
void        e_zimbra_item_set_im_list (EZimbraItem *item, GList *im_list);
void        e_zimbra_item_set_categories (EZimbraItem *item, GList *category_list);
GList*      e_zimbra_item_get_categories (EZimbraItem *item);
void 	    e_zimbra_item_set_to (EZimbraItem *item, const char *to) ;
const char* e_zimbra_item_get_to (EZimbraItem *item) ;
const char *e_zimbra_item_get_msg_content_type (EZimbraItem *item) ;
EZimbraItemStatus     e_zimbra_item_get_status (EZimbraItem *item);
void				e_zimbra_item_set_status( EZimbraItem * item, EZimbraItemStatus status );
void	    e_zimbra_item_set_content_type (EZimbraItem *item, const char *content_type) ;
void	    e_zimbra_item_set_link_info (EZimbraItem *item, EZimbraItemLinkInfo *info);
EZimbraItemLinkInfo *e_zimbra_item_get_link_info (EZimbraItem *item);
char	    *e_zimbra_item_get_content_type (EZimbraItem *item) ;
const char *e_zimbra_item_get_msg_body_id (EZimbraItem *item);
int	    e_zimbra_item_get_mail_size (EZimbraItem *item);
void e_zimbra_item_set_change (EZimbraItem *item, EZimbraItemChangeType change_type, char *field_name, gpointer field_value);
void e_zimbra_item_set_category_name (EZimbraItem *item, char *cateogry_name);
char* e_zimbra_item_get_category_name (EZimbraItem *item);
void e_zimbra_item_set_sendoptions (EZimbraItem *item, gboolean set);
void e_zimbra_item_set_reply_request (EZimbraItem *item, gboolean set);
gboolean e_zimbra_item_get_reply_request (EZimbraItem *item);
void e_zimbra_item_set_reply_within (EZimbraItem *item, char *reply_within);
char *e_zimbra_item_get_reply_within (EZimbraItem *item);
void e_zimbra_item_set_track_info (EZimbraItem *item, EZimbraItemTrack track_info);
EZimbraItemTrack e_zimbra_item_get_track_info (EZimbraItem *item);
void e_zimbra_item_set_autodelete (EZimbraItem *item, gboolean set);
gboolean e_zimbra_item_get_autodelete (EZimbraItem *item);
void e_zimbra_item_set_notify_completed (EZimbraItem *item, EZimbraItemReturnNotify notify);
EZimbraItemReturnNotify e_zimbra_item_get_notify_completed (EZimbraItem *item);
void e_zimbra_item_set_notify_accepted (EZimbraItem *item, EZimbraItemReturnNotify notify);
EZimbraItemReturnNotify e_zimbra_item_get_notify_accepted (EZimbraItem *item);
void e_zimbra_item_set_notify_declined (EZimbraItem *item, EZimbraItemReturnNotify notify);
EZimbraItemReturnNotify e_zimbra_item_get_notify_declined (EZimbraItem *item);
void e_zimbra_item_set_notify_opened (EZimbraItem *item, EZimbraItemReturnNotify notify);
EZimbraItemReturnNotify e_zimbra_item_get_notify_opened (EZimbraItem *item);
void e_zimbra_item_set_notify_deleted (EZimbraItem *item, EZimbraItemReturnNotify notify);
EZimbraItemReturnNotify e_zimbra_item_get_notify_deleted (EZimbraItem *item);
void e_zimbra_item_set_expires (EZimbraItem *item, char *expires);
char *e_zimbra_item_get_expires (EZimbraItem *item);
void e_zimbra_item_set_delay_until (EZimbraItem *item, char *delay_until);
char *e_zimbra_item_get_delay_until (EZimbraItem *item);
void e_zimbra_item_free_cal_id (EZimbraItemCalId *calid);


#define E_ZIMBRA_ITEM_CLASSIFICATION_PUBLIC       "Public"
#define E_ZIMBRA_ITEM_CLASSIFICATION_PRIVATE      "Private"
#define E_ZIMBRA_ITEM_CLASSIFICATION_CONFIDENTIAL "Confidential"

const char *e_zimbra_item_get_classification (EZimbraItem *item);
void        e_zimbra_item_set_classification (EZimbraItem *item, const char *new_class);

#define E_ZIMBRA_ITEM_ACCEPT_LEVEL_BUSY          "Busy"
#define E_ZIMBRA_ITEM_ACCEPT_LEVEL_OUT_OF_OFFICE "OutOfOffice"
#define E_ZIMBRA_ITEM_ACCEPT_LEVEL_FREE	     "Free" 	

const char *e_zimbra_item_get_accept_level (EZimbraItem *item);
void        e_zimbra_item_set_accept_level (EZimbraItem *item, const char *new_level);

#define E_ZIMBRA_ITEM_PRIORITY_HIGH     "High"
#define E_ZIMBRA_ITEM_PRIORITY_STANDARD "Standard"
#define E_ZIMBRA_ITEM_PRIORITY_LOW      "Low"

const char *e_zimbra_item_get_priority (EZimbraItem *item);
void        e_zimbra_item_set_priority (EZimbraItem *item, const char *new_priority);

const char *e_zimbra_item_get_task_priority (EZimbraItem *item);
void        e_zimbra_item_set_task_priority (EZimbraItem *item, const char *new_priority);

GSList *e_zimbra_item_get_recipient_list (EZimbraItem *item);
void e_zimbra_item_set_recipient_list (EZimbraItem *item, GSList *new_recipient_list);

EZimbraItemOrganizer *e_zimbra_item_get_organizer (EZimbraItem *item);
void e_zimbra_item_set_organizer (EZimbraItem  *item, EZimbraItemOrganizer *organizer);

GSList * e_zimbra_item_get_attach_id_list (EZimbraItem *item) ;
void e_zimbra_item_set_attach_id_list (EZimbraItem *item, GSList *attach_list) ;


GSList *e_zimbra_item_get_recurrence_dates (EZimbraItem *item);
void e_zimbra_item_set_recurrence_dates (EZimbraItem  *item, GSList *new_recurrence_dates);

GSList *e_zimbra_item_get_exdate_list (EZimbraItem *item);
void e_zimbra_item_set_exdate_list (EZimbraItem  *item, GSList *new_exdate_list);

void e_zimbra_item_set_rrule (EZimbraItem *item, EZimbraItemRecurrenceRule *rrule);
EZimbraItemRecurrenceRule *e_zimbra_item_get_rrule (EZimbraItem *item);
		

const icaltimetype*
e_zimbra_item_get_rid
	(
	EZimbraItem	*	item
	);


void
e_zimbra_item_set_rid
	(
	EZimbraItem			*	item,
	const icaltimetype	*	rid
	);


GSList * e_zimbra_item_get_attach_id_list (EZimbraItem *item) ;
void e_zimbra_item_set_attach_id_list (EZimbraItem *item, GSList *attach_list) ;

void e_zimbra_item_set_source (EZimbraItem *item, char *source) ;

int e_zimbra_item_get_trigger (EZimbraItem *item);
void e_zimbra_item_set_trigger (EZimbraItem *item, int trigger);


typedef struct
{
	char *email;
	char *display_name;
	gboolean status_enabled;
	char *delivered_date;
	char *opened_date;
	char *accepted_date;
	char *deleted_date;
	char *declined_date;
	char *completed_date;
	char *undelivered_date;
	EZimbraItemRole			role;
	EZimbraItemPartStatus	status;
} EZimbraItemRecipient;


gboolean
e_zimbra_item_append_to_soap_message
	(
	EZimbraItem			*	item,
	EZimbraItemChangeType	type,
	xmlTextWriterPtr		msg
	);


G_END_DECLS


#endif
