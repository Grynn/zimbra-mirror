NGINX_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(NGINX_ROOT)/../..; pwd)

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif

CYRUS_VERSION ?= 2.1.22.3z
CYRUS_LIB_DIR ?= $(ZIMBRA_HOME)/cyrus-sasl-$(CYRUS_VERSION)/lib

SSL_VERSION	?= 0.9.8g
CFLAGS := "-g -O2"

ZIMBRA_HOME ?= /opt/zimbra

NGINX_VERSION ?= 0.5.30
NGINX_TGZ_TARGET := $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/nginx/nginx-$(NGINX_VERSION)-zimbra.tgz
NGINX_INSTALL := $(ZIMBRA_HOME)/nginx-$(NGINX_VERSION)-zimbra

all: build tar

build:
	(cd nginx-$(NGINX_VERSION)-zimbra; \
	CFLAGS=$(CFLAGS) ./configure \
             --prefix=$(ZIMBRA_HOME)/nginx-$(NGINX_VERSION)-zimbra \
             --with-debug \
             --without-pcre \
             --without-http_rewrite_module \
             --with-http_ssl_module \
             --with-sasl-inc=/opt/zimbra/cyrus-sasl-$(CYRUS_VERSION)/include \
             --with-mail \
             --with-mail_ssl_module; \
	LD_RUN_PATH="/opt/zimbra/openssl-$(SSL_VERSION)/lib:$(CYRUS_LIB_DIR)" make; make install)
	ln -s $(NGINX_INSTALL) $(ZIMBRA_HOME)/nginx

tar:
	mkdir -p $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/nginx
	(cd $(ZIMBRA_HOME); tar czf $(NGINX_TGZ_TARGET) nginx-$(NGINX_VERSION)-zimbra)

p4edit: $(NGINX_TGZ_TARGET)
	p4 add $(NGINX_TGZ_TARGET)
	p4 edit $(NGINX_TGZ_TARGET)
	

clean:
	-make -C nginx-$(NGINX_VERSION)-zimbra clean

allclean: clean
	/bin/rm -rf $(ZIMBRA_HOME)/nginx-$(NGINX_VERSION)-zimbra
	/bin/rm -rf $(ZIMBRA_HOME)/nginx
