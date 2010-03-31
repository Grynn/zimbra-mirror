CYRUS_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(CYRUS_ROOT)/../..; pwd)
MAKE ?= make
MAKEARGS ?= -j2

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif

ZIMBRA_HOME ?= /opt/zimbra

ifdef BETA
	include $(CYRUS_ROOT)/../beta_versions.def
	BSTR	:= beta
else
	include $(CYRUS_ROOT)/../versions.def
endif

CYRUS_TGZ_TARGET := $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/cyrus-sasl/cyrus-sasl-$(CYRUS_VERSION).tgz

files	:= $(wildcard src/cyrus-sasl-$(CYRUS_RELEASE).tar.gz)

all: checksrc allclean build 

checksrc:
	$(if $(files), @echo "", exit 1)

check:
	@echo "Verifying cyrus-sasl build...";
	$(CYRUS_ROOT)/../zmcheck.pl -s $(ZIMBRA_HOME)/cyrus-sasl-$(CYRUS_VERSION)/sbin/saslauthd
	$(CYRUS_ROOT)/../zmcheck.pl -l $(ZIMBRA_HOME)/cyrus-sasl-$(CYRUS_VERSION)/lib/sasl2/libgssapiv2.so.2.0.23

build: 
ifneq	(,$(findstring MACOSX,$(BUILD_PLATFORM)))
	@echo "Building cyrus-sasl for $(BUILD_PLATFORM)"
	mkdir -p $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/cyrus-sasl
	_S_='~' ./zimbra-cyrus-sasl-build.sh ${BSTR}
	(cd build/cyrus-sasl-$(CYRUS_VERSION); $(MAKE) install)
	(./fix-sasl.pl)
	(cd $(ZIMBRA_HOME); ln -s cyrus-sasl-$(CYRUS_VERSION) cyrus-sasl; \
	tar czf $(CYRUS_TGZ_TARGET) \
	cyrus-sasl-$(CYRUS_VERSION))
else
	mkdir -p $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/cyrus-sasl
	_S_='~' ./zimbra-cyrus-sasl-build.sh ${BSTR}
	(cd build/cyrus-sasl-$(CYRUS_VERSION); $(MAKE) install)
	(cd $(ZIMBRA_HOME); ln -s cyrus-sasl-$(CYRUS_VERSION) cyrus-sasl; \
	tar czf $(CYRUS_TGZ_TARGET) \
	cyrus-sasl-$(CYRUS_VERSION))
endif

p4edit: $(CYRUS_TGZ_TARGET)
	p4 add $(CYRUS_TGZ_TARGET)
	p4 edit $(CYRUS_TGZ_TARGET)

clean:
	rm -rf build

allclean: clean
	rm -rf $(ZIMBRA_HOME)/cyrus-sasl-$(CYRUS_VERSION)
	rm -rf $(ZIMBRA_HOME)/cyrus-sasl
	rm -f $(CYRUS_TGZ_TARGET)
