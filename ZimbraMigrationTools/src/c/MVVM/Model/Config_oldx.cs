namespace MVVM.Model
{
    using System;

    public class Config
    {
        public Config() {}
        public Config(string exchangeProfile, string outlookProfile, string pstFile, string zimbraserverhostname, string zimbraport, string mailserver)
        {
            this.ExchangeProfile = exchangeProfile;
            this.OutlookProfile = outlookProfile;
            this.PSTFile = pstFile;
            this.ZimbraServerHostName1 = zimbraserverhostname;
            this.ZimbraPort1 = zimbraport;
            this.MailServerName1 = mailserver;
            this.test1 = new test();
            this.test1.Name = mailserver;
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

        private string ZimbraServerHostName;


        public string ZimbraServerHostName1
        {
            get { return ZimbraServerHostName; }
            set { ZimbraServerHostName = value; }
        }

        private string ZimbraPort;

        public string ZimbraPort1
        {
            get { return ZimbraPort; }
            set { ZimbraPort = value; }
        }

        private Boolean UseSecure;

        public Boolean UseSecure1
        {
            get { return UseSecure; }
            set { UseSecure = value; }
        }

        private Boolean InvalidCert;

        public Boolean InvalidCert1
        {
            get { return InvalidCert; }
            set { InvalidCert = value; }
        }

        private string ZimbraAccountName;

        public string ZimbraAccountName1
        {
            get { return ZimbraAccountName; }
            set { ZimbraAccountName = value; }
        }

        private string ZimbraPassword;

        public string ZimbraPassword1
        {
            get { return ZimbraPassword; }
            set { ZimbraPassword = value; }
        }

        private string ZimbraDomain;

        public string ZimbraDomain1
        {
            get { return ZimbraDomain; }
            set { ZimbraDomain = value; }
        }

        private string MailServerName;

        public string MailServerName1
        {
            get { return MailServerName; }
            set { MailServerName = value; }
        }

        private string MailAdminAccount;

        public string MailAdminAccount1
        {
            get { return MailAdminAccount; }
            set { MailAdminAccount = value; }
        }

        private string MailPassword;

        public string MailPassword1
        {
            get { return MailPassword; }
            set { MailPassword = value; }
        }

        public test test1;
    }
    public class test
    {

        public string Name { get; set; }


    }
}
