/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite CSharp Client
 * Copyright (C) 2011, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
// main base class for migration
// This will be a absatrct class with all virtual functions

class CMigration
{
protected:
    ATL::CComBSTR XMLConfigFileName;
    ATL::CComBSTR USerMapfilename;

public:
    virtual void Connecttoserver() = 0;         // do we need separate logon method or can we include it here..TBD
    virtual void ImportMail() = 0;
    virtual void ImportContacts() = 0;
    virtual void ImportCalendar() = 0;

    void DisplayMessageBox(ATL::CComBSTR Msg)
    {
        MessageBox(NULL, Msg, _T("Migartion tool"), MB_OK | MB_ICONEXCLAMATION);
    }

    virtual void SetConfigXMLFile(ATL::CComBSTR filename)
    {
        XMLConfigFileName = filename;
    }

    virtual void SetUserMapFile(ATL::CComBSTR filename)
    {
        USerMapfilename = filename;
    }
};
