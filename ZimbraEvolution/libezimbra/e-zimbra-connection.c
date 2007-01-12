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
 *  Scott Herscher<scott.herscher@zimbra.com>
 *
 * Copyright 2006, Zimbra, Inc.
 *
 */

#ifdef HAVE_CONFIG_H
#include <config.h>
#endif
#include <string.h>
#include <ctype.h>
#include <glib/gi18n-lib.h>
#include <libedataserver/e-file-cache.h>
#include <libedataserver/e-source-list.h>
#include <libedataserver/e-source-group.h>
#include <libedataserver/e-source.h>
#include <libedata-cal/e-cal-backend.h>
#include "e-zimbra-connection.h"
#include "e-zimbra-debug.h"
#include "e-zimbra-utils.h"
#include "e-zimbra-xml.h"
#include <curl/curl.h>
#include <glog/glog.h>

#define MY_ENCODING "ISO-8859-1"
#define EBOOK_PATH	"/apps/evolution/addressbook/sources"
#define ECAL_PATH	"/apps/evolution/calendar/sources"
#define SYNC_RATE	300000

static GObjectClass *	g_parent_class = NULL;
static GHashTable	*	g_connections = NULL;

struct _EZimbraConnectionPrivate
{
	CURL				*	curl;
	char				*	uri;
	char				*	account;
	char				*	hostname;
	char				*	username;
	char				*	password;
	int						port;
	gboolean				use_ssl;
	char				*	auth_token;
	char				*	session_id;
	char				*	cache_folder;
	EFileCache			*	cache;
	GList				*	folders;
	char				*	trash_id;
	int						timeout_id;
	char				*	user_name;
	char				*	user_email;
	char				*	user_uuid;
	char				*	version;
	GHashTable			*	clients;
	GHashTable			*	categories_by_name;
	GHashTable			*	categories_by_id;
	GMutex				*	reauth_mutex;
	GMutex				*	send_mutex;
	GStaticRecMutex			mutex;
};


typedef struct EZimbraConnectionClient
{
	gpointer						handle;
	EZimbraConnectionClientSyncFunc	sync_func;
} EZimbraConnectionClient;


typedef struct CurlResponse
{
	char	*	text;
	size_t		size;
} CurlResponse;


static EZimbraConnectionStatus
e_zimbra_connection_start_message
	(
	EZimbraConnection	*	cnc,
	const char			*	func,
	const char			*	urn,
	xmlBufferPtr		*	request_buffer,
	xmlTextWriterPtr	*	request
	);


static xmlDocPtr
e_zimbra_connection_send_message
	(
	EZimbraConnection	*	cnc,
	xmlBufferPtr		*	request_buffer,
	xmlTextWriterPtr	*	request
	);


static EZimbraConnectionStatus
e_zimbra_connection_parse_response_status
	(
	xmlDocPtr				response
	);


static char*
e_zimbra_connection_get_change_token_from_response
	(
	xmlNodePtr				root
	);


static EZimbraConnectionStatus
e_zimbra_connection_sync_folder
	(
	EZimbraConnection	*	cnc,
	xmlNode				*	node,
	ESourceList			**	ecal_source_list,
	ESourceList			**	ebook_source_list,
	const char			*	parent_name
	);


static EZimbraConnectionStatus
e_zimbra_connection_sync_appt
	(
	EZimbraConnection	*	cnc,
	xmlNode				*	node
	);


static EZimbraConnectionStatus
e_zimbra_connection_sync_contact
	(
	EZimbraConnection	*	cnc,
	xmlNode				*	node
	);


static EZimbraConnectionStatus
e_zimbra_connection_sync_delete
	(
	EZimbraConnection	*	cnc,
	xmlNode				*	node,
	ESourceList			**	ecal_source_list,
	ESourceList			**	ebook_source_list
	);


static EZimbraConnectionStatus
e_zimbra_connection_sync_client
	(
	EZimbraConnection		*	cnc,
	EZimbraFolder			*	folder,
	EZimbraConnectionClient	*	client,
	unsigned					sync_request_time,
	unsigned					sync_response_time
	);


static ESource*
e_zimbra_connection_peek_source_by_id
	(
	EZimbraConnection	*	cnc,
	ESourceList			*	source_list,
	ESourceGroup		*	group,
	const char			*	id
	);


static xmlNode*
xml_parse_path( xmlNode * node, const char * path )
{
	char	*	savept;
	char		buf[ 1024 ];

	sprintf( buf, path );

	char * tok = strtok_r( buf, "/", &savept );

	while ( tok )
	{
		xmlNode * child;

		for ( child = node->children; child; child = child->next )
		{
        	if ( child->type == XML_ELEMENT_NODE )
			{
				if ( strcmp( ( const char* ) child->name, tok ) == 0 )
				{
					break;
				}
			}
		}

		if ( child )
		{
			node = child;
			tok = strtok_r( NULL, "/", &savept );
		}
		else
		{
			node = NULL;
			break;
		}
	}

	return node;
}


static size_t
curl_write_func
    (
    void 	*	ptr,
    size_t  	size,
    size_t  	nmemb,
    void    *   data
    )
{
	size_t actualSize = size * nmemb;

	CurlResponse * response = ( CurlResponse* ) data;

    response->text = (char *) realloc( response->text, response->size + actualSize + 1 );

    if ( response->text )
    {
        memcpy( &( response->text[ response->size ] ), ptr, actualSize );
        response->size += actualSize;
        response->text[ response->size ] = '\0';
    }

    return actualSize;
}


