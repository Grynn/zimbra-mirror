using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
//using MVVM;
//using Exchange;
using System.IO;
using System.Reflection;

namespace CssLib
{

    public class CSMigrationwrapper
    {
        /* string m_ConfigXMLFile;

         public string ConfigXMLFile
         {
             get { return m_ConfigXMLFile; }
             set { m_ConfigXMLFile = value; }
         }
         string m_UserMapFile;

         public string UserMapFile
         {
             get { return m_UserMapFile; }
             set { m_UserMapFile = value; }
         }*/


        

        string m_MailClient;


        dynamic userobject;
        dynamic folderobject;
        dynamic[] folderobjectarray;
        dynamic itemobject;
        dynamic[] itemobjectarray;
        
        ZimbraAPI api;

        enum foldertype
        {
            Mail = 1,
            Contacts = 2,
            Calendar = 3
        };

        


        public string MailClient
        {
            get { return m_MailClient; }
            set { m_MailClient = value; }
        }

        /*   MVVM.Model.Config ConfigObj = new MVVM.Model.Config();
           MVVM.Model.ImportOptions ImportOptions = new MVVM.Model.ImportOptions();
           MVVM.Model.Users  users = new MVVM.Model.Users();*/

        dynamic MailWrapper;


        public CSMigrationwrapper()
        {
            userobject = new Exchange.UserObject();
            folderobject = new Exchange.folderObject();
            folderobjectarray = new Exchange.folderObject[20];
            itemobject = new Exchange.ItemObject();
            itemobjectarray = new Exchange.ItemObject[20];
            
            api = new ZimbraAPI();
        }


        /*private void CreateConfig(string Xmlfilename)
        {


            System.Xml.Serialization.XmlSerializer reader =
            new System.Xml.Serialization.XmlSerializer(typeof(MVVM.Model.Config));
            if (File.Exists(Xmlfilename))
            {

                System.IO.StreamReader fileRead = new System.IO.StreamReader(
                       Xmlfilename);

                ConfigObj = (MVVM.Model.Config)reader.Deserialize(fileRead);

            }
        }
        */

        public void InitializeInterop()
        {
            if (MailClient == "MAPI")
            {

                MailWrapper = new Exchange.MapiWrapper();

            }
        }



        public string InitializeMailClient(string Target, string AdminUser, string AdminPassword)
        {
            string s = "";
            if (MailClient == "MAPI")
            {
                MailWrapper = new Exchange.MapiWrapper();
                s = MailWrapper.GlobalInit(Target, AdminUser, AdminPassword);
            }
            return s;
        }


        public string UninitializeMailClient()
        {
            string s = "";
            if (MailClient == "MAPI")
            {
                MailWrapper = new Exchange.MapiWrapper();
                s = MailWrapper.GlobalUninit();
            }
            return s;
        }



        public void Initalize(string HostName, string Port, string AdminAccount, string UserID, string Mailserver, string AdminID)
        {
            //CreateConfig(ConfigXMLFile);   
            int status =0;
            MailWrapper = new Exchange.MapiWrapper();
            MailWrapper.ConnectToServer(Mailserver, Port, AdminID);

            status = api.Logon(HostName, Port, AdminAccount, "test123", true);

            //Initilaize user object


           // O1.InitializeUser(Mailserver, AdminID, UserID, "MAPI");

        }

        

        public void Migrate(string MailOptions)
        {

            MailWrapper.ImportMailOptions(MailOptions);
        }

