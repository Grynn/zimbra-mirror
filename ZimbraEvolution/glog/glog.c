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

#ifdef HAVE_CONFIG_H
#  include "config.h"
#endif

/* getpid */
#include <sys/types.h>
#include <unistd.h>
/* strtoul */
#include <stdlib.h>
#include <string.h>
#include <stdio.h>

#include "glog.h"

#define GLOG_RETURN_IF_NOT_INITIALIZED G_STMT_START{ \
  if (!log_functions) { \
    g_warning ("The log system has not been initialized. Call glog_init first"); \
    return; \
  } \
}G_STMT_END

#define GLOG_RETURN_VAL_IF_NOT_INITIALIZED(val) G_STMT_START{ \
  if (!log_functions) { \
    g_warning ("The log system has not been initialized. Call glog_init first"); \
    return (val); \
  } \
}G_STMT_END

struct _GLogLogMessage
{
  gchar *message;
  const gchar *format;
  va_list arguments;
};

/* global variables */

/* timeval of when the debug subsystem was initialized */

static gchar * g_logfolder	= NULL;
static gchar * g_logfile	= NULL;

GLOG_CATEGORY_STATIC (glog_debug, "GLOG", 0, "debugging of the glog library")

/* list of all name/level pairs from initialization and GLOG env var */
typedef struct
{
  GPatternSpec *pat;
  GLogLevel level;
}
LevelNameEntry;
static GArray *level_name = NULL;

/* all registered categories that are managed automatically */
static GSList *categories = NULL;

/* ATOMIC value that gets increased whenever level_name stuff gets updated.
 * It's used by categories that are not updated automatically */
static guint last_update = 0;

/* global mutex we use whenever we modify the categories or level/name list */
static GStaticRecMutex glog_mutex = G_STATIC_REC_MUTEX_INIT;

/* all registered logging handlers - note> no mutex used here for performance reasons */
typedef struct
{
  GLogLogFunction func;
  gpointer user_data;
}
LogFuncEntry;
static GArray *log_functions = NULL;

/* if we use colored output in the default debugging function. Can be disabled
 * by setting the env var GLOG_NO_COLOR */
gboolean colored_output = TRUE;

/* refcount of this library. Only access with glog_mutex held */
guint glog_refcount = 0;

GLogCategory GLOG_CAT_DEFAULT = {
  "default",
  "the void where all unspecified debugging goes",
  0,
  GLOG_LEVEL_DEFAULT,
  GLOG_LEVEL_DEFAULT,
  TRUE,
  0
};

/* code */

static void
parse_debug_list (const gchar * list)
{
  gchar **split;
  gchar **walk;

  g_return_if_fail (list != NULL);

  GLOG_CAT_LOG (&glog_debug, "parsing debugging list \"%s\"", list);
  walk = split = g_strsplit (list, ",", 0);

  while (walk[0]) {
    gchar **values;
    
    values = g_strsplit (walk[0], ":", 2);

    if (values[0] && values[1]) {
      gint level = 0;

      g_strstrip (values[0]);
      g_strstrip (values[1]);
      GLOG_CAT_LOG (&glog_debug, "parsing %s:%s", values[0], values[1]);
      level = strtoul (values[1], NULL, 0);
      if (level >= 0 && level < GLOG_LEVEL_COUNT) {
        glog_set_threshold (values[0], level);
      } else {
	GLOG_CAT_ERROR (&glog_debug, "invalid level for %s: %s",
	    values[0], values[1]);
      }
    } else {
      GLOG_CAT_ERROR (&glog_debug, "invalid entry in debugging list: \'%s\'", 
	  walk[0]);
    }
    g_strfreev (values);
    walk++;
  }
  g_strfreev (split);
}

extern void _glog_init_printf_extension (void);
/**
 * glog_init:
 *
 * Initializes the glog library. This function must be called before any 
 * other glog function may be called.
 */
