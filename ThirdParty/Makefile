THIRDPARTY_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(THIRDPARTY_ROOT)/..; pwd)

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif

ZIMBRA_HOME ?= /opt/zimbra
LDAP_VERSION ?= 2.2.28

BDB_VERSION ?= 4.2.52.4

BDB_ROOT ?= $(P4_ROOT)/ThirdParty/sleepycat

CYRUS_VERSION ?= cyrus-sasl-2.1.21.ZIMBRA
ASPELL_VERSION ?= aspell-0.60.3.tar.gz
CLAMAV_VERSION ?= clamav-0.85.1.tar.gz

MYSQL_VERSION ?= standard-4.1.10a-pc-linux-gnu-i686
MYSQL_CLIENT_VERSION ?= standard-4.1.10a-clientlibs

HTTPD_VERSION ?= 2.0.54

POSTFIX_VERSION ?= 2.2.3

DIRS := mysql \
	sleepycat \
	openldap \
	cyrus-sasl \
	aspell \
	clamav \
	apache-httpd \
	php \
	pcre \
	PostFix/PostFix-$(POSTFIX_VERSION)

all: $(ZIMBRA_HOME) build 

$(ZIMBRA_HOME):
	mkdir -p $@

build:
	for dir in $(DIRS); do \
		echo "*** Building in $$dir"; \
		make -C $$dir; \
	done
	make -C apache-httpd tar

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
	(cd $@; make)

sleepycat: force
	(cd $@; make)

openldap: force
	(cd $@; make)

cyrus-sasl: force
	(cd $@; make)

aspell: force
	(cd $@; make)

clamav: force
	(cd $@; make)

postfix: PostFix/PostFix-$(POSTFIX_VERSION)

PostFix/PostFix-$(POSTFIX_VERSION): force
	(cd $@; make)

force: ;
