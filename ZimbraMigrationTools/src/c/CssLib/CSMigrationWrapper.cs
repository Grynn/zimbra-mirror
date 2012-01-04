using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Reflection;
using System.Runtime.InteropServices;
using System.Text;

namespace CssLib
{
public class Log {
    public enum Level { None, Err, Warn, Info, Debug };

    public static void init(string file, Level level) {
        log_init(file, level);
    }

    public static void log(Level level, object obj) {
        log_print(level, obj.ToString());
    }

    public static void log(Level level, params object[] objs) {
        string s = "";

        foreach (object obj in objs) {
            if (s.Length > 0)
                s += ' ';
            s += obj.ToString();
        }
        log_print(level, s);
    }

    public static void err(string str) { log_print(Level.Err, str); }
    public static void err(params object[] objs) { log(Level.Err, objs); }
    public static void warn(string str) { log_print(Level.Warn, str); }
    public static void warn(params object[] objs) { log(Level.Warn, objs); }
    public static void info(string str) { log_print(Level.Info, str); }
    public static void info(params object[] objs) { log(Level.Info, objs); }
    public static void debug(string str) { log_print(Level.Debug, str); }
    public static void debug(params object[] objs) { log(Level.Debug, objs); }

    public static void open(string file) { log_open(file); }

    public static void prefix(string prefix) {
        log_prefix(prefix);
    }

    #region PInvokes

    [DllImport("CppLib.dll", CallingConvention = CallingConvention.Cdecl, CharSet = CharSet.Unicode)]
    static private extern void log_init(string file, Level level);

    [DllImport("CppLib.dll", CallingConvention = CallingConvention.Cdecl, CharSet = CharSet.Unicode)]
    static private extern void log_open(string file);

    [DllImport("CppLib.dll", CallingConvention = CallingConvention.Cdecl, CharSet = CharSet.Unicode)]
    static private extern void log_prefix(string prefix);

    [DllImport("CppLib.dll", CallingConvention = CallingConvention.Cdecl, CharSet = CharSet.Unicode)]
    static private extern void log_print(Level level, string str);

    #endregion PInvokes
}

[Flags] public enum ItemsAndFoldersOptions
{
    Junk = 0x0080, DeletedItems = 0x0040, Sent = 0x0020, Rules = 0x0010, Tasks = 0x0008,
    Calendar = 0x0004, Contacts = 0x0002, Mail = 0x0001, None = 0x0000
}

public enum ZimbraFolders
{
    Min = 0, UserRoot = 1, Inbox = 2, Trash = 3, Junk = 4, Sent = 5, Drafts = 6,
    Contacts = 7, Tags = 8, Conversations = 9, Calendar = 10, MailboxRoot = 11, Wiki = 12,
    EmailedContacts = 13, Chats = 14, Tasks = 15, Max = 16
}

public class MigrationOptions
{
    public ItemsAndFoldersOptions ItemsAndFolders;

    public string DateFilter;
    public string MessageSizeFilter;
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

    ZimbraAPI api;
    enum foldertype
    {
        Mail = 1, Contacts = 2, Calendar = 3
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

        Log.init(Path.GetTempPath() + "migration.log", Log.Level.Debug);
        Log.info("initialize");
        sourceProvider = Assembly.LoadFile(path);
        if (sourceProvider == null)
        {
            Console.WriteLine("Assembly dll file cannot be found");
            return;
        }

        Type[] types = sourceProvider.GetTypes();

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

            try
            {
                s = (string)calcType.InvokeMember("GlobalInit", BindingFlags.InvokeMethod |
                    BindingFlags.Instance | BindingFlags.Public, null, calcInstance, MyArgs,
                    null, null, null);
            }
            catch (Exception e)
            {
                s = string.Format("Initialization Exception.  Make sure to enter the proper credentials.\n{0}", e.Message);
            }
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

            s = (string)calcType.InvokeMember("GlobalUninit", BindingFlags.InvokeMethod |
                BindingFlags.Instance | BindingFlags.Public, null, calcInstance, null, null,
                null, null);
        }
        return s;
    }

