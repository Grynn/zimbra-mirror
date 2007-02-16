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

#include <config.h>

#if !defined (__GLOG_H__)
#error "Only <glog/glog.h> can be included directly, this file may disappear or change contents."
#endif

#ifndef __GLOGCATEGORY_H__
#define __GLOGCATEGORY_H__

#include <glib.h>
#include <glog/glogmacros.h>

G_BEGIN_DECLS


/**
 * GLogFormatFlags:
 * @GLOG_FORMAT_FG_BLACK: Use black as foreground color.
 * @GLOG_FORMAT_FG_RED: Use red as foreground color.
 * @GLOG_FORMAT_FG_GREEN: Use green as foreground color.
 * @GLOG_FORMAT_FG_YELLOW: Use yellow as foreground color.
 * @GLOG_FORMAT_FG_BLUE: Use blue as foreground color.
 * @GLOG_FORMAT_FG_MAGENTA: Use magenta as foreground color.
 * @GLOG_FORMAT_FG_CYAN: Use cyan as foreground color.
 * @GLOG_FORMAT_FG_WHITE: Use white as foreground color.
 * @GLOG_FORMAT_BG_BLACK: Use black as background color.
 * @GLOG_FORMAT_BG_RED: Use red as background color.
 * @GLOG_FORMAT_BG_GREEN: Use green as background color.
 * @GLOG_FORMAT_BG_YELLOW: Use yellow as background color.
 * @GLOG_FORMAT_BG_BLUE: Use blue as background color.
 * @GLOG_FORMAT_BG_MAGENTA: Use magenta as background color.
 * @GLOG_FORMAT_BG_CYAN: Use cyan as background color.
 * @GLOG_FORMAT_BG_WHITE: Use white as background color.
 * @GLOG_FORMAT_BOLD: Make the output bold.
 * @GLOG_FORMAT_UNDERLINE: Underline the output.
 *
 * These are some terminal-oriented flags you can use when creating your 
 * debugging categories to make them stand out in the debugging output.
 * They can be or'ed together when used as the format parameter to the 
 * #GLOG_CATEGORY or #GLOG_CATEGORY_STATIC.
 */

/* defines for format (colors etc)
 * don't change them around, it uses terminal layout
 * Terminal color strings:
 * 00=none 01=bold 04=underscore 05=blink 07=reverse 08=concealed
 * Text color codes:
 * 30=black 31=red 32=green 33=yellow 34=blue 35=magenta 36=cyan 37=white
 * Background color codes:
 * 40=black 41=red 42=green 43=yellow 44=blue 45=magenta 46=cyan 47=white
 */
typedef enum {
  /* colors */
  GLOG_FORMAT_FG_BLACK		= 0x0000,
  GLOG_FORMAT_FG_RED		= 0x0001,
  GLOG_FORMAT_FG_GREEN		= 0x0002,
  GLOG_FORMAT_FG_YELLOW		= 0x0003,
  GLOG_FORMAT_FG_BLUE		= 0x0004,
  GLOG_FORMAT_FG_MAGENTA      	= 0x0005,
  GLOG_FORMAT_FG_CYAN		= 0x0006,
  GLOG_FORMAT_FG_WHITE		= 0x0007,
  /* background colors */
  GLOG_FORMAT_BG_BLACK		= 0x0000,
  GLOG_FORMAT_BG_RED		= 0x0010,
  GLOG_FORMAT_BG_GREEN		= 0x0020,
  GLOG_FORMAT_BG_YELLOW		= 0x0030,
  GLOG_FORMAT_BG_BLUE		= 0x0040,
  GLOG_FORMAT_BG_MAGENTA      	= 0x0050,
  GLOG_FORMAT_BG_CYAN		= 0x0060,
  GLOG_FORMAT_BG_WHITE		= 0x0070,
  /* other formats */
  GLOG_FORMAT_BOLD		= 0x0100,
  GLOG_FORMAT_UNDERLINE		= 0x0200
} GLogFormatFlags;

#define GLOG_FORMAT_FG_MASK	(0x000F)
#define GLOG_FORMAT_BG_MASK	(0x00F0)
#define GLOG_FORMAT_FORMAT_MASK	(0xFF00)

/**
 * GLogCategory:
 *
 * This is an opaque structure used to represent the different logging 
 * categories. Use the different accessor functions to access its contents.
 */
typedef struct _GLogCategory GLogCategory;
struct _GLogCategory {
  /*< private >*/
  const gchar *		name;		/* name category is identified with */
  const gchar *		description;	/* English description of category */
  const guint		format;		/* defines format of output (see above) */
  