        public string[] GetListofMapiProfiles()
        {


            object var = new object();

            MailWrapper.GetProfilelist(out var);


            string[] s = (string[])var;

            return s;



        }

/*
        public void GetListofMapiFolders(string account)
        {

              UDTFolder[] folders;
              MapiWrapper M1 = new MapiWrapper();
              folders = (UDTFolder[]) M1.UDTFolderSequence(0, 10);
              string name = folders[0].Name;
              FolderType type = folders[0].Type;
            UDTItem item;
            item.EntryId = "000-444-444";
            item.Type = FolderType.Mail;
            item.CreationDate = DateTime.Now;
            M1.set_UDTItem( ref item);
            UDTItem i1 = M1.get_UDTItem();

             MapiWrapper M1 = new MapiWrapper();
             object[] objectArray;
             objectArray = M1.GetFolderObjects();

            // UserObject O1 = new UserObject();
            // O1.InitializeUser("ksomasil", "MAPI");


            object[] objectArray;
            objectArray = O1.GetFolderObjects();


            folderObject[] Folders = Array.ConvertAll(objectArray, folder => (folderObject)folder);

            string name = Folders[0].Name;
            long id = Folders[0].Id;

            string path = Folders[0].ParentPath;
            ZimbraAPI api = new ZimbraAPI();
            foreach (folderObject F1 in Folders)
            {
                if (F1.Id == 0)
                {


                    api.AccountName = account;
                    int stat = api.CreateFolder(F1.Name, "1");
                    //stat=  api.CreateFolder("testfolder","2");

                }
                else
                {
                    if ((F1.Name == "Contacts") && (F1.Id == 7))
                    {
                        api.AccountName = account;
                        // int stat = api.CreateContact(
                    }

                    DateTime dt;
                    dt = DateTime.UtcNow;
                    objectArray = O1.GetItemsForFolderObjects(F1, FolderType.Contacts, dt.ToOADate());
                    ItemObject[] Items = Array.ConvertAll(objectArray, Item => (ItemObject)Item);



                    foreach (ItemObject I1 in Items)
                    {
                        if (I1 != null)
                        {
                            Dictionary<string, string> dict = new Dictionary<string, string>();
                            FolderType type = I1.Type;
                            if (type == FolderType.Contacts)
                            {

                                string[,] data = O1.GetDataForItem(I1.ItemID);

                                int bound0 = data.GetUpperBound(0);



                                for (int i = 0; i <= bound0; i++)
                                {
                                    string Key = data[0, i];
                                    string Value = data[1, i];
                                    dict.Add(Key, Value);
                                    // Console.WriteLine("{0}, {1}", so1, so2);
                                }
                            }

                            api.AccountName = account;
                            if (dict.Count > 0)
                            { int stat = api.CreateContact(dict); }

                        }

                    }




                }

            }

             folderObject[] Folders = Array.ConvertAll(objectArray, folder => (folderObject)folder);

             string name = Folders[0].Name;
             long id = Folders[0].Id;

             string path = Folders[0].ParentPath;



        }
*/

        

        /*
        public object[] GetListFromObjectPicker()
        {

            object var = new object();
            MailWrapper.SelectExchangeUsers(out var);
            object[] o = (object[])var;
            return o;
        }
        */

        public string[] GetListFromObjectPicker()
        // Change this to above signature when I start getting the real ObjectPicker object back
        {

            object var = new object();
            MailWrapper.SelectExchangeUsers(out var);
            string[] s = (string[])var;
            return s;
        }

