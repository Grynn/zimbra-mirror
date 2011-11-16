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
                getFolders(sessioninfo);


                
            }
            else
            {
                System.Console.WriteLine(statusmessage);

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

               

                foreach (SystemFolder f1 in resp.folders)
                {

                    ws.session = new @string();
                    ws.session.Text = new String[1];
                    ws.session.Text[0] = sessioninfo;

                    if ((f1.folderType.ToString() == "Mailbox") /*|| (f1.folderType.ToString() == "SentItems")*/)
                    {
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
                }

                
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

    }
}
