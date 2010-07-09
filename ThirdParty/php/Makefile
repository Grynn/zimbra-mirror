PHP_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(PHP_ROOT)/../..; pwd)
MAKE ?= make
MAKEARGS ?= -j2

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif

ZIMBRA_HOME ?= /opt/zimbra

PATCH	:=
ifdef BETA
	include $(PHP_ROOT)/../beta_versions.def
ifeq ($(BUILD_PLATFORM), MACOSXx86_10.6)
	PATCH	:= patch -g0 -N -p1 < ../patches/dns.patch; patch -g0 -N -p1 < ../patches/MACOSXx86_10.6.fix;
else
	PATCH	:= patch -g0 -N -p1 < ../patches/dns.patch;
endif
else
	include $(PHP_ROOT)/../versions.def
ifeq ($(BUILD_PLATFORM), MACOSXx86_10.6)
	PATCH	:= patch -g0 -N -p1 < ../patches/dns.patch; patch -g0 -N -p1 < ../patches/MACOSXx86_10.6.fix;
else
	PATCH	:= patch -g0 -N -p1 < ../patches/dns.patch;
endif
endif

SED := sed -i .bak -e 's/-lxml2/\/opt\/zimbra\/libxml2-$(XML_VERSION)\/lib\/libxml2.a/g' Makefile

ifeq ($(BUILD_PLATFORM), MACOSXx86_10.5)
	ACEXE := ac_cv_exeext=
endif

PHP_TGZ_TARGET := \
	$(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/php/php-$(PHP_VERSION).tgz

files	:= $(wildcard src/php-$(PHP_VERSION).tar.bz2)

all: checksrc allclean build tar

checksrc:
	$(if $(files), @echo "", exit 1)

build:
	tar xzf src/php-$(PHP_VERSION).tar.bz2
	(cd php-$(PHP_VERSION); \
	$(PATCH) $(ACEXE) CFLAGS="-g -O2" \
	./configure --prefix=/opt/zimbra/php-$(PHP_VERSION) --with-apxs2=/opt/zimbra/httpd-$(HTTPD_VERSION)/bin/apxs --with-config-file-path=/opt/zimbra/conf --with-pspell=/opt/zimbra/aspell-$(ASPELL_VERSION) --with-libxml-dir=/opt/zimbra/libxml2-$(XML_VERSION); \
	$(SED); \
	$(MAKE) $(MAKEARGS); \
	$(MAKE) install)

clean:
	rm -rf php-$(PHP_VERSION)

allclean: clean
	rm -rf /opt/zimbra/php-$(PHP_VERSION)
	rm -f $(PHP_TGZ_TARGET)

tar: $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/php
	(cd $(ZIMBRA_HOME); tar czf \
	$(PHP_TGZ_TARGET) \
	php-$(PHP_VERSION))
	chmod -R a+w $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/php/*

$(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/php:
	mkdir -p $@

p4edit: $(PHP_TGZ_TARGET)
	p4 add $(PHP_TGZ_TARGET)
	p4 edit $(PHP_TGZ_TARGET)
