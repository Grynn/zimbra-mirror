using MVVM.Model;
using Misc;
using System.ComponentModel;
using System.Diagnostics;
using System.IO;
using System.Windows.Controls;
using System.Windows.Input;
using System.Windows;
using System.Xml.Linq;
using System.Xml;
using System;

namespace MVVM.ViewModel
{
public class BaseViewModel: INotifyPropertyChanged
{
    public enum ViewType
    {
        INTRO, SVRSRC, USRSRC, SVRDEST, USRDEST, OPTIONS, USERS, SCHED, RESULTS, MAX
    }

    public event PropertyChangedEventHandler PropertyChanged;

    public Config m_config = new Config("", "", "", "", "", "", "", "", "", "", "");
    public void OnPropertyChanged(PropertyChangedEventArgs e)
    {
        if (PropertyChanged != null)
            PropertyChanged(this, e);
    }
    public string Name { get; set; }
    public string ViewTitle { get; set; }
    public ListBox lb { get; set; }
    public static bool isServer { get; set; }
    public bool isBrowser { get; set; }
    public static Object[] ViewModelPtrs = new Object[(int)ViewType.MAX];

    public BaseViewModel()
    {
        this.ProcessHelpCommand = new ActionCommand(this.ProcessHelp, () => true);
    }
    public ICommand ProcessHelpCommand {
        get;
        private set;
    }
    private void ProcessHelp()
    {
        string helpFile = "";

        switch (lb.SelectedIndex)
        {
        case 0:
            DoHelp("welcome.html");
            break;
        case 1:
            helpFile = isServer ? "cfgS.html" : "cfgU.html";
            DoHelp(helpFile);
            break;
        case 2:
            helpFile = isServer ? "cfgSDest.html" : "cfgUDest.html";
            DoHelp(helpFile);
            break;
        case 3:
            DoHelp("options.html");
            break;
        case 4:
            helpFile = isServer ? "users.html" : "acctresults.html";
            DoHelp(helpFile);
            break;
        case 5:
            DoHelp("sched.html");
            break;
        case 6:
            DoHelp("acctresults.html");
            break;
        default:
            break;
        }
    }

    public void UpdateXmlElement(string XmlfileName, string XmlelementName)
    {
        System.Xml.Serialization.XmlSerializer xmlSerializer =
            new System.Xml.Serialization.XmlSerializer(typeof (MVVM.Model.Config));
        System.IO.StringWriter stringWriter = new System.IO.StringWriter();
        System.Xml.XmlWriter xmlWriter = new System.Xml.XmlTextWriter(stringWriter);
        xmlSerializer.Serialize(xmlWriter, m_config);

        string newSourceXml = stringWriter.ToString();
        XElement newSourceTypeElem = XElement.Parse(newSourceXml);
        XElement newimport = newSourceTypeElem.Element((XName)XmlelementName);
        XmlDocument xmlDoc = new XmlDocument();

        xmlDoc.PreserveWhitespace = true;

        try
        {
            xmlDoc.Load(XmlfileName);
        }
        catch (XmlException e)
        {
            Console.WriteLine(e.Message);
        }
        // Now create StringWriter object to get data from xml document.
        StringWriter sw = new StringWriter();
        XmlTextWriter xw = new XmlTextWriter(sw);

        xmlDoc.WriteTo(xw);

        string SourceXml = sw.ToString();

        // Replace the current view xml Sources node with the new one
        XElement viewXmlElem = XElement.Parse(SourceXml);
        XElement child = viewXmlElem.Element((XName)XmlelementName);

        // viewXmlElem.Element("importOptions").ReplaceWith(newSourcesElem);
        child.ReplaceWith(newimport);

        xmlDoc.LoadXml(viewXmlElem.ToString());
        xmlDoc.Save(XmlfileName);
    }

