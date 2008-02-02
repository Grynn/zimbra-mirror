PHP_VERSION ?= 5.2.2
HTTPD_VERSION ?= 2.2.6
ASPELL_VERSION ?= 0.60.5
LIBXML2_VERSION ?= 2.6.29

PHP_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(PHP_ROOT)/../..; pwd)
MAKE ?= make
MAKEARGS ?= -j2

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif

SED	:= echo

ifeq (MACOSXx86,$(findstring MACOSXx86,$(BUILD_PLATFORM)))
	SED := sed -i .bak -e 's/-lxml2/\/opt\/zimbra\/libxml2-$(LIBXML2_VERSION)\/lib\/libxml2.a/g' Makefile
endif

ifeq ($(BUILD_PLATFORM), MACOSX)
	SED := sed -i .bak -e 's/-lxml2/\/opt\/zimbra\/libxml2-$(LIBXML2_VERSION)\/lib\/libxml2.a/g' Makefile
endif

ZIMBRA_HOME ?= /opt/zimbra

PHP_TGZ_TARGET := \
	$(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/php/php-$(PHP_VERSION).tgz

all: build tar

build:
	tar xzf src/php-$(PHP_VERSION).tar.gz
	(cd php-$(PHP_VERSION); patch -g0 -N -p1 < ../patches/leopard.fix; CFLAGS="-g -O2" ./configure --prefix=/opt/zimbra/php-$(PHP_VERSION) --with-apxs2=/opt/zimbra/httpd-$(HTTPD_VERSION)/bin/apxs --with-config-file=/opt/zimbra/conf/php.ini --with-pspell=/opt/zimbra/aspell-$(ASPELL_VERSION) --with-libxml-dir=/opt/zimbra/libxml2-$(LIBXML2_VERSION); $(SED); $(MAKE) $(MAKEARGS); $(MAKE) install)

clean:
	rm -rf php-$(PHP_VERSION)

allclean: clean
	rm -rf /opt/zimbra/php-$(PHP_VERSION)

tar: $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/php
	(cd $(ZIMBRA_HOME); tar czf \
	$(PHP_TGZ_TARGET) \
	php-$(PHP_VERSION))
	chmod -R a+w $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/php

$(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/php:
	mkdir -p $@

p4edit: $(PHP_TGZ_TARGET)
	p4 add $(PHP_TGZ_TARGET)
	p4 edit $(PHP_TGZ_TARGET)
