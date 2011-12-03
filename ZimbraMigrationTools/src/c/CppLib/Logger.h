#pragma once
#ifdef CPPLIB_EXPORTS
#define CPPLIB_DLLAPI   __declspec(dllexport)
#else
#define CPPLIB_DLLAPI   __declspec(dllimport)
#endif

#include <sstream>

/* Derive from this to prohibit copying */
class CPPLIB_DLLAPI NoCopy
{
protected:
    NoCopy() {}

private:
    NoCopy(const NoCopy &);

    const NoCopy &operator =(const NoCopy &);
};

/* Fast lock */
class CPPLIB_DLLAPI Lock: NoCopy
{
public:
    Lock() { InitializeCriticalSection(&cs); }
    ~Lock() { DeleteCriticalSection(&cs); }

    bool TryEnter() { return TryEnterCriticalSection(&cs) == TRUE; }
    void Enter() { EnterCriticalSection(&cs); }
    void Leave() { LeaveCriticalSection(&cs); }

private:
    CRITICAL_SECTION cs;
};

/* Thread local storage for simple types */
template<class C>
class CPPLIB_DLLAPI TLS: NoCopy
{
public:
    TLS() { key = TlsAlloc(); }
    ~TLS() { TlsFree(key); }

    C operator =(C c) const { set(c); return c; }
    operator bool() const { return TlsGetValue(key) != NULL; }

    C get(void) const { return (C)TlsGetValue(key); }
    void set(const C c) const { TlsSetValue(key, (void *)c); }

protected:
    DWORD key;
};

/* Thread local storage for classes */
template<class C>
class CPPLIB_DLLAPI TLSClass: NoCopy
{
public:
    TLSClass() { key = TlsAlloc(); }
    ~TLSClass() { TlsFree(key); }

    C &operator *(void) const { return get(); }
    C *operator ->(void) const { return &get(); }
    void erase(void) { delete (C *)TlsGetValue(key); TlsSetValue(key, NULL); }
    C &get(void) const
    {
        C *c = (C *)TlsGetValue(key);

        if (!c)
            TlsSetValue(key, c = new C);
        return *c;
    }

    void set(C *c) const { TlsSetValue(key, c); }

protected:
    DWORD key;
};

class bufferstream: public basic_ostream<wchar_t>
{
public:
    bufferstream(): basic_ostream<wchar_t>(&sb), sb(ios::out) {}
    virtual ~bufferstream() {}

    streamsize pcount(void) const { return sb.pcount(); }
    streamsize size(void) const { return sb.pcount(); }

    const wchar_t *str(void) const { return sb.str(); }

    void reset(void)
    {
        if (sb.pcount())
            seekp(0, ios::beg);
    }

private:
    class bufferbuf: public basic_stringbuf<wchar_t>
    {
    public:
        bufferbuf(ios::openmode m): basic_stringbuf<wchar_t>(m) {}

        streamsize pcount(void) const { return pptr() - pbase(); }

        const wchar_t *str(void) const { return pbase(); }
    };

    bufferbuf sb;
};

/*
 * The Log class logs program information at escalating levels patterned after
 * UNIX syslog
 *
 * Log levels include the 8 levels from syslog as well as "trace" that allows
 * better segregation of verbose, low level msgs from normal debug output. A
 * thread-local prefix string can also be set so that logs made during a
 * progressively deeper stack will all start with the same string
 *
 * Each line in the log starts with a configurable string which defaults to
 * a date/time string with millisecond resolution
 *
 * A global "dlog" object allows for the simplest functionality but other
 * objects can be instantiated as well. A kv() member function eases logging
 * attr=val pairs with proper quoting
 *
 * dlog << Log::Warn << T("errno ") << errno << endlog;
 * dlogw(T("errno"), errno);
 * dlogw(Log::kv(T("errno"), errno));
 * DLOGW(T("errno ") << errno);
 */

class CPPLIB_DLLAPI Log
{
public:
    enum Level { None, Err, Warn, Info, Debug };

    template<class C>
    class KV: NoCopy
    {
    public:

        KV(const wchar_t *k, const C &v): key(k), val(v) {}
        KV(const wstring &k, const C &v): key(k.c_str()), val(v) {}

