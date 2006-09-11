INCLUDES = -DG_LOG_DOMAIN=\"zimbra-ebookbackend\" $(ZIMBRA_CFLAGS) $(DATABOOK_CFLAGS) $(LIBXML_CFLAGS)

backend_LTLIBRARIES = libebookbackendzimbra.la

libebookbackendzimbra_la_SOURCES = e-book-backend-zimbra.c e-book-backend-zimbra.h e-book-backend-zimbra-factory.c

libebookbackendzimbra_la_LIBADD = $(DATABOOK_LIBS) $(LIBXML_LIBS) $(CURL_LIBS) $(top_builddir)/libezimbra/libezimbra.la

libebookbackendzimbra_la_LDFLAGS = -module -avoid-version $(NO_UNDEFINED)

noinst_PROGRAMS = create-account
create_account_SOURCES = create-account.c

create_account_LDADD =  $(DATABOOK_LIBS)
