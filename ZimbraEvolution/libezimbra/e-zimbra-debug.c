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


#include "e-zimbra-debug.h"
#include "e-zimbra-log.h"
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <errno.h>
#include <stdio.h>


#if !defined(NDEBUG)


#	define MAX_MEMORY_BLOCKS 4192
#	define DEBUG_MEMORY

struct ZimbraDebugMemoryNode
{
	void	*	m_mem;
	size_t		m_size;
	char		m_function[128];
	char		m_file[128];
	int			m_line;
};

static struct ZimbraDebugMemoryNode  g_debugMemoryBlocks[ MAX_MEMORY_BLOCKS ] = { { 0 } };


static void
ZimbraMemoryAlloc
	(
	void		*	mem,
	size_t			size,
	const char	*	function,
	const char	*	file,
	int				line
	);


static void
ZimbraMemoryFree
	(
	void 			*	mem
	);


void
ZimbraDebugMemoryInUse
	(
	)
{
	size_t total;
	int		i;

	fprintf(stderr, "Zimbra Memory In-use\n{\n");
	for (i = 0, total = 0; i < 4192; i++)
	{
		if ( g_debugMemoryBlocks[i].m_mem != NULL)
		{
			fprintf(stderr, "   block 0x%x: size = %d: owner = %s,%d\n", (int) g_debugMemoryBlocks[i].m_mem, g_debugMemoryBlocks[i].m_size, g_debugMemoryBlocks[i].m_file, g_debugMemoryBlocks[i].m_line);
			total += g_debugMemoryBlocks[i].m_size;
		}
	}

	fprintf(stderr, "\n   total = %d\n}\n", total);
}


void*
ZimbraDebugMalloc
	(
	size_t			size,
	const char	*	function,
	const char	*	file,
	int				line
	)
{
	void * ret;

	ret = ( void* ) malloc( size );

#if defined(DEBUG_MEMORY)
	ZimbraMemoryAlloc(ret, size, function, file, line);
#endif

	return ret;
}


void*
ZimbraDebugRealloc
	(
	void				*	mem,
	size_t				size,
	const char		*	function,
	const char		*	file,
	int					line)
{
	void * ret;

#if defined(DEBUG_MEMORY)
	ZimbraMemoryFree( mem );
#endif

	ret = ( void* ) realloc( mem, size );

#if defined(DEBUG_MEMORY)
	ZimbraMemoryAlloc( ret, size, function, file, line );
#endif

	return ret;
}


void
ZimbraDebugFree
	(
	void				*	mem,
	const char		*	function,
	const char		*	file,
	int					line)
{
	// HOWL_UNUSED_PARAM( function );
	// HOWL_UNUSED_PARAM( file );
	// HOWL_UNUSED_PARAM( line );

#if defined(DEBUG_MEMORY)
	ZimbraMemoryFree( mem );
#endif

	free( mem );
}


static void
ZimbraMemoryAlloc
	(
	void			*	mem,
	size_t			size,
	const char	*	function,
	const char	*	file,
	int				line
	)
{
	int	i = 0;
	int	err;

	while ( ( g_debugMemoryBlocks[i].m_mem != NULL ) && ( i < MAX_MEMORY_BLOCKS ) )
	{
		i++;
	}

	zimbra_check_quiet( i < MAX_MEMORY_BLOCKS, exit, err = -1 );

	g_debugMemoryBlocks[i].m_mem = mem;
	g_debugMemoryBlocks[i].m_size = size;
	strcpy(g_debugMemoryBlocks[i].m_function, function);
	strcpy(g_debugMemoryBlocks[i].m_file, file );
	g_debugMemoryBlocks[i].m_line = line;

exit:

	return;
}


static void
ZimbraMemoryFree
	(
	void * mem
	)
{
	int i = 0;

	while ((g_debugMemoryBlocks[i].m_mem != mem) && (i < MAX_MEMORY_BLOCKS))
	{
		i++;
	}

	if ( i < MAX_MEMORY_BLOCKS )
	{
		g_debugMemoryBlocks[i].m_mem = NULL;
	}
	else
	{
		// sw_debug(SW_LOG_WARNING, "can't find memory block\n");
	}
}


#endif
