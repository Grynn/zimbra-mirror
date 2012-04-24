/*
 * Copyright (c) VMware, Inc. [1998 – 2011]. All Rights Reserved.
 *
 * For more information, see –
 * http://vmweb.vmware.com/legal/corporate/VMwareCopyrightPatentandTrademarkNotices.pdf
 */

#include <ngx_config.h>
#include <ngx_core.h>
#include <ngx_http.h>
#include <ngx_http_zm_sso_module.h>
#include <ngx_zm_lookup.h>

ngx_module_t ngx_http_zm_sso_module;

static ngx_conf_enum_t  ngx_http_zm_sso_types[] = {
    { ngx_string("certauth"), NGX_ZM_SSO_CERTAUTH },
    { ngx_string("certauth_admin"), NGX_ZM_SSO_CERTAUTH_ADMIN},
    { ngx_null_string, 0 }
};


static ngx_int_t ngx_http_zm_sso_handler(ngx_http_request_t *r);
static char * ngx_http_zm_sso(ngx_conf_t *cf, ngx_command_t *cmd,
        void *conf);
static char * ngx_http_zm_sso_redirect_url(ngx_conf_t *cf,
        ngx_command_t *cmd, void *conf);
static void * ngx_http_zm_sso_create_loc_conf(ngx_conf_t *cf);
static char * ngx_http_zm_sso_merge_loc_conf(ngx_conf_t *cf,
        void *parent, void *child);
static ngx_int_t ngx_http_zm_sso_cert_auth(ngx_http_request_t *r,
        ngx_zm_lookup_work_t * w);
static ngx_int_t ngx_http_zm_sso_set_auth_token_and_redirect(
        ngx_http_request_t *r, ngx_str_t token, ngx_flag_t isAdmin);
static void ngx_http_zm_sso_finalize_request(ngx_http_request_t *r,
        ngx_int_t rc);
static void ngx_http_zm_sso_cleanup (void *data);

static void ngx_http_zm_sso_cert_auth_on_success (ngx_zm_lookup_work_t *w);
static void ngx_http_zm_sso_cert_auth_on_failure (ngx_zm_lookup_work_t *w);


static void ngx_http_zm_sso_rd_check_broken_connection(ngx_http_request_t *r);
static void ngx_http_zm_sso_wr_check_broken_connection(ngx_http_request_t *r);
static void ngx_http_zm_sso_check_broken_connection(ngx_http_request_t *r, ngx_event_t *ev);

static ngx_command_t  ngx_http_zm_sso_commands[] = {

    { ngx_string("zm_sso"),
      NGX_HTTP_LOC_CONF|NGX_CONF_TAKE1,
      ngx_http_zm_sso,
      NGX_HTTP_LOC_CONF_OFFSET,
      offsetof(ngx_http_zm_sso_loc_conf_t, type),
      &ngx_http_zm_sso_types },

    { ngx_string("zm_sso_redirect_url"),
      NGX_HTTP_SRV_CONF|NGX_HTTP_LOC_CONF|NGX_CONF_TAKE1,
      ngx_http_zm_sso_redirect_url,
      NGX_HTTP_LOC_CONF_OFFSET,
      0,
      NULL },

    { ngx_string("zm_sso_redirect_schema"),
      NGX_HTTP_SRV_CONF|NGX_HTTP_LOC_CONF|NGX_CONF_TAKE1,
      ngx_conf_set_str_slot,
      NGX_HTTP_LOC_CONF_OFFSET,
      offsetof(ngx_http_zm_sso_loc_conf_t, redirect_schema),
      NULL },

      ngx_null_command
};


static ngx_http_module_t  ngx_http_zm_sso_module_ctx = {
    NULL,                             /* pre configuration */
    NULL,                             /* post configuration */

    NULL,                             /* create main configuration */
    NULL,                             /* init main configuration */

    NULL,                             /* create server configuration */
    NULL,                             /* merge server configuration */

    ngx_http_zm_sso_create_loc_conf,  /* create location configuration */
    ngx_http_zm_sso_merge_loc_conf    /* merge location configuration */
};


ngx_module_t  ngx_http_zm_sso_module = {
    NGX_MODULE_V1,
    &ngx_http_zm_sso_module_ctx,           /* module context */
    ngx_http_zm_sso_commands,              /* module directives */
    NGX_HTTP_MODULE,                       /* module type */
    NULL,                                  /* init master */
    NULL,                                  /* init module */
    NULL,                                  /* init process */
    NULL,                                  /* init thread */
    NULL,                                  /* exit thread */
    NULL,                                  /* exit process */
    NULL,                                  /* exit master */
    NGX_MODULE_V1_PADDING
};