void
glog_init (void)
{
  const gchar *str;
  
  g_static_rec_mutex_lock (&glog_mutex);
  glog_refcount++;
  if (glog_refcount > 1) {
    GLOG_CAT_LOG (&glog_debug, "glog has already been initialized, just bumping refcount.");
    g_static_rec_mutex_unlock (&glog_mutex);
    return;
  }
  
  _glog_init_printf_extension ();
  log_functions = g_array_new (FALSE, FALSE, sizeof (LogFuncEntry));
  level_name = g_array_new (FALSE, FALSE, sizeof (LevelNameEntry));

  __glog_add_category (&GLOG_CAT_DEFAULT);

  glog_add_log_function (glog_log_default, NULL);

  /* parse env vars */
  if (g_getenv ("GLOG_NO_COLOR")) {
    GLOG_CAT_INFO (&glog_debug, "disabling colored output");
    colored_output = FALSE;
  } else {
    colored_output = TRUE;
  }
  str = g_getenv ("GLOG");
  if (str)
    parse_debug_list (str);
  g_static_rec_mutex_unlock (&glog_mutex);
  GLOG_CAT_DEBUG (&glog_debug, "glog was initialized.");
}

/**
 * glog_exit:
 *
 * Exits the glog library. Use this function if you used glog from inside a 
 * plugin and the plugin gets unloaded. 
 */
void
glog_exit (void)
{
  g_static_rec_mutex_lock (&glog_mutex);
  glog_refcount--;
  if (glog_refcount > 0) {
    GLOG_CAT_LOG (&glog_debug, "glog is still initialized, just decreasing refcount.");
    g_static_rec_mutex_unlock (&glog_mutex);
    return;
  }
  GLOG_CAT_DEBUG (&glog_debug, "glog was exited.");
  glog_remove_log_function (glog_log_default, NULL);
  g_array_free (log_functions, TRUE);
  g_array_free (level_name, TRUE);
  __glog_remove_category (&GLOG_CAT_DEFAULT);
  g_static_rec_mutex_unlock (&glog_mutex);
}

/* we can't do this further above, because we work with the GLOG_CAT_DEFAULT struct */
#define GLOG_CAT_DEFAULT glog_debug

/**
 * glog_log:
 * @category: category to log
 * @level: level the message is in
 * @file: the file that emitted the message, usually the __FILE__ identifier
 * @function: the function that emitted the message, usually G_STRFUNC
 * @line: the line from that the message was emitted, usually __LINE__
 * @object: the object this message relates to or NULL if none. An object is
 *	    a memory location of at least sizeof (gpointer) bytes.
 * @format: a printf style format string
 * @...: optional arguments for the format
 * 
 * Logs the given message using the registered debugging handlers.
 */
void
glog_log (GLogCategory * category, GLogLevel level,
    const gchar * file, const gchar * function, gint line,
    gpointer object, const gchar * format, ...)
{
  va_list var_args;

  va_start (var_args, format);
  glog_log_valist (category, level, file, function, line, object, format, 
      var_args);
  va_end (var_args);
}

/**
 * glog_log_valist:
 * @category: category to log
 * @level: level of the message is in
 * @file: the file that emitted the message, usually the __FILE__ identifier
 * @function: the function that emitted the message, usually G_STRFUNC
 * @line: the line from that the message was emitted, usually __LINE__
 * @object: the object this message relates to or NULL if none. An object is
 *	    a memory location of at least sizeof (gpointer) bytes.
 * @format: a printf style format string
 * @args: optional arguments for the format
 * 
 * Logs the given message using the registered debugging handlers.
 */
void
glog_log_valist (GLogCategory * category, GLogLevel level,
    const gchar * file, const gchar * function, gint line,
    gpointer object, const gchar * format, va_list args)
{
  GLogLogMessage message;
  LogFuncEntry *entry;
  guint i;

  g_return_if_fail (category != NULL);
  g_return_if_fail (file != NULL);
  g_return_if_fail (function != NULL);
  g_return_if_fail (format != NULL);
  GLOG_RETURN_IF_NOT_INITIALIZED;
  
  message.message = NULL;
  message.format = format;
  G_VA_COPY (message.arguments, args);

  for (i = 0; i < log_functions->len; i++) {
    entry = &g_array_index (log_functions, LogFuncEntry, i);
    entry->func (category, level, file, function, line, object, &message,
        entry->user_data);
  }
  g_free (message.message);
  va_end (message.arguments);
}

/**
 * glog_log_message_get:
 * @message: a debug message
 *
 * Gets the string representation of a #GLogLogMessage. This function is used
 * in debug handlers to extract the message.
 *
 * Returns: the string representation of a #GLogLogMessage.
 */
const gchar *
glog_log_message_get (GLogLogMessage * message)
{
  if (message->message == NULL) {
    message->message = g_strdup_vprintf (message->format, message->arguments);
  }
  return message->message;
}

