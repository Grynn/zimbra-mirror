/*
 * Copyright (C) 2006-2007 Zimbra, Inc.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of version 2 of the GNU Lesser General Public
 * License as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */

#include <config.h>
#include <gconf/gconf-client.h>
#include <glib/gmain.h>
#include <libedataserver/e-source-list.h>

static GConfClient *conf_client;
static GMainLoop *main_loop;
static char *arg_hostname, *arg_username;
static char * arg_port;
static gboolean arg_useSSL;

static void
add_account (const char *conf_key, const char *hostname, const char *username, const char * port, gboolean useSSL)
{
	ESourceList *source_list;
	ESourceGroup *group;
	ESource *source;
	char *group_name;
    char *group_title;

	source_list = e_source_list_new_for_gconf (conf_client, conf_key);

	group_title = g_strdup_printf( "%s@%s", username, hostname );
	group_name = g_strdup_printf ("%s@%s:%s", username, hostname, port );
	group = e_source_group_new (group_title, "zimbra://");
	e_source_list_add_group (source_list, group, -1);

	source = e_source_new( "Contacts", group_name );
	e_source_set_property( source, "auth", "ldap/simple-binddn" );
	e_source_set_property( source, "binddn", username );

	if ( useSSL )
	{
		e_source_set_property( source, "use_ssl", "yes" );
	}

	e_source_group_add_source (group, source, -1);

	e_source_list_sync (source_list, NULL);

	g_free (group_name);
	g_object_unref (source);
	g_object_unref (group);
	g_object_unref (source_list);
}

static gboolean
idle_cb (gpointer data)
{
	add_account ("/apps/evolution/addressbook/sources", arg_hostname, arg_username, arg_port, arg_useSSL );

	g_main_loop_quit (main_loop);

	return FALSE;
}

int
main (int argc, char *argv[])
{
	gboolean bool;

	g_type_init ();

	if (argc != 5 ) {
		g_print ("Usage: %s hostname username port use_ssl\n", argv[0]);
		return -1;
	}

	arg_hostname = argv[1];
	arg_username = argv[2];
	arg_port = argv[3];
	bool = ( gboolean ) atoi( argv[4] );
	arg_useSSL = bool;

	conf_client = gconf_client_get_default ();

	main_loop = g_main_loop_new (NULL, TRUE);
	g_idle_add ((GSourceFunc) idle_cb, NULL);
	g_main_loop_run (main_loop);

	/* terminate */
	g_object_unref (conf_client);
	g_main_loop_unref (main_loop);

	return 0;
}
