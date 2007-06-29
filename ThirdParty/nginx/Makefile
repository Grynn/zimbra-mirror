NGINX_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(NGINX_ROOT)/../..; pwd)

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif

ifeq ($(BUILD_PLATFORM), RPL1)
	CFLAGS := "CLFAGS='-DOPENSSL_NO_KRB5'"
endif

ZIMBRA_HOME ?= /opt/zimbra

NGINX_VERSION ?= 0.5.20
NGINX_TGZ_TARGET := $(NGINX_ROOT)/builds/$(BUILD_PLATFORM)/nginx-$(NGINX_VERSION)-zimbra.tgz
NGINX_INSTALL := $(ZIMBRA_HOME)/nginx-$(NGINX_VERSION)-zimbra

all: build tar

build:
	(cd nginx-$(NGINX_VERSION)-zimbra; \
	$CFLAGS ./configure \
             --prefix=$(ZIMBRA_HOME)/nginx-$(NGINX_VERSION)-zimbra/ \
             --with-debug \
             --without-http  \
             --with-mail \
             --with-mail_ssl_module; \
	make; make install)
	ln -s $(NGINX_INSTALL) $(ZIMBRA_HOME)/nginx

tar:
	mkdir -p $(NGINX_ROOT)/builds/$(BUILD_PLATFORM)
	(cd $(ZIMBRA_HOME); tar czf $(NGINX_ROOT)/builds/$(BUILD_PLATFORM)/nginx-$(NGINX_VERSION)-zimbra.tgz nginx-$(NGINX_VERSION)-zimbra)

p4edit: $(NGINX_TGZ_TARGET)
	p4 add $(NGINX_TGZ_TARGET)
	p4 edit $(NGINX_TGZ_TARGET)
	

clean:
	/bin/rm -rf nginx-$(NGINX_VERSION)-zimbra

allclean: clean
	/bin/rm -rf $(ZIMBRA_HOME)/nginx-$(NGINX_VERSION)-zimbra
	/bin/rm -rf $(ZIMBRA_HOME)/nginx
