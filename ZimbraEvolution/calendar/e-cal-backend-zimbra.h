/* 
 * Copyright (C) 2006-2007 Zimbra, Inc.
 *
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
 */

#ifndef E_CAL_BACKEND_ZIMBRA_H
#define E_CAL_BACKEND_ZIMBRA_H

#include <libedata-cal/e-cal-backend-sync.h>
#include <libezimbra/e-zimbra-connection.h>

G_BEGIN_DECLS

#define E_TYPE_CAL_BACKEND_ZIMBRA            (e_cal_backend_zimbra_get_type ())
#define E_CAL_BACKEND_ZIMBRA(obj)            (G_TYPE_CHECK_INSTANCE_CAST ((obj), E_TYPE_CAL_BACKEND_ZIMBRA,	ECalBackendZimbra))
#define E_CAL_BACKEND_ZIMBRA_CLASS(klass)    (G_TYPE_CHECK_CLASS_CAST ((klass), E_TYPE_CAL_BACKEND_ZIMBRA,	ECalBackendZimbraClass))
#define E_IS_CAL_BACKEND_ZIMBRA(obj)         (G_TYPE_CHECK_INSTANCE_TYPE ((obj), E_TYPE_CAL_BACKEND_ZIMBRA))
#define E_IS_CAL_BACKEND_ZIMBRA_CLASS(klass) (G_TYPE_CHECK_CLASS_TYPE ((klass), E_TYPE_CAL_BACKEND_ZIMBRA))

typedef struct _ECalBackendZimbra        ECalBackendZimbra;
typedef struct _ECalBackendZimbraClass   ECalBackendZimbraClass;

typedef struct _ECalBackendZimbraPrivate ECalBackendZimbraPrivate;

struct _ECalBackendZimbra {
	ECalBackendSync backend;

	/* Private data */
	ECalBackendZimbraPrivate *priv;
};

struct _ECalBackendZimbraClass {
	ECalBackendSyncClass parent_class;
};

GType   e_cal_backend_zimbra_get_type (void);
EZimbraConnection* e_cal_backend_zimbra_get_connection (ECalBackendZimbra *cbz);
GHashTable* e_cal_backend_zimbra_get_categories_by_id (ECalBackendZimbra *cbz);
GHashTable* e_cal_backend_zimbra_get_categories_by_name (ECalBackendZimbra *cbz);
icaltimezone* e_cal_backend_zimbra_get_default_zone (ECalBackendZimbra *cbz);
icaltimezone* e_cal_backend_zimbra_get_zone( ECalBackendZimbra* cbz, const char * tzid );
const char * e_cal_backend_zimbra_peek_account( ECalBackendZimbra* cbz );
void    e_cal_backend_zimbra_notify_error_code (ECalBackendZimbra *cbz, EZimbraConnectionStatus status);
const char * e_cal_backend_zimbra_get_local_attachments_store (ECalBackendZimbra *cbz);


G_END_DECLS


#endif
