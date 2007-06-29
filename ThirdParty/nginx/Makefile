NGNIX_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(NGNIX_ROOT)/../..; pwd)

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif

ZIMBRA_HOME ?= /opt/zimbra

LIBEVENT_VERSION ?=1.3b
LIBEVENT_TGZ_TARGET := $(NGNIX_ROOT)/builds/$(BUILD_PLATFORM)/libevent-$(MEMCACHED_VERSION).tgz

MEMCACHED_VERSION ?= 1.2.2
MEMCACHED_TGZ_TARGET := $(NGNIX_ROOT)/builds/$(BUILD_PLATFORM)/memcached-$(MEMCACHED_VERSION).tgz

NGNIX_VERSION ?= 0.5.20
NGNIX_TGZ_TARGET := $(NGNIX_ROOT)/builds/$(BUILD_PLATFORM)/ngnix-$(NGNIX_VERSION)-zimbra.tgz

all: build tar

build:
	(tar xfz sources/libevent-$(LIBEVENT_VERSION).tar.gz; \
	cd libevent-$(LIBEVENT_VERSION); \
	./configure --prefix=$(ZIMBRA_HOME)/libevent-$(LIBEVENT_VERSION); \
	make; make install; \
	cd $(NGNIX_ROOT); \
	tar xzf sources/memcached-$(MEMCACHED_VERSION).tar.gz; \
	cd memcached-$(MEMCACHED_VERSION); \
	./configure --prefix=$(ZIMBRA_HOME)/memcached-$(MEMCACHED_VERSION); \
	make; make install; \
	cd $(NGNIX_ROOT); \
	cd nginx-$(NGNIX_VERSION)-zimbra; \
	./configure \
             --prefix=$(ZIMBRA_HOME)/nginx-$(NGNIX_VERSION)-zimbra/ \
             --with-debug \
             --without-http  \
             --with-mail \
             --with-mail_ssl_module; \
	make; make install)
	ln -s $(NGNIX_INSTALL) $(ZIMBRA_HOME)/ngnix

tar:
	mkdir -p $(NGNIX_ROOT)/builds/$(BUILD_PLATFORM)
	(cd $(ZIMBRA_HOME); tar czf $(NGNIX_ROOT)/builds/$(BUILD_PLATFORM)/ngnix-$(NGNIX_VERSION)-zimbra.tgz ngnix-$(NGNIX_VERSION)-zimbra)

p4edit: $(NGNIX_TGZ_TARGET)
	p4 add $(NGNIX_TGZ_TARGET)
	p4 edit $(NGNIX_TGZ_TARGET)
	

clean:
	/bin/rm -rf memcached-$(MEMCACHED_VERSION)
	/bin/rm -rf ngnix-$(NGNIX_VERSION)-zimbra

allclean: clean
	/bin/rm -rf $(ZIMBRA_HOME)/memcached-$(MEMCACHED_VERSION)
	/bin/rm -rf $(ZIMBRA_HOME)/ngnix-$(NGNIX_VERSION)-zimbra
	/bin/rm -rf $(ZIMBRA_HOME)/ngnix