/* FIXME: want to export this? */
/*
 * glog_construct_term_format:
 * @formatinfo: the format info
 * 
 * Constructs a string that can be used for getting the desired format in color
 * terminals.
 * You need to free the string after use.
 * 
 * Returns: a string containing the format definition
 */
static gchar *
glog_construct_term_format (guint formatinfo)
{
  GString *color;
  gchar *ret;

  color = g_string_new ("\033[00");

  if (formatinfo & GLOG_FORMAT_BOLD) {
    g_string_append (color, ";01");
  }
  if (formatinfo & GLOG_FORMAT_UNDERLINE) {
    g_string_append (color, ";04");
  }
  if (formatinfo & GLOG_FORMAT_FG_MASK) {
    g_string_append_printf (color, ";3%1d", formatinfo & GLOG_FORMAT_FG_MASK);
  }
  if (formatinfo & GLOG_FORMAT_BG_MASK) {
    g_string_append_printf (color, ";4%1d",
        (formatinfo & GLOG_FORMAT_BG_MASK) >> 4);
  }
  g_string_append (color, "m");

  ret = color->str;
  g_string_free (color, FALSE);
  return ret;
}

static void
my_g_time_val_diff (GTimeVal *elapsed, GTimeVal *to, GTimeVal *from)
{
  g_return_if_fail (elapsed != NULL);
  g_return_if_fail (from != NULL);
  g_return_if_fail (to != NULL);

  if (to->tv_usec < from->tv_usec) {
    elapsed->tv_usec = to->tv_usec + G_USEC_PER_SEC - from->tv_usec;
    elapsed->tv_sec = to->tv_sec - from->tv_sec - 1;
  } else {
    elapsed->tv_usec = to->tv_usec - from->tv_usec;
    elapsed->tv_sec = to->tv_sec - from->tv_sec;
  }
}

/**
 * glog_log_default:
 * @category: category to log
 * @level: level of the message
 * @file: the file that emitted the message, usually the __FILE__ identifier
 * @function: the function that emitted the message, usually G_STRFUNC
 * @line: the line from that the message was emitted, usually __LINE__
 * @message: the actual message to output
 * @object: the object this message relates to or NULL if none
 * @unused: an unused variable, reserved for some user_data.
 * 
 * The default logging handler used by glog. Logging functions get called
 * whenever a macro like GLOG_DEBUG() or similar is used. This function outputs the
 * message and additional info using the glib error handler.
 * You can add other handlers by using #glog_log_function. 
 * You can stop glog using this function by calling
 * #glog_remove_log_function (gst_debug_log_default, NULL);
 */
void
glog_log_default (GLogCategory * category, GLogLevel level, const gchar * file, 
    const gchar * function, gint line, gpointer object, 
    GLogLogMessage * message, gpointer unused)
{
	char		localtime[ 56 ] = { '\0' };
	gint		pid;
	time_t		t;

	if ( level > glog_category_get_threshold( category ) )
	{
    	return;
	}

	pid = getpid ();

	t = time( NULL );

	ctime_r( &t, localtime );

	// Get rid of trailing newline

	if ( strlen( localtime ) )
	{
		localtime[ strlen( localtime ) - 1 ] = '\0';
	}

	if ( !g_logfolder )
	{
		g_logfolder = g_build_filename( g_get_home_dir(), ".evolution/log", NULL );

		if ( g_mkdir_with_parents( g_logfolder, 0777 ) != 0 )
    	{
			g_error("g_mkdir_with_parents(%s) failed", g_logfolder );
		}
	}

	if ( g_logfolder && !g_logfile )
	{
		g_logfile = g_build_filename( g_logfolder, "zimbra.log" );
	}

	if ( g_logfile )
	{
		FILE * fp;

		fp = fopen( g_logfile, "a" );

		if ( fp )
		{
			fprintf( fp, "%s %5d %26s %s(%d):%s %s\n", glog_level_get_name( level ), pid, localtime, file, line, function, glog_log_message_get( message ) );
			fflush( fp );
			fclose( fp );
		}
	}

	g_printerr( "%s %5d %26s %s(%d):%s %s\n", glog_level_get_name( level ), pid, localtime, file, line, function, glog_log_message_get( message ) );
}

/**
 * glog_add_log_function:
 * @func: the function to use
 * @data: user data
 * 
 * Adds the logging function to the list of logging functions.
 * Be sure to use G_GNUC_NO_INSTRUMENT on that function, it is needed.
 * <emphasis>This function is not threadsafe, so the best thing is to only use 
 * it on initialization.</emphasis>
 */
