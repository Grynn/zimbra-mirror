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

#if !defined (__GLOG_H__)
#error "Only <glog/glog.h> can be included directly, this file may disappear or change contents."
#endif

#ifndef __GLOGISOVARARGS_H__
#define __GLOGISOVARARGS_H__

#include <glog/glogmacros.h>
#include <glog/glogmain.h>

G_BEGIN_DECLS


/* stupidity check */
#ifndef G_HAVE_ISO_VARARGS
#  error "You don't have G_HAVE_ISO_VARARGS defined, so don't include this header"
#endif


#ifndef GLOG_DISABLE_LOGGING

/**
 * GLOG_CAT_LEVEL_LOG:
 * @cat: category to use
 * @level: the severity of the message
 * @object: the object this message relates to or NULL if none. An object is
 *	    a memory location of at least sizeof (gpointer) bytes.
 * @Varargs: A printf-style message to output
 *
 * Outputs a logging message using glog_log with the default values for file, 
 * line and function. This is the most general macro for outputting debugging 
 * messages. You will probably want to use one of the ones described below.
 */
#define GLOG_CAT_LEVEL_LOG(cat,level,object,...) \
    glog_log ((cat), (level), __FILE__, G_STRFUNC, __LINE__, (object), \
	__VA_ARGS__)

/**
 * GLOG_CAT_ERROR_OBJECT:
 * @cat: category to use
 * @obj: the object this message relates to or NULL if none. An object is
 *       a memory location of at least sizeof (gpointer) bytes.
 * @Varargs: printf-style message to output
 *
 * Output an error message belonging to the given object in the given category.
 */
#define GLOG_CAT_ERROR_OBJECT(cat,obj,...)	GLOG_CAT_LEVEL_LOG (cat, GLOG_LEVEL_ERROR,   obj,  __VA_ARGS__)

/**
 * GLOG_CAT_WARNING_OBJECT:
 * @cat: category to use
 * @obj: the object this message relates to or NULL if none. An object is
 *       a memory location of at least sizeof (gpointer) bytes.
 * @Varargs: printf-style message to output
 *
 * Output a warning message belonging to the given object in the given category.
 */
#define GLOG_CAT_WARNING_OBJECT(cat,obj,...)	GLOG_CAT_LEVEL_LOG (cat, GLOG_LEVEL_WARNING, obj,  __VA_ARGS__)

/**
 * GLOG_CAT_INFO_OBJECT:
 * @cat: category to use
 * @obj: the object this message relates to or NULL if none. An object is
 *	 a memory location of at least sizeof (gpointer) bytes.
 * @Varargs: printf-style message to output
 *
 * Output an informational message belonging to the given object in the given 
 * category.
 */
#define GLOG_CAT_INFO_OBJECT(cat,obj,...)	GLOG_CAT_LEVEL_LOG (cat, GLOG_LEVEL_INFO,    obj,  __VA_ARGS__)

/**
 * GLOG_CAT_DEBUG_OBJECT:
 * @cat: category to use
 * @obj: the object this message relates to or NULL if none. An object is
 *	 a memory location of at least sizeof (gpointer) bytes.
 * @Varargs: printf-style message to output
 *
 * Output a debugging message belonging to the given object in the given category.
 */
#define GLOG_CAT_DEBUG_OBJECT(cat,obj,...)	GLOG_CAT_LEVEL_LOG (cat, GLOG_LEVEL_DEBUG,   obj,  __VA_ARGS__)

/**
 * GLOG_CAT_LOG_OBJECT:
 * @cat: category to use
 * @obj: the object this message relates to or NULL if none. An object is
 *	 a memory location of at least sizeof (gpointer) bytes.
 * @Varargs: printf-style message to output
 * 
 * Output a logging message belonging to the given object in the given category.
 */
#define GLOG_CAT_LOG_OBJECT(cat,obj,...)	GLOG_CAT_LEVEL_LOG (cat, GLOG_LEVEL_LOG,     obj,  __VA_ARGS__)


