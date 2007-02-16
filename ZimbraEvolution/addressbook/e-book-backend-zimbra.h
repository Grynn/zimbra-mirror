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
                                                                                                                          
#ifndef __E_BOOK_BACKEND_ZIMBRA_H__
#define __E_BOOK_BACKEND_ZIMBRA_H__
                                                                                                                             
#include <libedata-book/e-book-backend-sync.h>
                                                                                                                             
#define E_TYPE_BOOK_BACKEND_ZIMBRA        (e_book_backend_zimbra_get_type ())
#define E_BOOK_BACKEND_ZIMBRA(o)          (G_TYPE_CHECK_INSTANCE_CAST ((o), E_TYPE_BOOK_BACKEND_ZIMBRA, EBookBackendZimbra))
#define E_BOOK_BACKEND_ZIMBRA_CLASS(k)    (G_TYPE_CHECK_CLASS_CAST((k), E_TYPE_BOOK_BACKEND_ZIMBRA, EBookBackendZimbraClass))
#define E_IS_BOOK_BACKEND_ZIMBRA(o)       (G_TYPE_CHECK_INSTANCE_TYPE ((o), E_TYPE_BOOK_BACKEND_ZIMBRA))
#define E_IS_BOOK_BACKEND_ZIMBRA_CLASS(k) (G_TYPE_CHECK_CLASS_TYPE ((k), E_TYPE_BOOK_BACKEND_ZIMBRA))
#define E_BOOK_BACKEND_ZIMBRA_GET_CLASS(k) (G_TYPE_INSTANCE_GET_CLASS ((obj), E_TYPE_BOOK_BACKEND_ZIMBRA, EBookBackendZimbraClass))

typedef struct _EBookBackendZimbraPrivate EBookBackendZimbraPrivate;
                                                                                                                             
typedef struct
{
	EBookBackend					parent_object;
	EBookBackendZimbraPrivate	*	priv;
} EBookBackendZimbra;
                                                                                                                             
typedef struct
{
	EBookBackendClass parent_class;
} EBookBackendZimbraClass;
                                                                                                                             
EBookBackend*
e_book_backend_zimbra_new(void);


GType
e_book_backend_zimbra_get_type(void);
                                                                                                                             
#endif
