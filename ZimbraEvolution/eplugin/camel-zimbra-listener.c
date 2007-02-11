/* -*- Mode: C; tab-width: 4; indent-tabs-mode: t; c-basic-offset: 4 -*- */
/*
 * Authors :
 *  
 * Scott Herscher <scott.herscher@zimbra.com>
 *
 * Copyright 2006, Zimbra, Inc.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of version 2 of the GNU General Public
 * License as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */

#ifdef HAVE_CONFIG_H
#include <config.h>
#endif

#include "camel-zimbra-listener.h"
#include <libezimbra/e-zimbra-folder.h>
#include <libezimbra/e-zimbra-debug.h>
#include <camel/camel-i18n.h>
#include <e-util/e-error.h>
#include <libedataserverui/e-passwords.h>
#include <libedataserver/e-account.h>
#include <e-util/e-config.h>
#include <e-util/e-plugin.h>
#include <shell/es-event.h>
#include <string.h>
#include <glog/glog.h>

static GList * zimbra_accounts = NULL;
static gboolean g_hack = 0;

struct _CamelZimbraListenerPrivate
{
	GConfClient *gconf_client;
	/* we get notification about mail account changes form this object */
	EAccountList *account_list;                  
};


struct _ZimbraAccountInfo
{
	char	*	uid;
	char	*	name;
	char	*	user;
	char	*	host;
	int			port;
	char	*	use_ssl;
	char	*	source_url;
};


typedef struct _ZimbraAccountInfo ZimbraAccountInfo;

#define ZIMBRA_CALDAV_URI_PREFIX		"caldav://"
#define ZIMBRA_CALDAV_PREFIX_LENGTH		9
#define ZIMBRA_URI_PREFIX				"zimbra://"
#define ZIMBRA_PREFIX_LENGTH 			9
#define PARENT_TYPE						G_TYPE_OBJECT

static GObjectClass *parent_class = NULL;

static void dispose (GObject *object);
static void finalize (GObject *object);


static void 
camel_zimbra_listener_class_init
	(
	CamelZimbraListenerClass *class
	)
{
	GObjectClass *object_class;
	
	parent_class =  g_type_class_ref (PARENT_TYPE);
	object_class = G_OBJECT_CLASS (class);
	
	/* virtual method override */
	object_class->dispose = dispose;
	object_class->finalize = finalize;
}


static void 
camel_zimbra_listener_init (CamelZimbraListener *config_listener,  CamelZimbraListenerClass *class)
{
	config_listener->priv = g_new0 (CamelZimbraListenerPrivate, 1);	
	glog_init();
}


static void 
dispose (GObject *object)
{
	CamelZimbraListener *config_listener = CAMEL_ZIMBRA_LISTENER (object);
	
	g_object_unref (config_listener->priv->gconf_client);
	g_object_unref (config_listener->priv->account_list);

	G_OBJECT_CLASS (parent_class)->dispose (object);
}

static void 
finalize (GObject *object)
{
	CamelZimbraListener *config_listener = CAMEL_ZIMBRA_LISTENER (object);
	GList *list;
	ZimbraAccountInfo *info;

	if (config_listener->priv) {
		g_free (config_listener->priv);
	}

	for ( list = g_list_first (zimbra_accounts); list ; list = g_list_next (list) ) {
	       
		info = (ZimbraAccountInfo *) (list->data);

		if (info) {
			
			g_free (info->uid);
			g_free (info->name);
			g_free (info->source_url);
			g_free (info);
		}
	}
	
	g_list_free (zimbra_accounts);
}

/*determines whehter the passed in account is zimbra or not by looking at source url */

static gboolean
is_zimbra_account
	(
	EAccount * account
	)
{
	if ( account->source->url != NULL )
	{
		return ( strncmp( account->source->url, ZIMBRA_URI_PREFIX, ZIMBRA_PREFIX_LENGTH ) == 0 );
	}
	else
	{
		return FALSE;
	}
}


/* looks up for an existing zimbra account info in the zimbra_accounts list based on uid */

static ZimbraAccountInfo* 
lookup_account_info (const char *key)
{
	GList *list;
        ZimbraAccountInfo *info ;
	int found = 0;
                                                                      
        if (!key)
                return NULL;

	info = NULL;

        for (list = g_list_first (zimbra_accounts);  list;  list = g_list_next (list)) {
                info = (ZimbraAccountInfo *) (list->data);
                found = (strcmp (info->uid, key) == 0);
		if (found)
			break;
	}
	if (found)
		return info;
	return NULL;
}


