#pragma once
/*class Testunmanaged
{
public:
	Testunmanaged(void);
	~Testunmanaged(void);
};*/

#include "Logger.h"		// needed for EXAMPLEUNMANAGEDDLL_API

#ifdef __cplusplus
extern "C" {
#endif
	
extern LOGGERDLL_API CSingleton* GetInstance();
extern LOGGERDLL_API void DisposeTestClass(CSingleton* pObject);

//extern LOGGERDLL_API void Callinit(CSingleton* pObject);
extern LOGGERDLL_API void CallDoSomething(CSingleton* pObject, char* pchValue,LogType type);
//extern LOGGERDLL_API char* CallReturnString(CUnmanagedTestClass* pObject);

#ifdef __cplusplus
}
#endif
