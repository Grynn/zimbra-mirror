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
