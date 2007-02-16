/*
 * Copyright (C) 2006-2007 Zimbra, Inc.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of version 2 of the GNU Lesser General Public
 * License as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */

#ifndef __GLOGMACROS_H__
#define __GLOGMACROS_H__

#include <glib.h>

G_BEGIN_DECLS


/**
 * GLogLevel:
 * @GLOG_LEVEL_NONE: No debugging level specified or desired. Used to 
 *	deactivate debugging output.
 * @GLOG_LEVEL_ERROR: Error messages are to be used only when an error occured 
 *	that stops the application from keeping working correctly. The error is
 *	recoverable. If it is not, use g_warning or similar instead.
 *	An example where this level would be used is when parsing a broken 
 *	configuration file.
 * @GLOG_LEVEL_WARNING: Warning messages are to inform about abnormal behaviour
 *	that could lead to problems or weird behaviour later on. An example of 
 *	this in GStreamer would be clocking issues ("your computer is pretty 
 *	slow") or broken input data ("Can't synchronize to stream.")
 * @GLOG_LEVEL_INFO: Informational messages should be used to keep the 
 *	developer updated about what is happening. Examples where this should 
 *	be used are when a typefind function has successfully determined the 
 *	type of the stream or when an mp3 plugin detects the format to be used. 
 *	("This file has mono sound.")
 * @GLOG_LEVEL_DEBUG: Debugging messages should be used when something common
 *	happens that is not the expected default behavior. 
 *	An example from GStreamer would be notifications about state changes or 
 *	receiving/sending of events.
 * @GLOG_LEVEL_LOG: Log messages are messages that are very common but might be 
 *	useful to know. As a rule of thumb a function that is working as 
 *	expected should never output anything else but LOG messages.
 *	Examples for this are referencing/dereferencing of objects.
 * @GLOG_LEVEL_COUNT: The number of defined debugging levels.
 *
 * The level defines the importance of a debugging message. The more important 
 * a message is, the greater the probability that the debugging system outputs 
 * it.
 */
typedef enum {
  GLOG_LEVEL_NONE = 0,
  GLOG_LEVEL_ERROR,
  GLOG_LEVEL_WARNING,
  GLOG_LEVEL_INFO,
  GLOG_LEVEL_DEBUG,
  GLOG_LEVEL_LOG,
  /* add more */
  GLOG_LEVEL_COUNT
} GLogLevel;

/**
 * GLOG_LEVEL_DEFAULT:
 *
 * Defines the default debugging level to be used when defining categories. It 
 * is normally set to #GLOG_LEVEL_NONE so no output is printed. Developer builds 
 * often override this to #GLOG_LEVEL_ERROR though so the logging output is more 
 * verbose by default.
 */
#ifndef GLOG_LEVEL_DEFAULT
#define GLOG_LEVEL_DEFAULT GLOG_LEVEL_NONE
#endif

#if defined (__GNUC__)

/**
 * GLOG_HAVE_CONSTRUCTOR:
 *
 * Macro that is defined to 1 if the #GLOG_CONSTRUCTOR and #GLOG_DESTRUCTOR 
 * macros work with the given compiler.
 */
#  define GLOG_HAVE_CONSTRUCTOR (1)
/**
 * GLOG_CONSTRUCTOR:
 *
 * macro that defines a given function as constructor. Constructor functions
 * are run automatically when the given application, library or plugin they 
 * contain is loaded.
 * <note>This macro only works when #GLOG_HAVE_CONSTRUCTOR is defined.</note>
 */
#  define GLOG_CONSTRUCTOR __attribute__ ((constructor))
/**
 * GLOG_DESTRUCTOR:
 *
 * macro that defines a given function as destructor. Destructor functions
 * are run automatically when the given application, library or plugin they 
 * contain is unloaded.
 * <note>This macro only works when #GLOG_HAVE_CONSTRUCTOR is defined.</note>
 */
#  define GLOG_DESTRUCTOR __attribute__ ((destructor))

#else /* !defined (__GNUC__) */

#  undef GLOG_HAVE_CONSTRUCTOR
#  define GLOG_CONSTRUCTOR
#  define GLOG_DESTRUCTOR

#endif /* !defined (__GNUC__) */

#ifdef G_PLATFORM_WIN32
#  ifdef GLOG_COMPILATION
#    ifdef DLL_EXPORT
#      define GLOG_EXPORT __declspec(dllexport)
#    else /* !DLL_EXPORT */
#      define GLOG_EXPORT extern
#    endif /* !DLL_EXPORT */
#  else /* !GLOG_COMPILATION */
#    define GLOG_EXPORT extern __declspec(dllimport)
#  endif /* !GLOG_COMPILATION */
#else /* !G_PLATFORM_WIN32 */
#  define GLOG_EXPORT extern
#endif /* !G_PLATFORM_WIN32 */


G_END_DECLS

#endif /* __GLOGMACROS_H__ */
