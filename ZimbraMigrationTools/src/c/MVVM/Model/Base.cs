namespace MVVM.Model
{
    using System;

    public class Base 
    {
        internal Base(string name, string title, string imageName)
        {
            this.Name = name;
            this.Title = title;
            this.ImageName = imageName;
        }

        public string Name
        {
            get; set;
        }

        public string Title
        {
            get; set;
        }

        public string ImageName
        {
            get; set;
        }

    }
}
