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

#ifndef E_ZIMBRA_LOG_H
#define E_ZIMBRA_LOG_H

#include <stdarg.h>


#if (defined( __GNUC__))

#	if ((__GNUC__ > 3) || ((__GNUC__ == 3) && (__GNUC_MINOR__ >= 3)))

#		define  __C99_VA_ARGS__	1

#		define  __GNU_VA_ARGS__	0

#	else

#		define  __C99_VA_ARGS__	0

#		define  __GNU_VA_ARGS__	1

#	endif

#else

#	define  __C99_VA_ARGS__		0

#	define  __GNU_VA_ARGS__		0

#endif


#define kZimbraLogAlarm		0
#define kZimbraLogError		1
#define kZimbraLogWarning		2
#define kZimbraLogInfo			3
#define kZimbraLogDebug		4
#define kZimbraLogVerbose		5


#define kZimbraLogRaw			( 1 << 0 )


void
ZimbraLogSetComponent
	(
	const char * component
	);


void
ZimbraLogSetLevel
	(
	int level
	);


void
ZimbraLogPrint
	(
	int				level,
	int				flags,
	const char	*	format,
	...
	);


typedef void
(* ZimbraLogMessageHandler)
	(
	int				level,
	const char	*	message
	);


void
ZimbraLogSetMessageHandler
	(
	ZimbraLogMessageHandler handler
	);


#if (__C99_VA_ARGS__)

//#	define  ZimbraLog(...)			ZimbraLogPrint(__VA_ARGS__)
#	define  ZimbraLog				ZimbraLogPrint

#else

#	define  ZimbraLog				ZimbraLogPrint

#endif


#endif
