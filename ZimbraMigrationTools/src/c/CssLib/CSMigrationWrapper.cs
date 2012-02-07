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
        StringBuilder s = new StringBuilder();
        String last = null;

        foreach (object obj in objs) {
            if (s.Length > 0 && !last.EndsWith("="))
                s.Append(' ');
            last = obj.ToString();
            s.Append(last);
        }
        log_print(level, s.ToString());
    }

    public static void debug(string str) { log_print(Level.Debug, str); }
    public static void debug(params object[] objs) { log(Level.Debug, objs); }
    public static void err(string str) { log_print(Level.Err, str); }
    public static void err(params object[] objs) { log(Level.Err, objs); }
    public static void info(string str) { log_print(Level.Info, str); }
    public static void info(params object[] objs) { log(Level.Info, objs); }
    public static void warn(string str) { log_print(Level.Warn, str); }
    public static void warn(params object[] objs) { log(Level.Warn, objs); }

    public static void open(string file) { log_open(file); }
    public static string file() { return log_file(); }
    public static void prefix(string prefix) { log_prefix(prefix); }

    #region PInvokes

    [DllImport("CppLib.dll", CallingConvention = CallingConvention.Cdecl, CharSet = CharSet.Unicode)]
    static private extern string log_file();

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

public class CSMigrationWrapper
{
    enum foldertype
    {
        Mail = 1, Contacts = 2, Calendar = 3, Task = 4, MeetingReq = 5
    };

    string m_MailClient;
    public string MailClient {
        get { return m_MailClient; }
        set { m_MailClient = value; }
    }
    
    dynamic MailWrapper;

    public CSMigrationWrapper(string mailClient)
    {
        InitLogFile("migration", Log.Level.Info);
        Log.info("Initializing migration");
        MailClient = mailClient;
        if (MailClient == "MAPI") {
            MailWrapper = new Exchange.MapiWrapper();
        }
    }

    public string GlobalInit(string Target, string AdminUser, string AdminPassword)
    {
        string s = "";

        try
        {
            s = MailWrapper.GlobalInit(Target, AdminUser, AdminPassword);
        }
        catch (Exception e)
        {
            s = string.Format("Initialization Exception. Make sure to enter the proper credentials: {0}", e.Message);
        }
        return s;
    }

    public string GlobalUninit()
    {
        return MailWrapper.GlobalUninit();
    }

    public string[] GetListofMapiProfiles()
    {
        object var = new object();
        string msg = "";
        string[] s = { "" };

        try
        {
            msg = MailWrapper.GetProfilelist(out var);
            s = (string[])var;
        }
        catch (Exception e)
        {
            msg = string.Format("GetListofMapiProfiles Exception: {0}", e.Message);

            s[0]= msg;
        }
        return s;
    }

    public string[] GetListFromObjectPicker()
    {
        // Change this to above signature when I start getting the real ObjectPicker object back
        string[] s = { "" };
        string status = "";
        object var = new object();

        try
        {
            status = MailWrapper.SelectExchangeUsers(out var);
            s = (string[])var;
        }
        catch (Exception e)
        {
            status = string.Format("GetListFromObjectPicker Exception: {0}", e.Message);
            s[0]= status;
        }
        return s;
    }

