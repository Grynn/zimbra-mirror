/*
 * Copyright (C) Igor Sysoev
 */

/*
 * Portions Copyright (c) VMware, Inc. [1998-2011]. All Rights Reserved.
 */

#include <ngx_config.h>
#include <ngx_mail.h>
#include <ngx_mail_zmauth_module.h>
#include <ngx_zm_lookup.h>

typedef void (*ngx_mail_zmauth_handler_pt)(ngx_mail_session_t *s,
    ngx_mail_zmauth_ctx_t *ctx);

struct ngx_mail_zmauth_ctx_s {
    ngx_zm_lookup_work_t      *work;
    ngx_event_t               *wait_ev;
    ngx_pool_t                *pool;
    ngx_str_t                  errmsg; //TODO necessary?
};

static void ngx_mail_zmauth_wait_handler(ngx_event_t *ev);
static ngx_int_t ngx_mail_zmauth_escape(ngx_pool_t *pool, ngx_str_t *text,
        ngx_str_t *escaped);
static void ngx_mail_zmauth_unescape(ngx_str_t *text);
static void ngx_mail_zmauth_lookup_result_handler(ngx_zm_lookup_work_t * work);

static void *ngx_mail_zmauth_create_conf(ngx_conf_t *cf);
static char *ngx_mail_zmauth_merge_conf(ngx_conf_t *cf, void *parent,
        void *child);
static char * ngx_mail_zm_auth_http(ngx_conf_t *cf, ngx_command_t *cmd,
        void *conf);

static void ngx_mail_zmauth_block_read(ngx_event_t *ev);
static void ngx_mail_zmauth_cleanup(void * data);

static ngx_command_t ngx_mail_zmauth_module_commands[] = {
    { ngx_string("zm_auth_http"),
      NGX_MAIL_MAIN_CONF | NGX_MAIL_SRV_CONF | NGX_CONF_NOARGS,
      ngx_mail_zm_auth_http,
      NGX_MAIL_SRV_CONF_OFFSET,
      0,
      NULL },

     ngx_null_command
};

static ngx_mail_module_t ngx_mail_zmauth_module_ctx = {
     NULL,                            /* protocol */
     NULL,                            /* create main configuration */
     NULL,                            /* init main configuration */
     ngx_mail_zmauth_create_conf,     /* create server configuration */
     ngx_mail_zmauth_merge_conf       /* merge server configuration */
};

ngx_module_t ngx_mail_zmauth_module = {
     NGX_MODULE_V1,
     &ngx_mail_zmauth_module_ctx,     /* module context */
     ngx_mail_zmauth_module_commands, /* module directives */
     NGX_MAIL_MODULE,                 /* module type */
     NULL,                            /* init master */
     NULL,                            /* init module */
     NULL,                            /* init process */
     NULL,                            /* init thread */
     NULL,                            /* exit thread */
     NULL,                            /* exit process */
     NULL,                            /* exit master */
     NGX_MODULE_V1_PADDING
};

static ngx_str_t ngx_mail_zmauth_proto[] = { ngx_string("pop3"), ngx_string("imap"),
        ngx_string("smtp") };

/* the methods order must follow the macro definition of NGX_MAIL_AUTH_XXXX in mail.h */
static ngx_str_t ngx_mail_zmauth_method[] = {
        ngx_string("plain"),    /* NGX_MAIL_AUTH_PLAIN */
        ngx_string("login"),    /* NGX_MAIL_AUTH_LOGIN */
        ngx_string("login"),    /* NGX_MAIL_AUTH_LOGIN_USERNAME */
        ngx_string("apop"),     /* NGX_MAIL_AUTH_APOP */
        ngx_string("cram-md5"), /* NGX_MAIL_AUTH_CRAM_MD5 */
        ngx_string("none"),     /* NOT TAKE AUTH */
        ngx_string("passwd"),   /* NGX_MAIL_AUTH_PASSWD */
        ngx_string("plain"),    /* NGX_MAIL_AUTH_PLAIN_IR */
        ngx_string("gssapi"),   /* NGX_MAIL_AUTH_GSSAPI */
        ngx_string("gssapi")    /* NGX_MAIL_AUTH_GSSAPI_IR */
};

