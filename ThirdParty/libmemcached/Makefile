LIBMEMCACHED_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(LIBMEMCACHED_ROOT)/../..; pwd)
MAKE ?= make

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif

MAKEARGS ?= -j2
ZIMBRA_HOME ?= /opt/zimbra

PATCH	:=

ifdef BETA
	include $(LIBMEMCACHED_ROOT)/../beta_versions.def
else
	include $(LIBMEMCACHED_ROOT)/../versions.def
endif

LIBMCD_TGZ_TARGET := $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/libmemcached/libmemcached-$(LIBMCD_VERSION).tgz

files	:= $(wildcard src/libmemcached-$(LIBMCD_VERSION).tar.gz)

all: checksrc allclean build tar

checksrc:
	$(if $(files), @echo "", exit 1)

build:
	(tar xzf src/libmemcached-$(LIBMCD_VERSION).tar.gz; \
	cd libmemcached-$(LIBMCD_VERSION); \
	CFLAGS="-g -O0" \
	CPPFLAGS="-I$(ZIMBRA_HOME)/memcached/include" \
	LDFLAGS="-L$(ZIMBRA_HOME)/memcached/lib" \
	LIBEVENT_CPPFLAGS="-I$(ZIMBRA_HOME)/memcached/include" \
	LIBEVENT_LDFLAGS="-L$(ZIMBRA_HOME)/memcached/lib" \
	./configure --prefix=$(ZIMBRA_HOME)/libmemcached-$(LIBMCD_VERSION) \
	--with-memcached=/opt/zimbra/memcached; \
	$(MAKE) $(MAKEARGS); \
	$(MAKE) install STRIP="")
	(cd $(ZIMBRA_HOME); ln -s libmemcached-$(LIBMCD_VERSION) libmemcached)

tar:
	mkdir -p $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/libmemcached
	(cd $(ZIMBRA_HOME); tar czf $(LIBMCD_TGZ_TARGET) libmemcached-$(LIBMCD_VERSION))

p4edit: $(LIBMCD_TGZ_TARGET)
	p4 add $(LIBMCD_TGZ_TARGET)
	p4 edit $(LIBMCD_TGZ_TARGET)

clean:
	/bin/rm -rf libmemcached-$(LIBMCD_VERSION)

allclean: clean
	/bin/rm -rf $(ZIMBRA_HOME)/libmemcached-$(LIBMCD_VERSION)
	/bin/rm -rf $(ZIMBRA_HOME)/libmemcached
	rm -f $(LIBMCD_TGZ_TARGET)
