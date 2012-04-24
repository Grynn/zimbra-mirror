/*
 * Copyright (c) VMware, Inc. [1998 – 2011]. All Rights Reserved.
 *
 * For more information, see –
 * http://vmweb.vmware.com/legal/corporate/VMwareCopyrightPatentandTrademarkNotices.pdf
 */

#ifndef _NGX_MAIL_ZMAUTH_MODULE_H_INCLUDED_
#define _NGX_MAIL_ZMAUTH_MODULE_H_INCLUDED_

typedef struct ngx_mail_zmauth_ctx_s  ngx_mail_zmauth_ctx_t;

typedef struct {
    ngx_flag_t    use_zmauth;
} ngx_mail_zmauth_conf_t;

/* zmauth portal */
void ngx_mail_zmauth_init(ngx_mail_session_t *s);

/* utility */
ngx_flag_t has_zimbra_extensions (ngx_str_t login);
ngx_str_t strip_zimbra_extensions (ngx_str_t login);
ngx_str_t get_zimbra_extension (ngx_str_t login);

extern ngx_module_t ngx_mail_zmauth_module;

#endif