static ngx_str_t LOGIN_FAILED = ngx_string("login failed");

void
ngx_mail_zmauth_init(ngx_mail_session_t *s) {
    ngx_pool_t *pool;
    ngx_mail_zmauth_ctx_t    *ctx;
    ngx_zm_lookup_work_t     *work;
    ngx_str_t                 escaped_login, escaped_account_name;
    ngx_mail_cleanup_t       *cln;
    ngx_flag_t                proxy_ssl;

    s->connection->log->action = "in mail zmauth state";

    /* create pool and module context */
    pool = ngx_create_pool(2048, s->connection->log);
    if (pool == NULL) {
        ngx_mail_session_internal_server_error(s);
        return;
    }

    ctx = ngx_pcalloc(pool, sizeof(ngx_mail_zmauth_ctx_t));
    if (ctx == NULL) {
        ngx_destroy_pool(pool);
        ngx_mail_session_internal_server_error(s);
        return;
    }

    ctx->pool = pool;
    ngx_mail_set_ctx(s, ctx, ngx_mail_zmauth_module);

    /* init clean up */
    cln = ngx_mail_cleanup_add(s, 0);
    cln->data = s;
    cln->handler = ngx_mail_zmauth_cleanup;

    /* init wait event */
    ctx->wait_ev = ngx_palloc(pool, sizeof(ngx_event_t));
    if (ctx->wait_ev == NULL) {
        ngx_destroy_pool(pool);
        ngx_mail_session_internal_server_error(s);
        return;
    }
    ngx_memzero (ctx->wait_ev, sizeof (ngx_event_t));
    ctx->wait_ev->handler = ngx_mail_zmauth_wait_handler;
    ctx->wait_ev->log = s->connection->log;
    ctx->wait_ev->data = s->connection;

    work = ngx_pcalloc(pool, sizeof(ngx_zm_lookup_work_t));
    if (work == NULL) {
        ngx_destroy_pool(pool);
        ngx_mail_session_internal_server_error(s);
        return;
    }

    if (s->auth_method == NGX_MAIL_AUTH_PASSWD    ||
        s->auth_method == NGX_MAIL_AUTH_PLAIN     ||
        s->auth_method == NGX_MAIL_AUTH_PLAIN_IR  ||
        s->auth_method == NGX_MAIL_AUTH_LOGIN     ||
        s->auth_method == NGX_MAIL_AUTH_LOGIN_USERNAME) {
        work->auth_method = ZM_AUTHMETH_USERNAME;
    } else if (s->auth_method == NGX_MAIL_AUTH_GSSAPI ||
               s->auth_method == NGX_MAIL_AUTH_GSSAPI_IR) {
        work->auth_method = ZM_AUTHMETH_GSSAPI;
        work->auth_id = s->authid;
    } else {
        ngx_log_error(NGX_LOG_ERR, s->connection->log, 0,
                "unsupported auth method %V",
                &ngx_mail_zmauth_method[s->auth_method]);
        ngx_destroy_pool(pool);
        ngx_mail_session_internal_server_error(s);
        return;
    }

    proxy_ssl = ngx_mail_get_proxy_ssl(s);

    switch (s->protocol) {
    case NGX_MAIL_POP3_PROTOCOL:
        work->protocol = proxy_ssl?ZM_PROTO_POP3S:ZM_PROTO_POP3;
        break;
    case NGX_MAIL_IMAP_PROTOCOL:
        work->protocol = proxy_ssl?ZM_PROTO_IMAPS:ZM_PROTO_IMAP;
        break;
    default:
        ngx_log_error(NGX_LOG_ERR, s->connection->log, 0,
                "unsupported auth protocol %V",
                &ngx_mail_zmauth_proto[s->protocol]);
        ngx_destroy_pool(pool);
        ngx_mail_session_internal_server_error(s);
        return;
    }

    if (ngx_mail_zmauth_escape(pool, &s->login, &escaped_login) != NGX_OK) {
        ngx_destroy_pool(pool);
        ngx_mail_session_internal_server_error(s);
        return;
    }
    work->username = escaped_login;

    work->connection = s->connection;
    work->login_attempts = s->login_attempt;
    work->log = s->connection->log;
    work->pool = s->connection->pool;
    work->data = s;
    work->on_success = ngx_mail_zmauth_lookup_result_handler;
    work->on_failure = ngx_mail_zmauth_lookup_result_handler;

    switch (s->vlogin) {
    case 0:
        work->alias_check_stat = ZM_ALIAS_NOT_CHECKED;
        break;
    case 1:
        work->alias_check_stat = ZM_ALIAS_NOT_FOUND;
        work->account_name = work->username;
        work->alias_key = s->key_alias;
        break;
    case 2:
        work->alias_check_stat = ZM_ALIAS_FOUND;
        if (ngx_mail_zmauth_escape(pool, &s->qlogin,
                                   &escaped_account_name) != NGX_OK) {
            ngx_destroy_pool(pool);
            ngx_mail_session_internal_server_error(s);
            return;
        }
        work->account_name = escaped_account_name;
        work->alias_key = s->key_alias;
        break;
    default:
        ngx_log_error(NGX_LOG_ERR, s->connection->log, 0,
                "Should never reach here");
        return;
    }

    ctx->work = work;

    s->connection->read->handler = ngx_mail_zmauth_block_read;

    ngx_zm_lookup(work);
}

