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
 * Author: 
 *  Scott Herscher <scott.herscher@zimbra.com>
 *
 * Copyright 2006, Zimbra, Inc.
 *
 */

#ifdef HAVE_CONFIG_H
#include <config.h>
#endif
#include <string.h>
#include "e-zimbra-folder.h"
#include "e-zimbra-utils.h"
#include "e-zimbra-debug.h"
#include "e-zimbra-xml.h"
#include <libedataserver/e-file-cache.h>
#include <glog/glog.h>


struct _EZimbraFolderPrivate
{
	char					*	name;
	char					*	id;
	char					*	parent;
	EFileCache				*	cache;
	guint32						unread;
	guint32						total;
	int							sequence;
	char					*	owner;	
	GList					*	user_list;	
	char					*	modified;
	EZimbraFolderType			type;
	EZimbraFolderPermissions	permissions;
	gboolean					is_root ;
	gboolean					is_writable;
	gboolean					is_shared_by_me;   
	gboolean					is_shared_to_me;
	gboolean					dirty;
};

static GObjectClass *parent_class = NULL;


static void
free_node(EShUsers *user)
{
	if(user){
		g_free(user->email);
		user->email = NULL;
	}
	return ;
}

static void
e_zimbra_folder_dispose (GObject *object)
{
	EZimbraFolder *folder = (EZimbraFolder *) object;

	g_return_if_fail (E_IS_ZIMBRA_FOLDER (folder));

	if (parent_class->dispose)
		(* parent_class->dispose) (object);
}

static void
e_zimbra_folder_finalize (GObject *object)
{
	EZimbraFolder *folder = (EZimbraFolder *) object;
	EZimbraFolderPrivate *priv;

	g_return_if_fail (E_IS_ZIMBRA_FOLDER (folder));

	priv = folder->priv;
	if (priv) {
		if (priv->name) {
			g_free (priv->name);
			priv->name = NULL;
		}

		if (priv->id) {
			g_free (priv->id);
			priv->id = NULL;
		}

		if (priv->parent) {
			g_free (priv->parent);
			priv->parent = NULL;
		}

		if (priv->owner) {
			g_free (priv->owner);
			priv->owner = NULL;
		}

		if (priv->modified) {
			g_free (priv->modified);
			priv->modified = NULL;
		}

		if(priv->user_list) {
			g_list_foreach (priv->user_list,(GFunc) free_node, NULL);
			g_list_free (priv->user_list);
			priv->user_list = NULL;
		}

		g_free (priv);
		folder->priv = NULL;
	}

	if (parent_class->finalize)
		(* parent_class->finalize) (object);
}

static void
e_zimbra_folder_class_init (EZimbraFolderClass *klass)
{
	GObjectClass *object_class = G_OBJECT_CLASS (klass);

	parent_class = g_type_class_peek_parent (klass);

	object_class->dispose = e_zimbra_folder_dispose;
	object_class->finalize = e_zimbra_folder_finalize;
}

static void
e_zimbra_folder_init (EZimbraFolder *folder, EZimbraFolderClass *klass)
{
	EZimbraFolderPrivate *priv;

	/* allocate internal structure */
	priv = g_new0 (EZimbraFolderPrivate, 1);
	priv->is_writable = TRUE;
	folder->priv = priv;
	folder->priv->dirty = FALSE;
}


GType
e_zimbra_folder_get_type (void)
{
	static GType type = 0;

	if (!type) {
		static GTypeInfo info = {
                        sizeof (EZimbraFolderClass),
                        (GBaseInitFunc) NULL,
                        (GBaseFinalizeFunc) NULL,
                        (GClassInitFunc) e_zimbra_folder_class_init,
                        NULL, NULL,
                        sizeof (EZimbraFolder),
                        0,
                        (GInstanceInitFunc) e_zimbra_folder_init
                };
		type = g_type_register_static (G_TYPE_OBJECT, "EZimbraFolder", &info, 0);
	}

	return type;
}


EZimbraFolder *
e_zimbra_folder_new_from_soap_parameter
	(
	xmlNode		*	node,
	const char	*	cache_folder
	)
{
	EZimbraFolder	*	self		= NULL;
	char			*	filename	= NULL;
	gboolean			ok;
	int					err			= 0;

	zimbra_check( node, exit, err = -1 );

	self = g_object_new( E_TYPE_ZIMBRA_FOLDER, NULL );
	zimbra_check( self, exit, err = -1 );

	ok = e_zimbra_folder_set_from_soap_parameter( self, node );
	zimbra_check( ok, exit, err = -1 );

	if ( ( self->priv->type == E_ZIMBRA_FOLDER_TYPE_CALENDAR ) || ( self->priv->type == E_ZIMBRA_FOLDER_TYPE_CONTACTS ) )
	{
		filename = g_build_filename( cache_folder, self->priv->id, "cache.xml", NULL );
		zimbra_check( filename, exit, err = -1 );

		self->priv->cache = e_file_cache_new( filename );
		zimbra_check( self->priv->cache, exit, err = -1 );
	}

exit:

	if ( filename )
	{
		g_free( filename );
	}

	if ( err && self )
	{
		g_object_unref( self );
		self = NULL;
	}

	return self;
}


