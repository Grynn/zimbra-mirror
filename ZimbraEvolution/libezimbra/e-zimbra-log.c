/* -*- Mode: C; tab-width: 4; indent-tabs-mode: t; c-basic-offset: 4 -*- */
/* 
 * This program is free software; you can redistribute it and/or 
 * modify it under the terms of version 2 of the GNU Lesser General Public 
 * License as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 * 
 * Authors: Scott Herscher <scott.herscher@zimbra.com>
 * 
 * Copyright (C) 2006 Zimbra, Inc.
 * 
 */

#ifdef HAVE_CONFIG_H
#include <config.h>
#endif
#include "e-zimbra-log.h"
#include <glib.h>
#include <stdarg.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <stddef.h>
#include <stdint.h>
#include <limits.h>
#include <stdio.h>
#include <ctype.h>

static char						g_component[128]	= "howl";
static ZimbraLogMessageHandler	g_handler			= NULL;

#if defined(NDEBUG)

static int	g_logLevel = kZimbraLogError;

#else

static int	g_logLevel = kZimbraLogDebug;

#endif



// Macros for custom printing

#define ADD_C(x) do { char addc = (x);                                  \
                      if (++ret >= sz) {} else *buf++ = addc; } while (0)

#define ADD_PAD_C(x, y) do { while (y) { ADD_C(x); --(y); } } while (0)

#define ADD_NUM(num, base, chrs) do {                                   \
      char num_buf[sizeof(uintmax_t) * CHAR_BIT];                       \
      char *ptr = num_buf;                                              \
                                                                        \
      ADD_PAD_C('0', zeros);                                            \
      num_ret = ret;                                                    \
                                                                        \
      do                                                                \
      {                                                                 \
        unsigned int chr_offset = (num % base);                         \
                                                                        \
        num /= base;                                                    \
                                                                        \
        *ptr++ = chrs[chr_offset];                                      \
      } while (num);                                                    \
                                                                        \
      while (ptr != num_buf) ADD_C(*--ptr);                             \
                                                                        \
    } while (0)

/* parse data ... */
#define PARSE_FMT_NUM10(x)                                              \
    while ((*fmt >= '0') && (*fmt <= '9')) (x) = (x) * 10 + (*fmt++ - '0')


/* limited decrement */
#define DEC__VAL(x, y) do {                    \
      if ((unsigned int)(y) > (unsigned int)(x)) \
        (y) -= (x);                              \
      else                                       \
        (y)  = 0;                                \
    } while (0)
#define DEC_WIDTH(x) DEC__VAL(x, wid)
#define DEC_PREC(x)  DEC__VAL(x, prec)

/* all the helper macro function to format signed and unsigned numbers ... */
#define FMT_NUM_BEG()                                                   \
    unsigned int zeros   = 0;                                           \
    unsigned int espcs   = 0;                                           \
    unsigned int num_ret = ret;                                         \
                                                                        \
    if (prec < 0) prec = 0; else flg_zero = 0;                          \
                                                                        \
    if (wid || prec) {                                                  \
    char *       beg_buf  = buf;                                        \
    unsigned int beg_ret  = ret;                                        \
    unsigned int bspcs = 0
#define FMT_NUM_MID()                                                   \
    DEC_PREC(ret - num_ret);                                            \
    DEC_WIDTH((ret - beg_ret) + prec);                                  \
                                                                        \
    zeros = prec;                                                       \
                                                                        \
    if (0) {}                                                           \
    else if (flg_minus) espcs = wid;                                    \
    else if (flg_zero)  zeros = wid;                                    \
    else                bspcs = wid;                                    \
                                                                        \
    buf  = beg_buf;                                                     \
    ret  = beg_ret;                                                     \
                                                                        \
    ADD_PAD_C(' ', bspcs);                                              \
    }                                                                   \
    arg = tmp_arg
#define FMT_NUM_END()                                                   \
    ADD_PAD_C(' ', espcs)

#define FMT_NUM__S(tmp_arg, arg, base, chrs)                            \
      if (tmp_arg < 0) { ADD_C('-'); arg = -arg; }                      \
      else if (flg_plus) ADD_C('+');                                    \
      else if (flg_spac) ADD_C(' ');                                    \
                                                                        \
    ADD_NUM(arg, base, chrs)
#define FMT_NUM_S(cmd, dsT, pT, duT, base, chrs)                        \
    else if (MatchCommand(&fmt, cmd)) do {                                 \
      dsT tmp_arg = va_arg(ap, pT);                                     \
      duT arg = tmp_arg;                                                \
                                                                        \
      FMT_NUM_BEG();                                                    \
      FMT_NUM__S(tmp_arg, arg, base, chrs);                             \
      FMT_NUM_MID();                                                    \
      FMT_NUM__S(tmp_arg, arg, base, chrs);                             \
      FMT_NUM_END();                                                    \
    } while (0)