void
glog_add_log_function (GLogLogFunction func, gpointer data)
{
  LogFuncEntry entry;

  g_return_if_fail (func != NULL);
  GLOG_RETURN_IF_NOT_INITIALIZED;

  entry.func = func;
  entry.user_data = data;
  g_array_append_val (log_functions, entry);

  GLOG_INFO ("appended log function %p (user data %p) to log functions",
      func, data);
}

/**
 * glog_remove_log_function:
 * @func: the log function to remove
 * @data: the data associated with the log function
 * 
 * Removes the given func/data pair from the log functions if the pair is found.
 * <emphasis>This function is not threadsafe, so the best thing is to only use 
 * it on initialization.</emphasis>
 * 
 * Returns: TRUE if a logging function was removed.
 */
gboolean
glog_remove_log_function (GLogLogFunction func, gpointer data)
{
  guint i;

  g_return_val_if_fail (func != NULL, FALSE);
  GLOG_RETURN_VAL_IF_NOT_INITIALIZED (FALSE);
  
  for (i = 0; i < log_functions->len; i++) {
    LogFuncEntry *e = &g_array_index (log_functions, LogFuncEntry, i);
    if (e->func == func && e->user_data == data) {
      GLOG_DEBUG ("removed log function %p (user data %p) from log functions", 
	  func, data);
      g_array_remove_index_fast (log_functions, i);
      return TRUE;
    }
  }

  return FALSE;
}

static void
glog_category_update_threshold_unlocked (GLogCategory *cat)
{
  gint i, cur, set_to = cat->default_threshold;

  for (i = level_name->len - 1; i >= 0; i--) {
    LevelNameEntry *entry = &g_array_index (level_name, LevelNameEntry, i);

    if (g_pattern_match_string (entry->pat, cat->name)) {
      GLOG_LOG ("category %s matches pattern %p - gets set to level %d",
          cat->name, entry->pat, entry->level);
      set_to = entry->level;
      goto exit;
    }
  }

exit:
  do {
    cur = g_atomic_int_get (&cat->threshold);
  } while (!g_atomic_int_compare_and_exchange (&cat->threshold, cur, set_to));
  if (!cat->auto_update) {
    gint tmp = g_atomic_int_get (&last_update);
    do {
      cur = g_atomic_int_get (&cat->last_update);
    } while (!g_atomic_int_compare_and_exchange (&cat->last_update, cur, tmp));
  }
}

static void
glog_category_update_threshold (GLogCategory *cat)
{
  g_return_if_fail (cat->auto_update == FALSE);
  
  g_static_rec_mutex_lock (&glog_mutex);
  glog_category_update_threshold_unlocked (cat);
  g_static_rec_mutex_unlock (&glog_mutex);
}

/* holds: glog_mutex */
static void
glog_was_updated (void)
{
  GSList *walk;
  
  g_atomic_int_add (&last_update, 1);
  for (walk = categories; walk; walk = g_slist_next (walk)) {
    glog_category_update_threshold_unlocked (walk->data);
  }
}

/**
 * glog_set_threshold:
 * @pattern: pattern of categories that should match this entry
 * @level: level to set the categories' threshold to
 * 
 * Sets all categories which match the given glob style pattern to the given 
 * level.
 */
void
glog_set_threshold (const gchar *pattern, GLogLevel level)
{
  LevelNameEntry entry;
  
  g_return_if_fail (pattern != NULL);
  g_return_if_fail (level > GLOG_LEVEL_NONE && level < GLOG_LEVEL_COUNT);

  entry.pat = g_pattern_spec_new (pattern);
  entry.level = level;
  g_static_rec_mutex_lock (&glog_mutex);
  g_array_append_val (level_name, entry);
  glog_was_updated ();
  g_static_rec_mutex_unlock (&glog_mutex);
}

/**
 * glog_unset_threshold:
 * @pattern: pattern to unset
 * 
 * Resets all categories with the given name back to their default level.
 */
void
glog_unset_threshold (const gchar * pattern)
{
  gint i;
  GPatternSpec *pat;

  g_return_if_fail (pattern != NULL);

  pat = g_pattern_spec_new (pattern);
  g_static_rec_mutex_lock (&glog_mutex);
  for (i = level_name->len - 1; i >= 0; i++) {
    LevelNameEntry *entry = &g_array_index (level_name, LevelNameEntry, i);

    if (g_pattern_spec_equal (entry->pat, pat)) {
      g_pattern_spec_free (entry->pat);
      g_array_remove_index (level_name, i);
      glog_was_updated ();
      break;
    }
  }
  
  g_static_rec_mutex_unlock (&glog_mutex);
  g_pattern_spec_free (pat);
  return;
}