/**
 * GLOG_CAT_ERROR:
 * @cat: category to use
 * @Varargs: printf-style message to output
 * 
 * Output an error message in the given category.
 */
#define GLOG_CAT_ERROR(cat,...)			GLOG_CAT_LEVEL_LOG (cat, GLOG_LEVEL_ERROR,   NULL, __VA_ARGS__)

/**
 * GLOG_CAT_WARNING:
 * @cat: category to use
 * @Varargs: printf-style message to output
 * 
 * Output a warning message in the given category.
 */
#define GLOG_CAT_WARNING(cat,...)		GLOG_CAT_LEVEL_LOG (cat, GLOG_LEVEL_WARNING, NULL, __VA_ARGS__)

/**
 * GLOG_CAT_INFO:
 * @cat: category to use
 * @Varargs: printf-style message to output
 * 
 * Output an informational message in the given category.
 */
#define GLOG_CAT_INFO(cat,...)			GLOG_CAT_LEVEL_LOG (cat, GLOG_LEVEL_INFO,    NULL, __VA_ARGS__)

/**
 * GLOG_CAT_DEBUG:
 * @cat: category to use
 * @Varargs: printf-style message to output
 * 
 * Output a debugging message in the given category.
 */
#define GLOG_CAT_DEBUG(cat,...)			GLOG_CAT_LEVEL_LOG (cat, GLOG_LEVEL_DEBUG,   NULL, __VA_ARGS__)


/**
 * GLOG_CAT_LOG:
 * @cat: category to use
 * @Varargs: printf-style message to output
 * 
 * Output a logging message in the given category.
 */
#define GLOG_CAT_LOG(cat,...)			GLOG_CAT_LEVEL_LOG (cat, GLOG_LEVEL_LOG,     NULL, __VA_ARGS__)


/**
 * GLOG_ERROR_OBJECT:
 * @obj: the object this message relates to or NULL if none. An object is
 *	 a memory location of at least sizeof (gpointer) bytes.
 * @Varargs: printf-style message to output
 * 
 * Output an error message belonging to the given object in the category 
 * currently defined as default.
 */
#define GLOG_ERROR_OBJECT(obj,...)	GLOG_CAT_LEVEL_LOG (&(GLOG_CAT_DEFAULT), GLOG_LEVEL_ERROR,   obj,  __VA_ARGS__)

/**
 * GLOG_WARNING_OBJECT:
 * @obj: the object this message relates to or NULL if none. An object is
 *	 a memory location of at least sizeof (gpointer) bytes.
 * @Varargs: printf-style message to output
 * 
 * Output a warning message belonging to the given object in the category 
 * currently defined as default.
 */
#define GLOG_WARNING_OBJECT(obj,...)	GLOG_CAT_LEVEL_LOG (&(GLOG_CAT_DEFAULT), GLOG_LEVEL_WARNING, obj,  __VA_ARGS__)

/**
 * GLOG_INFO_OBJECT:
 * @obj: the object this message relates to or NULL if none. An object is
 *	 a memory location of at least sizeof (gpointer) bytes.
 * @Varargs: printf-style message to output
 * 
 * Output an informational message belonging to the given object in the category 
 * currently defined as default.
 */
#define GLOG_INFO_OBJECT(obj,...)	GLOG_CAT_LEVEL_LOG (&(GLOG_CAT_DEFAULT), GLOG_LEVEL_INFO,    obj,  __VA_ARGS__)

/**
 * GLOG_DEBUG_OBJECT:
 * @obj: the object this message relates to or NULL if none. An object is
 *	 a memory location of at least sizeof (gpointer) bytes.
 * @Varargs: printf-style message to output
 * 
 * Output a debugging message belonging to the given object in the category 
 * currently defined as default.
 */
#define GLOG_DEBUG_OBJECT(obj,...)	GLOG_CAT_LEVEL_LOG (&(GLOG_CAT_DEFAULT), GLOG_LEVEL_DEBUG,   obj,  __VA_ARGS__)

