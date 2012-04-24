/*
 * Copyright (c) VMware, Inc. [1998 – 2011]. All Rights Reserved.
 *
 * For more information, see –
 * http://vmweb.vmware.com/legal/corporate/VMwareCopyrightPatentandTrademarkNotices.pdf
 */

#ifndef _NGX_HTTP_ZM_SSO_INCLUDED_
#define _NGX_HTTP_ZM_SSO_INCLUDED_


#include <ngx_config.h>
#include <ngx_core.h>
#include <ngx_http.h>

typedef struct {
    ngx_uint_t  type;                     /* the authentication type */
    ngx_url_t  *redirect_url;             /* if only port is provided, just redirect to $host:port */
    ngx_str_t   redirect_schema;          /* https or http, https is the default */
    ngx_int_t   host_index;               /* the index of $host */
    ngx_int_t   ssl_client_verify_index;  /* the index of $ssl_client_verify */
    ngx_int_t   ssl_client_s_dn_index;    /* the index of $ssl_client_s_dn */
} ngx_http_zm_sso_loc_conf_t;

typedef struct {
    ngx_pool_t            *pool;
    ngx_log_t             *log;
    ngx_http_request_t    *r;
    ngx_flag_t             isAdmin;
    ngx_str_t              zm_auth_token;
} ngx_http_zm_sso_ctx_t;

/* SSO type */
#define NGX_ZM_SSO_CERTAUTH       1           /* client cert authentication */
#define NGX_ZM_SSO_CERTAUTH_ADMIN 2           /* client cert authentication for admin console */


extern ngx_module_t ngx_http_zm_sso_module;

#endif
