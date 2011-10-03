#include "common.h"
#include "Testunmanaged.h"

/*Testunmanaged::Testunmanaged(void)
 * {
 * }
 *
 *
 * Testunmanaged::~Testunmanaged(void)
 * {
 * }
 */
extern "C" CPPLIB_DLLAPI CSingleton *GetInstance()
{
    CSingleton *pv = CSingleton::getInstance();

    return pv;
}

extern "C" CPPLIB_DLLAPI void DisposeTestClass(CSingleton *pObject)
{
    if (pObject != NULL)
    {
        // delete pObject;
        pObject = NULL;
    }
}

extern "C" CPPLIB_DLLAPI void CallDoSomething(CSingleton *pObject, char *pchValue, LogType type)
{
    if (pObject != NULL)
        pObject->doSomething(type, pchValue);
}
