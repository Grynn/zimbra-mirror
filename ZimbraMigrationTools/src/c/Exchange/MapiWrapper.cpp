// MapiWrapper.cpp : Implementation of CMapiWrapper

#include "common.h"
#include "MapiWrapper.h"
#include "MAPICommon.h"


#define PR_PROFILE_UNRESOLVED_NAME 0x6607001e
#define PR_PROFILE_UNRESOLVED_SERVER 0x6608001e

//C5E4267B-AE6C-4E31-956A-06D8094D0CBE
const IID UDTVariable_IID = { 0xC5E4267B, 
                              0xAE6C,
                              0x4E31, {
                                       0x95,
                                       0x6A,
                                       0x06,
                                       0xD8,
                                       0x09,
                                       0x4D,
                                       0x0C,
                                       0xBE
                                      }
                            }; 


STDMETHODIMP CMapiWrapper::InterfaceSupportsErrorInfo(REFIID riid) {
    static const IID* const arr[] = {
	&IID_IMapiWrapper
    };

    for (int i=0; i < sizeof(arr) / sizeof(arr[0]); i++) {
	if (InlineIsEqualGUID(*arr[i],riid))
	    return S_OK;
    }
    return S_FALSE;
}

STDMETHODIMP CMapiWrapper::ConnectToServer(BSTR ServerHostName, BSTR Port,
    BSTR AdminID) {
    (void)ServerHostName;
    (void)Port;
    (void)AdminID;
    baseMigrationObj->Connecttoserver();
    return S_OK;
}

STDMETHODIMP CMapiWrapper::ConnecttoXchgServer(BSTR HostName, BSTR ProfileName,
    BSTR Password) {
    (void)HostName;
    (void)ProfileName;
    (void)Password;
    baseMigrationObj->Connecttoserver();
    return S_OK;
}

STDMETHODIMP CMapiWrapper::ImportMailOptions(BSTR OptionsTag) {
    (void)OptionsTag;
    baseMigrationObj->ImportMail();
    return S_OK;
}


STDMETHODIMP CMapiWrapper::GetProfilelist(VARIANT* Profiles)
{
	// TODO: Add your implementation code here
	HRESULT hr=S_OK;
	
	hr = MAPIInitialize(NULL);
	
	Zimbra::Mapi::Memory::SetMemAllocRoutines( NULL, MAPIAllocateBuffer, MAPIAllocateMore, MAPIFreeBuffer );
	vector<string> vProfileList;
	exchadmin->GetAllProfiles(vProfileList);

	vector<CComBSTR> tempvectors;
	std::vector<string>::iterator its;
	for (its = (vProfileList.begin()); its != vProfileList.end(); its++)
	{

		string str= (*its).c_str();

		CComBSTR temp = SysAllocString( str_to_wstr( str).c_str() );

		

		tempvectors.push_back(temp);


	}
	
	VariantInit(Profiles);
	Profiles->vt = VT_ARRAY | VT_BSTR;
	SAFEARRAY* psa;
	SAFEARRAYBOUND bounds = {vProfileList.size(), 0};
	psa = SafeArrayCreate(VT_BSTR, 1, &bounds);

	BSTR* bstrArray;

	SafeArrayAccessData(psa, (void**)&bstrArray);
	std::vector<CComBSTR>::iterator it;
	int i = 0;

	for (it = (tempvectors.begin()); it != tempvectors.end(); it++, i++)
	{
	bstrArray[i] = SysAllocString((*it).m_str);
	}

	SafeArrayUnaccessData(psa);
	Profiles->parray = psa;



	return hr;
}




STDMETHODIMP CMapiWrapper::get_UDTFolder(UDTFolder *pUDT)
{
	// TODO: Add your implementation code here
	 if( !pUDT ) 
        return( E_POINTER );

    pUDT->Value = m_pUDT.Value;  //return value

    ::SysFreeString( pUDT->Name );   //free old (previous) name
     pUDT->Name = ::SysAllocString( m_pUDT.Name );  //copy new name

    /*::VariantClear( &pUDT->Type );  //free old special value
    ::VariantCopy( &pUDT->Type, &m_pUDT.Type ); //copy new special value*/
	 ::SysFreeString( pUDT->Type );   //free old (previous) name
     pUDT->Type = ::SysAllocString( m_pUDT.Type );  //copy new name


	return S_OK;
}


