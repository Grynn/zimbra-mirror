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

#ifndef __GLOGMAIN_H__
#define __GLOGMAIN_H__

#include <glib.h>
#include <glog/glogcategory.h>

G_BEGIN_DECLS


/**
 * GLogLogMessage:
 *
 * This struct is used for performance reasons. Instead of evaluating the format
 * string everytime we get a debug message, we only evaluate it when a log 
 * function needs the evaluated string. The only function that may be used for 
 * this struct is glog_log_message_get(). 
 */
typedef struct _GLogLogMessage GLogLogMessage;
/**
 * GLogLogFunction:
 * @category: category to log
 * @level: level the message is in
 * @file: the file that emitted the message, usually the __FILE__ identifier
 * @function: the function that emitted the message, usually G_STRFUNC
 * @line: the line from that the message was emitted, usually __LINE__
 * @object: the object this message relates to or NULL if none. An object is
 *	    a memory location of at least sizeof (gpointer) bytes.
 * @message: the actual message that should be output.
 * @data: the user data passed to glog_log_function_add()
 *
 * prototype of logging functions. This is used by glog_log_function_add() and 
 * glog_log_function_remove().
 */
typedef void (*GLogLogFunction)	(GLogCategory *		category,
				 GLogLevel		level,
				 const gchar *		file,
				 const gchar *		function,
				 gint			line,
				 gpointer		object,
				 GLogLogMessage *	message,
				 gpointer		data);


void		glog_init		(void);
void		glog_exit		(void);

/* note we don't use G_GNUC_PRINTF (7, 8) to be able to extend printf conversion
 * specifiers, like GLOG_PTR_FORMAT */
void		glog_log		(GLogCategory *		category,
					 GLogLevel		level,
					 const gchar *		file,
					 const gchar *		function,
					 gint			line,
					 gpointer		object,
					 const gchar *		format,
					 ...) G_GNUC_NO_INSTRUMENT;
void		glog_log_valist		(GLogCategory *		category,
					 GLogLevel		level,
					 const gchar *		file,
					 const gchar *		function,
					 gint			line,
					 gpointer		object,
					 const gchar *		format,
					 va_list		args) G_GNUC_NO_INSTRUMENT;

const gchar *	glog_log_message_get	(GLogLogMessage *	message);

void		glog_log_default	(GLogCategory *		category,
					 GLogLevel		level,
					 const gchar *		file,
					 const gchar *		function,
					 gint			line,
					 gpointer		object,
					 GLogLogMessage *	message,
					 gpointer		unused) G_GNUC_NO_INSTRUMENT;

void		glog_add_log_function		(GLogLogFunction	func,
						 gpointer data);
gboolean	glog_remove_log_function	(GLogLogFunction      	func,
						 gpointer		data);

void		glog_set_threshold		(const gchar *		pattern,
						 GLogLevel		level);
void		glog_unset_threshold		(const gchar *		pattern);


G_END_DECLS

#endif /* __GLOGMAIN_H__ */
