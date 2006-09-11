/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 * 
 * Authors: Scott Herscher <scott.herscher@zimbra.com>
 * 
 * Copyright (c) 2006 Zimbra, Inc.
 * 
 */


#ifndef __GLOG_H__
#define __GLOG_H__

#include <glib.h>

#include <glog/glogversion.h>

#include <glog/glogcategory.h>
#include <glog/glogmain.h>
#include <glog/glogprintf.h>
#if defined (G_HAVE_ISO_VARARGS)
#  include <glog/glogisovarargs.h>
#elif defined (G_HAVE_GNUC_VARARGS)
#  include <glog/gloggnucvarargs.h>
#else
#  include <glog/gloginlinevarargs.h>
#endif

#endif /* __GLOG_H__ */
