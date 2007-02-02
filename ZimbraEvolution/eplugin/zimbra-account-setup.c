/* -*- Mode: C; tab-width: 4; indent-tabs-mode: t; c-basic-offset: 4 -*-
 *
 *  Copyright (C) 2006 Zimbra, Inc.
 *
 *  Author: Scott Herscher <scott.herscher@zimbra.com>
 *
 *  Permission is hereby granted, free of charge, to any person
 *  obtaining a copy of this software and associated documentation
 *  files (the "Software"), to deal in the Software without
 *  restriction, including without limitation the rights to use, copy,
 *  modify, merge, publish, distribute, sublicense, and/or sell copies
 *  of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 *  
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 *  DEALINGS IN THE SOFTWARE.
 */


#include "camel-zimbra-listener.h"
#include <gtk/gtk.h>
#include "mail/em-config.h"
#include <calendar/gui/e-cal-config.h>
#include <addressbook/gui/widgets/eab-config.h>
#include <libedataserver/e-url.h>
#include <glog/glog.h>

static CamelZimbraListener *config_listener = NULL;

int
e_plugin_lib_enable
	(
	EPluginLib *ep,
	int enable
	);


GtkWidget*
com_zimbra_account_setup
	(
	struct _EPlugin						*	epl,
	struct _EConfigHookItemFactoryData	*	data
	);


static void 
free_zimbra_listener()
{
	g_object_unref (config_listener);
}


int
e_plugin_lib_enable
	(
	EPluginLib	*	ep,
	int				enable
	)
{
	if (!config_listener)
	{
		config_listener = camel_zimbra_listener_new ();	
	 	g_atexit ( free_zimbra_listener );

		glog_init();
	}

	return 0;
}


GtkWidget*
com_zimbra_account_setup
	(
	struct _EPlugin						*	epl,
	struct _EConfigHookItemFactoryData	*	data
	)
{
	if (data->old)
	{
		return data->old;
	}

	return NULL;
}


GtkWidget *
com_zimbra_new_calendar_setup
	(
	EPlugin						*	epl,
	EConfigHookItemFactoryData	*	data
	)
{
	ECalConfigTargetSource	*	t				= NULL;
	ESource					*	source			= NULL;
	ESource					*	calendarSource	= NULL;
	ESourceGroup			*	group			= NULL;
	EUri					*	parsed_uri		= NULL;
	char					*	absolute_uri	= NULL;
	char					*	relative_uri	= NULL;
	char						encoded_user[ 256 ];
	const char				*	base_uri		= NULL;

 	t				= (ECalConfigTargetSource *) data->target;
	source			= t->source;
	group			= e_source_peek_group( source );
	base_uri		= e_source_group_peek_base_uri( group );

	GLOG_DEBUG( "%s", base_uri );

	if ( ( g_str_equal( base_uri, "zimbra://" ) ) && ( e_source_get_property( source, "account" ) == NULL ) && ( calendarSource = e_source_group_peek_source_by_name( group, "Calendar" ) ) )
	{
		const char	*	val;
		struct timeval	tv;

		val = e_source_get_property( calendarSource, "account" );
		e_source_set_property( source, "account", val );
	
		val = e_source_get_property( calendarSource, "auth" );
		e_source_set_property( source, "auth", val );
		
		val = e_source_get_property( calendarSource, "username" );
		e_source_set_property( source, "username", val );
	
		val = e_source_get_property( calendarSource, "binddn" );
		e_source_set_property( source, "binddn", val );
		
		val = e_source_get_property( calendarSource, "use_ssl" );
		e_source_set_property( source, "use_ssl", val );
		
		val = e_source_get_property( calendarSource, "offline_sync" );
		e_source_set_property( source, "offline_sync", val );

    	if ( parsed_uri = e_uri_new( e_source_get_uri( calendarSource ) ) )
		{
			gettimeofday( &tv, NULL );

			absolute_uri = g_strdup_printf( "zimbra://%s@%s:%d/%d/%d", e_zimbra_encode_url( parsed_uri->user, encoded_user, sizeof( encoded_user ) ), parsed_uri->host, parsed_uri->port, tv.tv_sec, tv.tv_usec );
			e_source_set_absolute_uri( source, absolute_uri );

			relative_uri = g_strdup_printf( "%s@%s:%d/%d/%d", e_zimbra_encode_url( parsed_uri->user, encoded_user, sizeof( encoded_user ) ), parsed_uri->host, parsed_uri->port, tv.tv_sec, tv.tv_usec );
			e_source_set_relative_uri( source, relative_uri );
		}
	}

	if ( relative_uri )
	{
		g_free( relative_uri );
	}

	if ( absolute_uri )
	{
		g_free( absolute_uri );
	}

	if ( parsed_uri )
	{
		e_uri_free( parsed_uri );
	}

	return NULL;
}


