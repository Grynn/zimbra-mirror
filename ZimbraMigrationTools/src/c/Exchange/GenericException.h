#pragma once
#include <windows.h>
#include <string>

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
	};
	GenericException(HRESULT hrErrCode, LPCWSTR lpszDescription,int nLine, LPCSTR strFile)
	{
		m_errcode = hrErrCode;
		m_strdescription = lpszDescription;
		m_srcLine = nLine;
		m_srcFile = strFile;
	}
	virtual ~GenericException(){};
};