STDMETHODIMP CMapiWrapper::put_UDTFolder(UDTFolder *pUDT)
{
	// TODO: Add your implementation code here
	if( !pUDT ) 
        return( E_POINTER );
    if( !pUDT->Name )
        return( E_POINTER );

    m_pUDT.Value = pUDT->Value;  //easy assignment

    ::SysFreeString( m_pUDT.Name );  //free the previous string first
    m_pUDT.Name = ::SysAllocString( pUDT->Name );   //make a copy of the incoming

   /* ::VariantClear( &m_pUDT.Special );   //free the previous variant first
    ::VariantCopy( &m_pUDT.Special, &pUDT->Special );   //make a copy*/

     ::SysFreeString( m_pUDT.Type );  //free the previous string first
    m_pUDT.Type = ::SysAllocString( pUDT->Type );   //make a copy of the incoming


	return S_OK;
}



std::wstring CMapiWrapper::str_to_wstr( const std::string& str )
{
  std::wstring wstr( str.length()+1, 0 );
  
  MultiByteToWideChar( CP_ACP,
             0,
             str.c_str(),
             str.length(),
             &wstr[0],
             str.length() );
  return wstr;
}




STDMETHODIMP CMapiWrapper::UDTFolderSequence(long start, long length, SAFEARRAY **SequenceArr )
{
    if( !SequenceArr ) 
        return( E_POINTER );

    if( length <= 0 ) { 
        HRESULT hr = Error( _T("Length must be greater than zero") );
        return( hr ); 
    }

    if( *SequenceArr != NULL ) {
        ::SafeArrayDestroy( *SequenceArr );
        *SequenceArr = NULL;
    } 

	//////////////////////////////////////////////////
    //here starts the actual creation of the array
    //////////////////////////////////////////////////
    IRecordInfo *pUdtRecordInfo = NULL;
    HRESULT hr = GetRecordInfoFromGuids( LIBID_Exchange,  
                                         1, 0, 
                                         0,
                                         UDTVariable_IID,
                                         &pUdtRecordInfo );
    if( FAILED( hr ) ) {
       /* HRESULT hr2 = Error( _T("Can not create RecordInfo interface for"
                                "UDTVariable") );*/
        return( hr ); //Return original HRESULT hr2 is for debug only
    }
    
    SAFEARRAYBOUND rgsabound[1];
    rgsabound[0].lLbound = 0;
    rgsabound[0].cElements =length;//

    *SequenceArr = ::SafeArrayCreateEx( VT_RECORD, 1, rgsabound, pUdtRecordInfo );

    pUdtRecordInfo->Release(); //do not forget to release the interface
    if( *SequenceArr == NULL ) {
      /*  HRESULT hr = Error( _T("Can not create array of UDTVariable "
                               "structures") );*/
        return( hr );
    }
	  hr = SequenceByElement( start, length, *SequenceArr );
    //hr = SequenceByData( start, length, *SequenceArr );

	return S_OK;
}

HRESULT CMapiWrapper::SequenceByElement(long start, long length, SAFEARRAY *SequenceArr)
{

    long lBound = 0;

    VARIANT         a_variant;
    UDTFolder    a_udt;
    
    HRESULT hr = SafeArrayGetLBound( SequenceArr, 1, &lBound );
    if( FAILED( hr ) ) return( hr );
    
    BSTR strDefPart = ::SysAllocString( L"Named  " );
    
    ::VariantInit( &a_variant );
    for( long i = lBound; i<length; i++, start++ ) {
        
       
		a_udt.Name= L"test";
		a_udt.Value = 10 +i;
		a_udt.Type = L"Mail";



		
		put_UDTFolder(&a_udt);



        hr = ::SafeArrayPutElement( SequenceArr, &i, (void*)&a_udt );
        if( FAILED(hr) ) 
            return( hr );

        ::VariantClear( &a_variant ); //frees the Name string
    }

    ::SysFreeString( strDefPart );

    return( S_OK );
}



