#include "stdafx.h"
#include "Util.h"

//#include <objsel.h>
//#include <cmnquery.h>
//#include <dsquery.h>
//#include <Shlobj.h>
//#include <shlguid.h>
//#include <dsclient.h>

#include <Iads.h>
#include <adshlp.h>

using namespace Zimbra::Util;



FILE* TraceFileAndLineInfo::m_fLogFile = fopen( "ltoast.log", "w+" );
Zimbra::Util::CriticalSection TraceFileAndLineInfo::cs;
/******************************************************************************
 ******************************************************************************
	XmlUtil
 ******************************************************************************
 ******************************************************************************/



/**
 * Returnes the text of the child of the given node 
 *
 * @param pNode			The node whose child is a text node
 * @param pValue		The value of pNodes child
 * @return				TRUE if successful
 */
BOOL XmlUtil::GetNodeValue( IXMLDOMNode* pNode, LPWSTR& pValue )
{
	HRESULT hr;
	IXMLDOMNode* pChild;
	hr = pNode->get_firstChild(&pChild);
	if( FAILED(hr) )
		return FALSE;

	VARIANT value;
	hr = pChild->get_nodeValue(&value);
	pChild->Release();
	if( FAILED(hr) )
		return FALSE;

	//convert the variant bstr to an LPWSTR
	if( value.vt != VT_BSTR )
		return FALSE;

	pValue = new WCHAR[wcslen(value.bstrVal) + 1];
	wcscpy( pValue, value.bstrVal );
	SysFreeString(value.bstrVal);
	return TRUE;
}




/**
 * Returns the text value of the first node in the list
 *
 * @param pNodes		The list of nodes
 * @param pValue		The text value of the first node in pNodes
 * @return				TRUE if successful
 */
BOOL XmlUtil::GetNodeValue( IXMLDOMNodeList* pNodes, LPWSTR& pValue )
{
	long nItems = 0;
	HRESULT hr = pNodes->get_length(&nItems);

	if( nItems < 1 )
		return FALSE;

	//get the first item in the list
	IXMLDOMNode* pNode;
	hr = pNodes->get_item(0, &pNode );
	if( FAILED(hr) )
		return FALSE;

	//get the childs 'value'
	BOOL rVal = XmlUtil::GetNodeValue( pNode, pValue );
	pNode->Release();
	return rVal;
}


/**
 * Returns the value of the node selecting by issuing the xpath query on the document.
 *
 *
 * @param pDoc				The document to query
 * @param pXPathQuery		The xpath query
 * @param pResult			The text value of the node found by issuing the xpath query on pDoc
 * @return					TRUE if successful
 */
BOOL XmlUtil::FindNodeValue( IXMLDOMDocument2* pDoc, LPWSTR pXPathQuery, LPWSTR& pResult )
{

	IXMLDOMNodeList* pNodes = NULL;
	HRESULT hr;
	hr = pDoc->selectNodes( pXPathQuery, &pNodes );
	if( FAILED(hr) )
		return FALSE;

	BOOL bResult = GetNodeValue( pNodes, pResult );
	pNodes->Release();
	return bResult;
}

BOOL XmlUtil::FindNodeValue( IXMLDOMNode* pNode, LPWSTR pXPathQuery, LPWSTR& pResult )
{
	IXMLDOMNodeList* pNodes = NULL;
	HRESULT hr;
	hr = pNode->selectNodes( pXPathQuery, &pNodes );
	if( FAILED(hr) )
		return FALSE;

	BOOL bResult = GetNodeValue( pNodes, pResult );
	pNodes->Release();
	return bResult;
}


/**
 * Returns the value of the BOOL node selected by issuing the xpath query on the document
 *
 * @param pDoc				The document to query
 * @param pXPathQuery		The xpath query
 * @param bResult			The bool value of the node found by issuing the xpath query
 * @return					TRUE if successful
 */
BOOL XmlUtil::FindNodeValue( IXMLDOMDocument2* pDoc, LPWSTR pXPathQuery, BOOL& bResult )
{
	LPWSTR pTemp = NULL;
	BOOL bRetVal = FindNodeValue( pDoc, pXPathQuery, pTemp );
	if( !bRetVal || pTemp == NULL )
		return FALSE;

	bResult = ( wcsicmp( pTemp, L"true" ) == 0 );
	delete [] pTemp;
	return bRetVal;		
}


/**
 * Returns the value of the int node selected by issuing the xpath query on the document
 *
 * @param pDoc				The document to query
 * @param pXPathQuery		The xpath query
 * @param uiResult			The integer value of the node found by issuing the xpath query
 * @return					TRUE if successful
 */
BOOL XmlUtil::FindNodeValue( IXMLDOMDocument2* pDoc, LPWSTR pXPathQuery, UINT& uiResult )
{
	LPWSTR pTemp = NULL;
	BOOL bRetVal = FindNodeValue( pDoc, pXPathQuery, pTemp );
	if( !bRetVal || pTemp == NULL )
		return FALSE;

	uiResult = _wtoi(pTemp);
	delete [] pTemp;
	return bRetVal;		
}


