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

#ifndef E_ZIMBRA_UTILS_H
#define E_ZIMBRA_UTILS_H

#include <glib.h>
#include <libedataserver/e-file-cache.h>
#include <libedataserver/e-url.h>
#include <glib/gstdio.h>
#include <time.h>


typedef enum _EFileCacheIDType
{
	E_FILE_CACHE_UPDATE_IDS,
	E_FILE_CACHE_DELETE_IDS
} EFileCacheIDType;

	
gboolean
e_zimbra_utils_find_cache_string
	(
	EFileCache	*	cache,
	const char	*	key,
	const char	*	str
	);


void
e_zimbra_utils_del_cache_string
	(
	EFileCache	*	cache,
	const char	*	key,
	const char	*	str
	);


GPtrArray*
e_zimbra_utils_get_cache_array
	(
	EFileCache	*	cache,
	const char	*	key
	);


GPtrArray*
e_zimbra_utils_make_array_from_string
	(
	const char * string
	);


const char*
e_zimbra_utils_check_array_for_string
	(
	GPtrArray	*	array,
	const char	*	string
	);


char*
e_zimbra_utils_make_string_from_array
	(
	GPtrArray * array
	);


char*
e_zimbra_utils_uri_to_fspath
	(
	const char * uri
	);


void
e_zimbra_utils_pack_id
	(
	char		*	packed_id,
	size_t			packed_id_len,
	const char	*	zid,
	const char	*	rev,
	time_t			md
	);


void
e_zimbra_utils_unpack_id
	(
	const char	*	packed_id,
	const char	**	zid,
	const char	**	rev,
	time_t		*	md
	);


GPtrArray*
e_file_cache_get_ids
	(
	EFileCache		*	cache,
	EFileCacheIDType	type
	);


gboolean
e_file_cache_set_ids
	(
	EFileCache		*	cache,
	EFileCacheIDType	type,
	GPtrArray		*	ids
	);


gboolean
e_file_cache_add_ids
	(
	EFileCache		*	cache,
	EFileCacheIDType	type,
	const char		*	ids
	);


const char*
g_ptr_array_lookup_id
	(
	GPtrArray	*	array,
	const char	*	id
	);


gboolean
g_ptr_array_remove_id
	(
	GPtrArray	*	array,
	const char	*	id
	);


char *path_from_uri (const char *uri);

void e_uri_set_path (EUri * uri, const char *path);

void g_string_append_url_encoded (GString * str,
                                  const char *in, const char *extra_enc_chars);

gboolean g_string_unescape (GString * string, const char *illegal_characters);

gboolean zimbra_parse_version_string (const char *version,
                                      guint * major,
                                      guint * minor, guint * micro);

gboolean zimbra_check_min_server_version (char *version_string);

void zimbra_recursive_delete (const char *path);


char*
e_zimbra_encode_url
	(
	const char	*	url,
	char		*	encoded,
	size_t			encoded_len,
	const char	*	extra
	);


#endif
