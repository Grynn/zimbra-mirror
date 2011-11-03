namespace MVVM.Model
{
using System;

public class Intro
{
    internal Intro()
    {
        Populate();
    }
    public string BuildNum {
        get;
        set;
    }
    public string WelcomeMsg {
        get;
        set;
    }
    public bool IsServerMigration {
        get;
        set;
    }
    public bool IsUserMigration {
        get;
        set;
    }
    public string InstallDir {
        get;
        set;
    }
    public Intro Populate()
    {
        this.BuildNum = new BuildNum().BUILD_NUM;
        this.InstallDir = Environment.CurrentDirectory;
        this.WelcomeMsg =
            "This application will guide you through the process of migrating from Microsoft products to Zimbra.\n\nServer mode is for migrating users from an Exchange server.  User mode is for migrating one user.  Specify source and destination credentials, and then choose the folders to migrate.\n\nUsers are selected via population tools, or via comma separated Excel spreadsheet files.  You have the option of migrating immediately, previewing the migration, or scheduling it for a later time.  Any errors and warnings will be listed in the result set, and log files will be created for each migrated user.";
        return this;
    }
}
}
