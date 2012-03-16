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
    OOO = 0x100, Junk = 0x0080, DeletedItems = 0x0040, Sent = 0x0020, Rules = 0x0010,
    Tasks = 0x0008, Calendar = 0x0004, Contacts = 0x0002, Mail = 0x0001, None = 0x0000
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


public class CompatibilityChk
{


    public enum MachineType : ushort
    {
        IMAGE_FILE_MACHINE_UNKNOWN = 0x0,
        IMAGE_FILE_MACHINE_AM33 = 0x1d3,
        IMAGE_FILE_MACHINE_AMD64 = 0x8664,
        IMAGE_FILE_MACHINE_ARM = 0x1c0,
        IMAGE_FILE_MACHINE_EBC = 0xebc,
        IMAGE_FILE_MACHINE_I386 = 0x14c,
        IMAGE_FILE_MACHINE_IA64 = 0x200,
        IMAGE_FILE_MACHINE_M32R = 0x9041,
        IMAGE_FILE_MACHINE_MIPS16 = 0x266,
        IMAGE_FILE_MACHINE_MIPSFPU = 0x366,
        IMAGE_FILE_MACHINE_MIPSFPU16 = 0x466,
        IMAGE_FILE_MACHINE_POWERPC = 0x1f0,
        IMAGE_FILE_MACHINE_POWERPCFP = 0x1f1,
        IMAGE_FILE_MACHINE_R4000 = 0x166,
        IMAGE_FILE_MACHINE_SH3 = 0x1a2,
        IMAGE_FILE_MACHINE_SH3DSP = 0x1a3,
        IMAGE_FILE_MACHINE_SH4 = 0x1a6,
        IMAGE_FILE_MACHINE_SH5 = 0x1a8,
        IMAGE_FILE_MACHINE_THUMB = 0x1c2,
        IMAGE_FILE_MACHINE_WCEMIPSV2 = 0x169,
    }
    public static MachineType GetDllMachineType(string dllPath)
    {
        // http://www.microsoft.com/whdc/system/platform/firmware/PECOFF.mspx 

        FileStream fs = new FileStream(dllPath, FileMode.Open, FileAccess.Read);
        BinaryReader br = new BinaryReader(fs);
        fs.Seek(0x3c, SeekOrigin.Begin);
        Int32 peOffset = br.ReadInt32();
        fs.Seek(peOffset, SeekOrigin.Begin);
        UInt32 peHead = br.ReadUInt32();
        if (peHead != 0x00004550) // "PE\0\0", little-endian 
            throw new Exception("Can't find PE header");
        MachineType machineType = (MachineType)br.ReadUInt16();
        br.Close();
        fs.Close();
        return machineType;
    }

    // returns true if the dll is 64-bit, false if 32-bit, and null if unknown 
    public static bool? UnmanagedDllIs64Bit(string dllPath)
    {
        switch (GetDllMachineType(dllPath))
        {
        case MachineType.IMAGE_FILE_MACHINE_AMD64:
        case MachineType.IMAGE_FILE_MACHINE_IA64:
            return true;
        case MachineType.IMAGE_FILE_MACHINE_I386:
            return false;
        default:
            return null;
        }
    }


