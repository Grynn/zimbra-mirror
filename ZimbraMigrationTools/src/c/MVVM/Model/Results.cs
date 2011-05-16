
namespace MVVM.Model
{
    using System;

    public class AccountResults
    {
        internal AccountResults(int pbValue, string pbMsgValue, string accountName, int accountProgress, int numErrs, int numWarns, bool enableStop)
        {
            this.PBValue = pbValue;
            this.PBMsgValue = pbMsgValue;
            this.AccountName = accountName;
            this.AccountProgress = accountProgress;
            this.NumErrs = numErrs;
            this.NumWarns = numWarns;
            this.EnableStop = enableStop;
        }

        public int PBValue
        {
            get;
            set;
        }

        public string PBMsgValue
        {
            get;
            set;
        }

        public string AccountName
        {
            get; set;
        }

        public int AccountProgress
        {
            get; set;
        }

        public int NumErrs
        {
            get; set;
        }

        public int NumWarns
        {
            get; set;
        }

        public int CurrentAccountSelection
        {
            get;
            set;
        }

        public bool OpenLogFileEnabled
        {
            get;
            set;
        }

        public bool EnableStop
        {
            get;
            set;
        }
    }

    public class UserResults
    {
        internal UserResults(string folderName, string objName, string theErr)
        {
            this.FolderName = folderName;
            this.ObjName = objName;
            this.TheErr = theErr;
        }

        public string FolderName
        {
            get;
            set;
        }

        public string ObjName
        {
            get;
            set;
        }

        public string TheErr
        {
            get;
            set;
        }
    }
}
