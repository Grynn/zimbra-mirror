using System.Collections.Generic;
using System.Linq;
using System.Text;
using System;
using System.IO;
using System.Reflection;

namespace CssLib
{
[Flags]
public enum ItemsAndFoldersOptions
{

    Junk         = 0x0080,
    DeletedItems = 0x0040,
    Sent         = 0x0020,
    Rules        = 0x0010,
    Tasks        = 0x0008,
    Calendar     = 0x0004,
    Contacts     = 0x0002,
    Mail         = 0x0001,
    None         = 0x0000
}

public class MigrationOptions
{
    public ItemsAndFoldersOptions ItemsAndFolders;
    public string DateFilter;
    public string AttachmentFilter;
    public string SkipFolders;
}

        
public class CSMigrationwrapper
{
    /* string m_ConfigXMLFile;
     *
     * public string ConfigXMLFile
     * {
     *   get { return m_ConfigXMLFile; }
     *   set { m_ConfigXMLFile = value; }
     * }
     * string m_UserMapFile;
     *
     * public string UserMapFile
     * {
     *   get { return m_UserMapFile; }
     *   set { m_UserMapFile = value; }
     * }*/

  
    Assembly sourceProvider; 

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
    public string MailClient {
        get { return m_MailClient; }
        set { m_MailClient = value; }
    }
    /*   MVVM.Model.Config ConfigObj = new MVVM.Model.Config();
     * MVVM.Model.ImportOptions ImportOptions = new MVVM.Model.ImportOptions();
     * MVVM.Model.Users  users = new MVVM.Model.Users();*/

    dynamic MailWrapper;

