package com.zimbra.cs.im.xp.tok;

/**
 * Caches conversion of byte subarrays into Strings.
 * @version $Revision: 1.3 $ $Date: 1998/02/17 04:24:24 $
 */
public class StringConversionCache {

    private static final int DEFAULT_CACHE_SIZE = 4001;
    private static final int CONVERSION_BUF_SIZE = 64;

    private static class Bucket {
        /* JDK 1.1 javac incorrectly fails to allows access to private members
       of inner class from enclosing class. */
        /* private */ byte[] bytes;
        /* private */ String string;
        /* private */ Bucket nextMru;
        /* private */ Bucket prevMru;
        /* private */ Bucket nextBucket;
        /* private */ Bucket prevBucket;
        /* private */ final boolean matches(byte[] buf, int start, int end) {
            if (end - start != bytes.length)
                return false;
            for (int i = 0; i < bytes.length; i++)
                if (bytes[i] != buf[start++])
                    return false;
            return true;
        }
    }

    /* JDK 1.1 javac can't handle blank finals correcltly when there are
     inner classes. */
    private /* final */ Bucket[] table;
    private /* final */ Bucket mru = new Bucket();
    private char[] conversionBuf = new char[CONVERSION_BUF_SIZE];
    private Encoding enc;
    private /* final */ int minBPC;
    private int cacheFree;
    private static final double LOAD_FACTOR = 0.7;

    /**
     * Create a cache of the specified size
     * for converting byte subarrays in the specified encoding
     * into Strings.
     */
    public StringConversionCache(Encoding enc, int cacheSize) {
        this.enc = enc;
        minBPC = enc.getMinBytesPerChar();
        table = new Bucket[cacheSize];
        cacheFree = (int)(table.length * LOAD_FACTOR);
        mru.nextMru = mru;
        mru.prevMru = mru;
    }

    /**
     * Create a cache of the default size for converting byte subarrays
     * in the specified encoding into Strings.
     */
    public StringConversionCache(Encoding enc) {
        this(enc, DEFAULT_CACHE_SIZE);
    }

    /**
     * Changes the encoding for the cache.
     * This cannot be called after any calls to <code>convert</code>
     * have been made.
     */
    public void setEncoding(Encoding enc) {
        if (cacheFree != (int)(table.length * LOAD_FACTOR))
            throw new IllegalStateException("cache already used");
        this.enc = enc;
        if (minBPC != enc.getMinBytesPerChar())
            throw new IllegalStateException("change to incompatible encoding");
    }

    /**
     * Convert a byte subarray into a String.
     * If <code>permanent</code> is true, then this conversion will
     * be kept in the cache in preference to any non-permanent conversions.
     */
    public String convert(byte[] buf, int start, int end, boolean permanent) {
        int i = hash(buf, start, end) % table.length;
        Bucket bucket = table[i];

        if (bucket != null) {
            for (Bucket p = bucket.nextBucket;
            p != bucket;
            p = p.nextBucket) {
                if (p.matches(buf, start, end)) {
                    if (p.nextMru != null) {
                        unlinkMru(p);
                        if (permanent)
                            p.nextMru = null;
                        else
                            linkMru(mru, p);
                    }
                    return p.string;
                }
            }
        }
        else {
            bucket = new Bucket();
            bucket.nextBucket = bucket.prevBucket = bucket;
            table[i] = bucket;
        }
        Bucket tem;
        if (cacheFree <= 0) {
            if (mru.nextMru == mru) {
                for (int j = 0; j < table.length; j++) {
                    Bucket h = table[j];
                    if (h != null) {
                        for (Bucket p = h.nextBucket;
                        p != h;
                        p = p.nextBucket) {
                            linkMru(mru, p);
                        }
                        h.nextBucket = h.prevBucket = h;
                    }
                }
            }
            tem = mru.prevMru;
            unlinkMru(tem);
            tem.prevBucket.nextBucket = tem.nextBucket;
            tem.nextBucket.prevBucket = tem.prevBucket;
            if (permanent)
                tem.nextMru = null;
            else
                linkMru(mru, tem);
        }
        else {
            --cacheFree;
            tem = new Bucket();
            if (!permanent)
                linkMru(mru, tem);
        }

        if (end - start > conversionBuf.length * minBPC)
            conversionBuf = new char[(end - start)/minBPC];

        tem.string = new String(conversionBuf,
                    0,
                    enc.convert(buf, start, end, conversionBuf, 0));

        byte[] bytes = new byte[end - start];
        System.arraycopy(buf, start, bytes, 0, bytes.length);
        tem.bytes = bytes;

        tem.nextBucket = bucket.nextBucket;
        tem.nextBucket.prevBucket = tem;
        tem.prevBucket = bucket;
        bucket.nextBucket = tem;
        return tem.string;
    }

    private static final int hash(byte buf[], int start, int end) {
        int h = 0;
        while (start != end)
            h += (h << 5) + (buf[start++] & 0xFF);
        return h & 0x7FFFFFFF;
    }

    static private final void linkMru(Bucket after, Bucket s) {
        s.nextMru = after.nextMru;
        s.nextMru.prevMru = s;
        s.prevMru = after;
        after.nextMru = s;
    }

    static private final void unlinkMru(Bucket s) {
        s.prevMru.nextMru = s.nextMru;
        s.nextMru.prevMru = s.prevMru;
    }

}
