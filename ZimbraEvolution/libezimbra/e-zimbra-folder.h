/* -*- Mode: C; tab-width: 4; indent-tabs-mode: t; c-basic-offset: 4 -*- */
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

#ifndef E_ZIMBRA_FOLDER_H
#define E_ZIMBRA_FOLDER_H


#include <glib-object.h>
#include <libxml/xmlwriter.h>
#include <libxml/parser.h>


G_BEGIN_DECLS


#define E_TYPE_ZIMBRA_FOLDER            (e_zimbra_folder_get_type ())
#define E_ZIMBRA_FOLDER(obj)            (G_TYPE_CHECK_INSTANCE_CAST ((obj), E_TYPE_ZIMBRA_FOLDER, EZimbraFolder))
#define E_ZIMBRA_FOLDER_CLASS(klass)    (G_TYPE_CHECK_CLASS_CAST ((klass), E_TYPE_ZIMBRA_FOLDER, EZimbraFolderClass))
#define E_IS_ZIMBRA_FOLDER(obj)         (G_TYPE_CHECK_INSTANCE_TYPE ((obj), E_TYPE_ZIMBRA_FOLDER))
#define E_IS_ZIMBRA_FOLDER_CLASS(klass) (G_TYPE_CHECK_CLASS_TYPE ((klass), E_TYPE_ZIMBRA_FOLDER))

typedef struct _EShUsers            	EShUsers;
typedef struct _EZimbraFolder        EZimbraFolder;
typedef struct _EZimbraFolderClass   EZimbraFolderClass;
typedef struct _EZimbraFolderPrivate EZimbraFolderPrivate;


struct _EZimbraFolder
{
	GObject						parent;
	EZimbraFolderPrivate	*	priv;
};


struct _EZimbraFolderClass
{
	GObjectClass parent_class;
};


struct _EShUsers
{
	char *email;
	int rights;
};


typedef enum
{
	E_ZIMBRA_FOLDER_TYPE_ROOT,
	E_ZIMBRA_FOLDER_TYPE_INBOX,
	E_ZIMBRA_FOLDER_TYPE_SENT,
	E_ZIMBRA_FOLDER_TYPE_CALENDAR,
	E_ZIMBRA_FOLDER_TYPE_CONTACTS,
	E_ZIMBRA_FOLDER_TYPE_DOCUMENTS,
	E_ZIMBRA_FOLDER_TYPE_QUERY,
	E_ZIMBRA_FOLDER_TYPE_CHECKLIST,
	E_ZIMBRA_FOLDER_TYPE_DRAFT,
	E_ZIMBRA_FOLDER_TYPE_CABINET,
	E_ZIMBRA_FOLDER_TYPE_TRASH,
	E_ZIMBRA_FOLDER_TYPE_JUNK,
	E_ZIMBRA_FOLDER_TYPE_FOLDER
} EZimbraFolderType;


typedef enum
{
	E_ZIMBRA_FOLDER_PERMISSIONS_READ	= ( 1 << 0 ),
	E_ZIMBRA_FOLDER_PERMISSIONS_WRITE	= ( 1 << 1 )
} EZimbraFolderPermissions;


typedef enum
{
	E_ZIMBRA_FOLDER_CHANGE_TYPE_UPDATE,
	E_ZIMBRA_FOLDER_CHANGE_TYPE_DELETE
} EZimbraFolderChangeType;


GType
e_zimbra_folder_get_type(void);


EZimbraFolder*
e_zimbra_folder_new_from_soap_parameter
	(
	xmlNode				*	node,
	const char			*	cache_folder
	);


gboolean
e_zimbra_folder_set_from_soap_parameter
	(
	EZimbraFolder	*	folder,
	xmlNode				*	param
	);


const char*
e_zimbra_folder_get_name
	(
	EZimbraFolder	*	folder
	);


void
e_zimbra_folder_set_name
	(
	EZimbraFolder	*	folder,
	const char			*	new_name
	);


const char*
e_zimbra_folder_get_id
	(
	EZimbraFolder	*	folder
	);


void
e_zimbra_folder_set_id
	(
	EZimbraFolder	*	folder,
	const char			*	new_id
	);


EZimbraFolderPermissions
e_zimbra_folder_get_permissions
	(
	EZimbraFolder	*	folder
	);


const char*
e_zimbra_folder_get_parent_id
	(
	EZimbraFolder	*	folder
	);


void
e_zimbra_folder_set_parent_id
	(
	EZimbraFolder	*	folder,
	const char			*	parent_id
	);


guint32
e_zimbra_folder_get_total_count
	(
	EZimbraFolder	*	folder
	);


guint32
e_zimbra_folder_get_unread_count
	(
	EZimbraFolder	*	folder
	);


gboolean
e_zimbra_folder_get_is_writable
	(
	EZimbraFolder	*	folder
	);


void
e_zimbra_folder_set_is_writable
	(
	EZimbraFolder	*	folder,
	gboolean				writable
	);


gboolean
e_zimbra_folder_is_root
	(
	EZimbraFolder	*	folder
	);


const char*
e_zimbra_folder_get_owner
	(
	EZimbraFolder	*	folder
	);


const char*
e_zimbra_folder_get_modified
	(
	EZimbraFolder	*	folder
	);


int
e_zimbra_folder_get_sequence
	(
	EZimbraFolder	*	folder
	);


gboolean
e_zimbra_folder_get_is_shared_by_me
	(
	EZimbraFolder	*	folder
	);


gboolean
e_zimbra_folder_get_is_shared_to_me
	(
	EZimbraFolder	*	folder
	);


int
e_zimbra_folder_get_rights
	(
	EZimbraFolder	*	folder,
	gchar				*	email
	);


EZimbraFolderType
e_zimbra_folder_get_folder_type
	(
	EZimbraFolder	*	folder
	);


void
e_zimbra_folder_get_user_list
	(
	EZimbraFolder	*	folder,
	GList				**	user_list
	);


void
e_zimbra_folder_add_changes
	(
	EZimbraFolder		*	self,
	EZimbraFolderChangeType	type,
	const char			*	ids
	);


gboolean
e_zimbra_folder_has_changes
	(
	EZimbraFolder		*	folder
	);


gboolean
e_zimbra_folder_get_changes
	(
	EZimbraFolder		*	folder,
	GPtrArray			**	updates,
	GPtrArray			**	deletes
	);


void
e_zimbra_folder_clr_changes
	(
	EZimbraFolder		*	folder
	);


G_END_DECLS


#endif
