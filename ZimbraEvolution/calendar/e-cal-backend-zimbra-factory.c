/* -*- Mode: C; tab-width: 4; indent-tabs-mode: t; c-basic-offset: 8 -*- */
/*
 *
 * Author:
 *   Scott Herscher (scott.herscher@zimbra.com)
 *
 * Copyright (C) 2006, Zimbra, Inc.
 */

#ifdef HAVE_CONFIG_H
#include <config.h>
#endif

#include <string.h>

#include "e-cal-backend-zimbra-factory.h"
#include "e-cal-backend-zimbra.h"
#include <libezimbra/e-zimbra-log.h>

typedef struct
{
	ECalBackendFactory            parent_object;
} ECalBackendZimbraFactory;

typedef struct
{
	ECalBackendFactoryClass parent_class;
} ECalBackendZimbraFactoryClass;

static void
e_cal_backend_zimbra_factory_instance_init (ECalBackendZimbraFactory *factory)
{
}

static const char *
_get_protocol (ECalBackendFactory *factory)
{
	return "zimbra";
}

static ECalBackend*
_todos_new_backend (ECalBackendFactory *factory, ESource *source)
{
	return g_object_new( e_cal_backend_zimbra_get_type (), "source", source, "kind", ICAL_VTODO_COMPONENT, NULL );
}

static icalcomponent_kind
_todos_get_kind (ECalBackendFactory *factory)
{
	return ICAL_VTODO_COMPONENT;
}

static ECalBackend*
_events_new_backend (ECalBackendFactory *factory, ESource *source)
{
	return g_object_new( e_cal_backend_zimbra_get_type (), "source", source, "kind", ICAL_VEVENT_COMPONENT, NULL );
}

static icalcomponent_kind
_events_get_kind (ECalBackendFactory *factory)
{
	return ICAL_VEVENT_COMPONENT;
}

static void
todos_backend_factory_class_init (ECalBackendZimbraFactoryClass *klass)
{
	E_CAL_BACKEND_FACTORY_CLASS (klass)->get_protocol = _get_protocol;
	E_CAL_BACKEND_FACTORY_CLASS (klass)->get_kind     = _todos_get_kind;
	E_CAL_BACKEND_FACTORY_CLASS (klass)->new_backend  = _todos_new_backend;
}

static void
events_backend_factory_class_init (ECalBackendZimbraFactoryClass *klass)
{
	E_CAL_BACKEND_FACTORY_CLASS (klass)->get_protocol = _get_protocol;
	E_CAL_BACKEND_FACTORY_CLASS (klass)->get_kind     = _events_get_kind;
	E_CAL_BACKEND_FACTORY_CLASS (klass)->new_backend  = _events_new_backend;
}

static GType
events_backend_factory_get_type (GTypeModule *module)
{
	GType type;

	GTypeInfo info =
	{
		sizeof (ECalBackendZimbraFactoryClass),
		NULL, /* base_class_init */
		NULL, /* base_class_finalize */
		(GClassInitFunc)  events_backend_factory_class_init,
		NULL, /* class_finalize */
		NULL, /* class_data */
		sizeof (ECalBackend),
		0,    /* n_preallocs */
		(GInstanceInitFunc) e_cal_backend_zimbra_factory_instance_init
	};

	type = g_type_module_register_type( module, E_TYPE_CAL_BACKEND_FACTORY, "ECalBackendZimbraEventsFactory", &info, 0 );

	return type;
}

static GType
todos_backend_factory_get_type (GTypeModule *module)
{
	GType type;

	GTypeInfo info =
	{
		sizeof (ECalBackendZimbraFactoryClass),
		NULL, /* base_class_init */
		NULL, /* base_class_finalize */
		(GClassInitFunc)  todos_backend_factory_class_init,
		NULL, /* class_finalize */
		NULL, /* class_data */
		sizeof (ECalBackend),
		0,    /* n_preallocs */
		(GInstanceInitFunc) e_cal_backend_zimbra_factory_instance_init
	};

	type = g_type_module_register_type( module, E_TYPE_CAL_BACKEND_FACTORY, "ECalBackendZimbraTodosFactory", &info, 0 );

	return type;
}

static GType zimbra_types[2];

void
eds_module_initialize (GTypeModule *module)
{
	zimbra_types[0] = events_backend_factory_get_type (module);

	glog_init();
}


void
eds_module_shutdown   (void)
{
}


void
eds_module_list_types (const GType **types, int *num_types)
{
	*types = zimbra_types;
	*num_types = 1;
}
