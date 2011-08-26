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

        public void InitializeInterop()
        {
            if (MailClient == "MAPI")
            {

                MailWrapper = new Exchange.MapiWrapper();              
            }
        }

        
        
        public string InitializeMailClient(string Target, string AdminUser, string AdminPassword)
        {
            string s = "";
            if (MailClient == "MAPI")
            {
                MailWrapper = new Exchange.MapiWrapper();
                s = MailWrapper.GlobalInit(Target, AdminUser, AdminPassword);
            }
            return s;
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

          /*  UDTFolder[] folders;
            MapiWrapper M1 = new MapiWrapper();
            folders = (UDTFolder[]) M1.UDTFolderSequence(0, 10);
            string name = folders[0].Name;
            FolderType type = folders[0].Type;*/
            /*UDTItem item;
            item.EntryId = "000-444-444";
            item.Type = FolderType.Mail;
            item.CreationDate = DateTime.Now;
            M1.set_UDTItem( ref item);
            UDTItem i1 = M1.get_UDTItem();*/

            MapiWrapper M1 = new MapiWrapper();
            object[] objectArray;
            objectArray = M1.GetFolderObjects();

            folderObject[] Folders = Array.ConvertAll(objectArray, folder => (folderObject)folder);

            string name = Folders[0].Name;
            long id = Folders[0].Id;

            string path = Folders[0].ParentPath;
           


        }


        public void GetListofItems()
        {
            MapiWrapper M1 = new MapiWrapper();
            /*UDTItem[] Items;
            Items = (UDTItem[])M1.UDTItemSequence(0, 5);
            DateTime cdate = (DateTime)Items[0].CreationDate;
            FolderType types = Items[0].Type;
            string entryid = Items[0].EntryId;*/

            object[] objectArray;
            objectArray = M1.GetFolderObjects();

            folderObject[] Folders = Array.ConvertAll(objectArray, folder => (folderObject)folder);


            ItemObject SI1 = new ItemObject();
            SI1.Id = "2131323";
            SI1.Type = FolderType.Contacts;
            SI1.Parentfolder = Folders[0];

            folderObject s2 = SI1.Parentfolder;

            string firstname = s2.Name;
            string path = s2.ParentPath;

            
            string[,] data = SI1.GetDataForItem();
            
               int bound0 = data.GetUpperBound(0);
              
               Dictionary<string, string> dict = new Dictionary<string, string>();

               for (int i = 0; i <= bound0; i++)
               {
                   string Key = data[0, i]; 
                   string Value = data[1, i];
                   dict.Add(Key, Value);
                  // Console.WriteLine("{0}, {1}", so1, so2);
               }



        }

        public void  StartMigration(MigrationAccount Acct)
        {

            //GetListofMapiFolders();
            GetListofItems();
            //Acct.Accountname = "testing";
            Acct.TotalNoContacts = 100;
            Acct.TotalNoMails = 1000;
            Acct.TotalNoRules = 10;
            Acct.TotalNoItems = 1110;
            //Acct.TotalNoErrors = 0;   don't set these -- adds 1 when it shouldn't
            //Acct.TotalNoWarnings = 0;
             long count = 0;

             long totalCount = 0;
             switch (Acct.Accountnum)
             {
                 case 0:
                     totalCount = 100;
                     break;
                 case 1:
                     totalCount = 200;
                     break;
                 case 2:
                     totalCount = 300;
                     break;
                 case 3:
                     totalCount = 400;
                     break;
                 default:
                     totalCount = 100;
                     break;
             }

             Acct.migrationFolders[0].FolderName = "Contacts";
             Acct.migrationFolders[0].TotalCountOFItems = totalCount;
             Acct.migrationFolders[0].CurrentCountOFItems = 0;

            while (count < totalCount)
            {

               

                System.Threading.Thread.Sleep(2000);
                Acct.migrationFolders[0].CurrentCountOFItems = Acct.migrationFolders[0].CurrentCountOFItems + 20;

                if (Acct.Accountnum == 0)
                {
                    if (count == 60)
                    {
                        Acct.LastProblemInfo = new ProblemInfo("John Doe", "Invalid character", ProblemInfo.TYPE_ERR);
                        Acct.TotalNoErrors++;
                    }
                }
                count = count + 20;

            }

            Acct.migrationFolders[0].LastFolderInfo = new FolderInfo("Contacts", "Contact", string.Format("{0} of {1}", totalCount.ToString(), totalCount.ToString()));

            switch (Acct.Accountnum)
            {
                case 0:
                    totalCount = 700;
                    break;
                case 1:
                    totalCount = 800;
                    break;
                case 2:
                    totalCount = 900;
                    break;
                case 3:
                    totalCount = 1000;
                    break;
                default:
                    totalCount = 1100;
                    break;
            }

            Acct.migrationFolders[0].FolderName = "Mails";
            Acct.migrationFolders[0].TotalCountOFItems = totalCount;
            Acct.migrationFolders[0].CurrentCountOFItems = 0;
            while ((count >= 100) & (count < totalCount))
            {
                if (Acct.Accountnum == 0)
                {
                    if (count == 200)
                    {
                        Acct.LastProblemInfo = new ProblemInfo("Message4", "Invalid UID", ProblemInfo.TYPE_ERR);
                        Acct.TotalNoErrors++;
                    }
                    if (count == 400)
                    {
                        Acct.LastProblemInfo = new ProblemInfo("TestMessage", "Invalid Attachment", ProblemInfo.TYPE_ERR);
                        Acct.TotalNoErrors++;
                    }
                    if (count == 500)
                    {
                        Acct.LastProblemInfo = new ProblemInfo("AnotherTest", "Address has an unsupported format", ProblemInfo.TYPE_ERR);
                        Acct.TotalNoErrors++;
                    }
                }

                if (Acct.Accountnum == 1)
                {
                    if (count == 300)
                    {
                        Acct.LastProblemInfo = new ProblemInfo("Status Report", "Illegal recipient", ProblemInfo.TYPE_ERR);
                        Acct.TotalNoErrors++;
                    }
                    if (count == 400)
                    {
                        Acct.LastProblemInfo = new ProblemInfo("Company picnic", "Unsupported encoding", ProblemInfo.TYPE_WARN);
                        Acct.TotalNoWarnings++;
                    }
                    if (count == 600)
                    {
                        Acct.LastProblemInfo = new ProblemInfo("Last call", "Duplicate UID", ProblemInfo.TYPE_WARN);
                        Acct.TotalNoWarnings++;
                    }
                }
               

                System.Threading.Thread.Sleep(2000);
                Acct.migrationFolders[0].CurrentCountOFItems = Acct.migrationFolders[0].CurrentCountOFItems + 100;
                count = count + 100;

            }

            Acct.migrationFolders[0].LastFolderInfo = new FolderInfo("Inbox", "Message", string.Format("{0} of {1}", totalCount.ToString(), totalCount.ToString()));

            switch (Acct.Accountnum)
            {
                case 0:
                    totalCount = 11;
                    break;
                case 1:
                    totalCount = 12;
                    break;
                case 2:
                    totalCount = 13
                        ; break;
                case 3:
                    totalCount = 14;
                    break;
                default:
                    totalCount = 10;
                    break;
            }

            Acct.migrationFolders[0].FolderName = "Rules";
            Acct.migrationFolders[0].TotalCountOFItems = totalCount;
            Acct.migrationFolders[0].CurrentCountOFItems = 0;

            long tempCount = count;
            while ((count >= tempCount) & (count <= (tempCount + 10)))
            {

                
                System.Threading.Thread.Sleep(2000);
                Acct.migrationFolders[0].CurrentCountOFItems = Acct.migrationFolders[0].CurrentCountOFItems + 10;
                if (Acct.Accountnum == 0)
                {
                    if (count == 710)
                    {
                        Acct.LastProblemInfo = new ProblemInfo("BugzillaRule", "Unsupported condition", ProblemInfo.TYPE_ERR);
                        Acct.TotalNoErrors++;
                    }
                }
                count = count + 10;


            }

            Acct.migrationFolders[0].LastFolderInfo = new FolderInfo("Inbox", "Rule", string.Format("{0} of {1}", totalCount.ToString(), totalCount.ToString()));

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

