using System;
using System.Collections.Generic;

namespace CssLib
{
public class MigrationAccount
{
    public MigrationAccount()
    {
        AccountNum = -1;
        migrationFolder = new MigrationFolder();
        tagDict = new Dictionary<string, string>();
    }

    public string AccountID;
    public MigrationFolder migrationFolder;
    public Dictionary<string, string> tagDict;
    public event MigrationObjectEventHandler OnChanged;

    public string AccountName {
        get { return this.accountName; }
        set {
            if (OnChanged != null)
                OnChanged(this, new MigrationObjectEventArgs("AccountName", accountName,
                    value));
            accountName = value;
        }
    }
    public int AccountNum {
        get { return this.accountNum; }
        set {
            if (OnChanged != null)
                OnChanged(this, new MigrationObjectEventArgs("AccountNum", accountNum,
                    value));
            accountNum = value;
        }
    }
    public System.DateTime DateRaised {
        get { return dateRaised; }
        set {
            if (OnChanged != null)
                OnChanged(this, new MigrationObjectEventArgs("DateRaised", dateRaised,
                    value));
            dateRaised = value;
        }
    }
    public int TotalAppointments {
        get { return totalAppointments; }
        set {
            if (OnChanged != null) {
                OnChanged(this, new MigrationObjectEventArgs("TotalAppointments",
                    totalAppointments, value));
            }
            totalAppointments = value;
        }
    }
    public int TotalContacts {
        get { return totalContacts; }
        set
        {
            if (OnChanged != null)
            {
                OnChanged(this, new MigrationObjectEventArgs("TotalContacts", totalContacts,
                    value));
            }
            totalContacts = value;
        }
    }
    public int TotalItems {
        get { return totalItems; }
        set {
            if (OnChanged != null)
                OnChanged(this, new MigrationObjectEventArgs("TotalItems", totalItems,
                    value));
            totalItems = value;
        }
    }
    public int TotalMails {
        get { return totalMails; }
        set {
            if (OnChanged != null)
                OnChanged(this, new MigrationObjectEventArgs("TotalMails", totalMails,
                    value));
            totalMails = value;
        }
    }
    public int TotalRules {
        get { return totalRules; }
        set
        {
            if (OnChanged != null)
                OnChanged(this, new MigrationObjectEventArgs("TotalRules", totalRules,
                    value));
            totalRules = value;
        }
    }
    public int TotalSent {
        get { return totalSent; }
        set {
            if (OnChanged != null)
                OnChanged(this, new MigrationObjectEventArgs("TotalSent", totalSent,
                    value));
            totalSent = value;
        }
    }
    public int TotalTasks {
        get { return totalTasks; }
        set {
            if (OnChanged != null)
                OnChanged(this, new MigrationObjectEventArgs("TotalTasks", totalTasks,
                    value));
            totalTasks = value;
        }
    }
    public int TotalErrors {
        get { return totalErrors; }
        set
        {
            if (OnChanged != null)
                OnChanged(this, new MigrationObjectEventArgs("TotalErrors", totalErrors,
                    value));
            totalErrors = value;
        }
    }
    public int TotalWarnings {
        get { return totalWarnings; }
        set
        {
            if (OnChanged != null)
            {
                OnChanged(this, new MigrationObjectEventArgs("TotalWarnings",
                    totalWarnings, value));
            }
            totalWarnings = value;
        }
    }
    public ProblemInfo LastProblemInfo {
        get { return lastProblemInfo; }
        set
        {
            // won't want this if the LastProblemInfo is updated every time the error warning count increases
            // if (OnChanged != null)
            // {
            // OnChanged(this, new MigrationObjectEventArgs("LastProblemInfo", this.lastProblemInfo, value));
            // }
            lastProblemInfo = value;
        }
    }

