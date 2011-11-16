using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using System.Runtime.InteropServices;

namespace GroupWise
{
    class GWadmin
    {
        //This class will represent all the admin funcationality for groupwise admin like trustedapp creation etc..

        [DllImport(@"GWTapp.DLL", CharSet = CharSet.Ansi)]
        public static extern Int32 CreateTrustedAppObject(string domain, string appname, string appdesc, string tcpaddress, string port, bool sslr, bool reqque, bool mesgreten, bool overwrite, StringBuilder outval);

        string username;

        public string Username
        {
            get { return username; }
            set { username = value; }
        }
        string userpwd;
        string key;

        public string Key
        {
            get { return key; }
            set { key = value; }
        }
        public GWadmin(string uname,string pwd)
        {
            Username = uname;
            userpwd = pwd;
            key = "";
        }

        public void Initialize(string domainpath)
        {
            StringBuilder outkey = new StringBuilder(516);
            
            if (!(File.Exists(@"Key.txt")))
            {
                try
                {
                    int status = CreateTrustedAppObject(domainpath, "ZimbraGWmigration", "", "", "", false, false, false, true, outkey);
                    Key = outkey.ToString();
                    System.IO.File.AppendAllText(@"Key.txt", outkey.ToString());
                    string szMsg ="" ;
                    switch( status)
                    {
                        case 0/*SUCCESS*/:
			              //  if (bDeleteTApp)
				                //strcpy(szMsg, "The Trusted Application was deleted successfully.");
			              //  else
				                szMsg = "The Trusted Application was created successfully.";
			                break;
		                case 1/*INIT_ERROR*/: 
                            szMsg = "An error has occurred: INIT_ERROR";
                            break;
		                case 2/*CONNECT_ERROR*/:
                            szMsg = "An error has occurred: CONNECT_ERROR";
                            break;
		                case 3/*NEW_RECORD_ERROR*/:
                            szMsg = "An error has occurred: NEW_RECORD_ERROR";
                            break;
		                case 4/*FIELD_ERROR*/:
                            szMsg = "An error has occurred: FIELD_ERROR";
                            break;
		                case 5/*GET_RECORD_ERROR*/:
                            szMsg = "An error has occurred: GET_RECORD_ERROR";
                            break;
		                case 6/*NOT_UNIQUE_ERROR*/:
                            szMsg = "An error has occurred: NOT_UNIQUE_ERROR";
                            break;
		                case 7/*OVERWRITE_ERROR*/:
                            szMsg= "An error has occurred: OVERWRITE_ERROR";
                            break;
		                case 8/*GET_RECORD_ID_ERROR*/: 
                            szMsg=  "An error has occurred: GET_RECORD_ID_ERROR";
                            break;
		                case 9/*KEY_DOESNT_MATCH_ERROR*/:
                            szMsg= "An error has occurred: KEY_DOESNT_MATCH_ERROR";
                            break;
		                case 10/*DELETE_ERROR*/: 
                            szMsg= "An error has occurred: DELETE_ERROR";
                            break;
		                case 11/*AUTHENTICATION_ERROR*/:
                            szMsg="An error has occurred: AUTHENTICATION_ERROR";
                            break;
	                }


                    System.Console.WriteLine(" CreateTrustedAppObject status returned " + szMsg);


                    }
               
                catch (Exception e)
                {

                    System.Console.WriteLine(" error with exeption " + e.Message);

                }
            }
            else
                Key = File.ReadAllText(@"key.txt");
                    
            
        }
    }
}
