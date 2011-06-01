

/* this ALWAYS GENERATED file contains the definitions for the interfaces */


 /* File created by MIDL compiler version 7.00.0555 */
/* at Wed Jun 01 13:06:09 2011
 */
/* Compiler settings for MapiExchange.idl:
    Oicf, W1, Zp8, env=Win32 (32b run), target_arch=X86 7.00.0555 
    protocol : dce , ms_ext, c_ext, robust
    error checks: allocation ref bounds_check enum stub_data 
    VC __declspec() decoration level: 
         __declspec(uuid()), __declspec(selectany), __declspec(novtable)
         DECLSPEC_UUID(), MIDL_INTERFACE()
*/
/* @@MIDL_FILE_HEADING(  ) */

#pragma warning( disable: 4049 )  /* more than 64k source lines */


/* verify that the <rpcndr.h> version is high enough to compile this file*/
#ifndef __REQUIRED_RPCNDR_H_VERSION__
#define __REQUIRED_RPCNDR_H_VERSION__ 475
#endif

#include "rpc.h"
#include "rpcndr.h"

#ifndef __RPCNDR_H_VERSION__
#error this stub requires an updated version of <rpcndr.h>
#endif // __RPCNDR_H_VERSION__

#ifndef COM_NO_WINDOWS_H
#include "windows.h"
#include "ole2.h"
#endif /*COM_NO_WINDOWS_H*/

#ifndef __MapiExchange_i_h__
#define __MapiExchange_i_h__

#if defined(_MSC_VER) && (_MSC_VER >= 1020)
#pragma once
#endif

/* Forward Declarations */ 

#ifndef __IExchangeMigObject_FWD_DEFINED__
#define __IExchangeMigObject_FWD_DEFINED__
typedef interface IExchangeMigObject IExchangeMigObject;
#endif 	/* __IExchangeMigObject_FWD_DEFINED__ */


#ifndef __ExchangeMigObject_FWD_DEFINED__
#define __ExchangeMigObject_FWD_DEFINED__

#ifdef __cplusplus
typedef class ExchangeMigObject ExchangeMigObject;
#else
typedef struct ExchangeMigObject ExchangeMigObject;
#endif /* __cplusplus */

#endif 	/* __ExchangeMigObject_FWD_DEFINED__ */


/* header files for imported files */
#include "oaidl.h"
#include "ocidl.h"

#ifdef __cplusplus
extern "C"{
#endif 


#ifndef __IExchangeMigObject_INTERFACE_DEFINED__
#define __IExchangeMigObject_INTERFACE_DEFINED__

/* interface IExchangeMigObject */
/* [unique][nonextensible][dual][uuid][object] */ 


EXTERN_C const IID IID_IExchangeMigObject;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("D5FD1423-78B7-4DCF-A293-88B24F96B098")
    IExchangeMigObject : public IDispatch
    {
    public:
        virtual /* [helpstring][id] */ HRESULT STDMETHODCALLTYPE InitializeMigration( 
            /* [in] */ BSTR ConfigXMLFileName,
            /* [in] */ BSTR UserMapFileName) = 0;
        
        virtual /* [helpstring][id] */ HRESULT STDMETHODCALLTYPE ConnectToserver( void) = 0;
        
        virtual /* [helpstring][id] */ HRESULT STDMETHODCALLTYPE ImportMailoptions( void) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct IExchangeMigObjectVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            IExchangeMigObject * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            IExchangeMigObject * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            IExchangeMigObject * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            IExchangeMigObject * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            IExchangeMigObject * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            IExchangeMigObject * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            IExchangeMigObject * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        /* [helpstring][id] */ HRESULT ( STDMETHODCALLTYPE *InitializeMigration )( 
            IExchangeMigObject * This,
            /* [in] */ BSTR ConfigXMLFileName,
            /* [in] */ BSTR UserMapFileName);
        
        /* [helpstring][id] */ HRESULT ( STDMETHODCALLTYPE *ConnectToserver )( 
            IExchangeMigObject * This);
        
        /* [helpstring][id] */ HRESULT ( STDMETHODCALLTYPE *ImportMailoptions )( 
            IExchangeMigObject * This);
        
        END_INTERFACE
    } IExchangeMigObjectVtbl;

    interface IExchangeMigObject
    {
        CONST_VTBL struct IExchangeMigObjectVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define IExchangeMigObject_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define IExchangeMigObject_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define IExchangeMigObject_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define IExchangeMigObject_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define IExchangeMigObject_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define IExchangeMigObject_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define IExchangeMigObject_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define IExchangeMigObject_InitializeMigration(This,ConfigXMLFileName,UserMapFileName)	\
    ( (This)->lpVtbl -> InitializeMigration(This,ConfigXMLFileName,UserMapFileName) ) 

#define IExchangeMigObject_ConnectToserver(This)	\
    ( (This)->lpVtbl -> ConnectToserver(This) ) 

#define IExchangeMigObject_ImportMailoptions(This)	\
    ( (This)->lpVtbl -> ImportMailoptions(This) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __IExchangeMigObject_INTERFACE_DEFINED__ */



#ifndef __MapiExchangeLib_LIBRARY_DEFINED__
#define __MapiExchangeLib_LIBRARY_DEFINED__

/* library MapiExchangeLib */
/* [version][uuid] */ 


EXTERN_C const IID LIBID_MapiExchangeLib;

EXTERN_C const CLSID CLSID_ExchangeMigObject;

#ifdef __cplusplus

class DECLSPEC_UUID("B3D2EE09-6B18-4327-9F91-6C940E97997D")
ExchangeMigObject;
#endif
#endif /* __MapiExchangeLib_LIBRARY_DEFINED__ */

/* Additional Prototypes for ALL interfaces */

unsigned long             __RPC_USER  BSTR_UserSize(     unsigned long *, unsigned long            , BSTR * ); 
unsigned char * __RPC_USER  BSTR_UserMarshal(  unsigned long *, unsigned char *, BSTR * ); 
unsigned char * __RPC_USER  BSTR_UserUnmarshal(unsigned long *, unsigned char *, BSTR * ); 
void                      __RPC_USER  BSTR_UserFree(     unsigned long *, BSTR * ); 

/* end of Additional Prototypes */

#ifdef __cplusplus
}
#endif

#endif


