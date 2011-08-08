#pragma once

	#define LOGGERDLL_API __declspec(dllexport)

extern "C"
{
typedef enum
{
	DBG,
	INFO,
	WARN,
	ERR,


}LogType;

class  LOGGERDLL_API CSingleton{

public:
    static CSingleton* getInstance();
    void destroyInstance();
    void doSomething(LogType type,char* Msg);
	//void doSomething(char* Msg);
	void init();

private:
    CSingleton() {_tcscpy(m_filename, _T(""));}
    ~CSingleton(){}
    static CSingleton* m_pInstance;
protected:
	 TCHAR m_filename[MAX_PATH + 100];
	 HANDLE m_LogFileHandle;
};

}