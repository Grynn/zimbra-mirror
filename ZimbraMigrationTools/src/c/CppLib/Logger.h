#pragma once
#ifdef CPPLIB_EXPORTS
#define CPPLIB_DLLAPI __declspec(dllexport)
#else
#define CPPLIB_DLLAPI __declspec(dllimport)
#endif

extern "C" {
typedef enum
{
    DBG,
    INFO,
    WARN,
    ERR,
} LogType;

class CPPLIB_DLLAPI CSingleton
{
public:
    static CSingleton *getInstance();
    void destroyInstance();
    void doSomething(LogType type, char *Msg);

    // void doSomething(char* Msg);
    void init();

private:
    CSingleton() { _tcscpy(m_filename, _T("")); }
    ~CSingleton() {}
    static CSingleton *m_pInstance;

protected:
    TCHAR m_filename[MAX_PATH + 100];
    HANDLE m_LogFileHandle;
};
}
