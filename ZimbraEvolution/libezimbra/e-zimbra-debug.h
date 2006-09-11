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


# if ((__GNUC__ > 2) || ((__GNUC__ == 2) && (__GNUC_MINOR__ >= 9)))

#	define	__ZIMBRA_FUNCTION__			__func__

#elif (defined( __GNUC__))

#	define	__ZIMBRA_FUNCTION__			__PRETTY_FUNCTION__

#elif( defined(_MSC_VER ) && !defined(_WIN32_WCE))

#	define	__ZIMBRA_FUNCTION__			__FUNCTION__

#else

#	define	__ZIMBRA_FUNCTION__			""

#endif


#define zimbra_check_quiet(expr, label, action)	\
do 														\
{															\
	if (!(expr)) 										\
	{														\
		{													\
			action;										\
		}													\
		goto label;										\
	}														\
} while (0)


#define zimbra_check(expr, label, action)			\
do 														\
{															\
	if (!(expr)) 										\
	{														\
		_ZimbraDebugPrint(0, NULL, __FILE__, __ZIMBRA_FUNCTION__, __LINE__);	\
		{													\
			action;										\
		}													\
		goto label;										\
	}														\
} while (0)


#define zimbra_check_okay_quiet(code, label)		\
do 														\
{															\
	if ((int) code != 0) 							\
	{														\
		goto label;										\
	}														\
} while (0)


#define zimbra_check_okay(code, label)			\
do 														\
{															\
	if ((int) code != 0) 							\
	{														\
		_ZimbraDebugPrint((int) code, NULL, __FILE__, __ZIMBRA_FUNCTION__, __LINE__);	\
		goto label;										\
	}														\
} while ( 0 )


#define ZimbraTranslateError(expr, errno)		((expr) ? 0 : (errno))


#if !defined(NDEBUG)

#	define zimbra_assert(X)		\
									\
	do								\
	{								\
		if (!(X))				\
		{							\
			_ZimbraDebugPrint( 0, #X, __FILE__, __ZIMBRA_FUNCTION__, __LINE__); \
		}							\
	} while( 0 )

#else

#	define zimbra_assert(X)

#endif


#if !defined(NDEBUG)

void
ZimbraDebugMemoryInUse();


void
ZimbraDebugPrint
	(
	int				error,
	const char	*	message,
	const char	*	file,
	const char	*	function,
	int				line
	);


#	if (__C99_VA_ARGS__)

#		define  _ZimbraDebugPrint(...)			ZimbraDebugPrint(__VA_ARGS__)

#	else

#		define  _ZimbraDebugPrint					ZimbraDebugPrint

#	endif

#else

#	if (__C99_VA_ARGS__)

#		define  _ZimbraDebugPrint(...)

#	else

#		define  _ZimbraDebugPrint					while( 0 )

#	endif

#endif


#define ZimbraUnusedParam(X)	(void) (X)


#endif