  const guint		default_threshold; /* default threshold value */
  guint			threshold;	/* ATOMIC value describing threshold 
					   for when to output debug messages */
  const gboolean	auto_update;	/* if the threshold is managed by the core */
  guint	        	last_update;	/* ATOMIC value describing when this 
					   category was last updated
					   (only used when auto_update == FALSE) */
};

GLOG_EXPORT GLogCategory	GLOG_CAT_DEFAULT;

#ifndef GLOG_DISABLE_LOGGING


/**
 * GLOG_CATEGORY:
 * @cat: the category
 * @name: the name of the category. This name should only contain alphanumeric 
 *        characters and underscores.
 * @format: or'ed #GLogFormatFlags to use for a color representation or 0 for 
 *	    default format.
 * @description: description of the category.
 *
 * Initializes a new #GstDebugCategory with the given properties and set to
 * the default threshold.
 *
 * <note>
 * <para>
 * This macro expands to nothing if debugging is disabled.
 * </para>
 * <para>
 * When naming your category, please follow the following conventions to ensure
 * that the pattern matching for categories works as expected. It is not
 * earth-shattering if you don't follow these conventions, but it would be nice
 * for everyone.
 * </para>
 * <para>
 * If you define a category for a plugin or a feature of it, name the category
 * like the feature. So if you wanted to write a "filesrc" element, you would
 * name the category "filesrc". Use lowercase letters only.
 * If you define more than one category for the same element, append an
 * underscore and an identifier to your categories, like this: "filesrc_cache"
 * </para>
 * <para>
 * If you create a library or an application using debugging categories, use a
 * common prefix followed by an underscore for all your categories. GStreamer
 * uses the GST prefix so GStreamer categories look like "GST_STATES". Be sure
 * to include uppercase letters.
 * </para>
 * </note>
 */
#ifdef GLOG_HAVE_CONSTRUCTOR

#define GLOG_CATEGORY(cat,name,format,description) \
GLogCategory cat = { \
  name, \
  description, \
  format, \
  GLOG_LEVEL_DEFAULT, \
  GLOG_LEVEL_DEFAULT, \
  TRUE, \
  0 \
}; \
static void GLOG_CONSTRUCTOR \
__glog_initialize_category_ ## cat (void) \
{ \
  __glog_add_category (&(cat)); \
} \
static void GLOG_DESTRUCTOR \
__glog_deinitialize_category_ ## cat (void) \
{ \
  __glog_remove_category (&(cat)); \
}

#else /* !GLOG_HAVE_CONSTRUCTOR */

#define GLOG_CATEGORY(cat,name,description,format) \
GLogCategory cat = { \
  name, \
  description, \
  format, \
  GLOG_LEVEL_DEFAULT, \
  GLOG_LEVEL_DEFAULT, \
  TRUE, \
  0 \
}; \

#endif /* !GLOG_HAVE_CONSTRUCTOR */

/**
 * GLOG_CATEGORY_EXTERN:
 * @cat: the category
 *
 * Declares a GstDebugCategory variable as extern. Use in header files.
 * This macro expands to nothing if debugging is disabled.
 * You should only use this macro in headers you are not going to install,
 * otherwise subtle errors can occur when debugging was disabled while creating
 * your lib, which makes the category not exist, but the header declares that 
 * it exists.
 */
#define GLOG_CATEGORY_EXTERN(cat) extern GLogCategory cat;
/**
 * GLOG_CATEGORY_STATIC:
 * @cat: the category
 * @name: the name of the category.
 * @format: or'ed #GLogFormatFlags to use for a color representation or 0 for 
 *	    default format.
 * @description: description of the category.
 *
 * Defines a static GLogCategory variable. See GLOG_CATEGORY() for details.
 * This macro expands to nothing if debugging is disabled.
 */
#define GLOG_CATEGORY_STATIC(cat,name,format,description) \
static GLOG_CATEGORY(cat,name,format,description)

#else /* !defined GLOG_DISABLE_LOGGING */

#define GLOG_CATEGORY(cat,name,format,description)
#define GLOG_CATEGORY_EXTERN(cat)
#define GLOG_CATEGORY_STATIC(cat,name,format,description)

#endif /* !defined GLOG_DISABLE_LOGGING */

const gchar *	glog_level_get_name		(GLogLevel    	level);

GLogLevel	glog_category_get_threshold	(GLogCategory *	category);
const gchar *	glog_category_get_name		(GLogCategory *	category);
guint		glog_category_get_format	(GLogCategory *	category);
const gchar *	glog_category_get_description	(GLogCategory *	category);

/*< private >*/
void		__glog_add_category		(GLogCategory * category);
void		__glog_remove_category		(GLogCategory * category);


G_END_DECLS

#endif /* __GLOGCATEGORY_H__ */
