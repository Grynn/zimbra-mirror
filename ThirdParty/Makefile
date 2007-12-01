THIRDPARTY_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(THIRDPARTY_ROOT)/..; pwd)

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif

ZIMBRA_HOME ?= /opt/zimbra
LDAP_VERSION ?= 2.3.39.6z

BDB_VERSION ?= 4.2.52.6
BDB_LIB_DIR ?= $(ZIMBRA_HOME)/sleepycat-$(BDB_VERSION)/lib

BDB_ROOT ?= $(P4_ROOT)/ThirdParty/sleepycat

CYRUS_VERSION ?= cyrus-sasl-2.1.22.3z
CYRUS_LIB_DIR ?= $(ZIMBRA_HOME)/cyrus-sasl-$(CYRUS_VERSION)/lib

ASPELL_VERSION ?= aspell-0.60.5
CLAMAV_VERSION ?= 0.91.2

MYSQL_VERSION_STRING ?= 5.0.45
MYSQL_VERSION ?= standard-$(MYSQL_VERSION_STRING)-pc-linux-gnu-i686
MYSQL_CLIENT_VERSION ?= standard-$(MYSQL_VERSION_STRING)-clientlibs

ifeq ($(BUILD_PLATFORM), MACOSX)
    MYSQL_VERSION := standard-$(MYSQL_VERSION_STRING)-apple-darwin8.2.0-powerpc
	MYSQL_CLIENT_VERSION := standard-$(MYSQL_VERSION_STRING)-apple-darwin8.2.0-powerpc
endif

ifeq ($(BUILD_PLATFORM), MACOSXx86)
	MYSQL_VERSION := standard-$(MYSQL_VERSION_STRING)-apple-darwin8.2.0-i686
	MYSQL_CLIENT_VERSION := standard-$(MYSQL_VERSION_STRING)-apple-darwin8.2.0-i686
endif

MYSQL_VERSION ?= standard-$(MYSQL_VERSION_STRING)-pc-linux-gnu-i686
MYSQL_CLIENT_VERSION ?= standard-$(MYSQL_VERSION_STRING)-clientlibs

OPENSSL_VERSION ?= 0.9.8e
OPENSSL_LIB_DIR ?= $(ZIMBRA_HOME)/openssl-$(OPENSSL_VERSION)/lib

HEIMDAL_VERSION ?=  1.0.1
HEIMDAL_LIB_DIR ?= $(ZIMBRA_HOME)/heimdal-$(HEIMDAL_VERSION)/lib

HTTPD_VERSION ?= 2.2.4

POSTFIX_VERSION ?= 2.4.3.3z

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
		echo -n "*** Building in $$dir "; \
		make -C $$dir >> $(LOGFILE) 2>&1; \
		if [ $$? -ne 0 ]; then  echo "FAILED.";  else  echo "SUCCEEDED.";  fi ; \
		if [ -x /sbin/ldconfig ]; then /sbin/ldconfig; fi ;\
	done)
	@(echo -n "*** Building in Perl "; make -C Perl ) 
	@(make -C apache-httpd tar >> $(LOGFILE) 2>&1)

clean:
	for dir in $(DIRS); do \
		echo "*** Building clean in $$dir"; \
		make -C $$dir clean; \
	done
	@(echo "*** Building clean in Perl"; make -C Perl clean )

allclean:
	for dir in $(DIRS); do \
		echo "*** Building allclean in $$dir"; \
		make -C $$dir allclean; \
	done
	@(echo "*** Building allclean in Perl"; make -C Perl allclean )
	rm -f $(LOGFILE)
	rm -rf $(ZIMBRA_HOME)/conf
	rm -rf $(ZIMBRA_HOME)/log
	rm -rf $(ZIMBRA_HOME)/zimbramon

mysql: force
	(cd $@; make > $(LOGFILE).$$ 2>&1)

sleepycat: force
	(cd $@; make > $(LOGFILE).$$ 2>&1)

openldap: force
	(cd $@; make > $(LOGFILE).$$ 2>&1)

cyrus-sasl: force
	(cd $@; make > $(LOGFILE).$$ 2>&1)

aspell: force
	(cd $@; make > $(LOGFILE).$$ 2>&1)

clamav: force
	(cd $@; make > $(LOGFILE).$$ 2>&1)

postfix: PostFix

PostFix: force
	(cd $@; make > $(LOGFILE).$$ 2>&1)

perl:
	(cd $@; make)

p4edit:
	for dir in $(DIRS); do \
		echo "***  p4 editing in $$dir"; \
		make -C $$dir p4edit; \
	done
	@(echo -n "*** p4 editing in Perl "; make -C Perl p4edit ) 


force: ;