/* this function is private, so no need to document it. */
void
__glog_add_category (GLogCategory *category)
{
  g_return_if_fail (category != NULL);
  g_return_if_fail (category->auto_update == TRUE);

  g_static_rec_mutex_lock (&glog_mutex);
  categories = g_slist_prepend (categories, category);
  /* set correct threshold only if glog is initialized */
  if (glog_refcount > 0) {
    glog_category_update_threshold_unlocked (category);
  }
  g_static_rec_mutex_unlock (&glog_mutex);
}

/* this function is private, so no need to document it. */
void
__glog_remove_category (GLogCategory * category)
{
  g_return_if_fail (category != NULL);

  g_static_rec_mutex_lock (&glog_mutex);
  categories = g_slist_remove (categories, category);
  g_static_rec_mutex_unlock (&glog_mutex);
}
  
/**
 * glog_level_get_name:
 * @level: the level to get the name for
 * 
 * Gets the string representation of a logging level. The name is 5 letters 
 * long.
 * 
 * Returns: the name of a logging level
 */
const gchar *
glog_level_get_name (GLogLevel level)
{
  switch (level) {
    case GLOG_LEVEL_NONE:
      return "     ";
    case GLOG_LEVEL_ERROR:
      return "ERROR";
    case GLOG_LEVEL_WARNING:
      return "WARN ";
    case GLOG_LEVEL_INFO:
      return "INFO ";
    case GLOG_LEVEL_DEBUG:
      return "DEBUG";
    case GLOG_LEVEL_LOG:
      return "LOG  ";
    default:
      g_warning ("invalid level specified for glog_level_get_name");
      return "     ";
  }
}

/**
 * glog_category_get_threshold:
 * @category: a #GLogCategory to get the threshold of.
 *
 * Returns the threshold of a #GLogCategory. A debugging function should not
 * output debugging messages with a level lower than its category's threshold.
 *
 * Returns: the #GLogLevel that is used as threshold.
 */
GLogLevel
glog_category_get_threshold (GLogCategory * category)
{
  g_return_val_if_fail (category != NULL, GLOG_LEVEL_DEFAULT);

  if (!category->auto_update &&
      g_atomic_int_get (&category->last_update) != g_atomic_int_get (&last_update)) {
    glog_category_update_threshold (category);
  }
  return g_atomic_int_get (&category->threshold);
}

/**
 * glog_category_get_name:
 * @category: a #GLogCategory to get the name of.
 *
 * Returns the name of a debug category. The name is only valid as long as the
 * category is not unloaded. If you don't know wether the category might be 
 * unloaded, please copy the value.
 *
 * Returns: the name of the category.
 */
const gchar *
glog_category_get_name (GLogCategory * category)
{
  g_return_val_if_fail (category != NULL, NULL);

  return category->name;
}

/**
 * glog_category_get_format:
 * @category: a #GLogCategory to get the format of.
 *
 * Returns the format of a category that should be used when displaying 
 * formatted output of this category.
 *
 * Returns: the format of the category.
 */
guint
glog_category_get_format (GLogCategory * category)
{
  g_return_val_if_fail (category != NULL, 0);

  return category->format;
}

/**
 * glog_category_get_description:
 * @category: a #GLogCategory to get the description of.
 *
 * Returns the description of a category. The name is only valid as long as the
 * category is not unloaded. If you don't know wether the category might be 
 * unloaded, please copy the value.
 *
 * Returns: the description of the category or NULL if none was set.
 */
const gchar *
glog_category_get_description (GLogCategory *category)
{
  g_return_val_if_fail (category != NULL, NULL);

  return category->description;
}

/**
 * glog_version:
 * @major: pointer to a guint to store the major version number
 * @minor: pointer to a guint to store the minor version number
 * @micro: pointer to a guint to store the micro version number
 *
 * Gets the version number of the glog library.
 */
void
glog_version (guint * major, guint * minor, guint * micro)
{
  g_return_if_fail (major);
  g_return_if_fail (minor);
  g_return_if_fail (micro);

  *major = GLOG_VERSION_MAJOR;
  *minor = GLOG_VERSION_MINOR;
  *micro = GLOG_VERSION_MICRO;
}