static void
ngx_mail_zmauth_wait_handler(ngx_event_t *ev) {
    ngx_connection_t *c;
    ngx_mail_session_t *s;
    ngx_mail_zmauth_ctx_t *ctx;

    ngx_log_debug0(NGX_LOG_DEBUG_MAIL, ev->log, 0, "mail zmauth wait handler");

    c = ev->data;
    s = c->data;
    ctx = ngx_mail_get_module_ctx(s, ngx_mail_zmauth_module);

    if (ev->timedout) {
        /* we need to close the connection immediately */

        ngx_destroy_pool(ctx->pool);
        ngx_mail_set_ctx(s, NULL, ngx_mail_zmauth_module);
        s->quit = 1;
        ngx_mail_send(c->write);

        return;
    }

    if (ev->active) {
        if (ngx_handle_read_event(ev, 0) != NGX_OK) {
            ngx_mail_close_connection(c);
        }
    }
}

static void
ngx_mail_zmauth_cleanup (void * data)
{
    ngx_mail_session_t * s;
    ngx_mail_zmauth_ctx_t * ctx;
    s = (ngx_mail_session_t *)data;
    ctx = ngx_mail_get_module_ctx(s, ngx_mail_zmauth_module);
    if (ctx != NULL) {
        ngx_zm_lookup_finalize(ctx->work);
        ngx_destroy_pool(ctx->pool);
        ngx_mail_set_ctx(s, NULL, ngx_mail_zmauth_module);
    }
}

static ngx_int_t
ngx_mail_zmauth_escape(ngx_pool_t *pool, ngx_str_t *text,
        ngx_str_t *escaped) {
    u_char *p;
    uintptr_t n;

    n = ngx_escape_uri(NULL, text->data, text->len, NGX_ESCAPE_MAIL_AUTH);

    if (n == 0) {
        *escaped = *text;
        return NGX_OK;
    }

    escaped->len = text->len + n * 2;

    p = ngx_pnalloc(pool, escaped->len);
    if (p == NULL) {
        return NGX_ERROR;
    }

    (void) ngx_escape_uri(p, text->data, text->len, NGX_ESCAPE_MAIL_AUTH);

    escaped->data = p;

    return NGX_OK;
}

