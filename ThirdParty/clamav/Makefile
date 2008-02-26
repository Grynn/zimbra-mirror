CLAMAV_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(CLAMAV_ROOT)/../..; pwd)
MAKE ?= make
MAKEARGS ?= -j2

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif

ZIMBRA_HOME ?= /opt/zimbra
CLAMAV_VERSION ?= 0.92.1

CLAMAV_TGZ_TARGET := \
	$(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/clamav/clamav-$(CLAMAV_VERSION).tgz

all: build tar

build:
	(tar xzf src/clamav-$(CLAMAV_VERSION).tar.gz; \
	cd clamav-$(CLAMAV_VERSION); \
	patch -N -g0 -p1 < ../patches/fix-configure; \
	aclocal; \
	autoconf; \
	automake; \
	CFLAGS="-g -O2" \
	./configure --prefix=/opt/zimbra/clamav-$(CLAMAV_VERSION) \
	--with-user=zimbra --with-group=zimbra --disable-zlib-vcheck --disable-gcc-vcheck; \
	$(MAKE) $(MAKEARGS); $(MAKE) install)
	-(cd $(ZIMBRA_HOME); ln -s clamav-$(CLAMAV_VERSION) clamav)

tar:
	mkdir -p $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/clamav
	(cd $(ZIMBRA_HOME); tar czf $(CLAMAV_TGZ_TARGET) clamav-$(CLAMAV_VERSION))
	chmod -R a+w $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/clamav

p4edit: $(CLAMAV_TGZ_TARGET)
	p4 add $(CLAMAV_TGZ_TARGET)
	p4 edit $(CLAMAV_TGZ_TARGET)

clean:
	/bin/rm -rf clamav-$(CLAMAV_VERSION)

allclean: clean
	/bin/rm -rf $(ZIMBRA_HOME)/clamav-$(CLAMAV_VERSION)
	/bin/rm -rf $(ZIMBRA_HOME)/clamav