    public void PopulateConfig(bool isServer)
    {
	ConfigViewModelS     serverSourceModel = (ConfigViewModelS)ViewModelPtrs[(int)ViewType.SVRSRC];
        ConfigViewModelSDest serverDestModel   = (ConfigViewModelSDest)ViewModelPtrs[(int)ViewType.SVRDEST];
        ConfigViewModelU     userSourceModel   = (ConfigViewModelU)ViewModelPtrs[(int)ViewType.USRSRC];
	ConfigViewModelUDest userDestModel     = (ConfigViewModelUDest)ViewModelPtrs[(int)ViewType.USRDEST];
	OptionsViewModel     optionsModel      = (OptionsViewModel)ViewModelPtrs[(int)ViewType.OPTIONS];
	UsersViewModel       usersModel        = (UsersViewModel)ViewModelPtrs[(int)ViewType.USERS];

        m_config.mailServer.OutlookProfile = "";
        if (isServer)
	{
            int sel = serverSourceModel.CurrentProfileSelection;
            if (sel != -1)
            {
                if (serverSourceModel.ProfileList.Count > 0)
                {
                    m_config.mailServer.OutlookProfile = serverSourceModel.ProfileList[sel];
                }
            } 
	    m_config.mailServer.SourceHostname   = serverSourceModel.MailServerHostName;
	    m_config.mailServer.SourceAdminID    = serverSourceModel.MailServerAdminID;
	    m_config.mailServer.SourceAdminPwd   = serverSourceModel.MailServerAdminPwd;
	    m_config.mailServer.UseProfile       = serverSourceModel.Isprofile;
	    m_config.zimbraServer.ZimbraHostname = serverDestModel.ZimbraServerHostName;
	    m_config.zimbraServer.Port           = serverDestModel.ZimbraPort;
	    m_config.zimbraServer.ZimbraAdminID  = serverDestModel.ZimbraAdmin;
	    m_config.zimbraServer.ZimbraAdminPwd = serverDestModel.ZimbraAdminPasswd;
            m_config.UserProvision.Domain        = usersModel.ZimbraDomain;
	}
	else
	{
            int sel = userSourceModel.CurrentProfileSelection;
            if (sel != -1)
            {
                if (userSourceModel.ProfileList.Count > 0)
                {
                    m_config.mailServer.OutlookProfile = userSourceModel.ProfileList[sel];
                }
            } 
	    m_config.mailServer.PSTFile          = userSourceModel.PSTFile;
	    m_config.mailServer.UseProfile       = userSourceModel.Isprofile;
	    m_config.zimbraServer.ZimbraHostname = userDestModel.ZimbraServerHostName;
	    m_config.zimbraServer.Port           = userDestModel.ZimbraPort;
	    m_config.zimbraServer.UserAccount    = userDestModel.ZimbraUser;
	    m_config.zimbraServer.UserPassword   = userDestModel.ZimbraUserPasswd;
	}

        m_config.LoggingOptions.Verbose     = optionsModel.LoggingVerbose;
        m_config.importOptions.Mail         = optionsModel.ImportMailOptions;
        m_config.importOptions.Calendar     = optionsModel.ImportCalendarOptions;
        m_config.importOptions.Contacts     = optionsModel.ImportContactOptions;
        m_config.importOptions.DeletedItems = optionsModel.ImportDeletedItemOptions;
        m_config.importOptions.Junk         = optionsModel.ImportJunkOptions;
        m_config.importOptions.Tasks        = optionsModel.ImportTaskOptions;
        m_config.importOptions.Sent         = optionsModel.ImportSentOptions;
        m_config.importOptions.Rules        = optionsModel.ImportRuleOptions;
        m_config.importOptions.OOO          = optionsModel.ImportOOOOptions;
        m_config.AdvancedImportOptions.MigrateONRAfter = DateTime.Parse(optionsModel.MigrateONRAfter);
        m_config.AdvancedImportOptions.MaxMessageSize = optionsModel.MaxMessageSize;

        // deal with skip folders
        if (optionsModel.FoldersToSkip != null)
        {
            if (optionsModel.FoldersToSkip.Length > 0)
            {
                string[] nameTokens = optionsModel.FoldersToSkip.Split(',');
                for (int i = 0; i < nameTokens.Length; i++)
                {
                    Folder folder = new Folder();
                    folder.FolderName = nameTokens.GetValue(i).ToString();
                    m_config.AdvancedImportOptions.FoldersToSkip[i] = folder;
                }
            }
        }
    }

    protected void DoHelp(string htmlFile)
    {
        string fileName;
        string urlString;
        bool bDoProcess;

        if (isBrowser)
        {
            fileName = urlString = "http://W764IIS.prom.eng.vmware.com/" + htmlFile;
            bDoProcess = true;                  // too lazy to check if xbap
        }
        else
        {
            fileName = ((IntroViewModel)ViewModelPtrs[(int)ViewType.INTRO]).InstallDir;
            fileName += "/";
            fileName += htmlFile;
            urlString = "file:///" + fileName;
            bDoProcess = File.Exists(fileName);
        }
        if (bDoProcess)
            Process.Start(new ProcessStartInfo(urlString));

        else
            MessageBox.Show("Help file not found", "Open file error", MessageBoxButton.OK,
                MessageBoxImage.Error);
    }
}
}
