NGINX_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(NGINX_ROOT)/../..; pwd)

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif

ZIMBRA_HOME ?= /opt/zimbra

LIBEVENT_VERSION ?=1.3b
LIBEVENT_TGZ_TARGET := $(NGINX_ROOT)/builds/$(BUILD_PLATFORM)/libevent-$(MEMCACHED_VERSION).tgz

MEMCACHED_VERSION ?= 1.2.2
MEMCACHED_TGZ_TARGET := $(NGINX_ROOT)/builds/$(BUILD_PLATFORM)/memcached-$(MEMCACHED_VERSION).tgz
MEMCACHED_INSTALL := $(ZIMBRA_HOME)/memcached-$(MEMCACHED_VERSION)

NGINX_VERSION ?= 0.5.20
NGINX_TGZ_TARGET := $(NGINX_ROOT)/builds/$(BUILD_PLATFORM)/nginx-$(NGINX_VERSION)-zimbra.tgz
NGINX_INSTALL := $(ZIMBRA_HOME)/nginx-$(NGINX_VERSION)-zimbra

all: build tar

build:
	(tar xfz sources/libevent-$(LIBEVENT_VERSION).tar.gz; \
	cd libevent-$(LIBEVENT_VERSION); \
	./configure --prefix=$(ZIMBRA_HOME)/memcached-$(MEMCACHED_VERSION); \
	make; make install; \
	cd $(NGINX_ROOT); \
	tar xzf sources/memcached-$(MEMCACHED_VERSION).tar.gz; \
	cd memcached-$(MEMCACHED_VERSION); \
	./configure --prefix=$(ZIMBRA_HOME)/memcached-$(MEMCACHED_VERSION) \
	--with-libevent=$(ZIMBRA_HOME)/memcached-$(MEMCACHED_VERSION); \
	make; make install; \
	cd $(NGINX_ROOT); \
	cd nginx-$(NGINX_VERSION)-zimbra; \
	./configure \
             --prefix=$(ZIMBRA_HOME)/nginx-$(NGINX_VERSION)-zimbra/ \
             --with-debug \
             --without-http  \
             --with-mail \
             --with-mail_ssl_module; \
	make; make install)
	ln -s $(MEMCACHED_INSTALL) $(ZIMBRA_HOME)/memcached
	ln -s $(NGINX_INSTALL) $(ZIMBRA_HOME)/nginx

tar:
	mkdir -p $(NGINX_ROOT)/builds/$(BUILD_PLATFORM)
	(cd $(ZIMBRA_HOME); tar czf $(NGINX_ROOT)/builds/$(BUILD_PLATFORM)/memcached-$(MEMCACHED_VERSION).tgz memcached-$(MEMCACHED_VERSION))
	(cd $(ZIMBRA_HOME); tar czf $(NGINX_ROOT)/builds/$(BUILD_PLATFORM)/nginx-$(NGINX_VERSION)-zimbra.tgz nginx-$(NGINX_VERSION)-zimbra)

p4edit: $(NGINX_TGZ_TARGET)
	p4 add $(NGINX_TGZ_TARGET)
	p4 edit $(NGINX_TGZ_TARGET)
	

clean:
	/bin/rm -rf memcached-$(MEMCACHED_VERSION)
	/bin/rm -rf nginx-$(NGINX_VERSION)-zimbra

allclean: clean
	/bin/rm -rf $(ZIMBRA_HOME)/memcached-$(MEMCACHED_VERSION)
	/bin/rm -rf $(ZIMBRA_HOME)/nginx-$(NGINX_VERSION)-zimbra
	/bin/rm -rf $(ZIMBRA_HOME)/nginx
