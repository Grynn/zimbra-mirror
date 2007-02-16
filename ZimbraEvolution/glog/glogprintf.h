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

#ifndef __GLOGPRINTF_H__
#define __GLOGPRINTF_H__

#include <glib.h>

G_BEGIN_DECLS

/**
 * GLogPrintFunction
 * Function prototype for getting a string representation of pointers.
 * See glog_register_print_function() for details.
 */
typedef gchar * (* GLogPrintFunction)	(gpointer pointer);

gchar *		glog_to_string			(gpointer		pointer);
void		glog_register_print_function	(GLogPrintFunction	func);


G_END_DECLS

#endif /* __GLOGPRINTF_H__ */
