package com.zimbra.cs.im.xp.util;

/**
 * A more efficient version of <code>java.util.Hashtable</code>
 * It is not synchronized.  It only performs allocation when
 * the hash table is resized.
 */

public class Hashtable extends java.util.Dictionary {
  // The first half of table contains the keys, the second half the values.
  // The value for a key at index i is at index i + halfTableLength
  private Object[] table;
  private int halfTableLength;
  private int used;
  private int usedLimit;
  private static final int INIT_SIZE = 16;
  private static final float LOAD_FACTOR = 0.5f;
  
  private final int nextIndex(int i) {
    return i == 0 ? halfTableLength - 1 : i - 1;
  }

  // It might be a good idea to multiple the hashCode by a large prime.
  private final int firstIndex(Object key) {
    return key.hashCode() & (halfTableLength - 1);
  }

  public Hashtable() { }

  /**
   * Creates a hash table with the specified initial capacity.
   */
  public Hashtable(int n) {
    n = (int)((n + 1)/LOAD_FACTOR);
    halfTableLength = 1;
    while (halfTableLength < n)
      halfTableLength <<= 1;
    table = new Object[halfTableLength << 1];
    usedLimit = (int)(n * LOAD_FACTOR);
  }
  
  public final int size() {
    return used;
  }

  public final boolean isEmpty() {
    return used == 0;
  }

  public final Object get(Object key) {
    if (used != 0) {
      for (int i = firstIndex(key); table[i] != null; i = nextIndex(i))
	if (table[i].equals(key))
	  return table[i | halfTableLength];
    }
    return null;
  }

  public final Object put(Object key, Object value) {
    if (value == null)
      throw new NullPointerException();
    int h;
    if (table == null) {
      table = new Object[INIT_SIZE];
      halfTableLength = INIT_SIZE >> 1;
      usedLimit = (int)((INIT_SIZE >> 1) * LOAD_FACTOR);
      h = firstIndex(key);
    }
    else {
      for (h = firstIndex(key); table[h] != null; h = nextIndex(h))
	if (key.equals(table[h])) {
	  h |= halfTableLength;
	  Object tem = table[h];
	  table[h] = value;
	  return tem;
	}
    }
    if (used >= usedLimit) {
      // rehash
      halfTableLength = table.length;
      usedLimit = (int)(halfTableLength * LOAD_FACTOR);
      Object[] oldTable = table;
      table = new Object[halfTableLength << 1];
      for (int i = oldTable.length >> 1; i > 0;) {
	--i;
	if (oldTable[i] != null) {
	  int j;
	  for (j = firstIndex(oldTable[i]); table[j] != null; j = nextIndex(j))
	    ;
	  table[j] = oldTable[i];
	  // copy the value
	  table[j | halfTableLength] = oldTable[i + (oldTable.length >> 1)];
	}
      }
      for (h = firstIndex(key); table[h] != null; h = nextIndex(h))
	;
    }
    used++;
    table[h] = key;
    table[h | halfTableLength] = value;
    return null;
  }

  private static class Enumerator implements java.util.Enumeration {
    private final Object[] table;
    private final int add;
    private int i;

    Enumerator(Object[] table, int add) {
      this.table = table;
      this.add = add;
      if (table == null)
	i = -1;
      else {
	i = (table.length >> 1);
	while (--i >= 0 && table[i] == null)
	  ;
      }
    }

    public boolean hasMoreElements() {
      return i >= 0;
    }

    public Object nextElement() {
      if (i < 0)
	throw new java.util.NoSuchElementException();
      Object tem = table[i + add];
      while (--i >= 0 && table[i] == null)
	;
      return tem;
    }
  }

  public final java.util.Enumeration keys() {
    return new Enumerator(table, 0);
  }

  public final java.util.Enumeration elements() {
    return new Enumerator(table, halfTableLength);
  }

  /**
   * Removes the object with the specified key from the table.
   * Returns the object removed or null if there was no such object
   * in the table.
   */
  public final Object remove(Object key) {
    if (used > 0) {
      for (int i = firstIndex(key); table[i] != null; i = nextIndex(i))
	if (table[i].equals(key)) {
	  Object obj = table[i];
	  do {
	    table[i] = null;
	    table[i | halfTableLength] = null;
	    int j = i;
	    int r;
	    do {
	      i = nextIndex(i);
	      if (table[i] == null)
		break;
	      r = firstIndex(table[i]);
	    } while ((i <= r && r < j) || (r < j && j < i) || (j < i && i <= r));
	    table[j] = table[i];
	    table[j | halfTableLength] = table[i | halfTableLength];
	  } while (table[i] != null);
	  --used;
	  return obj;
	}
    }
    return null;
  }

  /**
   * Removes all objects from the hash table, so that the hash table
   * becomes empty.
   */
  public final void clear() {
    int i = halfTableLength;
    while (--i >= 0) {
      table[i] = null;
      table[i | halfTableLength] = null;
    }
    used = 0;
  }
}