#define CONF_KEY_CAL					"/apps/evolution/calendar/sources"
#define CONF_KEY_TASKS					"/apps/evolution/tasks/sources"
#define CONF_KEY_CONTACTS				"/apps/evolution/addressbook/sources"
#define CONF_KEY_SELECTED_CAL_SOURCES	"/apps/evolution/calendar/display/selected_calendars"


static gboolean 
remove_esources
	(
	ZimbraAccountInfo	*	account, 
	ESourceList			*	source_list,
	gboolean				remove_group,
	EZimbraFolderType		type
	)
{
	ESourceGroup	*	group				= NULL;
	ESource			*	source				= NULL;
	GSList			*	groups				= NULL;
	GSList			*	sources				= NULL;
	GSList			*	sources_copy		= NULL;
	GSList			*	ids					= NULL;
	GSList			*	node_to_be_deleted	= NULL;
	gboolean			found_group			= FALSE;
	const char		*	source_uid			= NULL;
	GConfClient		*	client				= NULL;
	gboolean			ok					= TRUE;

	zimbra_check( source_list, exit, ok = FALSE );

	// Remove the ESource group

	client = gconf_client_get_default();
	zimbra_check( client, exit, ok = FALSE );

	groups = e_source_list_peek_groups( source_list );
	zimbra_check( groups, exit, ok = FALSE );
	found_group = FALSE;

	for ( ; groups != NULL && !found_group; groups = g_slist_next( groups ) )
	{
		group = E_SOURCE_GROUP( groups->data );

		if ( ( strcmp( e_source_group_peek_name( group ), account->name ) == 0 ) &&
		     ( strcmp( e_source_group_peek_base_uri( group ), ZIMBRA_URI_PREFIX ) == 0 ) )
		{
			sources = e_source_group_peek_sources( group );
			sources_copy = g_slist_copy( sources );

			for ( ; sources_copy != NULL; sources_copy = g_slist_next( sources_copy ) )
			{
				source		= E_SOURCE( sources_copy->data );
				source_uid	= e_source_peek_uid( source );

				GLOG_INFO( "found source %s", e_source_peek_name( source ) );

				// Remove from the selected folders

				if ( type == E_ZIMBRA_FOLDER_TYPE_CALENDAR )
				{
					ids = gconf_client_get_list( client, CONF_KEY_SELECTED_CAL_SOURCES , GCONF_VALUE_STRING, NULL );

					if ( ids )
					{
						node_to_be_deleted = g_slist_find_custom( ids, source_uid, ( GCompareFunc ) strcmp );

						if ( node_to_be_deleted )
						{
							GLOG_DEBUG( "source %s is selected", e_source_peek_name( source ) );
							g_free( node_to_be_deleted->data );
							ids = g_slist_delete_link( ids, node_to_be_deleted );
							gconf_client_set_list( client, CONF_KEY_SELECTED_CAL_SOURCES, GCONF_VALUE_STRING, ids, NULL );
						}

						g_slist_foreach( ids, ( GFunc ) g_free, NULL );
						g_slist_free( ids );
					}
				}

				GLOG_DEBUG( "removing source: %s", e_source_peek_name( source ) );
				e_source_group_remove_source( group, source );
			}

			if ( remove_group )
			{
				GLOG_DEBUG( "removing group: %s", e_source_group_peek_name( group ) );
				e_source_list_remove_group( source_list, group );
			}

			e_source_list_sync( source_list, NULL );
			found_group = TRUE;
		}
	}

exit:

	if ( client )
	{
		g_object_unref( client );
	}

	return ok;
}


/* looks for e-source with the same info as old_account_info and changes its values to the values passed in */


