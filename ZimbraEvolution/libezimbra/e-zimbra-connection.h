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

#ifndef E_ZIMBRA_CONNECTION_H
#define E_ZIMBRA_CONNECTION_H

#include <glib-object.h>
#include <libedataserver/e-source.h>
#include <libxml/xmlwriter.h>
#include <libxml/parser.h>
#include "e-zimbra-folder.h"
#include "e-zimbra-item.h"
#include <time.h>

G_BEGIN_DECLS

#define E_TYPE_ZIMBRA_CONNECTION            (e_zimbra_connection_get_type ())
#define E_ZIMBRA_CONNECTION(obj)            (G_TYPE_CHECK_INSTANCE_CAST ((obj), E_TYPE_ZIMBRA_CONNECTION, EZimbraConnection))
#define E_ZIMBRA_CONNECTION_CLASS(klass)    (G_TYPE_CHECK_CLASS_CAST ((klass), E_TYPE_ZIMBRA_CONNECTION, EZimbraConnectionClass))
#define E_IS_ZIMBRA_CONNECTION(obj)         (G_TYPE_CHECK_INSTANCE_TYPE ((obj), E_TYPE_ZIMBRA_CONNECTION))
#define E_IS_ZIMBRA_CONNECTION_CLASS(klass) (G_TYPE_CHECK_CLASS_TYPE ((klass), E_TYPE_ZIMBRA_CONNECTION))

typedef struct _EZimbraConnection        EZimbraConnection;
typedef struct _EZimbraConnectionClass   EZimbraConnectionClass;
typedef struct _EZimbraConnectionPrivate EZimbraConnectionPrivate;

struct _EZimbraConnection
{
	GObject							parent;
	EZimbraConnectionPrivate	*	priv;
};


struct _EZimbraConnectionClass
{
	GObjectClass parent_class;
};


typedef enum
{
	E_ZIMBRA_CONNECTION_STATUS_OK						= 0,
	E_ZIMBRA_CONNECTION_STATUS_INVALID_CONNECTION,
	E_ZIMBRA_CONNECTION_STATUS_INVALID_OBJECT,
	E_ZIMBRA_CONNECTION_STATUS_INVALID_RESPONSE,
	E_ZIMBRA_CONNECTION_STATUS_NO_RESPONSE,
	E_ZIMBRA_CONNECTION_STATUS_OBJECT_NOT_FOUND,
	E_ZIMBRA_CONNECTION_STATUS_AUTH_FAILED,
	E_ZIMBRA_CONNECTION_STATUS_UNKNOWN_USER,
	E_ZIMBRA_CONNECTION_STATUS_BAD_PARAMETER,
	E_ZIMBRA_CONNECTION_STATUS_ITEM_ALREADY_ACCEPTED,
	E_ZIMBRA_CONNECTION_STATUS_NO_SUCH_ITEM,
	E_ZIMBRA_CONNECTION_STATUS_REDIRECT,
	E_ZIMBRA_CONNECTION_STATUS_OTHER,
	E_ZIMBRA_CONNECTION_STATUS_UNKNOWN
} EZimbraConnectionStatus;


typedef	EZimbraConnectionStatus	( *EZimbraConnectionClientSyncFunc )
	(
	gpointer		client,
	const char	*	name,
	time_t			sync_request_time,
	time_t			sync_response_time,
	GPtrArray	*	zcs_update_ids,
	GPtrArray	*	zcs_delete_ids
	);


#define E_ZIMBRA_CURSOR_POSITION_CURRENT "current"
#define E_ZIMBRA_CURSOR_POSITION_START "start"
#define E_ZIMBRA_CURSOR_POSITION_END "end"

GType          e_zimbra_connection_get_type (void);

EZimbraConnection*
e_zimbra_connection_new
	(
	ESource		*	source,
	const char	*	username,
	const char	*	password
	);


gboolean
e_zimbra_connection_register_client
	(
	EZimbraConnection			*	cnc,
	const char					*	folder_id,
	gpointer						handle,
	EZimbraConnectionClientSyncFunc	sync_func
	);


void
e_zimbra_connection_unregister_client
	(
	EZimbraConnection	*	cnc,
	const char			*	folder_id
	);


gboolean
e_zimbra_connection_sync
	(
	EZimbraConnection	*	cnc
	);


const char*
e_zimbra_connection_get_error_message
	(
	EZimbraConnectionStatus status
	);


EZimbraConnectionStatus
e_zimbra_connection_logout
	(
	EZimbraConnection	*	cnc
	);


