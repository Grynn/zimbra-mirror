#pragma once

/*class Testunmanaged
 * {
 * public:
 *      Testunmanaged(void);
 *      ~Testunmanaged(void);
 * };*/

#include "Logger.h"                             // needed for EXAMPLEUNMANAGEDDLL_API

#ifdef __cplusplus
extern "C" {
#endif

extern CPPLIB_DLLAPI CSingleton *GetInstance();
extern CPPLIB_DLLAPI void DisposeTestClass(CSingleton *pObject);

// extern CPPLIB_DLLAPI void Callinit(CSingleton* pObject);
extern CPPLIB_DLLAPI void CallDoSomething(CSingleton *pObject, char *pchValue, LogType type);

// extern CPPLIB_DLLAPI char* CallReturnString(CUnmanagedTestClass* pObject);

#ifdef __cplusplus
}
#endif
