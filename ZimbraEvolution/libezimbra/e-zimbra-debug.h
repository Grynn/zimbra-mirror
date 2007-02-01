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

#ifndef E_ZIMBRA_DEBUG_H
#define E_ZIMBRA_DEBUG_H

#include <glog/glog.h>
#include <stdarg.h>


#define zimbra_check_quiet(expr, label, action)		\
do 													\
{													\
	if (!(expr)) 									\
	{												\
		{											\
			action;									\
		}											\
		goto label;									\
	}												\
} while (0)


#define zimbra_check(expr, label, action)			\
do 													\
{													\
	if (!(expr)) 									\
	{												\
		GLOG_ERROR( "check failed: %s", #expr );	\
		{											\
			action;									\
		}											\
		goto label;									\
	}												\
} while (0)


#define zimbra_check_okay_quiet(code, label)		\
do 													\
{													\
	if ((int) code != 0) 							\
	{												\
		goto label;									\
	}												\
} while (0)


#define zimbra_check_okay(code, label)				\
do 													\
{													\
	if ((int) code != 0) 							\
	{												\
		GLOG_ERROR( "check failed: %d", code );		\
		goto label;									\
	}												\
} while ( 0 )


#define ZimbraTranslateError(expr, errno)		((expr) ? 0 : (errno))


#if !defined(NDEBUG)

#	define zimbra_assert(X)							\
													\
	do												\
	{												\
		if (!(X))									\
		{											\
			GLOG_ERROR( "assert failed: %s", #X );	\
		}											\
	} while( 0 )

#else

#	define zimbra_assert(X)

#endif


#if !defined(NDEBUG)

void
ZimbraDebugMemoryInUse();


#define ZimbraUnusedParam(X)	(void) (X)


#endif


#endif