static gboolean
add_addressbook_sources
	(
	ZimbraAccountInfo	*	info,
	ESourceList			*	list
	)
{
	ESourceGroup	*	group		= NULL;
	ESource			*	source		= NULL;
	char			*	base_uri	= NULL;
	const char		*	port		= NULL;
	GConfClient		*	client		= NULL;
	const char		*	str			= NULL;
	char				encoded_user[ 256 ];
	char				source_name[ 256 ];
	gboolean			ok			= TRUE;

	client = gconf_client_get_default();
	zimbra_check( client, exit, ok = FALSE );

	group = e_source_group_new( info->name, ZIMBRA_URI_PREFIX );
	zimbra_check( group, exit, ok = FALSE );

	snprintf( source_name, sizeof( source_name ), "%s@%s:%d/7", e_zimbra_encode_url( info->user, encoded_user, sizeof( encoded_user ) , "@" ), info->host, info->port );

	source = e_source_new (			"Contacts",		source_name );
	zimbra_check( source, exit, ok = FALSE );

	e_source_set_property( source,	"account",		info->name );
	e_source_set_property( source,	"auth",			"plain/password");
	e_source_set_property( source,	"auth-domain",	"Zimbra");
	e_source_set_property( source,	"user",			info->user );
	e_source_set_property( source,	"binddn",		info->user );
	e_source_set_property( source,	"offline_sync",	"1");
	e_source_set_property( source,	"use_ssl",		info->use_ssl );
	e_source_set_property( source,	"id",			"7" );

	ok = e_source_group_add_source (group, source, -1);
	zimbra_check( ok, exit, ok = FALSE );

	ok = e_source_list_add_group (list, group, -1);
	zimbra_check( ok, exit, ok = FALSE );

	ok = e_source_list_sync (list, NULL);
	zimbra_check( ok, exit, ok = FALSE );

exit:

	if ( group )
	{
		g_object_unref (group);
	}

	if ( source )
	{
		g_object_unref (source);
	}

	if ( client )
	{
		g_object_unref (client);
	}

	return ok;
}


static gboolean 
remove_addressbook_sources
	(
	ZimbraAccountInfo	*	existing_account_info,
	ESourceList			*	list
	)
{
	char				encoded_user[ 256 ];
	char				command[ 256 ];
	gboolean			ret;

	ret = remove_esources( existing_account_info, list, TRUE, E_ZIMBRA_FOLDER_TYPE_CONTACTS );

	snprintf( command, sizeof( command ), "rm -fr '%s'/.evolution/cache/addressbook/*%s@%s_%d*", g_get_home_dir(), e_zimbra_encode_url( existing_account_info->user, encoded_user, sizeof( encoded_user ), "@" ), existing_account_info->host, existing_account_info->port );
	GLOG_DEBUG( "running command: %s", command );
	system( command );

	return ret;
}


/* add sources for calendar if the account added is ZIMBRA account
   adds the new account info to  ZIMBRA accounts list */

static gboolean 
add_calendar_sources
	(
	ZimbraAccountInfo 	*	info,
	ESourceList			*	list
	)
{
	ESourceGroup	*	group		= NULL;
	ESource			*	source		= NULL;
	char			*	base_uri	= NULL;
	char				encoded_user[ 256 ];
	char				source_name[ 256 ];
	GConfClient		*	client		= NULL;
	gboolean 			ret			= TRUE;

	client			= gconf_client_get_default();
	zimbra_check( client, exit, ret = FALSE );

	// group			= e_source_list_peek_group_by_name( list, info->name );

	if ( !group )
	{
		if ( g_hack )
		{
			char name[256];
			time_t t;
	
			sprintf( name, "%s:%d", info->name, t );
	
			t = time( NULL );
	
			group			= e_source_group_new( name, ZIMBRA_URI_PREFIX );
	
			g_hack = 0;	
		}
		else
		{
			group			= e_source_group_new( info->name, ZIMBRA_URI_PREFIX );
		}
	}

	zimbra_check( group, exit, ret = FALSE );

	snprintf( source_name, sizeof( source_name ), "%s@%s:%d/10", e_zimbra_encode_url( info->user, encoded_user, sizeof( encoded_user ), "@" ), info->host, info->port );

	source = e_source_new (				"Calendar",		source_name );
	zimbra_check( source, exit, ret = FALSE );
	
	e_source_set_property( source,		"account",		info->name );
	e_source_set_property( source,		"auth",			"plain/password");
	e_source_set_property( source,		"username",		info->user );
	e_source_set_property( source,		"binddn",		info->user );
	e_source_set_property( source,		"offline_sync",	"1");
	e_source_set_property( source,		"use_ssl",		info->use_ssl );
	e_source_set_property( source,		"id",			"10" );

	e_source_set_color(source, 0xfed4d3 );

	ret = e_source_group_add_source (group, source, -1);
	zimbra_check( ret, exit, ret = FALSE );

	ret = e_source_list_add_group (list, group, -1);
	zimbra_check( ret, exit, ret = FALSE );

	ret = e_source_list_sync (list, NULL);
	zimbra_check( ret, exit, ret = FALSE );

exit:

	if ( group )
	{
		g_object_unref( group );
	}

	if ( source )
	{
		g_object_unref( source );
	}

	if ( client )
	{
		g_object_unref( client );
	}

	return ret;
}


