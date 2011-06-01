//main base class for migration 
//This will be a absatrct class with all virtual functions

#include <atlstr.h>
class CMigration
{

protected:
		  ATL::CComBSTR XMLConfigFileName;
		  ATL::CComBSTR USerMapfilename;

public:
	  virtual void Connecttoserver() =0;//do we need separate logon method or can we include it here..TBD
	  virtual void ImportMail() = 0;
	  virtual void ImportContacts() = 0;
	  virtual void ImportCalendar() = 0;

	  void DisplayMessageBox(ATL::CComBSTR msg);

	  virtual void SetConfigXMLFile(ATL::CComBSTR filename);
	  virtual void SetUserMapFile(ATL::CComBSTR filename);



};

void CMigration::DisplayMessageBox(ATL::CComBSTR Msg)
{
	
				MessageBox(NULL, Msg, _T("Migartion tool"), MB_OK | MB_ICONEXCLAMATION );

}

void CMigration::SetConfigXMLFile(ATL::CComBSTR filename)
{
	XMLConfigFileName = filename;


}

void CMigration::SetUserMapFile(ATL::CComBSTR filename)
{
	USerMapfilename = filename;


}