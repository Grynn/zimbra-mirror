dnl ===========================================================================
dnl  evolution-zimbra
dnl ===========================================================================
AC_PREREQ(0.01)
AC_INIT(libezimbra/zimbra.h)

PACKAGE=evolution-zimbra
VERSION=0.1.0

AC_SUBST([PACKAGE])
AC_SUBST([VERSION])

dnl Versioning
EVO_REQUIRED_VERSION=2.3.7
EDS_REQUIRED_VERSION=1.3.7

AC_CONFIG_SRCDIR(libezimbra)
AM_CONFIG_HEADER(config.h)
AM_INIT_AUTOMAKE($PACKAGE, $VERSION)

AC_ISC_POSIX
AC_PROG_CC
AM_PROG_LIBTOOL

# Libtool versioning

LIBEZIMBRA_CURRENT=0
LIBEZIMBRA_REVISION=0
LIBEZIMBRA_AGE=0

LIBEZIMBRA_BOOK_CURRENT=0
LIBEZIMBRA_BOOK_REVISION=0
LIBEZIMBRA_BOOK_AGE=0

LIBEZIMBRA_CAL_CURRENT=0
LIBEZIMBRA_CAL_REVISION=0
LIBEZIMBRA_CAL_AGE=0

AC_SUBST(LIBEZIMBRA_CURRENT)
AC_SUBST(LIBEZIMBRA_REVISION)
AC_SUBST(LIBEZIMBRA_AGE)
AC_SUBST(LIBEZIMBRA_BOOK_CURRENT)
AC_SUBST(LIBEZIMBRA_BOOK_REVISION)
AC_SUBST(LIBEZIMBRA_BOOK_AGE)
AC_SUBST(LIBEZIMBRA_CAL_CURRENT)
AC_SUBST(LIBEZIMBRA_CAL_REVISION)
AC_SUBST(LIBEZIMBRA_CAL_AGE)


dnl ===========================================================================
dnl Versioning
dnl ===========================================================================

AC_MSG_CHECKING(Evolution version)
EVOLUTION_VERSION=`pkg-config --modversion evolution-shell-2.2 2>/dev/null`

if test -z "$EVOLUTION_VERSION"; then
	dnl No 2.2 check for 2.6
	EVOLUTION_VERSION=`pkg-config --modversion evolution-shell-2.6 2>/dev/null`
	if test -z "$EVOLUTION_VERSION"; then
		dnl No 2.6 check for 2.8
		EVOLUTION_VERSION=`pkg-config --modversion evolution-shell-2.8 2>/dev/null`
		if test -z "$EVOLUTION_VERSION"; then
			dnl No 2.8 check for 2.10
			EVOLUTION_VERSION=`pkg-config --modversion evolution-shell-2.10 2>/dev/null`
			if test -z "$EVOLUTION_VERSION"; then
				AC_MSG_ERROR(Evolution development libraries not installed)	
			else
				EVOLUTION_API_VERSION=2.10
				EAPI_MAJOR_VERSION=2
				EAPI_MINOR_VERSION=10
				CAMEL_API_VERSION=1.2
				EDS_API_VERSION=1.2
			fi
		else
			EVOLUTION_API_VERSION=2.8
			EAPI_MAJOR_VERSION=2
			EAPI_MINOR_VERSION=8
			CAMEL_API_VERSION=1.2
			EDS_API_VERSION=1.2
		fi
	else
		EVOLUTION_API_VERSION=2.6
		EAPI_MAJOR_VERSION=2
		EAPI_MINOR_VERSION=6
		CAMEL_API_VERSION=1.2
		EDS_API_VERSION=1.2
	fi
else
	EVOLUTION_API_VERSION=2.2
	EAPI_MAJOR_VERSION=2
	EAPI_MINOR_VERSION=2
	CAMEL_API_VERSION=1.2
	EDS_API_VERSION=1.2
fi

AC_MSG_NOTICE("Using Evolution API version $EVOLUTION_VERSION")

