MEMCACHED_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(MEMCACHED_ROOT)/../..; pwd)

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif

ZIMBRA_HOME ?= /opt/zimbra

ifdef BETA
	include $(MEMCACHED_ROOT)/../beta_versions.def
else
	include $(MEMCACHED_ROOT)/../versions.def
endif

LIBEVENT_TGZ_TARGET := $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/memcached/libevent-$(LIBEVENT_VERSION).tgz

MEMCACHED_TGZ_TARGET := $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/memcached/memcached-$(MEMCACHED_VERSION).tgz
MEMCACHED_INSTALL := $(ZIMBRA_HOME)/memcached-$(MEMCACHED_VERSION)

all: allclean build tar

build:
	(tar xfz src/libevent-$(LIBEVENT_VERSION).tar.gz; \
	cd libevent-$(LIBEVENT_VERSION); \
	CFLAGS="-g -O2" ./configure --prefix=$(ZIMBRA_HOME)/memcached-$(MEMCACHED_VERSION); \
	make; make install; \
	cd $(MEMCACHED_ROOT); \
	tar xzf src/memcached-$(MEMCACHED_VERSION).tar.gz; \
	cd memcached-$(MEMCACHED_VERSION); \
	CFLAGS="-g -O2" ./configure --prefix=$(ZIMBRA_HOME)/memcached-$(MEMCACHED_VERSION) \
	--with-libevent=$(ZIMBRA_HOME)/memcached-$(MEMCACHED_VERSION); \
	LD_RUN_PATH=/opt/zimbra/memcached/lib make; make install)
	ln -s $(MEMCACHED_INSTALL) $(ZIMBRA_HOME)/memcached

tar:
	mkdir -p $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/memcached
	(cd $(ZIMBRA_HOME); tar czf $(MEMCACHED_TGZ_TARGET) memcached-$(MEMCACHED_VERSION))

p4edit: $(MEMCACHED_TGZ_TARGET)
	p4 add $(MEMCACHED_TGZ_TARGET)
	p4 edit $(MEMCACHED_TGZ_TARGET)
	

clean:
	/bin/rm -rf libevent-$(LIBEVENT_VERSION)
	/bin/rm -rf memcached-$(MEMCACHED_VERSION)

allclean: clean
	/bin/rm -rf $(ZIMBRA_HOME)/memcached-$(MEMCACHED_VERSION)
	/bin/rm -rf $(ZIMBRA_HOME)/memcached
	rm -f $(MEMCACHED_TGZ_TARGET)
