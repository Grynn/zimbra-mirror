#pragma once

class GenericException
{
private:
    HRESULT m_errcode;
    std::wstring m_strdescription;
    int m_srcLine;
    std::string m_srcFile;

public:
    GenericException(HRESULT hrErrCode, LPCWSTR lpszDescription)
    {
        m_errcode = hrErrCode;
        m_strdescription = lpszDescription;
    }
    GenericException(HRESULT hrErrCode, LPCWSTR lpszDescription, int nLine, LPCSTR strFile)
    {
        m_errcode = hrErrCode;
        m_strdescription = lpszDescription;
        m_srcLine = nLine;
        m_srcFile = strFile;
    }
    HRESULT ErrCode() { return m_errcode; }
    std::wstring Description() { return m_strdescription; }
    int SrcLine() { return m_srcLine; }
    std::string SrcFile() { return m_srcFile; }
    virtual ~GenericException() {}
};
