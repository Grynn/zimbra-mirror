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

files	:= $(wildcard src/httpd-$(HTTPD_VERSION).tar.gz)

all: checksrc allclean build check

checksrc:
	$(if $(files), @echo "", exit 1)

check:
	@echo "Verifying apache-httpd build...";
	$(HTTPD_ROOT)/../zmcheck.pl -b $(ZIMBRA_HOME)/httpd-$(HTTPD_VERSION)/bin/httpd $(ZIMBRA_HOME)/httpd-$(HTTPD_VERSION)/bin/apachectl

build:
	tar xzf src/httpd-$(HTTPD_VERSION).tar.gz
	(cd httpd-$(HTTPD_VERSION); $(ENVMOD) LDFLAGS="-L/opt/zimbra/convertd/lib -R/opt/zimbra/convertd/lib"  CFLAGS="-g -O2" ./configure --prefix=/opt/zimbra/httpd-$(HTTPD_VERSION) --enable-so --with-included-apr; $(MAKE) $(MAKEARGS); $(MAKE) install)

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
