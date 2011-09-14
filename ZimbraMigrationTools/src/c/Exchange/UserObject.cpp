// UserObject.cpp : Implementation of CUserObject

#include "common.h"
#include "UserObject.h"



// CUserObject

STDMETHODIMP CUserObject::InterfaceSupportsErrorInfo(REFIID riid)
{
	static const IID* const arr[] = 
	{
		&IID_IUserObject
	};

	for (int i=0; i < sizeof(arr) / sizeof(arr[0]); i++)
	{
		if (InlineIsEqualGUID(*arr[i],riid))
			return S_OK;
	}
	return S_FALSE;
}


long CUserObject::Initialize(BSTR Id)
{
	UserID = Id;
	MailType = L"MAPI";
	m_pLogger = CSingleton::getInstance();
return 0;

}

long CUserObject::GetFolders(VARIANT* folders)
{

		VariantInit(folders);
		return 0;
}
long CUserObject::GetItems(VARIANT* Items)
{

		VariantInit(Items);
		return 0;

}
long CUserObject::UnInitialize()
{

		return 0;
}


STDMETHODIMP  CUserObject::InitializeUser(BSTR UserID,BSTR MailType)
	{
		HRESULT hr = S_OK;long retval =0;
		UserID = UserID;
		MailType = MailType;

		retval =Initialize(UserID);
		//Logger = CSingleton::getInstance();
		if(wcscmp(MailType,L"MAPI") == 0)
		{
			//Initialize the Mapi API..

			m_pLogger->doSomething(DBG,"In Initalize User");

			maapi = new Zimbra::MAPI::MAPIAccessAPI(L"10.20.136.140",L"MyAdmin",L"TestZimbra1");
			//Init session and stores
			maapi->Initialize();
		}

		return hr;

	}

STDMETHODIMP CUserObject::GetFolderObjects(/*[out, retval]*/ VARIANT* vObjects)
{

	HRESULT hr = S_OK;
	VariantInit(vObjects);
	vObjects->vt = VT_ARRAY |VT_DISPATCH;
	SAFEARRAY* psa;
	
	USES_CONVERSION;
	vector<Folder_Data> vfolderlist;
		m_pLogger->doSomething(DBG,"In GetFolderObjects User");
	//Get all folders
	maapi->GetRootFolderHierarchy(vfolderlist);

	//Amitabh: This test function has been removed from MAPIAccessAPI
	//maapi->IterateVectorList(vfolderlist, m_pLogger );


	std::vector<Folder_Data>::iterator it;
	size_t size = vfolderlist.size();
	it = vfolderlist.begin();
	SAFEARRAYBOUND bounds ={(ULONG)size,0};

	psa = SafeArrayCreate(VT_DISPATCH,1,&bounds);
	IfolderObject** pfolders;
	SafeArrayAccessData(psa,(void**)&pfolders);
	for (size_t i = 0 ;i < size ; i ++,it++)
	{
		CComPtr<IfolderObject> pIFolderObject;
		//Isampleobj* pIStatistics;
		hr = CoCreateInstance(CLSID_folderObject, NULL, CLSCTX_ALL, IID_IfolderObject, reinterpret_cast<void **>(&pIFolderObject)); 
		if (SUCCEEDED(hr)) 
		{
				/*pIFolderObject->put_Name(L"testoing"); // so far so good 
				pIFolderObject->put_Id(12222);
				pIFolderObject->put_ParentPath(L"\\Inbox\\personal\\mine");*/
			
			CComBSTR temp((*it).name.c_str());
			pIFolderObject->put_Name(SysAllocString(temp));
			pIFolderObject->put_Id((*it).zimbraid);
			CComBSTR tempS((*it).folderpath.c_str());
			pIFolderObject->put_ParentPath(SysAllocString(tempS));

		}
		if(FAILED(hr))
		{
			return S_FALSE;
		}
		 pIFolderObject.CopyTo(&pfolders[i]);
	}

	SafeArrayUnaccessData(psa);
	vObjects->parray = psa;

	return hr;






}