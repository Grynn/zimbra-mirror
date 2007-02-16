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
static char *arg_account , *arg_hostname, *arg_username;
static char * arg_port;
static gboolean arg_useSSL;

static void
add_account (const char *conf_key, const char * account, const char * hostname, const char *username, const char * port, gboolean useSSL )
{
	ESourceList *source_list;
	ESourceGroup *group;
	ESource *source;
	char * uri;

	source_list = e_source_list_new_for_gconf (conf_client, conf_key);
	group = e_source_group_new (account, "zimbra://");
	e_source_list_add_group (source_list, group, -1);

	uri = g_strdup_printf ("%s@%s:%s", username, hostname, port );

	source = e_source_new ("Calendar", uri );
	e_source_set_property( source, "account", account );
	e_source_set_property( source, "username", username );
	e_source_set_property (source, "auth", "1");
	e_source_set_property( source, "use_ssl", useSSL ? "always" : "never" );
	e_source_set_property( source, "zid", "10" );
	e_source_group_add_source (group, source, -1);

	e_source_list_sync (source_list, NULL);

	g_free ( uri);
	g_object_unref (source);
	g_object_unref (group);
	g_object_unref (source_list);
}

static gboolean
idle_cb (gpointer data)
{
	add_account ("/apps/evolution/calendar/sources", arg_account , arg_hostname, arg_username, arg_port, arg_useSSL);

	g_main_loop_quit (main_loop);

	return FALSE;
}

int
main (int argc, char *argv[])
{
	gboolean bool;

	g_type_init ();

	if (argc != 6 ) {
		g_print ("Usage: %s account hostname username port use_ssl\n", argv[0]);
		return -1;
	}

	arg_account  = argv[1];
	arg_hostname = argv[2];
	arg_username = argv[3];
	arg_port = argv[4];
	bool = ( gboolean ) atoi( argv[5] );
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
