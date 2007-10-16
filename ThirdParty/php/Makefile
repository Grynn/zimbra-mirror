PHP_VERSION ?= 5.2.2
HTTPD_VERSION ?= 2.2.4
ASPELL_VERSION ?= 0.60.5
LIBXML2_VERSION ?= 2.6.29

PHP_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(PHP_ROOT)/../..; pwd)

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif

ifeq ($(BUILD_PLATFORM), SuSEES9)
	LIBXML2 := --with-libxml-dir=/opt/zimbra/libxml2-$(LIBXML2_VERSION)
endif

ZIMBRA_HOME ?= /opt/zimbra

PHP_TGZ_TARGET := \
	$(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/php-$(PHP_VERSION).tgz

all: build tar

build:
	tar xzf src/php-$(PHP_VERSION).tar.gz
	(cd php-$(PHP_VERSION); CFLAGS="-g -O2" ./configure --prefix=/opt/zimbra/php-$(PHP_VERSION) --with-apxs2=/opt/zimbra/httpd-$(HTTPD_VERSION)/bin/apxs --with-config-file=/opt/zimbra/conf/php.ini --with-pspell=/opt/zimbra/aspell-$(ASPELL_VERSION) $(LIBXML2); make; make install)

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
