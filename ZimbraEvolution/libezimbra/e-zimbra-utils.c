/* -*- Mode: C; tab-width: 4; indent-tabs-mode: t; c-basic-offset: 4 -*- */
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
 * Authors: Scott Herscher <scott.herscher@zimbra.com>
 * 
 * Copyright (C) 2006 Zimbra, Inc.
 * 
 */


#include "e-zimbra-utils.h"
#include "e-zimbra-debug.h"
#include <glog/glog.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <errno.h>
#include <stdio.h>


static gboolean
set_cache_string
	(
	EFileCache	*	cache,
	const char	*	key,
	const char	*	str
	)
{
	gboolean ok;

	if ( e_file_cache_get_object( cache, key ) )
	{
		ok = e_file_cache_replace_object( cache, key, str );
	}
	else
	{
		ok = e_file_cache_add_object( cache, key, str );
	}

	return ok;
}


static gboolean
add_cache_string
	(
	EFileCache	*	cache,
	const char	*	key,
	const char	*	str
	)
{
	GPtrArray	*	array	=	NULL;
	char		*	val		=	NULL;
	gboolean		ok		=	FALSE;

	if ( ( val = ( char* ) e_file_cache_get_object( cache, key ) ) != NULL )
	{
		char * copy;
		char * savept;
		char * tok;

		array = e_zimbra_utils_make_array_from_string( val );
		zimbra_check( array, exit, ok = FALSE; g_warning( "e_zimbra_utils_make_array_from_string returned NULL" ) );

		copy = g_strdup( str );
		zimbra_check( copy, exit, ok = FALSE );

		tok = strtok_r( copy, ",", &savept );

		while ( tok )
		{
			if ( !e_zimbra_utils_check_array_for_string( array, tok ) )
			{
				char * dup = g_strdup( str );
				zimbra_check( dup, exit, ok = FALSE; g_warning( "g_strdup returned NULL" ) );
				g_ptr_array_add( array, dup );
			}

			tok = strtok_r( NULL, ",", &savept );
		}

		val = e_zimbra_utils_make_string_from_array( array );
		zimbra_check( val, exit, ok = FALSE; g_warning( "e_zimbra_utils_make_string_from_array returned NULL" ) );

		e_file_cache_replace_object( E_FILE_CACHE( cache ), key, val );

		g_free( val );
	}
	else
	{
		e_file_cache_add_object( E_FILE_CACHE( cache ), key, str );
	}

	ok = TRUE;

exit:

	if ( array )
	{
		g_ptr_array_free( array, TRUE );
	}

	return ok;
}


gboolean
e_zimbra_utils_find_cache_string
	(
	EFileCache	*	cache,
	const char	*	key,
	const char	*	str
	)
{
	GPtrArray	*	array	=	NULL;
	char		*	val		=	NULL;
	gboolean		ret		=	FALSE;

	if ( ( val = ( char* ) e_file_cache_get_object( cache, key ) ) != NULL )
	{
		array = e_zimbra_utils_make_array_from_string( val );
		zimbra_check( array, exit, g_warning( "e_zimbra_utils_make_array_from_string returned NULL" ) );

		ret = e_zimbra_utils_check_array_for_string( array, str ) ? TRUE : FALSE;
	}

exit:

	if ( array )
	{
		g_ptr_array_free( array, TRUE );
	}

	return ret;
}


void
e_zimbra_utils_del_cache_string
	(
	EFileCache	*	cache,
	const char	*	key,
	const char	*	str
	)
{
	GPtrArray	*	array	=	NULL;
	char		*	val		=	NULL;
	char		*	new_str	=	NULL;

	if ( ( val = ( char* ) e_file_cache_get_object( cache, key ) ) != NULL )
	{
		array = e_zimbra_utils_make_array_from_string( val );
		zimbra_check( array, exit, g_warning( "e_zimbra_utils_make_array_from_string returned NULL" ) );

		g_ptr_array_remove_id( array, str );

		new_str = e_zimbra_utils_make_string_from_array( array );

		e_file_cache_replace_object( E_FILE_CACHE( cache ), key, new_str );
	}

exit:

	if ( new_str )
	{
		g_free( new_str );
	}

	if ( array )
	{
		g_ptr_array_free( array, TRUE );
	}

	return;
}


GPtrArray*
e_zimbra_utils_get_cache_array
	(
	EFileCache	*	cache,
	const char	*	key
	)
{
	char		*	val;
	GPtrArray	*	ret	=	NULL;

	if ( ( val = ( char* ) e_file_cache_get_object( cache, key ) ) != NULL )
	{
		ret = e_zimbra_utils_make_array_from_string( val );
	}
	else
	{
		ret = g_ptr_array_new();
	}

	return ret;
}