/**
 * Returns the value of an attribute of a node found by issuing an XPath query.
 *
 * @param pDoc				The document to query
 * @param pXPathQuery		The query
 * @param pAttrName			The attribute whose value should be returned
 * @param pResult			The value of the attribute
 * @return					TRUE if successful
 */
BOOL XmlUtil::FindNodeAttributeValue( IXMLDOMDocument2* pDoc, LPWSTR pXPathQuery, LPWSTR pAttrName, LPWSTR& pResult )
{
	IXMLDOMNodeList* pNodes;
	HRESULT hr;
	hr = pDoc->selectNodes( pXPathQuery, &pNodes );
	if( FAILED(hr) )
		return FALSE;

	BOOL bResult = GetSingleAttribute( pNodes, pAttrName, pResult );
	pNodes->Release();
	return bResult;
}

/**
 * Returns the value of an attribute of a node found by issuing an XPath query.
 *
 * @param pDoc				The node to query
 * @param pXPathQuery		The query
 * @param pAttrName			The attribute whose value should be returned
 * @param pResult			The value of the attribute
 * @return					TRUE if successful
 */

BOOL XmlUtil::FindNodeAttributeValue( IXMLDOMNode* pNode, LPWSTR pXPathQuery, LPWSTR pAttrName, LPWSTR& pResult )
{
	IXMLDOMNodeList* pNodes;
	HRESULT hr;
	hr = pNode->selectNodes( pXPathQuery, &pNodes );
	if( FAILED(hr) )
		return FALSE;

	BOOL bResult = GetSingleAttribute( pNodes, pAttrName, pResult );
	pNodes->Release();
	return bResult;
}


/**
 *  Returns the value of the attribute of a given node
 *
 * @param pNode			The node to extract the attribute value from
 * @param pAttrName		The name of the attribute
 * @param pResult		The value of the attribute
 * @return				TRUE if successful
 */
BOOL XmlUtil::GetSingleAttribute( IXMLDOMNode* pNode, LPWSTR pAttrName, LPWSTR& pResult )
{
	IXMLDOMNamedNodeMap* pAttributes = NULL;
	pNode->get_attributes(&pAttributes);
	if( pAttributes == NULL )
	{
		pResult = NULL;
		return FALSE;
	}

	IXMLDOMNode* pItem = NULL;
	pAttributes->getNamedItem( pAttrName, &pItem );

	pAttributes->Release();

	if( pItem == NULL )
	{
		pResult = NULL;
		return FALSE;
	}

	VARIANT vAttrVal;
	pItem->get_nodeValue(&vAttrVal);
	pItem->Release();

	if( vAttrVal.vt != VT_BSTR )
	{
		return FALSE;
	}

	_bstr_t bstrAttrVal( vAttrVal.bstrVal, false );
	pResult = new WCHAR[ bstrAttrVal.length() + 1 ];
	wcscpy( pResult, (LPWSTR)bstrAttrVal );
	return TRUE;	
}




/**
 * Returns the value of an attribute of the first node in the node list
 *
 * @param pNodeList			The list whose first node is inspected
 * @param pAttrName			The name of the attribute whose value should be returned
 * @param pResult			The value of the attribute
 * @return					TRUE if successful
 */
BOOL XmlUtil::GetSingleAttribute( IXMLDOMNodeList* pNodeList, LPWSTR pAttrName, LPWSTR& pResult )
{
	long nItems = 0;
	pNodeList->get_length(&nItems);

	if( nItems < 1 )
		return FALSE;

	//get the first item in the list
	IXMLDOMNode* pNode;
	HRESULT hr = pNodeList->get_item(0, &pNode );
	if( FAILED(hr) )
		return FALSE;

	BOOL rVal = GetSingleAttribute(pNode, pAttrName, pResult);
	pNode->Release();
	return rVal;

}


/******************************************************************************
 ******************************************************************************
	XmlParseException
 ******************************************************************************
 ******************************************************************************/



/**
 * Create an XmlParseException from an IXMLDOMParseError
 *
 * @param pError	The IXMLDOMParseError to base the XmlParseException on
 */
XmlParseException::XmlParseException(IXMLDOMParseError* pError)
	:_errorCode(0), _filepos(0), _line(0), _linepos(0), _reason(NULL), _source(NULL), _url(NULL)
{
	pError->get_errorCode(&_errorCode);
	pError->get_filepos(&_filepos);
	pError->get_line(&_line);
	pError->get_linepos(&_linepos);

	BSTR bstrReason, bstrSrc, bstrUrl;
	pError->get_reason(&bstrReason);
	pError->get_srcText(&bstrSrc);
	pError->get_url(&bstrUrl);

	_bstr_t _bReason(bstrReason, false);
	_bstr_t _bSrc(bstrSrc, false);
	_bstr_t _bUrl(bstrUrl, false);

	CopyString( _reason, (LPWSTR)_bReason );
	CopyString( _source, (LPWSTR)_bSrc );
	CopyString( _url,    (LPWSTR)_bUrl );
}