gboolean
e_zimbra_folder_set_from_soap_parameter
	(	
	EZimbraFolder	*	folder,
	xmlNode			*	node
	)
{
	char	*	value;
	gboolean	ok = FALSE;

	zimbra_check( folder, exit, ok = FALSE );
	zimbra_check( node, exit, ok = FALSE );

	// Retrieve the name

	if ( ( value = e_zimbra_xml_find_attribute( node, "name" ) ) != NULL )
	{
		e_zimbra_folder_set_name( folder, ( const char* ) value );
		g_free( value );
	}
	else
	{
		e_zimbra_folder_set_name( folder, "" );
	}

	// Retrieve the ID

	value = e_zimbra_xml_find_attribute( node, "id" );
	zimbra_check( value, exit, ok = FALSE );
	e_zimbra_folder_set_id( folder, ( const char* ) value );
	g_free( value );

	// Retrieve the parent folder id

	if ( ( value = e_zimbra_xml_find_attribute( node, "l" ) ) != NULL )
	{
		e_zimbra_folder_set_parent_id( folder, value );
		g_free( value );
	}

	// Retrieve the folder type

	value = e_zimbra_xml_find_attribute( node, "view" );

	if ( value && g_str_equal( value, "appointment" ) )
	{
		folder->priv->type = E_ZIMBRA_FOLDER_TYPE_CALENDAR ;
	}
	else if ( value && g_str_equal( value, "contact" ) )
	{
		folder->priv->type = E_ZIMBRA_FOLDER_TYPE_CONTACTS ;
	}
	else if ( g_str_equal( e_zimbra_folder_get_name( folder ), "USER_ROOT" ) )
	{
		folder->priv->type = E_ZIMBRA_FOLDER_TYPE_ROOT;
	}
	else if ( g_str_equal( e_zimbra_folder_get_name( folder ), "InBox" ) )
	{
		folder->priv->type = E_ZIMBRA_FOLDER_TYPE_INBOX ;
	}
	else if ( g_str_equal( e_zimbra_folder_get_name( folder ), "Sent" ) )
	{
		folder->priv->type = E_ZIMBRA_FOLDER_TYPE_SENT;
	}
	else if ( g_str_equal( e_zimbra_folder_get_name( folder ), "Drafts" ) )
	{
		folder->priv->type = E_ZIMBRA_FOLDER_TYPE_DRAFT ;
	}
	else if ( g_str_equal( e_zimbra_folder_get_name( folder ), "Junk" ) )
	{
		folder->priv->type = E_ZIMBRA_FOLDER_TYPE_JUNK;
	}
	else if ( g_str_equal( e_zimbra_folder_get_name( folder ), "Trash" ) )
	{
		folder->priv->type = E_ZIMBRA_FOLDER_TYPE_TRASH;
	}

	g_free( value );

	// Retrive the unread and total count

	if ( ( value = e_zimbra_xml_find_attribute( node, "n" ) ) != NULL )
	{
		folder->priv->total = atoi( value );
	}
	else
	{
		folder->priv->total = 0 ;
	}

	g_free( value );

	if ( ( value = e_zimbra_xml_find_attribute( node, "u" ) ) != NULL )
	{
		folder->priv->unread = atoi( value );
	}
	else
	{
		folder->priv->unread = 0 ;
	}

	g_free( value );

	// Set permissions

	if ( g_str_equal( node->name, "folder" ) )
	{
		folder->priv->permissions = E_ZIMBRA_FOLDER_PERMISSIONS_READ | E_ZIMBRA_FOLDER_PERMISSIONS_WRITE;
	}
	else
	{
		folder->priv->permissions = E_ZIMBRA_FOLDER_PERMISSIONS_READ;
	}

	ok = TRUE;

exit:

	return ok;
}