AC_SUBST(EVOLUTION_VERSION)
AC_SUBST(EVOLUTION_API_VERSION)

EVOLUTION_BASE_VERSION=$EVOLUTION_API_VERSION
AC_DEFINE_UNQUOTED(EVOLUTION_BASE_VERSION,"$EVOLUTION_BASE_VERSION",Evolution version)

AC_SUBST(EAPI_MAJOR_VERSION)
AC_SUBST(EAPI_MINOR_VERSION)

EVOLUTION_MAJOR_VERSION=$EAPI_MAJOR_VERSION
AC_DEFINE_UNQUOTED(EVOLUTION_MAJOR_VERSION,$EVOLUTION_MAJOR_VERSION,Evolution major version)

EVOLUTION_MINOR_VERSION=$EAPI_MINOR_VERSION
AC_DEFINE_UNQUOTED(EVOLUTION_MINOR_VERSION,$EVOLUTION_MINOR_VERSION,Evolution minor version)

BASE_VERSION=$EVOLUTION_API_VERSION
AC_DEFINE_UNQUOTED(BASE_VERSION, "$BASE_VERSION", Connector base version)
AC_SUBST(BASE_VERSION)


dnl for the spec file
AC_SUBST(EDS_REQUIRED_VERSION)
AC_SUBST(EVO_REQUIRED_VERSION)

dnl ===========================================================================
dnl Developer build?
dnl ===========================================================================

dnl ===========================================================================
dnl i18n 
dnl ===========================================================================

dnl ===========================================================================
dnl i18n
dnl ===========================================================================

ALL_LINGUAS="en"

AC_PROG_INTLTOOL([0.33])
AM_GLIB_GNU_GETTEXT

GETTEXT_PACKAGE=$PACKAGE-${BASE_VERSION}

AC_SUBST(GETTEXT_PACKAGE)
AC_DEFINE_UNQUOTED(GETTEXT_PACKAGE, "$GETTEXT_PACKAGE", [Package name for gettext])
AC_DEFINE_UNQUOTED(CAMEL_EXPLICIT_TRANSLATION_DOMAIN, "$GETTEXT_PACKAGE", [Package name for gettext])

localedir='$(prefix)/$(DATADIRNAME)/locale'
AC_SUBST(localedir)


dnl =========== Utility macro to set compiler flags for a specific lib.==========
dnl  EVO_SET_COMPILE_FLAGS(VAR-PREFIX, DEPS, EXTRA-CFLAGS, EXTRA-LIBS)
AC_DEFUN([EVO_SET_COMPILE_FLAGS], [
	PKG_CHECK_MODULES([$1], [$2])
	$1_CFLAGS="[$]$1_CFLAGS \$(WERROR) $3"
	$1_LIBS="[$]$1_LIBS $4"
])

dnl ===========================================================================
dnl Check for libraries
dnl ===========================================================================

dnl === glib ==========================
PKG_CHECK_MODULES(GLIB, glib-2.0 gconf-2.0)
AC_SUBST(GLIB_CFLAGS)
AC_SUBST(GLIB_LIBS)

dnl === gtk+ ==========================
PKG_CHECK_MODULES(GTK, gtk+-2.0 libglade-2.0)
AC_SUBST(GTK_CFLAGS)
AC_SUBST(GTK_LIBS)

dnl === evolution-plugin ==============
PKG_CHECK_MODULES(EPLUGIN, evolution-plugin-$EVOLUTION_API_VERSION)
AC_SUBST(EPLUGIN_CFLAGS)
AC_SUBST(EPLUGIN_LIBS)

eplugindir=`pkg-config --variable=plugindir evolution-plugin-$EVOLUTION_API_VERSION` 
AC_SUBST(eplugindir)

eerrordir=`pkg-config --variable=errordir evolution-plugin-$EVOLUTION_API_VERSION`
AC_SUBST(eerrordir)

EPLUGIN_RULE=$srcdir/eplugin.mk
AC_SUBST_FILE(EPLUGIN_RULE)

