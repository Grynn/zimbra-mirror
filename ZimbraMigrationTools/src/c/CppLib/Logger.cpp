#include "common.h"
#include "Logger.h"

extern "C"
{
CSingleton *CSingleton::m_pInstance = NULL;

CSingleton *CSingleton::getInstance()
{
    if (NULL == m_pInstance)
    {
        m_pInstance = new CSingleton();
    }
    return m_pInstance;
}

void CSingleton::destroyInstance()
{
    delete m_pInstance;
    m_pInstance = NULL;
}

void CSingleton::doSomething(LogType type, char *Msg)
{
    m_pInstance->init();
    CString strTemp;
    CString dest(Msg);
    // LogType type = DBG;
    // char* Temp = "DEBUG:";
    char *Temp = new char[100];
    switch (type)
    {
    case DBG:                                   // strTemp.Format( _T("( DEBUG: %s"), Msg );
    {
        strcpy(Temp, "Debug :");
        strcat(Temp, Msg);
        strcat(Temp, "\r\n");
    }
    break;

    case INFO:
    {
        strcpy(Temp, "Info :");
        strcat(Temp, Msg);
        strcat(Temp, "\r\n");
    }
    break;
    case ERR:
    {
        strcpy(Temp, "Error :");
        strcat(Temp, Msg);
        strcat(Temp, "\r\n");
    }
    break;
    case WARN:
    {
        strcpy(Temp, "Warning :");
        strcat(Temp, Msg);
        strcat(Temp, "\r\n");
    }
    break;
    default:
        break;
    }
    size_t ulLen = (strlen(Temp));

    DWORD dwWrittenBytes = NULL;
    // WriteFile( m_LogFileHandle, strTemp, strTemp.GetLength(), &dwWrittenBytes, NULL );
    WriteFile(m_LogFileHandle, Temp, (DWORD)ulLen, &dwWrittenBytes, NULL);
    CloseHandle(m_LogFileHandle);
}

void CSingleton::init()
{
    DWORD dwFileFlags = FILE_APPEND_DATA | GENERIC_WRITE;

    unsigned long ulCreateFlags = OPEN_ALWAYS;
    unsigned long ulError = NO_ERROR;
    unsigned long ulPos = NULL;

    m_LogFileHandle =
            CreateFile(L"C:\\Temp\\Migrationlog.log", dwFileFlags, FILE_SHARE_READ |
            FILE_SHARE_WRITE,
            NULL, OPEN_EXISTING, 0,
            NULL);
    if (m_LogFileHandle == INVALID_HANDLE_VALUE)
    {
        // Open the file to write to......using CreateFileA to
        // force the use of the ANSI version of this function
        m_LogFileHandle = CreateFile(L"C:\\Temp\\Migrationlog.log",
                dwFileFlags,
                0,
                NULL,
                ulCreateFlags,
                FILE_ATTRIBUTE_NORMAL,
                NULL);

        // check to see if the file was opened properly
    }
    else
    {
        if (m_LogFileHandle != INVALID_HANDLE_VALUE)
        {
            // since we are appending to the file, move the file pointer
            // to the end of the file
            ulPos = SetFilePointer(m_LogFileHandle,
                    0,
                    NULL,
                    FILE_END);
            // make sure the file pointer move was successful
            if (GetLastError() == NO_ERROR)
            {
                // since we were unable to set the
                // file pointer, get the error code.
                ulError = GetLastError();

                // close the file handle
                // CloseHandle ( m_LogFileHandle );
            }
        }
    }
}
}
