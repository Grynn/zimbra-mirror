#pragma once
#include "OaIdl.h"
#include "Wtypes.h"

/*
 * enum FolderType
 *  {	Mail	= 1,
 *      Contacts	= 2,
 *      Calendar	= 3
 *  }   FolderType;*/

class BaseItem
{
protected:
    BSTR ID;
    // FolderType TYPE; //todo:clena up exchange and use enum defnition here.

public:
    BaseItem(void)
    {}
    virtual ~BaseItem(void)
    {}
};
