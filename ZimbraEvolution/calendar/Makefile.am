INCLUDES = -DG_LOG_DOMAIN=\"libecalbackendzimbra\" -I$(top_srcdir) $(EVOLUTION_CALENDAR_CFLAGS) $(DATACAL_CFLAGS) $(DEBUG_CFLAGS)

backend_LTLIBRARIES = libecalbackendzimbra.la

libecalbackendzimbra_la_SOURCES =		\
	e-cal-backend-zimbra-factory.c	\
	e-cal-backend-zimbra-factory.h	\
	e-cal-backend-zimbra.c		\
	e-cal-backend-zimbra.h		\
	e-cal-backend-zimbra-utils.c		\
	e-cal-backend-zimbra-utils.h

libecalbackendzimbra_la_LIBADD = $(DATACAL_LIBS) $(LIBXML_LIBS) $(top_builddir)/libezimbra/libezimbra.la

libecalbackendzimbra_la_LDFLAGS = -module -avoid-version $(NO_UNDEFINED)

noinst_PROGRAMS = create-account list-account delete-account timezone
create_account_SOURCES = create-account.c

create_account_LDADD =  $(DATACAL_LIBS)

list_account_SOURCES = list-account.c

list_account_LDADD =  $(DATACAL_LIBS)

delete_account_SOURCES = delete-account.c

delete_account_LDADD =  $(DATACAL_LIBS)

timezone_SOURCES = timezone.c

timezone_LDADD =  $(DATACAL_LIBS)