//
// Removes calendar  sources if the account removed is ZIMBRA account 
//

static gboolean
remove_calendar_sources
	(
	ZimbraAccountInfo	*	existing_account_info,
	ESourceList			*	list
	)
{
	char				encoded_user[ 256 ];
	char				command[ 256 ];
	gboolean			ret;

	ret = remove_esources( existing_account_info, list, TRUE, E_ZIMBRA_FOLDER_TYPE_CALENDAR );

	snprintf( command, sizeof( command ), "rm -fr '%s'/.evolution/cache/calendar/*%s@%s_%d*", g_get_home_dir(), e_zimbra_encode_url( existing_account_info->user, encoded_user, sizeof( encoded_user ), "@" ), existing_account_info->host, existing_account_info->port );
	GLOG_DEBUG( "running command: %s", command );
	system( command );

	return ret;
}


static void 
add_account
	(
	EAccountList	*	account_listener,
	EAccount		*	account,
	ESourceList		*	ebook_source_list,
	ESourceList		*	ecal_source_list
	)
{
	ZimbraAccountInfo	*	info;
	EAccount			*	parent;
	CamelURL			*	parent_url;
	CamelURL			*	url;
	gboolean				sync;

	if ( !is_zimbra_account( account ) )
	{				
		return;
	}

	info				= g_new0( ZimbraAccountInfo, 1 );
	info->uid			= g_strdup( account->uid );
	info->name			= g_strdup( account->name );
	info->source_url	= g_strdup( account->source->url );

	GLOG_DEBUG( "uid = %s", info->uid );
	GLOG_DEBUG( "name = %s", info->name );
	GLOG_DEBUG( "url = %s", info->source_url );

	if ( url = camel_url_new( account->source->url, NULL ) )
	{
		const char * str;

		info->host	= g_strdup( url->host );  
		info->user	= g_strdup( url->user );  

		GLOG_DEBUG( "host = %s", info->host );
		GLOG_DEBUG( "user = %s", info->user );

		str = camel_url_get_param( url, "soap_port" );

		if ( str && ( strlen( str ) ) )
		{
			info->port = atoi( str );
		}
		else
		{
			info->port = 80;
		}

		GLOG_DEBUG( "port = %d", info->port );

		if ( ( str = camel_url_get_param( url, "soap_is_secure" ) ) != NULL )
		{
			info->use_ssl = g_strdup( "always" );
		}
		else
		{
			info->use_ssl = g_strdup( "never" );
		}

		GLOG_DEBUG( "use_ssl = %s", info->use_ssl );
	}

	if ( account->parent_uid )
	{
		parent = (EAccount *)e_account_list_find (account_listener, E_ACCOUNT_FIND_UID, account->parent_uid);

		if (!parent) 
		{
			return;
		}

		parent_url = camel_url_new( e_account_get_string(parent, E_ACCOUNT_SOURCE_URL), NULL );	
	}
	else 
	{
		add_addressbook_sources( info, ebook_source_list );
		sync = e_source_list_sync( ebook_source_list, NULL );
		zimbra_assert( sync );

		add_calendar_sources( info, ecal_source_list );
		sync = e_source_list_sync( ecal_source_list, NULL );
		zimbra_assert( sync );
	}
	
	zimbra_accounts = g_list_append( zimbra_accounts, info );
}


static void 
remove_account
	(
	EAccountList	*	account_listener,
	EAccount		*	account,
	ESourceList		*	ebook_source_list,
	ESourceList		*	ecal_source_list
	)
{
	ZimbraAccountInfo	*	info	= NULL;
	char					encoded_user[ 256 ];
	char					command[ 256 ];
	gboolean				sync;
	gboolean				ok		= TRUE;
	
	zimbra_check_quiet( is_zimbra_account( account ), exit, ok = TRUE );

	info = lookup_account_info( account->uid );
	zimbra_check( info, exit, ok = TRUE );

	remove_addressbook_sources( info, ebook_source_list );
	sync = e_source_list_sync( ebook_source_list, NULL );
	zimbra_assert( sync );
	
	remove_calendar_sources( info, ecal_source_list );
	sync = e_source_list_sync( ecal_source_list, NULL );
	zimbra_assert( sync );

	zimbra_accounts = g_list_remove( zimbra_accounts, info );

	snprintf( command, sizeof( command ), "rm -fr '%s'/.evolution/cache/zimbra/*%s@%s_%d*", g_get_home_dir(), e_zimbra_encode_url( info->user, encoded_user, sizeof( encoded_user ), "@" ), info->host, info->port );
	GLOG_DEBUG( "running command: %s", command );
	system( command );

exit:

	if ( info )
	{
		g_free( info->uid );
		g_free( info->name );
		g_free( info->source_url );
		g_free( info->host );
		g_free( info->user );
		g_free( info );
	}
}



