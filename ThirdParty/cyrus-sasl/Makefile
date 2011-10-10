CYRUS_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(CYRUS_ROOT)/../..; pwd)
MAKE ?= make
MAKEARGS ?= -j2

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif

ZIMBRA_HOME ?= /opt/zimbra

PATCH	:=

FIXOSX	:= @echo "no need to fix anything"

ifdef BETA
	include $(CYRUS_ROOT)/../beta_versions.def
	BSTR	:= beta
else
	include $(CYRUS_ROOT)/../versions.def
endif

ifneq	(,$(findstring MACOSX,$(BUILD_PLATFORM)))
	FIXOSX	:= (./fix-sasl.pl $(CYRUS_PATCH))
endif

CYRUS_TGZ_TARGET := $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/cyrus-sasl/cyrus-sasl-$(CYRUS_VERSION).tgz

files	:= $(wildcard src/cyrus-sasl-$(CYRUS_RELEASE).tar.gz)

all: checksrc allclean build check

checksrc:
	$(if $(files), @echo "", exit 1)

check:
	@echo "Verifying cyrus-sasl build...";
	$(CYRUS_ROOT)/../zmcheck.pl -s $(ZIMBRA_HOME)/cyrus-sasl-$(CYRUS_VERSION)/sbin/saslauthd
	$(CYRUS_ROOT)/../zmcheck.pl -l $(ZIMBRA_HOME)/cyrus-sasl-$(CYRUS_VERSION)/lib/sasl2/libgssapiv2.so.2.0.$(CYRUS_PATCH)

build:
	@echo "Building cyrus-sasl for $(BUILD_PLATFORM)"
	mkdir -p $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/cyrus-sasl
	(tar xfz src/cyrus-sasl-$(CYRUS_RELEASE).tar.gz; \
	mv cyrus-sasl-$(CYRUS_RELEASE) cyrus-sasl-$(CYRUS_VERSION); \
	cd cyrus-sasl-$(CYRUS_VERSION); \
	patch -g0 -p1 < ../patches/sasl-link-order.patch; \
	patch -g0 -p1 < ../patches/sasl-darwin.patch; \
	patch -g0 -p1 < ../patches/sasl-auth-zimbra.patch; \
	patch -g0 -p1 < ../patches/heimdal-build.patch; \
	patch -g0 -p1 < ../patches/gssapi.patch; \
	$(PATCH))
	_S_='~' ./zimbra-cyrus-sasl-build.sh ${BSTR}
	(cd cyrus-sasl-$(CYRUS_VERSION); $(MAKE) install)
	$(FIXOSX)
	(cd $(ZIMBRA_HOME); ln -s cyrus-sasl-$(CYRUS_VERSION) cyrus-sasl; \
	tar czf $(CYRUS_TGZ_TARGET) \
	cyrus-sasl-$(CYRUS_VERSION))

p4edit: $(CYRUS_TGZ_TARGET)
	p4 add $(CYRUS_TGZ_TARGET)
	p4 edit $(CYRUS_TGZ_TARGET)

clean:
	rm -rf cyrus-sasl-$(CYRUS_VERSION)

allclean: clean
	rm -rf $(ZIMBRA_HOME)/cyrus-sasl-$(CYRUS_VERSION)
	rm -rf $(ZIMBRA_HOME)/cyrus-sasl
	rm -f $(CYRUS_TGZ_TARGET)