        wostream &print(wostream &os) const
        {
            os << key << '=';
            value(os);
            return os;
        }

    private:
        const wchar_t *key;
        const C &val;

        void value(wostream &os) const
        {
            bufferstream<wchar_t> buf;

            buf << val << '\0';
            quote(os, buf.str());
        }

        static void quote(wostream &os, const wchar_t *s)
        {
            const wchar_t *p;
            static const unsigned char needquote[128] = {
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, // NUL - SI
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, // DLE - US
                1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, // SPACE - /
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, // 0 - ?
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, // @ - O
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, // P - _
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, // ` - o
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, // p - DEL
            };

            if (!s)
                return;
            for (p = s; *p; p++)
            {
                if ((*p > 127) || needquote[(unsigned char)*p])
                {
                    os << '"';
                    for (p = s; *p; p++)
                    {
                        wchar_t c = *p;

                        if (c == '"')
                        {
                            os << '\\' << '"';
                        }
                        else if (c == '\\')
                        {
                            os << '\\' << '\\';
                        }
                        else if (c == '\n')
                        {
                            os << '\\' << 'n';
                        }
                        else if (c == '\r')
                        {
                            os << '\\' << 'r';
                        }
                        else if ((c < ' ') && (c != '\t'))
                        {
                            wchar_t tmp[5];

                            wsprintf(tmp, L"\\%03o", (unsigned)c);
                            os << tmp;
                        }
                        else
                        {
                            os << c;
                        }
                    }
                    os << '"';
                    return;
                }
            }
            os.write(s, p - s);
        }
    };

    Log(const wchar_t *file = L"stdout", Level level = Info);
    ~Log() { close(); }

    bool file(const wchar_t *file) { return ffd.open(file); }

    const wchar_t *file(void) const { return ffd.filename(); }
    const wchar_t *format(void) const { return fmt; }
    void format(const wchar_t *s);

    Level level(void) const { return lvl; }
    void level(Level l) { lvl = l; }
    void level(const wchar_t *l) { level(str2enum(l)); }

    const wchar_t *prefix(void) const { return tls->prefix.c_str(); }
    void prefix(const wchar_t *p) { tls->prefix = p ? p : L""; }

    bool close(void) { return ffd.close(); }
    Log &endlog(void)
    {
        Tlsdata &tlsd(*tls);

        if (tlsd.clvl != None)
            endlog(tlsd, tlsd.clvl);
        return *this;
    }

    void logv(Level l, ...);

    template<class C> Log &operator <<(const C &c)
    {
        Tlsdata &tlsd(*tls);

        if (tlsd.clvl != None)
        {
            if (tlsd.space)
            {
                tlsd.space = false;
                tlsd.strm << ' ';
            }
            tlsd.strm << c;
        }
        return *this;
    }

    Log &operator <<(const Level l)
    {
        if (l <= lvl)
            tls->clvl = l;
        return *this;
    }

    template<class C>
    Log &operator <<(const KV<C> &kv)
    {
        Tlsdata &tlsd(*tls);

        if (tlsd.clvl != None)
        {
            if (tlsd.strm.size())
                tlsd.strm << ' ';
            kv.print(tlsd.strm);
            tlsd.space = true;
        }
        return *this;
    }

#define _func_(n, l) \
    template<class C> void n(const C &c) { log(l, c); } \
    template<class C, class D> void n(const C &c, const D &d) { log(l, c, d); } \
    template<class C, class D, class E> void n(const C &c, const D &d, const E \
        &e) { log(l, c, d, e); } \
    template<class C, class D, class E, class F> void n(const C &c, const D &d, const E &e, \
        const F &f) { log(l, c, d, e, f); } \
    template<class C, class D, class E, class F, class G> void n(const C &c, const D &d, const \
        E &e, const F &f, const G &g) { log(l, c, d, e, f, g); }

    _func_(err, Err);
    _func_(warn, Warn);
    _func_(info, Info);
    _func_(debug, Debug);

#undef _func_
#define _log_(s) \
    if (l <= lvl) { \
        Tlsdata &tlsd(*tls); \
 \
        tlsd.clvl = l; \
        tlsd.strm << s; \
        endlog(tlsd, l); \
    }

