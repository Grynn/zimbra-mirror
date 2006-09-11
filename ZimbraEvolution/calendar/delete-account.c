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

#define CALENDAR_SOURCES "/apps/evolution/calendar/sources"
#define SELECTED_CALENDARS "/apps/evolution/calendar/display/selected_calendars"

static void 
remove_esource (const char *conf_key, const char *group_name, char* source_name, const char* relative_uri)
{
	ESourceList *list;
        ESourceGroup *group;
        ESource *source;
        GSList *groups;
        GSList *sources;
	gboolean found_group;
	GConfClient* client;
	GSList *ids;
	GSList *node_tobe_deleted;
	char *source_selection_key;
                                                                                                                             
        client = gconf_client_get_default();
        list = e_source_list_new_for_gconf (client, conf_key);
	groups = e_source_list_peek_groups (list); 
	
	found_group = FALSE;

fprintf( stderr, "group_name = %s, relative_uri = %s\n", group_name, relative_uri );
	
	for ( ; groups != NULL && !found_group; groups = g_slist_next (groups)) {

		group = E_SOURCE_GROUP (groups->data);

char * name = e_source_group_peek_name( group );
fprintf( stderr, "group name = %s\n", name );
char * buri = e_source_group_peek_base_uri( group );
fprintf( stderr, "group base uri = %s\n", name );
		
//		if (strcmp (e_source_group_peek_name (group), group_name) == 0 && 
//		   strcmp (e_source_group_peek_base_uri (group), ZIMBRA_CALDAV_URI_PREFIX ) == 0) {
		if (strcmp (e_source_group_peek_name (group), group_name) == 0  )
		{

			sources = e_source_group_peek_sources (group);
			
			for( ; sources != NULL; sources = g_slist_next (sources)) {
				
				source = E_SOURCE (sources->data);

char * temp = e_source_peek_relative_uri( source );

fprintf( stderr, "uri: %s\n", temp );
				
				if (strcmp (e_source_peek_relative_uri (source), relative_uri) == 0) {
				
					if (!strcmp (conf_key, CALENDAR_SOURCES)) 
						source_selection_key = SELECTED_CALENDARS;
					else source_selection_key = NULL;
					if (source_selection_key) {
						ids = gconf_client_get_list (client, source_selection_key , 
									     GCONF_VALUE_STRING, NULL);
						node_tobe_deleted = g_slist_find_custom (ids, e_source_peek_uid (source), (GCompareFunc) strcmp);
						if (node_tobe_deleted) {
							g_free (node_tobe_deleted->data);
							ids = g_slist_delete_link (ids, node_tobe_deleted);
						}
						gconf_client_set_list (client,  source_selection_key, 
								       GCONF_VALUE_STRING, ids, NULL);

					}
					e_source_list_remove_group (list, group);
					e_source_list_sync (list, NULL);	
					found_group = TRUE;
					break;
					
				}
			}
		}
	}

	g_object_unref (list);
	g_object_unref (client);		
}


static void 
remove_account(const char *conf_key, const char * account, const char *hostname, const char *username, const char * port, gboolean useSSL )
{
	char * group_name;
	char * relative_uri;

fprintf( stderr, "in remove_account\n" );

	 group_name = g_strdup_printf ("%s@%s", username, hostname );
	 relative_uri = g_strdup_printf ("%s@%s:%s", username, hostname, port );
	remove_esource ("/apps/evolution/calendar/sources", account, "Calendar", relative_uri);
	g_free ( group_name );
}


static gboolean
idle_cb (gpointer data)
{
	remove_account ("/apps/evolution/calendar/sources", arg_account, arg_hostname, arg_username, arg_port, arg_useSSL);

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

	arg_account = argv[1];
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