dnl === evolution =====================

EVO_imagesdir="`pkg-config --variable=imagesdir evolution-shell-$EVOLUTION_API_VERSION`"
AC_SUBST(EVO_imagesdir)

dnl === camel =========================
PKG_CHECK_MODULES(CAMEL, camel-$CAMEL_API_VERSION)
AC_SUBST(CAMEL_CFLAGS)
AC_SUBST(CAMEL_LIBS)

PKG_CHECK_MODULES(CAMEL_PROVIDER, camel-provider-$CAMEL_API_VERSION)
AC_SUBST(CAMEL_PROVIDER_CFLAGS)
AC_SUBST(CAMEL_PROVIDER_LIBS)

camel_providerdir=`pkg-config --variable=camel_providerdir camel-provider-$CAMEL_API_VERSION`
AC_SUBST(camel_providerdir)

camel_privlibdir=`pkg-config --variable=privlibdir camel-provider-$CAMEL_API_VERSION`
AC_SUBST(camel_privlibdir)

camel_includedir=`pkg-config --variable=privincludedir camel-provider-$CAMEL_API_VERSION`
AC_SUBST(camel_privincludedir)


dnl === e-d-s =========================
PKG_CHECK_MODULES(EDS, evolution-data-server-$EDS_API_VERSION)
AC_SUBST(EDS_CFLAGS)
AC_SUBST(EDS_LIBS)

PKG_CHECK_MODULES(EDS_UI, libedataserverui-$EDS_API_VERSION libglade-2.0 libgnomeui-2.0)
AC_SUBST(EDS_UI_CFLAGS)
AC_SUBST(EDS_UI_LIBS)


PKG_CHECK_MODULES(DATABOOK,  libedata-book-$EDS_API_VERSION)
AC_SUBST(DATABOOK_CFLAGS)
AC_SUBST(DATABOOK_LIBS)

PKG_CHECK_MODULES(DATACAL,  libedata-cal-$EDS_API_VERSION)
AC_SUBST(DATACAL_CFLAGS)
AC_SUBST(DATACAL_LIBS)

backenddir=`pkg-config --variable=extensiondir evolution-data-server-$EDS_API_VERSION`
AC_SUBST(backenddir)

dnl === libxml ========================
PKG_CHECK_MODULES(LIBXML, libxml-2.0)
AC_SUBST(LIBXML_LIBS)
AC_SUBST(LIBXML_CFLAGS)

dnl === libcurl =======================
CURL_CFLAGS="$ADD_CFLAGS -I/usr/include"
CURL_LIBS="-lcurl"

AC_SUBST(CURL_CFLAGS)
AC_SUBST(CURL_LIBS)

dnl === Timezone checks ===============
dnl stolen form e-d-s
AC_CACHE_CHECK(for tm_gmtoff in struct tm, ac_cv_struct_tm_gmtoff,
	AC_TRY_COMPILE([
		#include <time.h>
		], [
		struct tm tm;
		tm.tm_gmtoff = 1;
		], ac_cv_struct_tm_gmtoff=yes, ac_cv_struct_tm_gmtoff=no))
if test $ac_cv_struct_tm_gmtoff = yes; then
	AC_DEFINE(HAVE_TM_GMTOFF, 1, [Define if struct tm has a tm_gmtoff member])
else
	AC_CACHE_CHECK(for timezone variable, ac_cv_var_timezone,
		AC_TRY_COMPILE([
			#include <time.h>
		], [
			timezone = 1;
		], ac_cv_var_timezone=yes, ac_cv_var_timezone=no))
	if test $ac_cv_var_timezone = yes; then
		AC_DEFINE(HAVE_TIMEZONE, 1, [Define if libc defines a timezone variable])
		AC_CACHE_CHECK(for altzone variable, ac_cv_var_altzone,
			AC_TRY_COMPILE([
				#include <time.h>
			], [
				altzone = 1;
			], ac_cv_var_altzone=yes, ac_cv_var_altzone=no))
		if test $ac_cv_var_altzone = yes; then
			AC_DEFINE(HAVE_ALTZONE, 1, [Define if libc defines an altzone variable])
		fi
	else
		AC_ERROR(unable to find a way to determine timezone)
	fi
