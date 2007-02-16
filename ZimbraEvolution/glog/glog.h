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
