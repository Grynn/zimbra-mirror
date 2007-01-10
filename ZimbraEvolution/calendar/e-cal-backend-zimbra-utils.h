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
 * Copyright 2006, Zimbra, Inc.
 *
 */

#ifndef E_CAL_BACKEND_ZIMBRA_UTILS_H
#define E_CAL_BACKEND_ZIMBRA_UTILS_H

#include <libezimbra/e-zimbra-connection.h>
#include <libecal/e-cal-component.h>
#include <e-cal-backend-zimbra.h>

G_BEGIN_DECLS


#define ZIMBRA_X_APPT_ID	"X-ZAPPTID"
#define ZIMBRA_X_REV_ID		"X-ZREVID"

#define ZIMBRA_EVENT_TYPE_ID "@4:"
#define ZIMBRA_TODO_TYPE_ID "@3:"

/*
 * Items management
 */
EZimbraItem*
e_zimbra_item_new_from_cal_component
	(
	const char			*	folder_id,
	ECalBackendZimbra	*	cbz,
	ECalComponent		*	comp
	);


EZimbraItem*
e_zimbra_item_new_from_cal_components
	(
	const char			*	folder_id,
	ECalBackendZimbra	*	cbz,
	GSList				*	components
	);


ECalComponent*
e_zimbra_item_to_cal_component
	(
	EZimbraItem			*	item,
	EZimbraItem			*	parent,
	ECalBackendZimbra	*	cbz
	);


GSList*
e_zimbra_item_to_cal_components
	(
	EZimbraItem			*	item,
	ECalBackendZimbra	*	cbz
	);


void          e_zimbra_item_set_changes (EZimbraItem *item, EZimbraItem *cached_item);


/*
 * Component related utility functions
 */
  
const char *
e_cal_component_get_zimbra_id
	(
	ECalComponent	*	comp
	);


const char *
e_cal_component_get_zimbra_data
	(
	ECalComponent	*	comp,
	const char		*	prop_name
	);


G_END_DECLS


#endif