    public CSMigrationwrapper()
    {
        string path = System.AppDomain.CurrentDomain.BaseDirectory + "interop.Exchange.dll";

        sourceProvider = Assembly.LoadFile(path);
        if (sourceProvider == null)
        {
            Console.WriteLine("Assembly dll file cannot be found");
            return;
        }
        object userinstance;
        Type[] types = sourceProvider.GetTypes();

        userobject = sourceProvider.GetType("Exchange.UserObjectClass");
        userinstance = Activator.CreateInstance(userobject);

        folderobject = sourceProvider.GetType("Exchange.folderObjectClass");
          

        itemobject = sourceProvider.GetType("Exchange.ItemObjectClass");
           

        MailWrapper = sourceProvider.GetType("Exchange.MapiWrapperClass");
            

        api = new ZimbraAPI();
    }
    /*private void CreateConfig(string Xmlfilename)
     * {
     *
     *
     *  System.Xml.Serialization.XmlSerializer reader =
     *  new System.Xml.Serialization.XmlSerializer(typeof(MVVM.Model.Config));
     *  if (File.Exists(Xmlfilename))
     *  {
     *
     *      System.IO.StreamReader fileRead = new System.IO.StreamReader(
     *             Xmlfilename);
     *
     *      ConfigObj = (MVVM.Model.Config)reader.Deserialize(fileRead);
     *
     *  }
     * }
     */
    public void InitializeInterop()
    {
        if (MailClient == "MAPI")
        {
            Type calcType = sourceProvider.GetType("Exchange.MapiWrapperClass");

            object calcInstance = Activator.CreateInstance(calcType);
            
        }
    }
    public string InitializeMailClient(string Target, string AdminUser, string AdminPassword)
    {
        string s = "";


        if (MailClient == "MAPI")
        {
            Type calcType = sourceProvider.GetType("Exchange.MapiWrapperClass");

            object calcInstance = Activator.CreateInstance(calcType);
            ParameterModifier pm = new ParameterModifier(1);
            pm[0] = true;
            ParameterModifier[] mods = { pm };

            object[] MyArgs = new object[3];
            MyArgs[0] = Target;
            MyArgs[1] = AdminUser;
            MyArgs[2] = AdminPassword;


            s = (string)calcType.InvokeMember("GlobalInit",
                   BindingFlags.InvokeMethod | BindingFlags.Instance | BindingFlags.Public,
                   null, calcInstance, MyArgs, null, null, null);
            
        
        }
        return s;
    }
    public string UninitializeMailClient()
    {
        string s = "";

        if (MailClient == "MAPI")
        {
            Type calcType = sourceProvider.GetType("Exchange.MapiWrapperClass");

            object calcInstance = Activator.CreateInstance(calcType);


             s = (string)calcType.InvokeMember("GlobalUninit",
                    BindingFlags.InvokeMethod | BindingFlags.Instance | BindingFlags.Public,
                    null, calcInstance, null, null, null, null);
            
        
        }
        return s;
    }
    public void Initalize(string HostName, string Port, string AdminAccount, string UserID,
            string Mailserver,
            string AdminID)
    {
        // CreateConfig(ConfigXMLFile);
        int status = 0;

        Type calcType = sourceProvider.GetType("Exchange.MapiWrapperClass");

        object calcInstance = Activator.CreateInstance(calcType);

        ParameterModifier pm = new ParameterModifier(1);
        pm[0] = true;
        ParameterModifier[] mods = { pm };

        object[] MyArgs = new object[3];
        MyArgs[0] = Mailserver;
        MyArgs[1] = Port;
        MyArgs[2] = AdminID;

        string value = (string)calcType.InvokeMember("ConnectToServer",
                BindingFlags.InvokeMethod | BindingFlags.Instance | BindingFlags.Public,
                null, calcInstance, MyArgs, mods, null, null);
            
        status = api.Logon(HostName, Port, AdminAccount, "test123", true);

        // Initilaize user object

        // O1.InitializeUser(Mailserver, AdminID, UserID, "MAPI");
    }
    public void Migrate(string MailOptions)
    {

        MailWrapper.ImportMailOptions(MailOptions);
    }
    public string[] GetListofMapiProfiles()
    {
        object var = new object();


        Type calcType = sourceProvider.GetType("Exchange.MapiWrapperClass");

        object calcInstance = Activator.CreateInstance(calcType);

        // object o = null;
        ParameterModifier pm = new ParameterModifier(1);
        pm[0] = true;
        ParameterModifier[] mods = { pm };

        object[] MyArgs = new object[1];
        
       

       var = calcType.InvokeMember("GetProfilelist",
                BindingFlags.InvokeMethod | BindingFlags.Instance | BindingFlags.Public,
                null, calcInstance, MyArgs, mods, null, null);
        


      //  MailWrapper.GetProfilelist(out var);

       string[] s = (string[])MyArgs[0];

        return s;
    }

    public string[] GetListFromObjectPicker()
    {
        // Change this to above signature when I start getting the real ObjectPicker object back
        object var = new object();
        
        Type calcType = sourceProvider.GetType("Exchange.MapiWrapperClass");
        object calcInstance = Activator.CreateInstance(calcType);
        ParameterModifier pm = new ParameterModifier(1);
        pm[0] = true;
        ParameterModifier[] mods = { pm };

        object[] MyArgs = new object[1];

        var = calcType.InvokeMember("SelectExchangeUsers",
                 BindingFlags.InvokeMethod | BindingFlags.Instance | BindingFlags.Public,
                 null, calcInstance, MyArgs, mods, null, null);
        
        //MailWrapper.SelectExchangeUsers(out var);
        string[] s = (string[])MyArgs[0];
        return s;
    }