static void *
ngx_http_zm_sso_create_loc_conf(ngx_conf_t *cf)
{
    ngx_http_zm_sso_loc_conf_t  *conf;

    conf = ngx_pcalloc(cf->pool, sizeof(ngx_http_zm_sso_loc_conf_t));
    if (conf == NULL) {
        return NULL;
    }

    conf->redirect_url = ngx_pcalloc(cf->pool, sizeof(ngx_url_t));
    if (conf->redirect_url == NULL) {
        return NULL;
    }

    ngx_str_null(&conf->redirect_schema);

    conf->type = NGX_CONF_UNSET_UINT;
    conf->host_index = NGX_CONF_UNSET;
    conf->ssl_client_s_dn_index = NGX_CONF_UNSET;
    conf->ssl_client_verify_index = NGX_CONF_UNSET;

    return conf;
}

static char *
ngx_http_zm_sso_merge_loc_conf(ngx_conf_t *cf, void *parent, void *child)
{
    ngx_http_zm_sso_loc_conf_t *prev = parent;
    ngx_http_zm_sso_loc_conf_t *conf = child;
    ngx_str_t         host_key;
    ngx_str_t         ssl_client_verify_key;
    ngx_str_t         ssl_client_s_dn_key;

    ngx_conf_merge_uint_value(conf->type, prev->type, NGX_ZM_SSO_CERTAUTH);

    ngx_conf_merge_str_value(conf->redirect_url->url,
                              prev->redirect_url->url, "443");

    ngx_conf_merge_str_value(conf->redirect_schema,
                              prev->redirect_schema, "https");

    conf->redirect_url->no_resolve = 1;
    conf->redirect_url->one_addr = 1;
    conf->redirect_url->listen = 1; /* with this option, the ngx_parse_url accepts
                                       only port case */
    if (ngx_parse_url(cf->pool, conf->redirect_url) != NGX_OK) {
        return NGX_CONF_ERROR;
    }

    if (conf->host_index == NGX_CONF_UNSET) {
        ngx_str_set(&host_key, "host");
        conf->host_index = ngx_http_get_variable_index(cf, &host_key);
    }

    if (conf->ssl_client_verify_index == NGX_CONF_UNSET) {
        ngx_str_set(&ssl_client_verify_key, "ssl_client_verify");
        conf->ssl_client_verify_index = ngx_http_get_variable_index(cf,
                &ssl_client_verify_key);
    }

    if (conf->ssl_client_s_dn_index == NGX_CONF_UNSET) {
        ngx_str_set(&ssl_client_s_dn_key, "ssl_client_s_dn");
        conf->ssl_client_s_dn_index = ngx_http_get_variable_index(cf,
                &ssl_client_s_dn_key);
    }

    return NGX_CONF_OK;
}

static char *
ngx_http_zm_sso(ngx_conf_t *cf, ngx_command_t *cmd, void *conf)
{
    ngx_http_core_loc_conf_t   *clcf;

    ngx_conf_set_enum_slot(cf, cmd, conf);

    clcf = ngx_http_conf_get_module_loc_conf(cf, ngx_http_core_module);

    clcf->handler = ngx_http_zm_sso_handler;

    return NGX_CONF_OK;
}

static char *
ngx_http_zm_sso_redirect_url(ngx_conf_t *cf, ngx_command_t *cmd, void *conf)
{
    ngx_str_t        *value;

    value = cf->args->elts;

    ngx_http_zm_sso_loc_conf_t * zlcf = conf;

    zlcf->redirect_url->url = value[1];

    return NGX_CONF_OK;
}

