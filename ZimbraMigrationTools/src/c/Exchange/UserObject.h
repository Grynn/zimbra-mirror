// UserObject.h : Declaration of the CUserObject

#pragma once
#include "resource.h"                           // main symbols

#include "Exchange_i.h"
#include "Logger.h"
#include "Exchange.h"
#include "ExchangeAdmin.h"
#include "..\Exchange\MAPIAccessAPI.h"
#include "BaseUser.h"

/*
 *
 * #if defined(_WIN32_WCE) && !defined(_CE_DCOM) && !defined(_CE_ALLOW_SINGLE_THREADED_OBJECTS_IN_MTA)
 * #error "Single-threaded COM objects are not properly supported on Windows CE platform, such as the Windows Mobile platforms that do not include full DCOM support. Define _CE_ALLOW_SINGLE_THREADED_OBJECTS_IN_MTA to force ATL to support creating single-thread COM object's and allow use of it's single-threaded COM object implementations. The threading model in your rgs file was set to 'Free' as that is the only threading model supported in non DCOM Windows CE platforms."
 * #endif
 *
 * using namespace ATL;
 *
 */
// CUserObject
extern Zimbra::MAPI::MAPIAccessAPI *maapi;

class ATL_NO_VTABLE CUserObject: public CComObjectRootEx<CComSingleThreadModel>,
    public CComCoClass<CUserObject,
    &CLSID_UserObject>, public BaseUser, public ISupportErrorInfo,
    public IDispatchImpl<IUserObject, &IID_IUserObject, &LIBID_Exchange, /*wMajor =*/ 1,
    /*wMinor =*/ 0>
{
private:
    /*BSTR UserId;
     * BSTR Mailtype;*/

public:
    CUserObject()
    {}
    DECLARE_REGISTRY_RESOURCEID(IDR_USEROBJECT)

    BEGIN_COM_MAP(CUserObject)
    COM_INTERFACE_ENTRY(IUserObject)
    COM_INTERFACE_ENTRY(IDispatch)
    COM_INTERFACE_ENTRY(ISupportErrorInfo)
    END_COM_MAP()

// ISupportsErrorInfo
    STDMETHOD(InterfaceSupportsErrorInfo) (REFIID riid);

    DECLARE_PROTECT_FINAL_CONSTRUCT()

    HRESULT FinalConstruct()
    {
        return S_OK;
    }
    void FinalRelease()
    {}
public:
    STDMETHOD(InitializeUser) (BSTR host, BSTR admin, BSTR UserID, BSTR MailType, BSTR* pErrorText);
    STDMETHOD(GetFolderObjects) ( /*[out, retval]*/ VARIANT * vObjects);
    STDMETHOD(GetItemsForFolderObjects) (IfolderObject * FolderObj, FolderType type,
        VARIANT creattiondate, VARIANT * vItems);
    STDMETHOD(GetDataForItem) (VARIANT ItemId, VARIANT * pVal);

    // /base class functions
    virtual long Initialize(BSTR Id);

    virtual long GetFolders(VARIANT *folders);
    virtual long GetItems(VARIANT *Items);
    virtual long UnInitialize();

protected:
    // CSingleton* Logger;
};

OBJECT_ENTRY_AUTO(__uuidof(UserObject), CUserObject)