    private int ComputeTotalMigrationCount(MigrationOptions importopts)
    {
        int count = 0;

        // set up check for skipping folders
        List<string> skipList = new List<string>();
        string skipfolders = importopts.SkipFolders;
        if (skipfolders != null)
        {
            if (skipfolders.Length > 0)
            {
                string[] tokens = skipfolders.Split(',');
                for (int i = 0; i < tokens.Length; i++)
                {
                    string token = tokens.GetValue(i).ToString();
                    skipList.Add(token.Trim());
                }
            }
        }
        ///

        foreach (dynamic folderobject in folderobjectarray)
        {
            // FBS NOTE THAT THESE ARE EXCHANGE SPECIFIC.  WE'LL HAVE TO CHANGE THIS GROU GROUPWISE !!!
            if ((folderobject.Name == "Sent Items") && !(importopts.ItemsAndFolders.HasFlag(ItemsAndFoldersOptions.Sent)))
            {
                continue;
            }
            if ((folderobject.Name == "Deleted Items") && !(importopts.ItemsAndFolders.HasFlag(ItemsAndFoldersOptions.DeletedItems)))
            {
                continue;
            }
            if ((folderobject.Name == "Junk E-Mail") && !(importopts.ItemsAndFolders.HasFlag(ItemsAndFoldersOptions.Junk)))
            {
                continue;
            }
            if ((folderobject.ContainerClass == "IPF.Contact") && !(importopts.ItemsAndFolders.HasFlag(ItemsAndFoldersOptions.Contacts)))
            {
                continue;
            }
            if ((folderobject.ContainerClass == "IPF.Appointment") && !(importopts.ItemsAndFolders.HasFlag(ItemsAndFoldersOptions.Calendar)))
            {
                continue;
            }
            if ((folderobject.ContainerClass == "IPF.Task") && !(importopts.ItemsAndFolders.HasFlag(ItemsAndFoldersOptions.Tasks)))
            {
                continue;
            }
            if ((folderobject.ContainerClass == "IPF.Note") && !(importopts.ItemsAndFolders.HasFlag(ItemsAndFoldersOptions.Mail)))
            {
                continue;
            }
            ////

            // check if we want to skip any folders
            bool bSkipIt = false;
            for (int i = 0; i < skipList.Count; i++)
            {
                if (folderobject.Name == skipList[i])
                {
                    bSkipIt = true;
                    break;
                }
            }
            if (bSkipIt)
            {
                continue;
            }
            ////

            // ANOTHER TEMP -- REMOVE WHEN WE DO APPOINTMENTS
            if (folderobject.ContainerClass == "IPF.Appointment")
            {
                continue;
            }

            // ANOTHER TEMP -- REMOVE WHEN WE DO TASKS
            if (folderobject.ContainerClass == "IPF.Task")
            {
                continue;
            }

            count += folderobject.ItemCount;
        }
        return count;
    }

    private string GetFolderViewType(string containerClass)
    {
        string retval = "";     // if it's a "message", blanks are cool
        if (containerClass == "IPF.Contact")
        {
            retval = "contact";
        }
        else
        if (containerClass == "IPF.Appointment")
        {
            retval = "appointment";
        }
        else
        if (containerClass == "IPF.Task")
        {
            retval = "task";
        }
        return retval;
    }

