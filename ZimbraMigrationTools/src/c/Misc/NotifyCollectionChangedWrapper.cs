using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Collections;
using System.Collections.Specialized;
using System.ComponentModel;
using System.Collections.ObjectModel;
using System.Windows;
using System.Windows.Threading;
using System.Threading;

namespace Misc
{
public class NotifyCollectionChangedWrapper<T> : IList<T>, IList, INotifyCollectionChanged, INotifyPropertyChanged, IWeakEventListener
{
    private INotifyCollectionChanged internalOC;
    private IList<T> AsIListT;
    private IList AsIList;
    private ICollection AsICollection;
    private ICollection<T> AsICollectionT;
    private IEnumerable AsIEnumerable;
    private IEnumerable<T> AsIEnumerableT;

    public NotifyCollectionChangedWrapper(INotifyCollectionChanged oc)
    {
        internalOC = oc;
        collectionChangedHandlers = new Dictionary<NotifyCollectionChangedEventHandler, CollectionChangedWrapperEventData>();

        AsIListT = internalOC as IList<T>;
        AsIList = internalOC as IList;
        AsICollection = internalOC as ICollection;
        AsICollectionT = internalOC as ICollection<T>;
        AsIEnumerable = internalOC as IEnumerable;
        AsIEnumerableT = internalOC as IEnumerable<T>;

        CollectionChangedEventManager.AddListener(internalOC, this);
    }

    // Handlers
    void internalOC_CollectionChanged(object sender, NotifyCollectionChangedEventArgs e)
    {
        KeyValuePair<NotifyCollectionChangedEventHandler, CollectionChangedWrapperEventData>[] handlers = collectionChangedHandlers.ToArray();
        if (handlers.Length > 0)
        {
            foreach (KeyValuePair<NotifyCollectionChangedEventHandler, CollectionChangedWrapperEventData> kvp in handlers)
            {
                if (kvp.Value.Dispatcher == null)
                {
                    kvp.Value.Action(e);
                }
                else
                {
                    kvp.Value.Dispatcher.Invoke(kvp.Value.Action, DispatcherPriority.DataBind, e);
                }
            }
        }
    }
    //

    // IList<T> Members    
    public int IndexOf(T item)
    {
        return AsIListT.IndexOf(item);
    }

    public void Insert(int index, T item)
    {
        AsIListT.Insert(index, item);
    }

    public void RemoveAt(int index)
    {
        AsIListT.RemoveAt(index);
    }

    public T this[int index]
    {
        get
        {
            return AsIListT[index];
        }
        set
        {
            AsIListT[index] = value;
        }
    }

    // ICollection<T> Members
    public void Add(T item)
    {
        AsICollectionT.Add(item);
    }

    public void Clear()
    {
        AsICollectionT.Clear();
    }

    public bool Contains(T item)
    {
        return AsICollectionT.Contains(item);
    }

    public void CopyTo(T[] array, int arrayIndex)
    {
        AsICollectionT.CopyTo(array, arrayIndex);
    }

    public int Count
    {
        get { return AsICollectionT.Count; }
    }

    public bool IsReadOnly
    {
        get { return AsICollectionT.IsReadOnly; }
    }

    public bool Remove(T item)
    {
        return AsICollectionT.Remove(item);
    }

    //IEnumerable<T> Members    
    public IEnumerator<T> GetEnumerator()
        {
            return AsIEnumerableT.GetEnumerator();
        }

    //IEnumerable Members
    IEnumerator IEnumerable.GetEnumerator()
    {
        return AsIEnumerable.GetEnumerator();
    }
    //

    //IList Members    
    public int Add(object value)
    {
        return AsIList.Add(value);
    }

    public bool Contains(object value)
    {
        return AsIList.Contains(value);
    }

    public int IndexOf(object value)
    {
        return AsIList.IndexOf(value);
    }

    public void Insert(int index, object value)
    {
        AsIList.Insert(index, value);
    }

    public bool IsFixedSize
    {
        get { return AsIList.IsFixedSize; }
    }

    public void Remove(object value)
    {
        AsIList.Remove(value);
    }

    object IList.this[int index]
    {
        get
        {
            return AsIList[index];
        }
        set
        {
            AsIList[index] = value;
        }
    }//

    //ICollection Members    
    public void CopyTo(Array array, int index)
    {
        AsICollection.CopyTo(array, index);
    }

    public bool IsSynchronized
    {
        get { return AsICollection.IsSynchronized; }
    }

    public object SyncRoot
    {
        get { return AsICollection.SyncRoot; }
    }
    //

    //INotifyCollectionChanged Members    
    private Dictionary<NotifyCollectionChangedEventHandler, CollectionChangedWrapperEventData> collectionChangedHandlers;
    public event NotifyCollectionChangedEventHandler CollectionChanged
    {
        add
        {
            //Dispatcher dispatcher = Dispatcher.CurrentDispatcher; // should always work
            Dispatcher dispatcher = Dispatcher.FromThread(Thread.CurrentThread); // experimental (can return null)...
            collectionChangedHandlers.Add(value, new CollectionChangedWrapperEventData(dispatcher, new Action<NotifyCollectionChangedEventArgs>((args) => value(this, args))));
        }
        remove
        {
            collectionChangedHandlers.Remove(value);
        }
    }
    //

    #pragma warning disable 67
    //INotifyPropertyChanged Members    
    public event PropertyChangedEventHandler PropertyChanged;
    //
    #pragma warning restore 67

    //IWeakEventListener Members    
    public bool ReceiveWeakEvent(Type managerType, object sender, EventArgs e)
    {
        internalOC_CollectionChanged(sender, e as NotifyCollectionChangedEventArgs);
        return true;
    }
    //
}
}
