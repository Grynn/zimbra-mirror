#include "common.h"
#include <stdarg.h>
#include <time.h>
#include "Logger.h"

typedef unsigned __int64 usec_t;

static const wchar_t *USubst = L"\001\001";
const wchar_t *const Log::LevelStr[] = {
    L"none", L"err", L"warn", L"info", L"debg"
};
const wchar_t *const Log::LevelStr2[] = {
    L"nothing", L"error", L"warning", L"information", L"debug"
};
Log glog(L"stdout", Log::Info);
CPPLIB_DLLAPI TLSClass<Log> tlog;

#define EPOCH_BIAS 116444736000000000i64

static usec_t microtime(void)
{
    union
    {
        usec_t u64;
        FILETIME ft;
    }

    ft;

    usec_t usec;

    GetSystemTimeAsFileTime(&ft.ft);
    usec = (ft.u64 - EPOCH_BIAS) / 10;
    return (usec / 1000000i64) * (usec_t)1000000 + (usec % 1000000i64);
}

bool Log::LogFile::close(void)
{
    if (fd == INVALID_HANDLE_VALUE)
        return false;
    CloseHandle(fd);
    fd = INVALID_HANDLE_VALUE;
    return true;
}

bool Log::LogFile::open(const wchar_t *f)
{
    if (f && file && !wcscmp(file, f))
        return true;
    close();
    if (file)
        free(file);
    file = wcsdup(f);
    if (!wcscmp(file, L"stdout") || !wcscmp(file, L"cout"))
    {
        fd = GetStdHandle(STD_OUTPUT_HANDLE);
    }
    else if (!wcscmp(file, L"stderr") || !wcscmp(file, L"cerr"))
    {
        fd = GetStdHandle(STD_ERROR_HANDLE);
    }
    else
    {
        fd = CreateFile(file, FILE_APPEND_DATA | GENERIC_WRITE, FILE_SHARE_READ, NULL,
            OPEN_ALWAYS, FILE_ATTRIBUTE_NORMAL | FILE_FLAG_SEQUENTIAL_SCAN, 0);
        SetFilePointer(fd, 0, NULL, FILE_END);
    }
    if (fd == INVALID_HANDLE_VALUE)
    {
        wcerr << L"unable to open log " << file << endl;
        return false;
    }
    return true;
}

bool Log::LogFile::write(const wchar_t *buf, unsigned chars)
{
    DWORD out;
    char sbuf[1024];
    int sz;

    if ((sz = WideCharToMultiByte(CP_UTF8, 0, buf, chars, sbuf, sizeof (sbuf), NULL, NULL)) > 0)
    {
        WriteFile(fd, sbuf, sz, &out, NULL);
    }
    else
    {
        sz = WideCharToMultiByte(CP_UTF8, 0, buf, chars, NULL, 0, NULL, NULL);

        char *s = new char[sz];

        WideCharToMultiByte(CP_UTF8, 0, buf, chars, s, sz, NULL, NULL);
        WriteFile(fd, buf, sz, &out, NULL);
        delete[] buf;
    }
    return out == chars;
}

Log::Log(const wchar_t *file, Level level): ffd(file), fmt(NULL), last_fmt(NULL), last_sec(0),
    lvl(level), upos(0)
{
    format(L"[%Y-%m-%d %H:%M:%S.%#]");
}

void Log::endlog(Tlsdata &tlsd, Level clvl)
{
    size_t tmlen;
    time_t now_sec;
    usec_t now_usec;
    wstring &strbuf(tlsd.strbuf);
    size_t sz = (size_t)tlsd.strm.size();
    wchar_t tmp[8];

    lck.Enter();
    now_usec = microtime();
    now_sec = (unsigned)(now_usec / 1000000);
    if (now_sec != last_sec)
    {
        wchar_t *p;
        wchar_t tbuf[128];
        struct tm *tm;

        tm = localtime(&now_sec);
        wcsftime(tbuf, sizeof (tbuf) / sizeof (wchar_t), fmt, tm);
        if (last_fmt != NULL)
            free(last_fmt);
        last_fmt = wcsdup(tbuf);
        last_sec = now_sec;
        p = wcsstr(last_fmt, USubst);
        upos = p ? p - last_fmt : -1;
    }
    strbuf = last_fmt;
    if (upos != -1)
    {
        wsprintf(tmp, L"%06u", (unsigned)(now_usec % 1000000));
        strbuf.replace(upos, 2, tmp);
    }
    if (!strbuf.empty())
        strbuf += ' ';
    tmlen = strbuf.size();
    strbuf += LevelStr[clvl];
    if (clvl == Err)
        strbuf += ' ';
    strbuf += ' ';
    if (!tlsd.prefix.empty())
    {
        strbuf += tlsd.prefix;
        strbuf += ' ';
    }
    for (const wchar_t *p = tlsd.strm.str(); sz--; p++)
    {
        if ((*p < ' ') && (*p != '\t'))
        {
            if (*p == '\n')
            {
                strbuf += L"\\n";
            }
            else if (*p == '\r')
            {
                strbuf += L"\\r";
            }
            else
            {
                wsprintf(tmp, L"\\%03o", *p);
                strbuf += tmp;
            }
        }
        else
        {
            strbuf += *p;
        }
    }
    strbuf += '\n';
    ffd.write(strbuf.c_str(), strbuf.size());
    lck.Leave();
    tlsd.clvl = None;
    tlsd.space = false;
    tlsd.strm.reset();
}

void Log::format(const wchar_t *f)
{
    wstring s(f);
    wstring::size_type pos;

    last_sec = 0;
    if ((pos = s.find(L"%#")) != s.npos)
        s.replace(pos, 2, USubst);
    if (fmt != NULL)
        free(fmt);
    fmt = wcsdup(s.c_str());
}

void Log::logv(Level l, ...)
{
    if (l > lvl)
        return;

    bool first = true;
    const wchar_t *p;
    Tlsdata &tlsd(*tls);
    va_list vl;

    va_start(vl, l);
    while ((p = va_arg(vl, const wchar_t *)) != NULL)
    {
        if (first)
            first = false;
        else
            tlsd.strm << ' ';
        tlsd.strm << p;
    }
    va_end(vl);
    endlog(tlsd, l);
}

Log::Level Log::str2enum(const wchar_t *l)
{
    for (int i = 0; i < (int)(sizeof (LevelStr) / sizeof (const wchar_t *)); i++)
    {
        if (!wcsicmp(l, LevelStr[i]) || !wcsicmp(l, LevelStr2[i]))
            return (Level)i;
    }
    return None;
}

extern "C" {
CPPLIB_DLLAPI void log_init(const wchar_t *file, Log::Level level)
{
    glog.file(file);
    glog.level(level);
}

CPPLIB_DLLAPI void log_open(const wchar_t *file)
{
    if (file)
    {
        if (wcscmp(tlog->file(), file))
        {
            if (&tlog.get() != &glog)
                tlog.erase();
            tlog.set(new Log(file, glog.level()));
        }
    }
    else
    {
        if (&tlog.get() != &glog)
            tlog.erase();
        tlog.set(&glog);
    }
}

CPPLIB_DLLAPI void log_prefix(const wchar_t *prefix)
{
    tlog->prefix(prefix);
}

CPPLIB_DLLAPI void log_print(Log::Level level, const wchar_t *str)
{
    tlog->log(level, str);
}

}
