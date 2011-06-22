using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
//using MVVM;
using Exchange;
using System.IO;

namespace CssLib
{
  
    public class CSMigrationwrapper
    {
       /* string m_ConfigXMLFile;

        public string ConfigXMLFile
        {
            get { return m_ConfigXMLFile; }
            set { m_ConfigXMLFile = value; }
        }
        string m_UserMapFile;

        public string UserMapFile
        {
            get { return m_UserMapFile; }
            set { m_UserMapFile = value; }
        }*/
        string m_MailClient;

        public string MailClient
        {
            get { return m_MailClient; }
            set { m_MailClient = value; }
        }

     /*   MVVM.Model.Config ConfigObj = new MVVM.Model.Config();
        MVVM.Model.ImportOptions ImportOptions = new MVVM.Model.ImportOptions();
        MVVM.Model.Users  users = new MVVM.Model.Users();*/

        Exchange.IMapiWrapper MailWrapper;

        /*private void CreateConfig(string Xmlfilename)
        {


            System.Xml.Serialization.XmlSerializer reader =
            new System.Xml.Serialization.XmlSerializer(typeof(MVVM.Model.Config));
            if (File.Exists(Xmlfilename))
            {

                System.IO.StreamReader fileRead = new System.IO.StreamReader(
                       Xmlfilename);

                ConfigObj = (MVVM.Model.Config)reader.Deserialize(fileRead);

            }
        }
        */
        public void Initalize(string HostName,string Port, string AdminAccount)
        {
            //CreateConfig(ConfigXMLFile);

            if( MailClient == "MAPI")
            {

                 MailWrapper = new Exchange.MapiWrapper();

                MailWrapper.ConnectToServer(HostName,Port,AdminAccount);




            }

        }

        public void Migrate(string MailOptions)
        {

            MailWrapper.ImportMailOptions(MailOptions);

        }

   
    }
 
    }