GPtrArray*
e_zimbra_utils_make_array_from_string
	(
	const char * string
	)
{
	GPtrArray	*	array 		= NULL;
	char		*	copy		= NULL;
	char		*	save_ptr	= NULL;
	char		*	tok			= NULL;

	array = g_ptr_array_new();
	zimbra_check( array, exit, g_warning( "g_ptr_array_new returned NULL" ) );

	if ( string )
	{
		copy = g_strdup( string );

		tok = strtok_r( copy, ",", &save_ptr );

		while ( tok )
		{
			if ( *tok )
			{
				char * dup = g_strdup( tok );
				zimbra_check( dup, exit, g_ptr_array_free( array, TRUE ); array = NULL; g_warning( "g_strdup returned NULL" ) );
				g_ptr_array_add( array, dup );
			}
	
			tok = strtok_r( NULL, ",", &save_ptr );
		}
	}

exit:

	if ( copy )
	{
		g_free( copy );
	}

	return array;
}


const char*
e_zimbra_utils_check_array_for_string
	(
	GPtrArray	*	array,
	const char	*	string
	)
{
	char	*	ret = NULL;
	int			i;

	zimbra_check( array, exit, g_warning( "check_array_for_string passed in NULL array" ) );
	zimbra_check( string, exit, g_warning( "check_array_for_string passed in NULL string" ) );

	for ( i = 0; i < array->len; i++ )
	{
		ret = g_ptr_array_index( array, i );

		if ( strcmp( string, ret ) == 0 )
		{
			break;
		}

		ret = NULL;
	}

exit:

	return ret;
}


char*
e_zimbra_utils_make_string_from_array
	(
	GPtrArray * array
	)
{
	char	*	string = NULL;
	int			i;

	zimbra_check( array, exit, g_warning( "make_string_from_array passed in NULL" ) );

	for ( i = 0; i < array->len; i++ )
	{
		char * tok = g_ptr_array_index( array, i );

		if ( string )
		{
			char * temp = string;

			string = g_strconcat( temp, ",", tok, NULL );
			g_free( temp );
			zimbra_check( string, exit, g_warning( "g_strconcat returned NULL" ) );
		}
		else
		{
			string = g_strdup( tok );
			zimbra_check( string, exit, g_warning( "g_strdup returned NULL" ) );
		}
	}

	if ( !string )
	{
		string = g_strdup( "" );
		zimbra_check( string, exit, g_warning( "g_strdup returned NULL" ) );
	}

exit:

	return string;
}


char*
e_zimbra_utils_uri_to_fspath
	(
	const char * uri
	)
{
    char	*	fspath_uri;
    int			i;

	// Mangle the URI to not contain invalid characters

	fspath_uri = g_strdup( uri );
	zimbra_check( fspath_uri, exit, fspath_uri = NULL );

    for ( i = 0; i < strlen( fspath_uri ); i++ )
	{
        switch ( fspath_uri[i] )
		{
        	case ':':
        	case '/':
            	fspath_uri[i] = '_';
        }
    }

exit:

    return fspath_uri;
}


/* This function is stolen from E-D-S */
char *
path_from_uri (const char *uri)
{
    char *mangled_uri, *path;
    int i;

    /* mangle the URI to not contain invalid characters */
    mangled_uri = g_strdup (uri);

    for (i = 0; i < strlen (mangled_uri); i++) {
        switch (mangled_uri[i]) {
        case ':':
        case '/':
            mangled_uri[i] = '_';
        }
    }

    /* generate the file name */
    path = g_build_path (G_DIR_SEPARATOR_S,
                         g_get_home_dir (),
                         ".evolution", "cache", "zimbra", mangled_uri, NULL);

    GLOG_DEBUG ("path [from uri: %s] = %s", uri, path);
    /* free memory */
    g_free (mangled_uri);

    return path;
}


/* Stolen from camel. Totally stolen! */

/* From RFC 2396 2.4.3, the characters that should always be encoded */
static const char url_encoded_char[] =
{
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,     /* 0x00 - 0x0f */
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,     /* 0x10 - 0x1f */
    1, 0, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,     /*  ' ' - '/'  */
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0,     /*  '0' - '?'  */
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,     /*  '@' - 'O'  */
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0,     /*  'P' - '_'  */
    1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,     /*  '`' - 'o'  */
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 1,     /*  'p' - 0x7f */
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1
};