static ngx_int_t
ngx_http_zm_sso_handler(ngx_http_request_t *r)
{
    ngx_http_zm_sso_loc_conf_t   *zlcf;
    ngx_http_ssl_srv_conf_t      *hscf;
    ngx_zm_lookup_work_t         *w;

    zlcf = ngx_http_get_module_loc_conf(r, ngx_http_zm_sso_module);
    hscf = ngx_http_get_module_srv_conf(r, ngx_http_ssl_module);

    if (hscf->verify == 0) {
        /* ssl_verify_client is off */
        ngx_log_error(NGX_LOG_INFO, r->connection->log, 0,
                "client has to enable client cert auth when accessing this url. "
                "please set this server block's \"ssl_verify_client\" to \"on\" "
                "or \"optional\"");
        return NGX_HTTP_FORBIDDEN;
    }

    w = ngx_pcalloc (r->pool, sizeof(ngx_zm_lookup_work_t));
    if (w == NULL) {
        return NGX_HTTP_INTERNAL_SERVER_ERROR;
    }

    w->data = r;
    w->pool = r->pool;
    w->log = r->connection->log;

    if (zlcf->type == NGX_ZM_SSO_CERTAUTH || zlcf->type == NGX_ZM_SSO_CERTAUTH_ADMIN) {
        return ngx_http_zm_sso_cert_auth(r, w);

    } else {
        /* Should never execute here */
        return NGX_HTTP_INTERNAL_SERVER_ERROR;
    }
}

static ngx_int_t
ngx_http_zm_sso_redirect (ngx_http_request_t *r)
{
    ngx_http_zm_sso_loc_conf_t   *zlcf;
    ngx_http_variable_value_t    *host_var;
    ngx_str_t                     host_value;

    zlcf = ngx_http_get_module_loc_conf(r, ngx_http_zm_sso_module);
    ngx_table_elt_t * location = ngx_list_push(&r->headers_out.headers);
    if (location == NULL) {
        return NGX_HTTP_INTERNAL_SERVER_ERROR;
    }

    ngx_str_set(&location->key, "Location");

    //redirect to https://<redirect_url>/?ignoreLoginURL=1
    if (zlcf->redirect_url->wildcard) {
        //url contain only port, just use $host
        host_var =
                ngx_http_get_indexed_variable(r, zlcf->host_index);
        host_value.data = host_var->data;
        host_value.len = host_var->len;

        location->value.len = zlcf->redirect_schema.len +
                              3 +     /* for "://" */
                              host_var->len +
                              1 +     /* for ":" */
                              zlcf->redirect_url->port_text.len +
                              sizeof ("/?ignoreLoginURL=1") - 1;

        location->value.data = ngx_palloc(r->pool, location->value.len);
        ngx_sprintf(location->value.data, "%V://%V:%V/?ignoreLoginURL=1",
                &zlcf->redirect_schema,
                &host_value,
                &zlcf->redirect_url->port_text);

    } else {
        //use schema and redirect url
        location->value.len = zlcf->redirect_schema.len +
                              3 + /* for "://" */
                              zlcf->redirect_url->url.len +
                              sizeof ("/?ignoreLoginURL=1") - 1;

        location->value.data = ngx_palloc(r->pool, location->value.len);
        ngx_sprintf(location->value.data, "%V://%V/?ignoreLoginURL=1",
                        &zlcf->redirect_schema,
                        &zlcf->redirect_url->url);
    }
    location->hash = 1;

    return NGX_HTTP_MOVED_TEMPORARILY;
}

static ngx_int_t
ngx_http_zm_sso_set_auth_token_and_redirect (ngx_http_request_t *r,
        ngx_str_t token, ngx_flag_t isAdmin)
{
    ngx_table_elt_t * token_cookie;

    token_cookie = ngx_list_push(&r->headers_out.headers);
    token_cookie->hash = 1;
    ngx_str_set(&token_cookie->key, "Set-Cookie");

    if (!isAdmin) {
        token_cookie->value.len = sizeof("ZM_AUTH_TOKEN=") - 1 + token.len
                + sizeof(";Path=/") - 1;
        token_cookie->value.data = ngx_palloc(r->pool, token_cookie->value.len);
        ngx_sprintf(token_cookie->value.data, "ZM_AUTH_TOKEN=%V;Path=/", &token);
    } else {
        /* admin token */
        token_cookie->value.len = sizeof("ZM_ADMIN_AUTH_TOKEN=") - 1
                + token.len + sizeof(";Path=/") - 1;
        token_cookie->value.data = ngx_palloc(r->pool, token_cookie->value.len);
        ngx_sprintf(token_cookie->value.data, "ZM_ADMIN_AUTH_TOKEN=%V;Path=/", &token);
    }
    return ngx_http_zm_sso_redirect(r);
}