#define FMT_NUM__U(arg, base, chrs, h1, h2)                             \
      if (flg_hash && h1) ADD_C(h1);                                    \
      if (flg_hash && h2) ADD_C(h2);                                    \
                                                                        \
      ADD_NUM(arg, base, chrs)
#define FMT_NUM_U(cmd, duT, pT, base, chrs, h1, h2)                     \
    else if (MatchCommand(&fmt, cmd)) do {                                 \
      duT tmp_arg = ( duT ) va_arg(ap, pT);                                     \
      duT arg = ( duT ) tmp_arg;                                                \
                                                                        \
      FMT_NUM_BEG();                                                    \
      FMT_NUM__U(arg, base, chrs, h1, h2);                              \
      FMT_NUM_MID();                                                    \
      FMT_NUM__U(arg, base, chrs, h1, h2);                              \
      FMT_NUM_END();                                                    \
    } while (0)

/* main functions for numbers, these do all the different sizes for each number
 * Ie. %hhd, %hd, %d, %ld, %lld, %zd, %td, and %jd */
#define FMT_ALL_NUM_S(cmd, b, c)                                           \
    FMT_NUM_S("hh" cmd, signed char,       int,      unsigned char, b, c); \
    FMT_NUM_S("h"  cmd,       short,       int,     unsigned short, b, c); \
    FMT_NUM_S(     cmd,         int,       int,       unsigned int, b, c); \
    FMT_NUM_S("l"  cmd,        long,      long,      unsigned long, b, c); \
    FMT_NUM_S("z"  cmd,     ssize_t,   ssize_t,             size_t, b, c); \
    FMT_NUM_S("t"  cmd,   ptrdiff_t, ptrdiff_t,          uintmax_t, b, c); \
    FMT_NUM_S("j"  cmd,    intmax_t,  intmax_t,          uintmax_t, b, c)
#define FMT_ALL_NUM_U(cmd, b, c, h1, h2)                                       \
    FMT_NUM_U("hh" cmd,      unsigned char,       unsigned int, b, c, h1, h2); \
    FMT_NUM_U("h"  cmd,     unsigned short,       unsigned int, b, c, h1, h2); \
    FMT_NUM_U(     cmd,       unsigned int,       unsigned int, b, c, h1, h2); \
    FMT_NUM_U("l"  cmd,      unsigned long,      unsigned long, b, c, h1, h2); \
    FMT_NUM_U("ll" cmd, unsigned long long, unsigned long long, b, c, h1, h2); \
    FMT_NUM_U("z"  cmd,             size_t,            ssize_t, b, c, h1, h2); \
    FMT_NUM_U("t"  cmd,          uintmax_t,          ptrdiff_t, b, c, h1, h2); \
    FMT_NUM_U("j"  cmd,          uintmax_t,          uintmax_t, b, c, h1, h2)

/* %n etc. is different to the return value, in that it's only the amount of
 *  data that has been "output" */
#define FMT_RET_N(strT, T) do {                 \
      if (MatchCommand(&fmt, strT "n"))            \
      {                                         \
        unsigned int msz = !sz ? 0 : (sz - 1);  \
                                                \
        T *tmp_ret = va_arg(ap, T *);           \
        *tmp_ret = ((ret > msz) ? msz : ret);   \
        ++fmt;                                  \
        continue;                               \
      } } while (0)


// ----------------------------------------------------------
// Static declarations
// ----------------------------------------------------------

static unsigned int
ZimbraVSNPrintF
	(
	char			*	buf,
	unsigned int	sz,
	const char	*	fmt,
	va_list 			ap
	);


static int
MatchCommand
	(
	const char	**	passed_fmt,
	const char	*	cmd
	);


// ----------------------------------------------------------
// ZimbraLog implementation
// ----------------------------------------------------------

void
ZimbraLogSetComponent
	(
	const char * name
	)
{
	strcpy( g_component, name );
}


void
ZimbraLogSetLevel
	(
	int level
	)
{
	g_logLevel = level;
}


extern void
___DoPrint( const char * string );


void
ZimbraLogPrint
	(
	int				level,
	int				flags,
	const char	*	format,
	...
	)
{
	if ( level <= g_logLevel )
	{
		char		buffer1[8192];
		char		buffer2[8192];
		va_list	args;

		va_start(args, format);

		ZimbraVSNPrintF( buffer1, sizeof( buffer1 ), format, args );
	
		va_end(args);

		if ( !( flags & kZimbraLogRaw ) )
		{
			if (buffer1[strlen(buffer1) - 1] == '\n')
			{
				buffer1[strlen(buffer1) - 1] = '\0';
			}

#if defined(WIN32)

			sprintf(buffer2, "[%s] %s (%d)\n", g_component, buffer1, GetCurrentThreadId());

#else

			sprintf(buffer2, "[%s] %s (%d)\n", g_component, buffer1, getpid());

#endif

		}
		else
		{
			sprintf( buffer2, format );
		}
	
		if ( g_handler )
		{
			g_handler( level, buffer2 );
		}
		else
		{
			g_warning( buffer2 );

#if defined(WIN32)

			OutputDebugString(buffer2);

#endif
		}
	}
}