void
e_zimbra_folder_add_changes
	(
	EZimbraFolder		*	self,
	EZimbraFolderChangeType	type,
	const char			*	ids,
	const char			*	rev,
	time_t					md
	)
{
	GPtrArray	*	zcs_update_ids			=	NULL;
	gboolean		zcs_update_ids_dirty	=	FALSE;
	GPtrArray	*	zcs_delete_ids			=	NULL;
	gboolean		zcs_delete_ids_dirty 	=	FALSE;
	char		*	ids_copy				=	NULL;
	char			packed_id[ 1024 ];
	char		*	savept					=	NULL;
	char		*	tok						=	NULL;
	gboolean		ok						=	TRUE;

	e_file_cache_freeze_changes( self->priv->cache );

	zcs_update_ids = e_file_cache_get_ids( self->priv->cache, E_FILE_CACHE_UPDATE_IDS );
	zimbra_check( zcs_update_ids, exit, ok = FALSE );

	zcs_delete_ids = e_file_cache_get_ids( self->priv->cache, E_FILE_CACHE_DELETE_IDS );
	zimbra_check( zcs_delete_ids, exit, ok = FALSE );

	ids_copy = g_strdup( ids );
	zimbra_check( ids_copy, exit, ok = FALSE );

	switch ( type )
	{
		case E_ZIMBRA_FOLDER_CHANGE_TYPE_UPDATE:
		{
			tok = strtok_r( ids_copy, ",", &savept );

			while ( tok )
			{
				e_zimbra_utils_pack_update_id( packed_id, sizeof( packed_id ), tok, rev, md );
				g_ptr_array_add( zcs_update_ids, g_strdup( packed_id ) );
				zcs_update_ids_dirty = TRUE;

				tok = strtok_r( NULL, ",", &savept );
			}
		}
		break;

		case E_ZIMBRA_FOLDER_CHANGE_TYPE_DELETE:
		{
			tok = strtok_r( ids_copy, ",", &savept );

			while ( tok )
			{
				if ( g_ptr_array_remove_id( zcs_update_ids, tok ) )
				{
					zcs_update_ids_dirty = TRUE;
				}

				e_zimbra_utils_pack_update_id( packed_id, sizeof( packed_id ), tok, rev, md );
				g_ptr_array_add( zcs_delete_ids, g_strdup( packed_id ) );
				zcs_delete_ids_dirty = TRUE;

				tok = strtok_r( NULL, ",", &savept );
			}
		}
	}

	self->priv->dirty = TRUE;

exit:

	if ( zcs_update_ids_dirty )
	{
		e_file_cache_set_ids( self->priv->cache, E_FILE_CACHE_UPDATE_IDS, zcs_update_ids );
	}

	if ( zcs_update_ids )
	{
		g_ptr_array_foreach( zcs_update_ids, ( GFunc ) g_free, NULL );
		g_ptr_array_free( zcs_update_ids, TRUE );
	}

	if ( zcs_delete_ids_dirty )
	{
		e_file_cache_set_ids( self->priv->cache, E_FILE_CACHE_DELETE_IDS, zcs_delete_ids );
	}

	if ( zcs_delete_ids )
	{
		g_ptr_array_foreach( zcs_delete_ids, ( GFunc ) g_free, NULL );
		g_ptr_array_free( zcs_delete_ids, TRUE );
	}

	if ( ids_copy )
	{
		g_free( ids_copy );
	}

	e_file_cache_thaw_changes( self->priv->cache );
}


void 
e_zimbra_folder_get_user_list (EZimbraFolder *folder, GList **user_list)
{
	g_return_if_fail (E_ZIMBRA_FOLDER (folder));
	
	*user_list = folder->priv->user_list;

}

int
e_zimbra_folder_get_sequence (EZimbraFolder *folder)
{
	g_return_val_if_fail (E_IS_ZIMBRA_FOLDER (folder), 0);
	
	return (int)folder->priv->sequence;
}


const char *
e_zimbra_folder_get_modified (EZimbraFolder *folder)
{
	g_return_val_if_fail (E_IS_ZIMBRA_FOLDER (folder), NULL);

	return (const char *) folder->priv->modified;
}


const char *
e_zimbra_folder_get_owner (EZimbraFolder *folder)
{
	g_return_val_if_fail (E_ZIMBRA_FOLDER (folder), NULL);
	
	return (const char *) folder->priv->owner;
}

int
e_zimbra_folder_get_rights (EZimbraFolder *folder, gchar *email)
{
	GList *user_list = NULL;
	GList *node = NULL;
	EShUsers *user = NULL;

	g_return_val_if_fail (E_IS_ZIMBRA_FOLDER (folder), 0);

	user_list = folder->priv->user_list;
	
	for (node = user_list; node != NULL; node = node->next) {
		user = node->data;
		if( !strcmp (user->email, email))
			return user->rights;
	}

	return 0;
}

gboolean
e_zimbra_folder_get_is_shared_by_me (EZimbraFolder *folder)
{
	g_return_val_if_fail (E_IS_ZIMBRA_FOLDER (folder), FALSE);
	
	return (gboolean) folder->priv->is_shared_by_me;
}

gboolean
e_zimbra_folder_get_is_shared_to_me (EZimbraFolder *folder)
{
	g_return_val_if_fail (E_IS_ZIMBRA_FOLDER (folder), FALSE);
	
	return (gboolean) folder->priv->is_shared_to_me;
}


