/* 
 * Copyright (C) 2006-2007 Zimbra, Inc.
 *
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
 */

#include <config.h>
#include <gconf/gconf-client.h>
#include <glib/gmain.h>
#include <libedataserver/e-source-list.h>

static GConfClient *conf_client;
static GMainLoop *main_loop;
static char * arg_account;
static char *arg_hostname, *arg_username;
static char * arg_port;
static gboolean arg_useSSL;

#define ZIMBRA_CALDAV_URI_PREFIX		"caldav://"
#define ZIMBRA_CALDAV_PREFIX_LENGTH		9
#define ZIMBRA_URI_PREFIX				"zimbra://"
#define ZIMBRA_PREFIX_LENGTH 			9
#define PARENT_TYPE						G_TYPE_OBJECT

#define CALENDAR_SOURCES "/apps/evolution/addressbook/sources"
#define SELECTED_CALENDARS "/apps/evolution/addressbook/display/selected_calendars"

static void 
remove_account( const char *conf_key, const char * account )
{
	ESourceList		*	list;
	ESourceGroup	*	group;
	ESource *source;
	GSList *groups;
	GSList *sources;
	GConfClient* client;
	GSList *ids;
	GSList *node_tobe_deleted;
	char *source_selection_key;
                                                                                                                             
	client = gconf_client_get_default();
	list = e_source_list_new_for_gconf (client, conf_key);
	groups = e_source_list_peek_groups (list); 
	
	for ( ; groups != NULL; groups = g_slist_next (groups))
	{
		group = E_SOURCE_GROUP (groups->data);

		char * name = e_source_group_peek_name( group );
fprintf( stderr, "group name = %s\n", name );
char * buri = e_source_group_peek_base_uri( group );
fprintf( stderr, "group base uri = %s\n", name );
		
		if ( strcmp( e_source_group_peek_name( group ), account ) == 0  )
		{
			sources = e_source_group_peek_sources (group);
			
			for ( ; sources != NULL; sources = g_slist_next (sources))
			{
				source = E_SOURCE (sources->data);

				e_source_list_remove_group (list, group);
				e_source_list_sync (list, NULL);	
			}
		}
	}

	g_object_unref (list);
	g_object_unref (client);		
}


static gboolean
idle_cb (gpointer data)
{
	fprintf( stderr, "deleting accounts = %s\n", arg_account );
	fprintf( stderr, "-----------------------------------------\n" );
	
	remove_account( "/apps/evolution/addressbook/sources",	arg_account );
	remove_account( "/apps/evolution/calendar/sources",		arg_account );

	g_main_loop_quit (main_loop);

	return FALSE;
}

int
main (int argc, char *argv[])
{
	gboolean bool;

	g_type_init ();

	if (argc != 2 )
	{
		g_print ("Usage: %s account\n", argv[0]);
		return -1;
	}

	arg_account = argv[1];

	conf_client = gconf_client_get_default ();

	main_loop = g_main_loop_new (NULL, TRUE);
	g_idle_add ((GSourceFunc) idle_cb, NULL);
	g_main_loop_run (main_loop);

	/* terminate */
	g_object_unref (conf_client);
	g_main_loop_unref (main_loop);

	return 0;
}
