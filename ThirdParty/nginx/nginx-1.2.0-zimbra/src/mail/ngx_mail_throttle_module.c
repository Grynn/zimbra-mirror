/*
 * Copyright (c) VMware, Inc. [1998 – 2011]. All Rights Reserved.
 *
 * For more information, see –
 * http://vmweb.vmware.com/legal/corporate/VMwareCopyrightPatentandTrademarkNotices.pdf
 */

#include <ngx_mail_throttle_module.h>
#include <ngx_zm_lookup.h>
#include <ngx_memcache.h>

static ngx_str_t throttle_zero = ngx_string("0");

static void *ngx_mail_throttle_create_srv_conf
    (ngx_conf_t *cf);
static char *ngx_mail_throttle_merge_srv_conf
    (ngx_conf_t *cf, void *parent, void *child);
static char *ngx_mail_throttle_ip_ttl
    (ngx_conf_t *cf, ngx_command_t* command, void * conf);
static char *ngx_mail_throttle_user_ttl
    (ngx_conf_t *cf, ngx_command_t* command, void * conf);
static char *ngx_mail_throttle_set_ttl_text
    (ngx_msec_t ttl, ngx_str_t * input, ngx_str_t * ttl_text);
static void ngx_mail_throttle_ip_success_handler
    (mc_work_t *w);
static void ngx_mail_throttle_ip_failure_handler
    (mc_work_t *w);
static void ngx_mail_throttle_ip_add 
    (ngx_str_t *ip, throttle_callback_t *callback);
static void ngx_mail_throttle_ip_add_success_handler
    (mc_work_t *w);
static void ngx_mail_throttle_ip_add_failure_handler
    (mc_work_t *w);
static void ngx_mail_throttle_quser
    (ngx_str_t *quser, throttle_callback_t *callback);
static void ngx_mail_throttle_quser_success_handler
    (mc_work_t *w);
static void ngx_mail_throttle_quser_failure_handler
    (mc_work_t *w);
static void ngx_mail_throttle_user_add
    (ngx_str_t *user, throttle_callback_t *callback);
static void ngx_mail_throttle_user_add_success_handler
    (mc_work_t *w);
static void ngx_mail_throttle_user_add_failure_handler
    (mc_work_t *w);
static void ngx_mail_throttle_user_success_handler
    (mc_work_t *w);
static void ngx_mail_throttle_user_failure_handler
    (mc_work_t *w);

static ngx_str_t ngx_mail_throttle_get_ip_throttle_key
   (ngx_pool_t *pool, ngx_log_t *log, ngx_str_t ip);
static ngx_str_t ngx_mail_throttle_get_user_throttle_key
   (ngx_pool_t *pool, ngx_log_t *log, ngx_str_t user);

static ngx_command_t  ngx_mail_throttle_commands[] = {
    { ngx_string("mail_login_ip_max"),
      NGX_MAIL_MAIN_CONF|NGX_CONF_TAKE1,
      ngx_conf_set_num_slot,
      NGX_MAIL_SRV_CONF_OFFSET,
      offsetof(ngx_mail_throttle_srv_conf_t, mail_login_ip_max),
      NULL },

    { ngx_string("mail_login_ip_ttl"),
      NGX_MAIL_MAIN_CONF|NGX_CONF_TAKE1,
      ngx_mail_throttle_ip_ttl,
      NGX_MAIL_SRV_CONF_OFFSET,
      offsetof(ngx_mail_throttle_srv_conf_t, mail_login_ip_ttl),
      NULL },

    { ngx_string("mail_login_ip_rejectmsg"),
      NGX_MAIL_MAIN_CONF|NGX_CONF_TAKE1,
      ngx_conf_set_str_slot,
      NGX_MAIL_SRV_CONF_OFFSET,
      offsetof(ngx_mail_throttle_srv_conf_t, mail_login_ip_rejectmsg),
      NULL },

    { ngx_string("mail_login_user_max"),
      NGX_MAIL_MAIN_CONF|NGX_CONF_TAKE1,
      ngx_conf_set_num_slot,
      NGX_MAIL_SRV_CONF_OFFSET,
      offsetof(ngx_mail_throttle_srv_conf_t, mail_login_user_max),
      NULL },

    { ngx_string("mail_login_user_ttl"),
      NGX_MAIL_MAIN_CONF|NGX_CONF_TAKE1,
      ngx_mail_throttle_user_ttl,
      NGX_MAIL_SRV_CONF_OFFSET,
      offsetof(ngx_mail_throttle_srv_conf_t, mail_login_user_ttl),
      NULL },

    { ngx_string("mail_login_user_rejectmsg"),
      NGX_MAIL_MAIN_CONF|NGX_CONF_TAKE1,
      ngx_conf_set_str_slot,
      NGX_MAIL_SRV_CONF_OFFSET,
      offsetof(ngx_mail_throttle_srv_conf_t, mail_login_user_rejectmsg),
      NULL },

     ngx_null_command
};