const char *
e_zimbra_folder_get_name (EZimbraFolder *folder)
{
	g_return_val_if_fail (E_IS_ZIMBRA_FOLDER (folder), NULL);

	return (const char *) folder->priv->name;
}

void
e_zimbra_folder_set_name (EZimbraFolder *folder, const char *new_name)
{
	EZimbraFolderPrivate *priv;

	g_return_if_fail (E_IS_ZIMBRA_FOLDER (folder));
	g_return_if_fail (new_name != NULL);

	priv = folder->priv;

	if (priv->name)
		g_free (priv->name);
	priv->name = g_strdup (new_name);
}

const char *
e_zimbra_folder_get_id (EZimbraFolder *folder)
{
	g_return_val_if_fail (E_IS_ZIMBRA_FOLDER (folder), NULL);

	return (const char *) folder->priv->id;
}

void
e_zimbra_folder_set_id (EZimbraFolder *folder, const char *new_id)
{
	EZimbraFolderPrivate *priv;

	g_return_if_fail (E_IS_ZIMBRA_FOLDER (folder));
	g_return_if_fail (new_id != NULL);

	priv = folder->priv;

	if (priv->id)
		g_free (priv->id);
	priv->id = g_strdup (new_id);
}


EZimbraFolderPermissions
e_zimbra_folder_get_permissions
	(
	EZimbraFolder * folder
	)
{
	return folder->priv->permissions;
}


const char *
e_zimbra_folder_get_parent_id (EZimbraFolder *folder) 
{
	g_return_val_if_fail (E_IS_ZIMBRA_FOLDER (folder), NULL);

	return (const char *) folder->priv->parent;
}

void
e_zimbra_folder_set_parent_id (EZimbraFolder *folder, const char *parent_id)
{
	EZimbraFolderPrivate *priv ;
	
	g_return_if_fail (E_IS_ZIMBRA_FOLDER (folder));
	g_return_if_fail (parent_id != NULL);

	priv = folder->priv ;

	if (priv->parent)
		g_free (priv->parent) ;

	priv->parent = g_strdup (parent_id) ;
}

guint32
e_zimbra_folder_get_total_count (EZimbraFolder *folder)
{
	g_return_val_if_fail (E_IS_ZIMBRA_FOLDER (folder), -1) ;

	return folder->priv->total ;
}
		
guint32
e_zimbra_folder_get_unread_count (EZimbraFolder *folder)
{
	g_return_val_if_fail (E_IS_ZIMBRA_FOLDER (folder), -1) ;

	return folder->priv->unread ;

}


gboolean 
e_zimbra_folder_get_is_writable (EZimbraFolder *folder)
{
	g_return_val_if_fail (E_IS_ZIMBRA_FOLDER (folder), FALSE);
	
	return folder->priv->is_writable;

}

void 
e_zimbra_folder_set_is_writable (EZimbraFolder *folder, gboolean is_writable)
{
	g_return_if_fail (E_IS_ZIMBRA_FOLDER (folder));
	
	folder->priv->is_writable = is_writable;
}


gboolean
e_zimbra_folder_is_root (EZimbraFolder *folder)
{
	g_return_val_if_fail (E_IS_ZIMBRA_FOLDER (folder), FALSE) ;
	
	return folder->priv->is_root ;
}

EZimbraFolderType
e_zimbra_folder_get_folder_type (EZimbraFolder *folder)
{
	g_return_val_if_fail (E_IS_ZIMBRA_FOLDER (folder), FALSE) ;
	return folder->priv->type ;
}


gboolean
e_zimbra_folder_has_changes
	(
	EZimbraFolder		*	folder
	)
{
	return folder->priv->dirty;
}


gboolean
e_zimbra_folder_get_changes
	(
	EZimbraFolder		*	folder,
	GPtrArray			**	updates,
	GPtrArray			**	deletes
	)
{
	const char	*	val;
	gboolean		ok = TRUE;

	*updates = *deletes = NULL;

	val = e_file_cache_get_object( folder->priv->cache, "update" );
	*updates = e_zimbra_utils_make_array_from_string( val );
	zimbra_check( *updates, exit, ok = FALSE );

	val = e_file_cache_get_object( folder->priv->cache, "delete" );
	*deletes = e_zimbra_utils_make_array_from_string( val );
	zimbra_check( *deletes, exit, ok = FALSE );

exit:

	return ok;
}


void
e_zimbra_folder_clr_changes
	(
	EZimbraFolder		*	folder
	)
{
	e_file_cache_remove_object( folder->priv->cache, "update" );
	e_file_cache_remove_object( folder->priv->cache, "delete" );

	folder->priv->dirty = FALSE;
}
