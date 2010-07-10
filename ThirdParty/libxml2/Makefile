XML_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(XML_ROOT)/../..; pwd)
MAKE ?= make
MAKEARGS ?= -j2

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)
ZIMBRA_HOME ?= /opt/zimbra

ifdef BETA
	include $(XML_ROOT)/../beta_versions.def
else
	include $(XML_ROOT)/../versions.def
endif

XML_NAME = libxml2
XML_TGZ_TARGET := $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/$(XML_NAME)/$(XML_NAME)-$(XML_VERSION).tgz
INSTALL_PREFIX := $(ZIMBRA_HOME)/$(XML_NAME)-$(XML_VERSION)
XML_CONFIGURE ?= --prefix=$(INSTALL_PREFIX) --enable-shared=no --enable-static=yes

ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif

files	:= $(wildcard src/$(XML_NAME)-$(XML_VERSION).tar.gz)

all: checksrc allclean build tar

checksrc:
	$(if $(files), @echo "", exit 1)

build:
	(tar xzf src/$(XML_NAME)-$(XML_VERSION).tar.gz; \
	mv $(XML_NAME)-$(XML_VERSION) $(XML_NAME)-$(XML_VERSION); \
	cd $(XML_NAME)-$(XML_VERSION); \
	patch -g0 -N -p1 < ../patches/CVE-2009-2414-CVE-2009-2416.patch; \
	CFLAGS="-g -O2 -fPIC" \
	./configure $(XML_CONFIGURE); $(MAKE) $(MAKEARGS); $(MAKE) install)
	(cp -f $(XML_NAME)-$(XML_VERSION)/COPYING $(INSTALL_PREFIX)/LICENSE)
	(cd $(ZIMBRA_HOME); ln -s $(XML_NAME)-$(XML_VERSION) $(XML_NAME))

tar:
	mkdir -p $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/$(XML_NAME)
	(cd $(ZIMBRA_HOME); tar czf $(XML_TGZ_TARGET) $(XML_NAME)-$(XML_VERSION))

p4edit: $(XML_TGZ_TARGET)

clean:
	/bin/rm -rf $(XML_NAME)-$(XML_VERSION)

allclean: clean
	/bin/rm -rf $(ZIMBRA_HOME)/$(XML_NAME)-$(XML_VERSION)
	/bin/rm -rf $(ZIMBRA_HOME)/$(XML_NAME)
	rm -f $(XML_TGZ_TARGET)