static EZimbraConnectionStatus 
reauthenticate
	(
	EZimbraConnection	*	cnc
	)
{
	xmlBufferPtr					request_buffer	= NULL;
	xmlTextWriterPtr				request			= NULL;
	xmlDocPtr						response		= NULL;
	EZimbraConnectionPrivate	*	priv			= NULL;
	gboolean						mutex_locked	= FALSE;
	int								rc;
	EZimbraConnectionStatus			err				= -1;
	
	priv = cnc->priv;
	zimbra_check( priv, exit, err = E_ZIMBRA_CONNECTION_STATUS_INVALID_CONNECTION );
	
	g_mutex_lock( priv->reauth_mutex );
	mutex_locked = TRUE;

	// Just to make sure we still have invalid session 
	// When multiple e_zimbra_connection apis see inavlid connection error 
	// at the same time this prevents this function sending login requests
	// multiple times

	err = e_zimbra_connection_start_message( cnc, "ContactActionRequest", "zimbraMail", &request_buffer, &request );
	zimbra_check_okay( err, exit );

	// Send message to server

	response = e_zimbra_connection_send_message( cnc, &request_buffer, &request );
	zimbra_check( response, exit, err = E_ZIMBRA_CONNECTION_STATUS_NO_RESPONSE );

	err = e_zimbra_connection_parse_response_status( response );

	if ( response )
	{
		xmlFreeDoc( response );
		response = NULL;
	}

	if ( request_buffer )
	{
		xmlBufferFree( request_buffer );
		request_buffer = NULL;
	}

	if ( request )
	{
		xmlFreeTextWriter( request );
		request = NULL;
	}

	if ( err != E_ZIMBRA_CONNECTION_STATUS_OK )
	{
		xmlNode * authNode;
		xmlNode * sessionNode;
	
		err = e_zimbra_connection_start_message( cnc, "AuthRequest", "zimbraAccount", &request_buffer, &request );
		zimbra_check_okay( err, exit );

		rc = xmlTextWriterStartElement( request, BAD_CAST "account" );
		zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

		rc = xmlTextWriterWriteAttribute( request, BAD_CAST "by", BAD_CAST "name" );
		zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

		rc = xmlTextWriterWriteString( request, BAD_CAST priv->username );
		zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

		rc = xmlTextWriterEndElement( request );
		zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

		rc = xmlTextWriterWriteElement( request, BAD_CAST "password", BAD_CAST priv->password );
		zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

		// Send message to server

		GLOG_DEBUG( "sending message" );

		response = e_zimbra_connection_send_message( cnc, &request_buffer, &request );
		zimbra_check( response, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

		err = e_zimbra_connection_parse_response_status( response );
		zimbra_check( err == E_ZIMBRA_CONNECTION_STATUS_OK, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN; g_warning( "parse_response_status returned %d", err ) );

		authNode = xml_parse_path( xmlDocGetRootElement( response ), "Body/AuthResponse/authToken" );
		zimbra_check( authNode, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

		sessionNode = xml_parse_path( xmlDocGetRootElement( response ), "Body/AuthResponse/sessionId" );
		zimbra_check( sessionNode, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

		cnc->priv->auth_token = g_strdup( ( const char* ) authNode->children->content );
		zimbra_check( cnc->priv->auth_token, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

		cnc->priv->session_id = g_strdup( ( const char* ) sessionNode->children->content );
		zimbra_check( cnc->priv->session_id, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

		GLOG_DEBUG( "authtoken = %s, session id = %s", cnc->priv->auth_token, cnc->priv->session_id );
	}

exit:

	if ( response )
	{
		xmlFreeDoc( response );
	}

	if ( request_buffer )
	{
		xmlBufferFree( request_buffer );
		request_buffer = NULL;
	}

	// Workaround bug in libxml2.  If there is an error while creating the xml document,
	// then calling xmlFreeTextWriter will crash the caller.

	if ( request && !err )
	{
		xmlFreeTextWriter( request );
	}

	if ( mutex_locked )
	{
		g_mutex_unlock( priv->reauth_mutex );
	}

	return err;
}


static EZimbraConnectionStatus 
e_zimbra_connection_sync_thread
	(
	EZimbraConnection	*	cnc
	)
{
	xmlBufferPtr			request_buffer		=	NULL;
	xmlTextWriterPtr		request				=	NULL;
	xmlDocPtr				response			=	NULL;
	const char			*	sync_token			=	NULL;
	char				*	new_sync_token		=	NULL;
	gboolean				mutex_locked		=	FALSE;
	xmlNode				*	body_node			=	NULL;
	EZimbraFolder		*	folder				=	NULL;
	const char			*	folder_id			=	NULL;
	ESourceList			*	ecal_source_list	=	NULL;
	ESourceList			*	ebook_source_list	=	NULL;
	xmlNode				*	child				=	NULL;
	int						rc;
	int						i;
	EZimbraConnectionStatus	err;

	zimbra_check( E_IS_ZIMBRA_CONNECTION( cnc ), exit, err = E_ZIMBRA_CONNECTION_STATUS_INVALID_OBJECT );

	g_static_rec_mutex_lock( &cnc->priv->mutex );
	mutex_locked = TRUE;

	// Keep pulling changes from ZCS until there are no more changes

	while ( 1 )
	{
		int changes = 0;

		sync_token = e_file_cache_get_object( cnc->priv->cache, "sync_token" );

		err = e_zimbra_connection_start_message( cnc, "SyncRequest", "zimbraMail", &request_buffer, &request );
		zimbra_check_okay( err, exit );

		if ( sync_token )
		{
			rc = xmlTextWriterWriteAttribute( request, BAD_CAST "token", BAD_CAST sync_token );
			zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );
		}

		// Send message to server

		response = e_zimbra_connection_send_message( cnc, &request_buffer, &request );
		zimbra_check( response, exit, err = E_ZIMBRA_CONNECTION_STATUS_NO_RESPONSE );

		err = e_zimbra_connection_parse_response_status( response );

		if ( err != E_ZIMBRA_CONNECTION_STATUS_OK )
		{
			if ( err == E_ZIMBRA_CONNECTION_STATUS_INVALID_CONNECTION )
			{
				reauthenticate( cnc );
			}

			g_warning( "e_zimbra_connection_parse_response_status returned %d", err );
			goto exit;
		}

		// Parse the parameters

		body_node = xml_parse_path( xmlDocGetRootElement( response ), "Body/SyncResponse" );
		zimbra_check( body_node, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

		new_sync_token = e_zimbra_xml_find_attribute( body_node, "token" );
		zimbra_check( new_sync_token, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

		e_file_cache_freeze_changes( cnc->priv->cache );

		for ( child = body_node->children; child; child = child->next )
		{
			if ( child->type == XML_ELEMENT_NODE )
			{
				if ( g_str_equal( ( const char* ) child->name, "folder" ) || ( g_str_equal( ( const char* ) child->name, "link" ) ) )
				{
					e_zimbra_connection_sync_folder( cnc, child, &ecal_source_list, &ebook_source_list, "" );
					changes++;
				}
				else if ( g_str_equal( ( const char* ) child->name, "appt" ) )
				{
					e_zimbra_connection_sync_appt( cnc, child );
					changes++;
				}
				else if ( g_str_equal( ( const char* ) child->name, "cn" ) )
				{
					e_zimbra_connection_sync_contact( cnc, child );
					changes++;
				}
				else if ( g_str_equal( ( const char* ) child->name, "deleted" ) )
				{
					e_zimbra_connection_sync_delete( cnc, child, &ecal_source_list, &ebook_source_list );
					changes++;
				}
			}
		}

		e_file_cache_thaw_changes( cnc->priv->cache );

		// Cache the sync token

		if ( sync_token )
		{
			e_file_cache_replace_object( cnc->priv->cache, "sync_token", new_sync_token );
		}
		else
		{
			e_file_cache_add_object( cnc->priv->cache, "sync_token", new_sync_token );
		}

		if ( new_sync_token )
		{
			g_free( new_sync_token );
			new_sync_token = NULL;
		}

		if ( response )
		{
			xmlFreeDoc( response );
			response = NULL;
		}

		if ( request_buffer )
		{
			xmlBufferFree( request_buffer );
			request_buffer = NULL;
		}

		if ( request )
		{
			xmlFreeTextWriter( request );
			request = NULL;
		}

		if ( !changes )
		{
			break;
		}
	}
	
	// Now call the clients who have registered interest 

	for ( i = 0; i < g_list_length( cnc->priv->folders ); i++ )
	{
		EZimbraConnectionClient * client;

		folder		= g_list_nth_data( cnc->priv->folders, i );
		folder_id	= e_zimbra_folder_get_id( folder );

		if ( ( client = g_hash_table_lookup( cnc->priv->clients, folder_id ) ) != NULL )
		{
			// Pull changes if there are any

			err = e_zimbra_connection_sync_client( cnc, folder, client, 0, 0 );

			if ( err )
			{
				g_warning( "unable to sync for folder: %s", folder_id );
				continue;
			}
		}
	}

	err = E_ZIMBRA_CONNECTION_STATUS_OK;

exit:

	if ( ecal_source_list )
	{
		GError * error;

		e_source_list_sync( ecal_source_list, &error );
		g_object_unref( ecal_source_list );
	}

	if ( ebook_source_list )
	{
		GError * error;

		e_source_list_sync( ebook_source_list, &error );
		g_object_unref( ebook_source_list );
	}

	if ( new_sync_token )
	{
		g_free( new_sync_token );
	}

	if ( response )
	{
		xmlFreeDoc( response );
	}

	if ( request_buffer )
	{
		xmlBufferFree( request_buffer );
	}

	// Workaround bug in libxml2.  If there is an error while creating the xml document,
	// then calling xmlFreeTextWriter will crash the caller.

	if ( request && !err )
	{
		xmlFreeTextWriter( request );
	}

	if ( mutex_locked )
	{
		g_static_rec_mutex_unlock( &cnc->priv->mutex );
	}

	return err;
}


gboolean
e_zimbra_connection_sync
	(
	EZimbraConnection * cnc
	)
{
	GThread	*	thread	=	NULL;
	GError	*	error	=	NULL;
	gboolean	ok		=	TRUE;

	zimbra_check( cnc, exit, ok = FALSE );

	thread = g_thread_create( ( GThreadFunc ) e_zimbra_connection_sync_thread, cnc, FALSE, &error );
	zimbra_check( thread, exit, g_warning( G_STRLOC ": %s", error->message ); g_error_free (error); ok = FALSE );

	ok = TRUE;

exit:

	return 1;
}


static char*
get_trash_id
	(
	EZimbraConnection	*	cnc
	)
{
	static int first_time = 1;

	if ( first_time )
	{
		cnc->priv->trash_id = g_strdup( "3" );
		first_time = 0;
	}

	return cnc->priv->trash_id;
}


EZimbraConnectionStatus
e_zimbra_connection_parse_response_status
	(
	xmlDocPtr	response
	)
{
	EZimbraConnectionStatus status;
	xmlNode				*	root;
	xmlNode				*	error;

	if ( !response )
	{
		status = E_ZIMBRA_CONNECTION_STATUS_NO_RESPONSE;
		goto exit;
	}

	root = xmlDocGetRootElement( response );

	if ( !root )
	{
		status = E_ZIMBRA_CONNECTION_STATUS_INVALID_RESPONSE;
		goto exit;
	}

	error = xml_parse_path( root, "Body/Fault" );

	if ( error )
	{
		error = xml_parse_path( root, "Body/Fault/Detail/Error/Code" );

		if ( strcmp( ( const char* ) error->children->content, "account.AUTH_EXPIRED" ) == 0 )
		{
			status = E_ZIMBRA_CONNECTION_STATUS_INVALID_CONNECTION;
			goto exit;
		}
		else if ( strcmp( ( const char* ) error->children->content, "account.AUTH_FAILED" ) == 0 )
		{
			status = E_ZIMBRA_CONNECTION_STATUS_AUTH_FAILED;
			goto exit;
		}
		else if ( strcmp( ( const char* ) error->children->content, "mail.NO_SUCH_CONTACT" ) == 0 )
		{
			status = E_ZIMBRA_CONNECTION_STATUS_NO_SUCH_ITEM;
			goto exit;
		}
		else if ( strcmp( ( const char* ) error->children->content, "mail.NO_SUCH_APPT" ) == 0 )
		{
			status = E_ZIMBRA_CONNECTION_STATUS_NO_SUCH_ITEM;
			goto exit;
		}
		else
		{
			status = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN;
			goto exit;
		}
	}

	status = E_ZIMBRA_CONNECTION_STATUS_OK;

exit:

	return status;
}


static char*
e_zimbra_connection_get_change_token_from_response
	(
	xmlNodePtr root
	)
{
	char				*	token	=	NULL;
	xmlNodePtr				node	=	NULL;
	xmlNodePtr				child;
	EZimbraConnectionStatus	err;

	node = xml_parse_path( root, "Header/context" );
	zimbra_check( node, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	for ( child = node->children; child; child = child->next )
	{
		if ( g_str_equal( child->name, "change" ) )
		{
			token = e_zimbra_xml_find_attribute( child, "token" );
			break;
		}
	}

exit:

	return token;
}


const char *
e_zimbra_connection_get_error_message
	(
	EZimbraConnectionStatus status
	)
{
	switch (status)
	{
		case E_ZIMBRA_CONNECTION_STATUS_OK :
			break;
		case E_ZIMBRA_CONNECTION_STATUS_INVALID_CONNECTION :
			return _("Invalid connection");
		case E_ZIMBRA_CONNECTION_STATUS_INVALID_OBJECT :
			return _("Invalid object");
		case E_ZIMBRA_CONNECTION_STATUS_INVALID_RESPONSE :
			return _("Invalid response from server");
		case E_ZIMBRA_CONNECTION_STATUS_NO_RESPONSE:
			return _("No response from the server");
		case E_ZIMBRA_CONNECTION_STATUS_OBJECT_NOT_FOUND :
			return _("Object not found");
		case E_ZIMBRA_CONNECTION_STATUS_UNKNOWN_USER :
			return _("Unknown User");
		case E_ZIMBRA_CONNECTION_STATUS_BAD_PARAMETER :
			return _("Bad parameter");
		case E_ZIMBRA_CONNECTION_STATUS_OTHER :
		case E_ZIMBRA_CONNECTION_STATUS_UNKNOWN :
		default :
			return _("Unknown error");
	}

	return NULL;
}


static EZimbraConnectionStatus
logout (EZimbraConnection *cnc)
{
	return E_ZIMBRA_CONNECTION_STATUS_OK;
}


static void
e_zimbra_connection_dispose
	(
	GObject *object
	)
{
	EZimbraConnection *cnc = (EZimbraConnection *) object;
	EZimbraConnectionPrivate *priv;
	gpointer orig_key, orig_value;
	
	g_return_if_fail (E_IS_ZIMBRA_CONNECTION (cnc));
	
	priv = cnc->priv;
	GLOG_INFO( "enter" );

	// Make sure we don't get interrupted with a sync

	g_static_rec_mutex_lock( &cnc->priv->mutex );
	
	// Remove the connection from the hash table

	if ( g_connections )
	{
		if ( g_hash_table_lookup_extended( g_connections, cnc->priv->uri, &orig_key, &orig_value ) )
		{
			g_hash_table_remove (g_connections, cnc->priv->uri);
			if (g_hash_table_size (g_connections) == 0)
			{
				g_hash_table_destroy (g_connections);
				g_connections = NULL;
			}
		}
	}
	
	if ( priv )
	{
		if ( priv->timeout_id )
		{
			g_source_remove( priv->timeout_id );
			priv->timeout_id = 0;
		}

		if (priv->session_id)
		{
			logout (cnc);
			priv->session_id = NULL;
		}

		if (priv->uri) {
			g_free (priv->uri);
			priv->uri = NULL;
		}

		if (priv->username) {
			g_free (priv->username);
			priv->username = NULL;
		}

		if (priv->password) {
			g_free (priv->password);
			priv->password = NULL;
		}

		if (priv->user_name) {
			g_free (priv->user_name);
			priv->user_name = NULL;
		}

		if (priv->user_email) {
			g_free (priv->user_email);
			priv->user_email = NULL;
		}

		if (priv->reauth_mutex) {
			g_mutex_free (priv->reauth_mutex);
			priv->reauth_mutex = NULL;
		}

		if ( priv->send_mutex )
		{
			g_mutex_free( priv->send_mutex );
			priv->send_mutex = NULL;
		}

		if (priv->categories_by_id) {
			g_hash_table_destroy (priv->categories_by_id);
			priv->categories_by_id = NULL;
		}	
		
		if (priv->categories_by_name) {
			g_hash_table_destroy (priv->categories_by_name);
			priv->categories_by_name = NULL;
		}
	
		if (priv->folders)
		{
			g_list_foreach (priv->folders, (GFunc) g_object_unref, NULL);
			g_list_free (priv->folders);
			priv->folders = NULL;
		}

		if (priv->version) {
			g_free (priv->version) ;
			priv->version = NULL ;
		}

		if ( priv->cache )
		{
			g_object_unref( priv->cache );
			priv->cache = NULL;
		}
	}

	// Unlock

	g_static_rec_mutex_unlock( &cnc->priv->mutex );

	if ( g_parent_class->dispose )
	{
		( *g_parent_class->dispose )( object );
	}
} 


static void
e_zimbra_connection_finalize (GObject *object)
{
	EZimbraConnection *cnc = (EZimbraConnection *) object;
	EZimbraConnectionPrivate *priv;

	g_return_if_fail (E_IS_ZIMBRA_CONNECTION (cnc));

	priv = cnc->priv;
	GLOG_INFO( "enter" );

	/* clean up */
	g_static_rec_mutex_free( &priv->mutex );
	g_free (priv);
	cnc->priv = NULL;

	if ( g_parent_class->finalize )
	{
		( *g_parent_class->finalize )( object );
	}
}


static void
e_zimbra_connection_class_init (EZimbraConnectionClass *klass)
{
	GObjectClass * object_class;

	object_class			= G_OBJECT_CLASS (klass);
	g_parent_class			= g_type_class_peek_parent (klass);
	object_class->dispose	= e_zimbra_connection_dispose;
	object_class->finalize	= e_zimbra_connection_finalize;
}


static void
e_zimbra_connection_init
	(
	EZimbraConnection		*	cnc,
	EZimbraConnectionClass	*	klass
	)
{
	EZimbraConnectionPrivate * priv;

	// allocate internal structure

	priv		= g_new0( EZimbraConnectionPrivate, 1 );
	cnc->priv	= priv;

	// create the Curl session for this connection

	priv->curl					= curl_easy_init();
	priv->reauth_mutex			= g_mutex_new ();
	priv->send_mutex			= g_mutex_new();
	g_static_rec_mutex_init( &priv->mutex );
	priv->categories_by_id		= NULL;
	priv->categories_by_name	= NULL;
	priv->folders				= NULL;
	priv->auth_token			= NULL;
	priv->session_id			= NULL;
}
	

GType
e_zimbra_connection_get_type()
{
	static GType type = 0;

	if (!type) {
		static GTypeInfo info = {
                        sizeof (EZimbraConnectionClass),
                        (GBaseInitFunc) NULL,
                        (GBaseFinalizeFunc) NULL,
                        (GClassInitFunc) e_zimbra_connection_class_init,
                        NULL, NULL,
                        sizeof (EZimbraConnection),
                        0,
                        (GInstanceInitFunc) e_zimbra_connection_init
                };
		type = g_type_register_static (G_TYPE_OBJECT, "EZimbraConnection", &info, 0);
	}

	return type;
}


EZimbraConnection*
e_zimbra_connection_new
	(
	ESource		*	source,
	const char	*	username,
	const char	*	password
	)
{
	static GStaticMutex		connecting		=	G_STATIC_MUTEX_INIT;	
	EZimbraConnection	*	self			=	NULL;
	xmlBufferPtr			request_buffer	=	NULL;
	xmlTextWriterPtr		request			=	NULL;
	xmlDocPtr  				response		=	NULL;
	char				*	filename		=	NULL;
	xmlNode 			*	authNode		=	NULL;
	xmlNode 			*	sessionNode		=	NULL;
	xmlNode				*	folderNode		=	NULL;
	xmlNode				*	child			=	NULL;
	GThread				*	thread			=	NULL;
	GError				*	error			=	NULL;
	char				*	raw_uri			=	NULL;
	char				*	temp_uri		=	NULL;
	char				*	fspath_uri		=	NULL;
	char				*	formed_uri		=	NULL;
	const char			*	use_ssl_prop	=	NULL;
	char				*	protocol		=	NULL;
	EUri				*	parsed_uri		=	NULL;
	gboolean				use_ssl;
	int						rc;
	EZimbraConnectionStatus err				=	0;
	
	g_static_mutex_lock( &connecting );

	// Create the URI

	raw_uri = e_source_get_uri( source );
	zimbra_check( raw_uri, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	GLOG_DEBUG( "raw_uri = %s", raw_uri );

	parsed_uri = e_uri_new( raw_uri );
	zimbra_check( parsed_uri, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	GLOG_DEBUG( "path = %s", parsed_uri->path );

	if ( ( use_ssl_prop = e_source_get_property( source, "use_ssl" ) ) && ( g_str_equal( use_ssl_prop, "always" ) ) )
	{
		protocol	= "https";
		use_ssl		= TRUE;
	}
	else
	{
		protocol	= "http";
		use_ssl		= FALSE;
	}

	formed_uri = g_strdup_printf( "%s://%s@%s:%d/service/soap", protocol, parsed_uri->user, parsed_uri->host, parsed_uri->port );
	zimbra_check( formed_uri, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	GLOG_DEBUG( "formed_uri = %s", formed_uri );

	// Search for the connection in our hash table

	if ( g_connections )
	{
		GLOG_DEBUG( "g_connections is set!!!" );

		self = g_hash_table_lookup( g_connections, formed_uri );

		if ( E_IS_ZIMBRA_CONNECTION( self ) )
		{
			g_object_ref( self );
			goto exit;
		}
	}

	GLOG_DEBUG( "g_connections is not set!!!" );

	// Not found, so create a new connection

	self = g_object_new( E_TYPE_ZIMBRA_CONNECTION, NULL );
	zimbra_check( self, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	self->priv->account = g_strdup( e_source_get_property( source, "account" ) );
	zimbra_check( self->priv->account, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	self->priv->hostname = g_strdup( parsed_uri->host );
	zimbra_check( self->priv->hostname, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	self->priv->username = g_strdup( parsed_uri->user );
	zimbra_check( self->priv->username, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	self->priv->port = parsed_uri->port;

	self->priv->uri = formed_uri;
	formed_uri = NULL;

	self->priv->use_ssl = use_ssl;

	self->priv->password = g_strdup (password);
	zimbra_check( self->priv->password, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	err = e_zimbra_connection_start_message( self, "AuthRequest", "zimbraAccount", &request_buffer, &request );
	zimbra_check_okay( err, exit );

	rc = xmlTextWriterStartElement( request, BAD_CAST "account" );
	zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	rc = xmlTextWriterWriteAttribute( request, BAD_CAST "by", BAD_CAST "name" );
	zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	rc = xmlTextWriterWriteString( request, BAD_CAST parsed_uri->user );
	zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	rc = xmlTextWriterEndElement( request );
	zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	rc = xmlTextWriterWriteElement( request, BAD_CAST "password", BAD_CAST password );
	zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	// Send message to server

	response = e_zimbra_connection_send_message( self, &request_buffer, &request );
	zimbra_check( response, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	err = e_zimbra_connection_parse_response_status( response );
	zimbra_check( err == E_ZIMBRA_CONNECTION_STATUS_OK, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN; g_warning( "parse_response_status returned %d", err ) );

	authNode = xml_parse_path( xmlDocGetRootElement( response ), "Body/AuthResponse/authToken" );
	zimbra_check( authNode, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	sessionNode = xml_parse_path( xmlDocGetRootElement( response ), "Body/AuthResponse/sessionId" );
	zimbra_check( sessionNode, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	self->priv->auth_token = g_strdup( ( const char* ) authNode->children->content );
	zimbra_check( self->priv->auth_token, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	self->priv->session_id = g_strdup( ( const char* ) sessionNode->children->content );
	zimbra_check( self->priv->session_id, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	temp_uri = g_strdup_printf( "zimbra://%s@%s:%d", parsed_uri->user ? parsed_uri->user : "", parsed_uri->host, parsed_uri->port );
	zimbra_check( temp_uri, exit, err = GNOME_Evolution_Calendar_OtherError );

	fspath_uri = e_zimbra_utils_uri_to_fspath( temp_uri );
	zimbra_check( fspath_uri, exit, err = GNOME_Evolution_Calendar_OtherError );

	self->priv->cache_folder = g_build_filename( g_get_home_dir(), ".evolution/cache/zimbra", fspath_uri, NULL );
	zimbra_check( self->priv->cache_folder, exit, err = GNOME_Evolution_Calendar_OtherError );

	self->priv->clients = g_hash_table_new( g_str_hash, g_str_equal );
	zimbra_check( self->priv->clients, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	// Add the connection to the g_connections hash table

	if ( !g_connections )
	{
		g_connections = g_hash_table_new( g_str_hash, g_str_equal );
		zimbra_check( g_connections, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );
	}

	g_hash_table_insert( g_connections, self->priv->uri, self );

	// Create all my folders

	folderNode = xml_parse_path( xmlDocGetRootElement( response ), "Header/context/refresh/folder" );
	zimbra_check( folderNode, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	for ( child = folderNode->children; child; child = child->next )
	{
		EZimbraFolder * folder;

		if ( g_str_equal( ( const char* ) child->name, "folder" ) || g_str_equal( ( const char* ) child->name, "link" ) )
		{
			GLOG_DEBUG( "creating new folder!!!" );

			folder = e_zimbra_folder_new_from_soap_parameter( child, self->priv->cache_folder );
			zimbra_check( folder, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

			if ( e_zimbra_folder_get_folder_type( folder ) == E_ZIMBRA_FOLDER_TYPE_TRASH )
			{
				self->priv->trash_id = g_strdup( e_zimbra_folder_get_id( folder ) );
				GLOG_DEBUG( "setting trash id to %s", self->priv->trash_id );
			}

			GLOG_DEBUG( "adding to list!!!!" );

			self->priv->folders = g_list_append( self->priv->folders, folder );
		}
	}

	// Create the cache...first mangle the URI to not contain invalid characters

	filename = g_build_filename( self->priv->cache_folder, "cache.xml", NULL );
	zimbra_check( filename, exit, err = GNOME_Evolution_Calendar_OtherError );

	self->priv->cache = e_file_cache_new( filename );
	zimbra_check( self->priv->cache, exit, err = GNOME_Evolution_Calendar_OtherError );

	// Now let's fire up a threaded sync

	thread = g_thread_create( ( GThreadFunc ) e_zimbra_connection_sync, self, FALSE, &error );
	zimbra_check( thread, exit, g_warning (G_STRLOC ": %s", error->message); g_error_free (error); err = GNOME_Evolution_Calendar_OtherError );

	// That was so much fun, let's do it every SYNC_RATE msec

	self->priv->timeout_id = g_timeout_add( SYNC_RATE, ( GSourceFunc ) e_zimbra_connection_sync, ( gpointer ) self );

exit:

	g_static_mutex_unlock( &connecting );

	if ( filename )
	{
		g_free( filename );
	}

	if ( temp_uri )
	{
		g_free( temp_uri );
	}

	if ( fspath_uri )
	{
		g_free( fspath_uri );
	}

	if ( parsed_uri )
	{
		e_uri_free(parsed_uri);
	}

	if ( raw_uri )
	{
		g_free( raw_uri );
	}

	if ( response )
	{
		xmlFreeDoc( response );
	}

	if ( request_buffer )
	{
		xmlBufferFree( request_buffer );
		request_buffer = NULL;
	}

	// Workaround bug in libxml2.  If there is an error while creating the xml document,
	// then calling xmlFreeTextWriter will crash the caller.

	if ( request && !err )
	{
		xmlFreeTextWriter( request );
	}

	if ( formed_uri )
	{
		g_free( formed_uri );
	}

	if ( err )
	{
		g_object_unref( self );
		self = NULL;
	}

	return self;
}


gboolean
e_zimbra_connection_register_client
	(
	EZimbraConnection			*	cnc,
	const char					*	folder_id,
	gpointer						handle,
	EZimbraConnectionClientSyncFunc	sync_func
	)
{
	EZimbraConnectionClient	*	client;
	GThread					*	thread	=	NULL;
	GError					*	error	=	NULL;
	gboolean					ret		= TRUE;

	g_static_rec_mutex_lock( &cnc->priv->mutex );

	client = g_hash_table_lookup( cnc->priv->clients, folder_id );
	zimbra_check( !client, exit, g_warning( "already registered client" ); ret = FALSE );

	client = ( EZimbraConnectionClient* ) malloc( sizeof( EZimbraConnectionClient ) );
	zimbra_check( client, exit, g_warning( "malloc failed" ); ret = FALSE );

	client->handle		= handle;
	client->sync_func	= sync_func;

	g_hash_table_insert( cnc->priv->clients, g_strdup( folder_id ), client );

	thread = g_thread_create( ( GThreadFunc ) e_zimbra_connection_sync, cnc, FALSE, &error );
	zimbra_check( thread, exit, g_warning( G_STRLOC ": %s", error->message ); g_error_free (error); ret = FALSE );

	ret = TRUE;

exit:

	g_static_rec_mutex_unlock( &cnc->priv->mutex );

	return ret;
}


void
e_zimbra_connection_unregister_client
	(
	EZimbraConnection	*	cnc,
	const char			*	folder_id
	)
{
	EZimbraConnectionClient * client;

	g_static_rec_mutex_lock( &cnc->priv->mutex );

	if ( ( client = g_hash_table_lookup( cnc->priv->clients, folder_id ) ) != NULL )
	{
		g_hash_table_remove( cnc->priv->clients, folder_id );
		free( client );
	}

	g_static_rec_mutex_unlock( &cnc->priv->mutex );
}


static EZimbraConnectionStatus
e_zimbra_connection_start_message
	(
	EZimbraConnection	*	cnc,
	const char			*	func,
	const char			*	urn,
	xmlBufferPtr		*	request_buffer,
	xmlTextWriterPtr	*	request
	)
{
	int						rc;
	EZimbraConnectionStatus err = 0;

	GLOG_INFO( "enter" );

	if ( !cnc )
	{
		g_warning( "cnc is NULL" );
		goto exit;
	}

	*request_buffer = xmlBufferCreate();
	zimbra_check( *request_buffer, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	*request = xmlNewTextWriterMemory( *request_buffer, 0 );
	zimbra_check( *request, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

    rc = xmlTextWriterStartDocument( *request, NULL, MY_ENCODING, NULL );
	zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	rc = xmlTextWriterStartElement( *request, BAD_CAST "soap:Envelope" );
	zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	rc = xmlTextWriterWriteAttribute( *request, BAD_CAST "xmlns:soap", BAD_CAST "http://www.w3.org/2003/05/soap-envelope" );
	zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	if ( cnc->priv->auth_token && cnc->priv->session_id )
	{
		rc = xmlTextWriterStartElement( *request, BAD_CAST "soap:Header" );
		zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

		rc = xmlTextWriterStartElement( *request, BAD_CAST "context" );
		zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );
	
		rc = xmlTextWriterWriteAttribute( *request, BAD_CAST "xmlns", BAD_CAST "urn:zimbra" );
		zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

		rc = xmlTextWriterWriteElement( *request, BAD_CAST "authToken", BAD_CAST cnc->priv->auth_token );
		zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

		rc = xmlTextWriterStartElement( *request, BAD_CAST "sessionId" );
		zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

		rc = xmlTextWriterWriteAttribute( *request, BAD_CAST "id", BAD_CAST cnc->priv->session_id );
		zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

		// </sessionId>

		rc = xmlTextWriterEndElement( *request );
		zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

		// </context>

		rc = xmlTextWriterEndElement( *request );
		zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

		// </soap:Header>

		rc = xmlTextWriterEndElement( *request );
		zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );
	}

	// <soap:Body>

	rc = xmlTextWriterStartElement( *request, BAD_CAST "soap:Body" );
	zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	// <func>

	rc = xmlTextWriterStartElement( *request, BAD_CAST func );
	zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	rc = xmlTextWriterWriteFormatAttribute( *request, BAD_CAST "xmlns", "urn:%s", urn );
	zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

exit:

	return err;
}


static xmlDocPtr
e_zimbra_connection_send_message
	(
	EZimbraConnection	*	cnc,
	xmlBufferPtr		*	request_buffer,
	xmlTextWriterPtr	*	request
	)
{
	struct CurlResponse				response	=	{ NULL, 0 };
	EZimbraConnectionPrivate	*	priv		=	NULL;
	xmlDocPtr						doc			=	NULL;
	int								rc;
	CURLcode						err;

	GLOG_INFO( "enter" );

	if ( !cnc )
	{
		g_warning( "cnc is NULL" );
		goto exit;
	}

	priv = cnc->priv;

	if ( !priv )
	{
		g_warning( "cnc->priv is NULL" );
		goto exit;
	}

	g_mutex_lock( priv->send_mutex );

	if ( !priv->curl )
	{
		g_warning( "priv->curl is NULL" );
		goto exit;
	}

	// Finish building the SOAP message

	rc = xmlTextWriterEndDocument( *request );
	zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	// This call hands off the data to the buffer.  I know it looks strange
	// to free this thing right in the middle of the battle.

	xmlFreeTextWriter( *request );
	*request = NULL;

	GLOG_DEBUG( "priv->uri = %s", priv->uri );
	GLOG_DEBUG( "sending message: %s", (*request_buffer)->content );

	curl_easy_setopt( priv->curl, CURLOPT_URL,            priv->uri );
	curl_easy_setopt( priv->curl, CURLOPT_NOPROGRESS,     1 );
	curl_easy_setopt( priv->curl, CURLOPT_NOSIGNAL,       1 );
	curl_easy_setopt( priv->curl, CURLOPT_POSTFIELDS,     (*request_buffer)->content );
	curl_easy_setopt( priv->curl, CURLOPT_WRITEFUNCTION,  curl_write_func );
	curl_easy_setopt( priv->curl, CURLOPT_WRITEDATA,      &response );

	err = curl_easy_perform( priv->curl );

	if ( err )
	{
		g_warning( "curl_easy_perform returned an error: %d\n", err );
		goto exit;
	}

	GLOG_DEBUG( "response = %s", response.text );

	doc = xmlParseMemory( response.text, response.size );

	if ( !doc )
	{
		g_warning( "doc is NULL" );
		goto exit;
	}

exit:

	if ( response.text )
	{
		free( response.text );
	}

	if ( *request_buffer )
	{
		xmlBufferFree( *request_buffer );
		*request_buffer = NULL;
	}

	if ( priv )
	{
		g_mutex_unlock( priv->send_mutex );
	}
		
	return doc;
}


EZimbraConnectionStatus
e_zimbra_connection_logout (EZimbraConnection *cnc)
{
	g_return_val_if_fail (E_IS_ZIMBRA_CONNECTION (cnc), E_ZIMBRA_CONNECTION_STATUS_INVALID_OBJECT);

	g_object_unref (cnc);

	return E_ZIMBRA_CONNECTION_STATUS_OK;
}


EZimbraFolder*
e_zimbra_connection_peek_folder_by_id
	(
	EZimbraConnection	*	cnc,
	const char			*	id
	)
{
	EZimbraFolder * folder = NULL;
	int				i;

	for ( i = 0; i < g_list_length( cnc->priv->folders ); i++ )
	{
		folder = g_list_nth_data( cnc->priv->folders, i );

		if ( g_str_equal( e_zimbra_folder_get_id( folder ), id ) )
		{
			break;
		}

		folder = NULL;
	}

	return folder;
}


EZimbraConnectionStatus
e_zimbra_connection_get_folders_by_type
	(
	EZimbraConnection	*	cnc,
	EZimbraFolderType		type,
	GList				**	list
	)
{
	int						i;
	EZimbraConnectionStatus	err;
	
	zimbra_check( E_IS_ZIMBRA_CONNECTION( cnc ), exit, err = E_ZIMBRA_CONNECTION_STATUS_INVALID_OBJECT );

	GLOG_INFO( "enter" );
	GLOG_DEBUG( "type = %d", type );

	for ( i = 0; i < g_list_length( cnc->priv->folders ); i++ )
	{
		EZimbraFolder * folder;

		folder = g_list_nth_data( cnc->priv->folders, i );

		if ( e_zimbra_folder_get_folder_type( folder ) == type )
		{
			g_object_ref( folder );
			*list = g_list_append( *list, folder );
		}
	}

	err = E_ZIMBRA_CONNECTION_STATUS_OK;
 
exit:

	return err;
}


void
e_zimbra_connection_free_folders
	(
	GList * folders
	)
{
	GLOG_INFO( "enter" );

	g_return_if_fail( folders != NULL );

	g_list_foreach( folders, (GFunc) g_object_unref, NULL );
	g_list_free( folders );
}


char*
e_zimbra_connection_get_folder_id
	(
	EZimbraConnection	*	cnc,
	const char			*	name
	)
{
	char	*	id = NULL;
	int			i;

	GLOG_INFO( "enter" );

	g_return_val_if_fail (E_IS_ZIMBRA_CONNECTION (cnc), NULL);
	g_return_val_if_fail (name != NULL, NULL);

	for ( i = 0; i < g_list_length( cnc->priv->folders ); i++ )
	{
		EZimbraFolder * folder;

		folder = g_list_nth_data( cnc->priv->folders, i );

		if ( g_str_equal( e_zimbra_folder_get_name( folder ), name ) )
		{
			id = g_strdup( e_zimbra_folder_get_id( folder ) );
			break;
		}
	}

	return id;
}


static EZimbraItem*
get_appointment_item
	(
	EZimbraConnection	*	cnc,
	const char			*	zid
	)
{
	xmlBufferPtr			request_buffer	=	NULL;
	xmlTextWriterPtr		request			=	NULL;
	xmlDocPtr				response		=	NULL;
	xmlNode				*	bodyNode		=	NULL;
	xmlNode				*	apptNode		=	NULL;
	EZimbraItem			*	item			=	NULL;
	int						rc;
	EZimbraConnectionStatus	err;

	// build the SOAP message

	err = e_zimbra_connection_start_message( cnc, "GetAppointmentRequest", "zimbraMail", &request_buffer, &request );
	zimbra_check_okay( err, exit );

	rc = xmlTextWriterWriteAttribute( request, BAD_CAST "id", BAD_CAST zid );
	zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	// Send message to server

	response = e_zimbra_connection_send_message( cnc, &request_buffer, &request );
	zimbra_check( response, exit, item = NULL; g_warning( "e_zimbra_connection_send_message returned NULL" ) );

	err = e_zimbra_connection_parse_response_status( response );

	if ( err != E_ZIMBRA_CONNECTION_STATUS_OK )
	{
		if ( err == E_ZIMBRA_CONNECTION_STATUS_INVALID_CONNECTION )
		{
			reauthenticate( cnc );
		}

		g_warning( "e_zimbra_connection_parse_response_status returned %d", err );
		item = NULL;
		goto exit;
	}

	bodyNode = xml_parse_path( xmlDocGetRootElement( response ), "Body/GetAppointmentResponse" );
	zimbra_check( bodyNode, exit, item = NULL );

	apptNode = e_zimbra_xml_find_child_by_name( bodyNode, "appt" );
	zimbra_check( apptNode, exit, item = NULL );

	item = e_zimbra_item_new_from_soap_parameter( cnc, E_ZIMBRA_ITEM_TYPE_APPOINTMENT, apptNode );
	zimbra_check( item, exit, item = NULL );
 
exit:

	if ( response )
	{
		xmlFreeDoc( response );
	}

	if ( request_buffer )
	{
		xmlBufferFree( request_buffer );
	}

	// Workaround bug in libxml2.  If there is an error while creating the xml document,
	// then calling xmlFreeTextWriter will crash the caller.

	if ( request && item )
	{
		xmlFreeTextWriter( request );
	}

	return item;
}


static EZimbraConnectionStatus
get_appointment_items
	(
	EZimbraConnection	*	cnc,
	GPtrArray			*	ids,
	GPtrArray			**	items
	)
{
	int						i;
	EZimbraConnectionStatus	err = 0;

	GLOG_DEBUG( "in get_appointment_items" );

	*items = g_ptr_array_new();
	
	for ( i = 0; i < ids->len; i++ )
	{
		const char	*	zid = g_ptr_array_index( ids, i );
		EZimbraItem *	item;

		GLOG_DEBUG( "looking at zid: %s", zid );

		if ( ( item = get_appointment_item( cnc, zid ) ) != NULL )
		{
			GLOG_DEBUG( "adding item to list" );

			g_ptr_array_add( *items, item );
		}
	}
 
	return err;
}


static EZimbraItem*
get_contact_item
	(
	EZimbraConnection	*	cnc,
	const char			*	id
	)
{
	xmlBufferPtr			request_buffer	= NULL;
	xmlTextWriterPtr		request			= NULL;
	xmlDocPtr				response		= NULL;
	xmlNode				*	bodyNode		= NULL;
	xmlNode				*	child			= NULL;
	char				*	folder_id		= NULL;
	EZimbraItem			*	item			= NULL;
	int						rc;
	EZimbraConnectionStatus	err				= 0;
	
	// build the SOAP message

	err = e_zimbra_connection_start_message( cnc, "GetContactsRequest", "zimbraMail", &request_buffer, &request );
	zimbra_check_okay( err, exit );

	rc = xmlTextWriterStartElement( request, BAD_CAST "cn" );
	zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	rc = xmlTextWriterWriteAttribute( request, BAD_CAST "id", BAD_CAST id );
	zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	rc = xmlTextWriterEndElement( request );
	zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	// Send message to server

	response = e_zimbra_connection_send_message( cnc, &request_buffer, &request );

	if ( !response )
	{
		g_warning( "e_zimbra_connection_send_message returned NULL" );
		err = E_ZIMBRA_CONNECTION_STATUS_NO_RESPONSE;
		goto exit;
	}

	err = e_zimbra_connection_parse_response_status( response );

	if ( err != E_ZIMBRA_CONNECTION_STATUS_OK )
	{
		if ( err == E_ZIMBRA_CONNECTION_STATUS_INVALID_CONNECTION )
		{
			reauthenticate (cnc);
		}

		g_warning( "e_zimbra_connection_parse_response_status returned %d", err );
		goto exit;
	}

	// Parse these parameters into ebook components

	bodyNode = xml_parse_path( xmlDocGetRootElement( response ), "Body/GetContactsResponse" );
	zimbra_check( bodyNode, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	for ( child = bodyNode->children; child; child = child->next )
	{
		if ( child->type == XML_ELEMENT_NODE )
		{
			folder_id = e_zimbra_xml_find_attribute( child, "l" );
			zimbra_check( folder_id, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

			if ( !g_str_equal( folder_id, get_trash_id( cnc ) ) )
			{
				item = e_zimbra_item_new_from_soap_parameter( cnc, E_ZIMBRA_ITEM_TYPE_CONTACT, child );
			}

			break;
		}
	}
 
exit:

	if ( folder_id )
	{
		g_free( folder_id );
	}

	if ( response )
	{
		xmlFreeDoc( response );
	}

	if ( request_buffer )
	{
		xmlBufferFree( request_buffer );
	}

	// Workaround bug in libxml2.  If there is an error while creating the xml document,
	// then calling xmlFreeTextWriter will crash the caller.

	if ( request && !err )
	{
		xmlFreeTextWriter( request );
	}

	return item;
}


static EZimbraConnectionStatus
get_contact_items
	(
	EZimbraConnection	*	cnc,
	GPtrArray			*	ids,
	GPtrArray			**	items
	)
{
	xmlBufferPtr			request_buffer	= NULL;
	xmlTextWriterPtr		request			= NULL;
	xmlDocPtr				response		= NULL;
	xmlNode				*	bodyNode		= NULL;
	xmlNode				*	child			= NULL;
	int						i;
	int						rc;
	EZimbraConnectionStatus	err				= 0;

	zimbra_check( ids && ids->len, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );
	
	// build the SOAP message

	err = e_zimbra_connection_start_message( cnc, "GetContactsRequest", "zimbraMail", &request_buffer, &request );
	zimbra_check_okay( err, exit );

	for ( i = 0; i < ids->len; i++ )
	{
		const char * id = g_ptr_array_index( ids, i );

		rc = xmlTextWriterStartElement( request, BAD_CAST "cn" );
		zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

		rc = xmlTextWriterWriteAttribute( request, BAD_CAST "id", BAD_CAST id );
		zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

		rc = xmlTextWriterEndElement( request );
		zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );
	}

	// Send message to server

	response = e_zimbra_connection_send_message( cnc, &request_buffer, &request );

	if ( !response )
	{
		g_warning( "e_zimbra_connection_send_message returned NULL" );
		err = E_ZIMBRA_CONNECTION_STATUS_NO_RESPONSE;
		goto exit;
	}

	err = e_zimbra_connection_parse_response_status( response );

	if ( err != E_ZIMBRA_CONNECTION_STATUS_OK )
	{
		if ( err == E_ZIMBRA_CONNECTION_STATUS_INVALID_CONNECTION )
		{
			reauthenticate (cnc);
		}

		g_warning( "e_zimbra_connection_parse_response_status returned %d", err );
		goto exit;
	}

	// Parse these parameters into ebook components

	bodyNode = xml_parse_path( xmlDocGetRootElement( response ), "Body/GetContactsResponse" );
	zimbra_check( bodyNode, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	*items = g_ptr_array_new();
	zimbra_check( *items, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	for ( child = bodyNode->children; child; child = child->next )
	{
		if ( child->type == XML_ELEMENT_NODE )
		{
			char		*	folder_id;
			EZimbraItem *	item;

			if ( ( folder_id = e_zimbra_xml_find_attribute( child, "l" ) ) && ( !g_str_equal( folder_id, get_trash_id( cnc ) ) ) && ( item = e_zimbra_item_new_from_soap_parameter( cnc, E_ZIMBRA_ITEM_TYPE_CONTACT, child ) ) )
			{
				g_ptr_array_add( *items, item );
			}

			if ( folder_id )
			{
				g_free( folder_id );
			}
		}
	}
 
exit:

	if ( response )
	{
		xmlFreeDoc( response );
	}

	if ( request_buffer )
	{
		xmlBufferFree( request_buffer );
	}

	// Workaround bug in libxml2.  If there is an error while creating the xml document,
	// then calling xmlFreeTextWriter will crash the caller.

	if ( request && !err )
	{
		xmlFreeTextWriter( request );
	}

	return err;
}


EZimbraConnectionStatus
e_zimbra_connection_get_item
	(
	EZimbraConnection	*	cnc,
	EZimbraItemType			type,
	const char			*	id,
	EZimbraItem			**	item
	)
{
	gboolean				mutex_locked	=	FALSE;
	EZimbraConnectionStatus	err				=	0;
	
	zimbra_check( E_IS_ZIMBRA_CONNECTION( cnc ), exit, err = E_ZIMBRA_CONNECTION_STATUS_INVALID_OBJECT );

	GLOG_INFO( "enter" );

	g_static_rec_mutex_lock( &cnc->priv->mutex );
	mutex_locked = TRUE;

	switch ( type )
	{
		case E_ZIMBRA_ITEM_TYPE_APPOINTMENT:
		{
			GLOG_DEBUG( "calling get_appointment_item" );

			*item = get_appointment_item( cnc, id );
			zimbra_check( *item, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );
		}
		break;

		case E_ZIMBRA_ITEM_TYPE_CONTACT:
		{
			*item = get_contact_item( cnc, id );
			zimbra_check( *item, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );
		}
		break;

		default:
		{
		}
		break;
	}
	
exit:

	if ( mutex_locked )
	{
		g_static_rec_mutex_unlock( &cnc->priv->mutex );
	}

	return err;
}


EZimbraConnectionStatus
e_zimbra_connection_get_items
	(
	EZimbraConnection	*	cnc,
	EZimbraItemType			type,
	GPtrArray 			*	ids,
	GPtrArray			**	items
	)
{
	gboolean				mutex_locked	=	FALSE;
	EZimbraConnectionStatus	err				=	0;
	
	zimbra_check( E_IS_ZIMBRA_CONNECTION( cnc ), exit, err = E_ZIMBRA_CONNECTION_STATUS_INVALID_OBJECT );

	GLOG_INFO( "enter" );

	g_static_rec_mutex_lock( &cnc->priv->mutex );
	mutex_locked = TRUE;

	switch ( type )
	{
		case E_ZIMBRA_ITEM_TYPE_APPOINTMENT:
		{
			GLOG_DEBUG( "calling get_appointment_items" );

			err = get_appointment_items( cnc, ids, items );
			zimbra_check_okay( err, exit );
		}
		break;

		case E_ZIMBRA_ITEM_TYPE_CONTACT:
		{
			err = get_contact_items( cnc, ids, items );
			zimbra_check_okay( err, exit );
		}
		break;

		default:
		{
		}
		break;
	}
	
exit:

	if ( mutex_locked )
	{
		g_static_rec_mutex_unlock( &cnc->priv->mutex );
	}

	return err;
}


static EZimbraConnectionStatus
e_zimbra_connection_sync_folder
	(
	EZimbraConnection	*	cnc,
	xmlNode				*	node,
	ESourceList			**	ecal_source_list,
	ESourceList			**	ebook_source_list,
	const char			*	parent_name
	)
{
	ESourceGroup		*	source_group		=	NULL;
	ESource				*	source				=	NULL;
	char				*	view				=	NULL;
	xmlNode				*	child				=	NULL;
	char				*	folder_id			=	NULL;
	char				*	full_folder_name	=	NULL;
	char				*	folder_name			=	NULL;
	EZimbraFolder		*	folder				=	NULL;
	char				*	uri					=	NULL;
	gboolean				ok;
	EZimbraFolderType		type;
	EZimbraConnectionStatus err					=	0;

	// Get the folder ids

	folder_id = e_zimbra_xml_find_attribute( node, "id" );
	zimbra_check( folder_id, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	folder_name = e_zimbra_xml_find_attribute( node, "name" );

	if ( !folder_name )
	{
		folder_name = g_strdup( "" );
	}

	zimbra_check( folder_name, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	if ( *parent_name )
	{
		full_folder_name = g_strdup_printf( "%s/%s", parent_name, folder_name );
	}
	else if ( !g_str_equal( folder_name, "USER_ROOT" ) )
	{
		full_folder_name = g_strdup( folder_name );
	}
	else
	{
		full_folder_name = g_strdup( "" );
	}

	folder = e_zimbra_connection_peek_folder_by_id( cnc, folder_id );

	if ( !folder )
	{
		folder = e_zimbra_folder_new_from_soap_parameter( node, cnc->priv->cache_folder );
		zimbra_check( folder, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

		cnc->priv->folders = g_list_append( cnc->priv->folders, folder );
	}

	zimbra_check( folder, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	type = e_zimbra_folder_get_folder_type( folder );

	switch ( type )
	{
		case E_ZIMBRA_FOLDER_TYPE_CALENDAR:
		{
			// Do we have a calendar ESource that corresponds to this folder?

			if ( !*ecal_source_list )
			{
				*ecal_source_list = e_source_list_new_for_gconf_default( ECAL_PATH );
				zimbra_check( *ecal_source_list, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );
			}
	
			source_group = e_source_list_peek_group_by_name( *ecal_source_list, cnc->priv->account );
			zimbra_check( source_group, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

			source = e_zimbra_connection_peek_source_by_id( cnc, *ecal_source_list, source_group, folder_id );

			if ( source )
			{
				source = NULL;
			}
			else
			{
				uri = g_strdup_printf( "%s@%s:%d/%s", cnc->priv->username, cnc->priv->hostname, cnc->priv->port, folder_id );
				zimbra_check( uri, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

				GLOG_DEBUG( "trying to create new source: name = %s, id = %s, uri = %s\n", folder_name, folder_id, uri );

				source = e_source_new( full_folder_name, uri );
				zimbra_check( source, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

				e_source_set_property( source, "account", cnc->priv->account );
				e_source_set_property( source, "id", folder_id );
				e_source_set_property( source, "username", cnc->priv->username );
				e_source_set_property( source, "auth", "1" );
				e_source_set_property( source, "use_ssl", cnc->priv->use_ssl ? "always" : "never" );

				e_source_set_color( source, 0xfed4d3 );

				ok = e_source_group_add_source( source_group, source, -1 );
				zimbra_check( ok, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );
			}
		}
		break;

		case E_ZIMBRA_FOLDER_TYPE_CONTACTS:
		{
			// Do we have a calendar ESource that corresponds to this folder?

			if ( !*ebook_source_list )
			{
				*ebook_source_list = e_source_list_new_for_gconf_default( EBOOK_PATH );
				zimbra_check( *ebook_source_list, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );
			}

			source_group = e_source_list_peek_group_by_name( *ebook_source_list, cnc->priv->account );
			zimbra_check( source_group, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );
	
			if ( ( source = e_zimbra_connection_peek_source_by_id( cnc, *ebook_source_list, source_group, folder_id ) ) != NULL )
			{
				source = NULL;
			}
			else
			{
				uri = g_strdup_printf( "%s@%s:%d/%s", cnc->priv->username, cnc->priv->hostname, cnc->priv->port, folder_id );
				zimbra_check( uri, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

				GLOG_DEBUG( "trying to create new source: name = %s, id = %s, uri = %s\n", full_folder_name, folder_id, uri );

				source = e_source_new( full_folder_name, uri );
				zimbra_check( source, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

				e_source_set_property( source, "account", cnc->priv->account );
				e_source_set_property( source, "id", folder_id );
				e_source_set_property( source, "auth", "plain/password");
				e_source_set_property( source, "auth-domain", "Zimbra");
				e_source_set_property( source, "user", cnc->priv->username );
				e_source_set_property( source, "binddn", cnc->priv->username );
				e_source_set_property( source, "auth", "1" );
				e_source_set_property( source, "use_ssl", cnc->priv->use_ssl ? "always" : "never" );

				ok = e_source_group_add_source( source_group, source, -1 );
				zimbra_check( ok, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );
			}
		}
		break;

		default:
		{
		}
		break;
	}

	// Get the ids of any constituent parts or recurse down through child folders

	for ( child = node->children; child; child = child->next )
	{
		// If it's a subfolder, then recurse

		if ( ( child->type == XML_ELEMENT_NODE ) && ( g_str_equal( ( const char* ) child->name, "folder" ) || g_str_equal( ( const char* ) child->name, "link" ) ) )
		{
			err = e_zimbra_connection_sync_folder( cnc, child, ecal_source_list, ebook_source_list, full_folder_name );
			zimbra_check( err == E_ZIMBRA_CONNECTION_STATUS_OK, exit, err = err );
		}

		// Else if it's a calendar or contacts folder, then get the changes

		else if ( ( ( type == E_ZIMBRA_FOLDER_TYPE_CALENDAR && ( g_str_equal( child->name, "appt" ) ) ) ) ||
		          ( ( type == E_ZIMBRA_FOLDER_TYPE_CONTACTS ) && ( g_str_equal( child->name, "cn" ) ) ) )
		{
			char * ids;
			char * savept;
			char * tok;

			if ( ( ids = e_zimbra_xml_find_attribute( child, "ids" ) ) != NULL )
			{
				e_zimbra_folder_add_changes( folder, E_ZIMBRA_FOLDER_CHANGE_TYPE_UPDATE, ids );

				tok = strtok_r( ids, ",", &savept );

				while ( tok )
				{
					e_file_cache_remove_object( cnc->priv->cache, tok );
					e_file_cache_add_object( cnc->priv->cache, tok, folder_id );
					tok = strtok_r( NULL, ",", &savept );
				}

				g_free( ids );
			}
		}
	}

exit:

	if ( source )
	{
		g_object_unref( source );
	}

	if ( folder_id )
	{
		g_free( folder_id );
	}

	if ( folder_name )
	{
		g_free( folder_name );
	}

	if ( full_folder_name )
	{
		g_free( full_folder_name );
	}

	if ( uri )
	{	
		g_free( uri );
	}

	if ( view )
	{
		g_free( view );
	}

	return err;
}


static EZimbraConnectionStatus
e_zimbra_connection_sync_appt
	(
	EZimbraConnection	*	cnc,
	xmlNode				*	node
	)
{
	char				*	folder_id	=	NULL;
	char				*	appt_id		=	NULL;
	char				*	rev			=	NULL;
	EZimbraFolder		*	folder		=	NULL;
	EZimbraConnectionStatus	err			=	E_ZIMBRA_CONNECTION_STATUS_OK;

	folder_id = e_zimbra_xml_find_attribute( node, "l" );
	zimbra_check( folder_id, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	folder = e_zimbra_connection_peek_folder_by_id( cnc, folder_id );
	zimbra_check( folder, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	if ( e_zimbra_folder_get_folder_type( folder ) != E_ZIMBRA_FOLDER_TYPE_TRASH )
	{
		char packed_id[ 1024 ];

		appt_id = e_zimbra_xml_find_attribute( node, "id" );
		zimbra_check( appt_id, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

		rev = e_zimbra_xml_find_attribute( node, "rev" );

		if ( !rev )
		{
			rev = e_zimbra_xml_find_attribute( node, "ms" );
		}

		zimbra_check( rev, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

		e_zimbra_utils_pack_update_id( packed_id, sizeof( packed_id ), appt_id, rev, 0 );

		// Add the change to the folder

		e_zimbra_folder_add_changes( folder, E_ZIMBRA_FOLDER_CHANGE_TYPE_UPDATE, packed_id );

		// And add this id to our cache

		e_file_cache_remove_object( cnc->priv->cache, appt_id );
		e_file_cache_add_object( cnc->priv->cache, appt_id, folder_id );
	}

exit:

	if ( rev )
	{
		g_free( rev );
	}

	if ( appt_id )
	{
		g_free( appt_id );
	}

	if ( folder_id )
	{
		g_free( folder_id );
	}

	return err;
}


static EZimbraConnectionStatus
e_zimbra_connection_sync_contact
	(
	EZimbraConnection	*	cnc,
	xmlNode				*	node
	)
{
	char				*	folder_id	=	NULL;
	char				*	contact_id	=	NULL;
	char				*	rev			=	NULL;
	EZimbraFolder		*	folder		=	NULL;
	EZimbraConnectionStatus	err			=	E_ZIMBRA_CONNECTION_STATUS_OK;

	folder_id = e_zimbra_xml_find_attribute( node, "l" );
	zimbra_check( folder_id, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	folder = e_zimbra_connection_peek_folder_by_id( cnc, folder_id );
	zimbra_check( folder, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	if ( e_zimbra_folder_get_folder_type( folder ) != E_ZIMBRA_FOLDER_TYPE_TRASH )
	{
		char packed_id[ 1024 ];

		contact_id = e_zimbra_xml_find_attribute( node, "id" );
		zimbra_check( contact_id, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );
			
		rev = e_zimbra_xml_find_attribute( node, "rev" );

		if ( !rev )
		{
			rev = e_zimbra_xml_find_attribute( node, "ms" );
		}

		zimbra_check( rev, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

		e_zimbra_utils_pack_update_id( packed_id, sizeof( packed_id ), contact_id, rev, 0 );

		e_zimbra_folder_add_changes( folder, E_ZIMBRA_FOLDER_CHANGE_TYPE_UPDATE, packed_id );

		// And add this id to our cache

		e_file_cache_remove_object( cnc->priv->cache, contact_id );
		e_file_cache_add_object( cnc->priv->cache, contact_id, folder_id );
	}

exit:

	if ( rev )
	{
		g_free( rev );
	}

	if ( contact_id )
	{
		g_free( contact_id );
	}

	if ( folder_id )
	{
		g_free( folder_id );
	}

	return err;
}


static EZimbraConnectionStatus
e_zimbra_connection_sync_delete
	(
	EZimbraConnection	*	cnc,
	xmlNode				*	node,
	ESourceList			**	ecal_source_list,
	ESourceList			**	ebook_source_list
	)
{
	const char			*	folder_id	=	NULL;
	GPtrArray			*	array		=	NULL;
	char				*	ids			=	NULL;
	char				*	id			=	NULL;
	EZimbraFolder		*	folder		=	NULL;
	int						i;
	EZimbraConnectionStatus	err			=	0;

	ids = e_zimbra_xml_find_attribute( node, "ids" );
	zimbra_check( ids, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	array = e_zimbra_utils_make_array_from_string( ids );
	zimbra_check( array, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	for ( i = 0; i < array->len; i++ )
	{
		id = g_ptr_array_index( array, i );

		// First check to see if this is a folder

		if ( ( folder = e_zimbra_connection_peek_folder_by_id( cnc, id ) ) != NULL )
		{
			ESourceList		*	source_list		= NULL;
			ESourceGroup	*	source_group	= NULL;
			ESource			*	source			= NULL;
			
			GLOG_DEBUG( "DELETING FOLDER!!!!" );

			if ( e_zimbra_folder_get_folder_type( folder ) == E_ZIMBRA_FOLDER_TYPE_CALENDAR )
			{
				if ( !*ecal_source_list )
				{
					*ecal_source_list = e_source_list_new_for_gconf_default( ECAL_PATH );
					zimbra_check( *ecal_source_list, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );
				}

				source_list		= *ecal_source_list;
				source_group	= e_source_list_peek_group_by_name( *ecal_source_list, cnc->priv->account );
			}
			else if ( e_zimbra_folder_get_folder_type( folder ) == E_ZIMBRA_FOLDER_TYPE_CONTACTS )
			{
				if ( !*ebook_source_list )
				{
					*ebook_source_list = e_source_list_new_for_gconf_default( EBOOK_PATH );
					zimbra_check( *ebook_source_list, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );
				}

				source_list		= *ebook_source_list;
				source_group	= e_source_list_peek_group_by_name( *ebook_source_list, cnc->priv->account );
			}
	
			if ( source_group )
			{
				if ( ( source = e_zimbra_connection_peek_source_by_id( cnc, source_list, source_group, id ) ) != NULL )
				{
					GLOG_DEBUG( "got source!...trying to remove" );

					e_source_group_remove_source( source_group, source );
				}
			}

			cnc->priv->folders = g_list_remove( cnc->priv->folders, folder );
			g_object_unref( folder );
		}

		// Else check to see if we have it in our cache

		else if ( ( folder_id = e_file_cache_get_object( cnc->priv->cache, id ) ) != NULL )
		{
			// Look up the folder

			folder = e_zimbra_connection_peek_folder_by_id( cnc, folder_id );

			if ( !folder )
			{
				g_warning( "e_zimbra_connection_sync_delete: unknown folder %s", folder_id );

				// Remove it from cache.  Don't do this until we don't need folder_id anymore.

				e_file_cache_remove_object( cnc->priv->cache, id );

				continue;
			}
	
			// Only tell folders whose type is contacts or calendar because they're the only ones
			// we're concerned with.

			if ( ( e_zimbra_folder_get_folder_type( folder ) == E_ZIMBRA_FOLDER_TYPE_CALENDAR ) || ( e_zimbra_folder_get_folder_type( folder ) == E_ZIMBRA_FOLDER_TYPE_CONTACTS ) )
			{
				e_zimbra_folder_add_changes( folder, E_ZIMBRA_FOLDER_CHANGE_TYPE_DELETE, id );
			}

			// Remove it from cache.  Don't do this until we don't need folder_id anymore.

			e_file_cache_remove_object( cnc->priv->cache, id );
		}
	}

exit:

	if ( array )
	{
		g_ptr_array_free( array, TRUE );
	}

	return err;
}


static EZimbraConnectionStatus
e_zimbra_connection_sync_client
	(
	EZimbraConnection		*	cnc,
	EZimbraFolder			*	folder,
	EZimbraConnectionClient	*	client,
	unsigned					sync_request_time,
	unsigned					sync_response_time
	)
{
	GPtrArray			*	zcs_update_ids	=	NULL;
	GPtrArray			*	zcs_delete_ids	=	NULL;
	gboolean				ok;
	EZimbraConnectionStatus	err = 0;

	ok = e_zimbra_folder_get_changes( folder, &zcs_update_ids, &zcs_delete_ids );
	zimbra_check( ok, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	e_zimbra_folder_clr_changes( folder );

	client->sync_func( client->handle, e_zimbra_folder_get_name( folder ), sync_request_time, sync_response_time, zcs_update_ids, zcs_delete_ids );

exit:

	if ( zcs_update_ids )
	{
		g_ptr_array_free( zcs_update_ids, TRUE );
	}

	if ( zcs_delete_ids )
	{
		g_ptr_array_free( zcs_delete_ids, FALSE );
	}

	return err;
}


static ESource*
e_zimbra_connection_peek_source_by_id
	(
	EZimbraConnection	*	cnc,
	ESourceList			*	source_list,
	ESourceGroup		*	group,
	const char			*	id
	)
{
	const char	*	source_id;
	ESource		*	source	=	NULL;
	GSList		*	l		=	NULL;

	for ( l = e_source_group_peek_sources( group ); l; l = l->next )
	{
		source = E_SOURCE( l->data );

		if ( ( source_id = e_source_get_property( source, "id" ) ) && g_str_equal( source_id, id ) )
		{
			break;
		}

		source = NULL;
	}

	return source;
}


EZimbraConnectionStatus
e_zimbra_connection_create_item
	(
	EZimbraConnection	*	cnc,
	EZimbraItem			*	item,
	char				**	id,
	char				**	rev
	)
{
	gboolean				mutex_locked	=	FALSE;
	xmlBufferPtr			request_buffer	=	NULL;
	xmlTextWriterPtr		request			=	NULL;
	xmlDocPtr				response		=	NULL;
	xmlNodePtr				root;
	gboolean				ok;
	EZimbraConnectionStatus	err;
	
	zimbra_check( E_IS_ZIMBRA_CONNECTION( cnc ), exit, err = E_ZIMBRA_CONNECTION_STATUS_INVALID_OBJECT );

	g_static_rec_mutex_lock( &cnc->priv->mutex );
	mutex_locked = TRUE;

	// build the SOAP message

	switch ( e_zimbra_item_get_item_type( item ) )
	{
		case E_ZIMBRA_ITEM_TYPE_APPOINTMENT:
		{
			err = e_zimbra_connection_start_message( cnc, "SetAppointmentRequest", "zimbraMail", &request_buffer, &request );
			zimbra_check( !err, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

			ok = e_zimbra_item_append_to_soap_message( item, E_ZIMBRA_ITEM_CHANGE_TYPE_ADD, request );
			zimbra_check( ok, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );
		}
		break;

		case E_ZIMBRA_ITEM_TYPE_CONTACT:
		{
			err = e_zimbra_connection_start_message( cnc, "CreateContactRequest", "zimbraMail", &request_buffer, &request );
			zimbra_check( !err, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

			ok = e_zimbra_item_append_to_soap_message( item, E_ZIMBRA_ITEM_CHANGE_TYPE_ADD, request );
			zimbra_check( ok, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );
		}
		break;

		default:
		{
			zimbra_check( 0, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );
			request = NULL;
		}
		break;
	}

	// Send message to server

	response = e_zimbra_connection_send_message( cnc, &request_buffer, &request );

	if ( !response )
	{
		g_warning( "e_zimbra_connection_send_message returned NULL" );
		err = E_ZIMBRA_CONNECTION_STATUS_NO_RESPONSE;
		goto exit;
	}

	err = e_zimbra_connection_parse_response_status( response );

	if ( err != E_ZIMBRA_CONNECTION_STATUS_OK )
	{
		if ( err == E_ZIMBRA_CONNECTION_STATUS_INVALID_CONNECTION )
		{
			reauthenticate (cnc);
		}

		g_warning( "e_zimbra_connection_parse_response_status returned %d", err );
		goto exit;
	}

	root = xmlDocGetRootElement( response );
	zimbra_check( root, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	*rev = e_zimbra_connection_get_change_token_from_response( root );
	zimbra_check( *rev, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	switch ( e_zimbra_item_get_item_type( item ) )
	{
		case E_ZIMBRA_ITEM_TYPE_APPOINTMENT:
		{
			xmlNode * node;

			node = xml_parse_path( root, "Body/SetAppointmentResponse" );
			zimbra_check( node, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

			*id = e_zimbra_xml_find_attribute( node, "apptId" );
			zimbra_check( *id, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );
		}
		break;

		case E_ZIMBRA_ITEM_TYPE_CONTACT:
		{
			xmlNode * node;

			node = xml_parse_path( root, "Body/CreateContactResponse/cn" );
			zimbra_check( node, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

			*id = g_strdup( e_zimbra_xml_find_attribute( node, "id" ) );
			zimbra_check( *id, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );
		}
		break;

		default:
		{
			zimbra_check( 0, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );
		}
		break;
	}

exit:

	if ( response )
	{
		xmlFreeDoc( response );
	}

	if ( request_buffer )
	{
		xmlBufferFree( request_buffer );
	}

	// Workaround bug in libxml2.  If there is an error while creating the xml document,
	// then calling xmlFreeTextWriter will crash the caller.

	if ( request && !err )
	{
		xmlFreeTextWriter( request );
	}

	if ( mutex_locked )
	{
		g_static_rec_mutex_unlock( &cnc->priv->mutex );
	}

	return err;
}


EZimbraConnectionStatus 
e_zimbra_connection_modify_item
	(
	EZimbraConnection	*	cnc,
	EZimbraItem			*	item,
	const char			*	id,
	char				**	rev
	)
{
	gboolean				mutex_locked	=	FALSE;
	xmlBufferPtr			request_buffer	=	NULL;
	xmlTextWriterPtr		request			=	NULL;
	xmlDocPtr				response		=	NULL;
	xmlNodePtr				root;
	gboolean				ok;
	EZimbraConnectionStatus err;
	
	zimbra_check( E_IS_ZIMBRA_CONNECTION (cnc), exit, err = E_ZIMBRA_CONNECTION_STATUS_INVALID_CONNECTION );
	zimbra_check( id, exit, err = E_ZIMBRA_CONNECTION_STATUS_INVALID_OBJECT );
	zimbra_check( item, exit, err = E_ZIMBRA_CONNECTION_STATUS_INVALID_OBJECT );

	g_static_rec_mutex_lock( &cnc->priv->mutex );
	mutex_locked = TRUE;

	switch ( e_zimbra_item_get_item_type( item ) )
	{
		case E_ZIMBRA_ITEM_TYPE_APPOINTMENT:
		{
			err = e_zimbra_connection_start_message( cnc, "SetAppointmentRequest", "zimbraMail", &request_buffer, &request );
			zimbra_check_okay( err, exit );

			ok = e_zimbra_item_append_to_soap_message( item, E_ZIMBRA_ITEM_CHANGE_TYPE_UPDATE, request );
			zimbra_check( ok, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );
		}
		break;

		case E_ZIMBRA_ITEM_TYPE_CONTACT:
		{
			err = e_zimbra_connection_start_message( cnc, "ModifyContactRequest", "zimbraMail", &request_buffer, &request );
			zimbra_check_okay( err, exit );

			ok = e_zimbra_item_append_to_soap_message( item, E_ZIMBRA_ITEM_CHANGE_TYPE_UPDATE, request );
			zimbra_check( ok, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );
		}
		break;

		default:
		{
			zimbra_check( 0, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );
			request = NULL;
		}
		break;
	}

	// Send message to server

	response = e_zimbra_connection_send_message( cnc, &request_buffer, &request );
	zimbra_check( response, exit, err = E_ZIMBRA_CONNECTION_STATUS_NO_RESPONSE );

	err = e_zimbra_connection_parse_response_status( response );

	if ( err != E_ZIMBRA_CONNECTION_STATUS_OK )
	{
		if ( err == E_ZIMBRA_CONNECTION_STATUS_INVALID_CONNECTION )
		{
			reauthenticate (cnc);
		}

		g_warning( "e_zimbra_connection_parse_response_status returned %d", err );
		goto exit;
	}

	root = xmlDocGetRootElement( response );
	zimbra_check( root, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	*rev = e_zimbra_connection_get_change_token_from_response( root );
	zimbra_check( *rev, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

exit:

	if ( response )
	{
		xmlFreeDoc( response );
	}

	if ( request_buffer )
	{
		xmlBufferFree( request_buffer );
	}

	// Workaround bug in libxml2.  If there is an error while creating the xml document,
	// then calling xmlFreeTextWriter will crash the caller.

	if ( request && !err )
	{
		xmlFreeTextWriter( request );
	}

	if ( mutex_locked )
	{
		g_static_rec_mutex_unlock( &cnc->priv->mutex );
	}

	return err;
}


EZimbraConnectionStatus
e_zimbra_connection_remove_item
	(
	EZimbraConnection	*	cnc,
	const char			*	container,
	EZimbraItemType			type,
	const char			*	id
	)
{
	gboolean				mutex_locked	=	FALSE;
	xmlBufferPtr			request_buffer	=	NULL;
	xmlTextWriterPtr		request			=	NULL;
	xmlDocPtr				response		=	NULL;
	int						rc;
	EZimbraConnectionStatus err;

	GLOG_INFO( "enter" );

	zimbra_check( E_IS_ZIMBRA_CONNECTION (cnc), exit, err = E_ZIMBRA_CONNECTION_STATUS_INVALID_CONNECTION );
	zimbra_check( id, exit, err = E_ZIMBRA_CONNECTION_STATUS_INVALID_OBJECT );

	g_static_rec_mutex_lock( &cnc->priv->mutex );
	mutex_locked = TRUE;

	switch ( type )
	{
		case E_ZIMBRA_ITEM_TYPE_APPOINTMENT:
		{
			err = e_zimbra_connection_start_message( cnc, "CancelAppointmentRequest", "zimbraMail", &request_buffer, &request );
			zimbra_check_okay( err, exit );

			rc = xmlTextWriterWriteAttribute( request, BAD_CAST "id", BAD_CAST id );
			zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

			rc = xmlTextWriterWriteAttribute( request, BAD_CAST "comp", BAD_CAST "0" );
			zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );
		}
		break;

		case E_ZIMBRA_ITEM_TYPE_CONTACT:
		{
			err = e_zimbra_connection_start_message( cnc, "ContactActionRequest", "zimbraMail", &request_buffer, &request );
			zimbra_check_okay( err, exit );

			rc = xmlTextWriterStartElement( request, BAD_CAST "action" );
			zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

			rc = xmlTextWriterWriteAttribute( request, BAD_CAST "id", BAD_CAST id );
			zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

			rc = xmlTextWriterWriteAttribute( request, BAD_CAST "op", BAD_CAST "move" );
			zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

			rc = xmlTextWriterWriteAttribute( request, BAD_CAST "l", BAD_CAST cnc->priv->trash_id );
			zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );
		}
		break;

		default:
		{
		}
		break;
	}

	zimbra_check( request, exit, err = E_ZIMBRA_CONNECTION_STATUS_INVALID_OBJECT );
	response = e_zimbra_connection_send_message( cnc, &request_buffer, &request );
	zimbra_check( response, exit, err = E_ZIMBRA_CONNECTION_STATUS_NO_RESPONSE );

	err = e_zimbra_connection_parse_response_status( response );

	if ( err != E_ZIMBRA_CONNECTION_STATUS_OK )
	{
		if ( err == E_ZIMBRA_CONNECTION_STATUS_INVALID_CONNECTION )
		{
			reauthenticate( cnc );
		}

		g_warning( "e_zimbra_connection_parse_response_status returned %d", err );
		goto exit;
	}

	err = E_ZIMBRA_CONNECTION_STATUS_OK;

exit:

	if ( response )
	{
		xmlFreeDoc( response );
	}

	if ( request_buffer )
	{
		xmlBufferFree( request_buffer );
	}

	// Workaround bug in libxml2.  If there is an error while creating the xml document,
	// then calling xmlFreeTextWriter will crash the caller.

	if ( request && !err )
	{
		xmlFreeTextWriter( request );
	}

	if ( mutex_locked )
	{
		g_static_rec_mutex_unlock( &cnc->priv->mutex );
	}

	return err;
}


EZimbraConnectionStatus
e_zimbra_connection_remove_items
	(
	EZimbraConnection	*	cnc,
	const char			*	container,
	EZimbraItemType			type,
	GPtrArray			*	ids
	)
{
	char				*	idString		=	NULL;
	xmlBufferPtr			request_buffer	=	NULL;
	xmlTextWriterPtr		request			=	NULL;
	xmlDocPtr				response		=	NULL;
	int						rc;
	EZimbraConnectionStatus err;

	GLOG_INFO( "enter" );

	zimbra_check( E_IS_ZIMBRA_CONNECTION (cnc), exit, err = E_ZIMBRA_CONNECTION_STATUS_INVALID_CONNECTION );
	zimbra_check( ids, exit, err = E_ZIMBRA_CONNECTION_STATUS_INVALID_OBJECT );

	switch ( type )
	{
		case E_ZIMBRA_ITEM_TYPE_CONTACT:
		{
			idString = e_zimbra_utils_make_string_from_array( ids );
			zimbra_check( idString, exit, err = E_ZIMBRA_CONNECTION_STATUS_INVALID_OBJECT );

			err = e_zimbra_connection_start_message( cnc, "ContactActionRequest", "zimbraMail", &request_buffer, &request );
			zimbra_check_okay( err, exit );

			rc = xmlTextWriterStartElement( request, BAD_CAST "action" );
			zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

			rc = xmlTextWriterWriteAttribute( request, BAD_CAST "id", BAD_CAST idString );
			zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

			rc = xmlTextWriterWriteAttribute( request, BAD_CAST "op", BAD_CAST "move" );
			zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

			rc = xmlTextWriterWriteAttribute( request, BAD_CAST "l", BAD_CAST cnc->priv->trash_id );
			zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );
		}
		break;

		default:
		{
		}
		break;
	}

	zimbra_check( request, exit, err = E_ZIMBRA_CONNECTION_STATUS_INVALID_OBJECT );
	response = e_zimbra_connection_send_message( cnc, &request_buffer, &request );
	zimbra_check( response, exit, err = E_ZIMBRA_CONNECTION_STATUS_NO_RESPONSE );

	err = e_zimbra_connection_parse_response_status( response );

	if ( err != E_ZIMBRA_CONNECTION_STATUS_OK )
	{
		if ( err == E_ZIMBRA_CONNECTION_STATUS_INVALID_CONNECTION )
		{
			reauthenticate( cnc );
		}

		g_warning( "e_zimbra_connection_parse_response_status returned %d", err );
		goto exit;
	}

	err = E_ZIMBRA_CONNECTION_STATUS_OK;

exit:

	if ( response )
	{
		xmlFreeDoc( response );
	}

	if ( request_buffer )
	{
		xmlBufferFree( request_buffer );
		request_buffer = NULL;
	}

	// Workaround bug in libxml2.  If there is an error while creating the xml document,
	// then calling xmlFreeTextWriter will crash the caller.

	if ( request && !err )
	{
		xmlFreeTextWriter( request );
	}

	if ( idString )
	{
		g_free( idString );
	}

	return err;
}


const char*
e_zimbra_connection_get_version (EZimbraConnection *cnc)
{
	g_return_val_if_fail (E_IS_ZIMBRA_CONNECTION (cnc), NULL);

	GLOG_INFO( "enter" );

	return (const char *) cnc->priv->version;
}


const char *
e_zimbra_connection_get_uri(EZimbraConnection *cnc)
{
	g_return_val_if_fail (E_IS_ZIMBRA_CONNECTION (cnc), NULL);

	GLOG_INFO( "enter" );

	return (const char *) cnc->priv->uri;
}


const char *
e_zimbra_connection_get_session_id (EZimbraConnection *cnc)
{
	g_return_val_if_fail (E_IS_ZIMBRA_CONNECTION (cnc), NULL);

	GLOG_INFO( "enter" );

	return (const char *) cnc->priv->session_id;
}


const char *
e_zimbra_connection_get_user_name (EZimbraConnection *cnc)
{
	g_return_val_if_fail (cnc != NULL, NULL);
	g_return_val_if_fail (E_IS_ZIMBRA_CONNECTION (cnc), NULL);

	GLOG_INFO( "enter" );
	return (const char *) cnc->priv->user_name;
}


const char* 
e_zimbra_connection_get_user_email (EZimbraConnection *cnc)
{
	g_return_val_if_fail (cnc != NULL, NULL);
	g_return_val_if_fail (E_IS_ZIMBRA_CONNECTION (cnc), NULL);
  
	GLOG_INFO( "enter" );
	return (const char*) cnc->priv->user_email;
	
}


const char *
e_zimbra_connection_get_user_uuid (EZimbraConnection *cnc)
{
	g_return_val_if_fail (E_IS_ZIMBRA_CONNECTION (cnc), NULL);

	GLOG_INFO( "enter" );
	return (const char *) cnc->priv->user_uuid;
}


static time_t
timet_from_string (const char *str)
{
	struct tm date;
        int len, i;
                                                              
        g_return_val_if_fail (str != NULL, -1);

	/* yyyymmdd[Thhmmss[Z]] */
        len = strlen (str);

        if (!(len == 8 || len == 15 || len == 16))
                return -1;

        for (i = 0; i < len; i++)
                if (!((i != 8 && i != 15 && isdigit (str[i]))
                      || (i == 8 && str[i] == 'T')
                      || (i == 15 && str[i] == 'Z')))
                        return -1;

#define digit_at(x,y) (x[y] - '0')

	date.tm_year = digit_at (str, 0) * 1000
                + digit_at (str, 1) * 100
                + digit_at (str, 2) * 10
                + digit_at (str, 3) -1900;
        date.tm_mon = digit_at (str, 4) * 10 + digit_at (str, 5) -1;
        date.tm_mday = digit_at (str, 6) * 10 + digit_at (str, 7);
        if (len > 8) {
                date.tm_hour = digit_at (str, 9) * 10 + digit_at (str, 10);
                date.tm_min  = digit_at (str, 11) * 10 + digit_at (str, 12);
                date.tm_sec  = digit_at (str, 13) * 10 + digit_at (str, 14);
        } else
		date.tm_hour = date.tm_min = date.tm_sec = 0; 

	return mktime (&date);
}


char *
e_zimbra_connection_format_date_string (const char *dtstring)
{
        char *str2;
        int i, j, len = strlen (dtstring);
	
	GLOG_INFO( "enter" );
        str2 = g_malloc0 (len);
	if (len <= 0)
		return str2;

        for (i = 0,j = 0; i < len; i++) {
                if ((dtstring[i] != '-') && (dtstring[i] != ':')) {
			str2[j] = dtstring[i];
			j++;
                }
        }

	str2[j] = '\0';
        return str2;
}


time_t
e_zimbra_connection_get_date_from_string (const char *dtstring)
{
	char *str2;
	int i, j, len = strlen (dtstring);
	time_t t;
	
	GLOG_DEBUG( "enter" );
        str2 = g_malloc0 (len+1);
        for (i = 0,j = 0; i < len; i++) {
                if ((dtstring[i] != '-') && (dtstring[i] != ':')) {
			str2[j] = dtstring[i];
			j++;
                }
        }

	str2[j] = '\0';
	t = timet_from_string (str2);
	g_free (str2);

        return t;
}


char*
e_zimbra_connection_uid_to_folder_id
	(
	EZimbraConnection	*	cnc,
	const char			*	uid
	)
{
	const char * folder_id;

	if ( ( folder_id = e_file_cache_get_object( cnc->priv->cache, uid ) ) != NULL )
	{
		return g_strdup( folder_id );
	}
	else
	{
		return NULL;
	}
}


static gboolean
e_zimbra_connection_update_source_id
	(
	EZimbraConnection	*	cnc,
	const char			*	path,
	const char			*	uid,
	const char			*	id
	)
{
	ESourceList		*	source_list		=	NULL;
	ESourceGroup	*	source_group	=	NULL;
	ESource			*	source			=	NULL;
	gboolean			ok				=	TRUE;

	source_list = e_source_list_new_for_gconf_default( path );
	zimbra_check( source_list, exit, ok = FALSE );

	source_group = e_source_list_peek_group_by_name( source_list, cnc->priv->account );
	zimbra_check( source_group, exit, ok = FALSE );

	source       = e_source_group_peek_source_by_uid( source_group, uid );
	zimbra_check( source, exit, ok = FALSE );

	e_source_set_property( source, "id", id );

exit:

	if ( source_list )
	{
		g_object_unref( source_list );
	}

	return ok;
}


EZimbraConnectionStatus 
e_zimbra_connection_create_folder
	(
	EZimbraConnection	*	cnc,
	const char			*	parent_id,
	ESource				*	source,
	EZimbraFolderType		type,
	char				**	folder_id,
	char				**	rev
	)
{
	const char			*	view			=	NULL;
	xmlBufferPtr			request_buffer	=	NULL;
	xmlTextWriterPtr		request			=	NULL;
	xmlDocPtr				response		=	NULL;
	const char			*	path			=	NULL;
	xmlNode				*	root;
	xmlNode				*	bodyNode;
	int						rc;
	EZimbraConnectionStatus	err;

	if ( type == E_ZIMBRA_FOLDER_TYPE_CALENDAR )
	{
		view = "appointment";
		path = ECAL_PATH;
	}
	else if ( type == E_ZIMBRA_FOLDER_TYPE_CONTACTS )
	{
		view = "contact";
		path = EBOOK_PATH;
	}
	else
	{
		view = NULL;
	}

	zimbra_check( view, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	// build the SOAP message

	err = e_zimbra_connection_start_message( cnc, "CreateFolderRequest", "zimbraMail", &request_buffer, &request );
	zimbra_check_okay( err, exit );

	rc = xmlTextWriterStartElement( request, BAD_CAST "folder" );
	zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	rc = xmlTextWriterWriteAttribute( request, BAD_CAST "view", BAD_CAST view );
	zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	rc = xmlTextWriterWriteAttribute( request, BAD_CAST "name", BAD_CAST e_source_peek_name( source ) );
	zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	rc = xmlTextWriterWriteAttribute( request, BAD_CAST "l", BAD_CAST parent_id );
	zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	// Send message to server

	response = e_zimbra_connection_send_message( cnc, &request_buffer, &request );
	zimbra_check( response, exit, g_warning( "e_zimbra_connection_send_message returned NULL" ) );

	err = e_zimbra_connection_parse_response_status( response );

	if ( err != E_ZIMBRA_CONNECTION_STATUS_OK )
	{
		if ( err == E_ZIMBRA_CONNECTION_STATUS_INVALID_CONNECTION )
		{
			reauthenticate (cnc);
		}

		g_warning( "e_zimbra_connection_parse_response_status returned %d", err );
		goto exit;
	}

	root = xmlDocGetRootElement( response );
	zimbra_check( root, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	*rev = e_zimbra_connection_get_change_token_from_response( root );
	zimbra_check( *rev, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	bodyNode = xml_parse_path( root, "Body/CreateFolderResponse/folder" );
	zimbra_check( bodyNode, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	*folder_id = e_zimbra_xml_find_attribute( bodyNode, "id" );

	e_zimbra_connection_update_source_id( cnc, path, e_source_peek_uid( source ), *folder_id );

exit:

	if ( response )
	{
		xmlFreeDoc( response );
	}

	if ( request_buffer )
	{
		xmlBufferFree( request_buffer );
	}

	// Workaround bug in libxml2.  If there is an error while creating the xml document,
	// then calling xmlFreeTextWriter will crash the caller.

	if ( request && !err )
	{
		xmlFreeTextWriter( request );
	}

	return err;
}


EZimbraConnectionStatus 
e_zimbra_connection_rename_folder
	(
	EZimbraConnection	*	cnc,
	const char			*	folder_id,
	const char			*	new_name,
	char				**	rev
	)
{
	xmlBufferPtr			request_buffer	=	NULL;
	xmlTextWriterPtr		request			=	NULL;
	xmlDocPtr				response		=	NULL;
	xmlNodePtr				root;
	int						rc;
	EZimbraConnectionStatus	err;

	// build the SOAP message

	err = e_zimbra_connection_start_message( cnc, "FolderActionRequest", "zimbraMail", &request_buffer, &request );
	zimbra_check_okay( err, exit );

	rc = xmlTextWriterStartElement( request, BAD_CAST "action" );
	zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	rc = xmlTextWriterWriteAttribute( request, BAD_CAST "id", BAD_CAST folder_id );
	zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	rc = xmlTextWriterWriteAttribute( request, BAD_CAST "op", BAD_CAST "rename" );
	zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	rc = xmlTextWriterWriteAttribute( request, BAD_CAST "name", BAD_CAST new_name );
	zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	// Send message to server

	response = e_zimbra_connection_send_message( cnc, &request_buffer, &request );
	zimbra_check( response, exit, g_warning( "e_zimbra_connection_send_message returned NULL" ) );

	err = e_zimbra_connection_parse_response_status( response );

	if ( err != E_ZIMBRA_CONNECTION_STATUS_OK )
	{
		if ( err == E_ZIMBRA_CONNECTION_STATUS_INVALID_CONNECTION )
		{
			reauthenticate (cnc);
		}

		g_warning( "e_zimbra_connection_parse_response_status returned %d", err );
		goto exit;
	}

	root = xmlDocGetRootElement( response );
	zimbra_check( root, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	*rev = e_zimbra_connection_get_change_token_from_response( root );
	zimbra_check( *rev, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

exit:

	if ( response )
	{
		xmlFreeDoc( response );
	}

	if ( request_buffer )
	{
		xmlBufferFree( request_buffer );
	}

	// Workaround bug in libxml2.  If there is an error while creating the xml document,
	// then calling xmlFreeTextWriter will crash the caller.

	if ( request && !err )
	{
		xmlFreeTextWriter( request );
	}

	return err;
}


EZimbraConnectionStatus 
e_zimbra_connection_delete_folder
	(
	EZimbraConnection	*	cnc,
	const char			*	folder_id
	)
{
	xmlBufferPtr			request_buffer	=	NULL;
	xmlTextWriterPtr		request			=	NULL;
	xmlDocPtr				response		=	NULL;
	int						rc;
	EZimbraConnectionStatus	err;

	// build the SOAP message

	err = e_zimbra_connection_start_message( cnc, "FolderActionRequest", "zimbraMail", &request_buffer, &request );
	zimbra_check_okay( err, exit );

	rc = xmlTextWriterStartElement( request, BAD_CAST "action" );
	zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	rc = xmlTextWriterWriteAttribute( request, BAD_CAST "id", BAD_CAST folder_id );
	zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	rc = xmlTextWriterWriteAttribute( request, BAD_CAST "op", BAD_CAST "delete" );
	zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	// Send message to server

	response = e_zimbra_connection_send_message( cnc, &request_buffer, &request );
	zimbra_check( response, exit, g_warning( "e_zimbra_connection_send_message returned NULL" ) );

	err = e_zimbra_connection_parse_response_status( response );

	if ( err != E_ZIMBRA_CONNECTION_STATUS_OK )
	{
		if ( err == E_ZIMBRA_CONNECTION_STATUS_INVALID_CONNECTION )
		{
			reauthenticate (cnc);
		}

		g_warning( "e_zimbra_connection_parse_response_status returned %d", err );
		goto exit;
	}

exit:

	if ( response )
	{
		xmlFreeDoc( response );
	}

	if ( request_buffer )
	{
		xmlBufferFree( request_buffer );
	}

	// Workaround bug in libxml2.  If there is an error while creating the xml document,
	// then calling xmlFreeTextWriter will crash the caller.

	if ( request && !err )
	{
		xmlFreeTextWriter( request );
	}

	return err;
}


EZimbraConnectionStatus
e_zimbra_connection_get_message
	(
	EZimbraConnection	*	cnc,
	const char			*	inv_id,
	char				**	message
	)
{
	xmlBufferPtr			request_buffer	=	NULL;
	xmlTextWriterPtr		request			=	NULL;
	xmlDocPtr				response		=	NULL;
	xmlNode				*	node			=	NULL;
	xmlNode				*	child			=	NULL;
	int						rc;
	EZimbraConnectionStatus	err;

	*message = NULL;

	// build the SOAP message

	err = e_zimbra_connection_start_message( cnc, "GetMsgRequest", "zimbraMail", &request_buffer, &request );
	zimbra_check_okay( err, exit );

	rc = xmlTextWriterStartElement( request, BAD_CAST "m" );
	zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	rc = xmlTextWriterWriteAttribute( request, BAD_CAST "id", BAD_CAST inv_id );
	zimbra_check( rc != -1, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	// Send message to server

	response = e_zimbra_connection_send_message( cnc, &request_buffer, &request );
	zimbra_check( response, exit, g_warning( "e_zimbra_connection_send_message returned NULL" ) );

	err = e_zimbra_connection_parse_response_status( response );

	if ( err != E_ZIMBRA_CONNECTION_STATUS_OK )
	{
		if ( err == E_ZIMBRA_CONNECTION_STATUS_INVALID_CONNECTION )
		{
			reauthenticate (cnc);
		}

		g_warning( "e_zimbra_connection_parse_response_status returned %d", err );
		goto exit;
	}

	node = xml_parse_path( xmlDocGetRootElement( response ), "Body/GetMsgResponse/m/mp" );
	zimbra_check( node, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

	for ( child = node->children; child; child = child->next )
	{
		if ( child->type == XML_ELEMENT_NODE )
		{
			char * type;

			if ( ( type = e_zimbra_xml_find_attribute( child, "ct" ) ) != NULL )
			{
				gboolean eq = g_str_equal( type, "text/plain" );

				g_free( type );

				if ( eq )
				{
					xmlNode * content;

					content = e_zimbra_xml_find_child_by_name( child, "content" );
					zimbra_check( content, exit, err = E_ZIMBRA_CONNECTION_STATUS_UNKNOWN );

					if ( content->children )
					{
						*message = g_strdup( ( const char* ) content->children->content );
					}

					break;
				}
			}
		}
	}

exit:

	if ( response )
	{
		xmlFreeDoc( response );
	}

	if ( request_buffer )
	{
		xmlBufferFree( request_buffer );
	}

	// Workaround bug in libxml2.  If there is an error while creating the xml document,
	// then calling xmlFreeTextWriter will crash the caller.

	if ( request && !err )
	{
		xmlFreeTextWriter( request );
	}

	return err;
}