        public void StartMigration(MigrationAccount Acct, bool UIflag = true)
        {


            //GetListofMapiFolders(Acct.Accountname);

            if (!UIflag)
            {

                userobject.InitializeUser("", "", Acct.AccountID, "MAPI");


                //object[] objectArray;
                //objectArray = userobject.GetFolderObjects();

                folderobjectarray = userobject.GetFolderObjects();

               // Exchange.folderObject[] Folders = Array.ConvertAll(objectArray, folder => (Exchange.folderObject)folder);

                Acct.migrationFolders[0].CurrentCountOFItems = folderobjectarray.Count();

              /*  string name = Folders[0].Name;
                long id = Folders[0].Id;

                string path = Folders[0].ParentPath;*/
                ZimbraAPI api = new ZimbraAPI();
                
                foreach (dynamic folderobject in folderobjectarray)
                {
                    if (folderobject.Id == 0)
                    {


                        api.AccountName = Acct.Accountname;
                        int stat = api.CreateFolder(folderobject.ParentPath);
                        //stat=  api.CreateFolder("testfolder","2");

                    }
                    else
                    {

                        DateTime dt;
                        dt = DateTime.UtcNow;
                        //objectArray = userobject.GetItemsForFolderObjects(folderobject, Exchange.FolderType.Contacts, dt.ToOADate());
                        itemobjectarray = userobject.GetItemsForFolderObjects(folderobject,(int) foldertype.Contacts, dt.ToOADate());

                        //Exchange.ItemObject[] Items = Array.ConvertAll(objectArray, Item => (Exchange.ItemObject)Item);
                        Acct.migrationFolders[0].FolderName = folderobject.Name;
                        Acct.migrationFolders[0].TotalCountOFItems = itemobjectarray.Count();
                        Acct.migrationFolders[0].CurrentCountOFItems = 0;
                        while (Acct.migrationFolders[0].CurrentCountOFItems < Acct.migrationFolders[0].TotalCountOFItems)
                        {
                            foreach (dynamic itemobject in itemobjectarray)
                            {
                                if (itemobject != null)
                                {
                                    Dictionary<string, string> dict = new Dictionary<string, string>();
                                    foldertype type = (foldertype)itemobject.Type;
                                    if (type == foldertype.Contacts)
                                    {

                                        //string[,] data = O1.GetDataForItem(I1.ItemID);
                                        string[,] data = itemobject.GetDataForItemID(itemobject.ItemID);

                                        int bound0 = data.GetUpperBound(0);



                                        for (int i = 0; i <= bound0; i++)
                                        {
                                            string Key = data[0, i];
                                            string Value = data[1, i];
                                            dict.Add(Key, Value);
                                            // Console.WriteLine("{0}, {1}", so1, so2);
                                        }
                                    }

                                    api.AccountName = Acct.Accountname;
                                    if (dict.Count > 0)
                                    { int stat = api.CreateContact(dict); }

                                }
                                Acct.migrationFolders[0].CurrentCountOFItems++;

                            }
                        }




                    }

                }
            }
            // GetListofItems();
            //Acct.Accountname = "testing";
            else
            {
                Acct.TotalNoContacts = 100;
                Acct.TotalNoMails = 1000;
                Acct.TotalNoRules = 10;
                Acct.TotalNoItems = 1110;
                //Acct.TotalNoErrors = 0;   don't set these -- adds 1 when it shouldn't
                //Acct.TotalNoWarnings = 0;
                long count = 0;

                long totalCount = 0;
                switch (Acct.Accountnum)
                {
                    case 0:
                        totalCount = 100;
                        break;
                    case 1:
                        totalCount = 200;
                        break;
                    case 2:
                        totalCount = 300;
                        break;
                    case 3:
                        totalCount = 400;
                        break;
                    default:
                        totalCount = 100;
                        break;
                }

                Acct.migrationFolders[0].FolderName = "Contacts";
                Acct.migrationFolders[0].TotalCountOFItems = totalCount;
                Acct.migrationFolders[0].CurrentCountOFItems = 0;

                while (count < totalCount)
                {



                    System.Threading.Thread.Sleep(2000);
                    Acct.migrationFolders[0].CurrentCountOFItems = Acct.migrationFolders[0].CurrentCountOFItems + 20;

                    if (Acct.Accountnum == 0)
                    {
                        if (count == 60)
                        {
                            Acct.LastProblemInfo = new ProblemInfo("John Doe", "Invalid character", ProblemInfo.TYPE_ERR);
                            Acct.TotalNoErrors++;
                        }
                    }
                    count = count + 20;

                }

                Acct.migrationFolders[0].LastFolderInfo = new FolderInfo("Contacts", "Contact", string.Format("{0} of {1}", totalCount.ToString(), totalCount.ToString()));

                switch (Acct.Accountnum)
                {
                    case 0:
                        totalCount = 700;
                        break;
                    case 1:
                        totalCount = 800;
                        break;
                    case 2:
                        totalCount = 900;
                        break;
                    case 3:
                        totalCount = 1000;
                        break;
                    default:
                        totalCount = 1100;
                        break;
                }

                Acct.migrationFolders[0].FolderName = "Mails";
                Acct.migrationFolders[0].TotalCountOFItems = totalCount;
                Acct.migrationFolders[0].CurrentCountOFItems = 0;
                while ((count >= 100) & (count < totalCount))
                {
                    if (Acct.Accountnum == 0)
                    {
                        if (count == 200)
                        {
                            Acct.LastProblemInfo = new ProblemInfo("Message4", "Invalid UID", ProblemInfo.TYPE_ERR);
                            Acct.TotalNoErrors++;
                        }
                        if (count == 400)
                        {
                            Acct.LastProblemInfo = new ProblemInfo("TestMessage", "Invalid Attachment", ProblemInfo.TYPE_ERR);
                            Acct.TotalNoErrors++;
                        }
                        if (count == 500)
                        {
                            Acct.LastProblemInfo = new ProblemInfo("AnotherTest", "Address has an unsupported format", ProblemInfo.TYPE_ERR);
                            Acct.TotalNoErrors++;
                        }
                    }

                    if (Acct.Accountnum == 1)
                    {
                        if (count == 300)
                        {
                            Acct.LastProblemInfo = new ProblemInfo("Status Report", "Illegal recipient", ProblemInfo.TYPE_ERR);
                            Acct.TotalNoErrors++;
                        }
                        if (count == 400)
                        {
                            Acct.LastProblemInfo = new ProblemInfo("Company picnic", "Unsupported encoding", ProblemInfo.TYPE_WARN);
                            Acct.TotalNoWarnings++;
                        }
                        if (count == 600)
                        {
                            Acct.LastProblemInfo = new ProblemInfo("Last call", "Duplicate UID", ProblemInfo.TYPE_WARN);
                            Acct.TotalNoWarnings++;
                        }
                    }


                    System.Threading.Thread.Sleep(2000);
                    Acct.migrationFolders[0].CurrentCountOFItems = Acct.migrationFolders[0].CurrentCountOFItems + 100;
                    count = count + 100;

                }

                Acct.migrationFolders[0].LastFolderInfo = new FolderInfo("Inbox", "Message", string.Format("{0} of {1}", totalCount.ToString(), totalCount.ToString()));

                switch (Acct.Accountnum)
                {
                    case 0:
                        totalCount = 11;
                        break;
                    case 1:
                        totalCount = 12;
                        break;
                    case 2:
                        totalCount = 13
                            ; break;
                    case 3:
                        totalCount = 14;
                        break;
                    default:
                        totalCount = 10;
                        break;
                }

                Acct.migrationFolders[0].FolderName = "Rules";
                Acct.migrationFolders[0].TotalCountOFItems = totalCount;
                Acct.migrationFolders[0].CurrentCountOFItems = 0;

                long tempCount = count;
                while ((count >= tempCount) & (count <= (tempCount + 10)))
                {


                    System.Threading.Thread.Sleep(2000);
                    Acct.migrationFolders[0].CurrentCountOFItems = Acct.migrationFolders[0].CurrentCountOFItems + 10;
                    if (Acct.Accountnum == 0)
                    {
                        if (count == 710)
                        {
                            Acct.LastProblemInfo = new ProblemInfo("BugzillaRule", "Unsupported condition", ProblemInfo.TYPE_ERR);
                            Acct.TotalNoErrors++;
                        }
                    }
                    count = count + 10;


                }

                Acct.migrationFolders[0].LastFolderInfo = new FolderInfo("Inbox", "Rule", string.Format("{0} of {1}", totalCount.ToString(), totalCount.ToString()));

                /*   foreach (MigrationFolder mt in Acct.migrationFolders)
                   {

               
                      mt.FolderName = "Contactsxx";
                      mt.TotalCountOFItems= 100;
                      mt.CurrentCountOFItems = 0;
                       //Acct.migrationFolders.Insert(i,temp);
                   }



                   System.Threading.Thread.Sleep(6000);
                   Acct.migrationFolders[0].CurrentCountOFItems = 30;*/





            }


        }