    private string accountName;
    private int accountNum;
    private System.DateTime dateRaised;
    private ProblemInfo lastProblemInfo;
    private int totalAppointments;
    private int totalContacts;
    private int totalErrors;
    private int totalItems;
    private int totalMails;
    private int totalRules;
    private int totalSent;
    private int totalTasks;
    private int totalWarnings;
}

public class MigrationFolder
{
    public event MigrationObjectEventHandler OnChanged;

    public MigrationFolder() {}
    private int accountNum;
    public int AccountNum {
        get { return accountNum; }
        set {
            if (OnChanged != null)
                OnChanged(this, new MigrationObjectEventArgs("AccountNum", accountNum,
                    value));
            accountNum = value;
        }
    }
    private string folderName;
    public string FolderName {
        get { return folderName; }
        set
        {
            if (OnChanged != null)
            {
                OnChanged(this, new MigrationObjectEventArgs("FolderName", folderName,
                    value));
            }
            folderName = value;
        }
    }
    private Int64 currentCountOfItems;
    public Int64 CurrentCountOfItems {
        get { return currentCountOfItems; }
        set {
            if (OnChanged != null) {
                OnChanged(this, new MigrationObjectEventArgs("CurrentCountOfItems",
                    currentCountOfItems, value));
            }
            currentCountOfItems = value;
        }
    }
    private Int64 totalCountOfItems;
    public Int64 TotalCountOfItems {
        get { return totalCountOfItems; }
        set
        {
            if (OnChanged != null)
            {
                OnChanged(this, new MigrationObjectEventArgs("TotalCountOfItems",
                    totalCountOfItems, value));
            }
            totalCountOfItems = value;
        }
    }
    private FolderInfo lastFolderInfo;
    public FolderInfo LastFolderInfo {
        get { return lastFolderInfo; }
        set                                     // handled when folder name changes
        {
            lastFolderInfo = value;
        }
    }
    // this prop doesn't need OnChanged
    private string folderView;
    public string FolderView {
        get { return folderView; }
        set { folderView = value; }
    }
}

public class FolderInfo
{
    public FolderInfo(string folderName, string folderType, string folderProgress)
    {
        this.folderName = folderName;
        this.folderType = folderType;
        this.folderProgress = folderProgress;
    }
    public string FolderName {
        get { return folderName; }
        set { folderName = value; }
    }
    public string FolderType {
        get { return folderType; }
        set { folderType = value; }
    }
    public string FolderProgress {
        get { return folderProgress; }
        set { folderProgress = value; }
    }

    private string folderName;
    private string folderProgress;
    private string folderType;
}

public class ProblemInfo
{
    public const int TYPE_ERR = 1;
    public const int TYPE_WARN = 2;

    public ProblemInfo(string objectName, string theMsg, int msgType)
    {
        this.objectName = objectName;
        this.msg = theMsg;
        this.msgType = msgType;
        formattedMsg = (msgType == ProblemInfo.TYPE_ERR) ? "Error: " : "Warning: ";
        formattedMsg += objectName + " -- ";
        formattedMsg += msg;
    }
    public string ObjectName {
        get { return objectName; }
        set { objectName = value; }
    }
    public string Msg {
        get { return msg; }
        set { msg = value; }
    }
    public int MsgType {
        get { return msgType; }
        set { msgType = value; }
    }
    public string FormattedMsg {
        get { return formattedMsg; }
        set { formattedMsg = value; }
    }

    private string formattedMsg;
    private string msg;
    private int msgType;
    private string objectName;
}

public class MigrationObjectEventArgs: EventArgs
{
    public MigrationObjectEventArgs(string propertyName, object oldValue, object newValue)
    {
        this.propertyName = propertyName;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
    public string PropertyName {
        get { return this.propertyName; }
    }
    public object OldValue {
        get { return this.oldValue; }
    }
    public object NewValue {
        get { return this.newValue; }
    }

    private object newValue;
    private object oldValue;
    private string propertyName;
}

public delegate void MigrationObjectEventHandler(object sender, MigrationObjectEventArgs e);
}
