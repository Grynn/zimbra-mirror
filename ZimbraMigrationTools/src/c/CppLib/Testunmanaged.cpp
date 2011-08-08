#include "common.h"
#include "Testunmanaged.h"


/*Testunmanaged::Testunmanaged(void)
{
}


Testunmanaged::~Testunmanaged(void)
{
}
*/
extern "C" LOGGERDLL_API CSingleton* GetInstance()
{
	CSingleton *pv = CSingleton::getInstance();
	return pv;
	
}

extern "C" LOGGERDLL_API void DisposeTestClass(CSingleton* pObject)
{
	if(pObject != NULL)
	{
		//delete pObject;
		pObject = NULL;
	}
}


extern "C" LOGGERDLL_API void CallDoSomething(CSingleton* pObject, char* pchValue,LogType type)
{
	if(pObject != NULL)
	{
		pObject->doSomething(type,pchValue);
	}
}