static ngx_int_t
ngx_http_zm_sso_cert_auth(ngx_http_request_t *r, ngx_zm_lookup_work_t *w)
{

    ngx_http_variable_value_t    *ssl_client_verify_var;
    ngx_http_variable_value_t    *ssl_client_s_dn_var;
    ngx_str_t                     ssl_client_verify_value;
    ngx_str_t                     ssl_client_s_dn_value;
    ngx_http_zm_sso_loc_conf_t   *zlcf;

    zlcf = ngx_http_get_module_loc_conf(r, ngx_http_zm_sso_module);
    w->protocol = ZM_PROTO_HTTP; /* it doesn't matter which protocol is used, for cert auth we don't want route */
    if (zlcf->type == NGX_ZM_SSO_CERTAUTH) {
        w->isAdmin = 0;
    } else if (zlcf->type == NGX_ZM_SSO_CERTAUTH_ADMIN) {
        w->isAdmin = 1;
    } else {
        return NGX_HTTP_FORBIDDEN;
    }

    ssl_client_verify_var = ngx_http_get_indexed_variable(r,
            zlcf->ssl_client_verify_index);

    if (!ssl_client_verify_var->valid) {
        ngx_log_error (NGX_LOG_ERR, r->connection->log, 0,
                "nginx ssl module return invalid result for "
                "client cert auth");
        return NGX_HTTP_INTERNAL_SERVER_ERROR;
    }

    ssl_client_verify_value.data = ssl_client_verify_var->data;
    ssl_client_verify_value.len = ssl_client_verify_var->len;

    if (ngx_strncasecmp(ssl_client_verify_value.data, (u_char*)"SUCCESS",
                sizeof ("SUCCESS") - 1) == 0) {
        /* get client cert subject dn */
        ssl_client_s_dn_var = ngx_http_get_indexed_variable(r,
                zlcf->ssl_client_s_dn_index);
        if (!ssl_client_s_dn_var->valid) {
            ngx_log_error (NGX_LOG_ERR, r->connection->log, 0,
                    "nginx ssl module return invalid subject DN for "
                    "client cert auth");
            return NGX_HTTP_INTERNAL_SERVER_ERROR;
        }

        ssl_client_s_dn_value.data = ssl_client_s_dn_var->data;
        ssl_client_s_dn_value.len = ssl_client_s_dn_var->len;
        w->auth_method = ZM_AUTHMETH_CERTAUTH;
        w->username = ssl_client_s_dn_value;
        w->virtual_host = r->headers_in.host->value;
        w->login_attempts = 0; /* HTTP is always 0 */
        w->connection = r->connection;
        w->on_success = ngx_http_zm_sso_cert_auth_on_success;
        w->on_failure = ngx_http_zm_sso_cert_auth_on_failure;

        ngx_http_cleanup_t * cln = ngx_http_cleanup_add(r, 0);
        cln->handler = ngx_http_zm_sso_cleanup;
        cln->data = w;
        r->main->count++;
        r->read_event_handler = ngx_http_zm_sso_rd_check_broken_connection;
        r->write_event_handler = ngx_http_zm_sso_wr_check_broken_connection;
        ngx_zm_lookup(w);
        return NGX_DONE;

    } else if (ngx_strncasecmp(ssl_client_verify_value.data, (u_char*)"NONE",
                sizeof ("NONE") - 1) == 0) {
        /* if ssl_verify_client is set to be "on", and the the verification fails,
         * nginx will return SSL handshake error; in this case, the code here will
         * never be executed. Only when ssl_verify_client is "optional", and client
         * doesn't send the cert, here will be executed. That is, directly redirect.
         */
        ngx_log_error (NGX_LOG_INFO, r->connection->log, 0,
                        "doesn't provide client cert, redirect to common https login page "
                        "to do the username/password login");
        return ngx_http_zm_sso_redirect (r);
    } else {
        ngx_log_error (NGX_LOG_ERR, r->connection->log, 0,
                "unexpected ssl client cert verification result %V",
                &ssl_client_verify_value);

        return NGX_HTTP_INTERNAL_SERVER_ERROR;
    }
}

static void
ngx_http_zm_sso_cert_auth_on_success(ngx_zm_lookup_work_t *w)
{
    ngx_int_t rc;
    ngx_http_request_t *r;
    r = w->data;
    ngx_log_error (NGX_LOG_INFO, w->log, 0,
                    "login succeed for subject dn %V after client cert auth,"
                    " redirect to common https url", &w->username);
    rc = ngx_http_zm_sso_set_auth_token_and_redirect (r, w->zm_auth_token,
            w->isAdmin);
    ngx_http_zm_sso_finalize_request(r, rc);
}