    private void ProcessItems(MigrationAccount Acct, dynamic folderobject, foldertype ftype, ZimbraAPI api, string path)
    {
        DateTime dt;
        dt = DateTime.UtcNow;

       /* itemobjectarray = userobject.GetItemsForFolderObjects(
                folderobject, (int)ftype, dt.ToOADate());*/
        Type userobject;
        object userinstance;
        
        userobject = sourceProvider.GetType("Exchange.UserObjectClass");
        userinstance = Activator.CreateInstance(userobject);
        // GetListofMapiFolders(Acct.Accountname);

        ParameterModifier pm = new ParameterModifier(1);
        pm[0] = true;
        ParameterModifier[] mods = { pm };

        object[] MyArgs = new object[3];
        MyArgs[0] = folderobject;
        MyArgs[1] = ftype;
        MyArgs[2] = dt.ToOADate();
        // MyArgs[3] = "MAPI";

        itemobjectarray = (object[])userobject.InvokeMember("GetItemsForFolderObjects",
                BindingFlags.InvokeMethod | BindingFlags.Instance | BindingFlags.Public,
                null, userinstance, MyArgs, mods, null, null);




        Type itemObject;
        object iteminstance;

        itemObject = sourceProvider.GetType("Exchange.ItemObjectClass");
        iteminstance = Activator.CreateInstance(itemObject);

        int iProcessedItems = 0;
        while (iProcessedItems < Acct.migrationFolder.TotalCountOFItems)               
        {
            foreach (dynamic itemobject in itemobjectarray)
            {
                if (itemobject != null)
                {
                    Dictionary<string, string> dict = new Dictionary<string, string>();
                    foldertype type = (foldertype)itemobject.Type;
                    if (type == ftype)
                    {
                        // string[,] data = O1.GetDataForItem(I1.ItemID);

                        object[] MyArgas = new object[2];
                        MyArgas[0] = itemobject.ItemID;
                        MyArgas[1] = itemobject.Type;

                        // MyArgs[3] = "MAPI";

                        string[,] data = (string[,])itemObject.InvokeMember("GetDataForItemID",
                                BindingFlags.InvokeMethod | BindingFlags.Instance | BindingFlags.Public,
                                null, iteminstance, MyArgas, null, null, null);

                      /*  string[,] data = itemobject.GetDataForItemID(
                                itemobject.ItemID);*/

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
                    {
                        int stat = 0;
                        if (ftype == foldertype.Mail)
                        {
                            dict.Add("folderId", folderobject.FolderPath);
                            dict.Add("tags", "");
                            stat = api.AddMessage(dict);
                        }
                        else
                        if (ftype == foldertype.Contacts)
                        {
                            stat = api.CreateContact(dict, path);
                        }
                    }
                    Acct.migrationFolder.CurrentCountOFItems++;
                }
                iProcessedItems++;
            }
        }
    }

    public void EndUserMigration()
    {
        Type userobject;
        object userinstance;

        userobject = sourceProvider.GetType("Exchange.UserObjectClass");
        userinstance = Activator.CreateInstance(userobject);

        ParameterModifier pm = new ParameterModifier(1);
        pm[0] = true;
        ParameterModifier[] mods = { pm };

        object[] MyArgs = new object[1];
        MyArgs[0] = "MAPI";
        userobject.InvokeMember("UMUnInitializeUser",
                BindingFlags.InvokeMethod | BindingFlags.Instance | BindingFlags.Public,
                null, userinstance, MyArgs, mods, null, null);
    }

    public void StartMigration(MigrationAccount Acct, MigrationOptions importopts, bool isServer = true, bool isPreview = false)
    {
        Type userobject;
        object userinstance;

        userobject = sourceProvider.GetType("Exchange.UserObjectClass");
        userinstance = Activator.CreateInstance(userobject);

        if (!isPreview)
        {
            ParameterModifier pm = new ParameterModifier(1);
            pm[0] = true;
            ParameterModifier[] mods = { pm };

            string value = "";
            if (isServer)
            {
                object[] MyArgs = new object[4];
                MyArgs[0] = "";
                MyArgs[1] = "";
                MyArgs[2] = Acct.AccountID;
                MyArgs[3] = "MAPI";

                value = (string)userobject.InvokeMember("InitializeUser",
                        BindingFlags.InvokeMethod | BindingFlags.Instance | BindingFlags.Public,
                        null, userinstance, MyArgs, mods, null, null);
            }
            else
            {
                object[] MyArgs = new object[2];
                MyArgs[0] = Acct.AccountID;
                MyArgs[1] = "MAPI";

                value = (string)userobject.InvokeMember("UMInitializeUser",
                        BindingFlags.InvokeMethod | BindingFlags.Instance | BindingFlags.Public,
                        null, userinstance, MyArgs, mods, null, null);
            }

            if (value.Length > 0)
            {
                Acct.LastProblemInfo = new ProblemInfo(value, "Error", ProblemInfo.TYPE_ERR);
                Acct.TotalNoErrors++;
                return;
            }

            folderobjectarray = (object[])userobject.InvokeMember("GetFolderObjects",
                   BindingFlags.InvokeMethod | BindingFlags.Instance | BindingFlags.Public,
                   null, userinstance, null, null, null, null);
            Acct.migrationFolder.CurrentCountOFItems = folderobjectarray.Count();

            Acct.TotalNoItems = ComputeTotalMigrationCount(importopts);

            ZimbraAPI api = new ZimbraAPI();

            // see if we're migrating a .pst file. If we are, we need to know that so we can mess with folder names in ZimbraAPI
            string u = Acct.AccountID.ToUpper();
            api.AcctIdIsPST = u.EndsWith(".PST");
            /////

            // set up check for skipping folders
            List <string> skipList = new List<string>();
            string skipfolders = importopts.SkipFolders;
            if (skipfolders != null)
            {
                if (skipfolders.Length > 0)
                {
                    string[] tokens = skipfolders.Split(',');
                    for (int i = 0; i < tokens.Length; i++)
                    {
                        string token = tokens.GetValue(i).ToString();
                        skipList.Add(token.Trim());
                    }
                }
            }
            ///

            foreach (dynamic folderobject in folderobjectarray)
            {
                string path = "";

                // FBS NOTE THAT THESE ARE EXCHANGE SPECIFIC.  WE'LL HAVE TO CHANGE THIS FOR GROUPWISE !!!
                if ((folderobject.Name == "Sent Items") && !(importopts.ItemsAndFolders.HasFlag(ItemsAndFoldersOptions.Sent)))
                {
                    continue;
                }
                if ((folderobject.Name == "Deleted Items") && !(importopts.ItemsAndFolders.HasFlag(ItemsAndFoldersOptions.DeletedItems)))
                {
                    continue;
                }
                if ((folderobject.Name == "Junk E-Mail") && !(importopts.ItemsAndFolders.HasFlag(ItemsAndFoldersOptions.Junk)))
                {
                    continue;
                }
                if ((folderobject.ContainerClass == "IPF.Contact") && !(importopts.ItemsAndFolders.HasFlag(ItemsAndFoldersOptions.Contacts)))
                {
                    continue;
                }
                if ((folderobject.ContainerClass == "IPF.Appointment") && !(importopts.ItemsAndFolders.HasFlag(ItemsAndFoldersOptions.Calendar)))
                {
                    continue;
                }
                if ((folderobject.ContainerClass == "IPF.Note") && !(importopts.ItemsAndFolders.HasFlag(ItemsAndFoldersOptions.Mail)))
                {
                    continue;
                }
                ////

                if (folderobject.ItemCount == 0)
                {
                    continue;
                }

                // check if we want to skip any folders
                bool bSkipIt = false;
                for (int i = 0; i < skipList.Count; i++)
                {
                    if (folderobject.Name == skipList[i])
                    {
                        bSkipIt = true;
                        break;
                    }
                }
                if (bSkipIt)
                {
                    continue;
                }
                ////

                // ANOTHER TEMP -- REMOVE WHEN WE DO APPOINTMENTS
                if (folderobject.ContainerClass == "IPF.Appointment")
                {
                    continue;
                }

                // ANOTHER TEMP -- REMOVE WHEN WE DO TASKS
                if (folderobject.ContainerClass == "IPF.Task")
                {
                    continue;
                }

                if (folderobject.Id == 0)
                {
                    api.AccountName = Acct.Accountname;
                    string ViewType = GetFolderViewType(folderobject.ContainerClass);
                    int stat = api.CreateFolder(folderobject.FolderPath, ViewType);
                    path = folderobject.FolderPath;
                }

                // Set FolderName at the end, since we trigger results on that, so we need all the values set
                Acct.migrationFolder.TotalCountOFItems = folderobject.ItemCount;//itemobjectarray.Count();
                Acct.migrationFolder.CurrentCountOFItems = 0;
                Acct.migrationFolder.FolderView = folderobject.ContainerClass;
                Acct.migrationFolder.FolderName = folderobject.Name;

                if (importopts.ItemsAndFolders.HasFlag(ItemsAndFoldersOptions.Contacts))
                {
                    if (folderobject.Name == "Deleted Items")   //FBS EXCHANGE SPECIFIC HACK.  CHANGE FOR GROUPWISE !!! 
                    {
                        path = api.AcctIdIsPST ? "/Top of Outlook data file/Deleted Items" : "/Top of Information Store/Deleted Items";
                    }
                    ProcessItems(Acct, folderobject, foldertype.Contacts, api, path);
                }
                if (importopts.ItemsAndFolders.HasFlag(ItemsAndFoldersOptions.Mail))
                {
                    ProcessItems(Acct, folderobject, foldertype.Mail, api, path);
                }
            }
        }
        else
        {
            Acct.TotalNoContacts = 100;
            Acct.TotalNoMails = 1000;
            Acct.TotalNoRules = 10;
            Acct.TotalNoItems = 1110;
            // Acct.TotalNoErrors = 0;   don't set these -- adds 1 when it shouldn't
            // Acct.TotalNoWarnings = 0;
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
            Acct.migrationFolder.FolderName = "Contacts";
            Acct.migrationFolder.TotalCountOFItems = totalCount;
            Acct.migrationFolder.CurrentCountOFItems = 0;
            while (count < totalCount)
            {
                System.Threading.Thread.Sleep(2000);
                Acct.migrationFolder.CurrentCountOFItems =
                        Acct.migrationFolder.CurrentCountOFItems + 20;
                if (Acct.Accountnum == 0)
                {
                    if (count == 60)
                    {
                        Acct.LastProblemInfo = new ProblemInfo("John Doe", "Invalid character",
                                ProblemInfo.TYPE_ERR);
                        Acct.TotalNoErrors++;
                    }
                }
                count = count + 20;
            }
            Acct.migrationFolder.LastFolderInfo =
                    new FolderInfo("Contacts", "Contact", string.Format("{0} of {1}",
                        totalCount.ToString(), totalCount.ToString()));
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
            Acct.migrationFolder.FolderName = "Inbox";
            Acct.migrationFolder.TotalCountOFItems = totalCount;
            Acct.migrationFolder.CurrentCountOFItems = 0;
            while ((count >= 100) & (count < totalCount))
            {
                if (Acct.Accountnum == 0)
                {
                    if (count == 200)
                    {
                        Acct.LastProblemInfo = new ProblemInfo("Message4", "Invalid UID",
                                ProblemInfo.TYPE_ERR);
                        Acct.TotalNoErrors++;
                    }
                    if (count == 400)
                    {
                        Acct.LastProblemInfo =
                                new ProblemInfo("TestMessage", "Invalid Attachment",
                                ProblemInfo.TYPE_ERR);
                        Acct.TotalNoErrors++;
                    }
                    if (count == 500)
                    {
                        Acct.LastProblemInfo = new ProblemInfo(
                                "AnotherTest", "Address has an unsupported format",
                                ProblemInfo.TYPE_ERR);
                        Acct.TotalNoErrors++;
                    }
                }
                if (Acct.Accountnum == 1)
                {
                    if (count == 300)
                    {
                        Acct.LastProblemInfo =
                                new ProblemInfo("Status Report", "Illegal recipient",
                                ProblemInfo.TYPE_ERR);
                        Acct.TotalNoErrors++;
                    }
                    if (count == 400)
                    {
                        Acct.LastProblemInfo =
                                new ProblemInfo("Company picnic", "Unsupported encoding",
                                ProblemInfo.TYPE_WARN);
                        Acct.TotalNoWarnings++;
                    }
                    if (count == 600)
                    {
                        Acct.LastProblemInfo = new ProblemInfo("Last call", "Duplicate UID",
                                ProblemInfo.TYPE_WARN);
                        Acct.TotalNoWarnings++;
                    }
                }
                System.Threading.Thread.Sleep(2000);
                Acct.migrationFolder.CurrentCountOFItems =
                        Acct.migrationFolder.CurrentCountOFItems + 100;
                count = count + 100;
            }
            Acct.migrationFolder.LastFolderInfo =
                    new FolderInfo("Inbox", "Message", string.Format("{0} of {1}",
                        totalCount.ToString(), totalCount.ToString()));
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
            Acct.migrationFolder.FolderName = "Rules";
            Acct.migrationFolder.TotalCountOFItems = totalCount;
            Acct.migrationFolder.CurrentCountOFItems = 0;

            long tempCount = count;
            while ((count >= tempCount) & (count <= (tempCount + 10)))
            {
                System.Threading.Thread.Sleep(2000);
                Acct.migrationFolder.CurrentCountOFItems =
                        Acct.migrationFolder.CurrentCountOFItems + 10;
                if (Acct.Accountnum == 0)
                {
                    if (count == 710)
                    {
                        Acct.LastProblemInfo =
                                new ProblemInfo("BugzillaRule", "Unsupported condition",
                                ProblemInfo.TYPE_ERR);
                        Acct.TotalNoErrors++;
                    }
                }
                count = count + 10;
            }
            Acct.migrationFolder.LastFolderInfo =
                    new FolderInfo("Inbox", "Rule", string.Format("{0} of {1}",
                        totalCount.ToString(), totalCount.ToString()));
        }
    }

    // This code is for loading the exchange.dll at runtime instead of adding it as a reference.
    // We will use this code after the Com itnerfaces get finalised and no more changes are required for the COM and MAPI libraires.

    // Type userobject;
    // object userinstance ;

    public void testCSMigrationwrapper()
    {
        // The following commented code tries to build the Assembly out of com dlls @ runtime instead of referencing to it at compile time.

        /*           private enum RegKind
         * {
         * RegKind_Default = 0,
         * RegKind_Register = 1,
         * RegKind_None = 2
         * }
         *
         * [DllImport("oleaut32.dll", CharSet = CharSet.Unicode, PreserveSig = false)]
         * private static extern void LoadTypeLibEx(String strTypeLibName, RegKind regKind,
         * [MarshalAs(UnmanagedType.Interface)] out Object typeLib);
         *
         *
         * Object typeLib;
         * LoadTypeLibEx(@"C:\Users\knuthi\Documents\Visual Studio 2010\Projects\TestRegFree\Debug\TestRegFree.dll", RegKind.RegKind_None, out typeLib);
         *
         * if (typeLib == null)
         * {
         *    Console.WriteLine("LoadTypeLibEx failed.");
         *    return;
         * }
         *
         * TypeLibConverter converter = new TypeLibConverter();
         * ConversionEventHandler eventHandler = new ConversionEventHandler();
         *
         *
         * System.Reflection.Emit.AssemblyBuilder ab = conv.ConvertTypeLibToAssembly(typeLib, "exploretest.dll",
         * 0, eventHandler, null, null, false);
         * // Save out the Assembly into the cache
         * // Filename should identical
         * ab.Save("exploretest.dll");
         */
        Type userobject;
        object userinstance;
        // Assembly testAssembly = Assembly.LoadFile(@"C:\Users\knuthi\Documents\Visual Studio 2010\Projects\TestRegClint\TestRegClint\bin\Debug\exploretest.dll");
        Type[] types = sourceProvider.GetTypes();

        Type calcType = sourceProvider.GetType("Exchange.MapiWrapperClass");

        object calcInstance = Activator.CreateInstance(calcType);

        // object o = null;
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

        userobject = sourceProvider.GetType("Exchange.UserObjectClass");
        userinstance = Activator.CreateInstance(userobject);

        api = new ZimbraAPI();
    }

}
}
