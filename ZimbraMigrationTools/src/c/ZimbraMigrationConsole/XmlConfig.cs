using System.Collections.Generic;
using System.IO;
using System;

namespace ZimbraMigrationConsole
{
class XmlConfig
{
    public XmlConfig() {}

    public XmlConfig(string xmlFile, string userFile)
    {
        Filename = xmlFile;
        Usermap = userFile;
        ConfigObj = new MVVM.Model.Config();
        UserList = new List<MVVM.Model.Users>();
    }

    private string filename;
    public string Filename {
        get { return filename; }
        set { filename = value; }
    }
    private string usermap;
    public string Usermap {
        get { return usermap; }
        set { usermap = value; }
    }
    public MVVM.Model.Config ConfigObj;
    public List<MVVM.Model.Users> UserList;
    public void InitializeConfig()
    {
        System.Xml.Serialization.XmlSerializer reader =
            new System.Xml.Serialization.XmlSerializer(typeof (MVVM.Model.Config));
        if (File.Exists(filename))
        {
            System.IO.StreamReader fileRead = new System.IO.StreamReader(filename);

            ConfigObj = (MVVM.Model.Config)reader.Deserialize(fileRead);
        }
    }

    public void InitializeGeneralConfig(string args1)
    {
        string[] parameters = args1.Split('&');
        string[] Thread = (string[])Array.FindAll(parameters, s => s.Contains("-T"));
        if (Thread.Length > 0)
        {
            ConfigObj.GeneralOptions.MaxThreadCount = Convert.ToInt32(Thread[0].Substring(2));
            // ConfigObj.GeneralOptions.MaxThreadCount = Convert.ToInt32(args1.Substring((args1.IndexOf("-T") + 2),1));
        }
        string[] Error = (string[])Array.FindAll(parameters, s => s.Contains("-E"));
        if (Error.Length > 0)
        {
            ConfigObj.GeneralOptions.MaxErrorCount = Convert.ToInt32(Error[0].Substring(2));
            // ConfigObj.GeneralOptions.MaxThreadCount = Convert.ToInt32(args1.Substring((args1.IndexOf("-T") + 2),1));
        }
        string[] Warns = (string[])Array.FindAll(parameters, s => s.Contains("-W"));
        if (Warns.Length > 0)
        {
            ConfigObj.GeneralOptions.MaxWarningCount = Convert.ToInt32(Warns[0].Substring(2));
            // ConfigObj.GeneralOptions.MaxThreadCount = Convert.ToInt32(args1.Substring((args1.IndexOf("-T") + 2),1));
        }
        string[] Enablelog = (string[])Array.FindAll(parameters, s => s.Contains("-L"));
        if (Enablelog.Length > 0)
        {
            ConfigObj.GeneralOptions.Enablelog = Convert.ToBoolean(Enablelog[0].Substring(2));
            if (ConfigObj.GeneralOptions.Enablelog)
            {
                string[] FileLoc = (string[])Array.FindAll(parameters, s => s.Contains("-Loc"));
                if (FileLoc.Length > 0)
                    ConfigObj.GeneralOptions.LogFilelocation = FileLoc[0].Substring(4);
            }
            // ConfigObj.GeneralOptions.MaxThreadCount = Convert.ToInt32(args1.Substring((args1.IndexOf("-T") + 2),1));
        }
    }

    public void GetUserList()
    {
        List<string[]> parsedData = new List<string[]>();
        try
        {
            if (File.Exists(Usermap))
            {
                using (StreamReader readFile = new StreamReader(Usermap)) {
                    string line;

                    string[] row;
                    while ((line = readFile.ReadLine()) != null)
                    {
                        row = line.Split(',');
                        parsedData.Add(row);
                    }
                    readFile.Close();
                }
            }
            else
            {
                Console.WriteLine("There is no UserMap file");  // "Zimbra Migration", MessageBoxButton.OK, MessageBoxImage.Error);
            }
        }
        catch (Exception e)
        {
            string message = e.Message;
        }
        // for (int i = 1; i < parsedData.Count; i++)
        {
            string[] strres = new string[parsedData.Count];

            int index = 0;

            for (int j = 1; j < parsedData.Count; j++)
            {
                MVVM.Model.Users tempuser = new MVVM.Model.Users();
                strres = parsedData[j];

                tempuser.UserName = strres[0];
                tempuser.MappedName = strres[1];
                

                 tempuser.ChangePWD = Convert.ToBoolean(strres[2]);
                 tempuser.PWDdefault = strres[3];
                // string result = tempuser.UserName + "," + tempuser.MappedName +"," + tempuser.ChangePWD + "," + tempuser.PWDdefault;

                UserList.Insert(index, tempuser);
            }
        }
    }
}
}
