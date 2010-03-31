CLAMAV_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(CLAMAV_ROOT)/../..; pwd)
MAKE ?= make
MAKEARGS ?= -j2

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif

ZIMBRA_HOME ?= /opt/zimbra

ifdef BETA
	include $(CLAMAV_ROOT)/../beta_versions.def
else
	include $(CLAMAV_ROOT)/../versions.def
endif

PATCH	:= patch -N -g0 -p1 < ../patches/fix-configure-953.patch; patch -N -g0 -p1 < ../patches/patch-0.95.3-bug1737.patch

files	:= $(wildcard src/clamav-$(CLAMAV_VERSION).tar.gz)

CLAMAV_TGZ_TARGET := \
	$(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/clamav/clamav-$(CLAMAV_VERSION).tgz

all: checksrc allclean build check tar

checksrc:
	$(if $(files), @echo "", exit 1)

check:
	@echo "Verifying clamav build...";
	$(CLAMAV_ROOT)/../zmcheck.pl -b $(ZIMBRA_HOME)/clamav-$(CLAMAV_VERSION)/bin/freshclam
	$(CLAMAV_ROOT)/../zmcheck.pl -s $(ZIMBRA_HOME)/clamav-$(CLAMAV_VERSION)/sbin/clamd

build:
	(tar xzf src/clamav-$(CLAMAV_VERSION).tar.gz; \
	cd clamav-$(CLAMAV_VERSION); \
	$(PATCH); \
	CFLAGS="-g -O2" \
	CPPFLAGS="-I/opt/zimbra/sendmail-$(SENDMAIL_VERSION)/include" \
	LDFLAGS="-L/opt/zimbra/sendmail-$(SENDMAIL_VERSION)/lib" \
	./configure --prefix=/opt/zimbra/clamav-$(CLAMAV_VERSION) --mandir=/opt/zimbra/clamav-$(CLAMAV_VERSION)/man \
	--with-user=zimbra --with-group=zimbra --disable-zlib-vcheck --disable-gcc-vcheck --disable-clamav --enable-milter; \
	$(MAKE) $(MAKEARGS); $(MAKE) install)
	-(cd $(ZIMBRA_HOME); ln -s clamav-$(CLAMAV_VERSION) clamav)

tar:
	mkdir -p $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/clamav
	(cd $(ZIMBRA_HOME); tar czf $(CLAMAV_TGZ_TARGET) clamav-$(CLAMAV_VERSION))
	chmod -R a+w $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/clamav/*

p4edit: $(CLAMAV_TGZ_TARGET)
	p4 add $(CLAMAV_TGZ_TARGET)
	p4 edit $(CLAMAV_TGZ_TARGET)

clean:
	/bin/rm -rf clamav-$(CLAMAV_VERSION)

allclean: clean
	/bin/rm -rf $(ZIMBRA_HOME)/clamav-$(CLAMAV_VERSION)
	/bin/rm -rf $(ZIMBRA_HOME)/clamav
	rm -f $(CLAMAV_TGZ_TARGET)
