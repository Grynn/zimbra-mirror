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

#ifdef HAVE_CONFIG_H
#  include "config.h"
#endif

#include "glog.h"
#ifdef HAVE_PRINTF_EXTENSION
#include <printf.h>
#include <stdio.h>              /* fprintf */
#endif

static GSList *_glog_print_functions = NULL;

/**
 * glog_to_string:
 * @pointer: pointer to be converted to a string. If it is not NULL, it must 
 *	     point to a memory region with a length of at least 
 *	     sizeof(gpointer) bytes.
 *
 * Converts a pointer to a string representation using the functions registered
 * with glog for this purpose. You need to g_free the string after use.
 *
 * Returns: A string representation of @pointer
 **/
gchar *
glog_to_string (gpointer pointer)
{
  gchar *ret;
  GSList *walk;
  GLogPrintFunction func;

  if (pointer == NULL)
    return g_strdup ("(NULL)");

  for (walk = _glog_print_functions; walk; walk = g_slist_next (walk)) {
    func = walk->data;
    ret = func (pointer);
    if (ret)
      return ret;
  }
  
  return g_strdup ("(\?\?\?)");
}

/**
 * glog_register_print_function:
 * @func: function to register
 *
 * Registers a function to convert pointers to a string representation. The
 * registered function is given a pointer to a memory area of at least 
 * sizeof(gpointer) bytes. It has to identify whether or not it can print the 
 * given pointer and if so, return a string representation of that pointer.
 * If it cannot print it, it should return NULL. 
 **/
void
glog_register_print_function (GLogPrintFunction func)
{
  g_return_if_fail (func != NULL);
  
  _glog_print_functions = g_slist_prepend (_glog_print_functions, func);
}

#ifdef HAVE_PRINTF_EXTENSION
static int
_glog_printf_extension (FILE * stream, const struct printf_info *info,
    const void *const *args)
{
  gchar *buffer;
  int len;
  void *ptr;

  buffer = NULL;
  ptr = *(void **) args[0];

  buffer = glog_to_string (ptr);
  len = fprintf (stream, "%*s", (info->left ? -info->width : info->width),
      buffer);

  g_free (buffer);
  return len;
}

static int
_glog_printf_extension_arginfo (const struct printf_info *info, size_t n,
    int *argtypes)
{
  if (n > 0)
    argtypes[0] = PA_POINTER;
  return 1;
}
#endif /* HAVE_PRINTF_EXTENSION */

void
_glog_init_printf_extension (void)
{
#ifdef HAVE_PRINTF_EXTENSION
  register_printf_function (GLOG_PTR_FORMAT[0], _glog_printf_extension,
      _glog_printf_extension_arginfo);
#endif
}


