INCLUDES = -DG_LOG_DOMAIN=\"libezimbra\" -I.. $(ADD_CFLAGS) $(GLIB_CFLAGS) $(GTK_CFLAGS) $(EDS_CFLAGS) $(EDS_UI_CFLAGS) $(DATACAL_CFLAGS) $(DATABOOK_CFLAGS) $(LIBXML_CFLAGS) $(ZIMBRA_CFLAGS) $(CURL_CFLAGS) $(SOUP_CFLAGS)

noinst_LTLIBRARIES = libezimbra.la

libezimbra_la_SOURCES =			\
	$(marshal_sources)			\
	e-zimbra-connection.c		\
	e-zimbra-connection.h		\
	e-zimbra-folder.c			\
	e-zimbra-folder.h			\
	e-zimbra-item.c				\
	e-zimbra-item.h				\
	e-zimbra-debug.c			\
	e-zimbra-debug.h			\
	e-zimbra-utils.h			\
	e-zimbra-utils.c			\
	e-zimbra-log.c				\
	e-zimbra-log.h				\
	e-zimbra-xml.c				\
	e-zimbra-xml.h				\
	zimbra.c

libezimbra_la_LIBADD =			\
	$(EDS_LIBS)					\
	$(LIBXML_LIBS)				\
	$(DATACAL_LIBS)				\
	$(DATABOOK_LIBS)			\
	$(GTK_LIBS)					\
	$(EDS_UI_LIBS)				\
	$(CURL_LIBS)				\
	../glog/libglog.la


libezimbraincludedir	= $(privincludedir)/zimbra
