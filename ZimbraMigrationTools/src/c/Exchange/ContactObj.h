#pragma once
#include <map>
#include <comutil.h>
typedef std::map<BSTR, BSTR> contactattributes;

class ContactObj
{
public:
    ContactObj(void);
    virtual ~ContactObj(void);
    void GetData(contactattributes &attributes);
};
