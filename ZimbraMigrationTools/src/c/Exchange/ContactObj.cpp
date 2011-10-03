#include "common.h"
#include "ContactObj.h"

ContactObj::ContactObj(void)
{}

ContactObj::~ContactObj(void)
{}

void ContactObj::GetData(contactattributes &attributes)
{
    attributes[L"firstname"] = L"karuna";
    attributes[L"lastname"] = L"nuthi";
    attributes[L"phone"] = L"650-996-4291";
    attributes[L"email"] = L"knuthi@vmware.com";
    attributes[L"location"] = L"PaloALto";
}
