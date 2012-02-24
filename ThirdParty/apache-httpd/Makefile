HTTPD_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(HTTPD_ROOT)/../..; pwd)
MAKE ?= make
MAKEARGS ?= -j2

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

ZIMBRA_HOME ?= /opt/zimbra
ifdef BETA
	include $(HTTPD_ROOT)/../beta_versions.def
else
	include $(HTTPD_ROOT)/../versions.def
endif

HTTPD_TGZ_TARGET := \
	$(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/apache-httpd/httpd-$(HTTPD_VERSION).tgz

ifeq ($(BUILD_PLATFORM), RHEL5_64)
	ENVMOD := env LIBS="-L/usr/lib64"
endif

ifeq (MACOSXx86,$(findstring MACOSXx86,$(BUILD_PLATFORM)))
	PCRE	:= --with-pcre=/opt/zimbra/bin/pcre-config
endif

files	:= $(wildcard src/httpd-$(HTTPD_VERSION).tar.bz2)
aprsrc	:= $(wildcard src/apr-$(APR_VERSION).tar.bz2)
aprutilsrc	:= $(wildcard src/apr-util-$(APR_UTIL_VERSION).tar.bz2)


all: checksrc allclean build check

checksrc:
	$(if $(files), @echo "", exit 1)
	$(if $(aprsrc), @echo "", exit 1)
	$(if $(aprutilsrc), @echo "", exit 1)

check:
	@echo "Verifying apache-httpd build...";
	$(HTTPD_ROOT)/../zmcheck.pl -b $(ZIMBRA_HOME)/httpd-$(HTTPD_VERSION)/bin/httpd $(ZIMBRA_HOME)/httpd-$(HTTPD_VERSION)/bin/apachectl

build:
	tar xfj src/httpd-$(HTTPD_VERSION).tar.bz2
	(cd httpd-$(HTTPD_VERSION)/srclib; tar xfj ../../src/apr-$(APR_VERSION).tar.bz2; \
	mv apr-$(APR_VERSION) apr;)
	(cd httpd-$(HTTPD_VERSION)/srclib; tar xfj ../../src/apr-util-$(APR_UTIL_VERSION).tar.bz2; \
	mv apr-util-$(APR_UTIL_VERSION) apr-util;)
	(cd httpd-$(HTTPD_VERSION); $(ENVMOD) LDFLAGS="-L/opt/zimbra/convertd/lib -R/opt/zimbra/convertd/lib -L/opt/zimbra/openssl-$(OPENSSL_VERSION)/lib -R/opt/zimbra/openssl-$(OPENSSL_VERSION)/lib"  CFLAGS="-g -O2" ./configure --prefix=/opt/zimbra/httpd-$(HTTPD_VERSION) --enable-so $(PCRE) --with-included-apr --enable-ssl --with-ssl=/opt/zimbra/openssl-$(OPENSSL_VERSION); $(MAKE) $(MAKEARGS); $(MAKE) install)

tar:
	mkdir -p $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/apache-httpd
	(cd $(ZIMBRA_HOME); tar czf $(HTTPD_TGZ_TARGET) httpd-$(HTTPD_VERSION))
	chmod -R a+w $(HTTPD_TGZ_TARGET)

p4edit: $(HTTPD_TGZ_TARGET)
	p4 add $(HTTPD_TGZ_TARGET)
	p4 edit $(HTTPD_TGZ_TARGET)

clean:
	rm -rf httpd-$(HTTPD_VERSION)

allclean: clean
	rm -rf $(ZIMBRA_HOME)/httpd-$(HTTPD_VERSION)
	rm -rf $(ZIMBRA_HOME)/httpd
	rm -f $(HTTPD_TGZ_TARGET)
