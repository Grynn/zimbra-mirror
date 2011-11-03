#pragma once
#include "OaIdl.h"
#include "Wtypes.h"
#include  "Logger.h"

class BaseUser
{
protected:
    BSTR UserID;
    BSTR MailType;
    CSingleton *m_pLogger;

public:
    BaseUser(void) {}
    virtual ~BaseUser(void) {}

    virtual long Initialize(BSTR Id) = 0;
    virtual long GetFolders(VARIANT *folders) = 0;
    virtual long GetItems(VARIANT *Items) = 0;
    virtual long UnInitialize() = 0;
};