static ngx_mail_module_t  ngx_mail_throttle_module_ctx = {
    NULL,                                  /* protocol */
    NULL,                                  /* create main configuration */
    NULL,                                  /* init main configuration */

    ngx_mail_throttle_create_srv_conf,     /* create server configuration */
    ngx_mail_throttle_merge_srv_conf       /* merge server configuration */
};

ngx_module_t  ngx_mail_throttle_module = {
    NGX_MODULE_V1,
    &ngx_mail_throttle_module_ctx,         /* module context */
    ngx_mail_throttle_commands,            /* module directives */
    NGX_MAIL_MODULE,                       /* module type */
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
ngx_mail_throttle_create_srv_conf(ngx_conf_t *cf)
{
    ngx_mail_throttle_srv_conf_t  *tscf;

    tscf = ngx_pcalloc(cf->pool, sizeof(ngx_mail_throttle_srv_conf_t));
    if (tscf == NULL) {
        return NULL;
    }

    tscf->mail_login_ip_max = NGX_CONF_UNSET;
    tscf->mail_login_ip_ttl = NGX_CONF_UNSET_MSEC;
    ngx_str_null (&tscf->mail_login_ip_ttl_text);
    tscf->mail_login_user_max = NGX_CONF_UNSET;
    tscf->mail_login_user_ttl = NGX_CONF_UNSET_MSEC;
    ngx_str_null (&tscf->mail_login_user_ttl_text);

    return tscf;
}

static char *
ngx_mail_throttle_merge_srv_conf(ngx_conf_t *cf, void *parent, void *child)
{
    ngx_mail_throttle_srv_conf_t *prev = parent;
    ngx_mail_throttle_srv_conf_t *conf = child;

    ngx_conf_merge_uint_value (conf->mail_login_ip_max,
                               prev->mail_login_ip_max, 1000);
    ngx_conf_merge_uint_value (conf->mail_login_user_max,
                               prev->mail_login_user_max, 100);
    ngx_conf_merge_msec_value (conf->mail_login_ip_ttl,
                               prev->mail_login_ip_ttl, 60000);
    ngx_conf_merge_msec_value (conf->mail_login_user_ttl,
                               prev->mail_login_user_ttl, 60000);
    ngx_conf_merge_str_value (conf->mail_login_ip_rejectmsg,
                              prev->mail_login_ip_rejectmsg, "");
    ngx_conf_merge_str_value (conf->mail_login_user_rejectmsg,
                              prev->mail_login_user_rejectmsg, "");
    ngx_conf_merge_str_value (conf->mail_login_ip_ttl_text,
                              prev->mail_login_ip_ttl_text, "60");
    ngx_conf_merge_str_value (conf->mail_login_user_ttl_text,
                              prev->mail_login_user_ttl_text, "60");

    return NGX_CONF_OK;
}

static char *
ngx_mail_throttle_ip_ttl (ngx_conf_t *cf, ngx_command_t *cmd, void *conf) {
    char      *res;
    ngx_str_t *value;
    ngx_mail_throttle_srv_conf_t *tscf = conf;

    res = ngx_conf_set_msec_slot(cf, cmd, conf);

    if (res != NGX_CONF_OK) {
        return res;
    }

    value = cf->args->elts;
    res = ngx_mail_throttle_set_ttl_text (tscf->mail_login_ip_ttl,
                    &value[1], &tscf->mail_login_ip_ttl_text);

    if (res != NGX_CONF_OK) {
        return res;
    }

    return NGX_CONF_OK;
}

static char *
ngx_mail_throttle_user_ttl (ngx_conf_t *cf, ngx_command_t *cmd, void *conf) {
    char      *res;
    ngx_str_t *value;
    ngx_mail_throttle_srv_conf_t *tscf = conf;

    res = ngx_conf_set_msec_slot(cf, cmd, conf);

    if (res != NGX_CONF_OK) {
        return res;
    }

    value = cf->args->elts;
    res = ngx_mail_throttle_set_ttl_text (tscf->mail_login_user_ttl,
                    &value[1], &tscf->mail_login_user_ttl_text);

    if (res != NGX_CONF_OK) {
        return res;
    }

    return NGX_CONF_OK;
}

static char *
ngx_mail_throttle_set_ttl_text (ngx_msec_t ttl, ngx_str_t * input, ngx_str_t * ttl_text) {

   if (ttl > 1000) {
       if (input->len > 2 &&
           input->data[input->len - 2] == 'm' &&
           input->data[input->len - 1] == 's') {
           /* for ms value, trim the last 5 characters 'NNNms' and become the number of seconds
            * for example, 3600000ms --> 3600 */
           ttl_text->data = input->data;
           ttl_text->len = input->len - 5;
       } else {
           /* no trailing "ms", directly used as second value */
           *ttl_text = *input;
       }
   } else if (ttl > 0) {
       /* 0 ~ 1000ms, make it 1 */
       ngx_str_set(ttl_text, "1");
   } else if (ttl == 0){
       ngx_str_set(ttl_text, "0");
   } else {
       return "invalid value";
   }

   return NGX_CONF_OK;
}

/* check whether the client ip should be allowed to proceed, or whether
   the connection should be throttled
 */
void ngx_mail_throttle_ip (ngx_str_t ip, throttle_callback_t *callback)
{
    ngx_log_t       *log;
    ngx_pool_t      *pool;
    mc_work_t        w;
    ngx_str_t        k;
    ngx_str_t       *value, *eip, *key;

    pool = callback->pool;
    log = callback->log;

    ngx_log_error (NGX_LOG_INFO, log, 0, "check ip throttle:[%V]", &ip);

    w.ctx = callback;
    w.request_code = mcreq_incr;
    w.response_code = mcres_unknown;
    w.on_success = ngx_mail_throttle_ip_success_handler;
    w.on_failure = ngx_mail_throttle_ip_failure_handler;

    k = ngx_mail_throttle_get_ip_throttle_key(pool, log, ip);

    if (k.len == 0) {
        ngx_log_error (NGX_LOG_ERR, log, 0,
                "allowing ip %V login because of internal error"
                "in ip throttle control (generate key for incr)", &ip);
        callback->on_allow(callback);
        return;
    }

    key = ngx_pstrcpy (pool, &k);
    if (key == NULL) {
        ngx_log_error (NGX_LOG_ERR, log, 0,
                "allowing ip %V login because of internal error"
                "in ip throttle control (deep copy key for incr)", &ip);
        callback->on_allow(callback);
    }

    value = ngx_palloc (pool, sizeof(ngx_str_t));
    if (value == NULL) {
        ngx_log_error (NGX_LOG_ERR, log, 0,
                "allowing ip %V login because of internal error"
                "in ip throttle control (alloc mem for incr value)", &ip);
        callback->on_allow(callback);
        return;
    }

    ngx_str_set(value, "1");

    /* make a copy of the input IP address for callback reference */
    eip = ngx_pstrcpy (pool, &ip);
    if (eip == NULL) {
        ngx_log_error (NGX_LOG_ERR, log, 0,
                "allowing ip %V login because of internal error"
                "in ip throttle control (deep copy ip for incr)", &ip);
        callback->on_allow(callback);
        return;
    }

    callback->ip = eip;
    callback->value = value;
    callback->key = key;

    ngx_memcache_post(&w, *key, *value, /* pool */ NULL, log);
}

/* memcache handler (return counter for the specified ip or NOT_FOUND) */
static void ngx_mail_throttle_ip_success_handler (mc_work_t *w)
{
    ngx_mail_throttle_srv_conf_t * tscf;
    throttle_callback_t     *callback = w->ctx;
    ngx_str_t                ip = *callback->ip;
    ngx_mail_session_t      *s = callback->session;
    size_t                   hits;
    ngx_str_t                counter;

    /* the increment was successful - deep copy w->payload to counter */
    counter.data = ngx_pstrdup (callback->pool, &w->payload);

    if (counter.data == NULL) {
        /* enomem */
        counter = throttle_zero;    /* "0" */
    } else {
        counter.len = w->payload.len;
    }

    /* check if the limit has exceeded */
    ngx_log_debug2(NGX_LOG_DEBUG_MAIL, callback->log, 0,
        "ip throttle:%V is %V", &ip, &counter);

    hits = ngx_atoi(counter.data, counter.len);

    tscf = ngx_mail_get_module_srv_conf(s, ngx_mail_throttle_module);
    if (tscf->mail_login_ip_max == 0) {
        //should never reach here because mail handler won't
        //start throttle control if it's unlimited.
        ngx_log_error (NGX_LOG_INFO, callback->log, 0,
            "ip throttle:[%V] allow [count:%d, limit:inf]",
            &ip, hits);
        callback->on_allow(callback);
    } else if (hits <= tscf->mail_login_ip_max) {
        ngx_log_error (NGX_LOG_INFO, callback->log, 0,
            "ip throttle:[%V] allow [count:%d, limit:%d]",
            &ip, hits, tscf->mail_login_ip_max);
        callback->on_allow(callback);
    } else {
        ngx_log_error (NGX_LOG_NOTICE, callback->log, 0,
            "ip throttle:[%V] deny [count:%d, limit:%d]",
            &ip, hits, tscf->mail_login_ip_max);
        callback->on_deny(callback);
    }
}

static void ngx_mail_throttle_ip_failure_handler (mc_work_t *w)
{
    throttle_callback_t  *callback = w->ctx;
    ngx_log_t            *log = callback->log;
    if (w->response_code == mcres_failure_normal) {
        /* increment failed, we must begin to add counter for this ip */
        ngx_log_debug1(NGX_LOG_DEBUG_MAIL, log, 0,
            "ip throttle:%V create counter", callback->ip);
        ngx_mail_throttle_ip_add (callback->ip, callback);

    } else if (w->response_code == mcres_failure_again) {
        mc_work_t nw; /* create a new work entry */
        nw.ctx = callback;
        nw.request_code = mcreq_incr;
        nw.response_code = mcres_unknown;
        nw.on_success = ngx_mail_throttle_ip_success_handler;
        nw.on_failure = ngx_mail_throttle_ip_failure_handler;
        ngx_log_error (NGX_LOG_NOTICE, log, 0,
                "retry to check ip throttle:[%V]", callback->ip);
        ngx_memcache_post(&nw, *callback->key, *callback->value,
                          /* pool */ NULL, log);

    } else { /* mcres_failure_unavailable */
        ngx_log_error (NGX_LOG_ERR, log, 0,
             "throttle allowing access from ip %V because of "
             "memcache service is unavailable when try to "
             "increment ip counter", callback->ip);
        callback->on_allow(callback);
    }
}

static void ngx_mail_throttle_ip_add
    (ngx_str_t *ip, throttle_callback_t *callback)
{
    ngx_pool_t     *pool    = callback->pool;
    ngx_log_t      *log     = callback->log;
    ngx_mail_session_t  *s  = callback->session;
    ngx_mail_throttle_srv_conf_t * tscf;
    mc_work_t       w;
    ngx_str_t       k, value;
    ngx_str_t      *key;

    ngx_log_error (NGX_LOG_INFO, log, 0, "counter for %V not found, "
                        "create ip throttle counter", ip);

    w.ctx = callback;
    w.request_code = mcreq_add;
    w.response_code = mcres_unknown;
    w.on_success = ngx_mail_throttle_ip_add_success_handler;
    w.on_failure = ngx_mail_throttle_ip_add_failure_handler;

    /* use ttl for discrete time sampling of ip login hits */
    tscf = ngx_mail_get_module_srv_conf(s, ngx_mail_throttle_module);

    k = ngx_mail_throttle_get_ip_throttle_key(pool, log, *ip);

    if (k.len == 0) {
        ngx_log_error (NGX_LOG_ERR, log, 0,
               "allowing ip %V login because of internal error "
               "in ip throttle control (generate key for add)", ip);
        callback->on_allow(callback);
        return;
    }

    key = ngx_pstrcpy (pool, &k);
    if (key == NULL) {
        ngx_log_error (NGX_LOG_ERR, log, 0,
               "allowing ip %V login because of internal error "
               "in ip throttle control (deep copy key for add)", ip);
    }

    ngx_str_set(&value, "1");

    callback->value = ngx_pstrcpy (pool, &value);
    if (key == NULL) {
        ngx_log_error (NGX_LOG_ERR, log, 0,
               "allowing ip %V login because of internal error "
               "in ip throttle control (deep copy key for add)", ip);
    }
    callback->key = key;
    callback->ip = ip;
    callback->ttl = &tscf->mail_login_ip_ttl_text;

    ngx_memcache_post_with_ttl(&w, *key, value, tscf->mail_login_ip_ttl_text,/* pool */ NULL, log);
}

static void ngx_mail_throttle_ip_add_success_handler(mc_work_t *w)
{
    throttle_callback_t *callback = w->ctx;
    ngx_log_t           *log = callback->log;

    /* counter addition succeeded */
    ngx_log_error (NGX_LOG_INFO, log, 0,
        "ip throttle:%V counter created and access allowed", callback->ip);
    /* TODO handle extreme case where ip_limit is 0 or 1 */
    callback->on_allow(callback);
}

/* memcache error handler (connection error or memory allocation error) */
static void ngx_mail_throttle_ip_add_failure_handler (mc_work_t *w)
{
    throttle_callback_t   *callback = w->ctx;
    ngx_log_t             *log = callback->log;

    if (w->response_code == mcres_failure_normal) {
        /* Counter creation failed because of getting "NOT_STORED". This could
         * occur when more than one processes try to login and post "incr"
         * and all get "NOT_FOUND", and then try to add new counter. One of
         * them will get "STORED" and others will reach here. In some other
         * extreme cases, such as the ttl is very short, or some mis-handling
         * of memcache, this case may also happen. Considering the little
         * probability and the endurable inaccuracy, just ignore it.
         */
        ngx_log_error (NGX_LOG_NOTICE, log, 0,
            "allowing ip %V login because unable to create the "
            "ip counter", callback->ip);
        callback->on_allow(callback);

    } else if (w->response_code == mcres_failure_again) {
        mc_work_t nw;
        nw.ctx = callback;
        nw.request_code = mcreq_add;
        nw.response_code = mcres_unknown;
        nw.on_success = ngx_mail_throttle_ip_add_success_handler;
        nw.on_failure = ngx_mail_throttle_ip_add_failure_handler;
        ngx_log_error (NGX_LOG_NOTICE, log, 0,
                "retry to check ip throttle:[%V]", callback->ip);
        ngx_memcache_post_with_ttl(&nw, *callback->key, *callback->value,
                *callback->ttl, /* pool */ NULL, callback->log);
    } else { /* mcres_failure_unavailable */
        ngx_log_error (NGX_LOG_ERR, log, 0,
                "throttle allowing access from ip %V because "
                "error occurs in memcache module when try to "
                "create counter", callback->ip);
        callback->on_allow(callback);
    }
}

/* check whether the client user name should be allowed to proceed, or whether
   the connection should be throttled
 */
void ngx_mail_throttle_user (ngx_str_t user, throttle_callback_t *callback)
{
    ngx_pool_t          *pool;
    ngx_log_t           *log;
    ngx_connection_t    *c;
    ngx_str_t           *cusr;
    ngx_mail_session_t  *s;
    mc_work_t            w;
    ngx_str_t            proxyip;
    ngx_str_t           *dummy_value;

    pool = callback->pool;
    log = callback->log;
    c = callback->connection;
    s = callback->session;

    ngx_log_debug1 (NGX_LOG_DEBUG_MAIL, log, 0,
        "user throttle: lookup alias, user:%V", &user);

    /* save a copy of the user name */
    cusr = ngx_pstrcpy(pool, &user);
    if (cusr == NULL) {
        ngx_log_error (NGX_LOG_ERR, log, 0,
                "allowing user %V login because of internal error "
                "in user throttle control (deep copy user for incr)", &user);
        callback->on_allow(callback);
        return;
    }

    if (s->vlogin) {
        /* user alias has already been looked up */
        ngx_log_debug1 (NGX_LOG_DEBUG_MAIL, log, 0,
            "user throttle: skip alias lookup, user:%V", &user);
        ngx_mail_throttle_quser(cusr, callback);
        return;
    }

    w.ctx = callback;
    w.request_code = mcreq_get;
    w.response_code = mcres_unknown;
    w.on_success = ngx_mail_throttle_user_success_handler;
    w.on_failure = ngx_mail_throttle_user_failure_handler;

    /* GSSAPI workaround: don't lookup aliases for GSSAPI */
    if (s->auth_method == NGX_MAIL_AUTH_GSSAPI) {
        ngx_log_error(NGX_LOG_INFO, log, 0,
            "not looking up cached aliases for auth=gssapi");
        ngx_mail_throttle_quser(cusr, callback);
        return;
    }

    /* first stringify the proxy-ip address */
    proxyip = ngx_mail_get_socket_local_addr_str (pool, c->fd);

    s->key_alias = ngx_zm_lookup_get_mail_alias_key(
            pool,
            log,
            *cusr,
            proxyip
        );

    if (s->key_alias.len == 0) {
        ngx_log_error (NGX_LOG_ERR, log, 0,
                "allowing user %V login because of internal error "
                "in user throttle control (create alias key)", &user);
        callback->on_allow(callback);
        return;
    }

    dummy_value = ngx_palloc (pool, sizeof (ngx_str_t));

    ngx_str_null (dummy_value);

    callback->key = &s->key_alias;
    callback->value = dummy_value;
    callback->user = cusr;

    ngx_memcache_post(&w, s->key_alias, *dummy_value,/* pool */ NULL, log);
}

/* callback to replace login user name with an alias, if any */
static void ngx_mail_throttle_user_success_handler (mc_work_t *w)
{
    throttle_callback_t     *callback = w->ctx;
    ngx_mail_session_t      *s = callback->session;
    ngx_str_t                login; //full qualified name

    /* deep copy w->payload onto s->login (pool is callback->pool) */
    login.data = ngx_pstrdup (callback->pool, &w->payload);
    if (login.data != NULL)
    {
        login.len = w->payload.len;
        s->vlogin = 2; // lookup alias and found
        s->qlogin = login;
        ngx_log_error (NGX_LOG_INFO, callback->log, 0,
            "user throttle: alias %V replaced by %V",
            callback->user, &s->login);
        ngx_mail_throttle_quser (&s->login, callback);
    } else {
        ngx_mail_throttle_quser (callback->user, callback);
    }
}

static void ngx_mail_throttle_user_failure_handler (mc_work_t *w)
{
    throttle_callback_t  *callback = w->ctx;
    ngx_str_t            *user = callback->user;
    ngx_mail_session_t   *s = callback->session;
    ngx_log_t            *log = callback->log;

    if (w->response_code == mcres_failure_normal) {
        //NOT_FOUND
        ngx_log_error(NGX_LOG_INFO, log, 0,
            "user throttle: no alias for user:%V",
            user);
        s->vlogin = 1;  /* avoid duplicate lookups for alias */
        s->qlogin = s->login;
        ngx_mail_throttle_quser (callback->user, callback);

    } else if(w->response_code == mcres_failure_again) {
        mc_work_t nw;
        nw.ctx = callback;
        nw.request_code = mcreq_get;
        nw.response_code = mcres_unknown;
        nw.on_success = ngx_mail_throttle_user_success_handler;
        nw.on_failure = ngx_mail_throttle_user_failure_handler;
        ngx_log_error (NGX_LOG_NOTICE, callback->log, 0,
                "retry to lookup alias %V before user throttle", callback->user);
        ngx_memcache_post(&nw, *callback->key, *callback->value,
                            /* pool */ NULL, log);

    } else if(w->response_code == mcres_failure_input) {
        ngx_log_error (NGX_LOG_ERR, log, 0,
                       "throttle deny since the user name %V is invalid",
                       callback->user);
        callback->on_deny(callback);
    } else { /* mcres_failure_unavailable */
        ngx_log_error (NGX_LOG_ERR, log, 0,
                "throttle allowing access from user %V because "
                "memcache service is unavailable when try to "
                "perform alias lookup", callback->user);
        callback->on_allow(callback);
    }
}

/* same as ngx_mail_throttle_user, but works on a fully qualified user name */
static void ngx_mail_throttle_quser (ngx_str_t * quser, throttle_callback_t *callback)
{
    ngx_log_t           *log;
    ngx_pool_t          *pool;
    mc_work_t            w;
    ngx_str_t            k;
    ngx_str_t           *value, *key;
    ngx_flag_t           check_only;

    pool = callback->pool;
    log = callback->log;
    check_only = callback->check_only;

    k = ngx_mail_throttle_get_user_throttle_key(pool, log, *quser);
    if (k.len == 0) {
        ngx_log_error (NGX_LOG_ERR, log, 0,
            "allowing user %V login because of internal error "
            "in user throttle control (generate key for get)", quser);
        callback->on_allow(callback);
        return;
    }

    key = ngx_pstrcpy (pool, &k);
    if (key == NULL) {
        ngx_log_error (NGX_LOG_ERR, log, 0,
            "allowing user %V login because of internal error "
            "in user throttle control (deep copy check user key)", quser);
        callback->on_allow(callback);
    }

    if (check_only == 0)
    {   // try to increment the counter for this user
        ngx_log_error (NGX_LOG_INFO, log, 0, "check user throttle:%V", quser);
        w.ctx = callback;
        w.request_code = mcreq_incr;
        w.response_code = mcres_unknown;
        w.on_success = ngx_mail_throttle_quser_success_handler;
        w.on_failure = ngx_mail_throttle_quser_failure_handler;

        value = ngx_palloc (pool, sizeof(ngx_str_t));
        if (value == NULL) {
            ngx_log_error (NGX_LOG_ERR, log, 0,
                    "allowing user %V login because of internal error"
                    "in ip throttle control (alloc mem for incr value)", quser);
            callback->on_allow(callback);
            return;
        }

        ngx_str_set(value, "1");
    }
    else
    {   // just check the counter
        ngx_log_error (NGX_LOG_INFO, log, 0, "check user throttle:%V, check only", quser);
        w.ctx = callback;
        w.request_code = mcreq_get;
        w.response_code = mcres_unknown;
        w.on_success = ngx_mail_throttle_quser_success_handler;
        w.on_failure = ngx_mail_throttle_quser_failure_handler;

        value = ngx_palloc (pool, sizeof(ngx_str_t));
        if (value == NULL) {
            ngx_log_error (NGX_LOG_ERR, log, 0,
                    "allowing ip %V login because of internal error"
                    "in user throttle control (alloc mem for get value)", quser);
            callback->on_allow(callback);
            return;
        }

        ngx_str_null (value);
    }

    callback->key = key;
    callback->value = value;
    callback->user = quser;

    ngx_memcache_post(&w, *key, *value,/* pool */ NULL, log);
}

static void ngx_mail_throttle_quser_success_handler (mc_work_t *w)
{
    throttle_callback_t     *callback = w->ctx;
    ngx_mail_session_t      *s = callback->session;
    ngx_log_t               *log = callback->log;
    ngx_mail_throttle_srv_conf_t * tscf;
    size_t                   hits;
    ngx_str_t                counter;

    /* increment succeeded / get succeeded */
    counter.data = ngx_pstrdup(callback->pool, &w->payload);

    if (counter.data == NULL) { /* enomem */
        counter = throttle_zero;
    } else {
        counter.len = w->payload.len;
    }

    /* check if the limit has exceeded */
    ngx_log_debug2 (NGX_LOG_DEBUG_MAIL, log, 0,
        "user throttle:%V is %V", callback->user, &counter);

    hits = ngx_atoi (counter.data, counter.len);

    tscf = ngx_mail_get_module_srv_conf (s, ngx_mail_throttle_module);
    if (tscf->mail_login_user_max == 0) {
        //should never reach here because unlimited case has been handled
        ngx_log_error (NGX_LOG_INFO, log, 0,
            "user throttle:%V allow [count:%d,limit:inf]",
            callback->user, hits);
        callback->on_allow(callback);
    } else if (hits <= tscf->mail_login_user_max) {
        ngx_log_error (NGX_LOG_INFO, log, 0,
            "user throttle:%V allow [count:%d,limit:%d]",
            callback->user, hits, tscf->mail_login_user_max);
        callback->on_allow(callback);
    } else {
        ngx_log_error (NGX_LOG_NOTICE, log, 0,
            "user throttle:%V deny [count:%d,limit:%d]",
            callback->user, hits, tscf->mail_login_user_max);
        callback->on_deny(callback);
    }
}

static void ngx_mail_throttle_quser_failure_handler (mc_work_t *w)
{
    throttle_callback_t *callback = w->ctx;
    ngx_log_t           *log = callback->log;
    ngx_flag_t           check_only = callback->check_only;

    if (w->response_code == mcres_failure_normal) {
        if (check_only) {
           ngx_log_error (NGX_LOG_INFO, log, 0,
                "user throttle:%V not found, allow due to check only",
                callback->user);
           callback->on_allow(callback);
       } else {
           ngx_log_debug1(NGX_LOG_DEBUG_MAIL, log, 0,
                "user throttle:%V add counter", callback->user);
           ngx_mail_throttle_user_add(callback->user, callback);
       }

    } else if (w->response_code == mcres_failure_again) {
        mc_work_t nw;
        nw.ctx = callback;
        if (check_only) {
            nw.request_code = mcreq_get;
        } else {
            nw.request_code = mcreq_incr;
        }
        nw.response_code = mcres_unknown;
        nw.on_success = ngx_mail_throttle_quser_success_handler;
        nw.on_failure = ngx_mail_throttle_quser_failure_handler;
        ngx_log_error (NGX_LOG_NOTICE, log, 0,
                "retry to check user throttle:%V", callback->user);
        ngx_memcache_post(&nw, *callback->key, *callback->value,
                            /* pool */ NULL, log);
        } else if(w->response_code == mcres_failure_input) {
            ngx_log_error (NGX_LOG_ERR, log, 0,
                       "throttle deny since the user name %V is invalid",
                       callback->user);
            callback->on_deny(callback);
        } else { /* mcres_failure_unavailable */
            if (check_only) {
                ngx_log_error (NGX_LOG_ERR, log, 0,
                    "throttle allowing access from user %V because "
                    "memcache service is unavailable when try to "
                    "get user counter", callback->user);
            } else {
                ngx_log_error (NGX_LOG_ERR, log, 0,
                    "throttle allowing access from user %V because "
                    "memcache service is unavailable when try to "
                    "increment user counter", callback->user);
            }
        callback->on_allow(callback);
    }
}

/* add a throttle counter for a user, here user might be an alias or a fqn */
static void ngx_mail_throttle_user_add
    (ngx_str_t *user, throttle_callback_t *callback)
{
    ngx_pool_t             *pool    = callback->pool;
    ngx_log_t              *log     = callback->log;
    ngx_mail_session_t     *s       = callback->session;
    ngx_mail_throttle_srv_conf_t *tscf;
    mc_work_t               w;
    ngx_str_t               k;
    ngx_str_t              *value, *key;

    ngx_log_error (NGX_LOG_INFO, log, 0, "create a throttle counter for user %V", user);
    w.ctx = callback;
    w.request_code = mcreq_add;
    w.response_code = mcres_unknown;
    w.on_success = ngx_mail_throttle_user_add_success_handler;
    w.on_failure = ngx_mail_throttle_user_add_failure_handler;

    k = ngx_mail_throttle_get_user_throttle_key(pool, log, *user);
    if (k.len == 0) {
        ngx_log_error (NGX_LOG_ERR, log, 0,
            "allowing user %V login because of internal error "
            "in user throttle control (generate key for add)", user);
        callback->on_allow(callback);
        return;
    }

    key = ngx_pstrcpy (pool, &k);
    if (key == NULL) {
        ngx_log_error (NGX_LOG_ERR, log, 0,
            "allowing user %V login because of internal error "
            "in user throttle control (deep copy key for add)", user);
        callback->on_allow(callback);
        return;
    }

    tscf = ngx_mail_get_module_srv_conf (s, ngx_mail_throttle_module);

    value = ngx_palloc (pool, sizeof(ngx_str_t));
    if (value == NULL) {
       ngx_log_error (NGX_LOG_ERR, log, 0,
               "allowing user %V login because of internal error"
               "in user throttle control (alloc mem for add value)", user);
       callback->on_allow(callback);
       return;
    }

    ngx_str_set(value, "1");

    callback->key = key;
    callback->value = value;
    callback->user = user;
    callback->ttl = &tscf->mail_login_user_ttl_text;

    ngx_memcache_post_with_ttl(&w, *key, *value, tscf->mail_login_user_ttl_text,
            /* pool */ NULL, log);
    return;
}

static void ngx_mail_throttle_user_add_success_handler(mc_work_t *w)
{
    throttle_callback_t *callback = w->ctx;
    ngx_log_t           *log = callback->log;

    /* counter addition succeeded */
    ngx_log_error (NGX_LOG_INFO, log, 0,
        "throttle allowing access from user %V, counter created",
        callback->user);
    callback->on_allow(callback);
}

/* memcache error handler (connection error or memory allocation error) */
static void ngx_mail_throttle_user_add_failure_handler (mc_work_t *w)
{
    throttle_callback_t  *callback = w->ctx;
    ngx_log_t            *log = callback->log;

    if (w->response_code == mcres_failure_normal) {
        /* Counter creation failed because of getting "NOT_STORED". This could
         * occur when more than one processes try to login and post "incr"
         * and all get "NOT_FOUND", and then try to add new counter. One of
         * them will get "STORED" and others will reach here. In some other
         * extreme cases, such as the ttl is very short, or some mis-handling
         * of memcache, this case may also happen. Considering the little
         * probability and the endurable inaccuracy, just ignore it.
         */
        ngx_log_error (NGX_LOG_NOTICE, log, 0,
            "allowing user %V login because unable to create the "
            "user counter", callback->user);
        callback->on_allow(callback);

    } else if (w->response_code == mcres_failure_again) {
        mc_work_t nw;
        nw.ctx = callback;
        nw.request_code = mcreq_add;
        nw.response_code = mcres_unknown;
        nw.on_success = ngx_mail_throttle_user_add_success_handler;
        nw.on_failure = ngx_mail_throttle_user_add_failure_handler;
        ngx_log_error (NGX_LOG_NOTICE, log, 0,
                "retry to check user throttle:%V", callback->user);
        ngx_memcache_post_with_ttl(&nw, *callback->key, *callback->value,
                            *callback->ttl, /* pool */ NULL, log);
    } else {
        ngx_log_error (NGX_LOG_ERR, log, 0,
            "throttle allowing access from ip %V because "
            "memcache service is unavailable when try to "
            "create user counter", callback->user);
        callback->on_allow(callback);
    }
}

static ngx_str_t
ngx_mail_throttle_get_user_throttle_key (
    ngx_pool_t      *pool,
    ngx_log_t       *log,
    ngx_str_t        user
)
{
    ngx_str_t       k;
    u_char         *p;
    size_t          l;
    uintptr_t       escape;

    escape = 2 * ngx_escape_uri(NULL, user.data, user.len,
            NGX_ESCAPE_MEMCACHED);

    l = sizeof("throttle:") - 1 +
        sizeof("user=") - 1 +
        user.len + escape;

    k.data = ngx_palloc(pool, l);
    if (k.data == NULL)
    {
        k.len = 0;
        return k;
    }

    p = k.data;
    p = ngx_cpymem(p, "throttle:", sizeof("throttle:") - 1);
    p = ngx_cpymem(p, "user=", sizeof("user=") - 1);

    if (escape == 0) {
        p = ngx_cpymem(p, user.data, user.len);
    } else {
        p = (u_char *)ngx_escape_uri(p, user.data, user.len,
                NGX_ESCAPE_MEMCACHED);
    }

    k.len = p - k.data;

    return k;
}

static ngx_str_t
ngx_mail_throttle_get_ip_throttle_key (
    ngx_pool_t      *pool,
    ngx_log_t       *log,
    ngx_str_t        ip
)
{
    ngx_str_t   k;
    size_t      l;
    u_char     *p;

    l = sizeof("throttle:") - 1 +
        sizeof("ip=") - 1 +
        ip.len;

    k.data = ngx_palloc(pool, l);
    if (k.data == NULL)
    {
        k.len = 0;
        return k;
    }

    p = k.data;
    p = ngx_cpymem(p, "throttle:", sizeof("throttle:") - 1);
    p = ngx_cpymem(p, "ip=", sizeof("ip=") - 1);
    p = ngx_cpymem(p, ip.data, ip.len);

    k.len = p - k.data;

    return k;
}

