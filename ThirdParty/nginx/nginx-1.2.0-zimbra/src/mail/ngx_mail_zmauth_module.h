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