    public void Initalize(string HostName, string Port, string AdminAccount, string UserID,
        string Mailserver, string AdminID)
    {
        // CreateConfig(ConfigXMLFile);
        int status = 0;

        Log.open(Path.GetTempPath() + UserID + ".log");

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
            BindingFlags.InvokeMethod | BindingFlags.Instance | BindingFlags.Public, null,
            calcInstance, MyArgs, mods, null, null);

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

        var = calcType.InvokeMember("GetProfilelist", BindingFlags.InvokeMethod |
            BindingFlags.Instance | BindingFlags.Public, null, calcInstance, MyArgs, mods,
            null, null);

        // MailWrapper.GetProfilelist(out var);

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

        var = calcType.InvokeMember("SelectExchangeUsers", BindingFlags.InvokeMethod |
            BindingFlags.Instance | BindingFlags.Public, null, calcInstance, MyArgs, mods,
            null, null);

        // MailWrapper.SelectExchangeUsers(out var);
        string[] s = (string[])MyArgs[0];
        return s;
    }

    private int ComputeTotalMigrationCount(MigrationOptions importopts, dynamic[] folderobjectarray)
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
        // /
        foreach (dynamic folderobject in folderobjectarray)
        {
            if ((folderobject.Id == (int)ZimbraFolders.Sent) && !(importopts.ItemsAndFolders.HasFlag(
                    ItemsAndFoldersOptions.Sent)))
                    continue;
            if ((folderobject.Id == (int)ZimbraFolders.Trash) && !(importopts.ItemsAndFolders.HasFlag(
                ItemsAndFoldersOptions.DeletedItems)))
                continue;
            if ((folderobject.Id == (int)ZimbraFolders.Junk) && !(importopts.ItemsAndFolders.HasFlag(
                ItemsAndFoldersOptions.Junk)))
                continue;
            if ((folderobject.ContainerClass == "IPF.Contact") &&
                !(importopts.ItemsAndFolders.HasFlag(ItemsAndFoldersOptions.Contacts)))
                continue;
            if ((folderobject.ContainerClass == "IPF.Appointment") &&
                !(importopts.ItemsAndFolders.HasFlag(ItemsAndFoldersOptions.Calendar)))
                continue;
            if ((folderobject.ContainerClass == "IPF.Task") &&
                !(importopts.ItemsAndFolders.HasFlag(ItemsAndFoldersOptions.Tasks)))
                continue;
            if ((folderobject.ContainerClass == "IPF.Note") &&
                !(importopts.ItemsAndFolders.HasFlag(ItemsAndFoldersOptions.Mail)))
                continue;
             // //

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
                continue;
             // //
             // ANOTHER TEMP -- REMOVE WHEN WE DO TASKS
            if (folderobject.ContainerClass == "IPF.Task")
                continue;
            count += folderobject.ItemCount;
        }
        return count;
    }

    private string GetFolderViewType(string containerClass)
    {
        string retval = "";                     // if it's a "message", blanks are cool

        if (containerClass == "IPF.Contact")
            retval = "contact";

        else if (containerClass == "IPF.Appointment")
            retval = "appointment";

        else if (containerClass == "IPF.Task")
            retval = "task";
        return retval;
    }

    private bool ProcessIt(MigrationOptions importopts, foldertype type)
    {
        bool retval = false;
        switch (type)
        {
            case foldertype.Mail:
                retval = importopts.ItemsAndFolders.HasFlag(ItemsAndFoldersOptions.Mail);
                break;
            case foldertype.Calendar:
                retval = importopts.ItemsAndFolders.HasFlag(ItemsAndFoldersOptions.Calendar);
                break;
            case foldertype.Contacts:
                retval = importopts.ItemsAndFolders.HasFlag(ItemsAndFoldersOptions.Contacts);
                break;
            default:
                break;
        }
        return retval;
    }

    private void ProcessItems(MigrationAccount Acct, bool isServer, dynamic userobject, object userinstance, dynamic folderobject,
        ZimbraAPI api, string path, MigrationOptions importopts)
    {
        DateTime dt;

        dt = DateTime.UtcNow;

        dynamic[] itemobjectarray;

        ParameterModifier pm = new ParameterModifier(1);

        pm[0] = true;
        ParameterModifier[] mods = { pm };

        object[] MyArgs = new object[2];
        MyArgs[0] = folderobject;
        MyArgs[1] = dt.ToOADate();

        itemobjectarray = (object[])userobject.InvokeMember("GetItemsForFolderObjects",
            BindingFlags.InvokeMethod | BindingFlags.Instance | BindingFlags.Public, null,
            userinstance, MyArgs, mods, null, null);

        Type itemObject;
        object iteminstance;

        itemObject = sourceProvider.GetType("Exchange.ItemObjectClass");
        iteminstance = Activator.CreateInstance(itemObject);

        int iProcessedItems = 0;

        if (itemobjectarray.GetLength(0) > 0)
        {
            while (iProcessedItems < Acct.migrationFolder.TotalCountOFItems)
            {
                Log.debug("Processing folder", folderobject.Name, "-- Total items:", folderobject.ItemCount);
                foreach (dynamic itemobject in itemobjectarray)
                {
                    foldertype type = (foldertype)itemobject.Type;
                    if (ProcessIt(importopts, type))
                    {
                        bool bSkipMessage = false;
                        Dictionary<string, string> dict = new Dictionary<string, string>();

                        object[] MyArgas = new object[3];
                        MyArgas[0] = isServer ? Acct.AccountID : "";
                        MyArgas[1] = itemobject.ItemID;
                        MyArgas[2] = itemobject.Type;

                        // MyArgs[3] = "MAPI";

                        string[,] data = (string[,])itemObject.InvokeMember(
                                "GetDataForItemID", BindingFlags.InvokeMethod |
                                BindingFlags.Instance | BindingFlags.Public, null, iteminstance,
                                MyArgas, null, null, null);

                            /*  string[,] data = itemobject.GetDataForItemID(
                                *        itemobject.ItemID);*/

                        int bound0 = data.GetUpperBound(0);

                        for (int i = 0; i <= bound0; i++)
                        {
                            string Key = data[0, i];
                            string Value = data[1, i];

                            dict.Add(Key, Value);
                            // Console.WriteLine("{0}, {1}", so1, so2);
                        }
 
                        api.AccountName = Acct.Accountname;
                        if (dict.Count > 0)
                        {
                            int stat = 0;

                            if (type == foldertype.Mail)
                            {
                                int msf = 0;
                                if (importopts.MessageSizeFilter != null)
                                {
                                    msf = Int32.Parse(importopts.MessageSizeFilter);
                                    msf *= 1000000;
                                    try
                                    {
                                        FileInfo f = new FileInfo(dict["filePath"]);
                                        if (f.Length > msf)
                                        {
                                            bSkipMessage = true;
                                            File.Delete(dict["filePath"]);
                                            // FBS -- When logging implemented, we should log this
                                            // Should we put a message in the UI as well?
                                        }
                                    }
                                    catch (Exception)
                                    {
                                    }
                                }

                                if (!bSkipMessage)
                                {
                                    dict.Add("folderId", folderobject.FolderPath);
                                    dict.Add("tags", "");
                                    stat = api.AddMessage(dict);
                                }
                            }
                            else if (type == foldertype.Contacts)
                            {
                                stat = api.CreateContact(dict, path);
                            }
                            else if (type == foldertype.Calendar)
                            {
                                stat = api.AddAppointment(dict, path);
                            }
                        }

                        // Note the : statement.  It seems weird to set Acct.migrationFolder.CurrentCountOFItems
                        // to itself, but this is done so the method will be called to increment the progress bar
                        Acct.migrationFolder.CurrentCountOFItems = (!bSkipMessage)
                                                                    ? Acct.migrationFolder.CurrentCountOFItems + 1
                                                                    : Acct.migrationFolder.CurrentCountOFItems;
                    }
                    iProcessedItems++;
                }
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
        userobject.InvokeMember("UMUnInitializeUser", BindingFlags.InvokeMethod |
            BindingFlags.Instance | BindingFlags.Public, null, userinstance, MyArgs, mods,
            null, null);
    }

    public void StartMigration(MigrationAccount Acct, MigrationOptions importopts, bool
        isServer = true, bool isPreview = false)
    {

        dynamic userobject;
        dynamic[] folderobjectarray;
        dynamic itemobject;
        object userinstance;

        userobject = sourceProvider.GetType("Exchange.UserObjectClass");
        userinstance = Activator.CreateInstance(userobject);
        itemobject = sourceProvider.GetType("Exchange.ItemObjectClass");

        if (!isPreview)
        {
            ParameterModifier pm = new ParameterModifier(1);

            pm[0] = true;
            ParameterModifier[] mods = { pm };

            string value = "";
            string acctname = "";
            int idx = Acct.Accountname.IndexOf("@");
            if (idx != -1)
            {
                acctname = Acct.Accountname.Substring(0, idx);
            }
            else
            {
                Log.err("Illegal account name");
                Acct.LastProblemInfo = new ProblemInfo("Illegal account name", "Error", ProblemInfo.TYPE_ERR);
                Acct.TotalNoErrors++;
                return;
            }

            Log.open(Path.GetTempPath() + acctname + ".log");
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
                Log.err("Unable to initialize", acctname, value);
                Acct.LastProblemInfo = new ProblemInfo(value, "Error", ProblemInfo.TYPE_ERR);
                Acct.TotalNoErrors++;
                return;
            }
            else
            {
                Log.info(acctname, "initialized");
            }
            folderobjectarray = (object[])userobject.InvokeMember("GetFolderObjects",
                BindingFlags.InvokeMethod | BindingFlags.Instance | BindingFlags.Public,
                null, userinstance, null, null, null, null);
            Acct.migrationFolder.CurrentCountOFItems = folderobjectarray.Count();

            Acct.TotalNoItems = ComputeTotalMigrationCount(importopts, folderobjectarray);

            Log.debug("Acct.TotalNoItems:", Acct.TotalNoItems.ToString());

            ZimbraAPI api = new ZimbraAPI();

            // see if we're migrating a .pst file. If we are, we need to know that so we can mess with folder names in ZimbraAPI
            string u = Acct.AccountID.ToUpper();
            /////

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
            // /
            foreach (dynamic folderobject in folderobjectarray)
            {
                string path = "";

                // FBS NOTE THAT THESE ARE EXCHANGE SPECIFIC.  WE'LL HAVE TO CHANGE THIS FOR GROUPWISE !!!
                if ((folderobject.Id == (int)ZimbraFolders.Sent) && !(importopts.ItemsAndFolders.HasFlag(
                    ItemsAndFoldersOptions.Sent)))
                {
                    Log.debug("Skipping folder", folderobject.Name);
                    continue;
                }
                if ((folderobject.Id == (int)ZimbraFolders.Trash) &&
                    !(importopts.ItemsAndFolders.HasFlag(
                    ItemsAndFoldersOptions.DeletedItems)))
                {
                    Log.debug("Skipping folder", folderobject.Name);
                    continue;
                }
                if ((folderobject.Id == (int)ZimbraFolders.Junk) &&
                    !(importopts.ItemsAndFolders.HasFlag(ItemsAndFoldersOptions.Junk)))
                {
                    Log.debug("Skipping folder", folderobject.Name);
                    continue;
                }
                if ((folderobject.ContainerClass == "IPF.Contact") &&
                    !(importopts.ItemsAndFolders.HasFlag(ItemsAndFoldersOptions.Contacts)))
                {
                    Log.debug("Skipping folder", folderobject.Name);
                    continue;
                }
                if ((folderobject.ContainerClass == "IPF.Appointment") &&
                    !(importopts.ItemsAndFolders.HasFlag(ItemsAndFoldersOptions.Calendar)))
                {
                    continue;
                }
                if ((folderobject.ContainerClass == "IPF.Note") &&
                    !(importopts.ItemsAndFolders.HasFlag(ItemsAndFoldersOptions.Mail)))
                {
                    Log.debug("Skipping folder", folderobject.Name);
                    continue;
                }
                 // //
                if (folderobject.ItemCount == 0)
                {
                    Log.debug("Skipping empty folder", folderobject.Name);
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
                    Log.debug("Skipping folder", folderobject.Name, "via filter option");
                    continue;
                }
                 // //
                 // ANOTHER TEMP -- REMOVE WHEN WE DO TASKS
                if (folderobject.ContainerClass == "IPF.Task")
                    continue;
                if (folderobject.Id == 0)
                {
                    api.AccountName = Acct.Accountname;

                    string ViewType = GetFolderViewType(folderobject.ContainerClass);
                    int stat = api.CreateFolder(folderobject.FolderPath, ViewType);

                    path = folderobject.FolderPath;
                }
                // Set FolderName at the end, since we trigger results on that, so we need all the values set
                Acct.migrationFolder.TotalCountOFItems = folderobject.ItemCount; // itemobjectarray.Count();
                Acct.migrationFolder.CurrentCountOFItems = 0;
                Acct.migrationFolder.FolderView = folderobject.ContainerClass;
                Acct.migrationFolder.FolderName = folderobject.Name;
                if (folderobject.Id == (int)ZimbraFolders.Trash)
                {
                    path = "/MAPIRoot/Deleted Items";   // FBS EXCHANGE SPECIFIC HACK !!!
                }
                ProcessItems(Acct, isServer, userobject, userinstance, folderobject, api, path, importopts);
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
            Acct.migrationFolder.LastFolderInfo = new FolderInfo("Contacts", "Contact",
                string.Format("{0} of {1}", totalCount.ToString(), totalCount.ToString()));
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
                        Acct.LastProblemInfo = new ProblemInfo("TestMessage",
                            "Invalid Attachment", ProblemInfo.TYPE_ERR);
                        Acct.TotalNoErrors++;
                    }
                    if (count == 500)
                    {
                        Acct.LastProblemInfo = new ProblemInfo("AnotherTest",
                            "Address has an unsupported format", ProblemInfo.TYPE_ERR);
                        Acct.TotalNoErrors++;
                    }
                }
                if (Acct.Accountnum == 1)
                {
                    if (count == 300)
                    {
                        Acct.LastProblemInfo = new ProblemInfo("Status Report",
                            "Illegal recipient", ProblemInfo.TYPE_ERR);
                        Acct.TotalNoErrors++;
                    }
                    if (count == 400)
                    {
                        Acct.LastProblemInfo = new ProblemInfo("Company picnic",
                            "Unsupported encoding", ProblemInfo.TYPE_WARN);
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
            Acct.migrationFolder.LastFolderInfo = new FolderInfo("Inbox", "Message",
                string.Format("{0} of {1}", totalCount.ToString(), totalCount.ToString()));
            switch (Acct.Accountnum)
            {
            case 0:
                totalCount = 11;
                break;
            case 1:
                totalCount = 12;
                break;
            case 2:
                totalCount = 13;
                break;
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
                        Acct.LastProblemInfo = new ProblemInfo("BugzillaRule",
                            "Unsupported condition", ProblemInfo.TYPE_ERR);
                        Acct.TotalNoErrors++;
                    }
                }
                count = count + 10;
            }
            Acct.migrationFolder.LastFolderInfo = new FolderInfo("Inbox", "Rule", string.Format(
                "{0} of {1}", totalCount.ToString(), totalCount.ToString()));
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
            BindingFlags.InvokeMethod | BindingFlags.Instance | BindingFlags.Public, null,
            calcInstance, MyArgs, mods, null, null);

        Console.WriteLine(MyArgs[0]);
        Console.ReadLine();

        userobject = sourceProvider.GetType("Exchange.UserObjectClass");
        userinstance = Activator.CreateInstance(userobject);

        api = new ZimbraAPI();
    }
}
}