    private void InitLogFile(string prefix, Log.Level level)
    {
        string bakfile = Path.GetTempPath() + prefix + ".bak";
        string logfile = Path.GetTempPath() + prefix + ".log";

        try
        {
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
        catch (Exception e)
        {
            // need to do better than Console.WriteLine -- we'll only see this during debugging
            // but at least we won't crash
            string temp = string.Format("Initialization error on {0}: {1}", logfile, e.Message);
            Console.WriteLine(temp);
            return;
        }
    }

    private bool SkipFolder(MigrationOptions options, List<string> skipList, dynamic folder) {
        if ((folder.Id == (int)ZimbraFolders.Calendar &&
            !options.ItemsAndFolders.HasFlag(ItemsAndFoldersOptions.Calendar)) ||
            (folder.Id == (int)ZimbraFolders.Contacts &&
            !options.ItemsAndFolders.HasFlag(ItemsAndFoldersOptions.Contacts)) ||
            (folder.Id == (int)ZimbraFolders.Junk &&
            !options.ItemsAndFolders.HasFlag(ItemsAndFoldersOptions.Junk)) ||
            (folder.Id == (int)ZimbraFolders.Sent &&
            !options.ItemsAndFolders.HasFlag(ItemsAndFoldersOptions.Sent)) ||
            (folder.Id == (int)ZimbraFolders.Tasks &&
            !options.ItemsAndFolders.HasFlag(ItemsAndFoldersOptions.Tasks)) ||
            (folder.Id == (int)ZimbraFolders.Trash &&
            !options.ItemsAndFolders.HasFlag(ItemsAndFoldersOptions.DeletedItems)) ||
            // FBS NOTE THAT THESE ARE EXCHANGE SPECIFIC and need to be removed
            (folder.ContainerClass == "IPF.Contact" &&
            !options.ItemsAndFolders.HasFlag(ItemsAndFoldersOptions.Contacts)) ||
            (folder.ContainerClass == "IPF.Appointment" &&
            !options.ItemsAndFolders.HasFlag(ItemsAndFoldersOptions.Calendar)) ||
            (folder.ContainerClass == "IPF.Task" &&
            !options.ItemsAndFolders.HasFlag(ItemsAndFoldersOptions.Tasks)) ||
            (folder.ContainerClass == "IPF.Note" &&
            !options.ItemsAndFolders.HasFlag(ItemsAndFoldersOptions.Mail)))
            return true;
        for (int i = 0; i < skipList.Count; i++) {
            if (folder.Name == skipList[i])
                return true;
        }
        return false;
    }

    // TODO - this is Exchange specific - should use folder ids
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

    private bool ProcessIt(MigrationOptions options, foldertype type)
    {
        bool retval = false;

        switch (type)
        {
        case foldertype.Mail:
        case foldertype.MeetingReq:
            retval = options.ItemsAndFolders.HasFlag(ItemsAndFoldersOptions.Mail);
            break;
        case foldertype.Calendar:
            retval = options.ItemsAndFolders.HasFlag(ItemsAndFoldersOptions.Calendar);
            break;
        case foldertype.Contacts:
            retval = options.ItemsAndFolders.HasFlag(ItemsAndFoldersOptions.Contacts);
            break;
        case foldertype.Task:
            retval = options.ItemsAndFolders.HasFlag(ItemsAndFoldersOptions.Tasks);
            break;
        default:
            break;
        }
        return retval;
    }

    private void ProcessItems(MigrationAccount Acct, bool isServer, dynamic user, dynamic folder,
        ZimbraAPI api, string path, MigrationOptions options)
    {
        DateTime dt = DateTime.UtcNow;
        dynamic[] itemobjectarray = user.GetItemsForFolder(folder, dt.ToOADate());
        int iProcessedItems = 0;

        if (itemobjectarray.GetLength(0) > 0)
        {
            while (iProcessedItems < Acct.migrationFolder.TotalCountOfItems)
            {
                Log.debug("Processing folder", folder.Name, "-- Total items:", folder.ItemCount);
                foreach (dynamic itemobject in itemobjectarray)
                {
                    foldertype type = (foldertype)itemobject.Type;
                    if (ProcessIt(options, type))
                    {
                        bool bSkipMessage = false;
                        Dictionary<string, string> dict = new Dictionary<string, string>();
                       string[,] data = itemobject.GetDataForItemID(user,
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
                                if (options.MessageSizeFilter != null)
                                {
                                    msf = Int32.Parse(options.MessageSizeFilter);
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
                                if (options.DateFilter != null)
                                {
                                    try
                                    {
                                        DateTime dtm = DateTime.Parse(dict["Date"]);
                                        DateTime filterDtm = Convert.ToDateTime(options.DateFilter);
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
                                    dict.Add("folderId", folder.FolderPath);
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
                                if (options.DateFilter != null)
                                {
                                    try
                                    {
                                        DateTime dtm = DateTime.Parse(dict["sCommon"]);
                                        DateTime filterDtm = Convert.ToDateTime(options.DateFilter);
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
                                    stat = api.AddAppointment(dict, path);
                            }
                            else if (type == foldertype.Task)
                            {
                                if (options.DateFilter != null)
                                {
                                    try
                                    {
                                        DateTime dtm = DateTime.Parse(dict["sCommon"]);
                                        DateTime filterDtm = Convert.ToDateTime(options.DateFilter);

                                        if (DateTime.Compare(dtm, filterDtm) < 0)
                                            bSkipMessage = true;
                                    }
                                    catch (Exception)
                                    {
                                        Log.info(dict["su"], ": unable to parse date");
                                    }
                                }
                                if (!bSkipMessage)
                                    stat = api.AddTask(dict, path);
                            }
                        }

                        // Note the : statement.  It seems weird to set Acct.migrationFolder.CurrentCountOFItems
                        // to itself, but this is done so the method will be called to increment the progress bar
                        Acct.migrationFolder.CurrentCountOfItems = bSkipMessage ?
                            Acct.migrationFolder.CurrentCountOfItems : Acct.migrationFolder.CurrentCountOfItems + 1;
                    }
                    iProcessedItems++;
                }
            }
        }
    }

    public void StartMigration(MigrationAccount Acct, MigrationOptions options, bool
        isServer = true, bool isVerbose = false, bool isPreview = false)
    {
        string accountName = "";
        dynamic[] folders;
        int idx = Acct.AccountName.IndexOf("@");
        dynamic user = new Exchange.UserObject();       
        string value = "";

        if (idx == -1)
        {
            Acct.LastProblemInfo = new ProblemInfo(Acct.AccountName, "Illegal account name",
                ProblemInfo.TYPE_ERR);
            Acct.TotalErrors++;
            return;
        }
        else
        {
            accountName = Acct.AccountName.Substring(0, idx);
        }

        Log.Level level = isVerbose ? Log.Level.Debug : Log.Level.Info;
        Log.init(Path.GetTempPath() + "migration.log", level);  // might have gotten a new level from options
        InitLogFile(accountName, level);
        try
        {
            value = user.Init(isServer ? "host" : "", Acct.AccountID, accountName);
        }
        catch (Exception e)
        {
            string s = string.Format("Initialization Exception. {0}", e.Message);

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

        // set up check for skipping folders
        List<string> skipList = new List<string>();

        if (options.SkipFolders != null && options.SkipFolders.Length > 0)
        {
            string[] tokens = options.SkipFolders.Split(',');

            for (int i = 0; i < tokens.Length; i++)
            {
                string token = tokens.GetValue(i).ToString();

                skipList.Add(token.Trim());
            }
        }

        folders = user.GetFolders();
        Acct.migrationFolder.CurrentCountOfItems = folders.Count();

        foreach (dynamic folder in folders) {
            if (!SkipFolder(options, skipList, folder))
                Acct.TotalItems += folder.ItemCount;
        }
        Log.info("Acct.TotalItems=", Acct.TotalItems.ToString());

        ZimbraAPI api = new ZimbraAPI();

        foreach (dynamic folder in folders)
        {
            string path = "";

            if (SkipFolder(options, skipList, folder))
            {
                Log.info("Skipping folder", folder.Name);
                continue;
            }
            if (folder.ItemCount == 0)
            {
                Log.info("Skipping empty folder", folder.Name);
                continue;
            }

            Log.info("Processing folder", folder.Name);
            if (folder.Id == 0)
            {
                api.AccountName = Acct.AccountName;

                string ViewType = GetFolderViewType(folder.ContainerClass);
                int stat = api.CreateFolder(folder.FolderPath, ViewType);

                path = folder.FolderPath;
            }
            // Set FolderName at the end, since we trigger results on that, so we need all the values set
            Acct.migrationFolder.TotalCountOfItems = folder.ItemCount;
            Acct.migrationFolder.CurrentCountOfItems = 0;
            Acct.migrationFolder.FolderView = folder.ContainerClass;
            Acct.migrationFolder.FolderName = folder.Name;
            if (folder.Id == (int)ZimbraFolders.Trash)
            {
                path = "/MAPIRoot/Deleted Items";   // FBS EXCHANGE SPECIFIC HACK !!!
            }
            if (!isPreview)
            {
                ProcessItems(Acct, isServer, user, folder, api, path, options);
            }
        }
        user.Uninit();
    }    
}
}