//////////////////////////////////////////////////////////////////////////////
//////
//////////////////////////////////////////////////////////////////////////////
HRESULT CMapiWrapper::SequenceByData(long start, long length, SAFEARRAY *SequenceArr)
{

    long lBound = 0;

    BSTR            a_variant = NULL;
    UDTFolder     *p_udt = NULL; //use a pointer now     
    
    HRESULT hr = SafeArrayGetLBound( SequenceArr, 1, &lBound );
    if( FAILED( hr ) ) return( hr );
    
    BSTR strDefPart = ::SysAllocString( L"Named  " );
    
    hr = ::SafeArrayAccessData( SequenceArr, (void**)&p_udt );
    if( FAILED( hr ) ) 
        return( hr );

    for( long i = 0; i<length; i++, start++, p_udt++ ) {
        a_variant = NULL;
        p_udt->Value = start;    //i holds the sequence value
        
       /* if( i & 1 ) {
            p_udt->Special.vt = VT_R8;
            p_udt->Special.dblVal = double( start );
            p_udt->Special.dblVal += 0.5;
            
            hr = ::VarBstrFromR8(p_udt->Special.dblVal, 
                                 GetUserDefaultLCID(), 
                                 LOCALE_NOUSEROVERRIDE, &a_variant); 
        } else {
            p_udt->Special.vt = VT_I4;
            p_udt->Special.lVal = start;

            hr = ::VarBstrFromI4( start, 
                                  GetUserDefaultLCID(),
                                  LOCALE_NOUSEROVERRIDE, &a_variant); 
        }*/
        
        if( p_udt->Name ) 
            ::SysFreeString( p_udt->Name );
                
        hr = ::VarBstrCat( strDefPart, a_variant, &(p_udt->Name) );
        ::SysFreeString( a_variant );
    }

    hr = ::SafeArrayUnaccessData( SequenceArr );
    ::SysFreeString( strDefPart );

    return( hr );
}



//////////////////////////////////////////////////////////////////////////////
//////
//////////////////////////////////////////////////////////////////////////////
/*HRESULT CMapiWrapper::IsUDTFolderArray( SAFEARRAY *pUDTArr, bool &isDynamic )
{
  /*     if( !pUDTArr ) {
        return( S_FALSE );  //we may create it
        //isDynamic = true;
    }

    //CHECK DIMENTIONS IF IN LIKE
    HRESULT hr = S_OK;
    long dims = SafeArrayGetDim( pUDTArr );
    if( dims != 1 ) {
        hr = Error( _T("Not Implemented for multidimentional arrays") );
        return( hr );  
    }

    unsigned short feats = pUDTArr->fFeatures; //== 0x0020;
    if( (feats & FADF_RECORD) != FADF_RECORD ) {
        hr = Error( _T("Array is expected to hold structures") );
        return( hr );
    }

    //check the actual structure type
    IRecordInfo *pUDTRecInfo = NULL;
    hr = ::SafeArrayGetRecordInfo( pUDTArr, &pUDTRecInfo );
    if( FAILED( hr ) &&  !pUDTRecInfo )
        return( hr );
    
    BSTR  udtName = ::SysAllocString( L"UDTVariable" );
    BSTR  bstrUDTName = NULL; //if not null. we are going to have problem
    hr = pUDTRecInfo->GetName( &bstrUDTName);
    if( VarBstrCmp( udtName, bstrUDTName, 0, GetUserDefaultLCID()) != VARCMP_EQ ) {
        ::SysFreeString( bstrUDTName );
        ::SysFreeString( udtName );
        //hr = Error(_T("Object Does Only support [UDTVariable] Structures") );
        return( hr );
    }
    



#ifndef NDEBUG
    { //debug block to see other information
        ULONG recsize, fieldcount = 1;
        BSTR  pBstr = NULL;
        CComBSTR   str;

        HRESULT hr1 = pUDTRecInfo->GetSize( &recsize );
        hr1 = pUDTRecInfo->GetName( &pBstr);
        str = pBstr;
        
        //Fields are 1 based 
        hr1 = pUDTRecInfo->GetFieldNames( &fieldcount, &pBstr ); 
        str = pBstr;    //retrieve one field
        fieldcount++;
        hr1 = pUDTRecInfo->GetFieldNames( &fieldcount, &pBstr ); 
        str = pBstr; //retrieve second fields
        fieldcount++;
        hr1 = pUDTRecInfo->GetFieldNames( &fieldcount, &pBstr ); 
        str = pBstr; //retrieve third fields
        
        ITypeInfo  *pInfo;
        hr1 = pUDTRecInfo->GetTypeInfo( &pInfo ); 
        if( pInfo ) 
            pInfo->Release(); 

//        bool isAuto = (feats & FADF_AUTO) != 0; //do not care so much

       // bool isStatic = (feats & FADF_STATIC) != 0;   //these are contradicory
        isDynamic = !((feats & FADF_FIXEDSIZE) != 0);
    }
#endif


    isDynamic = !((feats & FADF_FIXEDSIZE) != 0);

    pUDTRecInfo->Release();
    ::SysFreeString( bstrUDTName );
    ::SysFreeString( udtName );

    return( S_OK );
}*/


