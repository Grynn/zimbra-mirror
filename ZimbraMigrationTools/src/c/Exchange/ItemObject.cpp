// ItemObject.cpp : Implementation of CItemObject

#include "common.h"
#include "ItemObject.h"
#include "ContactObj.h"

// CItemObject

STDMETHODIMP CItemObject::InterfaceSupportsErrorInfo(REFIID riid)
{
	static const IID* const arr[] = 
	{
		&IID_IItemObject
	};

	for (int i=0; i < sizeof(arr) / sizeof(arr[0]); i++)
	{
		if (InlineIsEqualGUID(*arr[i],riid))
			return S_OK;
	}
	return S_FALSE;
}



STDMETHODIMP CItemObject::get_ID(BSTR* pVal)
{
	// TODO: Add your implementation code here
	CComBSTR str(ID);
	*pVal = str.m_str;
	return S_OK;
	
}


STDMETHODIMP CItemObject::put_ID(BSTR newVal)
{
	// TODO: Add your implementation code here
	ID = newVal;
	return S_OK;
}


STDMETHODIMP CItemObject::get_Type(FolderType* pVal)
{
	// TODO: Add your implementation code here
	/*CComBSTR str(TYPE);
	*pVal = str.m_str;
	return S_OK;*/
	*pVal = TYPE;
	return S_OK;
}


STDMETHODIMP CItemObject::put_Type(FolderType newVal)
{
	// TODO: Add your implementation code here
	TYPE = newVal;
	return S_OK;
}


STDMETHODIMP CItemObject::get_CreationDate(VARIANT* pVal)
{
	// TODO: Add your implementation code here
	_variant_t vt;
	*pVal = vt;
	return S_OK;
	
}


STDMETHODIMP CItemObject::put_CreationDate(VARIANT newVal)
{
	// TODO: Add your implementation code here
	_variant_t vt = newVal;
	return S_OK;
}


STDMETHODIMP CItemObject::get_Parentfolder(IfolderObject** pVal)
{
	// TODO: Add your implementation code here
	*pVal = parentObj;
	(*pVal)->AddRef();
	
	return S_OK;
}


STDMETHODIMP CItemObject::put_Parentfolder(IfolderObject* newVal)
{
	// TODO: Add your implementation code here
	parentObj = newVal ;
	return S_OK;
}

STDMETHODIMP CItemObject::GetDataForItem(VARIANT* data)
{
	std::map<BSTR,BSTR> pIt;
	std::map<BSTR,BSTR>::iterator it;
	FolderType Type;
	HRESULT hr = get_Type(&Type);

	if(Type == 2)
	{
		ContactObj *C1 = new ContactObj();
		C1->GetData(pIt);
		
		VariantInit(data);

    // Create SafeArray of VARIANT BSTRs
    SAFEARRAY *pSA= NULL;
    SAFEARRAYBOUND aDim[2];    // two dimensional array
    aDim[0].lLbound= 0;
    aDim[0].cElements= 5;
    aDim[1].lLbound= 0;
    aDim[1].cElements= 5;    // rectangular array
	pSA= SafeArrayCreate(VT_BSTR,2,aDim);  // again, 2 dimensions
    long aLong[2];
  
    if (pSA != NULL) {
        BSTR temp;
		
        for (long x= aDim[0].lLbound; x< 2 /*(aDim[0].cElements + aDim[0].lLbound)*/; x++) { 
            aLong[0]= x;    // set x index
			it= pIt.begin();
            for (long y= aDim[1].lLbound; y< (long) (aDim[1].cElements + aDim[1].lLbound); y++) {
                aLong[1]= y;    // set y index
               	if(aLong[0] > 0)
				{
					temp =SysAllocString((*it).second);
				}
				else
				temp =SysAllocString((*it).first);
				hr= SafeArrayPutElement(pSA, aLong, temp);
                
				it++;
            }
        }
    }    
data->vt =VT_ARRAY | VT_BSTR;
	data->parray = pSA;
    return S_OK;


	}
	return S_OK;
}