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

static gboolean
is_zimbra_caldav_account (EAccount *account)
{
        if (account->source->url != NULL) {
                return (strncmp (account->source->url,  ZIMBRA_CALDAV_URI_PREFIX, ZIMBRA_CALDAV_PREFIX_LENGTH ) == 0);
        } else {
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

#define CALENDAR_SOURCES "/apps/evolution/calendar/sources"
#define SELECTED_CALENDARS "/apps/evolution/calendar/display/selected_calendars"


/* looks for e-source with the same info as old_account_info and changes its values to the values passed in */


static gboolean
add_addressbook_sources
	(
	ZimbraAccountInfo *	info
	)
{
	ESourceList *list = NULL;
	ESourceGroup *group = NULL;
	ESource *source = NULL;
	char *base_uri = NULL;
	char * encoded_user = NULL;
	const char * port;
	GList *books_list, *temp_list;
	GConfClient* client;
	const char* str;
	gboolean is_frequent_contacts = FALSE, is_writable = FALSE;
	char group_name[ 256 ];
	gboolean ret = TRUE;

	client			= gconf_client_get_default();
	list			= e_source_list_new_for_gconf( client, "/apps/evolution/addressbook/sources" );
	group			= e_source_group_new( info->name, ZIMBRA_URI_PREFIX );
	encoded_user	= camel_url_encode( info->user, "@" );
	sprintf( group_name, "%s@%s:%d/7", encoded_user, info->host, info->port );
	source = e_source_new ( "Contacts", group_name );
	e_source_set_property( source, "account", info->name );
	e_source_set_property( source, "auth", "plain/password");
	e_source_set_property( source, "auth-domain", "Zimbra");
	e_source_set_property( source, "user", info->user );
	e_source_set_property( source, "binddn", info->user );
	e_source_set_property( source, "offline_sync", "1");
	e_source_set_property( source, "use_ssl", info->use_ssl );
	e_source_set_property( source, "id", "7" );
	e_source_group_add_source (group, source, -1);
	e_source_list_add_group (list, group, -1);
	e_source_list_sync (list, NULL);

exit:

	if ( encoded_user )
	{
		g_free( encoded_user );
	}

	if ( group )
	{
		g_object_unref (group);
	}

	if ( source )
	{
		g_object_unref (source);
	}

	if ( list )
	{
		g_object_unref (list);
	}

	if ( client )
	{
		g_object_unref (client);
	}

	return ret;
}


static void 
remove_addressbook_sources
	(
	ZimbraAccountInfo	*	existing_account_info
	)
{
	GConfClient		*	client			=	NULL;
	char				command[ 256 ];
	char			*	encoded_user	=	NULL;
	ESourceList		*	list			=	NULL;
	GSList			*	groups			=	NULL;
	ESourceGroup	*	group			=	NULL;
	int					err;

	GLOG_DEBUG( "enter" );

	client		= gconf_client_get_default();
	zimbra_check( client, exit, err = 0 );

	list		= e_source_list_new_for_gconf( client, "/apps/evolution/addressbook/sources" );
	zimbra_check( list, exit, err = 0 );

	groups		= e_source_list_peek_groups( list ); 
	zimbra_check( groups, exit, err = 0 );

	for ( ; groups; groups = g_slist_next( groups ) )
	{
		group = E_SOURCE_GROUP (groups->data);

		if ( ( strcmp( e_source_group_peek_base_uri( group ), ZIMBRA_URI_PREFIX ) == 0 ) && 
		     ( strcmp( e_source_group_peek_name( group ), existing_account_info->name ) == 0 ) )
		{
			e_source_list_remove_group( list, group );
			e_source_list_sync( list, NULL );
			break;
		}
	}

	encoded_user = camel_url_encode( existing_account_info->user, "@" );
	zimbra_check( encoded_user, exit, err = 0 );
	snprintf( command, sizeof( command ), "rm -fr '%s'/.evolution/cache/addressbook/*%s@%s_%d*", g_get_home_dir(), encoded_user, existing_account_info->host, existing_account_info->port );
	GLOG_DEBUG( "running command: %s", command );
	system( command );

exit:

	if ( encoded_user )
	{
		g_free( encoded_user );
	}

	if ( list )
	{
		g_object_unref( list );
	}

	if ( client )
	{
		g_object_unref( client );
	}
}


/* add sources for calendar if the account added is ZIMBRA account
   adds the new account info to  ZIMBRA accounts list */

static gboolean 
add_calendar_sources
	(
	ZimbraAccountInfo * info
	)
{
	ESourceList *list = NULL;
	ESourceGroup *group = NULL;
	ESource *source = NULL;
	char *base_uri = NULL;
	char * encoded_user = NULL;
	const char * port;
	GList *books_list, *temp_list;
	GConfClient* client;
	const char* use_ssl;
	const char* str;
	gboolean is_frequent_contacts = FALSE, is_writable = FALSE;
	char group_name[ 256 ];
	gboolean ret = TRUE;

	client			= gconf_client_get_default();
	list			= e_source_list_new_for_gconf( client, "/apps/evolution/calendar/sources" );
	group			= e_source_group_new( info->name, ZIMBRA_URI_PREFIX );
	encoded_user	= camel_url_encode( info->user, "@" );
	sprintf( group_name, "%s@%s:%d/10", encoded_user, info->host, info->port );
	source = e_source_new ( "Calendar", group_name );
	e_source_set_property( source, "account", info->name );
	e_source_set_property( source, "auth", "plain/password");
	e_source_set_property( source, "username", info->user );
	e_source_set_property( source, "binddn", info->user );
	e_source_set_property( source, "offline_sync", "1");
	e_source_set_property( source, "use_ssl", info->use_ssl );
	e_source_set_property( source, "id", "10" );
	e_source_set_color( source, 0xfed4d3 );
	e_source_group_add_source (group, source, -1);
	e_source_list_add_group (list, group, -1);
	e_source_list_sync (list, NULL);

exit:

	if ( group )
	{
		g_object_unref (group);
	}

	if ( source )
	{
		g_object_unref (source);
	}

	if ( list )
	{
		g_object_unref (list);
	}

	if ( client )
	{
		g_object_unref (client);
	}

	if ( encoded_user )
	{
		g_free( encoded_user );
	}

	return ret;
}

/* removes calendar  sources if the account removed is ZIMBRA account 
   removes the the account info from ZIMBRA_account list */

static void 
remove_calendar_sources
	(
	ZimbraAccountInfo	*	existing_account_info
	)
{
	GConfClient		*	client	=	NULL;
	char				command[ 256 ];
	char			*	encoded_user	= NULL;
	ESourceList		*	list	=	NULL;
	GSList			*	groups	=	NULL;
	ESourceGroup	*	group	=	NULL;
	int					err;

	client		= gconf_client_get_default();
	zimbra_check( client, exit, err = 0 );

	list		= e_source_list_new_for_gconf( client, "/apps/evolution/calendar/sources" );
	zimbra_check( list, exit, err = 0 );

	groups		= e_source_list_peek_groups( list ); 
	zimbra_check( groups, exit, err = 0 );

	for ( ; groups; groups = g_slist_next( groups ) )
	{
		group = E_SOURCE_GROUP (groups->data);

		if ( ( strcmp( e_source_group_peek_base_uri( group ), ZIMBRA_URI_PREFIX ) == 0 ) && 
		     ( strcmp( e_source_group_peek_name( group ), existing_account_info->name ) == 0 ) )
		{
			GSList * sources;
			GSList * sources_copy;

			sources			= e_source_group_peek_sources( group );
			sources_copy	= g_slist_copy( sources );

			for (; sources_copy; sources_copy = g_slist_next( sources_copy ) )
			{
				e_source_group_remove_source( group, E_SOURCE( sources_copy->data ) );
			}

			g_slist_free( sources_copy );
				
			e_source_list_remove_group( list, group );

			break;
		}
	}

	encoded_user = camel_url_encode( existing_account_info->user, "@" );
	zimbra_check( encoded_user, exit, err = 0 );
	snprintf( command, sizeof( command ), "rm -fr '%s'/.evolution/cache/calendar/*%s@%s_%d*", g_get_home_dir(), encoded_user, existing_account_info->host, existing_account_info->port );
	GLOG_DEBUG( "running command: %s", command );
	system( command );

	if ( list )
	{
		e_source_list_sync( list, NULL );
	}

exit:

	if ( encoded_user )
	{
		g_free( encoded_user );
	}

	if ( list )
	{
		g_object_unref( list );
	}

	if ( client )
	{
		g_object_unref( client );
	}
}


static void 
account_added
	(
	EAccountList	*	account_listener,
	EAccount		*	account
	)
{
	ZimbraAccountInfo	*	info;
	EAccount			*	parent;
	CamelURL			*	parent_url;
	CamelURL			*	url;

	if ( !is_zimbra_account( account ) )
	{				
		return;
	}

	info				= g_new0( ZimbraAccountInfo, 1 );
	info->uid			= g_strdup( account->uid );
	GLOG_DEBUG( "uid = %s", info->uid );
	info->name			= g_strdup( account->name );
	GLOG_DEBUG( "name = %s", info->name );
	info->source_url	= g_strdup( account->source->url );
	GLOG_DEBUG( "url = %s", info->source_url );

	if ( url = camel_url_new( account->source->url, NULL ) )
	{
		const char * str;

		info->host	= g_strdup( url->host );  
		GLOG_DEBUG( "host = %s", info->host );
		info->user	= g_strdup( url->user );  
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
	
		info->use_ssl = g_strdup( camel_url_get_param( url, "use_ssl" ) );
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
		add_addressbook_sources( info );
		add_calendar_sources( info );
	}
	
	zimbra_accounts = g_list_append( zimbra_accounts, info );
}


static void 
account_removed
	(
	EAccountList	*	account_listener,
	EAccount		*	account
	)
{
	ZimbraAccountInfo	*	info;
	char				*	encoded_user	= NULL;
	char					command[ 256 ];
	int						err;
	
	zimbra_check_quiet( is_zimbra_account( account ), exit, err = 0 );

	info = lookup_account_info( account->uid );
	zimbra_check( info, exit, err = 0 );

	remove_addressbook_sources( info );
	remove_calendar_sources( info );

	zimbra_accounts = g_list_remove( zimbra_accounts, info );

	encoded_user = camel_url_encode( info->user, "@" );
	zimbra_check( encoded_user, exit, err = 0 );
	snprintf( command, sizeof( command ), "rm -fr '%s'/.evolution/cache/zimbra/*%s@%s_%d*", g_get_home_dir(), encoded_user, info->host, info->port );
	system( command );

	g_free( encoded_user );
	g_free( info->uid );
	g_free( info->name );
	g_free( info->source_url );
	g_free( info->host );
	g_free( info->user );
	g_free( info );

exit:

	return;
}


static void
account_changed (EAccountList *account_listener, EAccount *account)
{
	GLOG_DEBUG( "enter" );
#if 0
	gboolean is_zimbra;
	CamelURL *old_url, *new_url;
	const char *old_caldav_port, *new_caldav_port;
	ZimbraAccountInfo *existing_account_info;
	const char *old_use_ssl, *new_use_ssl;
	const char *old_address, *new_address;
	
	is_zimbra = is_zimbra_account (account);
	if (is_zimbra == FALSE)
		is_zimbra = is_zimbra_caldav_account (account);
	
	existing_account_info = lookup_account_info (account->uid);
       
	if (existing_account_info == NULL && is_zimbra) {

		if (!account->enabled)
			return;

		/* some account of other type is changed to zimbra */
		account_added (account_listener, account);

	} else if (existing_account_info != NULL && !is_zimbra) {

		/* zimbra account is changed to some other type */
		remove_calendar_sources (existing_account_info);
		zimbra_accounts = g_list_remove (zimbra_accounts, existing_account_info);
		g_free (existing_account_info->uid);
		g_free (existing_account_info->name);
		g_free (existing_account_info->source_url);
		g_free (existing_account_info);
		
	} else if ( existing_account_info != NULL && is_zimbra ) {
		
		if (!account->enabled) {
			account_removed (account_listener, account);
			return;
		}
		
		/* some info of zimbra account is changed. update the sources with new info if required */
		old_url = camel_url_new (existing_account_info->source_url, NULL);
		old_address = old_url->host; 
		old_caldav_port = camel_url_get_param (old_url, "caldav_port");
		old_use_ssl = camel_url_get_param (old_url, "use_ssl");
		new_url = camel_url_new (account->source->url, NULL);
		new_address = new_url->host; 

		if (!new_address || strlen (new_address) ==0)
			return;

		new_caldav_port = camel_url_get_param (new_url, "caldav_port");

		if (!new_caldav_port || strlen (new_caldav_port) == 0)
			new_caldav_port = "8081";

		new_use_ssl = camel_url_get_param (new_url, "use_ssl");

		if ((old_address && strcmp (old_address, new_address))
		   ||  (old_caldav_port && strcmp (old_caldav_port, new_caldav_port)) 
		   ||  strcmp (old_url->user, new_url->user) 
		   || strcmp (old_use_ssl, new_use_ssl)) {
			
			account_removed (account_listener, account);
			account_added (account_listener, account);
		} else if (strcmp (existing_account_info->name, account->name)) {
			
			modify_esource ("/apps/evolution/calendar/sources", existing_account_info, account->name, new_url);
			
		}
		
		g_free (existing_account_info->name);
		g_free (existing_account_info->source_url);
		existing_account_info->name = g_strdup (account->name);
		existing_account_info->source_url = g_strdup (account->source->url);
		camel_url_free (old_url);
		camel_url_free (new_url);
	}	
#endif
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
		
				str = camel_url_get_param( url, "soap_port" );
		
				if ( str && ( strlen( str ) ) )
				{
					info->port = atoi( str );
				}
				else
				{
					info->port = 80;
				}
			
				info->use_ssl = g_strdup( camel_url_get_param( url, "use_ssl" ) );
			}
		}
	}

	g_signal_connect (config_listener->priv->account_list, "account_added", G_CALLBACK (account_added), NULL);
	g_signal_connect (config_listener->priv->account_list, "account_changed", G_CALLBACK (account_changed), NULL);
	g_signal_connect (config_listener->priv->account_list, "account_removed", G_CALLBACK (account_removed), NULL);    
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

