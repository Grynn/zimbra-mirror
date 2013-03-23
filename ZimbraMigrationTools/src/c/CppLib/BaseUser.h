/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite CSharp Client
 * Copyright (C) 2011, 2012 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
#pragma once

#include "OaIdl.h"
#include "Wtypes.h"

class BaseUser
{
public:
    BaseUser(void) {}
    virtual ~BaseUser(void) {}

    /*
    virtual long Init(BSTR Id) = 0;
    virtual long GetFolders(VARIANT *folders) = 0;
    virtual long GetItems(VARIANT *Items) = 0;
    virtual void Uninit(void) = 0;
    */

protected:
    BSTR MailType;
    BSTR UserID;
};