    template<class C> void log(Level l, const C &c) { _log_(c); }
    template<class C, class D> void log(Level l, const C &c, const D &d)
    {
        _log_(c << ' ' << d);
    }

    template<class C, class D, class E> void log(Level l, const C &c, const D &d, const E &e)
    {
        _log_(c << ' ' << d << ' ' << e);
    }

    template<class C, class D, class E, class F> void log(Level l, const C &c, const D &d, const
        E &e, const F &f)
    {
        _log_(c << ' ' << d << ' ' << e << ' ' << f);
    }

    template<class C, class D, class E, class F, class G> void log(Level l, const C &c, const
        D &d, const E &e, const F &f, const G &g)
    {
        _log_(c << ' ' << d << ' ' << e << ' ' << f << ' ' << g);
    }

#undef _log_

    template<class C> static const KV<C> kv(const wchar_t *key, const C &val)
    {
        return KV<C>(key, val);
    }

    template<class C> static const KV<C> kv(const wstring &key, const C &val)
    {
        return KV<C>(key, val);
    }

    static Level str2enum(const wchar_t *lvl);

private:
    class CPPLIB_DLLAPI LogFile
    {
    public:
        LogFile(const wchar_t *dfile): fd(INVALID_HANDLE_VALUE), file(NULL) { open(dfile); }
        ~LogFile() { close(); free(file); }

        const wchar_t *filename(void) const { return file; }

        bool close(void);

        bool open(const wchar_t *file);
        bool write(const wchar_t *buf, unsigned sz);

    private:
        HANDLE fd;
        wchar_t *file;
    };

    struct Tlsdata
    {
        Level clvl;
        wstring prefix;
        bool space;
        wstring strbuf;
        bufferstream strm;

        Tlsdata(): clvl(None), space(false) {}
    };

    Lock lck;

    TLSClass<Tlsdata> tls;

    LogFile ffd;
    wchar_t *fmt;
    wchar_t *last_fmt;
    time_t last_sec;
    Level lvl;
    int upos;
    static const wchar_t *const LevelStr[];
    static const wchar_t *const LevelStr2[];

    void endlog(Tlsdata &tlsd, Level lvl);
};

template<> inline void Log::KV<bool>::value(wostream &os) const
{
    os << (val ? 't' : 'f');
}

template<> inline void Log::KV<const wchar_t *>::value(wostream &os) const
{
    quote(os, val);
}

template<> inline void Log::KV<wchar_t *>::value(wostream &os) const
{
    quote(os, val);
}

template<> inline void Log::KV<wstring>::value(wostream &os) const
{
    quote(os, val.c_str());
}

template<class C> inline wostream &operator <<(wostream &os, const Log::KV<C> &kv)
{
    return kv.print(os);
}

inline Log &operator <<(Log &l, Log & (*manip)(Log &)) { return manip(l); }
inline Log &endlog(Log &l) { return l.endlog(); }

extern Log glog;
extern CPPLIB_DLLAPI TLSClass<Log> tlog;

extern "C" CPPLIB_DLLAPI void log_open(const wchar_t *file);

#define dlog                    tlog.get()

#define DLOGL(lvl, args)        { \
                                    if (lvl <= dlog.level()) \
                                        dlog << lvl << args << endlog; \
                                }

#define DLOGE(args)             DLOGL(Log::Err, args);
#define DLOGW(args)             DLOGL(Log::Warn, args);
#define DLOGI(args)             DLOGL(Log::Info, args);
#define DLOGD(args)             DLOGL(Log::Debug, args);

#define dlogl(lvl, ...)         { \
                                    if (lvl <= dlog.level()) \
                                        dlog.log(lvl, __VA_ARGS__); \
                                }
#define dloge(...)              dlogl(Log::Err, __VA_ARGS__);
#define dlogw(...)              dlogl(Log::Warn, __VA_ARGS__);
#define dlogi(...)              dlogl(Log::Info, __VA_ARGS__);
#define dlogd(...)              dlogl(Log::Debug, __VA_ARGS__);
