THIRDPARTY_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(THIRDPARTY_ROOT)/..; pwd)

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif

ZIMBRA_HOME ?= /opt/zimbra
LDAP_VERSION ?= 2.3.21

BDB_VERSION ?= 4.2.52.4

BDB_ROOT ?= $(P4_ROOT)/ThirdParty/sleepycat

CYRUS_VERSION ?= cyrus-sasl-2.1.21.ZIMBRA
ASPELL_VERSION ?= aspell-0.60.3.tar.gz
CLAMAV_VERSION ?= clamav-0.88.tar.gz

MYSQL_VERSION_STRING ?= 5.0.26
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

HTTPD_VERSION ?= 2.0.54

POSTFIX_VERSION ?= 2.2.9

DIRS := mysql \
	sleepycat \
	cyrus-sasl \
	openldap \
	aspell \
	clamav \
	apache-httpd \
	php \
	pcre \
	expat \
	popt \
	perdition \
	PostFix/PostFix-$(POSTFIX_VERSION) \
	dspam

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

allclean:
	for dir in $(DIRS); do \
		echo "*** Building allclean in $$dir"; \
		make -C $$dir allclean; \
	done

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

postfix: PostFix/PostFix-$(POSTFIX_VERSION)

PostFix/PostFix-$(POSTFIX_VERSION): force
	(cd $@; make > $(LOGFILE).$$ 2>&1)

perl:
	(cd $@; make)

force: ;
