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

#ifndef __GLOGGNUCVARARGS_H__
#define __GLOGGNUCVARARGS_H__

#include <glog/glogmacros.h>
#include <glog/glogmain.h>


G_BEGIN_DECLS

/* stupidity check */
#ifndef G_HAVE_GNUC_VARARGS
#  error "You don't have G_HAVE_GNUC_VARARGS defined, so don't include this header"
#endif


#ifndef GLOG_DISABLE_LOGGING


#ifdef G_HAVE_GNUC_VARARGS
#define GLOG_CAT_LEVEL_LOG(cat,level,object,args...) \
  glog_log ((cat), (level), __FILE__, G_STRFUNC, __LINE__, (object), ##args )

#define GLOG_CAT_ERROR_OBJECT(cat,obj,args...)	GLOG_CAT_LEVEL_LOG (cat, GLOG_LEVEL_ERROR,   obj,  ##args )
#define GLOG_CAT_WARNING_OBJECT(cat,obj,args...)	GLOG_CAT_LEVEL_LOG (cat, GLOG_LEVEL_WARNING, obj,  ##args )
#define GLOG_CAT_INFO_OBJECT(cat,obj,args...)	GLOG_CAT_LEVEL_LOG (cat, GLOG_LEVEL_INFO,    obj,  ##args )
#define GLOG_CAT_DEBUG_OBJECT(cat,obj,args...)	GLOG_CAT_LEVEL_LOG (cat, GLOG_LEVEL_DEBUG,   obj,  ##args )
#define GLOG_CAT_LOG_OBJECT(cat,obj,args...)	GLOG_CAT_LEVEL_LOG (cat, GLOG_LEVEL_LOG,     obj,  ##args )

#define GLOG_CAT_ERROR(cat,args...)		GLOG_CAT_LEVEL_LOG (cat, GLOG_LEVEL_ERROR,   NULL, ##args )
#define GLOG_CAT_WARNING(cat,args...)		GLOG_CAT_LEVEL_LOG (cat, GLOG_LEVEL_WARNING, NULL, ##args )
#define GLOG_CAT_INFO(cat,args...)		GLOG_CAT_LEVEL_LOG (cat, GLOG_LEVEL_INFO,    NULL, ##args )
#define GLOG_CAT_DEBUG(cat,args...)		GLOG_CAT_LEVEL_LOG (cat, GLOG_LEVEL_DEBUG,   NULL, ##args )
#define GLOG_CAT_LOG(cat,args...)		GLOG_CAT_LEVEL_LOG (cat, GLOG_LEVEL_LOG,     NULL, ##args )

#define GLOG_ERROR_OBJECT(obj,args...)	GLOG_CAT_LEVEL_LOG (&(GLOG_CAT_DEFAULT), GLOG_LEVEL_ERROR,   obj,  ##args )
#define GLOG_WARNING_OBJECT(obj,args...)	GLOG_CAT_LEVEL_LOG (&(GLOG_CAT_DEFAULT), GLOG_LEVEL_WARNING, obj,  ##args )
#define GLOG_INFO_OBJECT(obj,args...)	GLOG_CAT_LEVEL_LOG (&(GLOG_CAT_DEFAULT), GLOG_LEVEL_INFO,    obj,  ##args )
#define GLOG_DEBUG_OBJECT(obj,args...)	GLOG_CAT_LEVEL_LOG (&(GLOG_CAT_DEFAULT), GLOG_LEVEL_DEBUG,   obj,  ##args )
#define GLOG_LOG_OBJECT(obj,args...)	GLOG_CAT_LEVEL_LOG (&(GLOG_CAT_DEFAULT), GLOG_LEVEL_LOG,     obj,  ##args )

#define GLOG_ERROR(args...)		GLOG_CAT_LEVEL_LOG (&(GLOG_CAT_DEFAULT), GLOG_LEVEL_ERROR,   NULL, ##args )
#define GLOG_WARNING(args...)		GLOG_CAT_LEVEL_LOG (&(GLOG_CAT_DEFAULT), GLOG_LEVEL_WARNING, NULL, ##args )
#define GLOG_INFO(args...)		GLOG_CAT_LEVEL_LOG (&(GLOG_CAT_DEFAULT), GLOG_LEVEL_INFO,    NULL, ##args )
#define GLOG_DEBUG(args...)		GLOG_CAT_LEVEL_LOG (&(GLOG_CAT_DEFAULT), GLOG_LEVEL_DEBUG,   NULL, ##args )
#define GLOG_LOG(args...)		GLOG_CAT_LEVEL_LOG (&(GLOG_CAT_DEFAULT), GLOG_LEVEL_LOG,     NULL, ##args )


#else /* !GLOG_DISABLE_LOGGING */


#define GLOG_CAT_LEVEL_LOG(cat,level,args...)		/* NOP */

#define GLOG_CAT_ERROR_OBJECT(args...)			/* NOP */
#define GLOG_CAT_WARNING_OBJECT(args...)		/* NOP */
#define GLOG_CAT_INFO_OBJECT(args...)			/* NOP */
#define GLOG_CAT_DEBUG_OBJECT(args...)			/* NOP */
#define GLOG_CAT_LOG_OBJECT(args...)			/* NOP */

#define GLOG_CAT_ERROR(args...)				/* NOP */
#define GLOG_CAT_WARNING(args...)			/* NOP */
#define GLOG_CAT_INFO(args...)				/* NOP */
#define GLOG_CAT_DEBUG(args...)				/* NOP */
#define GLOG_CAT_LOG(args...)				/* NOP */

#define GLOG_ERROR_OBJECT(args...)			/* NOP */
#define GLOG_WARNING_OBJECT(args...)			/* NOP */
#define GLOG_INFO_OBJECT(args...)			/* NOP */
#define GLOG_DEBUG_OBJECT(args...)			/* NOP */
#define GLOG_LOG_OBJECT(args...)			/* NOP */

#define GLOG_ERROR(args...)				/* NOP */
#define GLOG_WARNING(args...)				/* NOP */
#define GLOG_INFO(args...)				/* NOP */
#define GLOG_DEBUG(args...)				/* NOP */
#define GLOG_LOG(args...)				/* NOP */


#endif /* !GLOG_DISABLE_LOGGING */


G_END_DECLS

#endif /* __GLOGGNUCVARARGS_H__ */