static void
ngx_http_zm_sso_cert_auth_on_failure(ngx_zm_lookup_work_t *w)
{
    ngx_http_request_t        *r;
    ngx_http_ssl_srv_conf_t   *hscf;
    ngx_int_t                  rc;
    r = w->data;
    hscf = ngx_http_get_module_srv_conf(r, ngx_http_ssl_module);
    if (w->result == ZM_LOOKUP_LOGIN_FAILED) {
        ngx_log_error (NGX_LOG_WARN, w->log, 0,
                "login failed for subject dn %V during client cert auth"
                ", reason:%V", &w->username, &w->err);
        if (hscf->verify == 1) {
            // verify client cert mode is on
            ngx_http_zm_sso_finalize_request(r, NGX_HTTP_FORBIDDEN);
        } else {
            // verify client cert mode is optional
            rc = ngx_http_zm_sso_redirect (r);
            ngx_http_zm_sso_finalize_request(r, rc);
        }

    } else {
        ngx_http_zm_sso_finalize_request(r, NGX_HTTP_INTERNAL_SERVER_ERROR);
    }
}

static void
ngx_http_zm_sso_finalize_request(ngx_http_request_t *r, ngx_int_t rc)
{
    ngx_log_debug1(NGX_LOG_DEBUG_HTTP, r->connection->log, 0,
                   "finalize http zm sso request: %i", rc);

    if (rc == NGX_DECLINED) {
        return;
    }

    r->connection->log->action = "sending to client";

    ngx_http_finalize_request(r, rc);
}

static void ngx_http_zm_sso_cleanup (void *data)
{
    ngx_zm_lookup_work_t * w;
    w = data;
    ngx_zm_lookup_finalize (w);
}

static void
ngx_http_zm_sso_rd_check_broken_connection(ngx_http_request_t *r)
{
    ngx_http_zm_sso_check_broken_connection(r, r->connection->read);
}


static void
ngx_http_zm_sso_wr_check_broken_connection(ngx_http_request_t *r)
{
    ngx_http_zm_sso_check_broken_connection(r, r->connection->write);
}

static void
ngx_http_zm_sso_check_broken_connection(ngx_http_request_t * r, ngx_event_t *ev)
{
    ngx_connection_t            *c;
    int                          n;
    char                         buf[1];
    ngx_err_t                    err;
    ngx_int_t                    event;

    c = ev->data;
    r = c->data;

    if (c->error) {
        if ((ngx_event_flags & NGX_USE_LEVEL_EVENT) && ev->active) {
           event = ev->write ? NGX_WRITE_EVENT : NGX_READ_EVENT;
           if (ngx_del_event(ev, event, 0) != NGX_OK) {
               ngx_http_finalize_request(r, NGX_HTTP_INTERNAL_SERVER_ERROR);
               return;
           }
       }
       ngx_http_finalize_request(r, NGX_HTTP_CLIENT_CLOSED_REQUEST);
       return;
    }

    n = recv(c->fd, buf, 1, MSG_PEEK);

    err = ngx_socket_errno;

    ngx_log_debug1(NGX_LOG_DEBUG_HTTP, ev->log, err,
                  "zm sso recv(): %d", n);

    if (ev->write && (n >= 0 || err == NGX_EAGAIN)) {
        return;
    }

    if ((ngx_event_flags & NGX_USE_LEVEL_EVENT) && ev->active) {

       event = ev->write ? NGX_WRITE_EVENT : NGX_READ_EVENT;

       if (ngx_del_event(ev, event, 0) != NGX_OK) {
           ngx_http_finalize_request(r, NGX_HTTP_INTERNAL_SERVER_ERROR);
           return;
       }
   }

   if (n > 0) {
       return;
   }

   if (n == -1) {
       if (err == NGX_EAGAIN) {
           return;
       }

       ev->error = 1;

   } else { /* n == 0 */
       err = 0;
   }

   ev->eof = 1;
   c->error = 1;

   ngx_log_error(NGX_LOG_INFO, ev->log, err,
                 "client closed prematurely connection during sso authentication");

   ngx_http_finalize_request(r, NGX_HTTP_CLIENT_CLOSED_REQUEST);
}