fi


AC_CHECK_FUNCS(mkstemp mkdtemp isblank)


dnl === More warnings  ==========================
dnl GNOME_COMPILE_WARNINGS(maximum)
dnl CFLAGS="$CFLAGS $WARN_CFLAGS"

AC_ARG_ENABLE(more-warnings,
[  --enable-more-warnings  Maximum compiler warnings],
set_more_warnings="$enableval",[
if test "x$cvs_version" = "xyes" ; then
	set_more_warnings=yes
else
	set_more_warnings=no
fi
])

if test "$GCC" = "yes" -a "$set_more_warnings" != "no"; then
	echo "enable compile warnings = $set_more_warnings"
	ADD_CFLAGS="-Wall -Wchar-subscripts -Wmissing-declarations -Wmissing-prototypes -Wnested-externs -Wpointer-arith"

	for option in -Wno-strict-aliasing -Wno-sign-compare -Wno-pointer-sign; do
		SAVE_CFLAGS="$CFLAGS"
		CFLAGS="$VFS_CFLAGS $option $CFLAGS"
		AC_MSG_CHECKING([whether gcc understands $option])
		AC_TRY_COMPILE([], [],
			has_option=yes,
			has_option=no,)
		CFLAGS="$SAVE_CFLAGS"
		if test $has_option = yes; then
		  ADD_CFLAGS="$ADD_CFLAGS $option"
		fi
		AC_MSG_RESULT($has_option)
		unset has_option
		unset SAVE_CFLAGS
	done
	unset option
fi
dnl === libzimbra =====================

ZIMBRA_CFLAGS="$ADD_CFLAGS -I\$(top_srcdir) -I\$(top_builddir)"
ZIMBRA_LIBS="\$(top_builddir)/libezimbra/libezimbra.la"

AC_SUBST(ZIMBRA_CFLAGS)
AC_SUBST(ZIMBRA_LIBS)

dnl ===========================================================================
dnl we don't want to use deprecated API
dnl ===========================================================================
AC_DEFINE(G_DISABLE_DEPRECATED, 1, [No deprecated glib functions])
AC_DEFINE(GDK_DISABLE_DEPRECATED, 1, [No deprecated gdk functions])
AC_DEFINE(GTK_DISABLE_DEPRECATED, 1, [No deprecated gtk functions])
AC_DEFINE(GNOME_DISABLE_DEPRECATED, 1, [No deprecated gnome functions])
AC_DEFINE(BONOBO_DISABLE_DEPRECATED, 1, [No deprecated bonobo functions])
AC_DEFINE(BONOBO_UI_DISABLE_DEPRECATED, 1, [No deprecated bonoboui functions])
AC_DEFINE(GCONF_DISABLE_DEPRECATED, 1, [No deprecated gconf functions])
AC_DEFINE(LIBGLADE_DISABLE_DEPRECATED, 1, [No deprecated libglade functions])



dnl ===========================================================================
dnl Directory setup
dnl ===========================================================================

EZIMBRA_DATADIR='$(datadir)/evolution-zimbra/$(BASE_VERSION)'
AC_SUBST(EZIMBRA_DATADIR)

gladedir='$(datadir)/evolution-zimbra/$(BASE_VERSION)/glade'
AC_SUBST(gladedir)

dnl ===========================================================================
dnl Setup glog 
dnl ===========================================================================
GLOG_VERSION_MAJOR=0
GLOG_VERSION_MINOR=5
GLOG_VERSION_MICRO=0
AC_SUBST(GLOG_VERSION_MAJOR)
AC_SUBST(GLOG_VERSION_MINOR)
AC_SUBST(GLOG_VERSION_MICRO)

GLOG_MAJORMINOR=0.6
AC_SUBST(GLOG_MAJORMINOR)

