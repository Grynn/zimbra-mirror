namespace MVVM.Model
{
    using System;

    public class Intro 
    {
        internal Intro()
        {
            Populate();
        }

        public string WelcomeMsg
        {
            get; set;
        }

        public Intro Populate()
        {
            this.WelcomeMsg = "This application will guide you through the process of migrating from Microsoft products to Zimbra.  You can migrate from an Exchange Server, an Outlook profile, or a PST file.\n\nSpecify the host name, port, and credentials of the destination Zimbra server.  You can choose which folders to migrate, as well as corresponding date filters.\n\nAny errors and warnings will be listed in the result set.";
            return this;
        }

    }
}