    public static string CheckCompat(string path)
    {
        string status = "";
        string absolutepath = Path.GetFullPath("Exchange.dll");

        bool retval = UnmanagedDllIs64Bit(absolutepath).Value;


        string Bitness = (string)Microsoft.Win32.Registry.GetValue(@"HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Office\14.0\Outlook", "Bitness", null);
        if (Bitness != null)
        {
            if ((Bitness == "x64") && (!retval))
            {

                status = "Outlook is 64 bit and migration is 32 bit";

            }

        }
        return status;

    }


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
        try
        {
            return MailWrapper.GlobalUninit();
        }
        catch (Exception e)
        {
            string msg = string.Format("GetListofMapiProfiles Exception: {0}", e.Message);
            return msg;
        }
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
        // Note that Rules and OOO do not apply here
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
        {
            return true;
        }
        for (int i = 0; i < skipList.Count; i++)
        {
            if (string.Compare(folder.Name, skipList[i], StringComparison.OrdinalIgnoreCase) == 0)
            {
                return true;
            }
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

    // if the tag has already been created, just return it; if not, do the req and create it
    private string DealWithTags(string theTags, MigrationAccount acct, ZimbraAPI api)
    {
        string retval = "";
        string[] tokens = theTags.Split(',');
        for (int i = 0; i < tokens.Length; i ++)
        {
            if (i > 0)
            {
                retval += ",";
            }
            string token = tokens.GetValue(i).ToString();
            if (acct.tagDict.ContainsKey(token))
            {
                retval += acct.tagDict[token];
            }
            else
            {
                string tagID = "";  // output from CreateTag
                string color = (acct.tagDict.Count).ToString(); // color starts at 0, will keep bumping up by 1
                int stat = api.CreateTag(token, color, out tagID);  // do request and get the numstr back from response
                acct.tagDict.Add(token, tagID);   // add to existing dict; token is Key, tokenNumstr is Val
                retval += tagID;
            }
        }
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

                        api.AccountID = Acct.AccountID;
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
                                    if (options.MessageSizeFilter.Length > 0)
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
                                    if (dict["tags"].Length > 0)
                                    {
                                        // change the tag names into tag numbers for AddMessage
                                        string tagsNumstrs = DealWithTags(dict["tags"], Acct, api);
                                        bool bRet = dict.Remove("tags");
                                        dict.Add("tags", tagsNumstrs);
                                    }
                                    dict.Add("folderId", folder.FolderPath);
                                    stat = api.AddMessage(dict);
                                }
                            }
                            else if (type == foldertype.Contacts)
                            {
                                if (dict["tags"].Length > 0)
                                {
                                    // change the tag names into tag numbers for AddMessage
                                    string tagsNumstrs = DealWithTags(dict["tags"], Acct, api);
                                    bool bRet = dict.Remove("tags");
                                    dict.Add("tags", tagsNumstrs);
                                }
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
            Log.info("Processing folder", folder.Name);
            if (folder.Id == 0)
            {
                api.AccountID = Acct.AccountID;
                api.AccountName = Acct.AccountName;

                string ViewType = GetFolderViewType(folder.ContainerClass);
                int stat = api.CreateFolder(folder.FolderPath, ViewType);

                path = folder.FolderPath;
            }
            if (folder.ItemCount == 0)
            {
                Log.info("Skipping empty folder", folder.Name);
                continue;
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

        // now do Rules
        if (options.ItemsAndFolders.HasFlag(ItemsAndFoldersOptions.Rules))
        {
            string[,] data = user.GetRules();
            if (data != null)
            {
                Acct.TotalItems++;
                Acct.migrationFolder.TotalCountOfItems = 1;
                Acct.migrationFolder.CurrentCountOfItems = 0;
                Acct.migrationFolder.FolderView = "All Rules";
                Acct.migrationFolder.FolderName = "Rules Table";
                api.AccountID = Acct.AccountID;
                api.AccountName = Acct.AccountName;
                if (!isPreview)
                {
                    Dictionary<string, string> dict = new Dictionary<string, string>();
                    int bound0 = data.GetUpperBound(0);
                    for (int i = 0; i <= bound0; i++)
                    {
                        string Key = data[0, i];
                        string Value = data[1, i];

                        dict.Add(Key, Value);
                    }
                    api.AccountID = Acct.AccountID;
                    api.AccountName = Acct.AccountName;
                    int stat = api.AddRules(dict);
                    Acct.migrationFolder.CurrentCountOfItems = 1;
                }
            }
            else
            {
                Log.info("There are no rules to migrate");
            }
        }

        // now do OOO
        if (options.ItemsAndFolders.HasFlag(ItemsAndFoldersOptions.OOO))
        {
            bool isOOO = false;
            string ooo = user.GetOOO();
            if (ooo.Length > 0)
            {
                isOOO = (ooo != "0:");
            }
            if (isOOO)
            {
                Acct.TotalItems++;
                Acct.migrationFolder.TotalCountOfItems = 1;
                Acct.migrationFolder.CurrentCountOfItems = 0;
                Acct.migrationFolder.FolderView = "OOO";
                Acct.migrationFolder.FolderName = "Out of Office";
                api.AccountID = Acct.AccountID;
                api.AccountName = Acct.AccountName;
                if (!isPreview)
                {
                    Log.info("Migrating Out of Office");
                    api.AddOOO(ooo, isServer);
                }
            }
            else
            {
                Log.info("Out of Office state is off, and there is no message");
            }
        }

        user.Uninit();
    }    
}
}
