using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows.Threading;
using System.Collections.Specialized;

namespace Misc
{
internal class CollectionChangedWrapperEventData
{
    public Dispatcher Dispatcher
    {
        get;
        set;
    }

    public Action<NotifyCollectionChangedEventArgs> Action
    {
        get;
        set;
    }
        
    public CollectionChangedWrapperEventData(Dispatcher dispatcher, Action<NotifyCollectionChangedEventArgs> action)
    {
        Dispatcher = dispatcher;
        Action = action;
    }
}
}