static void 
account_added
	(
	EAccountList	*	account_listener,
	EAccount		*	account
	)
{
	GConfClient	*	gconf_client		= NULL;
	ESourceList	*	ecal_source_list	= NULL;
	ESourceList	*	ebook_source_list	= NULL;
	gboolean		sync				= TRUE;
	gboolean		ok 					= TRUE;

	zimbra_check_quiet( is_zimbra_account( account ), exit, ok = TRUE );

	gconf_client = gconf_client_get_default();
	zimbra_check( gconf_client, exit, ok = FALSE );

	ebook_source_list = e_source_list_new_for_gconf( gconf_client, "/apps/evolution/addressbook/sources" );
	zimbra_check( ebook_source_list, exit, ok = FALSE );

	ecal_source_list = e_source_list_new_for_gconf( gconf_client, "/apps/evolution/calendar/sources" );
	zimbra_check( ecal_source_list, exit, ok = FALSE );

	add_account( account_listener, account, ebook_source_list, ecal_source_list );

exit:

	if ( ebook_source_list )
	{
		g_object_unref( ebook_source_list );
	}

	if ( ecal_source_list )
	{
		g_object_unref( ecal_source_list );
	}

	if ( gconf_client )
	{
		g_object_unref( gconf_client );
	}

	if ( !ok )
	{
	}
}


static void 
account_removed
	(
	EAccountList	*	account_listener,
	EAccount		*	account
	)
{
	GConfClient		*	gconf_client		= NULL;
	ESourceList		*	ebook_source_list	= NULL;
	ESourceList		*	ecal_source_list	= NULL;
	gboolean			ok = TRUE;
	
	zimbra_check_quiet( is_zimbra_account( account ), exit, ok = TRUE );

	gconf_client = gconf_client_get_default();
	zimbra_check( gconf_client, exit, ok = FALSE );

	ebook_source_list = e_source_list_new_for_gconf( gconf_client, "/apps/evolution/addressbook/sources" );
	zimbra_check( ebook_source_list, exit, ok = FALSE );

	ecal_source_list = e_source_list_new_for_gconf( gconf_client, "/apps/evolution/calendar/sources" );
	zimbra_check( ecal_source_list, exit, ok = FALSE );

	remove_account( account_listener, account, ebook_source_list, ecal_source_list );

exit:

	if ( ebook_source_list )
	{
		g_object_unref( ebook_source_list );
	}

	if ( ecal_source_list )
	{
		g_object_unref( ecal_source_list );
	}

	if ( gconf_client )
	{
		g_object_unref( gconf_client );
	}

	if ( !ok )
	{
	}
}


