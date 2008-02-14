THIRDPARTY_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(THIRDPARTY_ROOT)/..; pwd)

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

MAKE := make
ECHO := /bin/echo
MAKEARGS ?= -j5

ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif

ZIMBRA_HOME ?= /opt/zimbra
LDAP_VERSION ?= 2.3.40.7z

BDB_VERSION ?= 4.2.52.6
BDB_LIB_DIR ?= $(ZIMBRA_HOME)/sleepycat-$(BDB_VERSION)/lib

BDB_ROOT ?= $(P4_ROOT)/ThirdParty/sleepycat

CYRUS_VERSION ?= cyrus-sasl-2.1.22.3z
CYRUS_LIB_DIR ?= $(ZIMBRA_HOME)/cyrus-sasl-$(CYRUS_VERSION)/lib

ASPELL_VERSION ?= aspell-0.60.5
CLAMAV_VERSION ?= 0.92.1

MYSQL_VERSION_STRING ?= 5.0.45
MYSQL_VERSION ?= standard-$(MYSQL_VERSION_STRING)-pc-linux-gnu-i686
MYSQL_CLIENT_VERSION ?= standard-$(MYSQL_VERSION_STRING)-clientlibs

ifeq ($(BUILD_PLATFORM), MACOSX)
    MYSQL_VERSION := standard-$(MYSQL_VERSION_STRING)-apple-darwin8.2.0-powerpc
	MYSQL_CLIENT_VERSION := standard-$(MYSQL_VERSION_STRING)-apple-darwin8.2.0-powerpc
endif

ifeq (MACOSXx86,$(findstring MACOSXx86,$(BUILD_PLATFORM)))
	MYSQL_VERSION := standard-$(MYSQL_VERSION_STRING)-apple-darwin8.2.0-i686
	MYSQL_CLIENT_VERSION := standard-$(MYSQL_VERSION_STRING)-apple-darwin8.2.0-i686
endif

MYSQL_VERSION ?= standard-$(MYSQL_VERSION_STRING)-pc-linux-gnu-i686
MYSQL_CLIENT_VERSION ?= standard-$(MYSQL_VERSION_STRING)-clientlibs

OPENSSL_VERSION ?= 0.9.8g
OPENSSL_LIB_DIR ?= $(ZIMBRA_HOME)/openssl-$(OPENSSL_VERSION)/lib

OPENSSH_VERSION	?= 4.7p1

HEIMDAL_VERSION ?= 1.0.2
HEIMDAL_LIB_DIR ?= $(ZIMBRA_HOME)/heimdal-$(HEIMDAL_VERSION)/lib

HTTPD_VERSION ?= 2.2.6

POSTFIX_VERSION ?= 2.4.3.4z

DIRS := mysql \
	openssl \
	sleepycat \
	libxml2 \
	heimdal \
	curl \
	cyrus-sasl \
	openldap \
	aspell \
	clamav \
	apache-httpd \
	php \
	pcre \
	expat \
	popt \
	PostFix \
	dspam \
	rrdtool \
	snmp

LOGFILE := /tmp/ThirdParty.make.log

all: $(ZIMBRA_HOME) build 

$(ZIMBRA_HOME):
	mkdir -p $@

build:
	 @(for dir in $(DIRS); do \
		$(ECHO) -n "*** Building in $$dir "; \
		$(MAKE) -C $$dir >> $(LOGFILE) 2>&1; \
		if [ $$? -ne 0 ]; then  $(ECHO) "FAILED.";  else  $(ECHO) "SUCCEEDED.";  fi ; \
		if [ -x /sbin/ldconfig ]; then /sbin/ldconfig; fi ;\
	done)
	@($(ECHO) -n "*** Building in Perl "; $(MAKE) -C Perl ) 
	@($(MAKE) -C apache-httpd tar >> $(LOGFILE) 2>&1)

clean:
	for dir in $(DIRS); do \
		$(ECHO) "*** Building clean in $$dir"; \
		$(MAKE) -C $$dir clean; \
	done
	@($(ECHO) "*** Building clean in Perl"; $(MAKE) -C Perl clean )

allclean:
	for dir in $(DIRS); do \
		$(ECHO) "*** Building allclean in $$dir"; \
		$(MAKE) -C $$dir allclean; \
	done
	@($(ECHO) "*** Building allclean in Perl"; $(MAKE) -C Perl allclean )
	rm -f $(LOGFILE)
	rm -rf $(ZIMBRA_HOME)/conf
	rm -rf $(ZIMBRA_HOME)/log
	rm -rf $(ZIMBRA_HOME)/zimbramon

mysql: force
	(cd $@; $(MAKE) > $(LOGFILE).$$ 2>&1)

sleepycat: force
	(cd $@; $(MAKE) > $(LOGFILE).$$ 2>&1)

openldap: force
	(cd $@; $(MAKE) > $(LOGFILE).$$ 2>&1)

cyrus-sasl: force
	(cd $@; $(MAKE) > $(LOGFILE).$$ 2>&1)

aspell: force
	(cd $@; $(MAKE) > $(LOGFILE).$$ 2>&1)

clamav: force
	(cd $@; $(MAKE) > $(LOGFILE).$$ 2>&1)

postfix: PostFix

PostFix: force
	(cd $@; $(MAKE) > $(LOGFILE).$$ 2>&1)

perl:
	(cd $@; $(MAKE))

p4edit:
	for dir in $(DIRS); do \
		$(ECHO) "***  p4 editing in $$dir"; \
		$(MAKE) -C $$dir p4edit; \
	done
	@($(ECHO) -n "*** p4 editing in Perl "; $(MAKE) -C Perl p4edit ) 


force: ;
