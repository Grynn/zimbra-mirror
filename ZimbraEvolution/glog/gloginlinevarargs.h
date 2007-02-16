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

#ifndef __GLOGINLINEVARARGS_H__
#define __GLOGINLINEVARARGS_H__

#include <glog/glogmacros.h>
#include <glog/glogcategory.h>
#include <glog/glogmain.h>


G_BEGIN_DECLS


#ifndef GLOG_DISABLE_LOGGING


static inline void
GLOG_CAT_LEVEL_LOG_valist (GLogCategory * cat,
    GLogLevel level, gpointer object, const char *format, va_list varargs)
{
  glog_log_valist (cat, level, "", G_STRFUNC, 0, object, format,
      varargs);
}

static inline void
GLOG_CAT_LEVEL_LOG (GLogCategory * cat, GLogLevel level,
    gpointer object, const char *format, ...)
{
  va_list varargs;

  va_start (varargs, format);
  GLOG_CAT_LEVEL_LOG_valist (cat, level, object, format, varargs);
  va_end (varargs);
}

static inline void
GLOG_CAT_ERROR_OBJECT (GLogCategory * cat, gpointer obj, const char *format,
    ...)
{
  va_list varargs;

  va_start (varargs, format);
  GLOG_CAT_LEVEL_LOG_valist (cat, GLOG_LEVEL_ERROR, obj, format, varargs);
  va_end (varargs);
}

static inline void
GLOG_CAT_WARNING_OBJECT (GLogCategory * cat, gpointer obj,
    const char *format, ...)
{
  va_list varargs;

  va_start (varargs, format);
  GLOG_CAT_LEVEL_LOG_valist (cat, GLOG_LEVEL_WARNING, obj, format, varargs);
  va_end (varargs);
}

static inline void
GLOG_CAT_INFO_OBJECT (GLogCategory * cat, gpointer obj, const char *format,
    ...)
{
  va_list varargs;

  va_start (varargs, format);
  GLOG_CAT_LEVEL_LOG_valist (cat, GLOG_LEVEL_INFO, obj, format, varargs);
  va_end (varargs);
}

static inline void
GLOG_CAT_DEBUG_OBJECT (GLogCategory * cat, gpointer obj, const char *format,
    ...)
{
  va_list varargs;

  va_start (varargs, format);
  GLOG_CAT_LEVEL_LOG_valist (cat, GLOG_LEVEL_DEBUG, obj, format, varargs);
  va_end (varargs);
}

static inline void
GLOG_CAT_LOG_OBJECT (GLogCategory * cat, gpointer obj, const char *format,
    ...)
{
  va_list varargs;

  va_start (varargs, format);
  GLOG_CAT_LEVEL_LOG_valist (cat, GLOG_LEVEL_LOG, obj, format, varargs);
  va_end (varargs);
}

static inline void
GLOG_CAT_ERROR (GLogCategory * cat, const char *format, ...)
{
  va_list varargs;

  va_start (varargs, format);
  GLOG_CAT_LEVEL_LOG_valist (cat, GLOG_LEVEL_ERROR, NULL, format, varargs);
  va_end (varargs);
}

static inline void
GLOG_CAT_WARNING (GLogCategory * cat, const char *format, ...)
{
  va_list varargs;

  va_start (varargs, format);
  GLOG_CAT_LEVEL_LOG_valist (cat, GLOG_LEVEL_WARNING, NULL, format, varargs);
  va_end (varargs);
}

static inline void
GLOG_CAT_INFO (GLogCategory * cat, const char *format, ...)
{
  va_list varargs;

  va_start (varargs, format);
  GLOG_CAT_LEVEL_LOG_valist (cat, GLOG_LEVEL_INFO, NULL, format, varargs);
  va_end (varargs);
}

static inline void
GLOG_CAT_DEBUG (GLogCategory * cat, const char *format, ...)
{
  va_list varargs;

  va_start (varargs, format);
  GLOG_CAT_LEVEL_LOG_valist (cat, GLOG_LEVEL_DEBUG, NULL, format, varargs);
  va_end (varargs);
}

static inline void
GLOG_CAT_LOG (GLogCategory * cat, const char *format, ...)
{
  va_list varargs;

  va_start (varargs, format);
  GLOG_CAT_LEVEL_LOG_valist (cat, GLOG_LEVEL_LOG, NULL, format, varargs);
  va_end (varargs);
}

static inline void
GLOG_ERROR_OBJECT (gpointer obj, const char *format, ...)
{
  va_list varargs;

  va_start (varargs, format);
  GLOG_CAT_LEVEL_LOG_valist (GLOG_CAT_DEFAULT, GLOG_LEVEL_ERROR, obj, format,
      varargs);
  va_end (varargs);
}

static inline void
GLOG_WARNING_OBJECT (gpointer obj, const char *format, ...)
{
  va_list varargs;

  va_start (varargs, format);
  GLOG_CAT_LEVEL_LOG_valist (GLOG_CAT_DEFAULT, GLOG_LEVEL_WARNING, obj, format,
      varargs);
  va_end (varargs);
}

