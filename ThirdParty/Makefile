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
LDAP_VERSION ?= 2.4.17.8z

BDB_VERSION ?= 4.7.25.4
BDB_LIB_DIR ?= $(ZIMBRA_HOME)/bdb-$(BDB_VERSION)/lib

BDB_ROOT ?= $(P4_ROOT)/ThirdParty/bdb

CYRUS_VERSION ?= 2.1.23.3z
CYRUS_LIB_DIR ?= $(ZIMBRA_HOME)/cyrus-sasl-$(CYRUS_VERSION)/lib

ASPELL_VERSION ?= aspell-0.60.6
CLAMAV_VERSION ?= 0.95.2

LIBTOOL_VERSION ?= 2.2.6a

MYSQL_VERSION_STRING ?= 5.0.83
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

OPENSSL_VERSION ?= 0.9.8k
OPENSSL_LIB_DIR ?= $(ZIMBRA_HOME)/openssl-$(OPENSSL_VERSION)/lib

HEIMDAL_VERSION ?= 1.2.1
HEIMDAL_LIB_DIR ?= $(ZIMBRA_HOME)/heimdal-$(HEIMDAL_VERSION)/lib

HTTPD_VERSION ?= 2.2.11

POSTFIX_VERSION ?= 2.6.2.2z

DIRS := openssl \
	mysql \
	bdb \
	libxml2 \
	heimdal \
	curl \
	cyrus-sasl \
	libtool \
	openldap \
	aspell \
	clamav \
	apache-httpd \
	php \
	pcre \
	expat \
	popt \
	postfix \
	dspam \
	rrdtool \
	snmp \
	memcached \
	nginx \
	tcmalloc

LOGFILE := ThirdParty.make.log

all: allclean $(ZIMBRA_HOME) build 

$(ZIMBRA_HOME):
	mkdir -p $@

build:
	 @(for dir in $(DIRS); do \
		$(ECHO) -n "*** Building in $$dir "; \
		$(MAKE) -C $$dir >> $(LOGFILE) 2>&1; \
		if [ $$? -ne 0 ]; then  $(ECHO) "FAILED.";  else  $(ECHO) "SUCCEEDED.";  fi ; \
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
	rm -rf $(ZIMBRA_HOME)/data

mysql: force
	(cd $@; $(MAKE) > $(LOGFILE).$$ 2>&1)

bdb: force
	(cd $@; $(MAKE) > $(LOGFILE).$$ 2>&1)

libtool: force
	(cd $@; $(MAKE) > $(LOGFILE).$$ 2>&1)

openldap: force
	(cd $@; $(MAKE) > $(LOGFILE).$$ 2>&1)

cyrus-sasl: force
	(cd $@; $(MAKE) > $(LOGFILE).$$ 2>&1)

aspell: force
	(cd $@; $(MAKE) > $(LOGFILE).$$ 2>&1)

clamav: force
	(cd $@; $(MAKE) > $(LOGFILE).$$ 2>&1)

postfix: force
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
