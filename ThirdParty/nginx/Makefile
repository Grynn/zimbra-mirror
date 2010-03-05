NGINX_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(NGINX_ROOT)/../..; pwd)

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif

ZIMBRA_HOME ?= /opt/zimbra

ifdef BETA
	include $(NGINX_ROOT)/../beta_versions.def
else
	include $(NGINX_ROOT)/../versions.def
endif

ifeq ($(BUILD_PLATFORM), MACOSXx86_10.6)
	PATCH   := patch -g0 -N -p1 < ../patches/MACOSXx86_10.6.patch;
endif

NGINX_TGZ_TARGET := $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/nginx/nginx-$(NGINX_VERSION)-zimbra.tgz
NGINX_INSTALL := $(ZIMBRA_HOME)/nginx-$(NGINX_VERSION)-zimbra

all: allclean build tar

build:
ifeq (MACOSX,$(findstring MACOSX,$(BUILD_PLATFORM)))
	(cd nginx-$(NGINX_VERSION)-zimbra; \
	$(PATCH) \
	./configure \
             --prefix=$(ZIMBRA_HOME)/nginx-$(NGINX_VERSION)-zimbra \
             --with-debug \
             --with-http_stub_status_module \
             --with-http_ssl_module \
             --with-sasl-inc=/opt/zimbra/cyrus-sasl-$(CYRUS_VERSION)/include \
             --with-mail \
             --with-pcre=/opt/zimbra \
             --http-log-path=/opt/zimbra/log/nginx.access.log \
             --error-log-path=/opt/zimbra/log/nginx.log \
             --http-client-body-temp-path=$(ZIMBRA_HOME)/data/tmp/nginx/client \
             --http-proxy-temp-path=$(ZIMBRA_HOME)/data/tmp/nginx/proxy \
             --http-fastcgi-temp-path=$(ZIMBRA_HOME)/data/tmp/nginx/fastcgi \
             --with-mail_ssl_module; \
	LD_RUN_PATH="$(OPENSSL_LIB_DIR):$(CYRUS_LIB_DIR)" make; make install)
else
	(cd nginx-$(NGINX_VERSION)-zimbra; \
	./configure \
             --prefix=$(ZIMBRA_HOME)/nginx-$(NGINX_VERSION)-zimbra \
             --with-debug \
             --with-http_stub_status_module \
             --with-http_ssl_module \
             --with-sasl-inc=/opt/zimbra/cyrus-sasl-$(CYRUS_VERSION)/include \
             --with-mail \
             --http-log-path=/opt/zimbra/log/nginx.access.log \
             --error-log-path=/opt/zimbra/log/nginx.log \
             --http-client-body-temp-path=$(ZIMBRA_HOME)/data/tmp/nginx/client \
             --http-proxy-temp-path=$(ZIMBRA_HOME)/data/tmp/nginx/proxy \
             --http-fastcgi-temp-path=$(ZIMBRA_HOME)/data/tmp/nginx/fastcgi \
             --with-mail_ssl_module; \
	LD_RUN_PATH="$(OPENSSL_LIB_DIR):$(CYRUS_LIB_DIR)" make; make install)
endif
	(cd $(ZIMBRA_HOME); ln -s $(NGINX_INSTALL) nginx)

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
	rm -f $(NGINX_TGZ_TARGET)
