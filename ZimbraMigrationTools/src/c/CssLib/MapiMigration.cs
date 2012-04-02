using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace CssLib
{
    public class MapiMigration : MailMigration
    {
        public dynamic MapiWrapper;
        public  MapiMigration()
        {
             MapiWrapper = new Exchange.MapiWrapper();
        }

        public override string GlobalInit(string Target, string AdminUser, string AdminPassword)
        {
        
        string s = "";

        try
        {
            s = MapiWrapper.GlobalInit(Target, AdminUser, AdminPassword);
        }
        catch (Exception e)
        {
            s = string.Format("Initialization Exception. Make sure to enter the proper credentials: {0}", e.Message);
        }
        return s;

        }


        public override string GlobalUninit()
        {
            try
            {
                return MapiWrapper.GlobalUninit();
            }
            catch (Exception e)
            {
                string msg = string.Format("GetListofMapiProfiles Exception: {0}", e.Message);
                return msg;
            }


        }

        public override string GetProfilelist(out object  var)
        {
            
            return MapiWrapper.GetProfilelist(out var);

        }
        public override string SelectExchangeUsers(out object var)
        {
            return MapiWrapper.SelectExchangeUsers(out var);

        }
    }

   public class MapiUser : MigrationUser
    {
       public Exchange.UserObject UserObj;
       public Exchange.UserObject GetInternalUser()
       {

           return UserObj;
       }
        public MapiUser()
        {
            UserObj = new Exchange.UserObject();
        }
        public override string Init(string host, string AccountID, string accountName)
        {

            string value = UserObj.Init(host, AccountID, accountName);
            return value;
        }

        public override dynamic[] GetFolders()
        {
            return UserObj.GetFolders();


        }

        public override dynamic[] GetItemsForFolder(dynamic folderobject, double Date)
        {

            return UserObj.GetItemsForFolder( folderobject,  Date);

        }

        public override string[,] GetRules()
        {
            try
            {

                return UserObj.GetRules();
            }

            catch (Exception e)
            {

                Log.err("Exception caught in GetRules", e.Message);
                return null;
            }
        }
        public override string GetOOO()
        {
            return UserObj.GetOOO();
        }

        public override void Uninit()
        {
             UserObj.Uninit();
        }



    }

    /*class MapiItem : MigrationItem
    {
        public dynamic ItemObj;
        public dynamic ItemID()
        {
            return ItemObj.ItemID;
        }
        public dynamic Type()
        {
            return ItemObj.Type;
        }
        public MapiItem()
        {
             ItemObj= new Exchange.ItemObject();

        }
        public override string[,]  GetDataForItemID(dynamic userobj, dynamic ItemId, dynamic type)
        {
            return ItemObj.GetDataForItemID( userobj,  ItemId,  type);

        }

    }*/
}