GtkWidget *
com_zimbra_new_addressbook_setup
	(
	EPlugin						*	epl,
	EConfigHookItemFactoryData	*	data
	)
{
	EABConfigTargetSource	*	t				=	NULL;
	ESource					*	source			=	NULL;
	ESource					*	contactSource	=	NULL;
	ESourceGroup			*	group			=	NULL;
	EUri					*	parsed_uri		=	NULL;
	char					*	absolute_uri	=	NULL;
	char					*	relative_uri	=	NULL;
	char						encoded_user[ 256 ];
	const char				*	base_uri		=	NULL;


 	t				= ( EABConfigTargetSource* ) data->target;
	source			= t->source;
	group			= e_source_peek_group( source );
	base_uri		= e_source_group_peek_base_uri( group );

	GLOG_DEBUG( "%s", base_uri );

	if ( ( g_str_equal( base_uri, "zimbra://" ) ) && ( e_source_get_property( source, "account" ) == NULL ) && ( contactSource = e_source_group_peek_source_by_name( group, "Contacts" ) ) )
	{
		const char	*	val;
		struct timeval	tv;

		val = e_source_get_property( contactSource, "account" );
		e_source_set_property( source, "account", val );
	
		val = e_source_get_property( contactSource, "auth" );
		e_source_set_property( source, "auth", val );
	
		val = e_source_get_property( contactSource, "auth-domain" );
		e_source_set_property( source, "auth-domain", val );
	
		val = e_source_get_property( contactSource, "user" );
		e_source_set_property( source, "user", val );
	
		val = e_source_get_property( contactSource, "binddn" );
		e_source_set_property( source, "binddn", val );
		
		val = e_source_get_property( contactSource, "offline_sync" );
		e_source_set_property( source, "offline_sync", val );

		val = e_source_get_property( contactSource, "use_ssl" );
		e_source_set_property( source, "use_ssl", val );
		
    	if ( parsed_uri = e_uri_new( e_source_get_uri( contactSource ) ) )
		{
			gettimeofday( &tv, NULL );
	
			absolute_uri = g_strdup_printf( "zimbra://%s@%s:%d/%d/%d", e_zimbra_encode_url( parsed_uri->user, encoded_user, sizeof( encoded_user ) ), parsed_uri->host, parsed_uri->port, tv.tv_sec, tv.tv_usec );
			e_source_set_absolute_uri( source, absolute_uri );

			relative_uri = g_strdup_printf( "%s@%s:%d/%d/%d", e_zimbra_encode_url( parsed_uri->user, encoded_user, sizeof( encoded_user ) ), parsed_uri->host, parsed_uri->port, tv.tv_sec, tv.tv_usec );
			e_source_set_relative_uri( source, relative_uri );
		}
	}

	if ( relative_uri )
	{
		g_free( relative_uri );
	}

	if ( absolute_uri )
	{
		g_free( absolute_uri );
	}

	if ( parsed_uri )
	{
		e_uri_free( parsed_uri );
	}

	return NULL;
}
