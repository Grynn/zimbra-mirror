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
   
    string m_MailClient;

    
    enum foldertype
    {
        Mail = 1, Contacts = 2, Calendar = 3, Task = 4, MeetingReq = 5
    };
    public string MailClient {
        get { return m_MailClient; }
        set { m_MailClient = value; }
    }
    
    dynamic MailWrapper;

    dynamic useruserobject = new Exchange.UserObject(); // for PST migration -- can be here since there's only one
  
    public CSMigrationwrapper()
    {
        InitLogFile("migration", Log.Level.Info);
        Log.info("Initializing migration");

        
    }

    
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
           
            try
            {
                MailWrapper = new Exchange.MapiWrapper();
                s = MailWrapper.GlobalInit(Target, AdminUser, AdminPassword);
                
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
           
            s = MailWrapper.GlobalUninit();
        }
        return s;
    }

   
    public void Migrate(string MailOptions)
    {
        MailWrapper.ImportMailOptions(MailOptions);
    }

    public string[] GetListofMapiProfiles()
    {
        object var = new object();

        string msg="";
        string[] s ={""};
        try
        {

            msg = MailWrapper.GetProfilelist(out var);

            s = (string[])var;

            
        }

        catch (Exception e)
        {
            msg = string.Format("GetListofMapiProfiles Exception. \n{0}", e.Message);

            s[0]= msg;
        }

        return s;
    }

    public string[] GetListFromObjectPicker()
    {
        // Change this to above signature when I start getting the real ObjectPicker object back
        object var = new object();
        string status = "";
        string[] s = {""};
        try
        {

            status = MailWrapper.SelectExchangeUsers(out var);
            s = (string[])var;
        }

        catch (Exception e)
        {
            status = string.Format("GetListFromObjectPicker Exception. \n{0}", e.Message);
            s[0]= status;

        }
        return s;
    }

    private void InitLogFile(string fileprefix, Log.Level level)
    {
        string bakfile = Path.GetTempPath() + fileprefix + ".bak";
        string logfile = Path.GetTempPath() + fileprefix + ".log";
        if (File.Exists(bakfile))
        {
            File.Delete(bakfile);
        }
        if (File.Exists(logfile))
        {
            File.Move(logfile, bakfile);
        }
        Log.init(logfile, level);
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
            case foldertype.MeetingReq:
                retval = importopts.ItemsAndFolders.HasFlag(ItemsAndFoldersOptions.Mail);
                break;
            case foldertype.Calendar:
                retval = importopts.ItemsAndFolders.HasFlag(ItemsAndFoldersOptions.Calendar);
                break;
            case foldertype.Contacts:
                retval = importopts.ItemsAndFolders.HasFlag(ItemsAndFoldersOptions.Contacts);
                break;
            case foldertype.Task:
                retval = importopts.ItemsAndFolders.HasFlag(ItemsAndFoldersOptions.Tasks);
                break;
            default:
                break;
        }
        return retval;
    }

    private void ProcessItems(MigrationAccount Acct, bool isServer, dynamic userobject, dynamic folderobject,
        ZimbraAPI api, string path, MigrationOptions importopts)
    {
        DateTime dt;

        dt = DateTime.UtcNow;

        dynamic[] itemobjectarray;

        itemobjectarray = userobject.GetItemsForFolderObjects(
              folderobject, dt.ToOADate());

        int iProcessedItems = 0;

        if (itemobjectarray.GetLength(0) > 0)
        {
            while (iProcessedItems < Acct.migrationFolder.TotalCountOfItems)
            {
                Log.debug("Processing folder", folderobject.Name, "-- Total items:", folderobject.ItemCount);
                foreach (dynamic itemobject in itemobjectarray)
                {
                    foldertype type = (foldertype)itemobject.Type;
                    if (ProcessIt(importopts, type))
                    {
                        bool bSkipMessage = false;
                        Dictionary<string, string> dict = new Dictionary<string, string>();

                       
                       string[,] data = itemobject.GetDataForItemID(userobject,
                                       itemobject.ItemID, itemobject.Type);

                        int bound0 = data.GetUpperBound(0);

                        for (int i = 0; i <= bound0; i++)
                        {
                            string Key = data[0, i];
                            string Value = data[1, i];

                            dict.Add(Key, Value);
                            // Console.WriteLine("{0}, {1}", so1, so2);
                        }
 
                        api.AccountName = Acct.AccountName;
                        if (dict.Count > 0)
                        {
                            int stat = 0;

                            if ((type == foldertype.Mail) || (type == foldertype.MeetingReq))
                            {
                                //Log.debug("Msg Subject: ", dict["Subject"]);
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
                                        Log.info("File exception on ", dict["filePath"]);
                                    }
                                }
                                if (importopts.DateFilter != null)
                                {
                                    try
                                    {
                                        DateTime dtm = DateTime.Parse(dict["Date"]);
                                        DateTime filterDtm = Convert.ToDateTime(importopts.DateFilter);
                                        if (DateTime.Compare(dtm, filterDtm) < 0)
                                        {
                                            bSkipMessage = true;
                                        }
                                    }
                                    catch (Exception)
                                    {
                                        Log.info(dict["Subject"], ": unable to parse date");
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
                                if (importopts.DateFilter != null)
                                {
                                    try
                                    {
                                        DateTime dtm = DateTime.Parse(dict["sCommon"]);
                                        DateTime filterDtm = Convert.ToDateTime(importopts.DateFilter);
                                        if (DateTime.Compare(dtm, filterDtm) < 0)
                                        {
                                            bSkipMessage = true;
                                        }
                                    }
                                    catch (Exception)
                                    {
                                        Log.info(dict["su"], ": unable to parse date");
                                    }
                                }
                                if (!bSkipMessage)
                                {
                                    stat = api.AddAppointment(dict, path);
                                }
                            }
                            else if (type == foldertype.Task)
                            {
                                if (importopts.DateFilter != null)
                                {
                                    try
                                    {
                                        DateTime dtm = DateTime.Parse(dict["sCommon"]);
                                        DateTime filterDtm = Convert.ToDateTime(importopts.DateFilter);
                                        if (DateTime.Compare(dtm, filterDtm) < 0)
                                        {
                                            bSkipMessage = true;
                                        }
                                    }
                                    catch (Exception)
                                    {
                                        Log.info(dict["su"], ": unable to parse date");
                                    }
                                }
                                if (!bSkipMessage)
                                {
                                    stat = api.AddTask(dict, path);
                                }
                            }
                        }

                        // Note the : statement.  It seems weird to set Acct.migrationFolder.CurrentCountOFItems
                        // to itself, but this is done so the method will be called to increment the progress bar
                        Acct.migrationFolder.CurrentCountOfItems = (!bSkipMessage)
                                                                    ? Acct.migrationFolder.CurrentCountOfItems + 1
                                                                    : Acct.migrationFolder.CurrentCountOfItems; 
                    }
                    iProcessedItems++;
                }
            }
        }
    }

    public void EndUserMigration()
    {
       useruserobject.UMUnInitializeUser();
    }

    public void StartMigration(MigrationAccount Acct, MigrationOptions importopts, bool
        isServer = true, bool isVerbose = false, bool isPreview = false)
    {       
        dynamic[] folderobjectarray;
        dynamic userobject;
        userobject = new Exchange.UserObject();       
        string value = "";
        string accountName = "";

        int idx = Acct.AccountName.IndexOf("@");
        if (idx != -1)
        {
            accountName = Acct.AccountName.Substring(0, idx);
        }
        else
        {
            Acct.LastProblemInfo = new ProblemInfo(Acct.AccountName, "Illegal account name", ProblemInfo.TYPE_ERR);
            Acct.TotalErrors++;
            return;
        }

        Log.Level level = isVerbose ? Log.Level.Debug : Log.Level.Info;
        Log.init(Path.GetTempPath() + "migration.log", level);  // might have gotten a new level from options
        InitLogFile(accountName, level);
        try
        {
            if (isServer)
            {
                value = userobject.InitializeUser("", "", Acct.AccountID, accountName);
            }
            else
            {
                value = useruserobject.UMInitializeUser(Acct.AccountID, accountName);
            }
        }
        catch (Exception e)
        {
            string s = string.Format("Initialization Exception.  {0}", e.Message);
            Acct.LastProblemInfo = new ProblemInfo(accountName, s, ProblemInfo.TYPE_ERR);
            Acct.TotalErrors++;
            return;
        }

        if (value.Length > 0)
        {
            Log.err("Unable to initialize", accountName, value);
            Acct.LastProblemInfo = new ProblemInfo(accountName, value, ProblemInfo.TYPE_ERR);
            Acct.TotalErrors++;
            return;
        }
        else
        {
            Log.info(accountName, "initialized");
        }

        folderobjectarray = (isServer) ? userobject.GetFolderObjects() : useruserobject.GetFolderObjects();
        Acct.migrationFolder.CurrentCountOfItems = folderobjectarray.Count();

        Acct.TotalItems = ComputeTotalMigrationCount(importopts, folderobjectarray);

        Log.debug("Acct.TotalItems:", Acct.TotalItems.ToString());

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
            if ((folderobject.ContainerClass == "IPF.Task") &&
                !(importopts.ItemsAndFolders.HasFlag(ItemsAndFoldersOptions.Tasks)))
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
            if (folderobject.Id == 0)
            {
                api.AccountName = Acct.AccountName;

                string ViewType = GetFolderViewType(folderobject.ContainerClass);
                int stat = api.CreateFolder(folderobject.FolderPath, ViewType);

                path = folderobject.FolderPath;
            }
            // Set FolderName at the end, since we trigger results on that, so we need all the values set
            Acct.migrationFolder.TotalCountOfItems = folderobject.ItemCount; // itemobjectarray.Count();
            Acct.migrationFolder.CurrentCountOfItems = 0;
            Acct.migrationFolder.FolderView = folderobject.ContainerClass;
            Acct.migrationFolder.FolderName = folderobject.Name;
            if (folderobject.Id == (int)ZimbraFolders.Trash)
            {
                path = "/MAPIRoot/Deleted Items";   // FBS EXCHANGE SPECIFIC HACK !!!
            }
            if (!isPreview)
            {
                if (isServer)
                {
                    ProcessItems(Acct, isServer, userobject, folderobject, api, path, importopts);
                }
                else
                {
                    ProcessItems(Acct, isServer, useruserobject, folderobject, api, path, importopts);
                }
            }
        }
        if (isServer)
        {
            userobject.SMUnInitializeUser();
        }
    }    
}
}