static void
account_changed
	(
	EAccountList	*	account_listener,
	EAccount		*	account
	)
{
	GConfClient			*	gconf_client		= NULL;
	ESourceList			*	ebook_source_list	= NULL;
	ESourceList			*	ecal_source_list	= NULL;
	ZimbraAccountInfo	*	existing_account_info;
	gboolean				is_zimbra;
	gboolean				sync;
	gboolean				ok = TRUE;
	
	gconf_client = gconf_client_get_default();
	zimbra_check( gconf_client, exit, ok = FALSE );
	
	ebook_source_list = e_source_list_new_for_gconf( gconf_client, "/apps/evolution/addressbook/sources" );
	zimbra_check( ebook_source_list, exit, ok = FALSE );

	ecal_source_list = e_source_list_new_for_gconf( gconf_client, "/apps/evolution/calendar/sources" );
	zimbra_check( ecal_source_list, exit, ok = FALSE );

	is_zimbra				= is_zimbra_account( account );
	existing_account_info	= lookup_account_info( account->uid );

	GLOG_INFO( "account has been changed" );
       
	// Check to see if some other account was changed to zimbra

	if ( is_zimbra && !existing_account_info )
	{
		if ( account->enabled )
		{
			GLOG_INFO( "account was changed to be of type zimbra" );
			add_account( account_listener, account, ebook_source_list, ecal_source_list );
		}
		else
		{
			GLOG_INFO( "account is disabled...ignoring" );
		}
	}

	// Check to see if zimbra account was changed to something else

	else if ( !is_zimbra && existing_account_info )
	{
		GLOG_INFO( "account was changed from type zimbra" );

		remove_account( account_listener, account, ebook_source_list, ecal_source_list );
	}

	// Check to see if some info on this account changed

	else if ( is_zimbra && existing_account_info )
	{
		if ( account->enabled )
		{
			GLOG_INFO( "account data was changed" );

			remove_account( account_listener, account, ebook_source_list, ecal_source_list );
			add_account( account_listener, account, ebook_source_list, ecal_source_list );
		}
		else
		{
			remove_account( account_listener, account, ebook_source_list, ecal_source_list );
		}
	}

exit:

	if ( ebook_source_list )
	{
		g_object_unref( ebook_source_list );
	}

	if ( ecal_source_list )
	{
		g_object_unref( ecal_source_list );
	}

	if ( gconf_client )
	{
		g_object_unref( gconf_client );
	}
}


static void
camel_zimbra_listener_construct (CamelZimbraListener *config_listener)
{
	EIterator *iter;
	EAccount *account;
	ZimbraAccountInfo *info ;
	
	config_listener->priv->account_list = e_account_list_new (config_listener->priv->gconf_client);

	for ( iter = e_list_get_iterator (E_LIST ( config_listener->priv->account_list) ) ; e_iterator_is_valid (iter); e_iterator_next (iter) )
	{
		account = E_ACCOUNT (e_iterator_get (iter));

		if ( is_zimbra_account (account) && account->enabled )
		{
			CamelURL * url;

			info				= g_new0 (ZimbraAccountInfo, 1);
			info->uid			= g_strdup (account->uid);
			info->name			= g_strdup (account->name);
			info->source_url	= g_strdup (account->source->url);
			zimbra_accounts		= g_list_append (zimbra_accounts, info);

			if ( url = camel_url_new( account->source->url, NULL ) )
			{
				const char * str;

				info->host	= g_strdup( url->host );  
				info->user	= g_strdup( url->user );
		
				if ( ( str = camel_url_get_param( url, "soap_is_secure" ) ) != NULL )
				{
					info->use_ssl = g_strdup( "always" );
				}
				else
				{
					info->use_ssl = g_strdup( "never" );
				}

				str = camel_url_get_param( url, "soap_port" );
		
				if ( str && ( strlen( str ) ) )
				{
					info->port = atoi( str );
				}
				else
				{
					info->port = 80;
				}
			
			}
		}
	}

	g_signal_connect( config_listener->priv->account_list,	"account_added",	G_CALLBACK( account_added ),	NULL );
	g_signal_connect( config_listener->priv->account_list,	"account_changed",	G_CALLBACK( account_changed ),	NULL );
	g_signal_connect( config_listener->priv->account_list,	"account_removed",	G_CALLBACK( account_removed ),	NULL );    
}

GType
camel_zimbra_listener_get_type (void)
{
	static GType camel_zimbra_listener_type  = 0;

	if (!camel_zimbra_listener_type) {
		static GTypeInfo info = {
                        sizeof (CamelZimbraListenerClass),
                        (GBaseInitFunc) NULL,
                        (GBaseFinalizeFunc) NULL,
                        (GClassInitFunc) camel_zimbra_listener_class_init,
                        NULL, NULL,
                        sizeof (CamelZimbraListener),
                        0,
                        (GInstanceInitFunc) camel_zimbra_listener_init
                };
		camel_zimbra_listener_type = g_type_register_static (PARENT_TYPE, "CamelZimbraListener", &info, 0);
	}

	return camel_zimbra_listener_type;
}

CamelZimbraListener*
camel_zimbra_listener_new ()
{
	CamelZimbraListener *config_listener;
       
	config_listener = g_object_new (CAMEL_TYPE_ZIMBRA_LISTENER, NULL);
	config_listener->priv->gconf_client = gconf_client_get_default();
	
	camel_zimbra_listener_construct (config_listener);

	return config_listener;
}

static gboolean
idle_sync_sources( gpointer data )
{
	return FALSE;
}

