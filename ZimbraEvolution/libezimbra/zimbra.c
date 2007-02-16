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

#include "zimbra.h"
#include "zimbra-version.h"

#include <libxml/parser.h>
#include <libxml/tree.h>

#include <glog/glog.h>

static gpointer
_libezimbra_init (gpointer data)
{
#if 0
    char *path;
    gboolean init_camel;

    init_camel = GPOINTER_TO_UINT (data);

    /* do we need this? */
    if (!g_thread_supported ()) {
        g_thread_init (NULL);
    }

    if (init_camel) {
        path = g_build_filename (g_get_home_dir (),
                                 ".evolution", "zimbra", NULL);

        if (camel_init (path, FALSE)) {
            return GUINT_TO_POINTER (FALSE);
        }

        g_free (path);

        camel_provider_init ();
    }

    LIBXML_TEST_VERSION;

    glog_init ();

    GLOG_DEBUG ("All set!");
// YO #if EAPI_CHECK_VERSION (2,4)
#if 0
    GLOG_DEBUG ("Using 2.4 API");
#endif
#endif

    return GUINT_TO_POINTER (TRUE);
}

gboolean
libezimbra_init (gboolean init_camel)
{
    static GOnce initialize = G_ONCE_INIT;

    g_once (&initialize, _libezimbra_init, GUINT_TO_POINTER (init_camel));

    return GPOINTER_TO_UINT (initialize.retval);
}