EZimbraFolder*
e_zimbra_connection_peek_folder_by_id
	(
	EZimbraConnection	*	cnc,
	const char			*	id
	);


EZimbraConnectionStatus
e_zimbra_connection_peek_folders_by_type
	(
	EZimbraConnection	*	cnc,
	EZimbraFolderType		type,
	GList				**	folders
	);


void
e_zimbra_connection_free_folders
	(
	GList * folders
	);


char*
e_zimbra_connection_get_folder_id
	(
	EZimbraConnection	*	cnc,
	const char			*	name
	);


const char*
e_zimbra_connection_get_uri
	(
	EZimbraConnection	*	cnc
	);


const char*
e_zimbra_connection_get_session_id
	(
	EZimbraConnection	*	cnc
	);


const char*
e_zimbra_connection_get_user_name
	(
	EZimbraConnection	*	cnc
	);


const char*
e_zimbra_connection_get_user_email
	(
	EZimbraConnection	*	cnc
	);


const char*
e_zimbra_connection_get_user_uuid
	(
	EZimbraConnection	*	cnc
	);
	

const char*
e_zimbra_connection_get_version
	(
	EZimbraConnection	*	cnc
	);


time_t
e_zimbra_connection_get_date_from_string
	(
	const char			*	dtstring
	);


char*
e_zimbra_connection_format_date_string
	(
	const char			*	dtstring
	);


EZimbraConnectionStatus
e_zimbra_connection_get_item
	(
	EZimbraConnection	*	cnc,
	EZimbraItemType			type,
	const char			*	zid,
	EZimbraItem			**	item
	);


EZimbraConnectionStatus
e_zimbra_connection_get_items
	(
	EZimbraConnection	*	cnc,
	EZimbraItemType			type,
	GPtrArray		 	*	ids,
	GPtrArray			**	items
	);


EZimbraConnectionStatus
e_zimbra_connection_create_item
	(
	EZimbraConnection	*	cnc,
	EZimbraItem			*	item,
	char				**	id,
	char				**	rev
	);


EZimbraConnectionStatus
e_zimbra_connection_modify_item
	(
	EZimbraConnection	*	cnc,
	EZimbraItem			*	item,
	const char			*	id,
	char				**	rev
	);


EZimbraConnectionStatus
e_zimbra_connection_remove_item
	(
	EZimbraConnection	*	cnc,
	const char			*	container,
	EZimbraItemType			type,
	const char			*	id
	);


EZimbraConnectionStatus
e_zimbra_connection_remove_items
	(
	EZimbraConnection	*	cnc,
	const char			*	container,
	EZimbraItemType			type,
	GPtrArray			*	ids
	);


char*
e_zimbra_connection_uid_to_folder_id
	(
	EZimbraConnection	*	cnc,
	const char			*	uid
	);


EZimbraConnectionStatus
e_zimbra_connection_get_folders_by_type
	(
	EZimbraConnection	*	cnc,
	EZimbraFolderType		type,
	GList				**	list
	);


EZimbraConnectionStatus
e_zimbra_connection_create_folder
	(
	EZimbraConnection	*	cnc,
	const char			*	parent_name,
	ESource				*	source,
	EZimbraFolderType		folder_type,
	char				**	folder_id,
	char				**	rev
	);


EZimbraConnectionStatus
e_zimbra_connection_rename_folder
	(
	EZimbraConnection	*	cnc,
	const char			*	folder_id,
	const char			*	new_name,
	char				**	rev
	);


EZimbraConnectionStatus
e_zimbra_connection_delete_folder
	(
	EZimbraConnection	*	cnc,
	const char			*	folder_id
	);


EZimbraConnectionStatus
e_zimbra_connection_get_message
	(
	EZimbraConnection	*	cnc,
	const char			*	inv_id,
	char				**	message
	);
	

EZimbraConnectionStatus
e_zimbra_connection_get_page
	(
	EZimbraConnection	*	cnc,
	const char			*	url,
	char				**	page
	);


const char*
e_zimbra_connection_get_hostname
	(
	EZimbraConnection	*	cnc
	);


gboolean
e_zimbra_connection_use_ssl
	(
	EZimbraConnection	*	cnc
	);


int
e_zimbra_connection_get_port
	(
	EZimbraConnection	*	cnc
	);


gboolean
e_zimbra_connection_zombie
	(
	EZimbraConnection	*	cnc
	);


G_END_DECLS


#endif
