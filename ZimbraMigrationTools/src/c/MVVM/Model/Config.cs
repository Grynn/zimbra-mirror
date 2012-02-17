namespace MVVM.Model
{
using System;

public class Config
{
    public Config() {}
    public Config(string mailserver, string srcAdminId, string srcAdminPwd, string
        outlookProfile, string pstFile, string zimbraserverhostname, string zimbraport,
        string zimbraAdmin, string zimbrapasswd, string zimbradomain, string pstfile)
    {
        this.zimbraServer = new ZimbraServer();
        this.mailServer = new MailServer();
        this.importOptions = new ImportOptions();
        this.UserProvision = new UserProvision();
        this.AdvancedImportOptions = new AdvancedImportOptions();
        this.LoggingOptions = new LoggingOptions();
        this.AdvancedImportOptions.FoldersToSkip = new Folder[15];

        this.zimbraServer.ZimbraHostname = zimbraserverhostname;
        this.zimbraServer.Port = zimbraport;
        this.zimbraServer.ZimbraAdminID = zimbraAdmin;
        this.zimbraServer.ZimbraAdminPwd = zimbrapasswd;
        // this.zimbraServer.Domain = zimbradomain;
        this.mailServer.SourceHostname = mailserver;
        this.mailServer.SourceAdminID = srcAdminId;
        this.mailServer.SourceAdminPwd = srcAdminPwd;
        this.mailServer.OutlookProfile = outlookProfile;
        this.mailServer.PSTFile = pstFile;
        this.mailServer.UseProfile = true;

        this.GeneralOptions = new GeneralOptions();
        // this.mailServer.PSTFile = pstfile;
        // this.mailServer.ProfileName = outlookProfile;
    }

    public MailServer mailServer;
    public ZimbraServer zimbraServer;
    public ImportOptions importOptions;
    public UserProvision UserProvision;
    public AdvancedImportOptions AdvancedImportOptions;
    public LoggingOptions LoggingOptions;
    public GeneralOptions GeneralOptions;
}

public class MailServer
{
    private string m_SourceHostname;
    public string SourceHostname {
        get { return m_SourceHostname; }
        set { m_SourceHostname = value; }
    }
    private string m_SourceAdminID;
    public string SourceAdminID {
        get { return m_SourceAdminID; }
        set { m_SourceAdminID = value; }
    }
    private string m_SourceAdminPwd;
    public string SourceAdminPwd {
        get { return m_SourceAdminPwd; }
        set { m_SourceAdminPwd = value; }
    }
    private string m_OutlookProfile;
    public string OutlookProfile
    {
        get { return m_OutlookProfile; }
        set { m_OutlookProfile = value; }
    }
    private string m_PSTFile;
    public string PSTFile {
        get { return m_PSTFile; }
        set { m_PSTFile = value; }
    }
    private bool m_UseProfile;
    public bool UseProfile
    {
        get { return m_UseProfile; }
        set { m_UseProfile = value; }
    }
}

public class ZimbraServer
{
    private string m_ZimbraHostname;
    public string ZimbraHostname {
        get { return m_ZimbraHostname; }
        set { m_ZimbraHostname = value; }
    }
    private string m_Port;
    public string Port {
        get { return m_Port; }
        set { m_Port = value; }
    }
    private bool m_UseSSL;
    public bool UseSSL {
        get { return m_UseSSL; }
        set { m_UseSSL = value; }
    }
    private string m_ZimbraAdminID;
    public string ZimbraAdminID {
        get { return m_ZimbraAdminID; }
        set { m_ZimbraAdminID = value; }
    }
    private string m_ZimbraAdminPwd;
    public string ZimbraAdminPwd {
        get { return m_ZimbraAdminPwd; }
        set { m_ZimbraAdminPwd = value; }
    }
    private string m_UserAccount;
    public string UserAccount {
        get { return m_UserAccount; }
        set { m_UserAccount = value; }
    }
    private string m_UserPassword;
    public string UserPassword {
        get { return m_UserPassword; }
        set { m_UserPassword = value; }
    }
}

public class UserProvision
{
    private string m_COS;
    public string COS {
        get { return m_COS; }
        set { m_COS = value; }
    }
    private string m_defaultPWD;
    public string DefaultPWD {
        get { return m_defaultPWD; }
        set { m_defaultPWD = value; }
    }
    private string m_Domain;
    public string Domain {
        get { return m_Domain; }
        set { m_Domain = value; }
    }
}

public class GeneralOptions
{
    private Int32 MaxThreadCnt;
    public Int32 MaxThreadCount {
        get { return MaxThreadCnt; }
        set { MaxThreadCnt = value; }
    }
    private Int32 MaxErrorCnt;
    public Int32 MaxErrorCount {
        get { return MaxErrorCnt; }
        set { MaxErrorCnt = value; }
    }
    private Int32 MaxWarningCnt;
    public Int32 MaxWarningCount {
        get { return MaxWarningCnt; }
        set { MaxWarningCnt = value; }
    }
    private Boolean EnableLog;
    public Boolean Enablelog {
        get { return EnableLog; }
        set { EnableLog = value; }
    }
    private string LogFileLocation;
    public string LogFilelocation {
        get { return LogFileLocation; }
        set { LogFileLocation = value; }
    }
}
}