dnl test for register_printf_function
AC_CHECK_FUNC(register_printf_function,
  [
    GLOG_PRINTF_EXTENSION_FORMAT_DEFINE="#define GLOG_PTR_FORMAT \"P\""
    AC_DEFINE(HAVE_PRINTF_EXTENSION, 1,
             [Defined if we have register_printf_function ()])
  ],
  GLOG_PRINTF_EXTENSION_FORMAT_DEFINE="#define GLOG_PTR_FORMAT \"p\""
)
AC_SUBST(GLOG_PRINTF_EXTENSION_FORMAT_DEFINE)

dnl flags shared for all internal objects (core libs, elements, applications)
dnl we disable deprecated internally
dnl XML, GLib, popt, GLOG_INT, and the right include for CFLAGS
GLOG_INT_CFLAGS="$GLIB_CFLAGS $GLOG_PKG_CFLAGS \
 $GLOG_INT_CFLAGS $GLOG_ERROR_CFLAGS -DGLOG_DISABLE_DEPRECATED"

dnl Private vars for libglogonly
GLOG_LIB_CFLAGS="$GLOG_INT_CFLAGS -I\$(top_srcdir)"
GLOG_LIB_LIBS="$GLIB_LIBS $GLOG_PKG_LIBS $LTLIBINTL"
GLOG_LIB_LDFLAGS="$GLOG_LT_LDFLAGS -version-info $GLOG_LIBVERSION $EXPORT_LDFLAGS"
AC_SUBST(GLOG_LIB_CFLAGS)
AC_SUBST(GLOG_LIB_LIBS)
AC_SUBST(GLOG_LIB_LDFLAGS)

dnl Vars for all internal objects built on libglog
GLOG_OBJ_CFLAGS="$GLOG_INT_CFLAGS -I\$(top_srcdir)"
GLOG_OBJ_LIBS="\$(top_builddir)/glog/libglog-$GLOG_MAJORMINOR.la"

AC_SUBST(GLOG_OBJ_CFLAGS, "$GLOG_OBJ_CFLAGS")
AC_SUBST(GLOG_OBJ_LIBS, "$GLOG_OBJ_LIBS")

AC_ARG_ENABLE(logging,
[  --enable-logging       Enable detailed logging])

if test "x$enable_logging" = "xyes" ; then
	AC_DEFINE(GLOG_LEVEL_DEFAULT, 6, [Log everything by default])
 	enable_logging=yes
else
	AC_DEFINE(GLOG_DISABLE_LOGGING, 1, [Do not log])
 	enable_logging=no
fi


dnl ===========================================================================
dnl Write Makefiles
dnl ===========================================================================
AC_OUTPUT([
Makefile
glog/Makefile
glog/glogversion.h
libezimbra/Makefile
libezimbra/zimbra-version.h
calendar/Makefile
addressbook/Makefile
eplugin/Makefile
camel/Makefile
evolution-zimbra.spec
po/Makefile.in
])

echo ""
echo ""
echo "=========================================="
echo " Ready to build Zimbra Connector          "
echo "=========================================="
echo "  Logging  : ${enable_logging} (version: ${GLOG_MAJORMINOR})"
echo ""
echo "=========================================="
echo ""
echo "CFLAGS:     ${CFLAGS}"
echo "ADD_CFLAGS: ${ADD_CFLAGS}"
echo ""
echo "====== Evolution Settings ================"
echo "  API Version        : ${EVOLUTION_API_VERSION}"
echo "  Evolution version  : ${EVOLUTION_VERSION}"
echo "  E-D-S backend dir  : ${backenddir}"
echo ""
echo "====== Evolution Plugin Settings =========="
echo "  Plugin Path        : ${eplugindir}"
echo "  Error Path         : ${eerrordir}"
echo ""
echo "====== Camel-Provider Settings ==========="
echo "  privlibdir         : ${camel_privlibdir}"
echo "  Install Path       : ${camel_providerdir}"
echo ""
echo "Type 'make' to compile and then 'make install' to install"
echo ""