/**
 * Copy Constructor
 *
 * @param e		The XmlParseException to copy
 */
XmlParseException::XmlParseException( const XmlParseException& e )
{
	_errorCode = e._errorCode;
	_filepos = e._filepos;
	_line = e._line;
	_linepos = e._linepos;
	CopyString( _reason, e._reason );
	CopyString( _source, e._source );
	CopyString( _url, e._url );
}



/**
 *  Destructor
 *
 */
XmlParseException::~XmlParseException()
{
	SafeDelete(_reason);
	SafeDelete(_source);
	SafeDelete(_url);
}





//HRESULT Zimbra::Util::ActiveDirectory::GetLoggedOnUserDN( BSTR* pbstrUserName )
//{
//	IADsADSystemInfo* pADsys = NULL;
//	HRESULT hr = CoCreateInstance(CLSID_ADSystemInfo,
//                              NULL,
//                              CLSCTX_INPROC_SERVER,
//                              IID_IADsADSystemInfo,
//                              (void**)&pADsys);
//	if( FAILED(hr) )
//		return hr;
//
//	hr = pADsys->get_UserName( pbstrUserName );
//	SafeRelease(pADsys);
//	return hr;
//
//}
//
//
//HRESULT Zimbra::Util::ActiveDirectory::GetUserExServerDN( BSTR bstrUserDN, BSTR* pbstrLegacyExchangeDN )
//{
//	IDirectoryObject* pObject = NULL;
//
//	_bstr_t bstrAdsPath(L"LDAP://");
//	bstrAdsPath += bstrUserDN;
//	HRESULT hr = ADsGetObject( bstrAdsPath, IID_IDirectoryObject, (void**)&pObject );
//	if( FAILED(hr) )
//	{
//		//do something 
//		return hr;
//	}
//
//	LPWSTR pAttributeNames[] = { L"msExchHomeServerName" };
//	DWORD dwNumAttrs = sizeof(pAttributeNames) / sizeof(LPWSTR);
//	PADS_ATTR_INFO pAttributeEntries;
//	DWORD dwNumAttributesReturned;
//	
//	hr = pObject->GetObjectAttributes( pAttributeNames, dwNumAttrs, &pAttributeEntries, &dwNumAttributesReturned );
//	if( FAILED(hr) || dwNumAttributesReturned != dwNumAttrs)
//	{
//		//do something
//		pObject->Release();
//		return hr;
//	}
//
//	_bstr_t bstrRet(pAttributeEntries->pADsValues->CaseIgnoreString);
//	*pbstrLegacyExchangeDN = bstrRet.copy();
//
//	SafeRelease(pObject);
//	return hr;
//}
//
//HRESULT Zimbra::Util::ActiveDirectory::GetUserLegacyExDN( BSTR bstrUserDN, BSTR* pbstrLegacyExchangeDN )
//{
//	IDirectoryObject* pObject;
//
//	_bstr_t bstrAdsPath(L"LDAP://");
//	bstrAdsPath += bstrUserDN;
//	HRESULT hr = ADsGetObject( bstrAdsPath, IID_IDirectoryObject, (void**)&pObject );
//	if( FAILED(hr) )
//	{
//		//do something 
//		return hr;
//	}
//
//	LPWSTR pAttributeNames[] = { L"legacyExchangeDN" };
//	DWORD dwNumAttrs = sizeof(pAttributeNames) / sizeof(LPWSTR);
//	PADS_ATTR_INFO pAttributeEntries;
//	DWORD dwNumAttributesReturned;
//	
//	hr = pObject->GetObjectAttributes( pAttributeNames, dwNumAttrs, &pAttributeEntries, &dwNumAttributesReturned );
//	SafeRelease(pObject);
//
//	if( FAILED(hr) || dwNumAttributesReturned != dwNumAttrs)
//	{
//		return hr;
//	}
//
//	_bstr_t bstrRet(pAttributeEntries->pADsValues->CaseIgnoreString);
//	*pbstrLegacyExchangeDN = bstrRet.copy();
//	
//	return hr;
//}
//



void Zimbra::Util::HexEncode( LPBYTE pBuffer, DWORD nBufferBytes, LPWSTR& pHexEncodedBuffer )
{
	pHexEncodedBuffer = new WCHAR[ (nBufferBytes * 2) + 1 ];
	ZeroMemory( pHexEncodedBuffer, ((nBufferBytes * 2) + 1 ) * sizeof(WCHAR) );

	for( DWORD i = 0; i < nBufferBytes; i++ )
	{
		swprintf( pHexEncodedBuffer + (i*2), L"%02x", pBuffer[i] );
	}
}