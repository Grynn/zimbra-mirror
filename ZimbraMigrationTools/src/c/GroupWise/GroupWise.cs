using System.Collections.Generic;
using System;

namespace GroupWise
{
    public class GroupWise
    {
        public static string name = "GroupWise";

        GroupWiseBinding ws;
        GWadmin admin;

        public void InitializeMailClient(string gwadmin, string adminpwd, string domainpath, string serverip)
        {
            try
            {
                admin = new GWadmin(gwadmin, adminpwd);
                admin.Initialize(domainpath);
            }
            catch (Exception e)
            {
                System.Console.WriteLine("Exception occured at Initializemailcleint " + e.Message);
            }


        }



        public void Login()
        {

            System.Net.ServicePointManager.Expect100Continue = false;

            loginRequest req = new loginRequest();
            loginResponse resp;
            PlainText pt = new PlainText();

            ws = new GroupWiseBinding();
            string str = "http://";
            str += "10.20.136.206";
            str += ":";
            str += "7191";
            str += "/soap";
            ws.Url = str;

            //    ws.Discover();
            
            ws.Timeout = 100000;
            //commenting out the following since we will have to use trusted connection thru the admin acoount.

           /* pt.username = "knuthi";
            pt.password = "zimbra";
            req.auth = pt;*/

            TrustedApplication trustedapp = new TrustedApplication();
            trustedapp.name = "ZimbraGWmigration";
            trustedapp.key = admin.Key;
            trustedapp.username = admin.Username;

            req.auth = trustedapp;


           
 


            try
            {


                resp = ws.loginRequest(req);
            }
            catch (Exception ex)
            {
                string message = ex.Message;
                ws.Discover();
                resp = ws.loginRequest(req);
            }

            string statusmessage = resp.status.code.ToString();
            if (0 == resp.status.code)
            {
                System.Console.WriteLine(" Login success ful");

                ws.session = new @string();
                ws.session.Text = new String[1];
                ws.session.Text[0] = resp.session;


                ws.Timeout = 300000;
                string uid = resp.userinfo.uuid;

               string sessioninfo = resp.session;
                //bLogin = false;
                //getFolders(sessioninfo);


                
            }
            else
            {
                System.Console.WriteLine(statusmessage);

            }


        }


        public void GetUsersList()
        {

            getUserListRequest listreq = new getUserListRequest();
            getUserListResponse listresp = new getUserListResponse();
            listreq.name = "ZimbraGWmigration";
            listreq.key = admin.Key;
            try
            {

                listresp = ws.getUserListRequest(listreq);
                int count = listresp.users.Length;
                System.Console.WriteLine("Total users on this Postoffice are :" + count);

                string message = " users are : ";
                while (count >= 0)
                {
                    message += listresp.users[count].name;

                    count--;


                }

                System.Console.WriteLine(message);
                
            }
            catch (Exception e)
            {

                System.Console.WriteLine("Exception in Getuserlist : " + e.Message);


            }




        }

       
        protected void getContactFolders(string uid, string key)
        {
            String str;
            UserInfo User = new UserInfo();
            User.userid = "knuthi";

            ws = new GroupWiseBinding();
            str = "http://";
            str += "10.20.136.206";
            str += ":";
            str += "7191";
            str += "/soap";
            ws.Url = str;



            TrustedApplication trusted = new TrustedApplication();

            trusted.name = "ZimbraGWMigration";
            trusted.key = key;//outkey.ToString();


            trusted.username = "knuthi";



            loginRequest reql = new loginRequest();
            reql.auth = trusted;

            loginResponse respl;
            respl = ws.loginRequest(reql);
            ws.session = new @string();
            ws.session.Text = new String[1];
            ws.session.Text[0] = respl.session;


            getFolderRequest req = new getFolderRequest();
            getFolderResponse resp;




            req.folderType = FolderType.Contacts;
            req.view = "";
            req.folderTypeSpecified = true;
            req.source = "folders";/*
            req.view = "";
            req.imap = false;
            req.nntp = false;*/


            resp = ws.getFolderRequest(req);
            if (0 == resp.status.code)
            {

                ws.session = new @string();
                ws.session.Text = new String[1];
                ws.session.Text[0] = respl.session;

                str = "Folders: ";
                if (null != resp.folder)
                {
                    // str += resp.folders.Length;

                    Folder f1 = (Folder)resp.folder;

                    string type = f1.name.ToString();

                    str += type;
                   System.Console.WriteLine(str);
                   string id = f1.id;
                   Dictionary<string, string> map = new Dictionary<string, string>();
                    getContactItems(id,map);
                    ws.session = new @string();
                    ws.session.Text = new String[1];
                    ws.session.Text[0] = respl.session;



                }



            }
            else
            {
                System.Console.WriteLine(resp.status.description);
                
            }






        }