void
ZimbraLogSetMessageHandler
	(
	ZimbraLogMessageHandler handler
	)
{
	g_handler = handler;
}


static unsigned int
ZimbraVSNPrintF
	(
	char				*	buf,
	unsigned int		sz,
	const char		*	fmt,
	va_list 				ap
	)
{
	unsigned int ret = 0;
  
	while ( *fmt )
	{
		unsigned int wid	=	0;
		int prec				= -1;
		int flg_zero		=	0;
		int flg_hash		=	0;
		int flg_minus		=	0;
		int flg_plus		=	0;
		int flg_spac		=	0;
		int flg_parse		=	1;
  
		if ( ( *fmt != '%' ) || ( *++fmt == '%' ) )
		{
			ADD_C( *fmt++ );
			continue;
		}

		// Write out how much data has been written

		FMT_RET_N("hh", signed char);
		FMT_RET_N("h",  short);
		FMT_RET_N("",   int);
		FMT_RET_N("l",  long);
		FMT_RET_N("ll", long long);
		FMT_RET_N("z",  ssize_t);
		FMT_RET_N("t",  ptrdiff_t);
		FMT_RET_N("j",  intmax_t);

		// Parse the flags for the data

		while ( *fmt && flg_parse )
		{
      	switch (*fmt)
      	{
				case '0': flg_zero  = 1; ++fmt; break;
				case '#': flg_hash  = 1; ++fmt; break;
				case '-': flg_minus = 1; ++fmt; break;
				case '+': flg_plus  = 1; ++fmt; break;
				case ' ': flg_spac  = 1; ++fmt; break;
				default:  flg_parse = 0;        break;
			}
		}
    
		if ( MatchCommand( &fmt, "*" ) )
		{
			int tmp = va_arg(ap, int);
			wid = tmp;

			if ( tmp < 0 )
			{
				wid = -wid;
			}
		}
		else
		{
			PARSE_FMT_NUM10(wid);
		}
    
		if ( MatchCommand( &fmt, ".*" ) )
		{
			prec = va_arg(ap, int);
		}
		else if ( MatchCommand( &fmt, "." ) )
		{
      	prec = 0;
      	PARSE_FMT_NUM10(prec);
    	}
    
		//  A C style string type, limited by precision

		if ( MatchCommand( &fmt, "s" ) )
		{
			const char	*	arg = va_arg(ap, char *);
			const char	*	tmp = arg;
			unsigned int	len = 0;
      
			if ( !arg )
			{
				arg = "";
			}
			else
			{
				while (*tmp++)
				{
					++len;
				}
			}

			if ( prec > 0 )
			{  
        		if ( len > ( unsigned int ) prec )
				{
					len = prec;
				}

				if ( wid > ( unsigned int ) prec )
				{
					wid = prec;
				}
			}

			DEC_WIDTH(len);
      
			if ( !flg_minus )
			{
				ADD_PAD_C(' ', wid);
			}
      
			while ( len-- )
			{
				ADD_C(*arg++);
			}
      
			ADD_PAD_C(' ', wid);
		}

		// Just a simple character

		else if ( MatchCommand( &fmt, "c" ) )
		{
			ADD_C( va_arg( ap, int ) );
		}

		// The numbers, including all the sized variations...

		FMT_ALL_NUM_S("d", 10, "0123456789");
		FMT_ALL_NUM_S("i", 10, "0123456789");
		FMT_ALL_NUM_U("u", 10, "0123456789",         0,   0);
		FMT_ALL_NUM_U("o",  8, "01234567",         '0',   0);
		FMT_ALL_NUM_U("x", 16, "0123456789abcdef", '0', 'x');
		FMT_ALL_NUM_U("X", 16, "0123456789ABCDEF", '0', 'X');

		// This isn't that portable, but it will work most places... and everyone needs %p :)
		else
		{
			flg_hash = 1;
			if (0) { }
			FMT_NUM_U("p", uintptr_t, void *, 16, "0123456789ABCDEF", '0', 'x');
			else break;
		}
	}
  
	if (sz)
	{
		*buf = 0;
	}

	return ret;
}


static int
MatchCommand
	(
	const char	**	passed_fmt,
	const char	*	cmd
	)
{
	const char *fmt = *passed_fmt;

	while (*cmd && (*fmt++ == *cmd))
	{
		++cmd;
	}

	if (!*cmd)
	{
		*passed_fmt = fmt;
	}

	return (!*cmd);
}