void
g_string_append_url_encoded (GString * str,
                             const char *in, const char *extra_enc_chars)
{
    const unsigned char *s = (const unsigned char *) in;

    while (*s) {
        if (url_encoded_char[*s] ||
            (extra_enc_chars && strchr (extra_enc_chars, *s)))
            g_string_append_printf (str, "%%%02x", (int) *s++);
        else
            g_string_append_c (str, *s++);
    }
}


void
e_uri_set_path (EUri * uri, const char *path)
{
    GString *str;

    g_return_if_fail (uri != NULL);

    if (path == NULL || !strlen (path)) {
        g_free (uri->path);
        uri->path = NULL;
        return;
    }

    str = g_string_new ("");

    if (path[strlen (path) - 1] != '/') {
        g_string_append_c (str, '/');
    }

    /* FIXME: url encode, decode? */
    str = g_string_append (str, path);
    uri->path = str->str;
    g_string_free (str, FALSE);
}


gboolean
zimbra_parse_version_string (const char *version,
                             guint * major, guint * minor, guint * micro)
{
    gchar **sa;
    guint len;

    if (version == NULL) {
        return FALSE;
    }

    sa = g_strsplit (version, ".", 0);

    if (sa == NULL || *sa == NULL || major == NULL) {
        return FALSE;
    }

    len = g_strv_length (sa);

    if (major && minor && micro) {
        len = MIN (len, 3);
    } else if (major && minor) {
        len = MIN (len, 2);
    } else {
        len = 1;
    }

    switch (len) {

    case 3:
        *micro = atoi (sa[2]);
        /* FALL */

    case 2:
        *minor = atoi (sa[1]);
        /* FALL */

    case 1:
        *major = atoi (sa[0]);
        break;

    default:
        return FALSE;
    }

    g_strfreev (sa);
    return TRUE;
}


static int
hex_to_int (char c)
{
    return c >= '0' && c <= '9' ? c - '0'
         : c >= 'A' && c <= 'F' ? c - 'A' + 10
         : c >= 'a' && c <= 'f' ? c - 'a' + 10 : -1;
}


static int
unescape_character (const char *scanner)
{
    int first_digit;
    int second_digit;

    first_digit = hex_to_int (*scanner++);
    if (first_digit < 0) {
        return -1;
    }

    second_digit = hex_to_int (*scanner++);
    if (second_digit < 0) {
        return -1;
    }

    return (first_digit << 4) | second_digit;
}

#define HEX_ESCAPE '%'

gboolean
g_string_unescape (GString * string, const char *illegal_characters)
{
    const char *in;
    char *out;
    gint character;

    if (string == NULL) {
        return FALSE;
    }

    for (in = out = string->str; *in != '\0'; in++) {
        character = *in;
        if (*in == HEX_ESCAPE) {
            character = unescape_character (in + 1);

            /* Check for an illegal character. We consider '\0' illegal here. */
            if (character <= 0
                || (illegal_characters != NULL
                    && strchr (illegal_characters, (char) character) != NULL)) {
                return FALSE;
            }
            in += 2;
        }
        *out++ = (char) character;
    }

    *out = '\0';
    return TRUE;
}

void
zimbra_recursive_delete (const char *path)
{
    GDir *dh;
    const char *name;

    if (!g_file_test (path, G_FILE_TEST_IS_DIR)) {
        g_unlink (path);
        return;
    }

    dh = g_dir_open (path, 0, NULL);

    while ((name = g_dir_read_name (dh))) {
        char *fp;

        if (g_str_equal (name, ".") || g_str_equal (name, "..")) {
            continue;
        }

        fp = g_build_filename (path, name, NULL);
        zimbra_recursive_delete (fp);
        g_free (fp);
    }

    if (dh != NULL) {
        g_dir_close (dh);
    }

    g_rmdir (path);
}


void
e_zimbra_utils_pack_id
	(
	char		*	packed_id,
	size_t			packed_id_len,
	const char	*	zid,
	const char	*	rev,
	time_t			md
	)
{
	snprintf( packed_id, packed_id_len, "%s|%s|%lu", zid, rev ? rev : "0", md );
}


static const char * NullRev = "0";

void
e_zimbra_utils_unpack_id
	(
	const char	*	packed_id,
	const char	**	zid,
	const char	**	rev,
	time_t		*	md
	)
{
	char * delim;
	
	if ( zid )
	{
		*zid = packed_id;
	}

	delim = strchr( packed_id, '|' );

	if ( delim )
	{
		*delim = '\0';
		delim++;

		if ( rev )
		{
			*rev = delim;
		}

		delim = strchr( delim, '|' );

		if ( delim )
		{
			*delim = '\0';
			delim++;

			if ( md )
			{
				*md = atol( delim );
			}
		}
		else if ( md )
		{
			*md = 0;
		}
	}
	else
	{
		if ( rev )
		{
			*rev = NullRev;
		}

		if ( md )
		{
			*md	 = 0;
		}
	}
}