/* text will be modified */
static void
ngx_mail_zmauth_unescape(ngx_str_t *text) {
    u_char       *src, *dst;
    size_t        len;

    src = text->data;
    dst = text->data;

    ngx_unescape_uri(&dst, &src, text->len, NGX_UNESCAPE_URI);

    len = (text->data + text->len) - src;
    if (len) {
        dst = ngx_copy(dst, src, len);
    }

    text->len = dst - text->data;
}

static void *
ngx_mail_zmauth_create_conf(ngx_conf_t *cf) {
    ngx_mail_zmauth_conf_t *zmcf;

    zmcf = ngx_pcalloc(cf->pool, sizeof(ngx_mail_zmauth_conf_t));
    if (zmcf == NULL) {
        return NGX_CONF_ERROR;
    }

    zmcf->use_zmauth = NGX_CONF_UNSET;

    return zmcf;
}

static char *
ngx_mail_zmauth_merge_conf(ngx_conf_t *cf, void *parent, void *child) {
    ngx_mail_zmauth_conf_t *prev = parent;
    ngx_mail_zmauth_conf_t *conf = child;

    ngx_conf_merge_value(conf->use_zmauth, prev->use_zmauth, 1);

    return NGX_CONF_OK;
}

static char *
ngx_mail_zm_auth_http(ngx_conf_t *cf, ngx_command_t *cmd, void *conf) {
    ngx_mail_zmauth_conf_t *zmcf = conf;
    zmcf->use_zmauth = 1;

    return NGX_CONF_OK;
}

static void
ngx_mail_zmauth_lookup_result_handler(ngx_zm_lookup_work_t * work) {
    ngx_mail_session_t     *s;
    ngx_mail_zmauth_ctx_t  *ctx;
    size_t                  size;
    u_char                 *p;
    ngx_addr_t             *peer;
    ngx_str_t               errmsg;

    s = (ngx_mail_session_t *) work->data;
    ctx = (ngx_mail_zmauth_ctx_t *)ngx_mail_get_module_ctx(s, ngx_mail_zmauth_module);

    if (work->result == ZM_LOOKUP_SUCCESS) {
        ngx_mail_zmauth_unescape(&work->account_name);

        /* copy the lookup result from zmauth pool to s->connection pool */
        s->qlogin = *(ngx_pstrcpy(s->connection->pool, &work->account_name));
        s->key_alias = *(ngx_pstrcpy(s->connection->pool, &work->alias_key));
        s->key_route = *(ngx_pstrcpy(s->connection->pool, &work->route_key));
        peer = ngx_palloc(s->connection->pool, sizeof(ngx_addr_t));

        peer->name = *(ngx_pstrcpy(s->connection->pool, &work->route->name));
        peer->socklen = work->route->socklen;
        peer->sockaddr = ngx_palloc(s->connection->pool, peer->socklen);
        if(peer->sockaddr == NULL) {
            return; /* NO MEM */
        }
        ngx_memcpy(peer->sockaddr, work->route->sockaddr, peer->socklen);

        switch (work->alias_check_stat) {
           case ZM_ALIAS_NOT_FOUND:
               s->vlogin = 1;
               break;
           case ZM_ALIAS_FOUND:
               s->vlogin = 2;
               break;
           default:
               break;
               /* do nothing */
           }

        ngx_destroy_pool(ctx->pool);
        ngx_mail_set_ctx(s, NULL, ngx_mail_zmauth_module);
        ngx_mail_proxy_init(s, peer);
        return;
    } else {
        ngx_log_error(NGX_LOG_ERR, s->connection->log, 0,
                      "An error occurred in mail zmauth: %V",
                       &work->err);

        /* construct error msg */
        if (work->result != ZM_LOOKUP_LOGIN_FAILED) {
            /* zmauth clean up will destroy the ctx->pool */
            ngx_mail_session_internal_server_error(s);
            return;
        }

        errmsg = LOGIN_FAILED; /* should we return the real err msg to user? */

        switch (s->protocol) {

        case NGX_MAIL_POP3_PROTOCOL:
            size = sizeof("-ERR ") - 1 + errmsg.len + sizeof(CRLF) - 1;
            break;

        case NGX_MAIL_IMAP_PROTOCOL:
            size = s->tag.len + 1 /*for space*/+ sizeof("NO ") - 1 + errmsg.len
                   + sizeof(CRLF) - 1;
            break;

        default: /* NGX_MAIL_SMTP_PROTOCOL */
            ngx_log_error(NGX_LOG_CRIT, s->connection->log, 0, "smtp is not supported!!");
            return;
        }

        p = ngx_pnalloc(s->connection->pool, size);
        if (p == NULL) {
            ngx_destroy_pool(ctx->pool);
            ngx_mail_session_internal_server_error(s);
            return;
        }

        ctx->errmsg.data = p;

        switch (s->protocol) {

        case NGX_MAIL_POP3_PROTOCOL:
            *p++ = '-'; *p++ = 'E'; *p++ = 'R'; *p++ = 'R'; *p++ = ' ';
            break;

        case NGX_MAIL_IMAP_PROTOCOL:
            p = ngx_cpymem(p, s->tag.data, s->tag.len);
            *p++ = ' '; *p++ = 'N'; *p++ = 'O'; *p++ = ' ';
            break;

        default: /* NGX_MAIL_SMTP_PROTOCOL */
            break;
        }

        p = ngx_cpymem(p, errmsg.data, errmsg.len);
        *p++ = CR; *p++ = LF;

        ctx->errmsg.len = p - ctx->errmsg.data;
        s->out = ctx->errmsg;

        if (work->result == ZM_LOOKUP_LOGIN_FAILED && work->wait_time > 0) {
            ngx_add_timer(ctx->wait_ev, (ngx_msec_t) (work->wait_time * 1000));

            s->connection->read->handler = ngx_mail_zmauth_block_read;
        } else {
            s->quit = 1;
            ngx_mail_send(s->connection->write);
            ngx_mail_set_ctx(s, NULL, ngx_mail_zmauth_module);
            ngx_destroy_pool(ctx->pool);
        }
        return;
    }
}

