#pragma once
#include "OaIdl.h"
#include "Wtypes.h"

class BaseFolder
{
protected:
    BSTR Strname;
    LONG LngID;
    BSTR parentPath;
    SHORT ZimbraID;

public:
    BaseFolder(void)
    {}
    virtual ~BaseFolder(void)
    {}
};
