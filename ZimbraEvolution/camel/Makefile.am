## Process this file with automake to produce Makefile.in

camel_provider_LTLIBRARIES	= libcamelzimbra.la
camel_provider_DATA			= libcamelzimbra.urls

INCLUDES					= -I..  $(GLIB_CFLAGS)	$(CAMEL_PROVIDER_CFLAGS) $(GNOME_INCLUDEDIR) -DG_LOG_DOMAIN=\"camel-sx-provider\"

libcamelzimbra_la_SOURCES	= camel-zimbra-provider.c		
	     	       	
libcamelzimbra_la_LDFLAGS	= -avoid-version -module $(NO_UNDEFINED)
libcamelzimbra_la_LIBADD	= $(EDS_LIBS) $(CAMEL_PROVIDER_LIBS) $(CAMEL_LIBS)

EXTRA_DIST = libcamelzimbra.urls