static void
ngx_mail_zmauth_block_read(ngx_event_t *ev)
{
    ngx_connection_t          *c;
    ngx_mail_session_t        *s;

    ngx_log_debug0(NGX_LOG_DEBUG_MAIL, ev->log, 0,
                   "mail zmauth block read");

    if (ngx_handle_read_event(ev, 0) != NGX_OK) {
        c = ev->data;
        s = c->data;

        ngx_mail_session_internal_server_error(s);
    }
}

/* Utility function to check if a (login) name has the zimbra
   supported `special' extensions
   The test is to see if the name ends with /tb, /wm, or /ni
   tb = thunderbird
   wm = windows mobile
   ni = no idle
 */
ngx_flag_t
has_zimbra_extensions (ngx_str_t login)
{
    ngx_flag_t  f = 0;

    if ((login.len > 3) &&
        (!ngx_memcmp (login.data + (login.len - 3), "/tb", 3) ||
         !ngx_memcmp (login.data + (login.len - 3), "/wm", 3) ||
         !ngx_memcmp (login.data + (login.len - 3), "/ni", 3)
        )
       ) {
       f = 1;
    }

    return f;
}

/* Strip off any zimbra `special' extensions from a (login) name
   Returns a shallow copy of the original name, with the length
   shortened by 3 to strip off the trailing characters
 */
/* Never use this ngx_str_t
strip_zimbra_extensions (ngx_str_t login)
{
    ngx_str_t   t = login;

    if (has_zimbra_extensions (login)) {
        t.len -= 3;
    }

    return t;
} */

ngx_str_t
get_zimbra_extension (ngx_str_t login)
{
    ngx_str_t   e = ngx_string("");

    if (has_zimbra_extensions(login)) {
        e.data = login.data + (login.len - 3);
        e.len = 3;
    }

    return e;
}
