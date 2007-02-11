/* -*- Mode: C; tab-width: 4; indent-tabs-mode: t; c-basic-offset: 4 -*- */
/*
 * Author: Scott Herscher (scott.herscher@zimbra.com)
 *
 * Copyright 2006 Zimbra, Inc. (www.zimbra.com)
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

#ifdef HAVE_CONFIG_H
#include <config.h>
#endif

#include <string.h>
#include <camel/camel-provider.h>
#include <camel/camel-session.h>
#include <camel/camel-url.h>
#include <camel/camel-sasl.h>
#include <camel/camel-i18n.h>

static void add_hash (guint *hash, char *s);
static guint zimbra_url_hash (gconstpointer key);
static gint check_equal (char *s1, char *s2);
static gint zimbra_url_equal (gconstpointer a, gconstpointer b);


static CamelProviderConfEntry zimbra_conf_entries[] =
{
	/* override the labels/defaults of the standard settings */
	{ CAMEL_PROVIDER_CONF_SECTION_START,	"mailcheck", NULL, N_("Checking for New Mail") },
	{ CAMEL_PROVIDER_CONF_CHECKBOX,			"check_all", NULL, N_("C_heck for new messages in all folders"), "1" },
	{ CAMEL_PROVIDER_CONF_SECTION_END },
	{ CAMEL_PROVIDER_CONF_SECTION_START,	"cmdsection", NULL, N_("Connection to Server") },
	{ CAMEL_PROVIDER_CONF_CHECKBOX,			"use_command", NULL, N_("_Use custom command to connect to server"), "0" },
	{ CAMEL_PROVIDER_CONF_ENTRY,			"command", "use_command", N_("Command:"), "ssh -C -l %u %h exec /usr/sbin/imapd" },
	{ CAMEL_PROVIDER_CONF_SECTION_END },
	{ CAMEL_PROVIDER_CONF_SECTION_START,	"folders", NULL, N_("Folders") },
	{ CAMEL_PROVIDER_CONF_CHECKBOX,			"use_lsub", NULL, N_("_Show only subscribed folders"), "1" },
	{ CAMEL_PROVIDER_CONF_CHECKBOX,			"override_namespace", NULL, N_("O_verride server-supplied folder namespace"), "0" },
	{ CAMEL_PROVIDER_CONF_ENTRY,			"namespace", "override_namespace", N_("Namespace") },
	{ CAMEL_PROVIDER_CONF_SECTION_END },

	/* Extra Zimbra configuration settings */
	{ CAMEL_PROVIDER_CONF_SECTION_START,	"soapport",			NULL, N_("SOAP Settings") },
	{ CAMEL_PROVIDER_CONF_CHECKBOX,			"soap_is_secure",	NULL, N_("Use Secure Connection"), "0" },
	{ CAMEL_PROVIDER_CONF_ENTRY,			"soap_port",		NULL, N_("Port:"), "80" },
	{ CAMEL_PROVIDER_CONF_SECTION_END },

	{ CAMEL_PROVIDER_CONF_END }
};


static CamelProvider zimbra_provider =
{
	"zimbra",
	N_("Zimbra Collaboration Suite"),
	N_("For accessing Zimbra servers"),
	"mail",
	CAMEL_PROVIDER_IS_REMOTE | CAMEL_PROVIDER_IS_SOURCE |
	CAMEL_PROVIDER_IS_STORAGE | CAMEL_PROVIDER_SUPPORTS_SSL | CAMEL_PROVIDER_DISABLE_SENT_FOLDER,
	CAMEL_URL_NEED_USER | CAMEL_URL_NEED_HOST | CAMEL_URL_ALLOW_AUTH,
	zimbra_conf_entries,
	/* ... */
};


static CamelServiceAuthType camel_zimbra_password_authtype =
{
	N_("Password"),
	
	N_("This option will connect to the Zimbra server using a "
	   "plaintext password."),
	
	"",
	TRUE
};


static int
zimbra_auto_detect_cb (CamelURL *url, GHashTable **auto_detected,
			 CamelException *ex)
{
	*auto_detected = g_hash_table_new (g_str_hash, g_str_equal);

	g_hash_table_insert (*auto_detected, g_strdup ("caldav_host"), g_strdup (url->host));

	return 0;
}


void
camel_provider_module_init(void)
{
	CamelProvider *imap_provider = NULL;
	CamelException ex = CAMEL_EXCEPTION_INITIALISER;

	imap_provider =  camel_provider_get("imap://", &ex);

	zimbra_provider.url_hash			= zimbra_url_hash;
	zimbra_provider.url_equal			= zimbra_url_equal;
	zimbra_provider.auto_detect			= zimbra_auto_detect_cb;
	zimbra_provider.authtypes			= g_list_prepend (zimbra_provider.authtypes, &camel_zimbra_password_authtype);
	zimbra_provider.translation_domain	= GETTEXT_PACKAGE;
	
	zimbra_provider.object_types[CAMEL_PROVIDER_STORE] = imap_provider->object_types [CAMEL_PROVIDER_STORE];
	
	camel_provider_register (&zimbra_provider);
}


static void
add_hash (guint *hash, char *s)
{
	if (s)
		*hash ^= g_str_hash(s);
}

static guint
zimbra_url_hash (gconstpointer key)
{
	const CamelURL *u = (CamelURL *)key;
	guint hash = 0;

	add_hash (&hash, u->user);
	add_hash (&hash, u->authmech);
	add_hash (&hash, u->host);
	hash ^= u->port;
	
	return hash;
}

static gint
check_equal (char *s1, char *s2)
{
	if (s1 == NULL) {
		if (s2 == NULL)
			return TRUE;
		else
			return FALSE;
	}
	
	if (s2 == NULL)
		return FALSE;

	return strcmp (s1, s2) == 0;
}

static gint
zimbra_url_equal (gconstpointer a, gconstpointer b)
{
	const CamelURL *u1 = a, *u2 = b;
	
	return check_equal (u1->protocol, u2->protocol)
		&& check_equal (u1->user, u2->user)
		&& check_equal (u1->authmech, u2->authmech)
		&& check_equal (u1->host, u2->host)
		&& u1->port == u2->port;
}
