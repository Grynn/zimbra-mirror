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

        public void InitializeMailClient()
        {

            if (MailClient == "MAPI")
            {

                MailWrapper = new Exchange.MapiWrapper();

              
            }


        }
        public void Initalize(string HostName,string Port, string AdminAccount)
        {
            //CreateConfig(ConfigXMLFile);

            
                MailWrapper.ConnectToServer(HostName,Port,AdminAccount);




        }

        public void Migrate(string MailOptions)
        {

            MailWrapper.ImportMailOptions(MailOptions);

            
           
                   
           

        }

        public string[] GetListofMapiProfiles()
        {


            object var = new object();

            MailWrapper.GetProfilelist(out var);


            string[] s = (string[])var;

            return s;



        }


        public void GetListofMapiFolders()
        {

            UDTFolder[] folders;
            MapiWrapper M1 = new MapiWrapper();
            folders = (UDTFolder[]) M1.UDTFolderSequence(0, 10);
            string name = folders[0].Name;

        }

        public void  StartMigration(MigrationAccount Acct)
        {

            //GetListofMapiFolders();
            //Acct.Accountname = "testing";
            Acct.TotalNoContacts = 100;
            Acct.TotalNoMails = 1000;
            Acct.TotalNoRules = 10;
             Acct.TotalNoItems = 1110;
             long count = 0;

             Acct.migrationFolders[0].FolderName = "Contacts";
             Acct.migrationFolders[0].TotalCountOFItems = 100;
             Acct.migrationFolders[0].CurrentCountOFItems = 0;

            while (count < 100)
            {

               

                System.Threading.Thread.Sleep(2000);
                Acct.migrationFolders[0].CurrentCountOFItems = Acct.migrationFolders[0].CurrentCountOFItems + 20;
                count = count + 20;

            }

            Acct.migrationFolders[0].FolderName = "Mails";
            Acct.migrationFolders[0].TotalCountOFItems = 1000;
            Acct.migrationFolders[0].CurrentCountOFItems = 0;
            while ((count >= 100) & (count < 1100))
            {

               

                System.Threading.Thread.Sleep(2000);
                Acct.migrationFolders[0].CurrentCountOFItems = Acct.migrationFolders[0].CurrentCountOFItems + 100;
                count = count + 100;

            }
            Acct.migrationFolders[0].FolderName = "Rules";
            Acct.migrationFolders[0].TotalCountOFItems = 10;
            Acct.migrationFolders[0].CurrentCountOFItems = 0;

            while (count == 1100)
            {

                
                System.Threading.Thread.Sleep(2000);
                Acct.migrationFolders[0].CurrentCountOFItems = Acct.migrationFolders[0].CurrentCountOFItems + 10;
                count = count + 10;


            }

        /*   foreach (MigrationFolder mt in Acct.migrationFolders)
           {

               
              mt.FolderName = "Contactsxx";
              mt.TotalCountOFItems= 100;
              mt.CurrentCountOFItems = 0;
               //Acct.migrationFolders.Insert(i,temp);
           }



           System.Threading.Thread.Sleep(6000);
           Acct.migrationFolders[0].CurrentCountOFItems = 30;*/

           



        }

   
    }
 
    }

