namespace MVVM.Model
{
    using System;

    public class Config
    {
        public Config() {}
        public Config(string exchangeProfile, string outlookProfile, string pstFile, string zimbraserverhostname, string zimbraport,
                      string mailserver,string zimbraAdmin,string zimbrapasswd,string zimbradomain,string pstfile,bool mailoptions)
        {
            this.ExchangeProfile = exchangeProfile;
            this.OutlookProfile = outlookProfile;
            this.PSTFile = pstFile;
            this.zimbraServer = new ZimbraServer();
            this.mailServer = new MailServer();
            this.importOptions = new ImportOptions();


           this.zimbraServer.HostName = zimbraserverhostname;
           this.zimbraServer.Port = zimbraport;
           this.zimbraServer.AdminAccount = zimbraAdmin;
           this.zimbraServer.AdminPassword = zimbrapasswd;
           this.zimbraServer.Domain = zimbradomain;
           this.mailServer.Hostname = mailserver;
           this.mailServer.PSTFile = pstfile;
           this.mailServer.ProfileName = outlookProfile;
           this.importOptions.Mail = mailoptions;

        }



        public string ExchangeProfile
        {
            get; set;
        }

        public string OutlookProfile
        {
            get; set;
        }

        public string PSTFile
        {
            get; set;
        }

        public MailServer mailServer;
        public ZimbraServer zimbraServer;
        public ImportOptions importOptions;
        
        
    }
    public class MailServer
    {

        private string m_Hostname;

        public string Hostname
        {
            get { return m_Hostname; }
            set { m_Hostname = value; }
        }
        private string m_AdminAccount;

        public string AdminAccount
        {
            get { return m_AdminAccount; }
            set { m_AdminAccount = value; }
        }
        private string m_ProfileName;

        public string ProfileName
        {
            get { return m_ProfileName; }
            set { m_ProfileName = value; }
        }
        private string m_PSTFile;

        public string PSTFile
        {
            get { return m_PSTFile; }
            set { m_PSTFile = value; }
        }
    }
    public class ZimbraServer
    {

        private string m_HostName;

        public string HostName
        {
            get { return m_HostName; }
            set { m_HostName = value; }
        }


        private string m_Port;

        public string Port
        {
            get { return m_Port; }
            set { m_Port = value; }
        }
        private bool m_UseSSL;


        public bool UseSSL
        {
            get { return m_UseSSL; }
            set { m_UseSSL = value; }
        }

        private string m_AdminAccount;

        public string AdminAccount
        {
            get { return m_AdminAccount; }
            set { m_AdminAccount = value; }
        }

        private string m_AdminPassword;

        public string AdminPassword
        {
            get { return m_AdminPassword; }
            set { m_AdminPassword = value; }
        }


        private string m_Domain;

        public string Domain
        {
            get { return m_Domain; }
            set { m_Domain = value; }
        }


    }

    public class ImportOptions
    {
        private bool m_Mail;

        public bool Mail
        {
            get { return m_Mail; }
            set { m_Mail = value; }
        }
        private bool m_Contacts;

        public bool Contacts
        {
            get { return m_Contacts; }
            set { m_Contacts = value; }
        }
        private bool m_Calendar;

        public bool Calendar
        {
            get { return m_Calendar; }
            set { m_Calendar = value; }
        }
        private bool m_Tasks;

        public bool Tasks
        {
            get { return m_Tasks; }
            set { m_Tasks = value; }
        }


        private bool m_DeletedItems;


        public bool DeletedItems
        {
            get { return m_DeletedItems; }
            set { m_DeletedItems = value; }
        }
        private bool m_Junk;

        public bool Junk
        {
            get { return m_Junk; }
            set { m_Junk = value; }
        }

        private bool m_Sent;

        public bool Sent
        {
            get { return m_Sent; }
            set { m_Sent = value; }
        }

        private bool m_Rules;

        public bool Rules
        {
            get { return m_Rules; }
            set { m_Rules = value; }
        }


    }
}
