INCLUDES =								\
	$(ZIMBRA_CFLAGS)					\
	-I.									\
	-I$(top_srcdir)						\
	-DSX_LOCALEDIR=\"$(localedir)\"		\
	$(GLIB_CFLAGS)						\
	$(GTK_CFLAGS)						\
	$(EPLUGIN_CFLAGS)					\
	$(SOUP_CFLAGS)						\
	$(CAMEL_CFLAGS)					

@EPLUGIN_RULE@

eplugin_DATA							= com-zimbra-account-setup.eplug
eplugin_LTLIBRARIES						= libcom-zimbra-account-setup.la
libcom_zimbra_account_setup_la_SOURCES	= zimbra-account-setup.c camel-zimbra-listener.h camel-zimbra-listener.c
libcom_zimbra_account_setup_la_LIBADD	= $(GLIB_LIBS) $(GTK_LIBS) $(EPLUGIN_LIBS) $(CAMEL_LIBS) $(ZIMBRA_LIBS)
libcom_zimbra_account_setup_la_LDFLAGS	= -module -avoid-version
CLEANFILES								= $(eplugin_DATA)
EXTRA_DIST								= com-zimbra-account-setup.eplug.in
