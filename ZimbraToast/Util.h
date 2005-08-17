#pragma once


namespace Liquid { namespace Util {

/**
 *  Free the memory associated with a string
 **/
inline void SafeDelete( LPWSTR& pStr )
{
	if( pStr != NULL )
	{
		delete [] pStr;
		pStr = NULL;
	}
}

/**
 * Allocate memory and copy pSrc into pDest
 **/
inline void CopyString( LPWSTR& pDest, LPWSTR pSrc )
{
	if( pSrc == NULL )
	{
		pDest = NULL;
		return;
	}

	int nLength = (int)wcslen(pSrc);
	pDest = new WCHAR[ nLength + 1 ];
	wcscpy(pDest, pSrc);
}


/**
 *  Release an IUnknown
 **/
#define SafeRelease(pUnk) \
if( pUnk != NULL ) \
{ \
	pUnk->Release(); \
	pUnk = NULL; \
} \


void HexEncode( LPBYTE pBuffer, DWORD nBufferBytes, LPWSTR& pHexEncodedBuffer );

/**
 *  Instantiate one of the these per thread to initialize com.
 **/
typedef struct _ComInit{
	_ComInit(){ CoInitializeEx(NULL, COINIT_MULTITHREADED); }
	~_ComInit(){ CoUninitialize(); }
} ComInit;



class XmlUtil
{
	public:
		//get the value of the first node in the node list
		static BOOL GetNodeValue( IXMLDOMNodeList* pNodes, LPWSTR& pValue );

		//gets the value of a give node (really the value of the child of the given node)
		static BOOL GetNodeValue( IXMLDOMNode* pNode, LPWSTR& pValue );

		//finds the value of the first occurance of a node identified by the xpath query
		static BOOL FindNodeValue( IXMLDOMDocument2* pDoc, LPWSTR pXPathQuery, LPWSTR& pResult );
		static BOOL FindNodeValue( IXMLDOMDocument2* pDoc, LPWSTR pXPathQuery, BOOL& bResult );
		static BOOL FindNodeValue( IXMLDOMDocument2* pDoc, LPWSTR pXPathQuery, UINT& uiResult );

		static BOOL FindNodeValue( IXMLDOMNode* pNode, LPWSTR pXPathQuery, LPWSTR& pResult );

		//return the value of the attribute of a node found by issuing an xpath query
		static BOOL FindNodeAttributeValue( IXMLDOMDocument2* pDoc, LPWSTR pXPathQuery, LPWSTR pAttrName, LPWSTR& pResult );
		static BOOL FindNodeAttributeValue( IXMLDOMNode*    pNode, LPWSTR pXPathQuery, LPWSTR pAttrName, LPWSTR& pResult );

		//returns the value of a given attribute of the node, or null if it doesn't exist
		static BOOL GetSingleAttribute( IXMLDOMNode* pNode, LPWSTR pAttrName, LPWSTR& pResult );

		//returns the value of a given attribute of the first node in the list, or null if the attr doesn't exist
		static BOOL GetSingleAttribute( IXMLDOMNodeList* pNodes, LPWSTR pAttrName, LPWSTR& pResult );
};


/**
 *
 *  XmlParseException
 *
 **/
class XmlParseException
{
	public:
		XmlParseException( IXMLDOMParseError* pError);
		XmlParseException( const XmlParseException& e );
		~XmlParseException();

		long ErrorCode(){ return _errorCode; }
		long FilePos(){ return _filepos; }
		long Line(){ return _line; }
		long LinePos(){ return _linepos; }
		LPCWSTR Reason(){ return _reason; }
		LPCWSTR Source(){ return _source; }
		LPCWSTR Url(){ return _url; }

	private:
		long _errorCode;
		long _filepos;
		long _line;
		long _linepos;
		LPWSTR _reason;
		LPWSTR _source;
		LPWSTR _url;
};
//
////some windows directory operations
//namespace ActiveDirectory
//{
//	extern HRESULT GetLoggedOnUserDN( BSTR* pbstrUserName );
//	extern HRESULT GetUserExServerDN( BSTR bstrUserDN, BSTR* pbstrLegacyExchangeDN );
//	extern HRESULT GetUserLegacyExDN( BSTR bstrUserDN, BSTR* pbstrLegacyExchangeDN );
//}
//


class CriticalSection
{
	public:
		CriticalSection(){ InitializeCriticalSection(&cs); }
		~CriticalSection(){ DeleteCriticalSection(&cs); }
		void Enter(){ EnterCriticalSection(&cs); }
		void Leave(){ LeaveCriticalSection(&cs); }
	private:
		CRITICAL_SECTION cs;
};

#pragma warning( disable : 4512 )
class AutoCriticalSection
{
	public:
		AutoCriticalSection(CriticalSection& scs) : cs(scs){cs.Enter();}
		~AutoCriticalSection(){cs.Leave();}

	private:
		CriticalSection& cs;
};


} }

#pragma warning( default : 4512 )

#pragma warning( disable : 4512 )
class TraceFileAndLineInfo
{
	public:
		TraceFileAndLineInfo(LPCTSTR pszFileName, int nLineNo)
			: m_pszFileName(pszFileName), m_nLineNo(nLineNo)
		{}

		void __cdecl operator()(LPCTSTR pszFmt, ...) const
		{
			Liquid::Util::AutoCriticalSection guard(cs); //make this thread safe
			TCHAR buf[4096], *p = buf;

			SYSTEMTIME st;
			GetLocalTime(&st);

			DWORD tid = GetCurrentThreadId();
			p += _sntprintf( buf, sizeof buf - 1, _TEXT("%02d-%02d-%d %02d:%02d:%02d [%3d]: "), st.wDay, st.wMonth, st.wYear, st.wHour, st.wMinute, st.wSecond, tid);
			//p += _sntprintf( buf, sizeof buf - 1, _TEXT("%s:%d %02d:%02d [%3d]: "), m_pszFileName, m_nLineNo, st.wMinute, st.wSecond, tid);

			va_list args;
			va_start(args, pszFmt);
			p += _vsntprintf(p, sizeof buf - 1, pszFmt, args);
			va_end(args);

			while ( p > buf  &&  isspace(p[-1]) )
				*--p = '\0';

			*p++ = '\r';
			*p++ = '\n';
			*p   = '\0';

			flushit(buf);


		}

	private:
		const TCHAR *const m_pszFileName;
		const int m_nLineNo;
		static FILE* m_fLogFile;
		static Liquid::Util::CriticalSection cs;

		static void flushit(TCHAR* buf)
		{
			_ftprintf( m_fLogFile, buf );
			fflush(m_fLogFile);
		}
};
#pragma warning( default : 4512 )

#if _DEBUG
#define TRACE __noop
//#define TRACE TraceFileAndLineInfo( _TEXT(__FILE__), __LINE__ )
#define DTRACE __noop
#else
#define TRACE TraceFileAndLineInfo( _TEXT(__FILE__), __LINE__ )
#define DTRACE __noop
//#define TRACE __noop
#endif