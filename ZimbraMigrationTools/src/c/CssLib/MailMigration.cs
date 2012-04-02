using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace CssLib
{
   public abstract class MailMigration
    {

       public abstract  string GlobalInit(string Target, string AdminUser, string AdminPassword);
       public abstract string GlobalUninit();

       public abstract string GetProfilelist(out object var);
       public abstract string SelectExchangeUsers(out object var);
    }

   public abstract class MigrationUser
    {

       public abstract string Init(string host, string AccountID, string accountName);

       public abstract dynamic[] GetFolders();

       public abstract dynamic[] GetItemsForFolder(dynamic folderobject, double Date);

       public abstract string[,]  GetRules();
       public abstract string GetOOO();

       public abstract void Uninit();

    }

   /*public  abstract class MigrationItem
    {

       public abstract string[,] GetDataForItemID(dynamic userobj, dynamic ItemId, dynamic type);
    }*/

   
}
