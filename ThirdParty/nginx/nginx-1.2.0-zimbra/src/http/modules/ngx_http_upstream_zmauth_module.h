/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 Zimbra Software, LLC.
 *
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

#if !defined(_NGX_HTTP_UPSTREAM_ZMAUTH_H_INCLUDED_)
#define _NGX_HTTP_UPSTREAM_ZMAUTH_H_INCLUDED_

typedef enum {
    zmroutetype_fallback = 0,
    zmroutetype_authtoken,
    zmroutetype_rest,
    zmroutetype_activesync,
    zmroutetype_caldav
} zmroutetype_t;

typedef struct {
    /* the round robin data must be first */
    ngx_http_upstream_rr_peer_data_t    rrp;

    /* IPHASH */
    ngx_uint_t                          hash;
#if (NGX_HAVE_INET6)
    int                                 family;
    u_char                              addr[16];   /* both AF_INET and AF_INET6 */
#else
    u_char                              addr[4];    /* AF_INET addr */
#endif
    u_char                              porth;      /* AF_INET port-hi */
    u_char                              portl;      /* AF_INET port-lo */
    u_char                              tries;

    ngx_event_get_peer_pt               get_rr_peer;
    zmroutetype_t                       zmroutetype;
    ngx_addr_t                          zmpeer;
} ngx_http_upstream_zmauth_peer_data_t;

typedef struct {
    ngx_flag_t           dummy; /* just for place holder */
} ngx_http_upstream_zmauth_srv_conf_t;

typedef void (*ngx_http_upstream_zmauth_handler_pt)(ngx_http_request_t *r);

typedef struct {
    ngx_pool_t                                 *pool;
    ngx_log_t                                  *log;
    ngx_zm_lookup_work_t                       *work;
    ngx_str_t                                   err;
    ngx_http_upstream_zmauth_peer_data_t       *zmp;
    void (*connect) (ngx_http_request_t *, ngx_http_upstream_t *);
} ngx_http_upstream_zmauth_ctx_t;

ngx_int_t ngx_http_upstream_init_zmauth(ngx_conf_t *cf,
        ngx_http_upstream_srv_conf_t *us);

ngx_int_t ngx_http_upstream_init_admin_zmauth(ngx_conf_t *cf,
        ngx_http_upstream_srv_conf_t *us);

#endif

