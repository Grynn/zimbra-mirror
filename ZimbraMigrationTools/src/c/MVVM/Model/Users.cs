
namespace MVVM.Model
{
    using System;

    public class Users
    {
        internal Users(string usernameEntered, int currentUserSelection)
        {
            this.UsernameEntered = usernameEntered;
            this.CurrentUserSelection = currentUserSelection;
        }

        public string UsernameEntered
        {
            get; set;
        }

        public int CurrentUserSelection
        {
            get; set;
        }

        public bool MinusEnabled
        {
            get; set;
        }
    }
}
