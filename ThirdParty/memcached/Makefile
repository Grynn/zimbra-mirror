MEMCACHED_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(MEMCACHED_ROOT)/../..; pwd)

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif

ZIMBRA_HOME ?= /opt/zimbra

LIBEVENT_VERSION ?=1.3b
LIBEVENT_TGZ_TARGET := $(MEMCACHED_ROOT)/builds/$(BUILD_PLATFORM)/libevent-$(MEMCACHED_VERSION).tgz

MEMCACHED_VERSION ?= 1.2.2
MEMCACHED_TGZ_TARGET := $(MEMCACHED_ROOT)/builds/$(BUILD_PLATFORM)/memcached-$(MEMCACHED_VERSION).tgz
MEMCACHED_INSTALL := $(ZIMBRA_HOME)/memcached-$(MEMCACHED_VERSION)

all: build tar

build:
	(tar xfz libevent-$(LIBEVENT_VERSION).tar.gz; \
	cd libevent-$(LIBEVENT_VERSION); \
	./configure --prefix=$(ZIMBRA_HOME)/memcached-$(MEMCACHED_VERSION); \
	make; make install; \
	cd $(MEMCACHED_ROOT); \
	tar xzf memcached-$(MEMCACHED_VERSION).tar.gz; \
	cd memcached-$(MEMCACHED_VERSION); \
	./configure --prefix=$(ZIMBRA_HOME)/memcached-$(MEMCACHED_VERSION) \
	--with-libevent=$(ZIMBRA_HOME)/memcached-$(MEMCACHED_VERSION); \
	make; make install)
	ln -s $(MEMCACHED_INSTALL) $(ZIMBRA_HOME)/memcached

tar:
	mkdir -p $(MEMCACHED_ROOT)/builds/$(BUILD_PLATFORM)
	(cd $(ZIMBRA_HOME); tar czf $(MEMCACHED_ROOT)/builds/$(BUILD_PLATFORM)/memcached-$(MEMCACHED_VERSION).tgz memcached-$(MEMCACHED_VERSION))

p4edit: $(MEMCACHED_TGZ_TARGET)
	p4 add $(MEMCACHED_TGZ_TARGET)
	p4 edit $(MEMCACHED_TGZ_TARGET)
	

clean:
	/bin/rm -rf memcached-$(MEMCACHED_VERSION)

allclean: clean
	/bin/rm -rf $(ZIMBRA_HOME)/memcached-$(MEMCACHED_VERSION)
	/bin/rm -rf $(ZIMBRA_HOME/memcached
