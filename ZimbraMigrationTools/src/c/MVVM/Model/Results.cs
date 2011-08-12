
namespace MVVM.Model
{
    using System;

    public class AccountResults
    {
        internal AccountResults(int pbValue, string pbMsgValue, string accountName, int accountProgress, string acctProgressMsg, int numErrs, int numWarns, bool enableStop)
        {
            this.PBValue = pbValue;
            this.PBMsgValue = pbMsgValue;
            this.AccountName = accountName;
            this.AccountProgress = accountProgress;
            this.AcctProgressMsg = acctProgressMsg;
            this.NumErrs = numErrs;
            this.NumWarns = numWarns;
            this.EnableStop = enableStop;
            this.UserPBMsgValue = "";
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

        public string UserPBMsgValue
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

        public string AcctProgressMsg
        {
            get;
            set;
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

        public string SelectedTab
        {
            get;
            set;
        }
    }

    public class UserResults
    {
        internal UserResults(string folderName, string typeName, string progressMsg)
        {
            this.FolderName = folderName;
            this.TypeName = typeName;
            this.UserProgressMsg = progressMsg;
        }

        public string FolderName
        {
            get;
            set;
        }

        public string TypeName
        {
            get;
            set;
        }

        public string UserProgressMsg
        {
            get;
            set;
        }
    }
}