        protected void getContactItems(string uid, Dictionary <string,string> mapObj)
        {
            String str;
            getItemsRequest req = new getItemsRequest();
            getItemsResponse resp;

            Filter Flt = new Filter();
            FilterEntry FEN = new FilterEntry();
            FEN.op = FilterOp.eq;
            FEN.field = "@type";
            FEN.value = "Contact";
            Flt.element = FEN;
            req.filter = Flt;

            req.container = uid;

            resp = ws.getItemsRequest(req);
            //  resp = ws.getItemRequest(req);
            if (0 == resp.status.code)
            {
                str = "Items: for Contact folder ";
                if (null != resp.items)
                {
                    str += resp.items.Length;

                    int cnt = resp.items.Length;
                    cnt = cnt - 1;


                    while (cnt >= 0)
                    {

                        Contact mt = (Contact)resp.items[cnt];
                      

                        str += mt.officeInfo;
                        str += "\n";
                        str += mt.name;
                        cnt--;
                        if (mt.officeInfo != null)
                        mapObj.Add("Location", mt.officeInfo.location.ToString());
                        if (mt.officeInfo != null)
                            mapObj.Add("Organization", mt.officeInfo.organization.ToString());
                        if (mt.officeInfo != null)
                            mapObj.Add("Title", mt.officeInfo.title.ToString());
                        if (mt.name != null)
                        mapObj.Add("Name", mt.name.ToString());
                        if (mt.personalInfo != null)
                        mapObj.Add("birthday", mt.personalInfo.birthday.ToString());
                        if (mt.phoneList != null)
                        {
                            int count = mt.phoneList.phone.Length;
                            while (count > 0)
                            {
                                mapObj.Add("Phone" + mt.phoneList.phone[count-1].type.ToString(), mt.phoneList.phone[count-1].Value.ToString());
                                count--;
                            }
                        }
                        if (mt.imList != null)
                        mapObj.Add("imList", mt.imList.ToString());
                        if (mt.fullName.lastName != null)
                        mapObj.Add("LastName", mt.fullName.lastName.ToString());
                        if (mt.fullName.firstName != null)
                            mapObj.Add("FirstName", mt.fullName.firstName.ToString());
                        if (mt.fullName.middleName != null)
                            mapObj.Add("MiddleName", mt.fullName.middleName.ToString());
                        if (mt.addressList != null)
                        mapObj.Add("address", mt.addressList.address.ToString());
                        if (mt.addressList != null)
                            mapObj.Add("mailingaddress", mt.addressList.mailingAddress.ToString());
                        if (mt.contacts != null)
                        mapObj.Add("Contacts", mt.contacts.ToString());
                        

                                               
                        

                    }

                    str += resp.status.code.ToString();
                }
                System.Console.WriteLine(str);
            }
            else
            {

                System.Console.WriteLine(resp.status.description);

            }
        }