static inline void
GLOG_INFO_OBJECT (gpointer obj, const char *format, ...)
{
  va_list varargs;

  va_start (varargs, format);
  GLOG_CAT_LEVEL_LOG_valist (GLOG_CAT_DEFAULT, GLOG_LEVEL_INFO, obj, format,
      varargs);
  va_end (varargs);
}

static inline void
GLOG_DEBUG_OBJECT (gpointer obj, const char *format, ...)
{
  va_list varargs;

  va_start (varargs, format);
  GLOG_CAT_LEVEL_LOG_valist (GLOG_CAT_DEFAULT, GLOG_LEVEL_DEBUG, obj, format,
      varargs);
  va_end (varargs);
}

static inline void
GLOG_LOG_OBJECT (gpointer obj, const char *format, ...)
{
  va_list varargs;

  va_start (varargs, format);
  GLOG_CAT_LEVEL_LOG_valist (GLOG_CAT_DEFAULT, GLOG_LEVEL_LOG, obj, format,
      varargs);
  va_end (varargs);
}

static inline void
GLOG_ERROR (const char *format, ...)
{
  va_list varargs;

  va_start (varargs, format);
  GLOG_CAT_LEVEL_LOG_valist (GLOG_CAT_DEFAULT, GLOG_LEVEL_ERROR, NULL, format,
      varargs);
  va_end (varargs);
}

static inline void
GLOG_WARNING (const char *format, ...)
{
  va_list varargs;

  va_start (varargs, format);
  GLOG_CAT_LEVEL_LOG_valist (GLOG_CAT_DEFAULT, GLOG_LEVEL_WARNING, NULL, format,
      varargs);
  va_end (varargs);
}

static inline void
GLOG_INFO (const char *format, ...)
{
  va_list varargs;

  va_start (varargs, format);
  GLOG_CAT_LEVEL_LOG_valist (GLOG_CAT_DEFAULT, GLOG_LEVEL_INFO, NULL, format,
      varargs);
  va_end (varargs);
}

static inline void
GLOG_DEBUG (const char *format, ...)
{
  va_list varargs;

  va_start (varargs, format);
  GLOG_CAT_LEVEL_LOG_valist (GLOG_CAT_DEFAULT, GLOG_LEVEL_DEBUG, NULL, format,
      varargs);
  va_end (varargs);
}

static inline void
GLOG_LOG (const char *format, ...)
{
  va_list varargs;

  va_start (varargs, format);
  GLOG_CAT_LEVEL_LOG_valist (GLOG_CAT_DEFAULT, GLOG_LEVEL_LOG, NULL,
      format, varargs);
  va_end (varargs);
}


#else /* GLOG_DISABLE_LOGGING */


static inline void
GLOG_CAT_LEVEL_LOG (GLogCategory * cat, GLogLevel level,
    gpointer object, const char *format, ...)
{
}

static inline void
GLOG_CAT_ERROR_OBJECT (GLogCategory * cat, gpointer obj, const char *format,
    ...)
{
}

static inline void
GLOG_CAT_WARNING_OBJECT (GLogCategory * cat, gpointer obj,
    const char *format, ...)
{
}

static inline void
GLOG_CAT_INFO_OBJECT (GLogCategory * cat, gpointer obj, const char *format,
    ...)
{
}

static inline void
GLOG_CAT_DEBUG_OBJECT (GLogCategory * cat, gpointer obj, const char *format,
    ...)
{
}

static inline void
GLOG_CAT_LOG_OBJECT (GLogCategory * cat, gpointer obj, const char *format,
    ...)
{
}

static inline void
GLOG_CAT_ERROR (GLogCategory * cat, const char *format, ...)
{
}

static inline void
GLOG_CAT_WARNING (GLogCategory * cat, const char *format, ...)
{
}

static inline void
GLOG_CAT_INFO (GLogCategory * cat, const char *format, ...)
{
}

static inline void
GLOG_CAT_DEBUG (GLogCategory * cat, const char *format, ...)
{
}

static inline void
GLOG_CAT_LOG (GLogCategory * cat, const char *format, ...)
{
}

static inline void
GLOG_ERROR_OBJECT (gpointer obj, const char *format, ...)
{
}

static inline void
GLOG_WARNING_OBJECT (gpointer obj, const char *format, ...)
{
}

static inline void
GLOG_INFO_OBJECT (gpointer obj, const char *format, ...)
{
}

static inline void
GLOG_DEBUG_OBJECT (gpointer obj, const char *format, ...)
{
}

static inline void
GLOG_LOG_OBJECT (gpointer obj, const char *format, ...)
{
}

static inline void
GLOG_ERROR (const char *format, ...)
{
}

static inline void
GLOG_WARNING (const char *format, ...)
{
}

static inline void
GLOG_INFO (const char *format, ...)
{
}

static inline void
GLOG_DEBUG (const char *format, ...)
{
}

static inline void
GLOG_LOG (const char *format, ...)
{
}


#endif /* GLOG_DISABLE_LOGGING */


G_END_DECLS

#endif /* __GLOGINLINEVARARGS_H__ */