/**
 * GLOG_LOG_OBJECT:
 * @obj: the object this message relates to or NULL if none. An object is
 *	 a memory location of at least sizeof (gpointer) bytes.
 * @Varargs: printf-style message to output
 * 
 * Output a logging message belonging to the given object in the category 
 * currently defined as default.
 */
#define GLOG_LOG_OBJECT(obj,...)	GLOG_CAT_LEVEL_LOG (&(GLOG_CAT_DEFAULT), GLOG_LEVEL_LOG,     obj,  __VA_ARGS__)



/**
 * GLOG_ERROR:
 * @Varargs: printf-style message to output
 * 
 * Output an error message in the category currently defined as default.
 */
#define GLOG_ERROR(...)			GLOG_CAT_LEVEL_LOG (&(GLOG_CAT_DEFAULT), GLOG_LEVEL_ERROR,   NULL, __VA_ARGS__)

/**
 * GLOG_WARNING:
 * @Varargs: printf-style message to output
 * 
 * Output a warning message in the category currently defined as default.
 */
#define GLOG_WARNING(...)		GLOG_CAT_LEVEL_LOG (&(GLOG_CAT_DEFAULT), GLOG_LEVEL_WARNING, NULL, __VA_ARGS__)

/**
 * GLOG_INFO:
 * @Varargs: printf-style message to output
 * 
 * Output an informational message in the category currently defined as default.
 */
#define GLOG_INFO(...)			GLOG_CAT_LEVEL_LOG (&(GLOG_CAT_DEFAULT), GLOG_LEVEL_INFO,    NULL, __VA_ARGS__)

/**
 * GLOG_DEBUG:
 * @Varargs: printf-style message to output
 * 
 * Output a debugging message in the category currently defined as default.
 */
#define GLOG_DEBUG(...)			GLOG_CAT_LEVEL_LOG (&(GLOG_CAT_DEFAULT), GLOG_LEVEL_DEBUG,   NULL, __VA_ARGS__)

/**
 * GLOG_LOG:
 * @Varargs: printf-style message to output
 * 
 * Output a logging message in the category currently defined as default.
 */
#define GLOG_LOG(...)			GLOG_CAT_LEVEL_LOG (&(GLOG_CAT_DEFAULT), GLOG_LEVEL_LOG,     NULL, __VA_ARGS__)


#else /* GLOG_DISABLE_LOGGING */


#define GLOG_CAT_LEVEL_LOG(cat,level,...)		/* NOP */

#define GLOG_CAT_ERROR_OBJECT(...)			/* NOP */
#define GLOG_CAT_WARNING_OBJECT(...)			/* NOP */
#define GLOG_CAT_INFO_OBJECT(...)			/* NOP */
#define GLOG_CAT_DEBUG_OBJECT(...)			/* NOP */
#define GLOG_CAT_LOG_OBJECT(...)			/* NOP */

#define GLOG_CAT_ERROR(...)				/* NOP */
#define GLOG_CAT_WARNING(...)				/* NOP */
#define GLOG_CAT_INFO(...)				/* NOP */
#define GLOG_CAT_DEBUG(...)				/* NOP */
#define GLOG_CAT_LOG(...)				/* NOP */

#define GLOG_ERROR_OBJECT(...)				/* NOP */
#define GLOG_WARNING_OBJECT(...)			/* NOP */
#define GLOG_INFO_OBJECT(...)				/* NOP */
#define GLOG_DEBUG_OBJECT(...)				/* NOP */
#define GLOG_LOG_OBJECT(...)				/* NOP */

#define GLOG_ERROR(...)					/* NOP */
#define GLOG_WARNING(...)				/* NOP */
#define GLOG_INFO(...)					/* NOP */
#define GLOG_DEBUG(...)					/* NOP */
#define GLOG_LOG(...)					/* NOP */


#endif /* GLOG_DISABLE_LOGGING */


G_END_DECLS

#endif /* __GLOGISOVARARGS_H__ */
