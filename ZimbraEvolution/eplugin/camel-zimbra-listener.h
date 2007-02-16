/*
 * Copyright 2006-2007 Zimbra, Inc.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of version 2 of the GNU General Public
 * License as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 */

#ifndef CAMEL_ZIMBRA_LISTENER_H
#define CAMEL_ZIMBRA_LISTENER_H

#include <libedataserver/e-account-list.h>
#include<libedataserver/e-source.h>
#include<libedataserver/e-source-list.h>
#include <camel/camel-url.h>
                         
G_BEGIN_DECLS

#define CAMEL_TYPE_ZIMBRA_LISTENER            (camel_zimbra_listener_get_type ())
#define CAMEL_ZIMBRA_LISTENER(obj)            (G_TYPE_CHECK_INSTANCE_CAST ((obj), CAMEL_TYPE_ZIMBRA_LISTENER, CamelZimbraListener))
#define CAMEL_ZIMBRA_LISTENER_CLASS(klass)    (G_TYPE_CHECK_CLASS_CAST ((klass), CAMEL_TYPE_ZIMBRA_LISTENER,  CamelZIMBRAListenerClass))
#define CAMEL_IS_ZIMBRALISTENER(obj)         (G_TYPE_CHECK_INSTANCE_TYPE ((obj), CAMEL_TYPE_ZIMBRA_LISTENER))
#define CAMEL_IS_ZIMBRA_LISTENER_CLASS(klass) (G_TYPE_CHECK_CLASS_TYPE ((obj), CAMEL_TYPE_ZIMBRA_LISTENER))

typedef struct _CamelZimbraListener CamelZimbraListener;
typedef struct _CamelZimbraListenerClass CamelZimbraListenerClass;
typedef struct _CamelZimbraListenerPrivate CamelZimbraListenerPrivate;

struct _CamelZimbraListener
{
       GObject							parent;
       CamelZimbraListenerPrivate	*	priv;
};


struct _CamelZimbraListenerClass
{
       GObjectClass parent_class;     
};


GType
camel_zimbra_listener_get_type (void);

CamelZimbraListener*
camel_zimbra_listener_new (void);

G_END_DECLS


#endif