gboolean
e_file_cache_set_ids
	(
	EFileCache		*	cache,
	EFileCacheIDType	type,
	GPtrArray		*	ids
	)
{
	char	*	string	= NULL;
	gboolean	ok		= TRUE;

	string = e_zimbra_utils_make_string_from_array( ids );
	zimbra_check( string, exit, ok = FALSE );

	switch ( type )
	{
		case E_FILE_CACHE_UPDATE_IDS:
		{
			set_cache_string( cache, "update", string );
		}
		break;

		case E_FILE_CACHE_DELETE_IDS:
		{
			set_cache_string( cache, "delete", string );
		}
		break;
	}

exit:

	if ( string )
	{
		g_free( string );
	}

	return ok;
}


GPtrArray*
e_file_cache_get_ids
	(
	EFileCache		*	cache,
	EFileCacheIDType	type
	)
{
	GPtrArray	*	array	= NULL;
	char		*	string	= NULL;

	switch ( type )
	{
		case E_FILE_CACHE_UPDATE_IDS:
		{
			string  = ( char* ) e_file_cache_get_object( cache, "update" );
		}
		break;

		case E_FILE_CACHE_DELETE_IDS:
		{
			string  = ( char* ) e_file_cache_get_object( cache, "delete" );
		}
		break;
	}

	array = e_zimbra_utils_make_array_from_string( string );
	zimbra_check( array, exit, g_warning( "e_zimbra_utils_make_array_from_string returned NULL" ) );

exit:

	return array;
}


gboolean
e_file_cache_add_ids
	(
	EFileCache		*	cache,
	EFileCacheIDType	type,
	const char		*	ids
	)
{
	gboolean ok = FALSE;

	switch ( type )
	{
		case E_FILE_CACHE_UPDATE_IDS:
		{
			ok = add_cache_string( cache, "update", ids );
		}
		break;

		case E_FILE_CACHE_DELETE_IDS:
		{
			ok = add_cache_string( cache, "delete", ids );
		}
		break;
	}

	return ok;
}


const char*
g_ptr_array_lookup_id
	(
	GPtrArray	*	array,
	const char	*	id
	)
{
	const char	*	inserted	= NULL;
	size_t			slen;
	unsigned		i;

	zimbra_check( array, exit, g_warning( "g_ptr_array_lookup_id passed in NULL array" ) );
	zimbra_check( id, exit, g_warning( "g_ptr_array_lookup_id passed in NULL string" ) );

	slen = strlen( id );

	for ( i = 0; i < array->len; i++ )
	{
		char	*	spot;
		size_t		ilen;

		inserted = g_ptr_array_index( array, i );

		// Check the inserted string for the '|' character. We're using the '|' character to concatenate
		// a zid and a rev, but we don't want to include the rev in the strcmp.

		spot = strchr( inserted, '|' );
		ilen = spot ? spot - inserted : strlen( inserted );

		if ( slen != ilen )
		{
			continue;
		}

		if ( memcmp( inserted, id, slen ) != 0 )
		{
			inserted = NULL;
		}
		else
		{
			break;
		}
	}

exit:

	return inserted;
}


gboolean
g_ptr_array_remove_id
	(
	GPtrArray	*	array,
	const char	*	id
	)
{
	size_t		slen;
	unsigned	i;
	gboolean	ok = FALSE;

	zimbra_check( array, exit, g_warning( "g_ptr_array_remove_id passed in NULL array" ) );
	zimbra_check( id, exit, g_warning( "g_ptr_array_remove_id passed in NULL string" ) );

	slen = strlen( id );

	for ( i = 0; i < array->len; i++ )
	{
		char	*	inserted = g_ptr_array_index( array, i );
		char	*	spot;
		size_t		ilen;

		// Check the inserted string for the '|' character. We're using the '|' character to concatenate
		// a zid and a rev, but we don't want to include the rev in the strcmp.

		spot = strchr( inserted, '|' );
		ilen = spot ? spot - inserted : strlen( inserted );

		if ( slen != ilen )
		{
			continue;
		}

		if ( memcmp( inserted, id, slen ) == 0 )
		{
			g_ptr_array_remove_index( array, i );
			g_free( inserted );
			ok = TRUE;
			break;
		}
	}

exit:

	return ok;
}