        //This code is for loading the exchange.dll at runtime instead of adding it as a reference.
        // We will use this code after the Com itnerfaces get finalised and no more changes are required for the COM and MAPI libraires.

               
            //Type userobject;
              //  object userinstance ;
             

                public void testCSMigrationwrapper()
                {

                  //   The following commented code tries to build the Assembly out of com dlls @ runtime instead of referencing to it at compile time.

                    /*           private enum RegKind
                  {
                      RegKind_Default = 0,
                      RegKind_Register = 1,
                      RegKind_None = 2
                  }

                  [DllImport("oleaut32.dll", CharSet = CharSet.Unicode, PreserveSig = false)]
                  private static extern void LoadTypeLibEx(String strTypeLibName, RegKind regKind,
                      [MarshalAs(UnmanagedType.Interface)] out Object typeLib);

                     
                     Object typeLib;
                      LoadTypeLibEx(@"C:\Users\knuthi\Documents\Visual Studio 2010\Projects\TestRegFree\Debug\TestRegFree.dll", RegKind.RegKind_None, out typeLib);

                      if (typeLib == null)
                      {
                          Console.WriteLine("LoadTypeLibEx failed.");
                          return;
                      }

                      TypeLibConverter converter = new TypeLibConverter();
                      ConversionEventHandler eventHandler = new ConversionEventHandler();


                      System.Reflection.Emit.AssemblyBuilder ab = conv.ConvertTypeLibToAssembly(typeLib, "exploretest.dll",
          0, eventHandler, null, null, false);
                      // Save out the Assembly into the cache
                      // Filename should identical
                      ab.Save("exploretest.dll");
          */
                    Type userobject;
                    object userinstance ;
                    //Assembly testAssembly = Assembly.LoadFile(@"C:\Users\knuthi\Documents\Visual Studio 2010\Projects\TestRegClint\TestRegClint\bin\Debug\exploretest.dll");
                    Assembly testAssembly = Assembly.LoadFile(@"C:\Code\zimbra\main\ZimbraMigrationTools\src\c\Win32\dbg\interop.Exchange.dll");

                    Type[] types = testAssembly.GetTypes();

                    Type calcType = testAssembly.GetType("Exchange.MapiWrapperClass");

                    object calcInstance = Activator.CreateInstance(calcType);



                    //object o = null;
                    ParameterModifier pm = new ParameterModifier(1);
                    pm[0] = true;
                    ParameterModifier[] mods = { pm };

                    object[] MyArgs = new object[3];
                    MyArgs[0] = "10.20.136.140";
                    MyArgs[1] = "7071";
                    MyArgs[2] = "MyAdmin";


                    string value = (string)calcType.InvokeMember("ConnectToServer",
              BindingFlags.InvokeMethod | BindingFlags.Instance | BindingFlags.Public,
              null, calcInstance, MyArgs, mods, null, null);



                    Console.WriteLine(MyArgs[0]);
                    Console.ReadLine();

                    userobject = testAssembly.GetType("Exchange.UserObjectClass");
                    userinstance = Activator.CreateInstance(userobject);
           

                    api = new ZimbraAPI();
                }
         
           public void testStartMigration(MigrationAccount Acct, bool UIflag = true)
                {
               Type userobject;
             object userinstance ;
               Assembly testAssembly = Assembly.LoadFile(@"C:\Code\zimbra\main\ZimbraMigrationTools\src\c\Win32\dbg\interop.Exchange.dll");

                userobject = testAssembly.GetType("Exchange.UserObjectClass");
                userinstance = Activator.CreateInstance(userobject);

                    //GetListofMapiFolders(Acct.Accountname);

                    if (!UIflag)
                    {

                        ParameterModifier pm = new ParameterModifier(1);
                        pm[0] = true;
                        ParameterModifier[] mods = { pm };

                        object[] MyArgs = new object[4];
                        MyArgs[0] = "";
                        MyArgs[1] = "";
                        MyArgs[2] = Acct.AccountID;
                        MyArgs[3] = "MAPI";


                        string value = (string)userobject.InvokeMember("InitializeUser",
                  BindingFlags.InvokeMethod | BindingFlags.Instance | BindingFlags.Public,
                  null, userinstance, MyArgs, mods, null, null);
                }
                
           }
    }


    }

