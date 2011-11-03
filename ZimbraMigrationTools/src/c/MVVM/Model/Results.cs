namespace MVVM.Model
{
using System;

public class AccountResults
{
    internal AccountResults()
    {
        this.PBValue = 0;
        this.PBMsgValue = "";
        this.AccountName = "";
        this.AccountProgress = 0;
        this.AcctProgressMsg = "";
        this.NumErrs = 0;
        this.NumWarns = 0;
        this.EnableStop = false;
        this.UserPBMsgValue = "";
        this.CurrentItemNum = 0;
        this.TotalItemsToMigrate = 0;
    }
    public int PBValue {
        get;
        set;
    }
    public string PBMsgValue {
        get;
        set;
    }
    public string UserPBMsgValue {
        get;
        set;
    }
    public string AccountName {
        get;
        set;
    }
    public int CurrentItemNum {
        get;
        set;
    }
    public int TotalItemsToMigrate {
        get;
        set;
    }
    public int AccountProgress {
        get;
        set;
    }
    public string AcctProgressMsg {
        get;
        set;
    }
    public int NumErrs {
        get;
        set;
    }
    public int NumWarns {
        get;
        set;
    }
    public int CurrentAccountSelection {
        get;
        set;
    }
    public bool OpenLogFileEnabled {
        get;
        set;
    }
    public bool EnableStop {
        get;
        set;
    }
    public string SelectedTab {
        get;
        set;
    }
}

public class UserResults
{
    internal UserResults(string folderName, string typeName, string progressMsg)
    {
        this.FolderName = folderName;
        this.TypeName = typeName;
        this.UserProgressMsg = progressMsg;
    }
    public string FolderName {
        get;
        set;
    }
    public string TypeName {
        get;
        set;
    }
    public string UserProgressMsg {
        get;
        set;
    }
}
}