        public void UserLogin(string username)
        {

            ws = new GroupWiseBinding();
            string str = "http://";
            str += "10.20.136.206";
            str += ":";
            str += "7191";
            str += "/soap";
            ws.Url = str;
            TrustedApplication trusted = new TrustedApplication();

            trusted.name = "ZimbraGWmigration";
            trusted.key = admin.Key;//outkey.ToString();


            trusted.username = username;



            loginRequest reql = new loginRequest();
            reql.auth = trusted;

            loginResponse respl;

            try
            {

                respl = ws.loginRequest(reql);

                
                if (0 == respl.status.code)
                {
                    System.Console.WriteLine(" Login success ful");

                    ws.session = new @string();
                    ws.session.Text = new String[1];
                    ws.session.Text[0] = respl.session;


                    ws.Timeout = 300000;
                    string uid = respl.userinfo.uuid;

                    string sessioninfo = respl.session;

                    getContactFolders(uid,admin.Key);
                    //bLogin = false;
                   // getFolders(sessioninfo);


                }

            }
            catch (Exception e)
            {
                System.Console.WriteLine("Exception in Getuserlist : " + e.Message);

            }

            


        }
        public void getFolders(string sessioninfo)
        {
            String str;
            getFolderListRequest req = new getFolderListRequest();
            getFolderListResponse resp;

            req.recurse = true;
            req.parent = "folders";
            req.view = "";
            req.imap = false;
            req.nntp = false;

            resp = ws.getFolderListRequest(req);
            if (0 == resp.status.code)
            {
                str = "Total number of Folders: ";
                if (null != resp.folders)
                {
                    str += resp.folders.Length;

                    /*SystemFolder f1 = (SystemFolder)resp.folders[0];

                    string type = f1.folderType.ToString();

                    str += resp.folders[0].name;*/


                }
                System.Console.WriteLine(str);

                 foreach (Folder f in resp.folders)
                {
                    if ((f.name.ToString() == "Mailbox") || (f.name.ToString() == "SentItems"))
                    {
                        getItems(f.id);
                        ws.session = new @string();
                        ws.session.Text = new String[1];
                        ws.session.Text[0] = sessioninfo;
                    }
                    if (f.name.ToString() == "Contacts")
                    {
                       // getContactItems(f.id);
                        ws.session = new @string();
                        ws.session.Text = new String[1];
                        ws.session.Text[0] =sessioninfo;

                    }

                }
               

               /* foreach (SystemFolder f1 in resp.folders)
                {

                    ws.session = new @string();
                    ws.session.Text = new String[1];
                    ws.session.Text[0] = sessioninfo;
                    
                    //if ((f1.folderType.ToString() == "Mailbox") /*|| (f1.folderType.ToString() == "SentItems")*/
                   /* {
                        getItems(f1.id);

                        getTimestampRequest gt = new getTimestampRequest();
                        gt.noop = true;
                    }
                    else
                    {
                        if ((f1.folderType.ToString() == "SentItems"))
                        {
                            getItems(f1.id);

                            getTimestampRequest gt = new getTimestampRequest();
                            gt.noop = true;
                        }

                        else
                            System.Console.WriteLine("Not a Mail Folder Sorry>>>>");
                    }
                }*/

                
            }
            else
            {
                System.Console.WriteLine(resp.status.description);

            }






        }

        protected void getItems(string uid)
        {
            String str;
            getItemsRequest req = new getItemsRequest();
            getItemsResponse resp;

            /* WebReference.getItemRequest req = new WebReference.getItemRequest();

             WebReference.getItemResponse resp;*/


            /* WebReference.getQuickMessagesRequest req = new WebReference.getQuickMessagesRequest();

             WebReference.getQuickMessagesResponse resp;
           
             DateTime dt;*/



            //req.startDate = DateTime.Parse("09/09/2011");

            //   resp = ws.getQuickMessagesRequest(req);
            //  req.container = uid;
            // req.id = uid;
            // req.view = "modified";*/

            req.container = uid;
       
            
            resp = ws.getItemsRequest(req);
            //  resp = ws.getItemRequest(req);
            if (0 == resp.status.code)
            {
                str = "Items subjects in folder are : ";
                if (null != resp.items)
                {
                    str += resp.items.Length;

                    int cnt = resp.items.Length;
                    cnt = cnt - 1;
                    while (cnt >= 0)
                    {

                        Mail mt = (Mail)resp.items[cnt];

                        str += mt.subject;

                        cnt--;
                    }

                    /*WebReference.Mail mt1 = (WebReference.Mail)resp.items[1];
                    str += mt1.subject;

                    WebReference.Mail mt2 = (WebReference.Mail)resp.items[2];
                    str += mt2.subject;

                    str += resp.items[0].id;
                    str += resp.items[1].id;
                    str += resp.items[2].id;*/
                    str += resp.status.code.ToString();
                }
                System.Console.WriteLine(str);
            }
            else
            {
                //lblStatus.Text = resp.status.code.ToString();
                System.Console.WriteLine( resp.status.description);
            }
        }


       /* protected void getContactItems(string uid)
        {
            String str;
            getItemsRequest req = new getItemsRequest();
            getItemsResponse resp;

            req.container = uid;

            resp = ws.getItemsRequest(req);
            //  resp = ws.getItemRequest(req);
            if (0 == resp.status.code)
            {
                str = "Items: for Contact folder ";
                if (null != resp.items)
                {
                    str += resp.items.Length;

                    int cnt = resp.items.Length;
                    cnt = cnt - 1;


                    while (cnt >= 0)
                    {

                        Contact mt = (Contact)resp.items[cnt];

                        str += mt.officeInfo;
                        str += "\n";
                        str += mt.name;
                        cnt--;
                    }

                   
                    str += resp.status.code.ToString();
                }
                System.Console.WriteLine(str);
            }
            else
            {
               

                System.Console.WriteLine(resp.status.description);
            }
        }*/

    }
}
