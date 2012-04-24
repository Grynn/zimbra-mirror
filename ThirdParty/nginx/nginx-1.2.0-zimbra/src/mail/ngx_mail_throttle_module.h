/*
 * Copyright (c) VMware, Inc. [1998 – 2011]. All Rights Reserved.
 *
 * For more information, see –
 * http://vmweb.vmware.com/legal/corporate/VMwareCopyrightPatentandTrademarkNotices.pdf
 */

#ifndef _NGX_MAIL_THROTTLE_H_INCLUDED_
#define _NGX_MAIL_THROTTLE_H_INCLUDED_

#include <ngx_core.h>
#include <ngx_event.h>
#include <ngx_mail.h>
#include <ngx_memcache.h>

struct ngx_mail_throttle_srv_conf_s {
    ngx_uint_t  mail_login_ip_max;
    ngx_msec_t  mail_login_ip_ttl;
    ngx_str_t   mail_login_ip_ttl_text;
    ngx_str_t   mail_login_ip_rejectmsg;
    ngx_uint_t  mail_login_user_max;
    ngx_msec_t  mail_login_user_ttl;
    ngx_str_t   mail_login_user_ttl_text;
    ngx_str_t   mail_login_user_rejectmsg;
};
typedef struct ngx_mail_throttle_srv_conf_s ngx_mail_throttle_srv_conf_t;

struct throttle_callback_s;
typedef void (*throttle_handler_pt) (struct throttle_callback_s*);
typedef void * throttle_ctx_t;

struct throttle_callback_s {
    ngx_flag_t              check_only; /* whether just check the counter or increment it */
    ngx_mail_session_t     *session; /* current mail session */
    ngx_connection_t       *connection; /* current connection */
    ngx_event_t            *rev;    /* current read event */
    void                   *config; /* pointer to a configuration */
    ngx_log_t              *log;
    ngx_pool_t             *pool;
    throttle_handler_pt     on_allow; /* handler for allow access */
    throttle_handler_pt     on_deny;  /* handler for deny access */

    /* the following fields are used internally by throttle control */
    ngx_str_t              *user; /* user name used by user throttle control */
    ngx_str_t              *ip;   /* ip address used by ip throttle control */
    ngx_str_t              *value;/* the value for re-post memcache request */
    ngx_str_t              *key;  /* the key for re-post memcache request */
    ngx_str_t              *ttl;  /* the ttl value for re-post memcache request */
};
typedef struct throttle_callback_s throttle_callback_t;

ngx_flag_t ngx_mail_throttle_init (ngx_mail_core_srv_conf_t *cscf);
void ngx_mail_throttle_ip (ngx_str_t ip, throttle_callback_t *callback);
void ngx_mail_throttle_user (ngx_str_t user, throttle_callback_t *callback);

extern ngx_module_t ngx_mail_throttle_module;

#endif
