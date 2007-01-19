/* -*- Mode: C; tab-width: 4; indent-tabs-mode: t; c-basic-offset: 8 -*- */

/* e-book-backend-zimbra-factory.c - Zimbra contact backend factory.
 *
 * Copyright (C) 2006 Zimbra, Inc.
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
 * Authors: Scott Herscher <scott.herscher@zimbra.com>
 */

#ifdef HAVE_CONFIG_H
#include <config.h>
#endif

#include <libedataserver/e-data-server-module.h>
#include <libedata-book/e-book-backend-factory.h>
#include "e-book-backend-zimbra.h"
#include <libezimbra/e-zimbra-log.h>

E_BOOK_BACKEND_FACTORY_SIMPLE(zimbra, Zimbra, e_book_backend_zimbra_new)

static GType zimbra_type;

void
eds_module_initialize(GTypeModule *module)
{
	zimbra_type = _zimbra_factory_get_type(module);

	glog_init();
}


void
eds_module_shutdown(void)
{
}


void
eds_module_list_types(const GType **types, int *num_types)
{
	*types = &zimbra_type;
	*num_types = 1;
}
