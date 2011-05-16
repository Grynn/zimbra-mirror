
namespace MVVM.Model
{
    using System;

    public class Schedule
    {
        internal Schedule(int pbValue, string pbMsgValue, string usernameEntered, bool EnableMigrate)
        {
            this.PBValue = pbValue;
            this.PBMsgValue = pbMsgValue;
            this.EnableMigrate = EnableMigrate;
        }

        public int PBValue
        {
            get; set;
        }

        public string PBMsgValue
        {
            get; set;
        }

        public bool EnableMigrate
        {
            get; set;
        }
    }
}
