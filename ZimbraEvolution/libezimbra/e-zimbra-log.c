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

#ifdef HAVE_CONFIG_H
#include <config.h>
#endif
#include "e-zimbra-log.h"
#include <string.h>
#include <stdio.h>
#include <time.h>

static gchar * g_logfolder	= NULL;
static gchar * g_logfile	= NULL;


void
glog_log_zimbra
	(
	GLogCategory	*	category,
	GLogLevel			level,
	const gchar		*	file, 
    const gchar		*	function,
	gint				line,
	gpointer			object, 
    GLogLogMessage	*	message,
	gpointer			unused
	)
{
	char		localtime[ 56 ] = { '\0' };
	gint		pid;
	time_t		t;

	if ( level > glog_category_get_threshold( category ) )
	{
    	return;
	}

	pid = getpid ();

	t = time( NULL );

	ctime_r( &t, localtime );

	// Get rid of trailing newline

	if ( strlen( localtime ) )
	{
		localtime[ strlen( localtime ) - 1 ] = '\0';
	}

	if ( !g_logfolder )
	{
		g_logfolder = g_build_filename( g_get_home_dir(), ".evolution/log", NULL );

		if ( g_mkdir_with_parents( g_logfolder, 0777 ) != 0 )
    	{
			g_error("g_mkdir_with_parents(%s) failed", g_logfolder );
		}
	}

	if ( g_logfolder && !g_logfile )
	{
		g_logfile = g_build_filename( g_logfolder, "zimbra.log" );
	}

	if ( g_logfile )
	{
		FILE * fp;

		fp = fopen( g_logfile, "a" );

		if ( fp )
		{
			fprintf( fp, "%s %5d %26s %s(%d):%s %s\n", glog_level_get_name( level ), pid, localtime, file, line, function, glog_log_message_get( message ) );
			fflush( fp );
			fclose( fp );
		}
	}

	g_printerr( "%s %5d %26s %s(%d):%s %s\n", glog_level_get_name( level ), pid, localtime, file, line, function, glog_log_message_get( message ) );
}
